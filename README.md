# JsonAPIary

The JsonAPIary project is a library that makes it easy to serialize/deserialize Java objects into/out-of json conforming to the [JSON API specification](http://jsonapi.org/format/). The library accomplishes this by providing a module that can be registered on a [Jackson](https://github.com/FasterXML/jackson-databind) ObjectMapper.

## Getting Started

### Importing

*NOTE: We are currently in the process of publishing the JsonAPIary library as a jar to Maven Central... but in the meantime, it can be pulled down and built/installed locally by running:*
```
mvn clean install
```

*(Once the project is built locally, then)* the library can be imported by another project via maven as follows:
```
<dependency>
	<groupId>com.cradlepoint</groupId>
	<artifactId>jsonapiary</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Registering the module

Wherever the [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html) instance (that you will be serializing/deserializing JsonAPI JSON with) is initialized in your project, you can register the JsonAPIary module as follows:
```
ObjectMapper objectMapper = new ObjectMapper();
...
JsonApiModule jsonApiModule = new JsonApiModule(
	FirstClass.class,
	SecondClass.class);
objectMapper.registerModule(jsonApiModule);
```
The types passed into the JsonApiModule Object’s constructor are all of the types that you wish to be able to serialize/deserialize according to the JsonAPI Specification. Please note that all of the passed in types **MUST** be annotated with the JsonAPIary annotations covered below.

## Usage

### JsonAPI(ary) Annotations

The JsonAPIary library has 7 annotations that it uses to drive the serialization/deserialization of objects.

* **@JsonApiType** - Every type that will be either serialized or deserialized by JsonAPIary needs to be annotated with this. You can also use this annotation to set/override the JSON API "type" value.
* **@JsonApiId** - Every type must also have an ID, and that ID field/methods must be annotated with this annotation.
* **@JsonApiAttribute** - Identifies a field/method as a JSON API "attribute". These object will appear as objects in the JSON API "attributes" object, in which they will be serialized as standard json.
* **@JsonApiLink** - Identifies a field/method as explicitly as a JSON API "Link". Only URLs and Object who's toString() values that represent URLs are valid "Link" fields/methods.
* **@JsonApiMeta** - Identifies a field/method as a JSON API "meta" data. These object will appear as objects in the JSON API "meta" object, in which they will be serialized as standard json.
* **@JsonApiRelationship** - Identifies a field/method as a JSON API "relationship". These objects will partially appear as JSON API "resource linkages" in the JSON API "relationships" object, as well as in their entirity in JSON API format the JSON API "included" object.
* **@JsonApiIgnore** - Fields/Methods annotated with this will not be included in serialized JSON API output. Otherwise, if a field/method is annotated with the Jackson @JsonProperty annotation, it will auto-magically included in the JSON API "meta" object.

### Serialization

The JsonAPIary library uses an "envelope" pattern to initiate serialization/deserialization according to the JSON API specification. Because of this, it also allows the user to be able to utilize the *same* Jackson ObjectMapper to serialize and deserialzie the *same* Object in *either* JSON API format, *or* as standard json. This proves to be a particularly nice JsonAPIary feature when adding JSON API support to an existing project with a strong IOC backend (such as the SpringFramework).

A simple example of a JsonAPIary annotated Singly Linked List Java POJO is as follows:
```
@JsonApiType("node")
public class SingleLinkNode {

    @JsonApiId
    @JsonProperty("id")
    private Long id;

    @JsonApiAttribute("element")
    @JsonProperty("value")
    private String value;

    @JsonApiRelationship
    @JsonProperty("link")
    private SingleLinkNode linkNode;

    public SingleLinkNode() { }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SingleLinkNode getLinkNode() {
        return this.linkNode;
    }

    public void setLinkNode(SingleLinkNode linkNode) {
        this.linkNode = linkNode;
    }

}
```

With the above annotated type, one could manually create a simple Singly Linked List as follows:
```
SingleLinkNode singleLinkNode1 = new SingleLinkNode();
singleLinkNode1.setId(1l);
singleLinkNode1.setValue("ONE");

SingleLinkNode singleLinkNode2 = new SingleLinkNode();
singleLinkNode2.setId(2l);
singleLinkNode2.setValue("2nd");

SingleLinkNode singleLinkNode3 = new SingleLinkNode();
singleLinkNode3.setId(3l);
singleLinkNode3.setValue("tertiary.");

singleLinkNode1.setLinkNode(singleLinkNode2);
singleLinkNode2.setLinkNode(singleLinkNode3);
singleLinkNode3.setLinkNode(null);
```

Then, with the created List above, serializing it out in JSON API format is as easy as:
```
String json = objectMapper.writeValueAsString(new JsonApiEnvelope<SingleLinkNode>(singleLinkNode1));
```

From which the outputted json would be:
```
{
  "data" : {
    "id" : "1",
    "type" : "node",
    "attributes" : {
      "element" : "ONE"
    },
    "relationships" : {
      "link" : {
        "data" : {
          "id" : "2",
          "type" : "node"
        }
      }
    }
  },
  "included" : [ {
    "id" : "2",
    "type" : "node",
    "attributes" : {
      "element" : "2nd"
    },
    "relationships" : {
      "link" : {
        "data" : {
          "id" : "3",
          "type" : "node"
        }
      }
    }
  }, {
    "id" : "3",
    "type" : "node",
    "attributes" : {
      "element" : "tertiary."
    },
    "relationships" : { }
  } ]
}
```

*Additionally*, as mentioned above, because the JsonAPIary library is predicated on envelopes for JSON API serialization and deserialization, that same object can also continue to be serialized/deserialized in "standard" json with the *same* ObjectMapper. For example, if the same simple list created above were serialized without the JsonAPIary envelope, such as:
```
String json = objectMapper.writeValueAsString(singleLinkNode1);
```

The "standard" json created by the non-enveloped seriaization call above would produce:
```
{
  "id" : 1,
  "value" : "ONE",
  "link" : {
    "id" : 2,
    "value" : "2nd",
    "link" : {
      "id" : 3,
      "value" : "tertiary.",
      "link" : null
    }
  }
}
```


### Deserialization

Deserialization, behaves as (I hope) would be expected... if you are deserializing a JSON API String, invoke the ObjectMapper like so:
```
JsonApiEnvelope<SingleLinkNode> deserializedObject = objectMapper.readValue(json, JsonApiEnvelope.class);
```

Further, if you are deserializing a "standard" json String, you can continue to invoke the same ObjectMapper as:
```
SingleLinkNode deserializedObject = objectMapper.readValue(json, SingleLinkNode.class);
```

## TODO:

Currently, there are two outstanding tasks to make the library complete:
1. Publish the jar to Maven Central, for easier project inclusion
2. Provide a "callback" interface as part of deserialization, to allow the app to fetch/generate objects that referenced by the json but not actually included within it. One example of this to allow the app to fetch a sub-object that is a "link" in the JSON API json; another example would be if an object has a relationship to another object, but that related object is not present in the "included" JSON API object.

Both of those tasks should be completed shortly in the coming days/weeks. Stay tuned!


