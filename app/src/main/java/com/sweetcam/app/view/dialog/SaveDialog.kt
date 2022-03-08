package com.sweetcam.app.view.dialog

import android.content.Context
import android.view.View
import com.flyco.dialog.widget.base.BaseDialog
import com.sweetcam.app.R
import com.sweetcam.app.utils.MessageEvent
import kotlinx.android.synthetic.main.dialog_save.*
import org.greenrobot.eventbus.EventBus

class SaveDialog(context: Context):BaseDialog<SaveDialog>(context) {
    override fun onCreateView(): View {
        widthScale(0.85f)
        return View.inflate(context, R.layout.dialog_save, null)
    }

    override fun setUiBeforShow() {
        setCanceledOnTouchOutside(false)
        yes.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("save"))
        }
    }

    override fun onBackPressed() {

    }
}