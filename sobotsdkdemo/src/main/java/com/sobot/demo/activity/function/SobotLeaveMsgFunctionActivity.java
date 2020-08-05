package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.sobot.chat.MarkConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.enumtype.SobotAutoSendMsgMode;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.SobotJsonUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.model.SobotDemoOtherModel;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SobotLeaveMsgFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText sobot_et_leaveCusFieldMap, sobot_et_leaveMsgGroupId;
    private RelativeLayout sobot_tv_left, sobot_rl_4_3_5, sobot_rl_4_3_7;
    private ImageView sobotImage435, sobotImage437;
    private boolean status435, status437;
    private TextView tv_leavemsg_fun_4_3_5, tv_leavemsg_fun_4_3_7, tv_leavemsg_fun_4_3_8, sobot_tv_save;
    private Information information;
    private SobotDemoOtherModel otherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_leavemsg_func_activity);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        otherModel = (SobotDemoOtherModel) SobotSPUtil.getObject(getContext(), "sobot_demo_otherModel");
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("留言工单相关");
        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.VISIBLE);
        sobot_tv_save.setOnClickListener(this);

        sobot_rl_4_3_5 = (RelativeLayout) findViewById(R.id.sobot_rl_4_3_5);
        sobot_rl_4_3_5.setOnClickListener(this);
        sobotImage435 = (ImageView) findViewById(R.id.sobot_image_4_3_5);

        sobot_rl_4_3_7 = (RelativeLayout) findViewById(R.id.sobot_rl_4_3_7);
        sobot_rl_4_3_7.setOnClickListener(this);
        sobotImage437 = (ImageView) findViewById(R.id.sobot_image_4_3_7);


        sobot_et_leaveCusFieldMap = findViewById(R.id.sobot_et_leaveCusFieldMap);
        sobot_et_leaveMsgGroupId = findViewById(R.id.sobot_et_leaveMsgGroupId);

        if (information != null) {
            sobot_et_leaveMsgGroupId.setText(TextUtils.isEmpty(information.getLeaveMsgGroupId()) ? "" : information.getLeaveMsgGroupId());
            if (information.getLeaveCusFieldMap() != null) {
                try {
                    String str = JSON.toJSONString(information.getLeaveCusFieldMap());
                    sobot_et_leaveCusFieldMap.setText(TextUtils.isEmpty(str) ? "" : str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            status435 = ZCSobotApi.getSwitchMarkStatus(MarkConfig.LEAVE_COMPLETE_CAN_REPLY);
            setImageShowStatus(status435, sobotImage435);
            status437 = information.isShowLeaveDetailBackEvaluate();
            setImageShowStatus(status437, sobotImage437);
        }

        tv_leavemsg_fun_4_3_5 = findViewById(R.id.tv_leavemsg_fun_4_3_5);
        tv_leavemsg_fun_4_3_5.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-3-5-已完成状态的留言详情界面的回复按钮可通过参数配置是否显示");
        setOnClick(tv_leavemsg_fun_4_3_5, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-3-5-%E5%B7%B2%E5%AE%8C%E6%88%90%E7%8A%B6%E6%80%81%E7%9A%84%E7%95%99%E8%A8%80%E8%AF%A6%E6%83%85%E7%95%8C%E9%9D%A2%E7%9A%84%E5%9B%9E%E5%A4%8D%E6%8C%89%E9%92%AE%E5%8F%AF%E9%80%9A%E8%BF%87%E5%8F%82%E6%95%B0%E9%85%8D%E7%BD%AE%E6%98%AF%E5%90%A6%E6%98%BE%E7%A4%BA");
        tv_leavemsg_fun_4_3_7 = findViewById(R.id.tv_leavemsg_fun_4_3_7);
        tv_leavemsg_fun_4_3_7.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-3-7-添加留言评价主动提醒开关");
        setOnClick(tv_leavemsg_fun_4_3_7, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-3-7-%E6%B7%BB%E5%8A%A0%E7%95%99%E8%A8%80%E8%AF%84%E4%BB%B7%E4%B8%BB%E5%8A%A8%E6%8F%90%E9%86%92%E5%BC%80%E5%85%B3");
        tv_leavemsg_fun_4_3_8 = findViewById(R.id.tv_leavemsg_fun_4_3_8);
        tv_leavemsg_fun_4_3_8.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-3-8-添加留言扩展参数");
        setOnClick(tv_leavemsg_fun_4_3_8, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-3-8-%E6%B7%BB%E5%8A%A0%E7%95%99%E8%A8%80%E6%89%A9%E5%B1%95%E5%8F%82%E6%95%B0");
    }

    public void setOnClick(TextView view, final String url) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sobot_tv_save:
                if (information != null) {
                    String leaveCusFieldMap = sobot_et_leaveCusFieldMap.getText().toString().trim();
                    try {
                        Map lcMap = (Map<String, String>) JSON.parse(leaveCusFieldMap);
                        information.setLeaveCusFieldMap(lcMap);
                    } catch (Exception e) {

                    }

                    String leaveMsgGroupId = sobot_et_leaveMsgGroupId.getText().toString().trim();
                    information.setLeaveMsgGroupId(leaveMsgGroupId);
                    //已完成状态的留言，是否可持续回复 true 持续回复 ，false 不可继续回复 ；默认 true 用户可一直持续回复
                    ZCSobotApi.setSwitchMarkStatus(MarkConfig.LEAVE_COMPLETE_CAN_REPLY, status435);
                    //添加留言评价主动提醒开关
                    information.setShowLeaveDetailBackEvaluate(status437);

                    SobotSPUtil.saveObject(this, "sobot_demo_infomation", information);
                }
                ToastUtil.showToast(getContext(), "已保存");
                finish();
                break;

            case R.id.sobot_rl_4_3_5:
                status435 = !status435;
                setImageShowStatus(status435, sobotImage435);
                break;
            case R.id.sobot_rl_4_3_7:
                status437 = !status437;
                setImageShowStatus(status437, sobotImage437);
                break;

        }

    }

    private void setImageShowStatus(boolean status, ImageView imageView) {
        if (status) {
            imageView.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            imageView.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    public Context getContext() {
        return this;
    }
}