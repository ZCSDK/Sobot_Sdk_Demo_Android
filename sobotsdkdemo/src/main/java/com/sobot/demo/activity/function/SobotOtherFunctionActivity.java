package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.util.AndroidBug5497Workaround;

public class SobotOtherFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left;
    private RelativeLayout sobot_rl_4_7_6_1, sobot_rl_4_7_6_2, sobot_rl_4_7_8, sobot_rl_4_7_9;
    private ImageView sobotImage4761, sobotImage4762, sobotImage478, sobotImage479;
    private boolean status4761, status4762, status478, status479;
    private TextView tv_other_fun_4_7_2, sobot_tv_save;
    private EditText sobot_et_scope_time;
    private EditText sobot_et_langue;
    private EditText sobot_et_server_langue;
    private Information information;

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
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
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
        sobot_et_langue = findViewById(R.id.sobot_et_langue);
        sobot_et_server_langue = findViewById(R.id.sobot_et_server_langue);

        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.VISIBLE);
        sobot_tv_save.setOnClickListener(this);


        sobot_rl_4_7_6_1 = (RelativeLayout) findViewById(R.id.sobot_rl_4_7_6_1);
        sobot_rl_4_7_6_1.setOnClickListener(this);
        sobotImage4761 = (ImageView) findViewById(R.id.sobot_image_4_7_6_1);

        sobot_rl_4_7_6_2 = (RelativeLayout) findViewById(R.id.sobot_rl_4_7_6_2);
        sobot_rl_4_7_6_2.setOnClickListener(this);
        sobotImage4762 = (ImageView) findViewById(R.id.sobot_image_4_7_6_2);

        sobot_rl_4_7_8 = (RelativeLayout) findViewById(R.id.sobot_rl_4_7_8);
        sobot_rl_4_7_8.setOnClickListener(this);
        sobotImage478 = (ImageView) findViewById(R.id.sobot_image_4_7_8);

        sobot_rl_4_7_9 = (RelativeLayout) findViewById(R.id.sobot_rl_4_7_9);
        sobot_rl_4_7_9.setOnClickListener(this);
        sobotImage479 = (ImageView) findViewById(R.id.sobot_image_4_7_9);


        if (information != null) {
            status4761 = information.isHideRototEvaluationLabels();
            setImageShowStatus(status4761, sobotImage4761);
            status4762 = information.isHideManualEvaluationLabels();
            setImageShowStatus(status4762, sobotImage4762);
            sobot_et_server_langue.setText(information.getLocale());
        }
        status478 = SobotSPUtil.getBooleanData(this, "auto_match_timezone", false);
        setImageShowStatus(status478, sobotImage478);

        status479 = SobotSPUtil.getBooleanData(this, "show_permission_tips_pop", false);
        setImageShowStatus(status479, sobotImage479);

        sobot_et_scope_time.setText(SharedPreferencesUtil.getLongData(getContext(), ZhiChiConstant.SOBOT_SCOPE_TIME, 0) + "");
        String sobot_custom_language_value = SobotSPUtil.getStringData(this, "custom_language_value", "");
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
        if (v.getId() == R.id.sobot_tv_save) {
            if (information != null) {
                information.setHideManualEvaluationLabels(status4762);
                information.setHideRototEvaluationLabels(status4761);
                information.setLocale(sobot_et_server_langue.getText().toString());
                SobotSPUtil.saveObject(this, "sobot_demo_infomation", information);
            }
            ZCSobotApi.setSwitchMarkStatus(MarkConfig.AUTO_MATCH_TIMEZONE, status478);
            SobotSPUtil.saveBooleanData(this, "auto_match_timezone", status478);
            String scope_time = sobot_et_scope_time.getText().toString().trim();
            if (!TextUtils.isEmpty(scope_time)) {
                try {
                    ZCSobotApi.setScope_time(getContext(), Long.parseLong(scope_time));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(), "输入的时间范围格式错误，请检查输入内容");
                }
            }
            ToastUtil.showToast(getContext(), "已保存");
            String language = sobot_et_langue.getText().toString().trim();
            if (!TextUtils.isEmpty(language)) {
                ZCSobotApi.setInternationalLanguage(getApplicationContext(), language, true, false);
            } else {
                ZCSobotApi.setInternationalLanguage(getApplicationContext(), language, false, false);
            }
            ZCSobotApi.hideTimemsgForMessageList(getApplicationContext(), false);
            SobotSPUtil.saveStringData(this, "custom_language_value", language);
            // 是否在申请权限前弹出权限用途提示框,默认不弹
            ZCSobotApi.setSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP, status479);
            SobotSPUtil.saveBooleanData(this, "show_permission_tips_pop", status479);
            ZCSobotApi.outCurrentUserZCLibInfo(getContext(), "");
            finish();
        } else if (v.getId() == R.id.sobot_rl_4_7_6_1) {
            toggleStatus(status4761, sobotImage4761);
        } else if (v.getId() == R.id.sobot_rl_4_7_6_2) {
            toggleStatus(status4762, sobotImage4762);
        } else if (v.getId() == R.id.sobot_rl_4_7_8) {
            toggleStatus(status478, sobotImage478);
        } else if (v.getId() == R.id.sobot_rl_4_7_9) {
            toggleStatus(status479, sobotImage479);
        }
    }

    private void toggleStatus(boolean status, ImageView imageView) {
        status = !status;
        setImageShowStatus(status, imageView);
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