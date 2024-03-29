package com.maosong.mediapicker.scanner

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.maosong.mediapicker.bean.Album
import com.maosong.mediapicker.bean.MediaItem
import java.io.File


/**
 *create by colin 2018/9/14
 *
 * 图片扫描器
 */

//查询的uri
private val EXTERNAL_IMAGES_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//查询的字段
private val ALBUM_PROJECTION = arrayOf(
    MediaStore.Images.Media.BUCKET_ID, //相册ID
    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,//相册名字
    MediaStore.Images.Media._ID,//图片ID
    MediaStore.Images.Media.DATA
)//图片地址

//各自列的序号
private const val BUCKET_ID = 0
private const val BUCKET_DISPLAY_NAME = 1
private const val IMAGE_ID = 2
private const val DATA = 3

//大小阈值
private const val ALBUM_SELECTION = "_size> 20000"

fun Context.jumpActivity(clazz: Class<*>, args: Bundle? = null) {
    val intent = Intent(this, clazz)
    args?.let {
        intent.putExtras(it)
    }
    startActivity(intent)
}


class ImageScanner(private var contextWrapper: ContextWrapper, var packageName: String) {

    fun getImageAlbum(): LiveData<MutableList<Album>> {
        val liveData = MutableLiveData<MutableList<Album>>()
        Thread {
            val allAlbumData = Album("ALL")
            val hashSet: HashMap<String, Album> = HashMap()
            hashSet.put("ALL", allAlbumData)
            val cursor = contextWrapper.contentResolver.query(
                EXTERNAL_IMAGES_URI,
                ALBUM_PROJECTION,
                ALBUM_SELECTION, null, null
            )
            try {
                while (null != cursor && cursor.moveToNext()) {
                    val displayName = cursor.getString(BUCKET_DISPLAY_NAME)
                    val localPath = cursor.getString(DATA)
                    val uri = "file://" + cursor.getString(DATA)
                    val mediaItem = MediaItem(displayName, MediaItem.ITEM_IMAGE, localPath, uri)
                    allAlbumData.datas.add(mediaItem)
                    val bucketId = cursor.getString(BUCKET_ID)
                    if (hashSet.containsKey(bucketId)) {
                        //同一个相册
                        val album = hashSet.get(bucketId)
                        album?.datas?.add(mediaItem)
                    } else {
                        //新的相册
                        val album = Album(displayName)
                        album?.datas?.add(mediaItem)
                        hashSet.put(bucketId, album)
                    }
                }
            } finally {
                cursor?.close()
                val allAlbumList = mutableListOf<Album>()
                allAlbumList.addAll(hashSet.values)
                liveData.postValue(allAlbumList)
            }
        }.start()
        return liveData
    }

    fun getCaptureIntent(outUri: Uri): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return intent
    }


    fun getCropIntent(imgUri: Uri, outUri: Uri): Intent {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(imgUri, "image/*")
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true")
        //该参数可以不设定用来规定裁剪区的宽高比
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        //该参数设定为你的imageView的大小
//        intent.putExtra("outputX", 800)
//        intent.putExtra("outputY", 800)
        intent.putExtra("scale", false)
        //是否返回bitmap对象
        intent.putExtra("return-data", false)
        //        intent.setData(outUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())//输出图片的格式
        intent.putExtra("noFaceDetection", true) // 头像识别
        return intent
    }

    /*fun generateCameraFilePath(): String {
        val cacheFile = File(contextWrapper.cacheDir, "JPEG_CPicker_cameraPhoto.jpg")
        Log.e("twp","cameraFileAbsolutePath = ${cacheFile.absolutePath}")
        Log.e("twp","cameraFilePath = ${cacheFile.path}")
        return cacheFile.absolutePath
    }*/

    fun generateCameraFile(): File? {
        // 路径： /data/user/0/packageName/cache/fileName
        return File(contextWrapper.externalCacheDir, "JPEG_CPicker_cameraPhoto.jpg")
    }

    fun generateCropFilePath(): String {
        val cf = File(contextWrapper.externalCacheDir, "JPEG_CPicker_cameraPhoto_crop.jpg")
        return "file:///${cf.absolutePath}"
    }

    fun getCropFilePath(): String {
        val cf = File(contextWrapper.externalCacheDir, "JPEG_CPicker_cameraPhoto_crop.jpg")
        return cf.absolutePath
    }

    fun getUriFromFile(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            FileProvider.getUriForFile(contextWrapper, "$packageName.colin.picklib.FileProvider", file)
        else
            Uri.fromFile(file)
    }

}