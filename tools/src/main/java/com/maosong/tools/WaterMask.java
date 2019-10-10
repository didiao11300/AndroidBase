package com.maosong.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

/**
 * create by colin on 2019-04-22
 * 水印相关功能
 */
public class WaterMask {
    /**
     * 往图片里面添加水印
     * 默认底部和右边都是20的距离
     */
    public static Bitmap WaterMask(Bitmap src, Bitmap watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Log.i("WaterMask", "原图宽: " + w);
        Log.i("WaterMask", "原图高: " + h);
        // 设置原图想要的大小
        float newWidth = QMUIDisplayHelper.getScreenWidth(ToolsApp.getAppContext());
        float newHeight = h * (newWidth / w);
        // 计算缩放比例
        float scaleWidth = (newWidth) / w;
        float scaleHeight = (newHeight) / h;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        src = Bitmap.createBitmap(src, 0, 0, w, h, matrix, true);

        //根据bitmap缩放水印图片
        float w1 = w / 5;
        float h1 = (float) (w1 / 5);
        //获取原始水印图片的宽、高
        int w2 = watermark.getWidth();
        int h2 = watermark.getHeight();

        //计算缩放的比例
        float scalewidth = ((float) w1) / w2;
        float scaleheight = ((float) h1) / h2;

        Matrix matrix1 = new Matrix();
        matrix1.postScale((float) 0.4, (float) 0.4);

        watermark = Bitmap.createBitmap(watermark, 0, 0, w2, h2, matrix1, true);
        //获取新的水印图片的宽、高
        w2 = watermark.getWidth();
        h2 = watermark.getHeight();

        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(result);
        //在canvas上绘制原图和新的水印图
        cv.drawBitmap(src, 0, 0, null);
        //水印图绘制在画布的右下角，距离右边和底部都为20
        cv.drawBitmap(watermark, src.getWidth() - w2 - 20, src.getHeight() - h2 - 20, null);
//        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.save();
        cv.restore();

        return result;
    }
}
