<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>监控报表查询</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/js/select2.js"></script>
	<link href="${ctxStatic}/css/select2.css" rel="stylesheet" />
	<style type="text/css">
		.table th{
			white-space: normal;
			align:center
		}
	</style>
	<script type="text/javascript">

	$(document).ready(function() {

        $('.selectpicker').select2({
        });

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
	<label><a href="#">监控统计报表</a><a href="#"><b>监控统计报表</b></a></label>
</div>
 	<form:form id="searchForm" action="${ctx}/platform/original/statistice/chanMchtStatistice"  method="post" class="breadcrumb form-search">
		<table>
			<tr>
				<td>
					<label class="control-label">商户名称：</label>
					<select name="mchtCode" id="mchtCode"  class="selectpicker" data-live-search="true">
						<option value="">--全部--</option>
						<c:forEach var="mcht" items="${mchtList}">
							<option value="${mcht.mchtCode}" <c:if test="${paramMap.mchtCode eq mcht.mchtCode}">selected</c:if> >${mcht.name}</option>
						</c:forEach>
					</select>
					<label class="control-label">通道商户支付方式</label>
					<select name="chanMchtPayTypeId" class="selectpicker" data-live-search="true" >
						<option value="">--全部--</option>
						<c:forEach items="${chanInfoList}" var="chan">
							<option
									<c:if test="${paramMap.chanMchtPayTypeId eq chan.id}">selected</c:if>
									value="${chan.id}">${chan.name}</option>
						</c:forEach>
					</select>

					<label class="control-label">通道名称</label>
					<select name="chanCode" class="selectpicker" id="chanCode">
						<option value="">--全部--</option>
						<c:forEach items="${chanInfos}" var="chanInfo">
							<option data-chanCode="${chanInfo.chanCode }"
									<c:if test="${paramMap.chanCode eq chanInfo.chanCode}">selected</c:if>
									value="${chanInfo.chanCode}">${chanInfo.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label class="control-label">支付方式</label>
					<select name="payType" class="selectpicker" id="payType">
						<option value="">--全部--</option>
						<c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
							<option
									<c:if test="${paramMap.payType == paymentTypeInfo.code.concat(',').concat(paymentTypeInfo.desc)}">selected</c:if>
									value="${paymentTypeInfo.code},${paymentTypeInfo.desc}">${paymentTypeInfo.desc}</option>
						</c:forEach>
					</select>

					<label>统计时间范围:</label>
					<input type="text" class="input-medium Wdate" name ="beginTime" value="${paramMap.beginTime}"
						   onclick="WdatePicker({dateFmt:'yyyyMMddHHmm',quickSel:['%y-%M-%d %H-00','%y-%M-%d %H-15','%y-%M-%d %H-30','%y-%M-%d %H-45','%y-%M-%d \#{%H+1}-00'],isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true,maxDate:'#F{$dp.$D(\'endTime\')}'});"/>-<input type="text" class="input-medium Wdate" id ="endTime" name ="endTime" value="${paramMap.endTime}"
																																																												onclick="WdatePicker({dateFmt:'yyyyMMddHHmm',quickSel:['%y-%M-%d %H-00','%y-%M-%d %H-15','%y-%M-%d %H-30','%y-%M-%d %H-45','%y-%M-%d \#{%H+1}-00'],isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true,maxDate:'%y-%M-%d'});"/>


					<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
				</td>
			</tr>
		</table>
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input id="paging" name="paging" type="hidden" value="0"/>
 	</form:form>
 	<div style="width: 99%;overflow-x:auto;">
    <table id="contentTable" class="table table-striped table-bordered table-condensed" style="width: 3000px">
		<thead>
			<tr>
				<th width="60px">商户名称</th>
				<th width="60px">通道商户支付方式</th>
				<th width="60px">通道名称</th>
				<th width="60px">支付方式</th>
				<th width="60px">商户请求条数</th>
				<th width="60px">我司拦截条数</th>
				<th width="60px">提交上游数</th>
				<th width="60px">提交上游成功条数</th>
				<th width="60px">提交上游返回成功</th>
				<th width="60px">提交上游返回失败</th>
				<th width="60px">提交上游失败条数</th>
				<th width="60px">支付成功条数</th>
				<th width="60px">我司与上游网络失败率</th>
				<th width="60px">上游通道可用率</th>
				<th width="60px">通道支付成功率</th>
				<th width="60px">商户支付成功率</th>
				<th width="60px">订单开始时间</th>
				<th width="60px">订单结束时间</th>
				<th width="60px">统计时间</th>

			</tr>
		</thead>
		<tbody>
				<c:forEach items="${list}" var="report">
					<tr>
						<td>${report.mchtName}</td>
						<td>${report.chanMchtPayTypeDesc}</td>
						<td>${report.chanName}</td>
						<td>
							<c:choose>
							<c:when test="${paramMap.payType==null || ''.equals(paramMap.payType)}">
								${report.payType}
							</c:when>
							<c:otherwise>
								${paramMap.payType.split(',')[1]}
							</c:otherwise>
							</c:choose>
						</td>
						<td>
							<fmt:formatNumber value="${report.mchtReqNum}" type="NUMBER" maxFractionDigits="0"></fmt:formatNumber>
						</td>
						<td>
							<fmt:formatNumber value="${report.compInterceptNum }" type="NUMBER" maxFractionDigits="0"></fmt:formatNumber>

						</td>
						<td>
							<fmt:formatNumber value="${report.chanCommitNum }" type="NUMBER" maxFractionDigits="0"></fmt:formatNumber>

						</td>
						<td>
							<fmt:formatNumber value="${report.chanCommitSuccNum }" type="NUMBER" maxFractionDigits="0"></fmt:formatNumber>

						</td>
						<td>
							<fmt:formatNumber value="${report.chanCommitBackSuccNum }" type="NUMBER" maxFractionDigits="0"></fmt:formatNumber>

						</td>
						<td>
							<fmt:formatNumber value="${report.chanCommitBackFailNum }" type="NUMBER" maxFractionDigits="0"></fmt:formatNumber>

						</td>
						<td>
							<fmt:formatNumber value="${report.chanCommitFailNum }" type="NUMBER" maxFractionDigits="0"></fmt:formatNumber>
						</td>
						<td>
							<fmt:formatNumber value="${report.paySuccNum}" type="NUMBER" maxFractionDigits="0"></fmt:formatNumber>

						<td>
							<c:choose>
							<c:when test="${report.chanCommitSuccNum.unscaledValue() == 0}">
								0%
							</c:when>
							<c:otherwise>
								<fmt:formatNumber value="${report.chanCommitBackTimeoutNum/report.chanCommitNum}" type="percent" maxFractionDigits="2"/>
							</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${report.chanCommitSuccNum.unscaledValue()==0}">
									0%
								</c:when>
								<c:otherwise>
									<fmt:formatNumber value="${report.chanCommitBackSuccNum/report.chanCommitSuccNum}" type="percent" maxFractionDigits="2"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${report.chanCommitSuccNum.unscaledValue()==0}">
									0%
								</c:when>
								<c:otherwise>
									<fmt:formatNumber value="${report.paySuccNum/report.chanCommitSuccNum}" type="percent" maxFractionDigits="2"/>
								</c:otherwise>
							</c:choose>

						</td>
						<td>
							<c:choose>
								<c:when test="${report.mchtReqNum.unscaledValue()==0}">
									0%
								</c:when>
								<c:otherwise>
									<fmt:formatNumber value="${report.paySuccNum/report.mchtReqNum}" type="percent" maxFractionDigits="2"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td>${report.beginTime}</td>
						<td>${report.endTime}</td>
						<td>${report.statisticsTime}</td>
					</tr>
				</c:forEach>

		</tbody>
		</table>
	</div>
<div class="pagination">${page}</div>
</body>
</html>