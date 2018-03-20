<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>新通道入驻-选择通道支付方式</title>
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

        $(function () {

            $("#registeForm").validate({
                debug: false, //调试模式取消submit的默认提交功能
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function () {   //表单提交句柄,为一回调函数，带一个参数：form

                    if ("0" == $('#chanMchtPaytypeId  option:selected').val()) {
                        alert('请选择商户通道支付方式');
                        return;
                    } else if ('0' == $('#cardType  option:selected').val()) {
                        alert('请选择卡类型');
                        return;
                    }

                    showShadow();
                    document.forms[0].submit();
                },
                errorPlacement: function (error, element) {
                    error.appendTo(element.parent());
                },
                rules: {
                    chanMchtPaytypeId: {required: true},
                    cardType: {required: true}
                },
                messages: {
                    chanMchtPaytypeId: {
                        required: '必选'
                    },
                    cardType: {
                        required: '必选'
                    }
                }
            });
        });

    </script>

</head>
<body>

<div class="breadcrumb">
    <label><a href="#">商户入驻信息管理</a> > <a href="#"><b>新通道入驻</b></a></label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<div class="shadow" style="display:block;">
    <div id="tbl_brand_processing" class="dataProcessing">
        <img src="${ctxStatic}/images/loading.gif"><span>&nbsp;&nbsp;处理中...</span>
    </div>
</div>

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
                        <select id="chanMchtPaytypeId" name="chanMchtPaytypeId" class="selectpicker bla bla bli" data-live-search="true">
                            <option value="0">--请选择--</option>
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
                        <select id="cardType" name="cardType" class="selectpicker bla bla bli" data-live-search="true">
                            <option value="0">--请选择--</option>
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