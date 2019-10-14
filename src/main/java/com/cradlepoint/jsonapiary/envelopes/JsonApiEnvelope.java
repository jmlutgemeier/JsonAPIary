package com.cradlepoint.jsonapiary.envelopes;

import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonApiEnvelope<T> {

    ////////////////
    // Attributes //
    ////////////////

    /**
     * JsonAPI annotated object to be serialized
     */
    @JsonProperty(JsonApiKeyConstants.DATA_KEY)
    @Valid
    private T data;

    /**
     * Top-level links
     */
    @JsonProperty(JsonApiKeyConstants.LINKS_KEY)
    private Map<String, URL> links;

    /**
     * Top-level meta-data
     */
    @JsonProperty(JsonApiKeyConstants.META_DATA_KEY)
    private Map<String, String> meta;

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Default void constructor
     */
    public JsonApiEnvelope() {
        data = null;
        links = new Hashtable<String, URL>();
        meta = new Hashtable<String, String>();
    }

    /**
     * Constructor
     */
    public JsonApiEnvelope(T data) {
        validateTypeJsonAPIAnnotated(data);
        this.data = data;
        links = new Hashtable<String, URL>();
        meta = new Hashtable<String, String>();
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

    /**
     * Fetch the top-level links
     * @return
     */
    public Map<String, URL> getLinks() {
        return this.links;
    }

    /**
     * Fetch the top-level meta-data
     * @return
     */
    public Map<String, String> getMeta() {
        return this.meta;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Adds a link to the top-level object links
     * @param key
     * @param url
     */
    public void addLink(String key, URL url) {
        links.put(key, url);
    }

    /**
     * Adds key-value pair to the top-level meta-data
     * @param key
     * @param value
     */
    public void addMeta(String key, String value) {
        meta.put(key, value);
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

    ///////////////
    // Overrides //
    ///////////////

    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof JsonApiEnvelope)){
            return false;
        } else {
            return (this.hashCode() == object.hashCode());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                data,
                links,
                meta);
    }

}
