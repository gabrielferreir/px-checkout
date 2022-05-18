package com.mercadopago.android.px.internal.features.checkout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

internal class CheckoutDeeplinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, CheckoutActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            it.data = intent.data
        })
    }
}
