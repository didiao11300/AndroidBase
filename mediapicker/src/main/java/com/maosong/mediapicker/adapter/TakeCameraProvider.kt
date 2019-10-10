package com.colin.picklib

import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.maosong.mediapicker.adapter.MediaAdapter
import com.maosong.mediapicker.R

/**
 * 相机录制视频
 * */
class TakeCameraProvider : BaseItemProvider<MultiItemEntity, BaseViewHolder>() {
    override fun viewType(): Int {
       return MediaAdapter.ITEM_TAKE_CAMERA
    }

    override fun layout(): Int {
        return R.layout.layout_take_photo_item
    }

    override fun convert(helper: BaseViewHolder, data: MultiItemEntity?, position: Int) {

    }
}