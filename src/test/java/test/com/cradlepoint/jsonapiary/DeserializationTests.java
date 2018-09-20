package test.com.cradlepoint.jsonapiary;

import com.cradlepoint.jsonapiary.JsonApiModule;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import test.com.cradlepoint.jsonapiary.pojos.SimpleObject;

import java.util.List;

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
                SimpleObject.class);

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(jsonApiModule);
    }

    ///////////
    // Tests //
    ///////////

    @Test
    public void aPassingDeserializationTest() throws Exception { }

    @Test
    public void deserializationTest() throws Exception {
        String json = "{\"objectId\":123}";
        SimpleObject simpleObject = objectMapper.readValue(json, SimpleObject.class);
        Assert.assertNotNull(simpleObject);
    }

    @Test
    public void listDeserializationTest() throws Exception {
        String json = "[{\"objectId\":123}]";

        JavaType javaType = objectMapper.getTypeFactory().constructCollectionLikeType(List.class, SimpleObject.class);
        List<SimpleObject> simpleObjectJsonApiEnvelope = objectMapper.readValue(json, javaType);

        Assert.assertNotNull(simpleObjectJsonApiEnvelope);
    }

    @Test
    public void envelopeDeserializationTest() throws Exception {
        String json = "{\"data\":{\"id\":123,\"type\":\"SimpleObject\",\"attributes\":{\"objectAttribute\":\"logan's attribute!\"}}}";

        JavaType javaType = objectMapper.getTypeFactory().constructParametrizedType(JsonApiEnvelope.class, JsonApiEnvelope.class, SimpleObject.class);
        JsonApiEnvelope<SimpleObject> simpleObjectJsonApiEnvelope = objectMapper.readValue(json, javaType);

        Assert.assertNotNull(simpleObjectJsonApiEnvelope);
    }

}
