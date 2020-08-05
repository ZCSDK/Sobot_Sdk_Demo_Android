package com.sobot.demo;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.SobotApi;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.base.SobotBaseActivity;
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

import static android.view.View.GONE;

public class SobotDemoSettingActivity extends SobotBaseActivity implements View.OnClickListener {


    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_demo_setting_fragment;
    }

    @Override
    protected void initView() {
        RelativeLayout rl_1 = (RelativeLayout) findViewById(R.id.rl_1);
        RelativeLayout rl_2 = (RelativeLayout) findViewById(R.id.rl_2);
        RelativeLayout rl_5 = (RelativeLayout) findViewById(R.id.rl_5);
        RelativeLayout rl_6 = (RelativeLayout) findViewById(R.id.rl_6);
        RelativeLayout rl_7 = (RelativeLayout) findViewById(R.id.rl_7);
        RelativeLayout rl_8 = (RelativeLayout) findViewById(R.id.rl_8);
        RelativeLayout rl_10 = (RelativeLayout) findViewById(R.id.rl_10);
        RelativeLayout rl_11 = (RelativeLayout) findViewById(R.id.rl_11);
        RelativeLayout rl_12 = (RelativeLayout) findViewById(R.id.rl_12);
        RelativeLayout rl_13 = (RelativeLayout) findViewById(R.id.rl_13);
        RelativeLayout rl_14 = (RelativeLayout) findViewById(R.id.rl_14);
        RelativeLayout rl_15 = (RelativeLayout) findViewById(R.id.rl_15);
        RelativeLayout rl_17 = (RelativeLayout) findViewById(R.id.rl_17);

        RelativeLayout sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left.setVisibility(GONE);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("智齿客服SDK Demo");

        TextView tv_16 = (TextView) findViewById(R.id.tv_16);
        tv_16.setText("V" + ZCSobotApi.getVersion(SobotDemoSettingActivity.this));

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
        rl_17.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_13://基本设置(必须)
                intent = new Intent(SobotDemoSettingActivity.this, SobotDemoMasterActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_1://接入信息设置
                intent = new Intent(SobotDemoSettingActivity.this, SobotAccessSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_2://用户信息设置
                intent = new Intent(SobotDemoSettingActivity.this, SobotPersonSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_5://对接客服设置
                intent = new Intent(SobotDemoSettingActivity.this, SobotReceptionistIdSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_6://对接技能组设置
                intent = new Intent(SobotDemoSettingActivity.this, SobotSkillSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_7://对接机器人设置
                intent = new Intent(SobotDemoSettingActivity.this, SobotRobotSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_8://评价设置
                intent = new Intent(SobotDemoSettingActivity.this, SobotSatisfactionSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_14://关闭设置
                intent = new Intent(SobotDemoSettingActivity.this, SobotCloseButtonSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_15://全局开关设置
                intent = new Intent(SobotDemoSettingActivity.this, SobotSwitchAllSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_10://推送设置
                intent = new Intent(SobotDemoSettingActivity.this, SobotNotificationSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_11://结束会话设置
                // 退出智齿客服、断开与服务器的连接,
                final SobotDemoExitDialog reSendDialog = new SobotDemoExitDialog(SobotDemoSettingActivity.this);
                reSendDialog.setOnClickListener(new SobotDemoExitDialog.OnItemClick() {
                    @Override
                    public void OnClick(int type) {
                        if (type == 0) {// 0：确定 1：取消
                            SobotApi.exitSobotChat(SobotDemoSettingActivity.this);
                        }
                        reSendDialog.dismiss();
                    }
                });
                reSendDialog.show();
                break;
            case R.id.rl_12:
                SobotUtils.startSobot(SobotDemoSettingActivity.this, "1");
                break;
            case R.id.rl_17://
                SobotUtils.startSobot(SobotDemoSettingActivity.this, "2");
                break;
            default:
                break;
        }
    }
}