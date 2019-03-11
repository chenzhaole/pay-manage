<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>重复订单投诉新增</title>
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
                    'sourceDataId':{
                        required:true
                    },
                    'sourceChanRepeatDataId':{
                        required:true
                    },
                    'complainType':{
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
        <li class="active"><a href="${ctx}/caAccountAudit/toAddRepeatAudits">重复订单投诉添加</a></li>
        <li><a href="${ctx}/caAccountAudit/queryRepeatAudits">重复订单投诉列表</a></li>
    </ul>
</div>

<form id="electronicAccountForm" action="${ctx}/caAccountAudit/doAddRepeatAudits" method="post">
    <!-- ********************************************************************** -->
    <table class="table">
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">投诉订单类型</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        <select name="complainType" id="mchtCode"  class="selectpicker" data-live-search="true">
                            <option value="">请选择</option>
                            <option value="B">代付订单</option>
                            <option value="P">支付订单</option>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">原平台交易订单号</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                    <input name="sourceDataId" placeholder="" class="input-xlarge" type="text" id="sourceDataId"
                           maxlength="64">
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">原上游交易订单号</label>
                    <div class="controls">
                        <input name="sourceChanDataId" placeholder="" class="input-xlarge" type="text" id="sourceChanDataId"
                               maxlength="64">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">上游重复交易订单号</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        <input name="sourceChanRepeatDataId" placeholder="" class="input-xlarge" type="text" id="sourceChanRepeatDataId"
                               maxlength="64">
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">备注</label>
                    <div class="controls">
                        <textarea name="customerMsg"></textarea>
                    </div>
                </div>
            </td>
        </tr>
    </table>
    <table id="order" style="display: none">
        <thead>
        <tr >
            <th>商户名称</th>
            <th>平台订单号</th>
            <th>商户订单号</th>
            <th>上游订单号</th>
            <th>交易金额</th>
            <th>订单状态</th>
            <th>创建时间</th>
            <th>支付时间</th>
        </tr>
        </thead>
        <tbody>
            <tr>

            </tr>
        </tbody>

    </table>
    <div class="breadcrumb">
        <input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
        <input name="btnSubmit" class="btn btn-primary" type="submit" value="保存"  style="margin-left: 5px;">
    </div>
</form>
</body>

</html>