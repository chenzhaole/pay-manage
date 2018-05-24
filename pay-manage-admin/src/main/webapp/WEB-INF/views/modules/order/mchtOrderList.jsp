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
            $("#beginDate")[0].value = "";
            $("#endDate")[0].value = "";
            $("input[type='text']").val("");
            $("#mchtId")[0].value = "";
            $("#chanId")[0].value = "";
            $("#platProductId")[0].value = "";
            $("#chanMchtPaytypeId")[0].value = "";
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
                            <option value="wx101" <c:if test="${paramMap.payType eq 'wx101'}">selected</c:if>>微信h5</option>
                            <option value="wx201" <c:if test="${paramMap.payType eq 'wx201'}">selected</c:if>>微信APP</option>
                            <option value="wx301&wx302" <c:if test="${paramMap.payType eq 'wx301&wx302'}">selected</c:if>>微信公众号</option>
                            <option value="wx401" <c:if test="${paramMap.payType eq 'wx401'}">selected</c:if>>微信扫码</option>
                            <option value="wx502&wx503" <c:if test="${paramMap.payType eq 'wx502&wx503'}">selected</c:if>>微信付款码</option>
                            <option value="al101&al102" <c:if test="${paramMap.payType eq 'al101&al102'}">selected</c:if>>支付宝h5</option>
                            <option value="al201" <c:if test="${paramMap.payType eq 'al201'}">selected</c:if>>支付宝APP</option>
                            <option value="al401" <c:if test="${paramMap.payType eq 'al401'}">selected</c:if>>支付宝扫码</option>
                            <option value="al502&al503" <c:if test="${paramMap.payType eq 'al502&al503'}">selected</c:if>>支付宝付款码</option>
                            <option value="sn401" <c:if test="${paramMap.payType eq 'sn401'}">selected</c:if>>苏宁扫码</option>
                            <option value="qq101&qq102" <c:if test="${paramMap.payType eq 'qq101&qq102'}">selected</c:if>>QQH5</option>
                            <option value="qq403" <c:if test="${paramMap.payType eq 'qq403'}">selected</c:if>>QQ扫码</option>
                            <option value="jd101&jd102" <c:if test="${paramMap.payType eq 'jd101&jd102'}">selected</c:if>>京东H5</option>
                            <option value="jd401" <c:if test="${paramMap.payType eq 'jd401'}">selected</c:if>>京东扫码</option>
                            <option value="yl401" <c:if test="${paramMap.payType eq 'yl401'}">selected</c:if>>银联二维码</option>
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
<tags:message content="${message}"/>

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
                <c:if test="${orderInfo.payType eq 'wx101'}">微信h5</c:if>
                <c:if test="${orderInfo.payType eq 'wx201'}">微信APP</c:if>
                <c:if test="${orderInfo.payType eq 'wx301' || orderInfo.payType eq 'wx302'}">微信公众号</c:if>
                <c:if test="${orderInfo.payType eq 'wx401'}">微信扫码</c:if>
                <c:if test="${orderInfo.payType eq 'wx502' || orderInfo.payType eq 'wx503'}">微信付款码</c:if>
                <c:if test="${orderInfo.payType eq 'al101' || orderInfo.payType eq 'al102'}">支付宝h5</c:if>
                <c:if test="${orderInfo.payType eq 'al201'}">支付宝APP</c:if>
                <c:if test="${orderInfo.payType eq 'al401'}">支付宝扫码</c:if>
                <c:if test="${orderInfo.payType eq 'al502' || orderInfo.payType eq 'al503'}">支付宝付款码</c:if>
                <c:if test="${orderInfo.payType eq 'sn401'}">苏宁扫码</c:if>
                <c:if test="${orderInfo.payType eq 'qq101' || orderInfo.payType eq 'qq102'}">QQH5</c:if>
                <c:if test="${orderInfo.payType eq 'qq403'}">QQ扫码</c:if>
                <c:if test="${orderInfo.payType eq 'jd101' || orderInfo.payType eq 'jd102'}">京东H5</c:if>
                <c:if test="${orderInfo.payType eq 'jd401'}">京东扫码</c:if>
                <c:if test="${orderInfo.payType eq 'yl401'}">银联二维码</c:if>
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