package com.sobot.chat.widget.dialog;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotDialogBaseActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotUserTicketEvaluate;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.widget.SobotEditTextLayout;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import java.util.List;

/**
 * 评价界面的显示
 * Created by jinxl on 2017/6/12.
 */
public class SobotTicketEvaluateActivity extends SobotDialogBaseActivity {
    private LinearLayout sobot_negativeButton;
    private RatingBar sobot_ratingBar;//评价  打分
    private TextView sobot_ratingBar_title;//评价  对人工客服打分不同显示不同的内容
    private EditText sobot_add_content;
    private TextView sobot_tv_evaluate_title;

    private SobotEditTextLayout setl_submit_content;//评价框
    private Button sobot_close_now;//提交评价按钮
    private SobotUserTicketEvaluate mEvaluate;


    @Override
    protected int getContentViewResId() {
        return ResourceUtils.getResLayoutId(this, "sobot_layout_ticket_evaluate");
    }

    @Override
    protected void initView() {
        sobot_add_content = (EditText) findViewById(getResId("sobot_add_content"));
        sobot_add_content.setHint(ResourceUtils.getResString(this,"sobot_edittext_hint"));
        sobot_close_now = (Button) findViewById(getResId("sobot_close_now"));
        sobot_close_now.setText(ResourceUtils.getResString(this,"sobot_btn_submit_text"));
        sobot_tv_evaluate_title= (TextView) findViewById(getResId("sobot_tv_evaluate_title"));
        sobot_tv_evaluate_title.setText(ResourceUtils.getResString(this,"sobot_please_comment"));
        sobot_ratingBar = (RatingBar) findViewById(getResId("sobot_ratingBar"));
        setl_submit_content = (SobotEditTextLayout) findViewById(getResId("setl_submit_content"));
        sobot_negativeButton = (LinearLayout) findViewById(getResId("sobot_negativeButton"));
        sobot_negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sobot_ratingBar_title = (TextView) findViewById(getResId("sobot_ratingBar_title"));
        mEvaluate = (SobotUserTicketEvaluate) getIntent().getSerializableExtra("sobotUserTicketEvaluate");
        if (mEvaluate != null) {
            if (mEvaluate.isOpen()) {
                sobot_add_content.setVisibility(mEvaluate.isTxtFlag() ? View.VISIBLE : View.GONE);
            }
            setViewListener();
        }
    }

    @Override
    protected void initData() {

    }

    private void setViewListener() {
        Information information = (Information) SharedPreferencesUtil.getObject(getContext(), "sobot_last_current_info");
        //根据infomation 配置是否隐藏星星评价描述
        if (!information.isHideManualEvaluationLabels()) {
            sobot_ratingBar_title.setVisibility(View.VISIBLE);
        } else {
            sobot_ratingBar_title.setVisibility(View.GONE);
        }
        sobot_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int score = (int) Math.ceil(sobot_ratingBar.getRating());
                if (score > 0 && score <= 5) {
                    List<SobotUserTicketEvaluate.TicketScoreInfooListBean> scoreInfooList = mEvaluate.getTicketScoreInfooList();
                    if (scoreInfooList != null && scoreInfooList.size() >= score) {
                        SobotUserTicketEvaluate.TicketScoreInfooListBean data = scoreInfooList.get(5 - score);
                        sobot_ratingBar_title.setText(data.getScoreExplain());
                    }
                }
            }
        });
        sobot_ratingBar.setRating(5);
        sobot_close_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交评价
                int score = (int) Math.ceil(sobot_ratingBar.getRating());
                KeyboardUtil.hideKeyboard(sobot_add_content);
                Intent intent = new Intent();
                intent.putExtra("score", score);
                intent.putExtra("content", sobot_add_content.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }
}