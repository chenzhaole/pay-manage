<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <c:if test="${queryFlag ne 'true'}">
        <title>支付代付电子账户调账审批</title>
    </c:if>
    <c:if test="${queryFlag eq 'true'}">
        <title>支付代付电子账户调账审批</title>
    </c:if>
    <meta name="decorator" content="default"/>
    <link href="${ctxStatic}/js/zoomify.css" type="text/css" rel="stylesheet" />
    <script src="${ctxStatic}/js/zoomify.js" type="text/javascript"></script>
</head>
<body>


<ul class="nav nav-tabs">
    <c:if test="${queryFlag ne 'true'}">
        <li><a href="#">支付代付电子账户调账审批</a></li>
    </c:if>
    <c:if test="${queryFlag eq 'true'}">
        <li><a href="#">支付代付电子账户调账审批</a></li>
    </c:if>
</ul>


<form id="searchForm" action="${ctx}/caAccountAudit/updateCaAccountAuditById" method="post" class="breadcrumb form-search">
    <table class="table">
    <tr>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="">电子账户名称：</label>
                <div class="">
                    <label>
                        ${accountAudit.accountId}
                    </label>
                </div>
            </div>
        </td>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">电子账户号</label>
                <div class="controls">
                    ${accountAudit.accountId}
                </div>
            </div>
        </td>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">通道商户</label>
                <div class="controls">
                    ${accountAudit.electronicAccount.electronicAccountName}
                </div>
            </div>
        </td>
    </tr>
    <tr>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">订单时间	</label>
                <div class="controls">
                    <fmt:formatDate value="${accountAudit.createdTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </div>
            </div>
        </td>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">订单完成时间	</label>
                <div class="controls">
                    <fmt:formatDate value="${accountAudit.updatedTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </div>
            </div>
        </td>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">审核状态</label>
                <div class="controls">
                    <c:if test="${accountAudit.auditStatus eq '0'}">
                        无需审核
                    </c:if>
                    <c:if test="${accountAudit.auditStatus eq '1'}">
                        创建未审核
                    </c:if>
                    <c:if test="${accountAudit.auditStatus eq '2'}">
                        客服审核通过
                    </c:if>
                    <c:if test="${accountAudit.auditStatus eq'3'}">
                        客服审核拒绝
                    </c:if>
                    <c:if test="${accountAudit.auditStatus eq '4'}">
                        运营审核通过
                    </c:if>
                    <c:if test="${accountAudit.auditStatus eq '5'}">
                        运营审核拒绝
                    </c:if>
                </div>
            </div>
        </td>
    </tr>

    <tr>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="">账户类型</label>
                <div class="controls">
                    电子账户
                </div>
            </div>
        </td>
        <td>
            <div class="control-group">
                <label class="control-label">商户留言</label>
                <div class="controls">
                    <span>${accountAudit.customerMsg}</span>
                </div>
            </div>
        </td>
        <!--    不是客服审核通过的显示 客服留言 和 运营留言     -->
        <!--    客服审核通过      -->
        <c:if test="${accountAudit.auditStatus eq 'customer_pass' ||
                        accountAudit.auditStatus eq 'operate_pass' ||
                        accountAudit.auditStatus eq 'customer_refuse' ||
                        accountAudit.auditStatus eq 'operate_refuse'}">
                <td width="30%">
                    <div class="control-group">
                        <label class="control-label">客服留言</label>
                        <div class="controls">
                                ${accountAudit.customerMsg}
                        </div>
                    </div>
                </td>

        </c:if>
        <c:if test="auditRechargeOrder.auditStatus eq 'operate_pass' ||
                        auditRechargeOrder.auditStatus eq 'operate_refuse'}">
            <td width="30%">
                <div class="control-group">
                    <label class="control-label">运营留言</label>
                    <div class="controls">
                            ${accountAudit.operateMsg}
                    </div>
                </div>
            </td>
        </c:if>
    </tr>
    <tr>
        <td>
            <div class="control-group">
                <label class="control-label">凭据</label>
                <div id="img_enlarge_id" class="controls" onclick="enlargeImg()">
                    <img src="${accountAudit.picUrl}" width="200px" height="200px">
                </div>
            </div>
        </td>
        <c:if test="${queryFlag ne 'true'}">

            <td width="30%">
                <div class="control-group">
                    <label class="control-label">运营留言</label>
                    <div class="controls">
                        <textarea id="operateMsg" name="operateMsg" maxlength="100"></textarea>
                        &nbsp;&nbsp;<label style="color: red">最多输入100个字符</label>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">审核状态</label>
                    <div class="controls">
                        <select name="auditStatus">
                            <option value="4" selected>通过</option>
                            <option value="5">拒绝</option>
                        </select>
                    </div>
                </div>
            </td>
        </c:if>

        <td>
            <input type="hidden" name="id" value="${accountAudit.id}">
            <input type="hidden" name="type" value="${accountAudit.type}"/>
        </td>
    </tr>


</table>
    <div style="position: relative; left: 45%;">
        <c:if test="${queryFlag ne 'true'}">
            <c:if test="${accountAudit.auditStatus eq '1'}">
                <input id="customerButId" onclick="checkCustomerBut()" type="submit" value="提交审批"/>
            </c:if>
        </c:if>
    </div>
</form>

<style type="text/css">
    .form-horizontal .controls{
        line-height: 23px;
        height: 23px;
    }
    .bottom_border_class{
        border-bottom: 1px solid #ddd;
    }

</style>

<script type="text/javascript">
    function subAdjustForm() {
        var checkValue=$(".adjust_class").val(); //获取Select选择的Value
        if(checkValue == null || checkValue == '' || checkValue == 0){
            alert("请选择审批状态.")
            return false;
        }
        $("#auditStatusId").val(checkValue);
        $("#inputForm").submit();
    }
    $('#img_enlarge_id img').zoomify();
    
    
    function checkCustomerBut() {
        document.getElementById("customerButId").setAttribute("disabled", true);
        $("#searchForm").submit();
    }
    function checkOperateBut() {
        document.getElementById("operateButId").setAttribute("disabled", true);
        $("#searchForm").submit();
    }
</script>
</body>
</html>


