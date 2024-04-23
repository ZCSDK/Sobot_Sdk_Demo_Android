package com.sobot.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

//解决gridview item 高度不一致 导致item有空白的问题
public class SobotGridView extends GridView {

    public SobotGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SobotGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SobotGridView(Context context) {
        super(context);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
