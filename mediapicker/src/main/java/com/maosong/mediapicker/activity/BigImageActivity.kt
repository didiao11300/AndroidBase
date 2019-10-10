package com.maosong.mediapicker.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.maosong.mediapicker.R

/**
 * 大图预览
 *
 * */
class BigImageActivity : AppCompatActivity() {
    companion object {
        val KEY_PATH = "_key_path"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_big_image)
        if (intent.extras == null)
            return
        val path = intent.extras.getString(KEY_PATH)
        val photoview = findViewById<PhotoView>(R.id.large_photo)
        Glide.with(this).load(path).into(photoview)
    }
}
