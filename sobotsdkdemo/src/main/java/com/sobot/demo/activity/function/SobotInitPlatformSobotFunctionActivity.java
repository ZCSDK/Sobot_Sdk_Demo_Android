package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.model.SobotDemoOtherModel;

public class SobotInitPlatformSobotFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left;
    private TextView tv_start_fun_3_3_2, sobot_tv_save, sobot_tv_init_platform;
    private Information information;
    private SobotDemoOtherModel otherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_initplatform_sobot_func_activity);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        otherModel = (SobotDemoOtherModel) SobotSPUtil.getObject(getContext(), "sobot_demo_otherModel");
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("初始化电商版");
        sobot_tv_left.setOnClickListener(this);
        tv_start_fun_3_3_2 = (TextView) findViewById(R.id.tv_start_fun_3_3_2);
        tv_start_fun_3_3_2.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_3-3-2-电商版");
        tv_start_fun_3_3_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", "https://www.sobot.com/developerdocs/app_sdk/android.html#_3-3-2-%E7%94%B5%E5%95%86%E7%89%88");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.GONE);
        sobot_tv_init_platform = findViewById(R.id.sobot_tv_init_platform);
        sobot_tv_init_platform.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            finish();
        }
        if (v == sobot_tv_init_platform) {
            if (otherModel != null) {
                if (TextUtils.isEmpty(otherModel.getPlatformUnionCode())) {
                    ToastUtil.showCustomToast(getContext(), "平台id不能为空,请前往基础设置中设置");
                    return;
                }
            }
            if (information != null) {
                if (TextUtils.isEmpty(information.getApp_key())) {
                    ToastUtil.showCustomToast(getContext(), "appkey不能为空,请前往基础设置中设置");
                    return;
                }
                ZCSobotApi.initSobotSDK(getContext(), information.getApp_key(), information.getPartnerid());
                ZCSobotApi.initPlatformUnion(getContext(), otherModel.getPlatformUnionCode(), otherModel.getPlatformSecretkey());
                ToastUtil.showCustomToast(getContext(), "已初始化");
                finish();
            }
        }

    }

    public Context getContext() {
        return this;
    }
}