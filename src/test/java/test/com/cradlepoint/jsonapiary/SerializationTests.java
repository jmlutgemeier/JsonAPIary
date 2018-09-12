package test.com.cradlepoint.jsonapiary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.SimpleObject;

public class SerializationTests {

    ////////////////
    // Attributes //
    ////////////////

    private ObjectMapper objectMapper;

    /////////////////
    // Constructor //
    /////////////////

    public SerializationTests() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonApiModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    public void passingTest() throws Exception {
        // Init Test Objects //
        SimpleObject simpleObject = new SimpleObject();

        // Call and Verify //
        String json = objectMapper.writeValueAsString(simpleObject);

        Assert.assertNotNull(json);
        Assert.assertFalse(json.isEmpty());
    }

    @Test
    public void nonJsonApiTest() throws Exception {
        String json = objectMapper.writeValueAsString(new SimpleObject());
        Assert.assertNotNull(json);
    }

    @Test
    public void envelopeTest() throws Exception {
        // Init Test Objects //
        SimpleObject simpleObject = new SimpleObject(5);
        simpleObject.getThing2().setThing2(new SimpleObject());
        JsonApiEnvelope<SimpleObject> envelope = new JsonApiEnvelope<SimpleObject>(simpleObject);

        // Call and Verify //
        String json = objectMapper.writeValueAsString(envelope);

        Assert.assertNotNull(json);
        Assert.assertFalse(json.isEmpty());
        System.out.println("\n\n\n" + json + "\n\n\n");
    }

}
