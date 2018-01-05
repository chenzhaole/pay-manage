<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>站点统计管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					$("#btnBack").attr("disabled", true);
					$("#btnSubmit").attr("disabled", true);
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
		<li class="active" ><a href="#">站点统计管理</a></li>
	</ul><br/>
    <form id="inputForm" action="${ctx}/sys/config/saveStatScript" method="post" class="form-horizontal">
        <tags:message content="${message}"/>
        <c:forEach items="${sysConfigList}"  var="conf">
            <div class="control-group">
                <label class="control-label">${conf.description}:</label>
                <div class="controls">
                    <textarea name="${conf.configName}" rows="7" maxlength="5000" class="input-xxlarge">${conf.configValue}</textarea>
                </div>
            </div>
        </c:forEach>
        <div class="form-actions">
            <shiro:hasPermission name="sys:config:view">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;
            </shiro:hasPermission>
        </div>
    </form>
</body>
</html>