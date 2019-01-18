<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折线图</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/echarts/dist/echarts.js" type="text/javascript"></script>
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

	<!--横坐标-->
	var xdata;
	<!--纵总表-->
	var ydata;
	<!--曲线描述-->
	var xxdata;
	<!-- -->
</head>
<body>
<div class="breadcrumb">
	<label><a href="#">折线图</a><a href="#"><b>折线图</b></a></label>
</div>
 	<form:form id="searchForm" action="${ctx}/platform/original/statistice/chanMchtStatistice"  method="post" class="breadcrumb form-search">
		<table>
			<tr>
				<td>
					<label class="control-label">商户名称：</label>
					<select name="mchtCode" id="mchtCode"  class="selectpicker bla bla bli" data-live-search="true">
						<option value="">---全部---</option>
						<c:forEach var="mcht" items="${mchtList}">
							<option value="${mcht.mchtCode}" <c:if test="${paramMap.mchtCode eq mcht.mchtCode}">selected</c:if> >${mcht.name}</option>
						</c:forEach>
					</select>
					<label class="control-label">通道商户支付方式</label>
					<select name="chanMchtPayTypeId" class="selectpicker bla bla bli" data-live-search="true" >
						<option value="">--全部--</option>
						<c:forEach items="${chanInfoList}" var="chanInfo">
							<option <c:if test="${paramMap.chanMchtPaytypeId == chanInfo.id}">selected</c:if>
									value="${chanInfo.id}">${chanInfo.name}</option>
						</c:forEach>
					</select>

					<label class="control-label">通道名称</label>
					<select name="chanCode" class="input-xlarge" id="chanCode">
						<option value="">--全部--</option>
						<c:forEach items="${chanInfos}" var="chanInfo">
							<option data-chanCode="${chanInfo.chanCode }"
									<c:if test="${paramMap.chanCode == chanInfo.chanCode}">selected</c:if>
									value="${chanInfo.chanCode}">${chanInfo.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label class="control-label">支付方式</label>
					<select name="payType" class="input-xlarge" id="payType">
						<option value="">--全部--</option>
						<c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
							<option
									<c:if test="${paramMap.payType == paymentTypeInfo.code.concat(',').concat(paymentTypeInfo.desc)}">selected</c:if>
									value="${paymentTypeInfo.code},${paymentTypeInfo.desc}">${paymentTypeInfo.desc}</option>
						</c:forEach>
					</select>

					<label>统计时间范围:</label>
					<input type="text" class="input-medium Wdate" name ="beginTime" value="${paramMap.beginTime}"
						   onclick="WdatePicker({dateFmt:'yyyyMMddHHmmss',quickSel:['%y-%M-%d %H-00-00','%y-%M-%d %H-15-00','%y-%M-%d %H-30-00','%y-%M-%d %H-45-00'],isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true});"/>-<input type="text" class="input-medium Wdate" name ="endTime" value="${paramMap.endTime}"
																																																												onclick="WdatePicker({dateFmt:'yyyyMMddHHmmss',quickSel:['%y-%M-%d %H-00-00','%y-%M-%d %H-15-00','%y-%M-%d %H-30-00','%y-%M-%d %H-45-00'],isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true});"/>


					<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
				</td>
			</tr>
		</table>
 	</form:form>
    <div id ="echart"></div>
</body>
</html>