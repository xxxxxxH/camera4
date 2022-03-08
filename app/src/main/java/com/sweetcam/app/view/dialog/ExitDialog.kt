package com.sweetcam.app.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.flyco.dialog.widget.base.BaseDialog
import com.sweetcam.app.R
import com.sweetcam.app.utils.MessageEvent
import kotlinx.android.synthetic.main.dialog_exit.*
import org.greenrobot.eventbus.EventBus

class ExitDialog(context: Context):BaseDialog<ExitDialog>(context) {
    override fun onCreateView(): View {
        widthScale(0.85f)
        return View.inflate(context, R.layout.dialog_exit, null)
    }

    override fun setUiBeforShow() {
        setCanceledOnTouchOutside(false)
        yes.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("confirmExit"))
        }
        no.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("cancelExit"))
        }
    }

}