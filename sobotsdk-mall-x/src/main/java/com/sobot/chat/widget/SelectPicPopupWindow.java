package com.sobot.chat.widget;

import static com.sobot.network.http.SobotOkHttpUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.widget.zxing.Result;
import com.sobot.chat.widget.zxing.util.CodeUtils;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


@SuppressLint("ViewConstructor")
public class SelectPicPopupWindow extends PopupWindow {

    private Button sobot_btn_take_photo, sobot_btn_cancel, sobot_btn_scan_qr_code;
    private View mView;
    private String imgUrl;
    private Context context;
    private String type;
    private LayoutInflater inflater;
    private String uid;
    //如果需要识别图片是否是二维码，记录识别结果
    private Result[] result;

    public SelectPicPopupWindow(final Activity context, String uid) {
        this.context = context;
        this.uid = uid;
        initView();
    }

    public SelectPicPopupWindow(final Activity context, String url, String type) {
        super(context);
        imgUrl = url;
        this.type = type;
        this.context = context.getApplicationContext();
        initView();
    }

    /**
     * 收到消息是图片，判断是否是二维码，如果只识别出一张有效二维码，显示识别二维码按钮，跳转；
     * 如果一张没有或者多余一张，隐藏识别二维码按钮
     *
     * @param context
     * @param url
     * @param type
     * @param isParseMultiQRCode 是否先要识别二维码 ,true :是，false:否
     */
    public SelectPicPopupWindow(final Activity context, String url, String type, boolean isParseMultiQRCode) {
        super(context);
        imgUrl = url;
        this.type = type;
        this.context = context.getApplicationContext();
        initView();
        if (isParseMultiQRCode) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    result = CodeUtils.parseMultiQRCode(imgUrl);
                    if (null != result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result.length == 1) {
                                    LogUtils.i("图片中二维码:" + result[0].getText());
                                    sobot_btn_scan_qr_code.setVisibility(View.VISIBLE);
                                } else {
                                    LogUtils.i("图片中有 " + result.length + " 个二维码");
                                    sobot_btn_scan_qr_code.setVisibility(View.GONE);
                                }

                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void initView() {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(ResourceUtils.getIdByName(context, "layout", "sobot_clear_history_dialog"), null);
        sobot_btn_take_photo = (Button) mView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_btn_take_photo"));
        sobot_btn_take_photo.setText(ResourceUtils.getResString(context, "sobot_save_pic"));
        sobot_btn_cancel = (Button) mView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_btn_cancel"));
        sobot_btn_cancel.setText(ResourceUtils.getResString(context, "sobot_btn_cancle"));
        sobot_btn_scan_qr_code = (Button) mView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_btn_scan_qr_code"));
        sobot_btn_scan_qr_code.setText(ResourceUtils.getResString(context, "sobot_scan_qr_code"));
        // 设置SelectPicPopupWindow的View
        this.setContentView(mView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(ResourceUtils.getIdByName(context, "style", "sobot_AnimBottom"));
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_pop_layout")).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        if (!TextUtils.isEmpty(imgUrl)) {
            sobot_btn_take_photo.setTextColor(context.getResources()
                    .getColor(ResourceUtils.getIdByName(context, "color", "sobot_common_black")));
            sobot_btn_cancel.setTextColor(context.getResources()
                    .getColor(ResourceUtils.getIdByName(context, "color", "sobot_common_black")));
            sobot_btn_scan_qr_code.setTextColor(context.getResources()
                    .getColor(ResourceUtils.getIdByName(context, "color", "sobot_common_black")));
            // 取消按钮
            sobot_btn_cancel.setOnClickListener(savePictureOnClick);
            // 设置按钮监听
            sobot_btn_take_photo.setOnClickListener(savePictureOnClick);
            //识别二维码
            sobot_btn_scan_qr_code.setOnClickListener(savePictureOnClick);
        }
    }

    // 为弹出窗口popupwindow实现监听类
    private OnClickListener savePictureOnClick = new OnClickListener() {
        public void onClick(View v) {
            dismiss();
            if (v == sobot_btn_take_photo) {
                LogUtils.i("imgUrl:" + imgUrl);
                if (type.equals("gif")) {
                    saveImageToGallery(context, imgUrl);
                } else {
                    Bitmap bitmap = SobotBitmapUtil.compress(imgUrl, context, true);
                    saveImageToGallery(context, bitmap);
                }
            }

            if (v == sobot_btn_cancel) {

            }

            if (v == sobot_btn_scan_qr_code) {
                if (null != result && result.length == 1) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("url", result[0].getText());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    sobot_btn_scan_qr_code.setVisibility(View.GONE);
                }
            }
        }
    };

    private void showHint(String content) {
        CustomToast.makeText(context, content, 1000,
                ResourceUtils.getDrawableId(context, "sobot_iv_login_right")).show();
    }

    public void saveImageToGallery(Context context, Bitmap bmp) {
        if (!isSdCardExist()) {
            ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_save_err_sd_card"));
            return;
        }
        if (bmp == null) {
            ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_save_err_pic"));
            return;
        }
        String savePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File
                .separator + "Sobot";
        // 首先保存图片
        File appDir = new File(savePath, "sobot_pic");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_save_error_file"));
            e.printStackTrace();
        } catch (IOException e) {
            ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_save_err"));
            e.printStackTrace();
        } catch (Exception e) {
            ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_save_err"));
            e.printStackTrace();
        }

        notifyUpdatePic(file, fileName);
    }

    public boolean isSdCardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public void saveImageToGallery(Context context, String bmp) {
        if (!isSdCardExist()) {
            ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_save_err_sd_card"));
            return;
        }
        if (TextUtils.isEmpty(bmp)) {
            ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_save_err_pic"));
            return;
        }
        String savePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File
                .separator + "Sobot";
        // 首先保存图片
        File appDir = new File(savePath, "sobot_pic");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".gif";
        File file = new File(appDir, fileName);
        if (fileChannelCopy(new File(bmp), file)) {
            notifyUpdatePic(file, fileName);
        }
    }

    // 最后通知图库更新
    public void notifyUpdatePic(File file, String fileName) {
        try {
            if (file != null && file.exists() && !TextUtils.isEmpty(fileName)) {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
                MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        showHint(ResourceUtils.getResString(context, "sobot_already_save_to_picture"));
    }

    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */
    public boolean fileChannelCopy(File s, File t) {
        boolean isSuccess = true;
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            isSuccess = false;
            ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_save_err"));
            e.printStackTrace();
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            }
        }
        return true;
    }
}