<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>调账记录列表</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">
        .wrap{
            width: 100px; //设置需要固定的宽度
        white-space: nowrap; //不换行
        text-overflow: ellipsis; //超出部分用....代替
        overflow: hidden; //超出隐藏
        }
    </style>
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

<shiro:hasPermission name="platform:adjust:apply">
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/platform/adjust">调账列表</a></li>
    <c:if test="${logo == 'apply'}">
        <li><a href="${ctx}/platform/adjust/form">调账添加</a></li>
    </c:if>
</ul>
</shiro:hasPermission>

<tags:message content="${message}" type="${messageType}"/>

<form:form id="searchForm" modelAttribute="platAccountAdjust" action="${ctx}/platform/adjust" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${pageInfo.pageSize}"/>
    <table>
        <tr>
            <td>
                <label>商户名称：</label>
                <select name="mchtId" class="selectpicker bla bla bli" data-live-search="true">
                    <option value="">--请选择--</option>
                    <c:forEach items="${mchtInfos}" var="mchtInfo">
                        <option value="${mchtInfo.id}"
                                <c:if test="${adjustInfo.mchtId == mchtInfo.id}">selected</c:if>
                                >${mchtInfo.name}</option>
                    </c:forEach>
                </select>
            </td>
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
        <th>账户类型</th>
        <th>调账方向</th>
        <th>申请调账金额／元</th>
        <th>申请调账日期</th>
        <th>申请人</th>
        <th>审批日期</th>
        <th>审批人</th>
        <th>审批状态</th>
        <th>备注</th>
        <shiro:hasPermission name="platform:adjust:audit"><th>操作</th></shiro:hasPermission>
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
            <td <div  title="${adjust.remark}" class="wrap">${fn:substring(adjust.remark,0,50)}</div></td>
            <shiro:hasPermission name="platform:adjust:audit">
                <td>
                    <c:if test="${adjust.auditStatus!='4' and adjust.auditStatus!='5'}">
                        <a href="${ctx}/platform/adjust/audit?id=${adjust.id}&auditStatus=4"
                           onclick="return confirmx('确认通过？', this.href)">通过</a>|
                        <a href="${ctx}/platform/adjust/audit?id=${adjust.id}&auditStatus=5"
                           onclick="return confirmx('确认拒绝？', this.href)">拒绝</a>
                    </c:if>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
