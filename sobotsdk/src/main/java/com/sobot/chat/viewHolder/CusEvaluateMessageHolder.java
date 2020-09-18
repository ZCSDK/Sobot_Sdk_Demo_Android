package com.sobot.chat.viewHolder;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.SatisfactionSet;
import com.sobot.chat.api.model.SatisfactionSetBase;
import com.sobot.chat.api.model.SobotEvaluateModel;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 客服主动邀请客户评价
 * Created by jinxl on 2017/3/17.
 */
public class CusEvaluateMessageHolder extends MessageHolderBase implements RadioGroup.OnCheckedChangeListener, RatingBar.OnRatingBarChangeListener {
    TextView sobot_center_title;
    RadioGroup sobot_readiogroup;
    RadioButton sobot_btn_ok_robot;
    RadioButton sobot_btn_no_robot;
    TextView sobot_tv_star_title;
    RatingBar sobot_ratingBar;
    TextView sobot_ratingBar_title;//星星对应描述
    TextView sobot_submit;//提交
    View sobot_ratingBar_split_view;//如果有已解决按钮和未解决按钮就显示，否则隐藏；
    private LinearLayout sobot_evaluate_ll_lable1;//评价  用来放前两个标签，标签最多可以有六个
    private LinearLayout sobot_evaluate_ll_lable2;//评价  用来放中间两个标签
    private LinearLayout sobot_evaluate_ll_lable3;//评价  用来放最后两个标签
    private CheckBox sobot_evaluate_cb_lable1;//六个评价标签
    private CheckBox sobot_evaluate_cb_lable2;
    private CheckBox sobot_evaluate_cb_lable3;
    private CheckBox sobot_evaluate_cb_lable4;
    private CheckBox sobot_evaluate_cb_lable5;
    private CheckBox sobot_evaluate_cb_lable6;
    private LinearLayout sobot_hide_layout;
    private List<CheckBox> checkBoxList = new ArrayList<>();
    SobotEvaluateModel sobotEvaluateModel;
    public ZhiChiMessageBase message;


    private List<SatisfactionSetBase> satisFactionList;
    private int deftaultScore = 5;

    public CusEvaluateMessageHolder(Context context, View convertView) {
        super(context, convertView);
        satisFactionList = new ArrayList<>();
        sobot_center_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_center_title"));
        sobot_readiogroup = (RadioGroup) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_readiogroup"));
        sobot_btn_ok_robot = (RadioButton) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_btn_ok_robot"));
        sobot_btn_ok_robot.setText(ResourceUtils.getResString(context,"sobot_evaluate_yes"));
        sobot_btn_no_robot = (RadioButton) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_btn_no_robot"));
        sobot_btn_no_robot.setText(ResourceUtils.getResString(context,"sobot_evaluate_no"));
        sobot_tv_star_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_tv_star_title"));
        sobot_tv_star_title.setText(ResourceUtils.getResString(context,"sobot_please_evaluate"));
        sobot_ratingBar = (RatingBar) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_ratingBar"));
        sobot_submit = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_submit"));
        sobot_submit.setText(ResourceUtils.getResString(context,"sobot_submit"));
        sobot_ratingBar_split_view = convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_ratingBar_split_view"));
        sobot_btn_ok_robot.setSelected(true);
        sobot_ratingBar_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_ratingBar_title"));
        sobot_ratingBar_title.setText(ResourceUtils.getResString(context,"sobot_great_satisfaction"));
        sobot_hide_layout = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_hide_layout"));
        sobot_evaluate_ll_lable1 = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_evaluate_ll_lable1"));
        sobot_evaluate_ll_lable2 = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_evaluate_ll_lable2"));
        sobot_evaluate_ll_lable3 = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_evaluate_ll_lable3"));
        sobot_evaluate_cb_lable1 = (CheckBox) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_evaluate_cb_lable1"));
        sobot_evaluate_cb_lable2 = (CheckBox) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_evaluate_cb_lable2"));
        sobot_evaluate_cb_lable3 = (CheckBox) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_evaluate_cb_lable3"));
        sobot_evaluate_cb_lable4 = (CheckBox) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_evaluate_cb_lable4"));
        sobot_evaluate_cb_lable5 = (CheckBox) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_evaluate_cb_lable5"));
        sobot_evaluate_cb_lable6 = (CheckBox) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_evaluate_cb_lable6"));
        checkBoxList.add(sobot_evaluate_cb_lable1);
        checkBoxList.add(sobot_evaluate_cb_lable2);
        checkBoxList.add(sobot_evaluate_cb_lable3);
        checkBoxList.add(sobot_evaluate_cb_lable4);
        checkBoxList.add(sobot_evaluate_cb_lable5);
        checkBoxList.add(sobot_evaluate_cb_lable6);
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        this.message = message;
        this.sobotEvaluateModel = message.getSobotEvaluateModel();
        if (satisFactionList == null || satisFactionList.size() == 0) {
            //2.8.5 获取人工满意度配置信息，默认几星和5星时展示对应标签
            ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(context).getZhiChiApi();
            ZhiChiInitModeBase initMode = (ZhiChiInitModeBase) SharedPreferencesUtil.getObject(context,
                    ZhiChiConstant.sobot_last_current_initModel);
            zhiChiApi.satisfactionMessage(CusEvaluateMessageHolder.this, initMode.getPartnerid(), new ResultCallBack<SatisfactionSet>() {
                @Override
                public void onSuccess(SatisfactionSet satisfactionSet) {
                    if (satisfactionSet != null && "1".equals(satisfactionSet.getCode()) && satisfactionSet.getData() != null && satisfactionSet.getData().size() != 0) {
                        satisFactionList = satisfactionSet.getData();
                        int score = 5;
                        if (satisFactionList.get(0) != null && satisFactionList.get(0).getDefaultType() != -1) {
                            score = (satisFactionList.get(0).getDefaultType() == 0) ? 5 : 0;
                            deftaultScore = score;
                        }

                        sobotEvaluateModel.setScore(deftaultScore);
                        sobot_ratingBar.setRating(deftaultScore);

                        if (0 == score) {
                            sobot_hide_layout.setVisibility(View.GONE);
                            sobot_submit.setVisibility(View.GONE);
                            sobot_ratingBar_title.setText(ResourceUtils.getResString(context, "sobot_evaluate_zero_score_des"));
                            sobot_ratingBar_title.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray3")));
                        } else {
                            sobot_hide_layout.setVisibility(View.VISIBLE);
                            sobot_submit.setVisibility(View.VISIBLE);
                            sobot_ratingBar_title.setText(satisFactionList.get(4).getScoreExplain());
                            sobot_ratingBar_title.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_color_evaluate_ratingBar_des_tv")));
                        }


                        SatisfactionSetBase satisfactionSetBase = getSatisFaction(score, satisFactionList);
                        if (satisfactionSetBase != null && !TextUtils.isEmpty(satisfactionSetBase.getLabelName())) {
                            String tmpData[] = convertStrToArray(satisfactionSetBase.getLabelName());
                            setLableViewVisible(tmpData);
                        } else {
                            setLableViewVisible(null);
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


        sobot_center_title.setText(String.format(ChatUtils.getResString(context, "sobot_question"), message.getSenderName()));
        sobot_tv_star_title.setText(String.format(ChatUtils.getResString(context, "sobot_please_evaluate"), message.getSenderName()));

        checkQuestionFlag();
        refreshItem();

        sobot_readiogroup.setOnCheckedChangeListener(this);
        sobot_ratingBar.setOnRatingBarChangeListener(this);
        sobot_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //校验评5星评价标签是否必选
                if (TextUtils.isEmpty(checkBoxIsChecked()) && satisFactionList != null && satisFactionList.size() == 5
                        && satisFactionList.get(4).getIsTagMust()
                        && !TextUtils.isEmpty(satisFactionList.get(4).getLabelName())) {
                    ToastUtil.showToast(mContext, ResourceUtils.getResString(mContext, "sobot_the_label_is_required"));//标签必选
                    return;
                }
                // true 直接提交  false 打开评价窗口 显示提交 肯定是5星
                doEvaluate(true, 5);
            }
        });
    }

    /**
     * 检查是否开启   是否已解决配置
     */
    private void checkQuestionFlag() {
        if (sobotEvaluateModel == null) {
            return;
        }
        if (ChatUtils.isQuestionFlag(sobotEvaluateModel)) {
            //是否已解决开启
            sobot_center_title.setVisibility(View.VISIBLE);
            sobot_readiogroup.setVisibility(View.VISIBLE);
            sobot_ratingBar_split_view.setVisibility(View.VISIBLE);

        } else {
            //是否已解决关闭
            sobot_center_title.setVisibility(View.GONE);
            sobot_readiogroup.setVisibility(View.GONE);
            sobot_ratingBar_split_view.setVisibility(View.GONE);
        }
    }


    /**
     * 根据是否已经评价设置UI
     */
    public void refreshItem() {
        if (sobotEvaluateModel == null) {
            return;
        }
        if (0 == sobotEvaluateModel.getEvaluateStatus()) {

            //未评价
            setNotEvaluatedLayout();
            sobot_submit.setVisibility(View.VISIBLE);
        } else if (1 == sobotEvaluateModel.getEvaluateStatus()) {
            //已评价
            setEvaluatedLayout();
            sobot_submit.setVisibility(View.GONE);
        }
    }

    private void setEvaluatedLayout() {
        if (sobot_readiogroup.getVisibility() == View.VISIBLE) {
            if (sobotEvaluateModel.getIsResolved() == -1) {
                sobot_btn_ok_robot.setChecked(false);
                sobot_btn_no_robot.setChecked(false);
                sobot_btn_ok_robot.setVisibility(View.VISIBLE);
                sobot_btn_no_robot.setVisibility(View.VISIBLE);
            } else if (sobotEvaluateModel.getIsResolved() == 0) {
                sobot_btn_ok_robot.setChecked(true);
                sobot_btn_no_robot.setChecked(false);
                sobot_btn_ok_robot.setVisibility(View.VISIBLE);
                sobot_btn_no_robot.setVisibility(View.GONE);
            } else {
                sobot_btn_ok_robot.setChecked(false);
                sobot_btn_no_robot.setChecked(true);
                sobot_btn_ok_robot.setVisibility(View.GONE);
                sobot_btn_no_robot.setVisibility(View.VISIBLE);
            }
        }
//        sobot_ratingBar.setRating(sobotEvaluateModel.getScore());
        sobot_ratingBar.setEnabled(false);
    }

    private void setNotEvaluatedLayout() {
        if (sobotEvaluateModel == null) {
            return;
        }
        if (sobot_readiogroup.getVisibility() == View.VISIBLE) {
            if (sobotEvaluateModel.getIsResolved() == -1) {
                sobot_btn_ok_robot.setChecked(false);
                sobot_btn_no_robot.setChecked(false);
                sobot_btn_ok_robot.setVisibility(View.VISIBLE);
                sobot_btn_no_robot.setVisibility(View.VISIBLE);
            } else if (sobotEvaluateModel.getIsResolved() == 0) {
                sobot_btn_ok_robot.setChecked(true);
                sobot_btn_no_robot.setChecked(false);
                sobot_btn_ok_robot.setVisibility(View.VISIBLE);
                sobot_btn_no_robot.setVisibility(View.VISIBLE);
            } else {
                sobot_btn_ok_robot.setChecked(false);
                sobot_btn_no_robot.setChecked(true);
                sobot_btn_ok_robot.setVisibility(View.VISIBLE);
                sobot_btn_no_robot.setVisibility(View.VISIBLE);
            }
        }

        sobot_ratingBar.setEnabled(true);
//        sobot_ratingBar.setRating(sobotEvaluateModel.getScore());
    }

    /**
     * 评价 操作
     *
     * @param evaluateFlag true 直接提交  false 打开评价窗口
     */
    private void doEvaluate(boolean evaluateFlag, int score) {
        if (mContext != null && message != null && message.getSobotEvaluateModel() != null) {
            int resolved = -1;
            if (ChatUtils.isQuestionFlag(message.getSobotEvaluateModel())) {
                if (sobot_btn_ok_robot.isChecked()) {
                    resolved = 0;
                } else if (sobot_btn_no_robot.isChecked()) {
                    resolved = 1;
                } else if (message.getSobotEvaluateModel().getScore() == 5) {
                    resolved = 0;
                }
            }
            message.getSobotEvaluateModel().setIsResolved(resolved);
            message.getSobotEvaluateModel().setScore(score);
            message.getSobotEvaluateModel().setProblem(checkBoxIsChecked());
            if (msgCallBack != null) {
                msgCallBack.doEvaluate(evaluateFlag, message);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (sobotEvaluateModel == null) {
            return;
        }
        if (checkedId == sobot_btn_ok_robot.getId()) {
            sobotEvaluateModel.setIsResolved(0);
            sobot_btn_ok_robot.setChecked(true);
            sobot_btn_no_robot.setChecked(false);
            sobot_btn_ok_robot.setSelected(true);
            sobot_btn_no_robot.setSelected(false);
        }
        if (checkedId == sobot_btn_no_robot.getId()) {
            sobotEvaluateModel.setIsResolved(1);
            sobot_btn_ok_robot.setChecked(false);
            sobot_btn_no_robot.setChecked(true);
            sobot_btn_ok_robot.setSelected(false);
            sobot_btn_no_robot.setSelected(true);
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        LogUtils.i(sobotEvaluateModel.getScore() + "-----" + deftaultScore + "=====" + rating);
        if (sobotEvaluateModel != null && 0 == sobotEvaluateModel.getEvaluateStatus() && rating > 0 && deftaultScore != (int) Math.ceil(rating)) {
            //未评价时进行评价
            int score = (int) Math.ceil(rating);
            sobotEvaluateModel.setScore(score);
            sobot_ratingBar.setOnRatingBarChangeListener(null);
            sobot_ratingBar.setRating(deftaultScore);
            sobot_ratingBar.setOnRatingBarChangeListener(this);
            doEvaluate(false, score);
        }
    }


    private SatisfactionSetBase getSatisFaction(int score, List<SatisfactionSetBase> satisFactionList) {
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

    // 使用String的split 方法把字符串截取为字符串数组
    private static String[] convertStrToArray(String str) {
        String[] strArray = null;
        if (!TextUtils.isEmpty(str)) {
            strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        }
        return strArray;
    }

    //设置评价标签的显示逻辑
    private void setLableViewVisible(String tmpData[]) {
        if (tmpData == null) {
            sobot_hide_layout.setVisibility(View.GONE);
            return;
        } else {
            sobot_hide_layout.setVisibility(View.VISIBLE);
        }

        switch (tmpData.length) {
            case 1:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setVisibility(View.INVISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.GONE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
                break;
            case 2:
                sobot_evaluate_cb_lable1.setText(tmpData[0]);
                sobot_evaluate_cb_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_cb_lable2.setText(tmpData[1]);
                sobot_evaluate_cb_lable2.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable1.setVisibility(View.VISIBLE);
                sobot_evaluate_ll_lable2.setVisibility(View.GONE);
                sobot_evaluate_ll_lable3.setVisibility(View.GONE);
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
                break;
            default:
                break;
        }
    }

    //检测选中的标签
    private String checkBoxIsChecked() {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < checkBoxList.size(); i++) {
            if (checkBoxList.get(i).isChecked()) {
                str.append(checkBoxList.get(i).getText() + ",");
            }
        }
        if (str.length()>0){
            str.substring(0,str.length()-1);
        }
        return str + "";
    }
}