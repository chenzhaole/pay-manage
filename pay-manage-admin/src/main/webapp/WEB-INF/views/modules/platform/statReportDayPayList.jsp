<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ page import="java.util.Date" %>
<html>
<head>
    <title>日报汇总列表</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">

    </style>
    <script type="text/javascript">

        $(document).ready(function () {
            $("#btnExport").click(function () {
                $("#searchForm").attr("action", "${ctx}/platform/statReportDayPay/export");
                $("#searchForm").submit();
                $("#searchForm").attr("action", "${ctx}/platform/statReportDayPay/list");
            });
        });

    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">统计报表</a> > <a href="#"><b>运营日报</b></a></th>
    </label>
</div>
<form id="searchForm" action="${ctx}/platform/statReportDayPay/list" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <input id="paging" name="paging" type="hidden" value="0"/>
    <table>
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">统计日期</label>

                    <div class="controls">
                        <input id="startDate" name="startDate" type="text" readonly="readonly" maxlength="20"
                               class="input-medium Wdate"
                               value="${startDate}"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false,readOnly:true,maxDate:$dp.$('endDate').value,isShowOK:false,isShowToday:false});"/>至

                        <input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20"
                               class="input-medium Wdate"
                               value="${endDate}"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false,readOnly:true,minDate:$dp.$('startDate').value,isShowOK:false,isShowToday:false});"/>
                    </div>
                </div>
            </td>
            <td colspan="2" align="right">
                <div class="btn-group">
                    <input id="btnSubmit" class="btn btn-primary pull-right" type="submit" value="查询">
                </div>
                <div class="btn-group">
                    <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
                </div>

            </td>
        </tr>
    </table>
</form>
<tags:message content="${message}" type="${messageType}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed table-hover"
       style="word-wrap:break-word; word-break:break-all;">
    <thead>
    <tr>
        <th>统计日期</th>
        <th>支付业务交易金额</th>
        <th>支付业务利润</th>
        <th>代付业务交易金额</th>
        <th>代付业务利润</th>
        <th>代付交易笔数</th>
        <th>代付交易利润</th>
        <th>充值业务交易金额</th>
        <th>充值业务利润</th>
        <th>利润合计</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${list}" var="info">
        <tr>
            <td>${info.tradeDate}</td>
            <td><fmt:formatNumber type="number" value="${info.payBizTradeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.payBizProfitAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.dfBizTradeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.dfBizProfitAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.dfTradeCount}" pattern="0" maxFractionDigits="0"/></td>
            <td><fmt:formatNumber type="number" value="${info.dfProfitAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.chongzhiBizTradeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.chongzhiBizProfitAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber type="number" value="${info.totalProfitAmount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>

            <td>
                <a href="${ctx}/platform/statReportDayPayDetail/list?tradeDate=${info.tradeDate}" >详情</a> |
                <a href="${ctx}/platform/statReportDayPay/reStat?tradeDate=${info.tradeDate}" onclick="return confirmx('是否重新统计数据？', this.href)">重新统计</a>
            </td>

        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>