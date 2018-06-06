<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>代付批次列表</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });

        function del(id) {
            if (confirm("是否确认删除ID为“" + id + "”的记录？")) {
                document.forms[0].action = "${ctx}/proxy/deleteMcht?id=" + id;
                document.forms[0].submit();
            }
        }

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        //显示代付详情信息
        function toShowDetailInfo(platOrderId) {
           var beginDate = $("#beginDate").val();
           var endDate = $("#endDate").val();
           var url = "${ctx}/mchtProxy/proxyDetailList?platBatchId="+platOrderId+"&isSearch=1&beginDate="+beginDate+"&endDate="+endDate;
            location.href = url;
        }


    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">代付批次管理</a> > <a href="#"><b>代付批次列表</b></a></th>
    </label>
</div>

<form id="searchForm" action="${ctx}/mchtProxy/proxyBatchList" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <table>
        <tr>
            <td>
                <div class="control-group">
                    <div class="controls">
                        <label>商户代付批次流水号：</label>
                        <input value="${paramMap.mchtOrderId}" name="mchtOrderId" type="text" maxlength="64" class="input-medium"/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <div class="controls">
                        <label>代付批次状态：</label>
                        <select name="payStatus" class="input-medium" id="">
                            <option <c:if test="${paramMap.payStatus == ''}">selected</c:if> value="">--请选择--</option>
                            <option <c:if test="${paramMap.payStatus == '10'}">selected</c:if> value="10">审核中</option>
                            <option <c:if test="${paramMap.payStatus == '11'}">selected</c:if> value="11">审核通过</option>
                            <option <c:if test="${paramMap.payStatus == '12'}">selected</c:if> value="12">审核未通过</option>
                            <option <c:if test="${paramMap.payStatus == '23'}">selected</c:if> value="23">代付处理中</option>
                            <option <c:if test="${paramMap.payStatus == '24'}">selected</c:if> value="24">代付结束</option>
                        </select>&nbsp;&nbsp;&nbsp;
                    </div>
                </div>
            </td>

        </tr>
        <tr>
            <td style="margin-top: 20px;">
                <div class="control-group">
                    <div class="controls">
                        <label class="control-label">交易时间：</label>
                        <input id="beginDate" name="beginDate" type="text" readonly="readonly" maxlength="20"
                               class="input-medium Wdate"
                               value="${paramMap.beginDate}"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,readOnly:true,maxDate:$dp.$('endDate').value,isShowOK:false,isShowToday:false});"/>至

                        <input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20"
                               class="input-medium Wdate"
                               value="${paramMap.endDate}"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,readOnly:true,minDate:$dp.$('beginDate').value,isShowOK:false,isShowToday:false});"/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <div class="controls">
                        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 25px;">
                        <input id="isSearch" name="isSearch" type="hidden" value="1">
                    </div>
                </div>
            </td>
         </tr>
</table>
</form>

<tags:message content="${message}" type="${messageType}"/>

<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>NO</th>
        <th>商户代付批次流水号</th>
        <th>代付商户</th>
        <th>来源</th>
        <th>总笔数</th>
        <th>总金额(元)</th>
        <th>手续费(元)</th>
        <th>代付批次状态</th>
        <th>创建时间</th>
        <th>更新时间</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <%int i = 0; %>
    <c:forEach items="${page.list}" var="proxyBatch">
        <%i++; %>
        <tr>
            <td><%=i%>
            </td>
            <td>${proxyBatch.mchtOrderId}</td>
            <td>${proxyBatch.mchtId}</td>
            <td><c:if test="${proxyBatch.dataSource == 0}">线上接口</c:if>
                <c:if test="${proxyBatch.dataSource == 1}">线下手工</c:if></td>
            <td>${proxyBatch.totalNum}</td>
            <td><fmt:formatNumber type="number" value="${proxyBatch.totalAmount}" pattern="0.0000" maxFractionDigits="4"/></td>
            <td><fmt:formatNumber type="number" value="${proxyBatch.totalFee}" pattern="0.0000" maxFractionDigits="4"/></td>
            <td>${proxyBatch.payStatus}</td>
            <td><fmt:formatDate value="${proxyBatch.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td><fmt:formatDate value="${proxyBatch.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>
                <a href="javaScript:void(0)" onclick="toShowDetailInfo('${proxyBatch.platOrderId}')">明细</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

</body>
</html>