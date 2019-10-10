package com.maosong.mediapicker.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN
import com.maosong.mediapicker.scanner.VideoScanner
import com.maosong.mediapicker.GalleryItemDecoration
import com.maosong.mediapicker.R
import com.maosong.mediapicker.adapter.MediaAdapter
import com.maosong.mediapicker.bean.Album
import com.maosong.mediapicker.bean.MediaItem
import com.maosong.mediapicker.bean.PickParameters
import com.maosong.mediapicker.scanner.ImageScanner
import com.maosong.mediapicker.scanner.jumpActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_media_picker.*
import java.io.File
import java.lang.Exception


class MediaPickerAct : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    companion object {
        val KEY_RESULT_PATHS = "_key_paths"
        val KEY_PARAMETER = "_key_parameter"
    }

    private var mAdapter: MediaAdapter? = null
    private var spinnerAdapter: ArrayAdapter<Album>? = null
    private var mImageScanner: ImageScanner? = null
    private var mVideoScanner: VideoScanner? = null
    private var maxChecked = 9
    private var needCrop = false //是否需要裁剪
    private var singleModel = false
    private var needLarge = true
    private var permission: RxPermissions? = null
    private var type = MediaItem.ITEM_IMAGE
    private var mMediaScannerConnection: MediaScannerConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //1.创建MediaScannerConnection
        mMediaScannerConnection = MediaScannerConnection(this, null)
        //调用connect
        mMediaScannerConnection?.connect()

        setContentView(R.layout.activity_media_picker)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        val parameter = intent.getParcelableExtra(KEY_PARAMETER) as PickParameters
        maxChecked = parameter.maxCount
        needCrop = parameter.needCrop
        singleModel = parameter.singleModel
        needLarge = parameter.needLarge
        type = parameter.type
        initToolbar()
        if (type == MediaItem.ITEM_IMAGE) {
//            mMediaScannerConnection?.scanFile(Environment.getExternalStorageDirectory().absolutePath, "image/jpeg")
            mImageScanner = ImageScanner(this, this.packageName)
            initImageList()
        } else if (type == MediaItem.ITEM_VIDEO) {
//            mMediaScannerConnection?.scanFile(Environment.getExternalStorageDirectory().absolutePath, "video/mp4")
            mVideoScanner = VideoScanner(this)
            initVideoList()
        }

        permission = RxPermissions(this)
        val dispose = permission?.request(Manifest.permission.READ_EXTERNAL_STORAGE)
            ?.subscribe { hasPermission ->
                if (hasPermission) {
                    if (type == MediaItem.ITEM_IMAGE) {
                        scanImage()
                    } else if (type == MediaItem.ITEM_VIDEO) {
                        scanVideo()
                    }
                }
            }
        initEvent()
    }

    /**
     * 初始化toopbar
     * */
    private fun initToolbar() {
        setSupportActionBar(picker_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        picker_toolbar.setNavigationOnClickListener {
            finish()
        }
        picker_tv_count.text = getString(R.string.picker_done, 0, maxChecked)
    }

    /**
     * 初始化视频列表
     * */
    private fun initVideoList() {
        mAdapter = MediaAdapter(mutableListOf())
        mAdapter?.addData(MediaItem(MediaAdapter.ITEM_TAKE_CAMERA))
        mAdapter?.openLoadAnimation(ALPHAIN)
        val layoutManager = GridLayoutManager(this, 3)
        picker_list.layoutManager = layoutManager
        picker_list.adapter = mAdapter
        picker_list.addItemDecoration(GalleryItemDecoration(this, 2, 2, 2, 2))
    }

    /**
     * 初始化图片列表
     * */
    private fun initImageList() {
        mAdapter = MediaAdapter(mutableListOf())
        mAdapter?.addData(MediaItem(MediaAdapter.ITEM_TAKE_PHOTO))
        mAdapter?.openLoadAnimation(ALPHAIN)
        val layoutManager = GridLayoutManager(this, 3)
        picker_list.layoutManager = layoutManager
        picker_list.adapter = mAdapter
        picker_list.addItemDecoration(GalleryItemDecoration(this, 2, 2, 2, 2))
    }


    /**
     * 扫描图片
     * */
    private fun scanImage() {
        mImageScanner?.getImageAlbum()?.observe({ lifecycle }, {
            it?.let { albumList: MutableList<Album> ->
                mAdapter?.setAllAlbum(albumList[0])
                initSpinner(albumList)
            }
        })
    }

    /**
     * 扫描视频*/
    private fun scanVideo() {
        mVideoScanner?.getAllVideoInfos()
            ?.observe({ lifecycle }, {
                mAdapter?.setAllAlbum(it!![0])
                initSpinner(it!!)
            })

    }

    /**
     * 初始化右上角选择器
     * */
    private fun initSpinner(albumList: MutableList<Album>) {
        spinnerAdapter = ArrayAdapter(this, R.layout.layout_spinner_withe, albumList)
        spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        picker_spinner.adapter = spinnerAdapter
        picker_spinner.onItemSelectedListener = this
    }

    /**
     * 初始化点击事件
     * */
    private fun initEvent() {
        mAdapter?.setOnItemClickListener { _, view, position ->
            //跳转到大图页面查看大图
            val item = mAdapter!!.data[position]
            if (item.itemType == MediaAdapter.ITEM_IMAGE) {
                val mediaItem = item as MediaItem
                if (needLarge) {
                    val bundle = Bundle()
                    bundle.putString(BigImageActivity.KEY_PATH, mediaItem.avaliablePath)
                    jumpActivity(BigImageActivity::class.java, bundle)
                } else {
                    val ckb = view.findViewById<CheckBox>(R.id.cb_item_check)
                    handItemClick(ckb, position)
                }
            } else if (item.itemType == MediaAdapter.ITEM_TAKE_PHOTO) {
                //拍照，并接受拍照结果，并设置为 Image 类型回传给调用者
                permission?.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                    ?.subscribe {
                        if (it) takePhoto()
                    }
            } else if (item.itemType == MediaAdapter.ITEM_VIDEO) {
                //todo 可以判断播放功能;
                val ckb = view.findViewById<CheckBox>(R.id.cb_item_check)
                handItemClick(ckb, position)
            } else if (item.itemType == MediaAdapter.ITEM_TAKE_CAMERA) {
                permission?.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                    ?.subscribe {
                        if (it) takeCamera()
                    }
            }
        }
        mAdapter?.setOnItemChildClickListener { _, view, position ->
            handItemClick(view, position)
        }
    }

    private fun handItemClick(view: View, position: Int) {
        if (view.id == R.id.cb_item_check) {
            if (maxChecked == 0) {
                return
            }
            val item = mAdapter!!.data[position]
            if (item is MediaItem && item.itemType == MediaAdapter.ITEM_IMAGE) {
                if (singleModel) {//判断是否是单选模式
                    //单选状态下，选择一张就回传，如果需要裁剪
                    if (needCrop) {
                        handleCropIntent(mImageScanner!!.getUriFromFile(File(item.mainUri)))
                    } else {
                        setListResult(arrayListOf(item))
                    }
                } else {
                    val checked = mAdapter?.getCheckedCount() ?: 0
                    if (checked < maxChecked) {
                        item.isChecked = !item.isChecked
                        mAdapter?.notifyItemChanged(position)
                        mAdapter?.checkedInAll(item)
                        //改变按钮上的数字加
                        picker_tv_count.text = getString(
                            R.string.picker_done, mAdapter?.getCheckedCount()
                                ?: 0, maxChecked
                        )
                    } else {
                        val result = mAdapter?.getCheckedMedia()
                        if (result == null || result.isEmpty()) {
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                            return
                        }
                        setListResult(result)
                    }

                }
            } else if (item is MediaItem && item.itemType == MediaAdapter.ITEM_VIDEO) {
                if (singleModel) {
                    setListResult(arrayListOf(item))
                } else {
                    val checked = mAdapter?.getCheckedCount() ?: 0
                    if (checked < maxChecked) {
                        item.isChecked = !item.isChecked
                        mAdapter?.notifyItemChanged(position)
                        mAdapter?.checkedInAll(item)
                        //改变按钮上的数字加
                        picker_tv_count.text = getString(
                            R.string.picker_done, mAdapter?.getCheckedCount()
                                ?: 0, maxChecked
                        )
                    } else {
                        val result = mAdapter?.getCheckedMedia()
                        if (result == null || result.isEmpty()) {
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                            return
                        }
                        setListResult(result)
                    }
                }
            }
        }
    }

    /**
     * 拍摄视频
     * */
    private fun takeCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        // 录制视频最大时长15s
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 101)
        }
    }

    /**
     * 拍照功能
     * */
    private fun takePhoto() {
        val imageFile = mImageScanner?.generateCameraFile()
        if (imageFile == null) {
            Toast.makeText(this, "Please check if the storage space is sufficient", Toast.LENGTH_LONG).show()
            return
        }
        val uri = mImageScanner?.getUriFromFile(imageFile)
        val captureIntent = mImageScanner?.getCaptureIntent(uri ?: Uri.EMPTY)
        startActivityForResult(captureIntent, 100)
    }

    /**
     * 去裁剪
     * @param imageUri 图片uri
     */
    private fun handleCropIntent(imageUri: Uri) {
        val outUri = Uri.parse(mImageScanner?.generateCropFilePath())
        val cropIntent = mImageScanner?.getCropIntent(imageUri, outUri)
        startActivityForResult(cropIntent, 200)
    }

    /**
     * 拍照成功
     * @param path 最终图片路径
     */
    private fun takePhotoResultSuccess(path: String) {
        val photo = MediaItem()
        photo.isChecked = false
        photo.localPath = path
        photo.mainUri = "file:///$path"
        setListResult(arrayListOf(photo))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            //拍照成功
            /* val photoPath = mImageScanner.generateCameraFile()?.absolutePath
             if (TextUtils.isEmpty(photoPath)) {
                 Toast.makeText(this, "Please check if the storage space is sufficient", Toast.LENGTH_LONG).show()
                 return
             }*/
            if (needCrop) {//去裁剪
                val file = mImageScanner?.generateCameraFile() ?: return
                handleCropIntent(mImageScanner?.getUriFromFile(file) ?: Uri.EMPTY)
            } else {
                //不需要裁剪，直接返回
                takePhotoResultSuccess(mImageScanner?.generateCameraFile()?.absolutePath ?: "")
            }
        } else if (requestCode == 200) {
            //裁剪成功
            if (resultCode == Activity.RESULT_OK) {
                takePhotoResultSuccess(mImageScanner?.getCropFilePath() ?: "")
            } else {
                //清空选中状态
                mAdapter?.clear()
            }
        } else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            try {
                val uri = data?.getData()
                val cursor = this.getContentResolver().query(uri, null, null, null, null)
                if (cursor != null && cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID))
                    // 视频路径
                    val filePath = cursor?.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                    // ThumbnailUtils类2.2以上可用  Todo 获取视频缩略图
//                val bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MICRO_KIND)
                    // 图片Bitmap转file
//                val file = CommonUtils.compressImage(bitmap)
                    // 保存成功后插入到图库，其中的file是保存成功后的图片path。这里只是插入单张图片
                    // 通过发送广播将视频和图片插入相册
//                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    cursor.close()
                    takePhotoResultSuccess(filePath)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //没有任何选中
        Log.d("picker", "没有任何选中")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //切换相册源
        val album = spinnerAdapter?.getItem(position)
        mAdapter?.replaceData(album?.datas ?: mutableListOf())
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.picker_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // 设置返回值，结束当前Activity
        val result = mAdapter?.getCheckedMedia()
        if (result == null || result.isEmpty()) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return true
        }
        setListResult(result)
        return true
    }

    private fun setListResult(result: ArrayList<MediaItem>) {
        val intent = Intent()
        intent.putParcelableArrayListExtra(KEY_RESULT_PATHS, result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaScannerConnection?.disconnect()
    }

}
