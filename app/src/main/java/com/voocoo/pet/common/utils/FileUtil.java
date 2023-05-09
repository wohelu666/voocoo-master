package com.voocoo.pet.common.utils;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件目录工具
 * Created by andy on 2017/12/20.
 */

public class FileUtil {
    //文件存储根目录
    public static String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 得到二维码路径
     *
     * @param context
     * @return
     */
    public static String getQRCodePath(Context context) {
        return getFileRoot(context) + File.separator
                + "qr_" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * 文件转字节流
     */
    public static byte[] fullyReadFileToBytes(File file) throws IOException {
        int size = (int) file.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(file);

        int read = fis.read(bytes, 0, size);
        if (read < size) {
            int remain = size - read;
            while (remain > 0) {
                read = fis.read(tmpBuff, 0, remain);
                System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                remain -= read;
            }
        }
        fis.close();

        return bytes;
    }

    public static byte[] file2byte(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] b = new byte[1024];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }

        byte[] buffer = bos.toByteArray();
        fis.close();
        bos.close();

        return buffer;
    }

}
