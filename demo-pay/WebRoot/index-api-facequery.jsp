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
            var mchtId = $.trim($('input[name=mchtId]').val());
            if (mchtId == '') {
                alert('商户号不能为空');
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
            <li class="current">提交信息（面值库存查询请求）</li>
        </ol>
    </div>
    <form action="testQueryFace" method="post" target="_blank">
        <div id="body" style="clear:left">
            <dl class="content">

                <%--*****************  head  ****************--%>
                <dt>面值库存查询：</dt>
                <dd>
                    <span class="null-star"></span>
                    <select name="queryUrl">
                        <option value="http://127.0.0.1:12080/gateway/api/queryFace/">本地 http://127.0.0.1:12080/gateway/api/queryFace/</option>
                        <option value="http://114.115.206.62:12080/gateway/api/queryFace/">开发 http://114.115.206.62:12080/gateway/api/queryFace/</option>
                        <option value="http://114.115.160.132:12080/gateway/api/queryFace/">测试 http://114.115.160.132:12080/gateway/api/queryFace/</option>
                        <option value="http://106.2.6.41:12080/gateway/api/queryFace/">生产 http://106.2.6.41:12080/gateway/api/queryFace/</option>
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