<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>通道银行管理</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/js/select2.js"></script>
    <link href="${ctxStatic}/css/select2.css" rel="stylesheet" />
    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').select2({
            });
        });


        $(function () {
            jQuery.validator.addMethod("alnum", function (value, element) {
                return this.optional(element) || /^[a-zA-Z0-9]+$/.test(value);
            }, "只能包括英文字母和数字");

            jQuery.validator.addMethod("MaxMoneyCheck", function (value, element) {
                var maxMoney = value;
                var minMoneyEle = $(element).siblings("input").val();
                var maxMoneyInt = parseInt(maxMoney);
                var minMoneyEleInt = parseInt(minMoneyEle);
                if(maxMoneyInt < minMoneyEleInt){
                    return false;
                }
                return true;
            }, "开始金额不能大于结束金额");

            jQuery.validator.addMethod("MinMoneyCheck", function (value, element) {
                var minMoney = value;
                var maxMoneyEle = $(element).siblings("input").val();
                var minMoneyInt = parseInt(minMoney);
                var maxMoneyEleInt = parseInt(maxMoneyEle);
                if(minMoneyInt > maxMoneyEleInt){
                    return false;
                }
                return true;
            }, "开始金额不能大于结束金额");

            $("#channelForm").validate({
                debug: false, //调试模式取消submit的默认提交功能
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function (form) {   //表单提交句柄,为一回调函数，带一个参数：form
                    showShadow();
                    document.forms[0].action = "${ctx}/channel/addChanPaytypeBank";
                    document.forms[0].submit();
                },
                errorPlacement: function (error, element) {
                    error.appendTo(element.parent());
                },
                rules: {
                    chanCode: {
                        required: true
                    },
                    payType: {
                        required: true
                    },
                    bankName: {
                        required: true
                    },
                    platBankCode: {
                        required: true
                    },
                    chanBankCode: {
                        alnum: true,
                        required: true,
                        maxlength: 32
                    },
                    debitMinMoney: {
                        number: true,
                        max: 999999,
                        MinMoneyCheck:true
                    },
                    debitMaxMoney: {
                        number: true,
                        max: 999999,
                        MaxMoneyCheck:true
                    },
                    debitTotalMoney: {
                        number: true,
                        max: 999999
                    },
                    creditMinMoney: {
                        number: true,
                        max: 999999,
                        MinMoneyCheck:true
                    },
                    creditMaxMoney: {
                        number: true,
                        max: 999999,
                        MaxMoneyCheck:true
                    },
                    creditTotalMoney: {
                        number: true,
                        max: 999999
                    },
                    passbookMinMoney: {
                        number: true,
                        max: 999999,
                        MinMoneyCheck:true
                    },
                    passbookMaxMoney: {
                        number: true,
                        max: 999999,
                        MaxMoneyCheck:true
                    },
                    passbookTotalMoney: {
                        number: true
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
        <th><a href="#">通道支付方式银行管理</a> > <a href="#"><b>通道支付方式银行编辑</b></a></th>
    </label>
</div>

<form id="channelForm" action="" method="post">
    <input type="hidden" id="op" name="op" value="${op }"/>
    <input type="hidden" name="id" value="${chanBank.id }"/>

    <div class="breadcrumb">
        <label>
            <th>基本信息</th>
        </label>
    </div>
    <table class="table">
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">支付通道<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="chanCode" class="input-xlarge" id="chanCode" onchange="getName()"
                                <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                            <option>--请选择--</option>
                            <c:forEach items="${chanInfos}" var="chanInfo">
                                <option data-chanCode="${chanInfo.chanCode }"
                                        <c:if test="${chanBank.chanCode == chanInfo.chanCode}">selected</c:if>
                                        value="${chanInfo.chanCode}">${chanInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">支付方式<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="payType" class="input-xlarge" id="payType" onchange="getName()"
                                <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                            <option value="">--请选择--</option>
                            <c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
                                <option
                                        <c:if test="${chanBank.payType == paymentTypeInfo.code}">selected</c:if>
                                        value="${paymentTypeInfo.code}">${paymentTypeInfo.desc}</option>
                            </c:forEach>
                        </select>
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
                                    <c:if test="${chanBank.status == 2}">selected</c:if> value="2">无效
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
                                  style="width:500px;" rows="3">${chanBank.extend}</textarea>
                    </div>
                </div>
            </td>

        </tr>
    </table>


    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>
            <th>银行信息</th>
        </label>
    </div>
    <tags:message content="${message}" type="${messageType}"/>
    <table class="table">
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">平台银行名称<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="platBankCode" class="selectpicker bla bla bli" data-live-search="true"
                                <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                            <option value="">--请选择--</option>
                            <c:forEach items="${platBanks}" var="platBank">
                                <option data-platBankCode="${platBank.bankCode }"
                                        <c:if test="${chanBank.platBankCode == platBank.bankCode}">selected</c:if>
                                        value="${platBank.bankCode}">${platBank.bankName}-${platBank.bankCode}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>

            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="chanBankCode">通道银行代码<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="chanBankCode" value="${chanBank.chanBankCode }" placeholder=""
                               type="text" id="chanBankCode" class=" input">
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="chanBankName">通道银行名称<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="chanBankName" value="${chanBank.chanBankName }" placeholder=""
                               type="text" id="chanBankName" class=" input">
                    </div>
                </div>
            </td>

            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="chanLineCode">通道银行联行号<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="chanLineCode" value="${chanBank.chanLineCode }" placeholder=""
                               type="text" id="chanLineCode" class=" input">
                    </div>
                </div>
            </td>

        </tr>
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">借记卡单笔限额<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="debitMinMoney" value="${chanBank.debitMinMoney }" placeholder=""
                               type="text" id="debitMinMoney" class=" input-small">
                        <label class="control-label">到</label>
                        <input name="debitMaxMoney" value="${chanBank.debitMaxMoney }" placeholder=""
                               type="text" id="debitMaxMoney" class=" input-small">
                    </div>
                </div>
            </td>

            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">信用卡单笔限额<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="creditMinMoney" value="${chanBank.creditMinMoney }" placeholder=""
                               type="text" id="creditMinMoney" class=" input-small">
                        <label class="control-label">到</label>
                        <input name="creditMaxMoney" value="${chanBank.creditMaxMoney }" placeholder=""
                               type="text" id="creditMaxMoney" class=" input-small">
                    </div>
                </div>
            </td>

            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">存折单笔限额<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="passbookMinMoney" value="${chanBank.passbookMinMoney }" placeholder=""
                               type="text" id="passbookMinMoney" class=" input-small">
                        <label class="control-label">到</label>
                        <input name="passbookMaxMoney" value="${chanBank.passbookMaxMoney }" placeholder=""
                               type="text" id="passbookMaxMoney" class=" input-small">
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" >借记卡每日限额<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="debitTotalMoney" value="${chanBank.debitTotalMoney }" placeholder=""
                               type="text" id="debitTotalMoney" class=" input-small">
                    </div>
                </div>
            </td>

            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" >信用卡每日限额<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="creditTotalMoney" value="${chanBank.creditTotalMoney }" placeholder=""
                               type="text" id="creditTotalMoney" class=" input-small">
                    </div>
                </div>
            </td>

            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" >存折每日限额<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="passbookTotalMoney" value="${chanBank.passbookTotalMoney }" placeholder=""
                               type="text" id="passbookTotalMoney" class=" input-small">
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