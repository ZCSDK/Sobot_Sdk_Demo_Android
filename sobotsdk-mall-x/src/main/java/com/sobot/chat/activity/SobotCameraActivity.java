package com.sobot.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.camera.StCameraView;
import com.sobot.chat.camera.listener.StCameraListener;
import com.sobot.chat.camera.listener.StClickListener;
import com.sobot.chat.camera.listener.StErrorListener;
import com.sobot.chat.camera.util.FileUtil;
import com.sobot.chat.listener.PermissionListenerImpl;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotPathManager;

/**
 * @author Created by jinxl on 2018/12/3.
 */
public class SobotCameraActivity extends SobotBaseActivity {
    private static final String EXTRA_ACTION_TYPE = "EXTRA_ACTION_TYPE";
    private static final String EXTRA_IMAGE_FILE_PATH = "EXTRA_IMAGE_FILE_PATH";
    private static final String EXTRA_VIDEO_FILE_PATH = "EXTRA_VIDEO_FILE_PATH";
    public static final int RESULT_CODE = 103;

    public static final int ACTION_TYPE_PHOTO = 0;
    public static final int ACTION_TYPE_VIDEO = 1;

    private StCameraView jCameraView;

    /**
     * @param context 应用程序上下文
     * @return
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, SobotCameraActivity.class);
    }

    /**
     * 获取快照
     *
     * @param intent
     * @return
     */
    public static String getSelectedImage(Intent intent) {
        return intent.getStringExtra(EXTRA_IMAGE_FILE_PATH);
    }

    /**
     * 获取视频路径
     *
     * @param intent
     * @return
     */
    public static String getSelectedVideo(Intent intent) {
        return intent.getStringExtra(EXTRA_VIDEO_FILE_PATH);
    }

    /**
     * 获取动作类型
     *
     * @param intent
     * @return
     */
    public static int getActionType(Intent intent) {
        return intent.getIntExtra(EXTRA_ACTION_TYPE, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setContentView(ResourceUtils.getIdByName(SobotCameraActivity.this, "layout", "sobot_activity_camera"));
        //MyApplication.getInstance().addActivity(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else if (Build.VERSION.SDK_INT >= 16) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (null != jCameraView) {
            jCameraView.setErrorLisenter(null);
        }
        MyApplication.getInstance().deleteActivity(this);
        super.onDestroy();
    }

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_camera");
    }

    @Override
    protected void initView() {
        jCameraView = (StCameraView) findViewById(ResourceUtils.getIdByName(SobotCameraActivity.this, "id", "sobot_cameraview"));
        //设置视频保存路径
        jCameraView.setSaveVideoPath(SobotPathManager.getInstance().getVideoDir());
        jCameraView.setFeatures(StCameraView.BUTTON_STATE_BOTH);
        jCameraView.setTip(ResourceUtils.getResString(SobotCameraActivity.this, "sobot_tap_hold_camera"));
        jCameraView.setMediaQuality(StCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new StErrorListener() {
            @Override
            public void onError() {
                //错误监听
                finish();
            }

            @Override
            public void AudioPermissionError() {
                permissionListener = new PermissionListenerImpl() {
                };
                if (!isHasPermission(2, 3)) {
                }
            }


            @Override
            public boolean checkAutoPremission() {
                return isHasPermission(2,2);
            }

            @Override
            public boolean checkCameraPremission() {
                return isHasPermission(3, 3);
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new StCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ACTION_TYPE, ACTION_TYPE_PHOTO);
                if (bitmap != null) {
                    String path = FileUtil.saveBitmap(100, bitmap);
                    intent.putExtra(EXTRA_IMAGE_FILE_PATH, path);
                }
                setResult(RESULT_CODE, intent);
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ACTION_TYPE, ACTION_TYPE_VIDEO);
                if (firstFrame != null) {
                    String path = FileUtil.saveBitmap(80, firstFrame);
                    intent.putExtra(EXTRA_IMAGE_FILE_PATH, path);
                }
                intent.putExtra(EXTRA_VIDEO_FILE_PATH, url);
                setResult(RESULT_CODE, intent);
                finish();
            }
        });

        jCameraView.setLeftClickListener(new StClickListener() {
            @Override
            public void onClick() {
                SobotCameraActivity.this.finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

}
