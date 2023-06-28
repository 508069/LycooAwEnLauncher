package com.example.mysettings.settingsDeviceInfo.fragment;


import static android.content.Context.WIFI_SERVICE;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.example.mysettings.utils.IpUtil;
import com.lycoo.commons.util.DeviceUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;


import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mysettings.R;

import java.lang.ref.WeakReference;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;



public class StatusFragment extends PreferenceFragmentCompat {
    private static final int EVENT_UPDATE_STATS = 500;
    private static final int EVENT_UPDATE_CONNECTIVITY = 600;
    private Preference mIpAddress;
    private Preference mWifiMacAddress;
    private Preference mEthernetMacAddress;
    private WifiManager mWifiManager;
    private Preference mUptime;
    private Handler mHandler;
    private static final String[] CONNECTIVITY_INTENTS = {
            BluetoothAdapter.ACTION_STATE_CHANGED,
            ConnectivityManager.CONNECTIVITY_ACTION,
            WifiManager.NETWORK_STATE_CHANGED_ACTION,
    };
    private static final String KEY_IP_ADDRESS = "wifi_ip_address";
    private static final String KEY_SERIAL_NUMBER = "serial_number";
    private static final String KEY_WIFI_MAC_ADDRESS = "wifi_mac_address";
    private static final String KEY_ETHERNET_MAC_ADDRESS = "ethernet_mac_address";

    private static class MyHandler extends Handler {
        private WeakReference<StatusFragment> mStatus;

        public MyHandler(StatusFragment activity) {
            mStatus = new WeakReference<StatusFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            StatusFragment status = mStatus.get();
            if (status == null) {
                return;
            }

            switch (msg.what) {
                case EVENT_UPDATE_STATS:
                    status.updateTimes();
                    sendEmptyMessageDelayed(EVENT_UPDATE_STATS, 1000);
                    break;

                case EVENT_UPDATE_CONNECTIVITY:
                    status.updateConnectivity();
                    break;
            }
        }
    }



    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.device_info_status, rootKey);
        mIpAddress = findPreference(KEY_IP_ADDRESS);
        mWifiMacAddress = findPreference(KEY_WIFI_MAC_ADDRESS);
        mEthernetMacAddress = findPreference(KEY_ETHERNET_MAC_ADDRESS);
        mWifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        mUptime = findPreference("up_time");
        mHandler = new MyHandler(this);

        getIP();


        mWifiMacAddress.setSummary(getMacAddr());
        mEthernetMacAddress.setSummary(DeviceUtils.getEthernetMacBySeparator(":").toUpperCase());

        String serial = Build.SERIAL;
        if (serial != null && !serial.equals("")) {
            setSummaryText(KEY_SERIAL_NUMBER, serial);
        } else {
            removePreferenceFromScreen(KEY_SERIAL_NUMBER);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(EVENT_UPDATE_STATS);
        getActivity().setTitle("状态信息");
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(EVENT_UPDATE_STATS);
    }

    void updateConnectivity() {
        mWifiMacAddress.setSummary(getMacAddr());
        mEthernetMacAddress.setSummary(DeviceUtils.getEthernetMacBySeparator(":").toUpperCase());
        getIP();
    }

    public void getIP(){
        String ip = "不可用";
        ConnectivityManager conMann = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobileNetworkInfo.isConnected()) {
            ip = IpUtil.getIPAddress(true);
        }else if(wifiNetworkInfo.isConnected())
        {
//            WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = IpUtil.intToIp(ipAddress);
        }
        if (!"不可用".equals(ip)){
            ip += "\n";
            ip += IpUtil.getIPAddress(false);
        }
        mIpAddress.setSummary(ip);
    }

    private void setSummaryText(String preference, String text) {
        if (TextUtils.isEmpty(text)) {
            text = "未知";
        }
        // some preferences may be missing
        if (findPreference(preference) != null) {
            findPreference(preference).setSummary(text);
        }
    }

    private void removePreferenceFromScreen(String key) {
        Preference pref = findPreference(key);
        if (pref != null) {
            getPreferenceScreen().removePreference(pref);
        }
    }


    void updateTimes() {
        long at = SystemClock.uptimeMillis() / 1000;
        long ut = SystemClock.elapsedRealtime() / 1000;

        if (ut == 0) {
            ut = 1;
        }

        mUptime.setSummary(convert(ut));
    }

    private String pad(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }

    private String convert(long t) {
        int s = (int)(t % 60);
        int m = (int)((t / 60) % 60);
        int h = (int)((t / 3600));

        return h + ":" + pad(m) + ":" + pad(s);
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

}

