package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiId;
import com.cradlepoint.jsonapiary.annotations.JsonApiLink;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonApiType("LoganWasHere")
public class TypeWithALink {

    ////////////////
    // Attributes //
    ////////////////
    @JsonApiId
    @JsonProperty("ID")
    private String id;

    @JsonApiLink("LinkForLogan")
    @JsonProperty("missingFromJsonAPI")
    private Object link;

    /////////////////
    // Constructor //
    /////////////////
    public TypeWithALink() { }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getLink() {
        return this.link;
    }

    public void setLink(Object link) {
        this.link = link;
    }

}
