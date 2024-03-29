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
        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        })

    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#"><b>充值订单查询</b></a></th>
    </label>
</div>
<form id="searchForm" action="${ctx}/mchtRecharge/queryRechargePayOrders" method="post" class="breadcrumb form-search">
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
                        <input value="${paramMap.platOrderId}" id="platOrderId" name="platOrderId" type="text"
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
                            <c:forEach var="dict" items="${fns:getDictList('recharge_status')}">
                                <option value="${dict.value}" <c:if test="${paramMap.status eq dict.value}">selected</c:if>>${dict.label}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>

        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">订单时间：</label>
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

            <td>
                <div class="control-group">
                    <label class="control-label">充值方式：</label>
                    <div class="controls">
                        <select id="rechargeType" name="rechargeType">
                            <option value="">---请选择---</option>
                            <option value="1" <c:if test="${paramMap.rechargeType eq 1}">selected</c:if>  >汇款</option>
                            <option value="2" <c:if test="${paramMap.rechargeType eq 2}">selected</c:if>  >支付</option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">
                        <input type="submit" value="查询"/>
                    </label>
                </div>
            </td>
        </tr>

    </table>
    <label>
        | 总笔数：${totalTotal} |
    </label>
    <label>
        总金额：<fmt:formatNumber type="number" value="${totalAmount*0.01}" pattern="0.00" maxFractionDigits="2"/> 元|
    </label>
    <label>
        成功笔数：${successTotal} |
    </label>
    <label>
        成功金额：<fmt:formatNumber type="number" value="${successAmount*0.01}" pattern="0.00" maxFractionDigits="2"/> 元|
    </label>

<table id="contentTable" class="table table-striped table-bordered table-condensed table-hover"
       style="word-wrap:break-word; word-break:break-all;">
    <thead>
    <tr>
        <th>商户名称</th>
        <th>订单号</th>
        <td>充值方式</td>
        <th>订单金额</th>
        <th>手续费金额</th>
        <th>订单时间</th>
        <th>订单完成时间</th>
        <th>订单状态</th>
        <th>&nbsp;操&nbsp;作&nbsp;&nbsp;</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="orderInfo">
        <tr>
            <td>${orderInfo.mchtCode}</td>
            <td>${orderInfo.platOrderId}</td>
            <td>
               ${orderInfo.payType}
            </td>
            <td>
                <fmt:formatNumber type="number" value="${orderInfo.amount*0.01}" pattern="0.00" maxFractionDigits="2"/>元
            </td>
            <td>
                <fmt:formatNumber type="number" value="${orderInfo.mchtFeeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/>元
            </td>
            <td>
                <fmt:formatDate value="${orderInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>
                <fmt:formatDate value="${orderInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                    ${fns:getDictLabel(orderInfo.status,'pay_status' ,'' )}
            </td>
            <td>
                <!--    查询订单详情         -->
                <a href="${ctx}/mchtRecharge/adjustRechargeOrder?platOrderId=${orderInfo.platOrderId}&queryFlag=true">查看详情</a>
                <c:if test="${orderInfo.rechargeType eq '1'}">
                    <shiro:hasPermission name="mcht:proxy:operate">
                        <c:if test="${orderInfo.auditStatus eq 'customer_pass'}">
                            <a href="${ctx}/mchtRecharge/adjustOperateRechargeOrder?platOrderId=${orderInfo.platOrderId}">运营审批</a>
                        </c:if>
                    </shiro:hasPermission>
                    <shiro:hasPermission name="mcht:proxy:customer">
                        <c:if test="${orderInfo.rechargeType eq '2'}">
                            <!--    查询上游订单信息    -->
                            <c:if test="${orderInfo.status eq '0' }">
                                <a href="${ctx}/mchtRecharge/queryChanOrderStatus?platOrderId=${orderInfo.platOrderId}">查单</a>
                            </c:if>
                            <c:if test="${orderInfo.status eq '1'} ">
                                <a href="${ctx}/mchtRecharge/queryChanOrderStatus?platOrderId=${orderInfo.platOrderId}">查单</a>
                            </c:if>
                            <c:if test="${orderInfo.status eq '3'} ">
                                <a href="${ctx}/mchtRecharge/queryChanOrderStatus?platOrderId=${orderInfo.platOrderId}">查单</a>
                            </c:if>
                        </c:if>
                        <c:if test="${orderInfo.auditStatus eq 'created'}">
                            <a href="${ctx}/mchtRecharge/adjustRechargeOrder?platOrderId=${orderInfo.platOrderId}">客服审批</a>
                        </c:if>
                    </shiro:hasPermission>

                </c:if>


            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
<!-- 翻页（end） -->
</form>
</body>
</html>