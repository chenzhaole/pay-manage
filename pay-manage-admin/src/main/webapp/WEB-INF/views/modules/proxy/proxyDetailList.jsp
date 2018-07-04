<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>

<head>
    <title>代付明细列表</title>
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
        <th><a href="#">代付明细管理</a> > <a href="#"><b>代付明细列表</b></a></th>
    </label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="searchForm" action="${ctx}/proxy/proxyDetailList" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <input id="batchId" name="batchId" type="hidden" value="${proxyBatch.id}"/>
    <table>
        <tr>
            <td>
                <label>平台明细订单号：</label>
                <input value="${paramMap.detailId}" name="detailId" type="text" maxlength="64" class="input-medium"/>
            </td>

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

        </tr>
        <tr>
            <td>
                <label>代付状态：</label>
                <select name="payStatus" class="input-medium" id="">
                    <option value="">--请选择--</option>
                    <c:forEach var="ps" items="${fns:getDictList('proxypay_detail_status')}">
                        <option value="${ps.value}" <c:if test="${paramMap.payStatus == ps.value}">selected</c:if>>${ps.label}</option>
                    </c:forEach>
                </select>&nbsp;&nbsp;&nbsp;
            </td>
            <td>
                <label>代付通道：</label>
                <select name="chanId" class="selectpicker bla bla bli" data-live-search="true">
                    <option value="">--请选择--</option>
                    <c:forEach items="${chanInfos}" var="chanInfo">
                        <option value="${chanInfo.id}"
                                <c:if test="${paramMap.chanId == chanInfo.id}">selected</c:if>
                                >${chanInfo.name}</option>
                    </c:forEach>
                </select>
            </td>
            <td><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;"></td>

            <%--<td>
                <label>代付结果：</label>
                <select name="checkStatus" class="input-medium">
                    <option value="">--请选择--</option>
                    <option <c:if test="${paramMap.checkStatus == 1}">selected</c:if> value="1">成功</option>
                    <option <c:if test="${paramMap.checkStatus == 2}">selected</c:if> value="2">失败</option>
                </select>&nbsp;&nbsp;&nbsp;
            </td>--%>


    </tr>
</table>
</form>

<c:if test="${proxyBatch != null}">

    <table id="infoTable" class="table table-striped table-bordered table-condensed">
        <tbody>
        <tr>
            <td colspan="4" style="text-align: center;"><b>批次信息</b></td>
        </tr>
        <tr>
            <td width="25%">平台批次：</td>
            <td width="25%">${proxyBatch.platOrderId}</td>
            <td width="25%">商户批次：</td>
            <td width="25%">${proxyBatch.mchtOrderId} </td>
        </tr>
        <tr>
            <td width="25%">商户名称：</td>
            <td width="25%">${proxyBatch.extend3}</td>
            <td width="25%">商户编号：</td>
            <td width="25%">${proxyBatch.mchtId} </td>
        </tr>
        <tr>
            <td width="25%">成功笔数：</td>
            <td width="25%">${proxyBatch.successNum}</td>
            <td width="25%">成功金额：</td>
            <td width="25%"><fmt:formatNumber type="number" value="${proxyBatch.successAmount*0.01}" pattern="0.0000" maxFractionDigits="4"/> </td>
        </tr>
        <tr>
            <td width="25%">失败笔数：</td>
            <td width="25%">${proxyBatch.failNum}</td>
            <td width="25%">失败金额：</td>
            <td width="25%"><fmt:formatNumber type="number" value="${proxyBatch.failAmount*0.01}" pattern="0.0000" maxFractionDigits="4"/> </td>
        </tr>
        <tr>
            <td width="25%">商户费用：</td>
            <td width="25%"><fmt:formatNumber type="number" value="${proxyBatch.totalFee*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td width="25%">代付状态：</td>
            <td width="25%">
                <c:if test="${proxyBatch.payStatus == 10}">审核中</c:if>
                <c:if test="${proxyBatch.payStatus == 11}">审核通过</c:if>
                <c:if test="${proxyBatch.payStatus == 12}">审核未通过</c:if>
                <c:if test="${proxyBatch.payStatus == 23}">代付处理中</c:if>
                <c:if test="${proxyBatch.payStatus == 24}">代付结束</c:if>
            </td>
        </tr>
        </tbody>
    </table>

</c:if>
<div class="breadcrumb">
    <label>
        <th>明细信息</th>
    </label>
</div>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <%--<th>NO</th>--%>
        <th>商户名称</th>
        <th>平台批次订单号<br>平台明细订单号</th>
        <th>商户订单号</th>
        <th>批次内序号</th>
        <th>收款户名</th>
        <th>平台银行名称<br>平台银行编码</th>
        <th>收款账号</th>
        <%--<th>收款银行名称</th>--%>
        <th>金额（元）</th>
        <th>手续费（元）</th>
        <th>状态</th>
        <th>通道名称</th>
        <th width="100px">上游响应</th>
        <th>创建时间<br>更新时间</th>
        <th >操作</th>
    </tr>
    </thead>
    <tbody>
    <%--<%int i = 0; %>--%>
    <c:forEach items="${page.list}" var="proxyDetail">
        <%--<%i++; %>--%>
        <tr>
            <%--<td><%=i%>--%>
            <td>${proxyDetail.extend2}</td>
            <td>${proxyDetail.platBatchId}<br>${proxyDetail.id}</td>
            <td>${proxyDetail.mchtBatchId}</td>
            <td>${proxyDetail.mchtSeq}</td>
            <td>${proxyDetail.bankCardName}</td>
            <td>${proxyDetail.bankName}<br>${proxyDetail.bankCode}</td>
            <td>${proxyDetail.bankCardNo}</td>
            <%--<td>${proxyDetail.bankName}</td>--%>
            <td><fmt:formatNumber type="number" value="${proxyDetail.amount*0.01}" pattern="0.0000" maxFractionDigits="4"/></td>
            <td><fmt:formatNumber type="number" value="${proxyDetail.mchtFee*0.01}" pattern="0.0000" maxFractionDigits="4"/></td>
            <td>
                ${fns:getDictLabel(proxyDetail.payStatus,'proxypay_detail_status' ,'' )}
            </td>
                <td>${proxyDetail.extend3}</td>

                <td><div  title="${proxyDetail.returnMessage2}" class="wrap">${fn:substring(proxyDetail.returnMessage2,0,50)} </div></td>
                <td><fmt:formatDate value="${proxyDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/><br><fmt:formatDate value="${proxyDetail.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>
                <a href="${ctx}/proxy/proxyDetail?detailId=${proxyDetail.id}">详情</a>
                    <%--|<a href="${ctx}/platform/deleteCardBin?id=${proxyDetail.id}" onclick="return confirmx('是否确认删除此记录？', this.href)">删除</a>--%>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

</body>
</html>