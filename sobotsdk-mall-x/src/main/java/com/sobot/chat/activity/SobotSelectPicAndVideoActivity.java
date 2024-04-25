package com.sobot.chat.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.chat.R;
import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.SobotSelectPicAndVideoAdapter;
import com.sobot.chat.adapter.model.SobotAlbumFile;
import com.sobot.chat.adapter.model.SobotMediaReaderScanUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.attachment.SpaceItemDecoration;
import com.sobot.chat.widget.horizontalgridpage.SobotRecyclerCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * android 14 部分权限 允许后的回显界面
 */
public class SobotSelectPicAndVideoActivity extends SobotBaseActivity {

    private TextView tv_go_to_settring;
    private RecyclerView sobot_rcy;
    private Button sobot_btn_submit;
    private SobotSelectPicAndVideoAdapter picAdapter;
    private List<SobotAlbumFile> albumFileList;
    private int selectType = 1;


    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_select_pic_and_video;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (albumFileList == null) {
            albumFileList = new ArrayList<>();
        }
        albumFileList.clear();
        albumFileList = SobotMediaReaderScanUtils.getAllMedia(getSobotBaseActivity(), selectType);
        if (albumFileList == null) {
            albumFileList = new ArrayList<>();
        }
        albumFileList.add(new SobotAlbumFile());
        if (picAdapter != null) {
            picAdapter.setmSelectedPos(-1);
            picAdapter.updateList(albumFileList);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(selectType == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                //如果android 14 手机有永久权限 直接关闭当前界面
                finish();
            }
        }else if(selectType == 2){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(getSobotBaseActivity(), Manifest.permission.READ_MEDIA_VIDEO)
                    == PackageManager.PERMISSION_GRANTED) {
                //如果android 14 手机有永久权限 直接关闭当前界面
                finish();
            }
        }
    }

    @Override
    protected void initView() {
        tv_go_to_settring = findViewById(R.id.tv_go_to_settring);
        sobot_rcy = findViewById(R.id.sobot_rcy);
        sobot_btn_submit = findViewById(R.id.sobot_btn_submit);
        sobot_btn_submit.setAlpha(0.5f);
        sobot_btn_submit.setClickable(false);
        setTitle(getString(R.string.sobot_str_select_pic_video));
        displayInNotch(sobot_rcy);
        tv_go_to_settring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去设置打开权限
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                startActivity(intent);
            }
        });
    }


    protected void initData() {
        selectType = getIntent().getIntExtra("selectType", 1);
        albumFileList = SobotMediaReaderScanUtils.getAllMedia(getSobotBaseActivity(), selectType);
        if (albumFileList == null) {
            albumFileList = new ArrayList<>();
        }
        albumFileList.add(new SobotAlbumFile());
        picAdapter = new SobotSelectPicAndVideoAdapter(this, albumFileList, new SobotRecyclerCallBack() {
            @Override
            public void onItemClickListener(View view, int position) {

            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
        picAdapter.setClickListener(new SobotSelectPicAndVideoAdapter.myClickListener() {
            @Override
            public void onClickOtherListener() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (selectType == 3) {
                        requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                    } else if (selectType == 2) {
                        requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_ACTIVITY_CODE);
                    }
                }
            }

            @Override
            public void onCheckListener() {
                if (picAdapter.getmSelectedPos() > -1) {
                    sobot_btn_submit.setAlpha(1f);
                    sobot_btn_submit.setClickable(true);
                } else {
                    sobot_btn_submit.setAlpha(0.5f);
                    sobot_btn_submit.setClickable(false);
                }
            }
        });
        sobot_rcy.setAdapter(picAdapter);
        GridLayoutManager gridlayoutmanager = new GridLayoutManager(SobotSelectPicAndVideoActivity.this, 4);
        sobot_rcy.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(SobotSelectPicAndVideoActivity.this, 3), ScreenUtils.dip2px(SobotSelectPicAndVideoActivity.this, 1.5f), 0, SpaceItemDecoration.GRIDLAYOUT));
        sobot_rcy.setLayoutManager(gridlayoutmanager);
        sobot_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (albumFileList != null && albumFileList.size() > 0 && picAdapter != null && picAdapter.getmSelectedPos() > -1) {
                    SobotAlbumFile albumFile = albumFileList.get(picAdapter.getmSelectedPos());
                    Intent intent = new Intent();
                    intent.setData(albumFile.getUri());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


}