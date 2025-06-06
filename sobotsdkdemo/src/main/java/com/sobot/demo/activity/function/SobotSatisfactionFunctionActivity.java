package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.activity.SobotDemoBaseActivity;

public class SobotSatisfactionFunctionActivity extends SobotDemoBaseActivity implements View.OnClickListener {

    private RelativeLayout sobot_tv_left, sobot_rl_4_4_2_1, sobot_rl_4_4_2_2, sobot_rl_4_4_3_1, sobot_rl_4_4_3_2, sobot_rl_4_4_4, sobot_rl_4_4_5;
    private ImageView sobotImage4421, sobotImage4422, sobotImage4431, sobotImage4432, sobotImage444, sobotImage445;
    private boolean status4421, status4422, status4431, status4432, status444, status445;
    private TextView tv_satisfaction_fun_4_4_2, tv_satisfaction_fun_4_4_3, tv_satisfaction_fun_4_4_4, tv_satisfaction_fun_4_4_5, sobot_tv_save;
    private Information information;

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_demo_satisfaction_func_activity;
    }

    @Override
    protected void initView() {
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
        sobot_text_title.setText("评价");
        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setVisibility(View.VISIBLE);
        sobot_tv_save.setOnClickListener(this);
        tv_satisfaction_fun_4_4_2 = findViewById(R.id.tv_satisfaction_fun_4_4_2);
        tv_satisfaction_fun_4_4_2.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-4-2-导航栏左侧点击返回时是否弹出满意度评价");
        setOnClick(tv_satisfaction_fun_4_4_2, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-4-2-%E5%AF%BC%E8%88%AA%E6%A0%8F%E5%B7%A6%E4%BE%A7%E7%82%B9%E5%87%BB%E8%BF%94%E5%9B%9E%E6%97%B6%E6%98%AF%E5%90%A6%E5%BC%B9%E5%87%BA%E6%BB%A1%E6%84%8F%E5%BA%A6%E8%AF%84%E4%BB%B7");
        tv_satisfaction_fun_4_4_3 = findViewById(R.id.tv_satisfaction_fun_4_4_3);
        tv_satisfaction_fun_4_4_3.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-4-3-导航栏右侧关闭按钮是否显示和点击时是否弹出满意度评价");
        setOnClick(tv_satisfaction_fun_4_4_3, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-4-3-%E5%AF%BC%E8%88%AA%E6%A0%8F%E5%8F%B3%E4%BE%A7%E5%85%B3%E9%97%AD%E6%8C%89%E9%92%AE%E6%98%AF%E5%90%A6%E6%98%BE%E7%A4%BA%E5%92%8C%E7%82%B9%E5%87%BB%E6%97%B6%E6%98%AF%E5%90%A6%E5%BC%B9%E5%87%BA%E6%BB%A1%E6%84%8F%E5%BA%A6%E8%AF%84%E4%BB%B7");
        tv_satisfaction_fun_4_4_4 = findViewById(R.id.tv_satisfaction_fun_4_4_4);
        tv_satisfaction_fun_4_4_4.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-4-4-配置用户提交人工满意度评价后释放会话");
        setOnClick(tv_satisfaction_fun_4_4_4, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-4-4-%E9%85%8D%E7%BD%AE%E7%94%A8%E6%88%B7%E6%8F%90%E4%BA%A4%E4%BA%BA%E5%B7%A5%E6%BB%A1%E6%84%8F%E5%BA%A6%E8%AF%84%E4%BB%B7%E5%90%8E%E9%87%8A%E6%94%BE%E4%BC%9A%E8%AF%9D");
        tv_satisfaction_fun_4_4_5 = findViewById(R.id.tv_satisfaction_fun_4_4_5);
        tv_satisfaction_fun_4_4_5.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-4-5-左上角返回和右上角关闭时,人工满意度评价弹窗界面配置是否显示暂不评价按钮");
        setOnClick(tv_satisfaction_fun_4_4_5, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-4-5-%E5%B7%A6%E4%B8%8A%E8%A7%92%E8%BF%94%E5%9B%9E%E5%92%8C%E5%8F%B3%E4%B8%8A%E8%A7%92%E5%85%B3%E9%97%AD%E6%97%B6-%E4%BA%BA%E5%B7%A5%E6%BB%A1%E6%84%8F%E5%BA%A6%E8%AF%84%E4%BB%B7%E5%BC%B9%E7%AA%97%E7%95%8C%E9%9D%A2%E9%85%8D%E7%BD%AE%E6%98%AF%E5%90%A6%E6%98%BE%E7%A4%BA%E6%9A%82%E4%B8%8D%E8%AF%84%E4%BB%B7%E6%8C%89%E9%92%AE");

        sobot_rl_4_4_2_1 = (RelativeLayout) findViewById(R.id.sobot_rl_4_4_2_1);
        sobot_rl_4_4_2_1.setOnClickListener(this);
        sobotImage4421 = (ImageView) findViewById(R.id.sobot_image_4_4_2_1);

        sobot_rl_4_4_2_2 = (RelativeLayout) findViewById(R.id.sobot_rl_4_4_2_2);
        sobot_rl_4_4_2_2.setOnClickListener(this);
        sobotImage4422 = (ImageView) findViewById(R.id.sobot_image_4_4_2_2);

        sobot_rl_4_4_3_1 = (RelativeLayout) findViewById(R.id.sobot_rl_4_4_3_1);
        sobot_rl_4_4_3_1.setOnClickListener(this);
        sobotImage4431 = (ImageView) findViewById(R.id.sobot_image_4_4_3_1);

        sobot_rl_4_4_3_2 = (RelativeLayout) findViewById(R.id.sobot_rl_4_4_3_2);
        sobot_rl_4_4_3_2.setOnClickListener(this);
        sobotImage4432 = (ImageView) findViewById(R.id.sobot_image_4_4_3_2);

        sobot_rl_4_4_4 = (RelativeLayout) findViewById(R.id.sobot_rl_4_4_4);
        sobot_rl_4_4_4.setOnClickListener(this);
        sobotImage444 = (ImageView) findViewById(R.id.sobot_image_4_4_4);

        sobot_rl_4_4_5 = (RelativeLayout) findViewById(R.id.sobot_rl_4_4_5);
        sobot_rl_4_4_5.setOnClickListener(this);
        sobotImage445 = (ImageView) findViewById(R.id.sobot_image_4_4_5);

        if (information != null) {
            status4421 = information.isShowLeftBackPop();
            setImageShowStatus(status4421, sobotImage4421);
            status4422 = information.isShowSatisfaction();
            setImageShowStatus(status4422, sobotImage4422);
            status4431 = information.isShowCloseBtn();
            setImageShowStatus(status4431, sobotImage4431);
            status4432 = information.isShowCloseSatisfaction();
            setImageShowStatus(status4432, sobotImage4432);
            status444 = SharedPreferencesUtil.getBooleanData(getContext(), ZhiChiConstant.SOBOT_CHAT_EVALUATION_COMPLETED_EXIT, false);
            setImageShowStatus(status444, sobotImage444);
            status445 = information.isCanBackWithNotEvaluation();
            setImageShowStatus(status445, sobotImage445);
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
        if (v.getId() == R.id.sobot_tv_save) {
            if (information != null) {
                information.setShowLeftBackPop(status4421);
                information.setShowSatisfaction(status4422);
                information.setShowCloseBtn(status4431);
                information.setShowCloseSatisfaction(status4432);
                ZCSobotApi.setEvaluationCompletedExit(SobotSatisfactionFunctionActivity.this, status444);
                information.setCanBackWithNotEvaluation(status445);
                SobotSPUtil.saveObject(this, "sobot_demo_infomation", information);
            }
            ToastUtil.showToast(getContext(), "已保存");
            finish();
        } else if (v.getId() == R.id.sobot_rl_4_4_2_1) {
            toggleStatus(status4421, sobotImage4421);
        } else if (v.getId() == R.id.sobot_rl_4_4_2_2) {
            toggleStatus(status4422, sobotImage4422);
        } else if (v.getId() == R.id.sobot_rl_4_4_3_1) {
            toggleStatus(status4431, sobotImage4431);
        } else if (v.getId() == R.id.sobot_rl_4_4_3_2) {
            toggleStatus(status4432, sobotImage4432);
        } else if (v.getId() == R.id.sobot_rl_4_4_4) {
            toggleStatus(status444, sobotImage444);
        } else if (v.getId() == R.id.sobot_rl_4_4_5) {
            toggleStatus(status445, sobotImage445);
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