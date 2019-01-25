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
		<label>交易日期:</label>
		<input type="text" class="input-medium Wdate" name ="beginTime" value="${paramMap.beginTime}"
               onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:false,isShowToday:true});"/>-
		<input type="text" class="input-medium Wdate" name ="endTime" value="${paramMap.endTime}"
			   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:false,isShowToday:true});"/>

		<label class="control-label">公户：</label>
		<select name="publicAccountCode" id="mchtCode"  class="selectpicker bla bla bli" data-live-search="true">
			<option value="">---请选择---</option>
			<c:forEach var="item" items="${pais}">
				<option value="${item.publicAccountCode}" <c:if test="${paramMap.publicAccountCode eq item.publicAccountCode}">selected</c:if> >${item.publicAccountName}</option>
			</c:forEach>
		</select>

		<label class="control-label">借方：</label>
		<select name="publicAccountCode" id="mchtCode"  class="selectpicker bla bla bli" data-live-search="true">
			<option value="">---请选择---</option>
			<c:forEach var="item" items="${pais}">
				<option value="${item.publicAccountCode}" <c:if test="${paramMap.publicAccountCode eq item.publicAccountCode}">selected</c:if> >${item.publicAccountName}</option>
			</c:forEach>
		</select>

        <input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>

		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="paging" name="paging" type="hidden" value="0"/>
 	</form:form>
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>交易日期</th>
				<th>借方发生额</th>
				<th>贷方发生额</th>
				<th>账户余额</th>
				<th>对方账号</th>
				<th>对方账号名</th>
				<th>对方开户行</th>
				<th>交易时间</th>
			</tr>
		</thead>
		<tbody>
				<c:forEach items="${list}" var="report">
					<tr>
						<td><fmt:formatDate value="${report.tradeTime}" pattern="yyyy-MM-dd"/></td>
						<td>${report.reduceAmount }</td>
						<td>${report.addAmount }</td>
						<td>${report.balance }</td>
						<td>${report.accountNo}</td>
						<td>${report.accountName }</td>
						<td>${report.openAccountBankName}</td>
						<td><fmt:formatDate value="${report.tradeTime}" pattern="HH:mm:ss"/></td>
					</tr>
				</c:forEach>

		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>