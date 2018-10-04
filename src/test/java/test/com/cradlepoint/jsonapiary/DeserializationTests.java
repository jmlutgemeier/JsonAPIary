package test.com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.*;

public class DeserializationTests {

    ////////////////
    // Attributes //
    ////////////////

    private ObjectMapper objectMapper;

    /////////////////
    // Constructor //
    /////////////////

    public DeserializationTests() {
        JsonApiModule jsonApiModule = new JsonApiModule(
                SimpleObject.class,
                SimpleSubObject.class,
                SimpleNestedSubObject.class,
                SingleLinkNode.class,
                ABaseClass.class,
                AChildClass.class,
                TypeWithABoolean.class);

        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(jsonApiModule);
    }

    ///////////
    // Tests //
    ///////////

    @Test
    public void deserializeObjectWithoutIdTest() throws Exception {
        // Init Test Objects //
        String json = "{\n" +
                "  \"data\" : {\n" +
                "    \"type\" : \"TypeWithABoolean\",\n" +
                "    \"attributes\" : {\n" +
                "      \"bool\" : false\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // Deserialize and Verify //
        JsonApiEnvelope<TypeWithABoolean> jsonApiEnvelope = objectMapper.readValue(json, JsonApiEnvelope.class);
        Assert.assertNotNull(jsonApiEnvelope);
        Assert.assertNotNull(jsonApiEnvelope.getData());
        Assert.assertEquals(false, jsonApiEnvelope.getData().isBool());
    }
}
