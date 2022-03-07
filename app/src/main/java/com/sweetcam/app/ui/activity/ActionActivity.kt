package com.sweetcam.app.ui.activity

import com.sweetcam.app.callback.IDialogCallBack
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.loadWith
import com.sweetcam.app.R
import com.sweetcam.app.ui.dialog.ContentDialog
import com.sweetcam.app.ui.dialog.ShareDialog
import com.sweetcam.app.utils.click
import kotlinx.android.synthetic.main.activity_slimming.*

class ActionActivity : BaseActivity(R.layout.activity_slimming), IDialogCallBack {

    private val actionSlimmingList by lazy {
        mutableListOf(
            activityActionTvBottomActionSlimming,
            activityActionTvBottomActionWaist,
            activityActionTvBottomActionLegs,
            activityActionTvBottomActionLegsLength,
            activityActionTvBottomActionBreast,
            activityActionTvBottomActionShoulder
        )
    }

    override fun onConvert() {
        val url = intent.getStringExtra("url")
        url?.let {
            slimming_pv.loadWith(it)
            cancel.click { finish() }
            save.click {
                ContentDialog.newInstance(
                    "Saving", rightVisible = false
                ).show(supportFragmentManager, "")
            }
            actionSlimmingList.forEach {
                it.click { clickView ->
                    actionSlimmingList.forEach {
                        if (it.id == clickView.id) {
                            it.isSelected = !it.isSelected
                        } else {
                            it.isSelected = false
                        }
                    }
                }
            }


        } ?: kotlin.run {
            finish()
        }
    }

    override fun onClick(position: Int) {
        if (position == 0) {
            ShareDialog.newInstance().show(supportFragmentManager, "")
        }
    }
}