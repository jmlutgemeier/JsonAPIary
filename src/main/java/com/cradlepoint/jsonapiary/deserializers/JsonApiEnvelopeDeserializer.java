package com.cradlepoint.jsonapiary.deserializers;

import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonApiEnvelopeDeserializer extends StdDeserializer<JsonApiEnvelope> {

    ////////////////
    // Attributes //
    ////////////////

    private Map<String, Class> jsonApiTypeMap;

    /////////////////
    // Constructor //
    /////////////////

    /**
     * private void constructor
     */
    private JsonApiEnvelopeDeserializer() {
        super(JsonApiEnvelope.class);
    }

    /**
     * Default constructor taking in JsonAPI type map
     * @param jsonApiTypeMap
     */
    public JsonApiEnvelopeDeserializer(
            Map<String, Class> jsonApiTypeMap) {
        super(JsonApiEnvelope.class);
        this.jsonApiTypeMap = jsonApiTypeMap;
    }

    /////////////////////////////
    // StdDeserializer Methods //
    /////////////////////////////

    /**
     * Deserializes json into enveloped, and properly annotated, new object
     * @param jsonParser
     * @param deserializationContext
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     */
    public JsonApiEnvelope deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        // Convert to tree //
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        // Fetch the Data, Included, and Meta nodes... //
        JsonNode dataNode = rootNode.get(JsonApiKeyConstants.DATA_KEY);
        JsonNode includedNode = rootNode.get(JsonApiKeyConstants.INCLUDED_KEY);
        JsonNode metaNode = rootNode.get(JsonApiKeyConstants.META_DATA_KEY);

        // Bootstrap a new JsonApiObjectManager //
        JsonApiObjectManager jsonApiObjectManager = new JsonApiObjectManager(
                dataNode,
                includedNode,
                jsonApiTypeMap);

        // Loop through processing and collecting all the primary object(s) //
        Object dataObject = null;
        List<ResourceLinkage> dataObjectResourceLinkages = jsonApiObjectManager.getPrimaryObjects();
        if(dataNode.isArray()) {
            List<Object> dataObjects = new ArrayList<Object>();
            for(ResourceLinkage dataObjectResourceLinkage : dataObjectResourceLinkages) {
                dataObjects.add(
                        jsonApiObjectManager.lazyFetchObject(dataObjectResourceLinkage, deserializationContext));
            }
            dataObject = dataObjects;
        } else {
            dataObject = jsonApiObjectManager.lazyFetchObject(dataObjectResourceLinkages.get(0), deserializationContext);
        }

        return new JsonApiEnvelope(dataObject);
    }

}
