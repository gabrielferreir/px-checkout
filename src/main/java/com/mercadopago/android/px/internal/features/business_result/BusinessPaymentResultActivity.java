package com.mercadopago.android.px.internal.features.business_result;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.mercadolibre.android.mlbusinesscomponents.components.touchpoint.tracking.print.MLBusinessTouchpointListener;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.addons.BehaviourProvider;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.MapperProvider;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.Constants;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel;
import com.mercadopago.android.px.internal.features.payment_result.presentation.PaymentResultFooter;
import com.mercadopago.android.px.internal.util.Logger;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;
import com.mercadopago.android.px.model.ExitAction;

import static com.mercadopago.android.px.internal.features.Constants.RESULT_CUSTOM_EXIT;
import static com.mercadopago.android.px.internal.util.MercadoPagoUtil.getSafeIntent;
import static com.mercadopago.android.px.internal.util.MercadoPagoUtil.isMP;

public class BusinessPaymentResultActivity extends PXActivity<BusinessPaymentResultPresenter>
    implements BusinessPaymentResult.View {

    private static final String TAG = BusinessPaymentResultActivity.class.getSimpleName();
    private static final String PAYMENT_CONGRATS = "payment_congrats";

    private PaymentResultFooter footer;

    @Override
    protected boolean shouldHaltSession(@NonNull final Session.State state) {
        return state == Session.State.INVALID;
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        setContentView(R.layout.px_activity_payment_result);
        footer = findViewById(R.id.footer);

        presenter = createPresenter();
        presenter.attachView(this);
        if (savedInstanceState == null) {
            presenter.onFreshStart();
        }
        new MLBusinessTouchpointListener().setOnTouchListener(findViewById(R.id.scroll_view));
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @NonNull
    private BusinessPaymentResultPresenter createPresenter() {
        final PaymentCongratsModel model = getIntent().getParcelableExtra(PAYMENT_CONGRATS);
        return new BusinessPaymentResultPresenter(model,
            BehaviourProvider.getFlowBehaviour(), isMP(this), Session.getInstance().getTracker(),
            MapperProvider.INSTANCE.getBusinessPaymentResultMapper(),
            Session.getInstance().getHelperModule().getBankInfoHelper());
    }

    @Override
    public void configureViews(@NonNull final BusinessPaymentResultViewModel model,
        @NonNull final PaymentResultBody.Listener bodyListener,
        @NonNull final PaymentResultFooter.Listener footerListener) {
        final PaymentResultHeader header = findViewById(R.id.header);
        header.setModel(model.getHeaderModel());
        final PaymentResultBody body = findViewById(R.id.body);
        body.init(model.getBodyModel(), bodyListener);
        footer.init(model.getFooterModel(), footerListener);
    }

    @Override
    public void onBackPressed() {
        presenter.onAbort();
    }

    @Override
    public void processCustomExit(@Nullable final String backUrl, @Nullable final String redirectUrl) {
        processCustomExit(new ExitAction("exit", RESULT_OK), backUrl, redirectUrl);
    }

    @Override
    public void processCustomExit(@NonNull final ExitAction action, @Nullable final String backUrl,
        @Nullable final String redirectUrl) {
        final Intent intent = action.toIntent();
        intent.putExtra(Constants.EXTRA_BACK_URL, backUrl);
        intent.putExtra(Constants.EXTRA_REDIRECT_URL, redirectUrl);
        setResult(RESULT_CUSTOM_EXIT, intent);
        finish();
    }

    @Override
    public void setStatusBarColor(@ColorRes final int color) {
        ViewUtils.setStatusBarColor(ContextCompat.getColor(this, color), getWindow());
    }

    @Override
    public void updateAutoReturnLabel(@NonNull final String label) {
        footer.showAutoReturn();
        footer.updateAutoReturnLabel(label);
    }

    @Override
    public void launchDeepLink(@NonNull final String deepLink) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)));
        } catch (final ActivityNotFoundException e) {
            Logger.debug(TAG, e);
        }
    }

    @Override
    public void processCrossSellingBusinessAction(@NonNull final String deepLink) {
        try {
            startActivity(getSafeIntent(this, Uri.parse(deepLink)));
        } catch (final ActivityNotFoundException e) {
            Logger.debug(TAG, e);
        }
    }
}
