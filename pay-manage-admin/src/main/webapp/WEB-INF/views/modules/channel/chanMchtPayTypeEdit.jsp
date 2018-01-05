<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>渠道管理</title>
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

        //名字生成
        function getName() {
            var name = $("#mchtId").find("option:selected").data("shortname") + "-" + $("#chanId option:selected").text() + "-" + $("#payType option:selected").text();
            $("#name").attr("value", name);

        }

        //合同类型决定是否有上下级
        function parentDiss() {
            if ( $("#contractType").val() === "2"){
                $("#parentId").attr("disabled", true);
                $("#parentId").val("");
            }else {
                $("#parentId").attr("disabled", false);
            }
        }

        //商户信息级联
        function mchtChange() {
            var code = $("#mchtId").find("option:selected").data("mchtcode");
            $("#mchtCode").val(code);
        }

        function rateOrAmount() {
            if ( $("#feeType").val() === "1"){
                $("#feeRate").attr("disabled", "disabled");
                $("#feeAmount").attr("disabled", false);
            }else {
                $("#feeAmount").attr("disabled", "disabled");
                $("#feeRate").attr("disabled", false);
            }
        }

        $(function () {
            getName();
            cascadeSmsContent();

            $("#chanMchtForm").validate({
                debug: true, //调试模式取消submit的默认提交功能
                //errorClass: "label.error", //默认为错误的样式类为：error
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function (form) {   //表单提交句柄,为一回调函数，带一个参数：form
                    form.submit();   //提交表单
                },

                rules: {
                    mchtId: {required: true},
                    chanId: {required: true},
                    payType: {required: true},
                    name: {required: true},
                    terminalNo: {maxlength:64},
                    bizCode: {maxlength:64},
                    chanMchtPassword: {maxlength:128},
                    opAccount: {maxlength:32},
                    opPassword: {maxlength:32},
                    payUrl: {maxlength:255, required: true},
                    synNotifyUrl: {maxlength:255},
                    refundUrl: {maxlength:255},
                    tranUrl: {maxlength:255, required: true},
                    extend1: {maxlength:255},
                    checkUrl: {maxlength:255},
                    asynNotifyUrl: {maxlength:255},
                    queryUrl: {maxlength:255},
                    certPath1: {maxlength:128},
                    certPath2: {maxlength:128},
                    certPath3: {maxlength:128},
                    certContent1: {maxlength:2048},
                    certContent2: {maxlength:2048},
                    certContent3: {maxlength:2048},
                    contractType: {required: true},

                    settleCycle: {},
                    settleMode: {},
                    feeType: {},
                    lowestFee: {
                        max: 99999999999,
                        number: true
                    },
                    feeRate: {
                        max: 99999999999,
                        number: true
                    },
                    feeAmount: {
                        max: 99999999999,
                        number: true
                    }
                }
            });
            $("#chanId").change(function (){
            	 $("#chanCode").val($("#chanId").find("option:selected").data("chancode"));
            	 console.log("11=="+$("#chanCode").val());
            });
            $("#chanCode").val($("#chanId").find("option:selected").data("chancode"));
            console.log("33=="+$("#chanCode").val());
            
            $("#smsSendType").change(function(){
            	cascadeSmsContent();
            });
        });

        function cascadeSmsContent(){
        	var smsSendType = $("#smsSendType").val();
        	if(smsSendType=='0'){
        		$("#smsContentTemplet").attr("disabled",true);
        	}else{
        		$("#smsContentTemplet").attr("disabled",false);
        	}
        }
    </script>
</head>
<body>

<div class="breadcrumb">
    <label><a href="#">支付通道管理</a> > <a href="#"><b>支付通道新增/编辑</b></a></label>
</div>

<form id="chanMchtForm" action="${ctx}/channel/addChanMchtPayType" method="post">
    <tags:message content="${message}" type="${messageType}"/>
    <input type="hidden" id="op" name="op" value="${op }"/>
    <input type="hidden" name="id" value="${chanMchPaytye.id }"/>
	<input type="hidden" name="chanCode" value="${chanMchPaytye.chanCode }" id="chanCode"/>
    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>支付通道基本信息</label>
    </div>
    <tags:message content="${message}" type="${messageType}"/>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">通道商户支付方式名称<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="name" value="${chanMchPaytye.name }" placeholder=""
                               class="input-xxlarge" type="text" id="name" readonly>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">合同类型<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="contractType" class="input-xlarge" id="contractType" onchange="parentDiss()">
                            <option value="">--请选择--</option>
                            <option <c:if test="${chanMchPaytye.contractType == 1}">selected</c:if> value="1">商户合同</option>
                            <option <c:if test="${chanMchPaytye.contractType == 2}">selected</c:if> value="2">服务商合同</option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
            <div class="control-group">
                <label class="control-label">所属上级</label>
                <div class="controls">
                    <select class="input-xlarge" name="parentId" id="parentId">
                        <option>无</option>
                        <c:forEach items="${chanMchts}" var="chanMcht">
                            <option <c:if test="${chanMchPaytye.parentId == chanMcht.id}">selected</c:if>
                                    value="${chanMcht.id}">${chanMcht.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">商户名称<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="mchtId" class="input-xlarge" id="mchtId" onchange="getName()"
                                <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                            <option data-shortName="请选择" value="">--请选择--</option>
                            <c:forEach items="${mchtInfos}" var="mchtInfo">
                                <option <c:if test="${chanMchPaytye.mchtId == mchtInfo.id}">selected</c:if>
                                        data-mchtCode="${mchtInfo.mchtCode}" data-shortName="${mchtInfo.shortName}"
                                        value="${mchtInfo.id}">${mchtInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">支付通道<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="chanId" class="input-xlarge" id="chanId" onchange="getName()"
                                <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                            <option value="">--请选择--</option>
                            <c:forEach items="${chanInfos}" var="chanInfo">
                                <option data-chanCode="${chanInfo.chanCode }"<c:if test="${chanMchPaytye.chanId == chanInfo.id}">selected</c:if> value="${chanInfo.id}">${chanInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">支付方式<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="payType" class="input-xlarge" id="payType" onchange="getName()"
                                <c:if test="${op == 'edit'}">disabled="disabled"</c:if>>
                            <option value="">--请选择--</option>
                            <c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
                                <option <c:if test="${chanMchPaytye.payType == paymentTypeInfo.code}">selected</c:if> value="${paymentTypeInfo.code}">${paymentTypeInfo.desc}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
        </tr>

        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label" for="chanMchtNo">通道商户编号</label>
                    <div class="controls">
                        <input name="chanMchtNo" value="${chanMchPaytye.chanMchtNo }" placeholder=""
                               class="" type="text" id="chanMchtNo">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label" for="terminalNo">通道合作号</label>
                    <div class="controls">
                        <input name="terminalNo" value="${chanMchPaytye.terminalNo }" placeholder=""
                               class="" type="text" id="terminalNo">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label" for="bizCode">业务代码</label>
                    <div class="controls">
                        <input name="bizCode" value="${chanMchPaytye.bizCode}" placeholder="" class="" type="text"
                               id="bizCode">
                    </div>
                </div>
            </td>

        </tr>

        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label" for="chanMchtPassword">商户密码</label>
                    <div class="controls">
                        <input name="chanMchtPassword" placeholder="" value="${chanMchPaytye.chanMchtPassword}" type="text" id="chanMchtPassword">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="opAccount">操作员账号</label>
                    <div class="controls">
                        <input name="opAccount" placeholder="" value="${chanMchPaytye.opAccount}" class="" type="text"
                               id="opAccount">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="opPassword">操作员密码</label>
                    <div class="controls">
                        <input name="opPassword" placeholder="" value="${chanMchPaytye.opPassword}" class="input-xlarge" type="text"
                               id="opPassword">
                    </div>
                </div>
            </td>
        </tr>
        
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="payUrl">下单地址<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="payUrl" value="${chanMchPaytye.payUrl }" placeholder=""
                               style="width:500px;" type="text" id="payUrl" class=" {required:true,url:true}">
                    </div>
                </div>
            </td>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="synNotifyUrl">同步通知地址</label>
                    <div class="controls">
                        <input name="synNotifyUrl" value="${chanMchPaytye.synNotifyUrl }" placeholder=""
                               style="width:500px;" type="text" id="synNotifyUrl" class=" {required:true,url:true}">
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="refundUrl">退款地址</label>
                    <div class="controls">
                        <input name="refundUrl" value="${chanMchPaytye.refundUrl }" placeholder=""
                               style="width:500px;" type="text" id="refundUrl" class=" {required:true,url:true}">
                    </div>
                </div>
            </td>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="asynNotifyUrl">异步通知地址</label>
                    <div class="controls">
                        <input name="asynNotifyUrl" value="${chanMchPaytye.asynNotifyUrl }" placeholder=""
                               style="width:500px;" type="text" id="asynNotifyUrl" class=" {required:true,url:true}">
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="queryUrl">查单地址<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="queryUrl" value="${chanMchPaytye.queryUrl }" placeholder=""
                               style="width:500px;" type="text" id="queryUrl" class=" {required:true,url:true}">
                    </div>
                </div>
            </td>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="checkUrl">对账地址</label>
                    <div class="controls">
                        <input name="checkUrl" value="${chanMchPaytye.checkUrl }" placeholder=""
                               style="width:500px;" type="text" id="checkUrl" class=" {required:true,url:true}">
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label" for="certPath1">通道公钥地址<span style="color: red;"><span style="color: red;"></span></span></label>
                    <div class="controls">
                        <input name="certPath1" value="${chanMchPaytye.certPath1 }" placeholder=""
                               type="text" id="certPath1">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="certPath2">平台私钥地址<span style="color: red;"><span style="color: red;"></span></span></label>
                    <div class="controls">
                        <input name="certPath2" value="${chanMchPaytye.certPath2 }" placeholder=""
                               type="text" id="certPath2">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="certPath3">平台公钥地址<span style="color: red;"><span style="color: red;"></span></span></label>
                    <div class="controls">
                        <input name="certPath3" value="${chanMchPaytye.certPath3 }" placeholder=""
                               type="text" id="certPath3">
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label" for="certContent1">通道公钥内容<span style="color: red;"><span style="color: red;"></span></span></label>
                    <div class="controls">
			    <textarea name="certContent1" placeholder="" class="" id="certContent1"
                          rows="5">${chanMchPaytye.certContent1}</textarea>
                    </div>
                </div>
            </td>
            <td>
                        <div class="control-group">
                            <label class="control-label" for="certContent2">平台私钥内容<span style="color: red;"><span style="color: red;"></span></span></label>
                            <div class="controls">
			    <textarea name="certContent2" placeholder="" class="" id="certContent2"
                          rows="5">${chanMchPaytye.certContent2}</textarea>
                            </div>
                        </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="certContent3">平台公钥内容<span style="color: red;"><span style="color: red;"></span></span></label>
                    <div class="controls">
			    <textarea name="certContent3" placeholder="" class="" id="certContent3"
                          rows="5">${chanMchPaytye.certContent3}</textarea>
                    </div>
                </div>
            </td>
        </tr>
        <tr>

            <td colspan="1">
                <div class="control-group">
                    <label class="control-label" for="smsSendType">快捷支付组合类型</label>
                    <div class="controls">
                        <select name="combType" id="combType">
                            <option value="0" >--请选择--</option>
                            <option value="1" <c:if test="${chanMchPaytye.combType == 1}">selected</c:if>>（绑卡+支付）组合接口</option>
                            <option value="2" <c:if test="${chanMchPaytye.combType == 2}">selected</c:if>>标准接口</option>
                        </select>
                    </div>
                </div>
            </td>

	        	<td colspan="1">
	                <div class="control-group">
	                    <label class="control-label" for="smsSendType">短信发送方式</label>
	                    <div class="controls">
	                    	<select name="smsSendType" id="smsSendType">
                                <option value="0" >--请选择--</option>
	                    		<c:forEach items="${fns:getDictList('sms_send_type') }" var="dict">
		                    		<option value="${dict.value}" <c:if test="${dict.value eq chanMchPaytye.smsSendType}">selected</c:if>>
		                    			${dict.label}
		                    		</option>
		                    	</c:forEach>
	                    	</select>
	                    </div>
	                </div>
	            </td>
	            
	        <td colspan="3">
                <div class="control-group">
                    <label class="control-label" for="smsContentTemplet">短信内容模版</label>
                    <div class="controls">
                        <textarea name="smsContentTemplet" placeholder="" class="" id="smsContentTemplet"
                          rows="3" style="width:500px;">${chanMchPaytye.smsContentTemplet}</textarea>
                    </div>
                </div>
            </td>
        </tr>
    </table>

    <!-- ********************************************************************** -->
    <%--<div class="breadcrumb">--%>
        <%--<label>商务合作信息</label>--%>
    <%--</div>--%>
    <%--<tags:message content="${message}" type="${messageType}"/>--%>
    <%--<table class="table">--%>
        <%--<tr>--%>

            <%--<td>--%>
                <%--<div class="control-group">--%>
                    <%--<label class="control-label">合同类型<span style="color: red;">*</span></label>--%>
                    <%--<div class="controls">--%>
                        <%--<select name="contractType" class="input-xlarge" id="contractType">--%>
                            <%--<option value="">--请选择--</option>--%>
                            <%--<option <c:if test="${chanMchPaytye.contractType == 1}">selected</c:if> value="1">商户合同</option>--%>
                            <%--<option <c:if test="${chanMchPaytye.contractType == 2}">selected</c:if> value="2">服务商合同</option>--%>
                        <%--</select>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</td>--%>

            <%--<td>--%>
                <%--<div class="control-group">--%>
                    <%--<label class="control-label">所属上级</label>--%>
                    <%--<div class="controls">--%>
                        <%--<select name="parentId">--%>
                            <%--<option>请选择</option>--%>
                            <%--<c:forEach items="${chanMchts}" var="chanMcht">--%>
                                <%--<option <c:if test="${chanMchPaytye.parentId == chanMcht.id}">selected</c:if>--%>
                                        <%--value="${chanMcht.id}">${chanMcht.name}</option>--%>
                            <%--</c:forEach>--%>
                        <%--</select>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</td>--%>
        <%--</tr>--%>

    <%--</table>--%>
    <!-- ********************************************************************** -->
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
                            <option <c:if test="${chanMchPaytye.settleMode == 1}">selected</c:if> value="1">收单对公</option>
                            <option <c:if test="${chanMchPaytye.settleMode == 2}">selected</c:if> value="2">收单对私</option>
                            <option <c:if test="${chanMchPaytye.settleMode == 3}">selected</c:if> value="3">代付</option>
                            <option <c:if test="${chanMchPaytye.settleMode == 4}">selected</c:if> value="4">银行直清</option>
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
                            <option <c:if test="${chanMchPaytye.settleCycle == 1}">selected</c:if> value="1">D0</option>
                            <option <c:if test="${chanMchPaytye.settleCycle == 2}">selected</c:if> value="2">T0</option>
                            <option <c:if test="${chanMchPaytye.settleCycle == 3}">selected</c:if> value="3">T1</option>
                        </select>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label" for="lowestFee">结算最低金额(分)</label>
                    <div class="controls">
                        <input name="lowestFee" value="${chanMchPaytye.lowestFee }" placeholder="" class="input-xlarge"
                               type="text" id="lowestFee">
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
                            <option <c:if test="${chanMchPaytye.feeType == 1}">selected</c:if> value="1">按笔收费</option>
                            <option <c:if test="${chanMchPaytye.feeType == 2}">selected</c:if> value="2">按比例收费</option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="feeRate">收费比率(‰)</label>
                    <div class="controls">
                        <input name="feeRate" value="${chanMchPaytye.feeRate }" placeholder=""
                               class="input-xlarge" type="text" id="feeRate">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" for="feeAmount">收费金额(/笔(分))</label>
                    <div class="controls">
                        <input name="feeAmount" value="${chanMchPaytye.feeAmount }" placeholder="" class="input-xlarge"
                               type="text" id="feeAmount">
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label" for="feeAmount">当日限额(分)</label>
                    <div class="controls">
                        <input name="perdayPayMaxAmount" value="${chanMchPaytye.perdayPayMaxAmount }" placeholder="" class="input-xlarge"
                               type="text" id="perdayPayMaxAmount">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">生效时间</label>
                    <div class="controls">
                        <label class="control-label" for="activeNow">立即</label>
                        <input name="feeStatus" value="1" placeholder="" class="input-xlarge"
                               type="radio" <c:if test="${chanMchPaytye.feeStatus == 1}">checked</c:if>
                               <c:if test="${op == 'add'}">checked</c:if> id="activeNow">
                        <label class="control-label" for="activeThan">定时</label>
                        <input name="feeStatus" value="0" placeholder="" class="input-xlarge"
                               type="radio" <c:if test="${chanMchPaytye.feeStatus == 0}">checked</c:if> id="activeThan">
                        <input id="activeTime" name="activeTime" type="text" class="input-medium Wdate"
                               value="${chanMchPaytye.activeTime}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true, minDate:'%y-%M-%d'});"/>

                    </div>
                </div>
            </td>
        </tr>
    </table>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>平台信息</label>
    </div>
    <tags:message content="${message}" type="${messageType}"/>
    <table class="table">
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label" for="tranUrl">平台接口地址<span style="color: red;">*</span></label>
                    <div class="controls">
                        <input name="tranUrl" value="${chanMchPaytye.tranUrl }" placeholder=""
                               style="width:500px;" type="text" id="tranUrl" class=" {required:true,url:true}">
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">状态</label>
                    <div class="controls">
                        <select name="status" class="input-xlarge" id="status">
                            <option value="1">启用</option>
                            <option <c:if test="${chanMchPaytye.status == 2}">selected</c:if> value="2">停用</option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">SDK类型</label>
                    <div class="controls">
                        <select name="sdkType" class="input-xlarge" id="sdkType">
                            <option <c:if test="${chanMchPaytye.sdkType == 1}">selected</c:if> value="1">实时</option>
                            <option <c:if test="${chanMchPaytye.sdkType == 2}">selected</c:if> value="2">循环</option>
                            <option <c:if test="${chanMchPaytye.sdkType == 3}">selected</c:if> value="3">一次</option>
                        </select>
                    </div>
                </div>
            </td>
        </tr>

        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label" for="extend1">说明</label>
                    <div class="controls">
		    			<textarea name="extend1" placeholder="" style="width:500px;" id="extend1" rows="3">${chanMchPaytye.extend1}</textarea>
                    </div>
                </div>
            </td>
        </tr>
    </table>
    <div class="breadcrumb">
        <input name="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);"/>
        <input name="btnSubmit" class="btn btn-primary" type="submit" value="保存" style="margin-left: 5px;">
    </div>

</form>
</body>
</html>