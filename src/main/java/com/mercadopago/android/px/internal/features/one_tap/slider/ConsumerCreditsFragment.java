package com.mercadopago.android.px.internal.features.one_tap.slider;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import com.meli.android.carddrawer.model.CardDrawerView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.presentation.extensions.DrawableExtKt;
import com.mercadopago.android.px.core.presentation.extensions.ViewExtKt;
import com.mercadopago.android.px.internal.di.MapperProvider;
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesLinkableMapper;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.LinkableTextView;
import com.mercadopago.android.px.internal.viewmodel.DisableConfiguration;
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem;
import com.mercadopago.android.px.model.ConsumerCreditsDisplayInfo;
import com.mercadopago.android.px.model.internal.Text;

public class ConsumerCreditsFragment extends PaymentMethodFragment<ConsumerCreditsDrawableFragmentItem> {

    private ConstraintLayout creditsLayout;
    private ImageView background;
    private ImageView logo;
    private LinkableTextView topText;
    private LinkableTextView bottomText;
    private ViewGroup tagContainer;
    private final RemediesLinkableMapper remediesLinkableMapper = MapperProvider.INSTANCE.getRemediesLinkableMapper();

    @NonNull
    public static Fragment getInstance(final ConsumerCreditsDrawableFragmentItem model) {
        final ConsumerCreditsFragment instance = new ConsumerCreditsFragment();
        instance.storeModel(model);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.px_fragment_consumer_credits, container, false);
    }

    @Override
    public void initializeViews(@NonNull final View view) {
        super.initializeViews(view);
        creditsLayout = view.findViewById(R.id.credits_layout);
        background = view.findViewById(R.id.background);
        logo = view.findViewById(R.id.logo);
        topText = view.findViewById(R.id.top_text);
        bottomText = view.findViewById(R.id.bottom_text);
        tagContainer = view.findViewById(R.id.card_tag_container);
        final ConsumerCreditsDisplayInfo displayInfo = model.metadata.displayInfo;
        tintBackground(background, displayInfo.color);
        showDisplayInfo(displayInfo);
        if (model.tag != null) {
            showTag(model.tag);
        }
        configureListener();
        view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
    }

    private void showTag(@NonNull final Text tag) {
        AppCompatTextView text = tagContainer.findViewById(R.id.card_cc_tag);
        tagContainer.setVisibility(View.VISIBLE);
        text.setText(tag.getMessage());
        ViewUtils.setTextColor(text, tag.getTextColor());
        DrawableExtKt.setColorFilter(text.getBackground(),tag.getBackgroundColor(),Color.WHITE);
        FontHelper.setFont(text, PxFont.from(tag.getWeight()));
    }

    @Override
    protected void updateCardDrawerView(@NonNull final CardDrawerView cardDrawerView) { }

    public void showDisplayInfo(@NonNull final ConsumerCreditsDisplayInfo displayInfo) {
        if (topText != null) {
            topText.updateModel(remediesLinkableMapper.map(displayInfo.topText));
        }

        if (bottomText != null) {
            bottomText.updateModel(remediesLinkableMapper.map(displayInfo.bottomText));
        }
    }

    public void configureListener() {
        final Fragment parent = getParentFragment();
        LinkableTextView.LinkableTextListener listener = null;
        if (parent instanceof LinkableTextView.LinkableTextListener) {
            listener = (LinkableTextView.LinkableTextListener) parent;
        }

        if (listener != null && topText != null) {
            topText.setLinkableTextListener(listener);
        }

        if (listener != null && bottomText != null) {
            bottomText.setLinkableTextListener(listener);
        }
    }

    @Override
    public void disable() {
        super.disable();
        final DisableConfiguration disableConfiguration = new DisableConfiguration(getContext());
        ViewExtKt.grayScaleViewGroup(creditsLayout);
        background.clearColorFilter();
        background.setImageResource(0);
        background.setBackgroundColor(disableConfiguration.getBackgroundColor());
        if (topText != null) {
            topText.setVisibility(View.GONE);
        }
        bottomText.setVisibility(View.GONE);
        centerLogo();
    }

    private void centerLogo() {
        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(creditsLayout);
        constraintSet.connect(logo.getId(), ConstraintSet.LEFT, creditsLayout.getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(logo.getId(), ConstraintSet.RIGHT, creditsLayout.getId(), ConstraintSet.RIGHT, 0);
        constraintSet.connect(logo.getId(), ConstraintSet.TOP, creditsLayout.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(logo.getId(), ConstraintSet.BOTTOM, creditsLayout.getId(), ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(creditsLayout);
    }

    @Override
    protected String getAccessibilityContentDescription() {
        return model.getCommonsByApplication().getCurrent().getDescription();
    }
}