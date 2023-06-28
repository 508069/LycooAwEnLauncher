package com.lycoo.desktop;

import android.content.Context;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.db.DesktopDbManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.lycoo.desktop.config.DesktopConstants;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = ExampleInstrumentedTest.class.getSimpleName();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

    }

    @Test
    public void test_getContainerItemPackageNames() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<String> containerItemPackageNames = DesktopDbManager
                .getInstance(appContext)
                .getContainerItemPackageNames(
                        DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_CONTAINER_TYPE + " = ?",
                        new String[]{String.valueOf(32)});
        LogUtils.debug(TAG, "containerItemPackageNames : " + containerItemPackageNames);

    }
}
