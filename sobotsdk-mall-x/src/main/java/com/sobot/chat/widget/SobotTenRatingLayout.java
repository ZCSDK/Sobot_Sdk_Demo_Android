package com.sobot.chat.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.R;
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

    private LinearLayout line1,line2;

    public SobotTenRatingLayout(Context context) {
        super(context);
    }

    public SobotTenRatingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SobotTenRatingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        line1 = new LinearLayout(context);
//        line1.setOrientation(LinearLayout.HORIZONTAL);
//        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
//        line1.setLayoutParams(layoutParams);
//        addView(line1);
//        line2 = new LinearLayout(context);
//        line2.setOrientation(LinearLayout.HORIZONTAL);
//        layoutParams.topMargin = ScreenUtils.dip2px(context,10);
//        line2.setLayoutParams(layoutParams);
//        addView(line2);
    }

    public boolean isInit(){
        return null == line1;
    }
    /**
     * @param defScore    显示个数
     * @param isCanChange 点击后是否变色
     */
    public void init(int defScore, final boolean isCanChange,int width) {
        if(null == line1){
            line1 = new LinearLayout(getContext());
            line1.setOrientation(LinearLayout.HORIZONTAL);
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            line1.setLayoutParams(layoutParams);
            addView(line1);
            FrameLayout.LayoutParams layoutParams2=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            layoutParams2.gravity = Gravity.CENTER_HORIZONTAL;
            line2 = new LinearLayout(getContext());
            line2.setOrientation(LinearLayout.HORIZONTAL);
            layoutParams2.topMargin = ScreenUtils.dip2px(getContext(),10);
            line2.setLayoutParams(layoutParams2);
            addView(line2);
        }

        LayoutParams lp = null;
        selectContent=defScore;
        for (int i = 0; i < 6; i++) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.sobot_ten_rating_item, null);
            textView.setText(i + "");
            if (i != 5) {
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(getContext(), width),
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.rightMargin = ScreenUtils.dip2px(getContext(), 10f);
            } else {
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(getContext(), width),
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.rightMargin = 0;
            }
            textView.setLayoutParams(lp);
            if (i <= defScore) {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_white));
                textView.setBackgroundResource(R.drawable.sobot_ten_rating_item_bg_sel);
            } else {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_gray1));
                textView.setBackgroundResource(R.drawable.sobot_ten_rating_item_bg_def);
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
            line1.addView(textView);
        }
        for (int i = 6; i < 11; i++) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.sobot_ten_rating_item, null);
            textView.setText(i + "");
            if (i != 10) {
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(getContext(), width),
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.rightMargin = ScreenUtils.dip2px(getContext(), 10f);
            } else {
                lp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(getContext(), width),
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.rightMargin = 0;
            }
            textView.setLayoutParams(lp);
            if (i <= defScore) {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_white));
                textView.setBackgroundResource(R.drawable.sobot_ten_rating_item_bg_sel);
            } else {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_gray1));
                textView.setBackgroundResource(R.drawable.sobot_ten_rating_item_bg_def);
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
            line2.addView(textView);
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
        int totalNum = line1.getChildCount();
        for (int i = 0; i < totalNum; i++) {
            TextView tv = (TextView) line1.getChildAt(i);
            if (i <= selIndex) {
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_white));
                tv.setBackgroundResource(R.drawable.sobot_ten_rating_item_bg_sel);
            } else {
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_gray1));
                tv.setBackgroundResource(R.drawable.sobot_ten_rating_item_bg_def);
            }
        }
        if(selIndex>5) {
            int totalNum2 = line2.getChildCount();
            for (int i = 0; i < totalNum2; i++) {
                TextView tv = (TextView) line2.getChildAt(i);
                if (i <= selIndex -6) {
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_white));
                    tv.setBackgroundResource(R.drawable.sobot_ten_rating_item_bg_sel);
                } else {
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_gray1));
                    tv.setBackgroundResource(R.drawable.sobot_ten_rating_item_bg_def);
                }
            }
        }else{
            int totalNum2 = line2.getChildCount();
            for (int i = 0; i < totalNum2; i++) {
                TextView tv = (TextView) line2.getChildAt(i);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_gray1));
                tv.setBackgroundResource(R.drawable.sobot_ten_rating_item_bg_def);
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