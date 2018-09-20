package com.cradlepoint.jsonapiary.deserializers.helpers;

import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;
import com.cradlepoint.jsonapiary.deserializers.ResourceLinkage;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DataObjectDeserializer {

    /////////////////
    // Constructor //
    /////////////////

    /**
     * private void constructor
     */
    private DataObjectDeserializer() { }

    ////////////////////
    // Public Methods //
    ////////////////////

    public static <T> List<T> deserializeDataObjectList(Class<? extends T> dataType, JsonNode jsonNode) {
        return null;
    }

    public static void deserializeDataObject(
            Class dataType,
            Object dataObject,
            JsonNode dataNode,
            Map<String, Class> jsonApiTypeMap,
            Map<ResourceLinkage, JsonNode> includedsSet,
            Map<ResourceLinkage, Object> includeds,
            DeserializationContext deserializationContext) throws IOException {
        // Fetch the JsonAPI nodes //
        JsonNode attributesNode = dataNode.get(JsonApiKeyConstants.ATTRIBUTES_KEY);
        JsonNode linksNode = dataNode.get(JsonApiKeyConstants.LINKS_KEY);
        JsonNode metaNode = dataNode.get(JsonApiKeyConstants.META_DATA_KEY);
        JsonNode relationshipsNode = dataNode.get(JsonApiKeyConstants.RELATIONSHIPS_KEY);

        // Populate from Attributes //
        if(attributesNode != null) {
            AttributesDeserializer.deserializeAttributesInto(
                    dataType,
                    dataObject,
                    attributesNode,
                    deserializationContext);
        }

        // Populate from Links //
        if(linksNode != null) {
            // TODO: do something here!
        }

        // Populate from Meta //
        if(metaNode != null) {
            MetaDeserializer.deserializeMetaInto(
                    dataType,
                    dataObject,
                    attributesNode,
                    deserializationContext);
        }

        // Populate from Relationships //
        if(relationshipsNode != null) {
            RelationshipsManager.deserializeRelationshipsInto(
                    dataType,
                    dataObject,
                    relationshipsNode,
                    jsonApiTypeMap,
                    includedsSet,
                    includeds,
                    deserializationContext);
        }
    }

}
