<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0"/>
    <meta name="format-detection" content="telephone=no,email=no,date=no,address=no">
    <title>出错啦...</title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/aui.css" />
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/app.css" />
</head>
<body id="Error" class="bgF">
<header class="aui-bar aui-bar-nav">
    <!--<a class="aui-pull-left aui-btn" onclick="history.back()">
        <img src="${ctxStatic}/images/back.png"/>
    </a>-->
    <div class="aui-title">支付中心</div>
</header>
<div class="item findError">
    <!--<div class="resultBox">
     <img src="${ctxStatic}/images/fail.png" alt="" />
    </div>-->
    <div class="tipText">
        <p>${respMsg},请稍后重试！</p>
        <p>错误码：${respCode}</p>

    </div>

    <div class="submitBox">
        <a href="javascript:window.history.back();">
            <input type="submit" class="reverseBtn" value="返回">
        </a>
    </div>
</div>

<div class="tipbox">
    <c:if test="${ !empty mobile || !empty qq}">
        <p>如遇支付问题请联系客服</p>
    </c:if>
    <c:if test="${ !empty mobile}">
        <p><span class="tag">客服热线 </span><span> ${mobile}</span></p>
    </c:if>
</div>
</body>
</html>

