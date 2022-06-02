package com.sobot.chat.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;

/**
 * @Description: 十分评价布局，
 * @Author: znw
 * @Date: 2021/4/2 下午1:41
 * @Version: 1.0
 */
public class SobotTenRatingLayout extends LinearLayout {

    private int selectContent;//选中的值
    private OnClickItemListener onClickItemListener;

    public SobotTenRatingLayout(Context context) {
        super(context);
    }

    public SobotTenRatingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SobotTenRatingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param defScore    显示个数
     * @param isCanChange 点击后是否变色
     */
    public void init(int defScore, final boolean isCanChange) {
        LayoutParams lp = null;
        selectContent=defScore;
        for (int i = 0; i < 11; i++) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(ResourceUtils.getIdByName(getContext(), "layout", "sobot_ten_rating_item"), null);
            textView.setText(i + "");
            if (i != 10) {
                lp = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                lp.rightMargin = ScreenUtils.dip2px(getContext(), 6f);
            } else {
                lp = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                lp.rightMargin = 0;
            }
            textView.setLayoutParams(lp);
            if (i <= defScore) {
                textView.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_common_white")));
                textView.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_ten_rating_item_bg_sel"));
            } else {
                textView.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_common_gray1")));
                textView.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_ten_rating_item_bg_def"));
            }
            final int position = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickItemListener != null) {
                        if (isCanChange) {
                            updateUI(position);
                        }
                        onClickItemListener.onClickItem(position);
                        selectContent = position;
                    }
                }
            });
            addView(textView);
        }
    }


    //(x,y)是否在view的区域内
    private boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        //view.isClickable() &&
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    public void updateUI(int selIndex) {
        int totalNum = getChildCount();
        for (int i = 0; i < totalNum; i++) {
            TextView tv = (TextView) getChildAt(i);
            if (i <= selIndex) {
                tv.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_common_white")));
                tv.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_ten_rating_item_bg_sel"));
            } else {
                tv.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_common_gray1")));
                tv.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_ten_rating_item_bg_def"));
            }
        }
    }

    //选中item的回调,返回选中的分值
    public interface OnClickItemListener {
        void onClickItem(int selectIndex);
    }

    public OnClickItemListener getOnClickItemListener() {
        return onClickItemListener;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public int getSelectContent() {
        return selectContent;
    }

    public void setSelectContent(int selectContent) {
        this.selectContent = selectContent;
    }
}