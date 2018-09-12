package com.cradlepoint.jsonapiary.envelopes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;

public class JsonApiEnvelope<T> {

    ////////////////
    // Attributes //
    ////////////////

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
        this.data = data;
    }
}
