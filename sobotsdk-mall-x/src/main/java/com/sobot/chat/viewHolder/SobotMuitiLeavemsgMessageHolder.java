package com.sobot.chat.viewHolder;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 工单节点留言回显
 */
public class SobotMuitiLeavemsgMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private LinearLayout sobot_text_ll;
    private ImageView sobot_msgStatus;
    private ProgressBar sobot_msgProgressBar;
    private ZhiChiMessageBase mMessage;

    public SobotMuitiLeavemsgMessageHolder(Context context, View convertView) {
        super(context, convertView);
        sobot_text_ll = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_text_ll"));
        sobot_msgProgressBar = (ProgressBar) convertView.findViewById(ResourceUtils.getResId(context, "sobot_msgProgressBar"));
        sobot_msgStatus = (ImageView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_msgStatus"));
    }

    @Override
    public void bindData(Context context, ZhiChiMessageBase message) {
        mMessage = message;
        setMsgContent(mContext, message);
        refreshUi();
    }

    private void refreshUi() {
        try {
            if (mMessage == null) {
                return;
            }
            if (mMessage.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_SUCCESS) {// 成功的状态
                sobot_msgStatus.setVisibility(View.GONE);
                sobot_msgProgressBar.setVisibility(View.GONE);
            } else if (mMessage.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_ERROR) {
                sobot_msgStatus.setVisibility(View.VISIBLE);
                sobot_msgProgressBar.setVisibility(View.GONE);
                sobot_msgProgressBar.setClickable(true);
                sobot_msgStatus.setOnClickListener(this);
            } else if (mMessage.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_LOADING) {
                sobot_msgStatus.setVisibility(View.GONE);
                sobot_msgProgressBar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setMsgContent(final Context context, final ZhiChiMessageBase message) {
        sobot_text_ll.removeAllViews();
        if (!TextUtils.isEmpty(message.getAnswer().getMsg())) {
            String[] arr = message.getAnswer().getMsg().split("\n");
            for (int i = 0; i < arr.length; i++) {
                TextView textView = new TextView(mContext);
                textView.setTextSize(14);
                LinearLayout.LayoutParams wlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                if (i != 0) {
                    wlayoutParams.setMargins(0, ScreenUtils.dip2px(context, 8), 0, 0);
                }else{
                    wlayoutParams.setMargins(0, 0, 0, 0);
                }
                textView.setLayoutParams(wlayoutParams);

                sobot_text_ll.addView(textView);
                if ((i + 1) % 2 == 0) {
                    if (StringUtils.isEmpty(arr[i])){
                        textView.setText(" - -");
                    }else{
                        textView.setText(Html.fromHtml(arr[i]).toString().trim());
                    }
                    textView.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_common_gray1")));
                } else {
                    textView.setText(Html.fromHtml(arr[i]).toString().trim() + ":");
                    textView.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_common_gray2")));
                }
                if ((i + 1) % 2 == 0 && i < (arr.length - 1)) {
                    View lineView = new View(mContext);
                    LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            ScreenUtils.dip2px(context, 0.4f));
                    lineLayoutParams.setMargins(0, ScreenUtils.dip2px(context, 8), 0, 0);
                    lineView.setLayoutParams(lineLayoutParams);
                    lineView.setBackgroundColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_line_1dp")));
                    sobot_text_ll.addView(lineView);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_msgStatus) {
            showReSendDialog(mContext, msgStatus, new ReSendListener() {

                @Override
                public void onReSend() {
                    if (msgCallBack != null && mMessage != null && mMessage.getAnswer() != null) {
                        // msgCallBack.sendMessageToRobot(mMessage, 5, 0, null);
                    }
                }
            });
        }
    }
}
