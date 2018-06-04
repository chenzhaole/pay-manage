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
<div class="breadcrumb">
	<input id="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);" name="btnCancel"/>
</div>
	<table id="infoTable" class="table table-striped table-bordered table-condensed">
		<tr>
			<td colspan="4" style="text-align: center;"><b>代付结果</b></td>
		</tr>
	<tbody>
	<tr>
		<td width="25%"><b>代付状态:</b></td>	<td width="25%">${fns:getDictLabel(proxyDetail.payStatus,'proxypay_detail_status' ,'' )}</td>
		<td width="25%"><b>上游响应信息:</b></td>	<td width="25%"> ${proxyDetail.returnMessage2}</td>
	</tr>
		<tr>
	        <td width="25%"><b>批次订单号:</b></td>	<td width="25%"> ${proxyDetail.platBatchId}</td>
			<td width="25%"><b>明细订单号:</b></td>	<td width="25%"> ${proxyDetail.id}</td>
   		</tr>
		<tr>
			<td width="25%"><b>上游流水号:</b></td>	<td width="25%"> ${proxyDetail.channelSeq}</td>
			<td width="25%"><b>商户流水号:</b></td>	<td width="25%"> ${proxyDetail.mchtSeq}</td>
		</tr>
	<tr>
		<td width="25%"><b>代付金额:</b></td>	<td width="25%"> <fmt:formatNumber type="number" value="${proxyDetail.amount*0.01}" pattern="0.00" maxFractionDigits="2"/>元</td>
		<td width="25%"><b>手续费:</b></td>	<td width="25%"> <fmt:formatNumber type="number" value="${proxyDetail.mchtFee*0.01}" pattern="0.00" maxFractionDigits="2"/>元</td>
	</tr>
		<tr>
			<td width="25%"><b>创建时间:</b></td>	<td width="25%"> <fmt:formatDate value="${proxyDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td width="25%"><b>更新时间:</b></td>	<td width="25%"> <fmt:formatDate value="${proxyDetail.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		</tr>
	</tbody>
	</table>
	<table class="table table-striped table-bordered table-condensed">
		<tr>
			<td colspan="4" style="text-align: center;"><b>代付详情</b></td>
		</tr>
		<tbody>

		<tr>
			<td width="25%"><b>商户编码:</b></td>	<td width="25%"> ${proxyDetail.mchtId}</td>
			<td width="25%"><b>商户名称:</b></td>	<td width="25%"> ${proxyDetail.extend2}</td>
		</tr>
		<tr>
			<td width="25%"><b>通道编码:</b></td>	<td width="25%"> ${proxyDetail.chanId}</td>
			<td width="25%"><b>通道名称:</b></td>	<td width="25%"> ${proxyDetail.extend3}</td>
		</tr>

		<tr>
			<td width="25%"><b>代付人姓名:</b></td>	<td width="25%"> ${proxyDetail.bankCardName}</td>
			<td width="25%"><b>预留手机号:</b></td>	<td width="25%"> ${proxyDetail.mobile}</td>
		</tr>
		<tr>
			<td width="25%"><b>银行卡号:</b></td>	<td width="25%"> ${proxyDetail.bankCardNo}</td>
			<td width="25%"><b>银行名称:</b></td>	<td width="25%"> ${proxyDetail.bankName}</td>
		</tr>
		<tr>
			<td width="25%"><b>省:</b></td>	<td width="25%"> ${proxyDetail.province}</td>
			<td width="25%"><b>市:</b></td>	<td width="25%"> ${proxyDetail.city}</td>
		</tr>
		<tr>
			<td><b>备注: </b></td>
			<td colspan="3">${proxyDetail.remark}</td>
		</tr>
		</tbody>
	</table>
</body>
</html>