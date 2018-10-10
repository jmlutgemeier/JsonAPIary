package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiAttribute;
import com.cradlepoint.jsonapiary.annotations.JsonApiId;
import com.cradlepoint.jsonapiary.annotations.JsonApiRelationship;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonApiType("node")
public class SingleLinkNode {

    ////////////////
    // Attributes //
    ////////////////

    @JsonApiId
    @JsonProperty("id")
    private Long id;

    @JsonApiAttribute("element")
    @JsonProperty("value")
    private String value;

    @JsonApiRelationship
    @JsonProperty("link")
    private SingleLinkNode linkNode;

    /////////////////
    // Constructor //
    /////////////////

    public SingleLinkNode() { }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SingleLinkNode getLinkNode() {
        return this.linkNode;
    }

    public void setLinkNode(SingleLinkNode linkNode) {
        this.linkNode = linkNode;
    }

}
