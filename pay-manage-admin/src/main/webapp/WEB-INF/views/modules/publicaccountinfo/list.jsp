<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公户信息查询</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.table th{
			white-space: normal;
			align:center
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
	<label><a href="#">平台管理> </a><a href="#">公户信息管理> </a><a href="#"><b>公户信息查询</b></a></label>
</div>
<tags:message content="${message}" type="${messageType}"/>
 	<form:form id="searchForm" action="${ctx}/publicaccountinfo/list"  method="post" class="breadcrumb form-search">
		<table>
			<tr>
				<td>
					<label class="control-label">公户编号：</label>
					<input value="${paramMap.publicAccountCode}" id="publicAccountCode" name="publicAccountCode" type="text" maxlength="64" class="input-large"/>
				</td>
				<td>
					<label class="control-label">公户账号：</label>
					<input value="${paramMap.publicAccountNo}" id="publicAccountNo" name="publicAccountNo" type="text" maxlength="64" class="input-large"/>
				</td>
				<td>
					<label class="control-label">公户名称：</label>
					<input value="${paramMap.publicAccountName}" id="publicAccountName" name="publicAccountName" type="text" maxlength="64" class="input-large"/>
				</td>
			</tr>
		<tr>
			<td colspan="3" align="right">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
				<input  class="btn btn-primary" type="button" value="新增公户" onclick="batchDelete()"/>
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
				<th>公户编号</th>
				<th>公户名称</th>
				<th>公户账号</th>
				<th>开户银行</th>
				<th>模板名称</th>
				<th>备注</th>
				<th>已绑定手机号</th>
				<th>状态</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
				<c:forEach items="${list}" var="report">
					<tr>
						<td>${report.publicAccountCode }</td>
						<td>${report.publicAccountName }</td>
						<td>${report.publicAccountNo }</td>
						<td>${report.publicOpenAccountBankName }</td>
						<td>${report.modelName }</td>
						<td>${report.remark}</td>
						<td>${report.bindPhones}</td>
						<td><c:if test="${report.status == 1}">启用</c:if>
							<c:if test="${report.status == 2}">停用</c:if>
						</td>
						<td>${report.createOperatorName}</td>
						<td><fmt:formatDate value="${report.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><a href="${ctx}/publicaccountinfo/toEdit?publicAccountCode=${report.publicAccountCode}">修改</a></td>
					</tr>
				</c:forEach>

		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>