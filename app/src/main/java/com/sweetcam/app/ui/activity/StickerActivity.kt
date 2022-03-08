package com.sweetcam.app.ui.activity

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.jessewu.library.SuperAdapter
import com.jessewu.library.view.ViewHolder
import com.lcw.library.stickerview.Sticker
import com.shehuan.niv.NiceImageView
import com.squareup.picasso.Picasso
import com.sweetcam.app.R
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.ResourceUtils
import kotlinx.android.synthetic.main.activity_sticker.*

class StickerActivity : BaseActivity(R.layout.activity_sticker) {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onConvert() {
        val url = intent.getStringExtra("url") as String
        Picasso.get().load(url).into(stickerMainIv)
        val map = ResourceUtils.initStickersData()
        val data = ArrayList<String>()
        map.forEach { (_, v) ->
            data.add(ResourceUtils.res2String(this, v))
        }
        val adapter: SuperAdapter<String> = object : SuperAdapter<String>(R.layout.item_stickers) {
            override fun bindView(p0: ViewHolder?, p1: String?, p2: Int) {
                val iv = p0?.getView<NiceImageView>(R.id.itemSticker)
                Picasso.get().load(p1).into(iv)
            }
        }
        adapter.setData(data)
        recycler.layoutManager = GridLayoutManager(this, 3)
        recycler.adapter = adapter
        adapter.setOnItemClickListener { _, s ->
            val bitmap: Bitmap? = ResourceUtils.convertStringToIcon(s)
            bitmap?.let {
                sticker.addSticker(Sticker(this, it))
            }
        }
    }
}