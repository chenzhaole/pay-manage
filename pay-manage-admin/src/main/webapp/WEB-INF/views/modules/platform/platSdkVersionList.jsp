<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>支付SDK列表</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">
        function add(){
        	document.forms[0].action="${ctx}/platform/editPlatSdkVersion";
        	document.forms[0].submit();
        }

        function del(id){
        	if(confirm("是否确认删除ID为“"+id+"”的记录？")){
        		document.forms[0].action="${ctx}/channel/repaymentDel?id="+id;
        		document.forms[0].submit();
        	}
        }
	   
	</script>
</head>
<body>
<div class="breadcrumb">
	<label><a href="#">支付SDK管理 > </a><a href="#"><b>支付SDK列表</b></a></label> 
	<a style="float:right;cursor: pointer;font-size:15px;text-decoration: none;" href="${ctx}/platform/editPlatSdkVersion/">新增</a>
</div>
 	<form action="">
 	</form>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr >
				<th>NO</th>
	        	<th>版本</th>
	        	<th>说明</th>
	        	<th>是否生效</th>
	        	<th>创建时间</th>
	        	<th>修改时间</th>
	        	<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<%int i=0; %>
		<c:forEach items="${sdkList}" var="sdk">
			<%i++; %>
			<tr>
				<td><%=i%></td>
				<td>${sdk.version}</td>
				<td>${sdk.description}</td>
				<td>
					<c:if test="${sdk.isValid == '1'}">是</c:if>
					<c:if test="${sdk.isValid == '0' }">否</c:if>
				</td>
				<td><fmt:formatDate value="${sdk.createTime}"  pattern="yyyy-MM-dd  HH:mm:ss"/> </td>
				<td><fmt:formatDate value="${sdk.updateTime}"  pattern="yyyy-MM-dd  HH:mm:ss"/></td>
				<td>
					<a href="${ctx}/platform/form?version=${sdk.version}"  style="cursor: pointer;text-decoration: none;">编辑</a>&nbsp;
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
		
</body>
</html>