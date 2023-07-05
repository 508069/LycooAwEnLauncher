package com.mono.mysettings.settingInputAndLanguage.preference;

import android.content.Context;
import android.content.Intent;

import com.lycoo.commons.util.LogUtils;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.mysettings.R;
import com.mono.mysettings.settingInputAndLanguage.Bean.CurrentLocale;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class LanguagePreference extends PreferenceFragmentCompat {
    private static final String TAG = LanguagePreference.class.getSimpleName();
    private static final String KEY_LANGUAGE_PREFERENCE = "language_preference";
    private PreferenceScreen language_preference;
    private static List<CurrentLocale> currentLocaleList;




    public LanguagePreference() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.language_setting);
        init();
        setListLanguage();
    }

    private void setListLanguage() {
        // 获取当前系统的语言
        Locale systemLocale = Locale.getDefault();
        LogUtils.info(TAG, "当前系统语言 ： " + systemLocale.toString());
        for (int i = 0; i < currentLocaleList.size(); i++) {
            CurrentLocale currentLocale = currentLocaleList.get(i);
            Preference preference = new Preference(getContext());
            preference.setTitle(currentLocale.getName());
//            LogUtils.info(TAG, "系统语言 ： " + currentLocale.toString());
            if (currentLocale.getLocale() == systemLocale) {
                preference.setSummary("当前");
            }
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    updateLocale(currentLocale.getLocale());
                    return true;
                }
            });
            language_preference.addPreference(preference);
        }
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this.getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.getContext().startActivity(intent);
            }
        }
        if (currentLocaleList == null) {
            currentLocaleList = new ArrayList<>();
            getLocales();
        }
        language_preference = findPreference(KEY_LANGUAGE_PREFERENCE);

    }

    // 创建一个中文语言环境的Collator对象
    Collator collator = Collator.getInstance(Locale.CHINA);

    /**
     * 比较规则，中文放在最前面，英文从A到Z排序
     * Create by HonceH 23/5/25
     */
    Comparator<CurrentLocale> comparator = new Comparator<CurrentLocale>() {
        @Override
        public int compare(CurrentLocale value1, CurrentLocale value2) {
            boolean isChinese1 = value1.getName().matches("[\\u4E00-\\u9FA5]+");
            boolean isChinese2 = value2.getName().matches("[\\u4E00-\\u9FA5]+");

            if (isChinese1 == isChinese2) {
                // 如果都是中文字符串或都不是中文字符串，则使用Collator进行比较
                return collator.compare(value1.getName(), value2.getName());
            } else if (isChinese1) {
                // 如果value1是中文字符串而value2不是，则value1排在前面
                return -1;
            } else {
                // 如果value2是中文字符串而value1不是，则value2排在前面
                return 1;
            }
        }
    };

    /**
     * 修改系统语言
     *
     * @param locale Create by HonceH 23/5/25
     */
    private void updateLocale(Locale locale) {
        try {
            Class<?> localePickerClass = Class.forName("com.android.internal.app.LocalePicker");
            Method updateLocaleMethod = localePickerClass.getDeclaredMethod("updateLocale", Locale.class);
            updateLocaleMethod.setAccessible(true);
            updateLocaleMethod.invoke(null, locale);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取系统能使用语言的集合
     * Create by HonceH 23/5/25
     */
    private void getLocales() {
        try {
            Class<?> localeStoreClass = Class.forName("com.android.internal.app.LocaleStore");
            Class<?> localeInfoClass = Class.forName("com.android.internal.app.LocaleStore$LocaleInfo");
            Method getLevelLocalesMethod = localeStoreClass.getDeclaredMethod("getLevelLocales", Context.class, Set.class, localeInfoClass, boolean.class);
            getLevelLocalesMethod.setAccessible(true);
            HashSet<String> langTagsToIgnore = new HashSet<>();
            Set<?> mLocaleList = (Set<?>) getLevelLocalesMethod.invoke(null, getContext(), langTagsToIgnore, null, false);
            for (Object localeInfo : mLocaleList) {
                Method getLocale = localeInfo.getClass().getMethod("getLocale");
                Locale locale = (Locale) getLocale.invoke(localeInfo);
                Method Name = localeInfo.getClass().getMethod("getFullNameNative");
                String name = (String) Name.invoke(localeInfo);
                CurrentLocale currentLocale = new CurrentLocale(name, locale);
                currentLocaleList.add(currentLocale);
            }
            Collections.sort(currentLocaleList, comparator); // 将中文字符串排在最前面，非中文字符串按照字母顺序排序
        } catch (ClassNotFoundException e) {
            // 处理类未找到的异常
            e.printStackTrace();
            LogUtils.error(TAG, "处理类未找到");
        } catch (NoSuchMethodException e) {
            // 处理方法未找到的异常
            e.printStackTrace();
            LogUtils.error(TAG, "处理方法未找到");
        } catch (IllegalAccessException e) {
            // 处理非法访问异常
            e.printStackTrace();
            LogUtils.error(TAG, "处理非法访问异常");
        } catch (InvocationTargetException e) {
            // 处理调用目标异常
            e.printStackTrace();
            LogUtils.error(TAG, "处理调用目标异常");
        }
    }
}
