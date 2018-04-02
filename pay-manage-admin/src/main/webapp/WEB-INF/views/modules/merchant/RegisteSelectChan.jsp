<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>通道补录-选择通道支付方式</title>
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

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });

    </script>

</head>
<body>

<div class="breadcrumb">
    <label><a href="#">申报信息管理</a> > <a href="#"><b>通道补录</b></a></label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="registeForm" action="${ctx}/merchant/reRegiste" method="post">

    <input type="hidden" name="op" value="add">
    <input type="hidden" name="mchtChanRegisteId" value="${mchtChanRegisteId}">

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>选择通道商户支付方式</label>
    </div>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">通道商户支付方式</label>
                    <div class="controls">
                        <select name="chanMchtPaytypeId" class="selectpicker bla bla bli" data-live-search="true">
                            <option value="">--请选择--</option>
                            <c:forEach items="${chanMchtPaytypes}" var="chanMchtPaytype">
                                <option value="${chanMchtPaytype.id}"
                                >${chanMchtPaytype.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">卡类型</label>
                    <div class="controls">
                        <select name="cardType" class="selectpicker bla bla bli" data-live-search="true">
                            <option value="">--请选择--</option>
                            <option value="1">借记卡</option>
                            <option value="2">信用卡</option>
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