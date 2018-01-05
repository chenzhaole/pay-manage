package com.sys.admin.common.security;


import org.apache.commons.codec.binary.Base64;

import java.io.*;


/**
 * =======================================================================
 * <p/>
 * BASE64编码解码工具包
 * <p/>
 * 依赖于 Apache 的 commons-codec（版本1.8或1.9）
 * <p/>
 * =======================================================================.
 */
public class Base64Utils {

    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * BASE64字符串解码为二进制数据
     *
     * @param base64String BASE64字符串
     * @return 二进制数据
     * @throws Exception
     */
    public static byte[] decode(String base64String) throws Exception {
        Base64 base64 = new Base64();
        return base64.decode(base64String.getBytes());
    }

    /**
     * 二进制数据编码为BASE64字符串
     *
     * @param bytes 二进制数据
     * @return BASE64字符串
     * @throws Exception
     */
    public static String encode(byte[] bytes) throws Exception {
        Base64 base64 = new Base64();
        return new String(base64.encode(bytes));
    }

    /**
     * 将文件编码为BASE64字符串
     * <p/>
     * 大文件慎用，可能会导致内存溢出
     *
     * @param filePath 文件绝对路径
     * @return BASE64字符串
     * @throws Exception
     */
    public static String encodeFile(String filePath) throws Exception {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }

    /**
     * BASE64字符串转回文件
     *
     * @param filePath 文件绝对路径
     * @param base64   编码字符串
     * @throws Exception
     */
    public static void decodeToFile(String filePath, String base64) throws Exception {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    /**
     * 文件转换为二进制数组
     *
     * @param filePath 文件路径
     * @return 二进制数组
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
        }
        return data;
    }

    /**
     * 二进制数据写文件
     *
     * @param bytes    二进制数据
     * @param filePath 文件生成目录
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            boolean dirOK = destFile.getParentFile().mkdirs();
            if (!dirOK) {
				return;
			}
        }
        boolean createOK = destFile.createNewFile();
        if (createOK) {
            OutputStream out = new FileOutputStream(destFile);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
        }
    }


}
