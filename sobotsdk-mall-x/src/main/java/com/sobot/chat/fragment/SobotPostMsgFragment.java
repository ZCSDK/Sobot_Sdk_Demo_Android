package com.sobot.chat.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.activity.SobotCameraActivity;
import com.sobot.chat.activity.SobotPhotoActivity;
import com.sobot.chat.activity.SobotPostCascadeActivity;
import com.sobot.chat.activity.SobotPostCategoryActivity;
import com.sobot.chat.activity.SobotPostMsgActivity;
import com.sobot.chat.activity.SobotVideoActivity;
import com.sobot.chat.adapter.SobotPicListAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.PostParamModel;
import com.sobot.chat.api.model.SobotCacheFile;
import com.sobot.chat.api.model.SobotCusFieldConfig;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.api.model.SobotLeaveMsgConfig;
import com.sobot.chat.api.model.SobotLeaveMsgParamModel;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.application.MyApplication;
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
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.SobotGridView;
import com.sobot.chat.widget.attachment.FileTypeConfig;
import com.sobot.chat.widget.dialog.SobotDeleteWorkOrderDialog;
import com.sobot.chat.widget.dialog.SobotDialogUtils;
import com.sobot.chat.widget.dialog.SobotSelectPicDialog;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;
import com.sobot.network.http.callback.StringResultCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 留言界面
 *
 * @author Created by jinxl on 2019/3/7.
 */
public class SobotPostMsgFragment extends SobotBaseFragment implements View.OnClickListener, ISobotCusField {
    private View mRootView;

    private EditText sobot_post_email, sobot_et_content, sobot_post_phone, sobot_post_title;
    private TextView sobot_tv_post_msg, sobot_post_email_lable, sobot_post_phone_lable, sobot_post_lable, sobot_post_title_lable, sobot_post_question_type, sobot_post_question_lable, sobot_tv_problem_description, tv_problem_description_required;
    private View sobot_frist_line, sobot_post_title_line, sobot_post_question_line, sobot_post_customer_line, sobot_post_title_sec_line, sobot_post_question_sec_line, sobot_post_customer_sec_line, sobot_phone_line;
    private Button sobot_btn_submit;
    private SobotGridView sobot_post_msg_pic;
    private LinearLayout sobot_post_customer_field, sobot_post_question_ll, sobot_ll_content_img, ll_problem_description_title;
    private RelativeLayout sobot_post_email_rl, sobot_post_phone_rl, sobot_post_title_rl;
    private TextView title_hint_input_lable, email_hint_input_label, phone_hint_input_label;
    private ArrayList<ZhiChiUploadAppFileModelResult> pic_list = new ArrayList<>();
    private SobotPicListAdapter adapter;
    private SobotSelectPicDialog menuWindow;


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

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(final android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (flag_exit_type == 1) {
                        finishPageOrSDK(true);
                    } else if (flag_exit_type == 2) {
                        getSobotActivity().setResult(200);
                        finishPageOrSDK(false);
                    } else {
                        finishPageOrSDK(flag_exit_sdk);
                    }
                    break;
            }
        }
    };

    public static SobotPostMsgFragment newInstance(Bundle data) {
        Bundle arguments = new Bundle();
        arguments.putBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, data);
        SobotPostMsgFragment fragment = new SobotPostMsgFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments().getBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION);
            if (bundle != null) {
                uid = bundle.getString(StPostMsgPresenter.INTENT_KEY_UID);
                mGroupId = bundle.getString(StPostMsgPresenter.INTENT_KEY_GROUPID);
                flag_exit_type = bundle.getInt(ZhiChiConstant.FLAG_EXIT_TYPE, -1);
                flag_exit_sdk = bundle.getBoolean(ZhiChiConstant.FLAG_EXIT_SDK, false);
                mConfig = (SobotLeaveMsgConfig) bundle.getSerializable(StPostMsgPresenter.INTENT_KEY_CONFIG);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(getResLayoutId("sobot_fragment_post_msg"), container, false);
        initView(mRootView);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initData();
        super.onActivityCreated(savedInstanceState);
    }

    protected void initView(View rootView) {
        sobot_post_msg_pic = mRootView.findViewById(getResId("sobot_post_msg_pic"));
        sobot_ll_content_img = (LinearLayout) rootView.findViewById(getResId("sobot_ll_content_img"));
        sobot_post_phone = (EditText) rootView.findViewById(getResId("sobot_post_phone"));
        sobot_post_email = (EditText) rootView.findViewById(getResId("sobot_post_email"));
        sobot_post_title = (EditText) rootView.findViewById(getResId("sobot_post_title"));
        sobot_frist_line = rootView.findViewById(getResId("sobot_frist_line"));
        sobot_post_title_line = rootView.findViewById(getResId("sobot_post_title_line"));
        sobot_post_question_line = rootView.findViewById(getResId("sobot_post_question_line"));
        sobot_post_customer_line = rootView.findViewById(getResId("sobot_post_customer_line"));
        sobot_post_title_sec_line = rootView.findViewById(getResId("sobot_post_title_sec_line"));
        sobot_post_question_sec_line = rootView.findViewById(getResId("sobot_post_question_sec_line"));
        sobot_post_customer_sec_line = rootView.findViewById(getResId("sobot_post_customer_sec_line"));
        sobot_phone_line = rootView.findViewById(getResId("sobot_phone_line"));
        sobot_et_content = (EditText) rootView.findViewById(getResId("sobot_post_et_content"));
        sobot_tv_post_msg = (TextView) rootView.findViewById(getResId("sobot_tv_post_msg"));
        sobot_post_email_lable = (TextView) rootView.findViewById(getResId("sobot_post_email_lable"));
        sobot_post_phone_lable = (TextView) rootView.findViewById(getResId("sobot_post_phone_lable"));
        sobot_post_title_lable = (TextView) rootView.findViewById(getResId("sobot_post_title_lable"));
        sobot_post_lable = (TextView) rootView.findViewById(getResId("sobot_post_question_lable"));
        String test = getResString("sobot_problem_types") + "<font color='#f9676f'>&nbsp;*</font>";
        sobot_post_lable.setText(Html.fromHtml(test));
        sobot_post_question_lable = (TextView) rootView.findViewById(getResId("sobot_post_question_lable"));
        sobot_post_question_type = (TextView) rootView.findViewById(getResId("sobot_post_question_type"));
        sobot_post_msg_layout = (LinearLayout) rootView.findViewById(getResId("sobot_post_msg_layout"));
        sobot_post_customer_field = (LinearLayout) rootView.findViewById(getResId("sobot_post_customer_field"));
        sobot_post_email_rl = (RelativeLayout) rootView.findViewById(getResId("sobot_post_email_rl"));
        email_hint_input_label = (TextView) rootView.findViewById(getResId("sobot_post_email_lable_hint"));
        email_hint_input_label.setHint(ResourceUtils.getResString(getSobotActivity(), "sobot_please_input"));
        title_hint_input_lable = (TextView) rootView.findViewById(getResId("sobot_post_title_lable_hint"));
        title_hint_input_lable.setHint(ResourceUtils.getResString(getSobotActivity(), "sobot_please_input"));
        sobot_post_phone_rl = (RelativeLayout) rootView.findViewById(getResId("sobot_post_phone_rl"));
        phone_hint_input_label = (TextView) rootView.findViewById(getResId("sobot_post_phone_lable_hint"));
        phone_hint_input_label.setHint(ResourceUtils.getResString(getSobotActivity(), "sobot_please_input"));
        sobot_post_title_rl = (RelativeLayout) rootView.findViewById(getResId("sobot_post_title_rl"));
        sobot_post_question_ll = (LinearLayout) rootView.findViewById(getResId("sobot_post_question_ll"));
        sobot_post_question_ll.setOnClickListener(this);
        ll_problem_description_title = rootView.findViewById(R.id.ll_problem_description_title);
        sobot_tv_problem_description = rootView.findViewById(R.id.sobot_tv_problem_description);
        tv_problem_description_required = rootView.findViewById(R.id.tv_problem_description_required);
        sobot_tv_problem_description.setText(R.string.sobot_problem_description);
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
        sobot_btn_submit = (Button) rootView.findViewById(getResId("sobot_btn_submit"));
        sobot_btn_submit.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_btn_submit_text"));
        sobot_btn_submit.setOnClickListener(this);
        sobot_post_customer_field.setVisibility(View.GONE);

        if (mConfig.isEmailShowFlag()) {
            sobot_post_email_rl.setVisibility(View.VISIBLE);
            sobot_post_email_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sobot_post_email.setVisibility(View.VISIBLE);
                    sobot_post_email_lable.setTextColor(ContextCompat.getColor(getSobotActivity(), ResourceUtils.getResColorId(getSobotActivity(), "sobot_common_gray2")));
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
                        sobot_post_email_lable.setTextColor(ContextCompat.getColor(getSobotActivity(), ResourceUtils.getResColorId(getSobotActivity(), "sobot_common_gray1")));
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
                    sobot_post_phone_lable.setTextColor(ContextCompat.getColor(getSobotActivity(), ResourceUtils.getResColorId(getSobotActivity(), "sobot_common_gray2")));
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
                        sobot_post_phone_lable.setTextColor(ContextCompat.getColor(getSobotActivity(), ResourceUtils.getResColorId(getSobotActivity(), "sobot_common_gray1")));
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
                    sobot_post_title_lable.setTextColor(ContextCompat.getColor(getSobotActivity(), ResourceUtils.getResColorId(getSobotActivity(), "sobot_common_gray2")));
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
                        sobot_post_title_lable.setTextColor(ContextCompat.getColor(getSobotActivity(), ResourceUtils.getResColorId(getSobotActivity(), "sobot_common_gray1")));
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
            sobot_post_phone_lable.setTextColor(ContextCompat.getColor(getSobotActivity(), ResourceUtils.getResColorId(getSobotActivity(), "sobot_common_gray2")));
            sobot_post_phone_lable.setTextSize(12);
        }
        String sobotUserEmail = (information != null ? information.getUser_emails() : "");
        if (mConfig.isEmailShowFlag() && !TextUtils.isEmpty(sobotUserEmail)) {
            sobot_post_email.setVisibility(View.VISIBLE);
            sobot_post_email.setText(sobotUserEmail);
            email_hint_input_label.setVisibility(View.GONE);
            sobot_post_email_lable.setTextColor(ContextCompat.getColor(getSobotActivity(), ResourceUtils.getResColorId(getSobotActivity(), "sobot_common_gray2")));
            sobot_post_email_lable.setTextSize(12);
        }

        if (mConfig.isEnclosureShowFlag()) {
            sobot_post_msg_pic.setVisibility(View.VISIBLE);
            initPicListView();
        } else {
            sobot_post_msg_pic.setVisibility(View.GONE);
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

    protected void initData() {
        information = (Information) SharedPreferencesUtil.getObject(getSobotActivity(), "sobot_last_current_info");
        zhiChiApi.getTemplateFieldsInfo(SobotPostMsgFragment.this, uid, mConfig.getTemplateId(), new StringResultCallBack<SobotLeaveMsgParamModel>() {

            @Override
            public void onSuccess(SobotLeaveMsgParamModel result) {
                if (result != null) {
                    if (result.getField() != null && result.getField().size() != 0) {
                        sobot_post_customer_line.setVisibility(View.VISIBLE);
                        sobot_post_customer_sec_line.setVisibility(View.VISIBLE);
                        mFields = result.getField();
                        StCusFieldPresenter.addWorkOrderCusFields(getSobotActivity(), SobotPostMsgFragment.this.getSobotActivity(), mFields, sobot_post_customer_field, SobotPostMsgFragment.this);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                try {
                    showHint(ResourceUtils.getResString(getSobotActivity(), "sobot_try_again"));
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
        String checkCustomFieldResult = StCusFieldPresenter.formatCusFieldVal(getSobotActivity(), sobot_post_customer_field, mFields);
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
            CustomToast.makeText(getSobotActivity(), content, 1000).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == sobot_post_question_ll) {
            if (mConfig.getType() != null && mConfig.getType().size() != 0) {
                Intent intent = new Intent(getSobotActivity(), SobotPostCategoryActivity.class);
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

    public void onBackPressed() {
        if (flag_exit_type == 1 || flag_exit_type == 2) {
            finishPageOrSDK(false);
        } else {
            finishPageOrSDK(flag_exit_sdk);
        }
    }

    private void postMsg(String userPhone, String userEamil, String title) {

        PostParamModel postParam = new PostParamModel();
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
        postParam.setTicketFrom("4");
        if (information != null && information.getLeaveParamsExtends() != null) {
            postParam.setParamsExtends(SobotJsonUtils.toJson(information.getLeaveParamsExtends()));
        }
        if (sobot_post_question_type.getTag() != null && !TextUtils.isEmpty(sobot_post_question_type.getTag().toString())) {
            postParam.setTicketTypeId(sobot_post_question_type.getTag().toString());
        }
        if (mFields == null) {
            mFields = new ArrayList<>();
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

        zhiChiApi.postMsg(SobotPostMsgFragment.this, postParam, new StringResultCallBack<CommonModelBase>() {
            @Override
            public void onSuccess(CommonModelBase base) {
                try {
                    if (Integer.parseInt(base.getStatus()) == 0) {
                        showHint(base.getMsg());
                    } else if (Integer.parseInt(base.getStatus()) == 1) {
                        if (getSobotActivity() == null) {
                            return;
                        }
                        KeyboardUtil.hideKeyboard(getSobotActivity().getCurrentFocus());
                        Intent intent = new Intent();
                        intent.setAction(SobotPostMsgActivity.SOBOT_ACTION_SHOW_COMPLETED_VIEW);
                        CommonUtils.sendLocalBroadcast(getSobotActivity(), intent);
                    }
                } catch (Exception e) {
                    showHint(base.getMsg());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                try {
                    showHint(ResourceUtils.getResString(getSobotActivity(), "sobot_try_again"));
                } catch (Exception e1) {

                }
            }
        });
    }

    /**
     * 返回键监听
     */
    public void onBackPress() {
        if (getView() != null) {
            KeyboardUtil.hideKeyboard(((ViewGroup) getView()).getFocusedChild());
        }

        if (flag_exit_type == 1 || flag_exit_type == 2) {
            finishPageOrSDK(false);
        } else {
            finishPageOrSDK(flag_exit_sdk);
        }
    }

    private void finishPageOrSDK(boolean flag) {
        if (!flag) {
            if (getSobotActivity() == null) {
                Activity tempActivity = MyApplication.getInstance().getLastActivity();
                if (tempActivity != null && tempActivity instanceof SobotPostMsgActivity) {
                    tempActivity.finish();
                    tempActivity.overridePendingTransition(ResourceUtils.getIdByName(tempActivity
                                    , "anim", "sobot_push_right_in"),
                            ResourceUtils.getIdByName(tempActivity, "anim", "sobot_push_right_out"));
                }
            } else {
                getSobotActivity().finish();
                getSobotActivity().overridePendingTransition(ResourceUtils.getIdByName(getSobotActivity()
                                , "anim", "sobot_push_right_in"),
                        ResourceUtils.getIdByName(getSobotActivity(), "anim", "sobot_push_right_out"));
            }
        } else {
            MyApplication.getInstance().exit();
        }
    }

    @Override
    public void onDestroy() {
        SobotDialogUtils.stopProgressDialog(getSobotActivity());
        super.onDestroy();
    }

    /**
     * 初始化图片选择的控件
     */
    private void initPicListView() {
        adapter = new SobotPicListAdapter(getSobotActivity(), pic_list);
        sobot_post_msg_pic.setAdapter(adapter);
//        sobot_post_msg_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                KeyboardUtil.hideKeyboard(view);
//                if (pic_list.get(position).getViewState() == 0) {
//                    menuWindow = new SobotSelectPicDialog(getSobotActivity(), itemsOnClick);
//                    menuWindow.show();
//                } else {
//                    LogUtils.i("当前选择图片位置：" + position);
//                    Intent intent = new Intent(getSobotActivity(), SobotPhotoListActivity.class);
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
                        menuWindow = new SobotSelectPicDialog(getSobotActivity(), itemsOnClick);
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
                                    Intent intent = SobotVideoActivity.newIntent(getSobotActivity(), cacheFile);
                                    getSobotActivity().startActivity(intent);
                                    return;
                                }
                                if (SobotOption.imagePreviewListener != null) {
                                    //如果返回true,拦截;false 不拦截
                                    boolean isIntercept = SobotOption.imagePreviewListener.onPreviewImage(getSobotActivity(), TextUtils.isEmpty(result.getFileLocalPath()) ? result.getFileUrl() : result.getFileLocalPath());
                                    if (isIntercept) {
                                        return;
                                    }
                                }
                                Intent intent = new Intent(getSobotActivity(), SobotPhotoActivity.class);
                                intent.putExtra("imageUrL", TextUtils.isEmpty(result.getFileLocalPath()) ? result.getFileUrl() : result.getFileLocalPath());
                                getSobotActivity().startActivity(intent);
                            }
                        }
                        break;
                    case SobotPicListAdapter.DEL:
                        String popMsg = ResourceUtils.getResString(getSobotActivity(), "sobot_do_you_delete_picture");
                        if (adapter == null || adapter.getPicList() == null)
                            return;
                        ZhiChiUploadAppFileModelResult delResult = adapter.getPicList().get(position);
                        if (delResult != null) {
                            if (!TextUtils.isEmpty(delResult.getFileLocalPath()) && MediaFileUtils.isVideoFileType(delResult.getFileLocalPath())) {
                                popMsg = ResourceUtils.getResString(getSobotActivity(), "sobot_do_you_delete_video");
                            }
                        }
                        if (seleteMenuWindow != null) {
                            seleteMenuWindow.dismiss();
                            seleteMenuWindow = null;
                        }
                        if (seleteMenuWindow == null) {
                            seleteMenuWindow = new SobotDeleteWorkOrderDialog(getSobotActivity(), popMsg, new View.OnClickListener() {
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
            HtmlTools.getInstance(getSobotActivity().getApplicationContext()).setRichText(sobot_tv_post_msg, information.getLeaveMsgGuideContent().replace("<p>", "").replace("</p>", "<br/>").replace("\n", "<br/>"),
                    ResourceUtils.getIdByName(getSobotActivity(), "color", "sobot_postMsg_url_color"));
        } else {
            if (!TextUtils.isEmpty(mConfig.getMsgTxt())) {
                mConfig.setMsgTxt(mConfig.getMsgTxt().replace("<p>", "").replace("</p>", "<br/>").replace("\n", "<br/>"));
                HtmlTools.getInstance(getSobotActivity().getApplicationContext()).setRichText(sobot_tv_post_msg, mConfig.getMsgTxt(),
                        ResourceUtils.getIdByName(getSobotActivity(), "color", "sobot_postMsg_url_color"));
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
            zhiChiApi.fileUploadForPostMsg(SobotPostMsgFragment.this, mConfig.getCompanyId(), uid, filePath, new ResultCallBack<ZhiChiMessage>() {
                @Override
                public void onSuccess(ZhiChiMessage zhiChiMessage) {
                    SobotDialogUtils.stopProgressDialog(getSobotActivity());
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
                    SobotDialogUtils.stopProgressDialog(getSobotActivity());
                    showHint(TextUtils.isEmpty(des) ? ResourceUtils.getResString(getSobotActivity(), "sobot_net_work_err") : des);

                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {

                }
            });
        }

        @Override
        public void onError() {
            SobotDialogUtils.stopProgressDialog(getSobotActivity());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
                if (data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage == null) {
                        selectedImage = ImageUtils.getUri(data, getSobotActivity());
                    }
                    String path = ImageUtils.getPath(getSobotActivity(), selectedImage);
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
                                SobotDialogUtils.startProgressDialog(getSobotActivity());
//                            ChatUtils.sendPicByFilePath(getSobotActivity(),path,sendFileListener,false);
                                String fName = MD5Util.encode(path);
                                String filePath = null;
                                try {
                                    filePath = FileUtil.saveImageFile(getSobotActivity(), selectedImage, fName + FileUtil.getFileEndWith(path), path);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastUtil.showToast(getSobotActivity(), ResourceUtils.getResString(getSobotActivity(), "sobot_pic_type_error"));
                                    return;
                                }
                                sendFileListener.onSuccess(filePath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            SobotDialogUtils.startProgressDialog(getSobotActivity());
                            ChatUtils.sendPicByUriPost(getSobotActivity(), selectedImage, sendFileListener, false);
                        }
                    } else {
                        showHint(getResString("sobot_did_not_get_picture_path"));
                    }
                } else {
                    showHint(getResString("sobot_did_not_get_picture_path"));
                }
            } else if (requestCode == ZhiChiConstant.REQUEST_CODE_makePictureFromCamera) {
                if (cameraFile != null && cameraFile.exists()) {
                    SobotDialogUtils.startProgressDialog(getSobotActivity());
                    ChatUtils.sendPicByFilePath(getSobotActivity(), cameraFile.getAbsolutePath(), sendFileListener, true);
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
                        SobotDialogUtils.startProgressDialog(getSobotActivity());
                        sendFileListener.onSuccess(videoFile.getAbsolutePath());
                    } else {
                        showHint(getResString("sobot_pic_select_again"));
                    }
                } else {
                    File tmpPic = new File(SobotCameraActivity.getSelectedImage(data));
                    if (tmpPic.exists()) {
                        cameraFile = tmpPic;
                        SobotDialogUtils.startProgressDialog(getSobotActivity());
                        ChatUtils.sendPicByFilePath(getSobotActivity(), tmpPic.getAbsolutePath(), sendFileListener, true);
                    } else {
                        showHint(getResString("sobot_pic_select_again"));
                    }
                }
            }
        }
        StCusFieldPresenter.onStCusFieldActivityResult(getSobotActivity(), data, mFields, sobot_post_customer_field);
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
                            sobot_post_question_lable.setTextColor(ContextCompat.getColor(getSobotActivity(), ResourceUtils.getResColorId(getSobotActivity(), "sobot_common_gray2")));
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
                StCusFieldPresenter.openTimePicker(getSobotActivity(), view, fieldType);
                break;
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_SPINNER_TYPE:
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_RADIO_TYPE:
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE:
                StCusFieldPresenter.startSobotCusFieldActivity(getSobotActivity(), SobotPostMsgFragment.this, cusField);
                break;
            case ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CASCADE_TYPE:
                if (cusField != null && cusField.getCusFieldDataInfoList() != null && cusField.getCusFieldDataInfoList().size() > 0) {
                    Intent intent = new Intent(getSobotActivity(), SobotPostCascadeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("cusField", cusField);
                    bundle.putSerializable("fieldId", cusField.getCusFieldConfig().getFieldId());
                    intent.putExtra("bundle", bundle);
                    startActivityForResult(intent, ZhiChiConstant.work_order_list_display_type_category);
                }
                break;
            default:
                break;
        }
    }
}
