package com.zxing.qrcode.produce;


import android.util.Base64;

import java.nio.charset.Charset;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * 使用DES加解密
 */
public class DESencryptionUtil {

    private static final String KEYALGORITHM = "DESede";
    private static final String CIPHERALGORITHM = "DESede/ECB/PKCS5Padding";
    private static byte[] KEY = {34, 60, 89, 17, 37, 25, 37, 64, 62, 26, 30, 92, 4,
            61, 15, 31, 66, 67, 58, 34, 67, 32, 62, 3};

    private DESencryptionUtil() {
    }

    /**
     * 将字符串使用DES加密,然后再转化Base64
     *
     * @param source 需加密字符串
     * @return 成功返回 加密和转化Base64的字符串, 失败返回空字符串
     */
    public static String encrypt(String source) {
        try {
            byte[] source_byte = encryptOrdecryptByMode(source.getBytes(Charset.forName("UTF-8")), Cipher.ENCRYPT_MODE);

            return Base64.encodeToString(source_byte, 16);
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 将字符串使用Base64解编码,然后在通过DES解密
     *
     * @param source 已使用DES加密且转化Base64字符串
     * @return 成功返回 Base64解编码DES解密的字符串, 失败返回空字符串
     */
//    public static String decrypt(String source) {
//        try {
//            return new String(encryptOrdecryptByMode(Base64.decodeBase64(source),
//                    Cipher.DECRYPT_MODE), Charsets.UTF_8);
//        } catch (Exception e) {
//        }
//        return "";
//    }
    private static byte[] encryptOrdecryptByMode(byte[] source, int mode) throws Exception {
        DESedeKeySpec skey = new DESedeKeySpec(KEY);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEYALGORITHM);
        Key key = keyFactory.generateSecret(skey);
        final Cipher cipher = Cipher.getInstance(CIPHERALGORITHM);
        cipher.init(mode, key);
        return cipher.doFinal(source);
    }
}
