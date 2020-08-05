package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;

/**
 * Created by Administrator on 2017/11/21.
 */

public class SobotSwitchAllSetActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout sobotDemoTvLeft;
    private TextView sobotDemoTvTitle;
    private RelativeLayout sobotScreenSet;
    private RelativeLayout sobotIsShowSatisfactionClose;
    private ImageView imgOpenClose;
    private ImageView imgOpenNotify3;
    private RelativeLayout sobotRl3;
    private ImageView sobotImage3;
    private RelativeLayout sobotRl4;
    private ImageView sobotImage4;
    private RelativeLayout sobotRl5;
    private ImageView sobotImage5;
    private RelativeLayout sobotRl6;
    private ImageView sobotImage6;
    private RelativeLayout sobotRl7;
    private ImageView sobotImage7;
    private RelativeLayout sobotRl8;
    private ImageView sobotImage8;
    private RelativeLayout sobotRl9;
    private ImageView sobotImage9;
    private RelativeLayout sobotRl10;
    private ImageView sobotImage10;
    private RelativeLayout sobotRl11;
    private ImageView sobotImage11;


    private boolean isLandscape_Screen, isInterceptLink, isShowChatStatus;
    private boolean status4, status5, status6, status7, status8, status9, status10, status11;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_switch_all_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        findvViews();
    }

    private void findvViews() {
        sobotDemoTvLeft = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        sobotDemoTvTitle = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobotScreenSet = (RelativeLayout) findViewById(R.id.sobot_screen_set);
        sobotIsShowSatisfactionClose = (RelativeLayout) findViewById(R.id.sobot_isShowSatisfactionClose);
        imgOpenClose = (ImageView) findViewById(R.id.img_open_close);
        imgOpenNotify3 = (ImageView) findViewById(R.id.img_open_notify3);


        sobotScreenSet.setOnClickListener(this);
        sobotIsShowSatisfactionClose.setOnClickListener(this);
        sobotDemoTvLeft.setOnClickListener(this);


        sobotRl3 = (RelativeLayout) findViewById(R.id.sobot_rl3);
        sobotRl3.setOnClickListener(this);
        sobotImage3 = (ImageView) findViewById(R.id.sobot_image3);

        sobotRl4 = (RelativeLayout) findViewById(R.id.sobot_rl4);
        sobotRl4.setOnClickListener(this);
        sobotImage4 = (ImageView) findViewById(R.id.sobot_image4);

        sobotRl5 = (RelativeLayout) findViewById(R.id.sobot_rl5);
        sobotRl5.setOnClickListener(this);
        sobotImage5 = (ImageView) findViewById(R.id.sobot_image5);

        sobotRl6 = (RelativeLayout) findViewById(R.id.sobot_rl6);
        sobotRl6.setOnClickListener(this);
        sobotImage6 = (ImageView) findViewById(R.id.sobot_image6);

        sobotRl7 = (RelativeLayout) findViewById(R.id.sobot_rl7);
        sobotRl7.setOnClickListener(this);
        sobotImage7 = (ImageView) findViewById(R.id.sobot_image7);

        sobotRl8 = (RelativeLayout) findViewById(R.id.sobot_rl8);
        sobotRl8.setOnClickListener(this);
        sobotImage8 = (ImageView) findViewById(R.id.sobot_image8);

        sobotRl9 = (RelativeLayout) findViewById(R.id.sobot_rl9);
        sobotRl9.setOnClickListener(this);
        sobotImage9 = (ImageView) findViewById(R.id.sobot_image9);

        sobotRl10 = (RelativeLayout) findViewById(R.id.sobot_rl10);
        sobotRl10.setOnClickListener(this);
        sobotImage10 = (ImageView) findViewById(R.id.sobot_image10);


        sobotRl11 = (RelativeLayout) findViewById(R.id.sobot_rl11);
        sobotRl11.setOnClickListener(this);
        sobotImage11 = (ImageView) findViewById(R.id.sobot_image11);

        sobotDemoTvTitle.setText("全局开关设置");

        getSobotStartSet();
    }

    private void getSobotStartSet() {
        isLandscape_Screen = SobotSPUtil.getBooleanData(this, "sobot_isLandscapeScreen", false);
        setShowSatisfaction(isLandscape_Screen);
        isInterceptLink = SobotSPUtil.getBooleanData(this, "isInterceptLink", false);
        setShowSatisfactionClose(isInterceptLink);
        isShowChatStatus = SobotSPUtil.getBooleanData(this, "isShowChatStatus", false);
        setImageShowChatStatus(isShowChatStatus);

        status4 = SobotSPUtil.getBooleanData(this, "sobot_isShowLeaveMsg", false);
        setImageShowStatus4(status4);
        status5 = SobotSPUtil.getBooleanData(this, "sobot_isShowSatisfaction", false);
        setImageShowStatus5(status5);
        status6 = SobotSPUtil.getBooleanData(this, "sobot_isShowPicture", false);
        setImageShowStatus6(status6);
        status7 = SobotSPUtil.getBooleanData(this, "sobot_isShowVedio", false);
        setImageShowStatus7(status7);
        status8 = SobotSPUtil.getBooleanData(this, "sobot_isShowCamera", false);
        setImageShowStatus8(status8);
        status9 = SobotSPUtil.getBooleanData(this, "sobot_isShowFile", false);
        setImageShowStatus9(status9);
        status10 = SobotSPUtil.getBooleanData(this, "sobot_leave_complete_can_reply", false);
        setImageShowStatus10(status10);
        status11 = SobotSPUtil.getBooleanData(this, "sobot_isshowLeaveDetailBackEvaluate", false);
        setImageShowStatus11(status11);

    }

    private void saveSobotStartSet() {
        SobotSPUtil.saveBooleanData(this, "sobot_isLandscapeScreen", isLandscape_Screen);
        SobotSPUtil.saveBooleanData(this, "isInterceptLink", isInterceptLink);
        SobotSPUtil.saveBooleanData(this, "isShowChatStatus", isShowChatStatus);

        SobotSPUtil.saveBooleanData(this, "sobot_isShowLeaveMsg", status4);
        SobotSPUtil.saveBooleanData(this, "sobot_isShowSatisfaction", status5);
        SobotSPUtil.saveBooleanData(this, "sobot_isShowPicture", status6);
        SobotSPUtil.saveBooleanData(this, "sobot_isShowVedio", status7);
        SobotSPUtil.saveBooleanData(this, "sobot_isShowCamera", status8);
        SobotSPUtil.saveBooleanData(this, "sobot_isShowFile", status9);

        SobotSPUtil.saveBooleanData(this, "sobot_leave_complete_can_reply", status10);
        SobotSPUtil.saveBooleanData(this, "sobot_isshowLeaveDetailBackEvaluate", status11);

    }

    @Override
    public void onBackPressed() {
        saveSobotStartSet();
        finish();
    }


    @Override
    public void onClick(View v) {
        if (v == sobotDemoTvLeft) {
            saveSobotStartSet();
            finish();
        } else if (v == sobotScreenSet) {//横竖屏
            isLandscape_Screen = !isLandscape_Screen;
            setShowSatisfaction(isLandscape_Screen);
            saveSobotStartSet();
        } else if (v == sobotIsShowSatisfactionClose) {//超链接拦截
            isInterceptLink = !isInterceptLink;
            setShowSatisfactionClose(isInterceptLink);
            saveSobotStartSet();
        } else if (v == sobotRl3) {//是否显示聊天状态
            isShowChatStatus = !isShowChatStatus;
            setImageShowChatStatus(isShowChatStatus);
            saveSobotStartSet();
        } else if (v == sobotRl4) {//是否显示留言
            status4 = !status4;
            setImageShowStatus4(status4);
            saveSobotStartSet();
        } else if (v == sobotRl5) {//是否显示服务评价
            status5 = !status5;
            setImageShowStatus5(status5);
            saveSobotStartSet();
        } else if (v == sobotRl6) {//是否显示发送图片
            status6 = !status6;
            setImageShowStatus6(status6);
            saveSobotStartSet();
        } else if (v == sobotRl7) {//是否显示显示发送视频
            status7 = !status7;
            setImageShowStatus7(status7);
            saveSobotStartSet();
        } else if (v == sobotRl8) {//是否显示拍摄
            status8 = !status8;
            setImageShowStatus8(status8);
            saveSobotStartSet();
        } else if (v == sobotRl9) {//是否显示发送文件
            status9 = !status9;
            setImageShowStatus9(status9);
            saveSobotStartSet();
        }
        else if (v == sobotRl10) {//是否显示发送文件
            status10 = !status10;
            setImageShowStatus10(status10);
            saveSobotStartSet();
        }
        else if (v == sobotRl11) {//是否显示发送文件
            status11 = !status11;
            setImageShowStatus11(status11);
            saveSobotStartSet();
        }
    }

    private void setImageShowChatStatus(boolean open) {
        if (open) {
            isShowChatStatus = true;
            sobotImage3.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            isShowChatStatus = false;
            sobotImage3.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setImageShowStatus4(boolean open) {
        if (open) {
            status4 = true;
            sobotImage4.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status4 = false;
            sobotImage4.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setImageShowStatus5(boolean open) {
        if (open) {
            status5 = true;
            sobotImage5.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status5 = false;
            sobotImage5.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setImageShowStatus6(boolean open) {
        if (open) {
            status6 = true;
            sobotImage6.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status6 = false;
            sobotImage6.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setImageShowStatus7(boolean open) {
        if (open) {
            status7 = true;
            sobotImage7.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status7 = false;
            sobotImage7.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setImageShowStatus8(boolean open) {
        if (open) {
            status8 = true;
            sobotImage8.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status8 = false;
            sobotImage8.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setImageShowStatus9(boolean open) {
        if (open) {
            status9 = true;
            sobotImage9.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status9 = false;
            sobotImage9.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }
    private void setImageShowStatus10(boolean open) {
        if (open) {
            status10 = true;
            sobotImage10.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status10 = false;
            sobotImage10.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }
    private void setImageShowStatus11(boolean open) {
        if (open) {
            status11 = true;
            sobotImage11.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            status11 = false;
            sobotImage11.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setShowSatisfaction(boolean open) {
        if (open) {
            isLandscape_Screen = true;
            imgOpenClose.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            isLandscape_Screen = false;
            imgOpenClose.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setShowSatisfactionClose(boolean open) {
        if (open) {
            isInterceptLink = true;
            imgOpenNotify3.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            isInterceptLink = false;
            imgOpenNotify3.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }


}