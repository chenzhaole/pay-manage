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

        $(function () {
            jQuery.validator.addMethod("alnum", function (value, element) {
                return this.optional(element) || /^[a-zA-Z0-9]+$/.test(value);
            }, "只能包括英文字母和数字");
            $("#channelForm").validate({
                debug: false, //调试模式取消submit的默认提交功能
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function (form) {   //表单提交句柄,为一回调函数，带一个参数：form
                    showShadow();
                    document.forms[0].action = "${ctx}/proxy/changeProxyStatusSave";
                    document.forms[0].submit();
                },
                errorPlacement: function (error, element) {
                    error.appendTo(element.parent());
                },
                rules: {
                    bankCode: {
                        alnum: true,
                        required: true,
                        maxlength: 32
                    },
                    bankName: {
                        required: true,
                        maxlength: 32
                    },
                    extend: {
                        maxlength: 255
                    }
                },
                messages: {
                    name: {
                        required: '必填'
                    },
                    busiEmail: {
                        email: 'email格式不正确'
                    }
                }
            });
        });
	</script>
</head>
<body>
<div class="breadcrumb">
	<input id="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);" name="btnCancel"/>
</div>
	<table id="infoTable" class="table table-striped table-bordered table-condensed">
		<tr>
			<td colspan="4" style="text-align: center;"><b>代付结果</b></td>
		</tr>
	<tbody>
	<tr>
		<td width="25%"><b>代付状态:</b></td>	<td width="25%">${fns:getDictLabel(proxyDetail.payStatus,'proxypay_detail_status' ,'' )}</td>
		<td width="25%"><b>上游响应信息:</b></td>	<td width="25%"> ${proxyDetail.returnMessage2}</td>
	</tr>
		<tr>
	        <td width="25%"><b>批次订单号:</b></td>	<td width="25%"> ${proxyDetail.platBatchId}</td>
			<td width="25%"><b>明细订单号:</b></td>	<td width="25%"> ${proxyDetail.id}</td>
   		</tr>
		<tr>
			<td width="25%"><b>上游流水号:</b></td>	<td width="25%"> ${proxyDetail.channelSeq}</td>
			<td width="25%"><b>商户流水号:</b></td>	<td width="25%"> ${proxyDetail.mchtSeq}</td>
		</tr>
	<tr>
		<td width="25%"><b>代付金额:</b></td>	<td width="25%"> <fmt:formatNumber type="number" value="${proxyDetail.amount*0.01}" pattern="0.0000" maxFractionDigits="4"/>元</td>
		<td width="25%"><b>手续费:</b></td>	<td width="25%"> <fmt:formatNumber type="number" value="${proxyDetail.mchtFee*0.01}" pattern="0.0000" maxFractionDigits="4"/>元</td>
	</tr>
		<tr>
			<td width="25%"><b>创建时间:</b></td>	<td width="25%"> <fmt:formatDate value="${proxyDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td width="25%"><b>更新时间:</b></td>	<td width="25%"> <fmt:formatDate value="${proxyDetail.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		</tr>
	</tbody>
	</table>
	<table class="table table-striped table-bordered table-condensed">
		<tr>
			<td colspan="4" style="text-align: center;"><b>代付详情</b></td>
		</tr>
		<tbody>

		<tr>
			<td width="25%"><b>商户编码:</b></td>	<td width="25%"> ${proxyDetail.mchtId}</td>
			<td width="25%"><b>商户名称:</b></td>	<td width="25%"> ${proxyDetail.extend2}</td>
		</tr>
		<tr>
			<td width="25%"><b>通道编码:</b></td>	<td width="25%"> ${proxyDetail.chanId}</td>
			<td width="25%"><b>通道名称:</b></td>	<td width="25%"> ${proxyDetail.extend3}</td>
		</tr>

		<tr>
			<td width="25%"><b>代付人姓名:</b></td>	<td width="25%"> ${proxyDetail.bankCardName}</td>
			<td width="25%"><b>预留手机号:</b></td>	<td width="25%"> ${proxyDetail.mobile}</td>
		</tr>
		<tr>
			<td width="25%"><b>银行卡号:</b></td>	<td width="25%"> ${proxyDetail.bankCardNo}</td>
			<td width="25%"><b>银行名称:</b></td>	<td width="25%"> ${proxyDetail.bankName}</td>
		</tr>
		<tr>
			<td width="25%"><b>省:</b></td>	<td width="25%"> ${proxyDetail.province}</td>
			<td width="25%"><b>市:</b></td>	<td width="25%"> ${proxyDetail.city}</td>
		</tr>
		<tr>
			<td><b>备注: </b></td>
			<td colspan="3">${proxyDetail.remark}</td>
		</tr>
		</tbody>
	</table>

<form id="channelForm" action="" method="post">
	<input type="hidden" id="op" name="op" value="${op }"/>
	<input type="hidden" name="detailId" value="${proxyDetail.id }"/>

	<div class="breadcrumb">
		<label>
			<th>修改代付状态</th>
		</label>
	</div>
	<table class="table">
		<tr>

			<td>
				<div class="control-group">
					<label class="control-label">新代付明细状态</label>
					<div class="controls">
						<select name="status" class="input-xlarge" id="status">
							<option value="21">代付成功</option>
							<option value="22">代付失败</option>
						</select>
					</div>
				</div>
			</td>

			<td>
				<div class="control-group">
					<label class="control-label" for="notes">修改说明</label>
					<div class="controls">
                        <textarea name="notes" placeholder="" id="notes"
								  style="width:500px;" rows="3"></textarea>
					</div>
				</div>
			</td>

		</tr>
	</table>

	<div class="breadcrumb">
		<input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
		<input name="btnSubmit" class="btn btn-primary" type="submit" value="保存" style="margin-left: 5px;">
	</div>


</form>
</body>
</html>