<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道管理</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">
		$(function(){
            jQuery.validator.addMethod("alnum", function(value, element){
                return this.optional(element) ||/^[a-zA-Z0-9]+$/.test(value);
            }, "只能包括英文字母和数字");
			$("#channelForm").validate({
		         debug: false, //调试模式取消submit的默认提交功能   
		         focusInvalid: false, //当为false时，验证无效时，没有焦点响应  
		         onkeyup: false,   
		         submitHandler: function(form){   //表单提交句柄,为一回调函数，带一个参数：form   
		        	var op = $("#op").val();
		         	if(op == 'add'){
			         	url = "${ctx}/channel/addSave";
		         	}else if(op == 'edit'){
		         		url = "${ctx}/channel/editSave";
		         	}
		         	document.forms[0].action=url;
		         	document.forms[0].submit();
		         },
		         errorPlacement:function(error,element) {
		        	 error.appendTo(element.parent());
		         },
		         rules:{
		        	 name:{
		                 required:true,
                         maxlength:64
		             },
		             busiEmail:{
                         maxlength:64
		             },
                     techEmail:{
                         maxlength:64
		             },
                     chanCode:{
                         alnum: true,
                         required:true,
                         maxlength:32
                     },
                     corpAddr:{
                         maxlength:128
                     },
                     busiContacts:{
                         maxlength:32
                     },
                     techContacts:{
                         maxlength:32
                     },
                     busiPhone:{
                         maxlength:20
                     },
                     techPhone:{
                         maxlength:20
                     },
                     busiMobile:{
                         maxlength:11
                     },
                     techMobile:{
                         maxlength:11
                     }
		         },
		         messages:{
		        	 name:{
		                 required:'必填'
		             },
		             busiEmail:{
		            	 email:'email格式不正确'
		             },
		             techEmail:{
		            	 email:'email格式不正确'
		             }
		         }
	    	});  
		});
		

        function del(id){
        	if(confirm("是否确认删除ID为“"+id+"”的记录？")){
        		document.forms[0].action="${ctx}/bowei/repaymentDel?id="+id;
        		document.forms[0].submit();
        	}
        }
        
	   
	</script>
</head>
<body>

<div class="breadcrumb">
	<label><th><a href="#">通道管理</a> > <a href="#"><b>通道编辑</b></a></th></label>
</div>

<form id="channelForm" action="" method="post" >
<input type="hidden" id="op" name="op" value="${op }"/>
<input type="hidden" name="id" value="${channel.id }"/>

<div class="breadcrumb">
	<label><th>基本信息</th></label>
</div>
<table class="table">
<tr>

	<td>
		<div class="control-group">
			<label class="control-label" >通道编号<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
				<input name="chanCode" value="${channel.chanCode }" placeholder="请输入字母或数字" class="input-small" type="text" <c:if test="${op == 'edit'}">disabled="disabled"</c:if>/>
			</div>
		</div>
	</td>

	<td>
		<div class="control-group">
			<label class="control-label" >通道名称<span style="color: red;"><span style="color: red;">*</span></span></label>
			<div class="controls">
	    <input name="name"  value="${channel.name }"  placeholder="" class="input-xlarge" type="text" required maxlength="64">
	  </div>
	</div>
</td>

<td>
	<div class="control-group">
       <label class="control-label">合作方式</label>
       <div class="controls">
         <select name="channelCooType" class="input-small">
	       <option value="1">两方协议</option>
	       <option
				   <c:if test="${channel.channelCooType == 2}">selected</c:if>
			   value="2">三方协议</option>
	     </select>
       </div>
     </div>
</td>

<td>
	<div class="control-group">
	  <label class="control-label" >公司地址</label>
	  <div class="controls">
	    <input name="corpAddr" value="${channel.corpAddr }" placeholder="" class="input-xxlarge" type="text">
	  </div>
	</div>
</td>

</tr>
<tr>
	<td>
		<div class="control-group">
			<label class="control-label" >备注</label>
			<div class="controls">
                        <textarea name="remark" placeholder="" style="width:350px;" id="remark"
								  rows="3">${channel.remark}</textarea>
			</div>
		</div>
	</td>

</tr>

</table>


<!-- ********************************************************************** -->
<div class="breadcrumb">
	<label><th>联络信息</th></label>
</div>
<tags:message content="${message}" type="${messageType}"/>
<table class="table">
<tr>
<td>
	<div class="control-group">
	  <label class="control-label" >业务联系人</label>
	  <div class="controls">
	    <input name="busiContacts" value="${channel.busiContacts }" placeholder="" class="input-large" type="text">
	  </div>
	</div>
	<div class="control-group">
	  <label class="control-label" >技术联系人</label>
	  <div class="controls">
	    <input name="techContacts" value="${channel.techContacts }" placeholder="" class="input-large" type="text">
	  </div>
	</div>
</td>

<td>
	<div class="control-group">
	  <label class="control-label" >业务电话</label>
	  <div class="controls">
	    <input name="busiPhone" value="${channel.busiPhone }" placeholder="" class="input-large" type="text">
	  </div>
	</div>
	<div class="control-group">
	  <label class="control-label" >技术电话</label>
	  <div class="controls">
	    <input name="techPhone" value="${channel.techPhone }" placeholder="" class="input-large" type="text">
	  </div>
	</div>
</td>

<td>
	<div class="control-group">
	  <label class="control-label" >业务手机</label>
	  <div class="controls">
	    <input name="busiMobile" value="${channel.busiMobile }" placeholder="" class="input-large" type="text">
	  </div>
	</div>
	<div class="control-group">
	  <label class="control-label" >技术手机</label>
	  <div class="controls">
	    <input name="techMobile" value="${channel.techMobile }" placeholder="" class="input-large" type="text">
	  </div>
	</div>
</td>

<td>
	<div class="control-group">
	  <label class="control-label" >业务邮箱</label>
	  <div class="controls">
	    <input name="busiEmail" value="${channel.busiEmail }" placeholder="" class="input-large" type="email">
	  </div>
	</div>
	<div class="control-group">
	  <label class="control-label" >技术邮箱</label>
	  <div class="controls">
	    <input name="techEmail" value="${channel.techEmail }" placeholder="" class="input-large" type="email">
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