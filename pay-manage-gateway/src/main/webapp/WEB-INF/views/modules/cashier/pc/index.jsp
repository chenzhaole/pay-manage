<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>收银台pc</title>
    <link rel="stylesheet" href="./lib/layui/css/layui.css">
    <link rel="stylesheet" href="./lib/css/pc.css">
    <link rel="stylesheet" href="./lib/css/jquery.mCustomScrollbar.min.css" />

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
                    <%--<c:if test="${paymentType=='qj'}">
                        <li id="${paymentType}">
                            <div class="cardpay"><img class="timg" src="${ctxStatic}/images/bank.png" alt="" />银行卡支付<i class="selected"><img src="${ctxStatic}/images/gou.png" alt="" /></i></div>
                        </li>
                    </c:if>--%>
                    </c:forEach>
                </ul>

                <div class="layui-tab-content">
                    <c:forEach items="${paymentTypes}" var="paymentType">
                        <!--layui-show-->
                        <c:if test="${paymentType == 'wx'}">
                            <div class="layui-tab-item ">
                               <div class="wxpay-content">
                                  <div class="layui-row">
                                    <div class="layui-col-xs6 layui-col-md6 layui-col-sm6">
                                        <p>距离二维码过期还剩<span class="timeout">5</span>:<span class="timeout">31</span></p>
                                        <div class="qrcode">
                                            <div class="overdue" onclick="javascript:reGetQrCode('${paymentType}',this)">
                                                二维码已过期,请重新获取
                                            </div>

                                            <img src="${ctxStatic}/images/qr.png" alt="" class="photo" id="wxqrcode"/>
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
                                <div class="alipay-content">
                                    <p>距离二维码过期还剩<span class="timeout">5</span>:<span class="timeout">31</span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>
                                        <img src="${ctxStatic}/images/qr.png" alt="" class="photo" id="aliqrcode"/>
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
                                <div class="tenpay-content">
                                    <p>距离二维码过期还剩<span class="timeout">5</span>:<span class="timeout">31</span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>

                                        <img src="${ctxStatic}/images/qr.png" alt="" class="photo" id="qqWalletQrcode"/>
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
                                <div class="jdpay-content">
                                    <p>距离二维码过期还剩<span class="timeout">5</span>:<span class="timeout">31</span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>
                                        <img src="${ctxStatic}/images/qr.png" alt="" class="photo" id="jdqrcode"/>
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
                                <div class="snpay-content">
                                    <p>距离二维码过期还剩<span class="timeout">5</span>:<span class="timeout">31</span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>

                                        <img src="${ctxStatic}/images/qr.png" alt="" class="photo" id="snWalletQrcode"/>
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
                                <div class="unionpay-content">
                                    <p>距离二维码过期还剩<span class="timeout">5</span>:<span class="timeout">31</span></p>
                                    <div class="qrcode">
                                        <div class="overdue" onclick="javascript:reGetQrCode('${paymentType}',this)">
                                            二维码已过期,请重新获取
                                        </div>
                                        <img src="${ctxStatic}/images/qr.png" alt="" class="photo" id="unionQrCode"/>
                                        <div class="wx-tip clearfix">
                                            <img src="${ctxStatic}/images/scan.png" alt="" />
                                            <span>银联二维码进行支付，支付完成后，请点击查看支付结果	</span>
                                        </div>
                                        <div class="btn">查看支付结果</div>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${paymentType=='owner_cardpay'}">
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
                    <div class="layui-tab-item nouse" id="alipay">
                        <div class="warn-content" id="errorMsg">
                            <p>当前支付不可用，请尝试其他支付方式</p>
                        </div>
                    </div>

                    <!--提示-->
                    <div class="layui-tab-item nopay layui-show">
                        <div class="warn-content">
                            <img src="${ctxStatic}/images/pop3.png" alt="暂未支付" />
                            <p>该订单暂未支付，请继续支付</p>
                            <button class="layui-btn">重新支付</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
    <script src="${ctxStatic}/js/jquery.mCustomScrollbar.js"></script>
    <script src="${ctxStatic}/layui/layui.js"></script>
    <script src="${ctxStatic}/js/cashier.js?v=1.0"></script>
    <script>
        //获取价格
        var price = $(".totalprice").text();
        var sign = '${tradeCashierResponse.sign}';//签名
        var orderId = '${tradeCashierResponse.orderId}';//商户订单号

            /**
             * start 页面点击获取二维码
             */
            layui.config({});
            layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element'], function() {
                var laydate = layui.laydate, //日期
                    laypage = layui.laypage, //分页
                    layer = layui.layer //弹层
                    , element = layui.element; //元素操作

                //监听Tab切换
                var index =0,inter=[],ele=[0,0,0,0,0];
                element.on('tab(pay)', function(data) {
                    $("div[class='layui-tab-content']").show();
                    //this是li标签,即拿到li标签的id属性值
                    var tab = $(this).attr("id");
                    //银行卡的时候退出,不在获取二维码
                    if(tab.indexOf("qj") == 0){return;}
                    //点击支付宝，微信图标时，发送请求操作,调用下边getQrCode函数异步获取二维码
                    getQrCode(tab);
                });
            });
            /**
             * end 页面点击获取二维码
             */




        //选择银行卡
        $(document).on("click", ".list-container li", function(e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).addClass("active").siblings().removeClass("active");
        })

        //解绑按钮
        function unbind(event, el) {
            var id = $(el).parent().attr("cid");
            layer.open({
                type: 0,
                title: '解绑',
                area: ['390px', '200px'],
                shade: 0,
    //					maxmin: true,
                offset: [ //为了演示，随机坐标
                    ($(window).height() / 2 - 300 / 2), ($(window).width() / 2 - 390 / 2)
                ],
                content: '<img src="${ctxStatic}/images/popb.png" style="display:inline-block;height:36px;width:36px;margin-right:8px;"><span style="color:#666;font-size:14px;">你确定要解绑该快捷卡吗？</span>',
                btn: ['暂不解绑', '解绑']
                ,
                yes: function() {
                    layer.closeAll();
                },
                btn2: function() {

                    //解绑成功
                    $(el).parent().parent().remove();
                    alert("解绑" + id + "成功");

                    $(".nobind").fadeIn();
                    setTimeout(function() {
                        $(".nobind").fadeOut();
                    }, 1000)

                    layer.closeAll();
                },
                zIndex: layer.zIndex //重点1
                ,
                success: function(layero) {
                    layer.setTop(layero); //重点2
                }
            });
        }

        //更多银行卡
        $(".morecard").on("click", function() {
            $(".list-container").append('<li class="clearfix"><div class="flag"><div class="dotbox"><div></div></div></div><div class="cardinfo" cid="0000"><img src="${ctxStatic}/images/logo/北京银行.svg" alt="北京银行" /><span> <b>北京银行</b> 尾号4994  储蓄卡 | 快捷支付</span><span class="unbind" onclick="unbind(event,this)">解绑</span></div></li>');
        })

        //新增卡支付
        $(".newcard").on("click", function() {
            layer.open({
                type: 2,
                area: ['800px', '600px'],
                fixed: false, //不固定
                //		maxmin: true,
                title: '',
                content: ['./addcard.html', 'no'],
                success: function(layero, index) {
                    var body = layer.getChildFrame('body', index);
                    body.find("#price").val(price);
                }
            });
        })

        //快捷支付
        $(".speedpay").on("click", function() {
            layer.open({
                type: 2,
                area: ['800px', '600px'],
                fixed: false, //不固定
                //		maxmin: true,
                title: '',
                content: ['./speedpay.html', 'no'],
                success: function(layero, index) {
                    var body = layer.getChildFrame('body', index),
                        item = body.find(".layui-tab-title li");
                    item.on("click", function(e) {
                        $(this).addClass("layui-this").siblings().removeClass("layui-this");


                        //弹窗-start
                        layer.open({
                            type: 0,
                            title: '请在完成支付后选择',
                            area: ['390px', '200px'],
                            shade: 0,
                            id:"speedpop",
                            content: '<img src="${ctxStatic}/images/popb.png"  class="speedImg" style="height:36px;width:36px;margin-right:8px;"><div style="display:inline-block;vertical-align:middle;" class="speedtxt"><p>支付成功：<span class="weightFont">去支付成功页面</span></p><p>支付失败：<span  class="weightFont">重新选择其他支付方式</span></p></div>',
                            btn: ['选择其他支付方式', '重新支付'],
                            btnAlign:"c",
                            yes: function() {
                                layer.closeAll();
                            },
                            btn2: function() {

                                //重新支付


                                layer.closeAll();
                            },
                            zIndex: layer.zIndex //重点1
                            ,
                            success: function(layero,index) {
                                layer.setTop(layero);
                                var body = layer.getChildFrame('body', index),
                                    item = body.find(".layui-layer-title");
                                item.css({"background":"#FFF",})

                            }
                        });
                        //弹窗-end

                        window.open($(this).attr("href"));
                    })
                },
            })
        })

        //银行卡立即支付
        $(".paybtn").on("click", function() {
            var cid = $(".list-container .active .cardinfo").attr("cid");
            layer.open({
                type: 2,
                title: false,
                closeBtn: 1, //不显示关闭按钮
                shade: [0],
                area: ['600px', '260px'],
                //offset: '', //右下角弹出
                //time: 2000, //2秒后自动关闭
                anim: 2,
                title: '',
                //maxmin:true,
                content: ['pay.html', 'no'], //iframe的url，no代表不显示滚动条
                success: function(layero, index) {
                    var body = layer.getChildFrame('body', index);
                    //							    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
                    body.find("#cid").val(cid);
                    body.find(".layui-btn").on("click", function() {
                        var code = body.find('input').val();
                        if(code !== '') {
                            layer.closeAll();

                            cardpay(price, cid, code);
                        }
                    })
                },
                end: function() { //此处用于演示

                }
            });
        });

        //tips("nobind");
        function tips(ele) {
            $(ele).addClass("layui-show").siblings().removeClass("layui-show");
        }
        //银行卡立即支付
        function cardpay(price, cid, code) {
            //参数分别是 价格    已绑定卡ID  验证码
            console.log(price, cid, code);
            alert("完成已有卡支付过程");
        }


        //点击查看支付结果
   			$(".qrcode .btn").on("click", function() {
   				layer.open({
   					type: 1,
   					title: false,
   					closeBtn: 0, //不显示关闭按钮
   					shade: [0],
   					area: ['600px', '260px'],
   					//							  anim: 2,
   					title: '',
   					//maxmin:true,
   					content: '<div class="layui-tab-item nopay layui-show"><div class="warn-content"><div class="warntip"><img src="${ctxStatic}/images/warn.png" alt="已支付" />温馨提示</div><p>当前订单已支付，请勿重复支付。</p><button class="layui-btn close">返回</button></div>', //iframe的url，no代表不显示滚动条
   					success: function(layero, index) {
   						$(".close").on("click", function() {
   							layer.closeAll();
   						})
   					},
   					end: function() {

   					}
   				})
   			})

             (function($){
                 $(".cardpay-content").mCustomScrollbar();
             })(jQuery);
</script>
</body>

</html>

