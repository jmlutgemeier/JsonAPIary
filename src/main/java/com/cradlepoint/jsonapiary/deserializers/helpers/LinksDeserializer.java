package com.cradlepoint.jsonapiary.deserializers.helpers;

import com.cradlepoint.jsonapiary.envelopes.JsonApiEnvelope;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class LinksDeserializer {

    /////////////////
    // Constructor //
    /////////////////

    /**
     * private void constructor
     */
    private LinksDeserializer() { }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Deserializes Links into the passed in JsonApiEnvelope
     * @param jsonApiEnvelope
     * @param linksNode
     */
    public static void deserializeLinksInto(
            JsonApiEnvelope jsonApiEnvelope,
            JsonNode linksNode) {
        Iterator<String> linksFields = linksNode.fieldNames();
        while(linksFields.hasNext()) {
            String field = linksFields.next();
            String value = linksNode.get(field).textValue();
            try {
                jsonApiEnvelope.addLink(field, new URL(value));
            } catch(MalformedURLException e) {
                String issue = "Encountered issue deserializing link with key: " + field + " and value (URL): " + value +
                        " ... however, the value does not appear to be a valid URL.";
                throw new IllegalStateException(issue, e);
            }
        }
    }

}
