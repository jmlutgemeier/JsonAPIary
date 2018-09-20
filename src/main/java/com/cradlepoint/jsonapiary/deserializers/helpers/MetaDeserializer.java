package com.cradlepoint.jsonapiary.deserializers.helpers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class MetaDeserializer {

    /////////////////
    // Constructor //
    /////////////////

    private MetaDeserializer() { }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     *  Deserializes the attributes into the passed in object
     * @param dataType
     * @param dataObject
     * @param metaNode
     * @param deserializationContext
     */
    public static void deserializeMetaInto(
            Class dataType,
            Object dataObject,
            JsonNode metaNode,
            DeserializationContext deserializationContext) throws IOException {
        // Fetch the "default" deserializer for the type from Jackson //
        JavaType javaDataType = deserializationContext.getTypeFactory().constructType(dataType);
        JsonDeserializer dataTypeDeserializer = deserializationContext.findRootValueDeserializer(javaDataType);

        // Deserialize from the Meta Nodes //
        JsonParser attributesParser = new JsonFactory().createParser(metaNode.toString());
        attributesParser.nextToken();
        dataTypeDeserializer.deserialize(attributesParser, deserializationContext, dataObject);
    }

}
