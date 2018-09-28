package com.cradlepoint.jsonapiary.serializers.helpers;

import com.cradlepoint.jsonapiary.annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        List<Field> completeFields = new ArrayList<Field>();
        Class type = jsonApiObject.getClass();
        while(type != null) {
            for(Field field : type.getDeclaredFields()) {
                completeFields.add(field);
            }
            type = type.getSuperclass();
        }

        for(Field field : completeFields) {
            if(field.isAnnotationPresent(annotation)) {
                // This Field is EXPLICITLY part of the annotation //
                jsons.put(
                        fetchFieldKey(field, annotation),
                        fetchFieldValue(jsonApiObject, field, jsonGenerator));
            } else if(isOtherJsonApiAnnotationPresent(field.getDeclaredAnnotations(), annotation)) {
                // This Field is explicitly NOT part of the annotation //
                continue;
            } else if (field.isAnnotationPresent(JsonApiIgnore.class)) {
                // This Field is explicitly NOT to be included in the JsonAPI serialization
            } else if(isAnnotationAlsoCatchAll && field.isAnnotationPresent(JsonProperty.class)) {
                // This Field is IMPLICITLY part of the annotation //
                jsons.put(
                        fetchFieldKey(field, annotation),
                        fetchFieldValue(jsonApiObject, field, jsonGenerator));
            } else {
                // This Field is un-annotated //
            }
        }

        // Then Fetch all the Methods //
        List<Method> completeMethods = new ArrayList<Method>();
        type = jsonApiObject.getClass();
        while(type != null) {
            for(Method method : type.getDeclaredMethods()) {
                completeMethods.add(method);
            }
            type = type.getSuperclass();
        }

        for(Method method : completeMethods) {
            if(method.isAnnotationPresent(annotation)) {
                // This Method is EXPLICITLY part of the annotation //
                jsons.put(
                        fetchMethodKey(method,  annotation),
                        fetchMethodValue(jsonApiObject, method, jsonGenerator));
            } else if(isOtherJsonApiAnnotationPresent(method.getDeclaredAnnotations(), annotation)) {
                // This Method is explicitly NOT part of the annotation //
                continue;
            } else if (method.isAnnotationPresent(JsonApiIgnore.class)) {
                // This Field is explicitly NOT to be included in the JsonAPI serialization
            } else if(isAnnotationAlsoCatchAll && method.isAnnotationPresent(JsonProperty.class)) {
                // This Method is IMPLICITLY part of the annotation //
                jsons.put(
                        fetchMethodKey(method,  annotation),
                        fetchMethodValue(jsonApiObject, method, jsonGenerator));
            } else {
                // This Method is un-annotated //
            }
        }

        return jsons;
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private static String fetchFieldKey(
            Field field,
            Class<? extends Annotation> jsonApiAnnotation) {
        String key = null;

        // Check to see if there is a JsonAPI annotation with a value //
        if(jsonApiAnnotation == JsonApiAttribute.class && field.isAnnotationPresent(JsonApiAttribute.class)) {
            key = field.getAnnotation(JsonApiAttribute.class).value();
        } else if(jsonApiAnnotation == JsonApiLink.class && field.isAnnotationPresent(JsonApiLink.class)) {
            key = field.getAnnotation(JsonApiLink.class).value();
        } else if(jsonApiAnnotation == JsonApiMeta.class && field.isAnnotationPresent(JsonApiMeta.class)) {
            key = field.getAnnotation(JsonApiMeta.class).value();
        } else if(jsonApiAnnotation == JsonApiRelationship.class && field.isAnnotationPresent(JsonApiRelationship.class)) {
            key = field.getAnnotation(JsonApiRelationship.class).value();
        }

        if(key != null && !key.isEmpty()) {
            return key;
        }

        // Check to see if there is a value on a Jackson annotation //
        if(field.isAnnotationPresent(JsonProperty.class)) {
            key = field.getAnnotation(JsonProperty.class).value();
        }

        if(key != null && !key.isEmpty()) {
            return key;
        }

        // As a last resort, return the field name //
        return field.getName();
    }

    private static String fetchMethodKey(
            Method method,
            Class<? extends Annotation> jsonApiAnnotation) {
        String key = null;

        // Check to see if there is a JsonAPI annotation with a value //
        if(jsonApiAnnotation == JsonApiAttribute.class && method.isAnnotationPresent(JsonApiAttribute.class)) {
            key = method.getAnnotation(JsonApiAttribute.class).value();
        } else if(jsonApiAnnotation == JsonApiLink.class && method.isAnnotationPresent(JsonApiLink.class)) {
            key = method.getAnnotation(JsonApiLink.class).value();
        } else if(jsonApiAnnotation == JsonApiMeta.class && method.isAnnotationPresent(JsonApiMeta.class)) {
            key = method.getAnnotation(JsonApiMeta.class).value();
        } else if(jsonApiAnnotation == JsonApiRelationship.class && method.isAnnotationPresent(JsonApiRelationship.class)) {
            key = method.getAnnotation(JsonApiRelationship.class).value();
        }

        if(key != null && !key.isEmpty()) {
            return key;
        }

        // Check to see if there is a value on a Jackson annotation //
        if(method.isAnnotationPresent(JsonProperty.class)) {
            key = method.getAnnotation(JsonProperty.class).value();
        }

        if(key != null && !key.isEmpty()) {
            return key;
        }

        // As a last resort, return the field name //
        return method.getName();
    }

    private static Object fetchFieldValue(
            Object jsonApiObject,
            Field field,
            JsonGenerator jsonGenerator) throws IOException {
        try {
            return field.get(jsonApiObject);
        } catch (IllegalAccessException e) {
            // Noop...
        }

        Class type = jsonApiObject.getClass();
        while(type != null) {
            try {
                Method getter = type.getDeclaredMethod(generateGetterName(field.getName()));
                return getter.invoke(jsonApiObject);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // Noop...
            }
            type = type.getSuperclass();
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

        String issue = "Unable to access json annotated method: " + method.getName() + " on type: " +
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
