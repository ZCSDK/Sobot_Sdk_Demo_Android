package com.sobot.chat.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.sobot.chat.api.apiUtils.SobotApp;
import com.sobot.chat.application.MyApplication;

import java.io.File;
import java.security.MessageDigest;

/**
 * @author Created by jinxl on 2018/12/3.
 */
public class SobotPathManager {
    private Context mContext;

    private static String mRootPath;

    private static final String ROOT_DIR = "download";
    private static final String VIDEO_DIR = "video";
    private static final String VOICE_DIR = "voice";
    private static final String PIC_DIR = "pic";
    private static final String CACHE_DIR = "cache";

    private SobotPathManager(Context context) {
        if (context != null) {
            mContext = context.getApplicationContext();
        } else {
            mContext = MyApplication.getInstance().getLastActivity();
        }
    }

    private static SobotPathManager instance;

    public static SobotPathManager getInstance() {
        if (instance == null) {
            synchronized (SobotPathManager.class) {
                if (instance == null) {
                    instance = new SobotPathManager(SobotApp.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public String getRootDir() {
        if (mRootPath == null) {
            String packageName = mContext != null ? mContext.getPackageName() : "";
            mRootPath = Environment.getExternalStorageDirectory().getPath() + File.separator + ROOT_DIR + File.separator + encode(packageName + "cache_sobot");
        }
        return mRootPath;
    }

    public String getVideoDir() {
        String path;
        if (isExitsSdcard()) {
            //SD卡已装入
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                path = getRootDir() + File.separator + VIDEO_DIR + File.separator;
            } else {
                path = mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath() + File.separator;
            }
        } else {
            //外部存储不可用
            path = mContext.getFilesDir().getPath() + File.separator + "sobot" + File.separator + VIDEO_DIR + File.separator;
        }
        return path;
    }

    public String getVoiceDir() {
        String path;
        if (isExitsSdcard()) {
            //SD卡已装入
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                path = getRootDir() + File.separator + VOICE_DIR + File.separator;
            } else {
                path = mContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath() + File.separator;
            }
        } else {
            //外部存储不可用
            path = mContext.getFilesDir().getPath() + File.separator + "sobot" + File.separator + VOICE_DIR + File.separator;
        }
        return path;
    }

    public String getPicDir() {
        String path;
        if (isExitsSdcard()) {
            //SD卡已装入
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                path = getRootDir() + File.separator + PIC_DIR + File.separator;
            } else {
                path = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + File.separator;
            }
        } else {
            //外部存储不可用
            path = mContext.getFilesDir().getPath() + File.separator + "sobot" + File.separator + PIC_DIR + File.separator;
        }
        return path;

    }

    //sdcard/download/xxxx/cache
    public String getCacheDir() {
        String path;
        if (isExitsSdcard()) {
            //SD卡已装入
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                path = getRootDir() + File.separator + CACHE_DIR + File.separator;
            } else {
                path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator;
            }
        } else {
            //外部存储不可用
            path = mContext.getFilesDir().getPath() + File.separator + "sobot" + File.separator + CACHE_DIR + File.separator;
        }
        return path;

    }

    private String encode(String str) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(str.getBytes());
            for (byte b : digest) {
                int num = b & 0xff;
                String hex = Integer.toHexString(num);
                if (hex.length() < 2) {
                    sb.append("0");
                }
                sb.append(hex);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
