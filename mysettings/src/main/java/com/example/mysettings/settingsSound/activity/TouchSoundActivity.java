package com.example.mysettings.settingsSound.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mysettings.R;
import com.example.mysettings.settingsSound.fragment.TouchFragment;

public class TouchSoundActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 加载 preferences.xml 文件
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new TouchFragment())
                .commit();

    }

}
