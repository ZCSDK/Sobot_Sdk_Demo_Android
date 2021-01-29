package com.sobot.chat.listener;

import android.content.Context;

import com.sobot.chat.api.model.SobotLocationModel;

/**
 * 位置卡片点击事件的监听
 */

public interface SobotMapCardListener {
    //返回true 拦截点击事件，false 不拦截点击事件，默认false
    boolean onClickMapCradMsg(Context context, SobotLocationModel locationModel);
}