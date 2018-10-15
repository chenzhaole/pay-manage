<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>API支付查询页面</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="css/index.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript">

        function doSubmit() {
            var orderId = $.trim($('input[name=orderId]').val());
            if (orderId == '') {
                alert('商户订单号不能为空');
                return false;
            }

            var mchtId = $.trim($('input[name=mchtId]').val());
            if (mchtId == '') {
                alert('商户号不能为空');
                return false;
            }

            var orderTime = $.trim($('input[name=orderTime]').val());
            if (orderTime == '') {
                alert('下单时间不能为空');
                return false;
            }

            $('form').submit();
        }
    </script>
</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0 topMargin=4>
<div id="main">
    <div class="cashier-nav">
        <ol>
            <li class="current">提交信息（通用API支付请求）</li>
        </ol>
    </div>
    <form action="testApiQuery" method="post" target="_blank">
        <div id="body" style="clear:left">
            <dl class="content">

                <%--*****************  head  ****************--%>
                <dt>api支付查单地址：</dt>
                <dd>
                    <span class="null-star"></span>
                    <select name="queryUrl">
                        <option value="http://127.0.0.1:12080/gateway/api/queryPay/">本地 http://127.0.0.1:12080/gateway/api/queryPay/</option>
                        <option value="http://114.115.206.62:12080/gateway/api/queryPay/">开发 http://114.115.206.62:12080/gateway/api/queryPay/</option>
                        <option value="http://114.115.206.62:12080/gateway/api/queryPay/">测试 http://114.115.206.62:12080/gateway/api/queryPay/</option>
                        <option value="http://106.2.6.41:12080/gateway/api/queryPay/">生产 http://106.2.6.41:12080/gateway/api/queryPay/</option>
                    </select>
                    <span class="null-star">(payUrl)*</span>
                    <span></span>
                </dd>

                <dt>商户编号：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="mchtId" value="" maxlength="32" size="32" placeholder="长度32"/>
                    <span class="null-star">(mchtId)*</span>
                    <span></span>
                </dd>
                <dt>版本号：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="version" value="20" maxlength="32" size="2" placeholder="长度32"/>
                    <span class="null-star">(version)*</span>
                    <span></span>
                </dd>

                <dt>支付类型：</dt>
                <dd>
                    <span class="null-star"></span>
                    <select name="biz">
                        <option value="wx000">微信</option>
                        <option value="wx101">微信H5</option>
                        <option value="wx401">微信扫码</option>
                        <option value="al000">支付宝</option>
                        <option value="al101">支付宝H5</option>
                        <option value="al401">支付宝扫码</option>
                        <option value="qq101">qqH5</option>
                        <option value="qq403">qq扫码</option>
                        <option value="yl000">银联</option>
                        <option value="yl401">银联二维码</option>
                        <option value="qj202">快捷支付</option>
                    </select>
                    <span class="null-star">(biz)*</span>
                    <span></span>
                </dd>

                <%--*****************  body  ****************--%>
                <dt>商户订单号：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="orderId" value="" maxlength="32" size="32" placeholder="长度32"/>
                    <span class="null-star">(orderId)*</span>
                    <span></span>
                </dd>

                <dt>平台订单号：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="tradeId" value="" maxlength="32" size="32" placeholder="长度32"/>
                    <span class="null-star">(tradeId)</span>
                    <span></span>
                </dd>

                <dt>订单时间：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="orderTime" value="" maxlength="128" size="30" placeholder="长度128"/>
                    <span class="null-star">(orderTime)*格式：yyyyMMddHHmmss 例如：20180822134034</span>
                    <span></span>
                </dd>

                <%--*****************  key  ****************--%>
                <dt>商户秘钥：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="key" value="" maxlength="32" size="32" placeholder=""/>
                    <span class="null-star">(key)*</span>
                    <span></span>
                </dd>

                </dd>
                <dd>
                        <span class="new-btn-login-sp">
                            <button class="new-btn-login" type="button" onclick="doSubmit()" style="text-align:center;">
                                确 认
                            </button>
                        </span>
                </dd>
            </dl>
        </div>
    </form>
</div>
</body>
</html>