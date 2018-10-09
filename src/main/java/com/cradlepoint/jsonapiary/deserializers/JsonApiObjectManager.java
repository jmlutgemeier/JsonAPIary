package com.cradlepoint.jsonapiary.deserializers;

import com.cradlepoint.jsonapiary.annotations.JsonApiRelationship;
import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;
import com.cradlepoint.jsonapiary.deserializers.helpers.AttributesDeserializer;
import com.cradlepoint.jsonapiary.deserializers.helpers.DeserializationUtilities;
import com.cradlepoint.jsonapiary.deserializers.helpers.MetaDeserializer;
import com.cradlepoint.jsonapiary.types.ResourceLinkage;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

class JsonApiObjectManager {

    ////////////////
    // Attributes //
    ////////////////

    private List<ResourceLinkage> primaryObjects;
    private Map<ResourceLinkage, JsonNode> includedsSet;
    private Map<ResourceLinkage, Object> includeds;
    private Map<String, Class> jsonApiTypeMap;

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Private void constructor
     */
    private JsonApiObjectManager() { }

    /**
     * Constructor
     * @param dataNode
     * @param includedsNode
     * @param jsonApiTypeMap
     */
    public JsonApiObjectManager(
            JsonNode dataNode,
            JsonNode includedsNode,
            Map<String, Class> jsonApiTypeMap) {
        // Sanity Check //
        if(dataNode == null || dataNode.isNull()) {
            String issue = "Somehow the \"data\" object in the Json is null! Cannot deserialize!";
            throw new IllegalArgumentException(issue);
        }

        // Load the primary "data" object(s) //
        primaryObjects = new ArrayList<ResourceLinkage>();
        includedsSet = new Hashtable<ResourceLinkage, JsonNode>();
        if(dataNode.isArray()) {
            for(JsonNode dataArrayElementNode : dataNode) {
                ResourceLinkage resourceLinkage =
                        DeserializationUtilities.generateResourceLinkageFromNode(dataArrayElementNode, jsonApiTypeMap);
                includedsSet.put(
                        resourceLinkage,
                        dataArrayElementNode);
                primaryObjects.add(resourceLinkage);
            }
        } else {
            ResourceLinkage resourceLinkage =
                    DeserializationUtilities.generateResourceLinkageFromNode(dataNode, jsonApiTypeMap);
            includedsSet.put(
                    resourceLinkage,
                    dataNode);
            primaryObjects.add(resourceLinkage);
        }

        // Load all of the includeds //
        if(includedsNode != null && !(includedsNode.isNull())) {
            for (JsonNode includedNode : includedsNode) {
                includedsSet.put(
                        DeserializationUtilities.generateResourceLinkageFromNode(includedNode, jsonApiTypeMap),
                        includedNode);
            }
        }

        includeds = new Hashtable<ResourceLinkage, Object>();
        this.jsonApiTypeMap = jsonApiTypeMap;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Fetch the "primary" Objects from the json's "data" Object
     * @return
     */
    public List<ResourceLinkage> getPrimaryObjects() {
        return this.primaryObjects;
    }

    /**
     * Lazily fetches an object from the json from a ResourceLinkage. If that object is not present in the json,
     * null is returned.
     * @param resourceLinkage
     * @param deserializationContext
     * @return
     * @throws IOException
     */
    public Object lazyFetchObject(
            ResourceLinkage resourceLinkage,
            DeserializationContext deserializationContext) throws IOException {
        // Sanity Check... //
        if(!includedsSet.containsKey(resourceLinkage)) {
            return null;
        }

        // First, check to see if we've already generated this thing... //
        if(includeds.containsKey(resourceLinkage)) {
            return includeds.get(resourceLinkage);
        }

        // Generate the Object, and deserialize into it! //
        JsonNode objectNode = includedsSet.get(resourceLinkage);
        Object object = DeserializationUtilities.generateObjectFromNode(objectNode, jsonApiTypeMap);

        deserializeInto(
                object,
                objectNode,
                deserializationContext);

        return object;
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Deserializes json into the passed in Object
     * @param object
     * @param objectNode
     * @param deserializationContext
     * @throws IOException
     */
    private void deserializeInto(
            Object object,
            JsonNode objectNode,
            DeserializationContext deserializationContext) throws IOException {
        // Fetch the JsonAPI nodes //
        JsonNode attributesNode = objectNode.get(JsonApiKeyConstants.ATTRIBUTES_KEY);
        JsonNode linksNode = objectNode.get(JsonApiKeyConstants.LINKS_KEY);
        JsonNode metaNode = objectNode.get(JsonApiKeyConstants.META_DATA_KEY);
        JsonNode relationshipsNode = objectNode.get(JsonApiKeyConstants.RELATIONSHIPS_KEY);

        // Populate from Attributes //
        if(attributesNode != null) {
            AttributesDeserializer.deserializeAttributesInto(
                    object,
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
                    object,
                    metaNode,
                    deserializationContext);
        }

        // Populate from Relationships //
        if(relationshipsNode != null) {
            deserializeRelationshipsInto(
                    object,
                    relationshipsNode,
                    deserializationContext);
        }
    }

    /**
     * Deserializes JsonAPI "relationships" object into the passed in (java) object
     * @param object
     * @param relationshipsNode
     * @param deserializationContext
     * @throws IOException
     */
    private void deserializeRelationshipsInto(
            Object object,
            JsonNode relationshipsNode,
            DeserializationContext deserializationContext) throws IOException {

        /////////////////
        // BUCKLE. UP. //
        /////////////////

        // Fetch the Relationship Fields on the Type //
        List<Field> completeFields = new ArrayList<Field>();
        Class type = object.getClass();
        while(type != null) {
            for(Field field : type.getDeclaredFields()) {
                completeFields.add(field);
            }
            type = type.getSuperclass();
        }

        for(Field field : completeFields) {
            if(field.isAnnotationPresent(JsonApiRelationship.class)) {
                // Fetch the Serialization/Deserialization key //
                String relationshipKey = DeserializationUtilities.getFieldJsonKey(field);

                // First, check to see if that object exists in the json //
                JsonNode relationshipNode = relationshipsNode.get(relationshipKey);
                if(relationshipNode == null || relationshipNode.isNull()) {
                    // Nothing to see here...
                    continue;
                }

                // Process based on Data, Link, or Meta //
                if(relationshipNode.has(JsonApiKeyConstants.DATA_KEY)) {
                    if(relationshipNode.get(JsonApiKeyConstants.DATA_KEY).isArray()) {
                        // We're dealing with an Array... deserialize accordingly //
                        List<Object> relationshipList = new ArrayList<Object>();
                        for(JsonNode relationshipArrayElementNode : relationshipNode.get(JsonApiKeyConstants.DATA_KEY)) {
                            // Generate the Resource Linkage for the Element //
                            ResourceLinkage relationshipResourceLinkage = DeserializationUtilities.generateResourceLinkageFromNode(
                                    relationshipArrayElementNode, jsonApiTypeMap);

                            // Build up the List of things //
                            relationshipList.add(lazyFetchObject(
                                    relationshipResourceLinkage,
                                    deserializationContext));

                        }

                        // Set the Relationship Object on the Data object! //
                        DeserializationUtilities.setObjectOnField(
                                field,
                                relationshipList,
                                object);
                        continue;
                    } else {
                        // We're dealing with a singe Object //

                        // Generate Relationship Linkage from the Data json //
                        ResourceLinkage relationshipResourceLinkage = DeserializationUtilities.generateResourceLinkageFromNode(
                                relationshipNode.get(JsonApiKeyConstants.DATA_KEY), jsonApiTypeMap);

                        if (!includedsSet.containsKey(relationshipResourceLinkage)) {
                            // Relationship was not included in the JSON.. leave it as null //
                            continue;
                        }

                        // Generate the Relationship Object //
                        Object relationshipObject = lazyFetchObject(
                                relationshipResourceLinkage,
                                deserializationContext);

                        // Set the Relationship Object on the Data object! //
                        if (relationshipObject != null) {
                            DeserializationUtilities.setObjectOnField(
                                    field,
                                    relationshipObject,
                                    object);
                        }
                        continue;
                    }
                }

                if(relationshipNode.has(JsonApiKeyConstants.LINKS_KEY)) {
                    // TODO: Links needs wiring up!
                }

                if(relationshipNode.has(JsonApiKeyConstants.META_DATA_KEY)) {
                    // TODO: Meta (might) need wiring up!
                }
            }
        }

        // Then, fetch the Relationship Methods on the Type //
        List<Method> completeMethods = new ArrayList<Method>();
        type = object.getClass();
        while(type != null) {
            for(Method method : type.getDeclaredMethods()) {
                completeMethods.add(method);
            }
            type = type.getSuperclass();
        }

        for(Method method : completeMethods) {
            if(method.isAnnotationPresent(JsonApiRelationship.class)) {
                // Fetch the Serialization/Deserialization key //
                String relationshipKey = DeserializationUtilities.getMethodJsonKey(method);

                // First, check to see if that object exists in the json //
                JsonNode relationshipNode = relationshipsNode.get(relationshipKey);
                if(relationshipNode == null || relationshipNode.isNull()) {
                    // Nothing to see here...
                    continue;
                }

                // Process based on Data, Link, or Meta //
                if(relationshipNode.has(JsonApiKeyConstants.DATA_KEY)) {
                    if(relationshipNode.get(JsonApiKeyConstants.DATA_KEY).isArray()) {
                        // We're dealing with an Array... deserialize accordingly //
                        List<Object> relationshipList = new ArrayList<Object>();
                        for(JsonNode relationshipArrayElementNode : relationshipNode.get(JsonApiKeyConstants.DATA_KEY)) {
                            // Generate the Resource Linkage for the Element //
                            ResourceLinkage relationshipResourceLinkage = DeserializationUtilities.generateResourceLinkageFromNode(
                                    relationshipArrayElementNode, jsonApiTypeMap);

                            // Build up the List of things //
                            relationshipList.add(lazyFetchObject(
                                    relationshipResourceLinkage,
                                    deserializationContext));

                        }

                        // Set the Relationship Object on the Data object! //
                        DeserializationUtilities.setObjectOnMethod(
                                method,
                                relationshipList,
                                object);
                        continue;
                    } else {
                        // We're dealing with a singe Object //

                        // Generate Relationship Linkage from the Data json //
                        ResourceLinkage relationshipResourceLinkage = DeserializationUtilities.generateResourceLinkageFromNode(
                                relationshipNode.get(JsonApiKeyConstants.DATA_KEY), jsonApiTypeMap);

                        if (!includedsSet.containsKey(relationshipResourceLinkage)) {
                            // Relationship was not included in the JSON.. leave it as null //
                            continue;
                        }

                        // Generate the Relationship Object //
                        Object relationshipObject = lazyFetchObject(
                                relationshipResourceLinkage,
                                deserializationContext);

                        // Set the Relationship Object on the Data object! //
                        if (relationshipObject != null) {
                            DeserializationUtilities.setObjectOnMethod(
                                    method,
                                    relationshipObject,
                                    object);
                        }
                        continue;
                    }
                }

                if(relationshipNode.has(JsonApiKeyConstants.LINKS_KEY)) {
                    // TODO: Links needs wiring up!
                }

                if(relationshipNode.has(JsonApiKeyConstants.META_DATA_KEY)) {
                    // TODO: Meta (might) need wiring up!
                }
            }
        }
    }

}
