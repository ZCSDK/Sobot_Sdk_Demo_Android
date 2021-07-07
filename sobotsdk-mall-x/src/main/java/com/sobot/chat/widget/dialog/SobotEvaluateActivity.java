package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotDialogBaseActivity;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SatisfactionSet;
import com.sobot.chat.api.model.SatisfactionSetBase;
import com.sobot.chat.api.model.SobotCommentParam;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.SobotEditTextLayout;
import com.sobot.chat.widget.SobotTenRatingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价界面的显示
 * Created by jinxl on 2017/6/12.
 */
public class SobotEvaluateActivity extends SobotDialogBaseActivity {
    private final String CANCEL_TAG = SobotEvaluateActivity.class.getSimpleName();
    private Activity context;
    private int score;//默认 选中几颗星 从前面界面传过来
    private int isSolve;//默认 是否已解决 从前面界面传过来  0 是已解决  1 未解决
    private boolean isFinish;
    private boolean isExitSession;
    private boolean isSessionOver;//会话是否已经结束
    private boolean isBackShowEvaluate;//是否是 返回弹出评价框
    private ZhiChiInitModeBase initModel;
    private Information information;
    private int current_model;
    private int commentType;/*commentType 评价类型 主动评价1 邀请评价0*/
    private String customName;
    private List<SatisfactionSetBase> satisFactionList;
    private SatisfactionSetBase satisfactionSetBase;
    private LinearLayout sobot_negativeButton;
    private LinearLayout coustom_pop_layout;
    private LinearLayout sobot_robot_relative;//评价 机器人布局
    private LinearLayout sobot_custom_relative;//评价人工布局
    private LinearLayout sobot_hide_layout;//评价机器人和人工未解决时显示出来的布局
    private RadioGroup sobot_readiogroup;//
    private RadioButton sobot_btn_ok_robot;//评价  已解决
    private RadioButton sobot_btn_no_robot;//评价  未解决
    private Button sobot_close_now;//提交评价按钮
    private View sobot_ratingBar_split_view;//如果有已解决按钮和未解决按钮就显示，否则隐藏；

    private EditText sobot_add_content;//评价  添加建议
    private TextView sobot_tv_evaluate_title;//评价   当前是评价机器人还是评价人工客服
    private TextView sobot_robot_center_title;//评价  机器人或人工是否解决了问题的标题
    private TextView sobot_text_other_problem;//评价  机器人或人工客服存在哪些问题的标题
    private TextView sobot_custom_center_title;//评价  对 哪个人工客服 打分  的标题
    private TextView sobot_ratingBar_title;//评价  对人工客服打分不同显示不同的内容
    private TextView sobot_evaluate_cancel;//评价  暂不评价
    private TextView sobot_tv_evaluate_title_hint;//评价  提交后结束评价
    private RatingBar sobot_ratingBar;//评价  打分
    private LinearLayout sobot_ten_root_ll;//评价  十分全布局
    private TextView sobot_ten_very_dissatisfied;//评价 非常不满意
    private TextView sobot_ten_very_satisfaction;//评价  非常满意
    private SobotTenRatingLayout sobot_ten_rating_ll;//评价  十分 父布局 动态添加10个textview
    private int ratingType;//评价  类型   0 5星 ；1 十分 默认5星

    private String evaluateChecklables;//主动邀请评价选中的标签
    private LinearLayout sobot_evaluate_ll_lable1;//评价  用来放前两个标签，标签最多可以有六个
    private LinearLayout sobot_evaluate_ll_lable2;//评价  用来放中间两个标签
    private LinearLayout sobot_evaluate_ll_lable3;//评价  用来放最后两个标签
    private CheckBox sobot_evaluate_cb_lable1;//六个评价标签
    private CheckBox sobot_evaluate_cb_lable2;
    private CheckBox sobot_evaluate_cb_lable3;
    private CheckBox sobot_evaluate_cb_lable4;
    private CheckBox sobot_evaluate_cb_lable5;
    private CheckBox sobot_evaluate_cb_lable6;
    private SobotEditTextLayout setl_submit_content;//评价框

    private List<CheckBox> checkBoxList = new ArrayList<>();


    @Override
    protected int getContentViewResId() {
        return ResourceUtils.getResLayoutId(this, "sobot_layout_evaluate");
    }

    @Override
    protected void initView() {
        information = (Information) SharedPreferencesUtil.getObject(getContext(), "sobot_last_current_info");
        context = getContext();
        score = getIntent().getIntExtra("score", 0);
        evaluateChecklables = getIntent().getStringExtra("evaluateChecklables");
        isFinish = getIntent().getBooleanExtra("isFinish", false);
        isSessionOver = getIntent().getBooleanExtra("isSessionOver", false);
        isExitSession = getIntent().getBooleanExtra("isExitSession", false);
        isBackShowEvaluate = getIntent().getBooleanExtra("isBackShowEvaluate", false);
        initModel = (ZhiChiInitModeBase) getIntent().getSerializableExtra("initModel");
        current_model = getIntent().getIntExtra("current_model", 0);
        commentType = getIntent().getIntExtra("commentType", 0);
        customName = getIntent().getStringExtra("customName");
        isSolve = getIntent().getIntExtra("isSolve", 0);
        sobot_close_now = (Button) findViewById(getResId("sobot_close_now"));
        sobot_close_now.setText(ResourceUtils.getResString(context, "sobot_btn_submit_text"));
        sobot_readiogroup = (RadioGroup) findViewById(getResId("sobot_readiogroup"));
        sobot_tv_evaluate_title = (TextView) findViewById(getResId("sobot_tv_evaluate_title"));
        sobot_tv_evaluate_title.setText(ResourceUtils.getResString(context, "sobot_robot_customer_service_evaluation"));
        sobot_robot_center_title = (TextView) findViewById(getResId("sobot_robot_center_title"));
        sobot_robot_center_title.setText(ResourceUtils.getResString(context, "sobot_question"));
        sobot_text_other_problem = (TextView) findViewById(getResId("sobot_text_other_problem"));
        sobot_custom_center_title = (TextView) findViewById(getResId("sobot_custom_center_title"));
        sobot_custom_center_title.setText(ResourceUtils.getResString(context, "sobot_please_evaluate"));
        sobot_ratingBar_title = (TextView) findViewById(getResId("sobot_ratingBar_title"));
        sobot_ratingBar_title.setText(ResourceUtils.getResString(context, "sobot_great_satisfaction"));
        sobot_tv_evaluate_title_hint = (TextView) findViewById(getResId("sobot_tv_evaluate_title_hint"));
        sobot_evaluate_cancel = (TextView) findViewById(getResId("sobot_evaluate_cancel"));
        sobot_evaluate_cancel.setText(ResourceUtils.getResString(context, "sobot_temporarily_not_evaluation"));
        sobot_ratingBar_split_view = findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_ratingBar_split_view"));
        sobot_negativeButton = (LinearLayout) findViewById(getResId("sobot_negativeButton"));
        sobot_negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (information != null && information.isCanBackWithNotEvaluation()) {
            sobot_evaluate_cancel.setVisibility(View.VISIBLE);
        } else {
            sobot_evaluate_cancel.setVisibility(View.GONE);
        }
        sobot_ratingBar = (RatingBar) findViewById(getResId("sobot_ratingBar"));
        sobot_ten_root_ll = findViewById(getResId("sobot_ten_root_ll"));
        sobot_ten_rating_ll = findViewById(getResId("sobot_ten_rating_ll"));
        sobot_ten_very_dissatisfied= findViewById(getResId("sobot_ten_very_dissatisfied"));
        sobot_ten_very_satisfaction= findViewById(getResId("sobot_ten_very_satisfaction"));
        sobot_ten_very_dissatisfied.setText(ResourceUtils.getResString(context, "sobot_very_dissatisfied"));
        sobot_ten_very_satisfaction.setText(ResourceUtils.getResString(context, "sobot_great_satisfaction"));

        sobot_evaluate_ll_lable1 = (LinearLayout) findViewById(getResId("sobot_evaluate_ll_lable1"));
        sobot_evaluate_ll_lable2 = (LinearLayout) findViewById(getResId("sobot_evaluate_ll_lable2"));
        sobot_evaluate_ll_lable3 = (LinearLayout) findViewById(getResId("sobot_evaluate_ll_lable3"));
        sobot_evaluate_cb_lable1 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable1"));
        sobot_evaluate_cb_lable2 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable2"));
        sobot_evaluate_cb_lable3 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable3"));
        sobot_evaluate_cb_lable4 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable4"));
        sobot_evaluate_cb_lable5 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable5"));
        sobot_evaluate_cb_lable6 = (CheckBox) findViewById(getResId("sobot_evaluate_cb_lable6"));
        checkBoxList.add(sobot_evaluate_cb_lable1);
        checkBoxList.add(sobot_evaluate_cb_lable2);
        checkBoxList.add(sobot_evaluate_cb_lable3);
        checkBoxList.add(sobot_evaluate_cb_lable4);
        checkBoxList.add(sobot_evaluate_cb_lable5);
        checkBoxList.add(sobot_evaluate_cb_lable6);
        sobot_add_content = (EditText) findViewById(getResId("sobot_add_content"));
        sobot_add_content.setHint(ResourceUtils.getResString(context, "sobot_edittext_hint"));
        sobot_btn_ok_robot = (RadioButton) findViewById(getResId("sobot_btn_ok_robot"));
        sobot_btn_ok_robot.setText(ResourceUtils.getResString(context, "sobot_evaluate_yes"));
        sobot_btn_ok_robot.setChecked(true);
        sobot_btn_no_robot = (RadioButton) findViewById(getResId("sobot_btn_no_robot"));
        sobot_btn_no_robot.setText(ResourceUtils.getResString(context, "sobot_evaluate_no"));
        sobot_robot_relative = (LinearLayout) findViewById(getResId("sobot_robot_relative"));
        sobot_custom_relative = (LinearLayout) findViewById(getResId("sobot_custom_relative"));
        sobot_hide_layout = (LinearLayout) findViewById(getResId("sobot_hide_layout"));
        setl_submit_content = (SobotEditTextLayout) findViewById(getResId("setl_submit_content"));

        setViewGone();
        setViewListener();
        if (ScreenUtils.isFullScreen(context)) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        displayInNotch(setl_submit_content);
    }

    @Override
    protected void initData() {

        if (current_model == ZhiChiConstant.client_model_customService) {
            ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
            zhiChiApi.satisfactionMessage(CANCEL_TAG, initModel.getPartnerid(), new ResultCallBack<SatisfactionSet>() {
                @Override
                public void onSuccess(SatisfactionSet satisfactionSet) {
                    if (satisfactionSet != null && "1".equals(satisfactionSet.getCode()) && satisfactionSet.getData() != null && satisfactionSet.getData().size() != 0) {
                        satisFactionList = satisfactionSet.getData();
                        if (commentType == 1) {
                            //主动评价需要判断默认星级
                            if (satisFactionList.get(0) != null && satisFactionList.get(0).getDefaultType() != -1) {
                                if (satisFactionList.get(0).getScoreFlag() == 0) {
                                    //defaultType 0-默认5星,1-默认0星
                                    score = (satisFactionList.get(0).getDefaultType() == 0) ? 5 : 0;
                                    sobot_ten_root_ll.setVisibility(View.GONE);
                                    sobot_ratingBar.setVisibility(View.VISIBLE);
                                    ratingType = 0;//5星
                                } else {
                                    sobot_ten_root_ll.setVisibility(View.VISIBLE);
                                    sobot_ratingBar.setVisibility(View.GONE);
                                    ratingType = 1;//十分
                                    // defaultType 0-默认10分,1-默认5分,2-默认0分
                                    if (satisFactionList.get(0).getDefaultType() == 2) {
                                        score = 0;
                                    } else if (satisFactionList.get(0).getDefaultType() == 1) {
                                        score = 5;
                                    } else {
                                        score = 10;
                                    }
                                }
                            }
                        } else {
                            if (satisFactionList.get(0) != null && satisFactionList.get(0).getDefaultType() != -1) {
                                if (satisFactionList.get(0).getScoreFlag() == 0) {
                                    //defaultType 0-默认5星,1-默认0星
                                    sobot_ten_root_ll.setVisibility(View.GONE);
                                    sobot_ratingBar.setVisibility(View.VISIBLE);
                                    ratingType = 0;//5星
                                } else {
                                    sobot_ten_root_ll.setVisibility(View.VISIBLE);
                                    sobot_ratingBar.setVisibility(View.GONE);
                                    ratingType = 1;//十分
                                }
                            }
                        }
                        if (ratingType == 0) {
                            if (score == -1) {
                                score = 5;
                            }
                            sobot_ratingBar.setRating(score);
                        } else {
                            if (score == -1) {
                                score = 10;
                            }
                            sobot_ten_rating_ll.init(score, true);
                        }

                        if (isSolve == 0) {
                            sobot_btn_ok_robot.setChecked(true);
                            sobot_btn_no_robot.setChecked(false);
                        } else {
                            sobot_btn_ok_robot.setChecked(false);
                            sobot_btn_no_robot.setChecked(true);
                        }

                        setCustomLayoutViewVisible(score, satisFactionList);
                        if (ratingType == 0) {
                            if (0 == score) {
                                sobot_close_now.setVisibility(View.GONE);
                                sobot_ratingBar_title.setText(ResourceUtils.getResString(getContext(), "sobot_evaluate_zero_score_des"));
                                sobot_ratingBar_title.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_common_gray3")));
                            } else {
                                sobot_close_now.setVisibility(View.VISIBLE);
                                if (satisfactionSetBase != null) {
                                    sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
                                }
                                sobot_ratingBar_title.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_color_evaluate_ratingBar_des_tv")));
                            }
                        } else {
                            sobot_close_now.setVisibility(View.VISIBLE);
                            if (satisfactionSetBase != null) {
                                sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
                            }
                            sobot_ratingBar_title.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_color_evaluate_ratingBar_des_tv")));

                        }

                        if (satisFactionList.get(0).getIsQuestionFlag()) {
                            sobot_robot_relative.setVisibility(View.VISIBLE);
                            sobot_ratingBar_split_view.setVisibility(View.VISIBLE);
                        } else {
                            sobot_robot_relative.setVisibility(View.GONE);
                            sobot_ratingBar_split_view.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                }
            });
        }
    }

    private void setViewListener() {
        sobot_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                sobot_close_now.setVisibility(View.VISIBLE);
                int score = (int) Math.ceil(sobot_ratingBar.getRating());
                if (score == 0) {
                    sobot_ratingBar.setRating(1);
                }
                if (score > 0 && score <= 5) {
                    sobot_close_now.setSelected(true);
                    setCustomLayoutViewVisible(score, satisFactionList);
                }
            }
        });

        sobot_readiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (current_model == ZhiChiConstant.client_model_robot && initModel != null) {
                    if (checkedId == getResId("sobot_btn_ok_robot")) {
                        sobot_hide_layout.setVisibility(View.GONE);
                        setl_submit_content.setVisibility(View.GONE);
                    } else if (checkedId == getResId("sobot_btn_no_robot")) {
                        sobot_hide_layout.setVisibility(View.VISIBLE);
                        setl_submit_content.setVisibility(View.VISIBLE);
                        String tmpData[] = convertStrToArray(initModel.getRobotCommentTitle());
                        if (tmpData != null && tmpData.length > 0) {
                            setLableViewVisible(tmpData);
                        } else {
                            sobot_hide_layout.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        sobot_close_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subMitEvaluate();
            }
        });

        sobot_evaluate_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent();
                intent.setAction(ZhiChiConstants.sobot_close_now);
                intent.putExtra("isBackShowEvaluate", isBackShowEvaluate);
                CommonUtils.sendLocalBroadcast(context.getApplicationContext(), intent);
            }
        });

        //监听10分评价选择变化
        if (sobot_ten_rating_ll != null) {
            sobot_ten_rating_ll.setOnClickItemListener(new SobotTenRatingLayout.OnClickItemListener() {
                @Override
                public void onClickItem(int selectIndex) {
                    sobot_close_now.setVisibility(View.VISIBLE);
                    sobot_close_now.setSelected(true);
                    setCustomLayoutViewVisible(selectIndex, satisFactionList);
                }
            });
        }
    }


    private void setViewGone() {
        sobot_hide_layout.setVisibility(View.GONE);
        setl_submit_content.setVisibility(View.GONE);
        sobot_evaluate_ll_lable1.setVisibility(View.GONE);
        sobot_evaluate_ll_lable2.setVisibility(View.GONE);
        sobot_evaluate_ll_lable3.setVisibility(View.GONE);
        sobot_evaluate_cb_lable1.setVisibility(View.GONE);
        sobot_evaluate_cb_lable2.setVisibility(View.GONE);
        sobot_evaluate_cb_lable3.setVisibility(View.GONE);
        sobot_evaluate_cb_lable4.setVisibility(View.GONE);
        sobot_evaluate_cb_lable5.setVisibility(View.GONE);
        sobot_evaluate_cb_lable6.setVisibility(View.GONE);

        if (current_model == ZhiChiConstant.client_model_robot) {
            sobot_tv_evaluate_title.setText(getResString("sobot_robot_customer_service_evaluation"));
            sobot_robot_center_title.setText(initModel.getRobotName() + ChatUtils.getResString(context, "sobot_question"));
            sobot_robot_relative.setVisibility(View.VISIBLE);
            sobot_custom_relative.setVisibility(View.GONE);
        } else {
            boolean isExitTalk = SharedPreferencesUtil.getBooleanData(context, ZhiChiConstant.SOBOT_CHAT_EVALUATION_COMPLETED_EXIT, false);

            if (isExitTalk && !isSessionOver) {
                sobot_tv_evaluate_title_hint.setText(getResString("sobot_evaluation_completed_exit"));
                sobot_tv_evaluate_title_hint.setVisibility(View.VISIBLE);
            } else {
                sobot_tv_evaluate_title_hint.setVisibility(View.GONE);
            }
            sobot_tv_evaluate_title.setText(getResString("sobot_please_evaluate_this_service"));
            sobot_robot_center_title.setText(customName + " " + ChatUtils.getResString(context, "sobot_question"));
            sobot_custom_center_title.setText(customName + " " + ChatUtils.getResString(context, "sobot_please_evaluate"));
            sobot_robot_relative.setVisibility(View.GONE);
            sobot_custom_relative.setVisibility(View.VISIBLE);
        }
    }

    //设置人工客服评价的布局显示逻辑
    private void setCustomLayoutViewVisible(int score, List<
            SatisfactionSetBase> satisFactionList) {
        satisfactionSetBase = getSatisFaction(score, satisFactionList);
        for (int i = 0; i < checkBoxList.size(); i++) {
            checkBoxList.get(i).setChecked(false);
        }
        if (satisfactionSetBase != null) {
            sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
            sobot_ratingBar_title.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_color_evaluate_ratingBar_des_tv")));
            if (!TextUtils.isEmpty(satisfactionSetBase.getInputLanguage())) {
                if (satisfactionSetBase.getIsInputMust()) {
                    sobot_add_content.setHint(getResString("sobot_required") + satisfactionSetBase.getInputLanguage().replace("<br/>", "\n"));
                } else {
                    sobot_add_content.setHint(satisfactionSetBase.getInputLanguage().replace("<br/>", "\n"));
                }
            } else {
                sobot_add_content.setHint(String.format(ChatUtils.getResString(context, "sobot_edittext_hint")));
            }

            if (!TextUtils.isEmpty(satisfactionSetBase.getLabelName())) {
                String tmpData[] = convertStrToArray(satisfactionSetBase.getLabelName());
                setLableViewVisible(tmpData);
            } else {
                setLableViewVisible(null);
            }

            //根据infomation 配置是否隐藏星星评价描述
            if (!information.isHideManualEvaluationLabels()) {
                sobot_ratingBar_title.setVisibility(View.VISIBLE);
            } else {
                sobot_ratingBar_title.setVisibility(View.GONE);
            }
            if (score == 5) {
//                sobot_hide_layout.setVisibility(View.GONE);
                setl_submit_content.setVisibility(View.VISIBLE);
                sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
            } else {
                setl_submit_content.setVisibility(View.VISIBLE);
            }
        } else {
            //根据infomation 配置是否隐藏星星评价描述
            if (!information.isHideManualEvaluationLabels()) {
                sobot_ratingBar_title.setVisibility(View.VISIBLE);
            } else {
                sobot_ratingBar_title.setVisibility(View.GONE);
            }
        }
    }

    private SatisfactionSetBase getSatisFaction(int score, List<
            SatisfactionSetBase> satisFactionList) {
        if (satisFactionList == null) {
            return null;
        }
        for (int i = 0; i < satisFactionList.size(); i++) {
            if (satisFactionList.get(i).getScore().equals(score + "")) {
                return satisFactionList.get(i);
            }
        }
        return null;
    }

    //设置评价标签的显示逻辑
    private void setLableViewVisible(String tmpData[]) {
        if (tmpData == null) {
            sobot_hide_layout.setVisibility(View.GONE);
            return;
        } else {
            if (current_model == ZhiChiConstant.client_model_robot && initModel != null) {
                //根据infomation 配置是否隐藏机器人评价标签
                if (!information.isHideRototEvaluationLabels()) {
                    sobot_hide_layout.setVisibility(View.VISIBLE);
                } else {
                    sobot_hide_layout.setVisibility(View.GONE);
                }
            }
            if (current_model == ZhiChiConstant.client_model_customService && initModel != null) {
                //根据infomation 配置是否隐藏人工评价标签
                if (!information.isHideManualEvaluationLabels()) {
                    sobot_hide_layout.setVisibility(View.VISIBLE);
                } else {
                    sobot_hide_layout.setVisibility(View.GONE);
                }
            }
            if (current_model == ZhiChiConstant.client_model_customService) {
                if (satisfactionSetBase != null) {
                    if (TextUtils.isEmpty(satisfactionSetBase.getTagTips())) {
                        sobot_text_other_problem.setVisibility(View.GONE);
                    } else {
                        sobot_text_other_problem.setVisibility(View.VISIBLE);
                        if (satisfactionSetBase.getIsTagMust()) {
                            sobot_text_other_problem.setText(satisfactionSetBase.getTagTips());
                        } else {
                            sobot_text_other_problem.setText(satisfactionSetBase.getTagTips());
                        }
                    }
                }
            }
        }

        switch (tmpData.length) {
            case 1:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setVisibility(View.INVISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.GONE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
                checkLable(tmpData, 0, sobot_evaluate_cb_lable1);
                break;
            case 2:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.GONE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
                checkLable(tmpData, 0, sobot_evaluate_cb_lable1);
                checkLable(tmpData, 1, sobot_evaluate_cb_lable2);
                break;
            case 3:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable3.setText(tmpData[2]);
                sobot_evaluate_cb_lable3.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable4.setVisibility(View.INVISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
                checkLable(tmpData, 0, sobot_evaluate_cb_lable1);
                checkLable(tmpData, 1, sobot_evaluate_cb_lable2);
                checkLable(tmpData, 2, sobot_evaluate_cb_lable3);
                break;
            case 4:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable3.setText(tmpData[2]);
                sobot_evaluate_cb_lable3.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable4.setText(tmpData[3]);
                sobot_evaluate_cb_lable4.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
                checkLable(tmpData, 0, sobot_evaluate_cb_lable1);
                checkLable(tmpData, 1, sobot_evaluate_cb_lable2);
                checkLable(tmpData, 2, sobot_evaluate_cb_lable3);
                checkLable(tmpData, 3, sobot_evaluate_cb_lable4);
                break;
            case 5:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable3.setText(tmpData[2]);
                sobot_evaluate_cb_lable3.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable4.setText(tmpData[3]);
                sobot_evaluate_cb_lable4.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable5.setText(tmpData[4]);
                sobot_evaluate_cb_lable5.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable6.setVisibility(View.INVISIBLE);
                sobot_evaluate_ll_lable3.setVisibility(View.VISIBLE);
                checkLable(tmpData, 0, sobot_evaluate_cb_lable1);
                checkLable(tmpData, 1, sobot_evaluate_cb_lable2);
                checkLable(tmpData, 2, sobot_evaluate_cb_lable3);
                checkLable(tmpData, 3, sobot_evaluate_cb_lable4);
                checkLable(tmpData, 4, sobot_evaluate_cb_lable5);
                break;
            case 6:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable3.setText(tmpData[2]);
                sobot_evaluate_cb_lable3.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable4.setText(tmpData[3]);
                sobot_evaluate_cb_lable4.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable5.setText(tmpData[4]);
                sobot_evaluate_cb_lable5.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable6.setText(tmpData[5]);
                sobot_evaluate_cb_lable6.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable3.setVisibility(View.VISIBLE);
                checkLable(tmpData, 0, sobot_evaluate_cb_lable1);
                checkLable(tmpData, 1, sobot_evaluate_cb_lable2);
                checkLable(tmpData, 2, sobot_evaluate_cb_lable3);
                checkLable(tmpData, 3, sobot_evaluate_cb_lable4);
                checkLable(tmpData, 4, sobot_evaluate_cb_lable5);
                checkLable(tmpData, 5, sobot_evaluate_cb_lable6);
                break;
            default:
                break;
        }
    }

    private int getResovled() {
        if (current_model == ZhiChiConstant.client_model_robot) {
            if (sobot_btn_ok_robot.isChecked()) {
                return 0;
            } else {
                return 1;
            }
        } else if (current_model == ZhiChiConstant.client_model_customService) {
            if (satisfactionSetBase != null && satisfactionSetBase.getIsQuestionFlag()) {
                if (sobot_btn_ok_robot.isChecked()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        }
        return -1;
    }

    private SobotCommentParam getCommentParam() {
        SobotCommentParam param = new SobotCommentParam();
        String type = current_model == ZhiChiConstant.client_model_robot ? "0" : "1";
        int score;
        if (ratingType == 0) {
            param.setScoreFlag(0);//5星
            score = (int) Math.ceil(sobot_ratingBar.getRating());
        } else {
            param.setScoreFlag(1);//10分
            score = sobot_ten_rating_ll.getSelectContent();
        }

        String problem = checkBoxIsChecked();
        String suggest = sobot_add_content.getText().toString();
        param.setType(type);
        param.setProblem(problem);
        param.setSuggest(suggest);
        param.setIsresolve(getResovled());
        param.setCommentType(commentType);
        if (current_model == ZhiChiConstant.client_model_robot) {
            param.setRobotFlag(initModel.getRobotid());
        } else {
            param.setScore(score + "");
        }
        return param;
    }

    //提交评价
    private void subMitEvaluate() {
        if (!checkInput()) {
            return;
        }

        comment();
    }

    /**
     * 检查是否能提交评价
     *
     * @return
     */
    private boolean checkInput() {
        if (current_model == ZhiChiConstant.client_model_customService) {
            if (satisfactionSetBase != null) {
                SobotCommentParam commentParam = getCommentParam();
                if (!TextUtils.isEmpty(satisfactionSetBase.getLabelName()) && satisfactionSetBase.getIsTagMust()) {
                    if (TextUtils.isEmpty(commentParam.getProblem())) {
                        ToastUtil.showToast(context, getResString("sobot_the_label_is_required"));//标签必选
                        return false;
                    }
                }

                if (satisfactionSetBase.getIsInputMust()) {
                    if (TextUtils.isEmpty(commentParam.getSuggest().trim())) {
                        ToastUtil.showToast(context, getResString("sobot_suggestions_are_required"));//建议必填
                        return false;
                    }
                }
            }
        } else if (current_model == ZhiChiConstant.client_model_robot) {
            return true;
        }

        return true;
    }

    // 使用String的split 方法把字符串截取为字符串数组
    private static String[] convertStrToArray(String str) {
        String[] strArray = null;
        if (!TextUtils.isEmpty(str)) {
            strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        }
        return strArray;
    }

    //提交评价调用接口
    private void comment() {

        ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
        final SobotCommentParam commentParam = getCommentParam();
        zhiChiApi.comment(CANCEL_TAG, initModel.getCid(), initModel.getPartnerid(), commentParam,
                new StringResultCallBack<CommonModel>() {
                    @Override
                    public void onSuccess(CommonModel result) {
                        //评论成功 发送广播
                        Intent intent = new Intent();
                        intent.setAction(ZhiChiConstants.dcrc_comment_state);
                        intent.putExtra("commentState", true);
                        intent.putExtra("isFinish", isFinish);
                        intent.putExtra("isExitSession", isExitSession);
                        intent.putExtra("commentType", commentType);
                        if (!TextUtils.isEmpty(commentParam.getScore())) {
                            intent.putExtra("score", Integer.parseInt(commentParam.getScore()));
                        }
                        intent.putExtra("isResolved", commentParam.getIsresolve());

                        CommonUtils.sendLocalBroadcast(context, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception arg0, String msg) {
                        try {
                            ToastUtil.showToast(getContext(), msg);
                        } catch (Exception e) {
//                            e.printStackTrace();
                        }
                    }
                });
    }

    //检查标签是否选中（根据主动邀评传过来的选中标签判断）
    private void checkLable(String tmpData[], int pos, CheckBox cb) {
        if (tmpData != null && tmpData.length > 0 && !TextUtils.isEmpty(evaluateChecklables) && cb != null) {
            if (evaluateChecklables.contains(tmpData[pos])) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
        }
    }

    //检测选中的标签
    private String checkBoxIsChecked() {
        String str = "";
        for (int i = 0; i < checkBoxList.size(); i++) {
            if (checkBoxList.get(i).isChecked()) {
                str = str + checkBoxList.get(i).getText() + ",";
            }
        }
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str + "";
    }

    @Override
    public void onDetachedFromWindow() {
        OkHttpUtils.getInstance().cancelTag(CANCEL_TAG);
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {/*点击外部隐藏键盘*/
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /*是否在外部*/
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public Activity getContext() {
        return SobotEvaluateActivity.this;
    }
}