package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiAttribute;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonApiType(value = "AcHiLdClAsS")
public class AChildClass extends ABaseClass {

    ////////////////
    // Attributes //
    ////////////////

    @JsonApiAttribute
    @JsonProperty
    private String whaz;

    @JsonApiAttribute
    private Integer metaInt;

    /////////////////
    // Constructor //
    /////////////////

    public AChildClass() { }

    ///////////////
    // Overrides //
    ///////////////

    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof AChildClass)) {
            return false;
        } else {
            return (this.hashCode() == object.hashCode());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                whaz,
                metaInt);
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public String getWhaz() {
        return this.whaz;
    }

    public void setWhaz(String whaz) {
        this.whaz = whaz;
    }

    public Integer getMetaInt() {
        return this.metaInt;
    }

    public void setMetaInt(Integer metaInt) {
        this.metaInt = metaInt;
    }

}
