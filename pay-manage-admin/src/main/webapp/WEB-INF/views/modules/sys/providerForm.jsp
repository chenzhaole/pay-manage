<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分平台/提供商管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#title").focus();
			$("#inputForm").validate({
				rules: {
					providerId: {remote: "${ctx}/sys/provider/checkProviderId?oldProviderId=${provider.providerId}"}
				},
				messages: {
					providerId: {remote: "分平台/提供商编号已存在，请重新输入"}
				},
				submitHandler: function(form){
					$("#btnSubmit").attr("disabled", true);
					$("#btnCancel").attr("disabled", true);
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
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/provider/list">分平台/提供商列表</a></li>
		<li class="active"><a href="${ctx}/sys/provider/form?id=${provider.id}">分平台/提供商<shiro:hasPermission name="sys:provider:edit">${not empty provider.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:provider:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="provider" action="${ctx}/sys/provider/save" method="post" class="form-horizontal">
		<tags:message content="${message}"/>
		<form:hidden path="id"/>
		<div class="control-group">
			<label class="control-label">编号:</label>
			<div class="controls">
				<form:input path="providerId" htmlEscape="false" maxlength="6" class="input-large digits required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">名称:</label>
			<div class="controls">
				<form:input path="providerName" htmlEscape="false" maxlength="20" class="input-large required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">对外显示名称:</label>
			<div class="controls">
				<form:input path="providerDisplayName" htmlEscape="false" maxlength="20" class="input-large"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单来源ID:</label>
			<div class="controls">
				<form:input path="tckIdent" htmlEscape="false" maxlength="2" class="input-large digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">接口地址:</label>
			<div class="controls">
				<form:input path="interfaceUrl" htmlEscape="false" maxlength="100" class="input-xxlarge required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">密钥:</label>
			<div class="controls">
				<form:input path="privateKey" htmlEscape="false" maxlength="32" class="input-large"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否开通:</label>
			<div class="controls">
				<form:radiobuttons path="isOpen" items="${fns:getDictList('provider_open_flag')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开通时间:</label>
			<div class="controls">
				<input id="openDate" name="openDate" type="text" readonly="readonly" maxlength="20" class="input-large Wdate"
					   value="<fmt:formatDate value="${provider.openDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:false,isShowToday:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">支付方式:</label>
			<div class="controls">
				<form:select path="payType">
					<form:options items="${fns:getDictList('provider_pay_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:provider:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.location.href='${ctx}/sys/provider/'"/>
		</div>
	</form:form>
</body>
</html>