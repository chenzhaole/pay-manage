  <%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单详情</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
        function viewChange(divId) {
            $.jBox($("#" + divId).html(), {title:"改签记录", buttons:{"关闭":true},
                bottomText:""});
        }
        
        function sendQuestion() {
            $("#sendForm").submit();
        }
	</script>
</head>
<body>

<div class="breadcrumb">
	<label><th>基本信息</th></label>
</div>
<table id="infoTable" class="table table-striped table-bordered table-condensed">
  <!--   <thead>
    	<tr><th colspan="3">基本信息</th></tr>
    </thead> -->
    <tbody>
    <tr>
        <td width="33%">商户名称: ${merchant.name}</td>
        <td width="33%">商户编号: ${merchant.mchtNo}</td>
        <td width="33%">客服联系人: ${merchant.serviceContractName}</td>
    </tr>
    <tr>
        <td>商户简称: ${merchant.shortName}</td>
        <td>商户类型: ${merchant.mchtType}</td>
        <td>客服手机: ${merchant.serviceMobile}</td>
    </tr>
    <tr>
        <td>公司地址: ${merchant.companyAdr}</td>
        <td>经营类型: ${merchant.businessScope}</td>
        <td>客服电话: ${merchant.servicePhone}</td>
    </tr>
    <tr>
        <td>联系人姓名: ${merchant.contactName}</td>
        <td>行业类别: ${merchant.mchtType}</td>
        <td>客服QQ: ${merchant.serviceQq}</td>
    </tr>
    <tr>
        <td>联系人电话: ${merchant.mobile}</td>
        <td>邮箱地址: ${merchant.email}</td>
        <td>客服微信: ${merchant.serviceWx}</td>
    </tr>
    <tr>
        <td>电话: ${merchant.mobile}</td>
        <td>所属渠道: 自有大客户</td>
        <td></td>
    </tr>
    </tbody>
</table>

<div id="searchForm" class="breadcrumb">
	<label><th>支付通道信息</th></label>
</div>

<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    	<tr>
	        <th width="10%">支付类型名称</th>
	        <th width="5%">结算费率(‰)</th>
	        <th width="10%">单日限额</th>
	        <th width="10%">第三方商户号</th>
	        <th width="15%">银行卡号</th>
	        <th width="15%">开户人</th>
	        <th width="15%">开户银行</th>
    	</tr>
    </thead>
    <tbody>
        <tr>
            <td>支付宝扫码</td>
            <td>**</td>
            <td>${merchant.name}</td>
            <td>${merchant.shortName}</td>
            <td>${merchant.fundSettleBankCard}</td>
            <td>${merchant.fundSettleAccountName}</td>
            <td>${merchant.fundSettleBankName}</td>
        </tr>
        <tr>
            <td>微信Wap支付</td>
            <td>**</td>
            <td>${merchant.name}</td>
            <td>${merchant.shortName}</td>
            <td>${merchant.fundSettleBankCard}</td>
            <td>${merchant.fundSettleAccountName}</td>
            <td>${merchant.fundSettleBankName}</td>
        </tr>
    </tbody>
</table>

<div class="form-actions">
    <form id="sendForm" action="${ctx}/process/question/form" method="post">
    <input type="hidden" id="orderId" name="orderId" value="${order.orderId}" />
    <input type="hidden" id="status" name="status" value="${order.status}" />
    <input type="hidden" id="name" name="name" value="${order.name}" />
    <input type="hidden" id="addFlag" name="addFlag" value="1" />   
    </form>
    &nbsp;&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <shiro:hasPermission name="process:question:view">
    <input id="btnCancel" class="btn btn-primary" type="button" value="问题反馈" onclick="sendQuestion();"/>
    </shiro:hasPermission>
</div>

</body>
</html>