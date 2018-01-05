package com.sys.gateway.common;

import org.apache.commons.lang3.ArrayUtils;

//import com.system.gateway.lang.Pair;


public class CryptoEncode {

/*快速加密算法码表，请勿修改*/
static byte[] rev = { 0x0b, 0x04, 0x0f, 0x06, 0x01, 0x0a, 0x03, 0x09, 
							   0x0d, 0x07, 0x05, 0x00, 0x0e, 0x08, 0x0c, 0x02 };
static char[] sk  = { 0xd7, 0x6a, 0xa4, 0x78, 0xf5, 0x7c, 0x42, 0xab,
							   0xa4, 0x52, 0xf6, 0x76, 0x3b, 0x4d, 0x61, 0xce };

//20160614用一个不容易出现的字符串做新老版加密区分
private final static byte[] xjpay ="xjpay".getBytes();

/**
 * 加密函数
 * @param userkey 加密的key值
 * @param src 要加密的内容
 * @return boolean 是否是新版
 * @return byte[] 返回加密的内容
 */
public static byte[] ZCryptoEncode(byte[] userkey,byte[] src)
{
	
	ZcryptoQuickKeyt key =  ZCryptoQuickInitKey(userkey);
	byte[] ret  =  ZcryptoQuickEnc( key,src);

	String srcLength = String.valueOf(src.length) ;
	int lenOflength = srcLength.length();
	byte ll = (byte) lenOflength;
	//xjpay(标识新老加密)+ll(长度的长度)+srcLength(源串的长度)+B(加密源串)
	byte[] newRet = ArrayUtils.addAll(
			ArrayUtils.addAll( ArrayUtils.add(xjpay, ll), srcLength.getBytes() ),
			ret);
	return newRet;
}
/**
 * 解密函数
 * @param userkey 解密的key值
 * @param src 要解密的内容
 * @return byte[] 返回解密的内容
 */
public static  byte[]  ZCryptoDecode(byte[] userkey,byte[] src)
{
	ZcryptoQuickKeyt key =  ZCryptoQuickInitKey(userkey);
	//新版加密: xjpay(标识新老加密)+ll(长度的长度)+srcLength(源串的长度)+realSrc(加密源串)
	int lenOflength = (int) src[xjpay.length];
	
	byte[] srcLengthArr = ArrayUtils.subarray(src, xjpay.length+1, xjpay.length+1+lenOflength);
	
	byte[] realSrc = ArrayUtils.subarray(src, xjpay.length+1+lenOflength, src.length);
	byte[] ret  =  ZcryptoQuickDec( key,realSrc);
	
	int srcLength = Integer.parseInt(new String(srcLengthArr)) ;
	
	byte[] realRet = ArrayUtils.subarray(ret,0,srcLength);
	return realRet;
}

/*初始化加解密的KEY*/
public static ZcryptoQuickKeyt ZCryptoQuickInitKey(byte[] userkey)
{
	ZcryptoQuickKeyt key = new ZcryptoQuickKeyt();
	char[]  __smask = {0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};
	char[] ukey = {0, 0, 0, 0, 0, 0, 0, 0};
	int i, j;

	/*按8bits展开*/
	for (i=0; i<userkey.length; i++) 
		ukey[i&0x7] = (char) (ukey[i&0x7] ^ userkey[i]);

	for (i=0; i<8; i++)	
		for (j=0; j<8; j++)
			key.rk[i] |= ukey[(i+j)&0x07] & __smask[(i+j)&0x07];
	
	return key;
}

/*快速加密算法，输出为8倍整数*/
/* userkey按64bits展开 */
/* src[0] => div(8) => ^iv[0]=> rev(16) => ^key[0] => ^sk[0] => dst[0] (dst[0] -> iv[next]) */
public static byte[] ZcryptoQuickEnc(ZcryptoQuickKeyt key, byte[] src)
{
	int len = src.length;
	int mod = src.length%8;
	if(mod != 0)
	{
		len = src.length+8-mod;
	}
	
	byte[] pBuf = new byte[len];
	for(int i = 0; i<len ;i++)
		pBuf[i] = 0;
	System.arraycopy(src, 0, pBuf, 0, src.length);
	byte[] dst = new byte[len];
	byte[] iv = key.iv;
	byte[] rk = key.rk;
	
	int p = 0;
	while (len > 0)
	{
	
		iv[0] ^= pBuf[p+7];
		iv[1] ^= pBuf[p+6];
		iv[2] ^= pBuf[p+5];
		iv[3] ^= pBuf[p+4];
		iv[4] ^= pBuf[p+3];
		iv[5] ^= pBuf[p+2];
		iv[6] ^= pBuf[p+1];
		iv[7] ^= pBuf[p+0];
	
		
		dst[p+0] = (byte) (((rev[(byte)((iv[0]>>4)&0x0F)] + (rev[(byte)(iv[0]&0xF)]<<4)) ^ rk[0] ^ sk[0]));
		dst[p+1] = (byte) (((rev[(byte)((iv[1]>>4)&0x0F)] + (rev[(byte)(iv[1]&0xF)]<<4)) ^ rk[1] ^ sk[1]));
		dst[p+2] = (byte) (((rev[(byte)((iv[2]>>4)&0x0F)] + (rev[(byte)(iv[2]&0xF)]<<4)) ^ rk[2] ^ sk[2]));
		dst[p+3] = (byte) (((rev[(byte)((iv[3]>>4)&0x0F)] + (rev[(byte)(iv[3]&0xF)]<<4)) ^ rk[3] ^ sk[3]));
		dst[p+4] = (byte) (((rev[(byte)((iv[4]>>4)&0x0F)] + (rev[(byte)(iv[4]&0xF)]<<4)) ^ rk[4] ^ sk[4]));
		dst[p+5] = (byte) (((rev[(byte)((iv[5]>>4)&0x0F)] + (rev[(byte)(iv[5]&0xF)]<<4)) ^ rk[5] ^ sk[5]));
		dst[p+6] = (byte) (((rev[(byte)((iv[6]>>4)&0x0F)] + (rev[(byte)(iv[6]&0xF)]<<4)) ^ rk[6] ^ sk[6]));
		dst[p+7] = (byte) (((rev[(byte)((iv[7]>>4)&0x0F)] + (rev[(byte)(iv[7]&0xF)]<<4)) ^ rk[7] ^ sk[7]));
		System.arraycopy(dst, p, iv, 0, 8);  
		//memcpy(iv, dst, 8);
		len -= 8;
		p+=8;
	}
	return dst;

}

/*快速解密算法，输出为8倍整数*/
public static byte[] ZcryptoQuickDec(ZcryptoQuickKeyt key,byte[] src)
{
	int len = src.length;
	byte[] dst = new byte[len];
	byte[] iv = key.iv;
	byte[] rk = key.rk;
	byte[] r = {0, 0, 0, 0, 0, 0, 0, 0};
	int p = 0;
	while (len > 0) 
	{	
		r[0] = (byte) (src[p+0] ^ rk[0] ^ sk[0]);
		r[1] = (byte) (src[p+1] ^ rk[1] ^ sk[1]);
		r[2] = (byte) (src[p+2] ^ rk[2] ^ sk[2]);
		r[3] = (byte) (src[p+3] ^ rk[3] ^ sk[3]);
		r[4] = (byte) (src[p+4] ^ rk[4] ^ sk[4]);
		r[5] = (byte) (src[p+5] ^ rk[5] ^ sk[5]);
		r[6] = (byte) (src[p+6] ^ rk[6] ^ sk[6]);
		r[7] = (byte) (src[p+7] ^ rk[7] ^ sk[7]);
		
		dst[p+7] = (byte) ((rev[(r[0]>>4)&0x0F] + (rev[r[0]&0xF]<<4)) ^ iv[0]);
		dst[p+6] = (byte) ((rev[(r[1]>>4)&0x0F] + (rev[r[1]&0xF]<<4)) ^ iv[1]);
		dst[p+5] = (byte) ((rev[(r[2]>>4)&0x0F] + (rev[r[2]&0xF]<<4)) ^ iv[2]);
		dst[p+4] = (byte) ((rev[(r[3]>>4)&0x0F] + (rev[r[3]&0xF]<<4)) ^ iv[3]);
		dst[p+3] = (byte) ((rev[(r[4]>>4)&0x0F] + (rev[r[4]&0xF]<<4)) ^ iv[4]);
		dst[p+2] = (byte) ((rev[(r[5]>>4)&0x0F] + (rev[r[5]&0xF]<<4)) ^ iv[5]);
		dst[p+1] = (byte) ((rev[(r[6]>>4)&0x0F] + (rev[r[6]&0xF]<<4)) ^ iv[6]);
		dst[p+0] = (byte) ((rev[(r[7]>>4)&0x0F] + (rev[r[7]&0xF]<<4)) ^ iv[7]);
		System.arraycopy(src, p, iv, 0, 8);  
		//memcpy(iv, src, 8);
		
		len -= 8;
		p += 8;
	}
	return dst;
}

	public static class ZcryptoQuickKeyt {
		byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0};
		byte[] rk = {0, 0, 0, 0, 0, 0, 0, 0};

		public byte[] getIv() {
			return iv;
		}
		public void setIv(byte[] iv) {
			this.iv = iv;
		}
		public byte[] getRk() {
			return rk;
		}
		public void setRk(byte[] rk) {
			this.rk = rk;
		}
	}
}


