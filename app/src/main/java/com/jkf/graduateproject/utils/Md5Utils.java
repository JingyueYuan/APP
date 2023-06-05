package com.jkf.graduateproject.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Md5Utils {
    /*
    默认为大小32为的MD5加密方式，返回字符串长度为32
    如果是16位的加密方式的情况，只需要在32位的基础上返回8-24的16个字符  ---- md532.substring(8,24)
     */
    public static String Md5Decode(String info){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes(StandardCharsets.UTF_8));
            byte[] md5bytes = md5.digest();
            StringBuilder stringBuffer = new StringBuilder();
            for(int i =0; i< md5bytes.length;i++){
                if(Integer.toHexString(0xff & md5bytes[i]).length()==1){
                    stringBuffer.append("0").append(Integer.toHexString(0xff&md5bytes[i]));
                }else {
                    stringBuffer.append(Integer.toHexString(0xff&md5bytes[i]));
                }
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*
    DES 加解密，双向的操作
     */
    private static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    private static final String IVKEY = "graduate";

    /**
     *
     * @param key 密钥
     * @param ourData 明文
     * @return  加密之后的数据,然后利用Base64进行加密
     */
    public static String encodeDES(String key, String ourData) {
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(IVKEY.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(ourData.getBytes());
            return Base64.encodeToString(bytes,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *
     * @param key  密钥
     * @param ourData  本地储存的数据
     * @return   返回最原始的数据
     */
    public static String decodeDES(String key, String ourData) {
        try {
            // ivKey = Des.STRING_IV;
            byte [] decodeData = Base64.decode(ourData,0);

            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(IVKEY.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(decodeData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
