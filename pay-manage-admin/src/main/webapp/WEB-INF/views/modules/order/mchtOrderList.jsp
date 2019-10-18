<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>商户交易订单列表</title>
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

        <%--function supplyNotify(orderId,suffix){--%>
        <%--if(confirm("确认补发通知吗？")){--%>
        <%--$.ajax({--%>
        <%--url:'${ctx}/order/supplyNotify',--%>
        <%--type:'POST', //GET--%>
        <%--async:true,    //或false,是否异步--%>
        <%--data:{--%>
        <%--'orderId':orderId,'suffix':suffix--%>
        <%--},--%>
        <%--timeout:5000,    //超时时间--%>
        <%--dataType:'text',    //返回的数据格式：json/xml/html/script/jsonp/text--%>
        <%--success:function(data){--%>
        <%--alert(data);--%>
        <%--}--%>
        <%--});--%>
        <%--}--%>
        <%--}--%>

    </script>
</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">交易管理</a> > <a href="#"><b>交易流水列表</b></a></th>
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
                    <label class="control-label">商户订单号：</label>
                    <div class="controls">
                        <input value="${paramMap.customerSeq}" id="customerSeq" name="customerSeq" type="text"
                               maxlength="64" class="input-large"/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">平台订单号：</label>
                    <div class="controls">
                        <input value="${paramMap.platformSeq}" id="platformSeq" name="platformSeq" type="text"
                               maxlength="64" class="input-large"/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">官方订单号：</label>
                    <div class="controls">
                        <input value="${paramMap.officialSeq}" id="officialSeq" name="officialSeq" type="text"
                               maxlength="64" class="input-large"/>
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">订单状态：</label>
                    <div class="controls">
                        <select id="status" name="status">
                            <option value="">---请选择---</option>
                            <option value="0" <c:if test="${paramMap.status eq '0'}">selected</c:if>  >初始创建</option>
                            <option value="1" <c:if test="${paramMap.status eq '1'}">selected</c:if>  >提交成功</option>
                            <option value="2" <c:if test="${paramMap.status eq '2'}">selected</c:if> >支付成功</option>
                            <option value="4002" <c:if test="${paramMap.status eq '4002'}">selected</c:if> >支付失败</option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">补单状态：</label>
                    <div class="controls">
                        <select id="supplyStatus" name="supplyStatus">
                            <option value="">---请选择---</option>
                            <option value="0" <c:if test="${paramMap.supplyStatus eq '0'}">selected</c:if>>成功</option>
                            <option value="1" <c:if test="${paramMap.supplyStatus eq '1'}">selected</c:if>>失败</option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">支付方式：</label>
                    <div class="controls">
                        <select name="payType" id="payType">
                            <option value="">---全部---</option>
                            <option value="wx" <c:if test="${fn:substring(paramMap.payType,0,2) eq 'wx' }">selected</c:if>>微信支付</option>

                            <option value="al" <c:if test="${fn:substring(paramMap.payType,0,2) eq 'al' }">selected</c:if>>支付宝支付</option>

                            <option value="sn" <c:if test="${fn:substring(paramMap.payType,0,2) eq 'sn' }">selected</c:if>>苏宁支付</option>

                            <option value="qq" <c:if test="${fn:substring(paramMap.payType,0,2) eq 'qq' }">selected</c:if>>QQ支付</option>

                            <option value="jd" <c:if test="${fn:substring(paramMap.payType,0,2) eq 'jd' }">selected</c:if>>京东支付</option>

                            <option value="yl" <c:if test="${fn:substring(paramMap.payType,0,2) eq 'yl' }">selected</c:if>>银联支付</option>

                            <option value="qj202" <c:if test="${paramMap.payType eq 'qj202' }">selected</c:if>>快捷支付</option>

                            <option value="qj301" <c:if test="${paramMap.payType eq 'qj301' }">selected</c:if>>网银支付</option>

<%--
                            <option value="df101" <c:if test="${paramMap.payType eq 'df101'}">selected</c:if>>单笔代付</option>
--%>
                        </select>
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td colspan="2">
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
                        <input type="checkbox" name="isstat" id="isstat" value="1"/>统计汇总
                    </div>
                </div>
            </td>
            <td colspan="2" align="right">
                <div class="btn-group">
                    <input id="btnSubmit" class="btn btn-primary pull-right" type="submit" value="查询">
                </div>
                <div class="btn-group">
                    <input id="clearButton" class="btn btn-primary pull-right" type="button" value="重置"
                           onclick="reSet()"/>
                </div>
                <div class="btn-group">
                    <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
                </div>

            </td>
        </tr>
    </table>
</form>
<label>| 总笔数：${orderCount} | </label>
<label>总金额：${amount} 元| </label>
<label>成功笔数：${successCount} | </label>
<label>成功金额：${successAmount} 元 </label>
<tags:message content="${message}" type="${messageType}"/>

<table id="contentTable" class="table table-striped table-bordered table-condensed table-hover"
       style="word-wrap:break-word; word-break:break-all;">
    <thead>
    <tr>
        <th>商户名称</th>
        <th>支付方式</th>
        <th>商户订单号</th>
        <th>平台订单号</th>
        <th>官方订单号</th>
        <th>交易金额</th>
        <th>订单状态</th>
        <th>补单状态</th>
        <th>创建时间</th>
        <shiro:hasPermission name="order:list:op">
            <th>&nbsp;操&nbsp;作&nbsp;&nbsp;</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="orderInfo">
        <tr>
            <td>${orderInfo.mchtCode}</td>
            <td>
                ${orderInfo.payType}
            </td>
            <td>${orderInfo.mchtOrderId}</td>
            <td>${orderInfo.platOrderId}</td>
            <td>${orderInfo.officialOrderId}</td>
            <td><fmt:formatNumber type="number" value="${orderInfo.amount*0.01}" pattern="0.00"
                                  maxFractionDigits="2"/>元
            </td>
            <td>
                    ${fns:getDictLabel(orderInfo.status,'pay_status' ,'' )}
            </td>
            <td>
                    ${fns:getDictLabel(orderInfo.supplyStatus,'supply_status' ,'' )}
            </td>
            <td><fmt:formatDate value="${orderInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <shiro:hasPermission name="order:list:op">
                <td>
                    <c:if test="${orderInfo.status == '2'}">
                        <a href="${ctx}/mchtOrder/supplyNotify?orderId=${orderInfo.id}&suffix=<fmt:formatDate value="${orderInfo.createTime}"  pattern="yyyyMM"/>">补发通知</a>
                    </c:if>
                    <c:if test="${orderInfo.status != '2'}"><a href="${ctx}/mchtOrder/querySupply?orderId=${orderInfo.id}&beginDate=${paramMap.beginDate}&endDate=${paramMap.endDate}">|查单|</a></c:if>
                        <%--|
                        <a href="${ctx}/process/question/form?orderId=${orderInfo.id}">同步状态</a>  --%>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<!-- 翻页（start） -->
<!--  <div class="pagination"> -->
<!--     <table> -->
<!--         <tr> -->
<%--             <c:if test="${page.firstPage ==}"> --%>
<%--                 <td><a href="${ctx}/order/list?pageNo=${page.prePage}&pageSize=${page.pageSize}">前一页</a></td> --%>
<%--             </c:if> --%>
<%--             <c:forEach items="${page.navigatepageNos}" var="nav"> --%>
<%--                 <c:if test="${nav == page.pageNo}"> --%>
<%--                     <td style="font-weight: bold;">${nav}</td> --%>
<%--                 </c:if> --%>
<%--                 <c:if test="${nav != page.pageNo}"> --%>
<%--                     <td><a href="${ctx}/order/list?pageNo=${nav}&pageSize=${page.pageSize}">${nav}</a></td> --%>
<%--                 </c:if> --%>
<%--             </c:forEach> --%>
<%--             <c:if test="${page.hasNextPage}"> --%>
<%--                 <td><a href="${ctx}/order/list?pageNo=${page.nextPage}&pageSize=${page.pageSize}">下一页</a></td> --%>
<%--             </c:if> --%>
<!--            <td> -->
<%--            	<a href="javascript:">&nbsp;&nbsp;&nbsp;&nbsp;当前 ${page.pageNo} / ${page.pages} 页,共 ${page.total} 条</a> --%>
<!--            </td> -->
<!--         </tr> -->
<!--     </table> -->
<!--  </div> -->
<div class="pagination">${page}</div>
<!-- 翻页（end） -->
</body>
</html>