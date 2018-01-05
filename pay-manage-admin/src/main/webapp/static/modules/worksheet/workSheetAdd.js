var buttons={"1":"创建工单","2":"驳回工单","3":"修改工单","4":"转发工单","5":"关闭工单","6":"重启工单"};
$(document).ready(function () {
	formatFunName();
	/*$("#questionDescribe").focus();
	$("#inputForm").validate({
		submitHandler: function (form) {
		    var isValidate = true;
		    if ($("#questionDescribe").val().length > 100) {
		       top.$.jBox.tip('问题描述字数不能超过100个字', 'warning');
		       isValidate = false;
		    }
		    if ($("#technologyDescribe").val().length > 100) {
		       top.$.jBox.tip('技术核实字数不能超过100个字', 'warning');
		       isValidate = false;
		    }  
		    if ($("#financeDescribe").val().length > 100) {
		       top.$.jBox.tip('财务核实字数不能超过100个字', 'warning');
		       isValidate = false;
		    }   
		    if (isValidate) {
		       $("#btnSubmit").attr("disabled", true);
		       loading('正在提交，请稍等...');
		           form.submit();
		       }
		}    
	});*/
 });

 
 //根据订单号跳转的订单详情页
 function getOrderDetail(ele,rurl){
	 var orderid = ele.innerText;
	 if(orderid !=null && $.trim(orderid).length>0){
		 var param = rurl+"?orderId="+orderid;
	 var url = window.location.protocol + "//" + window.location.host  + param;
	 var h = (window.screen.height - 200)<300?window.screen.height:(window.screen.height - 200);
	 var w = (window.screen.width - 100)<300?window.screen.width:(window.screen.width - 100);
	 window.open (url,'newwindow','height='+h+',width='+w+',top=50,left=50,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no') 
	 }
 }
 
 function actionOrder(actionId){
	 actionId = Number(actionId);
	 switch(actionId){
	 	case 1:
	 		creatOrder();
	 		break;
		case 2:
			rejectOrder();
			break;
		case 3:
			modifyOrder();
			break;
		case 4:
			RelayOrder();
			break;
		case 5:
			closeOrder();
			break;
		case 6:
			resetOrder();
			break;
	 }
 }
 //创建工单
 function creatOrder(){
	 var ret = detailIsrequest();
	 if(ret != false){
		 ajaxServer("saveWF",ret,true,function(data){
			 if(data!=null){
				 if(data.flag=="0"){
					 top.$.jBox.tip('创建成功', 'warn');
			         $(this).focus();
                     window.location.href = window.location.href;
				 }else{
					 top.$.jBox.tip("操作失败："+ data.errorMsg, 'warn');
			         $(this).focus();
				 }
			 }
		 });
	 }
 }
 // 驳回工单
 function rejectOrder(){
	 var ret = detailIsrequest();
	 if(ret != false){
		 ajaxServer("rejectWF",ret,true,function(data){
			 if(data!=null){
				 if(data.flag=="0"){
					 top.$.jBox.tip("驳回成功", 'warn');
			         $(this).focus();
                     window.location.href = window.location.href;
				 }else{
					 top.$.jBox.tip("操作失败："+ data.errorMsg, 'warn');
			         $(this).focus();
				 }
			 }
		 });
	 }
 }
 // 修改工单
 function modifyOrder(){
	 var ret = detailIsrequest();
	 if(ret != false){
		 ajaxServer("modifyWF",ret,true,function(data){
			 if(data!=null){
				 if(data.flag=="0"){
					 top.$.jBox.tip("修改成功", 'warn');
			         $(this).focus();
                     window.location.href = window.location.href;
				 }else{
					 top.$.jBox.tip("操作失败："+ data.errorMsg, 'warn');
			         $(this).focus();
				 }
			 }
		 });
	 }
 }
 // 转发工单
 function RelayOrder(){
	 var ret = detailIsrequest();
	 if(ret != false){
		 ajaxServer("relayWF",ret,true,function(data){
			 if(data!=null){
				 if(data.flag=="0"){
					 top.$.jBox.tip("转发成功", 'warn');
			         $(this).focus();
                     window.location.href = window.location.href;
				 }else{
					 top.$.jBox.tip("操作失败："+ data.errorMsg, 'warn');
			         $(this).focus();
				 }
			 }
		 });
	 }
 }
 //关闭工单
 function closeOrder(){
	 var ret = detailIsrequest();
	 if(ret != false){
		 ajaxServer("closeWF",ret,true,function(data){
			 if(data!=null){
				 if(data.flag=="0"){
					 top.$.jBox.tip("关闭成功", 'warn');
			         $(this).focus();
                     window.location.href = window.location.href;
				 }else{
					 top.$.jBox.tip("操作失败："+ data.errorMsg, 'warn');
			         $(this).focus();
				 }
			 }
		 });
	 }
 }
 // 重启工单
 function resetOrder(){
	 var ret = detailIsrequest();
	 if(ret != false){
		 ajaxServer("resetWF",ret,true,function(data){
			 if(data!=null){
				 if(data.flag=="0"){
					 top.$.jBox.tip("重启成功", 'warn');
			         $(this).focus();
                     window.location.href = window.location.href;
				 }else{
					 top.$.jBox.tip("操作失败："+ data.errorMsg, 'warn');
			         $(this).focus();
				 }
			 }
		 });
	 }
 }
 
 //格式化按钮名称
 function formatFunName(){
	for(var i = 0; i < $(".actionOrder").length; i++){
		var num = $(".actionOrder").eq(i).attr('funnum');
		var numname = buttons[num]==null?num:buttons[num];
        $(".actionOrder").eq(i).attr('value',numname);
    }
}
 
 function detailIsrequest(notNextNote){
	 var wfid = $("#wfId").eq(0).attr("value");
	 var obj = {id : wfid};
	 
	 //工单标题
	 var title = $("#wfTitle").find("input").eq(0).attr("value");
	 if(title == null || $.trim(title) == ""){
		 top.$.jBox.tip('工单标题不能为空', 'warn');
         $(this).focus();
		 return false;
	 }
	 obj.title = $.trim(title);
	 
	 //订单编号
	 var orderid = $("#wfOrderId").find("input").eq(0).attr("value");
	 if(orderid == null || $.trim(orderid) == ""){
		 top.$.jBox.tip('订单编号不能为空', 'warn');
         $(this).focus();
		 return false;
	 }
	 obj.orderId = $.trim(orderid);
	 
	 //业务订单编号
	 var subId = $("#wfOrderSubId").find("input").eq(0).attr("value");
	 if(subId == null || $.trim(subId) == ""){
		 top.$.jBox.tip('业务订单编号不能为空', 'warn');
         $(this).focus();
		 return false;
	 }
	 obj.suborderId = $.trim(subId);
	 
	 //服务类型（下拉）
	 var serviceType = $("#wfserviceType").val();
	 if(serviceType == null || $.trim(serviceType) == ""){
		 top.$.jBox.tip('请选择工单类型', 'warn');
         $(this).focus();
		 return false;
	 }
	 obj.serviceType = $.trim(serviceType);
	 
	 //工单类型（下拉）
	 var worksheetType = $("#wfworksheetType").val();
	 if(worksheetType == null || $.trim(worksheetType) == ""){
		 top.$.jBox.tip('请选择工单类型', 'warn');
         $(this).focus();
		 return false;
	 }
	 obj.worksheetType = $.trim(worksheetType);
	 
	//自定义类型
	 var diyType = $("#wfDiyType").find("input").eq(0).attr("value");
	 if(diyType != null && $.trim(diyType) != ""){
		 obj.diyType = $.trim(diyType);
	 }
	 
	//处理时效
	 var duration = $("#wfDuration").find("input").eq(0).attr("value");
	 if(duration != null && $.trim(duration) != ""){
		 obj.duration = $.trim(duration);
	 }
	 
	 //紧急程度（下拉）
	 var wfDegree = $("#wfDegree").val();
	 if(wfDegree == null || $.trim(wfDegree) == ""){
		 top.$.jBox.tip('请选择紧急程度', 'warn');
         $(this).focus();
		 return false;
	 }
	 obj.degree = $.trim(wfDegree);
	 
	 //处理内容
	 var wfContent = $("#wfContent").eq(0).attr("value");
	 if(wfContent == null || $.trim(wfContent) == ""){
		 top.$.jBox.tip('处理内容不能为空', 'warn');
         $(this).focus();
		 return false;
	 }
	 obj.content = $.trim(wfContent);
	 
	 //指派给（下拉）
	 var tonodeid = $("#wfTonodeid").val();
	 if(notNextNote == null){
		 if(tonodeid == null || $.trim(tonodeid) == ""){
			 top.$.jBox.tip('请选择指派给谁处理', 'warn');
	         $(this).focus();
			 return false;
		 }
		 obj.toNodeId = $.trim(tonodeid);
		 obj.lastToNodeName = $("#wfTonodeid  option:selected").text()
	 }
	 
	//邮件通知checked  选中  undifiend  未选择
	 var email = $("input[name='email']").attr("checked");
	 // obj.noticeEmail = $.trim(email);
	 
	//短信通知 选中  undifiend  未选择
	 var sms = $("input[name='sms']").attr("checked");
	// obj.noticeSms = $.trim(sms);
	 
	 return obj;
 }

 
 //封装异步请求方法
 function ajaxServer(serverid,param,asyn,callbakfn) {
	 if(asyn == null){
		 asyn = true;
	 }
	 $.ajax({
	      url: serverid,
	      data: param,
	      type: "post",
	      async: asyn, //false 同步，true异步
	      timeout : 3000000, //超时时间设置，单位毫秒
	      datatype: "json",
	      success:function(result){
	        if(callbakfn!=null) callbakfn(result);
	      },
	       complete : function(){
	       },
	      error: function (data, status, e){//服务器响应失败处理函数
	      }
   });
}