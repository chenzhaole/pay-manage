]
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>系统参数</title>
    <%@include file="/WEB-INF/views/include/treetable.jsp" %>
    <meta name="decorator" content="default"/>
    <%--<link href="${ctx}/static/modules/cms/front/themes/basic/productDetail.css" type="text/css" rel="stylesheet"/>--%>
</head>
<body>
<ul class="nav nav-tabs _table">
    <li class="active"><a href="${ctx}/sys/config/list">参数列表</a></li>
    <shiro:hasPermission name="sys:config:edit">
        <li><a href="${ctx}/sys/config/form">参数添加</a></li>
    </shiro:hasPermission>
</ul>
<tags:message content="${message}"/>
<div class="tab1" id="tab1">
    <form name="applicationResourceForm" action="${ctx}/sys/config/save" method="post">
        <input type="hidden" name="actionType" value="update"/>
        <input type="hidden" id="categoryId" name="categoryId">

        <table id="contentTable" class="table table-striped table-bordered table-condensed">
            <tr><th>参数描述</th><th>参数名</th><th>参数值</th>
                <shiro:hasPermission name="sys:config:edit">
                <th>操作</th>
                </shiro:hasPermission>
            </tr>
            <c:forEach items="${list}" var="config" varStatus="count">
                <tr>
                    <td width="25%">${config.description}：</td>
                    <td width="25%">${config.configName}</td>
                    <td>
                        ${fns:escapeHtml(config.configValue)}
                    </td>
                    <shiro:hasPermission name="sys:config:edit">
                    <td width="25%">
                        <a href="${ctx}/sys/config/form?configName=${config.configName}">修改</a>
                        &nbsp;&nbsp;|&nbsp;&nbsp;
                        <a href="${ctx}/sys/config/delete?configName=${config.configName}" onclick="return confirmx('确认要删除该配置吗？', this.href)" >删除</a>
                    </td>
                    </shiro:hasPermission>
                </tr>
            </c:forEach>
        </table>
    </form>
</div>
</body>
</html>