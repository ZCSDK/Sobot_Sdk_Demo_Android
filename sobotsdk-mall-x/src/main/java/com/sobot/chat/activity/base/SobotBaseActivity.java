package com.sobot.chat.activity.base;

import static com.sobot.chat.fragment.SobotBaseFragment.REQUEST_CODE_CAMERA;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.R;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.ZCSobotConstant;
import com.sobot.chat.activity.SobotCameraActivity;
import com.sobot.chat.activity.SobotSelectPicAndVideoActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.SobotBaseUrl;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.HttpUtils;
import com.sobot.chat.core.channel.SobotMsgManager;
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
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.image.SobotRCImageView;
import com.sobot.chat.widget.statusbar.StatusBarUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;


/**
 * 基类
 */
public abstract class SobotBaseActivity extends AppCompatActivity {

    public ZhiChiApi zhiChiApi;

    protected File cameraFile;

    //权限回调
    public PermissionListener permissionListener;
    private int initMode;
    private View overlay;//权限用途提示蒙层
    private ViewGroup viewGroup;//根view content

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //修改国际化语言
        changeAppLanguage();
        super.onCreate(savedInstanceState);
        if (getSobotBaseContext() != null && getDelegate() != null) {
            //暗夜模式设置：默认跟随系统，可以根据设置切换
            int local_night_mode = SharedPreferencesUtil.getIntData(getSobotBaseContext(), ZCSobotConstant.LOCAL_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            if (local_night_mode != 0) {
                getDelegate().setLocalNightMode(local_night_mode); //切换模式
            }
        }
        initMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            if (!ZCSobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);//竖屏
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//横屏

            }
        }
        if (ZCSobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && ZCSobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH)) {
            // 支持显示到刘海区域
            NotchScreenManager.getInstance().setDisplayInNotch(this);
            // 设置Activity全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(getContentViewResId());
        try {
            String host = SharedPreferencesUtil.getStringData(getSobotBaseContext(), ZhiChiConstant.SOBOT_SAVE_HOST_AFTER_INITSDK, SobotBaseUrl.getApi_Host());
            if (!host.equals(SobotBaseUrl.getApi_Host())) {
                SobotBaseUrl.setApi_Host(host);
            }
        } catch (Exception e) {
        }
        try {
            View decorView = getWindow().getDecorView();
            ViewCompat.setOnApplyWindowInsetsListener(decorView, new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
                    int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                    View rootView = findViewById(R.id.view_root);
                    int targetSdkVersion = CommonUtils.getTargetSdkVersion(getSobotBaseActivity());
                    if (rootView != null && Build.VERSION.SDK_INT >= 35 && targetSdkVersion >= 35) {
                        //android 15 api 35 全屏沉侵式 底部避让
                        rootView.setPadding(0, 0, 0, bottomInset);
                    }
                    LogUtils.d("状态栏高度: " + statusBarHeight);
                    SharedPreferencesUtil.saveIntData(getSobotBaseActivity(), "SobotStatusBarHeight", statusBarHeight);
                    setUpToolBar();
                    return insets;
                }
            });
        } catch (Exception e) {
            setUpToolBar();
        }
        zhiChiApi = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        MyApplication.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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

        //左上角返回按钮水滴屏适配
        if (getLeftMenu() != null) {
            displayInNotch(getLeftMenu());
        }
    }

    public void displayInNotch(final View view) {
        if (ZCSobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && ZCSobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH) && view != null) {
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
        Locale language = (Locale) SharedPreferencesUtil.getObject(SobotBaseActivity.this, ZhiChiConstant.SOBOT_LANGUAGE);
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
        return getResources().getColor(R.color.sobot_status_bar_color);
    }

    protected void setUpToolBar() {
        View toolBar = getToolBar();
        if (toolBar == null) {
            return;
        }
        setToolBarDefBg();
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
        HttpUtils.getInstance().cancelTag(SobotBaseActivity.this);
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE:
                try {
                    //android 14 api 34 以上 部分权限直接打开“选择图片和视频界面”
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && grantResults.length > 1) {
                        boolean isAllGranted = true;
                        for (int i = 0; i < grantResults.length; i++) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                isAllGranted = false;
                            }
                        }
                        if (!isAllGranted) {
                            //如果android 14 有部分权限，直接启动“选择图片和视频界面”
                            for (int i = 0; i < grantResults.length; i++) {
                                if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                    int selectType;
                                    if (Arrays.asList(permissions).contains(Manifest.permission.READ_MEDIA_IMAGES) && Arrays.asList(permissions).contains(Manifest.permission.READ_MEDIA_VIDEO)) {
                                        selectType = 3;//部分视频和图片
                                    } else if (Arrays.asList(permissions).contains(Manifest.permission.READ_MEDIA_VIDEO)) {
                                        selectType = 2;//部分视频
                                    } else {
                                        selectType = 1;//部分图片
                                    }
                                    openSelectPic(selectType);
                                    return;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < grantResults.length; i++) {
                        //判断权限的结果，如果有被拒绝，就return
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                showPerssionSettingUi();
                                return;
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.RECORD_AUDIO)) {
                                showPerssionSettingUi();
                                return;
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.CAMERA)) {
                                showPerssionSettingUi();
                                return;
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_MEDIA_IMAGES)) {
                                showPerssionSettingUi();
                                return;
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_MEDIA_VIDEO)) {
                                showPerssionSettingUi();
                                return;
                            }
                            if (permissions[i] != null && !permissions[i].equals(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                                //不处理 READ_MEDIA_VISUAL_USER_SELECTED权限，如果是Android13 全部允许权限时，这个权限返回的还是-1
                                return;
                            }
                        }
                    }
                    if (permissionListener != null) {
                        permissionListener.onPermissionSuccessListener();
                    }
                    hidePerssionUi();
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 检测是否没有对应的权限，没有权限显示提示蒙层
     *
     * @param type                1：文件 2：麦克风 3：相机
     * @param checkPermissionType 0：图片权限 1：视频权限，2：音频权限，3，所有细分的权限， android 13 使用
     * @return true :有权限 false:没有权限
     */
    public boolean isHasPermission(int type, int checkPermissionType) {
        boolean isHasPermission = false;
        if (type == 1) {
            int result = checkStoragePermission(checkPermissionType);
            if (result == 0) {
                isHasPermission = true;
            } else if (result == 2) {
                //部分权限
                isHasPermission = false;
                if (checkPermissionType == 3) {
                    //1:部分图片 2:部分视频 3:部分视频和图片
                    openSelectPic(3);
                } else if (checkPermissionType == 1) {
                    openSelectPic(2);
                } else {
                    openSelectPic(1);
                }
            } else {
                isHasPermission = false;
                if (checkPermissionType == 3) {
                    showPerssionUi(1);
                } else {
                    showPerssionUi(0);
                }
                //申请权限
                requestStoragePermission(checkPermissionType);
            }
        } else if (type == 2) {
            isHasPermission = checkAudioPermission();
            if (!isHasPermission) {
                showPerssionUi(2);
            }
        } else if (type == 3) {
            isHasPermission = checkCameraPermission();
            if (!isHasPermission) {
                showPerssionUi(3);
            }
        }
        return isHasPermission;
    }

    /**
     * 显示权限蒙层
     *
     * @param type 0：照片和视频 1：文件 2：麦克风 3：相机
     */
    public void showPerssionUi(int type) {
        overlay = LayoutInflater.from(getSobotBaseActivity()).inflate(R.layout.sobot_layout_overlay, null);
        if (overlay != null) {
            FrameLayout fl_root = overlay.findViewById(R.id.fl_root);
            final LinearLayout ll_info = overlay.findViewById(R.id.ll_info);
            final LinearLayout ll_setting = overlay.findViewById(R.id.ll_setting);
            TextView tv_content = overlay.findViewById(R.id.tv_content);
            Button btn_left = overlay.findViewById(R.id.btn_left);
            Button btn_right = overlay.findViewById(R.id.btn_right);
            TextView tv_setting_title = overlay.findViewById(R.id.tv_setting_title);
            TextView tv_setting_content = overlay.findViewById(R.id.tv_setting_content);
            if (type == 0) {
                tv_content.setText("\"" + CommonUtils.getAppName(getSobotBaseActivity()) + "\" " + getResources().getString(R.string.sobot_album_permission_yongtu));
                tv_setting_title.setText(getResources().getString(R.string.sobot_please_open_album));
                tv_setting_content.setText(getResources().getString(R.string.sobot_use_album));
            } else if (type == 1) {
                tv_content.setText("\"" + CommonUtils.getAppName(getSobotBaseActivity()) + "\" " + getResources().getString(R.string.sobot_storage_permission_yongtu));
                tv_setting_title.setText(getResources().getString(R.string.sobot_please_open_storage));
                tv_setting_content.setText(getResources().getString(R.string.sobot_use_storage));
            } else if (type == 2) {
                tv_content.setText("\"" + CommonUtils.getAppName(getSobotBaseActivity()) + "\" " + getResources().getString(R.string.sobot_microphone_permission_yongtu));
                tv_setting_title.setText(getResources().getString(R.string.sobot_please_open_microphone));
                tv_setting_content.setText(getResources().getString(R.string.sobot_use_microphone));
            } else if (type == 3) {
                tv_content.setText("\"" + CommonUtils.getAppName(getSobotBaseActivity()) + "\" " + getResources().getString(R.string.sobot_camera_permission_yongtu));
                tv_setting_title.setText(getResources().getString(R.string.sobot_please_open_camera));
                tv_setting_content.setText(getResources().getString(R.string.sobot_use_camera));
            }
            viewGroup = getSobotBaseActivity().findViewById(android.R.id.content);
            viewGroup.addView(overlay);
            if (customDetailMsgEnabled()) {
                fl_root.setVisibility(View.GONE);
            } else {
                fl_root.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ll_setting.getVisibility() == View.GONE) {
                            ll_info.setVisibility(View.VISIBLE);
                        }
                    }
                }, 300);//延迟0.3s 是避免多次拒绝后ll_info 隐藏会出现闪一下的问题
            }
            overlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidePerssionUi();
                }
            });
            btn_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidePerssionUi();
                }
            });
            btn_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidePerssionUi();
                    Uri packageURI = Uri.parse("package:" + getSobotBaseActivity().getPackageName());
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                    startActivity(intent);
                }
            });
        }
    }

    //拒绝权限后显示 去设置UI
    public void showPerssionSettingUi() {
        String permissionTitle = "";
        if (overlay != null) {
            FrameLayout fl_root = overlay.findViewById(R.id.fl_root);
            fl_root.setVisibility(View.VISIBLE);
            LinearLayout ll_info = overlay.findViewById(R.id.ll_info);
            LinearLayout ll_setting = overlay.findViewById(R.id.ll_setting);
            TextView tv_content = overlay.findViewById(R.id.tv_content);
            ll_info.setVisibility(View.GONE);
            ll_setting.setVisibility(View.VISIBLE);
            if (tv_content != null && StringUtils.isNoEmpty(tv_content.getText().toString())) {
                permissionTitle = tv_content.getText().toString();
            }
        }
        if (permissionListener != null) {
            permissionListener.onPermissionErrorListener(getSobotBaseActivity(), permissionTitle);
        }
    }

    //移除权限提示蒙层
    public void hidePerssionUi() {
        if (overlay != null) {
            if (viewGroup == null) {
                viewGroup = findViewById(android.R.id.content);
            }
            viewGroup.removeView(overlay);
        }
    }


    /**
     * 申请存储权限
     *
     * @param checkType 0：图片权限 1：视频权限，3，所有细分的权限， android 13 使用
     */
    public void requestStoragePermission(int checkType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (checkType == 0) {
                    this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                } else if (checkType == 1) {
                    this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                } else {
                    this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                }
            } else {
                if (checkType == 0) {
                    this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                } else if (checkType == 1) {
                    this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                } else {
                    this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                }
            }
        } else {
            //申请READ_EXTERNAL_STORAGE权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
            }
        }
    }

    /**
     * 检查存储权限
     *
     * @param checkType 0：图片权限 1：视频权限，3，所有细分的权限， android 13 使用
     * @return int  0：有权限，1：没有权限，2:有部分权限
     */
    public int checkStoragePermission(int checkType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //android 13 api 33 以上
            if (checkType == 0 || checkType == 1) {
                //检测是否有图片权限
                if (ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_MEDIA_VIDEO)
                        == PackageManager.PERMISSION_GRANTED) {
                    //有权限
                    return 0;
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                        == PackageManager.PERMISSION_GRANTED) {
                    //android 14 有部分权限
                    return 2;
                } else {
                    //没有权限
                    return 1;
                }
            } else {
                //检测是否有图片权限
                if (ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_MEDIA_VIDEO)
                        == PackageManager.PERMISSION_GRANTED) {
                    //有权限
                    return 0;
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                        == PackageManager.PERMISSION_GRANTED) {
                    //android 14 有部分权限
                    return 2;
                } else {
                    //没有权限
                    return 1;
                }
            }
        } else {
            // android 13 api33 以前
            if (ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return 0; //有权限
            } else {
                return 1; //没有权限
            }
        }
    }


    /**
     * android 14 部分权限情况下，回显照片或者视频
     *
     * @param selectType 1:部分图片 2:部分视频 3:部分视频和图片
     */
    private void openSelectPic(int selectType) {
        //隐藏权限提示蒙层
        hidePerssionUi();
        Intent intent = new Intent(getSobotBaseActivity(), SobotSelectPicAndVideoActivity.class);
        intent.putExtra("selectType", selectType);
        startActivityForResult(intent, ZhiChiConstant.REQUEST_CODE_picture);
    }


    /**
     * 检查录音权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkAudioPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotBaseActivity().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                return false;
            }
        }
        return true;
    }


    /**
     * 检查相机权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotBaseActivity().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}
                        , ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                return false;
            }
        }
        return true;
    }


    /**
     * 通过照相上传图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            ToastUtil.showCustomToast(getSobotBaseActivity().getApplicationContext(), getResString("sobot_sdcard_does_not_exist"),
                    Toast.LENGTH_SHORT);
            return;
        }

        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                //如果有拍照所需的权限，跳转到拍照界面
                startActivityForResult(SobotCameraActivity.newIntent(getSobotBaseContext()), REQUEST_CODE_CAMERA);
            }
        };

        if (!isHasPermission(3, 3)) {
            return;
        }

        // 打开拍摄页面
        startActivityForResult(SobotCameraActivity.newIntent(getSobotBaseContext()), REQUEST_CODE_CAMERA);
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
        if (!isHasPermission(1, 0)) {
            return;
        }
        ChatUtils.openSelectPic(getSobotBaseActivity());
    }


    /**
     * 从图库获取视频
     */
    public void selectVedioFromLocal() {
        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                ChatUtils.openSelectVedio(getSobotBaseActivity());
            }
        };
        if (!isHasPermission(1, 1)) {
            return;
        }
        ChatUtils.openSelectVedio(getSobotBaseActivity());
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

    public Context getSobotBaseContext() {
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
        if (initMode != currentNightMode) {
            initMode = currentNightMode;
            recreate();
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


    /**
     * 是否支持权限弹密副标题自定义
     *
     * @return true：不弹权限说明蒙层；false 弹出蒙层
     */
    public boolean customDetailMsgEnabled() {
        String pkgName = "com.android.permissioncontroller";
        boolean enabled = false;
        try {
            if (getSobotBaseActivity() != null) {
                boolean isCheck = SharedPreferencesUtil.getBooleanData(getSobotBaseActivity(), ZCSobotConstant.IS_CHECK_AUTHORITY_SUBTITLE, false);
                if (isCheck) {
                    ApplicationInfo info = getSobotBaseActivity().getApplication().getPackageManager().getApplicationInfo(pkgName,
                            PackageManager.GET_META_DATA);
                    if ((info != null) && (info.metaData != null)) {
                        enabled = info.metaData.getBoolean("custom_detail_msg_enabled");
                    }
                }
            }
        } catch (Exception e) {
        }
        return enabled;
    }

    /**
     * 设置默认导航栏渐变色
     */
    public void setViewDefBgForStatusBar(View view) {
        if (view==null){
         return;
        }
        try {
            int[] colors = new int[]{getStatusBarColor(), getStatusBarColor()};
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setColors(colors); //添加颜色组
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);//设置线性渐变
            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);//设置渐变方向
            view.setBackground(gradientDrawable);
        } catch (Exception e) {
        }
    }

    /**
     * 设置默认导航栏渐变色
     */
    private void setToolBarDefBg() {
        try {
            int[] colors = new int[]{getStatusBarColor(), getStatusBarColor()};
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setColors(colors); //添加颜色组
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);//设置线性渐变
            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);//设置渐变方向
            getToolBar().setBackground(gradientDrawable);
            if (ZCSobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && ZCSobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH)) {
            } else {
                GradientDrawable aDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                StatusBarUtil.setColor(getSobotBaseActivity(), aDrawable);
                if (ZCSobotApi.getSwitchMarkStatus(MarkConfig.UPDATE_STARUS_TEXT_COLOR)) {
                    //状态栏文字白色
                    StatusBarUtil.setDarkMode(getSobotBaseActivity());
                } else {
                    //状态栏文字黑色
                    StatusBarUtil.setLightMode(getSobotBaseActivity());
                }
            }
        } catch (Exception e) {
        }
    }
}