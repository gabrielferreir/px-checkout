package com.mercadopago.android.px.internal.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.addons.BehaviourProvider;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.font.FontHelper;

public abstract class PXActivity<P extends BasePresenter> extends AppCompatActivity implements MvpView {

    protected P presenter;

    @Override
    protected final void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontHelper.init(getApplicationContext());
        if (shouldHaltSession(Session.getInstance().getSessionState())) {
            onHalted();
            setResult(MercadoPagoCheckout.SESSION_EXPIRED_RESULT_CODE);
            finish();
        } else {
            onCreated(savedInstanceState);
        }
    }

    public FragmentCommunicationViewModel getFragmentCommunicationViewModel() {
        return Session.getInstance().getViewModelModule().get(this, FragmentCommunicationViewModel.class);
    }

    protected abstract void onCreated(@Nullable final Bundle savedInstanceState);

    protected void onHalted() {
    }

    protected boolean shouldHaltSession(@NonNull final Session.State state) {
        return state != Session.State.VALID;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    @Override
    public void attachBaseContext(@NonNull final Context context) {
        super.attachBaseContext(BehaviourProvider.getLocaleBehaviour().attachBaseContext(context));
    }

    @Override
    public void onBackPressed() {
        doBack();
    }

    public final void forceBack() {
        doBack();
    }

    private void doBack() {
        if (presenter != null) {
            presenter.trackBack();
        }
        super.onBackPressed();
    }

    public void overrideTransitionIn() {
        overridePendingTransition(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out);
    }

    public void overrideTransitionOut() {
        overridePendingTransition(R.anim.px_slide_left_to_right_in, R.anim.px_slide_left_to_right_out);
    }
}