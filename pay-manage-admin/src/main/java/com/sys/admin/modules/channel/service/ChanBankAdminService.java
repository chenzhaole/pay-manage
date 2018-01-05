package com.sys.admin.modules.channel.service;

import com.sys.admin.modules.channel.bo.ChanBankFormInfo;

import java.util.List;

/**
 * 
 * @ClassName: ChanMchtAdminService 
 * @Description: 商户支付通道service
 * @author: ALI
 * @date: 2017年8月29日
 */
public interface ChanBankAdminService {
	/**
	 * 
	 * @Title: getChannelList 
	 * @Description: 获取支付通道list
	 * @param ChanBankFormInfo
	 * @return
	 * @return: List<ChanBankFormInfo>
	 */
   List<ChanBankFormInfo> search(ChanBankFormInfo chanBankFormInfo);

   /**
    * 
    * @Title: addChanPaytypeBank 
    * @Description: 新增支付通道
    * @param ChanBankFormInfo
    * @return
    * @return: String
    */
   int addChanPaytypeBank(ChanBankFormInfo chanBankFormInfo) throws Exception;
   /**
    * 
    * @Title: updateChanPaytypeBank 
    * @Description: 更新支付通道
    * @param ChanBankFormInfo
    * @return
    * @return: String
    */
   int updateChanPaytypeBank(ChanBankFormInfo chanBankFormInfo) throws Exception;
   /**
    * 
    * @Title: deleteChanPaytypeBank 
    * @Description: 删除支付通道
    * @param ChanBankFormInfo
    * @return
    * @return: String
    */
   int deleteChanPaytypeBank(ChanBankFormInfo chanBankFormInfo) throws Exception;
   /**
    * 
    * @Title: getChanPaytypeBankByid 
    * @Description: 根据id获取支付通道信息
    * @param id
    * @return
    * @return: ChanBankFormInfo
    */
   ChanBankFormInfo getChanPaytypeBankById(String id);

   int chanBankCount(ChanBankFormInfo chanBankFormInfo);
}
