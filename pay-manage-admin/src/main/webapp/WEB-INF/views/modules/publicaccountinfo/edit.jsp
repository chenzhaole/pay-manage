<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公户信息管理</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">
		$(function(){
            jQuery.validator.addMethod("alnum", function(value, element){
                return this.optional(element) ||/^[a-zA-Z0-9]+$/.test(value);
            }, "只能包括英文字母和数字");
			$("#publicAccountInfoForm").validate({
		         debug: false, //调试模式取消submit的默认提交功能   
		         focusInvalid: false, //当为false时，验证无效时，没有焦点响应  
		         onkeyup: false,   
		         submitHandler: function(form){   //表单提交句柄,为一回调函数，带一个参数：form   
		        	var op = $("#op").val();
		         	if(op == 'add'){
			         	url = "${ctx}/publicaccountinfo/addSave";
		         	}else if(op == 'edit'){
		         		url = "${ctx}/publicaccountinfo/editSave";
		         	}
		         	document.forms[0].action=url;
		         	document.forms[0].submit();
		         },
		         errorPlacement:function(error,element) {
		        	 error.appendTo(element.parent());
		         },
		         rules:{
                     publicAccountNo:{
		                 required:true,
                         maxlength:64
		             },
                     publicAccountName:{
                         maxlength:64
		             }
		         },
		         messages:{
                     publicAccountNo:{
		                 required:'必填'
		             },
                     publicAccountName:{
                         required:'必填'
                     }
		         }
	    	});  
		});
		
	</script>
</head>
<body>

<div class="breadcrumb">
	<label><a href="#">平台管理> </a><a href="#">公户信息管理> </a><a href="#"><b>公户信息编辑</b></a></label>
</div>

<form id="publicAccountInfoForm" action="" method="post" >
<input type="hidden" name="publicAccountCode" value="${publicAccountInfo.publicAccountCode }"/>

<div class="breadcrumb">
	<tags:message content="${message}" type="${messageType}"/>
	<label><th>基本信息</th></label>
</div>
<table class="table">
<tr>

	<td>
		<div class="control-group">
			<label class="control-label">账号<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<input name="publicAccountNo" value="${publicAccountInfo.publicAccountNo }" placeholder="请输入公户账号" class="input-small" type="text"/>
			</div>
		</div>
	</td>

	<td>
		<div class="control-group">
			<label class="control-label">账户名称<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
	    <input name="publicAccountName"  value="${publicAccountInfo.publicAccountName }"  placeholder="请输入公户名称" class="input-xlarge" type="text" required maxlength="64">
	  </div>
	</div>
</td>

<td>
	<div class="control-group">
		<label class="control-label" >开户银行</label>
		<div class="controls">
			<input name="publicOpenAccountBankName" value="${publicAccountInfo.publicOpenAccountBankName }" placeholder="" class="input-xxlarge" type="text">
		</div>
	</div>
</td>

<td>
	<div class="control-group">
       <label class="control-label">选择模板</label>
       <div class="controls">
         <select name="modelName" class="input-small">
	       <option value="m1">民生银行</option>
	     </select>
       </div>
     </div>
</td>

</tr>
<tr>
	<td>
		<div class="control-group">
			<label class="control-label">状态</label>
			<div class="controls">
				<select name="status" class="input-small">
					<option value="1">启用</option>
					<option
							<c:if test="${publicAccountInfo.status == 2}">selected</c:if>
							value="2">停用</option>
				</select>
			</div>
		</div>
	</td>
	<td>
		<div class="control-group">
			<label class="control-label" >已绑定手机号</label>
			<div class="controls">
                        <textarea name="bindPhones" placeholder="" style="width:350px;" id="bindPhones"
								  rows="3">${publicAccountInfo.bindPhones}</textarea>(最多5个)
			</div>
		</div>
	</td>
	<td>
		<div class="control-group">
			<label class="control-label" >备注</label>
			<div class="controls">
                        <textarea name="remark" placeholder="" style="width:350px;" id="remark"
								  rows="3">${publicAccountInfo.remark}</textarea>
			</div>
		</div>
	</td>

</tr>
</table>

<div class="breadcrumb">
	<input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
	<input name="btnSubmit" class="btn btn-primary" type="submit" value="保存"  style="margin-left: 5px;">
</div>


</form>
</body>
</html>