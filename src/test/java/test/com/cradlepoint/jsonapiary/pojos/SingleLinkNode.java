package test.com.cradlepoint.jsonapiary.pojos;

import com.cradlepoint.jsonapiary.annotations.JsonApiAttribute;
import com.cradlepoint.jsonapiary.annotations.JsonApiId;
import com.cradlepoint.jsonapiary.annotations.JsonApiRelationship;
import com.cradlepoint.jsonapiary.annotations.JsonApiType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

@JsonApiType
public class SingleLinkNode {

    ////////////////
    // Attributes //
    ////////////////

    @JsonApiId
    @JsonProperty("id")
    private Double id;

    @JsonApiAttribute
    @JsonProperty("value")
    private String value;

    @JsonApiRelationship
    @JsonProperty("link")
    private SingleLinkNode linkNode;

    /////////////////
    // Constructor //
    /////////////////

    public SingleLinkNode() {
        Random random = new Random();
        id = random.nextDouble();
        value = "Logan was here " + id + " times.";
        linkNode = null;
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public Double getId() {
        return this.id;
    }

    public void setId(Double id) {
        this.id = id;
        this.value = "Logan was here " + id + " times!";
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
