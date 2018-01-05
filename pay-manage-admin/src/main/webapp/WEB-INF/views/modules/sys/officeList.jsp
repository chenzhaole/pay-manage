<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微门户管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 5});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/office/">微门户列表</a></li>
		<shiro:hasPermission name="sys:office:edit"><li><a href="${ctx}/sys/office/form">微门户添加</a></li></shiro:hasPermission>
	</ul>
    <tags:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<tr><th>微门户名称</th><th>归属区域</th><th>微门户类型</th><th>星级</th><th>微门户状态</th><th>备注</th><shiro:hasPermission name="sys:office:edit"><th>操作</th></shiro:hasPermission></tr>
		<c:forEach items="${list}" var="office">
			<tr id="${office.id}" pId="${office.parent.id ne requestScope.office.id?office.parent.id:'0'}">
				<td><a href="${ctx}/sys/office/form?id=${office.id}">${office.name}</a></td>
				<td>${office.area.name}</td>
                <td>${fns:getDictLabel(office.portalInfo.type, 'portal_type', '无')}</td>
				<td>${fns:getDictLabel(office.portalInfo.starLevel, 'portal_star_level', '无')}</td>
				<td>${fns:getDictLabel(office.portalInfo.status, 'portal_status', '无')}</td>
				<td>${office.remarks}</td>
				<shiro:hasPermission name="sys:office:edit"><td>
					<a href="${ctx}/sys/office/form?id=${office.id}">修改</a>
					<%--<a href="${ctx}/sys/office/delete?id=${office.id}" onclick="return confirmx('要删除该微门户及所有子微门户项吗？', this.href)">删除</a>&nbsp;|&nbsp;--%>
					<%--<a href="${ctx}/sys/office/form?parent.id=${office.id}">添加下级微门户</a>&nbsp;|&nbsp;--%>
					<c:if test="${office.portalInfo.status eq '0'}">
						&nbsp;|&nbsp;<a href="${ctx}/sys/office/changeStatus?id=${office.id}" onclick="return confirmx('要冻结该微门户吗？', this.href)">冻结</a>
					</c:if>
					<c:if test="${office.portalInfo.status eq '1'}">
						&nbsp;|&nbsp;<a href="${ctx}/sys/office/changeStatus?id=${office.id}" onclick="return confirmx('要解冻该微门户吗？', this.href)">解冻</a>
					</c:if>
					<c:if test="${office.portalInfo.status eq '2'}">
                        &nbsp;|&nbsp;<a href="${ctx}/sys/office/checkPage?id=${office.id}">审核</a>
                    </c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
	</table>
</body>
</html>