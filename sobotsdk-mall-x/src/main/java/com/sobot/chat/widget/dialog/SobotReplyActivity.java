package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.SobotPhotoActivity;
import com.sobot.chat.activity.SobotVideoActivity;
import com.sobot.chat.activity.base.SobotDialogBaseActivity;
import com.sobot.chat.adapter.SobotPicListAdapter;
import com.sobot.chat.api.ResultCallBack;
import com.sobot.chat.api.model.SobotCacheFile;
import com.sobot.chat.api.model.SobotUserTicketInfo;
import com.sobot.chat.api.model.ZhiChiMessage;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.application.MyApplication;
import com.sobot.chat.camera.util.FileUtil;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.listener.PermissionListener;
import com.sobot.chat.listener.PermissionListenerImpl;
import com.sobot.chat.notchlib.INotchScreen;
import com.sobot.chat.notchlib.NotchScreenManager;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.FastClickUtils;
import com.sobot.chat.utils.ImageUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.MD5Util;
import com.sobot.chat.utils.MediaFileUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.attachment.FileTypeConfig;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SobotReplyActivity extends SobotDialogBaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {


    private TextView sobotTvTitle;
    private LinearLayout sobotNegativeButton;
    private EditText sobotReplyEdit;
    private GridView sobotReplyMsgPic;
    private Button sobotBtnSubmit;

    private ArrayList<ZhiChiUploadAppFileModelResult> pic_list = new ArrayList<>();
    private SobotPicListAdapter adapter;
    private SobotSelectPicDialog menuWindow;

    /**
     * 删除图片弹窗
     */
    protected SobotDeleteWorkOrderDialog seleteMenuWindow;

    //权限回调
    public PermissionListener permissionListener;
    protected File cameraFile;
    private String mUid = "";
    private String mCompanyId = "";
    private SobotUserTicketInfo mTicketInfo;


    @Override
    protected int getContentViewResId() {
        return ResourceUtils.getResLayoutId(this, "sobot_layout_dialog_reply");
    }

    @Override
    protected void initView() {

        sobotTvTitle = (TextView) findViewById(getResId("sobot_tv_title"));
        sobotTvTitle.setText(getResString("sobot_reply"));
        sobotNegativeButton = (LinearLayout) findViewById(getResId("sobot_negativeButton"));
        sobotReplyEdit = (EditText) findViewById(getResId("sobot_reply_edit"));
        sobotReplyEdit.setHint(ResourceUtils.getResString(SobotReplyActivity.this, "sobot_please_input_reply_hint"));
        sobotReplyMsgPic = (GridView) findViewById(getResId("sobot_reply_msg_pic"));
        sobotBtnSubmit = (Button) findViewById(getResId("sobot_btn_submit"));
        sobotBtnSubmit.setText(ResourceUtils.getResString(SobotReplyActivity.this, "sobot_btn_submit_text"));

        List<ZhiChiUploadAppFileModelResult> picTempList = (List<ZhiChiUploadAppFileModelResult>) getIntent().getSerializableExtra("picTempList");
        String replyTempContent = getIntent().getStringExtra("replyTempContent");
        if (!StringUtils.isEmpty(replyTempContent)) {
            sobotReplyEdit.setText(replyTempContent);
        }

        if (picTempList != null && picTempList.size() > 0) {
            pic_list.addAll(picTempList);
        }

        sobotNegativeButton.setOnClickListener(this);
        sobotBtnSubmit.setOnClickListener(this);
        adapter = new SobotPicListAdapter(SobotReplyActivity.this, pic_list);
        sobotReplyMsgPic.setAdapter(adapter);
        initPicListView();
        mUid = getIntent().getStringExtra("uid");
        mCompanyId = getIntent().getStringExtra("companyId");
        mTicketInfo = (SobotUserTicketInfo) getIntent().getSerializableExtra("ticketInfo");
        if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && SobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH)) {
            // 获取刘海屏信息
            NotchScreenManager.getInstance().getNotchInfo(SobotReplyActivity.this, new INotchScreen.NotchScreenCallback() {
                @Override
                public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                    if (notchScreenInfo.hasNotch) {
                        for (Rect rect : notchScreenInfo.notchRects) {
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(SobotReplyActivity.this, 104));
                            lp.setMargins((rect.right > 110 ? 110 : rect.right) + ScreenUtils.dip2px(SobotReplyActivity.this, 20), (rect.right > 110 ? 110 : rect.right)+ScreenUtils.dip2px(SobotReplyActivity.this, 20), ScreenUtils.dip2px(SobotReplyActivity.this, 20), ScreenUtils.dip2px(SobotReplyActivity.this, 20));
                            sobotReplyEdit.setLayoutParams(lp);
                        }
                    }
                }
            });

        }
        displayInNotch(sobotReplyMsgPic);
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {/*点击外部隐藏键盘*/
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() <= 0) {
                Intent intent = new Intent();
                intent.putExtra("replyTempContent", sobotReplyEdit.getText().toString());
                intent.putExtra("picTempList", (Serializable) pic_list);
                intent.putExtra("isTemp", true);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
        return true;
    }

    /*是否在外部*/
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v == sobotNegativeButton) {
            KeyboardUtil.hideKeyboard(sobotNegativeButton);
            Intent intent = new Intent();
            intent.putExtra("replyTempContent", sobotReplyEdit.getText().toString());
            intent.putExtra("picTempList", (Serializable) pic_list);
            intent.putExtra("isTemp", true);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        if (v == sobotBtnSubmit) {//提交
            KeyboardUtil.hideKeyboard(sobotBtnSubmit);
            if (StringUtils.isEmpty(sobotReplyEdit.getText().toString().trim())) {
                Toast.makeText(getContext(), ResourceUtils.getResString(getContext(), "sobot_please_input_reply_no_empty"), Toast.LENGTH_SHORT).show();
                return;
            }
            if (FastClickUtils.isCanClick()) {
                SobotDialogUtils.startProgressDialog(SobotReplyActivity.this);
                zhiChiApi.replyTicketContent(this, mUid, mTicketInfo.getTicketId(), sobotReplyEdit.getText().toString(), getFileStr(), mCompanyId, new StringResultCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        LogUtils.e(s);
                        CustomToast.makeText(SobotReplyActivity.this, ResourceUtils.getResString(SobotReplyActivity.this, "sobot_leavemsg_success_tip"), 1000, ResourceUtils.getDrawableId(SobotReplyActivity.this, "sobot_iv_login_right")).show();
                        try {
                            Thread.sleep(500);//睡眠一秒  延迟拉取数据
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pic_list.clear();
                        Intent intent = new Intent();
                        intent.putExtra("replyTempContent", "");
                        intent.putExtra("picTempList", (Serializable) pic_list);
                        intent.putExtra("isTemp", false);
                        setResult(Activity.RESULT_OK, intent);
                        SobotDialogUtils.stopProgressDialog(SobotReplyActivity.this);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        ToastUtil.showCustomToast(SobotReplyActivity.this,ResourceUtils.getResString(SobotReplyActivity.this, "sobot_leavemsg_error_tip"));
                        e.printStackTrace();
                        SobotDialogUtils.stopProgressDialog(SobotReplyActivity.this);
                    }
                });
            }
        }
    }


    /**
     * 初始化图片选择的控件
     */
    private void initPicListView() {
        adapter.setOnClickItemViewListener(new SobotPicListAdapter.ViewClickListener() {
            @Override
            public void clickView(View view, int position, int type) {
                KeyboardUtil.hideKeyboard(view);
                switch (type) {
                    case SobotPicListAdapter.ADD:
                        menuWindow = new SobotSelectPicDialog(SobotReplyActivity.this, itemsOnClick);
                        menuWindow.show();
                        break;
                    case SobotPicListAdapter.PIC:
                        LogUtils.i("当前选择图片位置：" + position);
                        if (adapter == null || adapter.getPicList() == null)
                            return;
                        ZhiChiUploadAppFileModelResult result = adapter.getPicList().get(position);
                        if (result != null) {
                            if (!TextUtils.isEmpty(result.getFileLocalPath()) && MediaFileUtils.isVideoFileType(result.getFileLocalPath())) {
                                File file = new File(result.getFileLocalPath());
                                SobotCacheFile cacheFile = new SobotCacheFile();
                                cacheFile.setFileName(file.getName());
                                cacheFile.setUrl(result.getFileUrl());
                                cacheFile.setFilePath(result.getFileLocalPath());
                                cacheFile.setFileType(FileTypeConfig.getFileType(FileUtil.getFileEndWith(result.getFileLocalPath())));
                                cacheFile.setMsgId("" + System.currentTimeMillis());
                                Intent intent = SobotVideoActivity.newIntent(SobotReplyActivity.this, cacheFile);
                                SobotReplyActivity.this.startActivity(intent);
                                return;
                            }
                            Intent intent = new Intent(SobotReplyActivity.this, SobotPhotoActivity.class);
                            intent.putExtra("imageUrL", TextUtils.isEmpty(result.getFileLocalPath()) ? result.getFileUrl() : result.getFileLocalPath());
                            startActivity(intent);
                        }
                        break;
                    case SobotPicListAdapter.DEL:
                        String popMsg = ResourceUtils.getResString(SobotReplyActivity.this, "sobot_do_you_delete_picture");
                        if (adapter == null || adapter.getPicList() == null)
                            return;
                        ZhiChiUploadAppFileModelResult delResult = adapter.getPicList().get(position);
                        if (delResult != null) {
                            if (!TextUtils.isEmpty(delResult.getFileLocalPath()) && MediaFileUtils.isVideoFileType(delResult.getFileLocalPath())) {
                                popMsg = ResourceUtils.getResString(SobotReplyActivity.this, "sobot_do_you_delete_video");
                            }
                        }
                        if (seleteMenuWindow != null) {
                            seleteMenuWindow.dismiss();
                            seleteMenuWindow = null;
                        }
                        if (seleteMenuWindow == null) {
                            seleteMenuWindow = new SobotDeleteWorkOrderDialog(SobotReplyActivity.this, popMsg, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    seleteMenuWindow.dismiss();
                                    if (v.getId() == getResId("btn_pick_photo")) {
                                        Log.e("onClick: ", seleteMenuWindow.getPosition() + "");
                                        pic_list.remove(seleteMenuWindow.getPosition());
                                        adapter.restDataView();
                                    }
                                }
                            });
                        }
                        seleteMenuWindow.setPosition(position);
                        seleteMenuWindow.show();
                        break;
                }

            }
        });
        adapter.restDataView();
    }

    // 为弹出窗口popupwindow实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            if (v.getId() == getResId("btn_take_photo")) {
                LogUtils.i("拍照");
                if (!CommonUtils.isExitsSdcard()) {
                    ToastUtil.showCustomToast(SobotReplyActivity.this, getResString("sobot_sdcard_does_not_exist"),
                            Toast.LENGTH_SHORT);
                    return;
                }
                permissionListener = new PermissionListenerImpl() {
                    @Override
                    public void onPermissionSuccessListener() {
                        if (isCameraCanUse()) {
                            cameraFile = ChatUtils.openCamera(SobotReplyActivity.this);
                        }
                    }
                };
                if (!checkStorageAndCameraPermission()) {
                    return;
                }
                cameraFile = ChatUtils.openCamera(SobotReplyActivity.this);
            }
            if (v.getId() == getResId("btn_pick_photo")) {
                LogUtils.i("选择照片");
                permissionListener = new PermissionListenerImpl() {
                    @Override
                    public void onPermissionSuccessListener() {
                        ChatUtils.openSelectPic(SobotReplyActivity.this);
                    }
                };
                if (!checkStoragePermission()) {
                    return;
                }
                ChatUtils.openSelectPic(SobotReplyActivity.this);
            }
            if (v.getId() == getResId("btn_pick_vedio")) {
                LogUtils.i("选择视频");
                permissionListener = new PermissionListenerImpl() {
                    @Override
                    public void onPermissionSuccessListener() {
                        ChatUtils.openSelectVedio(SobotReplyActivity.this, null);
                    }
                };
                if (!checkStoragePermission()) {
                    return;
                }
                ChatUtils.openSelectVedio(SobotReplyActivity.this, null);
            }

        }
    };

    public void addPicView(ZhiChiUploadAppFileModelResult item) {
        adapter.addData(item);
    }


    public String getFileStr() {
        String tmpStr = "";
        ArrayList<ZhiChiUploadAppFileModelResult> tmpList = adapter.getPicList();
        for (int i = 0; i < tmpList.size(); i++) {
            tmpStr += tmpList.get(i).getFileUrl() + ";";
        }
        return tmpStr;
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(SobotReplyActivity.this);
        MyApplication.getInstance().deleteActivity(this);
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
                if (data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage == null) {
                        selectedImage = ImageUtils.getUri(data, SobotReplyActivity.this);
                    }
                    String path = ImageUtils.getPath(this, selectedImage);
                    if (MediaFileUtils.isVideoFileType(path)) {
                        MediaPlayer mp = new MediaPlayer();
                        try {
                            mp.setDataSource(this, selectedImage);
                            mp.prepare();
                            int videoTime = mp.getDuration();
                            if (videoTime / 1000 > 15) {
                                ToastUtil.showToast(this, getResString("sobot_upload_vodie_length"));
                                return;
                            }
                            SobotDialogUtils.startProgressDialog(this);
//                            ChatUtils.sendPicByFilePath(this,path,sendFileListener,false);
                            String fName = MD5Util.encode(path);
                            String filePath = null;
                            try {
                                filePath = FileUtil.saveImageFile(this, selectedImage, fName + FileUtil.getFileEndWith(path), path);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtil.showToast(this, ResourceUtils.getResString(this, "sobot_pic_type_error"));
                                return;
                            }
                            sendFileListener.onSuccess(filePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        SobotDialogUtils.startProgressDialog(this);
                        ChatUtils.sendPicByUriPost(this, selectedImage, sendFileListener, false);
                    }
                } else {
                    showHint(getResString("sobot_did_not_get_picture_path"));
                }
            } else if (requestCode == ZhiChiConstant.REQUEST_CODE_makePictureFromCamera) {
                if (cameraFile != null && cameraFile.exists()) {
                    SobotDialogUtils.startProgressDialog(this);
                    ChatUtils.sendPicByFilePath(this, cameraFile.getAbsolutePath(), sendFileListener, true);
                } else {
                    showHint(getResString("sobot_pic_select_again"));
                }
            }
        }

    }

    public void showHint(String content) {
        CustomToast.makeText(this, content, 1000).show();
    }

    private ChatUtils.SobotSendFileListener sendFileListener = new ChatUtils.SobotSendFileListener() {
        @Override
        public void onSuccess(final String filePath) {
            zhiChiApi.fileUploadForPostMsg(SobotReplyActivity.this, mCompanyId, mUid,filePath, new ResultCallBack<ZhiChiMessage>() {
                @Override
                public void onSuccess(ZhiChiMessage zhiChiMessage) {

                    SobotDialogUtils.stopProgressDialog(SobotReplyActivity.this);
                    if (zhiChiMessage.getData() != null) {
                        ZhiChiUploadAppFileModelResult item = new ZhiChiUploadAppFileModelResult();
                        item.setFileUrl(zhiChiMessage.getData().getUrl());
                        item.setFileLocalPath(filePath);
                        item.setViewState(1);
                        addPicView(item);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    SobotDialogUtils.stopProgressDialog(SobotReplyActivity.this);
                    showHint(ResourceUtils.getResString(SobotReplyActivity.this, "sobot_net_work_err"));
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {

                }
            });
        }

        @Override
        public void onError() {
            SobotDialogUtils.stopProgressDialog(SobotReplyActivity.this);
        }
    };


}