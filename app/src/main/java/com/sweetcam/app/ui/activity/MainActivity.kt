package com.sweetcam.app.ui.activity

import android.view.View
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.callback.IDialogCallBack
import com.sweetcam.app.ui.dialog.ContentDialog
import com.sweetcam.app.utils.GlideEngine
import java.util.ArrayList

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
                EasyPhotos.createAlbum(this,false,true,GlideEngine.get())
                    .start(object :SelectCallback(){
                        override fun onResult(photos: ArrayList<Photo>?, isOriginal: Boolean) {

                        }

                        override fun onCancel() {

                        }

                    })
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