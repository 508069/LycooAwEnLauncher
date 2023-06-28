package com.lycoo.commons.util;

import android.annotation.SuppressLint;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    @SuppressLint("DefaultLocale")
    public static boolean checkMd5(String md5, String file) {
        return checkMd5(md5, new File(file));
    }

    public static boolean checkMd5(String md5, File file) {
        String str = createMd5(file);
        return md5.toUpperCase().compareTo(str.toUpperCase()) == 0;
    }

    @SuppressLint("DefaultLocale")
    public static String createMd5(File file) {
        MessageDigest mMDigest;
        FileInputStream Input;
        byte buffer[] = new byte[1024];
        int len;
        if (!file.exists())
            return null;
        try {
            mMDigest = MessageDigest.getInstance("MD5");
            Input = new FileInputStream(file);
            while ((len = Input.read(buffer, 0, 1024)) != -1) {
                mMDigest.update(buffer, 0, len);
            }
            Input.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        BigInteger mBInteger = new BigInteger(1, mMDigest.digest());
        return StringUtils.leftPad(mBInteger.toString(16), 32, "0");
    }

    public static String generateMd5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }


}
