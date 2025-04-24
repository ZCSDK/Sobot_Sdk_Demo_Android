package com.sobot.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.demo.activity.product.SobotDemoCloudCallActivity;
import com.sobot.demo.activity.product.SobotDemoCustomActivity;
import com.sobot.demo.activity.product.SobotDemoRobotActivity;
import com.sobot.demo.activity.product.SobotDemoWorkOrderActivity;

public class SobotDemoWelcomeFragment extends Fragment implements View.OnClickListener {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.sobot_demo_welcome_fragment, null);
        findViewsById();
        return view;
    }

    private void findViewsById() {
        LinearLayout sobot_robot_layout = (LinearLayout) view.findViewById(R.id.sobot_demo_robot_layout);
        LinearLayout sobot_demo_custom_service_layout = (LinearLayout) view.findViewById(R.id.sobot_demo_custom_service_layout);
        LinearLayout sobot_demo_cloud_call_layout = (LinearLayout) view.findViewById(R.id.sobot_demo_cloud_call_layout);
        LinearLayout sobot_demo_work_roder_layout = (LinearLayout) view.findViewById(R.id.sobot_demo_work_roder_layout);
        TextView sobot_tv_right = (TextView) view.findViewById(R.id.sobot_tv_right);
        sobot_tv_right.setOnClickListener(this);
        sobot_robot_layout.setOnClickListener(this);
        sobot_demo_custom_service_layout.setOnClickListener(this);
        sobot_demo_cloud_call_layout.setOnClickListener(this);
        sobot_demo_work_roder_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();

        if (id == R.id.sobot_demo_robot_layout) {
            // 机器人客服演示
            intent = new Intent(getActivity(), SobotDemoRobotActivity.class);
            startActivity(intent);
        } else if (id == R.id.sobot_demo_custom_service_layout) {
            // 自定义客服演示
            intent = new Intent(getActivity(), SobotDemoCustomActivity.class);
            startActivity(intent);
        } else if (id == R.id.sobot_demo_cloud_call_layout) {
            // 云呼叫演示
            intent = new Intent(getActivity(), SobotDemoCloudCallActivity.class);
            startActivity(intent);
        } else if (id == R.id.sobot_demo_work_roder_layout) {
            // 工单系统演示
            intent = new Intent(getActivity(), SobotDemoWorkOrderActivity.class);
            startActivity(intent);
        } else if (id == R.id.sobot_tv_right) {
            // 启动在线客服
            Information information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
            if (information != null) {
                // 检查 app_key
                if (TextUtils.isEmpty(information.getApp_key())) {
                    ToastUtil.showCustomToast(getActivity(), "appkey不能为空,请前往基础设置中设置");
                    return;
                }
                // 检查 SDK 初始化状态
                boolean initSdk = SharedPreferencesUtil.getBooleanData(getActivity(), ZhiChiConstant.SOBOT_CONFIG_INITSDK, false);
                if (!initSdk) {
                    ToastUtil.showCustomToast(getActivity(), "请前往基础设置中初始化后再启动");
                    return;
                }
                // 处理自定义语言
                String languageValue = SobotSPUtil.getStringData(getActivity(), "custom_language_value", "");
                if (!TextUtils.isEmpty(languageValue)) {
                    ZCSobotApi.setInternationalLanguage(getActivity(), languageValue, true, false);
                }
                // 启动聊天窗口
                ZCSobotApi.openZCChat(getContext(), information);
            }
        }

    }
}