package com.sobot.chat.widget.dialog;

import android.app.Activity;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotOrderScoreModel;
import com.sobot.chat.api.model.SobotUserTicketEvaluate;
import com.sobot.chat.notchlib.utils.ScreenUtil;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.widget.SobotAntoLineLayout;
import com.sobot.chat.widget.SobotEditTextLayout;
import com.sobot.chat.widget.SobotTenRatingLayout;
import com.sobot.chat.widget.dialog.base.SobotActionSheet;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价界面的显示
 * Created by jinxl on 2017/6/12.
 */
public class SobotTicketEvaluateDialog extends SobotActionSheet {
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

    private SobotAntoLineLayout sobot_evaluate_lable_autoline;//评价 标签 自动换行 最多可以有六个
    private SobotEditTextLayout setl_submit_content;//评价框
    private int score;

    private int themeColor;
    private boolean changeThemeColor;

    private List<CheckBox> checkBoxList = new ArrayList<>();
    private SobotUserTicketEvaluate mEvaluate;
    private SobotOrderScoreModel satisfactionSetBase;
    private Activity mContext;

    public SobotTicketEvaluateDialog(Activity context) {
        super(context);
        this.mContext = context;
    }

    public SobotTicketEvaluateDialog(Activity context, SobotUserTicketEvaluate evaluate) {
        super(context);
        mEvaluate = evaluate;
        this.mContext = context;
    }

    @Override
    protected String getLayoutStrName() {
        return "sobot_layout_evaluate";
    }

    @Override
    protected View getDialogContainer() {
        if (coustom_pop_layout == null) {
            coustom_pop_layout = (LinearLayout) findViewById(getResId("sobot_evaluate_container"));
        }
        return coustom_pop_layout;
    }

    @Override
    protected void initView() {
        sobot_close_now = (Button) findViewById(R.id.sobot_close_now);
        sobot_close_now.setText(R.string.sobot_btn_submit_text);
        sobot_readiogroup = (RadioGroup) findViewById(R.id.sobot_readiogroup);
        sobot_tv_evaluate_title = (TextView) findViewById(R.id.sobot_tv_evaluate_title);
        //统一显示为服务评价
        sobot_tv_evaluate_title.setText(R.string.sobot_please_evaluate_this_service);
        sobot_robot_center_title = (TextView) findViewById(R.id.sobot_robot_center_title);
        sobot_robot_center_title.setText(R.string.sobot_question);
        sobot_text_other_problem = (TextView) findViewById(R.id.sobot_text_other_problem);
        sobot_custom_center_title = (TextView) findViewById(R.id.sobot_custom_center_title);
        sobot_custom_center_title.setText(R.string.sobot_please_evaluate);
        sobot_ratingBar_title = (TextView) findViewById(R.id.sobot_ratingBar_title);
        sobot_ratingBar_title.setText(R.string.sobot_great_satisfaction);
        sobot_tv_evaluate_title_hint = (TextView) findViewById(R.id.sobot_tv_evaluate_title_hint);
        sobot_evaluate_cancel = (TextView) findViewById(R.id.sobot_evaluate_cancel);
        sobot_evaluate_cancel.setText(R.string.sobot_temporarily_not_evaluation);
        sobot_ratingBar_split_view = findViewById(R.id.sobot_ratingBar_split_view);
        sobot_negativeButton = (LinearLayout) findViewById(R.id.sobot_negativeButton);
        sobot_negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        sobot_evaluate_cancel.setVisibility(View.GONE);

        sobot_ratingBar = (RatingBar) findViewById(R.id.sobot_ratingBar);
        sobot_ten_root_ll = findViewById(R.id.sobot_ten_root_ll);
        sobot_ten_rating_ll = findViewById(R.id.sobot_ten_rating_ll);
        sobot_ten_very_dissatisfied = findViewById(R.id.sobot_ten_very_dissatisfied);
        sobot_ten_very_satisfaction = findViewById(R.id.sobot_ten_very_satisfaction);
        sobot_ten_very_dissatisfied.setText(R.string.sobot_very_dissatisfied);
        sobot_ten_very_satisfaction.setText(R.string.sobot_great_satisfaction);

        sobot_evaluate_lable_autoline = findViewById(R.id.sobot_evaluate_lable_autoline);
        sobot_add_content = (EditText) findViewById(R.id.sobot_add_content);
        sobot_btn_ok_robot = (RadioButton) findViewById(R.id.sobot_btn_ok_robot);
        sobot_btn_ok_robot.setText(R.string.sobot_evaluate_yes);
        sobot_btn_no_robot = (RadioButton) findViewById(R.id.sobot_btn_no_robot);
        sobot_btn_no_robot.setText(R.string.sobot_evaluate_no);
        sobot_robot_relative = (LinearLayout) findViewById(R.id.sobot_robot_relative);
        sobot_custom_relative = (LinearLayout) findViewById(R.id.sobot_custom_relative);
        sobot_hide_layout = (LinearLayout) findViewById(R.id.sobot_hide_layout);
        setl_submit_content = (SobotEditTextLayout) findViewById(R.id.setl_submit_content);
        if (mEvaluate != null) {
            if (mEvaluate.isOpen() && mEvaluate.getTxtFlag() == 1) {
                sobot_add_content.setVisibility(View.VISIBLE);
            } else {
                sobot_add_content.setVisibility(View.GONE);
            }
            //主动评价需要判断默认星级
            if (mEvaluate.getScoreFlag() == 0) {
                //defaultType 0-默认5星,1-默认0星
                score = (mEvaluate.getDefaultType() == 0) ? 5 : 0;
                sobot_ten_root_ll.setVisibility(View.GONE);
                sobot_ratingBar.setVisibility(View.VISIBLE);
                ratingType = 0;//5星
            } else {
                sobot_ten_root_ll.setVisibility(View.VISIBLE);
                sobot_ratingBar.setVisibility(View.GONE);
                ratingType = 1;//十分
                //0-10分，1-5分，2-0分，3-不选中
                if (mEvaluate.getDefaultType() == 2) {
                    score = 0;
                } else if (mEvaluate.getDefaultType() == 1) {
                    score = 5;
                } else if (mEvaluate.getDefaultType() == 3) {
                    score = -1;
                } else {
                    score = 10;
                }
            }
            if (ratingType == 0) {
                if (score == -1) {
                    score = 5;
                }
                sobot_ratingBar.setRating(score);
            } else {
                sobot_ten_rating_ll.init(score, true, 41);
            }

            if (mEvaluate.getDefaultQuestionFlag() == 1) {
                //(1)-解决
                sobot_btn_ok_robot.setChecked(true);
                sobot_btn_no_robot.setChecked(false);
            } else if (mEvaluate.getDefaultQuestionFlag() == 0) {
                //(0)-未解决
                sobot_btn_ok_robot.setChecked(false);
                sobot_btn_no_robot.setChecked(true);
            } else {
                sobot_btn_ok_robot.setChecked(false);
                sobot_btn_no_robot.setChecked(false);

            }
            setCustomLayoutViewVisible(score, mEvaluate.getScoreInfo());
            if (ratingType == 0) {
                if (0 == score) {
                    sobot_ratingBar_title.setText(R.string.sobot_evaluate_zero_score_des);
                    sobot_ratingBar_title.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_gray3));
                } else {
                    if (satisfactionSetBase != null) {
                        sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
                    }
                    sobot_ratingBar_title.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_color_evaluate_ratingBar_des_tv));
                }
            } else {
                if (-1 == score) {
                    sobot_ratingBar_title.setText(R.string.sobot_evaluate_zero_score_des);
                    sobot_ratingBar_title.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_common_gray3));
                } else {
                    if (satisfactionSetBase != null) {
                        sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
                    }
                    sobot_ratingBar_title.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_color_evaluate_ratingBar_des_tv));
                }
            }
            //1-开启 0-关闭
            if (mEvaluate.getIsQuestionFlag() == 1) {
                sobot_robot_relative.setVisibility(View.VISIBLE);
                sobot_ratingBar_split_view.setVisibility(View.VISIBLE);
            } else {
                sobot_robot_relative.setVisibility(View.GONE);
                sobot_ratingBar_split_view.setVisibility(View.GONE);
            }
            //是否是默认评价提示语
            if (mEvaluate.getIsDefaultGuide() == 0 && !TextUtils.isEmpty(mEvaluate.getGuideCopyWriting())) {
                sobot_tv_evaluate_title.setText(mEvaluate.getGuideCopyWriting());
            }
            //是否显示评价输入框
            if (mEvaluate.getTxtFlag() == 0) {
                //关闭评价输入框
                setl_submit_content.setVisibility(View.GONE);
            } else {
                setl_submit_content.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mEvaluate.getTempDescribe())) {
                    sobot_add_content.setHint(mEvaluate.getTempDescribe());
                } else {
                    sobot_add_content.setHint(R.string.sobot_edittext_hint);
                }
            }
            //是否是默认提交按钮
            if (mEvaluate.getIsDefaultButton() == 0 && !TextUtils.isEmpty(mEvaluate.getButtonDesc())) {
                sobot_close_now.setText(mEvaluate.getButtonDesc());
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
                if (score == 0) {
                    sobot_ratingBar.setRating(1);
                }
                if (score > 0 && score <= 5) {
                    changeCommitButtonUi(true);
                    setCustomLayoutViewVisible(score, mEvaluate.getScoreInfo());
                }
                sobot_close_now.setVisibility(View.VISIBLE);
                changeCommitButtonUi(true);
            }
        });
        sobot_ratingBar.setRating(5);
        sobot_close_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交评价
                if (mContext instanceof SobotTicketEvaluateCallback) {
                    if (!checkInput()) {
                        return;
                    }
                    SobotTicketEvaluateCallback callback = (SobotTicketEvaluateCallback) mContext;
                    int score = (int) Math.ceil(sobot_ratingBar.getRating());
                    String labelTag = checkBoxIsChecked();
                    int defaultQuestionFlag=2;
                    if (sobot_btn_ok_robot.isChecked()) {
                        defaultQuestionFlag = 1;
                    } else if (sobot_btn_no_robot.isChecked()) {
                        defaultQuestionFlag = 0;
                    }
                    KeyboardUtil.hideKeyboard(sobot_add_content);
                    callback.submitEvaluate(score, sobot_add_content.getText().toString(), labelTag, defaultQuestionFlag);
                    dismiss();
                }
            }
        });
//监听10分评价选择变化
        if (sobot_ten_rating_ll != null) {
            sobot_ten_rating_ll.setOnClickItemListener(new SobotTenRatingLayout.OnClickItemListener() {
                @Override
                public void onClickItem(int selectIndex) {
                    sobot_close_now.setVisibility(View.VISIBLE);
                    changeCommitButtonUi(true);
                    setCustomLayoutViewVisible(selectIndex, mEvaluate.getScoreInfo());
                }
            });
        }

    }
    private boolean checkInput() {
        //如果开启了是否解决问题
        if (mEvaluate != null && mEvaluate.getIsQuestionFlag() == 1 && mEvaluate.getIsQuestionMust()==1) {
            //“问题是否解决”是否为必填选项： 0-非必填 1-必填
            if (!sobot_btn_ok_robot.isChecked() && !sobot_btn_no_robot.isChecked()) {
                ToastUtil.showToast(mContext, getResString("sobot_str_please_check_is_solve"));//标签必选
                return false;
            }
        }
        if (satisfactionSetBase != null) {
            if (null!=satisfactionSetBase.getTags() && satisfactionSetBase.getTags().size()>0 && satisfactionSetBase.getIsTagMust()) {
                if (TextUtils.isEmpty(checkBoxIsChecked())) {
                    ToastUtil.showToast(mContext, getResString("sobot_the_label_is_required"));//标签必选
                    return false;
                }
            }

            if (mEvaluate.getTxtFlag()==1 && satisfactionSetBase.getIsInputMust()==1) {
                String suggest = sobot_add_content.getText().toString();
                if (TextUtils.isEmpty(suggest.trim())) {
                    ToastUtil.showToast(mContext, getResString("sobot_suggestions_are_required"));//建议必填
                    return false;
                }
            }
        }
        return true;
    }
    private void changeCommitButtonUi(boolean isCanClick) {
        if (isCanClick) {
            sobot_close_now.setFocusable(true);
            sobot_close_now.setClickable(true);
            sobot_close_now.getBackground().setAlpha(255);
        } else {
            sobot_close_now.setFocusable(false);
            sobot_close_now.setClickable(false);
            sobot_close_now.getBackground().setAlpha(90);
        }
    }

    //设置人工客服评价的布局显示逻辑
    private void setCustomLayoutViewVisible(int score, List<SobotOrderScoreModel> satisFactionList) {
        satisfactionSetBase = getSatisFaction(score, satisFactionList);
        for (int i = 0; i < checkBoxList.size(); i++) {
            checkBoxList.get(i).setChecked(false);
        }
        if (satisfactionSetBase != null) {
            sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
            sobot_ratingBar_title.setTextColor(ContextCompat.getColor(getContext(), R.color.sobot_color_evaluate_ratingBar_des_tv));

            if (null != satisfactionSetBase.getTags() && satisfactionSetBase.getTags().size() > 0) {
                String tmpData[] = satisfactionSetBase.getTagNames();
                setLableViewVisible(tmpData);
            } else {
                setLableViewVisible(null);
            }
            if (score == 5) {
                sobot_ratingBar_title.setText(satisfactionSetBase.getScoreExplain());
            }
        }
    }

    private SobotOrderScoreModel getSatisFaction(int score, List<SobotOrderScoreModel> satisFactionList) {
        if (satisFactionList == null) {
            return null;
        }
        for (int i = 0; i < satisFactionList.size(); i++) {
            if (satisFactionList.get(i).getScore() == score) {
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
        createChildLableView(sobot_evaluate_lable_autoline, tmpData);
        checkLable(tmpData);
    }

    //检查标签是否选中（根据主动邀评传过来的选中标签判断）
    private void checkLable(String tmpData[]) {
        if (tmpData != null && tmpData.length > 0 && sobot_evaluate_lable_autoline != null) {
            for (int i = 0; i < tmpData.length; i++) {
                CheckBox checkBox = (CheckBox) sobot_evaluate_lable_autoline.getChildAt(i);
                if (checkBox != null) {
                    if (mEvaluate!=null && !TextUtils.isEmpty(mEvaluate.getTag()) &&mEvaluate.getTag().contains(tmpData[i])) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }
                }
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

    private void createChildLableView(SobotAntoLineLayout antoLineLayout, String tmpData[]) {
        if (antoLineLayout != null) {
            antoLineLayout.removeAllViews();
            for (int i = 0; i < tmpData.length; i++) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.sobot_layout_evaluate_item, null);
                CheckBox checkBox = view.findViewById(R.id.sobot_evaluate_cb_lable);
                //50 =antoLineLayout 左间距20+右间距20 +antoLineLayout 子控件行间距10
                checkBox.setMinWidth((ScreenUtil.getScreenSize(mContext)[0] - ScreenUtils.dip2px(getContext(), 50)) / 2);
                checkBox.setText(tmpData[i]);
                if (changeThemeColor) {
                    checkBox.setTextColor(themeColor);
                }
                antoLineLayout.addView(view);
                checkBoxList.add(checkBox);
            }
        }
    }

    @Override
    public void dismiss() {
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public interface SobotTicketEvaluateCallback {
        void submitEvaluate(int score, String remark, String labletag, int defultSore);
    }
}