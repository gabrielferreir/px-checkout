package com.mercadopago.android.px.internal.features.one_tap.slider;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.presentation.extensions.SpannableExtKt;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.util.textformatter.AmountLabeledFormatter;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;
import com.mercadopago.android.px.internal.view.LabeledSwitch;
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.Split;
import com.mercadopago.android.px.model.internal.Application;
import java.util.List;

public class SplitPaymentHeaderAdapter extends HubableAdapter<List<SplitPaymentHeaderAdapter.Model>, LabeledSwitch>
    implements CompoundButton.OnCheckedChangeListener {

    @NonNull private final SplitListener splitListener;

    public interface SplitListener {
        void onSplitChanged(final boolean isChecked);
    }

    public abstract static class Model {
        public abstract void visit(final LabeledSwitch labeledSwitch);

        public abstract void visit(final boolean isChecked);
    }

    public static final class Empty extends Model {
        @Override
        public void visit(final LabeledSwitch labeledSwitch) {
            labeledSwitch.clearAnimation();
            labeledSwitch.setVisibility(View.GONE);
        }

        @Override
        public void visit(final boolean isChecked) {
            // do nothing
        }
    }

    public static final class SplitModel extends Model {

        private final Currency currency;
        @NonNull private final Split split;
        private boolean isChecked;

        public SplitModel(@NonNull final Currency currency, @NonNull final Split split) {
            this.currency = currency;
            this.split = split;
            isChecked = split.defaultEnabled;
        }

        @Override
        public void visit(final LabeledSwitch labeledSwitch) {
            // ${amount} semibold, color black
            final Spannable amount =
                new AmountLabeledFormatter(new SpannableStringBuilder(), labeledSwitch.getContext())
                    .withSemiBoldStyle()
                    .withTextColor(
                        ContextCompat.getColor(labeledSwitch.getContext(), R.color.px_expressCheckoutTextColor))
                    .apply(TextFormatter
                        .withCurrency(currency)
                        .amount(split.secondaryPaymentMethod.getVisibleAmountToPay())
                        .normalDecimals()
                        .toSpannable());

            // create text message
            final SpannableStringBuilder message = new SpannableStringBuilder(TextUtil.SPACE)
                .append(split.secondaryPaymentMethod.message);

            // added color
            SpannableExtKt.setColor(message, ContextCompat.getColor(labeledSwitch.getContext(),
                    R.color.px_checkout_secondary_color), 0, message.length());
            // build definitive message
            labeledSwitch.setText(new SpannableStringBuilder(amount).append(message));
            labeledSwitch.setChecked(isChecked);
            labeledSwitch.setVisibility(View.VISIBLE);

            labeledSwitch.setContentDescription(new SpannableStringBuilder()
                .append(String.valueOf(split.secondaryPaymentMethod.getVisibleAmountToPay().floatValue()))
                .append(labeledSwitch.getContext().getString(R.string.px_money))
                .append(message));
        }

        @Override
        public void visit(final boolean isChecked) {
            this.isChecked = isChecked;
        }
    }

    public SplitPaymentHeaderAdapter(@NonNull final LabeledSwitch view, @NonNull final SplitListener splitListener) {
        super(view);
        this.splitListener = splitListener;
        view.setOnCheckedChanged(this);
    }

    @Override
    public void updateData(final int currentIndex, final int payerCostSelected,
        @NonNull final SplitSelectionState splitSelectionState, final @NonNull Application application) {
        // Empty data case
        if (currentIndex >= data.size()) {
            new Empty().visit(view);
            return;
        }

        final Model model = data.get(currentIndex);
        if (!splitSelectionState.preferDefault()) {
            model.visit(splitSelectionState.userWantsToSplit());
        }
        model.visit(view);
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        splitListener.onSplitChanged(isChecked);
    }

    @Override
    public List<Model> getNewModels(final HubAdapter.Model model) {
        return model.splitModels;
    }
}