<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>批量拆包编辑</title>
	<meta name="decorator" content="default"/>
	<style>
		.table th, .table td {
		    border-top: none;
		}
		.control-group {
		    border-bottom: none;
		}
	</style>
	<script type="text/javascript">
	$(function(){
		 $("#splitForm").validate({
	         debug: true, //调试模式取消submit的默认提交功能   
	         //errorClass: "label.error", //默认为错误的样式类为：error   
	         focusInvalid: false, //当为false时，验证无效时，没有焦点响应  
	         onkeyup: false,   
	         submitHandler: function(form){   //表单提交句柄,为一回调函数，带一个参数：form   
//	             alert("提交表单");   
//	             form.submit();   //提交表单   
	         },   
	         errorPlacement:function(error,element) {
	        	 error.appendTo(element.parent());
	         },
	         rules:{
	        	 version:{
	                 required:true
	             },
	             updateTime:{
	          	   required:true
	             },
	             payType:{
	          	   required:true
	             },
	             unzipNum:{
	          	   required:true
	             },
	             fastNum:{
	          	   required:true
	             }
	             
	         },
	         messages:{
	        	 version:{
	        		 required:'必填'
	             },
	             updateTime:{
	            	 required:'必填'
	             },
	             payType:{
	            	 required:'必填'
	             },
	             unzipNum:{
	            	 required:'必填'
	             },
	             fastNum:{
	            	 required:'必填'
	             }
	         }
	    	});  
		});
        function edit(id){
        	document.forms[0].action="${ctx}/bowei/repaymentEdit?id="+id;
        	document.forms[0].submit();
        }

        function del(id){
        	if(confirm("是否确认删除ID为“"+id+"”的记录？")){
        		document.forms[0].action="${ctx}/bowei/repaymentDel?id="+id;
        		document.forms[0].submit();
        	}
        }
        
        function ok(){
        	var op = $("#op").attr("value");
        	url = "${ctx}/channel/addSave";
        	if(op=="edit"){
        		url = "${ctx}/channel/editSave";
        	}
        	document.forms[0].action=url;
        	document.forms[0].submit();
        }
        
	</script>
</head>
<body>

<div class="breadcrumb">
	<label><a href="#">支付SDK管理</a> > <a href="#"><b>支付SDK编辑</b></a></label>
</div>

<form id="splitForm" action="" method="post" >
<input type="hidden" id="op" name="op" value="${op }"/>
<input type="hidden" name="id" value="${chanMchPaytye.id }"/>

<!-- ********************************************************************** -->
<div class="breadcrumb">
	<label>SDK基本信息</label>
</div>
<tags:message content="${message}" type="${messageType}"/>
<table class="table">
<tr>

<td>
	<div class="control-group">
       <label class="control-label">版本号</label><span style="color: red;"><span style="color: red;">*</span></span>
       <div class="controls">
		    <input name="version" value="${chanMchPaytye.productName }" placeholder=""  style="width:300px;" type="text" />
       </div>
     </div>
</td>

<td>
	<div class="control-group">
       <label class="control-label">更新日期</label><span style="color: red;"><span style="color: red;">*</span></span>
       <div class="controls">
		     <input name="updateTime" type="text" readonly="readonly" maxlength="20" class="input-xlarge Wdate"
              			onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:false,isShowToday:true});"/>
     </div>
</td>
<td>
	<div class="control-group">
       <label class="control-label">是否生效</label>
       <div class="controls">
         <select name="status" class="input-medium" id="status">
	       <option selected="selected" value="1">是</option>
	       <option value="0">否</option>
	     </select>
       </div>
     </div>
	</td>

	</tr>
		
	<td >
		<div class="control-group">
		  <label class="control-label" for="input01">备注</label>
		  <div class="controls">
		    <textarea name="desc"  placeholder=""  style="width:300px;" id="desc" rows="5">
		    </textarea>
		  </div>
		</div>
	</td>
	<td >
		<div class="control-group">
		  <label class="control-label" for="input01">更新说明</label>
		  <div class="controls">
		    <textarea name="desc"  placeholder=""  style="width:300px;" id="desc" rows="5">
		    </textarea>
		  </div>
		</div>
	</td>
	<tr>
	</tr>
</table>
<!-- ********************************************************************** -->
<div class="breadcrumb">
	<label>批量拆包编辑</label>
</div>
<tags:message content="${message}" type="${messageType}"/>
<table class="table" >
<tr>

	<td>
	<div class="control-group">
       <label class="control-label">商户名称</label>
       <div class="controls">
         <span name="productCode" value="${chanMchPaytye.productCode }" placeholder=""   style="width:300px;"  type="text" id="productCode">展鸿软通</span>
       </div>
     </div>
	</td>
</tr>
   <tr>
	<td>
	<div class="control-group">
       <label class="control-label">支付类型</label>
       <div class="controls">
			<input type="radio" name="payType" id="optionsRadios1" value="option1" checked>批付
			<input type="radio" name="payType" id="optionsRadios2" value="option2">批扣
		</div>
       </div>
	</td>
	
	</tr>
	 <tr>
	<td>
	<div class="control-group">
       <label class="control-label">是否拆包</label>
       <div class="controls">
		 	<input type="radio" name="unzip" id="optionsRadios1" value="option1" checked>是
			<input type="radio" name="unzip" id="optionsRadios2" value="option2">否
		</div>
     </div>
	</td>
	<td>
	<div class="control-group">
       <label class="control-label">快速拆包条数</label><span style="color: red;"><span style="color: red;">*</span></span>
       <div class="controls">
		 	<input name="unzipNum" value="${chanMchPaytye.productCode }" placeholder="" class="input-medium" type="text" id="productCode">
       </div>
     </div>
	</td>
	<td>
	<div class="control-group">
       <label class="control-label">快速拆包类型</label>
       <div class="controls">
			<input type="radio" name="unzipType" id="optionsRadios1" value="option1" checked>否
			<input type="radio" name="unzipType" id="optionsRadios2" value="option2">平台快速拆
			<input type="radio" name="unzipType" id="optionsRadios2" value="option2">通道快速拆
       </div>
     </div>
	</td>
	<td>
	<div class="control-group">
       <label class="control-label">快速最大条数</label><span style="color: red;"><span style="color: red;">*</span></span>
       <div class="controls">
		 	<input name="fastNum" value="${chanMchPaytye.productCode }" placeholder="" class="input-medium" type="text" >
       </div>
     </div>
	</td>
	</tr>
	
	<tr>
	<td>
	<div class="control-group">
       <label class="control-label">支付通道</label>
       <div class="controls">
         <select name="status" class="input-xlarge" id="status">
	       <option selected="selected" value="1">通道1</option>
	       <option value="0">通道2</option>
	     </select>
       </div>
     </div>
	</td>
	<td>
	<div class="control-group">
       <label class="control-label">拆包银行</label>
       <div class="controls">
         <select name="status" class="input-xlarge" id="status">
	       <option selected="selected" value="1">民生银行</option>
	       <option value="0">招商银行</option>
	     </select>
       </div>
     </div>
	</td>
	<td>
	<div class="control-group">
       <label class="control-label">快速批量银行</label>
       <div class="controls">
         <select name="status" class="input-xlarge" id="status">
	       <option selected="selected" value="1">民生银行</option>
	       <option value="0">招商银行</option>
	     </select>
       </div>
     </div>
	</td>
	</tr>
	
	
</table>
<!-- ********************************************************************** -->
<div class="breadcrumb" >
	<input name="btnCancel" class="btn center-block"  type="button" value="返 回" onclick="window.history.go(-1);" />
	<input name="btnSubmit" class="btn btn-primary" type="submit" value="保存"  style="margin-left: 5px;">
	<input name="clearButton" class="btn btn-primary" type="button" value="重置" onclick="window.reset();" style="margin-left: 5px;"/>
</div>


</form>
</body>
</html>