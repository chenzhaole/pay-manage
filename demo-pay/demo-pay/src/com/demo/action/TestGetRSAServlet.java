package com.demo.action;

import com.demo.util.RSAUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


public class TestGetRSAServlet extends HttpServlet {

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
        String forwordUrl = "/index-rsa.jsp";
        try {

            Map<String, Object> keypair = RSAUtils.genKeyPair();
            String publicKey = RSAUtils.getPublicKey(keypair);
            String privateKey =  RSAUtils.getPrivateKey(keypair);
            request.setAttribute("privateKey", privateKey);
            request.setAttribute("publicKey", publicKey);
            request.getRequestDispatcher(forwordUrl).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write(respStr);
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> keypair = RSAUtils.genKeyPair();
        String publicKey = RSAUtils.getPublicKey(keypair);
        String privateKey =  RSAUtils.getPrivateKey(keypair);
        System.out.println("publicKey:" + publicKey);
        System.out.println("privateKey:" + privateKey);
    }
}