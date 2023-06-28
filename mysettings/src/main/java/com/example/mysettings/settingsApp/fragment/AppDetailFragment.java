package com.example.mysettings.settingsApp.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mysettings.settingsApp.manager.DataCleanManager;
import com.example.mysettings.R;

public class AppDetailFragment extends PreferenceFragmentCompat {

    private ApplicationInfo mApp;
    private PackageManager pm;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);

        // 获取应用信息
        pm = requireActivity().getPackageManager();
        mApp = getArguments().getParcelable("app");

        // 设置应用名称
        Preference appNamePref = findPreference("app_name");
        appNamePref.setSummary(mApp.loadLabel(pm));

        // 设置应用包名
        Preference appPackagePref = findPreference("app_package");
        appPackagePref.setSummary(mApp.packageName);

        // 设置应用版本
        try {
            PackageInfo pInfo = pm.getPackageInfo(mApp.packageName, 0);
            Preference appVersionPref = findPreference("app_version");
            appVersionPref.setSummary(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // 设置清除数据按钮的点击事件
        Preference clearDataPref = findPreference("app_clear_data");
        clearDataPref.setOnPreferenceClickListener(preference -> {
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            Uri uri = Uri.fromParts("package", mApp.packageName, null);
//            intent.setData(uri);
//            startActivity(intent);
            showCleanDataDialog();
            return true;
        });

        // 设置卸载应用按钮的点击事件
        Preference uninstallPref = findPreference("app_uninstall");
        uninstallPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + mApp.packageName));
            startActivity(intent);
            return true;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(mApp.loadLabel(pm));
        // 如果应用被卸载，返回上一级页面
        if (!isApplicationInstalled()) {
            getActivity().onBackPressed();
        }
    }

    private boolean isApplicationInstalled() {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(mApp.packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void showCleanDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("警告");
        builder.setMessage("即将清除应用数据，确定要继续吗？");
        builder.setPositiveButton("确定", (dialog, which) -> cleanData());
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void cleanData(){
        DataCleanManager.clearAppUserData(mApp.packageName);
    }
}


