package com.sobot.chat.provider;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Created by jinxl on 2018/11/18.
 */
public class SobotFileProvider extends FileProvider {
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        File file = new File(getContext().getFilesDir(),uri.getPath());
        if(file.exists()){
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        }
        throw new FileNotFoundException(uri.getPath());
    }
}
