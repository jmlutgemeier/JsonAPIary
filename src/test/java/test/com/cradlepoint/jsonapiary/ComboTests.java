package test.com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.SimpleNestedSubObject;
import test.com.cradlepoint.jsonapiary.pojos.SimpleObject;
import test.com.cradlepoint.jsonapiary.pojos.SimpleSubObject;

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
                SimpleNestedSubObject.class);

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
        simpleSubObject.setNestedThings(null);
        simpleObject.setThing2(simpleSubObject);

        // First Serialize //
        String serialization = objectMapper.writeValueAsString(new JsonApiEnvelope<SimpleObject>(simpleObject));
        System.out.println("\n\n" + serialization + "\n\n");

        // Then try to Deserialize the output back! //
        JsonApiEnvelope<SimpleObject> deserializedObject = objectMapper.readValue(serialization, JsonApiEnvelope.class);

        Assert.assertNotNull(deserializedObject);
    }

    @Test
    public void complexRelationshipTest() throws Exception {
        SimpleNestedSubObject simpleNestedSubObject = new SimpleNestedSubObject();

        SimpleSubObject simpleSubObject = new SimpleSubObject();
        simpleSubObject.setNestedThing(simpleNestedSubObject);

        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setThing2(simpleSubObject);

        simpleNestedSubObject.setCir(new SimpleObject());

        // First Serialize //
        String serialization = objectMapper.writeValueAsString(new JsonApiEnvelope<SimpleObject>(simpleObject));
        System.out.println("\n\n" + serialization + "\n\n");

        // Then try to Deserialize the output back! //
        JsonApiEnvelope<SimpleObject> deserializedObject = objectMapper.readValue(serialization, JsonApiEnvelope.class);

        Assert.assertTrue(simpleObject.equals(deserializedObject.getData()));
    }

}
