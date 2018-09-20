package com.cradlepoint.jsonapiary.deserializers.helpers;

import com.cradlepoint.jsonapiary.annotations.JsonApiRelationship;
import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;
import com.cradlepoint.jsonapiary.deserializers.ResourceLinkage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

public class RelationshipsManager {

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Private void constructor
     */
    private RelationshipsManager() { }

    ////////////////////
    // Public Methods //
    ////////////////////

    public static Map<ResourceLinkage, JsonNode> generateSetOfIncludeds(
            JsonNode includedsNode,
            Map<String, Class> jsonApiTypeMap) {
        Map<ResourceLinkage, JsonNode> includedsSet = new Hashtable<ResourceLinkage, JsonNode>();
        if(includedsNode != null) {
            for (JsonNode includedNode : includedsNode) {
                includedsSet.put(
                        DeserializationUtilities.generateResourceLinkageFromNode(includedNode, jsonApiTypeMap),
                        includedNode);
            }
        }

        return includedsSet;
    }

    public static void deserializeRelationshipsInto(
            Class dataType,
            Object dataObject,
            JsonNode relationshipsNode,
            Map<String, Class> jsonApiTypeMap,
            Map<ResourceLinkage, JsonNode> includedsSet,
            Map<ResourceLinkage, Object> includeds,
            DeserializationContext deserializationContext) throws IOException {
        // Fetch the Relationship Fields on the Type //
        for(Field field : dataType.getDeclaredFields()) {
            if(field.isAnnotationPresent(JsonApiRelationship.class) && field.isAnnotationPresent(JsonProperty.class)) {
                // Fetch the Serialization/Deserialization key //
                String relationshipKey = DeserializationUtilities.getFieldJsonKey(field);

                // Check to see if that object exists in the json //
                JsonNode relationshipNode = relationshipsNode.get(relationshipKey);
                if(relationshipNode == null || relationshipNode.isNull()) {
                    // Nothing to see here...
                    continue;
                }

                // Process based on Data, Link, or Meta //
                if(relationshipNode.has(JsonApiKeyConstants.DATA_KEY)) {
                    // Generate Relationship Linkage from the Data json //
                    ResourceLinkage relationshipResourceLinkage = DeserializationUtilities.generateResourceLinkageFromNode(
                            relationshipNode.get(JsonApiKeyConstants.DATA_KEY), jsonApiTypeMap);

                    if(!includedsSet.containsKey(relationshipResourceLinkage)) {
                        // Relationship was not included in the JSON.. leave it as null //
                        continue;
                    }

                    // Generate the Relationship Object //
                    Object relationshipObject = processRelationshipData(
                            relationshipKey,
                            relationshipNode,
                            jsonApiTypeMap,
                            includedsSet,
                            includeds,
                            deserializationContext);

                    // Set the Relationship Object on the Data object! //
                    if(relationshipObject != null) {
                        DeserializationUtilities.setObjectOnField(
                                field,
                                relationshipObject,
                                dataObject);
                    }
                    continue;
                }

                if(relationshipNode.has(JsonApiKeyConstants.LINKS_KEY)) {
                    // TODO: Links needs wiring up!
                }

                if(relationshipNode.has(JsonApiKeyConstants.META_DATA_KEY)) {
                    // TODO: Meta needs wiring up!
                }
            }
        }

        // Then, fetch the Relationship Methods on the Type //
        for(Method method : dataType.getDeclaredMethods()) {
            if(method.getName().startsWith("set") &&
                    method.isAnnotationPresent(JsonApiRelationship.class) &&
                    method.isAnnotationPresent(JsonProperty.class)) {
                // Fetch the Serialization/Deserialization key //
                String relationshipKey = DeserializationUtilities.getMethodJsonKey(method);

                // Check to see if that object exists in the json //
                JsonNode relationshipNode = relationshipsNode.get(relationshipKey);
                if(relationshipNode == null || relationshipNode.isNull()) {
                    // Nothing to see here...
                    continue;
                }

                if(relationshipNode.has(JsonApiKeyConstants.DATA_KEY)) {
                    // Generate Relationship Linkage from the Data json //
                    ResourceLinkage relationshipResourceLinkage = DeserializationUtilities.generateResourceLinkageFromNode(
                            relationshipNode.get(JsonApiKeyConstants.DATA_KEY), jsonApiTypeMap);

                    if(!includedsSet.containsKey(relationshipResourceLinkage)) {
                        // Relationship was not included in the JSON.. leave it  as null //
                        continue;
                    }

                    // Generate the Relationship Object //
                    Object relationshipObject = processRelationshipData(
                            relationshipKey,
                            relationshipNode,
                            jsonApiTypeMap,
                            includedsSet,
                            includeds,
                            deserializationContext);

                    // Set the Relationship Object on the Data object! //
                    DeserializationUtilities.setObjectOnMethod(
                            method,
                            relationshipObject,
                            dataObject);
                    continue;
                }

                if(relationshipNode.has(JsonApiKeyConstants.LINKS_KEY)) {
                    // TODO: Links needs wiring up!
                }

                if(relationshipNode.has(JsonApiKeyConstants.META_DATA_KEY)) {

                }
            }
        }
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private static void deserializeIncluded(
            ResourceLinkage resourceLinkage,
            JsonNode includedNode,
            Map<String, Class> jsonApiTypeMap,
            Map<ResourceLinkage, JsonNode> includedsSet,
            Map<ResourceLinkage, Object> includeds,
            DeserializationContext deserializationContext) throws IOException {
        // Sanity Check //
        if(includeds.containsKey(resourceLinkage)) {
            // This Included Node has already be created/deserialized...
            return;
        }

        // Generate the Target Object and add it to the Map //
        Object includedObject = DeserializationUtilities.generateObjectFromNode(includedNode, jsonApiTypeMap);
        includeds.put(resourceLinkage, includedObject);

        // Fetch the JsonAPI nodes //
        JsonNode attributesNode = includedNode.get(JsonApiKeyConstants.ATTRIBUTES_KEY);
        JsonNode linksNode = includedNode.get(JsonApiKeyConstants.LINKS_KEY);
        JsonNode metaNode = includedNode.get(JsonApiKeyConstants.META_DATA_KEY);
        JsonNode relationshipsNode = includedNode.get(JsonApiKeyConstants.RELATIONSHIPS_KEY);

        // Populate from Attributes //
        if(attributesNode != null) {
            AttributesDeserializer.deserializeAttributesInto(
                    resourceLinkage.getType(),
                    includedObject,
                    attributesNode,
                    deserializationContext);
        }

        // Populate Links //
        if(linksNode != null) {
            // TODO: do something here!
        }

        // Populate from Meta //
        if(metaNode != null) {
            MetaDeserializer.deserializeMetaInto(
                    resourceLinkage.getType(),
                    includedObject,
                    attributesNode,
                    deserializationContext);
        }

        // Populate the Relationships //
        if(relationshipsNode != null) {
            deserializeRelationshipsInto(
                    resourceLinkage.getType(),
                    includedObject,
                    relationshipsNode,
                    jsonApiTypeMap,
                    includedsSet,
                    includeds,
                    deserializationContext);
        }
    }

    private static Object processRelationshipData(
            String relationshipKey,
            JsonNode relationshipNode,
            Map<String, Class> jsonApiTypeMap,
            Map<ResourceLinkage, JsonNode> includedsSet,
            Map<ResourceLinkage, Object> includeds,
            DeserializationContext deserializationContext) throws IOException {
        // Generate Relationship Linkage from the Data json //
        ResourceLinkage relationshipResourceLinkage = DeserializationUtilities.generateResourceLinkageFromNode(
                relationshipNode.get(JsonApiKeyConstants.DATA_KEY), jsonApiTypeMap);

        // Generate the Relationship Object //
        Object relationshipObject = null;
        if(includeds.containsKey(relationshipResourceLinkage)) {
            relationshipObject = includeds.get(relationshipResourceLinkage);
        } else {
            // RECURSE! //
            deserializeIncluded(
                    relationshipResourceLinkage,
                    includedsSet.get(relationshipResourceLinkage),
                    jsonApiTypeMap,
                    includedsSet,
                    includeds,
                    deserializationContext);

            if(!includeds.containsKey(relationshipResourceLinkage)) {
                String issue = "Somehow, we're reached a point where we expect an Included (with key: " +
                        relationshipKey + ") to have been a deserialized object... but it is not there!";
                throw new IllegalStateException(issue);
            }

            relationshipObject = includeds.get(relationshipResourceLinkage);
        }

        return relationshipObject;
    }

}
