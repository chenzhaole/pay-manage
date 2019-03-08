<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>重复订单投诉审批</title>
    <meta name="decorator" content="default"/>
    <style>
        .table th, .table td {
            border-top: none;
        }

        .control-group {
            border-bottom: none;
        }
    </style>

    <script type="text/javascript">

        $(function(){

            $("#electronicAccountForm").validate({
                debug: true, //调试模式取消submit的默认提交功能
                //errorClass: "label.error", //默认为错误的样式类为：error
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function(form){   //表单提交句柄,为一回调函数，带一个参数：form
                    form.submit();   //提交表单
                },

                rules:{
                    'approveType':{
                        required:true
                    }
                }
            });
        });

            
    </script>

</head>
<body>

<div class="breadcrumb">
    <ul class="nav nav-tabs">
        <li class="active"><a href="${ctx}/caAccountAudit/toAddRepeatAudits">重复订单投诉审批</a></li>
        <li><a href="${ctx}/caAccountAudit/queryRepeatAudits">重复订单投诉列表</a></li>
    </ul>
</div>

<form id="electronicAccountForm" action="${ctx}/caAccountAudit/doApproveRepeatAudits" method="post">
    <!-- ********************************************************************** -->
    <table id="order" class="table table-striped table-bordered table-condensed table-hover">
        <thead>
        <tr >
            <th>商户名称</th>
            <th>平台订单号</th>
            <th>商户订单号</th>
            <th>上游订单号</th>
            <th>通道名称</th>
            <th>交易金额</th>
            <th>订单状态</th>
            <th>创建时间</th>
            <th>支付时间</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>
                ${mertName}
            </td>
            <td>
                ${mchtGatewayOrder.platOrderId}
            </td>
            <td>
                ${mchtGatewayOrder.mchtOrderId}
            </td>
            <td>
                ${mchtGatewayOrder.chanOrderId}
            </td>
            <td>
                ${chanName}
            </td>
            <td>
                ${mchtGatewayOrder.amount * 0.01}
            </td>
            <td>
                <c:forEach var="dict" items="${fns:getDictList('pay_status')}">
                    <c:if test="${mchtGatewayOrder.status eq dict.value}">${dict.label}</c:if>
                </c:forEach>
            </td>
            <td>
                <fmt:formatDate value="${mchtGatewayOrder.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>

            </td>
            <td>
                <fmt:formatDate value="${mchtGatewayOrder.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
        </tr>
        </tbody>
    </table>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">原平台交易订单号</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        ${caAccountAudit.sourceDataId}
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">原上游交易订单号</label>
                    <div class="controls">
                        ${caAccountAudit.sourceChanDataId}
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">上游重复交易订单号</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        ${caAccountAudit.sourceChanRepeatDataId}
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">审批状态</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        <select name="auditStatus" id="approveType"  class="selectpicker" data-live-search="true">
                            <option value="">请选择</option>
                            <option value="5">拒绝</option>
                            <option value="4">同意</option>
                        </select>
                    </div>
                </div>
                <input value="${caAccountAudit.id}" name="id" type="hidden">
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">客服备注</label>
                    <div class="controls">
                        ${caAccountAudit.customerMsg}
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">审批备注</label>
                    <div class="controls">
                        <textarea name="operateMsg"></textarea>
                    </div>
                </div>
            </td>
        </tr>
    </table>
    <div class="breadcrumb">
        <input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
        <input name="btnSubmit" class="btn btn-primary" type="submit" value="保存"  style="margin-left: 5px;">
    </div>
</form>
</body>

</html>