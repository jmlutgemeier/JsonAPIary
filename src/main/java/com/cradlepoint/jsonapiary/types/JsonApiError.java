package com.cradlepoint.jsonapiary.types;

import java.net.URL;
import java.util.*;

public final class JsonApiError implements Iterable<JsonApiError> {

    ////////////////
    // Attributes //
    ////////////////

    private String id;
    private Integer status;
    private String code;
    private String title;
    private String detail;
    private String sourcePointer;
    private Map<String, URL> links;
    private Map<String, Object> meta;
    private JsonApiError nextError;

    //////////////////
    // Constructors //
    //////////////////

    /**
     * void constructor
     */
    public JsonApiError() {
        links = new Hashtable<String, URL>();
        meta = new Hashtable<String, Object>();
        nextError = null;
    }

    ///////////////
    // Overrides //
    ///////////////

    /**
     * Clones the JsonApiError
     * @return
     */
    @Override
    protected JsonApiError clone() {
        JsonApiError jsonApiError = new JsonApiError();
        jsonApiError.id = id;
        jsonApiError.status = status;
        jsonApiError.code = code;
        jsonApiError.title = title;
        jsonApiError.detail = detail;
        jsonApiError.sourcePointer = sourcePointer;
        jsonApiError.links = links;
        jsonApiError.meta = meta;
        jsonApiError.nextError = nextError;
        return jsonApiError;
    }

    /**
     * Returns hashcode for the Object instance
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                status,
                code,
                title,
                detail,
                sourcePointer,
                links,
                meta);
    }

    /**
     * Determines if the two Objects are the same
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if(object == null) {
            return false;
        } else {
            return this.hashCode() == object.hashCode();
        }
    }

    /**
     * Returns an Iterator over each individual Error within the JsonApiError
     * @return
     */
    @Override
    public Iterator<JsonApiError> iterator() {
        Set<JsonApiError> errors = new HashSet<JsonApiError>();
        JsonApiError error = this;
        do {
            if(error.isPopulated()) {
                errors.add(error);
            }
            error = error.nextError;
        } while (error != null);
        return errors.iterator();
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Adds a new individual error to the JsonApiError
     * @return
     */
    public JsonApiError andError() {
        if(isPopulated()) {
            return cloneIntoNext();
        } else {
            return this;
        }
    }

    /**
     * Returns the ID of the "current" individual error
     * @return
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the ID of the "current" individual error
     * @param id
     * @return
     */
    public JsonApiError withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the HTTP Status Code of the "current" individual error
     * @return
     */
    public Integer getStatus() {
        return this.status;
    }

    /**
     * Sets the HTTP Status Code of the "current" individual error
     * @param status
     * @return
     */
    public JsonApiError withStatus(Integer status) {
        this.status = status;
        return this;
    }

    /**
     * Returns the Code of the "current" individual error
     * @return
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Sets the Code of the "current" individual error
     * @param code
     * @return
     */
    public JsonApiError withCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * Returns the Title of the "current" individual error
     * @return
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the Title of the "current" individual error
     * @param title
     * @return
     */
    public JsonApiError withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Returns the details of the "current" individual error
     * @return
     */
    public String getDetail() {
        return this.detail;
    }

    /**
     * Sets the details of the "current" individual error
     * @param detail
     * @return
     */
    public JsonApiError withDetail(String detail) {
        this.detail = detail;
        return this;
    }

    /**
     * Returns the source pointer of the "current" individual error
     * @return
     */
    public String getSourcePointer() {
        return this.sourcePointer;
    }

    /**
     * Sets the source pointer of the "current" individual error
     * @param sourcePointer
     * @return
     */
    public JsonApiError withSourcePointer(String sourcePointer) {
        this.sourcePointer = sourcePointer;
        return this;
    }

    /**
     * Returns the Links of the "current" individual error
     * @return
     */
    public Map<String, URL> getLinks() {
        return this.links;
    }

    /**
     * Adds a Link to the "current" individual error
     * @param key
     * @param link
     * @return
     */
    public JsonApiError withLink(String key, URL link) {
        this.links.put(key, link);
        return this;
    }

    /**
     * Returns the Metas of the "current" individual error
     * @return
     */
    public Map<String, Object> getMeta() {
        return this.meta;
    }

    /**
     * Adds a Meta element to the "current" individual error
     * @param key
     * @param data
     * @return
     */
    public JsonApiError withMeta(String key, Object data) {
        this.meta.put(key, data);
        return this;
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Clones current error into "nextError", so that code using the type always references the same object
     * @return
     */
    private JsonApiError cloneIntoNext() {
        JsonApiError clone = clone();
        this.id = null;
        this.status = null;
        this.code = null;
        this.title = null;
        this.detail = null;
        this.sourcePointer = null;
        this.links = new Hashtable<String, URL>();
        this.meta = new Hashtable<String, Object>();
        this.nextError = clone;
        return this;
    }

    /**
     * Returns whether or not the Error has any data within it or not.
     * @return
     */
    private boolean isPopulated() {
        if(id != null ||
                status != null ||
                code != null ||
                title != null ||
                detail != null ||
                sourcePointer != null ||
                links.isEmpty() == false ||
                meta.isEmpty() == false) {
            return true;
        } else {
            return false;
        }
    }

}
