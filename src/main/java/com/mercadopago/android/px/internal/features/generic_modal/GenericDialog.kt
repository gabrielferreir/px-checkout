package com.mercadopago.android.px.internal.features.generic_modal

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.view.View
import android.widget.Button
import com.mercadolibre.android.picassodiskcache.loadImage
import com.mercadolibre.android.ui.widgets.MeliDialog
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.di.Session
import com.mercadopago.android.px.internal.extensions.gone
import com.mercadopago.android.px.internal.extensions.runIfNull
import com.mercadopago.android.px.internal.extensions.visible
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.internal.view.OnSingleClickListener
import kotlinx.android.synthetic.main.px_dialog_generic.*

class GenericDialog : MeliDialog() {

    private lateinit var viewModel: GenericDialogViewModel
    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = when {
            targetFragment is Listener -> {
                targetFragment as Listener
            }
            parentFragment is Listener -> {
                parentFragment as Listener
            }
            context is Listener -> {
                context
            }
            else -> {
                throw IllegalStateException("Parent of ${javaClass.simpleName} " +
                    "should implement ${Listener::class.java.simpleName}")
            }
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel = GenericDialogViewModel(it.getParcelable(ARG_GENERIC_DIALOG_ITEM)!!, Session.getInstance().tracker)
        }
        savedInstanceState.runIfNull { viewModel.trackLoadDialog() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dialogState.nonNullObserve(viewLifecycleOwner) { state ->
            when (state) {
                is GenericDialogState.LoadView -> loadView(state.item)
                is GenericDialogState.ButtonClicked -> {
                    super.dismiss()
                    listener?.apply {
                        onAction(state.action)
                    }
                }
            }
        }

    }

    private fun loadView(item: GenericDialogItem) {
        with(item) {
            item.imageUrl?.let { if(it.isNotEmpty()) image.loadImage(it) } ?: image.gone()
            with(dialog_title) { ViewUtils.loadOrGone(item.title.get(context), this) }
            with(dialog_description) { ViewUtils.loadOrGone(item.description.get(context), this) }
            mainAction?.let {
                loadButton(main_button, it.label) { viewModel.onButtonClicked(it, true) }
            }
            secondaryAction?.let {
                loadButton(secondary_button, it.label) { viewModel.onButtonClicked(it, false) }
            }
        }
    }

    private fun loadButton(button: Button, text: String, callback: () -> Unit) {
        with(button) {
            visible()
            this.text = text
            setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    callback()
                }
            })
        }
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.onDialogDismissed()
        listener?.onAction(GenericDialogAction.CustomAction(ActionType.DISMISS))
    }

    override fun getContentView(): Int {
        return R.layout.px_dialog_generic
    }

    companion object {
        private const val TAG = "GENERIC_DIALOG_TAG"
        private const val ARG_GENERIC_DIALOG_ITEM = "arg_generic_dialog_item"

        @JvmStatic
        fun showDialog(fragmentManager: FragmentManager, item: GenericDialogItem): GenericDialog {
            val instance = GenericDialog()
            val arguments = Bundle()
            arguments.putParcelable(ARG_GENERIC_DIALOG_ITEM, item)
            instance.arguments = arguments
            instance.show(fragmentManager, TAG)
            return instance
        }
    }

    interface Listener {
        fun onAction(genericDialogAction: GenericDialogAction)
    }
}