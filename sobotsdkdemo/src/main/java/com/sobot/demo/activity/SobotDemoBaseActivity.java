package com.sobot.demo.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.demo.R;

public abstract class SobotDemoBaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResId());
        initView();
        View view_root = findViewById(R.id.view_root);
        if (view_root != null) {
            int targetSdkVersion = CommonUtils.getTargetSdkVersion(this);
//            LogUtils.d(" app targetSdkVersion版本号：" + targetSdkVersion);
            if (Build.VERSION.SDK_INT >= 35 && targetSdkVersion >= 35) {
                view_root.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        int topInset = insets.getInsets(WindowInsets.Type.systemBars()).top;
                        int bottomInset = insets.getInsets(WindowInsets.Type.systemBars()).bottom;
                        LogUtils.d("状态栏高度：" + topInset + " 底部导航：" + bottomInset);
                        //每个页面底部添加高度，避让底部导航栏
                        view_root.setPadding(0, 0, 0, bottomInset);
                        //每个页面底部添加高度，避让头部状态栏
                        View sobot_demo_titlebar=findViewById(R.id.sobot_demo_titlebar);
                        if (sobot_demo_titlebar != null  && sobot_demo_titlebar.getLayoutParams() != null) {
                            sobot_demo_titlebar.getLayoutParams().height = ScreenUtils.dip2px(SobotDemoBaseActivity.this,44) + topInset;
                            if (findViewById(R.id.view_empty) != null && findViewById(R.id.view_empty).getLayoutParams() != null) {
                                findViewById(R.id.view_empty).getLayoutParams().height = topInset;
                            }
                        }
                        return insets;
                    }
                });
            }
        }
    }

    protected abstract int getContentViewResId();
    protected abstract void initView();
}
