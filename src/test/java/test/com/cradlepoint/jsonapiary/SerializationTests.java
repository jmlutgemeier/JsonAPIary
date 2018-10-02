package test.com.cradlepoint.jsonapiary;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SerializationTests {

    ////////////////
    // Attributes //
    ////////////////

    private ObjectMapper objectMapper;

    /////////////////
    // Constructor //
    /////////////////

    public SerializationTests() {
        JsonApiModule jsonApiModule = new JsonApiModule(
                SimpleObject.class,
                SimpleSubObject.class,
                SimpleNestedSubObject.class,
                SingleLinkNode.class,
                ABaseClass.class,
                AChildClass.class);

        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(jsonApiModule);
    }

    ///////////
    // Tests //
    ///////////

    @Test
    public void circularReferenceTest() throws Exception {
        // Init Test Objects //
        SingleLinkNode singleLinkNode1 = new SingleLinkNode();
        SingleLinkNode singleLinkNode2 = new SingleLinkNode();
        SingleLinkNode singleLinkNode3 = new SingleLinkNode();

        singleLinkNode1.setId(1.0);
        singleLinkNode2.setId(2.0);
        singleLinkNode3.setId(3.0);

        singleLinkNode1.setLinkNode(singleLinkNode2);
        singleLinkNode2.setLinkNode(singleLinkNode3);
        singleLinkNode3.setLinkNode(singleLinkNode1);

        // Serialize //
        String json = objectMapper.writeValueAsString(new JsonApiEnvelope<SingleLinkNode>(singleLinkNode1));
        Assert.assertNotNull(json);
        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : {\n" +
                "    \"id\" : \"1.0\",\n" +
                "    \"type\" : \"SingleLinkNode\",\n" +
                "    \"attributes\" : {\n" +
                "      \"value\" : \"Logan was here 1.0 times!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"link\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"2.0\",\n" +
                "          \"type\" : \"SingleLinkNode\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"included\" : [ {\n" +
                "    \"id\" : \"2.0\",\n" +
                "    \"type\" : \"SingleLinkNode\",\n" +
                "    \"attributes\" : {\n" +
                "      \"value\" : \"Logan was here 2.0 times!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"link\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"3.0\",\n" +
                "          \"type\" : \"SingleLinkNode\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"3.0\",\n" +
                "    \"type\" : \"SingleLinkNode\",\n" +
                "    \"attributes\" : {\n" +
                "      \"value\" : \"Logan was here 3.0 times!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"link\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"1.0\",\n" +
                "          \"type\" : \"SingleLinkNode\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void complexRelationshipSerializationTest() throws Exception {
        SimpleNestedSubObject simpleNestedSubObject = new SimpleNestedSubObject();
        simpleNestedSubObject.setIid(54321l);
        simpleNestedSubObject.setNestedThing("qwerty");

        SimpleSubObject simpleSubObject = new SimpleSubObject(8);
        simpleSubObject.setId("AnID");
        simpleSubObject.setNestedThing(simpleNestedSubObject);
        long counter = 1;
        for(SimpleNestedSubObject nestedSubObject : simpleSubObject.getNestedThings()) {
            nestedSubObject.setIid(counter++);
            nestedSubObject.setNestedThing("Loooooooogan #" + counter);
        }

        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setId(9876l);
        simpleObject.setAttribute("AtTrIbUtE!");
        simpleObject.setThing2(simpleSubObject);

        SimpleObject simpleObject2 = new SimpleObject();
        simpleObject2.setId(4567l);
        simpleObject2.setAttribute("simpleObject2");
        simpleNestedSubObject.setMetaThing(simpleObject2);

        // First Serialize //
        String json = objectMapper.writeValueAsString(new JsonApiEnvelope<SimpleObject>(simpleObject));
        Assert.assertNotNull(json);
        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : {\n" +
                "    \"id\" : \"9876\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"AtTrIbUtE!\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"someOtherRelationship\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"AnID\",\n" +
                "          \"type\" : \"TypeOverride\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"included\" : [ {\n" +
                "    \"id\" : \"AnID\",\n" +
                "    \"type\" : \"TypeOverride\",\n" +
                "    \"attributes\" : {\n" +
                "      \"BAZ\" : \"...BAZ...! 8\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"NESTEDnestedNESTED\" : {\n" +
                "        \"data\" : [ {\n" +
                "          \"id\" : \"1\",\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : \"2\",\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : \"3\",\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : \"4\",\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : \"5\",\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : \"6\",\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : \"7\",\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        }, {\n" +
                "          \"id\" : \"8\",\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        } ]\n" +
                "      },\n" +
                "      \"SINGLEnestedTHING\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"54321\",\n" +
                "          \"type\" : \"SimpleNestedSubObject\"\n" +
                "        },\n" +
                "        \"meta\" : {\n" +
                "          \"SOMEmetaSIMPLEobjectttttt\" : {\n" +
                "            \"objectId\" : 4567,\n" +
                "            \"objectAttribute\" : \"simpleObject2\",\n" +
                "            \"ignoredThing\" : \"THIS SHOULDN'T BE HERE\",\n" +
                "            \"objectBlah\" : \"blah!\",\n" +
                "            \"catchAllThing\" : \"this should ahve been caught\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"54321\",\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"qwerty\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"SOMEmetaSIMPLEobjectttttt\" : {\n" +
                "        \"objectId\" : 4567,\n" +
                "        \"objectAttribute\" : \"simpleObject2\",\n" +
                "        \"ignoredThing\" : \"THIS SHOULDN'T BE HERE\",\n" +
                "        \"objectBlah\" : \"blah!\",\n" +
                "        \"catchAllThing\" : \"this should ahve been caught\"\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"1\",\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #2\"\n" +
                "    },\n" +
                "    \"meta\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"2\",\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #3\"\n" +
                "    },\n" +
                "    \"meta\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"3\",\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #4\"\n" +
                "    },\n" +
                "    \"meta\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"4\",\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #5\"\n" +
                "    },\n" +
                "    \"meta\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"5\",\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #6\"\n" +
                "    },\n" +
                "    \"meta\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"6\",\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #7\"\n" +
                "    },\n" +
                "    \"meta\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"7\",\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #8\"\n" +
                "    },\n" +
                "    \"meta\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"8\",\n" +
                "    \"type\" : \"SimpleNestedSubObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"aNestedTHING\" : \"Loooooooogan #9\"\n" +
                "    },\n" +
                "    \"meta\" : { }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void ListSerializationTest() throws Exception {
        // Init Test Object //
        List<SimpleObject> simpleObjectList = new ArrayList<SimpleObject>();
        for(int i1 = 0; i1 < 8; i1++) {
            SimpleObject simpleObject = new SimpleObject();
            simpleObject.setId(Long.valueOf(i1));
            simpleObject.setAttribute("object number " + i1);
            simpleObjectList.add(simpleObject);
        }

        JsonApiEnvelope<List<SimpleObject>> envelope = new JsonApiEnvelope<List<SimpleObject>>(simpleObjectList);

        // Serialize and Validate //
        String json = objectMapper.writeValueAsString(envelope);
        Assert.assertNotNull(json);
        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : [ {\n" +
                "    \"id\" : \"0\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"object number 0\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"1\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"object number 1\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"2\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"object number 2\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"3\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"object number 3\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"4\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"object number 4\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"5\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"object number 5\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"6\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"object number 6\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  }, {\n" +
                "    \"id\" : \"7\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"object number 7\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void topLevelLinksTest() throws Exception{
        // Init test object //
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setId(666l);
        simpleObject.setAttribute("the attribute of the beast.");
        JsonApiEnvelope<SimpleObject> jsonApiEnvelope = new JsonApiEnvelope<SimpleObject>(simpleObject);
        jsonApiEnvelope.addLink("google", new URL("http://www.google.com"));
        jsonApiEnvelope.addLink("what is jsonapi", new URL("https://www.google.com/search?q=jsonapi&ie=utf-8&oe=utf-8"));

        // Serialize and verify //
        String json = objectMapper.writeValueAsString(jsonApiEnvelope);

        Assert.assertNotNull(json);
        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : {\n" +
                "    \"id\" : \"666\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"the attribute of the beast.\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  },\n" +
                "  \"links\" : {\n" +
                "    \"google\" : \"http://www.google.com\",\n" +
                "    \"what is jsonapi\" : \"https://www.google.com/search?q=jsonapi&ie=utf-8&oe=utf-8\"\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void topLevelMetaTest() throws Exception {
        // Init test objects //
        SimpleObject simpleObject1 = new SimpleObject();
        simpleObject1.setId(1l);
        simpleObject1.setAttribute("O.N.E.");
        JsonApiEnvelope<SimpleObject> jsonApiEnvelope = new JsonApiEnvelope<SimpleObject>(simpleObject1);

        jsonApiEnvelope.addMeta("top-LEVEL-meta-THING", "this is a fancy thing!");
        jsonApiEnvelope.addMeta("helllllo", "good! buy!");

        // Serialize and verify //
        String json = objectMapper.writeValueAsString(jsonApiEnvelope);
        Assert.assertNotNull(json);
        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : {\n" +
                "    \"id\" : \"1\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"O.N.E.\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  },\n" +
                "  \"meta\" : {\n" +
                "    \"helllllo\" : \"good! buy!\",\n" +
                "    \"top-LEVEL-meta-THING\" : \"this is a fancy thing!\"\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void inheritanceTest() throws Exception {
        // Init Test Objects //
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setId(314l);
        simpleObject.setAttribute("pi");

        AChildClass aChildClass = new AChildClass();
        aChildClass.setWhoAmI("myId");
        aChildClass.setWhatDoIHave(simpleObject);
        aChildClass.setWhaz("this is whaz!");
        aChildClass.setMetaInt(21);

        // Serialize and Verify //
        String json = objectMapper.writeValueAsString(new JsonApiEnvelope<AChildClass>(aChildClass));
        Assert.assertNotNull(json);
        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : {\n" +
                "    \"id\" : \"myId\",\n" +
                "    \"type\" : \"AcHiLdClAsS\",\n" +
                "    \"attributes\" : {\n" +
                "      \"metaInt\" : 21,\n" +
                "      \"whaz\" : \"this is whaz!\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"jsonApiOverride\" : \"what is a whaz?\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"whatDoIHave\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"314\",\n" +
                "          \"type\" : \"SimpleObject\"\n" +
                "        },\n" +
                "        \"meta\" : {\n" +
                "          \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "          \"objectBlah\" : \"blah!\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"included\" : [ {\n" +
                "    \"id\" : \"314\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"pi\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : { }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void booleanGetterWithIsTest() throws Exception {
        // Init Test Objects //
        TypeWithABoolean typeWithABoolean = new TypeWithABoolean();
        typeWithABoolean.setId(1337);
        typeWithABoolean.setBool(false);

        // Serialize and Verify //
        String json = objectMapper.writeValueAsString(new JsonApiEnvelope<TypeWithABoolean>(typeWithABoolean));
        Assert.assertNotNull(json);
        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : {\n" +
                "    \"id\" : \"1337\",\n" +
                "    \"type\" : \"TypeWithABoolean\",\n" +
                "    \"attributes\" : {\n" +
                "      \"bool\" : false\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void listOfObjectsWithIncludedsTest() throws Exception {
        // Init Test Objects //
        List<SimpleObject> simpleObjectList = new ArrayList<SimpleObject>();
        for(int i1 = 0; i1 < 4; i1++) {
            SimpleSubObject simpleSubObject = new SimpleSubObject();
            simpleSubObject.setId("Object Number " + i1);
            simpleSubObject.setBaz("Baz " + (i1 * i1));

            SimpleObject simpleObject = new SimpleObject();
            simpleObject.setId((i1 * 10l));
            simpleObject.setAttribute("an attribute!");
            simpleObject.setThing2(simpleSubObject);

            simpleObjectList.add(simpleObject);
        }

        // Serialize and Verify //
        String json  = objectMapper.writeValueAsString(new JsonApiEnvelope<List<SimpleObject>>(simpleObjectList));
        Assert.assertNotNull(json);
        Assert.assertTrue(json.equals("{\n" +
                "  \"data\" : [ {\n" +
                "    \"id\" : \"0\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"an attribute!\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"someOtherRelationship\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"Object Number 0\",\n" +
                "          \"type\" : \"TypeOverride\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"10\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"an attribute!\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"someOtherRelationship\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"Object Number 1\",\n" +
                "          \"type\" : \"TypeOverride\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"20\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"an attribute!\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"someOtherRelationship\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"Object Number 2\",\n" +
                "          \"type\" : \"TypeOverride\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"30\",\n" +
                "    \"type\" : \"SimpleObject\",\n" +
                "    \"attributes\" : {\n" +
                "      \"objectAttribute\" : \"an attribute!\"\n" +
                "    },\n" +
                "    \"meta\" : {\n" +
                "      \"catchAllThing\" : \"this should ahve been caught\",\n" +
                "      \"objectBlah\" : \"blah!\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"someOtherRelationship\" : {\n" +
                "        \"data\" : {\n" +
                "          \"id\" : \"Object Number 3\",\n" +
                "          \"type\" : \"TypeOverride\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  } ],\n" +
                "  \"included\" : [ {\n" +
                "    \"id\" : \"Object Number 3\",\n" +
                "    \"type\" : \"TypeOverride\",\n" +
                "    \"attributes\" : {\n" +
                "      \"BAZ\" : \"Baz 9\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"NESTEDnestedNESTED\" : {\n" +
                "        \"data\" : [ ]\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"Object Number 2\",\n" +
                "    \"type\" : \"TypeOverride\",\n" +
                "    \"attributes\" : {\n" +
                "      \"BAZ\" : \"Baz 4\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"NESTEDnestedNESTED\" : {\n" +
                "        \"data\" : [ ]\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"Object Number 0\",\n" +
                "    \"type\" : \"TypeOverride\",\n" +
                "    \"attributes\" : {\n" +
                "      \"BAZ\" : \"Baz 0\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"NESTEDnestedNESTED\" : {\n" +
                "        \"data\" : [ ]\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"id\" : \"Object Number 1\",\n" +
                "    \"type\" : \"TypeOverride\",\n" +
                "    \"attributes\" : {\n" +
                "      \"BAZ\" : \"Baz 1\"\n" +
                "    },\n" +
                "    \"relationships\" : {\n" +
                "      \"NESTEDnestedNESTED\" : {\n" +
                "        \"data\" : [ ]\n" +
                "      }\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

}
