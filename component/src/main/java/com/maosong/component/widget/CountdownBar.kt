package com.maosong.component.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator

/**
 * create by colin on 2018/12/10
 */
class CountdownBar : View {

    private var mWidth = 0
    private var mHeight = 0
    private var mPaint = Paint()
    private var mStrokeWidth = 10f
    private var mColor: Int = Color.BLACK
    private var mMax = 100f
    private var mProgress = mMax
    private var mRectF: RectF? = null
    private var mAnim: ObjectAnimator? = null
    private var mEndCall: (() -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mStrokeWidth
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.color = mColor
        mPaint.isAntiAlias = true
        mAnim = ObjectAnimator.ofFloat(this, "progress", mMax, 0f)
        mAnim?.interpolator = LinearInterpolator()
        mAnim?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                reset()
                mEndCall?.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
                reset()
            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        val left = 0f + mStrokeWidth + paddingLeft
        val top = 0f + mStrokeWidth + paddingTop
        val right = mWidth.toFloat() - mStrokeWidth - paddingRight
        val bottom = mHeight.toFloat() - mStrokeWidth - paddingBottom
        mRectF = RectF(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        //画一个圆圈就行了。
        if (null != mRectF)
            canvas.drawArc(mRectF!!, 0f, -getArcByProgress(), false, mPaint)
    }

    fun setProgress(progress: Float) {
        if (progress !in 0f..mMax)
            return
        mProgress = progress
        postInvalidate()
    }

    fun incrementProgressBy(diff: Int) {
        mProgress += diff
        if (mProgress !in 0f..mMax) {
            mProgress -= diff
            return
        }
        postInvalidate()
    }

    fun setMax(max: Float) {
        mMax = max
        mProgress = mMax
        postInvalidate()
    }

    fun setColor(@ColorInt color: Int) {
        mColor = color
        mPaint.color = mColor
        postInvalidate()
    }

    fun setStrokeWidth(width: Float) {
        mPaint.strokeWidth = width
        postInvalidate()
    }

    fun reset() {
        mProgress = mMax
        postInvalidate()
    }

    //默认60秒倒计时
    fun startCountDown(duration: Long = 60000, endCall: (() -> Unit)? = null) {
        mAnim?.duration = duration
        mAnim?.start()
        mEndCall = endCall
    }

    fun stopCountDown() {
        mAnim?.cancel()
    }


    private fun getArcByProgress(): Float {
        return mProgress / mMax * 360
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mw = getMeasureSize(widthMeasureSpec)
        val mh = getMeasureSize(heightMeasureSpec)
        //保证是正方形的
        setMeasuredDimension(Math.min(mw, mh), Math.min(mw, mh))
    }

    private fun getMeasureSize(measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        var size = MeasureSpec.getSize(measureSpec)
        when (mode) {
            MeasureSpec.AT_MOST -> {
                val mp = parent
                if (mp is ViewGroup) {
                    if (size > Math.min(mp.measuredWidth, mp.measuredHeight)) {
                        size = Math.min(mp.measuredWidth, mp.measuredHeight)
                    }
                }
            }
            MeasureSpec.UNSPECIFIED -> {
                size = 100 //默认给个100像素
            }
            MeasureSpec.EXACTLY -> {
                //就是原来的值
            }
        }
        return size
    }
}