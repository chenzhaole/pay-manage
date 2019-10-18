<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>支付产品管理</title>
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
                if (payTypeNum > 49) {
                    alert("最多添加50条！");
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
            if (productNum > 49) {
                alert("最多添加50条！");
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
    <label><a href="#">支付产品管理</a> > <a href="#"><b>支付产品新增/编辑</b></a></label>
</div>

<form id="platProductForm" action="${ctx}/platform/addPlatProduct" method="post">
    <input type="hidden" id="op" name="op" value="${op }"/>
    <input type="hidden" name="id" value="${productInfo.id }"/>
    <input type="hidden" name="paymentType" value="${paymentType}"/>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>支付产品基本信息</label>
    </div>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">产品名称</label><span style="color: red;"><span style="color: red;">*</span></span>
                    <div class="controls">
                        <input name="productName" placeholder="" class="input-xlarge" type="text" id="productName"
                               maxlength="64" value="${productInfo.name}">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">产品编号</label>
                    <div class="controls">
                        <input name="code" placeholder="" class="input-xlarge" type="text" id="code"
                               maxlength="64" value="${productInfo.code}" readonly/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">状态</label>
                    <div class="controls">
                        <select name="status" class="input-medium" id="status">
                            <option <c:if test="${productInfo.status == '1' }"> selected</c:if> value="1">启用</option>
                            <option <c:if test="${productInfo.status == '2' }"> selected</c:if> value="2">停用</option>
                            <%--<option <c:if test="${productInfo.status == '3' }"> selected</c:if> value="3">待审核</option>--%>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">执行方式</label>
                    <div class="controls">
                        <select name="routeType" class="input-medium" id="routeType">
                            <c:choose>
                                <c:when test="${fn:contains(paymentType, '000')}">
                                    <option <c:if test="${productInfo.routeType == '1' }"> selected</c:if> value="1">顺序</option>
                                </c:when>
                                <c:otherwise>
                                    <option <c:if test="${productInfo.routeType == '1' }"> selected</c:if> value="1">顺序</option>
                                    <option <c:if test="${productInfo.routeType == '2' }"> selected</c:if> value="2">轮询</option>
                                </c:otherwise>
                            </c:choose>
                        </select>
                    </div>
                </div>
            </td>
            <td colspan="3">
                <div class="control-group">
                    <label class="control-label" for="extend1">说明</label>
                    <div class="controls">
				    <textarea name="extend1" placeholder="" style="width:500px;" id="extend1" rows="3">${productInfo.extend1}</textarea>
                    </div>
                </div>
            </td>
        </tr>
    </table>


    <!-- ********************************************************************** -->
    <%--费率移除

        <div class="breadcrumb">
            <label>资金结算信息</label>
        </div>
        <tags:message content="${message}" type="${messageType}"/>
        <table class="table">
            <tr>

                <td>
                    <div class="control-group">
                        <label class="control-label">结算方式</label>
                        <div class="controls">
                            <select name="settleMode" class="input-xlarge" id="settleMode">
                                <option value="">--请选择--</option>
                                <option
                                        <c:if test="${productInfo.settleMode == 1}">selected</c:if> value="1">收单对公
                                </option>
                                <option
                                        <c:if test="${productInfo.settleMode == 2}">selected</c:if> value="2">收单对私
                                </option>
                                <option
                                        <c:if test="${productInfo.settleMode == 3}">selected</c:if> value="3">代付
                                </option>
                                <option
                                        <c:if test="${productInfo.settleMode == 4}">selected</c:if> value="4">银行直清
                                </option>
                            </select>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="control-group">
                        <label class="control-label">结算周期</label>
                        <div class="controls">
                            <select name="settleCycle" class="input-xlarge" id="settleCycle">
                                <option value="">--请选择--</option>
                                <option
                                        <c:if test="${productInfo.settleCycle == 'D0'}">selected</c:if> value="D0">D0
                                </option>
                                <option
                                        <c:if test="${productInfo.settleCycle == 'D1'}">selected</c:if> value="D1">D1
                                </option>
                                <option
                                        <c:if test="${productInfo.settleCycle == 'T0'}">selected</c:if> value="T0">T0
                                </option>
                                <option
                                        <c:if test="${productInfo.settleCycle == 'T1'}">selected</c:if> value="T1">T1
                                </option>
                            </select>
                        </div>
                    </div>
                </td>

                <td>
                    <div class="control-group">
                        <label class="control-label" for="settleLowestFee">最低手续费(分)</label>
                        <div class="controls">
                            <input name="settleLowestFee" value="${productInfo.settleLowestFee }" placeholder=""
                                   class="input-xlarge"
                                   type="text" id="settleLowestFee">
                        </div>
                    </div>
                </td>
            </tr>

            <tr>
                <td>
                    <div class="control-group">
                        <label class="control-label">收费类型</label>
                        <div class="controls">
                            <select name="feeType" class="input-xlarge" id="feeType" onchange="rateOrAmount()">
                                <option value="">--请选择--</option>
                                <option
                                        <c:if test="${productInfo.feeType == 1}">selected</c:if> value="1">按笔收费
                                </option>
                                <option
                                        <c:if test="${productInfo.feeType == 2}">selected</c:if> value="2">按比例收费
                                </option>
                                <option
                                        <c:if test="${chanMchPaytye.feeType == 3}">selected</c:if> value="3">混合
                                </option>
                            </select>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="control-group">
                        <label class="control-label" for="feeRate">收费比率(‰)</label>
                        <div class="controls">
                            <input name="feeRate" value="${productInfo.feeRate }" placeholder=""
                                   class="input-xlarge" type="text" id="feeRate">
                        </div>
                    </div>
                </td>
                <td>
                    <div class="control-group">
                        <label class="control-label" for="feeAmount">收费金额(/笔(分))</label>
                        <div class="controls">
                            <input name="feeAmount" value="${productInfo.feeAmount }" placeholder="" class="input-xlarge"
                                   type="text" id="feeAmount">
                        </div>
                    </div>
                </td>
            </tr>
            <tr>

                <td>
                    <div class="control-group">
                        <label class="control-label">生效时间</label>
                        <div class="controls">
                            <label class="control-label" for="activeNow">立即</label>
                            <input name="feeStatus" value="1" placeholder="" class="input-xlarge"
                                   type="radio"
                                   <c:if test="${productInfo.feeStatus == 1}">checked</c:if> id="activeNow">
                            <label class="control-label" for="activeThan">定时</label>
                            <input name="feeStatus" value="0" placeholder="" class="input-xlarge"
                                   type="radio"
                                   <c:if test="${productInfo.feeStatus == 0}">checked</c:if> id="activeThan">
                            <input id="activeTime" name="activeTime" type="text" class="input-medium Wdate"
                                   value="${productInfo.activeTime}"
                                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true, minDate:'%y-%M-%d'});"/>

                        </div>
                    </div>
                </td>
            </tr>
        </table> --%>

    <!-- ********************************************************************** -->
    <c:if test="${!subPro}">
    <div class="breadcrumb">
        <label>通道商户支付方式</label>
        <a style="float:right;cursor: pointer;font-size:15px;text-decoration: none;" onclick="addPayType();">新增支付方式</a>
    </div>
    <table class="table" id="payTypeTable">
        <c:if test="${productInfo == null}">
            <tr>
                <input type="hidden" value="1" name="sort">
                <td>
                    <div class="control-group">
                        <label class="control-label"></label>
                        <div class="controls" style="padding-top:10px;">
                            <span>顺序 1</span>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="control-group">
                        <label class="control-label">通道商户支付方式</label>
                        <div class="controls"><select name="payType" class="input-xxlarge payTypeSelect" id="payType0">
                        	<option value="">--请选择--</option>
                            <c:forEach items="${chanInfoList}" var="chanInfo">
                                <option value="${chanInfo.id}">${chanInfo.name}</option>
                            </c:forEach>
                        </select>
                            &nbsp;
                            <a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="deletePayType(this);">删除</a>
                            &nbsp;&nbsp;<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="upTr(this);">上移</a>
                            &nbsp;&nbsp;<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="downTr(this);">下移</a>
                        </div>
                    </div>
                </td>
            </tr>
        </c:if>
        <c:forEach items="${productInfo.productRelas}" var="chanMchtInfo">
        <tr>
            <input type="hidden" value="${chanMchtInfo.sort}" name="sort">
            <td>
                <div class="control-group">
                    <label class="control-label"></label>
                    <div class="controls" style="padding-top:10px;">
                        <span>顺序 ${chanMchtInfo.sort}</span>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">通道商户支付方式</label>
                    <div class="controls"><select name="payType" class="input-xxlarge payTypeSelect" id="payType${chanMchtInfo.sort}">
                    	<option value="0">--请选择--</option>
                        <c:forEach items="${chanInfoList}" var="chanInfo">
                            <option <c:if test="${chanMchtInfo.chanMchtPaytypeId == chanInfo.id}">selected</c:if>
                                    value="${chanInfo.id}">${chanInfo.name}</option>
                        </c:forEach>
                    </select>
                        <a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="deletePayType(this);">删除</a>
                        &nbsp;&nbsp;<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="upTr(this);">上移</a>
                        &nbsp;&nbsp;<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="downTr(this);">下移</a>
                    </div>
                </div>
            </td>
        </tr>
        </c:forEach>
    </table>
    </c:if>

    <!-- ********************************************************************** -->

    <!-- ********************************************************************** -->
    <%--收银台展示--%>
    <c:if test="${subPro}">
    <div class="breadcrumb">
        <label>子产品列表</label>
        <a style="float:right;cursor: pointer;font-size:15px;text-decoration: none;" onclick="addProduct();">新增子产品</a>
    </div>
    <table class="table" id="productTable">
        <c:if test="${productInfo == null}">
            <tr>
                <input type="hidden" value="1" name="sort">
                <td>
                    <div class="control-group">
                        <label class="control-label"></label>
                        <div class="controls" style="padding-top:10px;">
                            <span>顺序 1</span>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="control-group">
                        <label class="control-label">子产品</label>
                        <div class="controls"><select name="subProductId" class="input-xxlarge productSelect" id="subProductId0">
                            <option value="">--请选择--</option>
                            <c:forEach items="${subProductLists}" var="subProductItem">
                                <option value="${subProductItem.id}">${subProductItem.name}</option>
                            </c:forEach>
                        </select>
                            &nbsp;
                            <a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="deleteProduct(this);">删除</a>
                            &nbsp;&nbsp;<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="upTr(this);">上移</a>
                            &nbsp;&nbsp;<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="downTr(this);">下移</a>
                        </div>
                    </div>
                </td>
            </tr>
        </c:if>
        <c:forEach items="${productInfo.subProducts}" var="subProduct">
            <tr>
                <input type="hidden" value="${subProduct.sort}" name="sort">
                <td>
                    <div class="control-group">
                        <label class="control-label"></label>
                        <div class="controls" style="padding-top:10px;">
                            <span>顺序 ${subProduct.sort}</span>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="control-group">
                        <label class="control-label">子产品</label>
                        <div class="controls"><select name="subProductId" class="input-xxlarge productSelect" id="subProductId${subProduct.sort}">
                            <option value="0">--请选择--</option>
                            <c:forEach items="${subProductLists}" var="subProductItem">
                                <option <c:if test="${subProduct.subProductId == subProductItem.id}">selected</c:if>
                                        value="${subProductItem.id}">${subProductItem.name}</option>
                            </c:forEach>
                        </select>
                            <a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="deleteProduct(this);">删除</a>
                            &nbsp;&nbsp;<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="upTrProduct(this);">上移</a>
                            &nbsp;&nbsp;<a style="cursor: pointer;font-size:15px;text-decoration: none;" onclick="downTrProduct(this);">下移</a>
                        </div>
                    </div>
                </td>
            </tr>
        </c:forEach>
    </table>
    </c:if>

    <div class="breadcrumb">
        <input id="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);"
               name="btnCancel"/>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"
               style="margin-left: 5px;">
    </div>

</form>
</body>

</html>