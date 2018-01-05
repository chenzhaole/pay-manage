<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收银台</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        function ok(biz){
        	document.forms[0].action="${ctx}/channel/createOrder?biz="+biz;
        	document.forms[0].submit();
        }
	   
	</script>
</head>
<body>

<div class="breadcrumb">
	<label><th><a href="#">收银台</a> > <a href="#"><b>收银台首页</b></a></th></label>
</div>

<form id="searchForm" action="${ctx}/channel/addSave" method="post" ">
<tags:message content="${message}" type="${messageType}"/>
<input type="hidden" id="op" name="op" value="${op }"/>
<input type="hidden" name="id" value="${channel.id }"/>

<div class="breadcrumb">
	<label><th>订单信息</th></label>
</div>

<table class="table">
<tr>

<td>
	<div class="control-group">
	  <label class="control-label" for="input01"><font color="red">&nbsp;*</font>商品名称</label>
	  <div class="controls">
	    <input name="goods" value="${goods }" placeholder="" class="input-xlarge" type="text">
	  </div>
	</div>
</td>

<td>
	<div class="control-group">
	  <label class="control-label" for="input01"><font color="red">&nbsp;*</font>价格(分)</label>
	  <div class="controls">
	    <input name="amount" value="${amount }" placeholder="" class="input-nomal" type="text">
	  </div>
	</div>
</td>

<td>
	<div class="control-group">
	  <label class="control-label" for="input01">备注</label>
	  <div class="controls">
	    <input name="desc" value="${desc }" placeholder="" class="input-xxlarge" type="text">
	  </div>
	</div>
</td>

</tr>
</table>

<div class="breadcrumb">
	<input id="btnSubmit" class="btn btn-primary" type="button" value="生成支付宝二维码" onclick="javascript:ok(31);" style="margin-left: 5px;">
	<input id="clearButton" class="btn btn-success" type="button" value="生成微信二维码" onclick="javascript:ok(21);" style="margin-left: 5px;"/>
</div>
<img src='http://qr.liantu.com/api.php?&w=200&text=weixin://wxpay/bizpayurl?pr=J9XpgHI'>
</form>
</body>
</html>