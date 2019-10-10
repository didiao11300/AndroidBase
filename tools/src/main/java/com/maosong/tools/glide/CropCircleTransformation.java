package com.maosong.tools.glide;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * 圆形
 * */
public class CropCircleTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID =
            "com.maosong.tools.circle." + VERSION;

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool,
                               @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight);
    }

    @Override
    public String toString() {
        return "CropCircleTransformation()";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CropCircleTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID).getBytes(CHARSET));
    }
}
