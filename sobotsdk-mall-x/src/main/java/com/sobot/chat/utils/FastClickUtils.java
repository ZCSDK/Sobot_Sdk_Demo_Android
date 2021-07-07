package com.sobot.chat.utils;

public class FastClickUtils {
    // 两次点击按钮之间的点击间隔不能少于3000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 3000;
    private static long lastClickTime;

    /**
     * 两次点击按钮之间的点击间隔不能少于3000毫秒才能再次点击
     * @return true 可以继续点击 false 不可以
     */
    public static boolean isCanClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}
