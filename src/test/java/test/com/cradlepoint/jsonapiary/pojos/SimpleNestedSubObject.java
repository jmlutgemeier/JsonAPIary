package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Objects;
import java.util.Random;

@JsonApiType
public class SimpleNestedSubObject {

    ////////////////
    // Attributes //
    ////////////////

    @JsonApiId
    @JsonProperty("iid")
    private Long iid;

    @JsonApiAttribute
    @JsonProperty("aNestedTHING")
    private String nestedThing;

    @JsonApiMeta
    @JsonProperty("circular")
    private SimpleObject cir;

    /////////////////
    // Constructor //
    /////////////////

    public SimpleNestedSubObject() {
        Random random = new Random();
        iid = random.nextLong();
        nestedThing = RandomStringUtils.randomAlphanumeric(10, 20);
    }

    ///////////////
    // Overrides //
    ///////////////

    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof SimpleNestedSubObject)) {
            return false;
        } else {
            return (this.hashCode() == object.hashCode());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                iid,
                nestedThing,
                cir);
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public Long getIid() {
        return this.iid;
    }

    public void setIid(Long iid) {
        this.iid = iid;
    }

    public String getNestedThing() {
        return this.nestedThing;
    }

    public void setNestedThing(String nestedThing) {
        this.nestedThing = nestedThing;
    }

    public SimpleObject getCir() {
        return this.cir;
    }

    public void setCir(SimpleObject cir) {
        this.cir = cir;
    }

}
