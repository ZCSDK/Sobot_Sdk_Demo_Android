package com.sobot.chat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.sobot.chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动换行布局
 */
public class SobotAntoLineLayout extends ViewGroup {

    public static final int MODE_FILL_PARENT = 0;
    public static final int MODE_WRAP_CONTENT = 1;

    private int mVerticalGap = 16;
    private int mHorizontalGap = 10;

    private int mFillMode = MODE_FILL_PARENT;

    private List<Integer> childOfLine; //Save the count of child views of each line;
    private List<Integer> mOriginWidth;

    public SobotAntoLineLayout(Context context) {
        super(context);
        mOriginWidth = new ArrayList<>();
    }

    public SobotAntoLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOriginWidth = new ArrayList<>();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.sobot_autoWrapLineLayout);
        mHorizontalGap = ta.getDimensionPixelSize(R.styleable.sobot_autoWrapLineLayout_horizontalGap, 0);
        mVerticalGap = ta.getDimensionPixelSize(R.styleable.sobot_autoWrapLineLayout_verticalGap, 0);
        mFillMode = ta.getInteger(R.styleable.sobot_autoWrapLineLayout_fillMode, 0);
        ta.recycle();
    }

    public SobotAntoLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOriginWidth = new ArrayList<>();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.sobot_autoWrapLineLayout);
        mHorizontalGap = ta.getDimensionPixelSize(R.styleable.sobot_autoWrapLineLayout_horizontalGap, 0);
        mVerticalGap = ta.getDimensionPixelSize(R.styleable.sobot_autoWrapLineLayout_verticalGap, 0);
        mFillMode = ta.getInteger(R.styleable.sobot_autoWrapLineLayout_fillMode, 0);
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mFillMode == MODE_FILL_PARENT) {
            layoutModeFillParent();
        } else {
            layoutWrapContent();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childOfLine = new ArrayList<>();
        int childCount = getChildCount();
        int totalHeight = 0;
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int curLineChildCount = 0;
        int curLineWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View childItem = getChildAt(i);
            if (mFillMode == MODE_FILL_PARENT) {
                if (mOriginWidth.size() <= i) {
                    measureChild(childItem, widthMeasureSpec, heightMeasureSpec);
                    mOriginWidth.add(childItem.getMeasuredWidth());
                } else {
                    childItem.measure(MeasureSpec.makeMeasureSpec(mOriginWidth.get(i), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(childItem.getMeasuredHeight(), MeasureSpec.EXACTLY));
                }
            } else {
                measureChild(childItem, widthMeasureSpec, heightMeasureSpec);
            }
            int childHeight = childItem.getMeasuredHeight();
            int childWidth = childItem.getMeasuredWidth();
            if (curLineWidth + childWidth <= totalWidth) {
                curLineWidth += childWidth;
                maxHeight = Math.max(childHeight, maxHeight);
                curLineChildCount++;
            } else {
                childOfLine.add(curLineChildCount);
                curLineWidth = childWidth;
                curLineChildCount = 1;
                totalHeight += maxHeight;
                maxHeight = childHeight;
            }

        }
        childOfLine.add(curLineChildCount);
        for (int i = 0; i < childOfLine.size(); i++) {
            if (childOfLine.get(i) == 0) {
                childOfLine.remove(i);
            }
        }
        totalHeight += (mVerticalGap * (childOfLine.size() - 1) + maxHeight);
        setMeasuredDimension(totalWidth, totalHeight);
    }

    private void layoutModeFillParent() {
        int index = 0;
        int width = getMeasuredWidth();
        int curHeight = 0;
        for (int i = 0; i < childOfLine.size(); i++) {
            int childCount = childOfLine.get(i);
            int maxHeight = 0;
            int lineWidth = 0;
            for (int j = 0; j < childCount; j++) {
                lineWidth += getChildAt(j + index).getMeasuredWidth();
            }
            int padding = (width - lineWidth - mHorizontalGap * (childCount - 1)) / childCount / 2;
            lineWidth = 0;
            int target = index + childCount;
            for (; index < target; index++) {
                View item = getChildAt(index);
                maxHeight = Math.max(maxHeight, item.getMeasuredHeight());
                item.setPadding(padding, item.getPaddingTop(),
                        padding, item.getPaddingBottom());
                item.measure(MeasureSpec.makeMeasureSpec(item.getMeasuredWidth() + padding * 2, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(item.getMeasuredHeight(), MeasureSpec.EXACTLY));
                item.layout(lineWidth, curHeight, lineWidth + item.getMeasuredWidth(), curHeight + item.getMeasuredHeight());
                lineWidth += item.getMeasuredWidth() + mHorizontalGap;
            }
            curHeight += maxHeight + mVerticalGap;
        }
    }

    private void layoutWrapContent() {
        int index = 0;
        int curHeight = 0;
        for (int i = 0; i < childOfLine.size(); i++) {
            int childCount = childOfLine.get(i);
            int maxHeight = 0;
            int lineWidth = 0;
            int target = index + childCount;
            for (; index < target; index++) {
                View item = getChildAt(index);
                maxHeight = Math.max(maxHeight, item.getMeasuredHeight());
                item.layout(lineWidth, curHeight, lineWidth + item.getMeasuredWidth(), curHeight + item.getMeasuredHeight());
                lineWidth += item.getMeasuredWidth() + mHorizontalGap;
            }
            curHeight += maxHeight + mVerticalGap;
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }

    public void setFillMode(int fillMode) {
        this.mFillMode = fillMode;
    }

    public void setHorizontalGap(int horizontalGap) {
        this.mHorizontalGap = horizontalGap;
    }

    public void setVerticalGap(int verticalGap) {
        this.mVerticalGap = verticalGap;
    }

}