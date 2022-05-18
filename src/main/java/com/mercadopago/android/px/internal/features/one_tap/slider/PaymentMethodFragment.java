package com.mercadopago.android.px.internal.features.one_tap.slider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.meli.android.carddrawer.model.CardDrawerView;
import com.meli.android.carddrawer.model.Label;
import com.meli.android.carddrawer.model.customview.CardDrawerSwitch;
import com.meli.android.carddrawer.model.customview.SwitchModel;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.commons.extensions.StringExtKt;
import com.mercadopago.android.px.internal.base.BasePagerFragment;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.disable_payment_method.DisabledPaymentMethodDetailDialog;
import com.mercadopago.android.px.internal.features.generic_modal.GenericDialog;
import com.mercadopago.android.px.internal.features.generic_modal.GenericDialogAction;
import com.mercadopago.android.px.internal.features.generic_modal.GenericDialogItem;
import com.mercadopago.android.px.internal.util.MercadoPagoUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import com.mercadopago.android.px.model.internal.Text;

import static com.mercadopago.android.px.internal.util.AccessibilityUtilsKt.executeIfAccessibilityTalkBackEnable;

public abstract class PaymentMethodFragment<T extends DrawableFragmentItem>
    extends BasePagerFragment<PaymentMethodPresenter, T>
    implements PaymentMethod.View, Focusable, GenericDialog.Listener {

    private static final String SWITCH_MODEL_EXTRA = "switch_model";
    private static final int CONTENT_DESCRIPTION_DELAY = 800;

    private CardView card;
    private boolean focused;
    private Handler handler;
    private CardDrawerView cardDrawerView;
    @Nullable
    private SwitchModel switchModel;
    private PaymentMethodPagerListener listener = paymentTypeId -> {
    };
    private boolean hasHighlightText;

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof PaymentMethodPagerListener) {
            listener = (PaymentMethodPagerListener) getParentFragment();
        }
    }

    @Override
    protected PaymentMethodPresenter createPresenter() {
        return new PaymentMethodPresenter(
            Session.getInstance().getConfigurationModule().getPayerCostSelectionRepository(),
            Session.getInstance().getAmountConfigurationRepository(),
            model, Session.getInstance().getTracker());
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            switchModel = savedInstanceState.getParcelable(SWITCH_MODEL_EXTRA);
        }

        initializeViews(view);
        updateState();
    }

    protected boolean isDisableMethod() {
        return model.getCommonsByApplication().getCurrent().getDisabledPaymentMethod() != null;
    }

    @CallSuper
    public void initializeViews(@NonNull final View view) {
        card = view.findViewById(R.id.payment_method);
        cardDrawerView = view.findViewById(R.id.card);
        updateView();
        setUpCardDrawerCustomView();
    }

    @Override
    public void updateView() {
        if (hasFocus()) {
            onFocusIn();
        }

        if (cardDrawerView != null) {
            setBottomLabel();
            updateCardDrawerView(cardDrawerView);
        }
    }

    private void setBottomLabel() {
        if (!model.shouldHighlightBottomDescription()) {
            Text text = model.getBottomDescription();
            Label label =
                new Label(text.getMessage(), text.getBackgroundColor(), text.getTextColor(), text.getWeight(), false);
            cardDrawerView.setBottomLabel(label);
            cardDrawerView.showBottomLabel();
        }
    }

    @Override
    public void updateState() {
        if (isDisableMethod()) {
            disable();
        } else {
            enable();
        }
    }

    protected abstract void updateCardDrawerView(@NonNull final CardDrawerView cardDrawerView);

    private void setUpCardDrawerCustomView() {
        switchModel = switchModel != null ? switchModel : model.getSwitchModel();
        if (cardDrawerView != null && switchModel != null) {
            final CardDrawerSwitch cardDrawerSwitch = new CardDrawerSwitch(getContext());
            cardDrawerSwitch.setSwitchModel(switchModel);
            cardDrawerSwitch.setConfiguration(cardDrawerView.buildCustomViewConfiguration());
            cardDrawerView.setCustomView(cardDrawerSwitch);
            cardDrawerSwitch.setSwitchListener(this::onApplicationChanged);
        }
    }

    private void onApplicationChanged(@NonNull final String paymentTypeId) {
        updateSwitchModel(paymentTypeId);
        listener.onApplicationChanged(paymentTypeId);
        presenter.onApplicationChanged(paymentTypeId);
    }

    private void updateSwitchModel(@NonNull final String paymentTypeId) {
        final SwitchModel old = model.getSwitchModel();
        if (old != null) {
            switchModel = new SwitchModel(
                old.getDescription(),
                old.getStates(),
                old.getOptions(),
                old.getSwitchBackgroundColor(),
                old.getPillBackgroundColor(),
                old.getSafeZoneBackgroundColor(),
                paymentTypeId);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SWITCH_MODEL_EXTRA, switchModel);
    }

    protected String getAccessibilityContentDescription() {
        return TextUtil.EMPTY;
    }

    @Override
    public void updateHighlightText(@Nullable final String text) {
        hasHighlightText = cardDrawerView != null && TextUtil.isNotEmpty(text);
        if (hasHighlightText) {
            cardDrawerView.setBottomLabel(new Label(text));
        }
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onFocusIn();
        } else {
            onFocusOut();
        }
    }

    @Override
    public void onFocusIn() {
        focused = true;
        if (presenter != null) {
            presenter.onFocusIn();
            executeIfAccessibilityTalkBackEnable(getContext(), () -> {
                updateForAccessibility();
                return null;
            });
        }
    }

    @Override
    public void onFocusOut() {
        focused = false;
        if (presenter != null) {
            presenter.onFocusOut();
            executeIfAccessibilityTalkBackEnable(getContext(), () -> {
                clearForAccessibility();
                return null;
            });
        }
    }

    private void updateForAccessibility() {
        final String description = getAccessibilityContentDescription();

        if (TextUtil.isNotEmpty(description)) {
            setDescriptionForAccessibility(description);
        }

        if (isDisableMethod()) {
            String statusMessage =
                model.getCommonsByApplication().getCurrent().getStatus().getMainMessage().getMessage();
            statusMessage = TextUtil.isNotEmpty(statusMessage) ? statusMessage : TextUtil.EMPTY;
            if (TextUtil.isNotEmpty(statusMessage)) {
                setDescriptionForAccessibility(statusMessage);
            }
        }
    }

    private void setDescriptionForAccessibility(@NonNull final CharSequence description) {
        final View rootView = getView();
        final View parent;
        if (rootView != null && rootView.getParent() instanceof View &&
            (parent = (View) rootView.getParent()).isAccessibilityFocused()) {
            parent.announceForAccessibility(description);
        }
        if (handler != null) {
            handler.postDelayed(() -> card.setContentDescription(description), CONTENT_DESCRIPTION_DELAY);
        }
    }

    private void clearForAccessibility() {
        card.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED);
        card.setContentDescription(TextUtil.SPACE);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean hasFocus() {
        return focused;
    }

    @Override
    public void animateHighlightMessageIn() {
        if (shouldAnimate()) {
            cardDrawerView.showBottomLabel();
        }
    }

    @Override
    public void animateHighlightMessageOut() {
        if (shouldAnimate()) {
            cardDrawerView.hideBottomLabel();
        }
    }

    private boolean shouldAnimate() {
        return cardDrawerView != null && hasHighlightText;
    }

    @Override
    public void disable() {
        final Fragment parentFragment = getParentFragment();
        final DisabledPaymentMethod disabledPaymentMethod =
            model.getCommonsByApplication().getCurrent().getDisabledPaymentMethod();

        if (!(parentFragment instanceof DisabledDetailDialogLauncher)) {
            throw new IllegalStateException(
                "Parent fragment should implement " + DisabledDetailDialogLauncher.class.getSimpleName());
        }
        if (disabledPaymentMethod == null) {
            throw new IllegalStateException(
                "Should have a disabledPaymentMethod to disable");
        }

        card.setOnClickListener(
            v -> DisabledPaymentMethodDetailDialog
                .showDialog(parentFragment, ((DisabledDetailDialogLauncher) parentFragment).getRequestCode(),
                    disabledPaymentMethod.getPaymentStatusDetail(),
                    model.getCommonsByApplication().getCurrent().getStatus()));
    }

    public void enable() {
        final GenericDialogItem genericDialogItem = model.getCommonsByApplication().getCurrent().getGenericDialogItem();

        if (genericDialogItem != null) {
            card.setOnClickListener(v -> GenericDialog.showDialog(getChildFragmentManager(), genericDialogItem));
        } else {
            card.setOnClickListener(null);
        }
    }

    @Override
    public void onAction(@NonNull final GenericDialogAction genericDialogAction) {
        if (genericDialogAction instanceof GenericDialogAction.DeepLinkAction) {
            startDeepLink(((GenericDialogAction.DeepLinkAction) genericDialogAction).getDeepLink());
        }
    }

    private void startDeepLink(@NonNull final String deepLink) {
        try {
            startActivity(MercadoPagoUtil.getIntent(deepLink));
        } catch (@NonNull final ActivityNotFoundException e) {
            final String errorMessage = StringExtKt.orIfEmpty(e.getLocalizedMessage(), StringExtKt.EMPTY);
            presenter.onTrackActivityNotFoundFriction(MercadoPagoError.createNotRecoverable(errorMessage));
        }
    }

    protected void tintBackground(@NonNull final ImageView background, @NonNull final String color) {
        final int backgroundColor = Color.parseColor(color);
        final int lighterBackgroundColor = ContextCompat.getColor(background.getContext(), R.color.design_mp_blue);

        Color.argb(0, 0, 0, 0);
        final int[] ints = { backgroundColor, lighterBackgroundColor };
        final GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
            ints);

        gradientDrawable.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.px_xs_margin));
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setDither(true);

        background.setImageDrawable(gradientDrawable);
    }

    public interface DisabledDetailDialogLauncher {
        default int getRequestCode() {
            return 0;
        }
    }

    public interface PaymentMethodPagerListener {
        void onApplicationChanged(@NonNull final String paymentTypeId);
    }
}
