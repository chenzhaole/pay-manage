<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>调账审批</title>
    <meta name="decorator" content="default"/>

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
    </script>
</head>
<body>


<ul class="nav nav-tabs">
    <li><a href="${ctx}/platform/adjust">调账列表</a></li>
    <li class="active"><a href="${ctx}/platform/adjust/viewAudit">调账审批</a></li>
</ul>
<form:form id="inputForm" modelAttribute="platAccountAdjust" action="${ctx}/platform/adjust/audit" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>

    <input type="hidden" name="platAccountAdjust.id" value="${platAccountAdjustOri.id}"/>
    <input id="auditStatusId" type="hidden" name="auditStatus" value="5"/>


    <tags:message content="${message}"/>
    <table class="table">
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">调账方向</label>
                    <div class="controls">
                        <c:if test="${platAccountAdjustOri.adjustType == 1}">
                            增加
                        </c:if>
                        <c:if test="${platAccountAdjustOri.adjustType == 2}">
                            减少
                        </c:if>
                        <c:if test="${platAccountAdjustOri.adjustType == 3}">
                            冻结
                        </c:if>
                        <c:if test="${platAccountAdjustOri.adjustType == 4}">
                            解冻
                        </c:if>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">商户名称：</label>
                    <div class="controls">
                        <label>
                            ${platAccountAdjustOri.mchtName}
                        </label>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">账户类型</label>
                    <div class="controls">
                        <c:if test="${platAccountAdjustOri.accountType == 1}">
                            结算账户
                        </c:if>
                        <c:if test="${platAccountAdjustOri.accountType == 2}">
                            现金账户
                        </c:if>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">审批状态</label>
                    <div class="controls">
                        <select class="adjust_class">
                            <option value="0">请选择</option>
                            <option value="4">通过</option>
                            <option value="5">拒绝</option>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">收费类型<span style="color: red;"></span></label>
                    <div class="controls">
                        <c:if test="${platAccountAdjustOri.feeType == 1}">
                            固定手续费
                        </c:if>
                        <c:if test="${platAccountAdjustOri.feeType == 2}">
                            按比例收费
                        </c:if>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">调账金额（元）<span style="color: red;">*</span></label>
                    <div class="controls">
                        <fmt:formatNumber type="number" value="${platAccountAdjustOri.adjustAmount*0.01}" pattern="0.00" maxFractionDigits="2"/>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">调账费率（‰）<span style="color: red;"></span></label>
                    <div class="controls">
                            ${platAccountAdjustOri.feeRate}

                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">备注</label>
                    <div class="controls">
                            ${platAccountAdjustOri.remark}
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">调账手续费（元）<span style="color: red;"></span></label>
                    <div class="controls">
                        <fmt:formatNumber type="number" value="${platAccountAdjustOri.feeAmount*0.01}" pattern="0.00" maxFractionDigits="2"/>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="form-actions">
                    <input id="btnSubmit" onclick="subAdjustForm()" class="btn btn-primary" type="button" value="提 交" />&nbsp;
                    <input id="btnCancel" class="btn" type="button" value="返 回"
                           onclick="window.location.href='${ctx}/platform/adjust'"/>
                </div>
            </td>
        </tr>
    </table>


</form:form>
<style type="text/css">
    .form-horizontal .controls{
        line-height: 23px;
        height: 23px;
    }
</style>
</body>
</html>
