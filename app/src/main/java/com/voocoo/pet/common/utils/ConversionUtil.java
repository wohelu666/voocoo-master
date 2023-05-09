package com.voocoo.pet.common.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ConversionUtil {
    /**
     * 将一个单字节的byte转换成32位的int
     *
     * @param b byte
     * @return convert result
     */
    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    /**
     * 将一个单字节的Byte转换成十六进制的数
     *
     * @param b byte
     * @return convert result
     */
    public static String byteToHex(byte b) {
        int i = b & 0xFF;
        return Integer.toHexString(i);
    }

    /**
     * 将一个4byte的数组转换成32位的int
     *
     * @param buf bytes buffer
     * @param pos
     * @return convert result
     */
    public static long unsigned4BytesToInt(byte[] buf, int pos) {
        int firstByte = 0;
        int secondByte = 0;
        int thirdByte = 0;
        int fourthByte = 0;
        int index = pos;
        firstByte = (0x000000FF & ((int) buf[index]));
        secondByte = (0x000000FF & ((int) buf[index + 1]));
        thirdByte = (0x000000FF & ((int) buf[index + 2]));
        fourthByte = (0x000000FF & ((int) buf[index + 3]));
        index = index + 4;
        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
    }

    /**
     * 8字节转long
     *
     * @param b
     * @return
     */
    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * 将16位的short转换成byte数组
     *
     * @param s short
     * @return byte[] 长度为2
     */
    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * 将32位整数转换成长度为4的byte数组
     *
     * @param s int
     * @return byte[]
     */
    public static byte[] intToByteArray(int s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 4; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * long to byte[]
     *
     * @param s long
     * @return byte[]
     */
    public static byte[] longToByteArray(long s) {
        byte[] targets = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * 32位int转byte[]
     */
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    /**
     * 将长度为2的byte数组转换为16位int
     *
     * @param res byte[]
     * @return int
     */
    public static int byte2int(byte[] res) {
        // res = InversionByte(res);
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets = (res[1] & 0xff) | ((res[0] << 8) & 0xff00); // | 表示安位或
        return targets;
    }

    /**
     * 字节数组转字符串
     *
     * @param prefer_name_b
     * @return
     */
    public static String bytesToString(byte[] prefer_name_b) {
        String msg = "";
        ByteArrayInputStream bintput = new ByteArrayInputStream(prefer_name_b);
        BufferedReader br = new BufferedReader(new InputStreamReader(bintput));
        try {
            msg = br.readLine();
        } catch (IOException e) {

        }
        return msg;
    }

    /**
     * byte数组与short数组转换
     *
     * @param data
     * @return
     */
    public static short[] byteArray2ShortArray(byte[] data) {
        if (data == null) {
            // Log.e(TAG, "byteArray2ShortArray, data = null");
            return null;
        }

        short[] retVal = new short[data.length / 2];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);
        }

        return retVal;
    }

    /**
     * byte数组与short数组转换
     *
     * @param data
     * @return
     */
    public static byte[] shortArray2ByteArray(short[] data) {
        byte[] retVal = new byte[data.length * 2];
        for (int i = 0; i < retVal.length; i++) {
            int mod = i % 2;
            if (mod == 0) {
                retVal[i] = (byte) (data[i / 2]);
            } else {
                retVal[i] = (byte) (data[i / 2] >> 8);
            }
        }

        return retVal;
    }

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    public static byte[] ListtoByteArray(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    public static List<Byte> ByteArraytoList(byte[] bytes) {
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            list.add(bytes[i]);
        }
        return list;
    }

    public static byte[] getUserfulContent(byte[] bytes) {
        if (bytes == null) return null;
        int index = bytes.length;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0x00) {
                index = i;
                break;
            }
        }
        byte[] arrayOfByte = new byte[index];
        System.arraycopy(bytes, 0, arrayOfByte, 0, arrayOfByte.length);
        return arrayOfByte;
    }

    public static String convertToBleMac(String mac) {
        if (TextUtils.isEmpty(mac) || mac.contains(":")) return mac;

        if (mac.length() % 2 != 0) return mac;

        StringBuilder builder = new StringBuilder();
        while (mac.length() > 0) {
            builder.append(mac.substring(0, 2));
            if (mac.length() > 2)
                builder.append(":");
            mac = mac.substring(2);
        }

        return builder.toString();
    }
}
