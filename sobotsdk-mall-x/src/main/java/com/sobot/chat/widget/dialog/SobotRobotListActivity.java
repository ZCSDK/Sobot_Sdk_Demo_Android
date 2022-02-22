package com.sobot.chat.widget.dialog;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotDialogBaseActivity;
import com.sobot.chat.adapter.SobotRobotListAdapter;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.SobotRobot;
import com.sobot.chat.core.HttpUtils;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.network.http.callback.StringResultCallBack;

import java.util.List;

/**
 * 机器人列表界面的显示
 * Created by jinxl on 2017/6/12.
 */
public class SobotRobotListActivity extends SobotDialogBaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private final String CANCEL_TAG = SobotRobotListActivity.class.getSimpleName();
    private LinearLayout sobot_negativeButton;
    private GridView sobot_gv;
    private TextView sobot_tv_title;
    private String mUid;
    private String mRobotFlag;
    private SobotRobotListAdapter mListAdapter;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_layout_switch_robot");
    }


    @Override
    protected void initView() {
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        win.setAttributes(lp);
        sobot_negativeButton = (LinearLayout) findViewById(getResId("sobot_negativeButton"));
        sobot_tv_title= (TextView) findViewById(getResId("sobot_tv_title"));
        sobot_tv_title.setText(ResourceUtils.getResString(getContext(),"sobot_switch_robot_title"));
        sobot_gv = (GridView) findViewById(getResId("sobot_gv"));
        sobot_gv.setOnItemClickListener(this);
        sobot_negativeButton.setOnClickListener(this);
        displayInNotch(this,sobot_gv);
    }

    @Override
    protected void initData() {
        mUid = getIntent().getStringExtra("partnerid");
        mRobotFlag = getIntent().getStringExtra("robotFlag");
        ZhiChiApi zhiChiApi = SobotMsgManager.getInstance(getBaseContext()).getZhiChiApi();
        zhiChiApi.getRobotSwitchList(CANCEL_TAG, mUid, new StringResultCallBack<List<SobotRobot>>() {

            @Override
            public void onSuccess(List<SobotRobot> sobotRobots) {
                for (SobotRobot bean : sobotRobots) {
                    if (bean.getRobotFlag() != null && bean.getRobotFlag().equals(mRobotFlag)) {
                        bean.setSelected(true);
                        break;
                    }
                }
                if (mListAdapter == null) {
                    mListAdapter = new SobotRobotListAdapter(getBaseContext(), sobotRobots);
                    sobot_gv.setAdapter(mListAdapter);
                } else {
                    List<SobotRobot> datas = mListAdapter.getDatas();
                    datas.clear();
                    datas.addAll(sobotRobots);
                    mListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }


    @Override
    public void onDetachedFromWindow() {
        HttpUtils.getInstance().cancelTag(CANCEL_TAG);
        super.onDetachedFromWindow();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SobotRobot item = (SobotRobot) mListAdapter.getItem(position);
        if (item.getRobotFlag() != null && !item.getRobotFlag().equals(mRobotFlag)) {
            Intent intent = new Intent();
            intent.putExtra("sobotRobot", item);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_negativeButton) {
            finish();
        }
    }

}