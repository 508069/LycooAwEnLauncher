package com.example.mysettings.utils;

import android.annotation.SuppressLint;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.os.Process;
import android.text.format.Formatter;

import androidx.annotation.RequiresApi;


import com.example.mysettings.settingsApp.observer.PackageStatsObserver;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class AppUtil {
    /**
     * 获取应用大小
     *Android 8.0以上使用
     **/
    @SuppressLint("NewApi")
    public static String getAppStorage(Context context, String packageName) {
        StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        String appSizeL = 0 + "";
        for (StorageVolume storageVolume : storageVolumes) {
            UUID uuid = null;
            String uuidStr = storageVolume.getUuid();
            try {
                if (TextUtils.isEmpty(uuidStr)){
                    uuid = StorageManager.UUID_DEFAULT;
                }else {
                    uuid = UUID.fromString(uuidStr);
                }
            }catch (Exception e){
                uuid = StorageManager.UUID_DEFAULT;
            }
            //通过包名获取uid
            int uid = 0;
            uid = getUid(context, packageName);
            StorageStats storageStats = null;
            try {
                UserHandle userHandle = Process.myUserHandle();
                storageStats = storageStatsManager.queryStatsForPackage(uuid, packageName, userHandle);
            } catch (Exception e) {
                e.printStackTrace();
                return 0 + "";
            }
            //获取到App的总大小
            appSizeL = size(storageStats.getAppBytes() + storageStats.getCacheBytes() + storageStats.getDataBytes());
        }
        return appSizeL;
    }

    private static int getUid(Context context, String pakName) {
        try {
            return context.getPackageManager().getApplicationInfo(pakName, PackageManager.GET_META_DATA).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 将文件大小显示为GB,MB等形式
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String size(long size)
    {
        if (size / (1024 * 1024 * 1024) > 0)
        {
            float tmpSize = (float) (size) / (float) (1024 * 1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.##");
            return "" + df.format(tmpSize) + "GB";
        }
        else if (size / (1024 * 1024) > 0)
        {
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.##");
            return "" + df.format(tmpSize) + "MB";
        }
        else if (size / 1024 > 0)
        {
            return "" + (size / (1024)) + "KB";
        }
        else
            return "" + size + "B";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getAppSize(Context context, PackageManager packageManager, String packageName) {
        // 创建一个 CountDownLatch
        final CountDownLatch countDownLatch = new CountDownLatch(1);


        // 创建一个 PackageStatsObserver 对象
        PackageStatsObserver packageStatsObserver = new PackageStatsObserver(countDownLatch);

        //  PackageManager 对象获取应用程序的信息
        try {
            Method getPackageSizeInfo = packageManager.getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            getPackageSizeInfo.invoke(packageManager, packageName, packageStatsObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 等待 CountDownLatch 释放
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 返回应用程序大小
        return Formatter.formatFileSize(context, packageStatsObserver.getAppSize());
    }
}
