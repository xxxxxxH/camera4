package com.sweetcam.app.adapter

import com.sweetcam.app.R
import com.sweetcam.app.pojo.ResourcePojo
import com.sweetcam.app.base.BaseAdapter
import com.sweetcam.app.utils.loadWith
import kotlinx.android.synthetic.main.item_stickers.*

class StickerAdapter(data: MutableList<ResourcePojo>) :
    BaseAdapter<ResourcePojo>(data) {

    override val layoutId: Int
        get() = R.layout.item_stickers

    override fun onConvert(holder: BaseViewHolder, item: ResourcePojo, position: Int) {
        holder.item_sticker_iv.loadWith(item.id)
    }
}