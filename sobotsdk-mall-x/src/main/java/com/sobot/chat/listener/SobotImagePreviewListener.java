package com.sobot.chat.listener;

import android.content.Context;

/**
 * 点击图片预览拦截器，客户可自己处理
 */

public interface SobotImagePreviewListener {
    //true 自己处理图片的预览逻辑，false跳转到sdk内部的图片预览界面
    boolean onPreviewImage(Context context, String imgPath);
}