package com.mercadopago.android.px.internal.util;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.mercadopago.android.px.model.ExternalFragment;

public final class FragmentUtil {

    private FragmentUtil() {
    }

    public static void tryRemoveNow(@NonNull final FragmentManager fragmentManager, @NonNull final String tag) {
        final Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            final FragmentTransaction transaction = fragmentManager.beginTransaction().remove(fragment);
            try {
                transaction.commitNowAllowingStateLoss();
            } catch (final IllegalStateException e) {
                transaction.commitAllowingStateLoss();
            }
        }
    }

    public static void replaceFragment(@NonNull final ViewGroup container, @NonNull final ExternalFragment model) {
        if (container.getContext() instanceof AppCompatActivity) {
            final AppCompatActivity activity = (AppCompatActivity) container.getContext();
            final Fragment fragment = FragmentUtil.createInstance(model.zClassName);
            fragment.setArguments(model.args);
            activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(container.getId(), fragment)
                .commit();
        } else {
            throw new IllegalArgumentException("Container context is not a activity");
        }
    }

    @Nullable
    public static Fragment getFragmentByTag(@NonNull final FragmentManager manager, @NonNull final String tag) {
        final Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null && fragment.isAdded() && fragment.getUserVisibleHint()) {
            return fragment;
        }
        return null;
    }

    public static boolean isFragmentVisible(@NonNull final FragmentManager manager, @NonNull final String tag) {
        return getFragmentByTag(manager, tag) != null;
    }

    @NonNull
    private static Fragment createInstance(@NonNull final String className) {
        try {
            final Class<Fragment> clazz = (Class<Fragment>) Class.forName(className);
            return clazz.newInstance();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final InstantiationException e) {
            e.printStackTrace();
        }

        return new Fragment();
    }
}