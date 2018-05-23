<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>调账添加</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });


        $(function () {
            $("#inputForm").validate({
                rules: {
                    loginName: {remote: "${ctx}/sys/user/checkLoginName?oldLoginName=" + encodeURIComponent('${user.loginName}')}
                },
                messages: {
                    loginName: {remote: "用户登录名已存在"},
                    confirmNewPassword: {equalTo: "输入与上面相同的密码"}
                },
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });

            $("#balanceBtn").click(function () {
                var mchtId = $("#mchtId").val();
                if (mchtId == '') {
                    alert("请输入商户号");
                } else {
                    $.ajax({
                        url: '${ctx}/platform/adjust/balance',
                        type: 'POST', //GET
                        async: true,    //或false,是否异步
                        data: {
                            'mchtId': mchtId
                        },
                        timeout: 5000,    //超时时间
                        dataType: 'text',    //返回的数据格式：json/xml/html/script/jsonp/text
                        success: function (data) {
                            console.log(data);
                            $("#balance").val(data);
                        }
                    });
                }
            });
        });

        function rateOrAmount() {
            var val = $("#feeType").val();
            if (val == "1") {
                $("input[name='feeAmount']").attr("disabled", false);
                $("input[name='feeRate']").attr("disabled", true);
                $("input[name='feeAmount']").addClass("required");
                $("input[name='feeRate']").removeClass("required");
            } else {
                $("input[name='feeAmount']").attr("disabled", true);
                $("input[name='feeRate']").attr("disabled", false);
                $("input[name='feeAmount']").removeClass("required");
                $("input[name='feeRate']").addClass("required");
            }
        }

    </script>
</head>
<body>


<ul class="nav nav-tabs">
    <li><a href="${ctx}/platform/adjust">调账列表</a></li>
    <li class="active"><a href="${ctx}/platform/adjust/form">调账添加</a></li>
</ul>
<form:form id="inputForm" modelAttribute="platAccountAdjust" action="${ctx}/platform/adjust/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <tags:message content="${message}"/>
    <table class="table">
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">调账方向<span style="color: red;">*</span></label>
                    <div class="controls">

                        <form:select path="adjustType">
                            <form:options items="${fns:getDictList('account_adjust_type')}" itemValue="value"
                                          itemLabel="label"/>
                        </form:select>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">商户名称：</label>
                    <div class="controls">
                        <label>
                            <select id="mchtId" name="mchtId" class="selectpicker bla bla bli" data-live-search="true">
                                <option value="">--请选择--</option>
                                <c:forEach items="${mchtInfos}" var="mchtInfo">
                                    <option value="${mchtInfo.id}">${mchtInfo.name}</option>
                                </c:forEach>
                            </select>
                        </label>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">账户类型<span style="color: red;">*</span></label>
                    <div class="controls">
                        <form:select path="accountType">
                            <form:options items="${fns:getDictList('account_type')}" itemValue="value"
                                          itemLabel="label"/>
                        </form:select>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">账户余额（元）</label>
                    <div class="controls">
                        <input type="text" readonly disabled id="balance"/> <input type="button" value="查询"
                                                                                   id="balanceBtn"/>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">收费类型<span style="color: red;"></span></label>
                    <div class="controls">
                        <select name="feeType" class="input-xlarge" id="feeType" onchange="rateOrAmount()">
                            <option value="1">固定手续费</option>
                            <option value="2">按比例收费</option>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">调账金额（元）<span style="color: red;">*</span></label>
                    <div class="controls">
                        <form:input path="adjustAmount" class="number required"/>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">调账费率（‰）<span style="color: red;"></span></label>
                    <div class="controls">
                        <form:input path="feeRate" class="number required" disabled="true"/>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">备注</label>
                    <div class="controls">
                        <form:textarea path="remark" cols="20" rows="5"/>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">调账手续费（元）<span style="color: red;"></span></label>
                    <div class="controls">
                        <form:input path="feeAmount" class="number required"/>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="form-actions">
                    <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
                    <input id="btnCancel" class="btn" type="button" value="返 回"
                           onclick="window.location.href='${ctx}/platform/adjust'"/>
                </div>
            </td>
        </tr>
    </table>


</form:form>
</body>
</html>
