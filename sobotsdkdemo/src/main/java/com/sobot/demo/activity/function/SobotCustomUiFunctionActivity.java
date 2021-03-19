package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.enumtype.SobotChatAvatarDisplayMode;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.model.SobotDemoOtherModel;

public class SobotCustomUiFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText sobot_et_custom_title, sobot_et_custom_avatar,sobot_et_custom_right_button_call;
    private RelativeLayout sobot_tv_left, sobot_rl_4_6_2, sobot_rl_4_6_2_2, sobot_rl_4_6_3, sobot_rl_4_6_4, sobot_rl_4_6_1_1, sobot_rl_4_6_1_2, sobot_rl_4_6_1_3;
    private ImageView sobotImage462, sobotImage4622, sobotImage463, sobotImage464, sobotImage4611, sobotImage4612, sobotImage4613;
    private boolean status462, status4622, status463, status464, status4611, status4612, status4613;
    private TextView tv_customui_fun_4_6_2, tv_customui_fun_4_6_3, tv_customui_fun_4_6_4, tv_customui_fun_4_6_5, sobot_tv_save;
    private Information information;
    private SobotDemoOtherModel otherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_customui_func_activity);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        otherModel = (SobotDemoOtherModel) SobotSPUtil.getObject(getContext(), "sobot_demo_otherModel");
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
        sobot_text_title.setText("会话页面自定义UI设置");
        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.VISIBLE);
        sobot_tv_save.setOnClickListener(this);

        sobot_rl_4_6_2 = (RelativeLayout) findViewById(R.id.sobot_rl_4_6_2);
        sobot_rl_4_6_2.setOnClickListener(this);
        sobotImage462 = (ImageView) findViewById(R.id.sobot_image_4_6_2);

        sobot_rl_4_6_2_2 = (RelativeLayout) findViewById(R.id.sobot_rl_4_6_2_2);
        sobot_rl_4_6_2_2.setOnClickListener(this);
        sobotImage4622 = (ImageView) findViewById(R.id.sobot_image_4_6_2_2);

        sobot_rl_4_6_3 = (RelativeLayout) findViewById(R.id.sobot_rl_4_6_3);
        sobot_rl_4_6_3.setOnClickListener(this);
        sobotImage463 = (ImageView) findViewById(R.id.sobot_image_4_6_3);

        sobot_rl_4_6_4 = (RelativeLayout) findViewById(R.id.sobot_rl_4_6_4);
        sobot_rl_4_6_4.setOnClickListener(this);
        sobotImage464 = (ImageView) findViewById(R.id.sobot_image_4_6_4);

        sobot_rl_4_6_1_1 = (RelativeLayout) findViewById(R.id.sobot_rl_4_6_1_1);
        sobot_rl_4_6_1_1.setOnClickListener(this);
        sobotImage4611 = (ImageView) findViewById(R.id.sobot_image_4_6_1_1);

        sobot_rl_4_6_1_2 = (RelativeLayout) findViewById(R.id.sobot_rl_4_6_1_2);
        sobot_rl_4_6_1_2.setOnClickListener(this);
        sobotImage4612 = (ImageView) findViewById(R.id.sobot_image_4_6_1_2);

        sobot_rl_4_6_1_3 = (RelativeLayout) findViewById(R.id.sobot_rl_4_6_1_3);
        sobot_rl_4_6_1_3.setOnClickListener(this);
        sobotImage4613 = (ImageView) findViewById(R.id.sobot_image_4_6_1_3);


        sobot_et_custom_title = findViewById(R.id.sobot_et_custom_title);
        sobot_et_custom_avatar = findViewById(R.id.sobot_et_custom_avatar);
        sobot_et_custom_right_button_call = findViewById(R.id.sobot_et_custom_right_button_call);

        String title = SharedPreferencesUtil.getStringData(getContext(), ZhiChiConstant.SOBOT_CHAT_TITLE_DISPLAY_CONTENT,
                "");
        sobot_et_custom_title.setText(title);
        String avatar = SharedPreferencesUtil.getStringData(getContext(), ZhiChiConstant.SOBOT_CHAT_AVATAR_DISPLAY_CONTENT,
                "");
        sobot_et_custom_avatar.setText(avatar);
        String callNum = SobotSPUtil.getStringData(getContext(), "sobot_et_custom_right_button_call",sobot_et_custom_avatar.getText().toString().trim());
        sobot_et_custom_right_button_call.setText(callNum);
        status462 = SharedPreferencesUtil.getBooleanData(getContext(), ZhiChiConstant.SOBOT_CHAT_TITLE_IS_SHOW, false);
        setImageShowStatus(status462, sobotImage462);
        status4622 = SharedPreferencesUtil.getBooleanData(getContext(), ZhiChiConstant.SOBOT_CHAT_AVATAR_IS_SHOW,
                true);
        setImageShowStatus(status4622, sobotImage4622);
        status463 = SobotSPUtil.getBooleanData(this, "landscape_screen", false);
        setImageShowStatus(status463, sobotImage463);
        status464 = SobotSPUtil.getBooleanData(this, "display_innotch", false);
        setImageShowStatus(status464, sobotImage464);

        status4611 = SobotSPUtil.getBooleanData(this, "sobot_title_right_menu1_display", false);
        setImageShowStatus(status4611, sobotImage4611);

        status4612 = SobotSPUtil.getBooleanData(this, "sobot_title_right_menu2_display", false);
        setImageShowStatus(status4612, sobotImage4612);

        status4613 = SobotSPUtil.getBooleanData(this, "sobot_title_right_menu3_display", false);
        setImageShowStatus(status4613, sobotImage4613);

        tv_customui_fun_4_6_2 = findViewById(R.id.tv_customui_fun_4_6_2);
        tv_customui_fun_4_6_2.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-6-2-动态控制显示标题栏的头像和昵称");
        setOnClick(tv_customui_fun_4_6_2, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-6-2-%E5%8A%A8%E6%80%81%E6%8E%A7%E5%88%B6%E6%98%BE%E7%A4%BA%E6%A0%87%E9%A2%98%E6%A0%8F%E7%9A%84%E5%A4%B4%E5%83%8F%E5%92%8C%E6%98%B5%E7%A7%B0");
        tv_customui_fun_4_6_3 = findViewById(R.id.tv_customui_fun_4_6_3);
        tv_customui_fun_4_6_3.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-6-3-控制横竖屏显示开关");
        setOnClick(tv_customui_fun_4_6_3, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-6-3-%E6%8E%A7%E5%88%B6%E6%A8%AA%E7%AB%96%E5%B1%8F%E6%98%BE%E7%A4%BA%E5%BC%80%E5%85%B3");
        tv_customui_fun_4_6_4 = findViewById(R.id.tv_customui_fun_4_6_4);
        tv_customui_fun_4_6_4.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-6-4-横屏下是否打开刘海屏开关");
        setOnClick(tv_customui_fun_4_6_4, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-6-4-%E6%A8%AA%E5%B1%8F%E4%B8%8B%E6%98%AF%E5%90%A6%E6%89%93%E5%BC%80%E5%88%98%E6%B5%B7%E5%B1%8F%E5%BC%80%E5%85%B3");
        tv_customui_fun_4_6_5 = findViewById(R.id.tv_customui_fun_4_6_5);
        tv_customui_fun_4_6_5.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-6-5-ui样式通过同名资源替换");
        setOnClick(tv_customui_fun_4_6_5, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-6-5-ui%E6%A0%B7%E5%BC%8F%E9%80%9A%E8%BF%87%E5%90%8C%E5%90%8D%E8%B5%84%E6%BA%90%E6%9B%BF%E6%8D%A2");

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
            case R.id.sobot_tv_save:
                if (information != null) {
                    String custom_title = sobot_et_custom_title.getText().toString().trim();
                    if (TextUtils.isEmpty(custom_title)) {
                        SobotApi.setChatTitleDisplayMode(getApplicationContext(),
                                SobotChatTitleDisplayMode.Default, "", status462);
                    } else {
                        SobotApi.setChatTitleDisplayMode(getApplicationContext(),
                                SobotChatTitleDisplayMode.ShowFixedText, custom_title, status462);
                    }
                    String custom_avatar = sobot_et_custom_avatar.getText().toString().trim();
                    if (TextUtils.isEmpty(custom_avatar)) {
                        SobotApi.setChatAvatarDisplayMode(getApplicationContext(), SobotChatAvatarDisplayMode.Default, "", status4622);
                    } else {
                        SobotApi.setChatAvatarDisplayMode(getApplicationContext(), SobotChatAvatarDisplayMode.ShowFixedAvatar, custom_avatar, status4622);
                    }
                    //true 横屏 , false 竖屏; 默认 false 竖屏
                    ZCSobotApi.setSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN, status463);
                    SobotSPUtil.saveBooleanData(this, "landscape_screen", status463);
                    //只有在横屏下才有用;竖屏已适配，可修改状态栏颜色
                    //true 打开 ,false 关闭; 默认 false 关闭
                    ZCSobotApi.setSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH, status464);
                    SobotSPUtil.saveBooleanData(this, "display_innotch", status464);
                    SobotSPUtil.saveBooleanData(this, "sobot_title_right_menu1_display", status4611);
                    SobotSPUtil.saveBooleanData(this, "sobot_title_right_menu2_display", status4612);
                    SobotSPUtil.saveBooleanData(this, "sobot_title_right_menu3_display", status4613);
                    SobotSPUtil.saveStringData(getContext(), "sobot_et_custom_right_button_call",sobot_et_custom_right_button_call.getText().toString().trim());
                    //设置 toolbar右边第一个按钮是否显示（更多）
                    SobotUIConfig.sobot_title_right_menu1_display = status4611;
                    //设置 toolbar右边第二个按钮是否显示（评价）
                    SobotUIConfig.sobot_title_right_menu2_display = status4612;
                    //设置 toolbar右边第三个按钮是否显示（电话）
                    SobotUIConfig.sobot_title_right_menu3_display = status4613;
                    // toolbar右边第三个按钮电话对应的电话号
                    SobotUIConfig.sobot_title_right_menu3_call_num = sobot_et_custom_right_button_call.getText().toString().trim();
                }
                ToastUtil.showToast(getContext(), "已保存");
                finish();
                break;
            case R.id.sobot_rl_4_6_2:
                status462 = !status462;
                setImageShowStatus(status462, sobotImage462);
                break;
            case R.id.sobot_rl_4_6_2_2:
                status4622 = !status4622;
                setImageShowStatus(status4622, sobotImage4622);
                break;
            case R.id.sobot_rl_4_6_3:
                status463 = !status463;
                setImageShowStatus(status463, sobotImage463);

                break;
            case R.id.sobot_rl_4_6_4:
                status464 = !status464;
                setImageShowStatus(status464, sobotImage464);
                break;
            case R.id.sobot_rl_4_6_1_1:
                status4611 = !status4611;
                setImageShowStatus(status4611, sobotImage4611);
                break;
            case R.id.sobot_rl_4_6_1_2:
                status4612 = !status4612;
                setImageShowStatus(status4612, sobotImage4612);
                break;
            case R.id.sobot_rl_4_6_1_3:
                status4613 = !status4613;
                setImageShowStatus(status4613, sobotImage4613);
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