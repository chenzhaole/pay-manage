package com.sys.admin.modules.channel.service;

import com.sys.admin.modules.channel.bo.ChanMchtFormInfo;

import java.util.List;

/**
 * 
 * @ClassName: ChanMchtAdminService 
 * @Description: 商户支付通道service
 * @author: ALI
 * @date: 2017年8月29日
 */
public interface ChanMchtAdminService {
	/**
	 * 
	 * @Title: getChannelList 
	 * @Description: 获取支付通道list
	 * @param chanMchtFormInfo
	 * @return
	 * @return: List<ChanMchtPaytype>
	 */
   List<ChanMchtFormInfo> getChannelList(ChanMchtFormInfo chanMchtFormInfo);

	/**
	 *
	 * @Title: getChannelList
	 * @Description: 获取支付通道list(单表查询)
	 * @param chanMchtFormInfo
	 * @return
	 * @return: List<ChanMchtPaytype>
	 */
	List<ChanMchtFormInfo> getChannelListSimple(ChanMchtFormInfo chanMchtFormInfo);

	/**
	 *
	 * @Title: getChannelList
	 * @Description: 获取所有通商支（只查一张表）
	 * @param chanMchtFormInfo
	 * @return
	 * @return: List<ChanMchtPaytype>
	 */
	List<ChanMchtFormInfo> getAllChannel(ChanMchtFormInfo chanMchtFormInfo);

   /**
    * 
    * @Title: addChanMchtPaytype 
    * @Description: 新增支付通道
    * @param chanMchtFormInfo
    * @return
    * @return: String
    */
   int addChanMchtPaytype(ChanMchtFormInfo chanMchtFormInfo) throws Exception;
   /**
    * 
    * @Title: updateChanMchtPaytype 
    * @Description: 更新支付通道
    * @param chanMchtFormInfo
    * @return
    * @return: String
    */
   int updateChanMchtPaytype(ChanMchtFormInfo chanMchtFormInfo) throws Exception;
   /**
    * 
    * @Title: deleteChanMchtPaytype 
    * @Description: 删除支付通道
    * @param chanMchtFormInfo
    * @return
    * @return: String
    */
   int deleteChanMchtPaytype(ChanMchtFormInfo chanMchtFormInfo);
   /**
    * 
    * @Title: getChanMchtPaytypeByid 
    * @Description: 根据id获取支付通道信息
    * @param id
    * @return
    * @return: ChanMchtFormInfo
    */
   ChanMchtFormInfo getChanMchtPaytypeById(String id);

   int chanMchtCount(ChanMchtFormInfo chanMchtFormInfo);
}
