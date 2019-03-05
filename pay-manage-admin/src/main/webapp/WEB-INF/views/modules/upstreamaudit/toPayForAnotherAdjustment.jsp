<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>代付电子账户调账添加</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });


        $(function () {
            $("#inputForm").validate({
                rules: {
                },
                messages: {
                },
                submitHandler: function (form) {

                    $("#btnSubmit").attr("disable", true);

                    var balance = $("#balance").val();
                    var adjustAmount = $("#adjustAmount").val();
                    var adjustType = $("#adjustType").val();
                    var acctType =$("#accountType").val();

                    if (adjustType === "2"){
                        if(parseFloat(adjustAmount) > parseFloat(balance)){
                            confirmx('减少金额不能大于当前余额');
                            $("#btnSubmit").attr("disable", false);
                            return;
                        }
                    }
                    if(acctType === "1" && (adjustType ==="3" || adjustType==="4" )){
                        confirmx('账户类型为[结算账户],调账方向只能为增加或减少');
                        $("#btnSubmit").attr("disable", false);
                        return;
                    }

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
                var accountType =$("#accountType").val();
                if (mchtId == '') {
                    alert("请选择商户");
                } else {
                    $.ajax({
                        url: '${ctx}/platform/adjust/balance',
                        type: 'POST', //GET
                        async: true,    //或false,是否异步
                        data: {
                            'mchtId': mchtId,
                            'accountType':accountType
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

        function changeMcht() {
            $("#balance").val("0");
        }

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

        function changeAmount() {
            $("#balance").val("0");
        }

    </script>
</head>
<body>


<ul class="nav nav-tabs">
    <li><a href="${ctx}/caAccountAudit/queryCaAccountAudits?type=5">调账列表</a></li>
    <li class="active"><a href="${ctx}/caAccountAudit/insertCaAccountAudit">调账添加</a></li>
</ul>
<form:form id="inputForm" modelAttribute="" action="${ctx}/caAccountAudit/insertCaAccountAudit" method="post"
           class="form-horizontal">
    <tags:message content="${message}"/>
    <table class="table">
        <tr>
            <td >
                <div class="control-group">
                    <label class="control-label">支付方式</label>
                    <div class="controls">
                        <select name="type" class="input-xlarge" id="payType" onchange="changeAmount();">
                            <option value="5">支付</option>
                            <option value="4">代付</option>
                        </select>
                    </div>
                </div>
            </td>
            <td >
                <div class="control-group">
                    <label class="control-label">调账方向</label>
                    <div class="controls">
                        <select name="adjustType" class="input-xlarge" id="accountType" onchange="changeAmount();">
                            <option value="2">增加</option>
                            <option value="1">减少</option>
                        </select>
                    </div>
                </div>
            </td>

        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">电子账户名称：</label>
                    <div class="controls">
                        <label>
                            <select id="mchtId" name="accountId" class="required" data-live-search="true" onchange="changeMcht()">
                                <option value="">--请选择--</option>
                                <c:forEach items="${electronicAccounts}" var="account">
                                    <option value="${account.id}">${account.electronicAccountName}</option>
                                </c:forEach>
                            </select>
                        </label>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">账户类型</label>
                    <div class="controls">
                        <select name="accountType" class="input-xlarge" id="accountTypeId" onchange="changeAmount();">
                            <option value="1">电子账户</option>
<%--
                            <option value="2">公户账户</option>
--%>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">电子账户余额（元）</label>
                    <div class="controls">
                        <input type="text" readonly disabled id="balance" value="0"/>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">调账金额（元）<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input type="number" name="amount" maxlength="10" name="amount"/>
                    </div>
                </div>
            </td>

        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">凭证</label>
                    <div class="controls">
                        <input type="hidden" name="picUrl" id="proofImage" value=""/>
                        <div style="width:400px;height:200px;border: 1px solid #000;   position: relative; left: 65px;" id="showimage">

                        </div>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">备注</label>
                    <div class="controls">
                        <textarea name="customerMsg"></textarea>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="form-actions">
                    <input id="btnSubmit" class="btn btn-primary" type="submit" value="提 交" />&nbsp;
                    <input id="btnCancel" class="btn" type="button" value="返 回"
                           onclick="window.location.href='${ctx}/platform/adjust'"/>
                </div>
            </td>
        </tr>
    </table>


</form:form>
<script src="${ctxStatic}/js/paste.js"></script>
<script>
    // 初始化插件，传入展示图片标签的jq对象
    upImageInit($("#showimage"));
</script>
</body>

</html>
