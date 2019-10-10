package com.maosong.mediapicker.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * create by colin on 2019/4/1
 * 单个资料
 */
public class MediaItem implements MultiItemEntity, Parcelable {
    public static final int ITEM_IMAGE = 1004;
    public static final int ITEM_VIDEO = 1005;
    public static final int ITEM_FILE = 1006;
    public String name = "";
    public int type = ITEM_FILE;
    //本地路径 /stroge/mnt/android/xxx.img
    public String localPath = "";
    //uri 地址 比如file:/// apk资源android_resource://packagename/resid 远程地址http://
    public String mainUri = "";
    // 视频或者图片的缩略图
    public String thumbPath = "";
    //如果type为视频 是封面图
    public String coverPath = "";
    //大小kb
    public Long size = 0l;
    //视频持续时间
    public Integer duration = 0;

    public Long lastModifyTime = 0L;
    public String extra;
    //for ui
    public boolean isChecked = false;


    public MediaItem() {

    }

    public MediaItem(int type) {
        this("", type);
    }

    public MediaItem(String name, int type) {
        this(name, type, "");
    }

    public MediaItem(String name, int type, String localPath) {
        this(name, type, localPath, "");
    }

    public MediaItem(String name, int type, String localPath, String uri) {
        this.name = name;
        this.type = type;
        this.localPath = localPath;
        this.mainUri = uri;
    }


    protected MediaItem(Parcel in) {
        name = in.readString();
        type = in.readInt();
        localPath = in.readString();
        mainUri = in.readString();
        thumbPath = in.readString();
        coverPath = in.readString();
        if (in.readByte() == 0) {
            size = null;
        } else {
            size = in.readLong();
        }
        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readInt();
        }
        if (in.readByte() == 0) {
            lastModifyTime = null;
        } else {
            lastModifyTime = in.readLong();
        }
        extra = in.readString();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel in) {
            return new MediaItem(in);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };

    //获取一个能用的路径,先从本地取，再从uri取
    public String getAvaliablePath() {
        if (TextUtils.isEmpty(localPath)) {
            return mainUri;
        } else {
            return localPath;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getItemType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(type);
        dest.writeString(localPath);
        dest.writeString(mainUri);
        dest.writeString(thumbPath);
        dest.writeString(coverPath);
        if (size == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(size);
        }
        if (duration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(duration);
        }
        if (lastModifyTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(lastModifyTime);
        }
        dest.writeString(extra);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}
