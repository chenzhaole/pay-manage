package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.ExcelUtil;
import com.sys.core.dao.dmo.AccountAmount;
import com.sys.core.dao.dmo.PublicAccountInfo;
import com.sys.core.service.AccountAmountService;
import com.sys.core.service.PublicAccountInfoService;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwPublicAccountService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公户账务查询接口
 */
@Controller
@RequestMapping(value = "")
public class GwPublicAccountController {

	protected final Logger logger = LoggerFactory.getLogger(GwPublicAccountController.class);

	@Autowired
	GwPublicAccountService gwPublicAccountService;

	@Autowired
	AccountAmountService accountAmountService;

	@Autowired
	PublicAccountInfoService publicAccountInfoService;


	private String BIZ = "公户余额查询";

	/**
	 *  公户余额查询
	 */
	@RequestMapping(value="/publicaccount/queryBalance")
	@ResponseBody
	public String queryBalance(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("code",ErrorCodeEnum.SUCCESS.getCode());
		try {
			//请求ip
			String ip = IpUtil.getRemoteHost(request);
			logger.info(BIZ+"获取到客户端请求ip："+ip);
			data = URLDecoder.decode(data, "utf-8");
			logger.info(BIZ+"收到客户端请求参数后做url解码后的值为："+data);
			if(data.endsWith("=")){
				data = data.substring(0,data.length()-1);
			}
			//解析请求参数
			Map<String,String> params = JSON.parseObject(data, Map.class);
			//校验请求参数
			CommonResponse checkResp = gwPublicAccountService.checkParam(params);
			logger.info(BIZ+"校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				resultMap.put("code",checkResp.getRespCode());
				resultMap.put("msg",checkResp.getRespMsg());
			}else{
				resultMap = gwPublicAccountService.query(params);
				logger.info(BIZ+"，返回的resultMap信息："+JSONObject.toJSONString(resultMap));
			}
		} catch (Exception e) {
			logger.error(BIZ+"异常",e);
			resultMap.put("code",ErrorCodeEnum.FAILURE.getCode());
			resultMap.put("msg",ErrorCodeEnum.FAILURE.getDesc());
		}
		logger.info(BIZ+"，返回值："+JSON.toJSONString(resultMap));
		return JSON.toJSONString(resultMap);
	}

	/**
	 * 提交公户账务数据
	 */
	@RequestMapping("/publicaccount/commitPublicAccount")
	@ResponseBody
	public String commitPublicAccount(MultipartFile file, HttpServletRequest request,Model model, RedirectAttributes redirectAttributes, @RequestParam Map<String, String> paramMap) {
		String tag = "api提交公户账务数据";
		Map<String,String> resultMap = new HashMap<>();
		try {
			String publicAccountCode = paramMap.get("publicAccountCode");	//公户编号
			//请求ip
			String ip = IpUtil.getRemoteHost(request);
			logger.info(BIZ+"获取到客户端请求ip："+ip);
			//校验请求参数
			CommonResponse checkResp = gwPublicAccountService.checkParam(paramMap);
			logger.info(BIZ+"校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
			String fileName = file.getOriginalFilename();
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				resultMap.put("code",checkResp.getRespCode());
				resultMap.put("msg",checkResp.getRespMsg());
			}else if(StringUtils.isBlank(fileName)){
				resultMap.put("code","2");
				resultMap.put("msg","未上传文件");
			}else {
				java.io.File f =java.io.File.createTempFile("tmp", null);
				file.transferTo(f);
				List<String[]> data = ExcelUtil.readexcel(f, fileName);
				f.deleteOnExit();
				//获取公户信息
				PublicAccountInfo pai = new PublicAccountInfo();
				pai.setPublicAccountCode(publicAccountCode);
				List<PublicAccountInfo> pais = publicAccountInfoService.list(pai);
				pai = new PublicAccountInfo();
				if (pais != null && pais.size() > 0) {
					pai = pais.get(0);
				}
				logger.info(tag + ",publicAccountCode=" + publicAccountCode + ",fileName=" + fileName + ",excel中数据的条数为" + (data == null ? 0 : data.size()) + ",选择的公户信息为" + JSON.toJSON(pai));
				//解析excel数据到标准模型
				Map resultMap1 = accountAmountService.convertExcelDataToAccountAmount(publicAccountCode,pai.getModelName(),data);
				String errMsg = resultMap1.get("errMsg")+"";
				List<Map> aas = (List<Map>)resultMap1.get("accountAmounts");
				//批量入库
				resultMap1 = accountAmountService.batchAccountAmount(aas);
				errMsg = errMsg+resultMap1.get("errMsg");
				resultMap.put("code", ErrorCodeEnum.SUCCESS.getCode());
				resultMap.put("msg", "提交成功"+(errMsg.length()==0?"":",错误信息为"+errMsg));
			}
		} catch (Exception e) {
			resultMap.put("code","1");
			resultMap.put("msg","提交失败");
			logger.error("提交公户账务数据异常",e);
		}
		return JSON.toJSONString(resultMap);
	}
}
