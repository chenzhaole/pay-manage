
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>订单详情</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp"%>
<script type="text/javascript">
	function viewChange(divId) {
		$.jBox($("#" + divId).html(), {
			title : "改签记录",
			buttons : {
				"关闭" : true
			},
			bottomText : ""
		});
	}

	function sendQuestion() {
		$("#sendForm").submit();
	}
</script>
</head>
<body>

	<ul class="nav nav-tabs">
		<li><a href="${ctx}/order/list">交易订单列表</a></li>
	</ul>


	<table id="infoTable"
		class="table table-striped table-bordered table-condensed">
		<tbody>
			<tr>
				<td width="33%">商户号: xxxxx</td>
				<td width="33%">结算卡号: xxxxx</td>
				<td width="33%">消费合计笔数: xxxxx</td>
			</tr>
			<tr>
				<td width="33%">费率: xxxxx</td>
				<td width="33%">结算类型: xxxxx</td>
				<td width="33%">消费合计金额: xxxxx</td>
			</tr>
		</tbody>
	</table>

	<div id="searchForm" class="breadcrumb">
		<label><th>订单信息</th></label>
	</div>

	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th width="33%">商品名称：<input id="goodsName" name="goodsName"
					type="text" value=""></th>
				<th width="33%">售价：<input id="price" name="price" type="text"
					value=""></th>
				<th width="33%">备注：<input id=remark name="remark" type="text"
					value=""></th>
			</tr>
		</thead>
	</table>
	
	<table id="contentTable" class="">
		<tbody>
			<tr align="center">
				<td >
					<input id="btnCancel" class="btn btn-primary" type="button" value="生成支付宝二维码" onclick="javascript:reset();"/>
	   			</td>
				<td></td>
				<td>
					<input id="btnOk" class="btn btn-success" type="button" value="生成微信二维码" onclick="save();"/>
				</td>
			</tr>
		</tbody>
	</table>

</body>
</html>