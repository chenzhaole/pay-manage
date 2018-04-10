<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>收银台pc</title>
    <link rel="stylesheet" href="${ctxStatic}/layui/css/layui.css">
    <link rel="stylesheet" href="${ctxStatic}/css/pc.css?v=1.0">
    <link rel="stylesheet" href="${ctxStatic}/css/jquery.mCustomScrollbar.min.css" />

</head>
<body>
    <div class="content">
        <div class="container">
            <div class="order">
                <span class="orderTip">请及时付款，以便订单尽快处理！</span>
                <span class="orderNum">订单编号：${mchtOrderId}</span>
            </div>
            <div class="goodsInfo">
                <span class="goodsName">商品名称：<b>${goods}</b></span>
                <span class="price">应付金额：<b>￥</b><b class="totalprice">${amount}</b></span>
            </div>
        </div>
    </div>

    <!--平台订单号-->
    <input id="platOrderId" type="hidden" value="${platOrderId}">
    <!--订单状态-->
    <input id="status" type="hidden" value="${status}">
    <!--支付类型-->
    <input id="payType" type="hidden" value="${payType}">
    <!--是否展示收银台-->
    <input id="pcIscashier" type="hidden" value="${pcIscashier}">
    <!--二维码倒计时-->
    <input id="countdownTime" type="hidden" value="${countdownTime}">
    <!--商户id-->
    <input id="mchtId" type="hidden" value="${mchtId}">
    <!--商户订单号-->
    <input id="mchtOrderId" type="hidden" value="${mchtOrderId}">
    <!--随机数-->
    <input id="extraData" type="hidden" value="${extraData}">
    <!--ctxStatic-->
    <input id="ctxStatic" type="hidden" value="${ctxStatic}">

    <div class="divide"></div>
    <div class="content">
        <div class="container">

            <div class="layui-tab layui-tab-brief" lay-filter="pay">
                <p class="paytitle">请选择支付方式</p>
                <ul class="layui-tab-title">
                    <c:forEach items="${paymentTypes}" var="paymentType">
                        <c:if test="${paymentType == 'wx'}">
                            <li id="${paymentType}">
                                <div class="wxpay"><img class="timg" src="${ctxStatic}/images/wechat.png" alt="" />微信支付 <i class="selected"><img src="${ctxStatic}/images/gou.png" alt="" /></i></div>
                             </li>
                        </c:if>
                        <c:if test="${paymentType=='al'}">
                            <li id="${paymentType}">
                                <div class="alipay"><img class="timg" src="${ctxStatic}/images/alipay.png" alt="" />支付宝支付<i class="selected"><img src="${ctxStatic}/images/gou.png" alt="" /></i></div>
                            </li>
                        </c:if>
                        <c:if test="${paymentType=='qq'}">
                            <li id="${paymentType}">
                                <div class="qqpay"><img class="timg" src="${ctxStatic}/images/qq.png" alt="" />QQ钱包<i class="selected"><img src="${ctxStatic}/images/gou.png" alt="" /></i></div>
                            </li>
                        </c:if>
                        <c:if test="${paymentType=='jd'}">
                            <li id="${paymentType}">
                                <div class="jdpay"><img class="timg" src="${ctxStatic}/images/bank.png" alt="" />京东支付<i class="selected"><img src="${ctxStatic}/images/gou.png" alt="" /></i></div>
                            </li>
                        </c:if>
                        <c:if test="${paymentType=='sn'}">
                            <li id="${paymentType}">
                                <div class="snpay"><img class="timg" src="${ctxStatic}/images/qq.png" alt="" />苏宁扫码<i class="selected"><img src="${ctxStatic}/images/gou.png" alt="" /></i></div>
                            </li>
                        </c:if>
                        <c:if test="${paymentType=='yl'}">
                            <li id="${paymentType}">
                                <div class="unionpay"><img class="timg" src="${ctxStatic}/images/union.png" alt="" />银联二维码<i class="selected"><img src="${ctxStatic}/images/gou.png" alt="" /></i></div>
                            </li>
                        </c:if>
                    <c:if test="${paymentType=='qj'}">
                        <li id="${paymentType}">
                            <div class="cardpay"><img class="timg" src="${ctxStatic}/images/bank.png" alt="" />银行卡支付<i class="selected"><img src="${ctxStatic}/images/gou.png" alt="" /></i></div>
                        </li>
                    </c:if>
                    </c:forEach>
                </ul>

                <div class="layui-tab-content">
                    <c:forEach items="${paymentTypes}" var="paymentType">
                        <!--layui-show-->
                        <c:if test="${paymentType == 'wx'}">
                            <div class="layui-tab-item ">
                               <div class="wx-content">
                                  <div class="layui-row">
                                    <div class="layui-col-xs6 layui-col-md6 layui-col-sm6">
                                        <p>距离二维码过期还剩<span class="timeout" id="wxTimeout"></span></p>
                                        <div class="qrcode">
                                            <div class="overdue" onclick="javascript:reGetAsynQrCode('${paymentType}',this)">
                                                二维码已过期,请重新获取
                                            </div>
                                            <img src="${payInfo}" alt="" class="photo" id="wxqrcode"/>
                                            <div class="wx-tip clearfix">
                                                <img src="${ctxStatic}/images/scan.png" alt="" />
                                                <span>微信扫一扫进行支付，支付完成后，请点击查看支付结果	</span>
                                            </div>
                                            <div class="btn">查看支付结果</div>
                                        </div>
                                    </div>
                                    <div class="layui-col-xs6 layui-col-md6 layui-col-sm6">
                                        <div class="wxtip">
                                            <img src="${ctxStatic}/images/iphone.png" alt="" />
                                        </div>
                                    </div>
                                  </div>
                               </div>
                            </div>
                        </c:if>

                        <c:if test="${paymentType=='al'}">
                            <div class="layui-tab-item">
                                <div class="al-content">
                                    <p>距离二维码过期还剩<span class="timeout" id="alTimeout"></span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetAsynQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>
                                        <img src="${payInfo}" alt="" class="photo" id="aliqrcode"/>
                                        <div class="wx-tip clearfix">
                                            <img src="${ctxStatic}/images/scan.png" alt="" />
                                            <span>支付宝扫一扫进行支付，支付完成后，请点击查看支付结果</span>
                                        </div>
                                        <div class="btn">查看支付结果</div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${paymentType=='qq'}">
                            <div class="layui-tab-item">
                                <div class="qq-content">
                                    <p>距离二维码过期还剩<span class="timeout" id="qqTimeout"></span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetAsynQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>

                                        <img src="${payInfo}" alt="" class="photo" id="qqWalletQrcode"/>
                                        <div class="wx-tip clearfix">
                                            <img src="${ctxStatic}/images/scan.png" alt="" />
                                            <span>手机QQ扫一扫进行支付，支付完成后，请点击查看支付结果	</span>
                                        </div>
                                        <div class="btn">查看支付结果</div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${paymentType=='jd'}">
                            <div class="layui-tab-item">
                                <div class="jd-content">
                                    <p>距离二维码过期还剩<span class="timeout" id="jdTimeout"></span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetAsynQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>
                                        <img src="${payInfo}" alt="" class="photo" id="jdqrcode"/>
                                        <div class="wx-tip clearfix">
                                            <img src="${ctxStatic}/images/scan.png" alt="" />
                                            <span>京东扫一扫进行支付，支付完成后，请点击查看支付结果</span>
                                        </div>
                                        <div class="btn">查看支付结果</div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${paymentType=='sn'}">
                            <div class="layui-tab-item">
                                <div class="sn-content">
                                    <p>距离二维码过期还剩<span class="timeout" id="snTimeout"></span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetAsynQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>

                                        <img src="${payInfo}" alt="" class="photo" id="snWalletQrcode"/>
                                        <div class="wx-tip clearfix">
                                            <img src="${ctxStatic}/images/scan.png" alt="" />
                                            <span>苏宁扫一扫进行支付，支付完成后，请点击查看支付结果	</span>
                                        </div>
                                        <div class="btn">查看支付结果</div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${paymentType=='yl'}">
                            <div class="layui-tab-item">
                                <div class="yl-content">
                                    <p>距离二维码过期还剩<span class="timeout" id="ylTimeout"></span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetAsynQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>
                                        <img src="${payInfo}" alt="" class="photo" id="unionQrCode"/>
                                        <div class="wx-tip clearfix">
                                            <img src="${ctxStatic}/images/scan.png" alt="" />
                                            <span>银联二维码进行支付，支付完成后，请点击查看支付结果	</span>
                                        </div>
                                        <div class="btn">查看支付结果</div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${paymentType=='qj'}">
                            <div class="layui-tab-item card">
                                <div class="cardpay-content">
                                    <!--银行卡部分-->
                                    <div class="cardlist">
                                        <ul class="list-container">

                                            <li class="clearfix  active">
                                                <div class="flag">
                                                    <div class="dotbox">
                                                        <div></div>
                                                    </div>
                                                </div>
                                                <div class="cardinfo" cid="0000">
                                                    <img src="${ctxStatic}/images/logo/北京银行.svg" alt="北京银行" />
                                                    <span> <b>北京银行</b> 尾号4994  储蓄卡 | 快捷支付</span>
                                                    <span class="unbind" onclick="unbind(event,this)">解绑</span>
                                                </div>
                                            </li>

                                            <li class="clearfix">
                                                <div class="flag">
                                                    <div class="dotbox">
                                                        <div></div>
                                                    </div>
                                                </div>
                                                <div class="cardinfo" cid="0001">
                                                    <img src="${ctxStatic}/images/logo/工商银行.svg" alt="工商银行" />
                                                    <span> <b>工商银行</b> 尾号4994  储蓄卡 | 快捷支付</span>
                                                    <span class="unbind" onclick="unbind(event,this)">解绑</span>
                                                </div>
                                            </li>

                                        </ul>

                                        <div class="morebtn">
                                            <button class="layui-btn morecard">更多银行卡</button>
                                            <button class="layui-btn newcard">添加新卡</button>
                                            <button class="layui-btn speedpay">网银支付</button>
                                        </div>

                                        <button class="layui-btn paybtn">立即支付</button>

                                        <p class="nobind">解绑成功！</p>

                                    </div>

                                </div>
                            </div>
                        </c:if>

                        <c:if test="${paymentType=='other_cardpay'}">
                            <div style="display: none" id="other_cardpay">

                            </div>
                        </c:if>



                    </c:forEach>
                    <!--提示-->
                    <div class="layui-tab-item nopay">
                        <div class="warn-content">
                            <img src="${ctxStatic}/images/pop3.png" alt="暂未支付" />
                            <p>请选择支付方式，发起支付</p>
                            <%--<button class="layui-btn">重新支付</button>--%>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
    <script src="${ctxStatic}/js/jquery.mCustomScrollbar.js"></script>
    <script src="${ctxStatic}/layui/layui.js"></script>
    <script src="${ctxStatic}/js/cashier.js?v=5.6"></script>
    <script src="${ctxStatic}/js/cardPay.js"></script>
    <script>
        /**
         * 前端框架使用的，不要误删
         */
         (function($){
             $(".cardpay-content").mCustomScrollbar();
         })(jQuery);
</script>
</body>

</html>

