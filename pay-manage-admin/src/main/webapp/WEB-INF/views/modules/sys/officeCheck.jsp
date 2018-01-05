<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微门户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});

            /**
             * 审核不通过
             */
            $("#btnFail").click(function(){
                if ($.trim($("#failReason").val()) == "") {
                    top.$.jBox.tip('请输入审核不通过的原因','warning');
                    return false;
                }
                $("#checkResult").val("3");
                top.$.jBox.confirm('确认审核不通过吗？', "审核确认", function(v, h, f) {
                    if (v === 'ok') {
                        $("#checkForm").submit();
                    }
                })
            });

            /**
             * 审核通过
             */
            $("#btnSubmit").click(function(){
                $("#checkResult").val("0");
                top.$.jBox.confirm('确认审核通过吗？', "审核确认", function(v, h, f) {
                    if (v === 'ok') {
                        $("#checkForm").submit();
                    }
                })
            });

            $("#failReasonSelect").change(function(){
                if ($(this).val() == "0") {
                    $("#failReason").val("");
                    $("#failReason").show();
                } else {
                    $("#failReason").hide();
                    $("#failReason").val($(this).val());
                }
            });
            $("#failReason").val($("#failReasonSelect").val());
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/office/">微门户列表</a></li>
		<li class="active"><a href="${ctx}/sys/office/checkPage?id=${office.id}">微门户审核</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="office" action="${ctx}/sys/office/save" method="post" class="form-horizontal" enctype="multipart/form-data">
        <tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">归属区域:</label>
			<div class="controls">
                <form:input path="area.name" htmlEscape="false" readonly="true" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">微门户名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" readonly="true" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">微门户编码:</label>
			<div class="controls">
				<form:input path="code" htmlEscape="false" readonly="true" maxlength="10" class="required"/>
			</div>
		</div>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">微门户类型:</label>--%>
			<%--<div class="controls">--%>
				<%--<form:select path="type">--%>
					<%--<form:options items="${fns:getDictList('sys_office_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>--%>
				<%--</form:select>--%>
			<%--</div>--%>
		<%--</div>--%>
		<div class="control-group">
			<label class="control-label">联系地址:</label>
			<div class="controls">
				<form:input path="address" htmlEscape="false" readonly="true" maxlength="50"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮政编码:</label>
			<div class="controls">
				<form:input path="zipCode" htmlEscape="false" readonly="true" maxlength="50" class="digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">负责人:</label>
			<div class="controls">
				<form:input path="master" htmlEscape="false" readonly="true" maxlength="50"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话:</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" readonly="true" maxlength="50" class="digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">传真:</label>
			<div class="controls">
				<form:input path="fax" htmlEscape="false" readonly="true" maxlength="50"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱:</label>
			<div class="controls">
				<form:input path="email" htmlEscape="false" readonly="true" maxlength="50" class="email"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">微门户Logo:</label>
			<div class="controls">
                <c:if test="${not empty office.portalInfo.companyLogo}">
                    <img src="${imgServer}${office.portalInfo.companyLogo}">
                </c:if>
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">微门户官网:</label>
            <div class="controls">
                <form:input path="portalInfo.domainAddress" htmlEscape="false" readonly="true" maxlength="50"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">微信公众号:</label>
            <div class="controls">
                <form:input path="portalInfo.wechatAccount" htmlEscape="false" readonly="true" maxlength="50"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">微信公众号二维码:</label>
            <div class="controls">
                <c:if test="${not empty office.portalInfo.wechatBarcode}">
                    <img src="${imgServer}${office.portalInfo.wechatBarcode}">
                </c:if>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">客服电话:</label>
            <div class="controls">
                <form:input path="portalInfo.customerServicePhone" htmlEscape="false" readonly="true" maxlength="50" class="digits"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">客服邮箱:</label>
            <div class="controls">
                <form:input path="portalInfo.customerServiceEmail" htmlEscape="false" readonly="true" maxlength="50" class="email"/>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" readonly="true" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
	</form:form>

    <form id="checkForm" action="${ctx}/sys/office/check" method="post" class="form-horizontal">
        <div class="control-group">
            <label class="control-label">审核不通过原因:</label>
            <div class="controls">
                <input type="hidden" name="checkResult" id="checkResult" value="" />
                <input type="hidden" name="officeId" id="officeId" value="${office.id}" />
                <select id="failReasonSelect" name="failReasonSelect">
                    <option value="">-请选择-</option>
                    <c:forEach items="${reasonList}" var="reason" varStatus="s">
                        <option value="${reason.failReason}" title="${reason.failReason}">${fns:abbr(reason.failReason, 30)}</option>
                    </c:forEach>
                    <option value="0">其他...</option>
                </select>
                <br>
                <textarea style="display: none;" id="failReason" name="failReason" rows="4" maxlength="200" class="input-xxlarge"></textarea>
            </div>
        </div>
        <div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.location.href='${ctx}/sys/office/'"/>
			<input id="btnFail" class="btn" type="button" value="审核不通过"/>
            <input id="btnSubmit" class="btn btn-primary" type="button" value="审核通过"/>&nbsp;
        </div>
    </form>
</body>
</html>