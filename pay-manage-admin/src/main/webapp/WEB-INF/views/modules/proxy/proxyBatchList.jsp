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

    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">代付批次管理</a> > <a href="#"><b>代付批次列表</b></a></th>
    </label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="searchForm" action="${ctx}/proxy/proxyBatchList" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <table>
        <tr>
            <td>
                <label>代付商户：</label>
                <select name="mchtId" class="selectpicker bla bla bli" data-live-search="true">
                    <option value="">--请选择--</option>
                    <c:forEach items="${mchtInfos}" var="mchtInfo">
                        <option value="${mchtInfo.id}"
                                <c:if test="${paramMap.mchtId == mchtInfo.id}">selected</c:if>
                        >${mchtInfo.name}</option>
                    </c:forEach>
                </select>
            </td>

            <td>
                <label>代付状态：</label>
                <select name="payStatus" class="input-medium" id="">
                    <option value="">--请选择--</option>
                    <option <c:if test="${paramMap.payStatus == 1}">selected</c:if> value="1">发起</option>
                    <option <c:if test="${paramMap.payStatus == 2}">selected</c:if> value="2">审核中</option>
                    <option <c:if test="${paramMap.payStatus == 3}">selected</c:if> value="3">代付中</option>
                    <option <c:if test="${paramMap.payStatus == 4}">selected</c:if> value="4">查单中</option>
                    <option <c:if test="${paramMap.payStatus == 5}">selected</c:if> value="5">查单完成</option>
                </select>&nbsp;&nbsp;&nbsp;
            </td>

            <td>
                <label>批次编号：</label>
                <input value="${paramMap.mchtOrderId}" name="mchtOrderId" type="text" maxlength="64" class="input-medium"/>
            </td>

        </tr>
        <tr>
            <td>
                <label>代付通道：</label>
                <select name="batchId" class="selectpicker bla bla bli" data-live-search="true">
                    <option value="">--请选择--</option>
                    <c:forEach items="${chanInfos}" var="chanInfo">
                        <option value="${chanInfo.id}"
                                <c:if test="${paramMap.chanId == chanInfo.id}">selected</c:if>
                        >${chanInfo.name}</option>
                    </c:forEach>
                </select>
            </td>

            <td>
                <label>代付结果：</label>
                <select name="checkStatus" class="input-medium">
    <option value="">--请选择--</option>
    <option <c:if test="${paramMap.checkStatus == 1}">selected</c:if> value="1">成功</option>
    <option <c:if test="${paramMap.checkStatus == 2}">selected</c:if> value="2">失败</option>
    </select>&nbsp;&nbsp;&nbsp;
    </td>

    <td>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
    </td>
    </tr>
</table>
</form>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>NO</th>
        <th>批次号</th>
        <th>来源</th>
        <th>总笔数</th>
        <th>总金额</th>
        <th>手续费</th>
        <th>状态</th>
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
            <td>${proxyBatch.id}</td>
            <td>${proxyBatch.dataSource}</td>
            <td>${proxyBatch.totalNum}</td>
            <td><fmt:formatNumber type="number" value="${proxyDetail.totalAmount*0.01}" pattern="0.0000" maxFractionDigits="4"/></td>
            <td><fmt:formatNumber type="number" value="${proxyDetail.totalFee*0.01}" pattern="0.0000" maxFractionDigits="4"/></td>
            <td><c:if test="${proxyBatch.payStatus == 0}">未处理</c:if>
                <c:if test="${proxyBatch.payStatus == 1}">已处理</c:if>
                <c:if test="${proxyBatch.payStatus == 2}">处理中</c:if>
                <c:if test="${proxyBatch.payStatus == 3}">不能处理</c:if>
                <c:if test="${proxyBatch.payStatus == 4}">部分失败</c:if>
                <c:if test="${proxyBatch.payStatus == 5}">全部失败</c:if>
                <c:if test="${proxyBatch.payStatus == 6}">全部成功</c:if></td>
            <td><fmt:formatDate value="${proxyBatch.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td><fmt:formatDate value="${proxyBatch.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>
                <a href="${ctx}/proxy/proxyDetailList?batchId=${proxyBatch.id}">明细</a>
                    <%--|<a href="${ctx}/platform/deleteCardBin?id=${proxyBatch.id}" onclick="return confirmx('是否确认删除此记录？', this.href)">删除</a>--%>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

</body>
</html>