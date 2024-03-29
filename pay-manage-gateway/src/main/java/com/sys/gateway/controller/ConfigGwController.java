package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.gateway.common.CryptoEncode;
import com.sys.gateway.service.ConfigGwService;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Description:
 * @author: ChenZL
 * @time: 2017年8月23日
 */

@Controller
@RequestMapping(value = "")
public class ConfigGwController {

	protected final Logger logger = LoggerFactory.getLogger(ConfigGwController.class);

	@Autowired
	private ConfigGwService configGwService;

	private static final String USER_KEY = "QVclMPCE";

	private static final String SUCCESS = "0000";
	private static final String FAIL = "7777";

	/**
	 * http://127.0.0.1:8580/myepay-manage-gateway/config/platSdkConfig?version=1.8
	 *
	 * @param data
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 * @throws java.io.IOException
	 */
	@RequestMapping(value = "config/platSdkConfig", method = RequestMethod.POST)
	@ResponseBody
	public String platSdkConfig(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
		logger.info("platSdkConfig收到客户端请求参数：data=" + data);
		String respStr = "";
		String decodeData;
		String resultCode = FAIL;
		try {
//			data = URLDecoder.decode(data, "utf-8");
//			if (data.startsWith("data=")) {
//				data = data.substring(5);
//			}
			byte[] dataCode = Base64.decodeBase64(data);
			byte[] dataDeCode = CryptoEncode.ZCryptoDecode(USER_KEY.getBytes(), dataCode);
			decodeData = new String(dataDeCode);
			JSONObject dataJson = JSON.parseObject(decodeData);
			String version = dataJson.getString("version"); // 2.0
			if (StringUtils.isBlank(version)) return null;
			String productCode = dataJson.getString("productCode"); //支付产品代码
			String userId = dataJson.getString("userId"); //用户Id
			String sim = dataJson.getString("sim"); //手机号
			String imsi = dataJson.getString("imsi");
			String imei = dataJson.getString("imei");
			String lon = dataJson.getString("lon"); //经度
			String lat = dataJson.getString("lat"); //纬度

			respStr = configGwService.querySdkConfig(version);
			if (StringUtils.isNotBlank(respStr)) {
				resultCode = SUCCESS;
			}

//			byte[] code2 = Base64.decodeBase64(respStr);
//			byte[] deCode = CryptoEncode.ZCryptoDecode(USER_KEY.getBytes(), code2);
//			respStr = new String(deCode);
		} catch (Exception e) {
			logger.error("platSdkConfig 查询SDK配置信息系统异常：" + e.getMessage());
			e.printStackTrace();
			resultCode = FAIL;
		}

		JSONObject result = new JSONObject();
		result.put("code", resultCode);
		result.put("info", respStr);
		String respStrJson = result.toJSONString();

		byte[] code0 = respStrJson.getBytes();
		byte[] code = CryptoEncode.ZCryptoEncode(USER_KEY.getBytes(), code0); //加密
		return Base64.encodeBase64String(code); //base64编码
	}

	/**
	 * http://127.0.0.1:8580/myepay-manage-gateway/config/platAuthAndroid/{mchappid}
	 * 2018-01-13 老sdk取不到userid,导致接口请求超时，影响支付
	 * http://gw.zpaychina.com/config/platAuthAndroid
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 * @throws java.io.IOException
	 */
	@RequestMapping(value = "config/platAuthAndroid/{mchappid}", method = RequestMethod.POST)
	@ResponseBody
	public Object platAuthAndroid(@PathVariable String mchappid, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
		logger.info("platAuthAndroid收到客户端请求参数：mchappid=" + mchappid);
		String userId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		String result = "{\"code\":\"0\",\"data\":{\"appId\":\"wxfe1848d43cdbb2eb\",\"appSign\":\"82c1abdec9d32dc9e4e23cb2663712e1\",\"code\":\"0\",\"data\":\"1f63bc09cd5984a3d6357c82e38d583c0\",\"mchId\":\"\",\"orderId\":\"8023637022391584594\",\"payConfigList\":{\"consumerConf\":{\"consumerQQ\":\"2845189775\",\"consumerTel\":\"\"},\"domainList\":[{\"type\":\"1\",\"url\":\"http:\\/\\/thirdpay.zpaychina.com:80\"},{\"type\":\"3\",\"url\":\"http:\\/\\/gw.zpaychina.com\\/config\"}],\"payConfigList\":[{\"isShowPayPage\":\"0\",\"isShowPayResultPage\":\"0\",\"paymentTypeList\":[{\"alipay\":\"0\",\"wxpay\":\"1\"}]}]},\"payConfigUpdateTime\":\"\",\"payWay\":\"app\",\"providerNickname\":\"zxbank_sz\",\"sdkType\":\"swiftpass\"},\"phoneNumber\":\"11125458952\",\"userId\":\""+ userId+"\"}\n";
		JSON json2 = JSON.parseObject(result);
		System.out.println(json2.toString());
		return json2.toString(); //base64编码
	}

	/**
	 * http://127.0.0.1:8580/myepay-manage-gateway/config/platAuthIOS/{mchappid}
	 * 2018-01-13 老sdk取不到userid,导致接口请求超时，影响支付
	 * http://gw.zpaychina.com/config/platAuthIOS
	 *
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 * @throws java.io.IOException
	 */
	@RequestMapping(value = "config/platAuthIOS/{mchappid}", method = RequestMethod.POST)
	@ResponseBody
	public Object platAuthIOS(@PathVariable String mchappid, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
		logger.info("platAuthIOS收到客户端请求参数：mchappid=" + mchappid);
		String userId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		String result = "{\"code\":\"0\",\"data\":{\"appId\":\"wxfe1848d43cdbb2eb\",\"appSign\":\"82c1abdec9d32dc9e4e23cb2663712e1\",\"code\":\"0\",\"data\":\"1f63bc09cd5984a3d6357c82e38d583c0\",\"mchId\":\"\",\"orderId\":\"8023637022391584594\",\"payConfigList\":{\"consumerConf\":{\"consumerQQ\":\"2845189775\",\"consumerTel\":\"\"},\"domainList\":[{\"type\":\"1\",\"url\":\"http:\\/\\/thirdpay.zpaychina.com:80\"},{\"type\":\"3\",\"url\":\"http:\\/\\/gw.zpaychina.com\\/config\"}],\"payConfigList\":[{\"isShowPayPage\":\"0\",\"isShowPayResultPage\":\"0\",\"paymentTypeList\":[{\"alipay\":\"0\",\"wxpay\":\"1\"}]}]},\"payConfigUpdateTime\":\"\",\"payWay\":\"app\",\"providerNickname\":\"zxbank_sz\",\"sdkType\":\"swiftpass\"},\"phoneNumber\":\"11125458952\",\"userId\":\""+userId+"\"}\n";
		JSON json2 = JSON.parseObject(result);
		System.out.println(json2.toString());
		return json2.toString(); //base64编码
	}

}
