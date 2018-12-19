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
			$("#mchtId")[0].value = "";
			$("#queryDate")[0].value = "";
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
	<div class="breadcrumb">
		<label>
			<th><a href="#">交易管理</a> > <a href="#"><b>余额查询</b></a></th>
		</label>
	</div>
	<form id="searchForm" action="${ctx}/order/balance" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="paging" name="paging" type="hidden" value="1"/>
		<input id="isSelectInfo" name="isSelectInfo" type="hidden" value="0"/>
        <div class="panel panel-default">
	        <div class="panel-heading clearfix">
	            <div class="pull-left">
	            	<label>商户名称：</label>
			        <select id="mchtId" name="mchtId">
			            <option value="">---请选择---</option>
			            <c:forEach items="${mchtInfoList}" var="mchtInfo">
			            	<option value="${mchtInfo.mchtCode}" <c:if test="${mchtInfo.mchtCode eq mchtId}">selected</c:if>>${mchtInfo.name}</option>
			            </c:forEach>
			        </select>&nbsp;
					<label>查询日期：</label>
					&nbsp;
			        <input id="queryDate" name="queryDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
			               value="${queryDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
				</div>
		            <div class="pull-right" style="margin-right: 20%;" >
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
	<label>商户总金额合计：${mchtTotalBalance}元</label>&nbsp;&nbsp;|&nbsp;&nbsp;
	<label>商户可用余额合计：${mchtAvailTotalBalance}元</label>&nbsp;&nbsp;|&nbsp;&nbsp;
	<label>商户冻结金额合计：${mchtFreezeTotalAmountBalance}元</label>&nbsp;&nbsp;|&nbsp;&nbsp;

	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed table-hover">
		<thead>
			<tr >
				<th width="25%">商户名称</th>
				<th width="25%">总金额(元)</th>
				<th width="25%">可用余额(元)</th>
				<th width="25%">冻结金额(元)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${mchtAccountDetail}" var="account" >
				<tr>
					<td>${account.mchtName}</td>
					<td
							<c:if test="${account.cashTotalAmount < 0}">style="color: red"</c:if>
					>
							${account.cashTotalAmount}
					</td>
					<td
							<c:if test="${account.settleTotalAmount < 0}">style="color: red"</c:if>
					>
							${account.settleTotalAmount}
					</td>
					<td  <c:if test="${account.freezeTotalAmount < 0}">style="color: red"</c:if>
					>
							${account.freezeTotalAmount}
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<%--<div class="pagination">${page}</div>--%>
</body>
</html>