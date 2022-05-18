package com.mercadopago.android.px.internal.di

import android.content.ComponentCallbacks
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel


internal inline fun <reified T : ViewModel> ComponentCallbacks.viewModel(): Lazy<T> {

    val viewModelModule = Session.getInstance().viewModelModule

    return  when(this) {
        is FragmentActivity -> {
            lazy { viewModelModule.get(this, T::class.java) }
        }

        is Fragment -> {
            lazy { viewModelModule.get(this, T::class.java) }
        }

        else -> error("Component must be a Fragment or FragmentActivity")
    }
}