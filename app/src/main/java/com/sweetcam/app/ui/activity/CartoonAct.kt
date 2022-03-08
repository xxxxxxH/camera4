package com.sweetcam.app.ui.activity

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.sweetcam.app.Constant
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.MessageEvent
import com.sweetcam.app.utils.loadWith
import com.sweetcam.app.view.dialog.LoadingDialog
import com.sweetcam.app.view.dialog.SaveDialog
import kotlinx.android.synthetic.main.activity_cartoon.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartoonAct : BaseActivity(R.layout.activity_cartoon) {

    private val saveDialog by lazy {
        SaveDialog(this)
    }

    private val loadingDialog by lazy {
        LoadingDialog(this)
    }

    override fun onConvert() {
        EventBus.getDefault().register(this)
        cartoonMainIv.loadWith(Constant.imageUrl)
        createItem()
        cancel.setOnClickListener { finish() }
        save.setOnClickListener {
            saveDialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        when (msg[0]) {
            "save" -> {
                saveDialog.dismiss()
                loadingDialog.show()
            }
        }
    }

    private fun createItem() {
        val img1 = ImageView(this)
        val img1LayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        img1LayoutParams.weight = 1f
        img1LayoutParams.marginStart = 8
        img1LayoutParams.marginEnd = 8
        img1.layoutParams = img1LayoutParams
        img1.setBackgroundResource(R.mipmap.cartoon_01)
        option.addView(img1)

        val img2 = ImageView(this)
        val img2LayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        img2LayoutParams.weight = 1f
        img2LayoutParams.marginStart = 8
        img2LayoutParams.marginEnd = 8
        img2.layoutParams = img2LayoutParams
        img2.setBackgroundResource(R.mipmap.cartoon_11)
        option.addView(img2)

        val img3 = ImageView(this)
        val img3LayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        img3LayoutParams.weight = 1f
        img3LayoutParams.marginStart = 8
        img3LayoutParams.marginEnd = 8
        img3.layoutParams = img3LayoutParams
        img3.setBackgroundResource(R.mipmap.cartoon_21)
        option.addView(img3)

        val img4 = ImageView(this)
        val img4LayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        img4LayoutParams.weight = 1f
        img4LayoutParams.marginStart = 8
        img4LayoutParams.marginEnd = 8
        img4.layoutParams = img4LayoutParams
        img4.setBackgroundResource(R.mipmap.cartoon_31)
        option.addView(img4)
    }
}