<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>通道商户余额</title>
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
    <label><a href="#">通道管理</a> > <a href="#"><b>通道余额</b></a></label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="platProductForm" action="${ctx}/platform/addPlatProductPage" method="post">

    <input type="hidden" name="op" value="add">

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>上游通道余额为：${balance} 元</label>
    </div>
    <div class="breadcrumb">
        <input id="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);"
               name="btnCancel"/>
    </div>
</form>
</body>

</html>