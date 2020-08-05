package com.sobot.demo.activity.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.R;

/**
 * Created by Administrator on 2017/11/21.
 */

public class SobotCloseButtonSetActivity extends AppCompatActivity implements View.OnClickListener{
    private RelativeLayout sobot_isShowClose;//是否显示关闭按钮
    private RelativeLayout sobot_isShowSatisfactionClose;//是否弹出满意度评价
    private RelativeLayout sobot_evaluationCompletedExit;//评价完是否结束会话
    private boolean isShowClose = false,isShowSatisfactionclose = false, isEvaluationCompletedExit = false;
    private RelativeLayout sobot_tv_left;
    private ImageView imageView3, imageView4,imageView5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_satisfaction_set_close_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        findvViews();
    }

    private void findvViews(){
        sobot_isShowClose = (RelativeLayout) findViewById(R.id.sobot_isShowClose);
        sobot_isShowSatisfactionClose = (RelativeLayout) findViewById(R.id.sobot_isShowSatisfactionClose);
        sobot_evaluationCompletedExit = (RelativeLayout) findViewById(R.id.sobot_evaluationCompletedExit);
        imageView3 = (ImageView) findViewById(R.id.img_open_notify3);
        imageView4 = (ImageView) findViewById(R.id.img_open_notify4);
        imageView5 = (ImageView) findViewById(R.id.img_open_close);
        sobot_isShowClose.setOnClickListener(this);
        sobot_isShowSatisfactionClose.setOnClickListener(this);
        sobot_evaluationCompletedExit.setOnClickListener(this);
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        sobot_tv_left .setOnClickListener(this);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("关闭设置");

        getSobotStartSet();
    }

    private void getSobotStartSet() {
        boolean sobot_bool_isShowSatisfaction = SobotSPUtil.getBooleanData(this, "sobot_isShowSatisfaction_close", false);
        setShowSatisfaction(sobot_bool_isShowSatisfaction);
        boolean sobot_isShow_close = SobotSPUtil.getBooleanData(this, "sobot_isShow_close", false);
        setShowSatisfactionClose(sobot_isShow_close);
        boolean sobot_evaluationCompletedExit_value = SobotSPUtil.getBooleanData(this, "sobot_evaluationCompletedExit_value", false);
        setEvaluationCompletedExit(sobot_evaluationCompletedExit_value);
    }

    private void saveSobotStartSet() {
        SobotSPUtil.saveBooleanData(this, "sobot_isShow_close", isShowClose);
        SobotSPUtil.saveBooleanData(this, "sobot_isShowSatisfaction_close", isShowSatisfactionclose);
        SobotSPUtil.saveBooleanData(this, "sobot_evaluationCompletedExit_value", isEvaluationCompletedExit);
    }

    @Override
    public void onBackPressed() {
        saveSobotStartSet();
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_left){
            saveSobotStartSet();
            finish();
        } else if (v == sobot_isShowSatisfactionClose){
            isShowSatisfactionclose = !isShowSatisfactionclose;
            setShowSatisfaction(isShowSatisfactionclose);
        } else if (v == sobot_evaluationCompletedExit){
            isEvaluationCompletedExit = !isEvaluationCompletedExit;
            setEvaluationCompletedExit(isEvaluationCompletedExit);
        } else if(v == sobot_isShowClose) {
            isShowClose = !isShowClose;
            setShowSatisfactionClose(isShowClose);
        }
    }

    private void setShowSatisfaction(boolean open){
        if (open){
            isShowSatisfactionclose = true;
            imageView3.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            isShowSatisfactionclose = false;
            imageView3.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }
    private void setShowSatisfactionClose(boolean open){
        if (open){
            isShowClose = true;
            imageView5.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            isShowClose = false;
            imageView5.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    private void setEvaluationCompletedExit(boolean open){
        if (open){
            isEvaluationCompletedExit = true;
            imageView4.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            isEvaluationCompletedExit = false;
            imageView4.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }
}