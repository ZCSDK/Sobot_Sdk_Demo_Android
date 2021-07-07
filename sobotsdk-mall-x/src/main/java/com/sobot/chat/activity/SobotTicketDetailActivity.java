package com.sobot.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.ZCSobotConstant;
import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.SobotTicketDetailAdapter;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotUserTicketEvaluate;
import com.sobot.chat.api.model.SobotUserTicketInfo;
import com.sobot.chat.api.model.StUserDealTicketInfo;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.widget.dialog.SobotReplyActivity;
import com.sobot.chat.widget.dialog.SobotTicketEvaluateActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SobotTicketDetailActivity extends SobotBaseActivity implements View.OnClickListener {
    public static final String INTENT_KEY_UID = "intent_key_uid";
    public static final String INTENT_KEY_COMPANYID = "intent_key_companyid";
    public static final String INTENT_KEY_TICKET_INFO = "intent_key_ticket_info";
    private static final int REQUEST_REPLY_CODE = 0x1001;

    private String mUid = "";
    private String mCompanyId = "";
    private SobotUserTicketInfo mTicketInfo;
    private int infoFlag;
    private Information information;

    private List<Object> mList = new ArrayList<>();
    private ListView mListView;
    private SobotTicketDetailAdapter mAdapter;

    private LinearLayout sobot_evaluate_ll;
    private LinearLayout sobot_reply_ll;
    private TextView sobot_evaluate_tv;
    private TextView sobot_reply_tv;

    private SobotUserTicketEvaluate mEvaluate;

    //进入回复界面弹窗界面 把 上次回复的临时内容传过去
    private String replyTempContent;
    private ArrayList<ZhiChiUploadAppFileModelResult> pic_list = new ArrayList<>();


    /**
     * @param context 应用程序上下文
     * @return
     */
    public static Intent newIntent(Context context, String companyId, String uid, SobotUserTicketInfo ticketInfo) {
        Intent intent = new Intent(context, SobotTicketDetailActivity.class);
        intent.putExtra(INTENT_KEY_UID, uid);
        intent.putExtra(INTENT_KEY_COMPANYID, companyId);
        intent.putExtra(INTENT_KEY_TICKET_INFO, ticketInfo);
        return intent;
    }

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_ticket_detail");
    }

    protected void initBundleData(Bundle savedInstanceState) {
        if (getIntent() != null) {
            mUid = getIntent().getStringExtra(INTENT_KEY_UID);
            mCompanyId = getIntent().getStringExtra(INTENT_KEY_COMPANYID);
            mTicketInfo = (SobotUserTicketInfo) getIntent().getSerializableExtra(INTENT_KEY_TICKET_INFO);
            if (mTicketInfo != null) {
                infoFlag = mTicketInfo.getFlag();//保留原始状态
            }
        }
    }

    @Override
    protected void initView() {
        showLeftMenu(getResDrawableId("sobot_btn_back_selector"), "", true);
        getLeftMenu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List ticketIds = (List) SharedPreferencesUtil.getObject(SobotTicketDetailActivity.this, "showBackEvaluateTicketIds");
                //已完成留言详情界面：返回时是否弹出服务评价窗口(只会第一次返回弹，下次返回不会再弹)
                if (information != null && information.isShowLeaveDetailBackEvaluate() && sobot_evaluate_ll.getVisibility() == View.VISIBLE) {
                    if (ticketIds != null && ticketIds.contains(mTicketInfo.getTicketId())) {
                        finish();
                    } else {
                        if (ticketIds == null) {
                            ticketIds = new ArrayList();
                        }
                        ticketIds.add(mTicketInfo.getTicketId());
                        SharedPreferencesUtil.saveObject(SobotTicketDetailActivity.this, "showBackEvaluateTicketIds", ticketIds);
                        Intent intent = new Intent(SobotTicketDetailActivity.this, SobotTicketEvaluateActivity.class);
                        intent.putExtra("sobotUserTicketEvaluate", mEvaluate);
                        startActivityForResult(intent, ZCSobotConstant.EXTRA_TICKET_EVALUATE_REQUEST_FINISH_CODE);
                    }
                } else {
                    finish();
                }
            }
        });
        setTitle(getResString("sobot_message_details"));
        mListView = (ListView) findViewById(getResId("sobot_listview"));
        sobot_evaluate_ll = (LinearLayout) findViewById(getResId("sobot_evaluate_ll"));
        sobot_reply_ll = (LinearLayout) findViewById(getResId("sobot_reply_ll"));
        sobot_evaluate_tv = (TextView) findViewById(getResId("sobot_evaluate_tv"));
        sobot_evaluate_tv.setText(ResourceUtils.getResString(this,"sobot_str_bottom_satisfaction"));
        sobot_reply_tv = (TextView) findViewById(getResId("sobot_reply_tv"));
        sobot_reply_tv.setText(ResourceUtils.getResString(this,"sobot_reply"));
        sobot_reply_ll.setOnClickListener(this);
        sobot_evaluate_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == sobot_evaluate_ll && mEvaluate != null) {
                    Intent intent = new Intent(SobotTicketDetailActivity.this, SobotTicketEvaluateActivity.class);
                    intent.putExtra("sobotUserTicketEvaluate", mEvaluate);
                    startActivityForResult(intent, ZCSobotConstant.EXTRA_TICKET_EVALUATE_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    protected void initData() {
        information = (Information) SharedPreferencesUtil.getObject(SobotTicketDetailActivity.this, "sobot_last_current_info");
        sobot_evaluate_ll.setVisibility(View.GONE);
        sobot_reply_ll.setVisibility(View.GONE);
        if (mTicketInfo == null) {
            return;
        }

        zhiChiApi.getUserDealTicketInfoList(SobotTicketDetailActivity.this, mUid, mCompanyId, mTicketInfo.getTicketId(), new StringResultCallBack<List<StUserDealTicketInfo>>() {

            @Override
            public void onSuccess(List<StUserDealTicketInfo> datas) {
                //
                zhiChiApi.updateUserTicketReplyInfo(SobotTicketDetailActivity.this, mCompanyId, information.getPartnerid(), mTicketInfo.getTicketId());
                if (datas != null && datas.size() > 0) {
                    mList.clear();
                    for (StUserDealTicketInfo info : datas) {
                        if (info.getFlag() == 1) {//创建
                            mTicketInfo.setFileList(info.getFileList());
                            mTicketInfo.setContent(info.getContent());
                            if (StringUtils.isEmpty(mTicketInfo.getTimeStr())) {
                                mTicketInfo.setTimeStr(info.getTimeStr());
                            }
                            break;
                        }
                    }
                    mList.add(mTicketInfo);
                    mList.addAll(datas);

                    for (StUserDealTicketInfo dealTicketInfo : datas) {
//                        StUserDealTicketInfo dealTicketInfo = datas.get(0);
                        if (dealTicketInfo.getFlag() == 3 && mTicketInfo.getFlag() != 3) {//有结束标志
                            mTicketInfo.setFlag(3);
                        }

                        if (mTicketInfo.getFlag() != 3 && mTicketInfo.getFlag() < dealTicketInfo.getFlag()) {//不是结束
                            mTicketInfo.setFlag(dealTicketInfo.getFlag());
                        }
                        if (dealTicketInfo.getFlag() == 3 && dealTicketInfo.getEvaluate() != null) {
                            mList.add(dealTicketInfo.getEvaluate());
                            mEvaluate = dealTicketInfo.getEvaluate();
                            if (mEvaluate.isOpen()) {
                                if (mEvaluate.isEvalution()) {
                                    //已评价
                                    sobot_evaluate_ll.setVisibility(View.GONE);
                                } else {
                                    sobot_evaluate_ll.setVisibility(View.VISIBLE);
                                    break;

                                }
                            } else {
                                sobot_evaluate_ll.setVisibility(View.GONE);
                            }

                        }
                    }
                    if (mAdapter == null) {
                        mAdapter = new SobotTicketDetailAdapter(SobotTicketDetailActivity.this, SobotTicketDetailActivity.this, mList);
                        mListView.setAdapter(mAdapter);
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }

                    if (!SobotApi.getSwitchMarkStatus(MarkConfig.LEAVE_COMPLETE_CAN_REPLY) && mTicketInfo.getFlag() == 3) {
                        sobot_reply_ll.setVisibility(View.GONE);
                    } else {
                        sobot_reply_ll.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ToastUtil.showToast(SobotTicketDetailActivity.this, des);
            }
        });
    }

    public void submitEvaluate(final int score, final String remark) {
        zhiChiApi.addTicketSatisfactionScoreInfo(SobotTicketDetailActivity.this, mUid, mCompanyId, mTicketInfo.getTicketId(), score, remark, new StringResultCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                CustomToast.makeText(SobotTicketDetailActivity.this, ResourceUtils.getResString(SobotTicketDetailActivity.this, "sobot_leavemsg_success_tip"), 1000, ResourceUtils.getDrawableId(SobotTicketDetailActivity.this, "sobot_iv_login_right")).show();
                sobot_evaluate_ll.setVisibility(View.GONE);
                for (int i = 0; i < mList.size(); i++) {
                    Object obj = mList.get(i);
                    if (obj instanceof StUserDealTicketInfo) {
                        StUserDealTicketInfo data = (StUserDealTicketInfo) mList.get(i);
                        if (data.getFlag() == 3 && data.getEvaluate() != null) {
                            SobotUserTicketEvaluate evaluate = data.getEvaluate();
                            evaluate.setScore(score);
                            evaluate.setRemark(remark);
                            evaluate.setEvalution(true);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                removeTicketId();
            }

            @Override
            public void onFailure(Exception e, String des) {
                ToastUtil.showToast(getApplicationContext(), des);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_reply_ll) {
            //回复
            Intent intent = new Intent(SobotTicketDetailActivity.this, SobotReplyActivity.class);
            intent.putExtra("uid", mUid);
            intent.putExtra("companyId", mCompanyId);
            intent.putExtra("ticketInfo", mTicketInfo);
            intent.putExtra("picTempList", (Serializable) pic_list);
            intent.putExtra("replyTempContent", replyTempContent);
            startActivityForResult(intent, REQUEST_REPLY_CODE);
        }
    }


    @Override
    public void onBackPressed() {//返回
        //已完成留言详情界面：返回时是否弹出服务评价窗口(只会第一次返回弹，下次返回不会再弹)
        List ticketIds = (List) SharedPreferencesUtil.getObject(SobotTicketDetailActivity.this, "showBackEvaluateTicketIds");
        //已完成留言详情界面：返回时是否弹出服务评价窗口(只会第一次返回弹，下次返回不会再弹)
        if (information != null && information.isShowLeaveDetailBackEvaluate() && sobot_evaluate_ll.getVisibility() == View.VISIBLE) {
            if (ticketIds != null && ticketIds.contains(mTicketInfo.getTicketId())) {
            } else {
                if (ticketIds == null) {
                    ticketIds = new ArrayList();
                }
                ticketIds.add(mTicketInfo.getTicketId());
                SharedPreferencesUtil.saveObject(SobotTicketDetailActivity.this, "showBackEvaluateTicketIds", ticketIds);
                Intent intent = new Intent(SobotTicketDetailActivity.this, SobotTicketEvaluateActivity.class);
                intent.putExtra("sobotUserTicketEvaluate", mEvaluate);
                startActivityForResult(intent, ZCSobotConstant.EXTRA_TICKET_EVALUATE_REQUEST_FINISH_CODE);
                return;
            }
        }
        if (mTicketInfo != null && infoFlag != mTicketInfo.getFlag()) {
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_REPLY_CODE) {
                boolean isTemp = false;
                if (data != null) {
                    isTemp = data.getBooleanExtra("isTemp", false);
                    //回复临时保存数据
                    replyTempContent = data.getStringExtra("replyTempContent");
                    pic_list = (ArrayList<ZhiChiUploadAppFileModelResult>) data.getSerializableExtra("picTempList");
                }
                if (!isTemp) {
                    initData();
                }
            }
            if (requestCode == ZCSobotConstant.EXTRA_TICKET_EVALUATE_REQUEST_CODE) {
                submitEvaluate(data.getIntExtra("score", 0), data.getStringExtra("content"));
            }

            if (requestCode == ZCSobotConstant.EXTRA_TICKET_EVALUATE_REQUEST_FINISH_CODE) {
                final int score = data.getIntExtra("score", 0);
                final String remark = data.getStringExtra("content");
                zhiChiApi.addTicketSatisfactionScoreInfo(SobotTicketDetailActivity.this, mUid, mCompanyId, mTicketInfo.getTicketId(), score, remark, new StringResultCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        sobot_evaluate_ll.setVisibility(View.GONE);
                        for (int i = 0; i < mList.size(); i++) {
                            Object obj = mList.get(i);
                            if (obj instanceof StUserDealTicketInfo) {
                                StUserDealTicketInfo data = (StUserDealTicketInfo) mList.get(i);
                                if (data.getFlag() == 3 && data.getEvaluate() != null) {
                                    SobotUserTicketEvaluate evaluate = data.getEvaluate();
                                    evaluate.setScore(score);
                                    evaluate.setRemark(remark);
                                    evaluate.setEvalution(true);
                                    mAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                        removeTicketId();
                        ToastUtil.showCustomToastWithListenr(SobotTicketDetailActivity.this, ResourceUtils.getResString(SobotTicketDetailActivity.this, "sobot_leavemsg_success_tip"), 1000, new ToastUtil.OnAfterShowListener() {
                            @Override
                            public void doAfter() {
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        ToastUtil.showToast(getApplicationContext(), des);
                    }
                });
            }

        }
    }

    //评价成功后移除工单id
    public void removeTicketId() {
        List ticketIds = (List) SharedPreferencesUtil.getObject(SobotTicketDetailActivity.this, "showBackEvaluateTicketIds");
        if (mTicketInfo != null && ticketIds != null)
            ticketIds.remove(mTicketInfo.getTicketId());
        SharedPreferencesUtil.saveObject(SobotTicketDetailActivity.this, "showBackEvaluateTicketIds", ticketIds);

    }
}