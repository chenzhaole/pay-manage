<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>支付产品新增-选择支付方式</title>
    <meta name="decorator" content="default"/>
    <style>
        .table th, .table td {
            border-top: none;
        }

        .control-group {
            border-bottom: none;
        }
    </style>

    <script type="text/javascript">

    </script>

</head>
<body>

<div class="breadcrumb">
    <label><a href="#">支付产品管理</a> > <a href="#"><b>支付产品新增/编辑</b></a></label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="platProductForm" action="${ctx}/platform/addPlatProductPage" method="post">

    <input type="hidden" name="op" value="add">

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>选择支付方式</label>
    </div>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">支付方式</label>
                    <div class="controls">
                        <select name="paymentType" class="input-xlarge" id="paymentType"
                                <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                            <c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
                                <option <c:if test="${chanMchPaytye.payType == paymentTypeInfo.code}">selected</c:if> value="${paymentTypeInfo.code}">${paymentTypeInfo.desc}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
    </table>
    <div class="breadcrumb">
        <input id="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);"
               name="btnCancel"/>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="下一步"
               style="margin-left: 5px;">
    </div>
</form>
</body>

</html>