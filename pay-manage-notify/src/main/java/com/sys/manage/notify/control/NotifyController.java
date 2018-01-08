package com.sys.manage.notify.control;

import com.alibaba.fastjson.JSON;
import com.sys.trans.api.entry.Result;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TranException;
import com.sys.common.util.DesUtil32;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:控制器支持类
 * 
 * @author: ChenZL
 * @time: 2017年11月14日
 */
@Controller
// @RequestMapping(value = "")
public class NotifyController extends BaseController {

	protected final Logger logger = LoggerFactory.getLogger(NotifyController.class);

	

	/**
	 * 通知商户
	 */
	@RequestMapping("notify2mcht")
	@ResponseBody
	public String notify2mcht(@RequestBody String data) throws TranException {
		logger.info("----"+getCurrentDate("")+"----[start]支付-支付结果通知---"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		logger.info("----"+getCurrentDate("")+"----接收到的原始数据 [data]: "+data);
		Result result = sysErrorResult("未知错误") ;
		try {
			String str = URLDecoder.decode(DesUtil32.decode(data, password), "UTF-8");//des32 解密
			String decData = str;
			if(str.endsWith("=")){
				decData = str.substring(0,str.length()-1);
			}
			Trade trade = JSON.parseObject(decData, Trade.class);
//			result = new ReciveXingLuoOrderNotifyHandler().process(trade);
			logger.info("----"+getCurrentDate("")+"----处理后的返回数据 [result]: "+ JSON.toJSONString(result));
			logger.info("----"+getCurrentDate("")+"----支付-支付结果通知 [end]");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("----"+getCurrentDate("")+"----支付-支付结果通知接口 异常:"+e.getMessage());
			result = sysErrorResult("支付-支付结果通知接口 异常:"+e.getMessage()) ;
		}finally {
		}
		return JSON.toJSONString(JSON.toJSONString(result));
	}

}
