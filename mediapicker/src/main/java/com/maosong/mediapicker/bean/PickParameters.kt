package com.maosong.mediapicker.bean

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.Fragment

/**
 *create by colin on 2019/4/1
 */
data class PickParameters(var id: Int) : Parcelable {
    var activity: Activity? = null
    var fragment: Fragment? = null
    var requestCode = -1
    //        var colorRes: Int = -1
    var maxCount: Int = 9
    var needCrop: Boolean = false
    var singleModel: Boolean = false
    var needLarge: Boolean = true
    var type = MediaItem.ITEM_IMAGE

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        requestCode = parcel.readInt()
        maxCount = parcel.readInt()
        needCrop = parcel.readByte() != 0.toByte()
        singleModel = parcel.readByte() != 0.toByte()
        needLarge = parcel.readByte() != 0.toByte()
        type = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(requestCode)
        parcel.writeInt(maxCount)
        parcel.writeByte(if (needCrop) 1 else 0)
        parcel.writeByte(if (singleModel) 1 else 0)
        parcel.writeByte(if (needLarge) 1 else 0)
        parcel.writeInt(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PickParameters> {
        override fun createFromParcel(parcel: Parcel): PickParameters {
            return PickParameters(parcel)
        }

        override fun newArray(size: Int): Array<PickParameters?> {
            return arrayOfNulls(size)
        }
    }
}