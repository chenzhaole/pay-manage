package com.sys.admin.common.security;


import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


/**
 * =======================================================================
 * <p>
 * RSA工具
 * </p>
 * <p>
 * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
 * </p>
 * <p>
 * <b>说明：</b> 使用本工具加密的数据长度可以超过117字节，解密数据长度可以超过128字节
 * </p>
 * <p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全。
 * </p>
 *
 * =======================================================================
 */
public class RSAUtils {

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 密钥长度
     */
    public static final int KEYSIZE = 1024;


    ///////////////////////////////////////////////////////////////////////////
    // <<密钥加载>>

    /**
     * 从文件中加载公钥
     *
     * @param path 公钥文件
     * @throws Exception 加载公钥时产生的异常
     */
    public static String loadPublicKeyByFile(String path) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @return RSA公钥
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @param path 私钥文件
     * @return 是否成功
     * @throws Exception
     */
    public static String loadPrivateKeyByFile(String path) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    /**
     * 从字符串中加载私钥
     *
     * @param privateKeyStr 私钥字符串
     * @return RSA私钥
     * @throws Exception
     */
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // <<签名验签>>

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return 签名字符串
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encode(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return 正确：true；不正确：false
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decode(sign));
    }


    ///////////////////////////////////////////////////////////////////////////
    // <<加密解密>>

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return 解密后的字节数组
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return 解密后的字节数组
     * @throws Exception
     */
    public static byte[] decryptByPrivateKeyAndroid(byte[] encryptedData, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm(), new org.bouncycastle.jce.provider.BouncyCastleProvider());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return 公钥解密后字节数组
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return 公钥加密后字节数组
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 针对安卓客户端公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return 公钥加密后字节数组
     * @throws Exception
     */
    public static byte[] encryptByPublicKeyAndroid(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm(), new org.bouncycastle.jce.provider.BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 私钥加密
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return 加密后的字节数组
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    ///////////////////////////////////////////////////////////////////////////
    // <<密钥生成和获取-- 测试使用>>

    /**
     * 生成密钥对（公钥和私钥）
     *
     * @return 密钥对
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(KEYSIZE);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 获取私钥
     *
     * @param keyMap 密钥对
     * @return 私钥
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encode(key.getEncoded());
    }

    /**
     * 获取公钥
     *
     * @param keyMap 密钥对
     * @return 公钥
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encode(key.getEncoded());
    }

    /**
     * 使用公钥加密
     * @param source 明文
     * @param publicKey 公钥
     * @return 加密后的字符串
     */
    public static String encrypt(String source, String publicKey) throws Exception {
        byte[] encryptData = RSAUtils.encryptByPublicKey(source.getBytes("utf-8"), publicKey);
        return Base64.encode(encryptData);
    }

    /**
     * 针对安卓客户端使用公钥加密
     * @param source 明文
     * @param publicKey 公钥
     * @return 加密后的字符串
     */
    public static String encryptAndroid(String source, String publicKey) throws Exception {
        byte[] encryptData = RSAUtils.encryptByPublicKeyAndroid(source.getBytes("utf-8"), publicKey);
        return Base64.encode(encryptData);
    }

    /**
     * 使用私钥解密
     * @param cipher 密文
     * @param privateKey 公钥
     * @return 加密后的字符串
     */
    public static String decrypt(String cipher, String privateKey) throws Exception {
        byte[] decryptData = RSAUtils.decryptByPrivateKey(Base64.decode(cipher), privateKey);
        return new String(decryptData, "utf-8");
    }

    /**
     * 针对安卓客户端使用私钥解密
     * @param cipher 密文
     * @param privateKey 公钥
     * @return 加密后的字符串
     */
    public static String decryptAndroid(String cipher, String privateKey) throws Exception {
        byte[] decryptData = RSAUtils.decryptByPrivateKeyAndroid(Base64.decode(cipher), privateKey);
        return new String(decryptData, "utf-8");
    }

    public static void main(String[] args) {
        //第一组公钥私钥
        try {
            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEmmYpfC2RPNzEmMhruomO4GySyaIOjSug0lxgKCpUOZarBI+iabNG7t3lkJbB8apeBo+HFIGXsbA4afI54+hCa3bT0CQMJFa5VlQxd1iyKKP92AtiQZhu1BPwHGXOZQCr+0VLgXhFGRPNvHMQpecWB6lxzALr7rnD62u+6F2/GQIDAQAB";
            String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMSaZil8LZE83MSYyGu6iY7gbJLJog6NK6DSXGAoKlQ5lqsEj6Jps0bu3eWQlsHxql4Gj4cUgZexsDhp8jnj6EJrdtPQJAwkVrlWVDF3WLIoo/3YC2JBmG7UE/AcZc5lAKv7RUuBeEUZE828cxCl5xYHqXHMAuvuucPra77oXb8ZAgMBAAECgYEAjzeeZmgUVbHNbNWTYo6/eDpaPU/u4sTJ3FyBeHap5zMQY8Jt07VGKM0HDh0XBW9DWT1UPuWcReHl6iKblrpETR2q8NnaZQdEOVKS8H+noLPzcihQmYwaTlZSxID8gsy7buikX9YdFQGRTOJGEHl5Ffr0Hhz5GiPdi9rcaws1XgECQQD4UN87vBDyHqFW54L6T20H5OPTldw8H6ZaEqlcIgAA9uDFxe8gCUzSRuw+Yz0nwP0ypl9sEDaNHmXuLVtTuUBBAkEAyq/dYNdfS6EqCZnIgiEzLWPRtWINyktr3IqcPjP3m/XIonqMYCm+CJMuL5R38fyjjcCXDCq3f+JKPOGyhURI2QJBAJp/duVrtf9vH2M62Dt1f5kRyM60xzqBVrdkNQul++qdsGxdItMD9lA+4G5QwJQjd2Y0LdrGm6ph53mxrskA0gECQQCLZp0ph7cg+v1AkSYYaeEa7LZEu+WkJm9OxX5kQbWg7FBLf2GgmdT7bM6tJr3ADvQmADliwtZl/Cr26HuxsJ8xAkAVezgjQ7cEi3k3y+ZYCjunL5d3mZiApoZ3u6MrJxJMCUJJZr626YUu+KB8v3u+i27x38xo6ofJ3BbBzrW2JD1k";

//            String source = "{imei = \"3047ADA0-E172-4722-A089-15AE61E4005D\";sessionId = 1;stamp = \"2015.05.20\";upgrade = \"1.0\";}";
//            System.out.println("\r明文：\r\n" + source);
//            String cipher = encrypt(source, publicKey);
//            System.out.println("加密后文字: \r\n" + cipher);

            String cipher = "oiI7KFnWkTuhpVGgkvBLTXiHWKHTYehAvUgzgxqO3I/akha6rG9Sd1c17181QooiruAi+hL4T/9G1noHIIRqP8bhDqQB7R5+juPXXMyC+KHmT8LWWMk8iZ6tTT2ZbRC69sOoHkknYUb9XB4zy0Fkps9r8E3+FM8kTBxUd0IWT1Q=";
            System.out.println("解密后文字: \r\n" + decrypt(cipher, privateKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        //第二组公钥私钥
//        try {
//            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCevGgHhmWuzRgvv4ZMk8CG/+D4Y6N6M8kD0/0LiKhYhDHnSwK5DpVo/dEw71WoUCioBWkAy3N9HAI38NanGa96CMjOzKg3oN2Dvh6uaHbIW3u+c+OdW709eeRYLHqidrUQVxE63gKQ5Fxv6Ka7Dlqnwmc5CAiEHW37rAx8dVd6XwIDAQAB";
//            String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ68aAeGZa7NGC+/hkyTwIb/4Phjo3ozyQPT/QuIqFiEMedLArkOlWj90TDvVahQKKgFaQDLc30cAjfw1qcZr3oIyM7MqDeg3YO+Hq5odshbe75z451bvT155FgseqJ2tRBXETreApDkXG/oprsOWqfCZzkICIQdbfusDHx1V3pfAgMBAAECgYAmBFml87pGszgnt2ci0AA8VOw8kSCGFAEAubTRHlmzWXXaP0zXpXaDPsbrQWbyeMBD/Ye4eQGf5SVi4HuIac5suzeBp3dEyfshXBg/W/0acb7j7KHFwrkdJzlR1wijK/o4SOqy2AzL2cjr8jKXbJtH98wDzLKTqnhXz96MCeTygQJBAM5zAKe0BXOjQ6+NANiV2EK4jl5H9O67YqKp/UbxTNxELIwpcBSThi5gqBOjD5k+UqHMoqyG4oK5y/ypJUMaNnMCQQDE1bnObmKxy64QPTOgHaLfMwviXghilleSwFb5+zTogpDBuSZWnjYu/YOKZ3W2kSrNSxEgbQJ3zPCpBXZfoUVlAkEAuq0KnpoNg+7ROWAKr81u64XuVhpCw8v0+Jk1SHzQvyt2DJc7nZ936V/FG1yKskrEdUGQpuig0QwoA6L+6po9OQJAfVCEpwRoGUHPY89TGtFDbE6Xdlrfg8SrpGaH+UcRfCYD+xHhcqvvWyNTp74Ol1j8ow7zrG06cQMtBK7RmjiWcQJBAJTEbvpHR6cwjEltH6MY8txCiUHJpeHhglESApjdE9+EaSs8BpkHEpVcdohwYo6eqY96ar+N/nQ1rtWwQ+LtH/s=";
//            String source = "北京";
//            System.out.println("\r明文：\r\n" + source);
//            String cipher = encrypt(source, publicKey);
//            System.out.println("加密后文字: \r\n" + cipher);
//            System.out.println("\r密文：\r\n" + cipher);
//            System.out.println("解密后文字: \r\n" + decrypt(cipher, privateKey));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
