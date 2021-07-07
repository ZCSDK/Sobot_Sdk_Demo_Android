package com.sobot.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sobot.chat.activity.SobotTicketDetailActivity;
import com.sobot.chat.api.model.SobotLeaveReplyModel;
import com.sobot.chat.api.model.SobotUserTicketInfo;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ZhiChiConstant;

/**
 * 留言广播接受者的逻辑处理类
 */
public class SobotLeaveMsgReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //获取工单回复列表，如果获取到有未读的留言回复，把最新的一条展示在通知栏中，点击通知栏通知，跳转到留言详情页面
        if (ZhiChiConstant.SOBOT_LEAVEREPLEY_NOTIFICATION_CLICK.equals(intent.getAction())) {
            SobotLeaveReplyModel leaveReplyModel = (SobotLeaveReplyModel) intent.getSerializableExtra("sobot_leavereply_model");
            String companyId = intent.getStringExtra("sobot_leavereply_companyId");
            String uid = intent.getStringExtra("sobot_leavereply_uid");
            LogUtils.i(" 留言回复：" + leaveReplyModel);
            SobotUserTicketInfo item = new SobotUserTicketInfo();
            item.setTicketId(leaveReplyModel.getTicketId());
            Intent mintent = SobotTicketDetailActivity.newIntent(context, companyId, uid, item);
            context.startActivity(mintent);
        }
    }
}
