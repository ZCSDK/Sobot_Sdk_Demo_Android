package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 提醒消息
 * Created by jinxl on 2017/3/17.
 */
public class RemindMessageHolder extends MessageHolderBase {
    TextView center_Remind_Info; // 中间提醒消息
    TextView center_Remind_Info1; // 已无更多记录
    TextView center_Remind_Info2; // simple tip
    RelativeLayout rl_not_read; //以下为新消息
    TextView sobot_center_Remind_note5;

    public RemindMessageHolder(Context context, View convertView) {
        super(context, convertView);
        center_Remind_Info = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id", "sobot_center_Remind_note"));
        center_Remind_Info1 = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id", "sobot_center_Remind_note1"));
        center_Remind_Info2 = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id", "sobot_center_Remind_note2"));
        rl_not_read = (RelativeLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id", "rl_not_read"));
        sobot_center_Remind_note5 = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id", "sobot_center_Remind_note5"));
        sobot_center_Remind_note5.setText(ResourceUtils.getResString(context, "sobot_no_read"));
    }

    @Override
    public void bindData(Context context, ZhiChiMessageBase message) {

        if (message.getAnswer() != null && !TextUtils.isEmpty(message.getAnswer().getMsg())) {
            if (message.getAnswer().getRemindType() == ZhiChiConstant.sobot_remind_type_nomore) {
                rl_not_read.setVisibility(View.GONE);
                center_Remind_Info2.setVisibility(View.GONE);
                center_Remind_Info.setVisibility(View.GONE);
                center_Remind_Info1.setVisibility(View.VISIBLE);
                center_Remind_Info1.setText(message.getAnswer().getMsg());
            } else if (message.getAnswer().getRemindType() == ZhiChiConstant.sobot_remind_type_below_unread) {
                rl_not_read.setVisibility(View.VISIBLE);
                center_Remind_Info.setVisibility(View.GONE);
                center_Remind_Info1.setVisibility(View.GONE);
                center_Remind_Info2.setVisibility(View.GONE);
            } else if (message.getAnswer().getRemindType() == ZhiChiConstant.sobot_remind_type_simple_tip) {
                rl_not_read.setVisibility(View.GONE);
                center_Remind_Info.setVisibility(View.GONE);
                center_Remind_Info1.setVisibility(View.GONE);
                center_Remind_Info2.setVisibility(View.VISIBLE);
                HtmlTools.getInstance(context).setRichText(center_Remind_Info2, message
                        .getAnswer().getMsg(), ResourceUtils.getIdByName(context, "color", "sobot_color"));
            } else if (message.getAnswer().getRemindType() == ZhiChiConstant.sobot_remind_type_keep_queuing_tip) {
                rl_not_read.setVisibility(View.GONE);
                center_Remind_Info.setVisibility(View.GONE);
                center_Remind_Info1.setVisibility(View.GONE);
                center_Remind_Info2.setVisibility(View.VISIBLE);
                HtmlTools.getInstance(context).setRichText(center_Remind_Info2, message
                        .getAnswer().getMsg(), ResourceUtils.getIdByName(context, "color", "sobot_color"));
            } else {
                rl_not_read.setVisibility(View.GONE);
                center_Remind_Info2.setVisibility(View.GONE);
                center_Remind_Info.setVisibility(View.VISIBLE);
                center_Remind_Info1.setVisibility(View.GONE);
                int remindType = message.getAnswer().getRemindType();
                if (ZhiChiConstant.action_remind_info_post_msg.equals(message.getAction())) {
                    if (remindType == ZhiChiConstant.sobot_remind_type_customer_offline) {
                        //暂无客服在线   和 暂时无法转接人工客服
                        if (message.isShake()) {
                            center_Remind_Info.setAnimation(shakeAnimation(5));
                        }
                        setRemindPostMsg(context, center_Remind_Info, message, false);
                    }
                    if (remindType == ZhiChiConstant.sobot_remind_type_unable_to_customer) {
                        //暂无客服在线   和 暂时无法转接人工客服
                        if (message.isShake()) {
                            center_Remind_Info.setAnimation(shakeAnimation(5));
                        }
                        setRemindPostMsg(context, center_Remind_Info, message, true);
                    }
                } else if (ZhiChiConstant.action_remind_info_paidui.equals(message.getAction())) {
                    if (remindType == ZhiChiConstant.sobot_remind_type_paidui_status) {
                        //您在队伍中的第...
                        if (message.isShake()) {
                            center_Remind_Info.setAnimation(shakeAnimation(5));
                        }
                        setRemindPostMsg(context, center_Remind_Info, message, false);
                    }
                } else if (ZhiChiConstant.action_remind_connt_success.equals(message.getAction())) {
                    if (remindType == ZhiChiConstant.sobot_remind_type_accept_request) {
                        //接受了您的请求
                        center_Remind_Info.setText(Html.fromHtml(message.getAnswer().getMsg()));
                    }
                } else if (ZhiChiConstant.sobot_outline_leverByManager.equals(message
                        .getAction()) || ZhiChiConstant.action_remind_past_time.equals(message.getAction())) {
                    //结束了本次会话  有事离开 超时下线 ....的提醒
                    HtmlTools.getInstance(context).setRichText(center_Remind_Info, message
                            .getAnswer().getMsg(), ResourceUtils.getIdByName(context, "color", "sobot_color_link_remind"));
                } else if (remindType == ZhiChiConstant.sobot_remind_type_tip || remindType == ZhiChiConstant.sobot_remind_type_accept_request) {
                    center_Remind_Info.setText(message.getAnswer().getMsg());
                }
            }

            if (message.isShake()) {
                center_Remind_Info.setAnimation(shakeAnimation(5));
                message.setShake(false);
            }
        } else if (ZhiChiConstant.action_remind_info_zhuanrengong.equals(message.getAction())) {
            rl_not_read.setVisibility(View.GONE);
            center_Remind_Info2.setVisibility(View.GONE);
            center_Remind_Info.setVisibility(View.VISIBLE);
            center_Remind_Info1.setVisibility(View.GONE);
            setRemindToCustom(context, center_Remind_Info);
        } else if (ZhiChiConstant.action_sensitive_auth_agree.equals(message.getAction())) {
            rl_not_read.setVisibility(View.GONE);
            center_Remind_Info2.setVisibility(View.GONE);
            center_Remind_Info.setVisibility(View.VISIBLE);
            center_Remind_Info1.setVisibility(View.GONE);
            center_Remind_Info.setText(ResourceUtils.getResStrId(context, "sobot_agree_sentisive_tip"));
        } else if (ZhiChiConstant.action_mulit_postmsg_tip_can_click.equals(message.getAction()) || ZhiChiConstant.action_mulit_postmsg_tip_nocan_click.equals(message.getAction())) {
            rl_not_read.setVisibility(View.GONE);
            center_Remind_Info.setVisibility(View.GONE);
            center_Remind_Info1.setVisibility(View.GONE);
            center_Remind_Info2.setVisibility(View.VISIBLE);
            ZhiChiInitModeBase initMode = (ZhiChiInitModeBase) SharedPreferencesUtil.getObject(mContext,
                    ZhiChiConstant.sobot_last_current_initModel);
            //47可以点击，48不可点击
            if (ZhiChiConstant.action_mulit_postmsg_tip_can_click.equals(message.getAction()) && initMode != null && initMode.getCid().equals(message.getCid())) {
                HtmlTools.getInstance(context).setRichText(center_Remind_Info2, message.getMsg().replace("<a>", "<a href='sobot:SobotMuItiPostMsgActivty?" + message.getDeployId()+"::"+message.getMsgId() + "'>"), ResourceUtils.getIdByName(context, "color", "sobot_color"));
            } else {
                HtmlTools.getInstance(context).setRichText(center_Remind_Info2, message.getMsg(), ResourceUtils.getIdByName(context, "color", "sobot_color"));
            }
        }
    }

    /**
     * @param context
     * @param remindInfo
     * @param message
     * @param haveDH     是否有 逗号
     */
    private void setRemindPostMsg(Context context, TextView remindInfo, ZhiChiMessageBase message, boolean haveDH) {
        int isLeaveMsg = SharedPreferencesUtil.getIntData(context, ZhiChiConstant.sobot_msg_flag, ZhiChiConstant.sobot_msg_flag_open);
        String postMsg = (haveDH ? "，" : " ") + ResourceUtils.getResString(context, "sobot_can") + "<a href='sobot:SobotPostMsgActivity'> " + ResourceUtils.getResString(context, "sobot_str_bottom_message") + "</a>";
        String content = message.getAnswer().getMsg().replace("<p>", "").replace("</p>", "").replace("\n", "<br/>");
        if (isLeaveMsg == ZhiChiConstant.sobot_msg_flag_open) {
            content = content + postMsg;
        }
        HtmlTools.getInstance(context).setRichText(remindInfo, content, ResourceUtils.getIdByName
                (context, "color", "sobot_color_link_remind"));
        remindInfo.setEnabled(true);
        message.setShake(false);
    }

    /**
     * @param context
     * @param remindInfo
     */
    private void setRemindToCustom(Context context, TextView remindInfo) {
        String content = context.getResources().getString(R.string.sobot_cant_solve_problem_new);
        String click = "<a href='sobot:SobotToCustomer'> " + context.getResources().getString(R.string.sobot_customer_service) + "</a>";
        HtmlTools.getInstance(context).setRichText(remindInfo, String.format(content,click),ResourceUtils.getIdByName
                (context, "color", "sobot_color_link_remind"));
        remindInfo.setEnabled(true);
    }

    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}
