package com.mercadopago.android.px.core;

import com.mercadopago.android.px.internal.util.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonTest  {
    private final Map<String, Object> body = new HashMap<>();

    @Before
    public void setUp(){
        body.put("card_token_id", "TOKEN_ID");
        final Map<String, Object> paymentMethodBody = new HashMap<>();
        paymentMethodBody.put("id", "PAYMENT_METHOD_ID");
        final Map<String, Object> issuerBody = new HashMap<>();
        issuerBody.put("id", "ISSUER_ID");
        body.put("payment_method", paymentMethodBody);
        body.put("issuer", issuerBody);
    }

    @Test
    public void testHashMapSerialization() {
        String json = JsonUtil.toJson(body);
        Assert.assertNotEquals("{}", json);
    }

    @Test
    public void testHashMapDeserialization() {
        final Map<String, Object> mapFromObject = JsonUtil.getMapFromObject(body);
        Assert.assertNotNull(mapFromObject);
    }
}