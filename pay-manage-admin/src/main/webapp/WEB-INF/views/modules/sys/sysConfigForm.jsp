<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统参数</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#value").focus();
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

			$("#category").change(function(){
				changeCategory();
				$("#configValue").val("");
			});

			function changeCategory() {
				var type = $("#category").val();
				var inputEle = "";
				if (type == "digits") {//数字
					inputEle = '<input type="text" maxlength="6" value="" class="required digits" name="configValue" id="configValue">';
				} else if (type == "date") {//日期
					inputEle = '<input type="text" maxlength="20" value="" class="required" name="configValue" id="configValue"' +
					' onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\',isShowClear:true,readOnly:true,firstDayOfWeek:1,isShowOK:false,isShowToday:false})"/>';
				} else if (type == "time_HHmm") {//时间
					inputEle = '<input type="text" maxlength="20" value="" class="required" name="configValue" id="configValue"' +
					' onclick="WdatePicker({dateFmt:\'HH:mm\',isShowClear:true,readOnly:true,firstDayOfWeek:1,isShowOK:false,isShowToday:false})"/>';
				} else {
					inputEle = '<input type="text" maxlength="100" value="" class="required" name="configValue" id="configValue">';
				}
				$("#valueContainer").html(inputEle);
				$("#configValue").val("${sysConfig.configValue}");
			}
			changeCategory();
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <li><a href="${ctx}/sys/config">系统参数</a></li>
		<li class="active"><a href="${ctx}/sys/config/form">参数添加</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="sysConfig" action="${ctx}/sys/config/saveForm" method="post" class="form-horizontal">
        <tags:message content="${message}"/>
		<input type="hidden" name="oldConfigName" value="${sysConfig.configName}">
		<div class="control-group">
			<label class="control-label">参数描述:<span style="color: red"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" maxlength="50" class="required input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">参数名:<span style="color: red"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="configName" htmlEscape="false" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group" id="typeDiv">
			<label class="control-label">类型:<span style="color: red"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:select id="category" path="category">
					<form:options items="${fns:getDictList('sys_config_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">参数值:<span style="color: red"><span style="color: red;">*</span></span></label>
			<div class="controls" id="valueContainer">
				<form:input path="configValue" htmlEscape="false" maxlength="100" class="required"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:config:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.location.href='${ctx}/sys/config/'"/>
		</div>
	</form:form>
</body>
</html>