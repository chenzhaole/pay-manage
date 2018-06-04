<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>代付请求页面</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="css/index.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript">
        $(function () {
            $('input[name=orderId]').val("TEST-" + new Date().getTime());
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
            var totalNum = $.trim($('input[name=totalNum]').val());
            if (totalNum == '') {
                alert('商品代付笔数');
                return false;
            }
            var totalAmount = $.trim($('input[name=totalAmount]').val());
            if (totalAmount == '') {
                alert('代付总金额不能为空');
                return false;
            }
            var detail = $.trim($('#detail').val());
            if (detail == '') {
                alert('代付订单明细不能为空');
                return false;
            }
            /*var ip = $.trim($('input[name=ip]').val());
             if(!isIP(ip)){
             alert("ip格式不正确");
             return false;
             }*/
            $('form').submit();
        }
    </script>
</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0 topMargin=4>
<div id="main">
    <div class="cashier-nav">
        <ol>
            <li class="current">提交信息（代付请求）</li>
        </ol>
    </div>
    <form action="testDFReq" method="post" target="_blank">
        <div id="body" style="clear:left">
            <dl class="content">

                <dt>代付地址：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="payUrl" value="http://api.ihengyuan.cn:17082/df/gateway/req/" maxlength="128" size="40"  placeholder="长度128"/>
                    <span class="null-star">(payUrl)*</span>
                    <span></span>
                </dd>

                <dt>商户编号：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="mchtId" value="18d22c3e" maxlength="32" size="16" placeholder="长度32"/>
                    <span class="null-star">(mchtId)*</span>
                    <span></span>
                </dd>
                <dt>版本号：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="version" value="21" maxlength="32" size="2" placeholder="长度32"/>
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
                    <input name="batchOrderNo" value="<%=System.currentTimeMillis()%>" maxlength="32" size="30"
                           placeholder="长度32"/>
                    <span class="null-star">(batchOrderNo)*</span>
                    <span></span>
                </dd>

                <dt>商户代付笔数：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="totalNum" value="2" maxlength="127" size="30" placeholder="长度127"/>
                    <span class="null-star">(totalNum)*</span>
                    <span></span>
                </dd>

                <dt>商户代付总金额：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="totalAmount" value="8000" size="10" placeholder="单位：分"/> 分
                    <span class="null-star">(totalAmount)*</span>
                    <span></span>
                </dd>

                <dt>异步通知地址：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="notifyUrl" value="127.0.0.1:12080/testNotify/4da5ebede6574a699cf307dd7c746ada"
                           maxlength="128" size="30" placeholder="长度128"/>
                    <span class="null-star">(notifyUrl)</span>
                    <span></span>
                </dd>

                <dt>代付订单明细：</dt>
                <dd>
                    <span class="null-star"></span>
                    <textarea rows="10" cols="40" name="detail" id="detail">[{"accType":"0","amount":"3500","bankLineCode":"102","bankCardName":"张三","bankCardNo":"6222030200005664577","bankName":"中国工商银行","seq":"001"},{"accType":"0","amount":"4500","bankLineCode":"102","bankCardName":"李四","bankCardNo":"630121199210021588","bankName":"中国农业银行","seq":"002"}]
                    </textarea>
                    <span class="null-star">(detail)*</span>
                    <span></span>
                </dd>

                <%-- <dt>订单时间：</dt>
                 <dd>
                     <span class="null-star"></span>
                     <input name="orderTime" value="20171209230101" maxlength="128" size="30"  placeholder="长度128"/>
                     <span class="null-star">(orderTime)*</span>
                     <span></span>
                 </dd>

                 <dt>操作员编号：</dt>
                 <dd>
                     <span class="null-star"></span>
                     <input name="operator" value="" maxlength="128" size="30"  placeholder="可空"/>
                     <span>(operator)</span>
                     <span></span>
                 </dd>
 --%>
                <%--<dt>终端IP：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="ip" value="127.0.0.1" maxlength="16"  placeholder="可空"/>
                    <span>(ip)</span>
                    <span></span>
                </dd>--%>
                <dt>商户KEY：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="key" value="af3c4b3c51d548b6bdac90d563bc7b46" maxlength="32" size="40" placeholder=""/>
                    <span class="null-star">(key)*</span>
                    <span></span>
                </dd>
                <dt>商户私钥：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="privateKey" value="MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBALlYHkGA9h9/npZ2mhZEJOflEmMMKN9X5o6cdzti4UhNOZfzNS4fETTDExrd0nrJxzWOwL2PoH807N6uXprgAxLIDA9yuU6FBuq3H81d/4oS9IQzVSZsw0ZtCy1jrDnfIz3h7PXVTPkS1kQi1NZVYRRR17hebsIt+kXVdMd0tIdxAgMBAAECgYEAgigufyOYM8CEVveM98v8+th0FBhq74UkBMw+Mvcaa5LHujxyASSSCbJgTUKvV8kxWxwXoEcnl41MNojPKQXdQXVg9zpvxp82RJ77LWjmdGt0g8ueP8zDZKGYWEOqfvkhUblGpG4rCUhtj7FhUN/0X8voKOhRhaGEhgwtvqER0C0CQQDrN9niuBwz4Yfs9U/m8X8Q/oj6hwOElIDbuylcapbsjzNJcaiNAAA1MT9Xql9Vr8TrfzE9yLX7N0A+tEhj4XirAkEAybg4ouUKBz/rbfg/fOsdW8+uvNcsSQBbSUYJmhKaQWyX6VztU22y8YJJRHz1vMZunsQOa4WuHAWOef1fap84UwJBAIPlPJNqI75fz8VqxVQ9xL+1yv+YZ7uXPi54Y7gDuP+LSPn89sOh5XvuUpOI4I5+0lAvcWAwxQLKN4cxtonza6MCQQCv47N2zlHtOl4V2KW7TeOen7vUq6bQzL/MZ4aN0vSwYXkgocfKvFeQ4LL0RiWcAkaIp5mPRdQoWOwAEN93P/hDAkEA2Qz9acucSAW58JnoOnBHYnO5lTUIH2/+924PHuyt9wK8X7TpoRghH+BbavxtKKC+pDRYBFITsXy9f55qnZSUZw==" maxlength="32" size="40" placeholder=""/>
                    <span class="null-star">(key)*</span>
                    <span></span>
                </dd>

                <dt>平台公钥：</dt>
                <dd>
                    <span class="null-star"></span>
                    <input name="publicKey"
                           value="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCm++Z9qgw6HxIOKWbAx1hbVU7PokmBBFlomwFhBdU2LInAQWfHEJTGM+2EX9D559J3XXxcPSVanVdM4LcTiJyVJoFSulIg01wR26yk7pzGqy+QJRv1uffL1+otRbgmDLhjeV16148CmwGG3j7xkVOqrv/fsiOViYd1EZdsit+vlQIDAQAB"
                           maxlength="32" size="40" placeholder=""/>
                    <span class="null-star">(publicKey)*</span>
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