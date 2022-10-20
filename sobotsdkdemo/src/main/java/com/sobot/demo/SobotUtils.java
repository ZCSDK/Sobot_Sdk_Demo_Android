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
import com.sobot.chat.utils.ZhiChiConstant;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.sobot.chat.api.apiUtils.SobotApp.getApplicationContext;

/**
 * Created by Administrator on 2017/12/12.
 */

public class SobotUtils {

    /**
     * @param context
     * @param startType "1" 启动客服界面 ,"2"启动客户中心
     */
    public static void startSobot(Context context, String startType) {
        if (context == null) {
            return;
        }

        Information information = (Information) SobotSPUtil.getObject(context, "sobot_demo_infomation");
        if (information != null) {
            if (TextUtils.isEmpty(information.getApp_key())) {
                ToastUtil.showCustomToast(context, "appkey不能为空,请前往基础设置中设置");
                return;
            }
            boolean initSdk = SharedPreferencesUtil.getBooleanData(context, ZhiChiConstant.SOBOT_CONFIG_INITSDK, false);
            if (!initSdk) {
                ToastUtil.showCustomToast(context, "请前往基础设置中初始化后再启动");
                return;
            }
            if(startType.equals("1")){
                ZCSobotApi.openZCChat(context, information);
            }else {
                ZCSobotApi.openZCServiceCenter(context,information);
            }

        }
    }
}