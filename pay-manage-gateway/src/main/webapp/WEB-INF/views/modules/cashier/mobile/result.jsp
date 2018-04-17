<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0"/>
		<meta name="format-detection" content="telephone=no,email=no,date=no,address=no">
		<title>支付结果页</title>
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/aui.css" />
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/app.css" />
	</head>
	<body id="Error" class="bgF">
		<header class="aui-bar aui-bar-nav">
		    <a class="aui-pull-left aui-btn" onclick="history.back()">
		        <img src="${ctxStatic}/images/back.png"/>
		    </a>
		    <div class="aui-title">支付中心</div>
			</header>
        <div class="item">
           
            <div class="tipText">
          	  <p class="warnTip"><img src="${ctxStatic}/images/lamp.png" alt="温馨提示"  class="lamp"/>温馨提示</p>
           	<p>支付时请注意身边安全,并注意避免重复支付。</p>
            </div>
            
            <div class="submitBox">	
              <a>
				  <div class="aui-btn aui-btn-define1" onclick="goback(this)">返回</div>
			  </a>
            </div>
        </div>
        <div class="tipbox">
			<c:if test="${ !empty mobile || !empty qq}">
				<p>如遇支付问题请联系客服</p>
			</c:if>
			<c:if test="${ !empty mobile}">
				<p><span class="tag">客服热线 </span><span> ${mobile}</span></p>
			</c:if>
			<c:if test="${ !empty qq}">
				<p><span class="tag">客服QQ </span><span> ${qq}</span></p>
			</c:if>
		</div>
	</body>
	<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
	<script src="${ctxStatic}/js/app.js?v=1.0.0"></script>
	<script type="text/javascript">
        var callbackUrl = '${callbackUrl}';
        function goback(el) {
            location.href = callbackUrl;
        }
	</script>
</html>