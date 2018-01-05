<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商户产品列表</title>
	<meta name="decorator" content="default"/>

	<script type="text/javascript">
        function add(){
            document.forms[0].action="${ctx}/platform/addPlatConfMchtProductPage?op=add";
            document.forms[0].submit();
        }

        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

	</script>

</head>
<body>
<div class="breadcrumb">
	<label><a href="#">商户产品管理 > </a><a href="#"><b>商户产品列表</b></a></label>
</div>

 	<tags:message content="${message}" type="${messageType}"/>
 	
 	<form id="searchForm" action="${ctx}/platform/platConfMchtProductList" method="post" class="breadcrumb form-search">
 		<table>
 		<tr>
 			<td>
 				<label>商户名称：</label><input value="${paramMap.mchtName}" name="mchtName" type="text" maxlength="64" class="input-medium"/>
 			</td>
			<td>
				<label>商户编码：</label><input value="${paramMap.mchtCode}" name="mchtCode" type="text" maxlength="64" class="input-medium"/>
			</td>

 			<td>
				<label>产品名称：</label><input value="${paramMap.productName}" name="productName" type="text" maxlength="64" class="input-medium"/>
 			</td>
 			<td>
				<label>产品编码：</label><input value="${paramMap.productCode}" name="productCode" type="text" maxlength="64" class="input-medium"/>
 			</td>
 		<%--</tr>--%>
 		<%--<tr>--%>
			<%--<td>--%>
				<%--<label>结算方式：</label>--%>
				<%--<select name="settleMode" class="input-medium" id="fund_settle_mode">--%>
					<%--<option value="">--请选择--</option>--%>
					<%--<option value="1">收单对公</option>--%>
					<%--<option value="2">收单对私</option>--%>
					<%--<option value="3">代付</option>--%>
					<%--<option value="4">银行直清</option>--%>
				<%--</select>--%>
			<%--</td>--%>
			<%--<td>--%>
				<%--<label>结算周期：</label>--%>
				<%--<select name="settleCycle" class="input-medium" id="fund_settle_day">--%>
					<%--<option value="">--请选择--</option>--%>
					<%--<option value="1">S0</option>--%>
					<%--<option value="2">D0</option>--%>
					<%--<option value="3">T1</option>--%>
				<%--</select>--%>
			<%--</td>--%>
	 		<td>
				<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
				<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
			<input id="clearButton" class="btn btn-primary" type="button" value="新增商户产品" onclick="add()" style="margin-left: 5px;"/>
	 		</td>
 		</tr>
 	</table>
	</form>
 	
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr >
				<th>NO</th>
	        	<th>商户名称</th>
	        	<th>商户编码</th>
	        	<th>产品名称</th>
	        	<th>产品编码</th>
	        	<th>结算方式</th>
	        	<th>结算周期</th>
	        	<th>状态</th>
	        	<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<%int i=0; %>
		<c:forEach items="${page.list}" var="productInfo">
			<%i++; %>
			<tr>
				<td><%=i%></td>
				<td>${productInfo.mchtName}</td>
				<td>${productInfo.mchtCode}</td>
				<td>${productInfo.productName}</td>
				<td>${productInfo.productCode}</td>

				<c:choose><c:when test="${productInfo.settleMode == 1}"><td>收单对公</td></c:when>
					<c:when test="${productInfo.settleMode == 2}"><td>收单对私</td></c:when>
					<c:when test="${productInfo.settleMode == 3}"><td>代付</td></c:when>
					<c:when test="${productInfo.settleMode == 4}"><td>银行直清</td></c:when>
					<c:otherwise><td></td></c:otherwise></c:choose>

				<c:choose><c:when test="${productInfo.settleCycle == 1}"><td>D0</td></c:when>
					<c:when test="${productInfo.settleCycle == 2}"><td>T0</td></c:when>
					<c:when test="${productInfo.settleCycle == 3}"><td>T1</td></c:when>
					<c:otherwise><td></td></c:otherwise></c:choose>

				<c:choose><c:when test="${productInfo.isValid == 1}"><td>启用</td></c:when>
					<c:when test="${productInfo.isValid == 0}"><td>禁用</td></c:when><c:otherwise><td></td></c:otherwise></c:choose>
				<td>
					<a href="${ctx}/platform/addPlatConfMchtProductPage?mchtId=${productInfo.mchtId}&productId=${productInfo.productId}">修改</a>|
					<a href="${ctx}/platform/deleteMchtProduct?mchtId=${productInfo.mchtId}&productId=${productInfo.productId}"
					   onclick="return confirmx('是否确认删除该记录？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>