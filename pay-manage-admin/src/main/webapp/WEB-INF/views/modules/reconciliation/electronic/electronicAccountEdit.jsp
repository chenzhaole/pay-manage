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
        var map = new Map();

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
            <c:forEach items="${electronicAccountVo.caBankElectronicAccountBankList}" var="item">
                map.set('${item.bankCode}','${item.bankCode}');

            </c:forEach>
        });
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
        var i='${electronicAccountVo.caBankElectronicAccountBankList==null?0:electronicAccountVo.caBankElectronicAccountBankList.size()}';
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
    <label><a href="#">电子账户管理</a> > <a href="#"><b>电子账户修改</b></a></label>
</div>

<form id="electronicAccountForm" action="${ctx}/electronic/doEditAccount" method="post">
    <input type="hidden" name="caElectronicAccount.id" value="${electronicAccountVo.caElectronicAccount.id}">
    <input type="hidden" name="caBankElectronicAccount.id" value="${electronicAccountVo.caBankElectronicAccount.id}">
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
                           maxlength="64" disabled="disabled" value="${electronicAccountVo.caElectronicAccount.electronicAccountName}">
                    <input name="caElectronicAccount.electronicAccountName" placeholder="" class="input-xlarge" type="hidden" id="electronicAccountNameCopy"
                           maxlength="64" value="${electronicAccountVo.caElectronicAccount.electronicAccountName}">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">商户名称:</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        <select name="caElectronicAccount.mchtCode" id="mchtCode"  class="selectpicker" data-live-search="true" onchange="selectMcht(this);" disabled="disabled">
                            <option value="">--请选择--</option>
                            <c:forEach var="mcht" items="${mchtList}">
                                <option value="${mcht.mchtCode}" <c:if test="${electronicAccountVo.caElectronicAccount.mchtCode eq mcht.mchtCode}">selected</c:if> >${mcht.name}</option>
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
                        <select name="caElectronicAccount.chanCode" class="selectpicker" id="chanCode" onchange="selectChan(this)" disabled="disabled">
                            <option value="">--请选择--</option>
                            <c:forEach items="${chanInfos}" var="chanInfo">
                                <option data-chanCode="${chanInfo.chanCode }"
                                        <c:if test="${electronicAccountVo.caElectronicAccount.chanCode eq chanInfo.chanCode}">selected</c:if>
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
                            <option <c:if test="${electronicAccountVo.caElectronicAccount.status eq '1'}"> selected</c:if> value="1">启用</option>
                            <option <c:if test="${electronicAccountVo.caElectronicAccount.status eq '2'}"> selected</c:if>value="2">停用</option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">电子账户类型:</label>
                    <div class="controls">
                        <select name="caElectronicAccount.accountType" class="input-medium" id="routeType">
                            <option <c:if test="${electronicAccountVo.caElectronicAccount.accountType eq '0'}"> selected</c:if> value="0">支付电子账户</option>
                            <option <c:if test="${electronicAccountVo.caElectronicAccount.accountType eq '1'}"> selected</c:if> value="1">充值电子账户</option>
                        </select>
                    </div>
                </div>
            </td>

            <td colspan="3">
                <div class="control-group">
                    <label class="control-label">绑定手机号:</label>
                    <div class="controls">
                        <textarea name="caElectronicAccount.bindPhones" placeholder="" style="width:500px;" id="bindPhones" rows="3" value="${electronicAccountVo.caElectronicAccount.bindPhones}"></textarea>
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
                            <option <c:if test="${electronicAccountVo.platFeerate.feeType eq '1'}">selected</c:if> value="1">按笔收费
                            </option>
                            <option <c:if test="${electronicAccountVo.platFeerate.feeType eq '2'}">selected</c:if> value="2">按比例收费
                            </option>
                            <option <c:if test="${electronicAccountVo.platFeerate.feeType eq '3'}">selected</c:if> value="3">混合
                            </option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="feeRate">收费比率(‰)</label>
                    <div class="controls">
                        <input name="platFeerate.feeRate"  placeholder=""
                               class="input-xlarge" type="text" id="feeRate" value="${electronicAccountVo.platFeerate.feeRate}">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="feeAmount">收费金额(/笔(分))</label>
                    <div class="controls">
                        <input name="platFeerate.feeAmount"  placeholder="" class="input-xlarge"
                               type="text" id="feeAmount" value="${electronicAccountVo.platFeerate.feeAmount}">
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
                           maxlength="64" value="${electronicAccountVo.caBankElectronicAccount.receivingBank}">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">清算行号:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.liquidationBankNo" placeholder="" class="input-xlarge" type="text"
                           maxlength="64" value="${electronicAccountVo.caBankElectronicAccount.liquidationBankNo}">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">开户网点:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.openingAccountBank" placeholder="" class="input-xlarge" type="text"
                           maxlength="64" value="${electronicAccountVo.caBankElectronicAccount.openingAccountBank}">
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
                           maxlength="64" value="${electronicAccountVo.caBankElectronicAccount.openingAccountLocation}">
                    </div>
                </div>

            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">联行号:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.lineNumber" placeholder="" class="input-xlarge" type="text"
                           maxlength="64" value="${electronicAccountVo.caBankElectronicAccount.lineNumber}">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">银行账号:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccount.accountNo" placeholder="" class="input-xlarge" type="text"
                           maxlength="64" value="${electronicAccountVo.caBankElectronicAccount.accountNo}">
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
                           maxlength="64" value="${electronicAccountVo.caBankElectronicAccount.mchtCode}">
                    </div>
                </div>

            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">状态:</label>
                    <div class="controls">
                        <select name="caBankElectronicAccount.status" class="input-medium" >
                            <option <c:if test="${electronicAccountVo.caBankElectronicAccount.status eq '1'}"> selected</c:if> value="1">启用</option>
                            <option <c:if test="${electronicAccountVo.caBankElectronicAccount.status eq '2'}"> selected</c:if>value="2">停用</option>
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
        <c:forEach items="${electronicAccountVo.caBankElectronicAccountBankList}" var="item" varStatus="status">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">银行名称</label>
                    <div class="controls">
                        <select name="caBankElectronicAccountBankList[${status.index}].bankCode" class="selectpicker bla bla bli" data-live-search="true" onchange="selectBank(this);">
                            <option value="">--请选择--</option>
                            <c:forEach items="${platBanks}" var="platBank">
                                <option <c:if test="${item.bankCode eq platBank.bankCode}">selected</c:if> value="${platBank.bankCode}">${platBank.bankName}</option>
                            </c:forEach>
                        </select>
                        <input name="caBankElectronicAccountBankList[${status.index}].bankName" type="hidden" value="${item.bankName}">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">银行别名:</label>
                    <div class="controls">
                    <input name="caBankElectronicAccountBankList[${status.index}].bankAlias" placeholder="" class="input-xlarge" type="text"
                           maxlength="64" value="${item.bankAlias}">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">状态:</label>
                    <div class="controls">
                        <select name="caBankElectronicAccountBankList[${status.index}].status" class="input-medium" >
                            <option <c:if test="${item.status eq '1'}"> selected</c:if> value="1">启用</option>
                            <option <c:if test="${item.status eq '2'}"> selected</c:if>value="2">停用</option>
                        </select>
                    </div>
                </div>
            </td>
            <td align="left">
                <img src="${ctxStatic}/images/timg-add.jpeg" onclick="addBank();" width="20" height="20">
            </td>
        </tr>
        </c:forEach>
    </table>
    <div class="breadcrumb">
        <input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
        <input name="btnSubmit" class="btn btn-primary" type="submit" value="保存"  style="margin-left: 5px;">
    </div>
</form>
</body>

</html>