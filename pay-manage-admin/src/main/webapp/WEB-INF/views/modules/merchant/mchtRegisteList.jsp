<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>商户入驻列表</title>
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
        <th><a href="#">商户入驻管理</a> > <a href="#"><b>商户入驻列表</b></a></th>
    </label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="searchForm" action="${ctx}/merchant/registeList" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <table>
        <tr>
            <td>
                <label>商户手机：</label>
                <select name="mchtCode" class="selectpicker bla bla bli" data-live-search="true">
                    <option value="">--请选择--</option>
                    <c:forEach items="${mchtInfos}" var="mchtInfo">
                        <option value="${mchtInfo.mchtCode}"
                                <c:if test="${paramMap.mchtCode == mchtInfo.id}">selected</c:if>
                        >${mchtInfo.mobile}</option>
                    </c:forEach>
                </select>
            </td>
            <td>
                <label>通道商户支付方式：</label>
                <select name="chanMchtPaytypeId" class="selectpicker bla bla bli" data-live-search="true">
                    <option value="">--请选择--</option>
                    <c:forEach items="${chanMchtPaytypes}" var="chanMchtPaytype">
                        <option value="${chanMchtPaytype.id}"
                                <c:if test="${paramMap.chanMchtPaytypeId == chanMchtPaytype.id}">selected</c:if>
                        >${chanMchtPaytype.name}</option>
                    </c:forEach>
                </select>
            </td>
            <td>
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
                <input id="clearButton" class="btn btn-primary" type="button" value="通道补录" onclick="registe()"
                       style="margin-left: 5px;"/>
            </td>
        </tr>
        <tr>
            <%--<td>
                <label>状态：</label>
                <select name="status" class="input-medium" id="fund_settle_mode">
                    <option value="">--请选择--</option>
                    <c:forEach var="st" items="${fns:getDictList('mcht_registe_status')}">
                        <option value="${st.value}" <c:if test="${paramMap.status == st.value}">selected</c:if>>${st.label}</option>
                    </c:forEach>
                </select>&nbsp;&nbsp;&nbsp;
            </td>--%>

        </tr>
    </table>
</form>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>ID</th>
        <th>商户名</th>
        <th>手机</th>
        <th>通道</th>
        <th>支付方式</th>
        <th>申报时间</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <%int i = 0; %>
    <c:forEach items="${page.list}" var="mcht">
        <%i++; %>
        <tr>
            <td><%=i%>
            </td>
            <td>${mcht.mchtName}
            </td>
            <td>${mcht.phone}</td>
            <td>
                    ${mcht.chanName}
            </td>
            <td>${fns:getDictLabel(mcht.paytype, "pay_type","" )}</td>
            <td><fmt:formatDate value="${mcht.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>
                    <a href="${ctx}/merchant/reRegiste?id=${mcht.id}">通道补录</a>
                    <%--<a href="${ctx}/merchant/deleteMcht?id=${mcht.id}&mchtCode=${mcht.mchtCode}" onclick="return confirmx('是否删除编号为“${mcht.mchtCode}”的记录？', this.href)">删除</a>--%>
                    </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

</body>
</html>