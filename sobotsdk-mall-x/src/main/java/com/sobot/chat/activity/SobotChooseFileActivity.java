package com.sobot.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.SobotFilesAdapter;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.FileOpenHelper;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SystemUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SobotChooseFileActivity extends SobotBaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int READ_REQUEST_CODE = 42;

    private ListView sobot_lv_files;
    private TextView sobot_tv_send;
    private TextView sobot_tv_total;

    private File mRootDir = Environment.getExternalStorageDirectory();
    private File mCurrentDir;
    private SobotFilesAdapter mAdapter;
    private List<File> mDatas = new ArrayList<>();

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_choose_file");
    }

    @Override
    protected void initView() {
        if (!(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q||Environment.isExternalStorageLegacy())){
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
//                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//            startActivityForResult(intent, READ_REQUEST_CODE);

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent,READ_REQUEST_CODE);

        }
        setTitle(getResString("sobot_internal_memory"));
        showLeftMenu(getResDrawableId("sobot_btn_back_selector"), "", true);
        sobot_lv_files = (ListView) findViewById(getResId("sobot_lv_files"));
        sobot_tv_send = (TextView) findViewById(getResId("sobot_tv_send"));
        sobot_tv_send.setText(ResourceUtils.getResString(SobotChooseFileActivity.this,"sobot_button_send"));
        sobot_tv_total = (TextView) findViewById(getResId("sobot_tv_total"));
        sobot_tv_send.setOnClickListener(this);
        displayInNotch(sobot_lv_files);
    }

    @Override
    protected void initData() {
        if (!checkStoragePermission() || !CommonUtils.isExitsSdcard()) {
            return;
        }
        mCurrentDir = mRootDir;

        showCurrentFiles(mCurrentDir);

        sobot_lv_files.setOnItemClickListener(this);
    }

    private void showCurrentFiles(File dir) {
        if (dir.isDirectory()) {
            File[] childFiles = getChildFiles(dir);
            showData(childFiles);
        }
    }

    private void showData(File[] files) {
        mDatas.clear();
        if (files != null) {
            mDatas.addAll(Arrays.asList(files));
        }
        Collections.sort(mDatas, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o2.getName().compareTo(o1.getName());
            }
        });
        if (mAdapter == null) {
            mAdapter = new SobotFilesAdapter(SobotChooseFileActivity.this, mDatas);
            sobot_lv_files.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private File[] getChildFiles(File dir) {
        if (dir.isDirectory()) {
            return dir.listFiles();
        }
        return null;
    }

    @Override
    protected void onLeftMenuClick(View view) {
        goback();
    }

    @Override
    public void onBackPressed() {
        goback();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            File file = mDatas.get(position);
            if (file != null) {
                if (file.isDirectory()) {
                    mCurrentDir = file;
                    showCurrentFiles(file);
                } else {
                    if (file.length() > 50 * 1024 * 1024) {
                        ToastUtil.showToast(SobotChooseFileActivity.this, getResString("sobot_file_upload_failed"));
                        return;
                    }
                    //不能上传可执行文件 （.exe、.sys、 .com、.bat、.dll、.sh、.py）
                    String fileName = file.getName().toLowerCase();
                    if (FileOpenHelper.checkEndsWithInStringArray(fileName, SobotChooseFileActivity.this, "sobot_fileEndingAll")) {
                        return;
                    }
                    if (mAdapter != null) {
                        String totalSize;
                        if (mAdapter.isCheckedFile(file)) {
                            mAdapter.setCheckedFile(null);
                            totalSize = "0B";
                            sobot_tv_send.setEnabled(false);
                        } else {
                            mAdapter.setCheckedFile(file);
                            totalSize = Formatter.formatFileSize(this, file.length());
                            sobot_tv_send.setEnabled(true);
                        }
                        mAdapter.notifyDataSetChanged();
                        sobot_tv_total.setText(getResString("sobot_files_selected")+"："+totalSize);
                    }
                }
            }
        } catch (Exception e) {
            //ignor
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_send) {
            File checkedFile = mAdapter.getCheckedFile();
            if (checkedFile != null) {
                Intent data = new Intent();
                data.putExtra(ZhiChiConstant.SOBOT_INTENT_DATA_SELECTED_FILE, checkedFile);
                setResult(ZhiChiConstant.REQUEST_COCE_TO_CHOOSE_FILE, data);
                finish();
            }
        }
    }

    private void goback() {
        if (!mRootDir.equals(mCurrentDir)) {
            mCurrentDir = mCurrentDir.getParentFile();
            showCurrentFiles(mCurrentDir);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent resultData) {
        //使用resultdata.getdata ( )提取该URI
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                if (uri!=null){
                    Intent data = new Intent();
                    data.setData(uri);
                    setResult(ZhiChiConstant.REQUEST_COCE_TO_CHOOSE_FILE, data);
                    finish();
                    return;
                }

            }
        }
        finish();
    }
}