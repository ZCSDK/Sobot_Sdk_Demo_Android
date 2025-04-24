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
import com.sobot.chat.utils.ToastUtil;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.model.SobotDemoOtherModel;
import com.sobot.demo.util.AndroidBug5497Workaround;

public class SobotDuolunActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left;
    private TextView tv_base_fun_16_1, update_appkey;
    private Information information;
    private SobotDemoOtherModel otherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_duolun_func_activity);
        AndroidBug5497Workaround.assistActivity(this);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        otherModel = (SobotDemoOtherModel) SobotSPUtil.getObject(getContext(), "sobot_demo_otherModel");
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("多轮会话");
        sobot_tv_left.setOnClickListener(this);




        if (otherModel != null) {
        }

        tv_base_fun_16_1 = findViewById(R.id.tv_base_fun_16_1);
        tv_base_fun_16_1.setText("https://www.sobot.com/developerdocs/service/knowledge_base.html#_4、多轮会话接口调用");
        tv_base_fun_16_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", "https://www.sobot.com/developerdocs/service/knowledge_base.html#_4%E3%80%81%E5%A4%9A%E8%BD%AE%E4%BC%9A%E8%AF%9D%E6%8E%A5%E5%8F%A3%E8%B0%83%E7%94%A8");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        update_appkey = findViewById(R.id.update_appkey);
        update_appkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (information != null) {
                    information.setApp_key("2497bd56d5ec42479f3b0bc0cf199ba2");
                    SobotSPUtil.saveObject(SobotDuolunActivity.this, "sobot_demo_infomation", information);
                }
                if (otherModel != null) {
                    otherModel.setApi_host("https://api.sobot.com");
                    SobotSPUtil.saveObject(SobotDuolunActivity.this, "sobot_demo_otherModel", otherModel);
                }
                ToastUtil.showCustomToastWithListenr(SobotDuolunActivity.this, "已替换成体验appkey，请重新初始化体验！", 3000, new ToastUtil.OnAfterShowListener() {
                    @Override
                    public void doAfter() {
                        finish();
                    }
                });
            }
        });
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