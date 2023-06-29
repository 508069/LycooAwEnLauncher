package com.mono.mysettings.settingsDeviceInfo.fragment;

import com.lycoo.commons.util.SystemPropertiesUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.UserManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mysettings.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceInfoFragment extends PreferenceFragmentCompat {
    private static final String KEY_DEVICE_MODEL = "device_model";
    private static final String KEY_CPU_TYPE = "cpu_type";
    private static final String KEY_FIRMWARE_VERSION = "firmware_version";
    private static final String KEY_SOFTWARE_VERSION = "software_version";
    private static final String PROPETY_FIRMWARE_VERSION = "ro.product.firmware";
    private static final String KEY_KERNEL_VERSION = "kernel_version";
    private static final String KEY_BUILD_NUMBER = "build_number";
    private static final String KEY_DEVICE_STORAGE = "device_storage";

    private static final String FILENAME_PROC_VERSION = "/proc/version";
    private static final String PROPERTY_CPUTYPE = "ro.sys.cputype";

    private Preference mBuildNum;

    private UserManager mUm;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.device_info_preferences, rootKey);

//        mUm = UserManager.get(getActivity());

        setStringSummary(KEY_DEVICE_MODEL, Build.MODEL);
        setStringSummary(KEY_CPU_TYPE, SystemPropertiesUtils.get(PROPERTY_CPUTYPE));
        setStringSummary(KEY_FIRMWARE_VERSION, Build.VERSION.RELEASE);

        String lycooVersion = SystemPropertiesUtils.get("ro.lycoo.firmware.verion.name");
        String softwareVersion = SystemPropertiesUtils.get(PROPETY_FIRMWARE_VERSION)+"-LYCOO-V" + lycooVersion;
        setStringSummary(KEY_SOFTWARE_VERSION,softwareVersion);
        setStringSummary(KEY_BUILD_NUMBER, Build.DISPLAY);

        findPreference(KEY_KERNEL_VERSION).setSummary(getFormattedKernelVersion());

        String flashSize = getFlashSize();
        String ddrSize = getTotalMemory();
        setStringSummary(KEY_DEVICE_STORAGE, "DDR: " + ddrSize + ", FLASH: " + flashSize);

        mBuildNum = findPreference(KEY_BUILD_NUMBER);
//        mBuildNum.setOnPreferenceClickListener(preference -> {
//            Intent intent = new Intent();
//            intent.setClassName("com.android.settings", "com.android.settings.Settings$DevelopmentSettingsActivity");
//            getContext().startActivity(intent);
//            return true;
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("关于设备");
    }

    private void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                    getResources().getString(R.string.device_info_default));
        }
    }

    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }
    public static String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));
        } catch (IOException e) {
            Log.e("mono", "IO Exception when getting kernel version for Device Info screen",
                    e);

            return "Unavailable";
        }
    }

    public static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        final String PROC_VERSION_REGEX =
                "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
                        "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
                        "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
                        "(#\\d+) " +              /* group 3: "#1" */
                        "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
                        "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            Log.e("mono", "Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            Log.e("mono", "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
                m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }

    private String getFlashSize(){
        File path_sdcard = Environment.getExternalStorageDirectory();
        StatFs stat_sdcard = new StatFs(path_sdcard.getPath());
        long blockSize_sdcard = stat_sdcard.getBlockSize();
        long availableBlocks_sdcard = stat_sdcard.getAvailableBlocks();
        long totalBlocks_sdcard = stat_sdcard.getBlockCount();

        File path_data = Environment.getDataDirectory();
        StatFs stat_data = new StatFs(path_data.getPath());
        long blockSize_data = stat_data.getBlockSize();
        long availableBlocks_data = stat_data.getAvailableBlocks();
        long totalBlocks_data = stat_data.getBlockCount();

        //LogUtils.log(TAG, "********** path_data == " + path_data);

        long total = (blockSize_sdcard * totalBlocks_sdcard/*availableBlocks_sdcard*/) + (blockSize_data * totalBlocks_data);

        //long total = /*(blockSize_sdcard * availableBlocks_sdcard)*/ + (blockSize_data * totalBlocks_data);

        int flashTotal = Math.round(total/(1024*1024*1024));

        //LogUtils.log(TAG, "********** flashTotal == " + flashTotal);

        String flash = "8G";
        if(flashTotal < 4){
            flash = "4G";
        }else if(flashTotal >= 4 && flashTotal < 8){
            flash = "8G";
        }else if(flashTotal >= 8 && flashTotal < 16){
            flash = "16G";
        }else if(flashTotal >= 16 && flashTotal < 32){
            flash = "32G";
        }else if(flashTotal >= 32){
            flash = "64G";
        }

        return flash;
    }

    private String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }

        int flashTotal = Math.round(initial_memory/(1024*1024));
        if(flashTotal < 512){
            return "512M";
        }else if(flashTotal > 512 && flashTotal < 1024){
            return "1G";
        }else{
            //return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
            return "2G";
        }
    }

//    @Override
//    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
//        if (preference.getKey().equals(KEY_BUILD_NUMBER)) {
//            // Don't enable developer options for secondary users.
//            if (!mUm.isAdminUser()) return true;
//
//            // Don't enable developer options until device has been provisioned
//            if (!Utils.isDeviceProvisioned(getActivity())) {
//                return true;
//            }
//
//            if (mUm.hasUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES)) {
//                if (mDebuggingFeaturesDisallowedAdmin != null &&
//                        !mDebuggingFeaturesDisallowedBySystem) {
//                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(),
//                            mDebuggingFeaturesDisallowedAdmin);
//                }
//                return true;
//            }
//
//            if (mDevHitCountdown > 0) {
//                boolean showDev=getActivity().getSharedPreferences(DevelopmentSettings.PREF_FILE,
//                        Context.MODE_PRIVATE).getBoolean(DevelopmentSettings.PREF_SHOW,
//                        android.os.Build.TYPE.equals("eng") && SystemProperties.getBoolean("persist.development.showdef", false));
//                mDevHitCountdown--;
//                if (mDevHitCountdown == 0) {
//                    if(showDev){//add if by lcchen on 20190716
//                        getActivity().getSharedPreferences(DevelopmentSettings.PREF_FILE,
//                                Context.MODE_PRIVATE).edit().putBoolean(
//                                DevelopmentSettings.PREF_SHOW, false).apply();
//                        if (mDevHitToast != null) {
//                            mDevHitToast.cancel();
//                        }
//                        mDevHitToast = Toast.makeText(getActivity(), R.string.hide_dev_on,
//                                Toast.LENGTH_LONG);
//                        mDevHitToast.show();
//                    }else{
//                        getActivity().getSharedPreferences(DevelopmentSettings.PREF_FILE,
//                                Context.MODE_PRIVATE).edit().putBoolean(
//                                DevelopmentSettings.PREF_SHOW, true).apply();
//                        if (mDevHitToast != null) {
//                            mDevHitToast.cancel();
//                        }
//                        mDevHitToast = Toast.makeText(getActivity(), R.string.show_dev_on,
//                                Toast.LENGTH_LONG);
//                        mDevHitToast.show();
//                    }
//                    // This is good time to index the Developer Options
//                    Index.getInstance(
//                            getActivity().getApplicationContext()).updateFromClassNameResource(
//                            DevelopmentSettings.class.getName(), true, true);
//                    //add by lcchen on 20190716
//                    mDevHitCountdown = TAPS_TO_BE_A_DEVELOPER;
//                    //add by lcchen on 20190716,END
//                } else if (mDevHitCountdown > 0
//                        && mDevHitCountdown < (TAPS_TO_BE_A_DEVELOPER-2)) {
//                    if (mDevHitToast != null) {
//                        mDevHitToast.cancel();
//                    }
//                    if(showDev){//add if by lcchen on 20190716
//                        mDevHitToast = Toast.makeText(getActivity(), getResources().getQuantityString(
//                                        R.plurals.hide_dev_countdown, mDevHitCountdown, mDevHitCountdown),
//                                Toast.LENGTH_SHORT);
//                    }else{
//                        mDevHitToast = Toast.makeText(getActivity(), getResources().getQuantityString(
//                                        R.plurals.show_dev_countdown, mDevHitCountdown, mDevHitCountdown),
//                                Toast.LENGTH_SHORT);
//                    }
//                    mDevHitToast.show();
//                }
//            } else if (mDevHitCountdown < 0) {
//                if (mDevHitToast != null) {
//                    mDevHitToast.cancel();
//                }
//                mDevHitToast = Toast.makeText(getActivity(), R.string.show_dev_already,
//                        Toast.LENGTH_LONG);
//                mDevHitToast.show();
//            }
//        }
//        return super.onPreferenceTreeClick(preference);
//    }
}
