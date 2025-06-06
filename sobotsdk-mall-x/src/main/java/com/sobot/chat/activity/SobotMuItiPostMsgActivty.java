package com.sobot.chat.activity;

import static com.sobot.chat.fragment.SobotBaseFragment.REQUEST_CODE_CAMERA;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.R;
import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.base.SobotDialogBaseActivity;
import com.sobot.chat.adapter.SobotPicListAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.PostParamModel;
import com.sobot.chat.api.model.SobotCacheFile;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.api.model.SobotLeaveMsgConfig;
import com.sobot.chat.api.model.SobotLeaveMsgParamModel;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.camera.util.FileUtil;
import com.sobot.chat.listener.ISobotCusField;
import com.sobot.chat.presenter.StCusFieldPresenter;
import com.sobot.chat.presenter.StPostMsgPresenter;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ImageUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.MD5Util;
import com.sobot.chat.utils.MediaFileUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotJsonUtils;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.SobotSerializableMap;
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.attachment.FileTypeConfig;
import com.sobot.chat.widget.dialog.SobotDeleteWorkOrderDialog;
import com.sobot.chat.widget.dialog.SobotDialogUtils;
import com.sobot.chat.widget.dialog.SobotSelectPicDialog;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;
import com.sobot.network.http.callback.StringResultCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 多伦 工单节点对应的 留言弹窗界面
 */
public class SobotMuItiPostMsgActivty extends SobotDialogBaseActivity implements View.OnClickListener, ISobotCusField {
    private LinearLayout sobot_btn_cancle;
    private TextView sobot_tv_title;
    private EditText sobot_post_email, sobot_et_content, sobot_post_phone, sobot_post_title;
    private TextView sobot_tv_post_msg, sobot_post_email_lable, sobot_post_phone_lable, sobot_post_lable, sobot_post_title_lable, sobot_post_question_type, sobot_post_question_lable, sobot_tv_problem_description, tv_problem_description_required;
    private View sobot_frist_line, sobot_post_title_line, sobot_post_question_line, sobot_post_customer_line, sobot_post_title_sec_line, sobot_post_question_sec_line, sobot_post_customer_sec_line, sobot_phone_line;
    private Button sobot_btn_submit;
    private GridView sobot_post_msg_pic;
    private LinearLayout sobot_enclosure_container, sobot_post_customer_field, sobot_post_question_ll, sobot_ll_content_img, ll_problem_description_title;
    private RelativeLayout sobot_post_email_rl, sobot_post_phone_rl, sobot_post_title_rl;
    private TextView title_hint_input_lable, email_hint_input_label, phone_hint_input_label;
    private ArrayList<ZhiChiUploadAppFileModelResult> pic_list = new ArrayList<>();
    private SobotPicListAdapter adapter;
    private SobotSelectPicDialog menuWindow;
    private String mUid = "";
    //临时 回显提示语时使用
    private String templateId = "";
    private String tipMsgId = "";

    /**
     * 删除图片弹窗
     */
    protected SobotDeleteWorkOrderDialog seleteMenuWindow;

    private ArrayList<SobotFieldModel> mFields;

    private LinearLayout sobot_post_msg_layout;

    private SobotLeaveMsgConfig mConfig;
    private Information information;
    private String uid = "";
    private String mGroupId = "";
    private boolean flag_exit_sdk;

    private int flag_exit_type = -1;


//    public static SobotMuItiPostMsgActivty newInstance(Bundle data) {
//        Bundle arguments = new Bundle();
//        arguments.putBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, data);
//        SobotMuItiPostMsgActivty muItiPostMsgActivty = new SobotMuItiPostMsgActivty();
//
//        return fragment;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            if (!SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);//竖屏
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//横屏

            }
        }
        super.onCreate(savedInstanceState);

        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        win.setAttributes(lp);
    }

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_muit_post_msg");
    }

    @Override
    protected void initView() {
        sobot_tv_title = (TextView) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_tv_title"));
        sobot_tv_title.setText(getResString("sobot_write_info_string"));
        sobot_btn_cancle = (LinearLayout) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_btn_cancle"));
        templateId = getIntent().getStringExtra("templateId");
        tipMsgId = getIntent().getStringExtra("tipMsgId");
        mUid = getIntent().getStringExtra(StPostMsgPresenter.INTENT_KEY_UID);
        mConfig = (SobotLeaveMsgConfig) getIntent().getSerializableExtra(StPostMsgPresenter.INTENT_KEY_CONFIG);
        mGroupId = getIntent().getStringExtra(StPostMsgPresenter.INTENT_KEY_GROUPID);
        ZhiChiInitModeBase initMode = (ZhiChiInitModeBase) SharedPreferencesUtil.getObject(getSobotBaseContext(),
                ZhiChiConstant.sobot_last_current_initModel);
        sobot_btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCollection();
                finish();
            }
        });

        if (mConfig == null) {
            //如果mConfig 为空，直接从初始化接口获取配置信息
            Information info = (Information) SharedPreferencesUtil.getObject(getSobotBaseContext(), "sobot_last_current_info");
            mConfig = new SobotLeaveMsgConfig();
            mConfig.setEmailFlag(initMode.isEmailFlag());
            mConfig.setEmailShowFlag(initMode.isEmailShowFlag());
            mConfig.setEnclosureFlag(initMode.isEnclosureFlag());
            mConfig.setEnclosureShowFlag(initMode.isEnclosureShowFlag());
            mConfig.setTelFlag(initMode.isTelFlag());
            mConfig.setTelShowFlag(initMode.isTelShowFlag());
            mConfig.setTicketStartWay(initMode.isTicketStartWay());
            mConfig.setTicketShowFlag(initMode.isTicketShowFlag());
            mConfig.setCompanyId(initMode.getCompanyId());
            if (!TextUtils.isEmpty(info.getLeaveMsgTemplateContent())) {
                mConfig.setMsgTmp(info.getLeaveMsgTemplateContent());
            } else {
                mConfig.setMsgTmp(initMode.getMsgTmp());
            }
            if (!TextUtils.isEmpty(info.getLeaveMsgGuideContent())) {
                mConfig.setMsgTxt(info.getLeaveMsgGuideContent());
            } else {
                mConfig.setMsgTxt(initMode.getMsgTxt());
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString(StPostMsgPresenter.INTENT_KEY_UID, mUid);
        bundle.putString(StPostMsgPresenter.INTENT_KEY_GROUPID, mGroupId);
        bundle.putInt(ZhiChiConstant.FLAG_EXIT_TYPE, flag_exit_type);
        bundle.putBoolean(ZhiChiConstant.FLAG_EXIT_SDK, flag_exit_sdk);
        bundle.putSerializable(StPostMsgPresenter.INTENT_KEY_CONFIG, mConfig);

        if (bundle != null) {
            uid = bundle.getString(StPostMsgPresenter.INTENT_KEY_UID);
            mGroupId = bundle.getString(StPostMsgPresenter.INTENT_KEY_GROUPID);
            flag_exit_type = bundle.getInt(ZhiChiConstant.FLAG_EXIT_TYPE, -1);
            flag_exit_sdk = bundle.getBoolean(ZhiChiConstant.FLAG_EXIT_SDK, false);
            mConfig = (SobotLeaveMsgConfig) bundle.getSerializable(StPostMsgPresenter.INTENT_KEY_CONFIG);
        }
        sobot_ll_content_img = (LinearLayout) findViewById(getResId("sobot_ll_content_img"));
        sobot_post_phone = (EditText) findViewById(getResId("sobot_post_phone"));
        sobot_post_email = (EditText) findViewById(getResId("sobot_post_email"));
        sobot_post_title = (EditText) findViewById(getResId("sobot_post_title"));
        sobot_frist_line = findViewById(getResId("sobot_frist_line"));
        sobot_post_title_line = findViewById(getResId("sobot_post_title_line"));
        sobot_post_question_line = findViewById(getResId("sobot_post_question_line"));
        sobot_post_customer_line = findViewById(getResId("sobot_post_customer_line"));
        sobot_post_title_sec_line = findViewById(getResId("sobot_post_title_sec_line"));
        sobot_post_question_sec_line = findViewById(getResId("sobot_post_question_sec_line"));
        sobot_post_customer_sec_line = findViewById(getResId("sobot_post_customer_sec_line"));
        sobot_phone_line = findViewById(getResId("sobot_phone_line"));
        sobot_et_content = (EditText) findViewById(getResId("sobot_post_et_content"));
        sobot_tv_post_msg = (TextView) findViewById(getResId("sobot_tv_post_msg"));
        sobot_post_email_lable = (TextView) findViewById(getResId("sobot_post_email_lable"));
        sobot_post_phone_lable = (TextView) findViewById(getResId("sobot_post_phone_lable"));
        sobot_post_title_lable = (TextView) findViewById(getResId("sobot_post_title_lable"));
        sobot_post_lable = (TextView) findViewById(getResId("sobot_post_question_lable"));
        String test = getResString("sobot_problem_types") + "<font color='#f9676f'>&nbsp;*</font>";
        sobot_post_lable.setText(Html.fromHtml(test));
        sobot_post_question_lable = (TextView) findViewById(getResId("sobot_post_question_lable"));
        sobot_post_question_type = (TextView) findViewById(getResId("sobot_post_question_type"));
        sobot_post_msg_layout = (LinearLayout) findViewById(getResId("sobot_post_msg_layout"));
        sobot_enclosure_container = (LinearLayout) findViewById(getResId("sobot_enclosure_container"));
        sobot_post_customer_field = (LinearLayout) findViewById(getResId("sobot_post_customer_field"));
        sobot_post_email_rl = (RelativeLayout) findViewById(getResId("sobot_post_email_rl"));
        email_hint_input_label = (TextView) findViewById(getResId("sobot_post_email_lable_hint"));
        email_hint_input_label.setHint(ResourceUtils.getResString(getSobotBaseActivity(), "sobot_please_input"));
        title_hint_input_lable = (TextView) findViewById(getResId("sobot_post_title_lable_hint"));
        title_hint_input_lable.setHint(ResourceUtils.getResString(getSobotBaseActivity(), "sobot_please_input"));
        sobot_post_phone_rl = (RelativeLayout) findViewById(getResId("sobot_post_phone_rl"));
        phone_hint_input_label = (TextView) findViewById(getResId("sobot_post_phone_lable_hint"));
        phone_hint_input_label.setHint(ResourceUtils.getResString(getSobotBaseActivity(), "sobot_please_input"));
        sobot_post_title_rl = (RelativeLayout) findViewById(getResId("sobot_post_title_rl"));
        sobot_post_question_ll = (LinearLayout) findViewById(getResId("sobot_post_question_ll"));
        sobot_post_question_ll.setOnClickListener(this);
        ll_problem_description_title = findViewById(R.id.ll_problem_description_title);
        sobot_tv_problem_description = (TextView) findViewById(getResId("sobot_tv_problem_description"));
        sobot_tv_problem_description.setText(ResourceUtils.getResString(getSobotBaseActivity(), "sobot_problem_description"));
        tv_problem_description_required = findViewById(R.id.tv_problem_description_required);
        if (mConfig.isTicketContentShowFlag()) {
            //问题描述是否显示
            ll_problem_description_title.setVisibility(View.VISIBLE);
            sobot_et_content.setVisibility(View.VISIBLE);
            //问题描述是否必填
            if (mConfig.isTicketContentFillFlag()) {
                tv_problem_description_required.setVisibility(View.VISIBLE);

            } else {
                tv_problem_description_required.setVisibility(View.GONE);
            }
        } else {
            ll_problem_description_title.setVisibility(View.GONE);
            sobot_et_content.setVisibility(View.GONE);
        }
        sobot_btn_submit = (Button) findViewById(getResId("sobot_btn_submit"));
        sobot_btn_submit.setText(ResourceUtils.getResString(getSobotBaseActivity(), "sobot_btn_submit_text"));
        sobot_btn_submit.setOnClickListener(this);
        sobot_post_customer_field.setVisibility(View.GONE);

        if (mConfig.isEmailShowFlag()) {
            sobot_post_email_rl.setVisibility(View.VISIBLE);
            sobot_post_email_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sobot_post_email.setVisibility(View.VISIBLE);
                    sobot_post_email_lable.setTextColor(ContextCompat.getColor(getSobotBaseActivity(), ResourceUtils.getResColorId(getSobotBaseActivity(), "sobot_common_gray2")));
                    sobot_post_email_lable.setTextSize(12);

                    sobot_post_email.setFocusable(true);
                    sobot_post_email.setFocusableInTouchMode(true);
                    sobot_post_email.requestFocus();
                    email_hint_input_label.setVisibility(View.GONE);
                    KeyboardUtil.showKeyboard(sobot_post_email);


                }
            });
        } else {
            sobot_post_email_rl.setVisibility(View.GONE);
        }
        sobot_post_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(sobot_post_email.getText().toString().trim())) {
                        sobot_post_email_lable.setTextSize(14);
                        sobot_post_email_lable.setTextColor(ContextCompat.getColor(getSobotBaseActivity(), ResourceUtils.getResColorId(getSobotBaseActivity(), "sobot_common_gray1")));
                        sobot_post_email.setVisibility(View.GONE);
                        email_hint_input_label.setVisibility(View.VISIBLE);
                    }
                } else {
                    email_hint_input_label.setVisibility(View.GONE);
                }
            }
        });

        if (mConfig.isTelShowFlag()) {
            sobot_post_phone_rl.setVisibility(View.VISIBLE);
            sobot_post_phone_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sobot_post_phone.setVisibility(View.VISIBLE);
                    sobot_post_phone_lable.setTextColor(ContextCompat.getColor(getSobotBaseActivity(), ResourceUtils.getResColorId(getSobotBaseActivity(), "sobot_common_gray2")));
                    sobot_post_phone_lable.setTextSize(12);
                    sobot_post_phone.setFocusable(true);
                    sobot_post_phone.setFocusableInTouchMode(true);
                    sobot_post_phone.requestFocus();
                    phone_hint_input_label.setVisibility(View.GONE);
                    KeyboardUtil.showKeyboard(sobot_post_phone);

                }
            });
        } else {
            sobot_post_phone_rl.setVisibility(View.GONE);
        }
        sobot_post_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(sobot_post_phone.getText().toString().trim())) {
                        sobot_post_phone_lable.setTextSize(14);
                        sobot_post_phone_lable.setTextColor(ContextCompat.getColor(getSobotBaseActivity(), ResourceUtils.getResColorId(getSobotBaseActivity(), "sobot_common_gray1")));
                        sobot_post_phone.setVisibility(View.GONE);
                        phone_hint_input_label.setVisibility(View.VISIBLE);
                    }
                } else {
                    phone_hint_input_label.setVisibility(View.GONE);
                }
            }
        });

        if (mConfig.isTicketTitleShowFlag()) {
            sobot_post_title_rl.setVisibility(View.VISIBLE);
            sobot_post_title_line.setVisibility(View.VISIBLE);
            sobot_post_title_sec_line.setVisibility(View.VISIBLE);
            sobot_post_title_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sobot_post_title.setVisibility(View.VISIBLE);
                    sobot_post_title_lable.setTextColor(ContextCompat.getColor(getSobotBaseActivity(), ResourceUtils.getResColorId(getSobotBaseActivity(), "sobot_common_gray2")));
                    sobot_post_title_lable.setTextSize(12);
                    sobot_post_title.setFocusable(true);
                    sobot_post_title.setFocusableInTouchMode(true);
                    sobot_post_title.requestFocus();
                    title_hint_input_lable.setVisibility(View.GONE);
                    KeyboardUtil.showKeyboard(sobot_post_title);

                }
            });
        } else {
            sobot_post_title_rl.setVisibility(View.GONE);
        }

        sobot_post_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(sobot_post_title.getText().toString().trim())) {
                        sobot_post_title_lable.setTextSize(14);
                        sobot_post_title_lable.setTextColor(ContextCompat.getColor(getSobotBaseActivity(), ResourceUtils.getResColorId(getSobotBaseActivity(), "sobot_common_gray1")));
                        sobot_post_title.setVisibility(View.GONE);
                        title_hint_input_lable.setVisibility(View.VISIBLE);
                    }
                } else {
                    title_hint_input_lable.setVisibility(View.GONE);
                }
            }
        });

        if (mConfig.isEmailShowFlag()) {
            sobot_frist_line.setVisibility(View.VISIBLE);
        } else {
            sobot_frist_line.setVisibility(View.GONE);
        }

        sobot_phone_line.setVisibility(mConfig.isTelShowFlag() ? View.VISIBLE : View.GONE);

        String sobotUserPhone = (information != null ? information.getUser_tels() : "");
        if (mConfig.isTelShowFlag() && !TextUtils.isEmpty(sobotUserPhone)) {
            sobot_post_phone.setVisibility(View.VISIBLE);
            sobot_post_phone.setText(sobotUserPhone);
            phone_hint_input_label.setVisibility(View.GONE);
            sobot_post_phone_lable.setTextColor(ContextCompat.getColor(getSobotBaseActivity(), ResourceUtils.getResColorId(getSobotBaseActivity(), "sobot_common_gray2")));
            sobot_post_phone_lable.setTextSize(12);
        }
        String sobotUserEmail = (information != null ? information.getUser_emails() : "");
        if (mConfig.isEmailShowFlag() && !TextUtils.isEmpty(sobotUserEmail)) {
            sobot_post_email.setVisibility(View.VISIBLE);
            sobot_post_email.setText(sobotUserEmail);
            email_hint_input_label.setVisibility(View.GONE);
            sobot_post_email_lable.setTextColor(ContextCompat.getColor(getSobotBaseActivity(), ResourceUtils.getResColorId(getSobotBaseActivity(), "sobot_common_gray2")));
            sobot_post_email_lable.setTextSize(12);
        }

        if (mConfig.isEnclosureShowFlag()) {
            sobot_enclosure_container.setVisibility(View.VISIBLE);
            initPicListView();
        } else {
            sobot_enclosure_container.setVisibility(View.GONE);
        }

        if (mConfig.isTicketTypeFlag() && mConfig.getType() != null && mConfig.getType().size() > 0) {
            sobot_post_question_ll.setVisibility(View.VISIBLE);
            sobot_post_question_line.setVisibility(View.VISIBLE);
            sobot_post_question_sec_line.setVisibility(View.VISIBLE);
        } else {
            sobot_post_question_ll.setVisibility(View.GONE);
            sobot_post_question_type.setTag(mConfig.getTicketTypeId());
        }

        displayInNotch(sobot_tv_post_msg);
        displayInNotch(sobot_post_email_lable);
        displayInNotch(sobot_post_phone_lable);
        displayInNotch(sobot_post_title_lable);
        displayInNotch(sobot_post_question_type);
        displayInNotch(sobot_post_question_lable);
        displayInNotch(sobot_ll_content_img);
        displayInNotch(sobot_post_email);
        displayInNotch(sobot_post_phone);
        displayInNotch(sobot_post_title);
        displayInNotch(title_hint_input_lable);
        displayInNotch(email_hint_input_label);
        displayInNotch(phone_hint_input_label);
    }

    @Override
    protected void initData() {
        information = (Information) SharedPreferencesUtil.getObject(getSobotBaseActivity(), "sobot_last_current_info");
        zhiChiApi.getTemplateFieldsInfo(SobotMuItiPostMsgActivty.this, uid, mConfig.getTemplateId(), new StringResultCallBack<SobotLeaveMsgParamModel>() {

            @Override
            public void onSuccess(SobotLeaveMsgParamModel result) {
                if (result != null) {
                    if (result.getField() != null && result.getField().size() != 0) {
                        sobot_post_customer_line.setVisibility(View.VISIBLE);
                        sobot_post_customer_sec_line.setVisibility(View.VISIBLE);
                        mFields = result.getField();
                        StCusFieldPresenter.addWorkOrderCusFields(getSobotBaseActivity(), SobotMuItiPostMsgActivty.this.getSobotBaseActivity(), mFields, sobot_post_customer_field, SobotMuItiPostMsgActivty.this);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                try {
                    showHint(ResourceUtils.getResString(getSobotBaseActivity(), "sobot_try_again"));
                } catch (Exception e1) {

                }
            }

        });

        msgFilter();
        editTextSetHint();
    }

    /**
     * 提交
     */
    private void setCusFieldValue() {
        //自定义表单校验结果:为空,校验通过,可以提交;不为空,说明自定义字段校验不通过，不能提交留言表单;
        String checkCustomFieldResult = StCusFieldPresenter.formatCusFieldVal(getSobotBaseActivity(), sobot_post_customer_field, mFields);
        if (TextUtils.isEmpty(checkCustomFieldResult)) {
            checkSubmit();
        } else {
            showHint(checkCustomFieldResult);
        }
    }


    private void checkSubmit() {
        String userPhone = "", userEamil = "", title = "";

        if (mConfig.isTicketTitleShowFlag()) {
            if (TextUtils.isEmpty(sobot_post_title.getText().toString().trim())) {
                showHint(getResString("sobot_title") + "  " + getResString("sobot__is_null"));
                return;
            } else {
                title = sobot_post_title.getText().toString();
            }
        }

        if (sobot_post_question_ll.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(sobot_post_question_type.getText().toString())) {
                showHint(getResString("sobot_problem_types") + "  " + getResString("sobot__is_null"));
                return;
            }
        }

        if (mFields != null && mFields.size() != 0) {
            for (int i = 0; i < mFields.size(); i++) {
                if (1 == mFields.get(i).getCusFieldConfig().getFillFlag()) {
                    if (TextUtils.isEmpty(mFields.get(i).getCusFieldConfig().getValue())) {
                        showHint(mFields.get(i).getCusFieldConfig().getFieldName() + "  " + getResString("sobot__is_null"));
                        return;
                    }
                }
            }
        }
        if (mConfig.isTicketContentShowFlag() && mConfig.isTicketContentFillFlag()) {
            if (TextUtils.isEmpty(sobot_et_content.getText().toString().trim())) {
                showHint(getResString("sobot_problem_description") + "  " + getResString("sobot__is_null"));
                return;
            }
        }

        if (mConfig.isEnclosureShowFlag() && mConfig.isEnclosureFlag()) {
            if (TextUtils.isEmpty(getFileStr())) {
                showHint(getResString("sobot_please_load"));
                return;
            }
        }

        if (mConfig.isEmailShowFlag()) {
            if (mConfig.isEmailFlag()) {
                if (TextUtils.isEmpty(sobot_post_email.getText().toString().trim())) {
                    showHint(getResString("sobot_email_no_empty"));
                    return;
                }
                if (!TextUtils.isEmpty(sobot_post_email.getText().toString().trim())
                        && ScreenUtils.isEmail(sobot_post_email.getText().toString().trim())) {
                    userEamil = sobot_post_email.getText().toString().trim();
                } else {
                    showHint(getResString("sobot_email_dialog_hint"));
                    return;
                }
            } else {
                if (!TextUtils.isEmpty(sobot_post_email.getText().toString().trim())) {
                    String emailStr = sobot_post_email.getText().toString().trim();
                    if (ScreenUtils.isEmail(emailStr)) {
                        userEamil = sobot_post_email.getText().toString().trim();
                    } else {
                        showHint(getResString("sobot_email_dialog_hint"));
                        return;
                    }
                }
            }
        }

        if (mConfig.isTelShowFlag()) {
            if (mConfig.isTelFlag()) {
                if (TextUtils.isEmpty(sobot_post_phone.getText().toString().trim())) {
                    showHint(getResString("sobot_phone_no_empty"));
                    return;
                }
                userPhone = sobot_post_phone.getText().toString();
            } else {
                if (!TextUtils.isEmpty(sobot_post_phone.getText().toString().trim())) {
                    String phoneStr = sobot_post_phone.getText().toString().trim();
                    userPhone = phoneStr;
                }
            }
        }

        postMsg(userPhone, userEamil, title);
    }

    public void showHint(String content) {
        if (!TextUtils.isEmpty(content)) {
            CustomToast.makeText(getSobotBaseActivity(), content, 1000).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == sobot_post_question_ll) {
            if (mConfig.getType() != null && mConfig.getType().size() != 0) {
                Intent intent = new Intent(getSobotBaseActivity(), SobotPostCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("types", mConfig.getType());
                if (sobot_post_question_type != null &&
                        !TextUtils.isEmpty(sobot_post_question_type.getText().toString()) &&
                        sobot_post_question_type.getTag() != null &&
                        !TextUtils.isEmpty(sobot_post_question_type.getTag().toString())) {
                    bundle.putString("typeName", sobot_post_question_type.getText().toString());
                    bundle.putString("typeId", sobot_post_question_type.getTag().toString());
                }
                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, ZhiChiConstant.work_order_list_display_type_category);
            }
        }

        if (view == sobot_btn_submit) {
            setCusFieldValue();
        }
    }

    private void postMsg(String userPhone, String userEamil, String title) {

        final PostParamModel postParam = new PostParamModel();
        postParam.setTemplateId(mConfig.getTemplateId());
        postParam.setPartnerId(information.getPartnerid());
        postParam.setUid(uid);
        postParam.setTicketContent(sobot_et_content.getText().toString());
        postParam.setCustomerEmail(userEamil);
        postParam.setCustomerPhone(userPhone);
        postParam.setTicketTitle(title);
        postParam.setCompanyId(mConfig.getCompanyId());
        postParam.setFileStr(getFileStr());
        postParam.setGroupId(mGroupId);
        postParam.setTicketFrom("21");
        if (sobot_post_question_type.getTag() != null && !TextUtils.isEmpty(sobot_post_question_type.getTag().toString())) {
            postParam.setTicketTypeId(sobot_post_question_type.getTag().toString());
        }
        if (information.getLeaveCusFieldMap() != null && information.getLeaveCusFieldMap().size() > 0) {
            for (String key :
                    information.getLeaveCusFieldMap().keySet()) {
                SobotFieldModel sobotFieldModel = new SobotFieldModel();
                SobotCusFieldConfig sobotCusFieldConfig = new SobotCusFieldConfig();
                sobotCusFieldConfig.setFieldId(key);
                sobotCusFieldConfig.setValue(information.getLeaveCusFieldMap().get(key));
                sobotFieldModel.setCusFieldConfig(sobotCusFieldConfig);
                mFields.add(sobotFieldModel);
            }
        }
        postParam.setExtendFields(StCusFieldPresenter.getSaveFieldVal(mFields));
        if (information != null && information.getLeaveParamsExtends() != null) {
            postParam.setParamsExtends(SobotJsonUtils.toJson(information.getLeaveParamsExtends()));
        }
        final LinkedHashMap tempMap = new LinkedHashMap();
        if (mConfig.isTicketTitleShowFlag()) {
            tempMap.put(sobot_post_title_lable.getText().toString().replace(" *", ""), StringUtils.isEmpty(title) ? " - -" : title);
        }

        if (mConfig.isTicketTypeFlag() && mConfig.getType() != null && mConfig.getType().size() > 0) {
            tempMap.put(sobot_post_question_lable.getText().toString().replace(" *", ""), StringUtils.isEmpty(sobot_post_question_type.getText().toString()) ? " - -" : sobot_post_question_type.getText().toString());
        }
        if (mFields != null && mFields.size() > 0) {
            Map map = StCusFieldPresenter.getSaveFieldNameAndVal((mFields));
            if (map != null) {
                tempMap.putAll(map);
            }
        }
        if (mConfig.isTicketContentShowFlag()) {
            tempMap.put(getResString("sobot_problem_description"), StringUtils.isEmpty(sobot_et_content.getText().toString()) ? " - -" : sobot_et_content.getText().toString());
        }
        if (mConfig.isEnclosureShowFlag()) {
            tempMap.put(getResString("sobot_enclosure_string"), StringUtils.isEmpty(getFileNameStr()) ? " - -" : getFileNameStr());
        }

        if (mConfig.isEmailShowFlag()) {
            tempMap.put(sobot_post_email_lable.getText().toString().replace(" *", ""), StringUtils.isEmpty(userEamil) ? " - -" : userEamil);
        }
        if (mConfig.isTelShowFlag()) {
            tempMap.put(sobot_post_phone_lable.getText().toString().replace(" *", ""), StringUtils.isEmpty(userPhone) ? " - -" : userPhone);
        }

        zhiChiApi.postMsg(SobotMuItiPostMsgActivty.this, postParam, new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase base) {
                try {
                    if (Integer.parseInt(base.getStatus()) == 0) {
                        showHint(base.getMsg());
                    } else if (Integer.parseInt(base.getStatus()) == 1) {
                        if (getSobotBaseActivity() == null) {
                            return;
                        }
                        KeyboardUtil.hideKeyboard(getSobotBaseActivity().getCurrentFocus());
                        Intent intent = new Intent();
                        intent.setAction(ZhiChiConstants.SOBOT_CHAT_MUITILEAVEMSG_TO_CHATLIST);
                        Bundle bundle = new Bundle();
                        SobotSerializableMap sobotSerializableMap = new SobotSerializableMap();
                        sobotSerializableMap.setMap(tempMap);
                        bundle.putSerializable("leaveMsgData", sobotSerializableMap);
                        bundle.putString("tipMsgId", tipMsgId);
                        intent.putExtras(bundle);
                        CommonUtils.sendLocalBroadcast(getSobotBaseActivity(), intent);
                        if (!TextUtils.isEmpty(tipMsgId)) {
                            final ZhiChiInitModeBase initMode = (ZhiChiInitModeBase) SharedPreferencesUtil.getObject(getSobotBaseContext(),
                                    ZhiChiConstant.sobot_last_current_initModel);
                            Map map = new HashMap();
                            map.put("uid", initMode.getPartnerid());
                            map.put("cid", initMode.getCid());
                            map.put("msg", ResourceUtils.getResString(getSobotBaseActivity(), "sobot_re_commit") + " <a>" + ResourceUtils.getResString(getSobotBaseActivity(), "sobot_re_write") + "</a>");
                            map.put("msgId", tipMsgId);
                            map.put("deployId", templateId);
                            map.put("updateStatus", 1);//0表示插入 1表示更新
                            zhiChiApi.infoCollection(SobotMuItiPostMsgActivty.this, map, new StringResultCallBack<CommonModel>() {
                                @Override
                                public void onSuccess(CommonModel commonModel) {

                                }

                                @Override
                                public void onFailure(Exception e, String s) {

                                }
                            });
                        }
                        finish();
                    }
                } catch (Exception e) {
                    showHint(base.getMsg());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                try {
                    showHint(ResourceUtils.getResString(getSobotBaseActivity(), "sobot_try_again"));
                } catch (Exception e1) {

                }
            }
        });
    }


    @Override
    public void onDestroy() {
        SobotDialogUtils.stopProgressDialog(getSobotBaseActivity());
        super.onDestroy();
    }

    /**
     * 初始化图片选择的控件
     */
    private void initPicListView() {
        sobot_post_msg_pic = (GridView) findViewById(getResId("sobot_post_msg_pic"));
        adapter = new SobotPicListAdapter(getSobotBaseActivity(), pic_list);
        sobot_post_msg_pic.setAdapter(adapter);
//        sobot_post_msg_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                KeyboardUtil.hideKeyboard(view);
//                if (pic_list.get(position).getViewState() == 0) {
//                    menuWindow = new SobotSelectPicDialog(getSobotBaseActivity(), itemsOnClick);
//                    menuWindow.show();
//                } else {
//                    LogUtils.i("当前选择图片位置：" + position);
//                    Intent intent = new Intent(getSobotBaseActivity(), SobotPhotoListActivity.class);
//                    intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST, adapter.getPicList());
//                    intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM, position);
//                    startActivityForResult(intent, ZhiChiConstant.SOBOT_KEYTYPE_DELETE_FILE_SUCCESS);
//                }
//            }
//        });
        adapter.setOnClickItemViewListener(new SobotPicListAdapter.ViewClickListener() {
            @Override
            public void clickView(View view, int position, int type) {
                KeyboardUtil.hideKeyboard(view);
                switch (type) {
                    case SobotPicListAdapter.ADD:
                        menuWindow = new SobotSelectPicDialog(getSobotBaseActivity(), itemsOnClick);
                        menuWindow.show();
                        break;
                    case SobotPicListAdapter.PIC:
                        LogUtils.i("当前选择图片位置：" + position);
                        if (adapter != null && adapter.getPicList() != null) {
                            ZhiChiUploadAppFileModelResult result = adapter.getPicList().get(position);
                            if (result != null) {
                                if (!TextUtils.isEmpty(result.getFileLocalPath()) && MediaFileUtils.isVideoFileType(result.getFileLocalPath())) {
                                    File file = new File(result.getFileLocalPath());
                                    SobotCacheFile cacheFile = new SobotCacheFile();
                                    cacheFile.setFileName(file.getName());
                                    cacheFile.setUrl(result.getFileUrl());
                                    cacheFile.setFilePath(result.getFileLocalPath());
                                    cacheFile.setFileType(FileTypeConfig.getFileType(FileUtil.checkFileEndWith(result.getFileLocalPath())));
                                    cacheFile.setMsgId("" + System.currentTimeMillis());
                                    Intent intent = SobotVideoActivity.newIntent(getSobotBaseActivity(), cacheFile);
                                    getSobotBaseActivity().startActivity(intent);
                                    return;
                                }
                                if (SobotOption.imagePreviewListener != null) {
                                    //如果返回true,拦截;false 不拦截
                                    boolean isIntercept = SobotOption.imagePreviewListener.onPreviewImage(getSobotBaseContext(), TextUtils.isEmpty(result.getFileLocalPath()) ? result.getFileUrl() : result.getFileLocalPath());
                                    if (isIntercept) {
                                        return;
                                    }
                                }
                                Intent intent = new Intent(getSobotBaseActivity(), SobotPhotoActivity.class);
                                intent.putExtra("imageUrL", TextUtils.isEmpty(result.getFileLocalPath()) ? result.getFileUrl() : result.getFileLocalPath());
                                getSobotBaseActivity().startActivity(intent);
                            }
                        }
                        break;
                    case SobotPicListAdapter.DEL:
                        String popMsg = ResourceUtils.getResString(getSobotBaseActivity(), "sobot_do_you_delete_picture");
                        if (adapter == null || adapter.getPicList() == null)
                            return;
                        ZhiChiUploadAppFileModelResult delResult = adapter.getPicList().get(position);
                        if (delResult != null) {
                            if (!TextUtils.isEmpty(delResult.getFileLocalPath()) && MediaFileUtils.isVideoFileType(delResult.getFileLocalPath())) {
                                popMsg = ResourceUtils.getResString(getSobotBaseActivity(), "sobot_do_you_delete_video");
                            }
                        }
                        if (seleteMenuWindow != null) {
                            seleteMenuWindow.dismiss();
                            seleteMenuWindow = null;
                        }
                        if (seleteMenuWindow == null) {
                            seleteMenuWindow = new SobotDeleteWorkOrderDialog(getSobotBaseActivity(), popMsg, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    seleteMenuWindow.dismiss();
                                    if (v.getId() == getResId("btn_pick_photo")) {
                                        Log.e("onClick: ", seleteMenuWindow.getPosition() + "");
                                        pic_list.remove(seleteMenuWindow.getPosition());
                                        adapter.restDataView();
                                    }
                                }
                            });
                        }
                        seleteMenuWindow.setPosition(position);
                        seleteMenuWindow.show();
                        break;
                }

            }
        });
        adapter.restDataView();
    }

    //对msg过滤
    private void msgFilter() {
        if (information != null && information.getLeaveMsgTemplateContent() != null) {
            sobot_et_content.setHint(Html.fromHtml(information.getLeaveMsgTemplateContent().replace("<p>", "").replace("</p>", "<br/>").replace("\n", "<br/>")));
        } else {
            if (!TextUtils.isEmpty(mConfig.getMsgTmp())) {
                mConfig.setMsgTmp(mConfig.getMsgTmp().replace("<p>", "").replace("</p>", "<br/>").replace("\n", "<br/>"));
                sobot_et_content.setHint(Html.fromHtml(mConfig.getMsgTmp()));
            }
        }

        if (information != null && information.getLeaveMsgGuideContent() != null) {
            if (TextUtils.isEmpty(information.getLeaveMsgGuideContent())) {
                sobot_tv_post_msg.setVisibility(View.GONE);
            }
            HtmlTools.getInstance(getSobotBaseActivity().getApplicationContext()).setRichText(sobot_tv_post_msg, information.getLeaveMsgGuideContent().replace("<p>", "").replace("</p>", "<br/>").replace("\n", "<br/>"),
                    ResourceUtils.getIdByName(getSobotBaseActivity(), "color", "sobot_postMsg_url_color"));
        } else {
            if (!TextUtils.isEmpty(mConfig.getMsgTxt())) {
                mConfig.setMsgTxt(mConfig.getMsgTxt().replace("<p>", "").replace("</p>", "<br/>").replace("\n", "<br/>"));
                HtmlTools.getInstance(getSobotBaseActivity().getApplicationContext()).setRichText(sobot_tv_post_msg, mConfig.getMsgTxt(),
                        ResourceUtils.getIdByName(getSobotBaseActivity(), "color", "sobot_postMsg_url_color"));
            } else {
                sobot_tv_post_msg.setVisibility(View.GONE);
            }
        }

        sobot_post_msg_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.hideKeyboard(sobot_post_msg_layout);
            }
        });
    }

    //设置editText的hint提示字体
    private void editTextSetHint() {
        String mustFill = "<font color='#f9676f'>&nbsp;*</font>";

        if (mConfig.isEmailFlag()) {
            sobot_post_email_lable.setText(Html.fromHtml(getResString("sobot_email") + mustFill));
        } else {
            sobot_post_email_lable.setText(Html.fromHtml(getResString("sobot_email")));
        }

        if (mConfig.isTelFlag()) {
            sobot_post_phone_lable.setText(Html.fromHtml(getResString("sobot_phone") + mustFill));
        } else {
            sobot_post_phone_lable.setText(Html.fromHtml(getResString("sobot_phone")));
        }
        if (mConfig.isTicketTitleShowFlag()) {
            sobot_post_title_lable.setText(Html.fromHtml(getResString("sobot_title") + mustFill));
        }

    }

    private ChatUtils.SobotSendFileListener sendFileListener = new ChatUtils.SobotSendFileListener() {
        @Override
        public void onSuccess(final String filePath) {
            zhiChiApi.fileUploadForPostMsg(SobotMuItiPostMsgActivty.this, mConfig.getCompanyId(), uid, filePath, new ResultCallBack<ZhiChiMessage>() {
                @Override
                public void onSuccess(ZhiChiMessage zhiChiMessage) {
                    SobotDialogUtils.stopProgressDialog(getSobotBaseActivity());
                    if (zhiChiMessage.getData() != null) {
                        ZhiChiUploadAppFileModelResult item = new ZhiChiUploadAppFileModelResult();
                        item.setFileUrl(zhiChiMessage.getData().getUrl());
                        item.setFileLocalPath(filePath);
                        item.setViewState(1);
                        adapter.addData(item);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    SobotDialogUtils.stopProgressDialog(getSobotBaseActivity());
                    showHint(TextUtils.isEmpty(des) ? ResourceUtils.getResString(getSobotBaseActivity(), "sobot_net_work_err") : des);
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {

                }
            });
        }

        @Override
        public void onError() {
            SobotDialogUtils.stopProgressDialog(getSobotBaseActivity());
        }
    };

    public String getFileStr() {
        String tmpStr = "";
        if (!mConfig.isEnclosureShowFlag()) {
            return tmpStr;
        }

        ArrayList<ZhiChiUploadAppFileModelResult> tmpList = adapter.getPicList();
        for (int i = 0; i < tmpList.size(); i++) {
            tmpStr += tmpList.get(i).getFileUrl() + ";";
        }
        return tmpStr;
    }

    public String getFileNameStr() {
        String tmpStr = "";
        if (!mConfig.isEnclosureShowFlag()) {
            return tmpStr;
        }

        ArrayList<ZhiChiUploadAppFileModelResult> tmpList = adapter.getPicList();
        for (int i = 0; i < tmpList.size(); i++) {
            if (!TextUtils.isEmpty(tmpList.get(i).getFileLocalPath())) {
                tmpStr += tmpList.get(i).getFileLocalPath().substring(tmpList.get(i).getFileLocalPath().lastIndexOf("/") + 1);
            }
            if (i != (tmpList.size() - 1)) {
                tmpStr += "<br/>";
            }
        }
        return tmpStr;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
                if (data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage == null) {
                        selectedImage = ImageUtils.getUri(data, getSobotBaseActivity());
                    }
                    String path = ImageUtils.getPath(getSobotBaseActivity(), selectedImage);
                    if (!StringUtils.isEmpty(path)) {
                        if (MediaFileUtils.isVideoFileType(path)) {
                            try {
                                File selectedFile = new File(path);
                                if (selectedFile.exists()) {
                                    if (selectedFile.length() > 50 * 1024 * 1024) {
                                        ToastUtil.showToast(getContext(), getResString("sobot_file_upload_failed"));
                                        return;
                                    }
                                }
                                SobotDialogUtils.startProgressDialog(getSobotBaseActivity());
//                            ChatUtils.sendPicByFilePath(getSobotBaseActivity(),path,sendFileListener,false);
                                String fName = MD5Util.encode(path);
                                String filePath = null;
                                try {
                                    filePath = FileUtil.saveImageFile(getSobotBaseActivity(), selectedImage, fName + FileUtil.getFileEndWith(path), path);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastUtil.showToast(getSobotBaseActivity(), ResourceUtils.getResString(getSobotBaseActivity(), "sobot_pic_type_error"));
                                    return;
                                }
                                sendFileListener.onSuccess(filePath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            SobotDialogUtils.startProgressDialog(getSobotBaseActivity());
                            ChatUtils.sendPicByUriPost(getSobotBaseActivity(), selectedImage, sendFileListener, false);
                        }
                    } else {
                        showHint(getResString("sobot_did_not_get_picture_path"));
                    }
                } else {
                    showHint(getResString("sobot_did_not_get_picture_path"));
                }
            } else if (requestCode == ZhiChiConstant.REQUEST_CODE_makePictureFromCamera) {
                if (cameraFile != null && cameraFile.exists()) {
                    SobotDialogUtils.startProgressDialog(getSobotBaseActivity());
                    ChatUtils.sendPicByFilePath(getSobotBaseActivity(), cameraFile.getAbsolutePath(), sendFileListener, true);
                } else {
                    showHint(getResString("sobot_pic_select_again"));
                }
            }
        } else if (resultCode == SobotCameraActivity.RESULT_CODE) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                int actionType = SobotCameraActivity.getActionType(data);
                if (actionType == SobotCameraActivity.ACTION_TYPE_VIDEO) {
                    File videoFile = new File(SobotCameraActivity.getSelectedVideo(data));
                    if (videoFile.exists()) {
                        cameraFile = videoFile;
                        SobotDialogUtils.startProgressDialog(SobotMuItiPostMsgActivty.this);
                        sendFileListener.onSuccess(videoFile.getAbsolutePath());
                    } else {
                        showHint(getResString("sobot_pic_select_again"));
                    }
                } else {
                    File tmpPic = new File(SobotCameraActivity.getSelectedImage(data));
                    if (tmpPic.exists()) {
                        cameraFile = tmpPic;
                        SobotDialogUtils.startProgressDialog(SobotMuItiPostMsgActivty.this);
                        ChatUtils.sendPicByFilePath(SobotMuItiPostMsgActivty.this, tmpPic.getAbsolutePath(), sendFileListener, true);
                    } else {
                        showHint(getResString("sobot_pic_select_again"));
                    }
                }
            }
        }
        StCusFieldPresenter.onStCusFieldActivityResult(getSobotBaseActivity(), data, mFields, sobot_post_customer_field);
        if (data != null) {
            switch (requestCode) {
                case ZhiChiConstant.SOBOT_KEYTYPE_DELETE_FILE_SUCCESS://图片预览
                    List<ZhiChiUploadAppFileModelResult> tmpList = (List<ZhiChiUploadAppFileModelResult>) data.getExtras().getSerializable(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST);
                    adapter.addDatas(tmpList);
                    break;
                case ZhiChiConstant.work_order_list_display_type_category:
                    if (!TextUtils.isEmpty(data.getStringExtra("category_typeId"))) {
                        String typeName = data.getStringExtra("category_typeName");
                        String typeId = data.getStringExtra("category_typeId");

                        if (!TextUtils.isEmpty(typeName)) {
                            sobot_post_question_type.setText(typeName);
                            sobot_post_question_type.setTag(typeId);
                            sobot_post_question_type.setVisibility(View.VISIBLE);
                            sobot_post_question_lable.setTextColor(ContextCompat.getColor(getSobotBaseActivity(), ResourceUtils.getResColorId(getSobotBaseActivity(), "sobot_common_gray2")));
                            sobot_post_question_lable.setTextSize(12);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }


    // 为弹出窗口popupwindow实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            if (v.getId() == getResId("btn_take_photo")) {
                LogUtils.i("拍照");
                selectPicFromCamera();
            }
            if (v.getId() == getResId("btn_pick_photo")) {
                LogUtils.i("选择照片");
                selectPicFromLocal();
            }
            if (v.getId() == getResId("btn_pick_vedio")) {
                LogUtils.i("选择视频");
                selectVedioFromLocal();
            }

        }
    };

    @Override
    public void onClickCusField(View view, int fieldType, SobotFieldModel cusField) {
        switch (fieldType) {
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE:
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_TIME_TYPE:
                StCusFieldPresenter.openTimePicker(getSobotBaseActivity(), view, fieldType);
                break;
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_SPINNER_TYPE:
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_RADIO_TYPE:
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE:
                StCusFieldPresenter.startSobotCusFieldActivity(getSobotBaseActivity(), null, cusField);
                break;
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CASCADE_TYPE:
                if (cusField != null && cusField.getCusFieldDataInfoList() != null && cusField.getCusFieldDataInfoList().size() > 0) {
                    Intent intent = new Intent(getContext(), SobotPostCascadeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("cusField", cusField);
                    bundle.putSerializable("fieldId", cusField.getCusFieldConfig().getFieldId());
                    intent.putExtra("bundle", bundle);
                    startActivityForResult(intent, ZhiChiConstant.work_order_list_display_type_category);
                }
            default:
                break;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() <= 0) {
                infoCollection();
                finish();
            }
        }
        return true;
    }

    //关闭按钮关闭界面和点击空白区域关闭界面,插入系统消息
    private void infoCollection() {
        if (TextUtils.isEmpty(tipMsgId)) {
            final ZhiChiInitModeBase initMode = (ZhiChiInitModeBase) SharedPreferencesUtil.getObject(getSobotBaseContext(),
                    ZhiChiConstant.sobot_last_current_initModel);
            Map map = new HashMap();
            map.put("uid", initMode.getPartnerid());
            map.put("cid", initMode.getCid());
            map.put("msg", ResourceUtils.getResString(getSobotBaseActivity(), "sobot_re_commit") + " <a>" + ResourceUtils.getResString(getSobotBaseActivity(), "sobot_re_write") + "</a>");
            String msgId = UUID.randomUUID().toString();
            if (!TextUtils.isEmpty(msgId)) {
                msgId = msgId.replace("-", "") + System.currentTimeMillis();
            } else {
                msgId = System.currentTimeMillis() + "";
            }
            map.put("msgId", msgId);
            map.put("deployId", templateId);
            map.put("updateStatus", 0);
            Intent intent = new Intent();
            intent.setAction(ZhiChiConstants.SOBOT_CHAT_MUITILEAVEMSG_TO_CHATLIST);
            intent.putExtra("msgId", msgId);
            intent.putExtra("deployId", templateId);
            intent.putExtra("msg", ResourceUtils.getResString(getSobotBaseActivity(), "sobot_re_commit") + " <a>" + ResourceUtils.getResString(getSobotBaseActivity(), "sobot_re_write") + "</a>");
            CommonUtils.sendLocalBroadcast(getSobotBaseActivity(), intent);
            zhiChiApi.infoCollection(SobotMuItiPostMsgActivty.this, map, new StringResultCallBack<CommonModel>() {
                @Override
                public void onSuccess(CommonModel commonModel) {

                }

                @Override
                public void onFailure(Exception e, String s) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        infoCollection();
        super.onBackPressed();
    }
}
