

function edit(id){
		document.forms[0].action="${ctx}/bowei/repaymentEdit?id="+id;
		document.forms[0].submit();
//		validateFrom();
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
//	    	validateFrom();
	    }
	   //删除一行支付方式
	   function deletePayType(obj){
		   var  payTypeNum = $("#payTypeTable tr").length;
		   if(payTypeNum <2){
			   alert("至少添加一条！");
				   return;
		   }
		   $(obj).parent().parent().parent().parent().remove();
		   var nowTr =  $(obj).parent().parent().parent().parent();
		   $(nowTr).insertBefore($(nowTr).prev());
			 for(var i=0;i<$("#payTypeTable tr").length;i++){
				 $("#payTypeTable tr").eq(i).find("td:first").find("span").text("顺序 "+(i+1));
				 $("#payTypeTable tr").eq(i).find("input[type='hidden']").val((i+1));
			 }
	   }

	//上移
	 function upTr(obj){
		 var nowTr =  $(obj).parent().parent().parent().parent();
		  if($(nowTr).prev().html()==null){ //获取tr的前一个相同等级的元素是否为空
             alert("已经是最顶部了!");
			 return;
		 }{
			 $(nowTr).insertBefore($(nowTr).prev());
			 for(var i=0;i<$("#payTypeTable tr").length;i++){
				 $("#payTypeTable tr").eq(i).find("td:first").find("span").text("顺序 "+(i+1));
				 $("#payTypeTable tr").eq(i).find("input[type='hidden']").val((i+1));
			 }
		 }
	 }
	  //下移
	 function downTr(obj){
		 var nowTr =  $(obj).parent().parent().parent().parent();
		  if($(nowTr).next().html()==null){ //获取tr的前一个相同等级的元素是否为空
            alert("已经是最底部了!");
			 return;
		 }{
			 $(nowTr).insertAfter($(nowTr).next());
			 for(var i=0;i<$("#payTypeTable tr").length;i++){
				 $("#payTypeTable tr").eq(i).find("td:first").find("span").text("顺序 "+(i+1));
				 $("#payTypeTable tr").eq(i).find("input[type='hidden']").val((i+1));
			 }
		 }
	 }
	  