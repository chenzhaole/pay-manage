<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公户账户查询</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.table th{
			white-space: normal;
			align:center
		}
	</style>
	<script type="text/javascript">
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
	</script>
</head>
<body>
<div class="breadcrumb">
	<label><a href="#">平台管理> </a><a href="#"><b>公户账户查询</b></a></label>
</div>
 	<form:form id="searchForm" action="${ctx}/publicaccount/publicAccountList"  method="post" class="breadcrumb form-search">
		<table>
			<tr>
				<td>
					<label class="control-label">&nbsp;&nbsp;&nbsp;选择公户：</label>
					<select name="publicAccountCode" id="mchtCode"  class="selectpicker bla bla bli" data-live-search="true">
						<option value="">---请选择---</option>
						<c:forEach var="item" items="${pais}">
							<option value="${item.publicAccountCode}" <c:if test="${paramMap.publicAccountCode eq item.publicAccountCode}">selected</c:if> >${item.publicAccountName}</option>
						</c:forEach>
					</select>
				</td>
				<td>
					<label class="control-label">对方账户名：</label>
					<label class="controls">
						<input value="${paramMap.accountName}" id="accountName" name="accountName" type="text" maxlength="64" class="input-large"/>
					</label>
				</td>
				<td>
					<label class="control-label">备注：</label>
					<label class="controls">
						<input value="${paramMap.summary}" id="summary" name="summary" type="text" maxlength="64" class="input-large"/>
					</label>
				</td>
			</tr>
			<tr>
				<td>
					<label class="control-label" >借方发生额：</label>
					<input value="${paramMap.reductAmount}" id="reductAmount" name="reductAmount" type="number" maxlength="64" class="input-large"/>
				</td>

				<td colspan="2">
					<label class="control-label">贷方发生额：</label>
					<label class="controls">
						<input value="${paramMap.addAmount}" id="addAmount" name="addAmount" type="number" maxlength="64" class="input-large"/>
					</label>
				</td>
			</tr>
		<tr>

			<td>
				<label class="control-label">&nbsp;&nbsp;&nbsp;交易日期：</label>
				<input type="text" class="input-medium Wdate" name ="beginTime" value="${paramMap.beginTime}"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:false,isShowToday:true});"/>-
				<input type="text" class="input-medium Wdate" name ="endTime" value="${paramMap.endTime}"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:false,isShowToday:true});"/>
			</td>
			<td colspan="2" align="right">
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
				<th>公户名称</th>
				<th>交易时间</th>
				<th>借方发生额</th>
				<th>贷方发生额</th>
				<th>账户余额</th>
				<th>对方账号</th>
				<th>对方账号名</th>
				<th>对方开户行</th>
				<th>备注</th>
			</tr>
		</thead>
		<tbody>
				<c:forEach items="${list}" var="report">
					<tr>
						<td>${paisMap[report.publicAccountCode].publicAccountName}</td>
						<td><fmt:formatDate value="${report.tradeTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>${report.reduceAmount }</td>
						<td>${report.addAmount }</td>
						<td>${report.balance }</td>
						<td>${report.accountNo}</td>
						<td>${report.accountName }</td>
						<td>${report.openAccountBankName}</td>
						<td>${report.summary}</td>
					</tr>
				</c:forEach>

		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>