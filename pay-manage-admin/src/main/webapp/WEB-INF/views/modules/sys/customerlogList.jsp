<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>会员日志管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
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
    <li class="active"><a href="${ctx}/sys/customerlog/">会员日志列表</a></li>
</ul>
<form:form id="searchForm" action="${ctx}/sys/customerlog/" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <div>
        <label>用户ID：</label><input id="customerId" name="customerId" type="text" maxlength="50" class="input-medium" value="${customerId}"/>
        <label>来源类型：</label>
        <select id="logSource" name="logSource">
            <option value="" />
            <c:forEach items="${sourcelist}" var="sourcelist">
                <option value="${sourcelist.key}" <c:if test="${sourcelist.key==logSource }"> selected="selected" </c:if>>${sourcelist.value}</option>
            </c:forEach>
        </select>
    </div><div style="margin-top:8px;">
    <label>日期范围：&nbsp;</label><input id="beginDate" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                                     value="${beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
    <label>&nbsp;--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label><input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                                                                value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>&nbsp;&nbsp;
    &nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
</div>
</form:form>
<tags:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead><th>操作会员</th><th>操作会员昵称</th><th>日志来源类型</th><th>操作者</th><th>创建时间</th><th>日志描述</th></thead>
    <tbody>
    <c:forEach items="${page.list}" var="customerlog">
        <tr>
            <td>${customerlog.customerId.customerName}</td>
            <td>${customerlog.customerId.nickname}</td>
            <td><strong>${customerlog.logSourceStr}</strong></td>
            <td>${customerlog.createBy.name}</td>
            <td><fmt:formatDate value="${customerlog.createDate}" type="both"/></td>
            <td>${customerlog.log}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>