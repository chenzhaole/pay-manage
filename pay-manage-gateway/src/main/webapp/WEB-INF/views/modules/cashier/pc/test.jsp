<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>Title</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

</head>
<body style="text-align: center">
    <form id="search" action="${ctx}/gateway/cashier/mchtCall" method="post" >
        商户编号: <input type="text" value="18101331" name="mchtId"/><br>
        版本: <input type="text" value="20" name="version"/><br>
        支付类型:
        <select name="biz">
            <option value="">--不指定--</option>
            <option value="ca01">平台收银台</option>
            <option value="ca02">通道收银台</option>
            <option value="jh01">聚合二维码</option>
            <option value="wx01">微信h5支付</option>
            <option value="wx03">微信公众号支付</option>
            <option value="wx04">微信扫码支付</option>
            <option value="wx05">微信条码-声波支付（扫描枪扫手机）支付</option>
            <option value="wx06">微信小程序支付</option>
            <option value="ali01">支付宝线上扫码支付</option>
            <option value="ali02">支付宝线下扫码支付</option>
            <option value="ali03">支付宝条码-声波支付</option>
            <option value="ali04">支付宝服务窗支付</option>
            <option value="ali05">支付宝线上扫码转h5支付</option>
            <option value="ali06">支付宝线下扫码转h5支付</option>
            <option value="sn01">苏宁扫码支付</option>
            <option value="sn02">苏宁扫码转h5支付</option>
            <option value="qq01">QQh5支付</option>
            <option value="qq02">QQ扫码转h5支付</option>
            <option value="qq03">QQ扫码支付</option>
            <option value="jd01">京东h5支付</option>
            <option value="jd02">京东扫码支付</option>
            <option value="jd03">京东扫码转h5支付</option>
            <option value="yl01">银联二维码支付</option>
            <option value="qj03">快捷支付(老平台叫银行卡支付)</option>
            <option value="qj05">网银/网银退款支付</option>
        </select>
        <br>
        商户订单号: <input type="text" value="<%= System.currentTimeMillis() %>" name="orderId"/><br>
        订单时间: <input type="text" value="<%= new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" name="orderTime"/><br>
        金额: <input type="text" value="10" name="amount"/>【分】<br>
        商品名称: <input type="text" value="游戏充值" name="goods"/><br>
        notifyUrl: <input type="text" value="http://www.baidu.com" name="notifyUrl"/><br>
        callBackUrl: <input type="text" value="http://www.baidu.com" name="callBackUrl"/><br>
        商品描述: <input type="text" value="10元大礼包" name="desc"/><br>
        应用ID: <input type="text" value="" name="appId"/><br>
        应用名称: <input type="text" value="" name="appName"/><br>
        操作员编号: <input type="text" value="" name="operator"/><br>
        订单超时时间: <input type="text" value="" name="expireTime"/><br>
        设备类型:
        <select name="deviceType">
            <option value="">--不指定--</option>
            <option value="1">手机端</option>
            <option value="2">pc端</option>
            <option value="3">微信内</option>
            <option value="4">支付宝内</option>
        </select><br>
        签名Key: <input type="text" value="4b5893bda3254933bf5d49c3675c3bc1" name="mchtKey"/><br>
        sign: <input type="text" name="sign" id="sign"> <input type="button" value="签名" id="genSign"/><br>
        <input type="submit" value="提交"/>
    </form>
</body>
<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>

<script type="text/javascript">
    $(document).ready(function () {
        $("#genSign").click(function () {
            var param = $("#search").serialize();
            param= decodeURIComponent(param,true);
            param= encodeURI(encodeURI(param));
            console.log(param);
            $.ajax({
                type:"POST",
                url: "${ctx}/gateway/cashier/genSign?"+param,
//                data:{"payType":paytype,"sign":sign},
                dataType:'text' ,
                async:true,

                success:function(data){
                    //返回值转换成json
                    console.log(data);
                    $("#sign").val(data);
                },
                error:function(){
                    //请求错误，提示1000错误码
                    console.log("[error...]");
                }
            });
        });
    });
</script>

</html>
