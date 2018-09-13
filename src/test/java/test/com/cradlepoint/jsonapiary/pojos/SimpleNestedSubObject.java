package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiAttribute;
import com.cradlepoint.jsonapiary.annotations.JsonApiRelationship;
import com.cradlepoint.jsonapiary.annotations.JsonApiId;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

@JsonApiType
public class SimpleNestedSubObject {

    @JsonApiId
    @JsonProperty("iid")
    private Long iid;

    @JsonApiAttribute
    @JsonProperty("aNestedTHING")
    private String nestedThing;
    @JsonApiRelationship
    @JsonProperty("circular")
    private SimpleObject cir;

    public SimpleNestedSubObject() {
        Random random = new Random();
        iid = random.nextLong();
        nestedThing = RandomStringUtils.randomAlphanumeric(10, 20);
    }

    public Long getIid() {
        return this.iid;
    }
    public SimpleObject getCir() {
        return this.cir;
    }
    public void setCir(SimpleObject cir) {
        this.cir = cir;
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

}
