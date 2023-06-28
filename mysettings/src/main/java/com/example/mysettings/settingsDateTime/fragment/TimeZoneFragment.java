package com.example.mysettings.settingsDateTime.fragment;

import android.app.AlarmManager;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.mysettings.R;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TimeZoneFragment extends PreferenceFragmentCompat {
    private static final String PREFERENCE_CATEGORY_TIME_ZONE = "preference_category_time_zone";
    private PreferenceCategory preferenceCategoryTimeZone;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.time_zone_prefs, rootKey);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceCategoryTimeZone = preferenceScreen.findPreference(PREFERENCE_CATEGORY_TIME_ZONE);
        InitTimeZoneList();

    }


    private List<String> getTimeZoneIds(){
        String[] timeZoneIds = TimeZone.getAvailableIDs();
        List<String> zoneIds = Arrays.asList(timeZoneIds);
        Collections.sort(zoneIds);
        List<String> result = new ArrayList<>();
        for(String timeZoneId: zoneIds){
            result.add(timeZoneId);
        }
        return result;
    }

    private void InitTimeZoneList(){
        List<String> zoneIds = getTimeZoneIds();
        for (String id : zoneIds){
            Preference preference = new Preference(getActivity());
            TimeZone timeZone = TimeZone.getTimeZone(id, TimeZone.LONG);
            String zoneDisplayName = timeZone.getDisplayName(Locale.CHINESE);
            preference.setTitle(zoneDisplayName);
            preference.setOnPreferenceClickListener(preference1 -> {
                AlarmManager alarmManager = getContext().getSystemService(AlarmManager.class);
                alarmManager.setTimeZone(id);
                getActivity().onBackPressed();
                return true;
            });
            preferenceCategoryTimeZone.addPreference(preference);
        }
    }
}
