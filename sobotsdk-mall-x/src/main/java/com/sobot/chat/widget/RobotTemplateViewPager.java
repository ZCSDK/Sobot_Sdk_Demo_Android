package com.sobot.chat.widget;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.sobot.chat.adapter.SobotRobotTemplatePageAdater;
import com.sobot.chat.api.model.ZhiChiMessageBase;

//多轮模板2 ViewPager
public class RobotTemplateViewPager extends ViewPager {
    private SobotRobotTemplatePageAdater adapter;
    private ZhiChiMessageBase message;

    public RobotTemplateViewPager(Context context) {
        super(context);
    }

    public RobotTemplateViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height)
                height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //绑定adapter 判断上一页下一页 使用 message  缓存当前页，下次加载时滚动上次选中页使用
    public void setTemplatePageAdater(SobotRobotTemplatePageAdater adapter, ZhiChiMessageBase message) {
        this.adapter = adapter;
        this.message = message;
    }

    //上一页
    public void selectPreviousPage() {
        if (adapter != null && getCurrentItem() != 0) {
            if (message != null) {
                message.setCurrentPageNum(getCurrentItem() - 1);
            }
            setCurrentItem(getCurrentItem() - 1);
        }
    }

    //下一页
    public void selectLastPage() {
        if (adapter != null && getCurrentItem() < (adapter.getCount() - 1)) {
            if (message != null) {
                message.setCurrentPageNum(getCurrentItem() + 1);
            }
            setCurrentItem(getCurrentItem() + 1);
        }
    }

    /**
     * 是否第一页
     */
    public boolean isFirstPage() {
        if (getCurrentItem() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否最后一页
     */
    public boolean isLastPage() {
        if (adapter != null && getCurrentItem() == (adapter.getCount() - 1)) {
            return true;
        } else {
            return false;
        }
    }

    //设置当前页面
    public void selectCurrentItem(int currentItem) {
        if (adapter != null && currentItem < adapter.getCount()) {
            if (message != null) {
                message.setCurrentPageNum(currentItem);
            }
            setCurrentItem(currentItem);
        }
    }

    //更新 message 里边的当前选中页面
    public void updateMessageSelectItem(int currentItem) {
        if (message != null) {
            message.setCurrentPageNum(currentItem);
        }
    }
}