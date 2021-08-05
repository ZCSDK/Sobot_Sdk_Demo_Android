package com.sobot.chat.widget.horizontalgridpage;

import android.view.View;

/**
 * 回调处理
 */
public interface SobotRecyclerCallBack {

    /**
     * 点击事件
     *
     * @param view     点击的view
     * @param position view的位置
     */
    void onItemClickListener(View view, int position);

    /**
     * 长按事件
     *
     * @param view     长按的view
     * @param position view的位置
     */
    void onItemLongClickListener(View view, int position);
}
