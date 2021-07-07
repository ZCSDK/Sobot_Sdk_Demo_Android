package com.sobot.chat.listener;

import com.sobot.chat.api.model.SobotLeaveReplyModel;

import java.util.List;

/**
 * 获取未读留言回复列表的监听
 */

public interface SobotNoReadLeaveReplyListener {

    void onNoReadLeaveReplyListener(List<SobotLeaveReplyModel> sobotLeaveReplyModelList);
}