package com.sys.admin.modules.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.common.utils.PictureUtils;
import com.sys.admin.common.web.BaseController;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.File;

/**
 * Created by ALI on 2017/9/29.
 */
@Controller
@RequestMapping("${adminPath}/picture")
public class PictureController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(PictureController.class);

	@RequestMapping("/upload")
	@ResponseBody
	public JSONObject upload(@RequestParam("file") MultipartFile fileUpload, HttpSession session, @RequestParam(value = "location", required = false) String location) throws Exception {
		JSONObject result = new JSONObject();

		if (StringUtils.isBlank(location)) {
			location = "";
		}

		//webapp路径
//		ServletContext servletContext = session.getServletContext();
//		String webappRootPath = servletContext.getRealPath("");
		String webappRootPath = ConfigUtil.getValue("image.path");

		//图片MD5,区别每张不同图片
		String digest = DigestUtils.md5Hex(fileUpload.getBytes());
		//文件拓展名
		String extension = PictureUtils.getExtension(fileUpload.getOriginalFilename());
		//图片保存位置
		String pictureLocation = PictureUtils.getPictureRootPath() + location;
		//图片在数据库保存的URL地址
		String picDBUrl = PictureUtils.SPRIT + location + PictureUtils.SPRIT + digest + extension;
		//图片全路径，去除domain
		String fullPathWithoutDomain = pictureLocation + PictureUtils.SPRIT + digest + extension;
		String pictureSavePath = webappRootPath + fullPathWithoutDomain;

		logger.info("图片保存位置:"+pictureLocation+",图片在数据库保存的url地址:"+picDBUrl+",图片全路径(去除domain):"+fullPathWithoutDomain+",pictureSavePath:"+pictureSavePath);
		File file = new File(pictureSavePath);
		//如果图片存在，无需再次保存
		boolean exists = file.exists();
		if(exists){
			//返回
			result.put("picURL", fullPathWithoutDomain);//PictureUtils.PIC_DOMAIN +
			return result;
		}

		//如果不存在，先保存到对应路径
		if (!exists) {
			File directory = new File(webappRootPath + pictureLocation);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			fileUpload.transferTo(file);
		}
		//返回
		result.put("picURL",  fullPathWithoutDomain);//PictureUtils.PIC_DOMAIN +
		return result;
	}

	@RequestMapping("/get")
	@ResponseBody
	public String getLocation(HttpSession session) throws Exception {
		JSONObject result = new JSONObject();

		//webapp路径
		ServletContext servletContext = session.getServletContext();
		String webappRootPath = servletContext.getRealPath("");
		return webappRootPath;
	}
}
