/*
 * Copyright 2017 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sobot.chat.adapter.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SobotAlbumFile implements Parcelable, Comparable<SobotAlbumFile> {

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_ADD = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_IMAGE, TYPE_VIDEO,TYPE_ADD})
    public @interface MediaType {
    }

    /**
     * 本地文件路径.
     */
    private String mPath;
    /**
     * 本地的uri
     */
    private Uri uri;
    private String fileNumKey;
    /**
     * 远程的地址
     */
    private String fileUrl;
    /**
     * 文件名字.
     */
    private String fileName;
    /**
     * 文件夹名字.
     */
    private String mBucketName;
    /**
     * 文件扩展类型.
     */
    private String mMimeType;
    /**
     * 添加日期.
     */
    private long mAddDate;
    /**
     * Latitude
     */
    private float mLatitude;
    /**
     * Longitude.
     */
    private float mLongitude;
    /**
     * 大小.
     */
    private long mSize;
    /**
     * Duration.
     */
    private long mDuration;
    /**
     * Thumb path.
     */
    private String mThumbPath;
    /**
     * MediaType.
     */
    private int mMediaType;
    /**
     * Checked.
     */
    private boolean isChecked;
    /**
     * Enabled.
     */
    private boolean isDisable;

    public SobotAlbumFile() {
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int compareTo(SobotAlbumFile o) {
        long time = o.getAddDate() - getAddDate();
        if (time > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        else if (time < -Integer.MAX_VALUE)
            return -Integer.MAX_VALUE;
        return (int) time;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SobotAlbumFile) {
            SobotAlbumFile o = (SobotAlbumFile) obj;
            String inPath = o.getPath();
            if (mPath != null && inPath != null) {
                return mPath.equals(inPath);
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return mPath != null ? mPath.hashCode() : super.hashCode();
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getBucketName() {
        return mBucketName;
    }

    public void setBucketName(String bucketName) {
        mBucketName = bucketName;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public long getAddDate() {
        return mAddDate;
    }

    public void setAddDate(long addDate) {
        mAddDate = addDate;
    }

    public float getLatitude() {
        return mLatitude;
    }

    public void setLatitude(float latitude) {
        mLatitude = latitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public void setLongitude(float longitude) {
        mLongitude = longitude;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public String getThumbPath() {
        return mThumbPath;
    }

    public void setThumbPath(String thumbPath) {
        mThumbPath = thumbPath;
    }

    @MediaType
    public int getMediaType() {
        return mMediaType;
    }

    public void setMediaType(@MediaType int mediaType) {
        mMediaType = mediaType;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        this.isDisable = disable;
    }

    public String getFileNumKey() {
        return fileNumKey;
    }

    public void setFileNumKey(String fileNumKey) {
        this.fileNumKey = fileNumKey;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    protected SobotAlbumFile(Parcel in) {
        mPath = in.readString();
        mBucketName = in.readString();
        mMimeType = in.readString();
        mAddDate = in.readLong();
        mLatitude = in.readFloat();
        mLongitude = in.readFloat();
        mSize = in.readLong();
        mDuration = in.readLong();
        mThumbPath = in.readString();
        mMediaType = in.readInt();
        isChecked = in.readByte() != 0;
        isDisable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeString(mBucketName);
        dest.writeString(mMimeType);
        dest.writeLong(mAddDate);
        dest.writeFloat(mLatitude);
        dest.writeFloat(mLongitude);
        dest.writeLong(mSize);
        dest.writeLong(mDuration);
        dest.writeString(mThumbPath);
        dest.writeInt(mMediaType);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isDisable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SobotAlbumFile> CREATOR = new Creator<SobotAlbumFile>() {
        @Override
        public SobotAlbumFile createFromParcel(Parcel in) {
            return new SobotAlbumFile(in);
        }

        @Override
        public SobotAlbumFile[] newArray(int size) {
            return new SobotAlbumFile[size];
        }
    };

}