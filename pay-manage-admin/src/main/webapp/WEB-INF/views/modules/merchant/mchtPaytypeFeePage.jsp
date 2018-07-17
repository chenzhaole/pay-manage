<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>订单详情</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/dialog.jsp" %>
    <script type="text/javascript">

        $(function () {
            jQuery.validator.addMethod("alnum", function (value, element) {
                return this.optional(element) || /^[a-zA-Z0-9]+$/.test(value);
            }, "只能包括英文字母和数字");

            $("#channelForm").validate({
                debug: false, //调试模式取消submit的默认提交功能
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function (form) {   //表单提交句柄,为一回调函数，带一个参数：form
                    showShadow();
                    document.forms[0].submit();
                },
                errorPlacement: function (error, element) {
                    error.appendTo(element.parent());
                },
                rules: {
                    feeRate: {
                        max: 99999,
                        number: true
                    },
                    feeAmount: {
                        max: 9999999,
                        number: true
                    }
                },
                messages: {}
            });
        });

    </script>
</head>
<body>

<form id="channelForm" action="${ctx}/merchant/updateMchtPaytypeFee" method="post">
    <input type="hidden" name="id" value="${mchtId }"/>

    <div class="breadcrumb">
        <label><th>修改商户 ${mchtId } 的费率</th></label>
    </div>

    <table id="infoTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>支付方式名称</th>
            <th>支付方式编码</th>
            <th>收费类型</th>
            <th>费率(‰)</th>
            <th>手续费(分)</th>
            <th>是否保存</th>
            <th>是否展示</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${mchtFees}" var="mchtFee">
            <tr>
                <td  style="width: 130px;" > ${mchtFee.paytypeName}</td>
                <td style="width: 100px;">
                    <div class="controls">
                        <input style="width: 100px;" name="paytype${mchtFee.paytypeCode}" value="${mchtFee.paytypeCode}" type="text" readonly>
                    </div>
                </td>
                <td style="width: 100px;">
                    <div class="control-group">
                        <div class="controls">
                            <select name="feeType${mchtFee.paytypeCode}" class="input-xlarge" id="feeType" style="width: 100px">
                                <option value="">--请选择--</option>
                                <option
                                        <c:if test="${mchtFee.feeType == 1}">selected</c:if> value="1">按笔收费
                                </option>
                                <option
                                        <c:if test="${mchtFee.feeType == 2}">selected</c:if> value="2">按比例收费
                                </option>
                                <option
                                        <c:if test="${mchtFee.feeType == 3}">selected</c:if> value="3">混合
                                </option>
                            </select>
                        </div>
                    </div>
                </td>
                <td style="width: 100px;">
                    <div class="controls">
                        <input style="width: 100px;" name="feeRate${mchtFee.paytypeCode}" value="${mchtFee.feeRate}" placeholder="请填写费率（‰）" type="text" maxlength="6" >
                    </div>
                </td>
                <td style="width: 100px;">
                    <div class="controls">
                        <input style="width: 100px;" name="feeAmount${mchtFee.paytypeCode}" value="${mchtFee.feeAmount}" placeholder="请填写手续费（分）" type="text" maxlength="8">
                    </div>
                </td>
                <td style="width: 100px;" >
                    <div class="controls">
                        <input name="save${mchtFee.paytypeCode}" value="1" type="checkbox">
                    </div>
                </td>
                <td style="width: 100px;" >
                    <div class="controls">
                        <input name="showMchtFeeRate${mchtFee.paytypeCode}" value="1"
                            <c:if test="${mchtFee.showMchtFeeRate == 1}">checked</c:if>
                        type="checkbox">
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

        <div class="control-group">
            <label class="control-label">生效时间</label>
            <div class="controls">
                <label class="control-label" for="activeNow">立即</label>
                <input name="feeStatus" value="1" checked class="input-xlarge"
                       type="radio" id="activeNow">
                <label class="control-label" for="activeThan">定时</label>
                <input name="feeStatus" value="0" placeholder="" class="input-xlarge"
                       type="radio" id="activeThan">
                <input id="activeTime" name="activeTime" type="text" class="input-medium Wdate"
                       value="${mchtFee.activeTime}"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true, minDate:'%y-%M-%d'});"/>

            </div>
        </div>

    <div class="breadcrumb">
        <input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
        <input name="btnSubmit" class="btn btn-primary" type="submit" value="保存" style="margin-left: 5px;">
    </div>

</form>
</body>
</html>