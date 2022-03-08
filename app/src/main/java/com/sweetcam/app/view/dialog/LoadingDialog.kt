package com.sweetcam.app.view.dialog

import android.content.Context
import android.view.View
import com.flyco.dialog.widget.base.BaseDialog
import com.sweetcam.app.R

class LoadingDialog(context: Context):BaseDialog<LoadingDialog>(context) {
    override fun onCreateView(): View {
        widthScale(0.85f)
        return View.inflate(context, R.layout.dialog_share, null)
    }

    override fun setUiBeforShow() {

    }
}