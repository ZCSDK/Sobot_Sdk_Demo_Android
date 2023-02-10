package com.sobot.demo;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.apiUtils.SobotBaseUrl;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.MiniProgramModel;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.api.model.SobotLocationModel;
import com.sobot.chat.listener.NewHyperlinkListener;
import com.sobot.chat.listener.SobotFunctionClickListener;
import com.sobot.chat.listener.SobotFunctionType;
import com.sobot.chat.listener.SobotMapCardListener;
import com.sobot.chat.listener.SobotMiniProgramClickListener;
import com.sobot.chat.listener.SobotPlusMenuListener;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.StMapOpenHelper;
import com.sobot.chat.utils.ToastUtil;
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
//        Fresco.initialize(this);
        ZCSobotApi.setShowDebug(true);
        SobotDemoOtherModel otherModel = (SobotDemoOtherModel) SobotSPUtil.getObject(this, "sobot_demo_otherModel");
        if (otherModel == null) {
            SobotSPUtil.saveObject(this, "sobot_demo_otherModel", new SobotDemoOtherModel());
        } else {
            if (!TextUtils.isEmpty(otherModel.getApi_host())) {
                SobotBaseUrl.setApi_Host(otherModel.getApi_host());
            }
        }
        Information information = (Information) SobotSPUtil.getObject(this, "sobot_demo_infomation");
        if (information == null) {
            SobotSPUtil.saveObject(this, "sobot_demo_infomation", new Information());
        }
        if (information != null) {
            if (TextUtils.isEmpty(information.getApp_key())) {
                ToastUtil.showCustomToast(this, "appkey不能为空,请前往基础设置中设置");
                return;
            }
            ZCSobotApi.initSobotSDK(this, information.getApp_key(), information.getPartnerid());
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

        ZCSobotApi.setMiniProgramClickListener(new SobotMiniProgramClickListener() {
            @Override
            public void onClick(Context context, MiniProgramModel miniProgramModel) {
                ToastUtil.showToast(getApplicationContext(), "小程序拦截了="+miniProgramModel.toString() );
            }
        });

        // 链接的点击事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
        // 可为订单号,商品详情地址等等;客户可自定义规则拦截,返回true时会把自定义的信息返回
        // 拦截范围  （帮助中心、留言、聊天、留言记录、商品卡片，订单卡片）
        ZCSobotApi.setNewHyperlinkListener(new NewHyperlinkListener() {
            @Override
            public boolean onUrlClick(Context context, String url) {
                if (url.contains("baidu1")) {
                    ToastUtil.showToast(getApplicationContext(), "点击了超链接，url=" + url);
                    //如果url链接是百度,拦截
                    //do().....
                    return true;
                }
                return false;
            }

            @Override
            public boolean onEmailClick(Context context, String email) {
                ToastUtil.showToast(getApplicationContext(), "点击了邮件，email=" + email);
                return false;
            }

            @Override
            public boolean onPhoneClick(Context context, String phone) {
                ToastUtil.showToast(getApplicationContext(), "点击了电话，phone=" + phone);
                return false;
            }
        });
        final String ACTION_LOCATION = "sobot_action_location";
        //位置
        final ChattingPanelUploadView.SobotPlusEntity locationEntity = new ChattingPanelUploadView.SobotPlusEntity(ResourceUtils.getDrawableId(getApplicationContext(), "sobot_location_btn_selector"), "位置", ACTION_LOCATION);
        List<ChattingPanelUploadView.SobotPlusEntity> tmpList = new ArrayList<>();
        tmpList.add(locationEntity);

        //发送订单卡片
        final String ACTION_SEND_ORDERCARD = "sobot_action_send_ordercard";
        ChattingPanelUploadView.SobotPlusEntity ordercardEntity = new ChattingPanelUploadView.SobotPlusEntity(ResourceUtils.getDrawableId(getApplicationContext(), "sobot_ordercard_btn_selector"), "订单", ACTION_SEND_ORDERCARD);
        tmpList.add(ordercardEntity);
        SobotUIConfig.pulsMenu.operatorMenus = tmpList;

        SobotUIConfig.pulsMenu.sSobotPlusMenuListener = new SobotPlusMenuListener() {
            @Override
            public void onClick(View view, String action) {
                if (ACTION_SEND_ORDERCARD.equals(action)) {
                    Context context = view.getContext();
                    List<OrderCardContentModel.Goods> goodsList = new ArrayList<>();
                    goodsList.add(new OrderCardContentModel.Goods("苹果", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
                    goodsList.add(new OrderCardContentModel.Goods("苹果1111111", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
                    goodsList.add(new OrderCardContentModel.Goods("苹果2222", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
                    goodsList.add(new OrderCardContentModel.Goods("苹果33333333", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
                    OrderCardContentModel orderCardContent = new OrderCardContentModel();
                    //订单编号（必填）
                    orderCardContent.setOrderCode("zc32525235425");
                    //订单状态
                    orderCardContent.setOrderStatus(0);
                    orderCardContent.setStatusCustom("自定义状态");
                    //订单总金额
                    orderCardContent.setTotalFee(1234);
                    //订单商品总数
                    orderCardContent.setGoodsCount("4");
                    //订单链接
                    orderCardContent.setOrderUrl("https://item.jd.com/1765513297.html");
                    //订单创建时间
                    orderCardContent.setCreateTime(System.currentTimeMillis() + "");
                    //订单商品集合
                    orderCardContent.setGoods(goodsList);
                    ZCSobotApi.sendOrderGoodsInfo(context, orderCardContent);
                }

                if (ACTION_LOCATION.equals(action)) {
                    Context context = view.getContext();
                    //在地图定位页面获取位置信息后发送给客服：
                    SobotLocationModel locationData = new SobotLocationModel();
                    //地图快照，必须传入本地图片地址，注意：如果不传会显示默认的地图图片
                    // locationData.setSnapshot(Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.png");
                    //纬度
                    locationData.setLat("40.007486");
                    //经度
                    locationData.setLng("116.360362");
                    //标点名称
                    locationData.setLocalName("金码大厦");
                    //标点地址
                    locationData.setLocalLabel("北京市海淀区六道口金码大厦");
                    ZCSobotApi.sendLocation(context, locationData);

                }
            }
        };
        //智齿部分功能点击的监听 2.9.7
        ZCSobotApi.setFunctionClickListener(new SobotFunctionClickListener() {
            @Override
            public void onClickFunction(Context context, SobotFunctionType functionType) {
//                ToastUtil.showCustomToast(context, functionType.toString()+" 对应值：  1:留言返回,2:会话页面返回,3:帮助中心返回,4:电商消息中心页面返回,5:电话联系客服");
            }
        });

        //优先打开百度地图，没有安装百度地图，再打开高德地图；可拦截拦截位置卡片点击事件，自己处理
        SobotOption.mapCardListener = new SobotMapCardListener() {
            @Override
            public boolean onClickMapCradMsg(Context context, SobotLocationModel locationModel) {
                if (context != null && locationModel != null) {
                    ToastUtil.showCustomToast(context, "点击拦截位置卡片事件");
                    StMapOpenHelper.firstOpenGaodeMap(context, locationModel);
//                    StMapOpenHelper.firstOpenBaiduMap(context, locationModel);
                }
                return true;
            }
        };
    }


}