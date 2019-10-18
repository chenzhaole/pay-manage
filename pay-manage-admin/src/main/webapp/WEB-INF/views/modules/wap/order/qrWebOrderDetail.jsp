<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <meta content="telephone=no" name="format-detection">
    <meta content="always" name="referrer">
    <title>订单详情</title>

    <link href="${ctxStatic}/modules/wap/order/css/order_detail.css" rel="stylesheet">


</head>


<body style="background:#f6f6f6">


<div id="app" class="h_100">
    <div class="phone-app">
        <div>

            <%--<div class="chooesCard-main"><!---->--%>
                <%--<div class="showCardOne-main">--%>
                    <%--<div class="swiper-container swiper-container-coverflow swiper-container-3d swiper-container-horizontal swiper-container-android">--%>
                        <%--<ul class="swiper-wrapper"--%>
                            <%--style="transition-duration: 0ms; transform: translate3d(272px, 0px, 0px); perspective-origin: 136px 50%;">--%>
                            <%--<li class="swiper-slide swiper-slide-active"--%>
                                <%--style="width: 272px; transition-duration: 0ms; transform: translate3d(0px, 0px, 0px) rotateX(0deg) rotateY(0deg); z-index: 1;">--%>
                                <%--<c:if test="${data.payType == 'wx'}">--%>
                                    <%--<img src="/static/images/wechat.png">--%>
                                <%--</c:if>--%>
                                <%--<c:if test="${data.payType == 'al'}">--%>
                                    <%--<img src="/static/images/alipay.png">--%>
                                <%--</c:if>--%>
                            <%--</li>--%>
                        <%--</ul>--%>
                    <%--</div>--%>
                <%--</div>--%>

                <div align="center" style="margin-top:15px;">
                    <c:if test="${data.payType == 'wx'}">
                        <img width="100" height="100" src="/static/images/wechat.png">
                    </c:if>
                    <c:if test="${data.payType == 'al'}">
                        <img width="100" height="100" src="/static/images/alipay.png">
                    </c:if>
                </div>

                <div class="cardFrom">
                    <div><!---->
                        <div class="weui-cells vux-no-group-title">
                            <div class="weui-cell braContext">
                                <div class="weui-cell__hd"></div>
                                <div class="vux-cell-bd">
                                    <p><label class="vux-label">
                                        <div class="titleText star">金额:</div>
                                    </label></p>
                                    <span class="vux-label-desc"></span></div>
                                <div class="weui-cell__ft vux-cell-primary vux-cell-align-right">
                                    <div class="soltText brandCla">
                                        <ul id="cardBrand" class="cardMsgUl">
                                            <li class="defaultText">¥ ${data.amount}</li>
                                        </ul>
                                    </div>
                                    <ul class="cardMsgUlAlert">
                                        <li><!----> <!----></li>
                                    </ul> <!----></div>
                            </div>

                            <!--   --------------------  -->
                            <div class="weui-cell">
                                <div class="weui-cell__hd"></div>
                                <div class="vux-cell-bd">
                                    <p>
                                        <label class="vux-label">
                                            <div class="titleText">确认码:</div>
                                        </label>

                                    </p>
                                    <span class="vux-label-desc"></span>
                                </div>
                                <div class="weui-cell__ft vux-cell-primary vux-cell-align-right">
                                    <div class="soltText">
                                        <ul class="cardMsgUl">
                                            <li class="defaultText">
                                                ${data.verifyCode}
                                            </li>
                                        </ul>
                                    </div> <!---->
                                </div>
                            </div> <!---->

                            <!--   --------------------  -->
                            <div class="weui-cell">
                                <div class="weui-cell__hd"></div>
                                <div class="vux-cell-bd">
                                    <p>
                                        <label class="vux-label">
                                            <div class="titleText">交易结果:</div>
                                        </label>

                                    </p>
                                    <span class="vux-label-desc"></span>
                                </div>
                                <div class="weui-cell__ft vux-cell-primary vux-cell-align-right">
                                    <div class="soltText">
                                        <ul class="cardMsgUl">
                                            <li class="defaultText">
                                                ${data.status}
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div> <!---->

                            <!--   --------------------  -->
                            <div class="weui-cell">
                                <div class="weui-cell__hd"></div>
                                <div class="vux-cell-bd">
                                    <p>
                                        <label class="vux-label">
                                            <div class="titleText">交易时间:</div>
                                        </label>

                                    </p>
                                    <span class="vux-label-desc"></span>
                                </div>
                                <div class="weui-cell__ft vux-cell-primary vux-cell-align-right">
                                    <div class="soltText">
                                        <ul class="cardMsgUl">
                                            <li class="defaultText">
                                                ${data.orderTime}
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div> <!---->

                            <!--   --------------------  -->
                            <div class="weui-cell">
                                <div class="weui-cell__hd"></div>

                                <div class="vux-cell-bd">
                                    <p>
                                        <label class="vux-label">
                                            <div class="titleText">订单流水号:</div>
                                        </label>

                                    </p>
                                    <span class="vux-label-desc"></span>
                                </div>

                                <div class="weui-cell__ft vux-cell-primary vux-cell-align-right">
                                    <div class="soltText">
                                        <ul class="cardMsgUl">
                                            <li class="defaultText">
                                                ${data.platOrderId}
                                            </li>
                                        </ul>
                                    </div>
                                </div>

                            </div> <!---->


                            <!--   --------------------  -->
                            <div class="weui-cell">
                                <div class="weui-cell__hd"></div>
                                <div class="vux-cell-bd">
                                    <p>
                                        <label class="vux-label">
                                            <div class="titleText">支付流水号:</div>
                                        </label>

                                    </p>
                                    <span class="vux-label-desc"></span>
                                </div>
                                <div class="weui-cell__ft vux-cell-primary vux-cell-align-right">
                                    <div class="soltText">
                                        <ul class="cardMsgUl">
                                            <li class="defaultText">
                                                ${data.officialOrderId}
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div> <!---->

                            <%--<!--   --------------------  -->--%>
                            <%--<div class="weui-cell">--%>
                                <%--<div class="weui-cell__hd"></div>--%>
                                <%--<div class="vux-cell-bd">--%>
                                    <%--<p>--%>
                                        <%--<label class="vux-label">--%>
                                            <%--<div class="titleText">门店:</div>--%>
                                        <%--</label>--%>

                                    <%--</p>--%>
                                    <%--<span class="vux-label-desc"></span>--%>
                                <%--</div>--%>
                                <%--<div class="weui-cell__ft vux-cell-primary vux-cell-align-right">--%>
                                    <%--<div class="soltText">--%>
                                        <%--<ul class="cardMsgUl">--%>
                                            <%--<li class="defaultText">--%>
                                                <%--积家美食广场总店--%>
                                            <%--</li>--%>
                                        <%--</ul>--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                            <%--</div> <!---->--%>



                        </div>


                    </div>
                </div>

</div>
<script type="text/javascript" src="./static/js/manifest.b862eca7bdd318afa481.js"></script>
<script type="text/javascript" src="./static/js/vendor.f9b23781b0648271abab.js"></script>
<script type="text/javascript" src="./static/js/app.49ebd21181e6d64aa591.js"></script>
<div class="vux-alert">
    <div class="vux-x-dialog">
        <div class="weui-mask" style="display: none;"></div>
        <div class="weui-dialog" style="display: none;">
            <div class="weui-dialog__hd"><strong class="weui-dialog__title"></strong></div>
            <div class="weui-dialog__bd">
                <div></div>
            </div>
            <div class="weui-dialog__ft"><a href="javascript:;" class="weui-dialog__btn weui-dialog__btn_primary">确定</a>
            </div>
        </div>
    </div>
</div>
<div class="vux-alert">
    <div class="vux-x-dialog">
        <div class="weui-mask" style="display: none;"></div>
        <div class="weui-dialog" style="display: none;">
            <div class="weui-dialog__hd"><strong class="weui-dialog__title"></strong></div>
            <div class="weui-dialog__bd">
                <div></div>
            </div>
            <div class="weui-dialog__ft"><a href="javascript:;" class="weui-dialog__btn weui-dialog__btn_primary">确定</a>
            </div>
        </div>
    </div>
</div>
<div id="userdata_el" style="visibility: hidden; position: absolute;"></div>
</body>


</html>