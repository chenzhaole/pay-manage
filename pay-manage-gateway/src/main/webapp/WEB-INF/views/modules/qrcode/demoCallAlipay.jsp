<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head><title>请不要关闭页面,支付跳转中.....</title></head>
<script src="https://gw.alipayobjects.com/as/g/h5-lib/alipayjsapi/3.1.1/alipayjsapi.inc.min.js"></script>
<script type="text/javascript">
    window.onload = function(){
        ap.tradePay({
            tradeNO: '2019091022001470550553711364'
        }, function (res) {
            ap.alert("支付失败," + res.resultCode);
        });

        $(".curtain").remove();
        setTimeout("window.location.href='/gwqr/toQrCodeOrderFinishPage?mchtOrderId=${jsonWechatMap.id}'", 5000);
    }

</script>
</html>