<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>

<html>
<head>
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
        var webRoot = './gw-web';
    </script>


    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/aui.css"/>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/app.css?v=1.0.0"/>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/loading.css?v=1.0.0"/>


    <link rel="stylesheet" href="/static/qrcode/css/Style.css">
    <title>扫码支付</title>
</head>


<script type="text/javascript">


</script>


<body onLoad="" class="bgF dialog is-loading">



<%--收单机构logo--%>
<div style="display:none">
    <%--<img src="/static/qrcode/images/logo.png"/>--%>
</div>

<!-- 主元素div -->
<div class="mainDiv">
    <!-- 引入css -->
    <link rel="stylesheet" href="/static/qrcode/css/keyCheckstand.20190403.css">

    <div class="rows" style="visibility:hidden;">
        <!-- 用于判断使用何种页面宽高 -->
        <input type="hidden" value="1" class="remChoose">

        <!-- 页面元素 -->
        <div class="header">
            <div>
                <%--收单机构logo--%>
                <%--<img src="/static/qrcode/images/logo.png" alt="">--%>
                <%--易付宝--%>
            </div>
        </div>
        <div class="shopImg">
            <img src="/static/qrcode/images/shopRed.png" alt="">
        </div>
        <%--<div class="shopOwner"> ${mchtName}--%>
    </div>

    <c:if test="${ !empty amount}">
        <div class="payment">
            <div class="" style="height: 0.5rem; text-align:center; ">
                <span class="im-monery" style="font-size: 0.18rem;  padding-top: 15px">¥  ${amount} 元</span>
            </div>
        </div>
    </c:if>


</div>
<script src="/static/qrcode/js/paymentNoAmt.js"></script>
<script src="/static/qrcode/js/common.20180928.js" type="text/javascript"></script>
</div>

<c:if test="${payStatus eq 'SUCCESS'}">
    <div class="shopOwner"><b>支付成功</b></div>
    <div class="shopOwner"><b>确认码: ${verifyCode}</b></div>
</c:if>

<c:if test="${payStatus ne 'SUCCESS'}">
    <div class="shopOwner">温馨提示</div>
    <div class="shopOwner">支付时请注意身边安全,并注意避免重复支付。</div>
</c:if>


<div class="">
    <div class="" style=" padding-top: 30px " align="center">
        <input id="btnId" type="button"
               style="-webkit-appearance:none; width:3.45rem; height: 0.5rem; font-size: 0.18rem; background-color:#e72327; color:#fff"
               value="返 回" onclick="onCancel()">
    </div>
</div>


<script src="/static/qrcode/js/base.20180928.js"></script>
</body>
</html>