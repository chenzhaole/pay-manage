<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>商户入驻流水</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });

        function registe() {
            document.forms[0].action = "${ctx}/merchant/reRegistePage";
            document.forms[0].submit();
        }

        function del(id) {
            if (confirm("是否确认删除ID为“" + id + "”的记录？")) {
                document.forms[0].action = "${ctx}/merchant/deleteMcht?id=" + id;
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
        <th><a href="#">商户入驻管理</a> > <a href="#"><b>商户入驻流水</b></a></th>
    </label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="searchForm" action="${ctx}/merchant/registeOrderList" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <table>
        <tr>
            <td><label>商户订单号：</label><input value="${paramMap.mchtOrderId}" name="mchtOrderId" type="text" maxlength="64" class="input-medium"/></td>
            <td><label>平台订单号：</label><input value="${paramMap.platOrderId}" name="platOrderId" type="text" maxlength="64" class="input-medium"/></td>
            <td><label>上游订单号：</label><input value="${paramMap.chanOrderId}" name="chanOrderId" type="text" maxlength="64" class="input-medium"/></td>
        </tr>
        <tr>
            <td>
                <label>状态：</label>
                <select name="status" class="input-medium" id="fund_settle_mode">
                    <option value="">--请选择--</option>
                    <c:forEach var="st" items="${fns:getDictList('mcht_registe_status')}">
                        <option value="${st.value}" <c:if test="${paramMap.status == st.value}">selected</c:if>>${st.label}</option>
                    </c:forEach>
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
        <th>ID</th>
        <th>商户订单号</th>
        <th>平台订单号</th>
        <th>上游订单号</th>
        <th>通道</th>
        <th>上游响应</th>
        <th>申报状态</th>
        <th>申报时间</th>
    </tr>
    </thead>
    <tbody>
    <%int i = 0; %>
    <c:forEach items="${page.list}" var="mcht">
        <%i++; %>
        <tr>
            <td><%=i%>
            </td>
            <td>${mcht.mchtOrderId}</td>
            <td><a href="${ctx}/merchant/mchtRegisteOrderDetail?id=${mcht.id}">${mcht.platOrderId}</a></td>
            <td>${mcht.chanOrderId}</td>
            <td>
                    ${mcht.chanName}
            </td>
            <td>${mcht.chan2PlatResMsg}</td>
            <td>
                ${fns:getDictLabel(mcht.status,'mcht_registe_status' , '')}
            </td>
            <td><fmt:formatDate value="${mcht.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

</body>
</html>