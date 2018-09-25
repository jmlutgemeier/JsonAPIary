package com.cradlepoint.jsonapiary.envelopes;

import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;

import java.util.List;

public class JsonApiEnvelope<T> {

    ////////////////
    // Attributes //
    ////////////////

    /**
     * JsonAPI annotated object to be serialized
     */
    @JsonProperty(JsonApiKeyConstants.DATA_KEY)
    private T data;

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Default void constructor
     */
    public JsonApiEnvelope() { }

    /**
     * Constructor
     */
    public JsonApiEnvelope(T data) {
        validateTypeJsonAPIAnnotated(data);
        this.data = data;
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    /**
     * Fetch the data object
     * @return
     */
    public T getData() {
        return this.data;
    }

    /**
     * Set the data object
     * @param data
     */
    public void setData(T data) {
        validateTypeJsonAPIAnnotated(data);
        this.data = data;
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Validates that a data object even makes sense in a JsonAPI Envelope
     * @param data
     * @param <T>
     */
    private static <T> void validateTypeJsonAPIAnnotated(T data) {
        if(data == null) {
            String issue = "Passed in data is null!";
            throw new IllegalArgumentException(issue);
        } else if(data instanceof List) {
            // Check to see if it's contents are correctly annotated //
            if(((List)data).isEmpty()) {
                return;
            } else {
                for(Object listElement : ((List)data)) {
                    if(!listElement.getClass().isAnnotationPresent(JsonApiType.class)) {
                        String issue = "Passed in List contains (at least) one element (type: " + listElement.getClass().getName() +
                                ") that is not JsonApi annotated. Expected ALL contents of the list be of type(s) with " +
                                "the @JsonApiType annotation";
                        throw new IllegalArgumentException(issue);
                    }
                }
            }
        } else if(!data.getClass().isAnnotationPresent(JsonApiType.class)) {
            String issue = "Passed in data (type: " + data.getClass().getName() + ") does not appear to be JsonApi annotated." +
                    " Expected type to have @JsonApiType annotation";
            throw new IllegalArgumentException(issue);
        }
    }

}
