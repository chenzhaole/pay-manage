package com.sys.admin.modules.channel.service;

import com.sys.admin.modules.channel.bo.ChannelFormInfo;
import com.sys.core.dao.dmo.ChanInfo;

import java.util.List;

/**
 * @ClassName: ChannelAdminService
 * @Description: 支付通道service
 * @author: cheng_fei
 * @date: 2017年8月29日 上午10:57:44
 */
public interface ChannelAdminService {
	/**
	 * @param channelInfo
	 * @return
	 * @Title: getChannelList
	 * @Description: 获取支付通道list
	 * @return: List<ChanInfo>
	 */
	List<ChanInfo> getChannelList(ChanInfo channelInfo);

	/**
	 * @param channelFormInfo
	 * @return
	 * @Title: addChanInfo
	 * @Description: 新增支付通道
	 * @return: String
	 */
	String addChanInfo(ChannelFormInfo channelFormInfo);

	/**
	 * @param channelFormInfo
	 * @return
	 * @Title: updateChanInfo
	 * @Description: 更新支付通道
	 * @return: String
	 */
	String updateChanInfo(ChannelFormInfo channelFormInfo);

	/**
	 * @param id
	 * @return
	 * @Title: deleteChanInfo
	 * @Description: 删除支付通道
	 * @return: String
	 */
	Integer deleteChanInfo(String id);

	/**
	 * @param id
	 * @return
	 * @Title: getChanInfoByid
	 * @Description: 根据id获取支付通道信息
	 * @return: ChannelFormInfo
	 */
	ChannelFormInfo getChanInfoById(String id);

	int chanCount(ChanInfo chanInfo);
}
