package com.sobot.demo;

import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.lzy.widget.AlphaIndicator;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */

public class SobotDemoNewActivity extends AppCompatActivity {

    private SobotUnReadMsgReceiver unReadMsgReceiver;//获取未读消息数的广播接收者

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_demo_new_activity);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        LogUtils.isDebug = true;
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        AlphaIndicator alphaIndicator = (AlphaIndicator) findViewById(R.id.alphaIndicator);
        alphaIndicator.setViewPager(viewPager);
        regReceiver();
    }

    private class MainAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();

        public MainAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(new SobotDemoWelcomeFragment());
            fragments.add(new SobotDemoNewSettingFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private void regReceiver(){
        IntentFilter filter = new IntentFilter();

        if (unReadMsgReceiver == null){
            unReadMsgReceiver = new SobotUnReadMsgReceiver();
        }
        filter.addAction(ZhiChiConstant.sobot_unreadCountBrocast);
        registerReceiver(unReadMsgReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        try {
            if (unReadMsgReceiver != null){
                unregisterReceiver(unReadMsgReceiver);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }
}