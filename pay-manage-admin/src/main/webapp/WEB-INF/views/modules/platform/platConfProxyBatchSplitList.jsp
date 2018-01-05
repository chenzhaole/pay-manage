<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>批量拆包列表</title>
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
	<label><a href="#">商户批量拆包管理 > </a><a href="#"><b>商户批量拆包列表</b></a></label>
</div>

 	<tags:message content="${message}" type="${messageType}"/>
 	
 	
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
		<tr>
			<td>
				1
			</td>
			<td>
				v.2.0.1
			</td>
			<td>
				xxxxxxx
			</td>
			<td>
				是
			</td>
			<td>
				xxxxxxxx
			</td>
			<td>
				xxxxxxxx
			</td>
			<td>
				<a href="${ctx}/platform/editPlatConfProxyBatchSplit" style="text-decoration: none;">编辑</a>&nbsp;
				<a style="cursor: pointer;text-decoration: none;" >删除</a>&nbsp;&nbsp;
				<a style="cursor: pointer;text-decoration: none;">查看</a>
			</td>
		</tr>
		<%int i=0; %>
		<c:forEach items="${list}" var="chanInfo">
			<%i++; %>
			<tr>
				<td><%=i%></td>
				<td><a href="${ctx}/channel/chanInfoEdit?chanInfoNo=${chanInfo.id}">${chanInfo.chanNo}</a></td>
				<td><a href="${ctx}/channel/detailByNo?chanInfoNo=${chanInfo.id}">${chanInfo.name}</a></td>
				<td>${chanInfo.busiContacts}</td>
				<td>${chanInfo.busiPhone}</td>
				<td>${chanInfo.busiMobile}</td>
				<td>${chanInfo.busiEmail}</td>
				<td>${chanInfo.createDate}</td>
				<td>${chanInfo.remark}</td>
				<td>
					<a href="${ctx}/channel/edit?id=${chanInfo.id}">修改</a>|
					<a href="${ctx}/channel/chanInfoDel?id=${chanInfo.id}" onclick="return confirmx('是否确认删除ID为“${repayment.id}”的记录？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
		
</body>
</html>