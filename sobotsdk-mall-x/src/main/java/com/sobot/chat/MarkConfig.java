package com.sobot.chat;

/**
 * 开关常量标识
 */
public class MarkConfig {

    private static int markValue = 0b00000000010;


    /**
     * 右起第一位 默认 false 竖屏
     */
    public static final int LANDSCAPE_SCREEN = 0b1;
    /**
     * 留言关闭状态下，默认 true 可一直回复
     */
    public static final int LEAVE_COMPLETE_CAN_REPLY = 0b10;

    /**
     * 横屏下刘海屏和水滴屏是否显示 默认 false 不显示
     */
    public static final int DISPLAY_INNOTCH = 0b100;

    /**
     * 是否自动适配时区，默认不适配，使用北京时区
     */
    public static final int AUTO_MATCH_TIMEZONE = 0b1000;

    /**
     * 是否在申请权限前弹出权限用途提示框,默认不弹
     */
    public static final int SHOW_PERMISSION_TIPS_POP = 0b10000;

    /**
     * 获取开关位
     *
     * @param mark 开关名
     * @return 1 true,0 false
     */
    public static boolean getON_OFF(int mark) {
        if ((markValue & mark) == mark) {
            return true;
        }
        return false;
    }


    /**
     * 设置开关
     *
     * @param mark 开关位名
     * @param isON true 1,false 0
     */
    public static void setON_OFF(int mark, boolean isON) {
        if (isON) {
            markValue = markValue | mark;
        } else {
            markValue = markValue & (~mark);

        }
    }

    public static void main(String[] args) {

//        setON_OFF(LANDSCAPE_SCREEN,true);
//        System.out.println(Integer.toBinaryString(markValue));

        System.out.println(getON_OFF(DISPLAY_INNOTCH));

    }

}
