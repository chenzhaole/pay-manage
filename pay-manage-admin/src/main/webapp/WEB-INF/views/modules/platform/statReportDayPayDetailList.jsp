<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>日报详情列表</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">

    </style>
    <script type="text/javascript">

        $(document).ready(function () {
            $("#btnExportPay").click(function () {
                $("#searchFormPay").attr("action", "${ctx}/platform/statReportDayPayDetail/exportPay");
                $("#searchFormPay").submit();
                $("#searchFormPay").attr("action", "${ctx}/platform/statReportDayPayDetail/list");
            });
        });
        $(document).ready(function () {
            $("#btnExportProxy").click(function () {
                $("#searchFormProxy").attr("action", "${ctx}/platform/statReportDayPayDetail/exportProxy");
                $("#searchFormProxy").submit();
                $("#searchFormProxy").attr("action", "${ctx}/platform/statReportDayPayDetail/list");
            });
        });
    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">统计报表</a> > <a href="#"><b>运营日报</b></a> > <a href="#"><b>日报详情</b></a></th>
    </label>
</div>
<tags:message content="${message}"/>
<form id="searchFormPay" action="${ctx}/platform/statReportDayPayDetail/list" method="post" class="breadcrumb form-search">
    <input  name="tradeDate" type="hidden" value="${tradeDate}"/>
    <input  name="bizType" type="hidden" value="${1}"/>
    <table>
        <tr>
            <td colspan="2">
                <div class="btn-group">
                    <label class="control-label">支付业务  </label>&nbsp;&nbsp;<input id="btnExportPay" class="btn btn-primary" type="button" value="导出"/>
                </div>
            </td>
        </tr>
    </table>
</form>
<table id="contentTable" class="table table-striped table-bordered table-condensed table-hover"
       style="word-wrap:break-word; word-break:break-all;">
    <thead>
    <tr>
        <th>统计日期</th>
        <th>代理商名称</th>
        <th>商户名称</th>
        <th>支付方式</th>
        <th>通道名称</th>
        <th>交易金额(元)</th>
        <th>通道费率(‰)</th>
        <th>商户费率(‰)</th>
        <th>代理商费率(‰)</th>
        <th>代理商分润(元)</th>
        <th>利润(元)</th>
        <th>成功笔数</th>
        <th>交易笔数</th>
        <th>成功率</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${payList}" var="info">
        <tr>
            <td>${info.tradeDate}</td>
            <td>${info.agentMchtName}</td>
            <td>${info.payMchtName}</td>
            <td>${info.payType}</td>
            <td>${info.cmpName}</td>
            <td><fmt:formatNumber type="number" value="${info.tradeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.cmpFeerate*0.001}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.payMchtFeerate*0.001}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.agenMchtFeerate*0.001}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.agentMchtProfitAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.totalProfitAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td>${info.tradeSuccessCount}</td>
            <td>${info.tradeTotalCount}</td>
            <td><fmt:formatNumber type="number" value="${info.tradeSuccessCount/info.tradeTotalCount * 100}" pattern="0.00" maxFractionDigits="2"/>%</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<form id="searchFormProxy" action="${ctx}/platform/statReportDayPayDetail/list" method="post" class="breadcrumb form-search">
    <input  name="tradeDate" type="hidden" value="${tradeDate}"/>
    <input  name="bizType" type="hidden" value="${2}"/>
    <table>
        <tr>
            <td colspan="2">
                <div class="btn-group">
                    <label class="control-label">代付业务  </label>&nbsp;&nbsp;<input id="btnExportProxy" class="btn btn-primary" type="button" value="导出"/>
                </div>
            </td>
        </tr>
    </table>
</form>
<table  class="table table-striped table-bordered table-condensed table-hover"
       style="word-wrap:break-word; word-break:break-all;">
    <thead>
    <tr>
        <th>统计日期</th>
        <th>商户名称</th>
        <th>交易金额(元)</th>
        <th>入账金额(元)</th>
        <th>利润(元)</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${proxyList}" var="info">
        <tr>
            <td>${info.tradeDate}</td>
            <td>${info.payMchtName}</td>
            <td><fmt:formatNumber type="number" value="${info.tradeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.accAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.totalProfitAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>