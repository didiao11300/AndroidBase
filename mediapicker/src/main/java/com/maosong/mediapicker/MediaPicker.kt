package com.maosong.mediapicker

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.maosong.mediapicker.activity.MediaPickerAct
import com.maosong.mediapicker.bean.MediaItem
import com.maosong.mediapicker.bean.PickParameters

/**
 *create by colin 2018/9/14
 * 媒体资料选择器
 */
class MediaPicker private constructor(private var parameters: PickParameters) {
    companion object {
        val KEY_RESULT_PATHS = MediaPickerAct.KEY_RESULT_PATHS
    }

    fun start() {
        if (parameters.activity == null && parameters.fragment == null)
            return
        val intent = if (parameters.activity != null) {
            Intent(parameters.activity, MediaPickerAct::class.java)
        } else {
            Intent(parameters.fragment?.activity, MediaPickerAct::class.java)
        }
        intent.putExtra(MediaPickerAct.KEY_PARAMETER, parameters)
        when {
            parameters.activity != null -> parameters.activity?.startActivityForResult(intent, parameters.requestCode)
            parameters.fragment != null -> parameters.fragment?.startActivityForResult(intent, parameters.requestCode)
            else -> {
                throw NullPointerException("没有启动项")
            }
        }
    }


    class Builder {
        private var mParameter: PickParameters = PickParameters(0)

        fun with(activity: Activity): Builder {
            mParameter.activity = activity
            return this
        }

        fun with(fragment: Fragment): Builder {
            mParameter.fragment = fragment
            return this
        }

        fun requestCode(requestCode: Int): Builder {
            mParameter.requestCode = requestCode
            return this
        }

        fun maxCount(maxCount: Int): Builder {
            mParameter.maxCount = maxCount
            return this
        }

        fun needCrop(needCrop: Boolean): Builder {
            mParameter.needCrop = needCrop
            return this
        }

        fun isSingleModel(isSingle: Boolean): Builder {
            mParameter.singleModel = isSingle
            return this
        }

        fun needLarge(needLarge: Boolean): Builder {
            mParameter.needLarge = needLarge
            return this
        }

        fun chooseVideo(): Builder {
            mParameter.type = MediaItem.ITEM_VIDEO
            return this
        }

        fun build(): MediaPicker {
            if (mParameter.activity == null && mParameter.fragment == null) {
                throw NullPointerException("没有启动项，请先调用with方法")
            }
            if (mParameter.requestCode == 0) {
                throw NullPointerException("请调用requestCode方法指定code")
            }
            return MediaPicker(mParameter)
        }
    }
}