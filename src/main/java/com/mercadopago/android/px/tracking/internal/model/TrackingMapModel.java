package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import java.util.Map;

public class TrackingMapModel {
    @NonNull
    public Map<String, Object> toMap() {
        return JsonUtil.getMapFromObject(this);
    }
}