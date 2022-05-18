package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.ExpressMetadata;

public enum ExpressMetadataStub implements JsonInjectable<ExpressMetadata> {
    DEFAULT;

    @NonNull
    @Override
    public ExpressMetadata get() {
        return null;
    }

    @NonNull
    @Override
    public String getJson() {
        return "";
    }

    @NonNull
    @Override
    public String getType() {
        return "%EXPRESS_METADATA%";
    }
}