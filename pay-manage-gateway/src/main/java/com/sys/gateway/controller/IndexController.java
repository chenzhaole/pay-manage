package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.DesUtil32;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.gateway.common.ClientUtil;
import com.sys.gateway.service.QrCodeWebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应微信发送的Token验证
 */
@Controller
@RequestMapping(value = "")
public class IndexController {

    protected final Logger logger = LoggerFactory.getLogger(IndexController.class);

    /**
     * 表单数据
     */
    @RequestMapping("/test/recParam")
    @ResponseBody
    public String recParam(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {

        System.out.println("recParam(接收form)接收后台通知开始（表单数据）");
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
        return "";
    }

    /**
     * Data数据
     */
    @RequestMapping("/test/recData")
    @ResponseBody
    public String recData(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {

        System.out.println("recData(接收data)接收后台通知开始");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
//		response.setContentType("application/json;charset=UTF-8");
        response.setContentType("application/json;charset=GBK");

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jb);
        System.out.println("RecNoticeServlet(接收data)接收后台通知结束,并返回SUCCESS");
        //返回给银联服务器http 200状态码
        response.getWriter().print("SUCCESS");
        return "";
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


    /**
     * 响应微信发送的Token验证
     */
    @RequestMapping("/gwqr")
    @ResponseBody
    public String wxconnect(HttpServletRequest req, HttpServletResponse response, Model model) throws IOException {

        Signature sg = new Signature(
                req.getParameter("signature"),
                req.getParameter("timestamp"),
                req.getParameter("nonce"),
                req.getParameter("echostr"));
        String method = req.getMethod();
        // 如果是微信发过来的GET请求
        if("GET".equals(method)){
            if(new CheckUtil().checkSignature(sg)){
                String rtn = sg.getEchostr();
                System.out.println("微信连接成功！ sg.getEchostr()="+rtn);
                return rtn;
            }
        }
        return "12345678909123456";

    }

    class CheckUtil {

        private static final String token = "yfb";

        public boolean checkSignature(Signature sg) {

            String[] arr = new String[] { token, sg.getTimestamp(), sg.getNonce() };
            // 排序
            Arrays.sort(arr);
            // 生成字符串
            StringBuffer content = new StringBuffer();
            for (int i = 0; i < arr.length; i++) {
                content.append(arr[i]);
            }

            // sha1加密
            String temp = getSha1(content.toString());
            // 比较
            return temp.equals(sg.getSignature());
        }

        // 加密算法
        public String getSha1(String str) {
            if (str == null || str.length() == 0) {
                return null;
            }

            char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            try {
                MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
                mdTemp.update(str.getBytes("UTF-8"));
                byte[] md = mdTemp.digest();
                int j = md.length;
                char buf[] = new char[j * 2];
                int k = 0;

                for (int i = 0; i < j; i++) {
                    byte byte0 = md[i];
                    buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                    buf[k++] = hexDigits[byte0 & 0xf];
                }

                return new String(buf);
            } catch (Exception e) {
                return null;
            }

        }
    }


    class Signature {
        private String signature;
        private String timestamp;
        private String nonce;
        private String echostr;

        public Signature() {
            super();
            // TODO Auto-generated constructor stub
        }

        public Signature(String signature, String timestamp, String nonce, String echostr) {
            super();
            this.signature = signature;
            this.timestamp = timestamp;
            this.nonce = nonce;
            this.echostr = echostr;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public String getEchostr() {
            return echostr;
        }

        public void setEchostr(String echostr) {
            this.echostr = echostr;
        }

        @Override
        public String toString() {
            return "Signature [signature=" + signature + ", timestamp=" + timestamp + ", nonce=" + nonce + ", echostr="
                    + echostr + "]";
        }
    }




}
