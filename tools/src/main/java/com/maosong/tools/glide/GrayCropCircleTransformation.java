package com.maosong.tools.glide;

import android.graphics.*;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * 灰色圆形
 * */
public class GrayCropCircleTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID =
            "com.maosong.tools.circlegray." + VERSION;

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool,
                               @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        Bitmap.Config config =
                toTransform.getConfig() != null ? toTransform.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = pool.get(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        ColorMatrix saturation = new ColorMatrix();
        saturation.setSaturation(0f);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(saturation));
        canvas.drawBitmap(toTransform, 0, 0, paint);

        toTransform = bitmap;
        return TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight);
    }

    @Override
    public String toString() {
        return "CropCircleTransformation()";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GrayCropCircleTransformation;
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
