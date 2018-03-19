package com.demo.action;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.demo.config.SwiftpassConfig;
//import com.demo.util.MD5;
//import com.demo.util.SignUtils;
import com.demo.util.XmlUtils;

public class TestQueryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.setCharacterEncoding("utf-8");
//        resp.setCharacterEncoding("utf-8");
//        SortedMap<String,String> map = XmlUtils.getParameterMap(req);
//        System.out.println(XmlUtils.toXml(map));
//        map.put("mch_id", SwiftpassConfig.mch_id);
//        String key = SwiftpassConfig.key;
//        String res = null;
//        String reqUrl = SwiftpassConfig.req_url+"/queryInterface_gate.action";
//        map.put("nonce_str", String.valueOf(new Date().getTime()));
//        Map<String,String> params = SignUtils.paraFilter(map);
//        StringBuilder buf = new StringBuilder((params.size() +1) * 10);
//        SignUtils.buildPayParams(buf,params,false);
//        String preStr = buf.toString();
//        String sign = MD5.sign(preStr, "&key=" + key, "utf-8");
//        map.put("sign", sign);
//
//        System.out.println("reqUrl:" + reqUrl);
//
//        CloseableHttpResponse response = null;
//        CloseableHttpClient client = null;
//        try {
//            HttpPost httpPost = new HttpPost(reqUrl);
//            StringEntity entityParams = new StringEntity(preStr+"&sign="+sign.toUpperCase(),"utf-8");
//            httpPost.setEntity(entityParams);
//            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//            client = HttpClients.createDefault();
//            response = client.execute(httpPost);
//            if(response != null && response.getEntity() != null){
//            	JSONObject jsonObject = null;
//                jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
//                Map<String,String> resultMap = (Map)jsonObject;
//                res = XmlUtils.toXml(resultMap);
//                System.out.println("请求结果：" + jsonObject);
//
//                if(resultMap.containsKey("sign") && !SignUtils.checkParam(resultMap, key)){
//                    res = "验证签名不通过";
//                }
//            }else{
//                res = "操作失败!";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            res = "操作失败";
//        } finally {
//            if(response != null){
//                response.close();
//            }
//            if(client != null){
//                client.close();
//            }
//        }
//        if(res.startsWith("<")){
//            resp.setHeader("Content-type", "text/xml;charset=UTF-8");
//        }else{
//            resp.setHeader("Content-type", "text/html;charset=UTF-8");
//        }
//        resp.getWriter().write(res);
    }
}
