<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>还款列表</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">
        function add() {
            document.forms[0].action = "${ctx}/merchant/add";
            document.forms[0].submit();
        }

        function del(id) {
            if (confirm("是否确认删除ID为“" + id + "”的记录？")) {
                document.forms[0].action = "${ctx}/merchant/deleteMcht?id=" + id;
                document.forms[0].submit();
            }
        }

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">商户管理</a> > <a href="#"><b>商户列表</b></a></th>
    </label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="searchForm" action="${ctx}/merchant/list" method="post" class="breadcrumb form-search">
    <label>商户编号：</label><input value="${paramMap.mchtCode}" id="mchtCode" name="mchtCode" type="text" maxlength="64"
                               class="input-medium"/>
    <label>商户名称：</label><input value="${paramMap.mchtName}" id="mchtName" name="mchtName" type="text" maxlength="64"
                               class="input-medium"/>

    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">


</form>

<form id="searchForm" action="${ctx}/merchant/list" method="post" class="breadcrumb form-search">
    <label>商户编号：</label><input value="${paramMap.mchtCode}" id="mchtCode" name="mchtCode" type="text" maxlength="64"
                               class="input-medium"/>
    <label>商户名称：</label><input value="${paramMap.mchtName}" id="mchtName" name="mchtName" type="text" maxlength="64"
                               class="input-medium"/>

    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">


</form>


</body>
</html>