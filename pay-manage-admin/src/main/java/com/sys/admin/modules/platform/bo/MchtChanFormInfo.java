package com.sys.admin.modules.platform.bo;

import com.sys.core.dao.common.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商户通道
 * @author ALI
 * at 2017/9/7 14:10
 */
public class MchtChanFormInfo {

	private String mchtId;

	private String mchtName;

	private String mchtCode;

	private String mchtStatus;

	private String mchtDesc;

	List<Channel> channels;

	private Integer chanCount;

	private Integer disableCount;

	public class Channel{

		private String chanId;

		private String chanName;

		private Integer isValid;
		
		private Long rate;
		
		private Integer sort;

		public Integer getSort() {
			return sort;
		}

		public void setSort(Integer sort) {
			this.sort = sort;
		}

		public Long getRate() {
			return rate;
		}

		public void setRate(Long rate) {
			this.rate = rate;
		}

		public String getChanId() {
			return chanId;
		}

		public void setChanId(String chanId) {
			this.chanId = chanId;
		}

		public String getChanName() {
			return chanName;
		}

		public void setChanName(String chanName) {
			this.chanName = chanName;
		}

		public Integer getIsValid() {
			return isValid;
		}

		public void setIsValid(Integer isValid) {
			this.isValid = isValid;
		}
	}

	private PageInfo pageInfo;

	private static final long serialVersionUID = 1L;

	public Integer getDisableCount() {
		return disableCount;
	}

	public void setDisableCount(Integer disableCount) {
		this.disableCount = disableCount;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

	public Integer getChanCount() {
		return chanCount;
	}

	public void setChanCount(Integer chanCount) {
		this.chanCount = chanCount;
	}

	public String getMchtId() {
		return mchtId;
	}

	public void setMchtId(String mchtId) {
		this.mchtId = mchtId;
	}

	public String getMchtName() {
		return mchtName;
	}

	public void setMchtName(String mchtName) {
		this.mchtName = mchtName;
	}

	public String getMchtCode() {
		return mchtCode;
	}

	public void setMchtCode(String mchtCode) {
		this.mchtCode = mchtCode;
	}

	public String getMchtStatus() {
		return mchtStatus;
	}

	public void setMchtStatus(String mchtStatus) {
		this.mchtStatus = mchtStatus;
	}

	public String getMchtDesc() {
		return mchtDesc;
	}

	public void setMchtDesc(String mchtDesc) {
		this.mchtDesc = mchtDesc;
	}

	public MchtChanFormInfo() {
	}

	public MchtChanFormInfo(Map<String, String> paramMap) {

		this.mchtId = paramMap.get("mchtId");
		this.mchtName = paramMap.get("mchtName");
		this.mchtCode = paramMap.get("mchtCode");

		List<Channel> channels = new ArrayList<>();
		Channel channel;
		//获取通道商户支付方式ID
		List<String> keys = new ArrayList<>();
		for (String key : paramMap.keySet()) {
			if (key.contains("chanId")) {
				keys.add(key);
			}
		}
		if (CollectionUtils.isEmpty(keys)) {
			return;
		}

		for (String key : keys) {
			channel = new Channel();
			String number = key.substring(6, key.length());
			if (StringUtils.isNotBlank(paramMap.get("isValid" + number))){
				channel.setIsValid(Integer.parseInt(StringUtils.isNotBlank(paramMap.get("isValid"+ number)) ? paramMap.get("isValid"+ number) : "0"));
			}
			channel.setChanId(paramMap.get(key));
			channels.add(channel);
		}
		this.channels = channels;
	}

	public Channel getChannel() {
		return new Channel();
	}

}