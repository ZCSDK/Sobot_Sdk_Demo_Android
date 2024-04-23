package com.sobot.chat.listener;

import android.content.Context;

import com.sobot.chat.api.model.Information;

/**
 * 帮助中心打开在线帮助是否拦截
 */

public interface SobotHelpPageOpenChatListener {

    // 帮助中心 在线客服的点击事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
    boolean onOpenChatClick(Context context, Information information);
}