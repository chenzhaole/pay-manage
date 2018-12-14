<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易订单列表</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">

	</style>
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

        $(document).ready(function() {
			$("#btnExport").click(function(){
				$("#searchForm").attr("action","${ctx}/order/export");
				$("#searchForm").submit();
                $("#searchForm").attr("action","${ctx}/order/list");
			});

            $("#batchNotify").click(function(){
                $("#searchForm").attr("action","${ctx}/order/batchReissueMchtNotify");
                $("#searchForm").submit();
            });

            $("#batchQuery").click(function(){
                $("#searchForm").attr("action","${ctx}/order/batchReissueMchtQuery");
                $("#searchForm").submit();
            });



        });
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
            $("#paging").val("1");
			$("#searchForm").submit();
	    	return false;
	    }
	    function reSet() {
			$("#customerSeq")[0].value = "";
			$("#platformSeq")[0].value = "";
			$("#beginDate")[0].value = "";
			$("#endDate")[0].value = "";
			$("input[type='text']").val("");
			$("#mchtId")[0].value = "";
            $("#chanId")[0].value = "";
			$("#platProductId")[0].value = "";
			$("#chanMchtPaytypeId")[0].value = "";
			$("#payType")[0].value = "";
			$("#status")[0].value = "";
			$("#supplyStatus")[0].value = "";
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
		//下拉搜索框初始化
		$(window).on('load', function () {
			$('.selectpicker').selectpicker({});
		});
        <%--function supplyNotify(orderId,suffix){--%>
            <%--if(confirm("确认补发通知吗？")){--%>
                <%--$.ajax({--%>
                    <%--url:'${ctx}/order/supplyNotify',--%>
                    <%--type:'POST', //GET--%>
                    <%--async:true,    //或false,是否异步--%>
                    <%--data:{--%>
                        <%--'orderId':orderId,'suffix':suffix--%>
                    <%--},--%>
                    <%--timeout:5000,    //超时时间--%>
                    <%--dataType:'text',    //返回的数据格式：json/xml/html/script/jsonp/text--%>
                    <%--success:function(data){--%>
                        <%--alert(data);--%>
                    <%--}--%>
                <%--});--%>
			<%--}--%>
		<%--}--%>

	</script>
</head>
<body>
<div class="breadcrumb">
	<label>
		<th><a href="#">交易管理</a> > <a href="#"><b>支付流水列表</b></a></th>
	</label>
</div>
	<form id="searchForm" action="${ctx}/order/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="paging" name="paging" type="hidden" value="0"/>
		<table>
			<tr>
	            <td>
	                <div class="control-group">
	                    <label class="control-label">商户名称：</label>
	                    <div class="controls">
	                        <select name="mchtId" id="mchtId"  class="selectpicker bla bla bli" data-live-search="true">
								<option value="">---请选择---</option>
								<c:forEach var="mcht" items="${mchtList}">
									<option value="${mcht.mchtCode}" <c:if test="${paramMap.mchtId eq mcht.id}">selected</c:if> >${mcht.name}</option>
								</c:forEach>
							</select>
	                    </div>
	                </div>
	            </td>
	            <td>
	                <div class="control-group">
	                    <label class="control-label">上游通道：</label>
	                    <div class="controls">
	                        <select name="chanId" id="chanId"  class="selectpicker bla bla bli" data-live-search="true">
								<option value="">---请选择---</option>
								<c:forEach var="chanInfo" items="${chanInfoList}">
									<option value="${chanInfo.chanCode}" <c:if test="${paramMap.chanId eq chanInfo.id}">selected</c:if> >${chanInfo.name}</option>
								</c:forEach>
							</select>
	                    </div>
	                </div>
	            </td>
	            <td>
	                <div class="control-group">
	                    <label class="control-label">支付产品：</label>
	                    <div class="controls">
	                       <select name="platProductId" id="platProductId"  class="selectpicker bla bla bli" data-live-search="true">
								<option value="">---请选择---</option>
								<c:forEach var="product" items="${productList}">
									<option value="${product.id}" <c:if test="${paramMap.platProductId eq product.id}">selected</c:if> >${product.name}</option>
								</c:forEach>
							</select>
	                    </div>
	                </div>
	            </td>

	            <td>
	                <div class="control-group">
	                    <label class="control-label">支付方式：</label>
	                    <div class="controls">
	                        <select name="payType" id="payType"  class="selectpicker bla bla bli" data-live-search="true">
								<option value="">---请选择---</option>
								<c:forEach var="paymentTypeInfo" items="${paymentTypeInfos}">
									<option value="${paymentTypeInfo.code}" <c:if test="${paramMap.payType eq paymentTypeInfo.code}">selected</c:if>>${paymentTypeInfo.desc}</option>
								</c:forEach>
							</select>
	                    </div>
	                </div>
	            </td>
	        </tr>
	        <tr>

	            <td>
	                <div class="control-group">
	                    <label class="control-label">官方订单号：</label>
	                    <div class="controls">
	                        <input value="${paramMap.officialSeq}" id="officialSeq" name="officialSeq" type="text" maxlength="64" class="input-large"/>
	                    </div>
	                </div>
	            </td>
	            <td>
	                <div class="control-group">
	                    <label class="control-label">商户订单号：</label>
	                    <div class="controls">
	                      <input value="${paramMap.customerSeq}" id="customerSeq" name="customerSeq" type="text" maxlength="64" class="input-large"/>
	                    </div>
	                </div>
	            </td>
	             <td>
	                <div class="control-group">
	                    <label class="control-label">平台订单号：</label>
	                    <div class="controls">
	                        <input value="${paramMap.platformSeq}" id="platformSeq" name="platformSeq" type="text" maxlength="64" class="input-large"/>
	                    </div>
	                </div>
	            </td>
	            <td>
	                <div class="control-group">
	                    <label class="control-label">上游订单号：</label>
	                    <div class="controls">
	                       <input value="${paramMap.chanSeq}" id="chanSeq" name="chanSeq" type="text" maxlength="64" class="input-large"/><br>
	                    </div>
	                </div>
	            </td>
	        </tr>

	        <tr>

	             <td>
	                <div class="control-group">
	                    <label class="control-label">通道商户支付方式：</label>
	                    <div class="controls">
	                        <select name="chanMchtPaytypeId" id="chanMchtPaytypeId" class="selectpicker bla bla bli" data-live-search="true">
								<option value="">---请选择---</option>
								<c:forEach var="chanMchtPayType" items="${chanMchtPaytypeList}">
									<option value="${chanMchtPayType.id}" <c:if test="${paramMap.chanMchtPaytypeId eq chanMchtPayType.id}">selected</c:if> >${chanMchtPayType.name}</option>
								</c:forEach>
							</select>
	                    </div>
	                </div>
	            </td>

	            <td>
	                <div class="control-group">
	                    <label class="control-label">订单状态：</label>
	                    <div class="controls">
	                      <select id="status" name="status">
								<option value="">---请选择---</option>
								<c:forEach var="dict" items="${fns:getDictList('pay_status')}">
									<option value="${dict.value}" <c:if test="${paramMap.status eq dict.value}">selected</c:if>>${dict.label}</option>
								</c:forEach>
							</select>
	                    </div>
	                </div>
	            </td>
	            <td>
	                <div class="control-group">
	                    <label class="control-label">补单状态：</label>
	                    <div class="controls">
	                        <select id="supplyStatus" name="supplyStatus">
								<option value="">---请选择---</option>
								<c:forEach var="dict" items="${fns:getDictList('supply_status')}">
                                    <option value="${dict.value}" <c:if test="${paramMap.supplyStatus eq dict.value}">selected</c:if>>${dict.label}</option>
								</c:forEach>
							</select>
	                    </div>
	                </div>
	            </td>
	        </tr>

	        <tr>
	         	<td colspan="2">
	         		<div class="control-group">
	                    <label class="control-label">交易时间：</label>
	                    <div class="controls">
	                       <input id="beginDate" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
			               value="${paramMap.beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,readOnly:true,maxDate:$dp.$('endDate').value,isShowOK:false,isShowToday:false});"/>至

			               <input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
				               value="${paramMap.endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,readOnly:true,minDate:$dp.$('beginDate').value,isShowOK:false,isShowToday:false});"/>
							<input type="checkbox" name="isstat" id="isstat" value="1"/>统计汇总
						</div>

	               	</div>
	         	</td>
	         	<td colspan="2" align="right">
	         		<div class="btn-group">
	           	    	<input id="btnSubmit" class="btn btn-primary pull-right" type="submit" value="查询">
	               	</div>
	                <div class="btn-group">
	                	<input id="clearButton" class="btn btn-primary pull-right" type="button" value="重置" onclick="reSet()" />
	                </div>
					<div class="btn-group">
					<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
					</div>
					<shiro:hasPermission name="order:list:op">
						<div class="btn-group">
							<input id="batchNotify" class="btn btn-primary" type="button" value="批量异步通知"/>
						</div>
						<div class="btn-group">
							<input id="batchQuery" class="btn btn-primary" type="button" value="批量查询"/>
						</div>
					</shiro:hasPermission>

	         	</td>
	        </tr>
		</table>
	</form>
	<label>| 总笔数：${orderCount} | </label>
	<label>总金额：${amount} 元| </label>
	<label>成功笔数：${successCount} | </label>
	<label>成功金额：${successAmount} 元| </label>
	<tags:message content="${message}"/>

	<table id="contentTable" class="table table-striped table-bordered table-condensed table-hover" style="word-wrap:break-word; word-break:break-all;">
		<thead>
			<tr >
				<th>商户名称</th>
				<th>支付产品名称<br>通道商户支付方式</th>
				<th>平台订单号<br>商户订单号</th>
				<th>上游通道订单号</th>
				<th>官方订单号</th>
				<th>交易金额</th>
				<th>订单状态</th>
				<th>补单状态</th>
				<th>创建时间<br>支付时间</th>
				<shiro:hasPermission name="order:list:op">
				<th>&nbsp;操&nbsp;作&nbsp;&nbsp;</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="orderInfo">
			<tr>
				<td>${orderInfo.mchtCode}</td>
				<td>${orderInfo.platProductId}<br>${orderInfo.chanMchtPaytypeId}</td>

				<td><a href="${ctx}/order/detail?id=${orderInfo.id}
				&beginDate=<fmt:formatDate value="${orderInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>">
						${orderInfo.platOrderId}</a><br>${orderInfo.mchtOrderId}</td>
				<td>${orderInfo.chanOrderId}</td>
				<td>${orderInfo.officialOrderId}</td>
				<td><fmt:formatNumber type="number" value="${orderInfo.amount*0.01}" pattern="0.00" maxFractionDigits="2"/>元</td>
				<td>
					${fns:getDictLabel(orderInfo.status,'pay_status' ,'' )}
				</td>
				<td>
						${fns:getDictLabel(orderInfo.supplyStatus,'supply_status' ,'' )}
				</td>
				<td><fmt:formatDate value="${orderInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				<br><fmt:formatDate value="${orderInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="order:list:op">
					<td>
						<c:if test="${orderInfo.status == '2'}">
							<a href="${ctx}/order/supplyNotify?orderId=${orderInfo.id}&suffix=<fmt:formatDate value="${orderInfo.createTime}"  pattern="yyyyMM"/>" onclick="return confirmx('是否确认向下游补发通知？', this.href)">补发通知</a> | </c:if>
						<c:if test="${orderInfo.status != '2'}"><a href="${ctx}/order/querySupply?orderId=${orderInfo.id}" onclick="return confirmx('是否确认重新向上游通道发起查单请求？', this.href)">查单</a> |</c:if>
						<shiro:hasPermission name="order:statPayOrderById">
							<a href="${ctx}/order/statPayOrderById?orderId=${orderInfo.id}" onclick="return confirmx('是否确认将此记录重新入账？', this.href)">入账</a>
						</shiro:hasPermission>
							<%--|
                            <a href="${ctx}/process/question/form?orderId=${orderInfo.id}">同步状态</a>  --%>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<!-- 翻页（start） -->
<!--  <div class="pagination"> -->
<!--     <table> -->
<!--         <tr> -->
<%--             <c:if test="${page.firstPage ==}"> --%>
<%--                 <td><a href="${ctx}/order/list?pageNo=${page.prePage}&pageSize=${page.pageSize}">前一页</a></td> --%>
<%--             </c:if> --%>
<%--             <c:forEach items="${page.navigatepageNos}" var="nav"> --%>
<%--                 <c:if test="${nav == page.pageNo}"> --%>
<%--                     <td style="font-weight: bold;">${nav}</td> --%>
<%--                 </c:if> --%>
<%--                 <c:if test="${nav != page.pageNo}"> --%>
<%--                     <td><a href="${ctx}/order/list?pageNo=${nav}&pageSize=${page.pageSize}">${nav}</a></td> --%>
<%--                 </c:if> --%>
<%--             </c:forEach> --%>
<%--             <c:if test="${page.hasNextPage}"> --%>
<%--                 <td><a href="${ctx}/order/list?pageNo=${page.nextPage}&pageSize=${page.pageSize}">下一页</a></td> --%>
<%--             </c:if> --%>
<!--            <td> -->
<%--            	<a href="javascript:">&nbsp;&nbsp;&nbsp;&nbsp;当前 ${page.pageNo} / ${page.pages} 页,共 ${page.total} 条</a> --%>
<!--            </td> -->
<!--         </tr> -->
<!--     </table> -->
<!--  </div> -->
<div class="pagination">${page}</div>
<!-- 翻页（end） -->
</body>
</html>