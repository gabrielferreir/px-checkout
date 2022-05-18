package com.mercadopago.android.px.internal.di

import androidx.lifecycle.ViewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider

internal class ViewModelModule {
    private val factory = ViewModelFactory()

    fun <T : ViewModel?> get(fragment: Fragment, modelClass: Class<T>): T {
        return ViewModelProvider(fragment, factory).get(modelClass)
    }

    fun <T : ViewModel?> get(activity: FragmentActivity, modelClass: Class<T>): T {
        return ViewModelProvider(activity, factory).get(modelClass)
    }
}