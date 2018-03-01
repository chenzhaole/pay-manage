package com.sys.admin.modules.channel.service.impl;

import com.sys.admin.common.service.BaseService;
import com.sys.admin.modules.channel.bo.ChannelFormInfo;
import com.sys.admin.modules.channel.service.ChannelAdminService;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.service.ChannelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
/**
 * 
 * @ClassName: ChannelAdminServiceImpl 
 * @Description: TODO
 * @author: cheng_fei
 * @date: 2017年8月29日 上午11:04:17
 */
@Transactional
@Service
public class ChannelAdminServiceImpl extends BaseService implements ChannelAdminService{
	@Autowired
	ChannelService channelService;
	@Override
	public List<ChanInfo> getChannelList(ChanInfo chanInfo) {
		return channelService.list(chanInfo);
	}

	@Override
	public String addChanInfo(ChannelFormInfo channelFormInfo) {
		String result = "success";
		try{
			if(channelFormInfo != null){
				ChanInfo chanInfo = new ChanInfo();
				BeanUtils.copyProperties(channelFormInfo, chanInfo);
				chanInfo.setCreateDate(new Date());
				chanInfo.setUpdateDate(new Date());
				//系统生成chanInfo
				/*String chanID = DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS");*/
				//这里我们为了业务的方便，将id跟chan_code的值设置成一样
				chanInfo.setId(channelFormInfo.getChanCode());
				channelService.create(chanInfo);
			}
		}catch(Exception e){
			e.printStackTrace();
			result = "fail";
		}
		return result;
	}

	@Override
	public String updateChanInfo(ChannelFormInfo channelFormInfo) {
		String result = "success";
		try{
			if(channelFormInfo != null){
				ChanInfo chanInfo = new ChanInfo();
				BeanUtils.copyProperties(channelFormInfo, chanInfo);
				chanInfo.setUpdateDate(new Date());
				channelService.saveByKey(chanInfo);
			}
		}catch(Exception e){
			e.printStackTrace();
			result = "fail";
		}
		return result;
	}

	@Override
	public Integer deleteChanInfo(String id) {
		return channelService.deleteByKey(id);
	}

	@Override
	public ChannelFormInfo getChanInfoById(String id) {
		if(StringUtils.isNotBlank(id)){
			ChanInfo chanInfo = channelService.queryByKey(id);
			if(chanInfo != null){
				ChannelFormInfo channelFormInfo = new ChannelFormInfo();
				BeanUtils.copyProperties(chanInfo, channelFormInfo);
				return channelFormInfo;
			}
		}
		return null;
	}

	@Override
	public int chanCount(ChanInfo chanInfo) {
		return channelService.chanCount(chanInfo);
	}

}
