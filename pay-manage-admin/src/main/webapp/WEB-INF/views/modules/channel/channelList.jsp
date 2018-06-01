<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道列表</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">
        function add(){
        	document.forms[0].action="${ctx}/channel/add";
        	document.forms[0].submit();
        }

        function del(id){
        	if(confirm("是否确认删除通道编号为“"+id+"”的记录？")){
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
	<label><th><a href="#">通道管理</a> > <a href="#"><b>通道列表</b></a></th></label>
</div>

 	<tags:message content="${message}" type="${messageType}"/>
 	
 	<form id="searchForm" action="${ctx}/channel/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>通道编号：</label><input value="${paramMap.chanCode}" name="chanCode" type="text" maxlength="64" class="input-medium"/>
		<label>通道名称：</label><input value="${paramMap.name}" name="name" type="text" maxlength="64" class="input-medium"/>
		&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
		<input id="clearButton" class="btn btn-primary" type="button" value="新增通道" onclick="add()" style="margin-left: 5px;"/>
		</div>
	</form>
 	
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr >
				<th>ID</th>
	        	<th>通道编号</th>
	        	<th>通道名称</th>
	        	<th>业务联系人</th>
	        	<th>业务电话</th>
	        	<th>业务手机</th>
	        	<th>业务邮箱</th>
	        	<th>创建时间</th>
	        	<th>备注</th>
	        	<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<%int i=0; %>
		<c:forEach items="${page.list}" var="chanInfo">
			<%i++; %>
			<tr>
				<td><%=i%></td>
				<td>${chanInfo.chanCode}</td>
				<td>${chanInfo.name}</td>
				<td>${chanInfo.busiContacts}</td>
				<td>${chanInfo.busiPhone}</td>
				<td>${chanInfo.busiMobile}</td>
				<td>${chanInfo.busiEmail}</td>
				<td><fmt:formatDate value="${chanInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${chanInfo.remark}</td>
				<td>
					<a href="${ctx}/channel/edit?id=${chanInfo.id}">修改</a>|
					<a href="${ctx}/channel/deleteChannel?id=${chanInfo.id}" onclick="return confirmx('是否确认删除编号为“${chanInfo.chanCode}”的记录？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>