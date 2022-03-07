package com.sweetcam.app.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.sweetcam.app.utils.getResourceByFolder
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.loadWith
import com.sweetcam.app.R
import com.sweetcam.app.adapter.OldAdapter
import com.sweetcam.app.utils.click
import kotlinx.android.synthetic.main.activity_ages.*

class OldActivity : BaseActivity(R.layout.activity_ages) {

    override fun onConvert() {
        val url = intent.getStringExtra("url")
        url?.let {
            age_pv.loadWith(it)
            recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recycler.adapter = OldAdapter(getResourceByFolder( R.mipmap::class.java, "mipmap", "icon_age"))
            cancel.click {
                finish()
            }
            save.click {
                finish()
            }
        } ?: kotlin.run {
            finish()
        }
    }
}