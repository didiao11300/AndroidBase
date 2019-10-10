package com.maosong.component.widget

/**
 * Created by colin on 2018/7/23.
 */
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.*
import com.maosong.tools.QMUIDisplayHelper
import android.R
import android.view.Gravity.CENTER


class BotView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val itemViews: MutableList<BotItemView> = mutableListOf()
    private var mCenterView: BotItemView? = null
    private var checkedIndex = 0
    private var mPager: ViewPager? = null
    private var centerClick: View.OnClickListener? = null

    private var mOnItemClickListener: AdapterView.OnItemClickListener? = null


    override fun onFinishInflate() {
        super.onFinishInflate()
        orientation = HORIZONTAL
//        clipChildren = false
//        setBackgroundColor(Color.WHITE)
    }

    /**
     * 添加对称的条目
     * */
    fun addItems(itemParams: MutableList<BotItemParams>) {
        if (itemParams.isEmpty()/* || itemParams.size % 2 != 0*/) {
            return
        }
        val itemLayoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
        for (i in 0 until itemParams.size) {
            val itemView = BotItemView(context)
            itemView.setParams(itemParams[i])
            itemView.layoutParams = itemLayoutParams
            itemViews.add(itemView)
            val index = i
            itemView.setOnClickListener {
                chooseItem(index)
                mOnItemClickListener?.onItemClick(null, it, index, it.id.toLong())
            }
            addView(itemView)
        }
        chooseItem(0)
    }

    /**
     * 添加剧居中的项目
     * */
    public fun addCenterItem(params: BotItemParams) {
        //加入中间大按钮
        val centerViewLayoutParams = LayoutParams(0, QMUIDisplayHelper.dp2px(context, 70), 1f)
        centerViewLayoutParams.gravity = Gravity.CENTER
        val centerView = BotItemView(context)
        mCenterView = centerView
        centerView.setCenterMode(params)
        val centerIndex = itemViews.size / 2
        if (centerIndex in 0 until itemViews.size) {
            addView(centerView, centerIndex, centerViewLayoutParams)
            centerView.setOnClickListener {
                centerClick?.onClick(it)
            }
        }
    }

    public fun getCenterView(): BotItemView? {
        return mCenterView
    }

    /**
     * 设置均分的点击
     * */
    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener) {
        mOnItemClickListener = listener
    }

    /**
     * 选中
     * @param index 第几个
     * */
    fun chooseItem(index: Int) {
        checkedIndex = index
        for (i in 0 until itemViews.size) {
            itemViews[i].unCheckMe()
            if (index == i) {
                itemViews[i].checkMe()
//                itemViews[i].setBadeCount(0)
            }
        }
        mPager?.setCurrentItem(index, false)
    }


    fun setCenterViewClickListener(listener: OnClickListener) {
        centerClick = listener
    }

    fun setStepWithViewPager(viewPager: ViewPager) {
        if (viewPager.adapter?.count == itemViews.size) {
            mPager = viewPager
            mPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    chooseItem(position)
                }

            })
        } else {
            throw IndexOutOfBoundsException("导航栏item数量与viewpager页数不一样")
        }
    }

    /**
     * 设置右上角小圆点
     * */
    fun setCount(position: Int, count: Int) {
        itemViews[position].setBadeCount(count)
    }

    /**
     * 子类item
     * */
    class BotItemView(context: Context?) : RelativeLayout(context) {
        private lateinit var mParams: BotItemParams
        private var mIvImage: ImageView? = null
        private var mTvTitle: TextView? = null
        private var mBadge: BadgeView? = null
        private var isChecked = false
        private var isCenterMode = false

        fun setParams(params: BotItemParams) {
            mParams = params
            //设置控件内部竖直居中
//            gravity = Gravity.CENTER_VERTICAL
            if (params.normalIcon != 0) {
                mIvImage = ImageView(context)
                val iconLayoutParams =
                    LayoutParams(QMUIDisplayHelper.dp2px(context, 25), QMUIDisplayHelper.dp2px(context, 25))
                iconLayoutParams.addRule(CENTER_IN_PARENT)
//                iconLayoutParams.topMargin = QMUIDisplayHelper.dp2px(context, 10)
                mIvImage?.layoutParams = iconLayoutParams
                mIvImage?.setImageResource(params.normalIcon)
                mIvImage?.id = com.maosong.component.R.id.nav_item_icon
                addView(mIvImage)
            }

            if (!TextUtils.isEmpty(params.title)) {
                mTvTitle = TextView(context)
                val titleLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                titleLayoutParams.addRule(CENTER_HORIZONTAL)
                if (null != mIvImage) {
                    titleLayoutParams.addRule(BELOW, mIvImage!!.id)
                }
                mTvTitle?.setTextColor(ContextCompat.getColor(context, com.maosong.component.R.color.main_bot_text))
                mTvTitle?.textSize = 12f
                mTvTitle?.text = params.title
                mTvTitle?.layoutParams = titleLayoutParams
                addView(mTvTitle)
            }

            mBadge = BadgeView(context)
            val badgeLayoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            badgeLayoutParams.addRule(RIGHT_OF, mIvImage!!.id)
            if (null != mIvImage) {
                badgeLayoutParams.addRule(END_OF, mIvImage!!.id)
            }
            /*badgeLayoutParams.topMargin =7
            badgeLayoutParams.marginStart = 185*/
            mBadge?.layoutParams = badgeLayoutParams
            if (params.badgeCount <= 0) {
                mBadge?.visibility = View.GONE
            }
            addView(mBadge)
        }


        fun setCenterMode(params: BotItemParams) {
            isCenterMode = true
            mParams = params
            //设置控件内部竖直居中
//            gravity = Gravity.CENTER_VERTICAL
            if (params.normalIcon != 0) {
                mIvImage = ImageView(context)
                val iconLayoutParams =
                    LayoutParams(QMUIDisplayHelper.dp2px(context, 25), QMUIDisplayHelper.dp2px(context, 25))
//                iconLayoutParams.bottomMargin = QMUIDisplayHelper.dp2px(context, 5)
                iconLayoutParams.addRule(CENTER_IN_PARENT)
                mIvImage?.setImageResource(params.normalIcon)
                mIvImage?.layoutParams = iconLayoutParams
                mIvImage?.id = com.maosong.component.R.id.nav_item_icon
                addView(mIvImage)
            }

            if (!TextUtils.isEmpty(params.title)) {
                mTvTitle = TextView(context)
                val titleLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                titleLayoutParams.addRule(CENTER_HORIZONTAL)
                if (null != mIvImage) {
                    titleLayoutParams.addRule(BELOW, mIvImage!!.id)
                }
                mTvTitle?.setTextColor(Color.parseColor("#d3d3d3"))
                mTvTitle?.textSize = 12f
                mTvTitle?.text = params.title
                mTvTitle?.layoutParams = titleLayoutParams
                addView(mTvTitle)
            }

            mBadge = BadgeView(context)
            val badgeLayoutParams =
                LayoutParams(QMUIDisplayHelper.dp2px(context!!, 15), QMUIDisplayHelper.dp2px(context!!, 15))
            mBadge!!.setBadgeGravity(Gravity.CENTER)
            badgeLayoutParams.addRule(RIGHT_OF, mIvImage!!.id)
            badgeLayoutParams.addRule(ALIGN_PARENT_TOP)
            badgeLayoutParams.topMargin = QMUIDisplayHelper.dp2px(context, 15)
            mBadge!!.textSize = 10.0f
            mBadge?.layoutParams = badgeLayoutParams
            if (params.badgeCount <= 0) {
                mBadge?.visibility = View.GONE
            }
            addView(mBadge)
        }

        fun checkMe() {
            if (isCenterMode)
                return
            isChecked = true
            mIvImage?.setImageResource(mParams.checkedIcon)
            mTvTitle?.setTextColor(ContextCompat.getColor(context, com.maosong.component.R.color.colorPrimary))
        }

        fun unCheckMe() {
            if (isCenterMode)
                return
            isChecked = false
            mIvImage?.setImageResource(mParams.normalIcon)
            mTvTitle?.setTextColor(Color.parseColor("#d3d3d3"))
        }

        fun setBadeCount(count: Int) {
            if (count > 0) {
                mBadge?.visibility = View.VISIBLE
                mBadge?.badgeCount = count
            } else {
                mBadge?.visibility = View.GONE
            }
        }
    }

    data class BotItemParams(val normalIcon: Int, val checkedIcon: Int, val title: String, val badgeCount: Int = 0)
}

