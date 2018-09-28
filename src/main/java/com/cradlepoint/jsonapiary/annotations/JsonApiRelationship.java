package com.cradlepoint.jsonapiary.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JsonApiProperty
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonApiRelationship {

    /**
     * Override for serialized key, otherwise will default to the @JsonProperty or attribute name
     * @return
     */
    String value() default "";

}
