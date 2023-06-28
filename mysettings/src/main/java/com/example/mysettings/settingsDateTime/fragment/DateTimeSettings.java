package com.example.mysettings.settingsDateTime.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.TimeZoneNames;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.mysettings.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeSettings extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
    private static final String KEY_AUTO_TIME = "auto_time";
    private Preference mTimePref;
    private Preference mDatePref;
    private SwitchPreference mAutoTimePref;
    private Preference mTime24Pref;
    private Calendar mDummyDate;
    private Preference mTimeZone;
    // Minimum time is Nov 5, 2007, 0:00.
    private static final long MIN_DATE = 1194220800000L;

    private SharedPreferences mPrefs;


    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        boolean autoTimeEnabled = getAutoState(Settings.Global.AUTO_TIME);

        setPreferencesFromResource(R.xml.date_time_prefs, rootKey);
        mAutoTimePref = findPreference(KEY_AUTO_TIME);
        mTimePref = findPreference("time");
        mDatePref = findPreference("date");
        mTime24Pref = findPreference("24 hour");
        mTimeZone = findPreference("timezone");
        mDummyDate = Calendar.getInstance();
        mAutoTimePref.setChecked(autoTimeEnabled);
        mAutoTimePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean autoEnabled = (Boolean) newValue;
                Settings.Global.putInt(getContext().getContentResolver(), Settings.Global.AUTO_TIME,
                        autoEnabled ? 1 : 0);
                mTimePref.setEnabled(!autoEnabled);
                mDatePref.setEnabled(!autoEnabled);
                return true;
            }
        });

        mTime24Pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                int timeFormat = (Boolean) newValue ? 24 : 12;
                android.provider.Settings.System.putString(getContext().getContentResolver(), android.provider.Settings.System.TIME_12_24, String.valueOf(timeFormat));
                updateTimeAndDateDisplay(getActivity());
                return true;
            }
        });

        mTimePref.setEnabled(!autoTimeEnabled);
        mDatePref.setEnabled(!autoTimeEnabled);

        mTimePref.setOnPreferenceClickListener(this);
        mDatePref.setOnPreferenceClickListener(this);
//        mTimeZone.setOnPreferenceClickListener(this);
    }

    private boolean getAutoState(String name) {
        try {
            return Settings.Global.getInt(getContext().getContentResolver(), name) > 0;
        } catch (Settings.SettingNotFoundException snfe) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("日期和时间");
        updateTimeAndDateDisplay(getActivity());
        ((SwitchPreference)mTime24Pref).setChecked(is24Hour());
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateTimeAndDateDisplay(Context context) {
        final Calendar now = Calendar.getInstance();
        mDummyDate.setTimeZone(now.getTimeZone());
        // We use December 31st because it's unambiguous when demonstrating the date format.
        // We use 13:00 so we can demonstrate the 12/24 hour options.
        mDummyDate.set(now.get(Calendar.YEAR), 11, 31, 13, 0, 0);
        Date dummyDate = mDummyDate.getTime();
        mDatePref.setSummary(DateFormat.getLongDateFormat(context).format(now.getTime()));
        mTimePref.setSummary(DateFormat.getTimeFormat(getActivity()).format(now.getTime()));
        mTimeZone.setSummary(getTimeZoneOffsetAndName(now.getTimeZone(), now.getTime()));
        mTime24Pref.setSummary(DateFormat.getTimeFormat(getActivity()).format(dummyDate));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getTimeZoneOffsetAndName(TimeZone tz, Date now) {
        Locale locale = Locale.getDefault();
        String gmtString = getGmtOffsetString(locale, tz, now);
        TimeZoneNames timeZoneNames = TimeZoneNames.getInstance(locale);
        String zoneNameString = getZoneLongName(timeZoneNames, tz, now);
        if (zoneNameString == null) {
            return gmtString;
        }

        // We don't use punctuation here to avoid having to worry about localizing that too!
        return gmtString + " " + zoneNameString;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String getZoneLongName(TimeZoneNames names, TimeZone tz, Date now) {
        TimeZoneNames.NameType nameType =
                tz.inDaylightTime(now) ? TimeZoneNames.NameType.LONG_DAYLIGHT
                        : TimeZoneNames.NameType.LONG_STANDARD;
        return names.getDisplayName(tz.getID(), nameType, now.getTime());
    }

    private static String getGmtOffsetString(Locale locale, TimeZone tz, Date now) {
        // Use SimpleDateFormat to format the GMT+00:00 string.
        SimpleDateFormat gmtFormatter = new SimpleDateFormat("ZZZZ");
        gmtFormatter.setTimeZone(tz);
        String gmtString = gmtFormatter.format(now);

        // Ensure that the "GMT+" stays with the "00:00" even if the digits are RTL.
        BidiFormatter bidiFormatter = BidiFormatter.getInstance();
        boolean isRtl = TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL;
        gmtString = bidiFormatter.unicodeWrap(gmtString,
                isRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR);
        return gmtString;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        switch (preference.getKey()){
            case "time":
                // 显示时间设置对话框
                showTimePickerDialog();
                break;
            case "date":
                // 显示日期设置对话框
                showDatePickerDialog();
                break;
//            case "timezone":
//                Intent intent = new Intent(getActivity(), TimeZoneActivity.class);
//                intent.setComponent(new ComponentName("com.example.mysettings.activity",
//                        "com.example.mysettings.activity.TimeZoneActivity"));
//                startActivity(intent);
//                break;
            default:
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showTimePickerDialog() {
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 创建时间选择器对话框
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 处理时间设置事件
                        final Activity activity = getActivity();
                        if (activity != null) {
                            setTime(activity, hourOfDay, minute);
                            updateTimeAndDateDisplay(activity);
                        }

                        // We don't need to call timeUpdated() here because the TIME_CHANGED
                        // broadcast is sent by the AlarmManager as a side effect of setting the
                        // SystemClock time.
                    }
                }, hour, minute, DateFormat.is24HourFormat(getActivity()));

        // 显示时间选择器对话框
        timePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDatePickerDialog() {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // 创建日期选择器对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 处理日期设置事件
                        final Activity activity = getActivity();
                        if (activity != null) {
                            setDate(activity, year, month, dayOfMonth);
                            updateTimeAndDateDisplay(activity);
                        }
                    }
                }, year, month, dayOfMonth);

        // 显示日期选择器对话框
        datePickerDialog.show();
    }

    /* package */ static void setTime(Context context, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long when = Math.max(c.getTimeInMillis(), MIN_DATE);

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    /* package */ static void setDate(Context context, int year, int month, int day) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = Math.max(c.getTimeInMillis(), MIN_DATE);

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(getActivity());
    }

}
