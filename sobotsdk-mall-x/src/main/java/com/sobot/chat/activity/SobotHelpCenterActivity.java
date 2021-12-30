package com.sobot.chat.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.base.SobotBaseHelpCenterActivity;
import com.sobot.chat.adapter.SobotHelpCenterAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.StCategoryModel;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.SobotFunctionType;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;

import java.util.List;

/**
 * 帮助中心
 */
public class SobotHelpCenterActivity extends SobotBaseHelpCenterActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //空态页面
    private View mEmptyView;
    private View mBottomBtn;
    private TextView tv_sobot_layout_online_service;
    private TextView tv_sobot_layout_online_tel;
    private GridView mGridView;
    private SobotHelpCenterAdapter mAdapter;
    private TextView tvNoData;
    private TextView tvNoDataDescribe;
    private TextView tvOnlineService;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_help_center");
    }

    @Override
    protected void initView() {
        setTitle(getResString("sobot_help_center_title"));
        showLeftMenu(getResDrawableId("sobot_btn_back_grey_selector"), "", true);
        mEmptyView = findViewById(getResId("ll_empty_view"));
        mBottomBtn = findViewById(getResId("ll_bottom"));
        tv_sobot_layout_online_service = findViewById(getResId("tv_sobot_layout_online_service"));
        tv_sobot_layout_online_tel = findViewById(getResId("tv_sobot_layout_online_tel"));
        mGridView = findViewById(getResId("sobot_gv"));
        tvNoData = findViewById(getResId("tv_sobot_help_center_no_data"));
        tvNoData.setText(ResourceUtils.getResString(this, "sobot_help_center_no_data"));
        tvNoDataDescribe = findViewById(getResId("tv_sobot_help_center_no_data_describe"));
        tvNoDataDescribe.setText(ResourceUtils.getResString(this, "sobot_help_center_no_data_describe"));
        tvOnlineService = findViewById(getResId("tv_sobot_layout_online_service"));
        tvOnlineService.setText(ResourceUtils.getResString(this, "sobot_help_center_online_service"));
        tv_sobot_layout_online_service.setOnClickListener(this);
        tv_sobot_layout_online_tel.setOnClickListener(this);
        mGridView.setOnItemClickListener(this);
        if (mInfo != null && !TextUtils.isEmpty(mInfo.getHelpCenterTelTitle()) && !TextUtils.isEmpty(mInfo.getHelpCenterTel())) {
            tv_sobot_layout_online_tel.setVisibility(View.VISIBLE);
            tv_sobot_layout_online_tel.setText(mInfo.getHelpCenterTelTitle());
        } else {
            tv_sobot_layout_online_tel.setVisibility(View.GONE);
        }
        displayInNotch(mGridView);
    }

    @Override
    protected void initData() {
        ZhiChiApi api = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        api.getCategoryList(SobotHelpCenterActivity.this, mInfo.getApp_key(), new StringResultCallBack<List<StCategoryModel>>() {
            @Override
            public void onSuccess(List<StCategoryModel> datas) {
                if (datas != null && datas.size() > 0) {
                    mEmptyView.setVisibility(View.GONE);
                    mGridView.setVisibility(View.VISIBLE);
                    if (mAdapter == null) {
                        mAdapter = new SobotHelpCenterAdapter(getApplicationContext(), datas);
                        mGridView.setAdapter(mAdapter);
                    } else {
                        List<StCategoryModel> list = mAdapter.getDatas();
                        list.clear();
                        list.addAll(datas);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ToastUtil.showToast(getApplicationContext(), des);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tv_sobot_layout_online_service) {
            SobotApi.startSobotChat(getApplicationContext(), mInfo);
        }
        if (v == tv_sobot_layout_online_tel) {
            if (!TextUtils.isEmpty(mInfo.getHelpCenterTel())) {
                if (SobotOption.functionClickListener != null) {
                     SobotOption.functionClickListener.onClickFunction(getSobotBaseActivity(), SobotFunctionType.ZC_PhoneCustomerService);
                }
                ChatUtils.callUp(mInfo.getHelpCenterTel(), getSobotBaseActivity());
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<StCategoryModel> datas = mAdapter.getDatas();
        StCategoryModel data = datas.get(position);
        Intent intent = SobotProblemCategoryActivity.newIntent(getApplicationContext(), mInfo, data);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SobotOption.functionClickListener != null) {
            SobotOption.functionClickListener.onClickFunction(getSobotBaseActivity(), SobotFunctionType.ZC_CloseHelpCenter);
        }
    }
}