package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.SobotApi;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;

public class SobotStartSobotFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left;
    private TextView tv_start_fun_3_4, sobot_tv_save, sobot_tv_start;
    private Information information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_startsobot_func_activity);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("启动客服");
        sobot_tv_left.setOnClickListener(this);
        tv_start_fun_3_4 = (TextView) findViewById(R.id.tv_start_fun_3_4);
        tv_start_fun_3_4.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_3-4-1-启动智齿页面");
        tv_start_fun_3_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", "https://www.sobot.com/developerdocs/app_sdk/android.html#_3-4-1-%E5%90%AF%E5%8A%A8%E6%99%BA%E9%BD%BF%E9%A1%B5%E9%9D%A2");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.GONE);
        sobot_tv_start = findViewById(R.id.sobot_tv_start);
        sobot_tv_start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            finish();
        }
        if (v == sobot_tv_start) {
            if (information != null) {
                information.setUseRobotVoice(true);
                ZCSobotApi.openZCChat(getContext(), information);
            }
        }

    }

    public Context getContext() {
        return this;
    }
}