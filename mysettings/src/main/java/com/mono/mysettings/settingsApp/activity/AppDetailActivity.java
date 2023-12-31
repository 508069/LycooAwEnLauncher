package com.mono.mysettings.settingsApp.activity;

import android.content.pm.ApplicationInfo;
import com.mono.mysettings.settingsApp.fragment.AppDetailFragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class AppDetailActivity extends AppCompatActivity {

    private static ApplicationInfo app;

    public void setApp(ApplicationInfo app) {
        AppDetailActivity.app = app;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDetailFragment fragment = new AppDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("app", app);
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }
}
