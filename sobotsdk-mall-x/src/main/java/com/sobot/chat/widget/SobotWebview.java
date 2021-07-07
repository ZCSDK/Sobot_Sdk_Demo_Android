package com.sobot.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 自定义webivew使用改成super(context.getApplicationContext(),避免(vivo（Android5.1），使用原生WebView空白)
 */
public class SobotWebview extends WebView {
    public SobotWebview(Context context) {
        super(context.getApplicationContext());
    }

    public SobotWebview(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
    }

    public SobotWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context.getApplicationContext(), attrs, defStyleAttr);
    }

}
