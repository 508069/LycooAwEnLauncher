package com.mono.mysettings.settingInputAndLanguage.preference;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.mysettings.R;


public class InputMethodAndLanguagePreference extends PreferenceFragmentCompat{

    public InputMethodAndLanguagePreference(){}
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.input_language_settings,rootKey);
//        preferenceScreen = findPreference("language");
//        preferenceScreen.setOnPreferenceClickListener(this);
//        if (preferenceScreen != null) {
//            LanguagePreference languagePreference = new LanguagePreference();
//            getChildFragmentManager().beginTransaction()
//                    .replace(android.R.id.content, languagePreference)
//                    .commit();
//        }
    }

//    @Override
//    public boolean onPreferenceClick(Preference preference) {
//        if(preference==preferenceScreen){
//            LanguagePreference languagePreference = new LanguagePreference();
//            getChildFragmentManager().beginTransaction()
//                    .replace(android.R.id.content, languagePreference)
//                    .commit();
//            LogUtils.debug("test","11");
//            getActivity().getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(android.R.id.content,new LanguagePreference())
//                    .commit();
//        }
//        return true;
//    }
}
