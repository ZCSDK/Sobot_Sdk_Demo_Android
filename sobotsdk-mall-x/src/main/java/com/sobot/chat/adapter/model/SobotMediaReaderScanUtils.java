package com.sobot.chat.adapter.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.WorkerThread;

import com.sobot.chat.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

//android 14 用于部分权限的 图片、视频扫描
public class SobotMediaReaderScanUtils {
    /**
     * Image attribute.
     */
    private static final String[] IMAGES = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID

    };

    /**
     * Scan for image files.
     */
    @WorkerThread
    public static List<SobotAlbumFile> scanImageFile(Context mContext) {
        List<SobotAlbumFile> albumFileList = new ArrayList<>();
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(collection,
                IMAGES,
                null,
                null,
                null);

        if (cursor != null) {
            LogUtils.d("======图片总数=======" + cursor.getCount());
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String bucketName = cursor.getString(1);
                String mimeType = cursor.getString(2);
                long addDate = cursor.getLong(3);
                float latitude = cursor.getFloat(4);
                float longitude = cursor.getFloat(5);
                long size = cursor.getLong(6);
                long id = cursor.getLong(7);
                SobotAlbumFile imageFile = new SobotAlbumFile();
                imageFile.setMediaType(SobotAlbumFile.TYPE_IMAGE);
                imageFile.setPath(path);
                imageFile.setBucketName(bucketName);
                imageFile.setMimeType(mimeType);
                imageFile.setAddDate(addDate);
                imageFile.setLatitude(latitude);
                imageFile.setLongitude(longitude);
                imageFile.setSize(size);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                imageFile.setUri(contentUri);
                albumFileList.add(imageFile);
            }
            cursor.close();
        }
        return albumFileList;
    }


    /**
     * Video attribute.
     */
    private static final String[] VIDEOS = {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.LATITUDE,
            MediaStore.Video.Media.LONGITUDE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media._ID
    };

    /**
     * Scan for image files.
     */
    @WorkerThread
    public static List<SobotAlbumFile> scanVideoFile(Context mContext) {
        List<SobotAlbumFile> albumFileList = new ArrayList<>();
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(collection,
                VIDEOS,
                null,
                null,
                null);
        LogUtils.d("======scanVideoFile=======" + (cursor == null));
        if (cursor != null) {
            LogUtils.d("======视频总数=======" + cursor.getCount());
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String bucketName = cursor.getString(1);
                String mimeType = cursor.getString(2);
                long addDate = cursor.getLong(3);
                float latitude = cursor.getFloat(4);
                float longitude = cursor.getFloat(5);
                long size = cursor.getLong(6);
                long duration = cursor.getLong(7);
                long id = cursor.getLong(8);

                SobotAlbumFile videoFile = new SobotAlbumFile();
                videoFile.setMediaType(SobotAlbumFile.TYPE_VIDEO);
                videoFile.setPath(path);
                videoFile.setBucketName(bucketName);
                videoFile.setMimeType(mimeType);
                videoFile.setAddDate(addDate);
                videoFile.setLatitude(latitude);
                videoFile.setLongitude(longitude);
                videoFile.setSize(size);
                videoFile.setDuration(duration);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                videoFile.setUri(contentUri);

                albumFileList.add(videoFile);
            }
            cursor.close();
        }
        return albumFileList;
    }


    @WorkerThread
    public static List<SobotAlbumFile> getAllMedia(Context mContext, int type) {
        List<SobotAlbumFile> list = new ArrayList<>();
        if (type == 3) {
            //图片和视频
            List<SobotAlbumFile> scannedImageFileList = scanImageFile(mContext);
            if (scannedImageFileList != null) {
                list.addAll(scannedImageFileList);
            }
            List<SobotAlbumFile> scannedVideoFileList = scanVideoFile(mContext);
            if (scannedVideoFileList != null) {
                list.addAll(scannedVideoFileList);
            }
        } else if (type == 2) {
            //视频
            List<SobotAlbumFile> scannedVideoFileList = scanVideoFile(mContext);
            if (scannedVideoFileList != null) {
                list.addAll(scannedVideoFileList);
            }
        } else {
            //图片
            List<SobotAlbumFile> scannedImageFileList = scanImageFile(mContext);
            if (scannedImageFileList != null) {
                list.addAll(scannedImageFileList);
            }
        }
        return list;
    }

}