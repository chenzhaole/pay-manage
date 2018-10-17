<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>充值订单查询</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">

    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            //导出数据提示
            $("#exportButton").click(function () {
                top.$.jBox.confirm("确认要导出订单问题反馈数据吗？", "系统提示", function (v, h, f) {
                    if (v == "ok") {
                        $("#searchForm").attr("action", "${ctx}/process/question/export/");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action", "${ctx}/process/question/list/");
                    }
                }, {buttonsFocus: 1});
                top.$('.jbox-body .jbox-icon').css('top', '55px');
            });
        });

        $(document).ready(function () {
            $("#btnExport").click(function () {
                $("#searchForm").attr("action", "${ctx}/mchtOrder/export");
                $("#searchForm").submit();
                $("#searchForm").attr("action", "${ctx}/mchtOrder/list");
            });
        });

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#paging").val("1");
            $("#searchForm").submit();
            return false;
        }

        function reSet() {
            $("#customerSeq")[0].value = "";
            $("#platformSeq")[0].value = "";
            $("#officialSeq")[0].value = "";
            $("#beginDate")[0].value = "";
            $("#endDate")[0].value = "";
            $("input[type='text']").val("");
            // $("#mchtId")[0].value = "";
            // $("#chanId")[0].value = "";
            // $("#platProductId")[0].value = "";
            // $("#chanMchtPaytypeId")[0].value = "";
            $("#payType")[0].value = "";
            $("#status")[0].value = "";
            $("#supplyStatus")[0].value = "";
        }

        function approveSend(content) {
            var orderId = $(content).parent().parent().children().eq(0).text();
            var url = "save";
            $.ajax({
                type: "POST",
                dataType: "text",
                async: false,
                url: url,
                data: {"approveSend": 1, "orderId": orderId}    //通过"确认发送"标识，来修改"审批状态"为已审批、"核实状态"为未核实
            });
            $(content).parent().next().text("已审批");
            $(content).parent().next().next().replaceWith("<td style='background-color:#ff0000'>未核实</td>");
            $(content).parent().next().next().next().html("<a href='${ctx}/process/question/form?orderId=" + orderId + "'>核实</a>");
            $(content).remove();
        }

    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">代付管理</a> > <a href="#"><b>充值订单查询</b></a></th>
    </label>
</div>
<form id="searchForm" action="${ctx}/mchtOrder/list" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <input id="paging" name="paging" type="hidden" value="0"/>
    <input id="isSelectInfo" name="isSelectInfo" type="hidden" value="0"/>
    <table>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">订单号：</label>
                    <div class="controls">
                        <input value="${paramMap.customerSeq}" id="customerSeq" name="customerSeq" type="text"
                               maxlength="64" class="input-large"/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">订单状态：</label>
                    <div class="controls">
                        <select id="status" name="status">
                            <option value="">---请选择---</option>

                            <option value="created"
                                <c:if test="${orderInfo.auditStatus eq 'created'}">
                                    selected
                                </c:if> >未审核
                            </option>

                            <option value="customer_pass"
                                    <c:if test="${orderInfo.auditStatus eq 'customer_pass'}">
                                        selected
                                    </c:if> >客服审核通过
                            </option>
                            <option value="operate_pass"
                                    <c:if test="${orderInfo.auditStatus eq'operate_pass'}">
                                        selected
                                    </c:if> >运营审核通过
                            </option>
                            <option value="customer_refuse"
                                    <c:if test="${orderInfo.auditStatus eq 'customer_refuse'}">
                                        selected
                                    </c:if> >客服审核未通过
                            </option>
                            <option value="operate_refuse"
                                    <c:if test="${orderInfo.auditStatus eq 'operate_refuse'}">
                                        selected
                                    </c:if> >运营审核未通过
                            </option>
                            <option value="no_need_audit"
                                    <c:if test="${orderInfo.auditStatus eq 'no_need_audit'}">
                                        selected
                                    </c:if> >无需审核
                            </option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">交易时间：</label>
                    <div class="controls">
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
        </tr>

    </table>
</form>
<label>| 总笔数：${orderCount} | </label>
<label>总金额：${amount} 元| </label>
<label>成功笔数：${successCount} | </label>
<label>成功金额：${successAmount} 元| </label>
<tags:message content="${message}" type="${messageType}"/>

<table id="contentTable" class="table table-striped table-bordered table-condensed table-hover"
       style="word-wrap:break-word; word-break:break-all;">
    <thead>
    <tr>
        <th>商户名称</th>
        <th>商户号</th>
        <th>订单号</th>
        <th>订单金额</th>
        <th>手续费金额</th>
        <th>我司收款账户</th>
        <th>我司收款账户</th>
        <th>订单时间</th>
        <th>审核状态</th>

        <shiro:hasPermission name="order:list:op">
            <th>&nbsp;操&nbsp;作&nbsp;&nbsp;</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="orderInfo">
        <tr>
            <td>${orderInfo.mchtCode}</td>
            <td>${orderInfo.mchtId}</td>
            <td>${orderInfo.platOrderId}</td>
            <td><fmt:formatNumber type="number" value="${orderInfo.amount*0.01}" pattern="0.00" maxFractionDigits="2"/></td>
            <td>${orderInfo.mchtFeeAmount}</td>
            <td>${orderInfo.rechargeConfig.compReceiptAcctNo}</td>
            <td>${orderInfo.rechargeConfig.compReceiptAcctName}</td>
            <td><fmt:formatDate value="${orderInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>
                <!--    创建未审核:created 客服审核通过:customer_pass 运营审核通过 operate_pass 客服审核拒绝: customer_refuse  运营审核拒绝 operate_refuse',-->
                <c:if test="${orderInfo.auditStatus eq 'created'}">
                    未审核
                </c:if>
                <c:if test="${orderInfo.auditStatus eq 'customer_pass'}">
                    客服审核通过
                </c:if>
                <c:if test="${orderInfo.auditStatus eq'operate_pass'}">
                    运营审核通过
                </c:if>
                <c:if test="${orderInfo.auditStatus eq 'customer_refuse'}">
                    客服审核未通过
                </c:if>
                <c:if test="${orderInfo.auditStatus eq 'operate_refuse'}">
                    运营审核未通过
                </c:if>
                <c:if test="${orderInfo.auditStatus eq 'no_need_audit'}">
                    无需审核
                </c:if>
            </td>

                <td>
                    <shiro:hasPermission name="mcht:proxy:customer">
                        <c:if test="${orderInfo.auditStatus eq 'created'}">
                            <a href="${ctx}/mchtRecharge/adjustRechargeOrder?platOrderId=${orderInfo.platOrderId}">客服审批</a>
                        </c:if>
                    </shiro:hasPermission>
                    <shiro:hasPermission name="mcht:proxy:operate">
                        <c:if test="${orderInfo.auditStatus eq 'customer_pass'}">
                            <a href="${ctx}/mchtRecharge/adjustRechargeOrder?platOrderId=${orderInfo.platOrderId}">运营审批</a>
                        </c:if>
                    </shiro:hasPermission>
                </td>

        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
<!-- 翻页（end） -->
</body>
</html>