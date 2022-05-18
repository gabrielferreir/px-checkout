package com.mercadopago.android.px.internal.base

import android.content.Context
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    protected var fragmentCommunicationViewModel: FragmentCommunicationViewModel? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PXActivity<*>) {
            fragmentCommunicationViewModel = context.fragmentCommunicationViewModel
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentCommunicationViewModel = null
    }

    protected fun forceBack() {
        if (context is PXActivity<*>) {
            (context as PXActivity<*>).forceBack()
        }
    }

    companion object {
        const val BUNDLE_STATE = "bundle_state"
    }
}
