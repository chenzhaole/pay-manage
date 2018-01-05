  <%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单详情</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
        function viewChange(divId) {
            $.jBox($("#" + divId).html(), {title:"改签记录", buttons:{"关闭":true},
                bottomText:""});
        }
        
        function sendQuestion() {
            $("#sendForm").submit();
        }
	</script>
</head>
<body>

	<%-- <ul class="nav nav-tabs">
		<li><a href="${ctx}/order/list">交易订单列表</a></li>
		<li class="active"><a href="#">交易订单详情</a></li>
	</ul> --%>

	<!-- <div id="searchForm" class="breadcrumb">
		<label><th>商户信息</th></label>
	</div> -->

	<table id="infoTable" class="table table-striped table-bordered table-condensed">
		<tr>
			<td colspan="4" style="text-align: center;"><b>商户信息</b></td>
		</tr>
	<tbody>
		<tr>
	        <td width="25%">商户编码:</td>
	        <td width="25%"> ${mchtInfo.mchtCode}</td>
	        <td width="25%">商户名称:</td>
	        <td width="25%">${mchtInfo.name}</td>
   		</tr>
   		<tr>
   			 <td>地址: </td>
	        <td colspan="3">${mchtInfo.companyAdr}</td>
   		</tr>
	</tbody>
	</table>
	
	<!-- <div id="searchForm" class="breadcrumb">
		<label><th>上游通道信息</th></label>
	</div> -->

	<table id="infoTable" class="table table-striped table-bordered table-condensed">
	<tbody>
		<tr>
			<td colspan="4" style="text-align: center;"><b>上游通道信息</b></td>
		</tr>
		<tr>
	        <td width="25%">上游通道编码:</td>
	        <td width="25%">${chanInfo.chanCode}</td>
	        <td width="25%">上游通道名称: </td>
	        <td width="25%">${chanInfo.name} </td>
   		</tr>
   		<tr>
   			<td>地址: </td>
	        <td colspan="3">${chanInfo.companyAdr}</td>
   		</tr>
	</tbody>
	</table>
	
	<!-- <div id="searchForm" class="breadcrumb">
		<label><th>订单信息</th></label>
	</div>
	 -->
	<table id="infoTable" class="table table-striped table-bordered table-condensed">
	<tr>
			<td colspan="4" style="text-align: center;"><b>订单信息</b></td>
		</tr>
	<tr>
		<td>交易金额（元）:</td>
		<td colspan="3"><fmt:formatNumber type="number" value="${orderInfo.amount/100}" pattern="0.00" maxFractionDigits="2"/></td>
	</tr>
	<tr>
		<td width="25%">订单创建时间:</td>
		<td width="25%"><fmt:formatDate value="${orderInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		<td width="25%">订单更新时间:</td>
		<td width="25%"><fmt:formatDate value="${orderInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	</tr>
	<tr>
		<td >官方交易订单号:</td>
		<td>${orderInfo.officialOrderId}</td>
		<td >平台订单号:</td>
		<td>${orderInfo.platOrderId}</td>
	</tr>
	<tr>
		<td >商户订单号: </td>
		<td>${orderInfo.mchtOrderId}</td>
		<td >上游通道订单号:</td>
		<td>${orderInfo.chanOrderId}</td>
	</tr>
	<tr>
	<td >支付产品名称:</td>
	<td>${platProduct.name}</td>
		<td >通道支付方式名称: </td>
		<td>${chanMchtPaytype.name}</td>
	</tr>
	<tr>
	<td >支付产品费率:</td>
	<td>${platProduct.chanOrderId}</td>
		<td >通道支付方式费率: </td>
		<td>${chanMchtPaytype.chanOrderId}</td>
	</tr>
	<tr>
	<td >支付类型:</td>
	<td>${orderInfo.payType}</td>
		<td>订单状态: </td>
		<td>${orderInfo.status}</td>
	</tr>
	<tr>
	<td>商品展示名称:</td>
	<td>${orderInfo.goodsName}</td>
		<td>商品描述: </td>
		<td>${orderInfo.goodsDesc}</td>
	</tr>
	
    <%--<tr>
		<td width="33%">官方交易订单号:</td>
		<td width="33%">平台订单号:</td>
        <td width="33%">商户订单号: </td>

		<td width="33%">上游通道订单号:</td>


        <td width="33%">交易类型: 	<c:choose>
						<c:when test="${orderInfo.payType eq '02'}">支付宝</c:when>
		            	<c:when test="${orderInfo.payType eq '05'}">微信</c:when>
		            	<c:when test="${orderInfo.payType eq '07'}">银行卡</c:when>
		            	<c:when test="${orderInfo.payType eq '08'}">QQ支付</c:when>
	            	</c:choose></td>
        <td width="33%">交易金额: ${orderInfo.amount/100}元</td>
    </tr>


    <tr>
        <td width="33%">平台流水号: ${orderInfo.platOrderId}</td>
        <td width="33%">产品名称: ${orderInfo.goodsName}</td>
        <td width="33%">手续费: </td>
    </tr>
    <tr>
        <td width="33%">商户代号: ${orderInfo.mchtId}</td>
        <td width="33%">产品描述: ${orderInfo.goodsDesc}</td>
         <td width="33%"></td>
&lt;%&ndash;         <td width="33%">退款金额: ${orderInfo.refundAmount}</td> &ndash;%&gt;
    </tr>
    <tr>
        <td width="33%">支付时间: <fmt:formatDate value="${orderInfo.updateTime}"  pattern="yyyy-MM-dd  HH:mm:ss"/></td>
        <td width="33%">支付状态: <c:choose>
						<c:when test="${orderInfo.status eq '-1'}">失败</c:when>
		            	<c:when test="${orderInfo.status eq '1'}">创建</c:when>
		            	<c:when test="${orderInfo.status eq '2'}">成功</c:when>
		            	<c:otherwise>未知状态</c:otherwise>
	            	</c:choose></td>
         <td width="33%"></td>
&lt;%&ndash;         <td width="33%">退款手续费: ${orderInfo.refundFee}</td> &ndash;%&gt;
    </tr>
    <tr>
        <td width="33%">平台错误码: ${orderInfo.chan2platResCode}</td>
        <td width="33%">创建时间: <fmt:formatDate value="${orderInfo.createTime}"  pattern="yyyy-MM-dd  HH:mm:ss"/></td>
        <td width="33%">更新时间：<fmt:formatDate value="${orderInfo.updateTime}"  pattern="yyyy-MM-dd  HH:mm:ss"/></td>
    </tr>--%>
    </tbody>
</table>

<!-- <div id="searchForm" class="breadcrumb">
	<label><th>支付请求详情</th></label>
</div> -->
	<%--<table id="infoTable" class="table table-striped table-bordered table-condensed">--%>
		<%--<tr>--%>
			<%--<td colspan="4" style="text-align: center;"><b>商户向平台发起支付</b></td>--%>
		<%--</tr>--%>
		<%--<tr>--%>
			<%--<td width="25%">IP:</td>--%>
			<%--<td width="25%"></td>--%>
			<%--<td width="25%">请求参数:</td>--%>
			<%--<td width="25%"></td>--%>
		<%--</tr>--%>
		<%--<tr>--%>
			<%--<td>解密参数:</td>--%>
			<%--<td></td>--%>
			<%--<td>响应内容:</td>--%>
			<%--<td></td>--%>
		<%--</tr>--%>
	<%--</table>--%>
	<%--<table id="infoTable" class="table table-striped table-bordered table-condensed">--%>
		<%--<tr>--%>
			<%--<td colspan="4" style="text-align: center;"><b>平台向上游通道发起支付</b></td>--%>
		<%--</tr>--%>
		<%--<tr>--%>
			<%--<td width="25%">请求参数:</td>--%>
			<%--<td width="25%"></td>--%>
			<%--<td width="25%">解密参数:</td>--%>
			<%--<td width="25%"></td>--%>
		<%--</tr>--%>
		<%--<tr>--%>
			<%----%>
			<%--<td>响应内容:</td>--%>
			<%--<td colspan="3"></td>--%>
		<%--</tr>--%>
	<%--</table>    --%>
<%--<!-- <div id="searchForm" class="breadcrumb">--%>
	<%--<label><th>支付结果异步通知详情</th></label>--%>
<%--</div> -->--%>
	<%--<table id="infoTable" class="table table-striped table-bordered table-condensed">--%>
		<%--<tr>--%>
			<%--<td colspan="4" style="text-align: center;"><b>通道异步通知平台</b></td>--%>
		<%--</tr>--%>
		<%--<tr>--%>
			<%--<td width="25%">请求参数:</td>--%>
			<%--<td width="25%"></td>--%>
			<%--<td width="25%">解密参数:</td>--%>
			<%--<td width="25%"></td>--%>
		<%--</tr>--%>
		<%--<tr>--%>
			<%--<td>通知详情:</td>--%>
			<%--<td colspan="3"></td>--%>
		<%--</tr>--%>
	<%--</table>--%>
	<%--<table id="infoTable" class="table table-striped table-bordered table-condensed">--%>
		<%--<tr>--%>
			<%--<td colspan="4" style="text-align: center;"><b>平台异步通知商户</b></td>--%>
		<%--</tr>--%>
		<%--<tr>--%>
			<%--<td width="25%">商户后台通知地址:</td>--%>
			<%--<td width="25%"></td>--%>
			<%--<td width="25%">响应内容:</td>--%>
			<%--<td width="25%"></td>--%>
		<%--</tr>--%>
	<%--</table>    --%>
   <%--<div class="breadcrumb">--%>
        <%--<input id="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);"--%>
               <%--name="btnCancel"/>--%>
    <%--</div>--%>
    
</body>
</html>