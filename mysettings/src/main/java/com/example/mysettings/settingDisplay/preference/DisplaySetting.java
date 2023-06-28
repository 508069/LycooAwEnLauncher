package com.example.mysettings.settingDisplay.preference;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.example.mysettings.R;
import com.lycoo.commons.helper.SystemPropertiesManager;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 设置的显示页面
 */
public class DisplaySetting extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = DisplaySetting.class.getSimpleName();

    private static final String BOOT_VIDEO_FILE_PATH = "/system/media/welcome.mp4";
    private static final String KEY_GLOBAL_DISPLAY = "display_global";
    private static final String KEY_GLOBAL_ARC_MENU = "global_arc_menu";
    private static final String KEY_GLOBAL_FLOATED_BACK = "global_floated_back";
    private static final String KEY_GLOBAL_BOOT_VIDEO = "global_boot_video";
    private static final String KEY_GLOBAL_DUAL_SCREEN = "global_dual_screen";
    private static final String KEY_HDMI_OUTPUT_MODE = "hdmi_output_mode";
    private static final String KEY_HDMI_WIDTH_SCALE = "hdmi_width_scale";
    private static final String KEY_HDMI_HEIGHT_SCALE = "hdmi_height_scale";
    public static final String HDMI_WSCALE = "hdmi_width_percent";
    public static final String HDMI_HSCALE = "hdmi_height_percent";
    private static final String PROPERTY_GLOBAL_ARC_MENU = "persist.sys.arc_menu.enable";
    private static final String PROPERTY_GLOBAL_FLOATED_BACK = "persist.sys.floated_back.enable";
    private static final String PROPERTY_GLOBAL_BOOT_VIDEO = "persist.sys.boot_video.enable";
    private static final String PROPERTY_GLOBAL_DUAL_SCREEN = "persist.sys.dual_screen.enable";
    private SwitchPreference mGlobalArcMenu;
    private SwitchPreference mGlobalFloatedBack;
    private SwitchPreference mGlobalBootVideo;
    private SwitchPreference mGlobalDualScreen;
    private ListPreference mHdmiOutputModePreference;
    private PreferenceCategory mGlobalDisplay;
    private SeekBarPreference mHdmiWidthScale;
    private SeekBarPreference mHdmiHeightScale;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        setPreferencesFromResource(R.xml.display_settings, rootKey);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.display_settings);
        init();
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
        final ContentResolver resolver = getActivity().getContentResolver();
        mGlobalDisplay = findPreference(KEY_GLOBAL_DISPLAY);

        mGlobalArcMenu = findPreference(KEY_GLOBAL_ARC_MENU);
        mGlobalArcMenu.setOnPreferenceChangeListener(this);

        mGlobalFloatedBack = findPreference(KEY_GLOBAL_FLOATED_BACK);
        mGlobalFloatedBack.setOnPreferenceChangeListener(this);

        mGlobalBootVideo = findPreference(KEY_GLOBAL_BOOT_VIDEO);
        mGlobalBootVideo.setOnPreferenceChangeListener(this);
        if (!isBootVideoExist()) {
            mGlobalDisplay.removePreference(mGlobalBootVideo);
        }

        mGlobalDualScreen = findPreference(KEY_GLOBAL_DUAL_SCREEN);
        mGlobalDualScreen.setOnPreferenceChangeListener(this);

        mHdmiOutputModePreference = findPreference(KEY_HDMI_OUTPUT_MODE);
        final int currentHdmiMode = Settings.System.getInt(resolver, KEY_HDMI_OUTPUT_MODE, 0);
        mHdmiOutputModePreference.setValue(String.valueOf(currentHdmiMode));
        Settings.System.putInt(resolver, KEY_HDMI_OUTPUT_MODE, currentHdmiMode);
        mHdmiOutputModePreference.setOnPreferenceChangeListener(this);

        mHdmiWidthScale = findPreference(KEY_HDMI_WIDTH_SCALE);
        mHdmiWidthScale.setMax(10);
        final int curWScaler = Settings.System.getInt(resolver, HDMI_WSCALE, 100);
        mHdmiWidthScale.setValue(100 - curWScaler);
        mHdmiWidthScale.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int progress =(int)newValue;
                LogUtils.info(TAG, "width : " + progress);
                Settings.System.putInt(getActivity().getContentResolver(), HDMI_WSCALE, 100 - progress);
                setExternDisplayMode(4, 100 - progress);
                return true;
            }
        });

        mHdmiHeightScale = findPreference(KEY_HDMI_HEIGHT_SCALE);
        int curHScaler = Settings.System.getInt(resolver, HDMI_HSCALE, 100);
        mHdmiHeightScale.setMax(10);
        mHdmiHeightScale.setValue(100-curHScaler);
        mHdmiHeightScale.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int progress =(int)newValue;
                LogUtils.info(TAG, "height : " + progress);
                Settings.System.putInt(getActivity().getContentResolver(), HDMI_HSCALE, 100 - progress);
                setExternDisplayMode(3, 100 - progress);
                return true;
            }
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mGlobalArcMenu) {
            toggleGlobalArcMen();
        }
        if (preference == mGlobalFloatedBack) {
            toggleGlobalFloatedBack();
        }
        if (preference == mGlobalBootVideo) {
            toggleGlobalBootVideo();
        }
        if (preference == mGlobalDualScreen) {
            toggleGlobalDualScreen();
        }
        if (preference == mHdmiOutputModePreference) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(), KEY_HDMI_OUTPUT_MODE, value);
            setExternDisplayMode(2, value);
        }

        return true;
    }

    /**
     * 使用反射调用内核层代码   SurfaceControl.setExternDisplayMode(2, value);
     *
     * @param type
     * @param mode
     */
    private void setExternDisplayMode(int type, int mode) {
        try {
            Class<?> surfaceControlClass = Class.forName("android.view.SurfaceControl");
            Method setExternDisplayModeMethod = surfaceControlClass.getDeclaredMethod("setExternDisplayMode", int.class, int.class);
            setExternDisplayModeMethod.setAccessible(true);
            setExternDisplayModeMethod.invoke(null, type, mode);
        } catch (ClassNotFoundException e) {
            // 处理类未找到的异常
            LogUtils.error(TAG, "处理类未找到");
        } catch (NoSuchMethodException e) {
            // 处理方法未找到的异常
            LogUtils.error(TAG, "处理方法未找到");
        } catch (IllegalAccessException e) {
            // 处理非法访问异常
            LogUtils.error(TAG, "处理非法访问异常");
        } catch (InvocationTargetException e) {
            // 处理调用目标异常
            LogUtils.error(TAG, "处理调用目标异常");
        }

    }

    private boolean isBootVideoExist() {
        return new File(BOOT_VIDEO_FILE_PATH).exists();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateState();
    }

    private void updateState() {
        if (mGlobalArcMenu != null) {
            mGlobalArcMenu.setChecked(isGlobalArcMenEnable());
        }
        if (mGlobalFloatedBack != null) {
            mGlobalFloatedBack.setChecked(isGlobalFloatedBackEnable());
        }
        if (mGlobalBootVideo != null) {
            mGlobalBootVideo.setChecked(isGlobalBootVideoEnable());
        }

        if (mGlobalDualScreen != null) {
            mGlobalDualScreen.setChecked(isGlobalDualScreenEnable());
        }
    }

    /**
     * 当前是否显示arcmenu
     *
     * @return
     */
    private boolean isGlobalArcMenEnable() {
        return SystemPropertiesUtils.getBoolean(PROPERTY_GLOBAL_ARC_MENU, false);
    }

    /**
     * 当前是否显示返回FloatedBack
     *
     * @return
     */
    private boolean isGlobalFloatedBackEnable() {
        return SystemPropertiesUtils.getBoolean(PROPERTY_GLOBAL_FLOATED_BACK, false);
    }

    /**
     * 当前是否显示返回BootVideo
     *
     * @return
     */
    private boolean isGlobalBootVideoEnable() {
        return SystemPropertiesUtils.getBoolean(PROPERTY_GLOBAL_BOOT_VIDEO, false);
    }

    /**
     * 当前是否显示返回DualScreen
     *
     * @return
     */
    private boolean isGlobalDualScreenEnable() {
        return SystemPropertiesUtils.getBoolean(PROPERTY_GLOBAL_DUAL_SCREEN, false);
    }

    /**
     * 显示/隐藏arcmenu
     */
    private void toggleGlobalArcMen() {
        boolean enable = isGlobalArcMenEnable();
        SystemPropertiesManager.getInstance(this.getContext()).set(PROPERTY_GLOBAL_ARC_MENU, enable ? "false" : "true");
        mGlobalArcMenu.setChecked(!enable);
    }

    private void toggleGlobalFloatedBack() {
        boolean enable = isGlobalFloatedBackEnable();
        SystemPropertiesManager.getInstance(this.getContext()).set(PROPERTY_GLOBAL_FLOATED_BACK, enable ? "false" : "true");
        mGlobalFloatedBack.setChecked(!enable);
    }

    private void toggleGlobalBootVideo() {
        boolean enable = isGlobalBootVideoEnable();
        SystemPropertiesManager.getInstance(this.getContext()).set(PROPERTY_GLOBAL_BOOT_VIDEO, enable ? "false" : "true");
        mGlobalBootVideo.setChecked(!enable);
    }

    private void toggleGlobalDualScreen() {
        boolean enable = isGlobalDualScreenEnable();
        SystemPropertiesManager.getInstance(this.getContext()).set(PROPERTY_GLOBAL_DUAL_SCREEN, enable ? "false" : "true");
        mGlobalDualScreen.setChecked(!enable);
    }
}
