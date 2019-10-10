package com.maosong.mediapicker.adapter

import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.maosong.mediapicker.R


class TakePhotoProvider : BaseItemProvider<MultiItemEntity, BaseViewHolder>() {
    override fun convert(helper: BaseViewHolder, data: MultiItemEntity?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun layout(): Int {
        return R.layout.layout_take_photo_item
    }

    override fun viewType(): Int {
        return MediaAdapter.ITEM_TAKE_PHOTO
    }
}