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
                    document.forms[0].action = "${ctx}/platform/addCardBin";
                    document.forms[0].submit();
                },
                errorPlacement: function (error, element) {
                    error.appendTo(element.parent());
                },
                rules: {
                    cardBinNo: {
                        alnum: true,
                        required: true,
                        maxlength: 32
                    },
                    cardName: {
                        required: true,
                        maxlength: 32
                    },
                    bankCode: {
                        required: true
                    },
                    orgNo: {
                        alnum: true,
                        maxlength: 32
                    },
                    cardLength: {
                        number: true,
                        max: 99
                    },
                    cardType: {
                        required: true
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
        <th><a href="#">平台银行卡BIN管理</a> > <a href="#"><b>平台银行卡BIN编辑</b></a></th>
    </label>
</div>

<form id="channelForm" action="" method="post">
    <input type="hidden" id="op" name="op" value="${op }"/>
    <input type="hidden" name="id" value="${platCardBin.id }"/>

    <div class="breadcrumb">
        <label>
            <th>卡BIN信息</th>
        </label>
    </div>
    <table class="table">
        <tr>

            <td  >
                <div class="control-group">
                    <label class="control-label">卡BIN代码<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="cardBinNo" value="${platCardBin.cardBinNo }" placeholder=""
                               type="text" id="cardBinNo" <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">卡名称<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="cardName" value="${platCardBin.cardName }" placeholder=""
                               type="text" id="cardName" class=" input">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">银行名称<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="bankCode" class="selectpicker bla bla bli" data-live-search="true"
                                <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                            <option value="">--请选择--</option>
                            <c:forEach items="${platBanks}" var="platBank">
                                <option data-bankCode="${platBank.bankCode }"
                                        <c:if test="${platCardBin.bankCode == platBank.bankCode}">selected</c:if>
                                        value="${platBank.bankCode}">${platBank.bankName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>

        </tr>
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">机构代码<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="orgNo" value="${platCardBin.orgNo }" placeholder=""
                               type="text" id="orgNo" class=" input">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">卡长度<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="cardLength" value="${platCardBin.cardLength }" placeholder=""
                               type="text" id="cardLength" class=" input">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">卡类型</label>
                    <div class="controls">
                        <select name="cardType" class="input-xlarge" id="cardType">
                            <option>--请选择--</option>
                            <option
                                    <c:if test="${platCardBin.cardType == 'DEBIT'}">selected</c:if> value="DEBIT">借记卡
                            </option>

                            <option
                                    <c:if test="${platCardBin.cardType == 'CREDIT'}">selected</c:if> value="CREDIT">信用卡
                            </option>

                            <option
                                    <c:if test="${platCardBin.cardType == 'PREPAID'}">selected</c:if> value="PREPAID">预付卡
                            </option>

                            <option
                                    <c:if test="${platCardBin.cardType == 'SEMI_CRE'}">selected</c:if> value="SEMI_CRE">准贷记卡
                            </option>
                        </select>
                    </div>
                </div>
            </td>

        </tr>
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">账户类型</label>
                    <div class="controls">
                        <select name="accType" class="input-xlarge" id="accType">
                            <option>--请选择--</option>
                            <option
                                    <c:if test="${platCardBin.accType == '1'}">selected</c:if> value="1">个人账户
                            </option>

                            <option
                                    <c:if test="${platCardBin.accType == '2'}">selected</c:if> value="2">对公账户
                            </option>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">状态</label>
                    <div class="controls">
                        <select name="status" class="input-xlarge" id="status">
                            <option value="1">有效</option>
                            <option
                                    <c:if test="${platCardBin.status == 2}">selected</c:if> value="2">无效
                            </option>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label" for="extend">说明</label>
                    <div class="controls">
                        <textarea name="extend" placeholder="" id="extend"
                                  style="width:500px;" rows="3">${platCardBin.extend}</textarea>
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