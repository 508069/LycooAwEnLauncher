package com.lycoo.commons;

import android.content.Context;
import com.lycoo.commons.util.LogUtils;
import com.rockchip.keily.jni.ReadPrivate;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 */
@RunWith(AndroidJUnit4.class)
public class PrivateStorageTest {
    private static final String TAG = PrivateStorageTest.class.getSimpleName();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.lycoo.commons.test", appContext.getPackageName());
    }
    @Test
    public void test_01() {
        String activateCode = ReadPrivate.readActivateCode();
        String authorizeCode = ReadPrivate.readAuthorizeCode();
        String dynamicCode = ReadPrivate.readDynamicCode();
        LogUtils.debug(TAG, "activateCode : [" + activateCode + "]");
        if (!TextUtils.isEmpty(activateCode)){
            LogUtils.debug(TAG, "activateCode len = " + activateCode.length());
        }

        LogUtils.debug(TAG, "authorizeCode : [" + authorizeCode + "]");
        if (!TextUtils.isEmpty(authorizeCode)){
            LogUtils.debug(TAG, "authorizeCode len = " + authorizeCode.length());
        }

        LogUtils.debug(TAG, "dynamicCode : [" + dynamicCode + "]");
        if (!TextUtils.isEmpty(dynamicCode)){
            LogUtils.debug(TAG, "dynamicCode len = " + dynamicCode.length());
        }
    }

    @Test
    public void test_02() {
        ReadPrivate.writeActivateCode("abcdefghijklmnopqddfsadfasdgfasg");
    }
}
