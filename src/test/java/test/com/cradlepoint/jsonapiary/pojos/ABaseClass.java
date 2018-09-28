package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiId;
import com.cradlepoint.jsonapiary.annotations.JsonApiMeta;
import com.cradlepoint.jsonapiary.annotations.JsonApiRelationship;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonApiType(value = "BaSeClAsS")
public class ABaseClass {

    ////////////////
    // Attributes //
    ////////////////

    @JsonApiId
    @JsonProperty("whoAmI")
    private String whoAmI;

    @JsonApiRelationship
    private SimpleObject whatDoIHave;

    /////////////////
    // Constructor //
    /////////////////

    public ABaseClass() { }

    ///////////////
    // Overrides //
    ///////////////

    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof ABaseClass)) {
            return false;
        } else {
            return (this.hashCode() == object.hashCode());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                whoAmI,
                whatDoIHave);
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public String getWhoAmI() {
        return this.whoAmI;
    }

    public void setWhoAmI(String whoAmI) {
        this.whoAmI = whoAmI;
    }

    public SimpleObject getWhatDoIHave() {
        return this.whatDoIHave;
    }

    public void setWhatDoIHave(SimpleObject whatDoIHave) {
        this.whatDoIHave = whatDoIHave;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    @JsonApiMeta("jsonApiOverride")
    @JsonProperty("baseMaddness")
    public String maddness() {
        return "what is a whaz?";
    }

}
