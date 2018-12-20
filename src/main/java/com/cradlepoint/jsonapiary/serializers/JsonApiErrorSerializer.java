package com.cradlepoint.jsonapiary.serializers;

import com.cradlepoint.jsonapiary.constants.JsonApiKeyConstants;
import com.cradlepoint.jsonapiary.types.JsonApiError;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class JsonApiErrorSerializer extends StdSerializer<JsonApiError> {

    /////////////////
    // Constructor //
    /////////////////

    /**
     * Constructor
     */
    public JsonApiErrorSerializer() {
        super(JsonApiError.class);
    }

    ////////////////////////
    // Serializer Methods //
    ////////////////////////

    /**
     * Serializes the JsonApiError
     * @param jsonApiError
     * @param jsonGenerator
     * @param serializerProvider
     * @throws IOException
     */
    public void serialize(
            JsonApiError jsonApiError,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {

        // Write out the beginning of the JSON //
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName(JsonApiKeyConstants.ERRORS_KEY);
        jsonGenerator.writeStartArray();

        // Loop through all of the Errors within the JsonApiError //
        if(jsonApiError != null) {
            for(JsonApiError error : jsonApiError) {
                // Write out all the present fields //
                jsonGenerator.writeStartObject();

                // id //
                if(error.getId() != null) {
                    jsonGenerator.writeStringField(JsonApiKeyConstants.ID_KEY, error.getId());
                }

                // links //
                if(error.getLinks() != null && !error.getLinks().isEmpty()) {
                    jsonGenerator.writeFieldName(JsonApiKeyConstants.LINKS_KEY);
                    jsonGenerator.writeStartObject();
                    for(String linkKey : error.getLinks().keySet()) {
                        jsonGenerator.writeStringField(linkKey, error.getLinks().get(linkKey).toString());
                    }
                    jsonGenerator.writeEndObject();
                }

                // status //
                if(error.getStatus() != null) {
                    jsonGenerator.writeStringField(JsonApiKeyConstants.STATUS_KEY, error.getStatus().toString());
                }

                // code //
                if(error.getCode() != null) {
                    jsonGenerator.writeStringField(JsonApiKeyConstants.CODE_KEY, error.getCode());
                }

                // title //
                if(error.getTitle() != null) {
                    jsonGenerator.writeStringField(JsonApiKeyConstants.TITLE_KEY, error.getTitle());
                }

                // detail //
                if(error.getDetail() != null) {
                    jsonGenerator.writeStringField(JsonApiKeyConstants.DETAIL_KEY, error.getDetail());
                }

                // source (pointer) //
                if(error.getSourcePointer() != null) {
                    jsonGenerator.writeFieldName(JsonApiKeyConstants.SOURCE_KEY);
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField(JsonApiKeyConstants.POINTER_KEY, error.getSourcePointer());
                    jsonGenerator.writeEndObject();
                }

                // meta //
                if(error.getMeta() != null && !error.getMeta().isEmpty()) {
                    jsonGenerator.writeFieldName(JsonApiKeyConstants.META_DATA_KEY);
                    jsonGenerator.writeStartObject();
                    for(String metaKey : error.getMeta().keySet()) {
                        jsonGenerator.writeFieldName(metaKey);
                        JsonSerializer dataSerializer = serializerProvider.findValueSerializer(error.getMeta().get(metaKey).getClass());
                        dataSerializer.serialize(error.getMeta().get(metaKey), jsonGenerator, serializerProvider);
                    }
                    jsonGenerator.writeEndObject();
                }

                jsonGenerator.writeEndObject();
            }
        }

        // Close out the JSON //
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }

}
