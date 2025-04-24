package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;

public class SobotStartHelpCenterFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left;
    private TextView tv_start_fun_3_5, sobot_tv_save, sobot_tv_start;
    private EditText sobot_et_phone_title, sobot_et_phone;
    private Information information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_starthelpcenter_func_activity);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        findViews();
    }

    private void findViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("启动客户服务中心");
        sobot_tv_left.setOnClickListener(this);
        sobot_et_phone_title = (EditText) findViewById(R.id.sobot_et_phone_title);
        sobot_et_phone = (EditText) findViewById(R.id.sobot_et_phone);
        tv_start_fun_3_5 = (TextView) findViewById(R.id.tv_start_fun_3_5);
        tv_start_fun_3_5.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_3-4-3-启动客户服务中心");
        tv_start_fun_3_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", "https://www.sobot.com/developerdocs/app_sdk/android.html#_3-4-3-%E5%90%AF%E5%8A%A8%E5%AE%A2%E6%88%B7%E6%9C%8D%E5%8A%A1%E4%B8%AD%E5%BF%83");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setOnClickListener(this);
        sobot_tv_save.setVisibility(View.GONE);
        sobot_tv_start = findViewById(R.id.sobot_tv_start);
        sobot_tv_start.setOnClickListener(this);
        if (information != null) {
            sobot_et_phone.setText(information.getHelpCenterTel());
            sobot_et_phone_title.setText(information.getHelpCenterTelTitle());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            finish();
        }
        if (v == sobot_tv_start) {
            if (information != null) {
                information.setHelpCenterTelTitle(sobot_et_phone_title.getText().toString().trim());
                information.setHelpCenterTel(sobot_et_phone.getText().toString().trim());
                SobotSPUtil.saveObject(this, "sobot_demo_infomation", information);
                ZCSobotApi.openZCServiceCenter(getContext(), information);
            }
        }

    }

    public Context getContext() {
        return this;
    }
}