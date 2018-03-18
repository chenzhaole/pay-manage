package com.sys.gateway.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Hashtable;

/**
 * 
 * 第三方支付扫码支付二维码生成器
 * 
 * @author lichuanyou
 */
@Controller
@RequestMapping("/qrCode")
public class GwCashierQrCodeController {
	
	private static Logger logger = LoggerFactory.getLogger(GwCashierQrCodeController.class);
	/**
	 * 
	 * 生成二维码返回
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/gen")
	@ResponseBody
	public String genQrCode(HttpServletRequest request, HttpServletResponse response){
		StringBuffer logStr = new StringBuffer();
		logStr.append("请求生成二维码");
		try {
			String code_url = URLDecoder.decode(StringUtils.isEmpty(request.getParameter("uuid"))?"":request.getParameter("uuid"),"UTF-8");
			OutputStream os = response.getOutputStream();
            try {
            	//如果二维码链接为空，则返回错误
            	if (StringUtils.isEmpty(code_url)){
            		return "二维码信息不存在";
            	}
            	response.reset();
            	response.setHeader("Content-Disposition", "attachment; filename=qrcode.png");
            	response.setContentType("application/octet-stream; charset=utf-8");
        		Hashtable hints= new Hashtable();
        		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        		BitMatrix bitMatrix = new MultiFormatWriter().encode(code_url, BarcodeFormat.QR_CODE, 301, 301,hints);
        		MatrixToImageWriter.writeToStream(bitMatrix, "png", os);
                os.flush();
            } finally {
                if (os != null) {
                    os.close();
                }
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logStr.append("生成二维码失败，抛出异常："+e.getMessage());
			return "系统异常";
		}finally {
			logger.info(logStr.toString());
		}
		return null;
	}
	
}
