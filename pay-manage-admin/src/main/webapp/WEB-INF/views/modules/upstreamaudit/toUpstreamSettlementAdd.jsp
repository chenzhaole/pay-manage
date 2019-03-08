<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>代付电子账户调账添加</title>
    <meta name="decorator" content="default"/>

    <style type="text/css">
        #electronicAccountId{
            display: none;
        }
        #publicAccountInfoId{
            display: none;
        }
    </style>

    <script type="text/javascript">
        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });

        function submitAudit() {
            //账户ID
            var sourceDataId = $("#sourceDataId").val();
            if(sourceDataId == null || sourceDataId == ''){
                alert("请选择对公账户!");
                return false;
            }

            //账户类型
            var accountType = $("#accountType").val();
            if(accountType == null || accountType == ''){
                alert("请选择账户类型!");
                return false;
            }else{
                if(accountType == 1){
                    var electronicAccountIdSelected = $("#electronicAccountIdSelected").val();
                    if(electronicAccountIdSelected == null || electronicAccountIdSelected ==''){
                        alert("请选择电子账户!");
                        return false;
                    }else{
                        $("#newDataId").val(electronicAccountIdSelected);
                    }
                }else if(accountType == 2){
                    var publicAccountInfoIdSelected = $("#publicAccountInfoIdSelected").val();
                    if(publicAccountInfoIdSelected == null || publicAccountInfoIdSelected ==''){
                        alert("请选择对公账户!");
                        return false;
                    }else{
                        $("#newDataId").val(publicAccountInfoIdSelected);
                    }
                }
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


        function acctTypeChange() {
            var accountType = $("#accountType").val();
            if(accountType == 1){
                $("#electronicAccountId").css('display','block');
                $("#publicAccountInfoId").css('display','none');

                $("#accountTypeId").val("1");

            }else if(accountType == 2){
                $("#electronicAccountId").css('display','none');
                $("#publicAccountInfoId").css('display','block');

                $("#accountTypeId").val("2");
            }else{
                $("#publicAccountInfoId").css('display','none');
                $("#electronicAccountId").css('display','none');
                console.log("请选择账户类型.");
            }


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
    <li><a href="${ctx}/caAccountAudit/queryCaAccountAudits?type=2">调账列表</a></li>
    <li class="active"><a href="${ctx}/caAccountAudit/insertCaAccountAudit">调账添加</a></li>
</ul>
<form:form id="inputForm" modelAttribute="" action="${ctx}/caAccountAudit/insertCaAccountAudit" method="post"
           class="form-horizontal">
    <tags:message content="${message}"/>
    <table class="table">
        <tr>
            <td>
                    <label>
                        <div class="control-group">
                            <label class="control-label">出款->公户账户名称：</label>
                            <div class="controls">
                                <label>
                                    <select id="sourceDataId" name="sourceDataId" class="required" data-live-search="true">
                                        <option value="">--请选择--</option>
                                        <c:forEach items="${publicAccountInfos}" var="account">
                                            <option value="${account.publicAccountCode}">${account.publicAccountName}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                            </div>
                        </div>
                    </label>

                    <label>
                        <div class="control-group">
                            <label>
                                <label class="control-label">入款->账户类型：</label>
                                <div class="controls">
                                    <label>
                                        <select id="accountType" name="accountType" class="required" data-live-search="true" onchange="acctTypeChange()">
                                            <option value="">--请选择--</option>
                                            <option value="1">电子账户</option>
                                            <option value="2">对公账户</option>
                                        </select>
                                    </label>
                                </div>
                            </label>
                            <label>
                                <label class="control-label">入款->账户名称：</label>
                                <div class="controls" id="electronicAccountId">
                                    <label>
                                        <select id="electronicAccountIdSelected" name="" class="required" data-live-search="true">
                                            <option value="">--请选择--</option>
                                            <c:forEach items="${electronicAccounts}" var="account">
                                                <option value="${account.id}">${account.electronicAccountName}</option>
                                            </c:forEach>
                                        </select>
                                    </label>
                                </div>
                                <div class="controls" id="publicAccountInfoId">
                                    <label>
                                        <select id="publicAccountInfoIdSelected" name="" class="required" data-live-search="true">
                                            <option value="">--请选择--</option>
                                            <c:forEach items="${publicAccountInfos}" var="account">
                                                <option value="${account.publicAccountCode}">${account.publicAccountName}</option>
                                            </c:forEach>
                                        </select>
                                    </label>
                                </div>
                            </label>
                        </div>
                    </label>
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
                    <label class="control-label">入账金额（元）<span style="color: red;">*</span></label>
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
    <input id="accountTypeId" type="hidden" name="accountType" value="1"/>
    <input type="hidden" name="adjustType" value="1"/>
    <input type="hidden" name="type" value="2"/>
    <input type="hidden" id="newDataId" name="newDataId" value=""/>
</form:form>
<script src="${ctxStatic}/js/paste.js"></script>
<script>
    // 初始化插件，传入展示图片标签的jq对象
    upImageInit($("#showimage"));
</script>
</body>

</html>
