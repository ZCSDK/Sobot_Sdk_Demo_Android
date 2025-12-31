package com.sobot.chat.server;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.enumtype.SobotChatStatusMode;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.NotificationUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.Util;
import com.sobot.chat.utils.ZhiChiConfig;
import com.sobot.chat.utils.ZhiChiConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

/**
 * Created by jinxl on 2016/9/13.
 */
public class SobotSessionServer extends Service {
    private LocalBroadcastManager localBroadcastManager;
    private MyMessageReceiver receiver;
    private MyNetWorkChangeReceiver receiverNet;
    private int tmpNotificationId = 0;
    private String currentUid = "";
    private Information info = null;
    private ZhiChiConfig config = null;
    private boolean isStartTimer = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i2Local("SobotSessionServer onStartCommand", "SobotSessionServer服务启动");
        if (intent != null) {
            currentUid = intent.getStringExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("SobotSessionServer  ---> onCreate");
        initBrocastReceiver();
    }

    /* 初始化广播接受者 */
    private void initBrocastReceiver() {
        if (receiver == null) {
            receiver = new MyMessageReceiver();
        }
        if (receiverNet == null) {
            receiverNet = new MyNetWorkChangeReceiver();
        }
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ZhiChiConstants.receiveMessageBrocast);
        filter.addAction(ZhiChiConstants.SOBOT_TIMER_BROCAST);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        // 注册广播接收器
        localBroadcastManager.registerReceiver(receiver, filter);
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter systemfilter = new IntentFilter();
        systemfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 检测网络的状态
        registerReceiver(receiverNet, systemfilter);
    }

    /**
     * 检测网络状态的广播
     */
    public class MyNetWorkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("MyNetWorkChangeReceiver action=" + intent.getAction());
//            LogUtils.i("广播是  :" + intent.getAction());
            if (context == null || intent == null) {
                return;
            }
            if (!Util.hasNetWork(getApplicationContext())) {

            } else {
                //网络状态发生变化的时候，在wifi下上传log
                /*String appkey = SharedPreferencesUtil.getStringData(context, ZhiChiConstant.SOBOT_CONFIG_APPKEY, null);
                if (IntenetUtil.isWifiConnected(context) && appkey != null) {
                    SobotMsgManager.getInstance(context).getZhiChiApi().logCollect(context, appkey);
                }*/
            }

        }
    }

    public class MyMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ZhiChiConstants.receiveMessageBrocast.equals(intent.getAction())) {

                // 接受下推的消息
                try {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        ZhiChiPushMessage pushMessage = (ZhiChiPushMessage) extras.getSerializable(ZhiChiConstants.ZHICHI_PUSH_MESSAGE);
                        if (pushMessage != null && isNeedShowMessage(pushMessage.getAppId())) {
                            LogUtils.i2Local("收到消息4", "接受到广播（SobotSessionServer）: " + pushMessage.getMsgId());
                            receiveMessage(context, pushMessage);
                        } else {
                            LogUtils.i2Local("收到消息4", "接受到广播（SobotSessionServer）: pushMessage是否为空：" + (pushMessage == null) + "或isNeedShowMessage为true");
                        }
                    }
                } catch (Exception e) {
                    //ignor
                }
            } else if (ZhiChiConstants.SOBOT_TIMER_BROCAST.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    isStartTimer = extras.getBoolean("isStartTimer");
                    if (!isStartTimer) {
                        return;
                    }
                    info = (Information) extras.getSerializable("info");
                    config = SobotMsgManager.getInstance(getApplicationContext()).getConfig(info.getApp_key());
                    if (config.getInitModel() != null) {
                        if (config.customerState == CustomerState.Online) {
                        }
                    }
                }
            }
        }
    }

    private void receiveMessage(Context context, ZhiChiPushMessage pushMessage) {
        if (pushMessage == null) {
            return;
        }
        // 接受下推的消息
        ZhiChiMessageBase base = new ZhiChiMessageBase();
        base.setT(Calendar.getInstance().getTime().getTime() + "");
        base.setSenderName(pushMessage.getAname());
        base.setSenderName(pushMessage.getFace());
        config = SobotMsgManager.getInstance(getApplication()).getConfig(pushMessage.getAppId());
        if (ZhiChiConstant.push_message_createChat == pushMessage.getType()) {
            if (config.getInitModel() != null) {
                config.adminFace = pushMessage.getAface();
                int type = Integer.parseInt(config.getInitModel().getType());
                ZhiChiInitModeBase initModel = config.getInitModel();
                if (initModel != null) {
                    initModel.setAdminHelloWord(!TextUtils.isEmpty(pushMessage.getAdminHelloWord()) ? pushMessage.getAdminHelloWord() : initModel.getAdminHelloWord());
                    initModel.setServiceEndPushMsg(!TextUtils.isEmpty(pushMessage.getServiceEndPushMsg()) ? pushMessage.getServiceEndPushMsg() : initModel.getServiceEndPushMsg());
                    initModel.setAdminTipTime(!TextUtils.isEmpty(pushMessage.getServiceOutTime()) ? pushMessage.getServiceOutTime() : initModel.getAdminTipTime());
                    initModel.setAdminTipWord(!TextUtils.isEmpty(pushMessage.getServiceOutDoc()) ? pushMessage.getServiceOutDoc() : initModel.getAdminTipWord());
                }
                createCustomerService(context, pushMessage.getAppId(), pushMessage.getAname(), pushMessage.getAface(), pushMessage);
            }
        } else if (ZhiChiConstant.push_message_receverNewMessage == pushMessage
                .getType()) {// 接收到新的消息
            if (config.getInitModel() != null) {
                if (config.customerState == CustomerState.Online) {
                    base.setMsgId(pushMessage.getMsgId());
                    base.setSender(pushMessage.getAname());
                    base.setSenderName(pushMessage.getAname());
                    base.setSenderFace(pushMessage.getAface());
                    base.setOrderCardContent(pushMessage.getOrderCardContent());
                    base.setConsultingContent(pushMessage.getConsultingContent());
                    base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                    base.setAnswer(pushMessage.getAnswer());
                    // 更新界面的操作
                    //添加“以下为未读消息”
                    if (config.isShowUnreadUi) {
                        config.addMessage(ChatUtils.getUnreadMode(this));
                        config.isShowUnreadUi = false;
                    }
                    config.addMessage(base);
                    LogUtils.i2Local("收到消息5", "加入到config中 msgId:" + base.getMsgId());
                    if (config.customerState == CustomerState.Online) {
                        config.customTimeTask = false;
                        config.userInfoTimeTask = true;
                    }
                }
            }

            if (isNeedShowMessage(pushMessage.getAppId())) {

                String content;
                int msgType = -1;
                try {
                    JSONObject jsonObject = new JSONObject(pushMessage.getContent());
                    content = jsonObject.optString("msg");
                    msgType = jsonObject.optInt("msgType");
                } catch (JSONException e) {
                    content = "";
                    e.printStackTrace();
                }
                if (msgType != -1 && !TextUtils.isEmpty(content)) {
                    String notificationContent = content;
                    if (msgType == ZhiChiConstant.message_type_textAndPic || msgType ==
                            ZhiChiConstant.message_type_textAndText) {
                        content = ResourceUtils.getResString(this, "sobot_chat_type_rich_text");
                        notificationContent = ResourceUtils.getResString(this, "sobot_receive_new_message");
                    } else if (msgType == ZhiChiConstant.message_type_pic) {
                        content = ResourceUtils.getResString(this, "sobot_upload");
                        notificationContent = ResourceUtils.getResString(this, "sobot_upload");
                    }
                    showNotification(getResString("sobot_receive_new_message"), pushMessage, true);
                    sendBroadcast(pushMessage, getResString("sobot_receive_new_message"), true);
                }
            }
        } else if (ZhiChiConstant.push_message_receverSystemMessage == pushMessage
                .getType()) {// 接收到系统消息
            if (config.getInitModel() != null) {
                base.setT(Calendar.getInstance().getTime().getTime() + "");
                base.setMsgId(pushMessage.getMsgId());
                base.setSender(pushMessage.getAname());
                base.setSenderName(pushMessage.getAname());
                base.setSenderFace(pushMessage.getAface());
                if (!TextUtils.isEmpty(pushMessage.getSysType()) && ("1".equals(pushMessage.getSysType()) || "2".equals(pushMessage.getSysType()) || "5".equals(pushMessage.getSysType()) || "6".equals(pushMessage.getSysType()))) {
                    //客服超时提示 1
                    //客户超时提示 2 都显示在左侧
                    //排队断开说辞系统消息 5 都显示在左侧
                    //排队断开前30秒说辞系统消息 6  都显示在左侧
                    base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                    ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
                    reply.setMsg(pushMessage.getContent());
                    reply.setMsgType(ZhiChiConstant.message_type_text + "");
                    base.setAnswer(reply);
                } else {
                    base.setAction(ZhiChiConstant.message_type_fraud_prevention + "");
                    base.setMsgId(pushMessage.getMsgId());
                    base.setMsg(pushMessage.getContent());
                }
                // 更新界面的操作
                config.addMessage(base);
                if (!TextUtils.isEmpty(pushMessage.getSysType()) && "6".equals(pushMessage.getSysType())) {
                    ZhiChiMessageBase keepQueuingMessageBase = ChatUtils.getKeepQueuingHint(ResourceUtils.getResString(context, "sobot_keep_queuing_string") + "<a href='sobot:SobotKeepQueuing'> " + ResourceUtils.getResString(context, "sobot_keep_queuing") + "</a>");
                    config.addMessage(keepQueuingMessageBase);
                }
                if (config.customerState == CustomerState.Online) {
                    config.customTimeTask = false;
                    config.userInfoTimeTask = true;
                }
                if (isNeedShowMessage(pushMessage.getAppId())) {
                    showNotification(pushMessage.getContent(), pushMessage, false);
                }
                sendBroadcast(pushMessage, pushMessage.getContent(), false);
            }


        } else if (ZhiChiConstant.push_message_paidui == pushMessage.getType()) {
            // 排队的消息类型
            if (config.getInitModel() != null) {
                createCustomerQueue(pushMessage.getAppId(), pushMessage.getCount(), pushMessage.getQueueDoc());
                if (isNeedShowMessage(pushMessage.getAppId())) {
                    showNotification(pushMessage.getQueueDoc(), pushMessage, false);
                }
                sendBroadcast(pushMessage, pushMessage.getQueueDoc(), false);
            }
        } else if (ZhiChiConstant.push_message_outLine == pushMessage.getType()) {// 用户被下线
            if (SobotOption.sobotChatStatusListener != null) {
                //修改聊天状态为离线状态
                SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectOffline);
            }
            // 发送用户被下线的广播
            SobotMsgManager.getInstance(getApplication()).clearAllConfig();
            CommonUtils.sendLocalBroadcast(getApplicationContext(), new Intent(Const.SOBOT_CHAT_USER_OUTLINE));
            if (isNeedShowMessage(pushMessage.getAppId())) {
                showNotification(getResString("sobot_dialogue_finish"), pushMessage, false);
            }
            sendBroadcast(pushMessage, getResString("sobot_dialogue_finish"), false);
        } else if (ZhiChiConstant.push_message_transfer == pushMessage.getType()) {
            if (config.getInitModel() != null) {
                LogUtils.i("用户被转接--->" + pushMessage.getName());
                //替换标题
                config.activityTitle = pushMessage.getName(); // 设置后台推送消息的对象
                config.adminFace = pushMessage.getFace();
                config.currentUserName = pushMessage.getName();
                if (isNeedShowMessage(pushMessage.getAppId())) {
//                    showNotification(ChatUtils.getResString(context.get, "sobot_service_accept_start") + " " + pushMessage.getName() + " " + ChatUtils.getResString(context, "sobot_service_accept_end"), pushMessage, false);
                    showNotification(getResString("sobot_service_accept_start") + " " + pushMessage.getName() + " " + ChatUtils.getResString(context, "sobot_service_accept_end"), pushMessage, false);
                }
                sendBroadcast(pushMessage, getResString("sobot_service_accept_start") + " " + pushMessage.getName() + " " + ChatUtils.getResString(context, "sobot_service_accept_end"), false);
            }
        } else if (ZhiChiConstant.push_message_retracted == pushMessage.getType()) {
            if (config.getInitModel() != null) {
                if (!TextUtils.isEmpty(pushMessage.getRevokeMsgId())) {
                    List<ZhiChiMessageBase> datas = config.getMessageList();
                    if (datas != null && datas.size() > 0) {
                        for (int i = datas.size() - 1; i >= 0; i--) {
                            ZhiChiMessageBase msgData = datas.get(i);
                            if (pushMessage.getRevokeMsgId().equals(msgData.getMsgId())) {
                                msgData.setRetractedMsg(true);
                                break;
                            }
                        }
                    }
                }
            }

        } else if (ZhiChiConstant.push_message_custom_evaluate == pushMessage.getType()) {
            if (config.getInitModel() != null) {
                if (config.isAboveZero && !config.isComment && config.customerState == CustomerState.Online) {
                    // 满足评价条件，并且之前没有评价过的话 才能 弹评价框
                    ZhiChiMessageBase customEvaluateMode = ChatUtils.getCustomEvaluateMode(SobotSessionServer.this, pushMessage);
                    config.addMessage(customEvaluateMode);
                    if (isNeedShowMessage(pushMessage.getAppId())) {
                        showNotification(getResString("sobot_cus_service") + " " + pushMessage.getAname() + " " + getResString("sobot_please_evaluate"), pushMessage, false);
                    }
                    sendBroadcast(pushMessage, getResString("sobot_cus_service") + " " + pushMessage.getAname() + " " + getResString("sobot_please_evaluate"), false);
                }
            }
        } else if (ZhiChiConstant.push_message_user_get_session_lock_msg == pushMessage.getType()) {
            LogUtils.i("SobotSessionServer  ---> push_message_user_get_session_lock_msg---------------" + pushMessage.getLockType());
            if (config.getInitModel() != null) {
                if (config.customerState == CustomerState.Online) {
                    if (1 == pushMessage.getLockType()) {
                        config.isChatLock = 1;
                    } else {
                        config.isChatLock = 2;
                    }
                }
            }
        }


    }

    /**
     * 连接客服时，需要排队
     * 显示排队的处理逻辑
     *
     * @param num      当前排队的位置
     * @param queueDoc 需要显示的排队提示语
     */
    private void createCustomerQueue(String appId, String num, String queueDoc) {
        ZhiChiConfig config = SobotMsgManager.getInstance(getApplication()).getConfig(appId);
        if (config.customerState == CustomerState.Queuing && !TextUtils
                .isEmpty(num) && Integer.parseInt(num) > 0) {
            ZhiChiInitModeBase initModel = config.getInitModel();
            if (initModel == null) {
                return;
            }
            int type = Integer.parseInt(initModel.getType());
            config.queueNum = Integer.parseInt(num);
            if (config.isShowQueueTip) {
                //显示当前排队的位置
                if (!TextUtils.isEmpty(queueDoc)) {
                    config.addMessage(ChatUtils.getInLineHint(queueDoc));
                }
            }

            if (type == ZhiChiConstant.type_custom_only) {
                //显示标题
                config.activityTitle = ChatUtils.getLogicTitle(getApplicationContext(), false, getResString("sobot_in_line"),
                        initModel.getCompanyName());
                config.bottomViewtype = ZhiChiConstant.bottomViewtype_onlycustomer_paidui;
            } else {
                config.activityTitle = ChatUtils.getLogicTitle(getApplicationContext(), false, initModel.getRobotName(),
                        initModel.getCompanyName());
                config.bottomViewtype = ZhiChiConstant.bottomViewtype_paidui;
            }
        }
    }

    /**
     * 建立与客服的对话
     *
     * @param name 客服的名称
     * @param face 客服的头像
     */
    private void createCustomerService(Context context, String appId, String name, String face, ZhiChiPushMessage pushMessage) {
        ZhiChiConfig config = SobotMsgManager.getInstance(getApplication()).getConfig(appId);
        ZhiChiInitModeBase initModel = config.getInitModel();
        if (initModel == null) {
            return;
        }
        //仅机器人模式不用显示人工
        //改变变量
        config.current_client_model = ZhiChiConstant.client_model_customService;
        if (SobotOption.sobotChatStatusListener != null) {
            //修改聊天状态为客服状态
            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectArtificial);
        }
        config.customerState = CustomerState.Online;
        config.isAboveZero = false;
        config.isComment = false;// 转人工时 重置为 未评价
        config.queueNum = 0;
        config.currentUserName = TextUtils.isEmpty(name) ? "" : name;
        //显示被xx客服接入
        config.addMessage(ChatUtils.getServiceAcceptTip(this, name));
        if (isNeedShowMessage(pushMessage.getAppId())) {
            showNotification(getResString("sobot_receive_new_message"), pushMessage, false);
        }
        sendBroadcast(pushMessage, getResString("sobot_service_accept_start") + " " + name + " " + ChatUtils.getResString(context, "sobot_service_accept_end"), false);

        //显示人工欢迎语
        if (initModel.isAdminHelloWordFlag()) {
            String adminHolloWord = ZCSobotApi.getCurrentInfoSetting(getApplicationContext()) != null ? ZCSobotApi.getCurrentInfoSetting(getApplicationContext()).getAdmin_hello_word() : "";
            if (!TextUtils.isEmpty(adminHolloWord)) {
                config.addMessage(ChatUtils.getServiceHelloTip(name, face, adminHolloWord));
            } else {
                config.addMessage(ChatUtils.getServiceHelloTip(name, face, initModel.getAdminHelloWord()));
            }
        }
        //显示标题
        config.activityTitle = ChatUtils.getLogicTitle(getApplicationContext(), false, name,
                initModel.getCompanyName());
        //设置底部键盘
        config.bottomViewtype = ZhiChiConstant.bottomViewtype_customer;

        // 启动计时任务
        config.userInfoTimeTask = true;
        config.customTimeTask = false;
        config.isProcessAutoSendMsg = true;

        // 把机器人回答中的转人工按钮都隐藏掉
        config.hideItemTransferBtn();
    }

    @Override
    public void onDestroy() {
        // 注销广播接收器
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(receiver);
            receiver = null;
        }
        if (receiverNet != null) {
            unregisterReceiver(receiverNet);
            receiverNet = null;
        }
        // 清理引用
        config = null;
        info = null;
        currentUid = null;
        LogUtils.i2Local("SobotSessionServer onDestroy", "SobotSessionServer服务被销毁");
        super.onDestroy();
    }

    public String getResString(String name) {
        return ResourceUtils.getResString(this, name);
//        return getResources().getString(getResStringId(name));
    }

    public int getResStringId(String name) {
        return ResourceUtils.getIdByName(getApplicationContext(), "string", name);
    }

    public void sendBroadcast(ZhiChiPushMessage pushMessage, String content, boolean isAddUnreadNum) {
        int localUnreadNum;
        if (isAddUnreadNum) {
            localUnreadNum = SobotMsgManager.getInstance(getApplicationContext()).addUnreadCount(pushMessage, Calendar.getInstance().getTime().getTime() + "", currentUid);
        } else {
            localUnreadNum = SobotMsgManager.getInstance(getApplicationContext()).getUnreadCount(pushMessage.getAppId(), false, currentUid);
        }
        Intent intent = new Intent();
        intent.setAction(ZhiChiConstant.sobot_unreadCountBrocast);
        intent.putExtra("noReadCount", localUnreadNum);
        intent.putExtra("content", content);
        intent.putExtra("sobot_appId", pushMessage.getAppId());
        Bundle bundle = new Bundle();
        bundle.putSerializable("sobotMessage", pushMessage);
        intent.putExtras(bundle);
        CommonUtils.sendBroadcast(getApplicationContext(), intent);
    }

    /**
     * 显示通知栏
     *
     * @param content
     */
    private void showNotification(String content, ZhiChiPushMessage pushMessage, boolean isShowKefuName) {
        boolean notification_flag = SharedPreferencesUtil.getBooleanData(getApplicationContext(), Const
                .SOBOT_NOTIFICATION_FLAG, false);

        if (notification_flag) {
            String notificationTitle = getResString("sobot_notification_tip_title");
            String contentTmp;
            if (!TextUtils.isEmpty(pushMessage.getAname()) && isShowKefuName) {
                contentTmp = getResString("sobot_cus_service") + " " + pushMessage.getAname() + "：" + content;
            } else {
                contentTmp = content;
            }
            NotificationUtils.createNotification(getApplicationContext(), notificationTitle, contentTmp, content, getNotificationId(), pushMessage);
        }
    }


    /**
     * 获取通知的id  如果id涨到了999那么重置为0，从1开始发送
     *
     * @return
     */
    private int getNotificationId() {
        if (tmpNotificationId == 999) {
            tmpNotificationId = 0;
        }
        tmpNotificationId++;
        return tmpNotificationId;
    }

    private boolean isNeedShowMessage(String appkey) {
        String currentAppid = SharedPreferencesUtil.getStringData(getApplicationContext(), ZhiChiConstant.SOBOT_CURRENT_IM_APPID, "");
        String currentClassName = "";
        try {
            if (MyApplication.getInstance().getLastActivity() != null) {
                currentClassName = MyApplication.getInstance().getLastActivity().getComponentName().getClassName();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return !currentAppid.equals(appkey) || (!currentClassName.contains(
                "SobotChatActivity") || CommonUtils.isScreenLock(getApplicationContext()));
    }

}