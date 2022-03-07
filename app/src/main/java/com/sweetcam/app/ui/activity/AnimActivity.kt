package com.sweetcam.app.ui.activity

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.sweetcam.app.utils.getResourceByFolder
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.loadWith
import com.sweetcam.app.utils.saveBitmap
import com.sweetcam.app.R
import com.sweetcam.app.adapter.AnimAdapter
import com.sweetcam.app.utils.click
import kotlinx.android.synthetic.main.activity_cartoons.*

class AnimActivity : BaseActivity(R.layout.activity_cartoons) {

    override fun onConvert() {
        val url = intent.getStringExtra("url")
        url?.let {
            slimming_pv.loadWith(it)
            recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recycler.adapter = AnimAdapter(getResourceByFolder(R.mipmap::class.java, "mipmap", "cartoon"))
            cancel.click {
                finish()
            }
            save.click {
                val d = slimming_pv.drawable as Drawable
                val bd: BitmapDrawable = d as BitmapDrawable
                val b = bd.bitmap
                saveBitmap(System.currentTimeMillis().toString(), b)
            }
        } ?: kotlin.run {
            finish()
        }
    }
}