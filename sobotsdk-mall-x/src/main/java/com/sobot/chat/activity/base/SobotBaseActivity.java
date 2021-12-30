package com.sobot.chat.activity.base;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.listener.PermissionListener;
import com.sobot.chat.listener.PermissionListenerImpl;
import com.sobot.chat.notchlib.INotchScreen;
import com.sobot.chat.notchlib.NotchScreenManager;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.image.SobotRCImageView;
import com.sobot.chat.widget.statusbar.StatusBarCompat;

import java.io.File;
import java.util.Locale;


/**
 * @author Created by jinxl on 2018/1/30.
 */
public abstract class SobotBaseActivity extends FragmentActivity {

    public ZhiChiApi zhiChiApi;

    protected File cameraFile;

    //权限回调
    public PermissionListener permissionListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            if (!SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);//竖屏
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//横屏

            }
        }
        if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && SobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH)) {
            // 支持显示到刘海区域
            NotchScreenManager.getInstance().setDisplayInNotch(this);
            // 设置Activity全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(getContentViewResId());
        int sobot_status_bar_color = getStatusBarColor();
        if (sobot_status_bar_color != 0) {
            try {
                StatusBarCompat.setStatusBarColor(this, sobot_status_bar_color);
            } catch (Exception e) {
                //请传入正确的颜色值
            }
        }
        setUpToolBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        zhiChiApi = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        MyApplication.getInstance().addActivity(this);
        View toolBar = findViewById(getResId("sobot_layout_titlebar"));
        if (toolBar != null) {
            setUpToolBarLeftMenu();

            setUpToolBarRightMenu();
        }
        try {
            initBundleData(savedInstanceState);
            initView();
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //修改国际化语言
        changeAppLanguage();

        //左上角返回按钮水滴屏适配
        if (getLeftMenu() != null) {
            displayInNotch(getLeftMenu());
        }
    }

    public void displayInNotch(final View view) {
        if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && SobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH) && view != null) {
            // 获取刘海屏信息
            NotchScreenManager.getInstance().getNotchInfo(this, new INotchScreen.NotchScreenCallback() {
                @Override
                public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                    if (notchScreenInfo.hasNotch) {
                        for (Rect rect : notchScreenInfo.notchRects) {
                            if (view instanceof WebView && view.getParent() instanceof LinearLayout) {
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.rightMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                layoutParams.leftMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                view.setLayoutParams(layoutParams);
                            } else if (view instanceof WebView && view.getParent() instanceof RelativeLayout) {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.rightMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                layoutParams.leftMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                view.setLayoutParams(layoutParams);
                            } else {
                                view.setPadding((rect.right > 110 ? 110 : rect.right) + view.getPaddingLeft(), view.getPaddingTop(), (rect.right > 110 ? 110 : rect.right) + view.getPaddingRight(), view.getPaddingBottom());
                            }
                        }
                    }
                }
            });

        }
    }

    public void changeAppLanguage() {
        Locale language = (Locale) SharedPreferencesUtil.getObject(SobotBaseActivity.this, "SobotLanguage");
        try {
            if (language != null) {
                // 本地语言设置
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = new Configuration();
                conf.locale = language;
                res.updateConfiguration(conf, dm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void setUpToolBarRightMenu() {
        if (getRightMenu() != null) {
            //找到 Toolbar 的返回按钮,并且设置点击事件,点击关闭这个 Activity
            applyTitleUIConfig(getRightMenu());
            getRightMenu().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightMenuClick(v);
                }
            });
        }
    }

    protected void setUpToolBarLeftMenu() {
        if (getLeftMenu() != null) {
            //找到 Toolbar 的返回按钮,并且设置点击事件,点击关闭这个 Activity
            applyTitleUIConfig(getLeftMenu());
            getLeftMenu().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftMenuClick(v);
                }
            });
        }
    }

    protected int getStatusBarColor() {
        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_statusbar_BgColor) {
            return getResources().getColor(SobotUIConfig.sobot_statusbar_BgColor);
        }
        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_titleBgColor) {
            return getResources().getColor(SobotUIConfig.sobot_titleBgColor);
        }
        return getResColor("sobot_status_bar_color");
    }

    protected void setUpToolBar() {
        View toolBar = getToolBar();
        if (toolBar == null) {
            return;
        }

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_apicloud_titleBgColor) {
            toolBar.setBackgroundColor(getResources().getColor(SobotUIConfig.sobot_apicloud_titleBgColor));
        }

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_titleBgColor) {
            toolBar.setBackgroundColor(getResources().getColor(SobotUIConfig.sobot_titleBgColor));
        }

        int robot_current_themeImg = SharedPreferencesUtil.getIntData(this, "robot_current_themeImg", 0);
        if (robot_current_themeImg != 0) {
            toolBar.setBackgroundResource(robot_current_themeImg);
        }
    }

    protected View getToolBar() {
        return findViewById(getResId("sobot_layout_titlebar"));
    }

    protected TextView getLeftMenu() {
        return findViewById(getResId("sobot_tv_left"));
    }

    protected TextView getRightMenu() {
        return findViewById(getResId("sobot_tv_right"));
    }

    protected SobotRCImageView getAvatarMenu() {
        return findViewById(getResId("sobot_avatar_iv"));
    }

    protected View getTitleView() {
        return findViewById(getResId("sobot_text_title"));
    }


    /**
     * @param resourceId
     * @param textId
     * @param isShow
     */
    protected void showRightMenu(int resourceId, String textId, boolean isShow) {
        View tmpMenu = getRightMenu();
        if (tmpMenu == null || !(tmpMenu instanceof TextView)) {
            return;
        }
        TextView rightMenu = (TextView) tmpMenu;
        if (!TextUtils.isEmpty(textId)) {
            rightMenu.setText(textId);
        } else {
            rightMenu.setText("");
        }

        if (resourceId != 0) {
            Drawable img = getResources().getDrawable(resourceId);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            rightMenu.setCompoundDrawables(null, null, img, null);
        } else {
            rightMenu.setCompoundDrawables(null, null, null, null);
        }

        if (isShow) {
            rightMenu.setVisibility(View.VISIBLE);
        } else {
            rightMenu.setVisibility(View.GONE);
        }
    }

    /**
     * @param resourceId
     * @param textId
     * @param isShow
     */
    protected void showLeftMenu(int resourceId, String textId, boolean isShow) {
        View tmpMenu = getLeftMenu();
        if (tmpMenu == null || !(tmpMenu instanceof TextView)) {
            return;
        }
        TextView leftMenu = (TextView) tmpMenu;
        if (!TextUtils.isEmpty(textId)) {
            leftMenu.setText(textId);
        } else {
            leftMenu.setText("");
        }

        if (resourceId != 0) {
            Drawable img = getResources().getDrawable(resourceId);
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_titleTextColor) {
                img = ScreenUtils.tintDrawable(getApplicationContext(), img, SobotUIConfig.sobot_titleTextColor);
            }
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            leftMenu.setCompoundDrawables(img, null, null, null);
        } else {
            leftMenu.setCompoundDrawables(null, null, null, null);
        }

        if (isShow) {
            leftMenu.setVisibility(View.VISIBLE);
        } else {
            leftMenu.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(SobotBaseActivity.this);
        MyApplication.getInstance().deleteActivity(this);
        super.onDestroy();
    }

    /**
     * 导航栏左边点击事件
     *
     * @param view
     */
    protected void onLeftMenuClick(View view) {
        onBackPressed();
    }

    /**
     * 导航栏右边点击事件
     *
     * @param view
     */
    protected void onRightMenuClick(View view) {

    }

    public void setTitle(CharSequence title) {
        View tmpMenu = getTitleView();
        if (tmpMenu == null || !(tmpMenu instanceof TextView)) {
            return;
        }
        TextView tvTitle = (TextView) tmpMenu;
        tvTitle.setText(title);
        applyTitleUIConfig(tvTitle);
    }

    public void setTitle(int title) {
        getAvatarMenu().setVisibility(View.GONE);
        View tmpMenu = getTitleView();
        if (tmpMenu == null || !(tmpMenu instanceof TextView)) {
            return;
        }
        TextView tvTitle = (TextView) tmpMenu;
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    //返回布局id
    protected abstract int getContentViewResId();

    protected void initBundleData(Bundle savedInstanceState) {
    }

    protected abstract void initView();

    protected abstract void initData();

    public int getResId(String name) {
        return ResourceUtils.getIdByName(SobotBaseActivity.this, "id", name);
    }

    public int getResDrawableId(String name) {
        return ResourceUtils.getIdByName(SobotBaseActivity.this, "drawable", name);
    }

    public int getResLayoutId(String name) {
        return ResourceUtils.getIdByName(SobotBaseActivity.this, "layout", name);
    }

    public int getResColorId(String name) {
        return ResourceUtils.getIdByName(SobotBaseActivity.this, "color", name);
    }

    public int getResStringId(String name) {
        return ResourceUtils.getIdByName(SobotBaseActivity.this, "string", name);
    }

    public String getResString(String name) {
        return ResourceUtils.getResString(SobotBaseActivity.this, name);
//        return  ResourceUtils.getResString(SobotBaseActivity.this,name);
    }

    public int getResColor(String name) {
        int resColorId = getResColorId(name);
        if (resColorId != 0) {
            return getResources().getColor(resColorId);
        }
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE:
                try {
                    for (int i = 0; i < grantResults.length; i++) {
                        //判断权限的结果，如果有被拒绝，就return
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            String permissionTitle = "sobot_no_permission_text";
                            if (permissions[i] != null && permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                permissionTitle = "sobot_no_write_external_storage_permission";
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                    ToastUtil.showCustomLongToast(this, CommonUtils.getAppName(this) + getResString("sobot_want_use_your") + getResString("sobot_memory_card") + " , " + getResString("sobot_memory_card_yongtu"));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(this, getResString(permissionTitle));
                                    }
                                }
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                permissionTitle = "sobot_no_write_external_storage_permission";
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    ToastUtil.showCustomLongToast(this, CommonUtils.getAppName(this) + getResString("sobot_want_use_your") + getResString("sobot_memory_card") + " , " + getResString("sobot_memory_card_yongtu"));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(this, getResString(permissionTitle));
                                    }
                                }
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.RECORD_AUDIO) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                permissionTitle = "sobot_no_record_audio_permission";
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                                    ToastUtil.showCustomLongToast(this, CommonUtils.getAppName(this) + getResString("sobot_want_use_your") + getResString("sobot_microphone") + " , " + getResString("sobot_microphone_yongtu"));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(this, getResString(permissionTitle));
                                    }
                                }
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.CAMERA) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                permissionTitle = "sobot_no_camera_permission";
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                                    ToastUtil.showCustomLongToast(this, CommonUtils.getAppName(this) + getResString("sobot_want_use_your") + getResString("sobot_camera") + " , " + getResString("sobot_camera_yongtu"));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(this, getResString(permissionTitle));
                                    }
                                }
                            }
                            return;
                        }
                    }
                    if (permissionListener != null) {
                        permissionListener.onPermissionSuccessListener();
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * 检查存储权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 29 && CommonUtils.getTargetSdkVersion(getSobotBaseActivity().getApplicationContext()) >= 29) {
            //分区存储 从andrid10手机开始 TargetSdkVersion >= 29，不需要文件存储权限
            return true;
        }
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotBaseActivity().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
            if (ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请READ_EXTERNAL_STORAGE权限
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查拍照权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkStorageAndCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotBaseActivity()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                ChatUtils.openSelectPic(getSobotBaseActivity());
            }
        };
        if (!checkStoragePermission()) {
            return;
        }
        ChatUtils.openSelectPic(this);
    }

    private void applyTitleUIConfig(TextView view) {
        if (view == null) {
            return;
        }
        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_titleTextColor) {
            view.setTextColor(getResources().getColor(SobotUIConfig.sobot_titleTextColor));
        }
        //title是否粗体
        if (!SobotUIConfig.sobot_head_title_is_bold) {
            view.setTypeface(null, Typeface.NORMAL);
        }
    }


    public SobotBaseActivity getSobotBaseActivity() {
        return this;
    }

    public static boolean isCameraCanUse() {

        boolean canUse = false;
        Camera mCamera = null;

        try {
            mCamera = Camera.open(0);
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }

        if (mCamera != null) {
            mCamera.release();
            canUse = true;
        }

        return canUse;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                LogUtils.i("=====关闭夜间模式====");
                recreate();
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                LogUtils.i("=====开启夜间模式====");
                recreate();
                break;
            default:
                break;
        }
    }

    /**
     * 是否是全屏
     *
     * @return
     */
    protected boolean isFullScreen() {
        return (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }
}