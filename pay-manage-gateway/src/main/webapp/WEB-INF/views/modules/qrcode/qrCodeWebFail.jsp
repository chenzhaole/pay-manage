<%--<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>--%>
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">--%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<html>
<head>
    <%--<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>--%>
    <meta name="renderer" content="webkit"/>
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no"/>
    <!-- Page Action -->
    <meta http-equiv="Page-Enter" content="revealTrans(duration=1.0,transtion=6)">
    <meta http-equiv="Page-Exit" content="revealTrans(duration=1.0,transtion=6)">
    <meta name="Keywords" Content="收款二维码"/>

    <!-- 缓存 -->
    <meta http-equiv="Cache-Control" content="no-cache,no-store,must-revalidate"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <script src="/static/qrcode/js/jquery.min.js"></script>
    <script type="text/javascript">
        var webRoot = './qrcGtwWeb-web';
    </script>
    <link rel="stylesheet" href="/static/qrcode/css/Style.css">
    <title>易付宝-扫码支付</title>
</head>
<body>
<!-- 隐藏元素，不展示仅用于参数传递 -->
<div class="hiddes">
    <input type="hidden" name="postUrl" value='https://qr.95516.com/UP03./qrcGtwWeb-web/front/confirmOrder'>
    <input name="sessionId" type="hidden" value='180ac4eb-f75c-44a6-8767-4912bd2c816d'>
</div>

<!-- logo -->
<div style="display:none">
    <%--收单机构logo--%>
    <%--<img src="/static/qrcode/images/logo.png"/>--%>

</div>

<!-- 主元素div -->
<div class="mainDiv">
    <!-- 引入css -->
    <link rel="stylesheet" href="/static/qrcode/css/keyCheckstand.20190403.css">

    <div class="rows" style="visibility:hidden;">
        <!-- 用于判断使用何种页面宽高 -->
        <input type="hidden" value="1" class="remChoose">


        <div align="center">
            <img style=" width: 3.0rem; height: 2.8rem;" src="/static/qrcode/images/fail.png" alt="">
        </div>

        <div style="text-align: center;  padding-top: 0.6rem; padding-bottom: 0.6rem; color: #888888; font-size: 15px">
            <i>${qrMessage}</i>

        </div>

    </div>

    <!-- 错误提示div -->
    <div class="hint-wrapper">
        <div class="hint">123423</div>
    </div>
    <script src="/static/qrcode/js/base.20180928.js"></script>
</body>
</html>