package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.util.AndroidBug5497Workaround;

public class SobotOtherFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left;
    private TextView tv_other_fun_4_7_2, sobot_tv_save;
    private EditText sobot_et_scope_time;
    private EditText sobot_et_langue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_other_func_activity);
        AndroidBug5497Workaround.assistActivity(this);
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
        sobot_text_title.setText("其它配置");
        sobot_et_scope_time = findViewById(R.id.sobot_et_scope_time);
        sobot_et_langue= findViewById(R.id.sobot_et_langue);

        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.VISIBLE);
        sobot_tv_save.setOnClickListener(this);

        sobot_et_scope_time.setText(SharedPreferencesUtil.getLongData(getContext(), ZhiChiConstant.SOBOT_SCOPE_TIME, 0) + "");
        String sobot_custom_language_value = SobotSPUtil.getStringData(this, "sobot_custom_language_value", "");
        if (!TextUtils.isEmpty(sobot_custom_language_value)) {
            sobot_et_langue.setText(sobot_custom_language_value);
        }

        tv_other_fun_4_7_2 = findViewById(R.id.tv_other_fun_4_7_2);
        tv_other_fun_4_7_2.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-7-2-自定义聊天记录显示时间范围");
        setOnClick(tv_other_fun_4_7_2, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-7-2-%E8%87%AA%E5%AE%9A%E4%B9%89%E8%81%8A%E5%A4%A9%E8%AE%B0%E5%BD%95%E6%98%BE%E7%A4%BA%E6%97%B6%E9%97%B4%E8%8C%83%E5%9B%B4");
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
        if (v == sobot_tv_save) {
            String scope_time = sobot_et_scope_time.getText().toString().trim();
            SobotApi.setScope_time(getContext(), Integer.parseInt(scope_time));
            ToastUtil.showToast(getContext(), "已保存");
            SobotSPUtil.saveStringData(this, "sobot_custom_language_value", sobot_et_langue.getText().toString());
            finish();
        }

    }

    public Context getContext() {
        return this;
    }
}