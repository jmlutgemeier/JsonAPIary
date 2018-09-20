package com.cradlepoint.jsonapiary.deserializers.helpers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

class AttributesDeserializer {

    /////////////////
    // Constructor //
    /////////////////

    /**
     * private void constructor
     */
    private AttributesDeserializer() { }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     *  Deserializes the attributes into the passed in object
     * @param dataType
     * @param dataObject
     * @param attributesNode
     * @param deserializationContext
     */
    public static void deserializeAttributesInto(
            Class dataType,
            Object dataObject,
            JsonNode attributesNode,
            DeserializationContext deserializationContext) throws IOException {
        // Fetch the "default" deserializer for the type from Jackson //
        JavaType javaDataType = deserializationContext.getTypeFactory().constructType(dataType);
        JsonDeserializer dataTypeDeserializer = deserializationContext.findRootValueDeserializer(javaDataType);

        // Deserialize from the Attributes Nodes //
        JsonParser attributesParser = new JsonFactory().createParser(attributesNode.toString());
        attributesParser.nextToken();
        dataTypeDeserializer.deserialize(attributesParser, deserializationContext, dataObject);
    }

}
