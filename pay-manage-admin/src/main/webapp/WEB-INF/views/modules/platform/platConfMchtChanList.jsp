<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商户通道列表</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">
        function add(){
        	document.forms[0].action="${ctx}/platform/editPlatConfMchtChan";
        	document.forms[0].submit();
        }

        function del(id){
        	if(confirm("是否确认删除ID为“"+id+"”的记录？")){
        		document.forms[0].action="${ctx}/channel/repaymentDel?id="+id;
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
	<label><a href="#">商户通道管理 > </a><a href="#"><b>商户通道列表</b></a></label>
</div>

	<tags:message content="${message}" type="${messageType}"/>

 	<form id="searchForm" action="${ctx}/platform/platConfMchtChanList" method="post" class="breadcrumb form-search">
		<label>商户名称：</label><input value="${paramMap.mchtName}" name="mchtName" type="text" maxlength="64" class="input-medium"/>
		<label>商户编码：</label><input value="${paramMap.mchtCode}" name="mchtCode" type="text" maxlength="64" class="input-medium"/>
		&nbsp;&nbsp;&nbsp;
		<input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
<!-- 		<input id="clearButton" class="btn btn-primary" type="button" value="新增商户通道" onclick="add()" style="margin-left: 5px;"/> -->
	</form>
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr >
				<th>NO</th>
	        	<th>商户名称</th>
	        	<th>商户编码</th>
	        	<th>商户状态</th>
	        	<th>通道总数</th>
				<th>禁用通道数</th>
	        	<th>说明</th>
	        	<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<%int i=0; %>
		<c:forEach items="${page.list}" var="chanInfo">
			<%i++; %>
			<tr>
				<td><%=i%></td>
				<td>${chanInfo.mchtName}</td>
				<td>${chanInfo.mchtCode}</td>
				<c:choose><c:when test="${chanInfo.mchtStatus == 1}"><td>启用</td></c:when>
					<c:when test="${chanInfo.mchtStatus == 0}"><td>禁用</td></c:when><c:otherwise><td></td></c:otherwise></c:choose>

				<td>${chanInfo.chanCount}</td>
				<td>${chanInfo.disableCount}</td>
				<td>${chanInfo.mchtDesc}</td>
				<td>
					<shiro:hasPermission name="platform:editPlatConfMchtChan">
						<a href="${ctx}/platform/editPlatConfMchtChanPage?mchtId=${chanInfo.mchtId}">修改</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>