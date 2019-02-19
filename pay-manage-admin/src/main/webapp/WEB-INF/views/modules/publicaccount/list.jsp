<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公户账户查询</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.table th{
			white-space: normal;
			align:center
		}
		.control-label{
			display: inline-block;
			width:100px;
			text-align: right;
		}
	</style>
	<script type="text/javascript">
	$(document).ready(function() {
		$("#btnSubmit").click(function(){
			$("#searchForm").submit();
		});
	});
    function page(n, s) {
        $("#pageNo").val(n);
        $("#pageSize").val(s);
        $("#paging").val("1");
        $("#searchForm").submit();
        return false;
    }
    //是否全选
    function selectAll() {
        if($('#isSelected').is(':checked')){
			//选中
            $('input[name="selected"]').attr("checked",'true');//全选
        }else{
            //未选中
            $('input[name="selected"]').removeAttr("checked");//取消全选
		}
    }
    //批量删除
    function batchDelete(){
        var str = "";
        var i = 0;
        $("input[name='selected']:checked").each(function(){
            if(i>0) str+=",";
            str+=$(this).val();
            i++;
        });
		if(str!=""){
            if (confirm("是否确认删除选中的记录？")) {
                document.forms[0].action = "${ctx}/publicaccount/deleteAccountAmount?ids=" + str;
                document.forms[0].submit();
            }
		}else{
		    alert("请选择要删除的记录");
		}
	}

	//数据导入
	function dataImport(){
		window.location.href="${ctx}/publicaccount/toCommitPublicAccount";
	}

    $(function () {
		$("#reduceAmountOperator").val("${paramMap.reduceAmountOperator}");
        $("#addAmountOperator").val("${paramMap.addAmountOperator}");
        $("#descriptionModel").val("${paramMap.descriptionModel}");
    });
	</script>
</head>
<body>
<div class="breadcrumb">
	<label><a href="#">平台管理> </a><a href="#">公户账务管理> </a><a href="#"><b>公户账务查询</b></a></label>
</div>
<tags:message content="${message}" type="${messageType}"/>
 	<form:form id="searchForm" action="${ctx}/publicaccount/publicAccountList"  method="post" class="breadcrumb form-search">
		<table>
			<tr>
				<td>
					<label class="control-label">&nbsp;&nbsp;&nbsp;选择公户：</label>
					<select name="publicAccountCode" id="mchtCode" >
						<option value="">---请选择---</option>
						<c:forEach var="item" items="${pais}">
							<option value="${item.publicAccountCode}" <c:if test="${paramMap.publicAccountCode eq item.publicAccountCode}">selected</c:if> >${item.publicAccountName}</option>
						</c:forEach>
					</select>
				</td>
				<td>
					<label class="control-label" >借方发生额：</label>
					<select id="reduceAmountOperator" name="reduceAmountOperator" style="width:60px;">
						<option value="0">等于</option>
						<option value="1">大于</option>
					</select>
					<input value="${paramMap.reductAmount}" id="reductAmount" name="reductAmount" type="number" maxlength="64" class="input-large"/>
				</td>
				<td>
					<label class="control-label">对方账号：</label>
					<input value="${paramMap.accountNo}" id="accountNo" name="accountNo" type="text" maxlength="64" class="input-large"/>
				</td>
			</tr>
			<tr>
				<td>
					<label class="control-label">对方账户名：</label>
					<input value="${paramMap.accountName}" id="accountName" name="accountName" type="text" maxlength="64" class="input-large"/>
				</td>

				<td >
					<label class="control-label">贷方发生额：</label>
					<select id="addAmountOperator" name="addAmountOperator" style="width:60px;">
						<option value="0">等于</option>
						<option value="1">大于</option>
					</select>
					<input value="${paramMap.addAmount}" id="addAmount" name="addAmount" type="number" maxlength="64" class="input-large"/>
				</td>
				<td>
					<label class="control-label">描述：</label>
					<select id="descriptionModel" name="descriptionModel" style="width:60px;">
						<option value="0">模糊</option>
						<option value="1">精确</option>
					</select>
					<input value="${paramMap.description}" id="description" name="description" type="text" maxlength="255" class="input-large"/>
				</td>
			</tr>
		<tr>
			<td>
				<label class="control-label">摘要：</label>
				<input value="${paramMap.summary}" id="summary" name="summary" type="text" maxlength="64" class="input-large"/>
			</td>
			<td>
				<label class="control-label">&nbsp;&nbsp;&nbsp;交易日期：</label>
				<input type="text" class="input-medium Wdate" name ="beginTime" value="${paramMap.beginTime}"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:false,isShowToday:true});"/>-
				<input type="text" class="input-medium Wdate" name ="endTime" value="${paramMap.endTime}"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:false,isShowToday:true});"/>
			</td>
			<td align="right">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
				<shiro:hasPermission name="publicaccount:commit">
					<input  class="btn btn-primary" type="button" value="数据导入" onclick="dataImport()"/>
				</shiro:hasPermission>
				<shiro:hasPermission name="publicaccount:batchDelete">
					<input  class="btn btn-primary" type="button" value="批量删除" onclick="batchDelete()"/>
				</shiro:hasPermission>
			</td>
		</tr>

		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="paging" name="paging" type="hidden" value="0"/>
 	</form:form>
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="isSelected" onchange="selectAll()"/></th>
				<th>公户名称</th>
				<th>交易时间</th>
				<th>借方发生额</th>
				<th>贷方发生额</th>
				<th>账户余额</th>
				<th>对方账号</th>
				<th>对方账号名</th>
				<th>对方开户行</th>
				<th>摘要</th>
				<th>描述</th>
				<th>提到账时间</th>
				<th>操作人</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
				<c:forEach items="${list}" var="report">
					<tr>
						<td><input type="checkbox" name="selected" value="${report.id}"/></td>
						<td>${paisMap[report.publicAccountCode].publicAccountName}</td>
						<td><fmt:formatDate value="${report.tradeTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>${report.reduceAmount }</td>
						<td>${report.addAmount }</td>
						<td>${report.balance }</td>
						<td>${report.accountNo}</td>
						<td>${report.accountName }</td>
						<td>${report.openAccountBankName}</td>
						<td>${report.summary}</td>
						<td>${report.description}</td>
						<td><fmt:formatDate value="${report.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>${report.operatorName}</td>
						<td><a href="${ctx}/publicaccount/toEdit?id=${report.id}">修改</a></td>
					</tr>
				</c:forEach>

		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>