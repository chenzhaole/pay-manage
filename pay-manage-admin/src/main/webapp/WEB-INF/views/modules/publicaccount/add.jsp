<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公户账务数据添加</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">
		$(function(){
            jQuery.validator.addMethod("alnum", function(value, element){
                return this.optional(element) ||/^[a-zA-Z0-9]+$/.test(value);
            }, "只能包括英文字母和数字");
			$("#publicAccountForm").validate({
		         debug: false, //调试模式取消submit的默认提交功能   
		         focusInvalid: false, //当为false时，验证无效时，没有焦点响应  
		         onkeyup: false,   
		         submitHandler: function(form){   //表单提交句柄,为一回调函数，带一个参数：form   
			        url = "${ctx}/publicaccount/commitPublicAccount";
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
                     },
                     queryfaceFrequency:{
		        	     digits:true,
						 min:0,
                         max:60
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
	<label><th><a href="#">公户账务管理</a> > <a href="#"><b>公户账务编辑</b></a></th></label>
</div>

<form id="publicAccountForm" action="" method="post" enctype="multipart/form-data">
<input type="hidden" id="op" name="op" value="${op }"/>
<input type="hidden" name="id" value="${channel.id }"/>

<div class="breadcrumb">
	<tags:message content="${message}" type="${messageType}"/>
	<label><th>基本信息</th></label>
</div>
<table class="table">
<tr>
	<td>
		<div class="control-group">
		   <label class="control-label">选择公户</label>
		   <div class="controls">
			 <select name="publicAccountCode" class="input-small">
			   <option value="1">test1</option>
			   <option value="2">test2</option>
			 </select>
		   </div>
		 </div>
	</td>
</tr>

<tr>
	<td>
		<div class="control-group">
			<label class="control-label" >上传账务文件</label>
			<div class="controls">
				<input type="file"  name="file" />
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

<div class="breadcrumb">
	<input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
	<input name="btnSubmit" class="btn btn-primary" type="submit" value="保存"  style="margin-left: 5px;">
</div>


</form>
</body>
</html>