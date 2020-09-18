package com.sobot.demo.activity.more;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.api.apiUtils.SobotBaseUrl;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;

/**
 * Created by Administrator on 2017/11/20.
 */

public class SobotDemoMasterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText sobot_appkey;//appkey
    private EditText sobot_partnerId;//partnerId唯一标识用户
    private EditText ed_hot_question;
    private EditText sobot_api_host;
    private RelativeLayout rl_;
    private RelativeLayout sobot_tv_left;
    private int mDeveloperModeCount = 0;

    private EditText et_sobot_custom_language;

    private EditText
            key1,
            key2,
            key3,
            value1,
            value2,
            value3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_demo_master_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        inifViews();
    }

    private void inifViews() {
        key1 = findViewById(R.id.key1);
        key2 = findViewById(R.id.key2);
        key3 = findViewById(R.id.key3);
        value1 = findViewById(R.id.value1);
        value2 = findViewById(R.id.value2);
        value3 = findViewById(R.id.value3);
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left.setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("基本设置");
        sobot_text_title.setOnClickListener(this);
        sobot_appkey = (EditText) findViewById(R.id.sobot_demo_appkey);
        sobot_partnerId = (EditText) findViewById(R.id.sobot_partnerId);
        ed_hot_question = (EditText) findViewById(R.id.ed_hot_question);
        et_sobot_custom_language= (EditText) findViewById(R.id.et_sobot_custom_language);
        sobot_api_host = (EditText) findViewById(R.id.sobot_api_host);
        rl_ = (RelativeLayout) findViewById(R.id.rl_);
        rl_.setOnClickListener(this);
        getSobotStartSet();
    }

    @Override
    public void onBackPressed() {
        saveSobotStartSet();
        finish();
    }

    private void saveSobotStartSet() {
        SobotSPUtil.saveStringData(this, "sobot_appkey", sobot_appkey.getText().toString());
        SobotSPUtil.saveStringData(this, "sobot_partnerId", sobot_partnerId.getText().toString());
        SobotSPUtil.saveStringData(this, "ed_hot_question_value", ed_hot_question.getText().toString());
        SobotSPUtil.saveStringData(this, "sobot_custom_language_value", et_sobot_custom_language.getText().toString());
        SobotSPUtil.saveStringData(this, "key1_value", key1.getText().toString());
        SobotSPUtil.saveStringData(this, "key2_value", key2.getText().toString());
        SobotSPUtil.saveStringData(this, "key3_value", key3.getText().toString());
        SobotSPUtil.saveStringData(this, "value1_value", value1.getText().toString());
        SobotSPUtil.saveStringData(this, "value2_value", value2.getText().toString());
        SobotSPUtil.saveStringData(this, "value3_value", value3.getText().toString());
        if (sobot_api_host.getVisibility() == View.VISIBLE) {
           SobotSPUtil.saveStringData(this, "sobot_api_host", sobot_api_host.getText().toString());
           android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void getSobotStartSet() {
        String sobot_value_appkey = SobotSPUtil.getStringData(this, "sobot_appkey", "");
        if (!TextUtils.isEmpty(sobot_value_appkey)) {
            sobot_appkey.setText(sobot_value_appkey);
        }
        String sobot_value_partnerId = SobotSPUtil.getStringData(this, "sobot_partnerId", "");
        if (!TextUtils.isEmpty(sobot_value_partnerId)) {
            sobot_partnerId.setText(sobot_value_partnerId);
        }
        String ed_hot_question_value = SobotSPUtil.getStringData(this, "ed_hot_question_value", "");
        if (!TextUtils.isEmpty(ed_hot_question_value)) {
            ed_hot_question.setText(ed_hot_question_value);
        }

        String sobot_custom_language_value = SobotSPUtil.getStringData(this, "sobot_custom_language_value", "");
        if (!TextUtils.isEmpty(sobot_custom_language_value)) {
            et_sobot_custom_language.setText(sobot_custom_language_value);
        }

        String key1_value = SobotSPUtil.getStringData(this, "key1_value", "");
        if (!TextUtils.isEmpty(key1_value)) {
            key1.setText(key1_value);
        }
        String key2_value = SobotSPUtil.getStringData(this, "key2_value", "");
        if (!TextUtils.isEmpty(key2_value)) {
            key2.setText(key2_value);
        }
        String key3_value = SobotSPUtil.getStringData(this, "key3_value", "");
        if (!TextUtils.isEmpty(key3_value)) {
            key3.setText(key3_value);
        }

        sobot_api_host.setText(SobotBaseUrl.getHost());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_:
                //用户资料设置
                Intent intent = new Intent(this, SobotPersonSetActivity.class);
                startActivity(intent);
                break;
            case R.id.sobot_demo_tv_left:
                saveSobotStartSet();
                finish();
                break;
            case R.id.sobot_demo_tv_title:
                if (mDeveloperModeCount >= 7) {
                    sobot_api_host.setVisibility(View.VISIBLE);
                } else {
                    sobot_api_host.setVisibility(View.GONE);
                    mDeveloperModeCount++;
                }
                break;
        }
    }
}