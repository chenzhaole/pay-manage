<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>代付明细列表</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });

        <%--function del(id) {--%>
        <%--if (confirm("是否确认删除ID为“" + id + "”的记录？")) {--%>
        <%--document.forms[0].action = "${ctx}/proxy/deleteMcht?id=" + id;--%>
        <%--document.forms[0].submit();--%>
        <%--}--%>
        <%--}--%>

        $(document).ready(function() {
            $("#btnExport").click(function(){
                $("#searchForm").attr("action","${ctx}/mchtProxy/export");
                $("#searchForm").submit();
                $("#searchForm").attr("action","${ctx}/mchtProxy/proxyDetailList");
            });
        });

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        //返回到代付批次页面
        function goBackProxyBatchPage() {
            var beginDate = $("#beginDate").val();
            var endDate = $("#endDate").val();
            var url = "${ctx}/mchtProxy/proxyBatchList?1=1&isSearch=1&beginDate=" + beginDate + "&endDate=" + endDate;
            location.href = url;
        }
    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">代付明细管理</a> > <a href="#"><b>代付明细列表</b></a></th>
    </label>
</div>


<form id="searchForm" action="${ctx}/mchtProxy/proxyDetailList" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <input id="batchId" name="batchId" type="hidden" value="${proxyBatch.id}"/>
    <table>
        <tr>
            <%-- <td>
                 <div class="control-group">
                     <div class="controls">
                         <label>商户代付明细订单号：</label>
                         <input value="${paramMap.mchtSeq}" name="mchtSeq" type="text" maxlength="64" class="input-medium"/>
                     </div>
                 </div>
             </td>--%>
            <td>
                <div class="control-group">
                    <div class="controls">
                        <label>平台明细订单号：</label>
                        <input value="${paramMap.id}" name="id" type="text" maxlength="64" class="input-medium"/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <div class="controls">
                        <label>代付明细状态：</label>
                        <select name="payStatus" class="input-medium" id="payStatus">
                            <option
                                    <c:if test="${paramMap.payStatus == ''}">selected</c:if> value="">--请选择--
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '10'}">selected</c:if> value="10">审核中
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '11'}">selected</c:if> value="11">审核通过
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '12'}">selected</c:if> value="12">审核未通过
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '21'}">selected</c:if> value="21">代付成功
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '22'}">selected</c:if> value="22">代付失败
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '23'}">selected</c:if> value="23">代付处理中
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '25'}">selected</c:if> value="25">未知,需人工确认
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '30'}">selected</c:if> value="30">已提交
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '31'}">selected</c:if> value="31">提交成功
                            </option>
                            <option
                                    <c:if test="${paramMap.payStatus == '32'}">selected</c:if> value="32">提交失败
                            </option>
                        </select>&nbsp;&nbsp;&nbsp;
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <c:if test="${empty paramMap.platBatchId}">
                <td>
                    <div class="control-group">
                        <div class="controls">
                            <label>商户代付批次号：</label>
                            <input value="${paramMap.mchtBatchId}" name="mchtBatchId" type="text" maxlength="64"
                                   class="input-medium"/>
                        </div>
                    </div>
                </td>
            </c:if>

            <input value="${paramMap.platBatchId}" name="platBatchId" type="hidden" maxlength="64"
                   class="input-medium"/>
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
            <td colspan="2">
                <div class="control-group">
                    <div class="controls">
                        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"
                               style="margin-left: 25px;">
                        &nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/></td>
                        <c:if test="${!empty paramMap.platBatchId}">
                            <input id="callback" class="btn btn-primary" type="button" onclick="goBackProxyBatchPage()"
                                   value="返回" style="margin-left: 25px;">
                        </c:if>
                        <input id="isSearch" name="isSearch" type="hidden" value="1">
                    </div>
                </div>
            </td>
        </tr>
    </table>
</form>
<tags:message content="${message}" type="${messageType}"/>

<c:if test="${proxyBatch != null}">
    <div class="breadcrumb">
        <label>
            <th>批次信息</th>
        </label>
    </div>
    <table>

        <tr>
            <td>
                <label>总笔数&nbsp;&nbsp;&nbsp;&nbsp;：</label>
                <input value="${proxyBatch.totalNum}" type="text" class="input-small" readonly/>
            </td>

            <td>
                <label>&nbsp;&nbsp;&nbsp;总金额&nbsp;&nbsp;&nbsp;&nbsp;（元）：</label>
                <input value="${proxyBatch.totalAmount}" type="text" class="input-small" readonly/>
            </td>

        </tr>

        <tr>
            <td>
                <label>成功笔数：</label>
                <input value="${proxyBatch.successNum}" type="text" class="input-small" readonly/>
            </td>

            <td>
                <label>&nbsp;&nbsp;&nbsp;成功金额（元）：</label>
                <input value="${proxyBatch.successAmount}" type="text" class="input-small" readonly/>
            </td>

        </tr>

        <tr>
            <td>
                <label>失败笔数：</label>
                <input value="${proxyBatch.failNum}" type="text" class="input-small" readonly/>
            </td>


            <td>
                <label>&nbsp;&nbsp;&nbsp;失败金额（元）：</label>
                <input value="${proxyBatch.failAmount}" type="text" class="input-small" readonly/>
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
        <th>NO</th>
        <th>平台明细订单号</th>
        <th>商户代付批次号</th>
        <%--<th>商户明细号</th>--%>
        <th>商户名称</th>
        <th>收款人户名</th>
        <th>收款人账号</th>
        <th>金额（元）</th>
        <th>手续费（元）</th>
        <th>代付明细状态</th>
        <th>创建时间</th>
        <th>更新时间</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <%int i = 0; %>
    <c:forEach items="${page.list}" var="proxyDetail">
        <%i++; %>
        <tr>
            <td><%=i%>
            </td>
            <td>${proxyDetail.id}</td>
            <td>${proxyDetail.mchtBatchId}</td>
                <%--<td>${proxyDetail.mchtSeq}</td>--%>
            <td>${proxyDetail.extend1}</td>
            <td>${proxyDetail.bankCardName}</td>
            <td>${proxyDetail.bankCardNo}</td>
            <td><fmt:formatNumber type="number" value="${proxyDetail.amount}" pattern="0.0000"
                                  maxFractionDigits="4"/></td>
            <td><fmt:formatNumber type="number" value="${proxyDetail.mchtFee}" pattern="0.0000"
                                  maxFractionDigits="4"/></td>
            <td>
                    ${fns:getDictLabel(proxyDetail.payStatus,'proxypay_detail_status' ,'' )}
            </td>
            <td><fmt:formatDate value="${proxyDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td><fmt:formatDate value="${proxyDetail.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>
                <a href="${ctx}/mchtProxy/proxyDetail?detailId=${proxyDetail.id}&isSearch=1">详情</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

</body>
</html>