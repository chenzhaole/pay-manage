<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="content" type="java.lang.String" required="true" description="消息内容"%>
<%@ attribute name="type" type="java.lang.String" description="消息类型：info、success、warning、error、loading"%>
<script type="text/javascript">top.$.jBox.closeTip();</script>
<c:if test="${not empty fns:escapeHtml(content)}">
	<c:if test="${not empty type}"><c:set var="ctype" value="${type}"/></c:if><c:if test="${empty type}"><c:set var="ctype" value="${fn:indexOf(content,'失败') eq -1?'success':'error'}"/></c:if>
	<div id="messageBox" class="alert alert-${ctype} hide"><button data-dismiss="alert" class="close">×</button>${fns:escapeHtml(content)}</div>
	<script type="text/javascript">if(!top.$.jBox.tip.mess){top.$.jBox.tip.mess=1;}top.$.jBox.tip("${fns:escapeHtml(content)}","${ctype}",{persistent:true,opacity:0});$("#messageBox").show();</script>
</c:if>
<script>
	/**
	 * 检查输入的字符是否具有特殊字符
	 * 输入:str  字符串
	 * 返回:true 或 flase; true表示包含特殊字符
	 * 主要用于注册信息的时候验证
	 */
	function checkQuote(str) {
		var items = new Array("~", "`", "!", "@", "#", "$", "%", "^", "&", "*", "{", "}", "[", "]", "(", ")");
		items.push(";", "'", "\"", "|", "\\", "<", ">", "?", "<<", ">>", "||", "//");
		str = str.toLowerCase();
		for (var i = 0; i < items.length; i++) {
			if (str.indexOf(items[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 *   查询是过滤特殊字符
	 */
	$("#searchForm").validate({
		submitHandler: function(form){
			var isValidate = true;
			$("input[type='text']", form).each(function(){
				if (checkQuote($(this).val())) {
					top.$.jBox.tip('查询条件不能输入特殊字符', 'warn');
					$(this).focus();
					isValidate = false;
					return false;
				}
			});
			if (isValidate) {
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

</script>