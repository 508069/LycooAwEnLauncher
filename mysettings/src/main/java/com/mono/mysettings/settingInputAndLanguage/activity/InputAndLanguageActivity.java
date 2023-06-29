package com.mono.mysettings.settingInputAndLanguage.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mysettings.R;
import com.mono.mysettings.settingInputAndLanguage.preference.InputMethodAndLanguagePreference;


public class InputAndLanguageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputandlanguage);
        InputMethodAndLanguagePreference fragment=new InputMethodAndLanguagePreference();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }
}
