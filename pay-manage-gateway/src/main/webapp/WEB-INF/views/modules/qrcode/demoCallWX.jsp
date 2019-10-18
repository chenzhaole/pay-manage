<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head><title>请不要关闭页面,wx支付跳转中.....</title></head>
<script type="text/javascript" charset="UTF-8" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script type="text/javascript">
    function onBridgeReady() {
        WeixinJSBridge.invoke('getBrandWCPayRequest', {
                    "appId": "wxeb6e671f5571abce",     //公众号名称，由商户传入
                    "timeStamp": "1568277000",         //时间戳，自1970年以来的秒数
                    "nonceStr": "20c445c4c47741248266bb4fcaa094a1", //随机串
                    "package": "prepay_id=wx12163000687804c8f88491ff1751868600",//package内置关键字不能直接使用
                    "signType": "RSA",  //微信签名方式：
                    "paySign": "ms7XNdMsTJIx0p2MwMTUmMsbqvvSg9aKfkiP9GXCZ1squMir6wCkJFq16jI7NeE8Rad4soIaTlNSuRpaJAgsw6kGhgtflUqV3CheD42nELsMAxhwpAtZztfF7ZT93IU0fPoH1DnnUw8tP/Qo6BhMDyCPw8reiGlMJsxsXR+mVocBY5EofhPbGWrorQjZ0Ap+CGmWrY5PN4XAlaPdB3Opqvo7Vyz7kDJqXKeqJM6levXwM9fCjhp/Ec5j1X/Ll1d3WPoNO1I76hz36jRAAsnObk1l+sI4i3qpgSMRijOAUQ18V4voXrqYL8rVZmb2uRSXCBT5p1wr8/xgEr8rZsB08w==" //微信签名
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



</script>
<body onLoad="onBridgeReady()" class="bgF dialog is-loading">
</body>
</html>