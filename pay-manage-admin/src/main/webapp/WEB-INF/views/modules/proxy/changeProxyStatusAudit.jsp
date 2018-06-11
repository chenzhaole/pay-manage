<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>代付审批列表</title>
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
        <th><a href="#">代付管理</a> > <a href="#"><b>代付审批列表</b></a></th>
    </label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="searchForm" action="${ctx}/proxy/changeProxyStatusAudit" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <input id="batchId" name="batchId" type="hidden" value="${proxyBatch.id}"/>
    <table>
        <tr>
            <td>
                <label>明细订单号：</label>
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


        </tr>
        <tr>
            <td>
                <label>代付状态：</label>
                <select name="payStatus" class="input-medium" id="">
                    <option value="">--请选择--</option>
                    <c:forEach var="ps" items="${fns:getDictList('proxypay_detail_status')}">
                        <option value="${ps.value}"
                                <c:if test="${paramMap.payStatus == ps.value}">selected</c:if>>${ps.label}</option>
                    </c:forEach>
                </select>&nbsp;&nbsp;&nbsp;
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
    <div class="breadcrumb">
        <label>
            <th>批次信息</th>
        </label>
    </div>
    <table>
        <tr>

            <td>
                <label>平台批次：</label>
                <textarea readonly class="input-small">${proxyBatch.mchtOrderId}</textarea>
            </td>


            <td>
                <label>商户批次：</label>
                <textarea readonly class="input-small">${proxyBatch.platOrderId}</textarea>
                    <%--<input value="${proxyBatch.platOrderId}" type="text" class="input-small" readonly />--%>
            </td>
            <td>
                <label>商户名称：</label>
                <input value="${proxyBatch.extend3}" type="text" class="input-small" readonly/>
            </td>

            <td>
                <label>商户编号：</label>
                <input value="${proxyBatch.mchtId}" type="text" class="input-small" readonly/>
            </td>

        </tr>

        <tr>
            <td>
                <label>成功笔数：</label>
                <input value="${proxyBatch.successNum}" type="text" class="input-small" readonly/>
            </td>

            <td>
                <label>成功金额：</label>
                <input value="<fmt:formatNumber type="number" value="${proxyBatch.successAmount*0.01}" pattern="0.0000" maxFractionDigits="4"/>"
                       type="text" class="input-small" readonly/>
            </td>
            <td>
                <label>失败笔数：</label>
                <input value="${proxyBatch.failNum}" type="text" class="input-small" readonly/>
            </td>


            <td>
                <label>失败金额：</label>
                <input value="<fmt:formatNumber type="number" value="${proxyBatch.failAmount*0.01}" pattern="0.0000" maxFractionDigits="4"/>"
                       type="text" class="input-small" readonly/>
            </td>

        </tr>
        <tr>

            <td>
                <label>通道名称：</label>
                <input value="${proxyBatch.chanId}" type="text" class="input-small" readonly/>
            </td>

            <td>
                <label>商户费用：</label>
                <input value="<fmt:formatNumber type="number" value="${proxyBatch.totalFee*0.01}" pattern="0.00" maxFractionDigits="2"/>"
                       type="text" class="input-small" readonly/>
            </td>
            <td>
                <label>代付状态：</label>
                <c:if test="${proxyBatch.payStatus == 10}"><input value="审核中" type="text" class="input-small"
                                                                  readonly/></c:if>
                <c:if test="${proxyBatch.payStatus == 11}"><input value="审核通过" type="text" class="input-small"
                                                                  readonly/></c:if>
                <c:if test="${proxyBatch.payStatus == 12}"><input value="审核未通过" type="text" class="input-small"
                                                                  readonly/></c:if>
                <c:if test="${proxyBatch.payStatus == 23}"><input value="代付处理中" type="text" class="input-small"
                                                                  readonly/></c:if>
                <c:if test="${proxyBatch.payStatus == 24}"><input value="代付结束" type="text" class="input-small"
                                                                  readonly/></c:if>
            </td>

        </tr>


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
        <th>批次订单号</th>
        <th>明细订单号</th>
        <th>代付商户</th>
        <th>代付通道</th>
        <th>收款户名</th>
        <th>收款账号</th>
        <%--<th>收款银行名称</th>--%>
        <th>金额（元）</th>
        <th>手续费（元）</th>
        <th>原始状态</th>
        <th>新状态</th>
        <th>上游响应</th>
        <th>创建时间</th>
        <th>更新时间</th>
        <th>申请人</th>
        <th>审批人</th>
        <th>审批状态</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <%--<%int i = 0; %>--%>
    <c:forEach items="${page.list}" var="proxyDetail">
        <%--<%i++; %>--%>
        <tr>
                <%--<td><%=i%></td>--%>
            <td>${proxyDetail.platBatchId}</td>
            <td>${proxyDetail.id}</td>
            <td>${proxyDetail.mchtId}</td>
            <td>${proxyDetail.chanId}</td>
            <td>${proxyDetail.bankCardName}</td>
            <td>${proxyDetail.bankCardNo}</td>
                <%--<td>${proxyDetail.bankName}</td>--%>
            <td><fmt:formatNumber type="number" value="${proxyDetail.amount*0.01}" pattern="0.0000"
                                  maxFractionDigits="4"/></td>
            <td><fmt:formatNumber type="number" value="${proxyDetail.mchtFee*0.01}" pattern="0.0000"
                                  maxFractionDigits="4"/></td>
            <td>
                    ${fns:getDictLabel(proxyDetail.payStatus,'proxypay_detail_status' ,'' )}
            </td>
            <td>
                    ${fns:getDictLabel(proxyDetail.newPayStatus,'proxypay_detail_status' ,'' )}
            </td>
            <td>${proxyDetail.returnMessage2}</td>
            <td><fmt:formatDate value="${proxyDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td><fmt:formatDate value="${proxyDetail.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>${proxyDetail.operatorId}</td>
            <td>${proxyDetail.auditorName}</td>
            <td>${proxyDetail.auditStatus}</td>
            <td>
                <a href="${ctx}/proxy/changeProxyStatusAuditEdit?detailId=${proxyDetail.id}">审批</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

</body>
</html>