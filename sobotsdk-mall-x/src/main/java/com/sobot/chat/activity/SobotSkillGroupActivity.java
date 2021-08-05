package com.sobot.chat.activity;

import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotDialogBaseActivity;
import com.sobot.chat.adapter.SobotSikllAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.ZhiChiGroup;
import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.presenter.StPostMsgPresenter;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.attachment.SpaceItemDecoration;
import com.sobot.chat.widget.horizontalgridpage.SobotRecyclerCallBack;

import java.util.ArrayList;
import java.util.List;

public class SobotSkillGroupActivity extends SobotDialogBaseActivity {

    private LinearLayout sobot_btn_cancle;
    private RecyclerView sobot_rcy_skill;

    private TextView sobot_tv_title;
    private SobotSikllAdapter sobotSikllAdapter;
    private List<ZhiChiGroupBase> list_skill = new ArrayList<ZhiChiGroupBase>();
    private boolean flag_exit_sdk;
    private String uid = null;
    private String companyId = null;
    private String customerId = null;
    private String appkey = null;
    private String msgTmp = null;
    private String msgTxt = null;
    private int transferType;
    private ZhiChiApi zhiChiApi;
    private int mType = -1;
    private int msgFlag = 0;

    private StPostMsgPresenter mPressenter;


    @Override
    protected int getContentViewResId() {
        return ResourceUtils.getResLayoutId(this, "sobot_activity_skill_group");
    }

    @Override
    protected void initView() {
        sobot_tv_title = (TextView) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_tv_title"));
        mPressenter = StPostMsgPresenter.newInstance(SobotSkillGroupActivity.this, SobotSkillGroupActivity.this);
        sobot_btn_cancle = (LinearLayout) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_btn_cancle"));
        sobot_rcy_skill = (RecyclerView) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_rcy_skill"));

        sobotSikllAdapter = new SobotSikllAdapter(this, list_skill, msgFlag, new SobotRecyclerCallBack() {
            @Override
            public void onItemClickListener(View view, int position) {
                if (list_skill != null && list_skill.size() > 0) {
                    if ("true".equals(list_skill.get(position).isOnline())) {
                        if (!TextUtils.isEmpty(list_skill.get(position).getGroupName())) {
                            Intent intent = new Intent();
                            intent.putExtra("groupIndex", position);
                            intent.putExtra("transferType", transferType);
                            setResult(ZhiChiConstant.REQUEST_COCE_TO_GRROUP, intent);
                            finish();
                        }
                    } else {
                        if (msgFlag == ZhiChiConstant.sobot_msg_flag_open) {
                            Intent intent = new Intent();
                            intent.putExtra("toLeaveMsg", true);
                            setResult(ZhiChiConstant.REQUEST_COCE_TO_GRROUP, intent);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
        sobot_rcy_skill.setAdapter(sobotSikllAdapter);

        sobot_btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishPageOrSDK();
            }
        });

        displayInNotch(this, sobot_rcy_skill);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() <= 0) {
                finishPageOrSDK();
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        mPressenter.destory();
        OkHttpUtils.getInstance().cancelTag(SobotSkillGroupActivity.this);
        MyApplication.getInstance().deleteActivity(this);
        super.onDestroy();
    }


    protected void initData() {
        if (getIntent() != null) {
            uid = getIntent().getStringExtra("uid");
            companyId = getIntent().getStringExtra("companyId");
            customerId = getIntent().getStringExtra("customerId");
            appkey = getIntent().getStringExtra("appkey");
            flag_exit_sdk = getIntent().getBooleanExtra(
                    ZhiChiConstant.FLAG_EXIT_SDK, false);
            mType = getIntent().getIntExtra("type", -1);
            msgTmp = getIntent().getStringExtra("msgTmp");
            msgTxt = getIntent().getStringExtra("msgTxt");
            msgFlag = getIntent().getIntExtra("msgFlag", 0);
            transferType = getIntent().getIntExtra("transferType", 0);
        }
        zhiChiApi = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        zhiChiApi.getGroupList(SobotSkillGroupActivity.this, appkey, uid, new StringResultCallBack<ZhiChiGroup>() {
            @Override
            public void onSuccess(ZhiChiGroup zhiChiGroup) {
                list_skill = zhiChiGroup.getData();
                if (list_skill != null && list_skill.size() > 0 && sobotSikllAdapter != null) {
                    if (list_skill.get(0).getGroupStyle() == 1) {
                        GridLayoutManager gridlayoutmanager = new GridLayoutManager(SobotSkillGroupActivity.this, 4);
                        sobot_rcy_skill.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(SobotSkillGroupActivity.this, 10), ScreenUtils.dip2px(SobotSkillGroupActivity.this, 10), 0, SpaceItemDecoration.GRIDLAYOUT));
                        sobot_rcy_skill.setLayoutManager(gridlayoutmanager);
                    } else if (list_skill.get(0).getGroupStyle() == 2) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SobotSkillGroupActivity.this);
                        sobot_rcy_skill.setLayoutManager(linearLayoutManager);
                    } else {
                        GridLayoutManager manager = new GridLayoutManager(SobotSkillGroupActivity.this, 2);
                        sobot_rcy_skill.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(SobotSkillGroupActivity.this, 10), ScreenUtils.dip2px(SobotSkillGroupActivity.this, 10), 0, SpaceItemDecoration.GRIDLAYOUT));
                        sobot_rcy_skill.setLayoutManager(manager);
                    }

                    sobotSikllAdapter.setList(list_skill);
                    sobotSikllAdapter.setMsgFlag(msgFlag);
                    sobotSikllAdapter.notifyDataSetChanged();
                    if (TextUtils.isEmpty(list_skill.get(0).getGroupGuideDoc())) {
                        sobot_tv_title.setText(ResourceUtils.getResString(SobotSkillGroupActivity.this, "sobot_switch_robot_title_2"));
                    } else {
                        sobot_tv_title.setText(list_skill.get(0).getGroupGuideDoc());
                    }
                } else {
                    sobot_tv_title.setText(ResourceUtils.getResString(SobotSkillGroupActivity.this, "sobot_switch_robot_title_2"));
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                sobot_tv_title.setText(ResourceUtils.getResString(SobotSkillGroupActivity.this, "sobot_switch_robot_title_2"));
            }
        });
    }

    private void finishPageOrSDK() {
        int initType = SharedPreferencesUtil.getIntData(
                getApplicationContext(), appkey + "_" + ZhiChiConstant.initType, -1);
        if (initType == ZhiChiConstant.type_custom_only) {
            finish();
            sendCloseIntent(1);
        } else {
            if (!flag_exit_sdk) {
                finish();
                sendCloseIntent(2);
            } else {
                MyApplication.getInstance().exit();
            }
        }
    }

    private void sendCloseIntent(int type) {
        Intent intent = new Intent();
        if (type == 1) {
            intent.setAction(ZhiChiConstants.sobot_close_now_clear_cache);
        } else {
            intent.setAction(ZhiChiConstants.sobot_click_cancle);
        }
        CommonUtils.sendLocalBroadcast(getApplicationContext(), intent);
    }

    @Override
    public void onBackPressed() {
        finishPageOrSDK();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            finish();
        }
    }
}