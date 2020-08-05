package com.sobot.demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.sobot.chat.utils.LogUtils;
import com.sobot.demo.R;
import com.sobot.demo.SobotDemoNewActivity;
import com.sobot.demo.SobotDemoSettingActivity;
import com.sobot.demo.permission.ZCPermission;

import static android.os.Build.VERSION_CODES.M;

/**
 * 闪屏界面
 *
 * @author Eric
 */
public class SplashActivity extends AppCompatActivity {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler();
    private int splashMin = 2000;//在闪屏界面等待的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_splash_activity);
        goActivity(SobotDemoNewActivity.class, splashMin, true);
        if (android.os.Build.VERSION.SDK_INT >= M) {
            ZCPermission.with(this)
                    .permissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request(new ZCPermission.ZCPermissionCallback() {

                        @Override
                        public void permissionSuccess(int requestCode) {
                            LogUtils.i("permissionSuccess:  授予权限成功");
                        }

                        @Override
                        public void permissionFail(int requestCode) {
                            LogUtils.i( "permissionFail: 授予权限失败");
                        }

                    });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void goActivity(final Class clz, long delMin, final boolean isSuccess) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, clz);
                intent.putExtra("isSuccess", isSuccess);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, delMin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}