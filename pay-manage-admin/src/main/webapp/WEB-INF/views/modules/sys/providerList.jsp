<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分平台/提供商管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
        $(document).ready(function(){
            $("#status").val(${paramMap.status});
        });
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/provider/">分平台/提供商列表</a></li>
		<shiro:hasPermission name="sys:provider:edit"><li><a href="${ctx}/sys/provider/form">分平台/提供商添加</a></li></shiro:hasPermission>
	</ul>
	<form id="searchForm" action="${ctx}/sys/provider/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>名称：</label><input type="text" name="providerName" maxlength="50" class="input-medium" value="${paramMap.providerName}"/>&nbsp;
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
	</form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>编号</th><th>名称</th><th>对外显示名称</th><th>订单来源ID</th><th>支付方式</th><th>是否开通</th><th>开通时间</th><shiro:hasPermission name="sys:provider:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="provider">
			<tr>
				<td><a href="${ctx}/sys/provider/form?id=${provider.id}">${provider.providerId}</a></td>
				<td>${provider.providerName}</td>
                <td>${provider.providerDisplayName}</td>
                <td>${provider.tckIdent}</td>
				<td>${fns:getDictLabel(provider.payType, 'provider_pay_type', '未开通')}</td>
				<td>${fns:getDictLabel(provider.isOpen, 'provider_open_flag', '未开通')}</td>
				<td><fmt:formatDate value="${provider.openDate}" type="both"/></td>
				<shiro:hasPermission name="sys:provider:edit"><td>
    				<a href="${ctx}/sys/provider/form?id=${provider.id}">修改</a>
					<a href="${ctx}/sys/provider/delete?id=${provider.id}" onclick="return confirmx('确认要删除该分平台/提供商吗？', this.href)" >删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>