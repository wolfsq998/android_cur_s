package com.example.install;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Des {
    private static byte[] iv = {1,2,3,4,5,6,7,8};
    //加密
    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        //此类为加密和解密提供密码功能。为创建 Cipher 对象，
        // 应用程序调用 Cipher 的 getInstance 方法并将所请求转换 的名称传递给它。
        //CBC是工作模式，DES一共有电子密码本模式（ECB）、加密分组链接模式（CBC）、
        // 加密反馈模式（CFB）和输出反馈模式（OFB）四种模式，
        //PKCS5Padding是填充模式，还有其它的填充模式
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        // cipher.init（)一共有三个参数：Cipher.ENCRYPT_MODE, key, zeroIv，zeroIv就是初始化向量
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

        return Base64.encode(encryptedData);
    }
    //解密，
    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
        byte[] byteMi = new Base64().decode(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return new String(decryptedData);

    }
}


