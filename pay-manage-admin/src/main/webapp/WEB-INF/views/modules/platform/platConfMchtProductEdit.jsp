<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>商户产品管理</title>
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
        
        function rateOrAmount() {
            if ( $("#feeType").val() === "1"){
                $("#feeRate").attr("disabled", "disabled");
                $("#feeAmount").attr("disabled", false);
            }else {
                $("#feeAmount").attr("disabled", "disabled");
                $("#feeRate").attr("disabled", false);
            }
        }

        //商户信息级联
        function mchtChange() {
            var code = $("#mchtId").find("option:selected").data("mchtcode");
            var status = $("#mchtId").find("option:selected").data("mchtstatus");
            var description = $("#mchtId").find("option:selected").data("mchtdescription");
            var mchtStatus;
            if (status == 1){
                mchtStatus = "启用";
            }else {
                mchtStatus = "禁用"
            }
            $("#mchtCode").val(code);
            $("#mchtStatus").val(mchtStatus);
            $("#description").val(description);
        }

        //商户信息级联
        function productChange() {
            var code = $("#productId").find("option:selected").data("productcode");
            $("#productCode").val(code);
        }

        $(function () {
            $("#mchtProductForm").validate({
                debug: true, //调试模式取消submit的默认提交功能
                //errorClass: "label.error", //默认为错误的样式类为：error
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function (form) {   //表单提交句柄,为一回调函数，带一个参数：form
                    form.submit();   //提交表单
                },

                rules: {
                    lowestFee: {
                        max: 99999,
                        number: true
                    },
                    feeRate: {
                        max: 99999,
                        number: true
                    },
                    feeAmount: {
                        max: 9999999,
                        number: true
                    },
                    settleLowestFee: {
                        max: 9999999,
                        number: true
                    },
                    settleHighestFee: {
                        max: 9999999,
                        number: true
                    }
                },
                messages: {
                    feeRate: {
                        required: '必填',
                        number: '请填写数字'
                    }
                }

            });
        });

        function edit(id) {
            document.forms[0].action = "${ctx}/bowei/repaymentEdit?id=" + id;
            document.forms[0].submit();
        }

        function del(id) {
            if (confirm("是否确认删除ID为“" + id + "”的记录？")) {
                document.forms[0].action = "${ctx}/bowei/repaymentDel?id=" + id;
                document.forms[0].submit();
            }
        }

        function ok() {
            var op = $("#op").attr("value");
            url = "${ctx}/channel/addSave";
            if (op == "edit") {
                url = "${ctx}/channel/editSave";
            }
            document.forms[0].action = url;
            document.forms[0].submit();
        }

    </script>
</head>
<body>

<div class="breadcrumb">
    <label><a href="#">商户产品管理</a> > <a href="#"><b>商户产品编辑</b></a></label>
</div>

<form id="mchtProductForm" action="${ctx}/platform/addPlatConfMchtProduct" method="post">
    <tags:message content="${message}" type="${messageType}"/>
    <input type="hidden" id="op" name="op" value="${op }"/>
    <c:if test="${op == 'edit'}"> <input type="hidden" name="mchtId" value="${productInfo.mchtId }"/> </c:if>
    <c:if test="${op == 'edit'}"> <input type="hidden" name="productId" value="${productInfo.productId }"/> </c:if>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>商户产品基本信息</label>
    </div>
    <tags:message content="${message}" type="${messageType}"/>
    <table class="table">
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">商户名称</label>
                    <div class="controls">
                        <select name="mchtId" class="input-xlarge" id="mchtId" onchange="mchtChange()"
                                <c:if test="${op == 'edit'}"> disabled="disabled" </c:if>>>
                            <option>--请选择--</option>
                            <c:forEach items="${mchtInfos}" var="mchtInfo">
                                <option data-mchtStatus="${mchtInfo.status}" data-mchtCode="${mchtInfo.mchtCode}"
                                        data-mchtDescription="${mchtInfo.description}"
                                        <c:if test="${productInfo.mchtId == mchtInfo.id}">selected</c:if>
                                        value="${mchtInfo.id}">${mchtInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">商户编码</label>
                    <div class="controls">
                        <input name="mchtCode" placeholder="" value="${productInfo.mchtCode}"
                               class="input-medium" type="text" id="mchtCode" readonly="readonly"/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">商户状态</label>
                      <div class="control-group">
                        <div class="controls">
                        <select name="mchtStatus" class="input-medium" id="status" disabled="disabled">
                            <option <c:if test="${mchtInfo.status == '1'}"> selected</c:if> value="1">启用</option>
                            <option <c:if test="${mchtInfo.status == '2'}"> selected</c:if> value="2">无效</option>
                            <option <c:if test="${mchtInfo.status == '3'}"> selected</c:if> value="3">待审核</option>
                        </select>
                        </div>
                      </div>
                </div>
            </td>
            <%--<td>--%>
                <%--<div class="control-group">--%>
                    <%--<label class="control-label">说明</label>--%>
                    <%--<div class="controls">--%>
                        <%--<textarea name="description" class="input-xlarge" id="description" readonly></textarea>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</td>--%>
        </tr>


    </table>
    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>支付产品信息</label>
    </div>
    <tags:message content="${message}" type="${messageType}"/>
    <table class="table" id="payTypeTable">
        <tr>
            <%--<td>--%>
                <%--<div class="control-group">--%>
                    <%--<label class="control-label">支付方式</label>--%>
                    <%--<div class="controls">--%>
                        <%--<select name="payType" class="input-xlarge" id="payType">--%>
                            <%--<option value="">--请选择--</option>--%>
                            <%--<c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">--%>
                                <%--<option value="${paymentTypeInfo.paymentType}">${paymentTypeInfo.paymentName}</option>--%>
                            <%--</c:forEach>--%>
                        <%--</select>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</td>--%>
            <td>
                <div class="control-group">
                    <label class="control-label">支付产品</label>
                    <div class="controls">
                        <select name="productId" class="input-xlarge" id="productId" onchange="productChange()"
                                <c:if test="${op == 'edit'}"> disabled="disabled" </c:if>>
                            <option>--请选择--</option>
                            <c:forEach items="${productFormInfos}" var="productFormInfo">
                                <option <c:if test="${productInfo.productId == productFormInfo.id}">selected</c:if>
                                        data-productCode="${productFormInfo.code}" value="${productFormInfo.id}">${productFormInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">产品编码</label>
                    <div class="controls">
                        <input name="productCode" placeholder="" value="${productInfo.productCode}"
                               class="input-medium" type="text" id="productCode"  readonly="readonly"/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">状态</label>
                    <div class="controls">
                        <select name="isValid" class="input-xlarge" id="isValid">
                            <option <c:if test="${productInfo.isValid == 1}">selected</c:if> value="1">启用</option>
                            <option <c:if test="${productInfo.isValid == 0}">selected</c:if> value="0">停用</option>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
    </table>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>资金结算信息</label>
    </div>
    <tags:message content="${message}" type="${messageType}"/>
    <table class="table">
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">结算方式</label>
                    <div class="controls">
                        <select name="settleMode" class="input-xlarge" id="settleMode">
                            <option value="">--请选择--</option>
                            <option
                                    <c:if test="${productInfo.settleMode == 1}">selected</c:if> value="1">收单对公
                            </option>
                            <option
                                    <c:if test="${productInfo.settleMode == 2}">selected</c:if> value="2">收单对私
                            </option>
                            <option
                                    <c:if test="${productInfo.settleMode == 3}">selected</c:if> value="3">代付
                            </option>
                            <option
                                    <c:if test="${productInfo.settleMode == 4}">selected</c:if> value="4">银行直清
                            </option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">结算周期</label>
                    <div class="controls">
                        <select name="settleCycle" class="input-xlarge" id="settleCycle">
                            <option value="">--请选择--</option>
                            <option
                                    <c:if test="${productInfo.settleCycle == 1}">selected</c:if> value="1">D0
                            </option>
                            <option
                                    <c:if test="${productInfo.settleCycle == 2}">selected</c:if> value="2">T0
                            </option>
                            <option
                                    <c:if test="${productInfo.settleCycle == 3}">selected</c:if> value="3">T1
                            </option>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label" for="settleLowestFee">最低手续费(分)</label>
                    <div class="controls">
                        <input name="settleLowestFee" value="${productInfo.settleLowestFee }" placeholder=""
                               class="input-xlarge"
                               type="text" id="settleLowestFee">
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">收费类型</label>
                    <div class="controls">
                        <select name="feeType" class="input-xlarge" id="feeType" onchange="rateOrAmount()">
                            <option value="">--请选择--</option>
                            <option
                                    <c:if test="${productInfo.feeType == 1}">selected</c:if> value="1">按笔收费
                            </option>
                            <option
                                    <c:if test="${productInfo.feeType == 2}">selected</c:if> value="2">按比例收费
                            </option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="feeRate">收费比率(‰)</label>
                    <div class="controls">
                        <input name="feeRate" value="${productInfo.feeRate }" placeholder=""
                               class="input-xlarge" type="text" id="feeRate">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="feeAmount">收费金额(/笔(分))</label>
                    <div class="controls">
                        <input name="feeAmount" value="${productInfo.feeAmount }" placeholder="" class="input-xlarge"
                               type="text" id="feeAmount">
                    </div>
                </div>
            </td>
        </tr>
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">生效时间</label>
                    <div class="controls">
                        <label class="control-label" for="activeNow">立即</label>
                        <input name="feeStatus" value="1" placeholder="" class="input-xlarge"
                               type="radio"
                               <c:if test="${productInfo.feeStatus == 1}">checked</c:if> id="activeNow">
                        <label class="control-label" for="activeThan">定时</label>
                        <input name="feeStatus" value="0" placeholder="" class="input-xlarge"
                               type="radio"
                               <c:if test="${productInfo.feeStatus == 0}">checked</c:if> id="activeThan">
                        <input id="activeTime" name="activeTime" type="text" class="input-medium Wdate"
                               value="${productInfo.activeTime}"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true, minDate:'%y-%M-%d'});"/>

                    </div>
                </div>
            </td>
        </tr>
    </table>

    <div class="breadcrumb">
        <input id="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);"
               name="btnCancel"/>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存" style="margin-left: 5px;"
               name="btnSubmit">
    </div>


</form>
</body>
</html>