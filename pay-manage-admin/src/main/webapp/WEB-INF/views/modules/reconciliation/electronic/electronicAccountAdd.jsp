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
    <script src="${ctxStatic}/js/module/platform/platProductEdit.js" type="text/javascript"></script>

    <script type="text/javascript">

        $(function(){

            $("#platProductForm").validate({
                debug: true, //调试模式取消submit的默认提交功能
                //errorClass: "label.error", //默认为错误的样式类为：error
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function(form){   //表单提交句柄,为一回调函数，带一个参数：form
                    //重新分配name
                    var payTypeNo = $("#payTypeTable tr").length;
                    for (var i=0; i<payTypeNo; i++){
                        if ($("#payTypeTable tr:eq("+i+") [name='payType']").val() == 0){
                            alert("请选择通道商户支付方式");
                            return ;
                        }
                        $("#payTypeTable tr:eq("+i+") [name='sort']").attr("name", "sort" + i);
                        $("#payTypeTable tr:eq("+i+") [name='payType']").attr("name", "payType" + i);
                        $("#payTypeTable tr:eq("+i+") [name='isValid']").attr("name", "isValid" + i);
                    }

                    //重新分配子产品name
                    var productNo = $("#productTable tr").length;
                    for (var i=0; i<productNo; i++){
                        if ($("#productTable tr:eq("+i+") [name='subProductId']").val() == 0){
                            alert("请选择子产品");
                            return ;
                        }
                        $("#productTable tr:eq("+i+") [name='sort']").attr("name", "sort" + i);
                        $("#productTable tr:eq("+i+") [name='subProductId']").attr("name", "subProductId" + i);
                    }


                    form.submit();   //提交表单
                },

                rules:{
                    productName:{
                        required:true,
                        maxlength:64
                    },
                    code:{
                        maxlength:64
                    },
                    extend1:{
                        maxlength:255
                    }
                }
            });
        });
        var map = {};
        function selectBank(){
            if(map.hasOwnProperty($(this).val())){
                console.log('key is ' + prop +' and value is' + map[prop]);
                alert("支持银行重复选择!");
                $(this).val("");
                return;
            }else{
                map[$(this).val()] = $(this).val();
                $(this).next().val($(this).text());
            }
        }
        //新增 银行
        function addBank() {
            $("#bankInfo").append('<tr>' +
                '<td>' +
                '<div class="control-group">' +
                ' <label class="control-label">银行名称</label>' +
                '<div class="controls">' +
                '<select name="caBankElectronicAccountBankList.bankCode" class="input-xxlarge payTypeSelect">' +
                '<option value="">--请选择--</option>'+
                <c:forEach items="${platBanks}" var="platBank">
                '<option value="${platBank.bankCode}">${platBank.bankName}</option>' +
                </c:forEach>
                '<input name="caBankElectronicAccountBankList.bankName" type="hidden" value="">' +
                '</div>' +
                '</div>' +
                '</td>'  +
                '<td>'+
                '<div class="control-group">' +
                '<label class="control-label">银行别名:</label></span>' +
                '<input name="caBankElectronicAccountBankList.bankAlias" placeholder="" class="input-xlarge" type="text" maxlength="64">' +
                '</div>'+
                '</td>' +
                '<td>'  +
                '<div class="control-group">' +
                '<label class="control-label">状态:</label>' +
                '<div class="controls">'+
                '<select name="caBankElectronicAccount.status" class="input-medium" >'+
                '<option value="1">启用</option>' +
                '<option value="2">停用</option>'+
                '</select>'+
                '</div>'+
                '</div>'+
                '<img src="">'+
                '</td>'+
                '</tr>');
        }
            
    </script>

</head>
<body>

<div class="breadcrumb">
    <label><a href="#">电子账户管理</a> > <a href="#"><b>电子账户新增</b></a></label>
</div>

<form id="platProductForm" action="${ctx}/platform/addPlatProduct" method="post">
    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>平台电子账户</label>
    </div>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">电子账户名称:</label></span>
                    <input name="caElectronicAccount.electronicAccountName" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64" disabled="disabled">
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">商户名称:</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        <select name="caElectronicAccount.mchtCode" id="mchtCode"  class="selectpicker" data-live-search="true">
                            <option value="">--请选择--</option>
                            <c:forEach var="mcht" items="${mchtList}">
                                <option value="${mcht.mchtCode}" <c:if test="${paramMap.mchtCode eq mcht.mchtCode}">selected</c:if> >${mcht.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">通道名称:</label>
                    <div class="controls">
                        <select name="caElectronicAccount.chanCode" class="selectpicker" id="chanCode">
                            <option value="">--请选择--</option>
                            <c:forEach items="${chanInfos}" var="chanInfo">
                                <option data-chanCode="${chanInfo.chanCode }"
                                        <c:if test="${paramMap.chanCode eq chanInfo.chanCode}">selected</c:if>
                                        value="${chanInfo.chanCode}">${chanInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
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
        <label>通道账户信息</label>
    </div>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">收款银行:</label></span>
                    <input name="caBankElectronicAccount.receivingBank" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">清算行号:</label></span>
                    <input name="caBankElectronicAccount.liquidationBankNo" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">开户网点:</label></span>
                    <input name="caBankElectronicAccount.openingAccountBank" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                </div>

            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">开户地:</label></span>
                    <input name="caBankElectronicAccount.openingAccountLocation" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                </div>

            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">联行号:</label></span>
                    <input name="caBankElectronicAccount.lineNumber" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">银行账号:</label></span>
                    <input name="caBankElectronicAccount.accountNo" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
                </div>

            </td>
        </tr>
        <tr>
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
                        <select name="caBankElectronicAccountBankList.bankCode" class="selectpicker bla bla bli" data-live-search="true" onchange="">
                            <option value="">--请选择--</option>
                            <c:forEach items="${platBanks}" var="platBank">
                                <option value="${platBank.bankCode}">${platBank.bankName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <input name="caBankElectronicAccountBankList.bankName" type="hidden" value="">
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">银行别名:</label></span>
                    <input name="caBankElectronicAccountBankList.bankAlias" placeholder="" class="input-xlarge" type="text"
                           maxlength="64">
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
                <img src="">
            </td>
        </tr>
    </table>

</form>
</body>

</html>