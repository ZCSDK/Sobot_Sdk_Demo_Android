package com.sobot.demo;

import android.app.Application;
import android.text.TextUtils;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.apiUtils.SobotBaseUrl;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.listener.NewHyperlinkListener;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.demo.model.SobotDemoOtherModel;
import com.umeng.commonsdk.UMConfigure;

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

        //是否在申请权限前弹出权限用途提示框,默认不弹
        ZCSobotApi.setSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP, SobotSPUtil.getBooleanData(this, "show_permission_tips_pop", false));
        ZCSobotApi.setSwitchMarkStatus(MarkConfig.AUTO_MATCH_TIMEZONE, SobotSPUtil.getBooleanData(this, "auto_match_timezone", false));
        ZCSobotApi.setSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN, SobotSPUtil.getBooleanData(this, "landscape_screen", false));
        ZCSobotApi.setSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH, SobotSPUtil.getBooleanData(this, "display_innotch", false));
        ZCSobotApi.setSwitchMarkStatus(MarkConfig.LEAVE_COMPLETE_CAN_REPLY, SobotSPUtil.getBooleanData(this, "leave_complete_can_reply", false));

        //友盟 初始化SDK
        UMConfigure.init(this, "5e66514bdbc2ec0777a3acd7", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(true);

        //-----------拦截事件️-------------

        // 链接的点击事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
        // 可为订单号,商品详情地址等等;客户可自定义规则拦截,返回true时会把自定义的信息返回
        // 拦截范围  （帮助中心、留言、聊天、留言记录、商品卡片，订单卡片）
        ZCSobotApi.setNewHyperlinkListener(new NewHyperlinkListener() {
            @Override
            public boolean onUrlClick(String url) {
                if (url.contains("baidu")) {
                    ToastUtil.showToast(getApplicationContext(), "点击了超链接，url=" + url);
                    //如果url链接是百度,拦截
                    //do().....
                    return true;
                }
                return false;
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
    }

}