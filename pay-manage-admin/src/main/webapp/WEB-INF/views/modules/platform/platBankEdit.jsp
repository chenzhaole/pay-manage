<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>卡BIN管理</title>
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

<div class="breadcrumb">
    <label>
        <th><a href="#">平台银行管理</a> > <a href="#"><b>平台银行编辑</b></a></th>
    </label>
</div>

<div class="shadow" style="display:block;">
    <div id="tbl_brand_processing" class="dataProcessing">
        <img src="${ctxStatic}/images/loading.gif"><span>&nbsp;&nbsp;处理中...</span>
    </div>
</div>

<form id="channelForm" action="" method="post">
    <input type="hidden" id="op" name="op" value="${op }"/>
    <input type="hidden" name="id" value="${platBank.id }"/>

    <div class="breadcrumb">
        <label>
            <th>银行信息</th>
        </label>
    </div>
    <table class="table">
        <tr>

            <td  >
                <div class="control-group">
                    <label class="control-label">银行代码<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="bankCode" value="${platBank.bankCode }" placeholder=""
                               type="text" id="bankCode"  <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">银行名称<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="bankName" value="${platBank.bankName }" placeholder=""
                               type="text" id="bankName" class=" input">
                    </div>
                </div>
            </td>


        </tr>
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">状态</label>
                    <div class="controls">
                        <select name="status" class="input-xlarge" id="status">
                            <option value="1">有效</option>
                            <option
                                    <c:if test="${platBank.status == 2}">selected</c:if> value="2">无效
                            </option>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label" for="extend">备注说明</label>
                    <div class="controls">
                        <textarea name="extend" placeholder="" id="extend"
                                  style="width:500px;" rows="3">${platBank.extend}</textarea>
                    </div>
                </div>
            </td>

        </tr>
    </table>

    <div class="breadcrumb">
        <input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
        <input name="btnSubmit" class="btn btn-primary" type="submit" value="保存" style="margin-left: 5px;">
    </div>


</form>
</body>
</html>