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

            //新增 一行支付方式
            function addPayType() {
            	//定义一个map用来保存通道支付方式，同时来验证是否有重复
            	var map = {};
            	$(".payTypeSelect").each(function(){
            		if($(this).val() != ''){
	            		//如果map
	           		    if(map.hasOwnProperty($(this).val())){
	           		        console.log('key is ' + prop +' and value is' + map[prop]);
	           		        alert("通道支付方式有重复!");
	           		        return;
	           		    }else{
	           		    	map[$(this).val()] = $(this).val();
	           		    }
            		}
        		});
            	var rateVal = 0;
            	 $('input[type="hidden"][name="isRate"]').each(function(){
                   	 if($(this).attr("checked") != true){
                   		rateVal += parseInt($(this).next().val());
                   	 }
                   }); 
            	if(rateVal > 100){
            		alert("占比总和不能超过100%");
            		return;
            	}
                var payTypeNum = $("#payTypeTable tr").length;
                if (payTypeNum > 9) {
                    alert("最多添加10条！");
                    return;
                }
                $("#payTypeTable").append('<tr>' +
                    '<input type="hidden" value="' + (payTypeNum + 1) + '" name="sort" />' +
                    '<td>' +
                    '<div class="control-group">' +
                    ' <label class="control-label"></label>' +
                    '<div class="controls" style="padding-top:10px;">' +
                    '<span>顺序 ' + (payTypeNum + 1) + '</span>' +
                    '</div>' +
                    '</div>' +
                    '</td>' +
                    '<td>' +
                    '<div class="control-group">' +
                    ' <label class="control-label">通道商户支付方式</label>' +
                    '<div class="controls">' +
                    '<select name="payType" class="input-xxlarge payTypeSelect" id="payType' + payTypeNum + '">' +
                    '<option value="">--请选择--</option>'+
                    <c:forEach items="${chanInfoList}" var="chanInfo">
                     '<option value="${chanInfo.id}">${chanInfo.name}</option>' +
                    </c:forEach>
                    '</select>&nbsp;&nbsp;&nbsp;' +
                    '<input name="isRate" type="hidden"/>' +
                    '<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="deletePayType(this);" >删除</a>&nbsp;&nbsp;&nbsp;' +
                    '<a style="cursor: pointer;font-size:15px;text-decoration: none; " onclick="upTr(this);" >上移</a>&nbsp;&nbsp;&nbsp;' +
                    '<a style="cursor: pointer;font-size:15px;text-decoration: none; " onclick="downTr(this);" >下移</a>' +
                    '</div>' +
                    '</div>' +
                    '</td>' +
                    '</tr>');
            }

        //新增 一行子产品
        function addProduct() {
            //定义一个map用来保存子产品，同时来验证是否有重复
            var map = {};
            $(".productSelect").each(function(){
                if($(this).val() != ''){
                    //如果map
                    if(map.hasOwnProperty($(this).val())){
                        console.log('key is ' + prop +' and value is' + map[prop]);
                        alert("子产品有重复!");
                        return;
                    }else{
                        map[$(this).val()] = $(this).val();
                    }
                }
            });
            var rateVal = 0;
            $('input[type="hidden"][name="isRate"]').each(function(){
                if($(this).attr("checked") != true){
                    rateVal += parseInt($(this).next().val());
                }
            });
            if(rateVal > 100){
                alert("占比总和不能超过100%");
                return;
            }
            var productNum = $("#productTable tr").length;
            if (productNum > 9) {
                alert("最多添加10条！");
                return;
            }
            $("#productTable").append('<tr>' +
                '<input type="hidden" value="' + (productNum + 1) + '" name="sort" />' +
                '<td>' +
                '<div class="control-group">' +
                ' <label class="control-label"></label>' +
                '<div class="controls" style="padding-top:10px;">' +
                '<span>顺序 ' + (productNum + 1) + '</span>' +
                '</div>' +
                '</div>' +
                '</td>' +
                '<td>' +
                '<div class="control-group">' +
                ' <label class="control-label">子产品</label>' +
                '<div class="controls">' +
                '<select name="subProductId" class="input-xxlarge productSelect" id="subProductId' + productNum + '">' +
                '<option value="">--请选择--</option>'+
                <c:forEach items="${subProductLists}" var="subProductItem">
                '<option value="${subProductItem.id}">${subProductItem.name}</option>' +
                </c:forEach>
                '</select>&nbsp;&nbsp;&nbsp;' +
                '<input name="isRate" type="hidden"/>' +
                '<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="deleteProduct(this);" >删除</a>&nbsp;&nbsp;&nbsp;' +
                '<a style="cursor: pointer;font-size:15px;text-decoration: none; " onclick="upTrProduct(this);" >上移</a>&nbsp;&nbsp;&nbsp;' +
                '<a style="cursor: pointer;font-size:15px;text-decoration: none; " onclick="downTrProduct(this);" >下移</a>' +
                '</div>' +
                '</div>' +
                '</td>' +
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
                    <input name="caBankElectronicAccount.receivingBank" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64">
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">清算行号:</label></span>
                    <input name="caBankElectronicAccount.liquidationBankNo" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64">
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">开户网点:</label></span>
                    <input name="caBankElectronicAccount.openingAccountBank" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64">
                </div>

            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">开户地:</label></span>
                    <input name="caBankElectronicAccount.openingAccountLocation" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64">
                </div>

            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">联行号:</label></span>
                    <input name="caBankElectronicAccount.lineNumber" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64">
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">银行账号:</label></span>
                    <input name="caBankElectronicAccount.accountNo" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64">
                </div>

            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">状态:</label>
                    <div class="controls">
                        <select name="caBankElectronicAccount.status" class="input-medium" id="status">
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
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">银行名称<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="platBankCode" class="selectpicker bla bla bli" data-live-search="true"
                                <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                            <option value="">--请选择--</option>
                            <c:forEach items="${platBanks}" var="platBank">
                                <option data-platBankCode="${platBank.bankCode }"
                                        <c:if test="${chanBank.platBankCode == platBank.bankCode}">selected</c:if>
                                        value="${platBank.bankCode}">${platBank.bankName}-${platBank.bankCode}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">清算行号:</label></span>
                    <input name="caBankElectronicAccount.liquidationBankNo" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64">
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">开户网点:</label></span>
                    <input name="caBankElectronicAccount.openingAccountBank" placeholder="" class="input-xlarge" type="text" id="electronicAccountName"
                           maxlength="64">
                </div>

            </td>
        </tr>
    </table>

</form>
</body>

</html>