package com.mono.mysettings.settingStorage.preference;


import android.content.Context;
import android.graphics.Color;
import com.lycoo.commons.util.LogUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.example.mysettings.R;
import com.mono.mysettings.settingStorage.util.DeviceStorage;


import java.util.Locale;

public class StorageVolumePreference extends Preference {
    private String[] units = {"B", "KB", "MB", "GB", "TB"};

    public StorageVolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.storage_volume);

    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView titleView = holder.itemView.findViewById(android.R.id.title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50); // 设置字体大小
        titleView.setTextColor(Color.GREEN); // 设置字体颜色
        TextView summaryView = holder.itemView.findViewById(android.R.id.summary);
//        long totalRom = getTotalRom();
        DeviceStorage deviceStorage = new DeviceStorage(getContext());
        long totalRom = deviceStorage.getTotalBytes();
        long enableRom = deviceStorage.getUsedBytes();
//        long enableRom = deviceStorage.getUserStorage();
        titleView.setText(getUnit(enableRom));
//        long totalRom = deviceStorage.getAllStorage();
//        deviceStorage.init();
//        int totalMemory = DeviceUtils.getTotalMemory();
        summaryView.setText("           共" + getUnit(totalRom));
        ProgressBar progressBar = holder.itemView.findViewById(R.id.progress_bar);
        progressBar.setMax((int) (totalRom/(1024*1024*1024)));
        progressBar.setProgress((int) (enableRom/(1024*1024*1024)));
    }


    /**
     * 单位转换
     */
    private String getUnit(float size) {
        int index = 0;
        while (size > 1024 && index < 4) {
            size = size / 1024;
            index++;
        }
        LogUtils.info("test", "rom :" + index);
        return String.format(Locale.getDefault(), " %.2f %s", size, units[index]);
    }

}

