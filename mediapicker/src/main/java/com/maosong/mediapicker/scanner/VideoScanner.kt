package com.maosong.mediapicker.scanner

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.maosong.mediapicker.bean.MediaItem
import com.maosong.mediapicker.bean.Album
import java.io.File


/**
 *create by colin on 2019/3/30
 *视频选择器
 */
class VideoScanner(private var context: Context) {
    /**
     * 获取手机中所有视频的信息
     */
    public fun getAllVideoInfos(): LiveData<MutableList<Album>> {
        val liveData = MutableLiveData<MutableList<Album>>()
        Thread(Runnable {
            val allAlbumData = Album("ALL")//所有资料
            val hashSet = HashMap<String, Album>()
            hashSet.put("ALL", allAlbumData)

            val mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val proj = arrayOf(
                MediaStore.Video.Thumbnails._ID,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED
            )
            val cursor = context.contentResolver.query(
                mediaUri,
                proj,
                MediaStore.Video.Media.MIME_TYPE + "=?",
                arrayOf("video/mp4"),
                MediaStore.Video.Media.DATE_MODIFIED + " desc"
            )
            try {
                while (cursor != null && cursor.moveToNext()) {
                    // 获取视频的路径
                    val videoId = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val localPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)) //单位ms
                    var size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024 //单位kb
                    if (size < 0) {
                        //某些设备获取size<0，直接计算
                        Log.e("dml", "this video size < 0 $localPath")
                        size = File(localPath).length() / 1024
                    }
                    if (duration > 30 * 1000) {
                        Log.e("VideoScanner", "skip " + localPath + " because video duration is less than 30s")
                        continue
                    }
                    val displayName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                    val modifyTime =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))//暂未用到

                    //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                    MediaStore.Video.Thumbnails.getThumbnail(
                        context.contentResolver,
                        videoId.toLong(),
                        MediaStore.Video.Thumbnails.MICRO_KIND,
                        null
                    )
                    val projection = arrayOf(MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA)
                    val cursorThumb = context.contentResolver.query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
                        arrayOf<String>((videoId.toString())),
                        null
                    )
                    var thumbPath = ""
                    while (null != cursorThumb && cursorThumb.moveToNext()) {
                        thumbPath =
                            cursorThumb.getString(cursorThumb.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
                    }
                    cursorThumb?.close()
                    val mediaItem = MediaItem(
                        displayName,
                        MediaItem.ITEM_VIDEO,
                        localPath
                    )
                    mediaItem.duration = duration
                    mediaItem.lastModifyTime = modifyTime
                    mediaItem.thumbPath = thumbPath
                    mediaItem.size = size
                    allAlbumData.datas.add(mediaItem)
                    val bucketId = File(localPath).parentFile.name
                    //存储对应关系
                    if (hashSet.containsKey(bucketId)) {
                        //添加到已有的目录
                        val album = hashSet.get(bucketId)
                        album?.datas?.add(mediaItem)
                        continue
                    } else {
                        val album = Album(bucketId)
                        album.datas.add(mediaItem)
                        hashSet.put(bucketId, album)
                    }
                }
            } finally {
                cursor?.close()
                val allAlbumList = mutableListOf<Album>()
                allAlbumList.addAll(hashSet.values)
                liveData.postValue(allAlbumList)
            }
        }).start()
        return liveData
    }
}