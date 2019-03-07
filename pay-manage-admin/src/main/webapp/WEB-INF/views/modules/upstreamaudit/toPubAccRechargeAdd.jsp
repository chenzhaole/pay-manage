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

        function submitAudit() {
            //账户ID
            var accountId = $("#accountId").val();
            if(accountId == null || accountId == ''){
                alert("请选择电子账户!");
                return false;
            }

            //调账金额
            var adjustAmountIds = $("#adjustAmountId").val();
            if(adjustAmountIds == null || adjustAmountIds==''){
                alert("请输入调账金额!");
                return false;
            }

            //客服备注
            var customerMsgId = $("#customerMsgId").val();
            if(customerMsgId == null || customerMsgId==''){
                alert("请输入备注信息!");
                return false;
            }

            //备注
            $("#proofImage").val($("#preview").attr("src"));
            $("#inputForm").submit();
        }
    </script>


    <script src="${ctxStatic}/js/paste.js"></script>
    <script>
        // 初始化插件，传入展示图片标签的jq对象
        upImageInit($("#showimage"));
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
            <td>
                <div class="control-group">
                    <label class="control-label">电子账户名称：</label>
                    <div class="controls">
                        <label>
                            <select id="sourceDataId" name="sourceDataId" class="required" data-live-search="true">
                                <option value="">--请选择--</option>
                                <c:forEach items="${electronicAccounts}" var="account">
                                    <option value="${account.id}">${account.electronicAccountName}</option>
                                </c:forEach>
                            </select>
                        </label>
                    </div>
                </div>


                <div class="control-group">
                    <label class="control-label">电子账户名称：</label>
                    <div class="controls">
                        <label>
                            <select id="newDataId" name="newDataId" class="required" data-live-search="true">
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

            </td>

        </tr>
        <tr>
            <td>

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
                        <input id="adjustAmountId" type="number" name="amount" maxlength="10" name="amount"/>
                    </div>
                </div>
            </td>

        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">凭证</label>
                    <div class="controls">
                        <input type="hidden" name="proofImage" id="proofImage" value=""/>
                        <div style="width:400px;height:200px;border: 1px solid #000;   position: relative; left: 65px;" id="showimage">

                        </div>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">备注</label>
                    <div class="controls">
                        <textarea id="customerMsgId" name="customerMsg"></textarea>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="form-actions">
                    <input id="btnSubmit" class="btn btn-primary" type="button" onclick="submitAudit()" value="提 交" />&nbsp;
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
