<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商户产品列表</title>
	<meta name="decorator" content="default"/>

	<script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });

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
				<label>商户名称：</label>
				<select name="mchtId" class="selectpicker bla bla bli" data-live-search="true">
					<option value="">--请选择--</option>
					<c:forEach items="${mchtInfos}" var="mchtInfo">
						<option value="${mchtInfo.id}"
								<c:if test="${paramMap.mchtId == mchtInfo.id}">selected</c:if>
						>${mchtInfo.name}</option>
					</c:forEach>
				</select>
			</td>
			<td>
				<label>产品名称：</label>
				<select name="productId" class="selectpicker bla bla bli" data-live-search="true">
					<option value="">--请选择--</option>
					<c:forEach items="${productInfos}" var="productInfo">
						<option value="${productInfo.id}"
								<c:if test="${paramMap.productId == productInfo.id}">selected</c:if>
						>${productInfo.name}</option>
					</c:forEach>
				</select>
			</td>
	 		<td>
				<input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
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
	        	<th>支付类型</th>
	        	<th>产品名称</th>
	        	<th>产品编码</th>
	        	<%--<th>结算方式</th>--%>
	        	<%--<th>结算周期</th>--%>
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
				<td>${productInfo.payType}</td>
				<td>${productInfo.productName}</td>
				<td>${productInfo.productCode}</td>

				<%--<c:choose><c:when test="${productInfo.settleMode == 1}"><td>收单对公</td></c:when>--%>
					<%--<c:when test="${productInfo.settleMode == 2}"><td>收单对私</td></c:when>--%>
					<%--<c:when test="${productInfo.settleMode == 3}"><td>代付</td></c:when>--%>
					<%--<c:when test="${productInfo.settleMode == 4}"><td>银行直清</td></c:when>--%>
					<%--<c:otherwise><td></td></c:otherwise></c:choose>--%>

				<%--<<td>${productInfo.settleCycle}</td>--%>

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