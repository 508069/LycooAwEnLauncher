package com.mono.mysettings.settingsSound.activity;

import com.mono.mysettings.settingsSound.fragment.TouchFragment;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mysettings.R;

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
