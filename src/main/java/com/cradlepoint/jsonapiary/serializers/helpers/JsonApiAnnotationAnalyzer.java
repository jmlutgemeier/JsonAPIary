package com.cradlepoint.jsonapiary.serializers.helpers;

import com.cradlepoint.jsonapiary.annotations.JsonApiIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.cradlepoint.jsonapiary.annotations.JsonApiMeta;
import com.cradlepoint.jsonapiary.annotations.JsonApiProperty;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JsonApiAnnotationAnalyzer {

    ////////////////
    // Attributes //
    ////////////////

    private static final Class<? extends Annotation> CATCH_ALL_JSON_API_OBJECT = JsonApiMeta.class;

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Private void constructor
     */
    private JsonApiAnnotationAnalyzer() { }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Returns a map of JSON API keys (Strings) and their unserialized value (Object), based on the annotations
     * on the the passed in Object.
     * @param jsonApiObject
     * @param annotation
     * @param jsonGenerator
     * @return
     * @throws IOException
     */
    public static Map<String, Object> fetchJsonsByAnnotation(
            Object jsonApiObject,
            Class<? extends Annotation> annotation,
            JsonGenerator jsonGenerator) throws IOException {
        Map<String, Object> jsons = new HashMap<String, Object>();
        boolean isAnnotationAlsoCatchAll = CATCH_ALL_JSON_API_OBJECT.equals(annotation);

        // First, fetch all the Fields //
        for(Field field : jsonApiObject.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(JsonProperty.class)) {
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);

                if(field.isAnnotationPresent(annotation)) {
                    // This Field is EXPLICITLY part of the annotation //
                    jsons.put(jsonProperty.value(), fetchFieldValue(jsonApiObject, field, jsonGenerator));
                } else if(isOtherJsonApiAnnotationPresent(field.getDeclaredAnnotations(), annotation)) {
                    // This Field is explicitly NOT part of the annotation //
                    continue;
                } else if (field.isAnnotationPresent(JsonApiIgnore.class)) {
                    // This Field is explicitly NOT to be included in the JsonAPI serialization
                } else if(isAnnotationAlsoCatchAll) {
                    // This Field is IMPLICITLY part of the annotation //
                    jsons.put(jsonProperty.value(), fetchFieldValue(jsonApiObject, field, jsonGenerator));
                } else {
                    // This Field is un-annotated //
                }
            }
        }

        // Then Fetch all the Methods //
        for(Method method : jsonApiObject.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(JsonProperty.class)) {
                JsonProperty jsonProperty = method.getAnnotation(JsonProperty.class);

                if(method.isAnnotationPresent(annotation)) {
                    // This Method is EXPLICITLY part of the annotation //
                    jsons.put(jsonProperty.value(), fetchMethodValue(jsonApiObject, method, jsonGenerator));
                } else if(isOtherJsonApiAnnotationPresent(method.getDeclaredAnnotations(), annotation)) {
                    // This Method is explicitly NOT part of the annotation //
                    continue;
                } else if (method.isAnnotationPresent(JsonApiIgnore.class)) {
                    // This Field is explicitly NOT to be included in the JsonAPI serialization
                } else if(isAnnotationAlsoCatchAll) {
                    // This Method is IMPLICITLY part of the annotation //
                    jsons.put(jsonProperty.value(), fetchMethodValue(jsonApiObject, method, jsonGenerator));
                } else {
                    // This Method is un-annotated //
                }
            }
        }

        return jsons;
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private static Object fetchFieldValue(
            Object jsonApiObject,
            Field field,
            JsonGenerator jsonGenerator) throws IOException {
        try {
            return field.get(jsonApiObject);
        } catch (IllegalAccessException e) {
            // Noop...
        }

        try {
            Method getter = jsonApiObject.getClass().getDeclaredMethod(generateGetterName(field.getName()));
            return getter.invoke(jsonApiObject);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // Noop...
        }

        String issue = "Unable to access value for field: " + field.getName() + " . The field is both private," +
                "and a default public void getter for it was not found!";
        throw JsonMappingException.from(jsonGenerator, issue);
    }

    private static String generateGetterName(
            String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static Object fetchMethodValue(
            Object jsonApiObject,
            Method method,
            JsonGenerator jsonGenerator) throws IOException {
        try {
            return method.invoke(jsonApiObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // Noop...
        }

        String issue = "Unable to access JsonProperty annotated method: " + method.getName() + " on type: " +
                jsonApiObject.getClass().getName() + " !!";
        throw JsonMappingException.from(jsonGenerator, issue);
    }

    private static boolean isOtherJsonApiAnnotationPresent(
            Annotation[] annotations,
            Class<? extends Annotation> annotation) {
        for(Annotation presentAnnotation : annotations) {
            if(!annotation.equals(presentAnnotation)) {
                if(presentAnnotation.annotationType().isAnnotationPresent(JsonApiProperty.class)) {
                    return true;
                }
            } else {
                return false;
            }
        }

        return false;
    }

}
