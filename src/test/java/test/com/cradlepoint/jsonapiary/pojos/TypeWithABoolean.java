package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiAttribute;
import com.cradlepoint.jsonapiary.annotations.JsonApiId;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;

@JsonApiType
public class TypeWithABoolean {

    ////////////////
    // Attributes //
    ////////////////

    @JsonApiId
    private Integer id;

    @JsonApiAttribute
    private Boolean bool;

    /////////////////
    // Constructor //
    /////////////////

    public TypeWithABoolean() { }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean isBool() {
        return this.bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

}
