package com.sweetcam.app.ui.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.huantansheng.easyphotos.EasyPhotos
import com.jessewu.library.SuperAdapter
import com.jessewu.library.view.ViewHolder
import com.lcw.library.stickerview.Sticker
import com.shehuan.niv.NiceImageView
import com.sweetcam.app.Constant
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.MessageEvent
import com.sweetcam.app.utils.ResourceUtils
import com.sweetcam.app.utils.loadWith
import com.sweetcam.app.utils.saveBitmap
import com.sweetcam.app.view.dialog.LoadingDialog
import com.sweetcam.app.view.dialog.SaveDialog
import kotlinx.android.synthetic.main.activity_sticker.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StickerActivity : BaseActivity(R.layout.activity_sticker) {

    var b: Bitmap? = null

    val url by lazy {
        intent.getStringExtra("url") as String
    }

    private val saveDialog by lazy {
        SaveDialog(this)
    }

    private val loadingDialog by lazy {
        LoadingDialog(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onConvert() {
        EventBus.getDefault().register(this)
        stickerMainIv.loadWith(Constant.imageUrl)
        val map = ResourceUtils.initStickersData()
        val data = ArrayList<Bitmap>()
        map.forEach { (_, v) ->
            data.add(BitmapFactory.decodeResource(resources, v))
        }
        val adapter: SuperAdapter<Bitmap> = object : SuperAdapter<Bitmap>(R.layout.item_stickers) {
            override fun bindView(p0: ViewHolder?, p1: Bitmap?, p2: Int) {
                val iv = p0?.getView<ImageView>(R.id.itemSticker)
                iv!!.layoutParams = iv.layoutParams.apply {
                    height = ResourceUtils.getScreenSize(this@StickerActivity)[1] / 4
                }
                val root = p0.getView<RelativeLayout>(R.id.root)
                root.layoutParams = iv.layoutParams.apply {
                    height = ResourceUtils.getScreenSize(this@StickerActivity)[1] / 4
                }
//                iv.setImageBitmap(p1)
                iv.loadWith(p1!!)
            }
        }
        adapter.setData(data)
        recycler.layoutManager = GridLayoutManager(this, 4)
        recycler.adapter = adapter
        adapter.setOnItemClickListener(object : SuperAdapter.OnItemClickListener<Bitmap> {
            override fun onClick(p0: Int, p1: Bitmap?) {
                p1?.let {
                    sticker.addSticker(Sticker(this@StickerActivity, it))
                }
            }

        })
        cancel.setOnClickListener {
            finish()
        }
        save.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main){
                    progress.visibility = View.VISIBLE
                }
                withContext(Dispatchers.IO) {
                    ResourceUtils.createBitmapFromView(main)
                }
                withContext(Dispatchers.Main) {
                    progress.visibility = View.GONE
                    saveDialog.show()
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
            "save" -> {
                saveDialog.dismiss()
                loadingDialog.show()
            }
        }
    }
}