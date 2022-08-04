package com.sobot.chat.listener;

import android.content.Context;

import com.sobot.chat.api.model.MiniProgramModel;

/**
 * 小程序点击的监听，3.1.4 新增
 */

public interface SobotMiniProgramClickListener {
    //返回小程序的信息
    void onClick(Context context, MiniProgramModel miniProgramModel);
}

