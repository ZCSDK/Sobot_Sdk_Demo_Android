package com.sobot.demo;

import android.content.Context;
import android.text.TextUtils;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.enumtype.SobotAutoSendMsgMode;
import com.sobot.chat.api.enumtype.SobotChatStatusMode;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.listener.NewHyperlinkListener;
import com.sobot.chat.listener.SobotChatStatusListener;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.sobot.chat.api.apiUtils.SobotApp.getApplicationContext;

/**
 * Created by Administrator on 2017/12/12.
 */

public class SobotUtils {

    private static int enumType = 0;//0 默认,  1  自定义,  2  公司name;
    private static long sobot_show_history_ruler = 0;//显示多少分钟内的历史记录  10分钟 -24小时


    /**
     * @param context
     * @param startType "1" 启动客服界面 ,"2"启动客户中心
     */
    public static void startSobot(Context context, String startType) {
        if (context == null) {
            return;
        }



        //设置用户自定义字段
        Information info = new Information();

        //------用户vip 和用户标签---------
        //指定客户是否为vip，0:普通 1:vip
        info.setIsVip("1");
        //vip级别可在智齿管理端（系统设置>自定义字段>客户字段）中编辑，拿到等级对应的ID
         info.setVip_level("ceweivip");//vip等级
        //用户标签可在智齿管理端（系统设置>自定义字段>客户字段）中编辑，拿到用户标签对应的ID
        //可添加多个对应的用户标签，多个标签ID之间用,分割
        info.setUser_label("ceweilabelwanju,ceweilabelwanju");
        Map<String, String> customerFields = new HashMap<>();
        customerFields.put("weixin", "55555");
        customerFields.put("weibo", "66666");
        customerFields.put("userSex", "女");
        customerFields.put("birthday", "2017-05-17");
        customerFields.put("cardNo", "142201198704102222");
        customerFields.put(SobotSPUtil.getStringData(context, "key1_value", ""), SobotSPUtil.getStringData(context, "value1_value", ""));
        customerFields.put(SobotSPUtil.getStringData(context, "key2_value", ""), SobotSPUtil.getStringData(context, "value2_value", ""));
        customerFields.put(SobotSPUtil.getStringData(context, "key3_value", ""), SobotSPUtil.getStringData(context, "value3_value", ""));

        info.setUseRobotVoice(true);//这个属性默认都是false。想使用需要付费。付费才可以设置为true。
        SobotSPUtil.getStringData(context, "sobot_key1", "");
        SobotSPUtil.getStringData(context, "sobot_key2", "");
        info.setUname(SobotSPUtil.getStringData(context, "person_uName", ""));
        info.setRealname(SobotSPUtil.getStringData(context, "sobot_realname", ""));
        info.setTel(SobotSPUtil.getStringData(context, "sobot_tel", ""));
        info.setEmail(SobotSPUtil.getStringData(context, "sobot_email", ""));
        info.setQq(SobotSPUtil.getStringData(context, "sobot_qq", ""));
        info.setRemark(SobotSPUtil.getStringData(context, "sobot_reMark", ""));
        info.setFace(SobotSPUtil.getStringData(context, "sobot_face", ""));
        info.setVisitTitle(SobotSPUtil.getStringData(context, "sobot_visitTitle", ""));
        info.setVisitUrl(SobotSPUtil.getStringData(context, "sobot_visitUrl", ""));
        Map<String, String> customInfo = new HashMap<>();
        customInfo.put("sobot_key1", SobotSPUtil.getStringData(context, "sobot_key1", ""));
        customInfo.put("sobot_key2", SobotSPUtil.getStringData(context, "sobot_key2", ""));
        info.setCustomInfo(customInfo);
        //用户信息设置结束

        //服务总结自定义字段
        Map<String, String> summaryInfo = new HashMap<>();
        String summary_key1 = SobotSPUtil.getStringData(context, "sobot_summary_key1", "");
        String summary_key2 = SobotSPUtil.getStringData(context, "sobot_summary_key2", "");
        String summary_val1 = SobotSPUtil.getStringData(context, "sobot_summary_val1", "");
        String summary_val2 = SobotSPUtil.getStringData(context, "sobot_summary_val2", "");
        if (!TextUtils.isEmpty(summary_key1)) {
            summaryInfo.put(summary_key1, summary_val1);
        }
        if (!TextUtils.isEmpty(summary_key2)) {
            summaryInfo.put(summary_key2, summary_val2);
        }
        info.setSummaryParams(summaryInfo);

        //多伦会话自定义参数
        info.setMulitParams(SobotSPUtil.getStringData(context, "sobot_mulit_params", null));

        //咨询信息设置开始
        boolean isShow = SobotSPUtil.getBooleanData(context, "sobot_goods_is_show_info", false);
        if (isShow) {
            ConsultingContent consult = new ConsultingContent();
            consult.setSobotGoodsTitle(SobotSPUtil.getStringData(context, "sobot_goods_title_value", ""));
            consult.setSobotGoodsDescribe(SobotSPUtil.getStringData(context, "sobot_goods_describe_value", ""));
            consult.setSobotGoodsLable(SobotSPUtil.getStringData(context, "sobot_goods_lable_value", ""));
            consult.setSobotGoodsImgUrl(SobotSPUtil.getStringData(context, "sobot_goods_imgUrl_value", ""));
            consult.setSobotGoodsFromUrl(SobotSPUtil.getStringData(context, "sobot_goods_fromUrl_value", ""));
            consult.setAutoSend(SobotSPUtil.getBooleanData(context, "sobot_goods_auto_send", false));
            info.setConsultingContent(consult);
        } else {
            info.setConsultingContent(null);
        }

        //初始化电商平台
        SobotApi.initPlatformUnion(getApplicationContext(), "1001", "");
        //flowType -是否溢出到主商户 0-不溢出 , 1-全部溢出，2-忙碌时溢出，3-不在线时溢出,  默认不溢出
        SobotApi.setFlowType(getApplicationContext(), "1");
        //转人工溢出公司技能组id
        SobotApi.setFlowGroupId(getApplicationContext(), "2d2083c2a7ba4503a8280c2f10c7abdf");
        //设置溢出公司id
        SobotApi.setFlowCompanyId(getApplicationContext(), "7f9c02497a9a4423b5d2ada26d666951");

        //设置横竖屏
        SobotApi.setSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN, SobotSPUtil.getBooleanData(context, "sobot_isLandscapeScreen", false));

        if (SobotSPUtil.getBooleanData(context, "isInterceptLink", false)) {
// 链接的点击事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
            // 可为订单号,商品详情地址等等;客户可自定义规则拦截,返回true时会把自定义的信息返回
            // 拦截范围  （帮助中心、留言、聊天、留言记录、商品卡片，订单卡片）
            SobotApi.setNewHyperlinkListener(new NewHyperlinkListener() {
                @Override
                public boolean onUrlClick(String url) {
                    ToastUtil.showToast(getApplicationContext(), "点击了超链接，url=" + url);
                    //如果url链接是百度,拦截
                    //do().....
                    return true;

                }

                @Override
                public boolean onEmailClick(String email) {
                    ToastUtil.showToast(getApplicationContext(), "点击了邮件，email=" + email);
                    return false;
                }

                @Override
                public boolean onPhoneClick(String phone) {
                    ToastUtil.showToast(getApplicationContext(), "点击了电话，phone=" + phone);
                    return false;
                }
            });
        } else {
            SobotApi.setNewHyperlinkListener(null);
        }

        if (SobotSPUtil.getBooleanData(context, "isShowChatStatus", false)) {
            //监听当前聊天状态变化
            SobotApi.setChatStatusListener(new SobotChatStatusListener() {
                @Override
                public void onChatStatusListener(SobotChatStatusMode chatStatusMode) {
                    switch (chatStatusMode) {
                        case ZCServerConnectRobot:
                            ToastUtil.showToast(getApplicationContext(), "机器人聊天模式");
                            break;
                        case ZCServerConnectArtificial:
                            ToastUtil.showToast(getApplicationContext(), "转人工客服聊天模式");
                            break;
                        case ZCServerConnectOffline:
                            ToastUtil.showToast(getApplicationContext(), "已离线");
                            break;
                        case ZCServerConnectWaiting:
                            ToastUtil.showToast(getApplicationContext(), "仅人工排队中");
                            break;
                    }
                }
            });
        } else {
            SobotApi.setChatStatusListener(null);

        }

        //咨询信息设置结束


        //------聊天页面底部加号中功能显示开关 start
        if (SobotSPUtil.getBooleanData(context, "sobot_isShowLeaveMsg", false)) {
            info.setHideMenuLeave(true);
        }
        if (SobotSPUtil.getBooleanData(context, "sobot_isShowSatisfaction", false)) {
            info.setHideMenuSatisfaction(true);
        }
        if (SobotSPUtil.getBooleanData(context, "sobot_isShowPicture", false)) {
            info.setHideMenuPicture(true);
        }
        if (SobotSPUtil.getBooleanData(context, "sobot_isShowVedio", false)) {
            info.setHideMenuVedio(true);
        }
        if (SobotSPUtil.getBooleanData(context, "sobot_isShowCamera", false)) {
            info.setHideMenuCamera(true);
        }
        if (SobotSPUtil.getBooleanData(context, "sobot_isShowFile", false)) {
            info.setHideMenuFile(true);
        }
        //------聊天页面底部加号中功能显示开关 end


        if (SobotSPUtil.getBooleanData(context, "sobot_leave_complete_can_reply", false)) {
            //已完成状态的留言，是否可持续回复 true 持续回复 ，false 不可继续回复 ；默认 true 用户可一直持续回复
            SobotApi.setSwitchMarkStatus(MarkConfig.LEAVE_COMPLETE_CAN_REPLY, false);
        } else {
            SobotApi.setSwitchMarkStatus(MarkConfig.LEAVE_COMPLETE_CAN_REPLY, true);
        }
        if (SobotSPUtil.getBooleanData(context, "sobot_isshowLeaveDetailBackEvaluate", false)) {
            //已完成留言详情界面-未评价：返回时是否弹出服务评价窗口(只会第一次返回弹，下次返回不会再弹) 默认false(不弹)
            info.setShowLeaveDetailBackEvaluate(true);
        }

        //自定义应答设置开始
        SobotApi.setCustomAdminHelloWord(context, SobotSPUtil.getStringData(context, "sobot_customAdminHelloWord", ""));//自定义客服欢迎语,默认为空 （如果传入，优先使用该字段）
        SobotApi.setCustomRobotHelloWord(context, SobotSPUtil.getStringData(context, "sobot_customRobotHelloWord", ""));//自定义机器人欢迎语,默认为空 （如果传入，优先使用该字段）
        SobotApi.setCustomUserTipWord(context, SobotSPUtil.getStringData(context, "sobot_customUserTipWord", ""));//自定义用户超时提示语,默认为空 （如果传入，优先使用该字段）
        SobotApi.setCustomAdminTipWord(context, SobotSPUtil.getStringData(context, "sobot_customAdminTipWord", ""));//自定义客服超时提示语,默认为空 （如果传入，优先使用该字段）
        SobotApi.setCustomAdminNonelineTitle(context, SobotSPUtil.getStringData(context, "sobot_customAdminNonelineTitle", ""));// 自定义客服不在线的说辞,默认为空 （如果传入，优先使用该字段）
        SobotApi.setCustomUserOutWord(context, SobotSPUtil.getStringData(context, "sobot_customUserOutWord", ""));// 自定义用户超时下线提示语,默认为空 （如果传入，优先使用该字段）
        //自定义应答设置结束


        String sobot_custom_language_value = SobotSPUtil.getStringData(context, "sobot_custom_language_value", "");
        if (TextUtils.isEmpty(sobot_custom_language_value)) {
            ZCSobotApi.setInternationalLanguage(context, sobot_custom_language_value, false, false);
            ZCSobotApi.hideTimemsgForMessageList(context, false);
        } else {
            ZCSobotApi.setInternationalLanguage(context, sobot_custom_language_value, true, false);
            ZCSobotApi.hideTimemsgForMessageList(context, true);
        }

        //启动参数设置开始
        info.setUid(SobotSPUtil.getStringData(context, "sobot_partnerId", ""));
        info.setReceptionistId(SobotSPUtil.getStringData(context, "sobot_receptionistId", ""));
        info.setRobotCode(SobotSPUtil.getStringData(context, "sobot_demo_robot_code", ""));
        info.setRobot_alias(SobotSPUtil.getStringData(context, "sobot_demo_robot_alias", ""));
        info.setTranReceptionistFlag(SobotSPUtil.getBooleanData(context, "sobot_receptionistId_must", false) ? 1 : 0);
        info.setCustomerFields(customerFields);
        if (SobotSPUtil.getBooleanData(context, "sobot_isArtificialIntelligence", false)) {
            info.setArtificialIntelligence(true);
            if (!TextUtils.isEmpty(SobotSPUtil.getStringData(context, "sobot_isArtificialIntelligence_num_value", "")) && !"".equals(SobotSPUtil.getStringData(context, "sobot_isArtificialIntelligence_num_value", ""))) {
                info.setArtificialIntelligenceNum(Integer.parseInt(SobotSPUtil.getStringData(context, "sobot_isArtificialIntelligence_num_value", "")));
            }
        } else {
            info.setArtificialIntelligence(false);
        }


        info.setQueueFirst(SobotSPUtil.getBooleanData(context, "sobot_isQueueFirst", false));
        info.setUseVoice(SobotSPUtil.getBooleanData(context, "sobot_isUseVoice", true));

        info.setShowCloseBtn(SobotSPUtil.getBooleanData(context, "sobot_isShow_close", false));
        info.setShowCloseSatisfaction(SobotSPUtil.getBooleanData(context, "sobot_isShowSatisfaction_close", false));
        info.setCanBackWithNotEvaluation(SobotSPUtil.getBooleanData(context, "sobot_canBackWithNotEvaluation", false));
        //聊天主页面返回是是否弹出退出询问框,true显示，false不显示
        info.setShowLeftBackPop(true);
        info.setShowSatisfaction(SobotSPUtil.getBooleanData(context, "sobot_isShowBackSatisfaction", false));

        if (!TextUtils.isEmpty(SobotSPUtil.getStringData(context, "sobot_rg_initModeType", ""))) {
            info.setInitModeType(Integer.parseInt(SobotSPUtil.getStringData(context, "sobot_rg_initModeType", "")));
        }
        info.setSkillSetName(SobotSPUtil.getStringData(context, "sobot_demo_skillname", ""));
        info.setSkillSetId(SobotSPUtil.getStringData(context, "sobot_demo_skillid", ""));
        boolean sobot_isOpenNotification = SobotSPUtil.getBooleanData(context, "sobot_isOpenNotification", false);
        boolean sobot_evaluationCompletedExit = SobotSPUtil.getBooleanData(context, "sobot_evaluationCompletedExit_value", false);
        String sobot_title_vlaue = SobotSPUtil.getStringData(context, "sobot_title_value", "");
        if (!TextUtils.isEmpty(SobotSPUtil.getStringData(context, "sobot_title_value_show_type", ""))) {
            enumType = Integer.parseInt(SobotSPUtil.getStringData(context, "sobot_title_value_show_type", ""));
        }
        if (!TextUtils.isEmpty(SobotSPUtil.getStringData(context, "sobot_show_history_ruler", ""))) {
            sobot_show_history_ruler = Long.parseLong(SobotSPUtil.getStringData(context, "sobot_show_history_ruler", ""));
        }
        String transferKey = SobotSPUtil.getStringData(context, "sobot_transferKeyWord", "");
        if (!TextUtils.isEmpty(transferKey)) {
            String[] transferKeys = transferKey.split(",");
            if (transferKeys.length > 0) {
                HashSet<String> tmpSet = new HashSet<>();
                for (int i = 0; i < transferKeys.length; i++) {
                    tmpSet.add(transferKeys[i]);
                }
                info.setTransferKeyWord(tmpSet);//设置转人工关键字
            }
        }
        //启动参数设置结束

        String ed_hot_question_value = SobotSPUtil.getStringData(context, "ed_hot_question_value", "");
        if (!TextUtils.isEmpty(ed_hot_question_value)) {
            try {
                Map<String, String> questionRecommendParams = new HashMap<>();

                String[] values = ed_hot_question_value.split(",");
                if (values.length > 0) {
                    for (String value1 : values) {
                        String[] value = value1.split(":");
                        if (value.length > 0) {
                            questionRecommendParams.put(value[0], value[1]);
                        }
                    }
                }
                info.setQuestionRecommendParams(questionRecommendParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        String sobot_transfer_action_str = SobotSPUtil.getStringData(context, "sobot_transfer_action", "");
        if (!TextUtils.isEmpty(sobot_transfer_action_str)) {
            info.setTransferAction(sobot_transfer_action_str);
        }

        String appkey = SobotSPUtil.getStringData(context, "sobot_appkey", "");
        if (!TextUtils.isEmpty(appkey)) {
            info.setAppkey(appkey);

            //设置标题显示模式
            SobotApi.setChatTitleDisplayMode(context,
                    SobotChatTitleDisplayMode.values()[enumType], sobot_title_vlaue, true);
            //设置是否开启消息提醒
            SobotApi.setNotificationFlag(context, sobot_isOpenNotification
                    , R.drawable.sobot_demo_logo_small_icon, R.drawable.sobot_demo_logo);
            SobotApi.hideHistoryMsg(context, sobot_show_history_ruler);
            SobotApi.setEvaluationCompletedExit(context, sobot_evaluationCompletedExit);
            // info.setLeaveMsgTemplateContent("123");
            // info.setLeaveMsgGuideContent("356");


            //------会话创建后自动发送消息的模式---------
            String videoPath = CommonUtils.getSDCardRootPath() + File.separator + "1.docx";
//                        String videoPath = CommonUtils.getSDCardRootPath() + File.separator + "2.jpg";
//                        String videoPath = CommonUtils.getSDCardRootPath() + File.separator + "1.txt";
            info.setAutoSendMsgMode(SobotAutoSendMsgMode.SendToOperator.setContent(videoPath).setAuto_send_msgtype(SobotAutoSendMsgMode.ZCMessageTypeVideo));

            //是否显示暂不评价按钮 true显示，false不显示
            SharedPreferencesUtil.saveBooleanData(context, "isShowEvaluateCancel", false);

            //启动聊天界面
            //    SobotApi.startSobotChat(context, info);
            //打开留言界面
            //    SobotApi.startToPostMsgActivty(context,info,false);

            switch (startType) {
                case "1":
                    //启动客服界面
                    ZCSobotApi.openZCChat(context, info);
                    break;
                case "2":
                    //启动客户中心
                    ZCSobotApi.openZCServiceCenter(context, info);
                    break;
            }
//
        } else {
            ToastUtil.showToast(context, "AppKey 不能为空 ！！！");
        }
    }
}