package com.cradlepoint.jsonapiary.serializers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;
import com.cradlepoint.jsonapiary.enums.JsonApiObjectSerializationContext;
import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class JsonApiEnvelopeSerializer extends StdSerializer<JsonApiEnvelope> {

    ////////////////
    // Attributes //
    ////////////////

    private JsonApiSerializer jsonApiSerializer;

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Default void constructors
     */
    public JsonApiEnvelopeSerializer() {
        super(JsonApiEnvelope.class);
        jsonApiSerializer = new JsonApiSerializer();
    }

    ///////////////////////////
    // StdSerializer Methods //
    ///////////////////////////

    /**
     * Serializes enveloped, and properly annotated, object according to JsonAPI specification
     * @param jsonApiEnvelope
     * @param jsonGenerator
     * @param serializerProvider
     * @throws IOException
     */
    public void serialize(
            JsonApiEnvelope jsonApiEnvelope,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        Set<Object> includes = new HashSet<Object>();

        // Create the JsonAPI "envelope" object, the call down expanding the "has-a" objects accordingly //
        jsonGenerator.writeStartObject();

        Map<String, Object> envelopeContents = fetchEnvelopeContents(jsonApiEnvelope, serializerProvider);
        for(String key : envelopeContents.keySet()) {
            jsonGenerator.writeFieldName(key);

            if(envelopeContents.get(key) == null) {
             jsonGenerator.writeNull();
            } else {
                // Is the sub-object a "JsonAPI" object? //
                Object value = envelopeContents.get(key);
                includes.addAll(
                        jsonApiSerializer.serializeRandomObject(value,
                                JsonApiObjectSerializationContext.PRIMARY,
                                jsonGenerator,
                                serializerProvider));
            }
        }

        // Write out the expanded "included" objects //
        if(!includes.isEmpty()) {
            Set<Object> newIncludes = new HashSet<Object>();
            jsonGenerator.writeFieldName(JsonApiKeyConstants.INCLUDED_KEY);
            jsonGenerator.writeStartArray();

            while(!includes.isEmpty()) {
                for (Object thingToInclude : includes) {
                    newIncludes.addAll(jsonApiSerializer.serializeRandomObject(
                            thingToInclude,
                            JsonApiObjectSerializationContext.PRIMARY,
                            jsonGenerator,
                            serializerProvider));
                }

                includes = determineNewNewIncludes(includes, newIncludes);
            }

            jsonGenerator.writeEndArray();
        }

        jsonGenerator.writeEndObject();
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    /**
     * Fetch the JsonApiSerializer
     * @return
     */
    public JsonApiSerializer getJsonApiSerializer() {
        return this.jsonApiSerializer;
    }

    /**
     * Set the JsonApiSerializer
     * @param jsonApiSerializer
     */
    public void setJsonApiSerializer(JsonApiSerializer jsonApiSerializer) {
        this.jsonApiSerializer = jsonApiSerializer;
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private Map<String, Object> fetchEnvelopeContents(JsonApiEnvelope jsonApiEnvelope, SerializerProvider serializerProvider) throws IOException {
        Map<String, Object> envelopeContents = new Hashtable<String, Object>();

        // Fetch all the Envelope attributes //
        for(Field field : jsonApiEnvelope.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(JsonProperty.class)) {
                // Holy reflection, batman! //
                JsonProperty annotation = field.getAnnotation(JsonProperty.class);

                try {
                    Object value = field.get(jsonApiEnvelope);
                    envelopeContents.put(annotation.value(), value);
                    continue;
                } catch (IllegalAccessException e) {
                    // Noop...
                }

                try {
                    Method getter = jsonApiEnvelope.getClass().getDeclaredMethod(generateGetterName(field.getName()));
                    Object value = getter.invoke(jsonApiEnvelope);
                    envelopeContents.put(annotation.value(), value);
                    continue;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    // Noop...
                }

                throw serializerProvider.mappingException("Unable to access value for field: " + field.getName() +
                        " . The field is both private, and a default public void getter for it was not found!");
            }
        }

        return envelopeContents;
    }

    private String generateGetterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private Set<Object> determineNewNewIncludes(Set<Object> includes, Set<Object> newIncludes) {
        Set<Object> newNewIncludes = new HashSet<Object>();

        for(Object newInclude : newIncludes) {
            if(!includes.contains(newInclude)) {
                newNewIncludes.add(newInclude);
            }
        }

        return newNewIncludes;
    }

}
