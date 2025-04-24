package com.sobot.demo.activity.product;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.model.Information;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;

public class SobotDemoCloudCallActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left;
    private RelativeLayout sobot_demo_bottom_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_cloud_call_activity);
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_demo_bottom_layout = (RelativeLayout) findViewById(R.id.sobot_demo_bottom_layout);
        sobot_demo_bottom_layout.setOnClickListener(this);
        sobot_text_title.setText("云呼叫中心");
        sobot_tv_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left) {
            finish();
        }

        if (v == sobot_demo_bottom_layout){
            Information information = (Information) SobotSPUtil.getObject(this, "sobot_demo_infomation");
            if (information != null) {
                String sobot_custom_language_value = SobotSPUtil.getStringData(this, "custom_language_value", "");
                if (!TextUtils.isEmpty(sobot_custom_language_value)) {
                    ZCSobotApi.setInternationalLanguage(this, sobot_custom_language_value, true, false);
                }
                ZCSobotApi.openZCChat(this, information);
            }
        }
    }
}