<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0" />
    <meta name="format-detection" content="telephone=no,email=no,date=no,address=no">
    <title>支付中心</title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/aui.css" />
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/app.css" />
</head>
<body id="payCenter">

<header class="aui-bar aui-bar-nav">
    <a class="aui-pull-left aui-btn" onclick="history.back()">
        <img src="${ctxStatic}/images/back.png"/>
    </a>
    <div class="aui-title">支付中心</div>
</header>
<div class="bgF orderInfo">
    <span class="label">商品名称：</span><span class="goodsName">${tradeCashierResponse.goods}</span> <span class="total">￥${tradeCashierResponse.amount}</span>
</div>
<div class="aui-content aui-margin-b-15 bgF">
    <ul class="aui-list aui-list-in">
        <li class="aui-list-header">请选择支付方式</li>
        <c:if test="${tradeCashierResponse.supportAliPay=='1'}">
            <a href="${ctx}/gateway/cashier/pay/alipay/${tradeCashierResponse.sign}">
                <li class="aui-list-item">
                    <div class="aui-list-item-label-icon">
                        <img src="${ctxStatic}/images/alipay.png" alt="支付宝支付" />
                    </div>
                    <div class="aui-list-item-inner aui-list-item-arrow">
                        <p class="wayName">支付宝</p>
                        <p class="wayIntro">推荐拥有支付宝账户的用户使用</p>
                    </div>
                </li>
            </a>
        </c:if>

        <c:if test="${tradeCashierResponse.supportWeiXinPay=='1'}">
            <a href="${ctx}/gateway/cashier/pay/weixinpay/${tradeCashierResponse.sign}">
                <li class="aui-list-item">
                    <div class="aui-list-item-label-icon">
                        <img src="${ctxStatic}/images/wechat.png" alt="微信支付" />
                    </div>
                    <div class="aui-list-item-inner aui-list-item-arrow">
                        <p class="wayName">微信支付</p>
                        <p class="wayIntro">推荐安装微信5.0以上版本的用户使用</p>
                    </div>
                </li>
            </a>
        </c:if>

        <a href="./fastpay.html">
            <li class="aui-list-item">
                <div class="aui-list-item-label-icon">
                    <img src="${ctxStatic}/images/bank.png" alt="快捷支付" />
                </div>
                <div class="aui-list-item-inner aui-list-item-arrow" >
                    <p class="wayName">快捷支付</p>
                    <p class="wayIntro">直接使用银行卡进行支付</p>
                </div>
            </li>
        </a>
    </ul>
</div>


<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
</body>
</html>

