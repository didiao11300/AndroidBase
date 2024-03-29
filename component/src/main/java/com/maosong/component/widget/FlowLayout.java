package com.maosong.component.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.maosong.component.R;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * create by colin
 * <p>
 * <p>
 * Horizontally lay out children until the row is filled and then moved to the next line. Call
 * {@link FlowLayout#setSingleLine(boolean)} to disable reflow and lay all children out in one line.
 */
@RestrictTo(LIBRARY_GROUP)
public class FlowLayout extends ViewGroup {
    private int lineSpacing;
    private int itemSpacing;
    private boolean singleLine;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        singleLine = false;
        loadFromAttributes(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        singleLine = false;
        loadFromAttributes(context, attrs);
    }


    private void loadFromAttributes(Context context, AttributeSet attrs) {
        final TypedArray array =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.FlowLayout, 0, 0);
        lineSpacing = array.getDimensionPixelSize(R.styleable.FlowLayout_lineSpacing, 0);
        itemSpacing = array.getDimensionPixelSize(R.styleable.FlowLayout_itemSpacing, 0);
        array.recycle();
    }

    protected int getLineSpacing() {
        return lineSpacing;
    }

    protected void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    protected int getItemSpacing() {
        return itemSpacing;
    }

    protected void setItemSpacing(int itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    protected boolean isSingleLine() {
        return singleLine;
    }

    /**
     * Sets whether this chip group is single line, or reflowed multiline.
     */
    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final int maxWidth =
                widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY
                        ? width
                        : Integer.MAX_VALUE;

        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childBottom = childTop;
        int childRight = childLeft;
        int maxChildRight = 0;
        final int maxRight = maxWidth - getPaddingRight();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            LayoutParams lp = child.getLayoutParams();
            int leftMargin = 0;
            int rightMargin = 0;
            if (lp instanceof MarginLayoutParams) {
                MarginLayoutParams marginLp = (MarginLayoutParams) lp;
                leftMargin += marginLp.leftMargin;
                rightMargin += marginLp.rightMargin;
            }

            childRight = childLeft + leftMargin + child.getMeasuredWidth();

            if (childRight > maxRight && !isSingleLine()) {
                childLeft = getPaddingLeft();
                childTop = childBottom + lineSpacing;
            }

            childRight = childLeft + leftMargin + child.getMeasuredWidth();
            childBottom = childTop + child.getMeasuredHeight();

            if (childRight > maxChildRight) {
                maxChildRight = childRight;
            }

            childLeft += (leftMargin + rightMargin + child.getMeasuredWidth()) + itemSpacing;
        }

        int finalWidth = getMeasuredDimension(width, widthMode, maxChildRight);
        int finalHeight = getMeasuredDimension(height, heightMode, childBottom);
        setMeasuredDimension(finalWidth, finalHeight);
    }

    private static int getMeasuredDimension(int size, int mode, int childrenEdge) {
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.AT_MOST:
                return Math.min(childrenEdge, size);
            default: // UNSPECIFIED:
                return childrenEdge;
        }
    }

    @Override
    protected void onLayout(boolean sizeChanged, int left, int top, int right, int bottom) {
        if (getChildCount() == 0) {
            // Do not re-layout when there are no children.
            return;
        }

        boolean isRtl = ViewCompat.getLayoutDirection(this) == LAYOUT_DIRECTION_RTL;
        int paddingStart = isRtl ? getPaddingRight() : getPaddingLeft();
        int paddingEnd = isRtl ? getPaddingLeft() : getPaddingRight();
        int childStart = paddingStart;
        int childTop = getPaddingTop();
        int childBottom = childTop;
        int childEnd;

        final int maxChildEnd = right - left - paddingEnd;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams lp = child.getLayoutParams();
            int startMargin = 0;
            int endMargin = 0;
            if (lp instanceof MarginLayoutParams) {
                MarginLayoutParams marginLp = (MarginLayoutParams) lp;
                startMargin = MarginLayoutParamsCompat.getMarginStart(marginLp);
                endMargin = MarginLayoutParamsCompat.getMarginEnd(marginLp);
            }

            childEnd = childStart + startMargin + child.getMeasuredWidth();

            if (!singleLine && (childEnd > maxChildEnd)) {
                childStart = paddingStart;
                childTop = childBottom + lineSpacing;
            }

            childEnd = childStart + startMargin + child.getMeasuredWidth();
            childBottom = childTop + child.getMeasuredHeight();

            if (isRtl) {
                child.layout(
                        maxChildEnd - childEnd, childTop, maxChildEnd - childStart - startMargin, childBottom);
            } else {
                child.layout(childStart + startMargin, childTop, childEnd, childBottom);
            }

            childStart += (startMargin + endMargin + child.getMeasuredWidth()) + itemSpacing;
        }
    }
}
