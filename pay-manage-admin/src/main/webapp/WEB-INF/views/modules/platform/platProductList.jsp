<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>支付产品列表</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">

        //下拉搜索框初始化
		$(window).on('load', function () {
			$('.selectpicker').selectpicker({});
		 });

        function add(){
        	document.forms[0].action="${ctx}/platform/getPayType";
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
	<label><a href="#">支付产品管理></a><a href="#"><b>支付产品列表</b></a></label>
</div>

 	<tags:message content="${message}" type="${messageType}"/>
 	
 	<form id="searchForm" action="${ctx}/platform/platProductList" method="post" class="breadcrumb form-search">
 		<table>
 			<tr>
 				<td>
					<label>产品名称：</label><input value="${paramMap.productName}" name="productName" type="text" maxlength="64" class="input-medium"/>
 				</td>

				<td>
					<label>支付方式：</label>
					<select name="paymentType"  class="selectpicker bla bla bli" data-live-search="true">
						<option value="">--请选择--</option>
						<c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
							<option value="${paymentTypeInfo.code}"
									<c:if test="${paramMap.paymentType == paymentTypeInfo.code}">selected</c:if>
							>${paymentTypeInfo.desc}</option>
						</c:forEach>
					</select>
				</td>

				<td>
					<label>通道商户支付方式：</label>
					<select name="chanMchtPaytypeId"  class="selectpicker bla bla bli" data-live-search="true">
						<option value="">--请选择--</option>
						<c:forEach items="${chanMchtPaytypes}" var="chanMchtPaytype">
							<option value="${chanMchtPaytype.id}"
									<c:if test="${paramMap.chanMchtPaytypeId == chanMchtPaytype.id}">selected</c:if>
							>${chanMchtPaytype.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label>产品编码：</label><input value="${paramMap.code}" name="code" type="text" maxlength="64" class="input-medium"/>
				</td>
				<td>
					<label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;状 态： </label>
					<select  value=""  name="status" type="text" maxlength="64" class="input-medium">
						<option value="">--请选择--</option>
						<option value="1" <c:if test="${paramMap.status == '1'}">selected</c:if> >启用</option>
						<option value="2" <c:if test="${paramMap.status == '2'}">selected</c:if> >停用</option>
						<option value="3" <c:if test="${paramMap.status == '3'}">selected</c:if> >待审核</option>
					</select>
				</td>


 				<td>
					&nbsp;&nbsp;&nbsp;
					<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
					<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
					<input id="clearButton" class="btn btn-primary" type="button" value="新增支付产品" onclick="add()" style="margin-left: 5px;"/>
 				</td>
 			</tr>
 		</table>
	</form>
 	
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr >
				<th>NO</th>
	        	<th>产品名称</th>
	        	<th>产品编码</th>
				<th>支付方式</th>
	        	<th>状态</th>
	        	<th>通道商户支付方式总数</th>
				<th>已禁用通道数</th>
	        	<th>说明</th>
	        	<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<%int i=0; %>
		<c:forEach items="${page.list}" var="productInfo">
			<%i++; %>
			<tr>
				<td><%=i%></td>
				<td>${productInfo.name}</td>
				<td>${productInfo.code}</td>
				<td>${fns:getDictLabel(productInfo.payType, "pay_type","" )}</td>
				<td><c:if test="${productInfo.status == 1}">启用</c:if>
					<c:if test="${productInfo.status == 2}">停用</c:if>
					<c:if test="${productInfo.status == 3}">待审核</c:if></td>
				<td>${productInfo.productRelasSize}</td>
				<td>${productInfo.disableCount}</td>
				<td>${productInfo.extend1}</td>
				<td>
					<a href="${ctx}/platform/addPlatProductPage?id=${productInfo.id}">修改</a>|
					<a href="${ctx}/platform/deletePlatProduct?id=${productInfo.id}&code=${productInfo.code}" onclick="return confirmx('是否确认删除此记录？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>