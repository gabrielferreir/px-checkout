package com.mercadopago.android.px.internal.features.security_code

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.BackHandler
import com.mercadopago.android.px.internal.base.BasePresenter
import com.mercadopago.android.px.internal.base.MvpView
import com.mercadopago.android.px.internal.base.PXActivity
import com.mercadopago.android.px.internal.extensions.runIfNull
import com.mercadopago.android.px.internal.features.security_code.model.SecurityCodeParams

private const val EXTRA_PARAMS = "bundle_params"

internal class SecurityCodeActivity : PXActivity<BasePresenter<MvpView>>() {

    override fun onCreated(savedInstanceState: Bundle?) {
        setContentView(R.layout.px_activity_security_code_new)
        supportFragmentManager.findFragmentByTag(SecurityCodeFragment.TAG).runIfNull {
            intent.getParcelableExtra<SecurityCodeParams>(EXTRA_PARAMS)?.let {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.container, SecurityCodeFragment.newInstance(it), SecurityCodeFragment.TAG)
                    commitAllowingStateLoss()
                }
            }
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentByTag(SecurityCodeFragment.TAG)?.let {
            if (it is BackHandler && it.isAdded) {
                if (it.handleBack()) return
            }
        }
        super.onBackPressed()
    }

    companion object {
        @JvmStatic
        fun start(fragment: Fragment, params: SecurityCodeParams, requestCode: Int) {
            fragment.startActivityForResult(Intent(fragment.context, SecurityCodeActivity::class.java).also {
                it.putExtra(EXTRA_PARAMS, params)
            }, requestCode)
        }

        @JvmStatic
        fun start(activity: Activity, params: SecurityCodeParams, requestCode: Int) {
            activity.startActivityForResult(Intent(activity, SecurityCodeActivity::class.java).also {
                it.putExtra(EXTRA_PARAMS, params)
            }, requestCode)
        }
    }
}
