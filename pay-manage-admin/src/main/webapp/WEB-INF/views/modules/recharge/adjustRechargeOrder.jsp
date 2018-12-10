<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <c:if test="${queryFlag ne 'true'}">
        <title>充值订单审批</title>
    </c:if>
    <c:if test="${queryFlag eq 'true'}">
        <title>充值订单详情</title>
    </c:if>

    <meta name="decorator" content="default"/>


    <link href="${ctxStatic}/js/zoomify.css" type="text/css" rel="stylesheet" />
    <script src="${ctxStatic}/js/zoomify.js" type="text/javascript"></script>


</head>
<body>


<ul class="nav nav-tabs">
    <c:if test="${queryFlag ne 'true'}">
        <li><a href="#">充值订单审批</a></li>
    </c:if>
    <c:if test="${queryFlag eq 'true'}">
        <li><a href="#">充值订单详情</a></li>
    </c:if>

</ul>


<form id="searchForm" action="${ctx}/mchtRecharge/commitAdjustRechargeOrder" method="post" class="breadcrumb form-search">
    <table class="table">
    <tr>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="">商户名称：</label>
                <div class="">
                    <label>
                        ${mchtInfo.name}
                    </label>
                </div>
            </div>
        </td>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">商户ID</label>
                <div class="controls">
                    ${mchtInfo.id}
                </div>
            </div>
        </td>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">订单号</label>
                <div class="controls">
                    ${auditRechargeOrder.platOrderId}
                </div>
            </div>
        </td>
    </tr>
    <tr>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="">订单金额</label>
                <div class="">
                    <label>
                        <fmt:formatNumber type="number" value="${auditRechargeOrder.amount*0.01}" pattern="0.00" maxFractionDigits="2"/> (元)
                    </label>
                </div>
            </div>
        </td>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">手续费金额</label>
                <div class="controls">
                    <fmt:formatNumber type="number" value="${auditRechargeOrder.mchtFeeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/> (元)
                </div>
            </div>
        </td>
        <td>

        </td>



    </tr>
    <tr>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">订单时间	</label>
                <div class="controls">
                    <fmt:formatDate value="${auditRechargeOrder.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </div>
            </div>
        </td>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">订单完成时间	</label>
                <div class="controls">
                    <fmt:formatDate value="${auditRechargeOrder.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </div>
            </div>
        </td>
    </tr>

    <tr>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="">收款账户</label>
                <div class="">
                    <label style="width: 30%">
                        ${rechargeConfig.compReceiptAcctNo}
                    </label>
                </div>
            </div>
        </td>
        <td class="bottom_border_class">
            <div class="control-group">
                <label class="control-label">收款名称</label>
                <div class="controls" style="width: 30%">
                    ${rechargeConfig.compReceiptAcctName}
                </div>
            </div>
        </td>
        <c:if test="${queryFlag eq 'true'}">
            <shiro:hasPermission name="mcht:proxy:customer">
                <td class="bottom_border_class">
                    <div class="control-group">
                        <label class="control-label">审核状态</label>
                        <div class="controls">
                            <c:if test="${auditRechargeOrder.auditStatus eq 'created'}">
                                未审核
                            </c:if>
                            <c:if test="${auditRechargeOrder.auditStatus eq 'customer_pass'}">
                                客服审核通过
                            </c:if>
                            <c:if test="${auditRechargeOrder.auditStatus eq'operate_pass'}">
                                运营审核通过
                            </c:if>
                            <c:if test="${auditRechargeOrder.auditStatus eq 'customer_refuse'}">
                                客服审核未通过
                            </c:if>
                            <c:if test="${auditRechargeOrder.auditStatus eq 'operate_refuse'}">
                                运营审核未通过
                            </c:if>
                            <c:if test="${auditRechargeOrder.auditStatus eq 'no_need_audit'}">
                                无需审核
                            </c:if>
                        </div>
                    </div>
                </td>
            </shiro:hasPermission>
        </c:if>

    </tr>
    <tr>
        <td style="width: 30%">
            <div class="control-group">
                <label class="control-label">商户留言</label>
                <div class="controls">
                    <span>${auditRechargeOrder.mchtMessage}</span>
                </div>
            </div>
        </td>
        <td>
            <div class="control-group">
                <label class="control-label">凭据</label>
                <div id="img_enlarge_id" class="controls" onclick="enlargeImg()">
                    <img id="" src="${auditRechargeOrder.imgUrl}" width="200px" height="200px">
                </div>
            </div>
        </td>

        <td>
            <input type="hidden" name="platOrderId" value="${auditRechargeOrder.platOrderId}">
                <input type="hidden" name="auditType" value="customer"/>
        </td>
    </tr>
    <tr>
        <c:if test="${queryFlag eq 'true'}">
            <shiro:hasPermission name="mcht:proxy:customer">
                <!--    不是客服审核通过的显示 客服留言 和 运营留言     -->
                <!--    客服审核通过      -->
                <c:if test="${auditRechargeOrder.auditStatus eq 'customer_pass' ||
                        auditRechargeOrder.auditStatus eq 'operate_pass' ||
                        auditRechargeOrder.auditStatus eq 'customer_refuse' ||
                        auditRechargeOrder.auditStatus eq 'operate_refuse'}">
                    <c:if test="${queryFlag eq 'true'}">
                        <td style="width: 30%">
                            <div class="control-group">
                                <label class="control-label">客服留言</label>
                                <div class="controls">
                                        ${auditRechargeOrder.extend1}
                                </div>
                            </div>
                        </td>
                        <td style="width: 30%">
                            <div class="control-group">
                                <label class="control-label">运营留言</label>
                                <div class="controls">
                                        ${auditRechargeOrder.extend2}
                                </div>
                            </div>
                        </td>
                    </c:if>
                </c:if>
            </shiro:hasPermission>
            <shiro:hasPermission name="mcht:proxy:operate">
                <!--    不是客服审核通过的显示 客服留言 和 运营留言     -->
                <!--    客服审核通过      -->
                <c:if test="${auditRechargeOrder.auditStatus eq 'customer_pass' ||
                        auditRechargeOrder.auditStatus eq 'operate_pass' ||
                        auditRechargeOrder.auditStatus eq 'customer_refuse' ||
                        auditRechargeOrder.auditStatus eq 'operate_refuse'}">
                    <c:if test="${queryFlag eq 'true'}">
                        <td>
                            <div class="control-group">
                                <label class="control-label">客服留言</label>
                                <div class="controls">
                                        ${auditRechargeOrder.extend1}
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="control-group">
                                <label class="control-label">运营留言</label>
                                <div class="controls">
                                        ${auditRechargeOrder.extend2}
                                </div>
                            </div>
                        </td>
                    </c:if>
                </c:if>
            </shiro:hasPermission>
        </c:if>
    </tr>
    <tr>
        <shiro:hasPermission name="mcht:proxy:customer">
            <c:if test="${auditRechargeOrder.auditStatus eq 'created'}">
                <c:if test="${queryFlag ne 'true'}">
                    <td>
                        <div class="control-group">
                            <label class="control-label">客服留言</label>
                            <div class="controls">
                                <textarea id="customerMessage" name="customerMessage" maxlength="100"></textarea>
                                &nbsp;&nbsp;<label style="color: red">最多输入100个字符</label>
                            </div>
                        </div>
                    </td>
                    <td>
                        <div class="control-group">
                            <label class="control-label">审核状态</label>
                            <div class="controls">
                                <select name="auditStatus">
                                    <option value="pass" selected>通过</option>
                                    <option value="refuse">拒绝</option>
                                </select>
                            </div>
                        </div>
                    </td>
                </c:if>
            </c:if>


        </shiro:hasPermission>
    </tr>


</table>
    <div style="position: relative; left: 45%;">
        <c:if test="${queryFlag ne 'true'}">
            <shiro:hasPermission name="mcht:proxy:customer">
                <c:if test="${auditRechargeOrder.auditStatus eq 'created'}">
                    <input  id="customerButId" onclick="checkCustomerBut()" type="submit" value="提交审批"/>
                </c:if>
            </shiro:hasPermission>
            <shiro:hasPermission name="mcht:proxy:operate">
                <c:if test="${auditRechargeOrder.auditStatus eq 'customer_pass'}">
                    <input id="operateButId" onclick="checkOperateBut()"  type="submit" value="提交审批"/>
                </c:if>
            </shiro:hasPermission>
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


