package com.maosong.component.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import android.view.MotionEvent

class PressedScaleButton : AppCompatImageButton {

    constructor(context: Context) : super(context) {
        setBackgroundColor(Color.TRANSPARENT)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        setBackgroundColor(Color.TRANSPARENT)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //缩小动画
                animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(80)
                    .start()
            }
            MotionEvent.ACTION_UP -> {
                //放大动画
                animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(80)
                    .start()
            }
            MotionEvent.ACTION_CANCEL -> {
                //放大动画
                animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(80)
                    .start()
            }
        }
        return super.onTouchEvent(event)
    }


}