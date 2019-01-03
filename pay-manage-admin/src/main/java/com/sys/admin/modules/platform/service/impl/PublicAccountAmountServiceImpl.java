package com.sys.admin.modules.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.admin.modules.platform.service.PublicAccountAmountService;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.dmo.AccountAmount;
import com.sys.core.service.AccountAmountService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author duanjintang
 * 2019-01-03
 */
@Service
public class PublicAccountAmountServiceImpl implements PublicAccountAmountService {
	private static final Logger logger = LoggerFactory.getLogger(PublicAccountAmountServiceImpl.class);
	@Autowired
	private AccountAmountService accountAmountService;

	/**
	 * 转换excel中数据为账务金额模型数据
	 * @param  publicAccountCode  公户编号
	 * @param  modelName
	 * @return
	 */
	public List<AccountAmount> convertExcelDataToAccountAmount(String publicAccountCode,String modelName,List<String[]> data){
		String id = IdUtil.createCommonId();
		List<AccountAmount> accountAmounts = new ArrayList<>();
		logger.info("excel数据转换,modelName="+modelName+",publicAccountCode="+publicAccountCode);
		if("m1".equalsIgnoreCase(modelName)){
			//模板
			int row = 1;
			for(String[] ss:data){
				if(row>=2){
					AccountAmount accountAmount = new AccountAmount();
					accountAmount.setId(id);
					accountAmount.setPublicAccountCode(publicAccountCode);	//公户编号
					accountAmount.setTradeTime(DateUtils.parseDate(ss[0].replaceAll("-","")+StringUtils.leftPad(ss[10],6,"0"),"yyyyMMddHHmmss"));
					//交易时间
					accountAmount.setBankSerialNo(ss[1]);					//银行流水号
					accountAmount.setAddAmount(new BigDecimal(ss[3]));		//增加金额
					accountAmount.setReduceAmount(new BigDecimal(ss[2]));	//减少金额
					accountAmount.setBalance(new BigDecimal(ss[4]));		//余额
					accountAmount.setAccountName(ss[8]);					//对方名称
					accountAmount.setAccountNo(ss[7]);						//对方账号
					accountAmount.setOpenAccountBankName(ss[9]);			//对方开户行
					accountAmount.setSummary(ss[6]);						//摘要
					accountAmount.setRemark(ss[11]);						//备注
					accountAmount.setCreateTime(new Date());				//创建时间
					accountAmounts.add(accountAmount);
					logger.info("第"+row+"行,modelName="+modelName+",accountAmount数据为:"+ JSON.toJSON(accountAmount));
				}else{
					logger.info("第"+row+"行,modelName="+modelName+",accountAmount数据为:"+ss[0]+"\t"+ss[1]+"\t"+ss[2]+"\t"+ss[3]+"\t"+ss[4]+"\t"+ss[5]+"\t"+ss[6]+"\t"+ss[7]+"\t"+ss[8]+"\t"+ss[9]+"\t"+ss[10]+"\t"+ss[11] );
				}
				row++;
			}
		}else if("m2".equalsIgnoreCase(modelName)){
			//模板2
			int row = 1;
			for(String[] ss:data){
				if(row>=2){
					AccountAmount accountAmount = new AccountAmount();
					accountAmount.setId(id);
					accountAmount.setPublicAccountCode(publicAccountCode);			//公户编号
					accountAmount.setTradeTime(DateUtils.parseDate(ss[0],"yyyy-MM-dd HH:mm"));//交易时间
					if(new BigDecimal(ss[4]).compareTo(BigDecimal.ZERO)>0){
						accountAmount.setAddAmount(new BigDecimal(ss[4]));			//增加金额
					}else{
						accountAmount.setReduceAmount(new BigDecimal(ss[4]).abs());	//减少金额
					}
					accountAmount.setBalance(new BigDecimal(ss[5]));				//余额
					accountAmount.setAccountName(ss[6]);							//对方名称
					accountAmount.setAccountNo(ss[7]);								//对方账号
					accountAmount.setOpenAccountBankName(ss[8]);					//对方开户行
					accountAmount.setRemark(ss[9]);									//备注
					accountAmount.setCreateTime(new Date());						//创建时间
					accountAmounts.add(accountAmount);
					logger.info("第"+row+"行,modelName="+modelName+",accountAmount数据为:"+ JSON.toJSON(accountAmount));
				}else{
					logger.info("第"+row+"行,modelName="+modelName+",accountAmount数据为:"+ss[0]+"\t"+ss[1]+"\t"+ss[2]+"\t"+ss[3]+"\t"+ss[4]+"\t"+ss[5]+"\t"+ss[6]+"\t"+ss[7]+"\t"+ss[8]+"\t"+ss[9]);
				}
				row++;
			}
		}
		return accountAmounts;
	}

	/**
	 * 批量导入公户账务数据
	 * @return
	 */
	public int batchAccountAmount(List<AccountAmount> accountAmounts){
		int row = 0;
		if(accountAmounts!=null){
			for(AccountAmount aa:accountAmounts){
				accountAmountService.create(aa);
				row++;
			}
		}
		return row;
	}


}
