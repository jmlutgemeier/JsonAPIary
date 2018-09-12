package com.cradlepoint.jsonapiary;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.cradlepoint.jsonapiary.serializers.JsonApiEnvelopeSerializer;

public class JsonApiModule extends SimpleModule {

    ////////////////
    // Attributes //
    ////////////////

    private static final String MODULE_NAME = "jsonapiary";

    /////////////////
    // Constructor //
    /////////////////

    public JsonApiModule() {
        super(PackageVersion.VERSION);
        this.addSerializer(new JsonApiEnvelopeSerializer());
    }

}
