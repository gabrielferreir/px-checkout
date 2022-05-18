package com.mercadopago.android.px.internal.view;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.mercadopago.android.px.R;

public class LabeledSwitch extends LinearLayoutCompat implements CompoundButton.OnCheckedChangeListener {

    private final TextView label;
    private final Switch lSwitch;
    private CompoundButton.OnCheckedChangeListener listener;

    public LabeledSwitch(final Context context) {
        this(context, null, 0);
    }

    public LabeledSwitch(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabeledSwitch(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.px_view_labeled_switch, this, true);
        setOrientation(VERTICAL);
        label = findViewById(R.id.label);
        lSwitch = findViewById(R.id.lSwitch);
        lSwitch.setOnCheckedChangeListener(this);
    }

    public void setText(final CharSequence charSequence) {
        label.setText(charSequence);
    }

    public void setChecked(final boolean checked) {
        lSwitch.setChecked(checked);
    }

    public void setOnCheckedChanged(final CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        if (listener != null) {
            listener.onCheckedChanged(buttonView, isChecked);
        }
    }
}