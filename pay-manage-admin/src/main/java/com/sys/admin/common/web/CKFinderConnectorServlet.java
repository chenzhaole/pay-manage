package com.sys.admin.common.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ckfinder.connector.ConnectorServlet;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.utils.*;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.util.DateUtils;
import com.sys.common.util.FileUtils;
import com.sys.common.util.FtpClient;
import com.sys.common.util.ImageUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CKFinderConnectorServlet extends ConnectorServlet {
	
	private static final long serialVersionUID = 1L;

	Logger logger = LoggerFactory.getLogger(CKFinderConnectorServlet.class);
	private static SimpleDateFormat fileFormatter;// 文件命名格式:yyyyMMddHHmmssSSS
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		prepareGetResponse(request, response, false);
		super.doGet(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		prepareGetResponse(request, response, true);
//		super.doPost(request, response);
		if (logger.isDebugEnabled()) {
			logger.debug("--- BEGIN DOPOST ---");
		}
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();

		String baseDir = GlobalConfig.getImagePath();

		// 从请求参数中获取上传文件的类型：File/Image/Flash
		String typeStr = request.getParameter("Type");
		if (typeStr == null) {
			typeStr = "File";
		}
		if (logger.isDebugEnabled()) {
			logger.debug(typeStr);
		}
		// 实例化dNow对象，获取当前时间
		Date dNow = new Date();
		// 文件名和文件真实路径
		String fileUrl = "";
		// 使用Apache Common组件中的fileupload进行文件上传
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(request);
			Map fields = new HashMap();
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					fields.put(item.getFieldName(), item.getString());
				} else {
					fields.put(item.getFieldName(), item);
				}
			}
			// CEKditor中file域的name值是upload
			FileItem uplFile = (FileItem) fields.get("upload");
			// 获取文件名并做处理
			String fileNameLong = uplFile.getName();
			fileNameLong = fileNameLong.replace('\\', '/');
			String[] pathParts = fileNameLong.split("/");
			String fileName = pathParts[pathParts.length - 1];
			// 获取文件扩展名
			String ext = getExtension(fileName);
			// 设置上传文件名
			fileName = fileFormatter.format(dNow) + "." + ext;
			//图片上传到ftp服务器
			String imgPath = "";
			if (uplFile.getSize() > 0) {
				if (StringUtils.isBlank(fileName)) {
					fileName = System.currentTimeMillis() + "." + ext;
				}

                String dir = System.getProperty("java.io.tmpdir") + "uploadTempFile/" + System.currentTimeMillis() + "/";
                File file = new File(dir);
                if (!file.exists()) {
                    file.mkdirs();
                }

                File tempFile = new File(dir + fileName);
                if (!tempFile.createNewFile()) {
                    return;
                }
                uplFile.write(tempFile);

                if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)
                        || "png".equalsIgnoreCase(ext) || "gif".equalsIgnoreCase(ext)
                        || "bmp".equalsIgnoreCase(ext)) {
                    if (ConstUtils.WATERMARK_SWITCH_ON.equals(GlobalConfig.getWatermarkSwitch())) {
                        ImageUtils.pressImage(dir + fileName, ConstUtils.WATERMARK_IMAGE_PATH, ConstUtils.WATERMARK_POSITION_BOTTOM_RIGHT, 1f, true);
                    }
                }
                //img_file.transferTo(tempFile);

				String ftp_url = GlobalConfig.getFTPUrl();
				int ftp_port = Integer.valueOf(GlobalConfig.getFTPPort());
				String ftp_user = GlobalConfig.getFTPUser();
				String ftp_password = GlobalConfig.getFTPPwd();

				FtpClient ftp = new FtpClient(ftp_url, ftp_port, ftp_user, ftp_password);
				ftp.ftpLogin();
				imgPath = baseDir + DateUtils.getNoSpSysDateString();
				ftp.uploadFile(tempFile, imgPath);
				ftp.ftpLogOut();
			}
			String image_server = GlobalConfig.getImgServer();
			fileUrl = image_server+imgPath + "/" + fileName;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		// CKEditorFuncNum是回调时显示的位置，这个参数必须有
		String callback = request.getParameter("CKEditorFuncNum");
		out.println("<script type=\"text/javascript\">");
		out.println("window.parent.CKEDITOR.tools.callFunction(" + callback + ",'" + fileUrl + "',''" + ")");
		out.println("</script>");
		out.flush();
		out.close();
		if (logger.isDebugEnabled()) {
			logger.debug("--- END DOPOST ---");
		}
	}
	
	private void prepareGetResponse(final HttpServletRequest request,
			final HttpServletResponse response, final boolean post) throws ServletException {

		// 格式化目录和文件命名方式
		fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String command = request.getParameter("command");
		String type = request.getParameter("type");
		// 初始化时，如果startupPath文件夹不存在，则自动创建startupPath文件夹
		if ("Init".equals(command)){
			User user = UserUtils.getUser();
			if (user!=null){
				String startupPath = request.getParameter("startupPath");// 当前文件夹可指定为模块名
				if (startupPath!=null){
					String[] ss = startupPath.split(":");
					if (ss.length==2){
						String path = "/userfiles/"+user.getId()+"/"+ss[0]+ss[1];
						String realPath = request.getSession().getServletContext().getRealPath(path);
						FileUtils.createDirectory(realPath);
					}
				}
			}
		}
		// 快捷上传，自动创建当前文件夹，并上传到该路径
		else if ("QuickUpload".equals(command) && type!=null){
			User user = UserUtils.getUser();
			if (user!=null){
				String currentFolder = request.getParameter("currentFolder");// 当前文件夹可指定为模块名
				String path = "/userfiles/"+user.getId()+"/"+type+(currentFolder!=null?currentFolder:"");
				String realPath = request.getSession().getServletContext().getRealPath(path);
				FileUtils.createDirectory(realPath);
			}
		}
//		System.out.println("------------------------");
//		for (Object key : request.getParameterMap().keySet()){
//			System.out.println(key + ": " + request.getParameter(key.toString()));
//		}
	}

	/**
	 * 获取扩展名的方法
	 */
	private String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	/**
	 * 获取文件名的方法
	 */
	private static String getNameWithoutExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
}
