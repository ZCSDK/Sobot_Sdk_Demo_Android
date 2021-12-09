package com.sobot.chat.conversation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.SobotQueryFromActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.apiUtils.SobotVerControl;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.enumtype.SobotAutoSendMsgMode;
import com.sobot.chat.api.model.BaseCode;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.api.model.SobotLocationModel;
import com.sobot.chat.api.model.SobotQueryFormModel;
import com.sobot.chat.api.model.SobotQuestionRecommend;
import com.sobot.chat.api.model.SobotUserTicketInfoFlag;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.camera.util.FileUtil;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.LimitQueue;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.fragment.SobotBaseFragment;
import com.sobot.chat.notchlib.INotchScreen;
import com.sobot.chat.notchlib.NotchScreenManager;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.FileOpenHelper;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.MD5Util;
import com.sobot.chat.utils.NotificationUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.Util;
import com.sobot.chat.utils.ZhiChiConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Created by jinxl on 2018/2/9.
 */
public abstract class SobotChatBaseFragment extends SobotBaseFragment implements SensorEventListener {

    protected Context mAppContext;

    protected SobotMsgAdapter messageAdapter;

    //消息发送状态
    protected static final int SEND_VOICE = 0;
    protected static final int UPDATE_VOICE = 1;
    protected static final int CANCEL_VOICE = 2;
    protected static final int SEND_TEXT = 0;
    protected static final int UPDATE_TEXT = 1;
    protected static final int UPDATE_TEXT_VOICE = 2;

    //当前客户端模式
    protected int current_client_model = ZhiChiConstant.client_model_robot;
    //客服在线状态
    protected CustomerState customerState = CustomerState.Offline;
    protected ZhiChiInitModeBase initModel;/*初始化成功服务器返回的实体对象*/
    protected Information info;

    protected String currentUserName;
    private String adminFace = "";

    protected boolean isAboveZero = false;//是否咨询过（给机器人或这客服发过消息）
    protected int remindRobotMessageTimes = 0;//机器人的提醒次数
    protected boolean isRemindTicketInfo;//是否已经进行过工单状态提醒

    //
    //防止询前表单接口重复执行
    private boolean isQueryFroming = false;
    //防止重复弹出询前表单
    protected boolean isHasRequestQueryFrom = false;

    //定时器
    protected boolean customTimeTask = false;
    protected boolean userInfoTimeTask = false;
    protected boolean is_startCustomTimerTask = false;
    protected int noReplyTimeUserInfo = 0; // 用户已经无应答的时间
    public int paseReplyTimeUserInfo = 0; // 会话返回山歌界面时  用户计时器暂停时间

    //会话是否锁定 0:默认未收到消息  1: true 锁定；2: false 解锁
    protected int isChatLock = 0;

    private Timer timerUserInfo;
    private TimerTask taskUserInfo;
    /**
     * 客服的定时任务
     */
    protected Timer timerCustom;
    protected TimerTask taskCustom;
    protected int noReplyTimeCustoms = 0;// 客服无应答的时间
    public int paseReplyTimeCustoms = 0;// 会话返回山歌界面时 客服计时器暂停时间
    protected int serviceOutTimeTipCount = 0; // 客服无应答超时提醒次数


    //正在输入监听
    private Timer inputtingListener = null;//用于监听正在输入的计时器
    private boolean isSendInput = false;//防止同时发送正在输入
    private String lastInputStr = "";
    private TimerTask inputTimerTask = null;

    //语音相关
    // 听筒模式转换
    private AudioManager audioManager = null; // 声音管理器
    private SensorManager _sensorManager = null; // 传感器管理器
    private Sensor mProximiny = null; // 传感器实例


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppContext = getContext().getApplicationContext();
        initAudioManager();
        if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && SobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH)) {
            // 支持显示到刘海区域
            NotchScreenManager.getInstance().setDisplayInNotch(getActivity());
            // 设置Activity全屏
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void displayInNotch(final View view) {
        if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && SobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH) && view != null) {
            // 获取刘海屏信息
            NotchScreenManager.getInstance().getNotchInfo(getActivity(), new INotchScreen.NotchScreenCallback() {
                @Override
                public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                    if (notchScreenInfo.hasNotch) {
                        for (Rect rect : notchScreenInfo.notchRects) {
                            if (view instanceof WebView && view.getParent() instanceof LinearLayout) {
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.rightMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                layoutParams.leftMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                view.setLayoutParams(layoutParams);
                            } else if (view instanceof WebView && view.getParent() instanceof RelativeLayout) {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.rightMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                layoutParams.leftMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                view.setLayoutParams(layoutParams);
                            } else {
                                view.setPadding((rect.right > 110 ? 110 : rect.right) + view.getPaddingLeft(), view.getPaddingTop(), (rect.right > 110 ? 110 : rect.right) + view.getPaddingRight(), view.getPaddingBottom());
                            }
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (initModel != null && customerState == CustomerState.Online && current_client_model == ZhiChiConstant
                .client_model_customService) {
            restartInputListener();
            CommonUtils.sendLocalBroadcast(mAppContext, new Intent(Const.SOBOT_CHAT_CHECK_CONNCHANNEL));
        }
        NotificationUtils.cancleAllNotification(mAppContext);
        //重新恢复连接
        if (customerState == CustomerState.Online || customerState == CustomerState.Queuing) {
            zhiChiApi.reconnectChannel();
        }

        if (_sensorManager != null) {
            _sensorManager.registerListener(this, mProximiny, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        stopInputListener();
        // 取消注册传感器
        _sensorManager.unregisterListener(this);
        super.onPause();
    }

    protected void finish() {
        if (isActive() && getSobotActivity() != null) {
            getSobotActivity().finish();
        }
    }

    /**
     * fragment是否有效
     *
     * @return
     */
    protected boolean isActive() {
        return isAdded();
    }

    /**
     * 用户的定时任务的处理
     */
    public void startUserInfoTimeTask(final Handler handler) {
        LogUtils.i("--->  startUserInfoTimeTask=====" + isChatLock);
        if (isChatLock == 1) {
            return;
        }
        if (current_client_model == ZhiChiConstant.client_model_customService) {
            if (initModel.isCustomOutTimeFlag()) {
                stopUserInfoTimeTask();
                userInfoTimeTask = true;
                timerUserInfo = new Timer();
                taskUserInfo = new TimerTask() {
                    @Override
                    public void run() {
                        // 需要做的事:发送消息
                        //sendHandlerUserInfoTimeTaskMessage(handler);
                    }
                };
                timerUserInfo.schedule(taskUserInfo, 1000, 1000);
            }
        }
    }

    public void stopUserInfoTimeTask() {
        userInfoTimeTask = false;
        if (timerUserInfo != null) {
            timerUserInfo.cancel();
            timerUserInfo = null;
        }
        if (taskUserInfo != null) {
            taskUserInfo.cancel();
            taskUserInfo = null;
        }
        noReplyTimeUserInfo = 0;

    }

    /**
     * 设置定时任务
     */
    public void setTimeTaskMethod(Handler handler) {
        if (customerState == CustomerState.Online) {
            //LogUtils.i(" 定时任务的计时的操作：" + current_client_model);
            // 断开我的计时任务
            if (current_client_model == ZhiChiConstant.client_model_customService) {
                if (!is_startCustomTimerTask) {
                    stopUserInfoTimeTask();
                    startCustomTimeTask(handler);
                }
            }
        } else {
            stopCustomTimeTask();
            stopUserInfoTimeTask();
        }
    }

    public void restartMyTimeTask(Handler handler) {
        if (customerState == CustomerState.Online) {
            // 断开我的计时任务
            if (current_client_model == ZhiChiConstant.client_model_customService) {
                if (!is_startCustomTimerTask) {
                    stopUserInfoTimeTask();
                    startCustomTimeTask(handler);
                }
            }
        }
    }

    /**
     * 客服的定时处理
     */
    public void startCustomTimeTask(final Handler handler) {
        if (isChatLock == 1) {
            return;
        }
        if (current_client_model == ZhiChiConstant.client_model_customService) {
            if (initModel.isServiceOutTimeFlag()) {
                if (initModel.isServiceOutCountRule() && serviceOutTimeTipCount >= 1) {
                    //开启 客服超时推送规则，只显示一次 时并且已经提醒过一次 屏蔽之后的操作
                    stopCustomTimeTask();
                    return;
                }
                if (!is_startCustomTimerTask) {
                    stopCustomTimeTask();
                    customTimeTask = true;
                    is_startCustomTimerTask = true;
                    timerCustom = new Timer();
                    taskCustom = new TimerTask() {
                        @Override
                        public void run() {
                            // 需要做的事:发送消息
                            //sendHandlerCustomTimeTaskMessage(handler);
                        }
                    };
                    timerCustom.schedule(taskCustom, 1000, 1000);
                }
            }
        }
    }

    public void stopCustomTimeTask() {
        customTimeTask = false;
        is_startCustomTimerTask = false;
        if (timerCustom != null) {
            timerCustom.cancel();
            timerCustom = null;
        }
        if (taskCustom != null) {
            taskCustom.cancel();
            taskCustom = null;
        }
        noReplyTimeCustoms = 0;

    }


    // ##################### 更新界面的ui ###############################

    /**
     * handler 消息实体message 更新ui界面
     *
     * @param messageAdapter
     * @param msg
     */
    protected void updateUiMessage(SobotMsgAdapter messageAdapter, Message msg) {
        ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
        updateUiMessage(messageAdapter, myMessage);
    }

    protected void updateMessageStatus(SobotMsgAdapter messageAdapter, Message msg) {
        ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
        messageAdapter.updateDataStateById(myMessage.getId(), myMessage);
        messageAdapter.notifyDataSetChanged();
    }

    protected void updateVoiceStatusMessage(SobotMsgAdapter messageAdapter, Message msg) {
        ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
        messageAdapter.updateVoiceStatusById(myMessage.getId(),
                myMessage.getSendSuccessState(), myMessage.getAnswer().getDuration());
        messageAdapter.notifyDataSetChanged();
    }

    protected void cancelUiVoiceMessage(SobotMsgAdapter messageAdapter, Message msg) {
        ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
        messageAdapter.cancelVoiceUiById(myMessage.getId());
        messageAdapter.notifyDataSetChanged();
    }

    /**
     * 通过消息实体 zhiChiMessage进行封装
     *
     * @param messageAdapter
     * @param zhichiMessage
     */
    protected void updateUiMessage(SobotMsgAdapter messageAdapter, ZhiChiMessageBase zhichiMessage) {
        messageAdapter.addData(zhichiMessage);
        messageAdapter.notifyDataSetChanged();
    }

    /**
     * 通过消息实体 zhiChiMessage进行封装
     *
     * @param messageAdapter
     * @param zhichiMessage
     */
    protected void updateUiMessageBefore(SobotMsgAdapter messageAdapter, ZhiChiMessageBase zhichiMessage) {
        messageAdapter.addDataBefore(zhichiMessage);
        messageAdapter.notifyDataSetChanged();
    }

    /**
     * @param messageAdapter
     * @param id
     * @param status
     * @param progressBar
     */
    protected void updateUiMessageStatus(SobotMsgAdapter messageAdapter,
                                         String id, int status, int progressBar) {
        messageAdapter.updateMsgInfoById(id, status, progressBar);
        messageAdapter.notifyDataSetChanged();
    }

    // ##################### 更新界面的ui ###############################

    protected String getAdminFace() {
        return this.adminFace;
    }

    protected void setAdminFace(String str) {
        LogUtils.i("头像地址是" + str);
        this.adminFace = str;
    }

    /**
     * @param context
     * @param initModel
     * @param handler
     * @param current_client_model
     */
    protected void sendMessageWithLogic(String msgId, String context,
                                        ZhiChiInitModeBase initModel, final Handler handler, int current_client_model, int questionFlag, String question) {
        if (ZhiChiConstant.client_model_robot == current_client_model) { // 客户和机械人进行聊天
            sendHttpRobotMessage(msgId, context, initModel.getPartnerid(),
                    initModel.getCid(), handler, questionFlag, question, info.getLocale());
            LogUtils.i("机器人模式");
        } else if (ZhiChiConstant.client_model_customService == current_client_model) {
            sendHttpCustomServiceMessage(context, initModel.getPartnerid(),
                    initModel.getCid(), handler, msgId);
            LogUtils.i("客服模式");
        }
    }

    // 人与机械人进行聊天
    protected void sendHttpRobotMessage(final String msgId, String requestText,
                                        String uid, String cid, final Handler handler, int questionFlag, String question, String serverInternationalLanguage) {
        Map<String, String> params = new HashMap<>();
        params.put("adminId", info.getChoose_adminid());//指定客服
        params.put("tranFlag", info.getTranReceptionistFlag() + "");//是否必转该指定客服
        params.put("groupId", info.getGroupid());//指定技能组
        params.put("transferAction", info.getTransferAction());//指定溢出策略
        if (SobotVerControl.isPlatformVer) {
            String flowCompanyId = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_FLOW_COMPANYID, "");
            if (!TextUtils.isEmpty(flowCompanyId)) {
                //是否可以溢出
                String flowType = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_FLOW_TYPE, "");
                // 是否溢出到主商户 0-不溢出 , 1-全部溢出，2-忙碌时溢出，3-不在线时溢出,默认不溢出
                params.put("flowType", flowType);
                //溢出公司id
                params.put("flowCompanyId", flowCompanyId);
                String flowGroupId = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_FLOW_GROUPID, "");
                //溢出groupid
                params.put("flowGroupId", flowGroupId);
            }
        }

        zhiChiApi.chatSendMsgToRoot(initModel.getRobotid(), requestText, questionFlag, question, uid, cid, params,
                new StringResultCallBack<ZhiChiMessageBase>() {
                    @Override
                    public void onSuccess(ZhiChiMessageBase simpleMessage) {
                        if (!isActive()) {
                            return;
                        }
                        if (simpleMessage != null && simpleMessage.getSentisive() == 1) {
                            isAboveZero = true;
                            sendTextMessageToHandler(msgId, null, handler, 1, UPDATE_TEXT, simpleMessage.getSentisive(), simpleMessage.getSentisiveExplain());
                        } else {
                            String id = System.currentTimeMillis() + "";
                            if (simpleMessage.getUstatus() == ZhiChiConstant.result_fail_code) {
                                sendTextMessageToHandler(msgId, null, handler, 0, UPDATE_TEXT);
                                simpleMessage.setId(id);
                                simpleMessage.setSenderName(initModel.getRobotName());
                                simpleMessage.setSender(initModel.getRobotName());
                                simpleMessage.setSenderFace(initModel.getRobotLogo());
                                simpleMessage.setSenderType(ZhiChiConstant.message_sender_type_robot + "");
                                if (messageAdapter != null) {
                                    messageAdapter.justAddData(simpleMessage);
                                    messageAdapter.notifyDataSetChanged();
                                }

                                //机器人超时下线
                                customerServiceOffline(initModel, 4);

                            } else if (simpleMessage.getUstatus() == 1) {
                                // 发送失败
                                sendTextMessageToHandler(msgId, null, handler, 0, UPDATE_TEXT);
                                LogUtils.i("应该是人工状态给机器人发消息拦截,连接通道，修改当前模式为人工模式");
                                ZCSobotApi.checkIMConnected(getSobotActivity(), info.getPartnerid());
                                current_client_model = ZhiChiConstant.client_model_customService;
                            } else {
                                // 机械人的回答语
                                sendTextMessageToHandler(msgId, null, handler, 1, UPDATE_TEXT);
                                isAboveZero = true;
                                simpleMessage.setId(id);
                                simpleMessage.setSenderName(initModel.getRobotName());
                                simpleMessage.setSender(initModel.getRobotName());
                                simpleMessage.setSenderFace(initModel.getRobotLogo());
                                simpleMessage.setSenderType(ZhiChiConstant.message_sender_type_robot + "");
                                Message message = handler.obtainMessage();
                                message.what = ZhiChiConstant.hander_robot_message;
                                message.obj = simpleMessage;
                                handler.sendMessage(message);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        if (!isActive()) {
                            return;
                        }
                        //LogUtils.e("sendHttpRobotMessage:e= " +e.toString() + des);
                        // 显示信息发送失败
                        sendTextMessageToHandler(msgId, null, handler, 0, UPDATE_TEXT);
                    }
                });
    }

    protected void sendHttpCustomServiceMessage(final String content, final String uid,
                                                String cid, final Handler handler, final String mid) {
        zhiChiApi.sendMsgToCoutom(content, uid, cid, new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase commonModelBase) {
                if (!isActive()) {
                    return;
                }
                Boolean switchFlag = Boolean.valueOf(commonModelBase.getSwitchFlag()).booleanValue();
                //如果switchFlag 为true，就会断开通道走轮训
                if (switchFlag) {
                    //不管是什么方式（service 还是定时器里边的轮询），至少先执行一次轮询接口
                    pollingMsgForOne();
                    if (!CommonUtils.isServiceWork(getSobotActivity(), "com.sobot.chat.core.channel.SobotTCPServer")) {
                        SobotMsgManager.getInstance(getSobotActivity()).getZhiChiApi().disconnChannel();
                        //SobotTCPServer不存在，直接走定时器轮训
                        if (!inPolling) {
                            startPolling();
                        }
                    } else {
                        // SobotTCPServer存在，通过广播方式告知要切换轮询
                        CommonUtils.sendLocalBroadcast(mAppContext, new Intent(Const.SOBOT_CHAT_CHECK_SWITCHFLAG));
                    }
                } else {
                    CommonUtils.sendLocalBroadcast(mAppContext, new Intent(Const.SOBOT_CHAT_CHECK_CONNCHANNEL));
                }
                if (commonModelBase.getSentisive() == 1) {
                    isAboveZero = true;
                    sendTextMessageToHandler(mid, null, handler, 1, UPDATE_TEXT, commonModelBase.getSentisive(), commonModelBase.getSentisiveExplain());
                } else {
                    if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(commonModelBase.getStatus())) {
                        sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
                        customerServiceOffline(initModel, 1);
                    } else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(commonModelBase.getStatus())) {
                        if (!TextUtils.isEmpty(mid)) {
                            isAboveZero = true;
                            // 当发送成功的时候更新ui界面
                            sendTextMessageToHandler(mid, null, handler, 1, UPDATE_TEXT);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                //LogUtils.e("sendHttpCustomServiceMessage:e= " + e.toString());
                sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
            }
        });
    }

    /**
     * 发送卡片信息
     *
     * @param consultingContent
     * @param uid
     * @param cid
     * @param handler
     * @param mid
     */
    protected void sendHttpCardMsg(final String uid,
                                   String cid, final Handler handler, final String mid, final ConsultingContent consultingContent) {
        zhiChiApi.sendCardMsg(consultingContent, uid, cid, new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase commonModelBase) {
                if (!isActive()) {
                    return;
                }
                if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(commonModelBase.getStatus())) {
//                    sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
                    customerServiceOffline(initModel, 1);
                } else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(commonModelBase.getStatus())) {
                    if (!TextUtils.isEmpty(mid)) {
                        isAboveZero = true;
                        // 当发送成功的时候更新ui界面
                        ZhiChiMessageBase myMessage = new ZhiChiMessageBase();
                        myMessage.setId(mid);
                        myMessage.setConsultingContent(consultingContent);
                        myMessage.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
                        myMessage.setSendSuccessState(1);
                        ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
                        answer.setMsgType(ZhiChiConstant.message_type_card + "");
                        myMessage.setAnswer(answer);
                        myMessage.setT(Calendar.getInstance().getTime().getTime() + "");
                        Message handMyMessage = handler.obtainMessage();
                        handMyMessage.what = ZhiChiConstant.hander_send_msg;
                        handMyMessage.obj = myMessage;
                        handler.sendMessage(handMyMessage);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                final Map<String, String> map = new HashMap<>();
                String cotent = e.toString() + des;
                map.put("sendHttpCardMsg", cotent);
                LogUtils.i2Local(map, LogUtils.LOGTYPE_ERROE);
                LogUtils.i("sendHttpCardMsg error:" + e.toString());
//                sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
            }
        });
    }

    /**
     * 发送订单卡片信息
     *
     * @param orderCardContent
     * @param uid
     * @param cid
     * @param handler
     * @param mid
     */
    protected void sendHttpOrderCardMsg(final String uid,
                                        String cid, final Handler handler, final String mid, final OrderCardContentModel orderCardContent) {
        zhiChiApi.sendOrderCardMsg(orderCardContent, uid, cid, new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase commonModelBase) {
                if (!isActive()) {
                    return;
                }
                if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(commonModelBase.getStatus())) {
//                    sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
                    customerServiceOffline(initModel, 1);
                } else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(commonModelBase.getStatus())) {
                    if (!TextUtils.isEmpty(mid)) {
                        isAboveZero = true;
                        // 当发送成功的时候更新ui界面
                        ZhiChiMessageBase myMessage = new ZhiChiMessageBase();
                        myMessage.setId(mid);
                        myMessage.setOrderCardContent(orderCardContent);
                        myMessage.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
                        myMessage.setSendSuccessState(1);
                        ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
                        answer.setMsgType(ZhiChiConstant.message_type_ordercard + "");
                        myMessage.setAnswer(answer);
                        myMessage.setT(Calendar.getInstance().getTime().getTime() + "");
                        Message handMyMessage = handler.obtainMessage();
                        handMyMessage.what = ZhiChiConstant.hander_send_msg;
                        handMyMessage.obj = myMessage;
                        handler.sendMessage(handMyMessage);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                final Map<String, String> map = new HashMap<>();
                String cotent = e.toString() + des;
                map.put("sendHttpOrderCardMsg", cotent);
                LogUtils.i2Local(map, LogUtils.LOGTYPE_ERROE);
                LogUtils.i("sendHttpOrderCardMsg error:" + e.toString());
//                sendTextMessageToHandler(mid, null, handler, 0, UPDATE_TEXT);
            }
        });
    }


    protected void uploadFile(File selectedFile, Handler handler, final ListView lv_message,
                              final SobotMsgAdapter messageAdapter, boolean isCamera) {
        if (selectedFile != null && selectedFile.exists()) {
            // 发送文件
            LogUtils.i(selectedFile.toString());
            String fileName = selectedFile.getName().toLowerCase();
            if (fileName.endsWith(".gif") || fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                ChatUtils.sendPicLimitBySize(selectedFile.getAbsolutePath(), initModel.getCid(),
                        initModel.getPartnerid(), handler, mAppContext, lv_message, messageAdapter, isCamera);
            } else {
                if (selectedFile.length() > 50 * 1024 * 1024) {
                    ToastUtil.showToast(getContext(), getResString("sobot_file_upload_failed"));
                    return;
                }
                //不能上传可执行文件 （.exe、.sys、 .com、.bat、.dll、.sh、.py）
                if (FileOpenHelper.checkEndsWithInStringArray(fileName, getContext(), "sobot_fileEndingAll")) {
                    return;
                }
                String tmpMsgId = String.valueOf(System.currentTimeMillis());
                LogUtils.i("tmpMsgId:" + tmpMsgId);
                zhiChiApi.addUploadFileTask(false, tmpMsgId, initModel.getPartnerid(), initModel.getCid(), selectedFile.getAbsolutePath(), null);
                updateUiMessage(messageAdapter, ChatUtils.getUploadFileModel(getContext(), tmpMsgId, selectedFile));
                isAboveZero = true;
            }
        }
    }

    protected void sendLocation(String msgId, SobotLocationModel data, final Handler handler, boolean isNewMsg) {
        if (!isActive() || initModel != null
                && current_client_model != ZhiChiConstant.client_model_customService) {
            return;
        }
        if (isNewMsg) {
            msgId = System.currentTimeMillis() + "";
            sendNewMsgToHandler(ChatUtils.getLocationModel(msgId, data), handler, ZhiChiConstant.MSG_SEND_STATUS_LOADING);
        } else {
            if (TextUtils.isEmpty(msgId)) {
                return;
            }
            updateMsgToHandler(msgId, handler, ZhiChiConstant.MSG_SEND_STATUS_LOADING);
        }
        final String finalMsgId = msgId;
        zhiChiApi.sendLocation(SobotChatBaseFragment.this, data, initModel.getPartnerid(), initModel.getCid(), new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase commonModelBase) {
                if (!isActive()) {
                    return;
                }
                if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(commonModelBase.getStatus())) {
                    updateMsgToHandler(finalMsgId, handler, ZhiChiConstant.MSG_SEND_STATUS_ERROR);
                    customerServiceOffline(initModel, 1);
                } else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(commonModelBase.getStatus())) {
                    if (!TextUtils.isEmpty(finalMsgId)) {
                        isAboveZero = true;
                        updateMsgToHandler(finalMsgId, handler, ZhiChiConstant.MSG_SEND_STATUS_SUCCESS);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                updateMsgToHandler(finalMsgId, handler, ZhiChiConstant.MSG_SEND_STATUS_ERROR);
            }
        });

    }

    protected void uploadVideo(File videoFile, Uri fileUri, final SobotMsgAdapter messageAdapter) {
        String tmpMsgId = String.valueOf(System.currentTimeMillis());
        LogUtils.i("tmpMsgId:" + tmpMsgId);
        String fName = MD5Util.encode(videoFile.getAbsolutePath());
        String filePath = null;
        try {
            filePath = FileUtil.saveImageFile(getSobotActivity(), fileUri, fName + FileUtil.getFileEndWith(videoFile.getAbsolutePath()), videoFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(getSobotActivity(), ResourceUtils.getResString(getSobotActivity(), "sobot_pic_type_error"));
            return;
        }
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(filePath);//path 本地视频的路径
        Bitmap bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        String snapshotPath = "";
        if (bitmap != null) {
            snapshotPath = FileUtil.saveBitmap(100, bitmap);
        }

        zhiChiApi.addUploadFileTask(true, tmpMsgId, initModel.getPartnerid(), initModel.getCid(), filePath, snapshotPath);
        updateUiMessage(messageAdapter, ChatUtils.getUploadVideoModel(getContext(), tmpMsgId, new File(filePath), snapshotPath));
        isAboveZero = true;
    }

    /**
     * 文本通知
     *
     * @param id
     * @param msgContent
     * @param handler
     * @param isSendStatus 0 失败  1成功  2 正在发送
     * @param updateStatus
     */
    protected void sendTextMessageToHandler(String id, String msgContent,
                                            Handler handler, int isSendStatus, int updateStatus) {
        sendTextMessageToHandler(id, msgContent,
                handler, isSendStatus, updateStatus, 0, "");
    }

    /**
     * 文本通知
     *
     * @param id
     * @param msgContent
     * @param handler
     * @param isSendStatus 0 失败  1成功  2 正在发送
     * @param updateStatus
     */
    protected void sendTextMessageToHandler(String id, String msgContent,
                                            Handler handler, int isSendStatus, int updateStatus, int sentisive, String sentisiveExplain) {
        ZhiChiMessageBase myMessage = new ZhiChiMessageBase();
        myMessage.setId(id);
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        if (!TextUtils.isEmpty(msgContent)) {
            msgContent = msgContent.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace
                    ("\n", "<br/>").replace("&lt;br/&gt;", "<br/>");
            reply.setMsg(msgContent);
        } else {
            reply.setMsg(msgContent);
        }
        reply.setMsgType(ZhiChiConstant.message_type_text + "");
        myMessage.setAnswer(reply);
        myMessage.setSenderName(info.getUser_nick());
        myMessage.setSenderFace(info.getFace());
        myMessage.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
        myMessage.setSendSuccessState(isSendStatus);
        myMessage.setT(Calendar.getInstance().getTime().getTime() + "");
        myMessage.setSentisive(sentisive);
        myMessage.setSentisiveExplain(sentisiveExplain);
        Message handMyMessage = handler.obtainMessage();
        switch (updateStatus) {
            case SEND_TEXT:
                handMyMessage.what = ZhiChiConstant.hander_send_msg;
                break;
            case UPDATE_TEXT:
                handMyMessage.what = ZhiChiConstant.hander_update_msg_status;
                break;
            case UPDATE_TEXT_VOICE:
                handMyMessage.what = ZhiChiConstant.update_send_data;
                break;
        }

        handMyMessage.obj = myMessage;
        handler.sendMessage(handMyMessage);
    }

    /**
     * 新增消息
     *
     * @param messageData
     * @param handler
     * @param updateStatus ZhiChiConstant.MSG_SEND_STATUS_SUCCESS
     *                     ZhiChiConstant.MSG_SEND_STATUS_LOADING
     *                     ZhiChiConstant.MSG_SEND_STATUS_ERROR
     */
    protected void sendNewMsgToHandler(ZhiChiMessageBase messageData, Handler handler, int updateStatus) {
        if (messageData == null) {
            return;
        }

        Message message = handler.obtainMessage();
        messageData.setSendSuccessState(updateStatus);
        message.what = ZhiChiConstant.hander_send_msg;
        message.obj = messageData;
        handler.sendMessage(message);
    }

    /**
     * 更新消息状态
     *
     * @param id
     * @param handler
     * @param updateStatus ZhiChiConstant.MSG_SEND_STATUS_SUCCESS
     *                     ZhiChiConstant.MSG_SEND_STATUS_LOADING
     *                     ZhiChiConstant.MSG_SEND_STATUS_ERROR
     */
    protected void updateMsgToHandler(String id, Handler handler, int updateStatus) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        ZhiChiMessageBase messageData = new ZhiChiMessageBase();
        messageData.setId(id);
        messageData.setSendSuccessState(updateStatus);
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.hander_update_msg_status;
        message.obj = messageData;
        handler.sendMessage(message);
    }

    /**
     * 发送语音消息
     *
     * @param voiceMsgId
     * @param voiceTimeLongStr
     * @param cid
     * @param uid
     * @param filePath
     * @param handler
     */
    protected void sendVoice(final String voiceMsgId, final String voiceTimeLongStr,
                             String cid, String uid, final String filePath, final Handler handler) {
        if (current_client_model == ZhiChiConstant.client_model_robot) {
            zhiChiApi.sendVoiceToRobot(filePath, uid, cid, initModel.getRobotid(), voiceTimeLongStr, new ResultCallBack<ZhiChiMessage>() {
                @Override
                public void onSuccess(ZhiChiMessage zhiChiMessage) {
                    if (!isActive()) {
                        return;
                    }
                    LogUtils.i("发送给机器人语音---sobot---" + zhiChiMessage.getMsg());
                    // 语音发送成功
                    String id = System.currentTimeMillis() + "";
                    isAboveZero = true;
                    restartMyTimeTask(handler);
                    if (!TextUtils.isEmpty(zhiChiMessage.getMsg())) {
                        sendTextMessageToHandler(voiceMsgId, zhiChiMessage.getMsg(), handler, 1, UPDATE_TEXT_VOICE);//语音通过服务器转为文字，发送给页面
                    } else {
                        sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 1, UPDATE_VOICE, handler);
                    }

                    ZhiChiMessageBase simpleMessage = zhiChiMessage.getData();
                    if (simpleMessage.getUstatus() == ZhiChiConstant.result_fail_code) {
                        //机器人超时下线
                        customerServiceOffline(initModel, 4);
                    } else {
                        isAboveZero = true;
                        simpleMessage.setId(id);
                        simpleMessage.setSenderName(initModel.getRobotName());
                        simpleMessage.setSender(initModel.getRobotName());
                        simpleMessage.setSenderFace(initModel.getRobotLogo());
                        simpleMessage.setSenderType(ZhiChiConstant.message_sender_type_robot + "");
                        Message message = handler.obtainMessage();
                        message.what = ZhiChiConstant.hander_robot_message;
                        message.obj = simpleMessage;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    if (!isActive()) {
                        return;
                    }
                    LogUtils.i("发送语音error:" + des + "exception:" + e.toString());
                    sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 0, UPDATE_VOICE, handler);
                }

                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {

                }
            });
        } else if (current_client_model == ZhiChiConstant.client_model_customService) {
            LogUtils.i("发送给人工语音---sobot---" + filePath);
            zhiChiApi.sendFile(cid, uid, filePath, voiceTimeLongStr,
                    new ResultCallBack<ZhiChiMessage>() {
                        @Override
                        public void onSuccess(ZhiChiMessage zhiChiMessage) {
                            if (!isActive()) {
                                return;
                            }
                            // 语音发送成功
                            isAboveZero = true;
                            restartMyTimeTask(handler);
                            sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 1, UPDATE_VOICE, handler);
                        }

                        @Override
                        public void onFailure(Exception e, String des) {
                            if (!isActive()) {
                                return;
                            }
                            final Map<String, String> map = new HashMap<>();
                            String cotent = e.toString() + des;
                            map.put("sendHttpCustomServiceMessage", cotent);
                            LogUtils.i2Local(map, LogUtils.LOGTYPE_ERROE);
                            LogUtils.i("发送语音error:" + des + "exception:" + e.toString());
                            sendVoiceMessageToHandler(voiceMsgId, filePath, voiceTimeLongStr, 0, UPDATE_VOICE, handler);
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {

                        }
                    });
        }

    }

    /**
     * @param voiceMsgId       语音暂时产生唯一标识符
     * @param voiceUrl         语音的地址
     * @param voiceTimeLongStr 语音的时长
     * @param isSendSuccess
     * @param state            发送状态
     * @param handler
     */
    protected void sendVoiceMessageToHandler(String voiceMsgId, String voiceUrl,
                                             String voiceTimeLongStr, int isSendSuccess, int state,
                                             final Handler handler) {

        ZhiChiMessageBase zhichiMessage = new ZhiChiMessageBase();
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        reply.setMsg(voiceUrl);
        reply.setDuration(voiceTimeLongStr);
        zhichiMessage.setT(Calendar.getInstance().getTime().getTime() + "");
        zhichiMessage.setAnswer(reply);
        zhichiMessage.setSenderType(ZhiChiConstant.message_sender_type_send_voice + "");
        zhichiMessage.setId(voiceMsgId);
        zhichiMessage.setSendSuccessState(isSendSuccess);
        // 设置语音的时长的操作

        Message message = handler.obtainMessage();
        if (state == UPDATE_VOICE) {// 更新界面布局
            message.what = ZhiChiConstant.message_type_update_voice;
        } else if (state == CANCEL_VOICE) {
            message.what = ZhiChiConstant.message_type_cancel_voice;
        } else if (state == SEND_VOICE) {
            message.what = ZhiChiConstant.hander_send_msg;
        }

        message.obj = zhichiMessage;
        handler.sendMessage(message);
    }

    /**
     * 重置输入预知
     */
    protected void restartInputListener() {
        stopInputListener();
        startInputListener();
    }

    //开启正在输入的监听
    protected void startInputListener() {
        inputtingListener = new Timer();
        inputTimerTask = new TimerTask() {
            @Override
            public void run() {
                //人工模式并且没有发送的时候
                if (customerState == CustomerState.Online && current_client_model == ZhiChiConstant.client_model_customService && !isSendInput) {
                    //获取对话
                    try {
                        String content = getSendMessageStr();
                        if (!TextUtils.isEmpty(content) && !content.equals(lastInputStr)) {
                            lastInputStr = content;
                            isSendInput = true;
                            //发送接口
                            zhiChiApi.input(initModel.getPartnerid(), content, new StringResultCallBack<CommonModel>() {
                                @Override
                                public void onSuccess(CommonModel result) {
                                    isSendInput = false;
                                }

                                @Override
                                public void onFailure(Exception e, String des) {
                                    isSendInput = false;
                                }
                            });
                        }
                    } catch (Exception e) {
//						e.printStackTrace();
                    }
                }
            }
        };
        // 500ms进行定时任务
        inputtingListener.schedule(inputTimerTask, 0, initModel.getInputTime() * 1000);
    }

    protected void stopInputListener() {
        if (inputtingListener != null) {
            inputtingListener.cancel();
            inputtingListener = null;
        }
    }

    // 设置听筒模式或者是正常模式的转换
    private void initAudioManager() {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        _sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        if (_sensorManager != null) {
            mProximiny = _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /* 获取当前手机品牌 过滤掉小米手机 */
        try {
            String phoneName = android.os.Build.MODEL.toLowerCase();
//            LogUtils.i("当前手机品牌是" + phoneName);
            // 模式的转化
            // 当前传感器距离
            float f_proximiny = event.values[0];
//            LogUtils.i("监听模式的转换：" + f_proximiny + " 听筒的模式：");
            // + mProximiny.getMaximumRange());
            if (!phoneName.contains("mi")) {
                if (f_proximiny == mProximiny.getMaximumRange()) {
                    audioManager.setSpeakerphoneOn(true);// 打开扬声器
                    audioManager.setMode(AudioManager.MODE_NORMAL);
//                    LogUtils.i("监听模式的转换：" + "正常模式");
                } else {
                    audioManager.setSpeakerphoneOn(false);// 关闭扬声器
                    if (getSobotActivity() != null) {
                        getSobotActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                    }
                    // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
                    //5.0以上
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    } else {
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                    }
//                    LogUtils.i("监听模式的转换：" + "听筒模式");
                }
            }
        } catch (Exception e) {
//			e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * 判断用户是否为黑名单
     *
     * @return
     */
    protected boolean isUserBlack() {
        if (initModel != null && "1".equals(initModel.getIsblack())) {
            return true;
        }
        return false;
    }

    /**
     * 重置内存中保存的数据
     */
    protected void clearCache() {
        SobotMsgManager.getInstance(mAppContext).clearAllConfig();
    }

    protected void requestQueryFrom(final String groupId, final String groupName) {
        requestQueryFrom(groupId, groupName);
    }

    /**
     * 检查是否有询前表单，这个方法在转人工时 会首先检查是否需要填写询前表单，
     * 如果有那么将会弹出询前表单填写界面，之后会调用转人工
     *
     * @param groupId
     * @param groupName
     */
    protected void requestQueryFrom(final String groupId, final String groupName, final int transferType, final boolean isCloseInquiryFrom) {
        if (customerState == CustomerState.Queuing || isHasRequestQueryFrom) {
            //如果在排队中就不需要填写询前表单 、或者之前弹过询前表单
            connectCustomerService(groupId, groupName);
            return;
        }
        if (isQueryFroming) {
            return;
        }
        isHasRequestQueryFrom = true;
        isQueryFroming = true;
        zhiChiApi.queryFormConfig(SobotChatBaseFragment.this, initModel.getPartnerid(), new StringResultCallBack<SobotQueryFormModel>() {
            @Override
            public void onSuccess(SobotQueryFormModel sobotQueryFormModel) {
                isQueryFroming = false;
                if (!isActive()) {
                    return;
                }
                if (sobotQueryFormModel.isOpenFlag() && !isCloseInquiryFrom && sobotQueryFormModel.getField() != null && sobotQueryFormModel.getField().size() > 0) {
                    // 打开询前表单
                    Intent intent = new Intent(mAppContext, SobotQueryFromActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPID, groupId);
                    bundle.putString(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPNAME, groupName);
                    bundle.putSerializable(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_FIELD, sobotQueryFormModel);
                    bundle.putSerializable(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_UID, initModel.getPartnerid());
                    bundle.putInt(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_TRANSFER_TYPE, transferType);
                    intent.putExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA, bundle);
                    startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM);
                } else {
                    connectCustomerService(groupId, groupName, transferType);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                isQueryFroming = false;
                if (!isActive()) {
                    return;
                }
                ToastUtil.showToast(mAppContext, des);
            }

        });
    }

    public void remindRobotMessage(final Handler handler, final ZhiChiInitModeBase initModel, final Information info) {
        //true代表结束会话，重新显示机器人提示语
        boolean flag = SharedPreferencesUtil.getBooleanData(mAppContext, ZhiChiConstant.SOBOT_IS_EXIT, false);
        if (initModel == null) {
            return;
        }
        // 修改提醒的信息
        remindRobotMessageTimes = remindRobotMessageTimes + 1;
        if (remindRobotMessageTimes == 1) {
            if ((initModel.getUstatus() == ZhiChiConstant.ustatus_robot) && !flag) {
                processNewTicketMsg(handler);
                return;
            }
            /* 首次的欢迎语 */
            ZhiChiMessageBase robot = new ZhiChiMessageBase();
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();

            if (initModel.isRobotHelloWordFlag()) {
                String robotHolloWord = ZCSobotApi.getCurrentInfoSetting(mAppContext) != null ? ZCSobotApi.getCurrentInfoSetting(mAppContext).getRobot_hello_word() : "";
                if (!TextUtils.isEmpty(robotHolloWord) || !TextUtils.isEmpty(initModel.getRobotHelloWord())) {
                    if (!TextUtils.isEmpty(robotHolloWord)) {
                        reply.setMsg(robotHolloWord);
                    } else {
                        if (TextUtils.isEmpty(initModel.getRobotHelloWord())) {
                            //如果提示语为空，直接返回，不然会显示错误数据
                            return;
                        }
                        String msgHint = initModel.getRobotHelloWord().replace("\n", "<br/>");
                        if (msgHint.startsWith("<br/>")) {
                            msgHint = msgHint.substring(5, msgHint.length());
                        }

                        if (msgHint.endsWith("<br/>")) {
                            msgHint = msgHint.substring(0, msgHint.length() - 5);
                        }
                        reply.setMsg(msgHint);
                    }
                    reply.setMsgType(ZhiChiConstant.message_type_text + "");
                    robot.setAnswer(reply);
                    robot.setSenderFace(initModel.getRobotLogo());
                    robot.setSender(initModel.getRobotName());
                    robot.setSenderType(ZhiChiConstant.message_sender_type_robot_welcome_msg + "");
                    robot.setSenderName(initModel.getRobotName());
                    Message message = handler.obtainMessage();
                    message.what = ZhiChiConstant.hander_robot_message;
                    message.obj = robot;
                    handler.sendMessage(message);
                }
            }
            //获取机器人带引导与的欢迎语
            if (1 == initModel.getGuideFlag()) {

                zhiChiApi.robotGuide(SobotChatBaseFragment.this, initModel.getPartnerid(), initModel.getRobotid(), info.getFaqId(), new
                        StringResultCallBack<ZhiChiMessageBase>() {
                            @Override
                            public void onSuccess(ZhiChiMessageBase robot) {
                                if (!isActive()) {
                                    return;
                                }
                                if (current_client_model == ZhiChiConstant.client_model_robot) {
                                    robot.setSenderFace(initModel.getRobotLogo());
                                    robot.setSenderType(ZhiChiConstant.message_sender_type_robot_guide + "");
                                    Message message = handler.obtainMessage();
                                    message.what = ZhiChiConstant.hander_robot_message;
                                    message.obj = robot;
                                    handler.sendMessage(message);

                                    questionRecommend(handler, initModel, info);
                                    processAutoSendMsg(info);
                                    processNewTicketMsg(handler);
                                }
                            }

                            @Override
                            public void onFailure(Exception e, String des) {
                            }
                        });
            } else {
                questionRecommend(handler, initModel, info);
                processAutoSendMsg(info);
                processNewTicketMsg(handler);
            }
        }
    }

    /**
     * 获取用户是否有新留言回复
     */
    protected void processNewTicketMsg(final Handler handler) {
        if (initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_open
                && !TextUtils.isEmpty(initModel.getCustomerId())) {
            isRemindTicketInfo = true;
            //留言开关打开并且 customerId不为空时获取最新的工单信息
            zhiChiApi.checkUserTicketInfo(SobotChatBaseFragment.this, initModel.getPartnerid(), initModel.getCompanyId(), initModel.getCustomerId(), new StringResultCallBack<SobotUserTicketInfoFlag>() {
                @Override
                public void onSuccess(SobotUserTicketInfoFlag data) {
                    if (data.isExistFlag()) {
                        ZhiChiMessageBase base = new ZhiChiMessageBase();

                        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");

                        ZhiChiReplyAnswer reply1 = new ZhiChiReplyAnswer();
                        reply1.setRemindType(ZhiChiConstant.sobot_remind_type_simple_tip);
                        reply1.setMsg("<font color='#ffacb5c4'>" + getResString("sobot_new_ticket_info") + " </font>" + " <a href='sobot:SobotTicketInfo'  target='_blank' >" + getResString("sobot_new_ticket_info_update") + "</a> ");
                        base.setAnswer(reply1);
                        Message message = handler.obtainMessage();
                        message.what = ZhiChiConstant.hander_send_msg;
                        message.obj = base;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {

                }
            });
        }
    }

    protected void processAutoSendMsg(final Information info) {
        if (info.getAutoSendMsgMode() == null) {
            return;
        }
        if (info.getAutoSendMsgMode() == SobotAutoSendMsgMode.Default) {
            return;
        }
        SobotAutoSendMsgMode autoSendMsgMode = info.getAutoSendMsgMode();
        if (TextUtils.isEmpty(autoSendMsgMode.getContent())) {
            return;
        }
        if (current_client_model == ZhiChiConstant.client_model_robot) {
            if (autoSendMsgMode == SobotAutoSendMsgMode.SendToRobot
                    || autoSendMsgMode == SobotAutoSendMsgMode.SendToAll) {
                sendMsg(autoSendMsgMode.getContent());
            }
        } else if (current_client_model == ZhiChiConstant.client_model_customService) {
            if ((autoSendMsgMode == SobotAutoSendMsgMode.SendToOperator
                    || autoSendMsgMode == SobotAutoSendMsgMode.SendToAll) && customerState == CustomerState.Online) {
                sendMsg(autoSendMsgMode.getContent());
            }
        }
    }

    private void questionRecommend(final Handler handler, final ZhiChiInitModeBase initModel, final Information info) {
        if (info.getMargs() == null || info.getMargs().size() == 0) {
            return;
        }
        zhiChiApi.questionRecommend(SobotChatBaseFragment.this, initModel.getPartnerid(), info.getMargs(), new StringResultCallBack<SobotQuestionRecommend>() {
            @Override
            public void onSuccess(SobotQuestionRecommend data) {
                if (!isActive()) {
                    return;
                }
                if (data != null && current_client_model == ZhiChiConstant.client_model_robot) {
                    ZhiChiMessageBase robot = ChatUtils.getQuestionRecommendData(initModel, data);
                    Message message = handler.obtainMessage();
                    message.what = ZhiChiConstant.hander_robot_message;
                    message.obj = robot;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
            }
        });
    }

    protected View.OnClickListener mLableClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (SobotOption.hyperlinkListener != null) {
                SobotOption.hyperlinkListener.onUrlClick(v.getTag() + "");
                return;
            }
            if (SobotOption.newHyperlinkListener != null) {
                //如果返回true,拦截;false 不拦截
                boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(getSobotActivity(), v.getTag() + "");
                if (isIntercept) {
                    return;
                }
            }

            Intent intent = new Intent(getContext(), WebViewActivity.class);
            intent.putExtra("url", v.getTag() + "");
            getSobotActivity().startActivity(intent);
        }
    };

    //-------------以下由子类实现-----------------------
    protected abstract String getSendMessageStr();

    protected void connectCustomerService(String groupId, String groupName) {
        connectCustomerService(groupId, groupName, 0);
    }

    protected void connectCustomerService(String groupId, String groupName, boolean isShowTips) {
        connectCustomerService(groupId, groupName, null, null, isShowTips, 0);
    }

    protected void connectCustomerService(String groupId, String groupName, final String keyword, final String keywordId, final boolean isShowTips) {
        connectCustomerService(groupId, groupName, keyword, keywordId, isShowTips, 0);
    }

    protected void connectCustomerService(String groupId, String groupName, int transferType) {
        connectCustomerService(groupId, groupName, null, null, true, transferType);
    }

    protected void connectCustomerService(String groupId, String groupName, String keyword, String keywordId, boolean isShowTips, int transferType) {
    }

    protected void customerServiceOffline(ZhiChiInitModeBase initModel, int outLineType) {
    }

    protected void sendMsg(String content) {
    }


    //轮询接口需要的参数
    private Map<String, String> pollingParams = new HashMap<>();
    //ack需要的参数
    private Map<String, String> ackParams = new HashMap<>();
    private PollingHandler pollingHandler;

    //轮询需要的handler
    private PollingHandler getPollingHandler() {
        if (this.pollingHandler == null) {
            this.pollingHandler = new PollingHandler();
        }
        return this.pollingHandler;
    }

    private static class PollingHandler extends Handler {

        public PollingHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    /**
     * 开启轮询
     */
    public void startPolling() {
        uid = SharedPreferencesUtil.getStringData(getSobotActivity(), Const.SOBOT_UID, "");
        puid = SharedPreferencesUtil.getStringData(getSobotActivity(), Const.SOBOT_PUID, "");
        getPollingHandler().removeCallbacks(pollingRun);
        getPollingHandler().postDelayed(pollingRun, 5 * 1000);
    }

    private String uid;
    private String puid;
    public boolean inPolling = false;//表示轮询接口是否在跑

    private Runnable pollingRun = new Runnable() {
        @Override
        public void run() {
            inPolling = true;
            pollingMsg();
        }
    };

    private void pollingMsg() {
        if (SobotVerControl.isPlatformVer) {
            pollingParams.put("platformUserId", uid);
        } else {
            pollingParams.put("uid", uid);
            pollingParams.put("puid", puid);
        }
        pollingParams.put("tnk", System.currentTimeMillis() + "");
        String platformUnionCode = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, "");
        zhiChiApi.pollingMsg(SobotChatBaseFragment.this, pollingParams, platformUnionCode, new StringResultCallBack<BaseCode>() {

            @Override
            public void onSuccess(BaseCode baseCode) {
                LogUtils.i("fragment 轮训请求结果:" + baseCode.getData().toString());
                getPollingHandler().removeCallbacks(pollingRun);
                if (baseCode != null) {
                    if ("0".equals(baseCode.getCode()) && "210021".equals(baseCode.getData())) {
                        //{"code":0,"data":"210021","msg":"当前用户被验证为非法用户，不能接入客服中心"}
                        //非法用户，停止轮训
                    } else if ("0".equals(baseCode.getCode()) && "200003".equals(baseCode.getData())) {
                        //{"code":0,"data":"200003","msg":"访客信息不存在"}
                        //找不到用户，停止轮训
                    } else {
                        getPollingHandler().postDelayed(pollingRun, 5 * 1000);
                        if (baseCode.getData() != null) {
                            responseAck(getSobotActivity(), baseCode.getData().toString());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                getPollingHandler().removeCallbacks(pollingRun);
                getPollingHandler().postDelayed(pollingRun, 10 * 1000);
                LogUtils.i("msg::::" + des);
            }
        });
    }

    //只请求一次轮训接口
    private void pollingMsgForOne() {
        uid = SharedPreferencesUtil.getStringData(getSobotActivity(), Const.SOBOT_UID, "");
        puid = SharedPreferencesUtil.getStringData(getSobotActivity(), Const.SOBOT_PUID, "");
        if (SobotVerControl.isPlatformVer) {
            pollingParams.put("platformUserId", uid);
        } else {
            pollingParams.put("uid", uid);
            pollingParams.put("puid", puid);
        }
        pollingParams.put("tnk", System.currentTimeMillis() + "");
        String platformUnionCode = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, "");
        zhiChiApi.pollingMsg(SobotChatBaseFragment.this, pollingParams, platformUnionCode, new StringResultCallBack<BaseCode>() {

            @Override
            public void onSuccess(BaseCode baseCode) {
                LogUtils.i("fragment 轮训请求结果:" + baseCode.getData().toString());
                if (baseCode != null) {
                    if ("0".equals(baseCode.getCode()) && "210021".equals(baseCode.getData())) {
                        //{"code":0,"data":"210021","msg":"当前用户被验证为非法用户，不能接入客服中心"}
                        //非法用户，停止轮训
                    } else if ("0".equals(baseCode.getCode()) && "200003".equals(baseCode.getData())) {
                        //{"code":0,"data":"200003","msg":"访客信息不存在"}
                        //找不到用户，停止轮训
                    } else {
                        if (baseCode.getData() != null) {
                            responseAck(getSobotActivity(), baseCode.getData().toString());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                LogUtils.i("msg::::" + des);
            }
        });
    }

    /**
     * 已收到消息的msgId队列
     */
    private LimitQueue<String> receiveMsgQueue = new LimitQueue<>(50);

    private void responseAck(Context mContext, String result) {
//        LogUtils.i("msg::::"+result);
        // 解析数据后给ack
        if (!TextUtils.isEmpty(result)) {
            JSONArray jsonArray = null;
            JSONArray acks = null;
            try {
                jsonArray = new JSONArray(result);
                acks = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String data = jsonArray.getString(i);
                    String msgId = Util.getMsgId(data);
                    if (!TextUtils.isEmpty(msgId)) {
                        if (receiveMsgQueue.indexOf(msgId) == -1) {
                            //队列中没有 表示是新数据
                            //新数据就添加进队列中
                            receiveMsgQueue.offer(msgId);
                            Util.notifyMsg(mContext, data);
                        }
                        //生成 回执
                        acks.put(new JSONObject("{msgId:" + msgId + "}"));
                    } else {
                        Util.notifyMsg(mContext, data);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (acks != null && acks.length() > 0) {
                ackParams.put("content", acks.toString());
                ackParams.put("tnk", System.currentTimeMillis() + "");
                zhiChiApi.msgAck(SobotChatBaseFragment.this, ackParams, new StringResultCallBack<BaseCode>() {
                    @Override
                    public void onSuccess(BaseCode baseCode) {

                    }

                    @Override
                    public void onFailure(Exception e, String des) {

                    }
                });
            }
        }
    }

    /**
     * 关闭轮询
     */
    public void stopPolling() {
        if (pollingRun != null && getPollingHandler() != null) {
            getPollingHandler().removeCallbacks(pollingRun);
            inPolling = false;
        }
    }

    @Override
    public void onDestroy() {
        stopPolling();
        OkHttpUtils.getInstance().cancelTag(SobotChatBaseFragment.this);
        super.onDestroy();
    }
}
