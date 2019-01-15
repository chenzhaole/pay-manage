<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商户维度报表查询</title>
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
	</script>
</head>
<body>
<div class="breadcrumb">
	<label><a href="#">商户维度统计报表 > </a><a href="#"><b>商户维度统计报表</b></a></label>
</div>
 	<form:form id="searchForm" action="${ctx}/platform/statistice/mchtOrderStatistice"  method="post" class="breadcrumb form-search">
		<label>日期:</label>
		<input type="text" class="input-medium Wdate" name ="statisticeTime" value="${paramMap.statisticeTime}"
               onclick="WdatePicker({dateFmt:'yyyyMMddHHmm',quickSel:['%y-%M-%d %H-00-00','%y-%M-%d %H-15-00','%y-%M-%d %H-30-00','%y-%M-%d %H-45-00'],isShowClear:false,readOnly:true,isShowOK:false,isShowToday:true});"/>
        <input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
 	</form:form>
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>报表日期</th>
				<th>商户名称</th>
				<th>请求数</th>
				<th>请求金额(元)</th>
				<th>成功数</th>
				<th>成功金额(元)</th>
			</tr>
		</thead>
		<tbody>
				<c:forEach items="${list}" var="report">
					<tr>
						<td>${report.statisticeTime}</td>
						<td>${report.mchtName}</td>
						<td>${report.mchtRequestSize }</td>
						<td>${report.mchtRequestAmount }</td>
						<td>${report.mchtSuccessSize }</td>
						<td>${report.mchtSuccessAmount }</td>
					</tr>
				</c:forEach>

		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>