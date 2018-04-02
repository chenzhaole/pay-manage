<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>调账记录列表</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

    </script>
</head>

<body>


<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/platform/adjust">调账列表</a></li>
    <li><a href="${ctx}/platform/adjust/form">调账添加</a></li>
</ul>
<tags:message content="${message}" type="${messageType}"/>

<form:form id="searchForm" modelAttribute="platAccountAdjust" action="${ctx}/platform/adjust" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${pageInfo.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${pageInfo.pageSize}"/>
    <table>
        <tr>
            <td>
                <label>申请日期：</label>
                <input id="createTime" name="createTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                      value="${createTime}"  onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
            </td>
            <td>
                <label>审批日期：</label>
                <input id="auditTime" name="auditTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                       value="${auditTime}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
            </td>
            <td>
                <label>审核状态：</label>
                <form:select path="auditStatus" >
                    <form:option value=""/>
                    <form:options items="${fns:getDictList('account_adjust_status')}" itemLabel="label" itemValue="value"/>
                </form:select>
            </td>
            <td><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();" style="margin-left: 5px;"></td>
        </tr>
    </table>
</form:form>


<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>调账订单号</th>
        <th>商户名称</th>
        <th>商户号</th>
        <th>调账订单号</th>
        <th>商户名称</th>
        <th>商户号</th>
        <th>账户类型</th>
        <th>调账方向</th>
        <th>申请调账金额／元</th>
        <th>申请调账日期</th>
        <th>申请人</th>
        <th>审批日期</th>
        <th>审批人</th>
        <th>审批状态</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${list}" var="adjust">
        <tr>
            </td>
            <td>${adjust.id}</td>
            <td>${adjust.mchtName}</td>
            <td>${adjust.mchtId}</td>
            <td>${fns:getDictLabel(adjust.accountType,'account_type' ,'' )}</td>
            <td>${fns:getDictLabel(adjust.adjustType,'account_adjust_type' ,'' )}</td>
            <td><fmt:formatNumber type="number" value="${adjust.adjustAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatDate value="${adjust.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>
                ${adjust.creatorName}
            </td>
            <td><fmt:formatDate value="${adjust.auditTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>${adjust.auditorName}</td>
            <td>${fns:getDictLabel(adjust.auditStatus,'account_adjust_status' ,'' )}</td>
            <td>
                <a href="${ctx}/platform/adjust/audit?id=${adjust.id}&auditStatus=4"
                   onclick="return confirmx('确认通过？', this.href)">通过</a>|
                <a href="${ctx}/platform/adjust/audit?id=${adjust.id}&auditStatus=5"
                   onclick="return confirmx('确认拒绝？', this.href)">拒绝</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
