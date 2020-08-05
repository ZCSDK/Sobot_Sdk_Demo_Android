package com.sobot.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.demo.activity.function.SobotBaseFunctionActivity;
import com.sobot.demo.activity.function.SobotCustomUiFunctionActivity;
import com.sobot.demo.activity.function.SobotEndSobotFunctionActivity;
import com.sobot.demo.activity.function.SobotInfomationFunctionActivity;
import com.sobot.demo.activity.function.SobotInitPlatformSobotFunctionActivity;
import com.sobot.demo.activity.function.SobotInitSobotFunctionActivity;
import com.sobot.demo.activity.function.SobotLeaveMsgFunctionActivity;
import com.sobot.demo.activity.function.SobotManualFunctionActivity;
import com.sobot.demo.activity.function.SobotMessageFunctionActivity;
import com.sobot.demo.activity.function.SobotOtherFunctionActivity;
import com.sobot.demo.activity.function.SobotReobotFunctionActivity;
import com.sobot.demo.activity.function.SobotSatisfactionFunctionActivity;
import com.sobot.demo.activity.function.SobotStartHelpCenterFunctionActivity;
import com.sobot.demo.activity.function.SobotStartSobotFunctionActivity;
import com.sobot.demo.model.SobotDemoOtherModel;

public class SobotDemoNewSettingFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Information information;
    private SobotDemoOtherModel otherModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.sobot_demo_new_setting_fragment, null);
        findViewsById();
        return view;
    }

    private void findViewsById() {
        TextView sobot_text_title = (TextView) view.findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("功能说明");

        RelativeLayout rl_1 = (RelativeLayout) view.findViewById(R.id.rl_1);
        rl_1.setOnClickListener(this);
        RelativeLayout rl_2 = (RelativeLayout) view.findViewById(R.id.rl_2);
        rl_2.setOnClickListener(this);
        RelativeLayout rl_3 = (RelativeLayout) view.findViewById(R.id.rl_3);
        rl_3.setOnClickListener(this);
        RelativeLayout rl_4 = (RelativeLayout) view.findViewById(R.id.rl_4);
        rl_4.setOnClickListener(this);
        RelativeLayout rl_5 = (RelativeLayout) view.findViewById(R.id.rl_5);
        rl_5.setOnClickListener(this);
        RelativeLayout rl_6 = (RelativeLayout) view.findViewById(R.id.rl_6);
        rl_6.setOnClickListener(this);
        RelativeLayout rl_7 = (RelativeLayout) view.findViewById(R.id.rl_7);
        rl_7.setOnClickListener(this);
        RelativeLayout rl_8 = (RelativeLayout) view.findViewById(R.id.rl_8);
        rl_8.setOnClickListener(this);
        RelativeLayout rl_9 = (RelativeLayout) view.findViewById(R.id.rl_9);
        rl_9.setOnClickListener(this);
        RelativeLayout rl_10 = (RelativeLayout) view.findViewById(R.id.rl_10);
        rl_10.setOnClickListener(this);
        RelativeLayout rl_11 = (RelativeLayout) view.findViewById(R.id.rl_11);
        rl_11.setOnClickListener(this);
        RelativeLayout rl_12 = (RelativeLayout) view.findViewById(R.id.rl_12);
        rl_12.setOnClickListener(this);
        RelativeLayout rl_13 = (RelativeLayout) view.findViewById(R.id.rl_13);
        rl_13.setOnClickListener(this);
        RelativeLayout rl_14 = (RelativeLayout) view.findViewById(R.id.rl_14);
        rl_14.setOnClickListener(this);
        RelativeLayout rl_15 = (RelativeLayout) view.findViewById(R.id.rl_15);
        rl_15.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        boolean initSdk = SharedPreferencesUtil.getBooleanData(getActivity(), ZhiChiConstant.SOBOT_CONFIG_INITSDK, false);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        otherModel = (SobotDemoOtherModel) SobotSPUtil.getObject(getContext(), "sobot_demo_otherModel");
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_1://基础设置
                intent = new Intent(getActivity(), SobotBaseFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_2://初始化普通版
                intent = new Intent(getActivity(), SobotInitSobotFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_3://初始化电商版
                intent = new Intent(getActivity(), SobotInitPlatformSobotFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_4://启动客服
                if (!initSdk) {
                    ToastUtil.showCustomToast(getActivity(), "请先初始化再启动");
                    return;
                }
                if (information != null) {
                    if (TextUtils.isEmpty(information.getApp_key())) {
                        ToastUtil.showCustomToast(getActivity(), "appkey不能为空,请前往基础设置中设置");
                        return;
                    }
                    intent = new Intent(getActivity(), SobotStartSobotFunctionActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rl_5://启动商家列表
                if (information != null) {
                    ZCSobotApi.openZCChatListView(getActivity(), information.getPartnerid());
                }
                break;
            case R.id.rl_6://启动客户帮助中心
                if (!initSdk) {
                    ToastUtil.showCustomToast(getActivity(), "请先初始化再启动");
                    return;
                }
                if (information != null) {
                    if (TextUtils.isEmpty(information.getApp_key())) {
                        ToastUtil.showCustomToast(getActivity(), "appkey不能为空,请前往基础设置中设置");
                        return;
                    }
                    intent = new Intent(getActivity(), SobotStartHelpCenterFunctionActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rl_7://机器人客服
                intent = new Intent(getActivity(), SobotReobotFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_8://人工客服
                intent = new Intent(getActivity(), SobotManualFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_9://留言工单相关
                intent = new Intent(getActivity(), SobotLeaveMsgFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_10://评价
                intent = new Intent(getActivity(), SobotSatisfactionFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_11://消息相关
                intent = new Intent(getActivity(), SobotMessageFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_12://自定义UI设置
                intent = new Intent(getActivity(), SobotCustomUiFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_13://其它配置
                intent = new Intent(getActivity(), SobotOtherFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_14://Information类说明
                intent = new Intent(getActivity(), SobotInfomationFunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_15://结束会话
                intent = new Intent(getActivity(), SobotEndSobotFunctionActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}