package com.sweetcam.app.ui.activity

import android.view.View
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.callback.IDialogCallBack
import com.sweetcam.app.ui.dialog.ContentDialog

class MainActivity : BaseActivity(R.layout.act_main), IDialogCallBack, View.OnClickListener {

    override fun onConvert() {

    }

    override fun onBackPressed() {
        ContentDialog.newInstance("Are you sure to exit the application?")
            .show(supportFragmentManager, "")
    }

    override fun onClick(position: Int) {
        if (position == 0) {
            super.onBackPressed()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.sticker -> {

            }
            R.id.slimming -> {

            }
            R.id.cartoon -> {

            }
            R.id.age -> {

            }
            R.id.mainCamera -> {

            }
        }
    }
}