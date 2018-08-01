<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>商户日报详情列表</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">

    </style>
    <script type="text/javascript">

        $(document).ready(function () {
            $("#btnExportPay").click(function () {
                $("#searchFormPay").attr("action", "${ctx}/merchant/statReport/exportPay");
                $("#searchFormPay").submit();
                $("#searchFormPay").attr("action", "${ctx}/merchant/statReport/detailList");
            });
        });
        $(document).ready(function () {
            $("#btnExportProxy").click(function () {
                $("#searchFormProxy").attr("action", "${ctx}/merchant/statReport/exportProxy");
                $("#searchFormProxy").submit();
                $("#searchFormProxy").attr("action", "${ctx}/merchant/statReport/detailList");
            });
        });
    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">统计报表</a> > <a href="#"><b>商户日报</b></a> > <a href="#"><b>日报详情</b></a></th>
    </label>
</div>
<tags:message content="${message}"/>
<form id="searchFormPay" action="${ctx}/merchant/statReport/detailList" method="post" class="breadcrumb form-search">
    <input  name="tradeDate" type="hidden" value="${tradeDate}"/>
    <input  name="bizType" type="hidden" value="${4}"/>
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
        <th>支付方式</th>
        <th>交易金额(元)</th>
        <th>手续费(元)</th>
        <th>结算金额(元)</th>

    </tr>
    </thead>
    <tbody>
    <c:forEach items="${payList}" var="info">
        <tr>
            <td>${info.tradeDate}</td>
            <td>${info.payType}</td>
            <td><fmt:formatNumber type="number" value="${info.tradeAmount * 0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.totalProfitAmount* 0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.tradeAmount * 0.01  - info.totalProfitAmount* 0.01}" pattern="0.00" maxFractionDigits="2"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<form id="searchFormProxy" action="${ctx}/merchant/statReport/detailList" method="post" class="breadcrumb form-search">
    <input  name="tradeDate" type="hidden" value="${tradeDate}"/>
    <input  name="bizType" type="hidden" value="${5}"/>
    <table>
        <tr>
            <td colspan="2">
                <div class="btn-group">
                    <label class="control-label">代付交易  </label>&nbsp;&nbsp;<input id="btnExportProxy" class="btn btn-primary" type="button" value="导出"/>
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
        <th>代付成功笔数</th>
        <th>代付成功金额</th>
        <th>代付手续费</th>
        <th>代付失败笔数</th>
        <th>代付失败金额</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${proxyList}" var="info">
        <tr>
            <td>${info.tradeDate}</td>
            <td>${info.tradeSuccessCount}</td>
            <td><fmt:formatNumber type="number" value="${info.tradeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.totalProfitAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.tradeFailCount}"/></td>
            <td><fmt:formatNumber type="number" value="${info.tradeFailAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>