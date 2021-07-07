package com.sobot.chat.listener;

import android.content.Context;

/**
 * 智齿功能点击的监听，可用于客户埋点 2.9.7 新增
 */

public interface SobotFunctionClickListener {
    //返回点击功能的类型
    void onClickFunction(Context context, SobotFunctionType functionType);
}

