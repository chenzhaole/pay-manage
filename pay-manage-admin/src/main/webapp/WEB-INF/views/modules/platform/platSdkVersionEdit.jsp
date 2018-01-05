<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>支付SDK管理</title>
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
		 $("#sdkEditForm").validate({
	         debug: false, //调试模式取消submit的默认提交功能   
	         //errorClass: "label.error", //默认为错误的样式类为：error   
	         focusInvalid: false, //当为false时，验证无效时，没有焦点响应  
	         onkeyup: false,   
	         submitHandler: function(form){   //表单提交句柄,为一回调函数，带一个参数：form   
	        	 var op = $("#op").attr("value");
	         	url = "${ctx}/platform/addPlatFormSdkSave";
	         	if(op=="edit"){
	         		url = "${ctx}/platform/addPlatFormSdkSave";
	         	}
	         	document.forms[0].action=url;
	         	document.forms[0].submit();
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
	             domainType:{
	            	 required:true
	             },
	             domainUrl:{
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
	             domainType:{
	            	 required:'必填'
	             },
	             domainUrl:{
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
        	url = "${ctx}/platForm/addPlatFormSdkSave";
        	if(op=="edit"){
        		url = "${ctx}/platForm/editPlatFormSdkSave";
        	}
        	document.forms[0].action=url;
        	document.forms[0].submit();
        }
        //新增支付类型
        function addPayType(){
 		   var  payTypeNum = $("#payTypeTable tr").length;
 		   if(payTypeNum >9){
 			   alert("最多添加10条！");
 			   return;
 		   }
 		   $("#payTypeTable").append('<tr>'+
 				  '<td>'+
 					'<div class="control-group">'+
 				       '<label class="control-label">名称</label>'+
 				       '<div class="controls">'+
 				        '  <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32" />'+
 				      ' </div>'+
 				    ' </div>'+
 				'</td>'+
 					'<td>'+
 					'<div class="control-group">'+
 				       '<label class="control-label">状态</label>'+
 				       '<div class="controls">'+
 				         '<select name="paymentValue" class="input-medium">'+
 					       '<option value="1">启用</option>'+
 					       '<option value="0">停用</option>'+
 					     '</select>'+
 					     '&nbsp;&nbsp;&nbsp;'+
 				         '<a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>'+
 				       '</div>'+
 				     '</div>'+
 					'</td>'+
 					'</tr>');
 	   	}
        //删除支付方式
 	   function deletePayType(obj){
 		   var  payTypeNum = $("#payTypeTable tr").length;
 		   if(payTypeNum <2){
 			   alert("至少添加一条！");
 			   return;
 		   }
 		   $(obj).parent().parent().parent().parent().remove();
 	   }
 	   //新增支付域名
 	  function addDomain(){
		   var  domainNum = $("#domainTable tr").length;
		   if(domainNum >9){
			   alert("最多添加10条！");
			   return;
		   }
		   var html = '<tr>'+
				'<td>'+
				'<div class="control-group">'+
			       '<label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>'+
			       '<div class="controls">'+
			         '<input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="50"/>'+
			       '</div>'+
			     '</div>'+
				'</td>'+
				'<td>'+
				'<div class="control-group">'+
			      ' <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>'+
			       '<div class="controls">'+
					' <input name="domainUrl"  placeholder=""   class="input-xlarge" type="text" maxlength="100"/>'+
				    ' &nbsp;&nbsp;&nbsp;'+
				       ' <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>'+
			       '</div>'+
			     '</div>'+
				'</td>'+
				'</tr>';
		   $("#domainTable").append(html);
	   }
 	   //删除支付域名
	   function deleteDomain(obj){
		   var  domainNum = $("#domainTable tr").length;
		   if(domainNum <2){
			   alert("至少添加一条！");
			   return;
		   }
		   $(obj).parent().parent().parent().parent().remove();
	   }
 	   
	</script>
</head>
<body>

<div class="breadcrumb">
	<label><a href="#">支付SDK管理</a> > <a href="#"><b>支付SDK编辑</b></a></label>
</div>

<form id="sdkEditForm"  action="" method="post" >
	<input type="hidden" id="op" name="op" value="${op}"/>
	<input type="hidden" id="op" name="createTime" value="<fmt:formatDate value="${platSdkConfig.createTime}" pattern="yyyy-MM-dd  HH:mm:ss" />"/>
<%-- 	<input type="hidden" name="version" value="${platSdkConfig.version }"/> --%>
<!-- 	<input type="hidden" name="payTypeNum" id="payTypeNum"/> -->
<!-- 	<input type="hidden" name="domainNum"  id="domainNum"/> -->
	<!-- ********************************************************************** -->
	<div class="breadcrumb">
		<label>SDK基本信息</label>
	</div>
	<table class="table">
		<tr>
			<td>
				<div class="control-group">
			       <label class="control-label">版本号</label><span style="color: red;"><span style="color: red;">*</span></span>
			       <div class="controls">
					    <input name="version"  value="${platSdkConfig.version}" placeholder=""  style="width:300px;" type="text" maxlength="64" />
			       </div>
			     </div>
			</td>
			<td>
			<div class="control-group">
		       <label class="control-label">更新日期</label><span style="color: red;"><span style="color: red;">*</span></span>
		       <div class="controls">
				    <input name="updateTime" type="text" readonly="readonly" maxlength="20" class="input-xlarge Wdate"  value="<fmt:formatDate value="${platSdkConfig.updateTime}" pattern="yyyy-MM-dd  HH:mm:ss" />"
              			onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:false,isShowToday:true});"/>
		       </div>
		     </div>
			</td>
				<td>
				<div class="control-group">
			       <label class="control-label">是否生效</label>
			       <div class="controls">
			         <select name="isValid" class="input-medium" id="isValid" value="${platSdkConfig.isValid}">
				       <option  value="1" <c:if test="${platSdkConfig.isValid == '1'}">selected</c:if>>是</option>
				       <option value="0" <c:if test="${platSdkConfig.isValid == '0'}">selected</c:if>>否</option>
				     </select>
			       </div>
			     </div>
			</td>
		</tr>
		<tr>
		<td>
				<div class="control-group">
				  <label class="control-label" for="input01">备注</label>
				  <div class="controls">
				    <textarea name="desc"  placeholder=""  style="width:300px;" id="desc" rows="5" maxlength="255"  >${platSdkConfig.description}</textarea>
				  </div>
				</div>
			</td>
			<td >
				<div class="control-group">
				  <label class="control-label" for="input01">更新说明</label>
				  <div class="controls">
				    <textarea name="updateDesc"  placeholder=""  style="width:300px;" rows="5" maxlength="255">${platSdkConfig.updateDesc}</textarea>
				  </div>
				</div>
			</td>		
		</tr>
	</table>
	<div class="row-fluid marketing">
        <div class="span6">
		    <!-- ********************************************************************** -->
			<div class="breadcrumb">
				<label>客服配置</label>
			</div>
			<table class="table" >
				<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">客服电话</label>
					       <div class="controls">
					         	<input name="consumerTel" value="${platSdkConfig.consumerTel}" placeholder=""   class="input-medium"  type="text"  maxlength="32"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">客服QQ</label>
					       <div class="controls">
						   		<input name="consumerQq" value="${platSdkConfig.consumerQq}" placeholder=""   class="input-medium"  type="text"  maxlength="32"/>
					       </div>
					     </div>
					</td>
				</tr>
			</table>
        </div>
        <div class="span6">
		     <!-- ********************************************************************** -->
			<div class="breadcrumb">
				<label>客服信息</label>
			</div>
			<table class="table">
			<tr>
				<td>
					<div class="control-group">
				       <label class="control-label">是否弹出收银台页面</label>
				        <div class="controls">
					         <select name="isshowpaypage" class="input-medium" id="isshowpaypage" value="${platSdkConfig.isshowpaypage}">
						       <option  value="1" <c:if test="${platSdkConfig.isshowpaypage == '1'}">selected</c:if>>是</option>
				       			<option value="0" <c:if test="${platSdkConfig.isshowpaypage == '0'}">selected</c:if>>否</option>
						     </select>
				       </div>
				     </div>
				</td>
				<td>
					<div class="control-group">
				       <label class="control-label">是否弹出支付结果</label>
				        <div class="controls">
				         <select name="isshowpayresultpage" class="input-medium" id="isshowpayresultpage"  value="${platSdkConfig.isshowpayresultpage}">
					       <option  value="1" <c:if test="${platSdkConfig.isshowpayresultpage == '1'}">selected</c:if>>是</option>
			       			<option value="0" <c:if test="${platSdkConfig.isshowpayresultpage == '0'}">selected</c:if>>否</option>
					     </select>
				       </div>
				     </div>
				</td>
			</tr>
		</table>
     </div>
  </div>
  
  <div class="row-fluid marketing">
       <div class="span6">
	       	<div class="breadcrumb">
				<label>支付类型信息列表</label>
				 <a style="float:right;cursor: pointer;font-size:15px;text-decoration: none" onclick="addPayType();">新增支付类型</a>
			</div>
			<table class="table" id="payTypeTable">
			  <c:if test="${op == 'add' }">
				<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					       <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" >
						       <option value="1">启用</option>
						       <option value="0">停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${op == 'edit' }">
					<c:if test="${platSdkConfig.paymentType1 != null or platSdkConfig.paymentValue1 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					       <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"  value="${platSdkConfig.paymentType1}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue1}>
						       <option  value="1" <c:if test="${platSdkConfig.paymentValue1 == '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue1 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${platSdkConfig.paymentType2 != null or platSdkConfig.paymentValue2 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					         <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"  value="${platSdkConfig.paymentType2}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue2}>
						       <option  value="1" <c:if test="${platSdkConfig.paymentValue2 == '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue2 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${platSdkConfig.paymentType3 != null or platSdkConfig.paymentValue3 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					       <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"  value="${platSdkConfig.paymentType3}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue3}>
						       <option  value="1" <c:if test="${platSdkConfig.paymentValue3== '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue3 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${platSdkConfig.paymentType4 != null or platSdkConfig.paymentValue4 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					         <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"  value="${platSdkConfig.paymentType4}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue4}>
						       <option  value="1" <c:if test="${platSdkConfig.paymentValue4 == '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue4 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${platSdkConfig.paymentType5 != null or platSdkConfig.paymentValue5 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					         <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"  value="${platSdkConfig.paymentType5}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue5}>
						       <option  value="1" <c:if test="${platSdkConfig.paymentValue5 == '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue5 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${platSdkConfig.paymentType6 != null or platSdkConfig.paymentValue6 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					         <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"  value="${platSdkConfig.paymentType6}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue6}>
						       <option  value="1" <c:if test="${platSdkConfig.paymentValue6 == '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue6 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${platSdkConfig.paymentType7 != null or platSdkConfig.paymentValue7 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					         <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"  value="${platSdkConfig.paymentType7}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue7}>
						      <option  value="1" <c:if test="${platSdkConfig.paymentValue7 == '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue7 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${platSdkConfig.paymentType8 != null or platSdkConfig.paymentValue8 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					          <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"  value="${platSdkConfig.paymentType8}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue8}>
						       <option  value="1" <c:if test="${platSdkConfig.paymentValue8 == '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue8 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${platSdkConfig.paymentType9 != null or platSdkConfig.paymentValue9 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					          <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"   value="${platSdkConfig.paymentType9}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue9}>
						       <option  value="1" <c:if test="${platSdkConfig.paymentValue9 == '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue9 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${platSdkConfig.paymentType10 != null or platSdkConfig.paymentValue10 != null}">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">名称</label>
					       <div class="controls">
					          <input name="paymentType"  placeholder=""   class="input-medium"  type="text"  maxlength="32"  value="${platSdkConfig.paymentType10}"/>
					       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">状态</label>
					       <div class="controls">
					         <select name="paymentValue" class="input-medium" ${platSdkConfig.paymentValue10}>
						       <option  value="1" <c:if test="${platSdkConfig.paymentValue10 == '1'}">selected</c:if>>启用</option>
			       				<option value="0" <c:if test="${platSdkConfig.paymentValue10 == '0'}">selected</c:if>>停用</option>
						     </select>&nbsp;&nbsp;
					         <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deletePayType(this);" >删除</a>
					       </div>
					     </div>
					</td>
				</tr>
				</c:if>
			</c:if>
			</table>
       	</div>
        <div class="span6">
        		<!-- ********************************************************************** -->
				<div class="breadcrumb">
					<label>服务器域名信息</label>
					 <a style="float:right;cursor: pointer;font-size:15px;text-decoration: none" onclick="addDomain();">新增域名</a>
				</div>
				<table class="table" id="domainTable">
					<c:if test="${op == 'add' }">
					<tr>
					<td>
						<div class="control-group">
					       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
						       <div class="controls">
						         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32"/>
						       </div>
					     </div>
					</td>
					<td>
						<div class="control-group">
					       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
						       <div class="controls">
								 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100" />&nbsp;&nbsp;
								 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
						       </div>
					     </div>
					</td>
				</tr>
				</c:if>
				<c:if test="${op == 'edit' }">
					<c:if test="${platSdkConfig.domainType1 != null or platSdkConfig.domainUrl1 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType1}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl1}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
					<c:if test="${platSdkConfig.domainType2 != null or platSdkConfig.domainUrl2 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType2}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl2}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
					<c:if test="${platSdkConfig.domainType3 != null or platSdkConfig.domainUrl3 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType3}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl3}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
					<c:if test="${platSdkConfig.domainType4 != null or platSdkConfig.domainUrl4 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType4}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl4}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
					<c:if test="${platSdkConfig.domainType5 != null or platSdkConfig.domainUrl5 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType5}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl5}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
					<c:if test="${platSdkConfig.domainType6 != null or platSdkConfig.domainUrl6 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType6}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl6}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
					<c:if test="${platSdkConfig.domainType7 != null or platSdkConfig.domainUrl7 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType7}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl7}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
					<c:if test="${platSdkConfig.domainType8 != null or platSdkConfig.domainUrl8 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType8}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl8}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
					<c:if test="${platSdkConfig.domainType9 != null or platSdkConfig.domainUrl9 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType9}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl9}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
					<c:if test="${platSdkConfig.domainType10 != null or platSdkConfig.domainUrl10 != null}">
						<tr>
						<td>
							<div class="control-group">
						       <label class="control-label">类型</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
							         <input name="domainType"  placeholder=""   style="width:200px;"  type="text" maxlength="32" value="${platSdkConfig.domainType10}"/>
							       </div>
						     </div>
						</td>
						<td>
							<div class="control-group">
						       <label class="control-label">地址</label><span style="color: red;"><span style="color: red;">*</span></span>
							       <div class="controls">
									 <input name="domainUrl"  placeholder=""  class="input-xlarge"  type="text"  maxlength="100"  value="${platSdkConfig.domainUrl10}"/>&nbsp;&nbsp;
									 <a style="cursor: pointer;font-size:15px;text-decoration: none" onclick="deleteDomain(this);" >删除</a>
							       </div>
						     </div>
						</td>
						</tr>
					</c:if>
				</c:if>
			</table>
        </div>
  	</div>

	<div class="breadcrumb" >
		<input name="btnCancel" class="btn center-block"  type="button" value="返 回" onclick="window.history.go(-1);" />
		<input name="btnSubmit" class="btn btn-primary" type="submit" value="保存"  style="margin-left: 5px;">
		<input name="clearButton" class="btn btn-primary" type="button" value="重置" onclick="window.reset();" style="margin-left: 5px;"/>
	</div>
</form>
</body>
</html>