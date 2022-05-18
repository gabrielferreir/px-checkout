package com.mercadopago.android.px.internal.view;

import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import com.mercadolibre.android.picassodiskcache.PicassoDiskLoader;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.viewmodel.GenericLocalized;
import com.squareup.picasso.RequestCreator;

public class PaymentResultHeader extends ConstraintLayout {

    private final MPTextView title;
    private final MPTextView label;
    private final ImageView icon;
    private final ImageView badge;

    private static final int BADGE_SIZE_DP = 90;

    public PaymentResultHeader(final Context context) {
        this(context, null);
    }

    public PaymentResultHeader(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentResultHeader(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.px_payment_result_header, this);
        title = findViewById(R.id.title);
        label = findViewById(R.id.label);
        icon = findViewById(R.id.icon);
        badge = findViewById(R.id.badge);
    }

    public void setModel(@NonNull final Model model) {
        if (model.dynamicHeight) {
            ViewUtils.stretchHeight(this);
        } else {
            ViewUtils.wrapHeight(this);
        }

        setBackgroundColor(ContextCompat.getColor(getContext(), model.background));
        ViewUtils.loadOrGone(model.title.get(getContext()), title);
        ViewUtils.loadOrGone(model.label.get(getContext()), label);
        renderIcon(icon, model);
        renderBadge(badge, model);
        post(() -> performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null));
    }

    private void renderIcon(@NonNull final ImageView icon, @NonNull final Model model) {
        final int size = ScaleUtil.getPxFromDp(BADGE_SIZE_DP, getContext());
        PicassoDiskLoader.get(getContext())
            .load(TextUtil.isNotEmpty(model.iconUrl) ? model.iconUrl : null)
            .transform(new CircleTransform())
            .resize(size, size)
            .centerInside()
            .noFade()
            .placeholder(model.iconImage)
            .into(icon);
    }

    private void renderBadge(@NonNull final ImageView badge, @NonNull final Model model) {
        final int size = ScaleUtil.getPxFromDp(BADGE_SIZE_DP, getContext());
        final RequestCreator requestCreator = PicassoDiskLoader.get(getContext())
            .load(TextUtil.isNotEmpty(model.badgeUrl) ? model.badgeUrl : null)
            .transform(new CircleTransform())
            .resize(size, size)
            .centerInside()
            .noFade();

        if (model.badgeImage != 0) {
            requestCreator.placeholder(model.badgeImage);
        }

        requestCreator.into(badge);
    }

    public static final class Model {

        /* default */ final boolean dynamicHeight;
        /* default */ final int background;
        /* default */ final int iconImage;
        /* default */ final int badgeImage;
        /* default */ final String iconUrl;
        /* default */ final String badgeUrl;
        /* default */ final GenericLocalized title;
        /* default */ final GenericLocalized label;

        /* default */ Model(@NonNull final Builder builder) {
            dynamicHeight = builder.dynamicHeight;
            background = builder.background;
            iconImage = builder.iconImage;
            iconUrl = builder.iconUrl;
            badgeUrl = builder.badgeUrl;
            badgeImage = builder.badgeImage;
            title = builder.title;
            label = builder.label;
        }

        @ColorRes
        public int getBackgroundColor() {
            return background;
        }

        public static class Builder {
            /* default */ boolean dynamicHeight;
            /* default */ int background;
            /* default */ int iconImage;
            /* default */ int badgeImage;
            @Nullable /* default */ String iconUrl;
            @Nullable /* default */ String badgeUrl;
            /* default */ GenericLocalized title;
            /* default */ GenericLocalized label;

            public Builder setBackground(@DrawableRes final int background) {
                this.background = background;
                return this;
            }

            public Builder setIconImage(@DrawableRes final int iconImage) {
                this.iconImage = iconImage;
                return this;
            }

            public Builder setIconUrl(@Nullable final String iconUrl) {
                this.iconUrl = iconUrl;
                return this;
            }

            public Builder setBadgeUrl(@Nullable final String badgeUrl) {
                this.badgeUrl = badgeUrl;
                return this;
            }

            public Builder setBadgeImage(@DrawableRes final int badgeImage) {
                this.badgeImage = badgeImage;
                return this;
            }

            public Builder setDynamicHeight(final boolean dynamicHeight) {
                this.dynamicHeight = dynamicHeight;
                return this;
            }

            public Builder setTitle(@NonNull final GenericLocalized title) {
                this.title = title;
                return this;
            }

            public Builder setLabel(@NonNull final GenericLocalized label) {
                this.label = label;
                return this;
            }

            public Model build() {
                return new Model(this);
            }
        }
    }
}