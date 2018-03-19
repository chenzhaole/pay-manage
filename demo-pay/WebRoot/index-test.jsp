<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
	<title>测试Demo</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="css/index.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0  topMargin=4>
	<div id="main">
        <div class="cashier-nav">
            <ol>
				<li class="current">选择测试功能</li>
            </ol>
        </div>
        <form action="testQuery" method="post"  target="_blank">
            <div id="body" style="clear:left">
                <dl class="">

					<dd>
						<a href="index-pay.jsp">支付测试</a>
					</dd>
                </dl>
             	<dl class="">

					<dd>
						<a href="index-ownership.jsp">银行卡信息查询</a>
					</dd>
                </dl>

				<dl class="">

					<dd>
						<a href="index-mchtRegiste.jsp">商户入驻</a>
					</dd>
				</dl>

				<dl class="">

					<dd>
						<a href="index-txQuickPrePay.jsp">TX快捷预支付支付</a>
					</dd>
				</dl>

				<dl class="">

					<dd>
						<a href="index-txQuickPay.jsp">TX快捷支付</a>
					</dd>
				</dl>

               <!--  
                <dl class="content">
                    <dt>订单查询测试：</dt>
					<dd>
						<a href="index-query.jsp">点此</a>
					</dd>
                </dl>
                <dl class="content">
                    <dt>退款测试：</dt>
					<dd>
						<a href="index-refund.jsp">点此</a>
					</dd>
                </dl>
                <dl class="content">
                    <dt>退款查询测试：</dt>
					<dd>
						<a href="index-refund-query.jsp">点此</a>
					</dd>
                </dl>
                 -->
            </div>
		</form>
	</div>
</body>
</html>