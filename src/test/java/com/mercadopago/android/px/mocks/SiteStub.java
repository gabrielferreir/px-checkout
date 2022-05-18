package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.utils.ResourcesUtil;

public enum SiteStub implements JsonInjectable<Site> {
    MLA("site_MLA.json"),
    MLB("site_MLB.json");

    @NonNull private final String fileName;

    SiteStub(@NonNull final String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    public Site get() {
        return JsonUtil.fromJson(getJson(), Site.class);
    }

    @NonNull
    @Override
    public String getJson() {
        return ResourcesUtil.getStringResource(fileName);
    }

    @NonNull
    @Override
    public String getType() {
        return "%SITE%";
    }
}