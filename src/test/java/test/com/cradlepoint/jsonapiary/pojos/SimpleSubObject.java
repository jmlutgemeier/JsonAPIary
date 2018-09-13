package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiId;
import com.cradlepoint.jsonapiary.annotations.JsonApiLink;
import com.cradlepoint.jsonapiary.annotations.JsonApiRelationship;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@JsonApiType("TypeOverride")
public class SimpleSubObject {

    @JsonApiId
    @JsonProperty("ID")
    private String id;

    @JsonApiLink
    @JsonProperty("BAZ")
    private String baz;

    @JsonApiRelationship
    @JsonProperty("NESTEDnestedNESTED")
    private List<SimpleNestedSubObject> nestedThings;

    public SimpleSubObject() {
        id = RandomStringUtils.random(8);

        Random random = new Random();
        int numNested = random.nextInt(3) + 2;

        baz = "...BAZ...! " + numNested;
        nestedThings = new ArrayList<>();
        for(int i1 = 0; i1 < numNested; i1++) {
            nestedThings.add(new SimpleNestedSubObject());
        }
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaz() {
        return this.baz;
    }

    public void setBaz(String baz) {
        this.baz = baz;
    }

    public List<SimpleNestedSubObject> getNestedThings() {
        return this.nestedThings;
    }

    public void setNestedThings(List<SimpleNestedSubObject> nestedThings) {
        this.nestedThings = nestedThings;
    }

}
