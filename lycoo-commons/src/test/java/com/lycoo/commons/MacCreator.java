package com.lycoo.commons;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Mac地址
 *
 * Created by lancy on 2019/9/7
 */
public class MacCreator {
    private static SimpleDateFormat mFormatter = new SimpleDateFormat("yyyyMMdd");
    private static final int ETHERNET = 0;
    private static final int WIFI = 1;

    @Test
    public void create_EthernetMac() {
//        createMac("0A0ADB15A17B", 3000, "", ETHERNET);
//        createMac("0A0ADB15AD34", 5000, ":", ETHERNET); // 20190912
//        createMac("0A0ADB15C0BD", 5000, "", ETHERNET);  // 20190917
//        createMac("0A0ADB15D446", 5000, "", ETHERNET);  // 20190924
//        createMac("0A0ADB15E7CF", 5000, "", ETHERNET);  // 20191008
//        createMac("0A0ADB15FB58", 100, ":", ETHERNET);  // 20191010
//        createMac("0A0ADB15FBBD", 5000, "", ETHERNET);  // 20191023
//        createMac("0A0ADB160F46", 10000, "", ETHERNET);  // 20191028
//        createMac("0A0ADB163657", 100, ":", ETHERNET);  // 20191029
//        createMac("0A0ADB1636BC", 50, ":", ETHERNET);  // 20191030
//        createMac("0A0ADB1636F0", 2000, ":", ETHERNET);  // 20191115
//        createMac("0A0ADB1636C1", 10000, "", ETHERNET);  // 20191115
    }


    private void createMac(String beginMac, int count, String separator, int mode) {
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

        File dir = new File(mode == ETHERNET ? "D:/Product Info/IronMan/MAC/ethernet" : "D:/Product Info/IronMan/MAC/Wifi");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String prefix = mode == ETHERNET ? "ethernet-mac-" : "wifi-mac-";
        File path = new File(dir, prefix + count + "_" + mFormatter.format(new Date()) + ".txt");
        wirteToLoacl(path, macList);

        System.out.println("打印 mac 生成成功.......!!!");
    }

    /**
     * 长度检查
     *
     * Created by lancy on 2019/9/7 12:29
     */
    private String checkLength(String mac) {
        while (mac.length() < 12) {
            mac = "0" + mac;
        }
        return mac;
    }

    /**
     * 插入分隔符
     *
     * @param mac       mac地址
     * @param separator 分割符，例如“:”
     * @return 带分隔符的mac
     *
     * Created by lancy on 2019/9/7 12:29
     */
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

    /**
     * 写入本地
     *
     * Created by lancy on 2019/9/6 17:15
     */
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
