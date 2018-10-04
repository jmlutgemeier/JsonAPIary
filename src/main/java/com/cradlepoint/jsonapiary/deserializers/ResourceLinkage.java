package com.cradlepoint.jsonapiary.deserializers;

import java.util.Objects;

public class ResourceLinkage {

    ////////////////
    // Attributes //
    ////////////////

    private Object id;

    private Class type;

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Private void constructor
     */
    private ResourceLinkage() { }

    /**
     * Constructor
     * @param id
     * @param type
     */
    public ResourceLinkage(
            Object id,
            Class type) {
        this.id = id;
        this.type = type;
    }

    ///////////////
    // Overrides //
    ///////////////

    /**
     * Determines equality between two ResourceLinkages
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if(!(object instanceof ResourceLinkage)) {
            return false;
        }

        return (this.hashCode() == object.hashCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                type);
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    /**
     * Returns ID
     * @return
     */
    public Object getId() {
        return this.id;
    }

    /**
     * Sets ID
     * @param id
     */
    public void setId(Object id) {
        this.id = id;
    }

    /**
     * Returns Type
     * @return
     */
    public Class getType() {
        return this.type;
    }

    /**
     * Sets Type
     * @param type
     */
    public void setType(Class type){
        this.type = type;
    }

}
