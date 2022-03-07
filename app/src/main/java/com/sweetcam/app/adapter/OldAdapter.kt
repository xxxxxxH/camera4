package com.sweetcam.app.adapter

import com.sweetcam.app.base.BaseAdapter
import com.sweetcam.app.utils.loadWith
import com.sweetcam.app.R
import com.sweetcam.app.pojo.ResourcePojo
import kotlinx.android.synthetic.main.item_cartoon.*

class OldAdapter(data: MutableList<ResourcePojo>) :
    BaseAdapter<ResourcePojo>(data) {

    override val layoutId: Int
        get() = R.layout.item_cartoon

    override fun onConvert(holder: BaseViewHolder, item: ResourcePojo, position: Int) {
        holder.item_cartoon_name.text = item.name
        holder.item_cartoon_iv.loadWith(item.id)
    }
}