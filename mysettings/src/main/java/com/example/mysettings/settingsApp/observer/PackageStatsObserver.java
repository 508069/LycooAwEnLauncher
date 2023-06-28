package com.example.mysettings.settingsApp.observer;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.os.RemoteException;



import java.util.concurrent.CountDownLatch;

public class PackageStatsObserver extends IPackageStatsObserver.Stub {
    private long appSize;
    private final CountDownLatch countDownLatch;

    public PackageStatsObserver(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
        // 获取应用程序大小（以字节为单位）
        appSize = pStats.codeSize + pStats.dataSize + pStats.cacheSize;

        // 释放 CountDownLatch
        countDownLatch.countDown();
    }

    public long getAppSize() {
        return appSize;
    }
}
