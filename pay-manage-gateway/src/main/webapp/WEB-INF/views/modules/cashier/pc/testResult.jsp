<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>Title</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

</head>
<body style="text-align: center">
<div class="aui-content aui-margin-b-15 bgF">
    <%--<ul class="aui-list aui-list-in">
        <li class="aui-list-item">--%>
            <div class="aui-list-item-label-icon">
                <p>平台订单号：${platOrderId}</p>
                <p>商户订单号订单号：${mchtOrderId}</p>
            </div>
            <div class="aui-list-item-inner aui-list-item-arrow">
                <p>支付结果：${status == '2'?"success":"unknow"}</p>
            </div>
        <%--</li>

    </ul>--%>
</div>

</body>
<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
</html>
