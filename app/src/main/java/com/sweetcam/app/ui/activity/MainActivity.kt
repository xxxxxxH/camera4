package com.sweetcam.app.ui.activity

import android.content.Intent
import android.view.View
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.sweetcam.app.Constant
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.GlideEngine
import com.sweetcam.app.utils.MessageEvent
import com.sweetcam.app.utils.requestPermission
import com.sweetcam.app.view.dialog.ExitDialog
import kotlinx.android.synthetic.main.layout_main_bottom.*
import kotlinx.android.synthetic.main.layout_main_center.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseActivity(R.layout.act_main), View.OnClickListener {

    val exitDialog by lazy {
        ExitDialog(this)
    }

    override fun onConvert() {
        EventBus.getDefault().register(this)
        requestPermission {
            sticker.setOnClickListener(this)
            slimming.setOnClickListener(this)
            cartoon.setOnClickListener(this)
            age.setOnClickListener(this)
            mainCamera.setOnClickListener(this)
        }
    }

    override fun onBackPressed() {
        exitDialog.setCancelable(false)
        exitDialog.show()
    }


    private fun selectImageWithAlbum(clazz: Class<*>) {
        EasyPhotos.createAlbum(this, false, true, GlideEngine.get())
            .start(object : SelectCallback() {
                override fun onResult(photos: ArrayList<Photo>?, isOriginal: Boolean) {
                    photos?.let {
                        Constant.imageUrl = ""
                        Constant.imageUrl = it[0].path
                        startActivity(Intent(this@MainActivity, clazz))
                    }
                }

                override fun onCancel() {

                }

            })
    }

    private fun takePhoto() {
        EasyPhotos.createCamera(this, false)
            .setFileProviderAuthority("org.snbeau.apcam.provider")
            .start(object : SelectCallback() {
                override fun onResult(photos: ArrayList<Photo>?, isOriginal: Boolean) {
                }

                override fun onCancel() {
                }

            })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.sticker -> {
                if (!showInsertAd(tag = "inter_filter")) {
                    selectImageWithAlbum(StickerActivity::class.java)
                }
            }
            R.id.slimming -> {
                if (!showInsertAd(tag = "inter_slim")) {
                    selectImageWithAlbum(SlimmingAct::class.java)
                }
            }
            R.id.cartoon -> {
                if (!showInsertAd()) {
                    selectImageWithAlbum(CartoonAct::class.java)
                }
            }
            R.id.age -> {
                if (!showInsertAd()) {
                    selectImageWithAlbum(AgeAct::class.java)
                }
            }
            R.id.mainCamera -> {
                if (!showInsertAd(tag = "inter_camera")) {
                    takePhoto()
                }
            }
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
            "confirmExit" -> {
                finish()
            }
            "cancelExit" -> {
                exitDialog.dismiss()
            }
        }
    }
}