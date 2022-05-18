package com.mercadopago.android.px.internal.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.mercadopago.android.px.R;

public abstract class BasePagerFragment<P extends BasePresenter, M extends Parcelable> extends Fragment implements MvpView {

    private static final String ARG_MODEL = "ARG_MODEL";

    protected P presenter;
    protected M model;

    protected abstract P createPresenter();

    @Nullable
    @Override
    public Animation onCreateAnimation(final int transit, final boolean enter, final int nextAnim) {
        final Fragment parent = getParentFragment();
        // Apply the workaround only if this is a child fragment, and the parent
        // is being removed.
        if (!enter && parent != null && parent.isRemoving()) {
            // This is a workaround for the bug where child fragments disappear when
            // the parent is removed (as all children are first removed from the parent)
            // See https://code.google.com/p/android/issues/detail?id=55228
            final Animation doNothingAnim = new AlphaAnimation(1, 1);
            doNothingAnim.setStartOffset(getResources().getInteger(R.integer.cf_anim_duration));
            doNothingAnim.setDuration(getResources().getInteger(R.integer.cf_anim_duration));
            return doNothingAnim;
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    @CallSuper
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_MODEL)) {
            //noinspection ConstantConditions
            model = getArguments().getParcelable(ARG_MODEL);
        } else {
            throw new IllegalStateException(getClass().getSimpleName() + " does not contain model info");
        }
    }

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = createPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
        presenter = null;
    }

    protected void storeModel(final M model) {
        final Bundle bundle = getArguments() != null ? getArguments() : new Bundle();
        bundle.putParcelable(ARG_MODEL, model);
        setArguments(bundle);
    }

    @NonNull
    @Override
    public Context getContext() {
        //noinspection ConstantConditions
        return super.getContext();
    }
}