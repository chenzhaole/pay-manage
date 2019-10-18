<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>

<html>
<head>
    <meta name="renderer" content="webkit"/>
    <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no"/>
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
    <link rel="stylesheet" href="/static/qrcode/css/Style.css">
    <title>扫码支付</title>
</head>
<body>
<!-- 隐藏元素，不展示仅用于参数传递 -->
<div class="hiddes">
    <input type="hidden" name="postUrl" value='${dataMap.postUrl}'>
    <input type="hidden" name="sessionId" value='${dataMap.token}'>
</div>

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
        <div class="shopOwner"> ${dataMap.mchtName}
        </div>

        <div class="payment">
            <div>
                <p style="padding-left:10px;padding-top:10px">付款金额</p>
                <div class="im-input">
                    <span class="im-monery"  style="padding-left:10px">¥</span><span class="payNum"></span>
                </div>
            </div>
        </div>
        <div class="keyboard">

            <div class="keyNum">
                <div class="im-kb-bt">1</div>
                <div class="im-kb-bt">2</div>
                <div class="im-kb-bt">3</div>
                <div class="im-kb-bt">4</div>
                <div class="im-kb-bt">5</div>
                <div class="im-kb-bt">6</div>
                <div class="im-kb-bt">7</div>
                <div class="im-kb-bt">8</div>
                <div class="im-kb-bt">9</div>
                <div class="im-kb-bt">.</div>
                <div class="im-kb-bt zero">0</div>
            </div>
            <div class="keyText">
                <div class="im-kb-bt im-kb-del">
                    <div><img src="/static/qrcode/images/keyDelete.png" alt=""></div>
                </div>
                <div class="im-kb-bt im-kb-submit">确定</div>
            </div>
        </div>
    </div>
    <script src="/static/qrcode/js/paymentNoAmt.js"></script>
    <script src="/static/qrcode/js/common.20180928.js" type="text/javascript"></script>
</div>

<!-- 错误提示div -->
<div class="hint-wrapper">
    <div class="hint"></div>
</div>
<script src="/static/qrcode/js/base.20180928.js"></script>
</body>
</html>