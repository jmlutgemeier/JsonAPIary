package test.com.cradlepoint.jsonapiary.pojos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.cradlepoint.jsonapiary.annotations.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@JsonApiType
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleObject {

    ////////////////
    // Attributes //
    ////////////////

    @JsonApiId
    @JsonProperty("objectId")
    private Long id;

    @JsonApiAttribute
    @JsonProperty("objectAttribute")
    private String attribute;

    @JsonApiRelationship
    @JsonProperty("someRelationship")
    private List<SimpleObject> thing;

    @JsonApiRelationship
    @JsonProperty("someOtherRelationship")
    private SimpleSubObject thing2;

    /////////////////
    // Constructor //
    /////////////////

    public SimpleObject() {
        Random random = new Random();
        id = random.nextLong();
        attribute = RandomStringUtils.randomAlphanumeric(1, 20);
    }

    public SimpleObject(int numThings) {
        Random random = new Random();
        id = random.nextLong();
        attribute = RandomStringUtils.randomAlphanumeric(1, 20);

        List<SimpleObject> blah = new ArrayList<>();
        for(int i1 = 0; i1 < numThings; i1++) {
            blah.add(new SimpleObject());
        }
        thing = blah;

        thing2 = new SimpleSubObject();
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public List<SimpleObject> getThing() {
        return this.thing;
    }

    public void setThing(List<SimpleObject> thing) {
        this.thing = thing;
    }

    public SimpleSubObject getThing2() {
        return this.thing2;
    }

    public void setThing2(SimpleSubObject thing2) {
        this.thing2 = thing2;
    }

    @JsonApiMeta
    @JsonProperty("objectBlah")
    public String blah() {
        return "blah!";
    }

    @JsonApiIgnore
    @JsonProperty("ignoredThing")
    public String ignored() {
        return "THIS SHOULDN'T BE HERE";
    }

    @JsonProperty("catchAllThing")
    public String catchAll() {
        return "this should ahve been caught";
    }

}
