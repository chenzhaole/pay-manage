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


<script type="text/javascript" charset="UTF-8" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script type="text/javascript">


    /*
     调用微信JS api 支付
     */
    function onBridgeReady() {


        WeixinJSBridge.invoke('getBrandWCPayRequest', {
                    "appId": "${jsonWechatMap.appId}",     //公众号名称，由商户传入
                    "timeStamp": "${jsonWechatMap.timeStamp}",         //时间戳，自1970年以来的秒数
                    "nonceStr": "${jsonWechatMap.nonceStr}", //随机串
                    "package": "${jsonWechatMap.packageV}",//package内置关键字不能直接使用
                    "signType": "${jsonWechatMap.signType}",  //微信签名方式：
                    "paySign": "${jsonWechatMap.paySign}" //微信签名
                },
                function (res) {
                    if (res.err_msg == "get_brand_wcpay_request:ok") {
                        //todo:安卓OK,苹果NG
//                        alert("支付成功:"+res.err_msg);
                    } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
                        //页面点击返回按钮
//                        alert("支付返回: "+res.err_msg)
                    } else {
                        //todo:跳转错误页面
                        // alert("支付失败,请重新尝试");
                    }
                });

        $(".curtain").remove();
        setTimeout("window.location.href='/gwqr/toQrCodeOrderFinishPage?mchtOrderId=${jsonWechatMap.mchtOrderId}'", 5000);

    }// end of onBridgeReady



    /*
     判断微信js对象
     */
    if (typeof WeixinJSBridge == "undefined") {
        if (document.addEventListener) {
            document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
        } else if (document.attachEvent) {
            document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
            document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
        }
    } else {
        onBridgeReady();
    }


    /*
     点击页面返回按钮
     */
    function onCancel() {
        //todo;完成页面
        window.location.href='/gwqr/toQrCodeOrderFinishPage?mchtOrderId=${mchtOrderId}';
    }



</script>


<body onLoad="onBridgeReady()" class="bgF dialog is-loading">

<div class="curtain">
    <div class="loader">
        loading...
    </div>
</div>


<!-- 隐藏元素，不展示仅用于参数传递 -->
<div class="hiddes">
    <input type="hidden" name="postUrl" value='${confirmMap.postUrl}'>
    <input type="hidden" name="sessionId" value='${confirmMap.token}'>
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
        <%--<div class="shopOwner"> ${mchtName}--%>
    </div>


    <div class="payment">
        <div class="" style="height: 0.5rem; text-align:center; ">
            <span class="im-monery" style="font-size: 0.18rem;  padding-top: 15px">¥  ${amount} 元</span>
        </div>
    </div>


</div>
<script src="/static/qrcode/js/paymentNoAmt.js"></script>
<script src="/static/qrcode/js/common.20180928.js" type="text/javascript"></script>
</div>


<div class="shopOwner">温馨提示</div>
<div class="shopOwner">支付时请注意身边安全,并注意避免重复支付。</div>


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