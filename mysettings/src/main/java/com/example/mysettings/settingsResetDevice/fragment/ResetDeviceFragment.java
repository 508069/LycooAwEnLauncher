package com.example.mysettings.settingsResetDevice.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mysettings.R;
import com.example.mysettings.settingsResetDevice.receiver.MyDeviceAdminReceiver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResetDeviceFragment extends PreferenceFragmentCompat {

    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
//    private Context mContext;
//
//
//    public ResetDeviceFragment(Context mContext) {
//        this.mContext = mContext;
//    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.reset_preferences, rootKey);

        devicePolicyManager = (DevicePolicyManager) getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(getContext(), MyDeviceAdminReceiver.class);

        Preference resetPref = findPreference("reset_device");
        resetPref.setOnPreferenceClickListener(preference -> {
            showResetConfirmationDialog();
            return true;
        });

        Preference recoveryPref = findPreference("recovery");
        recoveryPref.setOnPreferenceClickListener(preference -> {
            showRecoveryConfirmationDialog();
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("备份和重置");
    }

    private void showRecoveryConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("警告");
        builder.setMessage("设备即将进入Recovery模式，建议先备份好数据，确定要继续吗？");
        builder.setPositiveButton("确定", (dialog, which) -> rebootRecovery());
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showResetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("警告");
        builder.setMessage("恢复出厂设置将删除所有数据，确定要继续吗？");
        builder.setPositiveButton("确定", (dialog, which) -> resetDevice());
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void resetDevice() {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.wipeData(0);
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "需要管理员权限才能执行恢复出厂设置操作");
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        }
    }

    private void recoveryMode() {
        if (isRecovery()) {
            Toast.makeText(getContext(), "Recovery模式进入成功！", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "Recovery模式进入失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isRecovery() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes("reboot recovery\n");
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            process.waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void rebootRecovery() {
        File RECOVERY_DIR = new File("/cache/recovery");
        File COMMAND_FILE = new File(RECOVERY_DIR, "command");
        String SHOW_TEXT="--show_text";
        RECOVERY_DIR.mkdirs();
        // In case we need it                   COMMAND_FILE.delete();
        // In case it's not writable
        try {
            FileWriter command = new FileWriter(COMMAND_FILE);
            command.write(SHOW_TEXT);
            command.write("\n");
            command.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        pm.reboot("recovery");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == RESULT_OK) {
                resetDevice();
            } else {
                Toast.makeText(getContext(), "需要管理员权限才能执行恢复出厂设置操作", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
