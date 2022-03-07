package com.sweetcam.app.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.sweetcam.app.Constant
import com.sweetcam.app.callback.IDialogCallBack
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseDialog
import com.sweetcam.app.utils.app
import com.sweetcam.app.utils.click
import kotlinx.android.synthetic.main.dialog_exit.*

class ContentDialog : BaseDialog() {

    companion object {
        fun newInstance(
            content: String = "",
            leftVisible: Boolean = true,
            rightVisible: Boolean = true,
        ) = ContentDialog().apply {
            arguments = bundleOf(
                Constant.KEY_COMMON_CONTENT to content,
                Constant.KEY_COMMON_DATA to leftVisible,
                Constant.KEY_COMMON_ARGUMENT to rightVisible
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as? IDialogCallBack)?.let {
            iDialogCallBack = it
        }
    }

    private var iDialogCallBack: IDialogCallBack? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutInflater.from(app).inflate(R.layout.dialog_exit, container, false)
    }

    private var leftVisible = true
    private var rightVisible = true
    private var content = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            content = it.getString(Constant.KEY_COMMON_CONTENT, "")
            leftVisible = it.getBoolean(Constant.KEY_COMMON_DATA, true)
            rightVisible = it.getBoolean(Constant.KEY_COMMON_ARGUMENT, true)
        }
        title.text = content

        yes.let {
            it.isVisible = leftVisible
            it.click {
                dismiss()
                iDialogCallBack?.onClick(0)
            }
        }
        no.let {
            it.isVisible = rightVisible
            it.click {
                dismiss()
                iDialogCallBack?.onClick(2)
            }
        }
    }
}