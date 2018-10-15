package com.demo.servlet;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 接收Form表单数据
 */
public class RecNoticeServlet2 extends HttpServlet{
	
	@Override
	public void init() throws ServletException {
		super.init();
	}
	
	//   http://ip:port/CzlTest/noticeRcvServlet2
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("RecNoticeServlet2(接收form)接收后台通知开始（表单数据）");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");

		Map<String,String> map = getAllRequestParam(request);

		for (String key: map.keySet()) {
			System.out.println("key="+key+"  value="+map.get(key));
		}


		System.out.println("RecNoticeServlet2(接收form)接收后台通知结束（表单数据）,并返回SUCCESS");
		//返回给银联服务器http 200状态码
		response.getWriter().print("SUCCESS");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
		this.doPost(req, resp);
	}

	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				//在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				//System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
				if (null == res.get(en) || "".equals(res.get(en))) {
					res.remove(en);
				}
			}
		}
		return res;
	}
}
