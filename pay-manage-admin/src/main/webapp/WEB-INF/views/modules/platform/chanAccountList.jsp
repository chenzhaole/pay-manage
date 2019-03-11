<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>上游账务明细查询</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.table th{
			white-space: normal;
			align:center
		}
		.control-label{
			display: inline-block;
			width:100px;
			text-align: right;
		}
		.controls{
			float:right;
			width:200px;
		}

	</style>
	<script type="text/javascript">
        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").submit();
			});
		});
		function page(n, s) {
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#paging").val("1");
			$("#searchForm").submit();
			return false;
		}

		$(function () {
			$("#tradeType").val("${paramMap.tradeType}");
			$("#accountType").val("${paramMap.accountType}");
			$("#accountAddType").val("${paramMap.accountAddType}");
		});
	</script>
</head>
<body>
<div class="breadcrumb">
	<label><a href="#">上游对账> </a><a href="#"><b>上游账务明细查询</b></a></label>
</div>
<tags:message content="${message}" type="${messageType}"/>
 	<form:form id="searchForm" action="${ctx}/chanAccountDetail/list"  method="post" class="breadcrumb form-search">
		<table>
			<tr>
				<td>
					<label class="control-label">入账日期：</label>
					<div class="controls"><input id="createTime" name="createTime" type="text" readonly="readonly" maxlength="20"
						   class="input-medium Wdate"
						   value="${paramMap.createTime}"
												 onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false,readOnly:true,isShowOK:false,isShowToday:false});"/></div>
				</td>
				<td>
					<label class="control-label">电子账户：</label>
					<div class="controls">
						<select name="accountId" id="accountId"  class="selectpicker bla bla bli" data-live-search="true">
							<option value="">---请选择---</option>
							<c:forEach var="ea" items="${electronicAccounts}">
								<option value="${ea.id}" <c:if test="${accountId eq ea.id}">selected</c:if> >${ea.electronicAccountName}</option>
							</c:forEach>
						</select>
					</div>
				</td>
				<td>
					<label class="control-label">平台订单号：</label>
					<div class="controls">
						<input value="${paramMap.platOrderId}" id="platOrderId" name="platOrderId" type="text" maxlength="64" class="input-large"/>
					</div>
				</td>
				<td align="right">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td >
					<label class="control-label">交易类型：</label>
					<div class="controls">
						<select id="tradeType" name="tradeType" >
							<option value="">请选择</option>
							<option value="10">支付</option>
							<option value="11">重复支付补账</option>
							<option value="12">支付调账</option>
							<option value="20">商户充值</option>
							<option value="21">我司公户充值</option>
							<option value="22">上游结算充值</option>
							<option value="30">代付</option>
							<option value="31">重复代付补账</option>
							<option value="32">结算</option>
							<option value="33">代付调账</option>
						</select>
					</div>
				</td>
				<td >
					<label class="control-label">账户类型：</label>
					<div class="controls">
						<select id="accountType" name="accountType" >
							<option value="">请选择</option>
							<option value="0">公户</option>
							<option value="1">电子账户</option>
						</select>
					</div>
				</td>
				<td >
					<label class="control-label">记账类型：</label>
					<div class="controls">
						<select id="accountAddType" name="accountAddType" >
							<option value="">请选择</option>
							<option value="0">入账</option>
							<option value="1">出账</option>
						</select>
					</div>
				</td>
				<td align="right">
					<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
				</td>
			</tr>

		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="paging" name="paging" type="hidden" value="0"/>
 	</form:form>
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>电子账户名称</th>
				<th>账户类型</th>
				<th>平台订单号</th>
				<th>商户订单号</th>
				<th>交易类型</th>
				<th>记账类型</th>
				<th>交易金额</th>
				<th>收费类型</th>
				<th>通道手续费比例</th>
				<th>通道手续费金额</th>
				<th>本次增加金额</th>
				<th>本次减少金额</th>
				<th>原始现金总金额</th>
				<th>现金总金额</th>
				<th>记账时间</th>
			</tr>
		</thead>
		<tbody>
				<c:forEach items="${list}" var="accountDetail">
					<tr>
						<td>${accountDetail.accountName}</td>
						<td>
							<c:if test="${accountDetail.accountType=='0'}">公户</c:if>
							<c:if test="${accountDetail.accountType=='1'}">电子账户</c:if>
						</td>
						<td>${accountDetail.platOrderId }</td>
						<td>${accountDetail.mchtOrderId }</td>
						<td>
							<c:if test="${accountDetail.tradeType=='10'}">支付</c:if>
							<c:if test="${accountDetail.tradeType=='11'}">重复支付补账</c:if>
							<c:if test="${accountDetail.tradeType=='12'}">支付调账</c:if>
							<c:if test="${accountDetail.tradeType=='20'}">商户充值</c:if>
							<c:if test="${accountDetail.tradeType=='21'}">我司公户充值</c:if>
							<c:if test="${accountDetail.tradeType=='22'}">上游结算充值</c:if>
							<c:if test="${accountDetail.tradeType=='30'}">代付</c:if>
							<c:if test="${accountDetail.tradeType=='31'}">重复代付补账</c:if>
							<c:if test="${accountDetail.tradeType=='32'}">结算</c:if>
							<c:if test="${accountDetail.tradeType=='33'}">代付调账</c:if>
						</td>
						<td>
							<c:if test="${accountDetail.accountAddType=='0'}">入账</c:if>
							<c:if test="${accountDetail.accountAddType=='1'}">出账</c:if>
						</td>
						<td>
							<fmt:formatNumber type="number" value="${accountDetail.tradeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/>
						</td>
						<td>
							<c:if test="${accountDetail.feeType=='1'}">单笔</c:if>
							<c:if test="${accountDetail.feeType=='2'}">费率</c:if>
						</td>
						<td>
							<fmt:formatNumber type="number" value="${accountDetail.tradeFeeRate}" pattern="0.00"
											  maxFractionDigits="2"/>‰
						</td>
						<td>
							<fmt:formatNumber type="number" value="${accountDetail.tradeFeeAmount*0.01}" pattern="0.00"
											  maxFractionDigits="2"/>
						</td>
						<td>
							<fmt:formatNumber type="number" value="${accountDetail.addAmount*0.01}" pattern="0.00"
											  maxFractionDigits="2"/>
						</td>
						<td>
							<fmt:formatNumber type="number" value="${accountDetail.reduceAmount*0.01}" pattern="0.00"
											  maxFractionDigits="2"/>
						</td>
						<td>
							<fmt:formatNumber type="number" value="${accountDetail.oriCashTotalAmount*0.01}" pattern="0.00"
											  maxFractionDigits="2"/>
						</td>
						<td>
							<fmt:formatNumber type="number" value="${accountDetail.cashTotalAmount*0.01}" pattern="0.00"
											  maxFractionDigits="2"/>
						</td>
						<td><fmt:formatDate value="${accountDetail.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					</tr>
				</c:forEach>

		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>