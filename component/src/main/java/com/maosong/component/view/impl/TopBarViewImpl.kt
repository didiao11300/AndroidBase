package com.maosong.component.view.impl

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.maosong.component.R
import com.maosong.component.view.TopBarView

/**
 * create by colin on 2019-05-08
 */
class TopBarViewImpl(private val topBarView: View?) : TopBarView {

    init {
        hideTopBar()
    }

    override fun setBack(backClick: View.OnClickListener?): ImageView? {
        val back = topBarView?.findViewById<ImageView>(R.id.base_toolbar_back)
        back?.setOnClickListener(backClick)
        return back
    }

    override fun setTopBarDivideLineGone(visiable: Boolean) {
        val line = topBarView?.findViewById<View>(R.id.base_toolbar_line)
        line?.visibility = if (visiable) View.VISIBLE else View.GONE
    }

    override fun setTopBarTitle(title: CharSequence?): TextView? {
        val tvTitle = topBarView?.findViewById<TextView>(R.id.base_toolbar_title)
        if (tvTitle != null) {
            if (null == title) {
                hideTopBar()
            } else {
                showTopBar()
                tvTitle.visibility = View.VISIBLE
                tvTitle.text = title
            }
        }
        return tvTitle
    }

    override fun hideTopBar() {
        topBarView?.visibility = View.GONE
    }

    override fun showTopBar() {
        topBarView?.visibility = View.VISIBLE
    }

    override fun setTopBarColor(color: Int): View? {
        topBarView?.setBackgroundColor(color)
        return topBarView
    }

    override fun addTextButtonToRight(text: String): Button? {
        showTopBar()
        val imageButton = topBarView?.findViewById<ImageButton>(R.id.base_toolbar_right_img_btn)
        if (null != imageButton) {
            imageButton.visibility = View.GONE
        }
        val button = topBarView?.findViewById<Button>(R.id.base_toolbar_right)
        if (null != button) {
            button.visibility = View.VISIBLE
            button.text = text
        }
        return button
    }

    /**
     * @param imgRes <=0 not set
     */
    override fun addImageButtonToRight(imgRes: Int): ImageButton? {
        showTopBar()
        val button1 = topBarView?.findViewById<Button>(R.id.base_toolbar_right)
        if (button1 != null) {
            button1.visibility = View.GONE
        }
        val button = topBarView?.findViewById<ImageButton>(R.id.base_toolbar_right_img_btn)
        if (null != button) {
            if (imgRes > 0) {
                button.visibility = View.VISIBLE
                button.setImageResource(imgRes)
                button.setPadding(30, 30, 30, 30)
            } else {
                button.visibility = View.GONE
            }
        }
        return button
    }
}
