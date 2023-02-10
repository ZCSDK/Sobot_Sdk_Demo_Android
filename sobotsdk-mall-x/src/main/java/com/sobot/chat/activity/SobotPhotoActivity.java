package com.sobot.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.HttpUtils;
import com.sobot.chat.core.HttpUtils.FileCallBack;
import com.sobot.chat.utils.ImageUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.MD5Util;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.widget.RoundProgressBar;
import com.sobot.chat.widget.SelectPicPopupWindow;
import com.sobot.chat.widget.gif.GifView2;
import com.sobot.chat.widget.subscaleview.ImageSource;
import com.sobot.chat.widget.subscaleview.SobotScaleImageView;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.io.File;
import java.io.FileInputStream;

public class SobotPhotoActivity extends Activity implements View.OnLongClickListener {

    private SobotScaleImageView mImageView;

    private GifView2 sobot_image_view;
    private RelativeLayout sobot_rl_gif;
    private SelectPicPopupWindow menuWindow;
    String imageUrL;
    Bitmap bitmap;
    boolean isRight;
    String sdCardPath;
    private RoundProgressBar sobot_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.getIdByName(this, "layout",
                "sobot_photo_activity"));
        MyApplication.getInstance().addActivity(this);
        sobot_progress = (RoundProgressBar) findViewById(ResourceUtils.getResId(this, "sobot_pic_progress_round"));
        sobot_progress.setRoundWidth(10);//设置圆环的宽度
        sobot_progress.setCricleProgressColor(Color.WHITE);
        sobot_progress.setTextColor(Color.WHITE);
        sobot_progress.setTextDisplayable(true);
        sobot_progress.setVisibility(View.GONE);
        mImageView = (SobotScaleImageView) findViewById(ResourceUtils.getIdByName(this,
                "id", "sobot_big_photo"));
        sobot_image_view = (GifView2) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_image_view"));
        sobot_image_view.setIsCanTouch(true);
        sobot_rl_gif = (RelativeLayout) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_rl_gif"));
        sobot_rl_gif.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sobot_image_view.setLoadFinishListener(new GifView2.LoadFinishListener() {
            @Override
            public void endCallBack(String pathAbsolute) {
                showView(pathAbsolute);
            }
        });

        initBundleData(savedInstanceState);

        LogUtils.i("SobotPhotoActivity-------" + imageUrL);

        if (TextUtils.isEmpty(imageUrL)) {
            return;
        }

        if (imageUrL.startsWith("http")) {
            File dirPath = this.getImageDir(this);
            String encode = MD5Util.encode(imageUrL);
            File savePath = new File(dirPath, encode);
            sdCardPath = savePath.getAbsolutePath();
            if (!savePath.exists()) {
                if (imageUrL.contains("?")) {
                    imageUrL = imageUrL.substring(0, imageUrL.indexOf("?"));
                }
                displayImage(imageUrL, savePath, sobot_image_view);
            } else {
                showView(savePath.getAbsolutePath());
            }
        } else {
            File gifSavePath = new File(imageUrL);
            sdCardPath = imageUrL;
            if (gifSavePath.exists()) {
                showView(imageUrL);
            }
        }
        sobot_rl_gif.setVisibility(View.VISIBLE);
    }

    private void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            imageUrL = getIntent().getStringExtra("imageUrL");
            isRight = getIntent().getBooleanExtra("isRight", false);
        } else {
            imageUrL = savedInstanceState.getString("imageUrL");
            isRight = savedInstanceState.getBoolean("isRight");
        }

    }

    void showView(String savePath) {
        if (!TextUtils.isEmpty(imageUrL)
                && (imageUrL.endsWith(".gif") || imageUrL.endsWith(".GIF"))
                && isRight) {
            showGif(savePath);
        } else {
            if (!TextUtils.isEmpty(imageUrL)
                    && (imageUrL.endsWith(".gif") || imageUrL.endsWith(".GIF"))) {
                showGif(savePath);
            } else {
                bitmap = SobotBitmapUtil.compress(savePath, getApplicationContext(), true);
                //判断图片是否有旋转，有的话旋转后再显示
                try {
                    int degree = ImageUtils.readPictureDegree(savePath);
                    if (degree > 0) {
                        bitmap = ImageUtils.rotateBitmap(bitmap, degree);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mImageView.setImage(ImageSource.bitmap(bitmap));
                mImageView.setVisibility(View.VISIBLE);

                mImageView.setMinimumDpi(50);
                mImageView.setMinimumTileDpi(240);
                mImageView.setDoubleTapZoomStyle(SobotScaleImageView.ZOOM_FOCUS_FIXED);
                mImageView.setDoubleTapZoomScale(2F);
                mImageView.setPanLimit(SobotScaleImageView.PAN_LIMIT_INSIDE);
                mImageView.setPanEnabled(true);
                mImageView.setZoomEnabled(true);
                mImageView.setQuickScaleEnabled(true);

                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mImageView.playSoundEffect(SoundEffectConstants.CLICK);
                        finish();
                    }
                });
                mImageView.setOnLongClickListener(gifLongClickListener);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mImageView.playSoundEffect(SoundEffectConstants.CLICK);
        finish();
    }

    private void showGif(String savePath) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(savePath);
            bitmap = BitmapFactory.decodeFile(savePath);
            if (bitmap == null) {
                return;
            }
//			sobot_image_view.setGifImageType(GifView.GifImageType.COVER);
            sobot_image_view.setGifImage(in, imageUrL);
            int screenWidth = ScreenUtils
                    .getScreenWidth(SobotPhotoActivity.this);
            int screenHeight = ScreenUtils
                    .getScreenHeight(SobotPhotoActivity.this);
            int w = ScreenUtils.formatDipToPx(SobotPhotoActivity.this,
                    bitmap.getWidth());
            int h = ScreenUtils.formatDipToPx(SobotPhotoActivity.this,
                    bitmap.getHeight());
            if (w == h) {
                if (w > screenWidth) {
                    w = screenWidth;
                    h = w;
                }
            } else {
                if (w > screenWidth) {

                    h = (int) (h * (screenWidth * 1.0f / w));
                    w = screenWidth;
                }
                if (h > screenHeight) {

                    w = (int) (w * (screenHeight * 1.0f / h));
                    h = screenHeight;

                }
            }
            LogUtils.i("bitmap" + w + "*" + h);
//			sobot_image_view.setShowDimension(w, h);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    w, h);
            sobot_image_view.setLayoutParams(layoutParams);
        } catch (Exception e) {
        }
        sobot_rl_gif.setVisibility(View.VISIBLE);
        sobot_rl_gif.setOnLongClickListener(gifLongClickListener);
        sobot_image_view.setOnLongClickListener(gifLongClickListener);
    }

    private View.OnLongClickListener gifLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (!TextUtils.isEmpty(sdCardPath) && new File(sdCardPath).exists()) {
                menuWindow = new SelectPicPopupWindow(SobotPhotoActivity.this, sdCardPath, "gif");
                try {
                    menuWindow.showAtLocation(sobot_rl_gif, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
                } catch (Exception e) {
                    menuWindow = null;
//						e.printStackTrace();
                }
            }
            return false;
        }
    };

    public void displayImage(String url, File saveFile, final GifView2 gifView) {
        sobot_progress.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(url)){
            return;
        }
        if (url.startsWith("http") || url.startsWith("https")) {
            // 下载图片
            HttpUtils.getInstance().download(url, saveFile, null, new FileCallBack() {

                @Override
                public void onResponse(File file) {
                    LogUtils.i("down load onSuccess gif"
                            + file.getAbsolutePath());
                    // 把图片文件打开为文件流，然后解码为bitmap
                    showView(file.getAbsolutePath());
                    sobot_progress.setProgress(100);
                    sobot_progress.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e, String msg, int responseCode) {
                    LogUtils.w("图片下载失败:" + msg, e);
                }

                @Override
                public void inProgress(int progress) {
                    //LogUtils.i("图片下载进度:" + progress);
                    sobot_progress.setProgress(progress);
                }
            });
        }
    }

    public File getFilesDir(Context context, String tag) {
        if (isSdCardExist() == true) {
            return context.getExternalFilesDir(tag);
        } else {
            return context.getFilesDir();
        }
    }

    public File getImageDir(Context context) {
        File file = getFilesDir(context, "images");
        return file;
    }

    public boolean isSdCardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        sobot_image_view.pause();
        if (bitmap != null && bitmap.isRecycled() == false) {
            bitmap.recycle();
            System.gc();
        }
        if (menuWindow != null && menuWindow.isShowing()) {
            try {
                menuWindow.dismiss();
            } catch (Exception e) {
//				e.printStackTrace();
            }
            menuWindow = null;
        }

        MyApplication.getInstance().deleteActivity(SobotPhotoActivity.this);
        super.onDestroy();
    }

    @Override
    public boolean onLongClick(View v) {
        if (!TextUtils.isEmpty(sdCardPath) && new File(sdCardPath).exists()) {
            menuWindow = new SelectPicPopupWindow(SobotPhotoActivity.this, sdCardPath, "jpg/png", true);
            try {
                menuWindow.showAtLocation(sobot_rl_gif, Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
            } catch (Exception e) {
//				e.printStackTrace();
                menuWindow = null;
            }
        }
        return false;
    }

    protected void onSaveInstanceState(Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putString("imageUrL", imageUrL);
        outState.putBoolean("isRight", isRight);
        super.onSaveInstanceState(outState);
    }
}