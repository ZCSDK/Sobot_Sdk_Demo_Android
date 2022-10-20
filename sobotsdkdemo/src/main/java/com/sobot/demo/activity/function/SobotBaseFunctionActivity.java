package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.model.SobotDemoOtherModel;
import com.sobot.demo.util.AndroidBug5497Workaround;

public class SobotBaseFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left;
    private TextView tv_base_fun_3_1, tv_base_fun_3_2, sobot_tv_save;
    private EditText sobot_et_yuming, sobot_et_appkey, sobot_et_pingtaibiaoshi, sobot_et_pingtaimiyao, sobot_et_partnerid;
    private Information information;
    private SobotDemoOtherModel otherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_base_func_activity);
        AndroidBug5497Workaround.assistActivity(this);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        otherModel = (SobotDemoOtherModel) SobotSPUtil.getObject(getContext(), "sobot_demo_otherModel");
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("基础设置");
        sobot_tv_left.setOnClickListener(this);
        sobot_et_yuming = findViewById(R.id.sobot_et_yuming);
        sobot_et_appkey = findViewById(R.id.sobot_et_appkey);
        sobot_et_pingtaibiaoshi = findViewById(R.id.sobot_et_pingtaibiaoshi);
        sobot_et_pingtaimiyao = findViewById(R.id.sobot_et_pingtaimiyao);
        sobot_et_partnerid = findViewById(R.id.sobot_et_partnerid);

        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.VISIBLE);
        sobot_tv_save.setOnClickListener(this);

        if (information != null) {
            sobot_et_appkey.setText(information.getApp_key());
            sobot_et_partnerid.setText(information.getPartnerid());
        }

        if (otherModel != null) {
            if (!TextUtils.isEmpty(otherModel.getApi_host())) {
                sobot_et_yuming.setText(otherModel.getApi_host());
            }
            sobot_et_pingtaibiaoshi.setText(otherModel.getPlatformUnionCode());
            sobot_et_pingtaimiyao.setText(otherModel.getPlatformSecretkey());
        }


        tv_base_fun_3_1 = findViewById(R.id.tv_base_fun_3_1);
        tv_base_fun_3_1.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_3-1-域名设置");
        tv_base_fun_3_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", "https://www.sobot.com/developerdocs/app_sdk/android.html#_3-1-%E5%9F%9F%E5%90%8D%E8%AE%BE%E7%BD%AE");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        tv_base_fun_3_2 = findViewById(R.id.tv_base_fun_3_2);
        tv_base_fun_3_2.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_3-2-获取appkey");
        tv_base_fun_3_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", "https://www.sobot.com/developerdocs/app_sdk/android.html#_3-2-%E8%8E%B7%E5%8F%96appkey");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            finish();
        }
        if (v == sobot_tv_save) {
            String yuming = sobot_et_yuming.getText().toString().trim();
            if (information != null) {
                information.setApp_key(sobot_et_appkey.getText().toString().trim());
                information.setPartnerid(sobot_et_partnerid.getText().toString().trim());
                SobotSPUtil.saveObject(this, "sobot_demo_infomation", information);
            }
            if (otherModel != null) {
                String oldYUming = otherModel.getApi_host();
                if (!TextUtils.isEmpty(yuming)) {
                    if (!yuming.equals(oldYUming)) {
                        otherModel.setApi_host(yuming);
                        SobotSPUtil.saveObject(this, "sobot_demo_otherModel", otherModel);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                } else {
                    otherModel.setApi_host("https://api.sobot.com");
                }
                otherModel.setPlatformSecretkey(sobot_et_pingtaimiyao.getText().toString().trim());
                otherModel.setPlatformUnionCode(sobot_et_pingtaibiaoshi.getText().toString().trim());
                SobotSPUtil.saveObject(this, "sobot_demo_otherModel", otherModel);
            }
            ToastUtil.showToast(getContext(), "已保存");
            SharedPreferencesUtil.saveBooleanData(this, ZhiChiConstant.SOBOT_CONFIG_INITSDK, false);
            finish();
        }

    }

    public Context getContext() {
        return this;
    }
}