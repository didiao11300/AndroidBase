package com.maosong.mediapicker.adapter

import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.MultipleItemRvAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.colin.picklib.TakeCameraProvider
import com.colin.picklib.VideoProvider
import com.maosong.mediapicker.bean.Album
import com.maosong.mediapicker.bean.MediaItem

class MediaAdapter(data: MutableList<MultiItemEntity>) :
    MultipleItemRvAdapter<MultiItemEntity, BaseViewHolder>(data) {
    companion object {
        const val ITEM_IMAGE = MediaItem.ITEM_IMAGE
        const val ITEM_VIDEO = MediaItem.ITEM_VIDEO
        const val ITEM_FILE = MediaItem.ITEM_FILE
        const val ITEM_TAKE_PHOTO = MediaItem.ITEM_FILE + 1
        const val ITEM_TAKE_CAMERA = MediaItem.ITEM_FILE + 2
    }

    //图片类型是 0，加号类型是1
    private var mAllAlbum: Album? = null

    init {
        finishInitialize()
    }

    override fun registerItemProvider() {
        mProviderDelegate.registerProvider(TakePhotoProvider())
        mProviderDelegate.registerProvider(ImageProvider())
        mProviderDelegate.registerProvider(TakeCameraProvider())
        mProviderDelegate.registerProvider(VideoProvider())
    }

    override fun getViewType(t: MultiItemEntity): Int {
        return t.getItemType()
    }

    override fun replaceData(data: MutableCollection<out MultiItemEntity>) {
        // 不是同一个引用才清空列表
        if (data !== mData) {
            mData.removeAll {
                (it.getItemType() == ITEM_VIDEO || it.itemType == ITEM_IMAGE)
            }
            mData.addAll(data)
        }
        notifyDataSetChanged()
    }

    fun setAllAlbum(album: Album) {
        this.mAllAlbum = album
    }

    fun checkedInAll(item: MediaItem) {
        val index = mAllAlbum?.datas?.indexOf(item)
        if (index != null && index != -1) {
            mAllAlbum?.datas?.get(index)?.isChecked = item.isChecked
        }
    }

    fun clear() {
        mAllAlbum?.datas?.forEach {
            it.isChecked = false
        }
        notifyDataSetChanged()
    }

    fun getCheckedCount(): Int {
        return mAllAlbum?.datas?.count {
            it.isChecked
        } ?: 0
    }

    fun getCheckedMedia(): ArrayList<MediaItem> {
        val result: ArrayList<MediaItem> = arrayListOf()
        mAllAlbum?.datas?.forEach {
            if (it.isChecked)
                result.add(it)
        }
        return result
    }
}