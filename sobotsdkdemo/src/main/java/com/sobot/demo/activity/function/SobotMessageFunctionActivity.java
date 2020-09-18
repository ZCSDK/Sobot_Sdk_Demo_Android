package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;

public class SobotMessageFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left, sobot_rl_4_5_2, sobot_rl_4_5, sobot_rl_4_51;
    private ImageView sobotImage452;
    private boolean status452;
    private TextView tv_message_fun_4_5_2, tv_message_fun_4_5_3, tv_message_fun_4_5_4, tv_message_fun_4_5_6, sobot_tv_save;
    private Information information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_message_func_activity);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
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
        sobot_text_title.setText("消息相关");
        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setOnClickListener(this);
        sobot_tv_save.setVisibility(View.VISIBLE);

        tv_message_fun_4_5_2 = findViewById(R.id.tv_message_fun_4_5_2);
        tv_message_fun_4_5_2.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-2-设置是否开启消息提醒");
        setOnClick(tv_message_fun_4_5_2, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-2-%E8%AE%BE%E7%BD%AE%E6%98%AF%E5%90%A6%E5%BC%80%E5%90%AF%E6%B6%88%E6%81%AF%E6%8F%90%E9%86%92");

        tv_message_fun_4_5_3 = findViewById(R.id.tv_message_fun_4_5_3);
        tv_message_fun_4_5_3.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-3-设置离线消息");
        setOnClick(tv_message_fun_4_5_3, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-3-%E8%AE%BE%E7%BD%AE%E7%A6%BB%E7%BA%BF%E6%B6%88%E6%81%AF");

        tv_message_fun_4_5_4 = findViewById(R.id.tv_message_fun_4_5_4);
        tv_message_fun_4_5_4.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-4-注册广播、获取新收到的信息和未读消息数");
        setOnClick(tv_message_fun_4_5_4, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-4-%E6%B3%A8%E5%86%8C%E5%B9%BF%E6%92%AD%E3%80%81%E8%8E%B7%E5%8F%96%E6%96%B0%E6%94%B6%E5%88%B0%E7%9A%84%E4%BF%A1%E6%81%AF%E5%92%8C%E6%9C%AA%E8%AF%BB%E6%B6%88%E6%81%AF%E6%95%B0");

        tv_message_fun_4_5_6 = findViewById(R.id.tv_message_fun_4_5_6);
        tv_message_fun_4_5_6.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-6-自定义超链接的点击事件（拦截范围：帮助中心、留言、聊天、留言记录、商品卡片，订单卡片）");
        setOnClick(tv_message_fun_4_5_6, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-6-%E8%87%AA%E5%AE%9A%E4%B9%89%E8%B6%85%E9%93%BE%E6%8E%A5%E7%9A%84%E7%82%B9%E5%87%BB%E4%BA%8B%E4%BB%B6%EF%BC%88%E6%8B%A6%E6%88%AA%E8%8C%83%E5%9B%B4%EF%BC%9A%E5%B8%AE%E5%8A%A9%E4%B8%AD%E5%BF%83%E3%80%81%E7%95%99%E8%A8%80%E3%80%81%E8%81%8A%E5%A4%A9%E3%80%81%E7%95%99%E8%A8%80%E8%AE%B0%E5%BD%95%E3%80%81%E5%95%86%E5%93%81%E5%8D%A1%E7%89%87%EF%BC%8C%E8%AE%A2%E5%8D%95%E5%8D%A1%E7%89%87%EF%BC%89");

        sobot_rl_4_5_2 = (RelativeLayout) findViewById(R.id.sobot_rl_4_5_2);
        sobot_rl_4_5_2.setOnClickListener(this);
        sobotImage452 = (ImageView) findViewById(R.id.sobot_image_4_5_2);

        sobot_rl_4_5 = (RelativeLayout) findViewById(R.id.sobot_rl_4_5);
        sobot_rl_4_5.setOnClickListener(this);

        sobot_rl_4_51 = (RelativeLayout) findViewById(R.id.sobot_rl_4_51);
        sobot_rl_4_51.setOnClickListener(this);


        if (information != null) {
            status452 = SharedPreferencesUtil.getBooleanData(getContext(), Const.SOBOT_NOTIFICATION_FLAG, false);
            setImageShowStatus(status452, sobotImage452);
        }
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
        switch (v.getId()) {
            case R.id.sobot_tv_left:
                finish();
                break;
            case R.id.sobot_tv_save:
                //设置是否开启消息提醒
                ZCSobotApi.setNotificationFlag(getContext(), status452, R.drawable.sobot_logo_small_icon, R.drawable.sobot_logo_icon);
                ToastUtil.showToast(getContext(), "已保存");
                finish();
                break;
            case R.id.sobot_rl_4_5:
                if (information != null) {
                    int num = ZCSobotApi.getUnReadMessage(getContext(), information.getPartnerid());
                    ToastUtil.showToast(getContext(), "未读消息数=" + num);
                } else {
                    ZCSobotApi.getUnReadMessage(getContext(), null);
                }
                break;
            case R.id.sobot_rl_4_51:
                if (information != null) {
                    ZCSobotApi.clearUnReadNumber(getContext(), information.getPartnerid());
                    ToastUtil.showToast(getContext(), "已清除");
                } else {
                    ZCSobotApi.clearUnReadNumber(getContext(), null);
                }
                break;
            case R.id.sobot_rl_4_5_2:
                status452 = !status452;
                setImageShowStatus(status452, sobotImage452);
                break;
        }
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