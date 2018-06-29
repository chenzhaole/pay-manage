<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>平台银行卡BIN列表</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });

        function add(){
        	document.forms[0].action="${ctx}/platform/addCardBinPage";
        	document.forms[0].submit();
        }

        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
	   
	</script>
</head>
<body>
<div class="breadcrumb">
	<label><a href="#">平台银行卡BIN管理></a><a href="#"><b>平台银行卡BIN列表</b></a></label>
</div>

 	<tags:message content="${message}" type="${messageType}"/>
 	
 	<form id="searchForm" action="${ctx}/platform/cardBinList" method="post" class="breadcrumb form-search">
 		<table>
 			<tr>
 				<td>
					<label>卡BIN号：</label><input value="${paramMap.cardBinNo}" name="cardBinNo" type="text" maxlength="64" class="input-medium"/>
 				</td>
				<td>
					<label>所属银行：</label>
					<select name="bankCode" class="selectpicker bla bla bli" data-live-search="true">
						<option value="">--请选择--</option>
						<c:forEach items="${platBanks}" var="platBank">
							<option value="${platBank.bankCode}"
									<c:if test="${paramMap.bankCode == platBank.bankCode}">selected</c:if>
							>${platBank.bankName}</option>
						</c:forEach>
						</optgroup>
					</select>
				</td>
				<td>
					<label>卡类型：</label>
					<select  value=""  name="cardType" type="text" maxlength="64" class="input-medium">
						<option value="">--请选择--</option>
						<option value="DEBIT" <c:if test="${paramMap.cardType == 'DEBIT'}">selected</c:if>>借记卡</option>
						<option value="CREDIT" <c:if test="${paramMap.cardType == 'CREDIT'}">selected</c:if>>信用卡</option>
						<option value="PREPAID" <c:if test="${paramMap.cardType == 'PREPAID'}">selected</c:if>>预付卡</option>
						<option value="SEMI_CRE" <c:if test="${paramMap.cardType == 'SEMI_CRE'}">selected</c:if>>准贷记卡</option>
					</select>
				</td>
				<%--<td>--%>
					<%--<label>支付方式：</label><input value="${paramMap.paytype}" name="paytype" type="text" maxlength="64" class="input-medium"/>--%>
 				<%--</td>--%>
				<td>
					<label>状态：</label>
					<select name="status" type="text" maxlength="64" class="input-medium">
						<option value="">--请选择--</option>
						<option <c:if test="${paramMap.status == 1}">selected</c:if> value="1">启用</option>
						<option <c:if test="${paramMap.status == 2}">selected</c:if> value="2">停用</option>
					</select>
 				</td>
 				<td>
					&nbsp;&nbsp;&nbsp;
					<input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
					<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
					<input id="clearButton" class="btn btn-primary" type="button" value="新增" onclick="add()" style="margin-left: 5px;"/>
 				</td>
 			</tr>
 		</table>
	</form>
 	
 	
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr >
				<th>NO</th>
	        	<th>银行名称</th>
	        	<th>卡BIN号</th>
	        	<th>卡名称</th>
	        	<th>机构代码</th>
	        	<th>卡长度</th>
				<th>卡类型</th>
	        	<th>状态</th>
	        	<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<%int i=0; %>
		<c:forEach items="${page.list}" var="cardBin">
			<%i++; %>
			<tr>
				<td><%=i%></td>
				<td>${cardBin.bankName}</td>
				<td>${cardBin.cardBinNo}</td>
				<td>${cardBin.cardName}</td>
				<td>${cardBin.orgNo}</td>
				<td>${cardBin.cardLength}</td>
				<td><c:if test="${cardBin.cardType == 'DEBIT'}">借记卡</c:if>
					<c:if test="${cardBin.cardType == 'CREDIT'}">信用卡</c:if>
					<c:if test="${cardBin.cardType == 'PREPAID'}">预付卡</c:if>
					<c:if test="${cardBin.cardType == 'SEMI_CRE'}">准贷记卡</c:if>
				</td>
				<td><c:if test="${cardBin.status == 1}">启用</c:if>
					<c:if test="${cardBin.status == 2}">停用</c:if>
				<td>
					<a href="${ctx}/platform/addCardBinPage?id=${cardBin.id}">修改</a>|
					<a href="${ctx}/platform/deleteCardBin?id=${cardBin.id}" onclick="return confirmx('是否确认删除此记录？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
<div class="pagination">${page}</div>
</body>
</html>