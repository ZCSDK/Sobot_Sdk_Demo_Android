package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 系统提醒消息
 */
public class SystemMessageHolder extends MessageHolderBase {
    TextView center_Remind_Info; // 中间提醒消息

    public SystemMessageHolder(Context context, View convertView) {
        super(context, convertView);
        center_Remind_Info = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id", "sobot_center_Remind_note"));
    }

    @Override
    public void bindData(Context context, ZhiChiMessageBase message) {

        if (!TextUtils.isEmpty(message.getMsg())) {
            center_Remind_Info.setVisibility(View.VISIBLE);
            center_Remind_Info.setText(message.getMsg());
        }
    }
}
