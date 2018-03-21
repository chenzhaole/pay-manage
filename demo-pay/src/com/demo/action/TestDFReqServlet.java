package com.demo.action;

import com.alibaba.fastjson.JSONObject;
import com.demo.util.AESUtil;
import com.demo.util.PostUtil;
import com.demo.util.SignUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class TestDFReqServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        String respStr = "没有正确返回信息";
        try {
            String payUrl = request.getParameter("payUrl");
            String key = request.getParameter("key");


            System.out.println(request.getParameter("detail"));

            String sign = SignUtil.md5Sign(getBodyMap(request), key);
            JSONObject data = new JSONObject();
            JSONObject head = getHeadJson(request);
            JSONObject body = (JSONObject) JSONObject.toJSON(getBodyMap(request));

            data.put("head", head);
            data.put("body", body);
            data.put("sign", sign);
            System.out.println("请求数据："+data.toJSONString());
            respStr = PostUtil.post(payUrl, data.toJSONString());
            System.out.println("响应数据："+respStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().write(respStr);
    }


    private JSONObject getHeadJson(HttpServletRequest req) {
        JSONObject head = new JSONObject();
        head.put("mchtId", req.getParameter("mchtId"));
        head.put("version", req.getParameter("version"));
        head.put("biz", req.getParameter("biz"));
        return head;
    }

    private Map<String, String> getBodyMap(HttpServletRequest req) {
        Map<String,String> map = new HashMap();
        try {
            String detail = AESUtil.Encrypt(req.getParameter("detail").trim(),req.getParameter("key"));
            System.out.println("utf-8编码前："+detail);
            detail = URLEncoder.encode(detail,"UTF-8");
            System.out.println("utf-8编码后："+detail);
            map.put("batchOrderNo", req.getParameter("batchOrderNo").trim());
            map.put("totalNum", req.getParameter("totalNum").trim());
            map.put("totalAmount", req.getParameter("totalAmount").trim());
            map.put("notifyUrl", req.getParameter("notifyUrl"));
            map.put("detail", detail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
