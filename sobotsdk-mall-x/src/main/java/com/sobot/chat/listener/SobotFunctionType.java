package com.sobot.chat.listener;

/**
 * 智齿功能点击的分类
 */

public enum SobotFunctionType {
    // 留言返回
    ZC_CloseLeave(1),
    // 会话页面
    ZC_CloseChat(2),
    // 帮助中心
    ZC_CloseHelpCenter(3),
    // 电商消息中心
    ZC_CloseChatList(4),
    // 电话联系客服
    ZC_PhoneCustomerService(5);

    private int typeNum;

    // 构造方法
    private SobotFunctionType(int typeNum) {
        this.typeNum = typeNum;
    }

    @Override
    public String toString() {
        return "SobotFunctionEnum{" +
                "typeNum=" + typeNum +
                '}';
    }
}

