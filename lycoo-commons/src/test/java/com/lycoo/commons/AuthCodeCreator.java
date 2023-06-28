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

/**
 * xxx
 *
 * Created by lancy on 2019/9/6
 */
public class AuthCodeCreator {
    private static SimpleDateFormat mFormatter = new SimpleDateFormat("yyyyMMdd");

    @Test
    public void printSN() {
//        createSN(10000);
        /*
        String s = RandomUtils.generateSn(18);
        System.out.println(s);

        String str = "abcdefg";
        System.out.println(str.substring(0, 20));
         */
    }

    // ===========================================================================================================================================

    /**
     * 生产序列号
     *
     * Created by lancy on 2019/9/6 17:15
     */
    private void createSN(int count) {
        File dir = new File("D:/Product Info/IronMan/SN");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<String> codeList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String sn = RandomUtils.generateSn(18);
            codeList.add(sn);
            sb.append(sn).append("#");
        }

        File file = new File(dir, "authcode-" + count + "-" + mFormatter.format(new Date()) + ".txt");
        wirteToLoacl(file, codeList);

        sb.deleteCharAt(19 * count - 1);
        System.out.println(sb.toString());

        System.out.print("生成授权码成功[" + file.getPath() + "]");
    }

    private void createStringSn(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(RandomUtils.generateSn(18));
            sb.append("#");
        }

        sb.deleteCharAt(19 * count - 1);
//        sb.replace(count - 1, count - 1, "");
        System.out.println(sb.toString());
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
