<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>代付查询页面</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="css/index.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript">
        $(function () {
            $('input[name=orderId]').val("DEMO-" + new Date().getTime());
            $('.hideClass').hide();

            $('input[name=time_start]').val(getCurrentDate());
        });
        function getCurrentDate() {
            var date = new Date();
            return date.getFullYear() + '' + formatString(date.getMonth() + 1) + formatString(date.getDay()) + formatString(date.getHours()) + formatString(date.getMinutes()) + formatString(date.getSeconds());
        }
        function formatString(value) {
            if (parseInt(value) < 10) {
                return 0 + '' + value;
            }
            return value;
        }
        //验证ip
        function isIP(ip) {
            var reSpaceCheck = /^(\d+)\.(\d+)\.(\d+)\.(\d+)$/;
            if (reSpaceCheck.test(ip)) {
                ip.match(reSpaceCheck);
                if (RegExp.$1 <= 255 && RegExp.$1 >= 0
                        && RegExp.$2 <= 255 && RegExp.$2 >= 0
                        && RegExp.$3 <= 255 && RegExp.$3 >= 0
                        && RegExp.$4 <= 255 && RegExp.$4 >= 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        function doSubmit() {
            var batchOrderNo = $.trim($('input[name=batchOrderNo]').val());
            if (batchOrderNo == '') {
                alert('商户代付批次号不能为空');
                return false;
            }
            var tradeId = $.trim($('input[name=tradeId]').val());
            if (tradeId == '') {
                alert('平台代付批次号不能为空');
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
            <li class="current">提交信息（代付查单）</li>
        </ol>
    </div>
    <form action="testDFQuery" method="post" target="_blank">
        <div id="body" style="clear:left">
            <dl class="content">

                <dt>代付查单地址：</dt>
                <dd>
                    <span class="null-star"></span>
                    <%--<input name="payUrl" value="http://localhost:12080/df/gateway/query/" maxlength="128" size="40"  placeholder="长度128"/>--%>
                    <select name="payUrl">
                        <option value="http://127.0.0.1:12080/df/gateway/query/">本地
                            http://127.0.0.1:12080/df/gateway/query/
                        </option>
                        <option value="http://114.115.206.62:12080/df/gateway/query/">开发
                            http://114.115.206.62:12080/df/gateway/query/
                        </option>
                        <option value="http://114.115.206.62:12080/df/gateway/query/">测试
                            http://114.115.206.62:12080/df/gateway/query/
                        </option>
                        <option value="http://106.2.6.41:12080/df/gateway/query/">生产
                            http://106.2.6.41:12080/df/gateway/query/
                        </option>
                    </select>
                    <span class="null-star">(payUrl)*</span>
                    <span></span>
                </dd>

                <dt>商户编号：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="mchtId" value="" maxlength="64" size="40" placeholder="长度32"/>
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
                        <option value="df102">代付</option>
                    </select>
                    <span class="null-star">(biz)*</span>
                    <span></span>
                </dd>


                <dt>商户代付批次号：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="batchOrderNo" value="" maxlength="32" size="30" placeholder="长度32"/>
                    <span class="null-star">(batchOrderNo)*</span>
                    <span></span>
                </dd>
                <dt>平台代付批次号：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="tradeId" value="" maxlength="32" size="30" placeholder="长度32"/>
                    <span class="null-star">(tradeId)*</span>
                    <span></span>
                </dd>

                <dt>商户KEY：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="key" value="" maxlength="32" size="40" placeholder=""/>
                    <span class="null-star">(key)*</span>
                    <span></span>
                </dd>

                <dt>商户私钥：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="mchtPrivateKey"
                           value=""
                           size="40" placeholder=""/>
                    <span class="null-star">(mchtPrivateKey)*</span>
                    <span></span>
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