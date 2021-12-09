package com.sobot.chat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.sobot.chat.activity.SobotConsultationListActivity;
import com.sobot.chat.activity.SobotHelpCenterActivity;
import com.sobot.chat.activity.SobotPostMsgActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.SobotApp;
import com.sobot.chat.api.apiUtils.ZhiChiUrlApi;
import com.sobot.chat.api.enumtype.SobotChatAvatarDisplayMode;
import com.sobot.chat.api.enumtype.SobotChatStatusMode;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.api.model.SobotLeaveMsgConfig;
import com.sobot.chat.api.model.SobotLeaveReplyModel;
import com.sobot.chat.api.model.SobotLocationModel;
import com.sobot.chat.api.model.SobotMsgCenterModel;
import com.sobot.chat.api.model.SobotTransferOperatorParam;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.conversation.SobotChatActivity;
import com.sobot.chat.core.HttpUtils;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.HyperlinkListener;
import com.sobot.chat.listener.NewHyperlinkListener;
import com.sobot.chat.listener.SobotChatStatusListener;
import com.sobot.chat.listener.SobotFunctionClickListener;
import com.sobot.chat.listener.SobotLeaveMsgListener;
import com.sobot.chat.listener.SobotNoReadLeaveReplyListener;
import com.sobot.chat.listener.SobotOrderCardListener;
import com.sobot.chat.presenter.StPostMsgPresenter;
import com.sobot.chat.server.SobotSessionServer;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.NotificationUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotCache;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.StServiceUtils;
import com.sobot.chat.utils.SystemUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.sobot.chat.presenter.StPostMsgPresenter.INTENT_KEY_CONFIG;
import static com.sobot.chat.presenter.StPostMsgPresenter.INTENT_KEY_UID;
import static com.sobot.chat.utils.ZhiChiConstant.SOBOT_LANGUAGE_STRING_PATH;

/**
 * SobotChatApi接口输出类
 * 2.8.4之后和ios 统一部分方法名
 */
public class ZCSobotApi {


    private static String Tag = ZhiChiApi.class.getSimpleName();

    //---------2.8.4之后统一命名-------------

    /**
     * 打开会话页面
     *
     * @param context     上下文对象
     * @param information 接入参数
     */
    public static void openZCChat(Context context, Information information) {
        if (information == null || context == null) {
            Log.e(Tag, "Information is Null!");
            return;
        }
        boolean initSdk = SharedPreferencesUtil.getBooleanData(context, ZhiChiConstant.SOBOT_CONFIG_INITSDK, false);
        if (!initSdk) {
            Log.e(Tag, "请在Application中调用【SobotApi.initSobotSDK()】来初始化SDK!");
            return;
        }
        Intent intent = new Intent(context, SobotChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO, information);
        intent.putExtra(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 打开客户中心页面
     *
     * @param context     上下文对象
     * @param information 接入参数
     */
    public static void openZCServiceCenter(Context context, Information information) {
        if (information == null || context == null) {
            Log.e(Tag, "Information is Null!");
            return;
        }
        boolean initSdk = SharedPreferencesUtil.getBooleanData(context, ZhiChiConstant.SOBOT_CONFIG_INITSDK, false);
        if (!initSdk) {
            Log.e(Tag, "请在Application中调用【SobotApi.initSobotSDK()】来初始化SDK!");
            return;
        }
        Intent intent = new Intent(context, SobotHelpCenterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO, information);
        intent.putExtra(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * // 打开消息中心页面
     *
     * @param context   上下文对象
     * @param partnerid 用户唯一标识 与information中传的partnerid一致
     */
    public static void openZCChatListView(Context context, String partnerid) {
        Intent intent = new Intent(context, SobotConsultationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, partnerid);
        context.startActivity(intent);
    }

    /**
     * 打开留言页面
     *
     * @param context          上下文  必填
     * @param info             用户的appkey  必填 如果是平台用户需要传总公司的appkey
     * @param isOnlyShowTicket true只显示留言记录界面，false 请您留言和留言记录界面都显示
     */
    public static void openLeave(final Context context, final Information info, final boolean isOnlyShowTicket) {
        if (info == null) {
            Log.e(Tag, "参数info不能为空");
            return;
        }
        if (context == null || TextUtils.isEmpty(info.getApp_key())) {
            Log.e(Tag, "参数info中app_key的不能为空");
            return;
        }
        //保证每次进入留言页面时partnerid时有值，不然的话，每次都会生成新的，留言记录就会不准确
        if (TextUtils.isEmpty(info.getPartnerid())) {
            info.setPartnerid(CommonUtils.getDeviceId(context));
        }
        SobotMsgManager.getInstance(context).getZhiChiApi()
                .sobotInit(context, info, new StringResultCallBack<ZhiChiInitModeBase>() {
                    @Override
                    public void onSuccess(final ZhiChiInitModeBase initModel) {
                        SharedPreferencesUtil.saveObject(context,
                                ZhiChiConstant.sobot_last_current_info, info);
                        final List<SobotFieldModel> sobotFieldModels = new ArrayList<>();
                        if (info.getLeaveCusFieldMap() != null && info.getLeaveCusFieldMap().size() > 0) {
                            for (String key :
                                    info.getLeaveCusFieldMap().keySet()) {
                                SobotFieldModel sobotFieldModel = new SobotFieldModel();
                                SobotCusFieldConfig sobotCusFieldConfig = new SobotCusFieldConfig();
                                sobotCusFieldConfig.setFieldId(key);
                                sobotCusFieldConfig.setValue(info.getLeaveCusFieldMap().get(key));
                                sobotFieldModel.setCusFieldConfig(sobotCusFieldConfig);
                                sobotFieldModels.add(sobotFieldModel);
                            }
                        }
                        if (!TextUtils.isEmpty(info.getLeaveTemplateId())) {
                            SobotMsgManager.getInstance(context).getZhiChiApi().getMsgTemplateConfig(this, initModel.getPartnerid(), info.getLeaveTemplateId(), new StringResultCallBack<SobotLeaveMsgConfig>() {
                                @Override
                                public void onSuccess(SobotLeaveMsgConfig data) {
                                    if (data != null) {
                                        Intent intent = new Intent(context, SobotPostMsgActivity.class);
                                        intent.putExtra(INTENT_KEY_UID, initModel.getPartnerid());
                                        intent.putExtra(INTENT_KEY_CONFIG, data);
                                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, initModel.getCompanyId());
                                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, initModel.getCustomerId());
                                        intent.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, false);
                                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_GROUPID, info.getLeaveMsgGroupId());
                                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_CUS_FIELDS, (Serializable) sobotFieldModels);
                                        intent.putExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, isOnlyShowTicket);
                                        context.startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Exception e, String des) {
                                    e.printStackTrace();
                                    LogUtils.i("通过配置模版id跳转到留言界面：" + des);
                                }
                            });
                        } else {
                            SobotLeaveMsgConfig config = new SobotLeaveMsgConfig();
                            config.setEmailFlag(initModel.isEmailFlag());
                            config.setEmailShowFlag(initModel.isEmailShowFlag());
                            config.setEnclosureFlag(initModel.isEnclosureFlag());
                            config.setEnclosureShowFlag(initModel.isEnclosureShowFlag());
                            config.setTelFlag(initModel.isTelFlag());
                            config.setTelShowFlag(initModel.isTelShowFlag());
                            config.setTicketStartWay(initModel.isTicketStartWay());
                            config.setTicketShowFlag(initModel.isTicketShowFlag());
                            config.setCompanyId(initModel.getCompanyId());
                            if (!TextUtils.isEmpty(info.getLeaveMsgTemplateContent())) {
                                config.setMsgTmp(info.getLeaveMsgTemplateContent());
                            } else {
                                config.setMsgTmp(initModel.getMsgTmp());
                            }
                            if (!TextUtils.isEmpty(info.getLeaveMsgGuideContent())) {
                                config.setMsgTxt(info.getLeaveMsgGuideContent());
                            } else {
                                config.setMsgTxt(initModel.getMsgTxt());
                            }
                            Intent intent = new Intent(context, SobotPostMsgActivity.class);
                            intent.putExtra(INTENT_KEY_UID, initModel.getPartnerid());
                            intent.putExtra(INTENT_KEY_CONFIG, config);
                            intent.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, initModel.getCompanyId());
                            intent.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, initModel.getCustomerId());
                            intent.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, false);
                            intent.putExtra(StPostMsgPresenter.INTENT_KEY_GROUPID, info.getLeaveMsgGroupId());
                            intent.putExtra(StPostMsgPresenter.INTENT_KEY_CUS_FIELDS, (Serializable) sobotFieldModels);
                            intent.putExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, isOnlyShowTicket);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {

                    }
                });
    }

    /**
     * 发送位置信息
     *
     * @param context
     * @param locationData 地址对象的值
     */
    public static void sendLocation(Context context, SobotLocationModel locationData) {
        if (context == null || locationData == null) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_LOCATION);
        intent.putExtra(ZhiChiConstant.SOBOT_LOCATION_DATA, locationData);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 发送文字消息给客服
     *
     * @param context
     * @param content 文本内容
     */
    public static void sendTextToUser(Context context, String content) {
        if (context == null || TextUtils.isEmpty(content)) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_TEXT);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, content);
        intent.putExtra("sendTextTo", "user");
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 发送消息给客服
     *
     * @param context
     * @param content 发送内容
     * @param type    发送类型
     *                type = 0; //文本
     *                type = 1; //图片
     *                type = 3; //视频
     *                type = 4; //文件
     */
    public static void sendMessageToUser(Context context, String content, String type) {
        if (context == null || TextUtils.isEmpty(content)) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_OBJECT);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, content);
        intent.putExtra(ZhiChiConstant.SOBOT_TYPE_DATA, type);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 发送订单卡片消息
     *
     * @param context
     * @param content 卡片信息
     */
    public static void sendOrderGoodsInfo(Context context, OrderCardContentModel content) {
        if (context == null || content == null) {
            return;
        }
        if (TextUtils.isEmpty(content.getOrderCode())) {
            LogUtils.e("订单编号不能为空");
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_ORDER_CARD);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, content);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 发送商品卡片消息
     *
     * @param context
     * @param content 卡片信息
     */
    public static void sendProductInfo(Context context, ConsultingContent content) {
        if (context == null || content == null) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_CARD);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, content);
        localBroadcastManager.sendBroadcast(intent);
    }


    /**
     * 发送文本到机器人
     */
    public static void sendTextToRobot(Context context, String content) {
        if (context == null || TextUtils.isEmpty(content)) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_TEXT);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, content);
        intent.putExtra("sendTextTo", "robot");
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 转人工自定义
     *
     * @param context
     * @param param   转人工参数
     *                String groupId       技能组id
     *                String groupName     技能组名称
     *                String keyword       关键词转人工的关键词
     *                String keywordId     关键词转人工的关键词ID
     *                boolean isShowTips   是否显示转人工提示
     *                ConsultingContent consultingContent
     *                商品信息
     */
    public static void connectCustomerService(Context context, SobotTransferOperatorParam param) {
        if (context == null || param == null) {
            return;
        }
        Intent intent = new Intent();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        intent.setAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_TRASNFER_TO_OPERATOR);
        intent.putExtra(ZhiChiConstant.SOBOT_SEND_DATA, param);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 获取留言未读回复列表
     *
     * @param context                  上下文  必填
     * @param partnerId                用户唯一标识 与information中传的partnerId一致
     * @param noReadLeaveReplyListener 留言未读回复列表回调，返回List<SobotLeaveReplyModel>
     */
    public static void getLastLeaveReplyMessage(final Context context, String partnerId, final SobotNoReadLeaveReplyListener noReadLeaveReplyListener) {
        if (context == null || TextUtils.isEmpty(partnerId)) {
            partnerId = CommonUtils.getDeviceId(context);
        }
         String companyId =  SharedPreferencesUtil.getStringData(context,
                ZhiChiConstant.SOBOT_CONFIG_COMPANYID,"");
        final List<SobotLeaveReplyModel> sobotLeaveReplyModels = new ArrayList<>();
        SobotMsgManager.getInstance(context).getZhiChiApi()
                .getUserTicketReplyInfo(context, companyId, partnerId, new StringResultCallBack<List<SobotLeaveReplyModel>>() {
                    @Override
                    public void onSuccess(List<SobotLeaveReplyModel> leaveReplyModelList) {
                        if (leaveReplyModelList != null && leaveReplyModelList.size() > 0) {
                            //客户获取留言回复，如果获取到有未读的留言回复，把最新的一条回复展示在通知栏中
                            sobotLeaveReplyModels.addAll(leaveReplyModelList);
                            if (noReadLeaveReplyListener != null) {
                                noReadLeaveReplyListener.onNoReadLeaveReplyListener(sobotLeaveReplyModels);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {

                    }
                });
    }

    /**
     * 发送留言回复通知，点击通知栏通知后，跳转到留言详情页面
     *
     * @param context         上下文  必填
     * @param leaveReplyModel 单条最新留言回复对象(可通过getLastLeaveReplyMessage() 能拿到)
     */
    public static void sendLeaveReplyNotification(final Context context, SobotLeaveReplyModel leaveReplyModel, int smallIcon, int largeIcon) {
        final ZhiChiInitModeBase initMode = (ZhiChiInitModeBase) SharedPreferencesUtil.getObject(context,
                ZhiChiConstant.sobot_last_current_initModel);
        if (leaveReplyModel == null) {
            return;
        }

        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_SMALL_ICON, smallIcon);
        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_LARGE_ICON, largeIcon);

        String notificationTitle = ResourceUtils.getResString(context, "sobot_notification_tip_title");
        NotificationUtils.createLeaveReplyNotification(context, leaveReplyModel.getTicketTitle(), leaveReplyModel.getReplyContent(), notificationTitle, 1001, initMode == null ? "" : initMode.getCompanyId(), initMode == null ? "" : initMode.getUid(), leaveReplyModel);

    }

    /**
     * 获取 SDK 版本号
     */
    public static String getVersion(Context context) {
        return ZhiChiUrlApi.VERSION;
    }

    /**
     * 日志显示设置
     *
     * @param isShowLog true 显示日志信息 默认false不显示
     */
    public static void setShowDebug(Boolean isShowLog) {
        if (isShowLog) {
            LogUtils.isDebug = true;
            LogUtils.allowI = true;
            LogUtils.allowE = true;
            LogUtils.allowD = true;
        } else {
            LogUtils.isDebug = false;
            LogUtils.allowI = false;
            LogUtils.allowE = false;
            LogUtils.allowD = true;
        }
    }


    /**
     * 获取系统名称
     */
    public static String getSystem() {
        return SystemUtil.getSystemVersion();
    }

    /**
     * 获取app版本
     */
    public static String getAppVersion(Context context) {
        return SystemUtil.getVersionCode(context) + "";
    }

    /**
     * 获取手机型号
     */
    public static String getPhoneType() {
        return SystemUtil.getSystemModel();
    }

    /**
     * 获取当前集成的app名称
     */
    public static String getAppName(Context context) {
        return SystemUtil.getAppName(context);
    }

    /**
     * 获取用户的 UUID(如果打开智齿客服界面时不传用户标示partnerId,系统会自动生成一个)
     *
     * @param context
     * @return
     */
    public static String getUserUUID(Context context) {
        return CommonUtils.getPartnerId(context);
    }

    /**
     * 读取日志文件内容 保存最近的7天
     *
     * @param date 日期（yyyyMMdd） 例如: 20160312
     * @return
     */
    public static String readLogFileDateString(String date) {
        return LogUtils.getLogFileByDate(date);
    }

    /**
     * 关闭通道，退出客服，用于用户退出登录时调用
     *
     * @param context 上下文对象
     */
    public static void outCurrentUserZCLibInfo(final Context context) {
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_IS_EXIT, true);
        if (context == null) {
            return;
        }
        try {
            closeIMConnection(context);
            context.stopService(new Intent(context, SobotSessionServer.class));

            String cid = SharedPreferencesUtil.getStringData(context, Const.SOBOT_CID, "");
            String uid = SharedPreferencesUtil.getStringData(context, Const.SOBOT_UID, "");
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_WSLINKBAK);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_WSLINKDEFAULT);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_UID);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_CID);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_PUID);
            SharedPreferencesUtil.removeKey(context, Const.SOBOT_APPKEY);

            if (!TextUtils.isEmpty(cid) && !TextUtils.isEmpty(uid)) {
                ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
                zhiChiApi.out(cid, uid, new StringResultCallBack<CommonModel>() {
                    @Override
                    public void onSuccess(CommonModel result) {
                        LogUtils.i("下线成功");
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化sdk
     *
     * @param context   上下文  必填
     * @param appkey    用户的appkey  必填 如果是平台用户需要传总公司的appkey
     * @param partnerid 用户的唯一标识不能传一样的值
     */
    public static void initSobotSDK(final Context context, final String appkey, final String partnerid) {
        if (context == null || TextUtils.isEmpty(appkey)) {
            Log.e(Tag, "initSobotSDK  参数为空 context:" + context + "  appkey:" + appkey);
            return;
        }

        SobotApp.setApplicationContext(context);
        SharedPreferencesUtil.saveAppKey(context, appkey);

        SharedPreferencesUtil.saveStringData(context, Const.SOBOT_APPKEY, appkey);
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_CONFIG_INITSDK, true);
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_CONFIG_APPKEY, appkey);
        //清空sdk 语言设置
        SharedPreferencesUtil.saveObject(context, "SobotLanguage", null);
        if (!CommonUtils.inMainProcess(context.getApplicationContext())) {
            return;
        }
        LogUtils.setSaveDir(CommonUtils.getPrivatePath(context));
        new Thread(new Runnable() {
            @Override
            public void run() {
                SobotMsgManager.getInstance(context).initSobotSDK(context, appkey, partnerid);
            }
        }).start();

    }


    /**
     * 获取最后一条消息
     *
     * @param context
     * @return
     */
    public static String getLastMessage(Context context) {
        return SharedPreferencesUtil.getStringData(context, ZhiChiConstant.SOBOT_LAST_MSG_CONTENT, "");
    }


    /**
     * 检查当前消息通道是否建立，没有就重新建立
     *
     * @param partnerid 用户唯一标识 与information中传的partnerid一致
     * @param context   上下文对象
     */
    public static void checkIMConnected(Context context, String partnerid) {
        LogUtils.i("checkIMConnected partnerid=" + partnerid);
        if (context == null) {
            return;
        }
        context = context.getApplicationContext();
        SharedPreferencesUtil.removeKey(context, Const.SOBOT_WAYHTTP);
        SobotMsgManager.getInstance(context).getZhiChiApi().reconnectChannel();
        Intent intent = new Intent(context, SobotSessionServer.class);
        intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, partnerid);
        StServiceUtils.safeStartService(context, intent);
    }


    /**
     * 关闭当前消息通道，使其不再接受消息
     *
     * @param context 上下文对象
     */
    public static void closeIMConnection(Context context) {
        if (context == null) {
            return;
        }
        if (SobotOption.sobotChatStatusListener != null) {
            //修改聊天状态为离线
            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectOffline);
        }
        SobotMsgManager.getInstance(context).getZhiChiApi().disconnChannel();
        SobotMsgManager.getInstance(context).clearAllConfig();
    }


    /**
     * 清空用户下的所有未读消息计数
     *
     * @param context
     * @param partnerid 用户唯一标识 与information中传的partnerid一致
     */
    public static void clearUnReadNumber(Context context, String partnerid) {
        if (context == null) {
            return;
        }
        SobotMsgManager.getInstance(context).clearAllUnreadCount(context, partnerid);
    }

    /**
     * 获取当前未读消息数
     *
     * @param context
     * @param partnerid 用户唯一标识 与information中传的partnerid一致
     * @return
     */
    public static int getUnReadMessage(Context context, String partnerid) {
        if (context == null) {
            return 0;
        } else {
            int count = 0;
            List<SobotMsgCenterModel> msgCenterList = getMsgCenterList(context, partnerid);
            if (msgCenterList != null) {
                for (int i = 0; i < msgCenterList.size(); i++) {
                    count += msgCenterList.get(i).getUnreadCount();
                }
            }
            return count;
        }
    }


    /**
     * 2.8.4 添加
     * 替换消息中手机或固话识别的正则表达式
     * 默认为"d{3}-\d{8}|\d{3}-\d{7}|\d{4}-\d{8}|\d{4}-\d{7}|1+[34578]+\d{9}|\+\d{2}1+[34578]+\d{9}|400\d{7}|400-\d{3}-\d{4}|\d{11}|\d{10}|\d{8}|\d{7}"
     * * 例如：82563452、01082563234、010-82543213、031182563234、0311-82563234
     * 、+8613691080322、4008881234、400-888-1234
     *
     * @param regex 手机或固话的正则表达
     */
    public static void replacePhoneNumberPattern(String regex) {
        HtmlTools.setPhoneNumberPattern(Pattern.compile(regex));
    }

    /**
     * 2.8.4 添加
     * 替换消息中手网址识别的正则表达式
     * 默认为"((http[s]{0,1}|ftp)://[a-zA-Z0-9\.\-]+\.([a-zA-Z]{2,4})(:\d+)?(/[a-zA-Z0-9\.\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\.\-]+\.([a-zA-Z]{2,4})(:\d+)?(/[a-zA-Z0-9\.\-~!@#$%^&*+?:_/=<>]*)?)|([a-zA-Z0-9\.\-]+\.([a-zA-Z]{2,4})(:\d+)?(/[a-zA-Z0-9\.\-~!@#$%^&*+?:_/=<>]*)?)"
     *
     * @param regex 手机或固话的正则表达
     */
    public static void replaceWebUrlPattern(String regex) {
        HtmlTools.setWebUrl(Pattern.compile(regex));
    }


    //-------------


    /**
     * 初始化电商平台
     *
     * @param context           Context 对象
     * @param platformUnionCode 平台标识
     * @param platformSecretkey 平台标识 秘钥
     */
    public static void initPlatformUnion(Context context, String platformUnionCode, String platformSecretkey) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, platformUnionCode);
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_PLATFORM_KEY, platformSecretkey);
    }


    /**
     * 设置是否开启消息提醒   默认不提醒
     *
     * @param context
     * @param flag
     * @param smallIcon 小图标的id 设置通知栏中的小图片，尺寸一般建议在24×24
     * @param largeIcon 大图标的id
     */
    public static void setNotificationFlag(Context context, boolean flag, int smallIcon, int largeIcon) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveBooleanData(context, Const.SOBOT_NOTIFICATION_FLAG, flag);
        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_SMALL_ICON, smallIcon);
        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_NOTIFICATION_LARGE_ICON, largeIcon);
    }

    /**
     * 清除所有通知
     *
     * @param context
     */
    public static void cancleAllNotification(Context context) {
        if (context == null) {
            return;
        }
        NotificationUtils.cancleAllNotification(context);
    }

    /**
     * 设置超链接的点击事件监听
     *
     * @param hyperlinkListener
     */
    public static void setHyperlinkListener(HyperlinkListener hyperlinkListener) {
        SobotOption.hyperlinkListener = hyperlinkListener;
    }

    /**
     * 设置超链接的点击事件监听
     * 根据返回值用户可分开动态设置是否拦截，举例 监听到有订单编号，返回true 拦截；商品
     *
     * @param newHyperlinkListener
     */
    public static void setNewHyperlinkListener(NewHyperlinkListener newHyperlinkListener) {
        SobotOption.newHyperlinkListener = newHyperlinkListener;
    }

    /**
     * 智齿部分功能点击的监听，可用于客户埋点
     *
     * @param functionClickListener
     */
    public static void setFunctionClickListener(SobotFunctionClickListener functionClickListener) {
        SobotOption.functionClickListener = functionClickListener;
    }

    /**
     * 设置当前聊天状态的监听
     *
     * @param chatStatusListener
     */
    public static void setChatStatusListener(SobotChatStatusListener chatStatusListener) {
        SobotOption.sobotChatStatusListener = chatStatusListener;
    }

    /**
     * 设置订单卡片的点击事件监听
     *
     * @deprecated Use {@link #setHyperlinkListener(HyperlinkListener)} instead.
     */
    @Deprecated
    public static void setOrderCardListener(SobotOrderCardListener orderCardListener) {
        SobotOption.orderCardListener = orderCardListener;
    }

    /**
     * 设置跳转到留言页的监听
     *
     * @param sobotLeaveMsgListener
     */
    public static void setSobotLeaveMsgListener(SobotLeaveMsgListener sobotLeaveMsgListener) {
        SobotOption.sobotLeaveMsgListener = sobotLeaveMsgListener;
    }


    /**
     * 设置聊天界面标题显示模式
     *
     * @param context      上下文对象
     * @param title_type   titile的显示模式
     *                     SobotChatTitleDisplayMode.Default:显示客服昵称(默认)
     *                     SobotChatTitleDisplayMode.ShowFixedText:显示固定文本
     *                     SobotChatTitleDisplayMode.ShowCompanyName:显示console设置的企业名称
     * @param custom_title 如果需要显示固定文本，需要传入此参数，其他模式可以不传
     * @param isShowTitle  是否显示标题 true 显示;false 隐藏,默认 false
     */
    public static void setChatTitleDisplayMode(Context context, SobotChatTitleDisplayMode title_type, String custom_title, boolean isShowTitle) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_CHAT_TITLE_DISPLAY_MODE,
                title_type.getValue());
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_CHAT_TITLE_DISPLAY_CONTENT,
                custom_title);
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_CHAT_TITLE_IS_SHOW,
                isShowTitle);
    }


    /**
     * 设置聊天界面头像显示模式
     *
     * @param context           上下文对象
     * @param avatar_type       titile的显示模式
     *                          SobotChatAvatarDisplayMode.Default:显示客服头像(默认)
     *                          SobotChatAvatarDisplayMode.ShowFixedAvatar:显示固定头像
     *                          SobotChatAvatarDisplayMode.ShowCompanyAvatar:显示console设置的企业名称
     * @param custom_avatar_url 如果需要显示固定头像，需要传入此参数，其他模式可以不传
     * @param isShowAvatar      是否显示头像 true 显示;false 隐藏,默认 true
     */
    public static void setChatAvatarDisplayMode(Context context, SobotChatAvatarDisplayMode avatar_type, String custom_avatar_url, boolean isShowAvatar) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveIntData(context, ZhiChiConstant.SOBOT_CHAT_AVATAR_DISPLAY_MODE,
                avatar_type.getValue());
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_CHAT_AVATAR_DISPLAY_CONTENT,
                custom_avatar_url);
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_CHAT_AVATAR_IS_SHOW,
                isShowAvatar);
    }

    /**
     * 控制显示历史聊天记录的时间范围
     *
     * @param time 查询时间(例:100-表示从现在起前100分钟的会话)
     * @deprecated Use {@link #setScope_time(Context, long)} instead.
     */
    @Deprecated
    public static void hideHistoryMsg(Context context, long time) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveLongData(context, ZhiChiConstant.SOBOT_SCOPE_TIME,
                time);
    }

    /**
     *   控制显示历史聊天记录的时间范围，最多只能显示60天内的消息
     * 不传的话默认会显示所有的历史记录
     *   @param time  查询时间 单位(分钟) (例:100-表示从现在起前100分钟的会话)
     */
    public static void setScope_time(Context context, long time) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveLongData(context, ZhiChiConstant.SOBOT_SCOPE_TIME,
                time);
    }

    /**
     * 配置用户提交人工满意度评价后释放会话
     *
     * @param context
     * @param flag
     */
    public static void setEvaluationCompletedExit(Context context, boolean flag) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_CHAT_EVALUATION_COMPLETED_EXIT, flag);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服欢迎语
     * @deprecated Use {@link #setAdmin_Hello_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomAdminHelloWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_HELLO_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服欢迎语
     */
    public static void setAdmin_Hello_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_HELLO_WORD, content);
    }


    /**
     * @param context Context 对象
     * @param content 自定义机器人欢迎语
     * @deprecated Use {@link #setRobot_Hello_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomRobotHelloWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ROBOT_HELLO_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义机器人欢迎语
     */
    public static void setRobot_Hello_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ROBOT_HELLO_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义用户超时提示语
     * @deprecated Use {@link #setUser_Tip_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomUserTipWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_TIP_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义用户超时提示语
     */
    public static void setUser_Tip_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_TIP_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服超时提示语
     * @deprecated Use {@link #setAdmin_Tip_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomAdminTipWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_TIP_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服超时提示语
     */
    public static void setAdmin_Tip_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_TIP_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服不在线的说辞
     * @deprecated Use {@link #setAdmin_Offline_Title(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomAdminNonelineTitle(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_OFFLINE_TITLE, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义客服不在线的说辞
     */
    public static void setAdmin_Offline_Title(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_ADMIN_OFFLINE_TITLE, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义用户超时下线提示语
     * @deprecated Use {@link #setUser_Out_Word(Context, String)} instead.
     */
    @Deprecated
    public static void setCustomUserOutWord(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_OUT_WORD, content);
    }

    /**
     * @param context Context 对象
     * @param content 自定义用户超时下线提示语
     */
    public static void setUser_Out_Word(Context context, String content) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_OUT_WORD, content);
    }

    /**
     * 获取消息中心数据
     *
     * @param context
     * @param partnerid 用户唯一标识 与information中传的partnerid一致
     * @return
     */
    public static List<SobotMsgCenterModel> getMsgCenterList(Context context, String partnerid) {
        if (context == null) {
            return null;
        }
        partnerid = partnerid == null ? "" : partnerid;
        SobotCache sobotCache = SobotCache.get(context);
        ArrayList<String> msgDatas = (ArrayList<String>) sobotCache.getAsObject(SobotMsgManager.getMsgCenterListKey(partnerid));
        List<SobotMsgCenterModel> datas = new ArrayList<SobotMsgCenterModel>();
        if (msgDatas != null && msgDatas.size() > 0) {
            datas.clear();
            for (String appkey : msgDatas) {
                SobotMsgCenterModel data = (SobotMsgCenterModel) sobotCache.getAsObject(SobotMsgManager.getMsgCenterDataKey(appkey, partnerid));
                if (data != null) {
                    datas.add(data);
                }
            }
        }
        return datas;
    }

    /**
     * 清除所有消息中心消息
     *
     * @param context
     * @param partnerid 用户唯一标识 与information中传的partnerid一致
     */
    public static void clearMsgCenterList(Context context, String partnerid) {
        if (context == null) {
            return;
        }
        partnerid = partnerid == null ? "" : partnerid;
        SobotCache sobotCache = SobotCache.get(context);
        sobotCache.remove(SobotMsgManager.getMsgCenterListKey(partnerid));
    }


    /**
     * @param context        Context 对象
     * @param flow_companyid 设置溢出公司id
     * @deprecated Use {@link #setFlow_Company_Id(Context, String)} instead.
     */
    @Deprecated
    public static void setFlowCompanyId(Context context, String flow_companyid) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_COMPANYID, flow_companyid);
    }

    /**
     * @param context        Context 对象
     * @param flow_companyid 设置溢出公司id
     */
    public static void setFlow_Company_Id(Context context, String flow_companyid) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_COMPANYID, flow_companyid);
    }

    /**
     * @param context   Context 对象
     * @param flow_type flowType-是否溢出到主商户 0-不溢出 , 1-全部溢出，2-忙碌时溢出，3-不在线时溢出,默认不溢出
     * @deprecated Use {@link #setFlow_Type(Context, String)} instead.
     */
    @Deprecated
    public static void setFlowType(Context context, String flow_type) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_TYPE, flow_type);
    }

    /**
     * @param context   Context 对象
     * @param flow_type @param content flowType -是否溢出到主商户 0-不溢出 , 1-全部溢出，2-忙碌时溢出，3-不在线时溢出,默认不溢出
     */
    public static void setFlow_Type(Context context, String flow_type) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_TYPE, flow_type);
    }

    /**
     * @param context      Context 对象
     * @param flow_groupid 转人工溢出公司技能组id
     * @deprecated Use {@link #setFlow_GroupId(Context, String)} instead.
     */
    @Deprecated
    public static void setFlowGroupId(Context context, String flow_groupid) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_GROUPID, flow_groupid);
    }

    /**
     * @param context      Context 对象
     * @param flow_groupid 转人工溢出公司技能组id
     */
    public static void setFlow_GroupId(Context context, String flow_groupid) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_FLOW_GROUPID, flow_groupid);
    }


    /**
     * 判断当前用户是否正在与当前商户客服聊天
     *
     * @param context 上下文对象
     * @param appkey  当前商户的appkey
     * @return true 表示正在与当前商户客服聊天
     * false 表示当前没有与所选商户客服聊天
     */
    public static boolean isActiveOperator(Context context, String appkey) {
        return SobotMsgManager.getInstance(context.getApplicationContext()).isActiveOperator(appkey);
    }


    /**
     * 获取开关状态
     *
     * @param markConfig 开关名
     * @return
     * @see MarkConfig 取值
     */
    public static boolean getSwitchMarkStatus(int markConfig) {
        if ((markConfig & (markConfig - 1)) == 0)
            return MarkConfig.getON_OFF(markConfig);
        else {
            throw new Resources.NotFoundException("markConfig 必须为2的指数次幂");
        }
    }

    /**
     * 设置开关状态
     *
     * @param markConfig 开关名 必须为 2 的非负数整数次幂
     * @param isOn
     * @see MarkConfig 取值
     */
    public static void setSwitchMarkStatus(int markConfig, boolean isOn) {
        if ((markConfig & (markConfig - 1)) == 0)
            MarkConfig.setON_OFF(markConfig, isOn);
        else {
            throw new Resources.NotFoundException("markConfig 必须为2的指数次幂");
        }
    }


    /**
     * 2.8.6
     * 指定使用国际化语言包
     * 如果本地没有指定的语言时,开始下载,下载前会检测存储权限，没有权限，直接返回，使用系统权限
     *
     * @param isUse        是否使用指定语言  是false时，清理语言包
     * @param language     指定语言名字 例如 en,zh_rtw等
     * @param isReDownload 是否重新下载语言包 true 重新下载，false 不重新下载,默认false
     */
    public static void setInternationalLanguage(final Context context, final String language, boolean isUse, boolean isReDownload) {
        if (context == null || TextUtils.isEmpty(language)) {
            return;
        }
        //清空sdk 语言设置
        SharedPreferencesUtil.saveObject(context, "SobotLanguage", null);
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_SETTTINNG_LANGUAGE, "");
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_USE_LANGUAGE, isUse);
        SharedPreferencesUtil.saveStringData(context, SOBOT_LANGUAGE_STRING_PATH, "");

        if (!isUse) {
            //不使用指定语言,直接返回,使用sdk自带的国际化语言
            return;
        }
        SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_LANGUAGE_STRING_NAME, "sobot_android_strings_" + language);
        String languageFileName = "sobot_android_strings_" + language + ".json";
        //指定语言包保存路径
        final String languagePath = CommonUtils.getPrivatePath(context) + File.separator + getAppName(context) + File.separator + "sobot_language" + File.separator + ZhiChiUrlApi.LANGUAGE_VERSION + File.separator + languageFileName;
        File file = new File(languagePath);
        if (isReDownload && file.exists()) {
            //如果指定语言包已存在，并且要重新下载使用最新，先删除本地已存在的
            file.delete();
            SharedPreferencesUtil.saveStringData(context, SOBOT_LANGUAGE_STRING_PATH, "");
        }
        if (file.exists()) {
            //如果该语言包已存在,直接使用
            SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_LANGUAGE_STRING_PATH, languagePath);
            if ("ar".equals(language)) {
                //添加sdk语言，设置成阿拉伯语
                Locale locale = new Locale("ar");
                SharedPreferencesUtil.saveObject(context, "SobotLanguage", locale);
            }
            if ("he".equals(language)) {
                //添加sdk语言，设置成希伯来文
                Locale locale = new Locale("iw");
                SharedPreferencesUtil.saveObject(context, "SobotLanguage", locale);
            }
            //保存客服设置的语言，例如en,zh_rtw等
            SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_SETTTINNG_LANGUAGE, language);
            return;
        }
//        if (!checkStoragePermission(context)) {
//            SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_USE_LANGUAGE, false);
//            LogUtils.i("没有文件存储权限，无法下载语言包");
//            return;
//        }

        HttpUtils.getInstance().download("https://img.sobot.com/mobile/multilingual/android/" + ZhiChiUrlApi.LANGUAGE_VERSION + "/" + languageFileName, file, null, new HttpUtils.FileCallBack() {

            @Override
            public void onResponse(File result) {
                if ("ar".equals(language)) {
                    //添加sdk语言，设置成阿拉伯语
                    Locale locale = new Locale("ar");
                    SharedPreferencesUtil.saveObject(context, "SobotLanguage", locale);
                }
                if ("he".equals(language)) {
                    //添加sdk语言，设置成希伯来文
                    Locale locale = new Locale("iw");
                    SharedPreferencesUtil.saveObject(context, "SobotLanguage", locale);
                }
                //保存客服设置的语言，例如en,zh_rtw等
                SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_USER_SETTTINNG_LANGUAGE, language);
                SharedPreferencesUtil.saveStringData(context, ZhiChiConstant.SOBOT_LANGUAGE_STRING_PATH, languagePath);
                SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_USE_LANGUAGE, true);
                LogUtils.i(" 国际化语言包保存路径:" + result.getPath());
            }

            @Override
            public void onError(Exception e, String msg, int responseCode) {
                LogUtils.i(" 国际化语言包下载失败:", e);
                SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_USE_LANGUAGE, false);
            }

            @Override
            public void inProgress(int progress) {
            }
        });
    }

    /**
     * 2.8.6
     * 隐藏消息列表中的时间消息
     * 因为服务端时间都是东八区时间，如果使用国际化语言例如 阿拉伯语，就会出现时间不准确的情况
     */
    public static void hideTimemsgForMessageList(final Context context, boolean isHide) {
        if (context == null) {
            return;
        }
        SharedPreferencesUtil.saveBooleanData(context, ZhiChiConstant.SOBOT_HIDE_TIMEMSG, isHide);
    }

    /**
     * 获取启动客服时传入的参数对象 Information
     *
     * @return Information
     */
    public static Information getCurrentInfoSetting(Context context) {
        if (context != null) {
            Information information = (Information) SharedPreferencesUtil.getObject(context, "sobot_last_current_info");
            return information;
        }
        return null;
    }

}
