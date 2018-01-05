<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>还款列表</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">
        function add(){
        	document.forms[0].action="${ctx}/merchant/add";
        	document.forms[0].submit();
        }

        function del(id){
        	if(confirm("是否确认删除ID为“"+id+"”的记录？")){
        		document.forms[0].action="${ctx}/merchant/deleteMcht?id="+id;
        		document.forms[0].submit();
        	}
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
	<label><th><a href="#">商户管理</a> > <a href="#"><b>商户列表</b></a></th></label>
</div>

 	<tags:message content="${message}" type="${messageType}"/>
 	
 	<form id="searchForm" action="${ctx}/merchant/list" method="post" class="breadcrumb form-search">
	    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>商户编号：</label><input value="${paramMap.mchtCode}" id="mchtCode" name="mchtCode" type="text" maxlength="64" class="input-medium"/>
		<label>商户名称：</label><input value="${paramMap.mchtName}" id="mchtName" name="mchtName" type="text" maxlength="64" class="input-medium"/>
		<label>联系人手机：</label><input value="${paramMap.serviceMobile}" id="serviceMobile" name="serviceMobile" type="text" class="input-medium"/>
		<label>商户类别：</label>
		<select name="signType" class="input-medium" id="fund_settle_mode">
			<option value="">--请选择--</option>
			<option value="1">支付商户</option>
			<option value="2">申报商户</option>
			<option value="3">服务商 </option>
			<option value="4">代理商 </option>
		</select>&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
		<input id="clearButton" class="btn btn-primary" type="button" value="新增商户" onclick="add()" style="margin-left: 5px;"/>
		</div>
	</form>
 	
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr >
				<th>ID</th>
	        	<th>名称</th>
	        	<th>商户编号</th>
	        	<th>商户类别</th>
	        	<th>联系人名称</th>
	        	<th>联系人手机</th>
	        	<th>创建时间</th>
				<th>创建人</th>
				<th>商户状态</th>
	        	<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<%int i=0; %>
		<c:forEach items="${page.list}" var="mcht">
			<%i++; %>
			<tr>
				<td><%=i%></td>
				<td>${mcht.name}</td>    
				<td>
					${mcht.mchtCode}
				</td>
				<td><c:if test="${fn:contains(mcht.signType, '1')}">支付商户 </c:if>
					<c:if test="${fn:contains(mcht.signType, '2')}">申报商户 </c:if>
					<c:if test="${fn:contains(mcht.signType, '3')}">服务商 </c:if>
					<c:if test="${fn:contains(mcht.signType, '4')}">代理商</c:if></td>
				<td>${mcht.contactName}</td>
				<td>${mcht.serviceMobile}</td>
				<td><fmt:formatDate value="${mcht.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${mcht.operatorName}</td>
				<td><c:if test="${mcht.status == 1}">启用</c:if>
					<c:if test="${mcht.status == 2}">停用</c:if>
					<c:if test="${mcht.status == 3}">待审核</c:if></td>
				<td>
					<a href="${ctx}/merchant/edit?id=${mcht.id}">修改</a>|
					<a href="${ctx}/merchant/deleteMcht?id=${mcht.id}&mchtCode=${mcht.mchtCode}" onclick="return confirmx('是否删除编号为“${mcht.mchtCode}”的记录？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
<div class="pagination">${page}</div>
		
</body>
</html>