package com.lycoo.commons;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import com.lycoo.commons.util.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.lycoo.commons.test", appContext.getPackageName());
    }
    @Test
    public void test_01() {
        String data = "http://store.7po.com/api/interface?mod=download&id=13&channel=meisaitu_ltdhttp://store.7po.com/api/interface?mod=download&id=13&channel=meisaitu_ltd";
//        String data = "lancy";
        String encodeData1 = Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
        LogUtils.debug("test", "data1 : " + String.valueOf(encodeData1));
    }
}
