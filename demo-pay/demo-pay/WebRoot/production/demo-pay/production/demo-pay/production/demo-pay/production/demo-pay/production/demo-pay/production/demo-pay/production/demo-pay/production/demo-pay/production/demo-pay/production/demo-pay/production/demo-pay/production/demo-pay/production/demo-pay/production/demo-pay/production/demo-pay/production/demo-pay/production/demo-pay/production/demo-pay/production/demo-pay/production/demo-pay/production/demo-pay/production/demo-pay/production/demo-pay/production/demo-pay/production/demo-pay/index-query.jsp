<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>扫码查询测试页面</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href="css/index.css" rel="stylesheet" type="text/css">
		<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
		<script type="text/javascript">
			function doSubmit(){
				$("form").submit();
			}
			$(function(){
				$('.hideClass').hide();
			});
		</script>
	</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0  topMargin=4>
	<div id="main">
        <div class="cashier-nav">
            <ol>
				<li class="current">1、提交信息（扫码查询） </li> 
            </ol>
        </div>
        <form action="testQuery" method="post"  target="_blank">
            <div id="body" style="clear:left">
                <dl class="content">
					<dt class="hideClass">版本号：</dt>
					<dd class="hideClass">
						<span class="null-star"></span>
						<input size="30" name="version" value="1.0" readonly="readonly" maxlength="8"  placeholder="长度8"/>
						<span>(长度8)</span>
						<span></span>
					</dd>
					<dt class="hideClass">字符集：</dt>
					<dd class="hideClass">
						<span class="null-star"></span>
						<input size="30" name="charset" value="UTF-8" readonly="readonly" maxlength="8"  placeholder="长度8"/>
						<span>(长度8)</span>
						<span></span>
					</dd>
                    <dt class="hideClass">签名方式：</dt>
                    <dd class="hideClass">
                        <span class="null-star"></span>
                        <input size="30" name="sign_type" value="MD5" readonly="readonly" maxlength="8"  placeholder="长度8"/>
						<span>(长度8)</span>
                        <span></span>
                    </dd>
                    <dt>平台订单号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="transaction_id" value="" maxlength="32" size="30"  placeholder="长度32"/>
                        <span>(长度32)</span>
                        <span></span>
                    </dd>
                    
                    <dt></dt>
                    <dd>
                        <span class="new-btn-login-sp">
                            <button class="new-btn-login" type="button" onclick="doSubmit()" style="text-align:center;">确 认</button>
                        </span>
                    </dd>
                </dl>
            </div>
		</form>
        <div id="foot">
			<ul class="foot-ul">
				<li><font class="note-help">如果您点击“确认”按钮，即表示您同意该次的执行操作。 </font></li>
			</ul>
		</div>
	</div>
</body>
</html>