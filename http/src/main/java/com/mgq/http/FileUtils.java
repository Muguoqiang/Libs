package com.mgq.http;

import android.content.Context;
import android.os.Environment;

import java.io.File;


public class FileUtils {

    /**
     * 得到SD卡根目录.
     */
    public static File getRootPath(Context context) {
        if (FileUtils.sdCardIsAvailable()) {
            return Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        } else {
            return context.getFilesDir();
        }
    }

    /**
     * SD卡是否可用.
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        } else
            return false;
    }





}
