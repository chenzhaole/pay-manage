package com.sys.manage.notify.control;


import com.sys.common.enums.PayChannelEnum;
import com.sys.manage.notify.service.IChannelService;
import com.sys.trans.api.entry.Config;
import com.sys.trans.api.entry.Result;
import com.sys.trans.api.entry.Trade;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @Description:Controller基类
 * 
 * @author: ChenZL
 * @time: 2017年9月5日
 */
public abstract class BaseController {
	//des解密密钥
	protected static final String password = "aaadd29cc97c68d1";
	
	
	/**
	 * 根据支付通道Code，选择服务实现service
	 * 
	 * @param trade
	 * @return
	 */
	public IChannelService getChannelService(Trade trade) {
		Config config = trade.getConfig();
		String channelNo = config.getChannelCode();

		if (PayChannelEnum.SANDPAY.getCode().equals(channelNo)) {
			// 杉德支付
			return null;
		} else if (PayChannelEnum.BJRCB.getCode().equals(channelNo)) {
			// 北京农商银行
			return null;
		} else if (PayChannelEnum.NOWPAY.getCode().equals(channelNo)) {
			// 现在支付
			return null;
		} else if (PayChannelEnum.CHANPAY.getCode().equals(channelNo)) {
			// 畅捷支付
			return null;
		} else if (PayChannelEnum.XINGLUOPAY.getCode().equals(channelNo)) {
			// 星罗支付
			return null;
		} else {
			return null;
		}

	}
	
	protected Result sysErrorResult(String msg) {
		Result result = new Result();
		result.setRespCode("7777");
		result.setRespMsg("Tran系统异常 msg="+msg);
		return result;
	}
	protected String getCurrentDate(String orderNo) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"----"+orderNo;
	}
	protected Result notFindChannelResult() {
		Result result = new Result();
		result.setRespCode("7776");
		result.setRespMsg("没有找到匹配的支付通道");
		return result;
	}

	protected Result notFindPayTypeResult() {
		Result result = new Result();
		result.setRespCode("7775");
		result.setRespMsg("没有找到匹配的支付类型");
		return result;
	}

}
