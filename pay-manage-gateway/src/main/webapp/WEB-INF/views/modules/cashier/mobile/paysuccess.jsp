<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0"/>
		<meta name="format-detection" content="telephone=no,email=no,date=no,address=no">
		<title>支付中心</title>
		<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/aui.css" />
        <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/app.css" />
        <script type="text/javascript">
            function toCallBackUrl(callbackUrl) {
                window.location.href = callbackUrl;
            }
            
        </script>
	</head>
	<body id="Error" class="bgF">
		
		<header class="aui-bar aui-bar-nav">
		    <a class="aui-pull-left aui-btn" onclick="history.back()">
		        <img src="${ctxStatic}/images/back.png"/>
		    </a>
		    <div class="aui-title">支付中心</div>
			</header>
        <div class="item" style="margin-top: 1rem;">
           
          	<div class="payResult">
          		<div class="resultHeader">
          				<img src="${ctxStatic}/images/susess.png" alt="" /><span>恭喜你,支付成功！</span>
          		</div>	
          		<div class="resultBody">
          			<p><span>订单号</span><span>${platOrderId}</span></p>
          			<p><span>支付方式</span><span>${payType}</span></p>
          			<p><span>商品名称</span> <span>${goodsName}</span></p>
          			<p><span>商品金额</span><span>￥${amount}元</span></p>
          		</div>    		
          	</div>
            
            <div class="submitBox paysuccess" style="margin-top: 1.7rem;position: absolute;">
              <a href="javaScript:void(0)">
                <input type="button" class="repay" onclick="toCallBackUrl('${callbackUrl}')" value="好的">
              </a>
            </div>
        </div>
        <!--广告位置，id必须命名为divTag-->
        <div id="divTag" style="width:84%;height: 60px;position: relative;left:50%;top:15%;margin-left:-42%;margin-top:0%;"></div>
        <div class="tipbox" style="position:relative;bottom: -20%;">
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
    <script src="${adUrl}"></script>
</html>