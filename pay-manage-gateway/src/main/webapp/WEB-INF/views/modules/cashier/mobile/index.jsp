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
    <span class="label">商品名称：</span><span class="goodsName">${goods}</span> <span class="total">￥${amount}</span>
</div>
<div class="aui-content aui-margin-b-15 bgF">
    <ul class="aui-list aui-list-in">
        <li class="aui-list-header">请选择支付方式</li>

        <c:forEach items="${paymentTypes}" var="paymentType">
            <c:if test="${paymentType=='al'}">
                <a href="${ctx}/gateway/cashier/platMobileCall/${mchtId}/${mchtOrderId}/${paymentType}/${extraData}">
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

            <c:if test="${paymentType=='wx'}">
                <a href="${ctx}/gateway/cashier/platMobileCall/${mchtId}/${mchtOrderId}/${paymentType}/${extraData}">
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

            <c:if test="${paymentType=='qq'}">
                <a href="${ctx}/gateway/cashier/platMobileCall/${mchtId}/${mchtOrderId}/${paymentType}/${extraData}">
                    <li class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <img src="${ctxStatic}/images/qq.png" alt="QQ钱包支付" />
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <p class="wayName">QQ钱包支付</p>
                            <p class="wayIntro">推荐拥有QQ钱包的用户使用</p>
                        </div>
                    </li>
                </a>
            </c:if>

            <c:if test="${paymentType=='jd'}">
                <a href="${ctx}/gateway/cashier/platMobileCall/${mchtId}/${mchtOrderId}/${paymentType}/${extraData}">
                    <li class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <img src="${ctxStatic}/images/jd.png" alt="京东支付" />
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <p class="wayName">京东支付</p>
                            <p class="wayIntro">请使用京东客户端支付</p>
                        </div>
                    </li>
                </a>
            </c:if>

            <c:if test="${paymentType=='sn'}">
                <a href="${ctx}/gateway/cashier/platMobileCall/${mchtId}/${mchtOrderId}/${paymentType}/${extraData}">
                    <li class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <img src="${ctxStatic}/images/sn.png" alt="苏宁支付" />
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <p class="wayName">苏宁支付</p>
                            <p class="wayIntro">请使用苏宁金融客户端支付</p>
                        </div>
                    </li>
                </a>
            </c:if>

            <c:if test="${paymentType=='yl'}">
                <a href="${ctx}/gateway/cashier/platMobileCall/${mchtId}/${mchtOrderId}/${paymentType}/${extraData}">
                    <li class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <img src="${ctxStatic}/images/union.png" alt="银联二维码支付" />
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <p class="wayName">银联二维码支付</p>
                            <p class="wayIntro">请使用支持银联二维码功能的客户端支付</p>
                        </div>
                    </li>
                </a>
            </c:if>

            <c:if test="${paymentType=='qj'}">
                <a href="${ctx}/gateway/cashier/platMobileCall/${mchtId}/${mchtOrderId}/${paymentType}/${extraData}">
                    <li class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <img src="${ctxStatic}/images/bank.png" alt="银行卡支付" />
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <p class="wayName">银行卡支付</p>
                            <p class="wayIntro">直接使用银行卡进行支付</p>
                        </div>
                    </li>
                </a>
            </c:if>


        </c:forEach>
    </ul>
</div>


<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
</body>
</html>

