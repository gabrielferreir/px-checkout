package com.mercadopago.android.px.tracking.internal.views;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.BankInfoHelper;
import com.mercadopago.android.px.tracking.internal.mapper.FromApplicationToApplicationInfo;
import com.mercadopago.android.px.tracking.internal.model.DiscountInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class OneTapViewTest {

    private static final String EXPECTED_PATH = "/px_checkout/review/one_tap";
    private static final int DISABLED_METHODS_QUANTITY = 0;

    @Mock
    private DiscountConfigurationModel discountModel;

    @Mock private CheckoutPreference checkoutPreference;

    @Mock
    private BankInfoHelper bankInfoHelper;

    @Test
    public void verifyPath() {
        assertEquals(EXPECTED_PATH,
            new OneTapViewTracker(mock(FromApplicationToApplicationInfo.class),
                Collections.EMPTY_LIST, checkoutPreference, discountModel, Collections.emptySet(),
                Collections.emptySet(), DISABLED_METHODS_QUANTITY, Collections.emptyList(),
                    bankInfoHelper).getTrack().getPath());
    }

    @Test
    public void verifyTrackData() {
        final OneTapViewTracker track =
            new OneTapViewTracker(mock(FromApplicationToApplicationInfo.class),
                Collections.EMPTY_LIST, checkoutPreference, discountModel, Collections.emptySet(),
                Collections.emptySet(), DISABLED_METHODS_QUANTITY, Collections.emptyList(),
                    bankInfoHelper);
        assertEquals(expectedOneTapData(), track.getTrack().getData());
    }

    @NonNull
    private Map<String, Object> expectedOneTapData() {
        final Map<String, Object> data = new HashMap<>();
        data.put("discount", JsonUtil.fromJson("{}", DiscountInfo.class).toMap());
        data.put("available_methods", Collections.EMPTY_LIST);
        data.put("available_methods_quantity", 0);
        data.put("disabled_methods_quantity", 0);
        data.put("items", Collections.EMPTY_LIST);
        data.put("total_amount", null);
        return data;
    }
}
