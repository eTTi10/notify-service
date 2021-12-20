package com.lguplus.fleta.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class AesUtil {

    private final static String aesKey = "pml170726wof@!";
    private final static String aesKeyIv = "lvpml2l334eol";
    private final static String encoding = "UTF-8";
    private final static int aesKeySize = 128;


    /**
     * 암호화
     * @param value
     * @return
     * @throws Exception
     */
    public static String encryptAES(String value) throws Exception{

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec= new SecretKeySpec(Arrays.copyOf(aesKey.getBytes(encoding), aesKeySize / 8),"AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOf(aesKeyIv.getBytes(encoding), aesKeySize / 8));

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return new String(Base64.encodeBase64(cipher.doFinal(value.getBytes(encoding))));

    }

    /**
     * 복호화
     * @param value
     * @return
     * @throws Exception
     */
    public static String decryptAES(String value) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(aesKey.getBytes(encoding), aesKeySize / 8), "AES");
        SecretKeySpec secretKeySpec= new SecretKeySpec(secretKey.getEncoded(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOf(aesKeyIv.getBytes(encoding), aesKeySize / 8));

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(Base64.decodeBase64(value));

        return new String(decrypted);

    }

}
