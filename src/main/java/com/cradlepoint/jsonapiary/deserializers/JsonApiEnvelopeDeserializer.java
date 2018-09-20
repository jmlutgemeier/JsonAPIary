package com.cradlepoint.jsonapiary.deserializers;

import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;
import com.cradlepoint.jsonapiary.deserializers.helpers.DataObjectDeserializer;
import com.cradlepoint.jsonapiary.deserializers.helpers.DeserializationUtilities;
import com.cradlepoint.jsonapiary.deserializers.helpers.RelationshipsManager;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Hashtable;
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

        // Generate the Set of Includeds //
        Map<ResourceLinkage, JsonNode> includedsSet = RelationshipsManager.generateSetOfIncludeds(
                includedNode,
                jsonApiTypeMap);
        Map<ResourceLinkage, Object> includeds = new Hashtable<ResourceLinkage, Object>();

        // Process the "data" block //
        Object dataObject = null;
        if(dataNode.isArray()) {
            // Add "primary" list contents into the includeds, in case of circular references... //
            for(JsonNode primaryDataArrayElement : dataNode) {
                ResourceLinkage primaryArrayElementResourceLinkage =
                        DeserializationUtilities.generateResourceLinkageFromNode(primaryDataArrayElement, jsonApiTypeMap);
                includedsSet.put(primaryArrayElementResourceLinkage, primaryDataArrayElement);

                dataObject = DeserializationUtilities.generateObjectFromNode(primaryDataArrayElement, jsonApiTypeMap);
                includeds.put(primaryArrayElementResourceLinkage, dataObject);
            }

            // Deserialize the Things! //
            for(JsonNode primaryDataArrayElement : dataNode) {
                ResourceLinkage primaryArrayElementResourceLinkage =
                        DeserializationUtilities.generateResourceLinkageFromNode(primaryDataArrayElement, jsonApiTypeMap);

                DataObjectDeserializer.deserializeDataObject(
                        primaryArrayElementResourceLinkage.getType(),
                        includeds.get(primaryArrayElementResourceLinkage),
                        primaryDataArrayElement,
                        jsonApiTypeMap,
                        includedsSet,
                        includeds,
                        deserializationContext);
            }
        } else {
            // Add the "primary" object to the includeds... //
            ResourceLinkage primaryObjectResourceLinkage =
                    DeserializationUtilities.generateResourceLinkageFromNode(dataNode, jsonApiTypeMap);
            includedsSet.put(primaryObjectResourceLinkage, dataNode);

            dataObject = DeserializationUtilities.generateObjectFromNode(dataNode, jsonApiTypeMap);
            includeds.put(primaryObjectResourceLinkage, dataObject);

            // Deserialize the thing! //
            DataObjectDeserializer.deserializeDataObject(
                    dataObject.getClass(),
                    dataObject,
                    dataNode,
                    jsonApiTypeMap,
                    includedsSet,
                    includeds,
                    deserializationContext);
        }

        return new JsonApiEnvelope(dataObject);
    }

}
