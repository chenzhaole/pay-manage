<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道商户支付方式维度报表查询</title>
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
	<label><a href="#">通道维度统计报表 > </a><a href="#"><b>通道维度统计报表</b></a></label>
</div>
 	<form:form id="searchForm" action="${ctx}/platform/statistice/mchtPayTypeOrderStatistice"  method="post" class="breadcrumb form-search">
		<label>日期:</label>
		<input type="text" class="input-medium Wdate" name ="statisticeTime" value="${paramMap.statisticeTime}"
               onclick="WdatePicker({dateFmt:'yyyyMMddHHmm',quickSel:['%y-%M-%d %H-00-00','%y-%M-%d %H-15-00','%y-%M-%d %H-30-00','%y-%M-%d %H-45-00'],isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true});"/>
        <label class="control-label">支付通道</label>
        <select name="chanCode" class="input-xlarge" id="chanCode">
            <option value="">--请选择--</option>
            <c:forEach items="${chanInfos}" var="chanInfo">
                <option data-chanCode="${chanInfo.chanCode }"
                        <c:if test="${paramMap.chanCode == chanInfo.chanCode}">selected</c:if>
                        value="${chanInfo.chanCode}">${chanInfo.name}</option>
            </c:forEach>
        </select>


        <label class="control-label">支付方式</label>
        <select name="payType" class="input-xlarge" id="payType">
            <option value="">--请选择--</option>
            <c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
                <option
                        <c:if test="${paramMap.payType == paymentTypeInfo.code}">selected</c:if>
                        value="${paymentTypeInfo.code}">${paymentTypeInfo.desc}</option>
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
				<th>报表日期</th>
				<th>渠道名称</th>
				<th>支付方式</th>
				<th>请求数</th>
				<th>请求金额(元)</th>
				<th>成功数</th>
				<th>成功金额(元)</th>
                <th>成功率</th>
			</tr>
		</thead>
		<tbody>
				<c:forEach items="${list}" var="report">
					<tr>
						<td>${report.statisticeTime}</td>
						<td>${report.chanName}</td>
						<td>${report.payTypeName}</td>
						<td>${report.mchtRequestSize }</td>
						<td>${report.mchtRequestAmount }</td>
						<td>${report.mchtSuccessSize }</td>
						<td>${report.mchtSuccessAmount }</td>
                        <td><fmt:formatNumber value="${report.mchtSuccessSize/report.mchtRequestSize}" type="percent" maxFractionDigits="2"/></td>
					</tr>
				</c:forEach>

		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>