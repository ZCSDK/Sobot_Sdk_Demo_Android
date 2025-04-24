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

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.util.AndroidBug5497Workaround;

public class SobotReobotFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left, sobot_rl_4_1_5_1, sobot_rl_4_1_5_2;
    private ImageView sobotImage4151, sobotImage4152;
    private boolean status4151, status4152;
    private TextView tv_rebot_fun_4_1_1, tv_rebot_fun_4_1_2, tv_rebot_fun_4_1_4, tv_rebot_fun_4_1_5, sobot_tv_save;
    private EditText sobot_et_rebot_id, sobot_et_rebot_alise, sobot_et_service_mode, sobot_et_rebot_faqid;
    private Information information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_rebot_func_activity);
        AndroidBug5497Workaround.assistActivity(this);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("机器人客服");
        sobot_tv_left.setOnClickListener(this);
        sobot_et_rebot_id = findViewById(R.id.sobot_et_rebot_id);
        sobot_et_rebot_alise = findViewById(R.id.sobot_et_rebot_alise);
        sobot_et_service_mode = findViewById(R.id.sobot_et_service_mode);
        sobot_et_rebot_faqid= findViewById(R.id.sobot_et_rebot_faqid);
        sobot_rl_4_1_5_1 = (RelativeLayout) findViewById(R.id.sobot_rl_4_1_5_1);
        sobot_rl_4_1_5_1.setOnClickListener(this);
        sobotImage4151 = (ImageView) findViewById(R.id.sobot_image_4_1_5_1);

        sobot_rl_4_1_5_2 = (RelativeLayout) findViewById(R.id.sobot_rl_4_1_5_2);
        sobot_rl_4_1_5_2.setOnClickListener(this);
        sobotImage4152 = (ImageView) findViewById(R.id.sobot_image_4_1_5_2);

        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.VISIBLE);
        sobot_tv_save.setOnClickListener(this);

        if (information != null) {
            sobot_et_rebot_id.setText(TextUtils.isEmpty(information.getRobot_code()) ? "" : information.getRobot_code());
            sobot_et_rebot_alise.setText(TextUtils.isEmpty(information.getRobot_alias()) ? "" : information.getRobot_alias());
            sobot_et_service_mode.setText(information.getService_mode() + "");
            sobot_et_rebot_faqid.setText(information.getFaqId()+"");
            setImageShowStatus4(information.isHideMenuLeave());
            setImageShowStatus5(information.isHideMenuSatisfaction());
        }
        tv_rebot_fun_4_1_1 = findViewById(R.id.tv_rebot_fun_4_1_1);
        setOnClick(tv_rebot_fun_4_1_1, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-1-1-%E5%AF%B9%E6%8E%A5%E6%8C%87%E5%AE%9A%E6%9C%BA%E5%99%A8%E4%BA%BA");
        tv_rebot_fun_4_1_2 = findViewById(R.id.tv_rebot_fun_4_1_2);
        setOnClick(tv_rebot_fun_4_1_2, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-1-2-%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8E%A5%E5%85%A5%E6%A8%A1%E5%BC%8F");
        tv_rebot_fun_4_1_4 = findViewById(R.id.tv_rebot_fun_4_1_4);
        setOnClick(tv_rebot_fun_4_1_4, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-1-4-%E8%AE%BE%E7%BD%AE%E8%BD%AC%E4%BA%BA%E5%B7%A5%E6%BA%A2%E5%87%BA");
        tv_rebot_fun_4_1_5 = findViewById(R.id.tv_rebot_fun_4_1_5);
        setOnClick(tv_rebot_fun_4_1_5, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-1-5-%E6%9C%BA%E5%99%A8%E4%BA%BA%E5%92%A8%E8%AF%A2%E6%A8%A1%E5%BC%8F%E4%B8%8B%E5%8F%AF%E9%9A%90%E8%97%8F%E5%8A%A0%E5%8F%B7%E8%8F%9C%E5%8D%95%E6%A0%8F%E7%9A%84%E6%8C%89%E9%92%AE");
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
        if (v == sobot_tv_left) {
            finish();
        } else if (v == sobot_tv_save) {
            String rebotId = sobot_et_rebot_id.getText().toString().trim();
            String rebotAlise = sobot_et_rebot_alise.getText().toString().trim();
            String serviceMode = sobot_et_service_mode.getText().toString().trim();
            String faqid = sobot_et_rebot_faqid.getText().toString().trim();
            if (information != null) {
                information.setRobot_code(rebotId);
                information.setRobot_alias(rebotAlise);
                information.setService_mode(TextUtils.isEmpty(serviceMode) ? -1 : Integer.parseInt(serviceMode));
                information.setHideMenuLeave(status4151);
                information.setHideMenuSatisfaction(status4152);
                if (!TextUtils.isEmpty(faqid)){
                    information.setFaqId(Integer.parseInt(faqid));
                }else{
                    information.setFaqId(0);
                }
                SobotSPUtil.saveObject(this, "sobot_demo_infomation", information);
            }
            ToastUtil.showToast(getContext(), "已保存");
            finish();
        } else if (v == sobot_rl_4_1_5_1) {//是否显示留言
            status4151 = !status4151;
            setImageShowStatus4(status4151);
        } else if (v == sobot_rl_4_1_5_2) {//是否显示服务评价
            status4152 = !status4152;
            setImageShowStatus5(status4152);
        }

    }

    private void setImageShowStatus4(boolean open) {
        if (open) {
            status4151 = true;
            sobotImage4151.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status4151 = false;
            sobotImage4151.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setImageShowStatus5(boolean open) {
        if (open) {
            status4152 = true;
            sobotImage4152.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status4152 = false;
            sobotImage4152.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    public Context getContext() {
        return this;
    }
}