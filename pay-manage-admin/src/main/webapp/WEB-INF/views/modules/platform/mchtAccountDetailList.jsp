<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
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

        $(document).ready(function() {
            $("#btnExport").click(function(){
                $("#searchForm").attr("action","${ctx}/platform/accountDetail/export");
                $("#searchForm").submit();
                $("#searchForm").attr("action","${ctx}/platform/accountDetail/list");
            });
        });
    </script>
</head>

<body>


<tags:message content="${message}" type="${messageType}"/>

<form:form id="searchForm" modelAttribute="mchtAccountDetail" action="${ctx}/platform/accountDetail/list" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${pageInfo.pageSize}"/>
    <table>
        <tr>
            <td>
                <label>入账日期：</label>
                <%
                    request.setAttribute("createTimeMax", new Date());
                %>

                <input id="createTime" name="createTime" type="text" readonly="readonly" maxlength="20"
                       class="input-medium Wdate"
                       value="${createTime}"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false,readOnly:true,maxDate:$dp.$('createTimeMax').value,isShowOK:false,isShowToday:false});"/>

                <input style="display: none" id="createTimeMax" name="createTimeMax" type="text" readonly="readonly"
                       maxlength="20" class="input-medium Wdate"
                       value="${createTimeMax}"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false,readOnly:true,minDate:$dp.$('createTime').value,isShowOK:false,isShowToday:false});"/>


            </td>
            <td>
                <label>商户名称：</label>
                <form:select path="mchtId">
                    <form:option value="">--全部--</form:option>
                    <form:options items="${mchtInfos}" itemLabel="name" itemValue="id"/>
                </form:select>
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
                <select name="accountType" class="input-xlarge" id="accountType">
                    <option value="">--全部--</option>
                    <option value="2">现金账户</option>
                    <option value="1">结算账户</option>
                </select>
            </td>
            <td>
                <label>记账类型：</label>
                <form:select path="opType">
                    <form:option value="">--全部--</form:option>
                    <form:options items="${fns:getDictList('record_type')}" itemLabel="label" itemValue="value"/>
                </form:select>
            </td>
            <td><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"
                       style="margin-left: 5px;">&nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/></td>

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
        <th>手续费</th>
        <%--<th>冻结金额（元）</th>--%>
        <%--<th>现金余额（元）</th>--%>
        <th>可提现金额（元）</th>
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
            <td><c:if test="${accountDetail.accountType=='2'}">现金账户</c:if>
                <c:if test="${accountDetail.accountType=='1'}">结算账户</c:if></td>
            <td>${accountDetail.opType}</td>
            <td>${accountDetail.tradeType}</td>
            <td><fmt:formatNumber type="number" value="${accountDetail.tradeAmount*0.01}" pattern="0.00"
                                  maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${accountDetail.addAmount*0.01}" pattern="0.00"
                                  maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${accountDetail.reduceAmount*0.01}" pattern="0.00"
                                  maxFractionDigits="2"/></td>
            <td>
                <c:if test="${accountDetail.tradeType == '支付'}">
                    <fmt:formatNumber type="number" value="${accountDetail.tradeAmount*0.01 - accountDetail.addAmount*0.01}" pattern="0.00"
                                      maxFractionDigits="2"/>
                </c:if>
                <c:if test="${accountDetail.tradeType == '代付'}">
                    <fmt:formatNumber type="number" value="${accountDetail.tradeFeeAmount*0.01}" pattern="0.00"
                                      maxFractionDigits="2"/>
                </c:if>
                <c:if test="${accountDetail.tradeType == '调账'}">
                    <fmt:formatNumber type="number" value="${accountDetail.tradeFeeAmount*0.01}" pattern="0.00"
                                      maxFractionDigits="2"/>
                </c:if>
                <c:if test="${accountDetail.tradeType == '充值'}">
                    <fmt:formatNumber type="number" value="${accountDetail.tradeFeeAmount*0.01}" pattern="0.00"
                                      maxFractionDigits="2"/>
                </c:if>
            </td>
            <%--<td><fmt:formatNumber type="number" value="${accountDetail.freezeTotalAmount*0.01}" pattern="0.00"
                                  maxFractionDigits="2"/></td>--%>
            <%--<td><fmt:formatNumber type="number" value="${accountDetail.cashTotalAmount*0.01}" pattern="0.00"
                                  maxFractionDigits="2"/></td>--%>
            <td><fmt:formatNumber type="number"
                                  value="${accountDetail.cashTotalAmount*0.01 - accountDetail.freezeTotalAmount*0.01}"
                                  pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatDate value="${accountDetail.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
