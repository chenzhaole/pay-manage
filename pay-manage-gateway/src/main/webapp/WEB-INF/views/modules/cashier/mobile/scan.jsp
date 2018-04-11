<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0" />
    <meta name="format-detection" content="telephone=no,email=no,date=no,address=no">
    <title>扫码支付</title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/aui.css" />
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/app.css?v=1.0" />
</head>

<body id="qrcode" class="default_pop dialog">
<header class="aui-bar aui-bar-nav">
    <div class="aui-title">支付中心</div>
</header>

<div class="aui-content aui-padded-15 ">
    <div class="qr_img_box">

        <p class="qr_box">
            <img class="qr_img" src="${payInfo}" alt="" id="code"/>
        </p>

        <p class="pay_tip1">步骤1&nbsp;:&nbsp;点击截屏或者长按二维码,保存至相册</p>

        <c:choose>
            <c:when test="${paymentType eq 'yl'}">
                <p class="pay_tip2">步骤2&nbsp;:&nbsp;前往支持银联二维码的客户端，点击扫一扫选择二维码进行支付
                    （<a href="javascript:void(0);" onclick="location.href='https://wallet.95516.com/s/wl/web/activity/qrcode/html/app.html'"><span class="main_font_color">查看支持的客户端</span></a>）
                </p>
            </c:when>
            <c:otherwise>
                <p class="pay_tip2">步骤2&nbsp;:&nbsp;前往
                    <span>
                      <c:if test="${paymentType eq 'wx'}">微信</c:if>
                      <c:if test="${paymentType eq 'ali'}">支付宝</c:if>
                      <c:if test="${paymentType eq 'qq'}">QQ</c:if>
                    </span>"扫一扫",在相册中选择二维码进行扫描
                </p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<p class="guide_tip main_font_color">
    提示:&nbsp;支付成功后回到本页面,点击下方按钮返回
</p>
<div class="aui-content aui-margin-b-15">
    <input style="display: none" id="selectStatus" value=""/>
    <div class="aui-btn aui-btn-define1" onclick="goback(this)">已完成支付,点我返回</div>
</div>

<div class="tipbox">
    <c:if test="${ !empty mobile || !empty qq}">
        <p>如遇支付问题请联系客服</p>
    </c:if>
    <c:if test="${ !empty mobile}">
        <p><span class="tag">客服热线 </span><span> ${mobile}</span></p>
    </c:if>
    <c:if test="${ !empty qq}">
        <p><span class="tag">客服QQ </span><span> ${qq}</span></p>
    </c:if>
</div>
<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
<script src="${ctxStatic}/js/app.js?v=1.0"></script>
<script src="${ctxStatic}/js/cashier.js?v=1.0"></script>
<script src="${ctxStatic}/js/rotationOrder.js?version=2.1"></script>
<script type="text/javascript">
    //拼接页面回调地址
    var callbackUrl = "/gateway/cashier/chanCallBack/${platOrderId}/${payType}";
    //轮训查单需要的参数
    var queryInfo = "platOrderId="+ "${platOrderId}";

    $(function(){
        //3秒之后执行查单处理
        setTimeout("toOrderQuery('"+queryInfo+"')",3000);
    });

    var dialog = new auiDialog({});
    function goback(el){

        //首先查缓存，看订单是否已经成功
        orderStatusQuery(queryInfo);
        var status = $("#selectStatus").val();
        //2 支付成功, -1 未知失败, 4001 支付失败 ,4002 提交支付失败
        if(2 != status && -1 != status && 4001 != status && 4002 != status ) {
            dialog.alert({
                title: '温馨提示',
                msg: '客官,您尚未完成支付',
                buttons: ['残忍拒绝', '返回支付'],
            }, function (ret) {
                if (ret.buttonIndex == 2) {
                    //返回支付，啥都不做

                } else {
                    //拒绝操作
                    location.href = callbackUrl;
                }
            });
        }
    }

</script>

</body>

</html>
