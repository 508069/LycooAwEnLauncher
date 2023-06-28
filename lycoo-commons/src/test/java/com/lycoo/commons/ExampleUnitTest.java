package com.lycoo.commons;

import com.lycoo.commons.util.RandomUtils;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_regix() {
//        String regix = "^[1-5]$"; // 只能输入1 ~ 5 之间的某个数字
        String regix = "^\\d{3}$";  // 匹配3个数字
        String data = "000";
        Matcher matcher = Pattern.compile(regix).matcher(data);
        System.out.println(matcher.matches());
    }

    @Test
    public void generateCode() {
        StringBuilder code = new StringBuilder();
        List<String> codeList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            code.append(RandomUtils.generateAuthorizationCode()).append("#");
        }
        System.out.print(code.toString());
    }

    @Test
    public void generateCode_02() {
        String code;
        List<String> codeList = new ArrayList<>();
        int count = 5000;
        for (int i = 0; i < count; i++) {
            code = "INSERT INTO lycoocms.`dynamic_authority` (`authorization_code`) VALUE ('" + RandomUtils.generateAuthorizationCode() + "');";
            // System.out.println(code);
            codeList.add(code);
        }
        File dir = new File("D:/Product Info/授权码/动态");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File path = new File(dir, "dynamic_authcode_" + count + "_" + formatter.format(new Date()) + ".sql");
        wirteToLoacl(path, codeList);

        System.out.print("generate dynamic Code successfully......");
    }

    @Test
    public void generateCode2() {
        String code;
        for (int i = 0; i < 10000; i++) {
            code = "INSERT INTO lycoocms.`dynamic_authority` (`authorization_code`) VALUE ('" + RandomUtils.generateAuthorizationCode() + "');";
            System.out.println(code);
        }
    }

    @Test
    public void test_02() {
        String code;
        for (int i = 0; i < 1; i++) {
            code = RandomUtils.generateAppCode();
            System.out.println(code);
        }
    }

    @Test
    public void test_03() {
        String code;
        for (int i = 0; i < 1; i++) {
            code = RandomUtils.generateFirmwareCode();
            System.out.println(code);
        }
    }

    @Test
    public void create_ethernet_mac() {
        //createPrintMac("0A0ADB14DD5A", 5000, "", 0);
//        createPrintMac("0A0ADB15046C", 5000, "", 0);
//        createPrintMac("0A0ADB152B7E", 10000, "", 0);
//        createPrintMac("0A0ADB155290", 10000, ":", 0);
//        createPrintMac("0A0ADB1579A1", 200, "", 0);
        createPrintMac("0A0ADB157A6A", 10000, "", 0);
    }

    @Test
    public void create_wifi_mac() {
//        createPrintMac("0C0ADB150064", 100, "", 1);
    }

    private void createPrintMac(String beginMac, int count, String separator, int mode) {
        System.out.println("begin mac = " + beginMac + ", count = " + count + ", separator = " + separator);
        if (null == beginMac || beginMac.length() != 12) {
            System.out.println("begin mac is invalid.......................");
            return;
        }

        if (count <= 0) {
            System.out.println("ERROR, count is invalid");
            return;
        }

        long mac_begin = Long.parseLong(beginMac, 16);
        System.out.println("mac_begin = " + mac_begin);
        mac_begin++;

        long mac_end = mac_begin + count;
        System.out.println("mac_end = " + mac_end);

        List<String> macList = new ArrayList<>();
        while (mac_begin <= mac_end) {
            String result = Long.toHexString(mac_begin);
            result = checkLength(result).toUpperCase();

            if (!separator.isEmpty()) {
                macList.add(insertSeparator(result, separator));
            } else {
                macList.add(result);
                System.out.print(result + "\n");
            }

            mac_begin++;
        }

        File dir = new File(mode == 0 ? "D:/Product Info/Ethernet_mac" : "D:/Product Info/Wifi_mac");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String prefix = mode == 0 ? "ethernet_mac_" : "wifi_mac_";
        File path = new File(dir, prefix + count + "_" + formatter.format(new Date()) + ".txt");
        wirteToLoacl(path, macList);

        System.out.println("打印 mac 生成成功............................................!!!");

    }

    @Test
    public void createSN() {
        for (int i = 0; i < 10; i++) {
            System.out.println("" + RandomUtils.generateSn(18));
        }
    }

    private String checkLength(String mac) {
        while (mac.length() < 12) {
            mac = "0" + mac;
        }
        return mac;
    }

    private String insertSeparator(String mac, String separator) {
        String[] arrays = mac.split("");
        String result = "";
        for (int i = 0; i < arrays.length; i++) {
            if (i > 0 && i % 2 == 0) {
                result += separator;
            }
            result += arrays[i];
        }

        return result;
    }

    private void wirteToLoacl(File path, List<String> list) {
        try {
            FileWriter writer = new FileWriter(path);
            for (String mac : list) {
                writer.write(mac + "\r\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}