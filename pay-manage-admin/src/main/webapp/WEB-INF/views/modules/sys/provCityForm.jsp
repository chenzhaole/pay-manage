<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>省市管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#title").focus();
			$("#inputForm").validate({
				rules: {
					countyId: {remote: "${ctx}/sys/provCity/checkCountyId?oldCountyId=${provCity.countyId}"}
				},
				messages: {
					countyId: {remote: "区/县编码已存在，请重新输入"}
				},
				submitHandler: function(form){
					$("#lon").val($.trim($("#lon").val()));
					$("#lat").val($.trim($("#lat").val()));
					var lonTest = /^(\d|[1-9]\d|1[0-7]\d)(\.\d{1,6})/;
					var latTest = /^(\d|[1-8]\d)(\.\d{1,6})/;
					var isValidate = true;
					if ($("#lon").val() != "" && !lonTest.test($("#lon").val())) {
						top.$.jBox.tip('经度不正确','warning');
						isValidate = false;
					}
					if (isValidate && $("#lat").val() != "" && !latTest.test($("#lat").val())) {
						top.$.jBox.tip('纬度不正确','warning');
						isValidate = false;
					}

					if (isValidate) {
						$("#btnSubmit").attr("disabled", true);
						$("#btnCancel").attr("disabled", true);
						loading('正在提交，请稍等...');
						form.submit();
					}
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
		<li><a href="${ctx}/sys/provCity/list">省市列表</a></li>
		<li class="active"><a href="${ctx}/sys/provCity/form?id=${provCity.id}">省市<shiro:hasPermission name="sys:provCity:edit">${not empty provCity.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:provCity:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="provCity" action="${ctx}/sys/provCity/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">省编码:<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="provinceId" htmlEscape="false" maxlength="6" class="input-large digits required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">省名称:<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="provinceName" htmlEscape="false" maxlength="10" class="input-large required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">市编码:<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="cityId" htmlEscape="false" maxlength="6" class="input-large digits required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">市名称:<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="cityName" htmlEscape="false" maxlength="10" class="input-large required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">区/县编码:<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="countyId" htmlEscape="false" maxlength="8" class="input-large digits required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">区/县名称:<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="countyName" htmlEscape="false" maxlength="10" class="input-large required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">区/县名称简拼:<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="pinyinPrefix" htmlEscape="false" maxlength="10" class="input-large required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">区/县名称全拼:<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:input path="pinyin" htmlEscape="false" maxlength="64" class="input-large required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">经度:</label>
			<div class="controls">
				<form:input path="lon" htmlEscape="false" maxlength="11" class="input-large"/>范围：-180.00000~180.00000
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">纬度:</label>
			<div class="controls">
				<form:input path="lat" htmlEscape="false" maxlength="11" class="input-large"/>范围：-90.00000~90.00000
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分平台提供商:<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:select path="providerId" htmlEscape="false" cssClass="required">
					<form:option value="" label="--请选择--"/>
					<c:forEach items="${providerList}" var="provider">
						<form:option value="${provider.providerId}" label="${provider.providerId}-${provider.providerName}"/>
					</c:forEach>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否热门:<span style="color: red"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<form:select path="isRecommend">
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:provCity:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.location.href='${ctx}/sys/provCity/'"/>
		</div>
	</form:form>
</body>
</html>