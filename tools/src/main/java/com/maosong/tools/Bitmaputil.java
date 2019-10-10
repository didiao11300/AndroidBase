package com.maosong.tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;

import static com.maosong.tools.Constants.photoCompressDirPath;

/**
 * Created by tory on 2018/7/1.
 */

public class Bitmaputil {

    /**
     * 简单的压缩图片
     * 内存估计减小为原来的1/8
     */
    public static Bitmap compressBitmap(String path) {
        if (!TextUtils.isEmpty(path)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFile(path, options);
        }
        return null;
    }

    public static Bitmap compressBitmap(Resources res, int resId) {
        if (resId > 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeResource(res, resId, options);
        }
        return null;
    }

    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }

    public static String saveBitmap(Bitmap bitmap, String name) {
        if (null == bitmap) {
            LogUtil.e("tory", "saveBitmap()...bitmap is null");
            return "";
        }
        try {
            File dirs = new File(photoCompressDirPath);
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            File file = new File(photoCompressDirPath, name);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
            //保存图片后发送广播通知更新数据库
//            Uri uri = Uri.fromFile(file);
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showLongToast("File Not Find,Please Check File Path!");
        }
        return "";
     }
}
