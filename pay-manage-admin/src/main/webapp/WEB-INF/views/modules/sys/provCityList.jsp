<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>省市管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
        $(document).ready(function(){
            $("#isRecommend").val(${paramMap.isRecommend});
        });
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/provCity/">省市列表</a></li>
		<shiro:hasPermission name="sys:provCity:edit"><li><a href="${ctx}/sys/provCity/form">省市添加</a></li></shiro:hasPermission>
	</ul>
	<form id="searchForm" action="${ctx}/sys/provCity/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>省名称：</label><input type="text" name="provinceName" maxlength="50" class="input-medium" value="${paramMap.provinceName}"/>&nbsp;
		<label>市/县名称：</label><input type="text" name="cityName" maxlength="50" class="input-medium" value="${paramMap.cityName}"/>&nbsp;
		<label>是否热门：</label>
		<select id="isRecommend" name="isRecommend">
			<option value="">全部</option>
			<c:forEach items="${fns:getDictList('yes_no')}" var="st">
				<option value="${st.value}">${st.label}</option>
			</c:forEach>
		</select>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
	</form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>省编码</th><th>省名称</th><th>市编码</th><th>市名称</th><th>区/县编码</th><th>区/县名称</th><th>区/县简拼</th><th>区/县全拼</th><th>是否热门</th><shiro:hasPermission name="sys:provCity:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="provCity">
			<tr>
				<td><a href="${ctx}/sys/provCity/form?id=${provCity.id}">${provCity.provinceId}</a></td>
				<td>${provCity.provinceName}</td>
                <td>${provCity.cityId}</td>
                <td>${provCity.cityName}</td>
				<td>${provCity.countyId}</td>
				<td>${provCity.countyName}</td>
				<td>${provCity.pinyinPrefix}</td>
				<td>${provCity.pinyin}</td>
				<td>${fns:getDictLabel(provCity.isRecommend, 'yes_no', '否')}</td>
				<shiro:hasPermission name="sys:provCity:edit"><td>
    				<a href="${ctx}/sys/provCity/form?id=${provCity.id}">修改</a>
					<a href="${ctx}/sys/provCity/delete?id=${provCity.id}" onclick="return confirmx('确认要删除该省市吗？', this.href)" >删除</a>
					<a href="${ctx}/sys/provCity/form?id=${provCity.id}&clone=true">新增</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>