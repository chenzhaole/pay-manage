<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>余额查询列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	    $(document).ready(function() {
            //导出数据提示
            $("#exportButton").click(function(){
                top.$.jBox.confirm("确认要导出订单问题反馈数据吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/process/question/export/");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action","${ctx}/process/question/list/");
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            });
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
	    function reSet() {
			$("#customerSeq")[0].value = "";
			$("#platformSeq")[0].value = "";
			$("#status")[0].value = "";
			$("#beginDate")[0].value = "";
			$("#endDate")[0].value = "";
        }
        function approveSend(content){
            var orderId = $(content).parent().parent().children().eq(0).text();
		    var url = "save";
			$.ajax({
			type:"POST",
			dataType: "text",
			async:false,
			url: url,
			data: {"approveSend":1,"orderId":orderId}    //通过"确认发送"标识，来修改"审批状态"为已审批、"核实状态"为未核实
			});
		   $(content).parent().next().text("已审批");
		   $(content).parent().next().next().replaceWith("<td style='background-color:#ff0000'>未核实</td>");
		   $(content).parent().next().next().next().html("<a href='${ctx}/process/question/form?orderId="+orderId+"'>核实</a>");
		   $(content).remove();
        }
	   function add(){
		   
	   }
	   function showMoreSearch(){
		   $("#moreSearch").show();
		   $("#showMoreSearch").hide();
		   $("#hideMoreSearch").show();
	   }
	   
	   function hideMoreSearch(){
		   $("#moreSearch").hide();
		   $("#showMoreSearch").show();
		   $("#hideMoreSearch").hide();
	   }
	</script>
</head>
<body>
<!-- 	<ul class="nav nav-tabs"> -->
<%-- 		<li class="active"><a href="${ctx}/order/list">交易订单列表</a></li> --%>
<!-- 	</ul> -->



	<form id="searchForm" action="${ctx}/order/balance" method="post" class="breadcrumb form-search">
		<div class="panel panel-default">
	        <div class="panel-heading clearfix">
	            <div class="pull-left">
	            	<label for="status">商户名称：</label>
			        <select id="mchtId" name="mchtId">
			            <option value="">---请选择---</option>
			            <c:forEach items="${cpInfoList}" var="cpInfo">
			            	<option value="${cpInfo.cpId}" <c:if test="${cpInfo.cpId eq mchtId}">selected</c:if>>${cpInfo.cpName}</option>
			            </c:forEach>
			        </select>&nbsp;
					<label for="beginDate">查询日期：</label>
					&nbsp;
			        <input id="queryDay" name="queryDay" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
			               value="${queryDay}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
				</div>
		            <div class="pull-right" >
		           	    <div class="btn-group">
		           	    	<input id="btnSubmit" class="btn btn-primary pull-right" type="submit" value="查询" >
		               	</div>
		                <div class="btn-group">
		                	<input id="clearButton" class="btn btn-primary pull-right" type="button" value="重置" onclick="reSet()" />
		                </div>
		            </div>
	        
			</div>
		</div>
	</form>
	
	<tags:message content="${message}"/>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed table-hover">
		<thead>
			<tr >
				<th width="50%">商户名称</th>
				<th width="50%">余额</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${cpInfoList}" var="cpInfo" >
				<tr>
					<td>${cpInfo.cpName }</td>
					<td>
						<c:set var="key"> 
						    <c:out value="${cpInfo.cpId}" /> 
						</c:set> 
						<c:out value="${balanceMap[key]}" /> 
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>