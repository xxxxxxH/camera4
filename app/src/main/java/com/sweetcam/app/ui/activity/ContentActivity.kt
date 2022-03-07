package com.sweetcam.app.ui.activity

import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.loadWith
import com.sweetcam.app.R
import com.sweetcam.app.utils.click
import kotlinx.android.synthetic.main.activity_image.*

class ContentActivity : BaseActivity(R.layout.activity_image) {

    override fun onConvert() {
        val url = intent.getStringExtra("url")
        url?.let {
            iv.loadWith(it)
            cancel.click {
                finish()
            }
            confirm.click {
                finish()
            }
        } ?: kotlin.run {
            finish()
        }

    }
}