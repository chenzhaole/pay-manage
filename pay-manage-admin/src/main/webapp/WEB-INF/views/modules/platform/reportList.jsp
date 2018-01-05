<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>报表查询</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.table th{
			white-space: normal;
			align:center
		}
	</style>
	<script type="text/javascript">
	$(document).ready(function() {
		$("#btnExport").click(function(){
			$("#searchForm").attr("action","${ctx}/platform/report/export");
			$("#searchForm").submit();
		});
		
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action","${ctx}/platform/report/list");
			$("#searchForm").submit();
		});
	});
	</script>
</head>
<body>
<div class="breadcrumb">
	<label><a href="#">统计报表 > </a><a href="#"><b>报表查询</b></a></label> 
</div>
 	<form:form id="searchForm" action="${ctx}/platform/report/list" modelAttribute="reportFormInfo" method="post" class="breadcrumb form-search">
 		<form:hidden path="reportType"/>
		<label>日期:</label>
		<form:input path="queryDay" type="text" class="input-medium Wdate"
               onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false,readOnly:true,isShowOK:true,isShowToday:true});"/>
        <input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
        <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
 	</form:form>
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<c:choose>
				<c:when test="${reportFormInfo.reportType=='1' }">
					<tr><th>交易日期</th><th>实际支付商户</th><th>申报商户</th><th>所属通道</th><th>所属服务商</th><th>支付类型</th><th>支付成功笔数</th><th>支付成功金额(元)</th><th>申报商户费率</th><th>申报商户手续费(元)</th><th>上游通道费率</th><th>上游通道手续费(元)</th><th>服务商清算收益(元)</th></tr>
				</c:when>
				<c:when test="${reportFormInfo.reportType=='2' }">
					<tr><th>交易日期</th><th>实际支付商户</th><th>申报商户</th><th>所属上游通道</th><th>支付类型</th><th>支付成功笔数</th><th>支付成功金额(元)</th><th>实际支付商户费率</th><th>实际支付商户手续费(元)</th><th>申报商户费率</th><th>申报商户手续费(元)</th><th>实际支付商户结算金额(元)</th><th>费率差额清算收益(元)</th></tr>
				</c:when>
				<c:when test="${reportFormInfo.reportType=='3' }">
					<tr><th>交易日期</th><th>实际支付商户</th><th>申报商户</th><th>所属上游通道</th><th>支付类型</th><th>支付成功笔数</th><th>支付成功金额(元)</th><th>实际支付商户费率</th><th>实际支付商户手续费(元)</th><th>申报商户费率</th><th>申报商户手续费(元)</th><th>申报商户结算金额(元)</th><th>实际支付商户结算金额(元)</th><th>清算费率差额收益(元)</th></tr>
				</c:when>
				<c:when test="${reportFormInfo.reportType=='4' }">
					<tr><th>交易日期</th><th>实际支付商户</th><th>申报商户</th><th>所属代理商</th><th>支付类型</th><th>支付成功金额(元)</th><th>实际支付商户费率</th><th>实际支付商户清算金额(元)</th><th>平台收益代理商分成占比</th><th>代理商分成金额(元)</th></tr>
				</c:when>
			</c:choose>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${reportFormInfo.reportType=='1' }">
					<c:forEach items="${reportList}" var="report">
						<tr>
							<td>${report.tradeDay}</td>
							<td>${report.payMchtId }</td>
							<td>${report.declareMchtId }</td>
							<td>${report.chanId }</td>
							<td>${report.serviceMchtId }</td>
							<td>${report.payType }</td>
							<td>${report.paySuccessNum }</td>
							<td>${report.paySuccessAmount }</td>
							<td>
								${report.declareFeerate }
								<c:if test="${report.declareFeeType eq '1' }">
									分
								</c:if>
								<c:if test="${report.declareFeeType ne '1' }">
									 ‰
								</c:if>
							</td>
							<td>${report.declareFee }</td>
							<td>
								${report.serviceFeerate }
								<c:if test="${report.serviceFeeType eq '1' }">
									分
								</c:if>
								<c:if test="${report.serviceFeeType ne '1' }">
									 ‰
								</c:if>
							</td>
							<td>${report.serviceFee }</td>
							<td>${report.profit }</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:when test="${reportFormInfo.reportType=='2' }">
					<c:forEach items="${reportList}" var="report">
						<tr>
							<td>${report.tradeDay}</td>
							<td>${report.payMchtId }</td>
							<td>${report.declareMchtId }</td>
							<td>${report.chanId }</td>
							<td>${report.payType }</td>
							<td>${report.paySuccessNum }</td>
							<td>${report.paySuccessAmount }</td>
							<td>
								${report.payFeerate }
								<c:if test="${report.payFeeType eq '1' }">
									分
								</c:if>
								<c:if test="${report.payFeeType ne '1' }">
									 ‰
								</c:if>
							</td>
							<td>${report.payFee }</td>
							<td>
								${report.declareFeerate }
								<c:if test="${report.declareFeeType eq '1' }">
									分
								</c:if>
								<c:if test="${report.declareFeeType ne '1' }">
									 ‰
								</c:if>
							</td>
							<td>${report.declareFee }</td>
							<td>${report.payClearAmount }</td>
							<td>${report.profit }</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:when test="${reportFormInfo.reportType=='3' }">
					<c:forEach items="${reportList}" var="report">
						<tr>
							<td>${report.tradeDay}</td>
							<td>${report.payMchtId }</td>
							<td>${report.declareMchtId }</td>
							<td>${report.chanId }</td>
							<td>${report.payType }</td>
							<td>${report.paySuccessNum }</td>
							<td>${report.paySuccessAmount }</td>
							<td>
								${report.payFeerate }
								<c:if test="${report.payFeeType eq '1' }">
									分
								</c:if>
								<c:if test="${report.payFeeType ne '1' }">
									 ‰
								</c:if>
							</td>
							<td>${report.payFee }</td>
							<td>
								${report.declareFeerate }
								<c:if test="${report.declareFeeType eq '1' }">
									分
								</c:if>
								<c:if test="${report.declareFeeType ne '1' }">
									 ‰
								</c:if>
							</td>
							<td>${report.declareFee }</td>
							<td>${report.declareClearAmount }</td>
							<td>${report.payClearAmount }</td>
							<td>${report.profit }</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:when test="${reportFormInfo.reportType=='4' }">
					<c:forEach items="${reportList}" var="report">
						<tr>
							<td>${report.tradeDay}</td>
							<td>${report.payMchtId }</td>
							<td>${report.declareMchtId }</td>
							<td>${report.agentMchtId }</td>
							<td>${report.payType }</td>
							<td>${report.paySuccessAmount }</td>
							<td>
								${report.payFeerate }
								<c:if test="${report.payFeeType eq '1' }">
									分
								</c:if>
								<c:if test="${report.payFeeType ne '1' }">
									 ‰
								</c:if>
							</td>
							<td>${report.payClearAmount }</td>
							<td>
							
								${report.agentFeerate }
								<c:if test="${report.agentFeeType eq '1' }">
									分
								</c:if>
								<c:if test="${report.agentFeeType ne '1' }">
									 ‰
								</c:if>
							</td>
							<td>${report.agentClearAmount }</td>
						</tr>
					</c:forEach>
				</c:when>
			</c:choose>
		</tbody>
		</table>
</body>
</html>