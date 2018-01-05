<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>平台银行列表</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">

        //下拉搜索框初始化
//        $(window).on('load', function () {
//            $('.selectpicker').selectpicker({});
//        });

        function add(){
        	document.forms[0].action="${ctx}/platform/addPlatBankPage";
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
	<label><a href="#">平台银行管理></a><a href="#"><b>平台银行列表</b></a></label>
</div>

 	<tags:message content="${message}" type="${messageType}"/>
 	
 	<form id="searchForm" action="${ctx}/platform/platBankList" method="post" class="breadcrumb form-search">
 		<table>
 			<tr>
				<td>
					<label>银行名称：</label><input value="${paramMap.bankName}" name="bankName" type="text" maxlength="64" class="input-medium"/>
				</td>
 				<td>
					<label>银行代号：</label><input value="${paramMap.bankCode}" name="bankCode" type="text" maxlength="64" class="input-medium"/>
 				</td>
				<td>
					<label>状态：</label>
					<select name="status" type="text" maxlength="64" class="input-medium">
						<option value="">--请选择--</option>
						<option <c:if test="${chanInfo.status == 1}">selected</c:if> value="1">启用</option>
						<option <c:if test="${chanInfo.status == 2}">selected</c:if> value="2">停用</option>
					</select>
 				</td>
 				<td>
					&nbsp;&nbsp;&nbsp;
					<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
					<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
					<input id="clearButton" class="btn btn-primary" type="button" value="新增" onclick="add()" style="margin-left: 5px;"/>
 				</td>
 			</tr>
 		</table>
	</form>
 	
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr >
				<th>NO</th>
	        	<th>银行名称</th>
	        	<th>银行代码</th>
	        	<th>备注</th>
	        	<th>状态</th>
	        	<%--<th>操作</th>--%>
			</tr>
		</thead>
		<tbody>
		<%int i=0; %>
		<c:forEach items="${page.list}" var="platBank">
			<%i++; %>
			<tr>
				<td><%=i%></td>
				<td>${platBank.bankName}</td>
				<td>${platBank.bankCode}</td>
				<td>${platBank.extend}</td>
				<td><c:if test="${platBank.status == 1}">启用</c:if>
					<c:if test="${platBank.status == 2}">停用</c:if>
				<td>
					<a href="${ctx}/platform/addPlatBankPage?id=${platBank.id}">修改</a>
					<%--|<a href="${ctx}/platform/deleteCardBin?id=${platBank.id}" onclick="return confirmx('是否确认删除此记录？', this.href)">删除</a>--%>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>