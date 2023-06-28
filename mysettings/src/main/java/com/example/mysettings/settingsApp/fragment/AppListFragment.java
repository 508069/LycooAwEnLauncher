package com.example.mysettings.settingsApp.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.mysettings.R;
import com.example.mysettings.settingsApp.activity.AppDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class AppListFragment extends PreferenceFragmentCompat {
    private static final String PREFERENCE_CATEGORY_APPS = "preference_category_apps";

    private PackageManager packageManager;
    private PreferenceCategory preferenceCategoryApps;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.app_list, rootKey);

        packageManager = getActivity().getPackageManager();

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceCategoryApps = (PreferenceCategory) preferenceScreen.findPreference(PREFERENCE_CATEGORY_APPS);

        inItAppList();

    }

    @Override
    public void onResume() {
        super.onResume();
        inItAppList();
    }

    private List<ApplicationInfo> getInstalledApps() {
        PackageManager pm = getContext().getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
        List<ApplicationInfo> result = new ArrayList<>();
        for (ApplicationInfo app : apps) {
            if (isSystemPackage(app)) {
                result.add(app);
            }
        }
        return result;
    }

    private boolean isSystemPackage(ApplicationInfo app) {
//        return true;
//        显示所有应用
        return (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        //显示已安装应用
//        return (app.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
    }

    private void inItAppList(){
        List<ApplicationInfo> apps = getInstalledApps();
        for (ApplicationInfo app : apps) {
            Preference preference = new Preference(getActivity());
            preference.setTitle(app.loadLabel(packageManager));
            String packageName = app.packageName;
            preference.setSummary(packageName);
            preference.setIcon(app.loadIcon(packageManager));
            preference.setOnPreferenceClickListener(preference1 -> {
                AppDetailActivity appDetailActivity = new AppDetailActivity();
                appDetailActivity.setApp(app);
                Intent intent = new Intent(getContext(), AppDetailActivity.class);
                startActivity(intent);
                return true;
            });
            preferenceCategoryApps.addPreference(preference);
        }
    }
}
