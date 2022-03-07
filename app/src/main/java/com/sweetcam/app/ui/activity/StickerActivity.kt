package com.sweetcam.app.ui.activity

import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.sweetcam.app.callback.IDialogCallBack
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.R
import com.sweetcam.app.adapter.StickerAdapter
import com.sweetcam.app.ui.dialog.ContentDialog
import com.sweetcam.app.ui.dialog.ShareDialog
import com.sweetcam.app.utils.click
import com.sweetcam.app.utils.getResourceByFolder
import com.sweetcam.app.utils.loadWith
import com.sweetcam.app.view.paster.DrawablePaster
import kotlinx.android.synthetic.main.activity_sticker.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class StickerActivity : BaseActivity(R.layout.activity_sticker), IDialogCallBack {

    override fun onConvert() {
        val url = intent.getStringExtra("url")

        url?.let {
            show_edit_iv.loadWith(it)
            recycler.layoutManager = GridLayoutManager(this, 4)
            recycler.adapter = StickerAdapter(getResourceByFolder(R.mipmap::class.java, "mipmap", "sticker")).apply {
                setOnItemClickListener {
                    val drawable = ContextCompat.getDrawable(this@StickerActivity, it.id)
                    drawable?.let {
                        sticker_view.addSticker(DrawablePaster(it))
                    }
                }
            }
            cancel.click {
                finish()
            }
            save.click {
                ContentDialog.newInstance(
                    "Saving", rightVisible = false
                ).show(supportFragmentManager, "")

                lifecycleScope.launch(Dispatchers.IO) {
                    val file =
                        File(Environment.getExternalStorageDirectory().absolutePath + File.separator + System.currentTimeMillis() + "_sticker.jpg")
                    sticker_view.save(file)
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