<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>调账添加</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({
            });
        });


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
                    document.forms[0].action = "${ctx}/platform/addPlatBank";
                    document.forms[0].submit();
                },
                errorPlacement: function (error, element) {
                    error.appendTo(element.parent());
                },
                rules: {
                    bankCode: {
                        alnum: true,
                        required: true,
                        maxlength: 32
                    },
                    bankName: {
                        required: true,
                        maxlength: 32
                    },
                    extend: {
                        maxlength: 255
                    }
                },
                messages: {
                    name: {
                        required: '必填'
                    },
                    busiEmail: {
                        email: 'email格式不正确'
                    }
                }
            });
        });


        function del(id) {
            if (confirm("是否确认删除ID为“" + id + "”的记录？")) {
                document.forms[0].action = "${ctx}/bowei/repaymentDel?id=" + id;
                document.forms[0].submit();
            }
        }


    </script>
</head>
<body>


<ul class="nav nav-tabs">
    <li><a href="${ctx}/platform/adjust">调账列表</a></li>
    <li class="active"><a href="${ctx}/platform/adjust/form">调账添加</a></li>
</ul>
<form:form id="inputForm" modelAttribute="platAccountAdjust" action="${ctx}/platform/adjust/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <tags:message content="${message}"/>

    <%--<div class="control-group">--%>
    <%--<label class="control-label">归属部门:</label>--%>
    <%--<div class="controls">--%>
    <%--<tags:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"--%>
    <%--title="部门" url="/sys/office/treeData?type=2" cssClass="required"/>--%>
    <%--</div>--%>
    <%--</div>--%>

    <table class="table">
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">调账方向<span style="color: red;">*</span></label>
                    <div class="controls">

                       <form:select path="adjustType">
                            <form:options items="${fns:getDictList('account_adjust_type')}" itemValue="value" itemLabel="label"/>
                       </form:select>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">商户号<span style="color: red;">*</span></label>
                    <div class="controls">
                        <form:input path="mchtId"/>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">账户类型<span style="color: red;">*</span></label>
                    <div class="controls">
                        <form:select path="accountType">
                            <form:options items="${fns:getDictList('account_type')}" itemValue="value" itemLabel="label"/>
                        </form:select>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">账户余额<span style="color: red;">*</span></label>
                    <div class="controls">
                    <label class="control-label">账户余额</label>
                    <div class="controls">
                        <input type="text" readonly disabled/> <input type="button" value="查询"/>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">调账金额（元）<span style="color: red;">*</span></label>
                    <div class="controls">
                        <form:input path="adjustAmount" />
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">备注</label>
                    <div class="controls">
                        <form:textarea path="remark" cols="20" rows="5"/>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="form-actions">
                    <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
                    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.location.href='${ctx}/sys/user/'"/>
                </div>
            </td>
        </tr>
    </table>


</form:form>
</body>
</html>
