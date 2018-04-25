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
        商户编号: <input type="text" value="1848e6fe" name="mchtId"/><br>
        版本: <input type="text" value="20" name="version"/><br>
        支付类型:
        <select name="biz">
            <option value="">--不指定--</option>
            <option value="ca001">平台收银台</option>
            <option value="ca002">通道收银台</option>
            <option value="jh001">聚合二维码</option>
            <option value="wx000">微信组合支付类型</option>
            <option value="wx101">微信h5支付</option>
            <option value="wx201">微信APP支付</option>
            <option value="wx301">微信公众号支付原生方式</option>
            <option value="wx302">微信公众号支付非原生方式</option>
            <option value="wx401">微信扫码支付</option>
            <option value="wx501">微信条码-声波支付（扫描枪扫手机）支付</option>
            <option value="wx502">微信付款码包装成PC支付</option>
            <option value="wx503">微信付款码包装成h5支付</option>
            <option value="wx601">微信小程序支付</option>
            <option value="al000">支付宝组合支付类型</option>
            <option value="al101">支付宝h5支付</option>
            <option value="al102">支付宝扫码转h5支付</option>
            <option value="al104">支付宝pc支付</option>
            <option value="al201">支付宝APP支付</option>
            <option value="al301">支付宝服务窗支付</option>
            <option value="al401">支付宝扫码支付</option>
            <option value="al501">支付宝条码-声波支付</option>
            <option value="al502">支付宝付款码包装成PC支付</option>
            <option value="al503">支付宝付款码包装成h5支付</option>
            <option value="sn000">苏宁组合支付类型</option>
            <option value="sn101">苏宁H5支付</option>
            <option value="sn102">苏宁扫码转h5支付</option>
            <option value="sn401">苏宁扫码支付</option>
            <option value="sn501">苏宁条码-声波</option>
            <option value="sn502">苏宁付款码包装成PC支付</option>
            <option value="sn503">苏宁付款码包装成h5支付</option>
            <option value="qq000">QQ组合支付类型</option>
            <option value="qq101">QQh5支付</option>
            <option value="qq102">QQ扫码转h5支付</option>
            <option value="qq403">QQ扫码支付</option>
            <option value="qq501">QQ条码-声波</option>
            <option value="qq502">QQ付款码包装成PC支付</option>
            <option value="qq503">QQ付款码包装成h5支付</option>
            <option value="jd000">京东组合支付类型</option>
            <option value="jd101">京东h5支付</option>
            <option value="jd102">京东扫码转h5支付</option>
            <option value="jd401">京东扫码支付</option>
            <option value="jd501">京东条码-声波</option>
            <option value="jd502">京东付款码包装成PC支付</option>
            <option value="jd503">京东付款码包装成h5支付</option>
            <option value="yl000">银联组合支付类型</option>
            <option value="yl101">银联h5支付</option>
            <option value="yl104">银联PC支付</option>
            <option value="yl401">银联二维码支付</option>
            <option value="yl402">银联扫码转h5</option>
            <option value="yl501">银联条码-声波</option>
            <option value="yl502">银联付款码包装成PC支付</option>
            <option value="yl503">银联付款码包装成h5支付</option>
            <option value="qj202">快捷支付(即银行卡支付)</option>
            <option value="qj301">网银/网银退款支付</option>
        </select>
        <br>
        商户订单号: <input type="text" value="${mchtOrderId}" name="orderId"/><br>
        订单时间: <input type="text" value="<%= new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" name="orderTime"/><br>
        金额: <input type="text" value="10" name="amount"/>【分】<br>
        商品名称: <input type="text" value="游戏充值" name="goods"/><br>
        notifyUrl: <input id="notifyUrl" type="text" value="http://${testUrl}/testNotify/" name="notifyUrl"/><br>
        callBackUrl: <input type="text" value="http://${testUrl}/gateway/cashier/testResult/${mchtOrderId}" name="callBackUrl"/><br>
        商品描述: <input type="text" value="超级大礼包" name="desc"/><br>
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
        openId: <input type="text" value="" id="openId" name="openId"/><br>
        IP: <input type="text" value="" id="ip" name="ip"/><br>
        签名Key: <input type="text" value="605091ae24f8404086b56d74a20c9812" id="mchtKey" name="mchtKey"/><br>
        sign: <input type="text" name="sign" id="sign"> <input type="button" value="签名" id="genSign"/><br>
        <input type="submit" value="提交"/>
    </form>
</body>
<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>

<script type="text/javascript">
    $(document).ready(function () {
        $("#genSign").click(function () {
            var mchtKey = $("#mchtKey").val();
            var notifyUrl = $("#notifyUrl").val();
            var param = $("#search").serialize();
            param= decodeURIComponent(param,true);
            param= encodeURI(encodeURI(param));
            if(param.indexOf("testNotify/&") != -1){
                param = param.replace("testNotify","testNotify/"+mchtKey);
                notifyUrl = notifyUrl.replace("testNotify","testNotify/"+mchtKey);
                $("#notifyUrl").val(notifyUrl);
            }
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
