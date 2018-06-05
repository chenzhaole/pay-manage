<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>账务明细列表</title>
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



<tags:message content="${message}" type="${messageType}"/>

<form:form id="searchForm" modelAttribute="mchtAccountDetail" action="${ctx}/merchant/mchtAccountDetail" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${pageInfo.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${pageInfo.pageSize}"/>
    <input id="isSelectInfo" name="isSelectInfo" type="hidden" value="0"/>
    <table>
        <tr>
            <td>
                <label>入账日期：</label>
                <input id="createTime" name="createTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                       value="${createTime}"  onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
            </td>
            <td>
                <label>商户订单号：</label>
                <form:input path="mchtOrderId"/>
            </td>
            <td>
                <label>平台订单号：</label>
                <form:input path="platOrderId"/>
            </td>
        </tr>

        <tr>

            <td>
                <label>账户类型：</label>
                <form:select path="accountType" >
                    <form:option value=""/>
                    <form:options items="${fns:getDictList('account_type')}" itemLabel="label" itemValue="value"/>
                </form:select>
            </td>
            <td>
                <label>记账类型：</label>
                <form:select path="opType" >
                    <form:option value=""/>
                    <form:options items="${fns:getDictList('record_type')}" itemLabel="label" itemValue="value"/>
                </form:select>
            </td>
            <td><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();" style="margin-left: 5px;"></td>
        </tr>

    </table>
</form:form>


<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>账务明细号</th>
        <th>商户名称</th>
        <th>商户号</th>
        <th>商户订单号</th>
        <th>平台订单号</th>
        <th>账户类型</th>
        <th>记账类型</th>
        <th>交易类型</th>
        <th>交易金额（元）</th>
        <th>增加（元）</th>
        <th>减少（元）</th>
        <th>冻结金额（元）</th>
        <th>现金余额（元）</th>
        <th>记账时间</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${list}" var="accountDetail">
        <tr>
            </td>
            <td>${accountDetail.id}</td>
            <td>${accountDetail.mchtName}</td>
            <td>${accountDetail.mchtId}</td>
            <td>${accountDetail.mchtOrderId}</td>
            <td>${accountDetail.platOrderId}</td>
            <td>${fns:getDictLabel(accountDetail.accountType,'account_type' ,'' )}</td>
            <td>${accountDetail.opType}</td>
            <td>${accountDetail.tradeType}</td>
            <td><fmt:formatNumber type="number" value="${accountDetail.tradeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${accountDetail.addAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${accountDetail.reduceAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${accountDetail.freezeTotalAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${accountDetail.cashTotalAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatDate value="${accountDetail.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
