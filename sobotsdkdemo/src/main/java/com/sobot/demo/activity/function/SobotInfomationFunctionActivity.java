package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;

public class SobotInfomationFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left;
    private TextView tv_info_fun_5,sobot_tv_save;
    private Information information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_infomation_func_activity);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("Information类说明");
        sobot_tv_left.setOnClickListener(this);
        tv_info_fun_5 = (TextView) findViewById(R.id.tv_info_fun_5);
        tv_info_fun_5.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_5-Information类说明");
        tv_info_fun_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", "https://www.sobot.com/developerdocs/app_sdk/android.html#_5-information%E7%B1%BB%E8%AF%B4%E6%98%8E");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            finish();
        }
    }

    public Context getContext() {
        return this;
    }
}