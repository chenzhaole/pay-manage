<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>电子账户管理</title>
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
                    'caElectronicAccount.mchtCode':{
                        required:true
                    },
                    'caElectronicAccount.chanCode':{
                        required:true
                    },
                    'caBankElectronicAccount.mchtCode':{
                        required:true
                    },
                    'platFeerate.feeRate':{
                        required:function(){
                            var value =$('#feeType').val();
                            var flag =false;
                            if(value =='1'){
                                flag= true;
                            }
                            if(value =='3'){
                                flag= true;
                            }
                            return flag;
                        }
                    },
                    'platFeerate.feeAmount':{
                        required:function() {
                            var value =$('#feeType').val();
                            var flag =false;
                            if(value =='2'){
                                flag= true;
                            }
                            if(value =='3'){
                                flag= true;
                            }
                            return flag;
                        }
                    }
                }
            });
        });
        var map = new Map();
        function selectBank(select){
            var value =$(select).val();
            if(value ==''){
                return;
            }
            var text =$(select).find("option:selected").text();
            if(map.has(value)){
                console.log(' value is' + map.get());
                alert("支持银行重复选择!");
                $(select).val("");
                return;
            }else{
                map.set(value,value);
                $(select).next().val(text);
            }
        }
        function selectMcht(select) {
            $('#input_1').val($(select).find("option:selected").text());
            $('#electronicAccountName').val($('#input_2').val() +"-"+$('#input_1').val());
            $('#electronicAccountNameCopy').val($('#input_2').val() +"-"+$('#input_1').val());
        }

        function selectChan(select) {
            $('#input_2').val($(select).find("option:selected").text());
            $('#electronicAccountName').val($('#input_2').val() +"-"+$('#input_1').val());
            $('#electronicAccountNameCopy').val($('#input_2').val() +"-"+$('#input_1').val());
        }
        var i=0;
        //新增 银行
        function addBank() {
            i++;
            $("#bankInfo").append('<tr>' +
                '<td>' +
                '<div class="control-group">' +
                ' <label class="control-label">银行名称</label>' +
                '<div class="controls">' +
                '<select name="caBankElectronicAccountBankList['+i+'].bankCode" class="selectpicker bla bla bli" onchange="selectBank(this);">' +
                '<option value="">--请选择--</option>'+
                <c:forEach items="${platBanks}" var="platBank">
                '<option value="${platBank.bankCode}">${platBank.bankName}</option>' +
                </c:forEach>
                '<input name="caBankElectronicAccountBankList['+i+'].bankName" type="hidden" value="">' +
                '</div>' +
                '</div>' +
                '</td>'  +
                '<td>'+
                '<div class="control-group">' +
                '<label class="control-label">银行别名:</label></span>' +
                '<div class="controls">'+
                '<input name="caBankElectronicAccountBankList['+i+'].bankAlias" placeholder="" class="input-xlarge" type="text" maxlength="64">' +
                '</div>'+
                '</div>'+
                '</td>' +
                '<td>'  +
                '<div class="control-group">' +
                '<label class="control-label">状态:</label>' +
                '<div class="controls">'+
                '<select name="caBankElectronicAccountBankList['+i+'].status" class="input-medium" >'+
                '<option value="1">启用</option>' +
                '<option value="2">停用</option>'+
                '</select>'+
                '</div>'+
                '</div>'+
                '</td>' +
                '<td>'+
                '<img src="${ctxStatic}/images/timg.jpeg" onclick="delBank(this);" width="20" height="20">'+
                '</td>'+
                '</tr>');
        }

        function delBank(img){
           $(img).parent().parent().remove();
        }

            
    </script>

</head>
<body>

<div class="breadcrumb">
    <label><a href="#">电子账户管理</a> > <a href="#"><b>电子账户新增</b></a></label>
</div>

<form id="electronicAccountForm" action="${ctx}/electronic/doAddAccount" method="post">
    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>平台电子账户</label>
    </div>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">电子账户名称:</label>
                    <div class="controls">
                    <input name="caElectronicAccount.electronicAccountName" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64" disabled="disabled">
                    <input name="caElectronicAccount.electronicAccountName" placeholder="" class="input-xlarge" type="hidden" id="electronicAccountNameCopy"
                           maxlength="64">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">商户名称:</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        <select name="caElectronicAccount.mchtCode" id="mchtCode"  class="selectpicker" data-live-search="true" onchange="selectMcht(this);">
                            <option value="">--请选择--</option>
                            <c:forEach var="mcht" items="${mchtList}">
                                <option value="${mcht.mchtCode}" >${mcht.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <input id="input_1" value="" type="hidden">
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">通道名称:</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        <select name="caElectronicAccount.chanCode" class="selectpicker" id="chanCode" onchange="selectChan(this)">
                            <option value="">--请选择--</option>
                            <c:forEach items="${chanInfos}" var="chanInfo">
                                <option data-chanCode="${chanInfo.chanCode }"
                                        value="${chanInfo.chanCode}">${chanInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <input id="input_2" value="" type="hidden">
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">状态:</label>
                    <div class="controls">
                        <select name="caElectronicAccount.status" class="input-medium" id="status">
                            <option value="1">启用</option>
                            <option value="2">停用</option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">电子账户类型:</label>
                    <div class="controls">
                        <select name="caElectronicAccount.accountType" class="input-medium" id="routeType">
                            <option value="0">支付电子账户</option>
                            <option value="1">充值电子账户</option>
                        </select>
                    </div>
                </div>
            </td>

            <td colspan="3">
                <div class="control-group">
                    <label class="control-label">绑定手机号:</label>
                    <div class="controls">
                        <textarea name="caElectronicAccount.bindPhones" placeholder="" style="width:500px;" id="bindPhones" rows="3"></textarea>
                    </div>
                </div>
            </td>
        </tr>
    </table>

    <div class="breadcrumb">
        <label>汇款费率信息</label>
    </div>

    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">收费类型</label>
                    <div class="controls">
                        <select name="platFeerate.feeType" class="input-xlarge" id="feeType">
                            <option value="">--请选择--</option>
                            <option value="1">按笔收费</option>
                            <option value="2">按比例收费</option>
                            <option value="3">混合</option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="feeRate">收费比率(‰)</label>
                    <div class="controls">
                        <input name="platFeerate.feeRate"  placeholder=""
                               class="input-xlarge" type="text" id="feeRate">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="feeAmount">收费金额(/笔(分))</label>
                    <div class="controls">
                        <input name="platFeerate.feeAmount"  placeholder="" class="input-xlarge"
                               type="text" id="feeAmount">
                    </div>
                </div>
            </td>
        </tr>
    </table>


    <div class="breadcrumb">
        <label>通道账户信息</label>
    </div>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">收款银行:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.receivingBank" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">清算行号:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.liquidationBankNo" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">开户网点:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.openingAccountBank" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                    </div>
                </div>

            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">开户地:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.openingAccountLocation" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                    </div>
                </div>

            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">联行号:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.lineNumber" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">银行账号:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.accountNo" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                    </div>
                </div>

            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">上游商户号:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.mchtCode" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                    </div>
                </div>

            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">状态:</label>
                    <div class="controls">
                        <select name="caBankElectronicAccount.status" class="input-medium" >
                            <option value="1">启用</option>
                            <option value="2">停用</option>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
    </table>

    <div class="breadcrumb">
        <label>支持银行信息</label>
    </div>
    <table class="table" id ="bankInfo">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">银行名称</label>
                    <div class="controls">
                        <select name="caBankElectronicAccountBankList[0].bankCode" class="selectpicker bla bla bli" data-live-search="true" onchange="selectBank(this);">
                            <option value="">--请选择--</option>
                            <c:forEach items="${platBanks}" var="platBank">
                                <option value="${platBank.bankCode}">${platBank.bankName}</option>
                            </c:forEach>
                        </select>
                        <input name="caBankElectronicAccountBankList[0].bankName" type="hidden" value="">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">银行别名:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccountBankList[0].bankAlias" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">状态:</label>
                    <div class="controls">
                        <select name="caBankElectronicAccountBankList[0].status" class="input-medium" >
                            <option value="1">启用</option>
                            <option value="2">停用</option>
                        </select>
                    </div>
                </div>
            </td>
            <td align="left">
                <img src="${ctxStatic}/images/timg-add.jpeg" onclick="addBank();" width="20" height="20">
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