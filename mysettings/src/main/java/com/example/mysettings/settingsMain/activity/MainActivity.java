package com.example.mysettings.settingsMain.activity;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import com.example.mysettings.R;
import com.example.mysettings.settingsMain.fragment.SettingsFragment;


public class MainActivity extends AppCompatActivity{

    private Context mContext = this;
//    private SettingsAdapter settingsAdapter;
//
//    private static List<SettingListInfo> mList;
//
//    private ListView lv_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        lv_settings = findViewById(R.id.lv_settings);
//        mList = SettingListInfo.getDefaultList();
//        settingsAdapter = new SettingsAdapter(mContext, mList);
//        lv_settings.setAdapter(settingsAdapter);
//        lv_settings.setOnItemClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(mContext)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                mContext.startActivity(intent);
                return;
            }
        }

        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        String title = mList.get(position).getTitle();
//        switch (title){
//            case "声音设置":
//                Intent volIntent = new Intent(MainActivity.this, SoundSettingsActivity.class);
//                startActivity(volIntent);
//                break;
//            case "应用":
//                Intent appIntent = new Intent(MainActivity.this, AppListActivity.class);
//                startActivity(appIntent);
//                break;
//            case "备份和重置":
//                Intent resetIntent = new Intent(MainActivity.this, ResetDeviceActivity.class);
//                startActivity(resetIntent);
//                break;
//        }
//    }

}