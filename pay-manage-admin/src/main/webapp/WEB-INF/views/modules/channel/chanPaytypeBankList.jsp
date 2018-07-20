<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>通道支付方式银行列表</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });

        function add() {
            document.forms[0].action = "${ctx}/channel/addChanPaytypeBankPage";
            document.forms[0].submit();
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
    <label><a href="#">通道支付方式银行管理></a><a href="#"><b>通道支付方式银行列表</b></a></label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="searchForm" action="${ctx}/channel/chanBankList" method="post" class="breadcrumb form-search">
    <table>
        <tr>
            <td>
                <label>通道名称：</label><input value="${paramMap.chanName}" name="chanName" type="text" maxlength="64"
                                           class="input-medium"/>
            </td>
            <td>
                <label>支付方式：</label>
                <select name="payType" class="input-medium" id="payType">
                    <option value="">--请选择--</option>
                    <c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
                        <option value="${paymentTypeInfo.code}"
                                <c:if test="${paramMap.payType == paymentTypeInfo.code}">selected</c:if>>
                                ${paymentTypeInfo.desc} </option>
                    </c:forEach>
                </select>
            </td>
            <td>
                <label>通道银行代码：</label>
                <input value="${paramMap.chanBankCode}" name="chanBankCode" type="text"
                                             maxlength="64" class="input-medium"/>
                </select>
            </td>
            <td>
                <label>平台银行名称：</label>
                <select name="platBankCode" class="selectpicker bla bla bli" data-live-search="true">
                    <option value="">--请选择--</option>
                    <c:forEach items="${platBanks}" var="platBank">
                        <option value="${platBank.bankCode}"
                        <c:if test="${paramMap.platBankCode == platBank.bankCode}">selected</c:if>
                        >${platBank.bankName}</option>
                    </c:forEach>
                    </optgroup>
                </select>
            </td>
            <td>
                &nbsp;&nbsp;&nbsp;
                <input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
                <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
                <shiro:hasPermission name="channel:addChanPaytypeBankPage">
                    <input id="clearButton" class="btn btn-primary" type="button" value="新增" onclick="add()"
                       style="margin-left: 5px;"/>
                </shiro:hasPermission>
            </td>
        </tr>
    </table>
</form>


<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>NO</th>
        <th>通道名称</th>
        <th>通道编号</th>
        <th>通道银行编号</th>
        <th>通道银行</th>
        <th>支付方式</th>
        <th>平台银行名称</th>
        <th>平台银行编号</th>

        <th>状态</th>
        <th>创建日期</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <%int i = 0; %>
    <c:forEach items="${page.list}" var="chanBank">
        <%i++; %>
        <tr>
            <td><%=i%>
            </td>
            <td>${chanBank.chanName}</td>
            <td>${chanBank.chanCode}</td>
            <td>${chanBank.chanBankCode}</td>
            <td>${chanBank.chanBankName}</td>
            <td>${fns:getDictLabel(chanBank.payType, "pay_type","" )}</td>
            <td>${chanBank.bankName}</td>
            <td>${chanBank.platBankCode}</td>

            <td>
                <c:if test="${chanBank.status == 1}">有效</c:if>
                <c:if test="${chanBank.status == 2}">无效</c:if>
            <td><fmt:formatDate value="${chanBank.createDate}" pattern="yyyy-MM-dd  HH:mm:ss"/></td>
            <td>
                <shiro:hasPermission name="channel:editChanPaytypeBankPage">
                    <a href="${ctx}/channel/addChanPaytypeBankPage?id=${chanBank.id}">修改</a>|
                </shiro:hasPermission>
                <shiro:hasPermission name="channel:deleteChanPaytypeBank">
                    <a href="${ctx}/channel/deleteChanPaytypeBank?id=${chanBank.id}"
                   onclick="return confirmx('是否确认删除此记录？', this.href)">删除</a>
                </shiro:hasPermission>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>