package com.sobot.chat.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.sobot.chat.utils.IOUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotPathManager;
import com.sobot.chat.utils.ToastUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtil {

    public static String saveBitmap(int quality, Bitmap b) {
        String picDir = SobotPathManager.getInstance().getPicDir();
        IOUtils.createFolder(picDir);
        long dataTake = System.currentTimeMillis();
        String jpegName = picDir + "pic_" + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            return jpegName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (b != null && !b.isRecycled()) {
                b.recycle();
            }
        }
    }

    /**
     * 保存图片到沙盒
     * @param context
     * @param newFileName
     * @return
     * @throws Exception
     */
    public static String saveImageFile(Context context,Uri uri, String newFileName,String defualPath) throws Exception{

        if (null==uri){
            return defualPath;
        }

        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
        InputStream inputStream = new FileInputStream(pfd.getFileDescriptor());
        String picDir = SobotPathManager.getInstance().getPicDir();
        IOUtils.createFolder(picDir);
        String oldFilePath = picDir +newFileName;
        FileOutputStream fo = new FileOutputStream(oldFilePath);

        if (!IOUtils.copyFileWithStream(fo, inputStream)) {
            ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_pic_type_error"));
            return defualPath;
        }
        return oldFilePath;

    }



    public static boolean deleteFile(String url) {
        boolean result = false;
        File file = new File(url);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    /**
     * 获取文件后缀名
     * @param filePath
     * @return
     */
    public static String getFileEndWith(String filePath){
        if (filePath.indexOf(".")!=-1){
            return filePath.substring(filePath.lastIndexOf(".")-1);
        }
        return "";

    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
