package com.colin.picklib

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.maosong.mediapicker.adapter.MediaAdapter
import com.maosong.mediapicker.R
import com.maosong.mediapicker.bean.MediaItem

class VideoProvider : BaseItemProvider<MediaItem, BaseViewHolder>() {
    override fun layout(): Int {
        return R.layout.layout_media_item
    }

    override fun viewType(): Int {
        return MediaAdapter.ITEM_VIDEO
    }

    override fun convert(helper: BaseViewHolder, item: MediaItem, position: Int) {
        val photo = helper.getView<ImageView>(R.id.iv_image)
        Glide.with(mContext)
            .load(item.thumbPath)
//            .thumbnail(0.25f)
            .apply(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
            .into(photo)
        helper.setChecked(R.id.cb_item_check, item.isChecked)
        helper.addOnClickListener(R.id.cb_item_check)
        helper.setVisible(R.id.iv_play, true)
    }
}