package com.sobot.demo;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.sobot.chat.SobotApi;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.apiUtils.SobotBaseUrl;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotTransferOperatorParam;
import com.sobot.chat.listener.SobotPlusMenuListener;
import com.sobot.chat.listener.SobotTransferOperatorInterceptor;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelUploadView;
import com.sobot.demo.model.SobotDemoOtherModel;
import com.umeng.commonsdk.UMConfigure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/29.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ZCSobotApi.setShowDebug(true);
        Information information = (Information) SobotSPUtil.getObject(this, "sobot_demo_infomation");
        if (information == null) {
            SobotSPUtil.saveObject(this, "sobot_demo_infomation", new Information());
        }

        SobotDemoOtherModel otherModel = (SobotDemoOtherModel) SobotSPUtil.getObject(this, "sobot_demo_otherModel");
        if (otherModel == null) {
            SobotSPUtil.saveObject(this, "sobot_demo_otherModel", new SobotDemoOtherModel());
        } else {
            if (!TextUtils.isEmpty(otherModel.getApi_host())) {
                SobotBaseUrl.setApi_Host(otherModel.getApi_host());
            }
        }

        //友盟 初始化SDK
        UMConfigure.init(this, "5e66514bdbc2ec0777a3acd7", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(true);
    }

}