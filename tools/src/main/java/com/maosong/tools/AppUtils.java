package com.maosong.tools;

import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * app功能和运行状态的类
 */
public class AppUtils {
    private static DateFormat TRULY_FORMAT = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
    /**
     * 网络类型
     **/
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static Intent getCaptureIntent(final Uri outUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    public static Intent getCropIntent(Uri imgUri, Uri outUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imgUri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        //该参数可以不设定用来规定裁剪区的宽高比
//        intent.putExtra("aspectX", 2);
//        intent.putExtra("aspectY", 1);
        //该参数设定为你的imageView的大小
//        intent.putExtra("outputX", 600);
//        intent.putExtra("outputY", 500);
        intent.putExtra("scale", true);
        //是否返回bitmap对象
        intent.putExtra("return-data", false);
//        intent.setData(outUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片的格式
        intent.putExtra("noFaceDetection", true); // 头像识别
        return intent;
    }

    public static Uri getUriFromFile(Context context, File file) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file) :
                Uri.fromFile(file);
    }

    public static Intent getOpenAlbumIntent() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("image/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        return intent;
    }

    public static Intent getOpenMultipleImageFileIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        return intent;
    }

    public static String millis2String(final long millis) {
        return TRULY_FORMAT.format(new Date(millis));
    }


    private static long lastClickTime = 0;//上次点击的时间

    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();//当前系统时间
        boolean isAllowClick;//是否允许点击
        int spaceTime = 1000;
        isAllowClick = currentTime - lastClickTime <= spaceTime;
        lastClickTime = currentTime;
        return isAllowClick;
    }

    /**
     * Return the blur bitmap using stack.
     *
     * @param src    The source of bitmap.
     * @param radius The radius(0...25).
     * @return the blur bitmap
     */
    public static Bitmap stackBlur(final Bitmap src, final int radius) {
        return stackBlur(src, radius, false);
    }


    /**
     * Return the blur bitmap using stack.
     *
     * @param src     The source of bitmap.
     * @param radius  The radius(0...25).
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the blur bitmap
     */
    public static Bitmap stackBlur(final Bitmap src, int radius, final boolean recycle) {
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        if (radius < 1) {
            radius = 1;
        }
        int w = ret.getWidth();
        int h = ret.getHeight();

        int[] pix = new int[w * h];
        ret.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        ret.setPixels(pix, 0, w, 0, 0, w, h);
        return ret;
    }

    public static List<ContactsBean> getContacts(ContentResolver resolverCompat) throws NullPointerException {
        List<ContactsBean> data = new ArrayList<>();
        //联系人的Uri，也就是content://com.android.contacts/contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //指定获取_id和display_name两列数据，display_name即为姓名
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
        Cursor cursor = resolverCompat.query(uri, projection, null, null, null);
        ContactsBean cb;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(0);
                //获取姓名
                String name = cursor.getString(1);
                //指定获取NUMBER这一列数据
                String[] phoneProjection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
                String filterName = EmojiUtils.filterEmoji(name);
                cb = new ContactsBean();
                cb.name = filterName;
                //根据联系人的ID获取此人的电话号码
                Cursor phonesCusor = resolverCompat.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        phoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                        null,
                        null);

                //因为每个联系人可能有多个电话号码，所以需要遍历
                if (phonesCusor != null && phonesCusor.moveToFirst()) {
                    List<String> phoneList = new ArrayList<>();
                    do {
                        String num = phonesCusor.getString(0).replaceAll(" ", "");
                        String filterNum = EmojiUtils.filterEmoji(num);
                        phoneList.add(filterNum);
                    } while (phonesCusor.moveToNext());
                    if (phoneList.size() > 0) {
                        cb.phoneNumber = phoneList.get(0);
                    }
                    phonesCusor.close();
                }
                if (TextUtils.isEmpty(cb.name) || TextUtils.isEmpty(cb.phoneNumber))
                    continue;
                data.add(cb);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return data;
    }

    public static String getDisplayNameByPhone(Context context, String phoneNum) {
        //uri=  content://com.android.contacts/data/phones/filter/#
        Uri uri = Uri.parse(ContactsContract.CommonDataKinds.Phone.CONTENT_URI + "/filter/" + phoneNum);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data.DISPLAY_NAME}, null, null, null); //从raw_contact表中返回display_name
        if (null != cursor && cursor.moveToFirst()) {
            LogUtil.i("Contacts", "name=" + cursor.getString(0));
            int index = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(index);
            return name;
        }
        return null;
    }

    /**
     * 根据手机号码查询联系人姓名
     *
     * @param context  上下文
     * @param phoneNum (传入纯数字手机号码)
     * @return 姓名
     */
    public static String getDisplayNameByPhone1(Context context, String phoneNum) {
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        String displayName = "";
        String phone1 = String.valueOf(phoneNum.subSequence(0, 3)) + " " + phoneNum.substring(3, 7) +
                " " + phoneNum.substring(7, 11);
        String phone2 = String.valueOf(phoneNum.subSequence(0, 3)) + "-" + phoneNum.substring(3, 7) +
                "-" + phoneNum.substring(7, 11);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.NUMBER + " in(?,?,?)", new String[]{
                phoneNum, phone1, phone2}, null);

        if (cursor != null) {

            while (cursor.moveToNext()) {
                displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (!TextUtils.isEmpty(displayName)) {
                    break;
                }
                cursor.close();
            }
        }
        return displayName;
    }

    public static boolean isMe(String uid) {
        String me_id = SPUtils.getInstance().getString(Constants.SP_USER_ID);
        return !TextUtils.isEmpty(uid) && !TextUtils.isEmpty(me_id) && TextUtils.equals(me_id, uid);
    }

    public static boolean emailIsRight(String email) {
        return !TextUtils.isEmpty(email) && email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

    private static String[] MONTH_EN = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",};

    public static String getMonthEn(int month) {
        if (month < 1 || month > 12) {
            return "非法月份";
        }
        return MONTH_EN[month - 1];
    }

    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }


    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType(Context context) {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase(Locale.getDefault()).equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 对屏幕截屏
     */
    public static File takeScreenshot(View view) {
        View dView = view;
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        File file = null;
        if (bitmap != null) {
            try {
                // 获取内置SD卡路径
                String fileDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "mm";
                if (!(new File(fileDir).exists())) {
                    new File(fileDir).mkdirs();
                }
                // 图片文件路径
                String fileName = "mm_share" + System.currentTimeMillis() + ".png";
                file = new File(fileDir, fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void exitApp(boolean clearAllData) {
        if (clearAllData) {
            ClearUtils.clearDatabases(ToolsApp.getAppContext());
            SPUtils.getInstance().clear();
        }
        System.exit(0);
    }
}
