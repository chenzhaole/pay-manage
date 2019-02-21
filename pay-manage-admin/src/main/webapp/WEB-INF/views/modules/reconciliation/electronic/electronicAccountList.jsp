<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电子账户列表</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">

        //下拉搜索框初始化
		$(window).on('load', function () {
			$('.selectpicker').selectpicker({});
		 });

        function add(){
        	document.forms[0].action="${ctx}/electronic/toAddAccount";
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
	<label><a href="#">电子账户管理></a><a href="#"><b>电子账户列表</b></a></label>
</div>

 	<tags:message content="${message}" type="${messageType}"/>
 	
 	<form id="searchForm" action="${ctx}/electronic/list" method="post" class="breadcrumb form-search">
 		<table>
 			<tr>
				<td>
					<label class="control-label">电子账户:</label></span>
					<input name="caElectronicAccount.electronicAccountName" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
						   maxlength="64" value="${electronicAccountVo.caElectronicAccount.id}">
				</td>
				<td>
					<label>商户名称：</label>
					<select name="caElectronicAccount.mchtCode" id="mchtCode"  class="selectpicker" data-live-search="true">
						<option value="">--请选择--</option>
						<c:forEach var="mcht" items="${mchtList}">
							<option value="${mcht.mchtCode}" <c:if test="${electronicAccountVo.caElectronicAccount.mchtCode eq mcht.mchtCode}">selected</c:if> >${mcht.name}</option>
						</c:forEach>
					</select>
				</td>
				<td>
					<label>通道名称：</label>
					<select name="caElectronicAccount.chanCode" class="selectpicker" id="chanCode">
						<option value="">--请选择--</option>
						<c:forEach items="${chanInfos}" var="chanInfo">
							<option data-chanCode="${chanInfo.chanCode }"
									<c:if test="${electronicAccountVo.caElectronicAccount.chanCode eq chanInfo.chanCode}">selected</c:if>
									value="${chanInfo.chanCode}">${chanInfo.name}</option>
						</c:forEach>
					</select>
				</td>
				<td>
					&nbsp;&nbsp;&nbsp;
					<input id="pageNo" name="page.pageNo" type="hidden" value="${page.pageNo}"/>
					<input id="pageSize" name="page.pageSize" type="hidden" value="${page.pageSize}"/>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
					<shiro:hasPermission name="electronic:toAddAccount">
						<input id="clearButton" class="btn btn-primary" type="button" value="新增电子账户" onclick="add()" style="margin-left: 5px;"/>
					</shiro:hasPermission>
				</td>
			</tr>
 		</table>
	</form>
 	
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr >
				<th>电子账户</th>
	        	<th>电子账户名称</th>
				<th>电子账户类型</th>
	        	<th>申报商户名称</th>
				<th>通道名称</th>
	        	<th>状态</th>
	        	<th>创建时间</th>
	        	<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="info">
			<tr>
				<td>${info.id}</td>
				<td>${info.electronicAccountName}</td>
				<td>
					<c:if test="${info.accountType eq '0'}"> 支付电子账户</c:if>
					<c:if test="${info.accountType eq '1'}">充值电子账户</c:if>
				</td>

				<td>
					<c:forEach items="${mchtList}" var="mchtInfo">
						<c:if test="${info.mchtCode eq mchtInfo.mchtCode}">${mchtInfo.name}</c:if>
					</c:forEach>
				</td>
				<td>
					<c:forEach items="${chanInfos}" var="chanInfo">
						<c:if test="${info.chanCode eq chanInfo.chanCode}">${chanInfo.name}</c:if>
					</c:forEach>
				</td>
				<td>
					<c:if test="${info.status eq '1'}"> 启用</c:if>
					<c:if test="${info.status eq '2'}">停用</c:if>
				</td>
				<td>
					<fmt:formatDate value="${info.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<shiro:hasPermission name="electronic:toEditAccount">
						<a href="${ctx}/electronic/toEditAccount?caElectronicAccount.id=${info.id}">修改</a>|
					</shiro:hasPermission>
					<shiro:hasPermission name="electronic:doDeleteAccount">
						<a href="${ctx}/electronic/deleteAccount?caElectronicAccount.id=${info.id}" onclick="return confirmx('是否确认删除此记录？', this.href)">删除</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>