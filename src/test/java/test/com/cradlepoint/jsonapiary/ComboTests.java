package test.com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.*;

import java.net.URL;

public class ComboTests {

    ////////////////
    // Attributes //
    ////////////////

    private ObjectMapper objectMapper;

    /////////////////
    // Constructor //
    /////////////////

    public ComboTests() {
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
    public void simpleRelationshipTest() throws Exception {
        SimpleObject simpleObject = new SimpleObject();
        SimpleSubObject simpleSubObject = new SimpleSubObject();
        simpleObject.setThing2(simpleSubObject);

        // First Serialize //
        String serialization = objectMapper.writeValueAsString(new JsonApiEnvelope<SimpleObject>(simpleObject));

        // Then try to Deserialize the output back! //
        JsonApiEnvelope<SimpleObject> deserializedObject = objectMapper.readValue(serialization, JsonApiEnvelope.class);

        Assert.assertTrue(simpleObject.equals(deserializedObject.getData()));
    }

    @Test
    public void emptyRelationshipListTest() throws Exception {
        SimpleSubObject simpleSubObject = new SimpleSubObject();
        SimpleNestedSubObject simpleNestedSubObject = new SimpleNestedSubObject();
        simpleNestedSubObject.setMetaThing(new SimpleObject());
        simpleSubObject.setNestedThing(simpleNestedSubObject);

        // First Serialize //
        String serialization = objectMapper.writeValueAsString(new JsonApiEnvelope<SimpleSubObject>(simpleSubObject));

        // Then try to Deserialize the output back! //
        JsonApiEnvelope<SimpleSubObject> deserializedObject = objectMapper.readValue(serialization, JsonApiEnvelope.class);

        Assert.assertTrue(simpleSubObject.equals(deserializedObject.getData()));
    }

    @Test
    public void complexRelationshipTest() throws Exception {
        SimpleNestedSubObject simpleNestedSubObject = new SimpleNestedSubObject();

        SimpleSubObject simpleSubObject = new SimpleSubObject(8);
        simpleSubObject.setNestedThing(simpleNestedSubObject);

        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setThing2(simpleSubObject);

        simpleNestedSubObject.setMetaThing(new SimpleObject());

        // First Serialize //
        String serialization = objectMapper.writeValueAsString(new JsonApiEnvelope<SimpleObject>(simpleObject));

        // Then try to Deserialize the output back! //
        JsonApiEnvelope<SimpleObject> deserializedObject = objectMapper.readValue(serialization, JsonApiEnvelope.class);

        Assert.assertTrue(simpleObject.equals(deserializedObject.getData()));
    }

    @Test
    public void topLevelLinksAndMetaTest() throws Exception {
        // Init test objects //
        SimpleObject simpleObject1 = new SimpleObject();
        simpleObject1.setId(1l);
        simpleObject1.setAttribute("number: O.N.E.");
        JsonApiEnvelope<SimpleObject> jsonApiEnvelope = new JsonApiEnvelope<SimpleObject>(simpleObject1);

        jsonApiEnvelope.addMeta("top-LEVEL-meta-THING", "this is a fancy thing!");
        jsonApiEnvelope.addMeta("helllllo", "good! buy!");

        jsonApiEnvelope.addLink("google", new URL("http://www.google.com"));
        jsonApiEnvelope.addLink("jsonapi", new URL("http://jsonapi.org/"));

        // First, serialize //
        String json = objectMapper.writeValueAsString(jsonApiEnvelope);
        Assert.assertNotNull(json);

        // Then, try to deserialize back into an equal Object //
        JsonApiEnvelope<SimpleObject> deserializedObject = objectMapper.readValue(json, JsonApiEnvelope.class);
        Assert.assertNotNull(deserializedObject);
        Assert.assertTrue(jsonApiEnvelope.equals(deserializedObject));
    }

    @Test
    public void inheritanceTest() throws Exception {
        // Init Test Objects //
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setId(314l);
        simpleObject.setAttribute("pi");

        AChildClass aChildClass = new AChildClass();
        aChildClass.setWhoAmI("my(ComboTests)Id");
        aChildClass.setWhatDoIHave(simpleObject);
        aChildClass.setWhaz("this is (ComboTests) whaz!");
        aChildClass.setMetaInt(210);
        JsonApiEnvelope<AChildClass> jsonApiEnvelope = new JsonApiEnvelope<AChildClass>(aChildClass);

        // First, serialize //
        String json = objectMapper.writeValueAsString(jsonApiEnvelope);
        Assert.assertNotNull(json);

        // Then, attempt to deserialize and verify //
        JsonApiEnvelope<AChildClass> deserializedObject = objectMapper.readValue(json, JsonApiEnvelope.class);
        Assert.assertNotNull(deserializedObject);
        Assert.assertTrue(jsonApiEnvelope.equals(deserializedObject));
    }

}
