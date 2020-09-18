package com.sobot.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.SobotApi;
import com.sobot.chat.ZCSobotApi;
import com.sobot.demo.activity.more.SobotAccessSetActivity;
import com.sobot.demo.activity.more.SobotCloseButtonSetActivity;
import com.sobot.demo.activity.more.SobotDemoMasterActivity;
import com.sobot.demo.activity.more.SobotNotificationSetActivity;
import com.sobot.demo.activity.more.SobotPersonSetActivity;
import com.sobot.demo.activity.more.SobotReceptionistIdSetActivity;
import com.sobot.demo.activity.more.SobotRobotSetActivity;
import com.sobot.demo.activity.more.SobotSatisfactionSetActivity;
import com.sobot.demo.activity.more.SobotSkillSetActivity;
import com.sobot.demo.activity.more.SobotSwitchAllSetActivity;

public class SobotDemoSettingFragment extends Fragment implements View.OnClickListener {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.sobot_demo_setting_fragment, null);
        findViewsById();
        return view;
    }

    private void findViewsById() {
        RelativeLayout rl_1 = (RelativeLayout) view.findViewById(R.id.rl_1);
        RelativeLayout rl_2 = (RelativeLayout) view.findViewById(R.id.rl_2);
        RelativeLayout rl_5 = (RelativeLayout) view.findViewById(R.id.rl_5);
        RelativeLayout rl_6 = (RelativeLayout) view.findViewById(R.id.rl_6);
        RelativeLayout rl_7 = (RelativeLayout) view.findViewById(R.id.rl_7);
        RelativeLayout rl_8 = (RelativeLayout) view.findViewById(R.id.rl_8);
        RelativeLayout rl_10 = (RelativeLayout) view.findViewById(R.id.rl_10);
        RelativeLayout rl_11 = (RelativeLayout) view.findViewById(R.id.rl_11);
        RelativeLayout rl_12 = (RelativeLayout) view.findViewById(R.id.rl_12);
        RelativeLayout rl_13 = (RelativeLayout) view.findViewById(R.id.rl_13);
        RelativeLayout rl_14 = (RelativeLayout) view.findViewById(R.id.rl_14);
        RelativeLayout rl_15 = (RelativeLayout) view.findViewById(R.id.rl_15);

        RelativeLayout sobot_tv_left = (RelativeLayout) view.findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left.setVisibility(View.GONE);
        TextView sobot_text_title = (TextView) view.findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("设置");

        TextView tv_16 = (TextView) view.findViewById(R.id.tv_16);
        tv_16.setText("V" + ZCSobotApi.getVersion(getActivity()));

        rl_1.setOnClickListener(this);
        rl_2.setOnClickListener(this);
        rl_5.setOnClickListener(this);
        rl_6.setOnClickListener(this);
        rl_7.setOnClickListener(this);
        rl_8.setOnClickListener(this);
        rl_10.setOnClickListener(this);
        rl_11.setOnClickListener(this);
        rl_12.setOnClickListener(this);
        rl_13.setOnClickListener(this);
        rl_14.setOnClickListener(this);
        rl_15.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_13://基本设置(必须)
                intent = new Intent(getActivity(), SobotDemoMasterActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_1://接入信息设置
                intent = new Intent(getActivity(), SobotAccessSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_2://用户信息设置
                intent = new Intent(getActivity(), SobotPersonSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_5://对接客服设置
                intent = new Intent(getActivity(), SobotReceptionistIdSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_6://对接技能组设置
                intent = new Intent(getActivity(), SobotSkillSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_7://对接机器人设置
                intent = new Intent(getActivity(), SobotRobotSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_8://评价设置
                intent = new Intent(getActivity(), SobotSatisfactionSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_14://关闭设置
                intent = new Intent(getActivity(), SobotCloseButtonSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_15://全局开关设置
                intent = new Intent(getActivity(), SobotSwitchAllSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_10://推送设置
                intent = new Intent(getActivity(), SobotNotificationSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_11://结束会话设置
                // 退出智齿客服、断开与服务器的连接,
                final SobotDemoExitDialog reSendDialog = new SobotDemoExitDialog(getActivity());
                reSendDialog.setOnClickListener(new SobotDemoExitDialog.OnItemClick() {
                    @Override
                    public void OnClick(int type) {
                        if (type == 0) {// 0：确定 1：取消
                            SobotApi.exitSobotChat(getActivity());
                        }
                        reSendDialog.dismiss();
                    }
                });
                reSendDialog.show();
                break;
            case R.id.rl_12:
                SobotUtils.startSobot(getContext(),"1");
                break;
            default:
                break;
        }
    }

}