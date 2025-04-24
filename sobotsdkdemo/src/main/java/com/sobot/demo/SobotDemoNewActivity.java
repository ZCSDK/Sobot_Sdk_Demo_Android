package com.sobot.demo;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */

public class SobotDemoNewActivity extends AppCompatActivity {

    private SobotUnReadMsgReceiver unReadMsgReceiver;//获取未读消息数的广播接收者
    private List<String> tabs = new ArrayList<>();//
    private List<Fragment> mFragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_demo_new_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mFragments = new ArrayList<>();
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        tabs.add("产品介绍");
        tabs.add("更多");
        mFragments.add(new SobotDemoWelcomeFragment());
        mFragments.add(new SobotDemoNewSettingFragment());

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchFragment(item.getItemId());
                return true;
            }
        });
        switchFragment(R.id.navigation_info);
        regReceiver();
    }

    private void switchFragment(int itemId) {
        Fragment fragment = null;
        if (itemId == R.id.navigation_info) {
           fragment= mFragments.get(0);
        } else if (itemId == R.id.navigation_more) {
            fragment= mFragments.get(1);
        }
        if (fragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
        }
    }

    private void regReceiver() {
        IntentFilter filter = new IntentFilter();

        if (unReadMsgReceiver == null) {
            unReadMsgReceiver = new SobotUnReadMsgReceiver();
        }
        filter.addAction(ZhiChiConstant.sobot_unreadCountBrocast);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(unReadMsgReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(unReadMsgReceiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (unReadMsgReceiver != null) {
                unregisterReceiver(unReadMsgReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}