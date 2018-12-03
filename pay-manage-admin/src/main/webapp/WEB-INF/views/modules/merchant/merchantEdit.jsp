<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>商户编辑</title>
    <meta name="decorator" content="default"/>
    <link href="${ctxStatic}/city/SG_area_select.css" rel="stylesheet"/>
    <script type="text/javascript" src="${ctxStatic}/city/SG_area_select.js"></script>
    <script type="text/javascript" src="${ctxStatic}/city/iscroll.js"></script>

    <script src="https://cdn.bootcss.com/jquery-toast-plugin/1.3.1/jquery.toast.min.js" type="text/javascript"></script>
    <link href="https://cdn.bootcss.com/jquery-toast-plugin/1.3.1/jquery.toast.min.css" rel="stylesheet"
          type="text/css"/>

    <!-- webuploader.js -->
    <script type="text/javascript" src="${ctxStatic }/webuploader/webuploader.js"></script>
    <!-- webuploader.css -->
    <link rel="stylesheet" type="text/css" href="${ctxStatic }/webuploader/webuploader.css">

    <script type="text/javascript" src="${ctxStatic }/webuploader/mywbuploader.js"></script>

    <script type="text/javascript">

        function getUploadId(id) {
            window.id = id;
        }

        function checkType() {
            if ($('#agent').attr("checked") == 'checked') {
                $('[name="signType"]').removeAttr("checked", "");
                $('[name="signType"]').attr("disabled", true);
                $('#agent').attr("checked", "checked");
                $('#agent').attr("disabled", false);
                checkPayMcht();
            } else {
                $('[name="signType"]').attr("disabled", false);
                checkPayMcht();
            }
        }

        function checkPayMcht() {
            if ($('#payMcht').attr("checked") == 'checked') {
                $('#parentId').attr("disabled", false)
            } else {
                $('#parentId').val("");
                $('#parentId').attr("disabled", true)
            }
        }

        function accountType() {
            if ($('#fundSettleAccountType').val() == 1) {
                $('#fundSettleSubbranchName').attr("class", "required");
                $('#subbranchName').html("支行<span style='color: red;'></span>");
                $('#fundSettleLinkBankNo').attr("class", "required");
                $('#linkBankNo').html("联行号<span style='color: red;'></span>");
            } else {
                $('#fundSettleSubbranchName').attr("class", "span3");
                $('#subbranchName').text("支行");
                $('#fundSettleLinkBankNo').attr("class", "span3");
                $('#linkBankNo').text("联行号");
            }
        }

        $(function () {

            //初始化某些输入框
            checkType();
            accountType();

           /* $('#city').on('click', function () {
                var json = ${areas};
                if(json!= null && json!= ''){
                    $.areaSelect(json);
                }
            });*/

            customUploader.init({
                "location": "merchant", "thumb": {width: 100, height: 100, "crop": false}
            });

            $("#merchtForm").validate({
                debug: false, //调试模式取消submit的默认提交功能
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function (form) {   //表单提交句柄,为一回调函数，带一个参数：form
                    showShadow();

                    if ($("#chanCount").val() == "1") {
                        if (!($("#requestMcht").attr("checked") == 'checked')
                            && !($("#serverMcht").attr("checked") == 'checked')) {
                            alert("该商户已配置通道商户支付方式，必须勾选申报商户或服务商");
                            hideShadow();
                            return;
                        }
                    }
                    var isFix =$("input[name='isFixed']:checked").val();
                    if(isFix=="0"){
                        var activeTime =$("#activeTime").val();
                        if(activeTime == "" || activeTime ==null){
                            alert("结算生效为定时时，生效时间必填");
                            hideShadow();
                            return ;
                        }
                    }

                    var op = $("#op").val();
                    if (op == 'add') {
                        url = "${ctx}/merchant/addMerchantSave";
                    } else if (op == 'edit') {
                        url = "${ctx}/merchant/editMerchantSave";
                    }
                    document.forms[0].action = url;
                    document.forms[0].submit();
                },
                errorPlacement: function (error, element) {
                    error.appendTo(element.parent());
                },
                rules: {
                    shortName: {maxlength: 32, required: true},
                    mchtKey: {maxlength: 255},
                    // mchtType: {maxlength: 8, required: true},
                    clientIp: {maxlength: 255},
                    synNotifyUrl: {maxlength: 255},
                    asynNotifyUrl: {maxlength: 255},
                    // legalPerson: {maxlength: 32, required: true},
                    // legalCardNo: {maxlength: 32, required: true},
                    // city: {maxlength: 32, required: true},
                    businessLicenseCode: {maxlength: 32},
                    // companyAdr: {maxlength: 128, required: true},
                    // contactName: {maxlength: 32, required: true},
                    // mobile: {maxlength: 16, required: true},
                    // phone: {maxlength: 20, required: true},
                    // serviceMobile: {maxlength: 16, required: true},
                    servicePhone: {maxlength: 20},
                    serviceQq: {maxlength: 20},
                    serviceWx: {maxlength: 64},
                    // fundSettleAccountName: {maxlength: 64, required: true},
                    // fundSettleBankCard: {maxlength: 64, required: true},
                    // fundSettleBankName: {maxlength: 64, required: true},
                    // fundSettleProvince: {maxlength: 64, required: true},
                    // fundSettleCity: {maxlength: 64, required: true},
                    // fundSettleSubbranchName: {maxlength: 64},
                    // fundSettleLinkBankNo: {maxlength: 64},
                    // fundSettleAccountType: {maxlength: 64, required: true},

//                    blcPath: {required: true},
//                    contractFilePath: {required: true},
//                    openingPermitPath: {required: true},
//                    bankCardFrontPath: {required: true},
//                    bankIdcardPath: {required: true},

                    signType: {required: true},
                    merchantSettleCycle :{required:true},
                    name: {
                        required: true,
                        maxlength: 64
                    },
                    // email: {
                    //     email: true, required: true
                    // },
                    website: {maxlength: 64},
                    mchtPropertyTag: {maxlength: 64}
                },
                messages: {
                    name: {
                        required: '必填'
                    },
                    email: {
                        email: 'email格式不正确'
                    },
                    website: {
                        url: 'url格式不正确'
                    }
                }
            });
        });

        function checkShortName() {
            var mcht = {};
            mcht.shortName = $('#shortName').val();
            mcht.op = $('#op').val();
            mcht.id = $('#id').val();
            var checkUrl = "/admin/merchant/checkShortName";
            $.ajax({
                url: checkUrl, //服务器端请求地址
                data: mcht,
                dataType: 'json', //返回值类型 一般设置为json
                type: "post",
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                success: function (data) {  //服务器成功响应处理函数
                    if (data.count > 0) {
                        alert("商户简称重复");
                    }
                },
                error: function (data, e) {//服务器响应失败处理函数
                    console.log(data);
                }
            })
        }

    </script>
</head>
<body>

<div class="breadcrumb">
    <label>
        <th><a href="#">商户管理</a> > <a href="#"><b>商户编辑</b></a></th>
    </label>
</div>

<div class="shadow" style="display:block;">
    <div id="tbl_brand_processing" class="dataProcessing">
        <img src="${ctxStatic}/images/loading.gif"><span>&nbsp;&nbsp;处理中...</span>
    </div>
</div>

<form id="merchtForm" method="post">
    <input type="hidden" id="op" name="op" value="${op}">
    <input type="hidden" id="id" name="id" value="${merchant.id}">
    <input type="hidden" id="chanCount" name="chanCount" value="${chanCount}">
    <input type="hidden" id="productCount" name="productCount" value="${productCount}">

    <div class="breadcrumb">
        <label>
            <th>基本信息</th>
        </label>
    </div>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">商户编号</label>
                    <div class="controls">
                        <input name="mchtCode" value="${merchant.mchtCode}" placeholder="" class="input-nomal"
                               type="text" disabled="disabled"/>
                    </div>
                </div>
            </td>

            <td>

                <div class="control-group">
                    <label class="control-label">商户名称<span style="color: red;"><span style="color: red;">*</span></span></label>
                    <div class="controls">
                        <input name="name" value="${merchant.name}" placeholder="" class="input-xlarge" type="text"
                               maxlength="64">
                    </div>
                </div>
            </td>

            <td>

                <div class="control-group">
                    <label class="control-label">商户简称<span style="color: red;"><span style="color: red;">*</span></span></label>
                    <div class="controls">
                        <input name="shortName" id="shortName" value="${merchant.shortName}" placeholder=""
                               class="input-xlarge"
                               type="text" onblur="checkShortName()">
                    </div>
                </div>
            </td>

        </tr>
        <tr>
            <td>

                <%--<div class="control-group">--%>
                <%--<label class="control-label">商户类型</label>--%>
                <%--<div class="controls">--%>
                <%--<select name="mchtType" class="input-small">--%>
                <%--<c:forEach items="${merchantTypList }"  var="merchtType">--%>
                <%--<option value="${merchtType.code }" <c:if test="${merchant.mchtType == merchtType.code }">selected</c:if>>${merchtType.desc }</option>--%>
                <%--</c:forEach>--%>
                <%--</select>--%>
                <%--</div>--%>
                <%--</div>--%>

                <div class="control-group">
                    <label class="control-label">省市区<span style="color: red;"></span></label>
                    <div class="controls" id="demo">
                        <input id="cityCode" name="cityCode" type="hidden" class="sg-area-resultCode"/>
                        <input id="city" name="city" type="text" value="${merchant.city}" class="sg-area-result"/>
                    </div>
                </div>
            </td>

            <td>

                <div class="control-group">
                    <label class="control-label">注册地址<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="companyAdr" value="${merchant.companyAdr}" placeholder="" class="input-xlarge"
                               type="text">
                    </div>
                </div>
            </td>

            <td>

                <div class="control-group">
                    <label class="control-label">行业类别<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="mchtType" value="${merchant.mchtType}" placeholder="" class="input-xlarge"
                               type="text">
                    </div>
                </div>
            </td>

        </tr>
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">联系电话<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="phone" value="${merchant.phone}" placeholder="" class="input-xlarge" type="text">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">邮箱<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="email" value="${merchant.email}" placeholder="" class="input-xlarge" type="text">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">公司网址</label>
                    <div class="controls">
                        <input name="website" value="${merchant.website}" placeholder="" class="input-xlarge"
                               type="text">
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">客服电话</label>
                    <div class="controls">
                        <input name="servicePhone" value="${merchant.servicePhone}" placeholder="" class="input-xlarge"
                               type="text">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">客服微信</label>
                    <div class="controls">
                        <input name="serviceWx" value="${merchant.serviceWx}" placeholder="" class="input-xlarge"
                               type="text">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">客服QQ</label>
                    <div class="controls">
                        <input name="serviceQq" value="${merchant.serviceQq}" placeholder="" class="input-xlarge"
                               type="text">
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">联系人<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="contactName" value="${merchant.contactName}" placeholder="" class="input-xlarge"
                               type="text">
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">联系人手机<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="serviceMobile" value="${merchant.serviceMobile}" placeholder=""
                               class="input-xlarge" type="text">
                    </div>
                </div>
            </td>
        </tr>
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label" for="extend1">商户密钥</label>
                    <div class="controls">
                        <textarea name="mchtKey" placeholder="" style="width:350px;" id="key" readonly
                                  rows="3">${merchant.mchtKey}</textarea>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label" for="extend1">RSA公钥</label>
                    <div class="controls">
                        <textarea name="certContent1" placeholder="" style="width:350px;" id="certContent1"
                                  rows="3">${merchant.certContent1}</textarea>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label" for="extend1">商户IP（半角逗号分隔）</label>
                    <div class="controls">
                        <textarea name="clientIp" placeholder="" style="width:350px;" id="extend1"
                                  rows="3">${merchant.clientIp}</textarea>
                    </div>
                </div>
            </td>

        </tr>
    </table>


    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>
            <th>法人信息</th>
        </label>
    </div>
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">法人姓名<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="legalPerson" value="${merchant.legalPerson}" placeholder="" class="input-xlarge"
                               type="text">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">证件号码<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="legalCardNo" value="${merchant.legalCardNo}" placeholder="" class="input-xlarge"
                               type="text">
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">联系电话<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="mobile" value="${merchant.mobile}" placeholder="" class="input-xlarge" type="text">
                    </div>
                </div>
            </td>
        </tr>
    </table>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>
            <th>结算信息</th>
        </label>
    </div>

    <table class="table">
        <tr>
            <td>
                <label class="control-label"></label>
                <div class="controls">
                    <div class="input-prepend">
                        <span class="add-on">开户行<span style="color: red;"></span></span>
                        <input name="fundSettleBankName" value="${merchant.fundSettleBankName}" class="span3"
                               placeholder="" type="text">
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
            <td>
                <label class="control-label"></label>
                <div class="controls">
                    <div class="input-prepend">
                        <span class="add-on">账户类型</span>
                        <select id="fundSettleAccountType" name="fundSettleAccountType" class="input-xlarge"
                                onchange="accountType()">
                            <option
                                    <c:if test="${merchant.fundSettleAccountType == '2'}">selected</c:if>
                                    value="2">对私
                            </option>
                            <option
                                    <c:if test="${merchant.fundSettleAccountType == '1'}">selected</c:if>
                                    value="1">对公
                            </option>
                            <option
                                    <c:if test="${merchant.fundSettleAccountType == '3'}">selected</c:if>
                                    value="3">非法人
                            </option>
                        </select>
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
            <td>
                <label class="control-label"></label>
                <div class="controls">
                    <div class="input-prepend">
                        <span class="add-on">银行账号</span>
                        <input name="fundSettleBankCard" value="${merchant.fundSettleBankCard}" class="span3"
                               placeholder="" type="text">
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <label class="control-label"></label>
                <div class="controls">
                    <div class="input-prepend">
                        <span class="add-on">开户省</span>
                        <input name="fundSettleProvince" value="${merchant.fundSettleProvince}" class="span3"
                               placeholder="" type="text">
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
            <td>
                <label class="control-label"></label>
                <div class="controls">
                    <div class="input-prepend">
                        <span class="add-on">开户市</span>
                        <input name="fundSettleCity" value="${merchant.fundSettleCity}" class="span3"
                               placeholder="" type="text">
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
            <td>
                <label class="control-label"></label>
                <div class="controls">
                    <div class="input-prepend">
                        <span class="add-on">账户名</span>
                        <input name="fundSettleAccountName" value="${merchant.fundSettleAccountName}" class="span3"
                               placeholder="" type="text">
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <label class="control-label"></label>
                <div class="controls">
                    <div class="input-prepend">
                        <span id="subbranchName" class="add-on">支行</span>
                        <input name="fundSettleSubbranchName" value="${merchant.fundSettleSubbranchName}" class="span3"
                               id="fundSettleSubbranchName" placeholder="" type="text">
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
            <td>
                <label class="control-label"></label>
                <div class="controls">
                    <div class="input-prepend">
                        <span id="linkBankNo" class="add-on">联行号</span>
                        <input name="fundSettleLinkBankNo" value="${merchant.fundSettleLinkBankNo}" class="span3"
                               id="fundSettleLinkBankNo" placeholder="" type="text">
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>

        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">商户实时结算标志<span style="color: red;">*</span></label>
                    <div class="controls">
                        <select name="merchantSettleCycle" class="input-xlarge" id="merchantSettleCycle">
                            <option value="">--请选择--</option>
                            <option
                                    <c:if test="${merchant.merchantSettleCycle == 'T0'}">selected</c:if>
                                    <c:if test="${empty merchant.merchantSettleCycle && op !='add'}">selected</c:if> value="T0">实时结算
                            </option>
                            <option
                                    <c:if test="${merchant.merchantSettleCycle == 'TN'}">selected</c:if> value="TN">非实时结算
                            </option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">生效时间</label>
                    <div class="controls">
                        <label class="control-label" for="activeNow">立即</label>
                        <input name="isFixed" value="1" placeholder="" class="input-xlarge"
                               type="radio"
                               <c:if test="${merchant.isFixed == 1}">checked</c:if>
                               <c:if test="${op == 'add'}">checked</c:if>
                               <c:if test="${empty merchant.isFixed}">checked</c:if> id="activeNow">
                        <label class="control-label" for="activeThan">定时</label>
                        <input name="isFixed" value="0" placeholder="" class="input-xlarge"
                               type="radio"
                               <c:if test="${merchant.isFixed == 0}">checked</c:if> id="activeThan">
                        <input id="activeTime" name="activeTime" type="text" class="input-medium Wdate"
                               value="${activeTime}"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true, minDate:'%y-%M-%d'});"/>

                    </div>
                </div>
            </td>

        </tr>
    </table>
    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>
            <th>证件资料</th>
        </label>
    </div>

    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">营业执照<span style="color: red;"><span style="color: red;"></span></span></label>
                    <div class="controls">

                        <c:if test="${op == 'edit'}">
                            <%--<c:if test="${merchant.blcPath != ''} && ${merchant.blcPath != null}">--%>
                            <label>原图：</label>
                            <img src="${merchant.blcPath}" style="max-width: 100px;max-height: 100px">
                            <%--</c:if>--%>
                        </c:if>

                        <div id="uploader-blcPath">
                            <input type="hidden" value="${merchant.blcPath}" id="blcPath" name="blcPath"/>
                            <!--用来存放item，图片列表fileList-->
                            <div id="fileList_blcPath" class="uploader-list"></div>
                            <div id="filePicker_blcPath" class="filePicker" onclick="getUploadId('blcPath')"
                                 style="width:100px;">选择图片
                            </div>
                        </div>
                    </div>
                </div>
            </td>
            <%--<td>--%>
            <%--<div class="control-group">--%>
            <%--<label class="control-label">税务登记表</label>--%>
            <%--<div class="controls">--%>

            <%--<c:if test="${op == 'edit'}">--%>
            <%--<label>原图：</label>--%>
            <%--<img src="${merchant.taxRegisPath}" style="max-width: 100px;max-height: 100px">--%>
            <%--</c:if>--%>

            <%--<div id="uploader-taxRegisPath">--%>
            <%--<input type="hidden" value="${merchant.taxRegisPath}" id="taxRegisPath" name="taxRegisPath"/>--%>
            <%--<!--用来存放item，图片列表fileList-->--%>
            <%--<div id="fileList_taxRegisPath"></div>--%>
            <%--<div id="filePicker_taxRegisPath" class="filePicker" onclick="getUploadId('taxRegisPath')"--%>
            <%--style="width:100px;">选择图片--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</td>--%>
            <%--<td>--%>
            <%--<div class="control-group">--%>
            <%--<label class="control-label">组织机构代码证</label>--%>
            <%--<div class="controls">--%>
            <%--<c:if test="${op == 'edit'}">--%>
            <%--<label>原图：</label>--%>
            <%--<img src="${merchant.organizationPath}" style="max-width: 100px;max-height: 100px">--%>
            <%--</c:if>--%>
            <%--<div id="uploader-organizationPath">--%>
            <%--<input type="hidden" value="${merchant.organizationPath}" id="organizationPath" name="organizationPath"/>--%>
            <%--<!--用来存放item，图片列表fileList-->--%>
            <%--<div id="fileList_organizationPath"></div>--%>
            <%--<div id="filePicker_organizationPath" class="filePicker"--%>
            <%--onclick="getUploadId('organizationPath')" style="width:100px;">选择图片--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</td>--%>
            <td>
                <div class="control-group">
                    <label class="control-label">商户协议<span style="color: red;"><span style="color: red;"></span></span></label>
                    <div class="controls">
                        <c:if test="${op == 'edit'}">
                            <label>原图：</label>
                            <img src="${merchant.contractFilePath}" style="max-width: 100px;max-height: 100px">
                        </c:if>
                        <div id="uploader-contractFilePath">
                            <input type="hidden" value="${merchant.contractFilePath}" id="contractFilePath"
                                   name="contractFilePath"/>
                            <!--用来存放item，图片列表fileList-->
                            <div id="fileList_contractFilePath" class="uploader-list"></div>
                            <div id="filePicker_contractFilePath" class="filePicker"
                                 onclick="getUploadId('contractFilePath')" style="width:100px;">选择图片
                            </div>
                        </div>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">门牌照/其他</label>
                    <div class="controls">
                        <c:if test="${op == 'edit'}">
                            <label>原图：</label>
                            <img src="${merchant.boardPicPath}" style="max-width: 100px;max-height: 100px">
                        </c:if>
                        <div id="uploader-boardPicPath">
                            <input type="hidden" value="${merchant.boardPicPath}" id="boardPicPath"
                                   name="boardPicPath"/>
                            <!--用来存放item，图片列表fileList-->
                            <div id="fileList_boardPicPath" class="uploader-list"></div>
                            <div id="filePicker_boardPicPath" class="filePicker" onclick="getUploadId('boardPicPath')"
                                 style="width:100px;">选择图片
                            </div>
                        </div>
                    </div>
                </div>
            </td>


        </tr>
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">开户许可证<span style="color: red;"><span
                            style="color: red;"></span></span></label>
                    <div class="controls">
                        <c:if test="${op == 'edit'}">
                            <label>原图：</label>
                            <img src="${merchant.openingPermitPath}" style="max-width: 100px;max-height: 100px">
                        </c:if>
                        <div id="uploader-openingPermitPath">
                            <input type="hidden" value="${merchant.openingPermitPath}" id="openingPermitPath"
                                   name="openingPermitPath"/>
                            <!--用来存放item，图片列表fileList-->
                            <div id="fileList_openingPermitPath" class="uploader-list"></div>
                            <div id="filePicker_openingPermitPath" class="filePicker"
                                 onclick="getUploadId('openingPermitPath')" style="width:100px;">选择图片
                            </div>
                        </div>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">银行卡正面照<span style="color: red;"><span
                            style="color: red;"></span></span></label>
                    <div class="controls">
                        <c:if test="${op == 'edit'}">
                            <label>原图：</label>
                            <img src="${merchant.bankCardFrontPath}" style="max-width: 100px;max-height: 100px">
                        </c:if>
                        <div id="uploader-bankCardFrontPath">
                            <input type="hidden" value="${merchant.bankCardFrontPath}" id="bankCardFrontPath"
                                   name="bankCardFrontPath"/>
                            <!--用来存放item，图片列表fileList-->
                            <div id="fileList_bankCardFrontPath"></div>
                            <div id="filePicker_bankCardFrontPath" class="filePicker"
                                 onclick="getUploadId('bankCardFrontPath')" style="width:100px;">选择图片
                            </div>
                        </div>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">银行账户身份证正面照<span style="color: red;"><span
                            style="color: red;"></span></span></label>
                    <div class="controls">
                        <c:if test="${op == 'edit'}">
                            <label>原图：</label>
                            <img src="${merchant.bankIdcardPath}" style="max-width: 100px;max-height: 100px">
                        </c:if>
                        <div id="uploader-bankIdcardPath">
                            <input type="hidden" value="${merchant.bankIdcardPath}" id="bankIdcardPath"
                                   name="bankIdcardPath"/>
                            <!--用来存放item，图片列表fileList-->
                            <div id="fileList_bankIdcardPath" class="uploader-list"></div>
                            <div id="filePicker_bankIdcardPath" class="filePicker"
                                 onclick="getUploadId('bankIdcardPath')" style="width:100px;">选择图片
                            </div>
                        </div>
                    </div>
                </div>
            </td>

        </tr>
    </table>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>
            <th>平台信息</th>
        </label>
    </div>

    <table class="table">
        <tr>
            <td>
                <label class="control-label">产品名称受控</label>
                <div class="controls">
                    <div class="input-prepend">
                        <select style="width:50px;" name="isProductControl">
                            <option value="0" <c:if test="${merchant.isProductControl == '0'}">selected</c:if>>否
                            </option>
                            <option value="1" <c:if test="${merchant.isProductControl == '1'}">selected</c:if>>是
                            </option>
                        </select>
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">商户状态</label>
                    <div class="controls">
                        <select name="status" id="status">
                            <option value="1">启用</option>
                            <option
                                    <c:if test="${merchant.status == 2}">selected</c:if> value="2">停用
                            </option>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">上级代理商户</label>
                    <div class="controls">
                        <select id="parentId" name="parentId">
                            <option value="0">无</option>
                            <c:forEach items="${mchts}" var="mchtInfo">
                                <option data-mchtStatus="${mchtInfo.status}" data-mchtCode="${mchtInfo.mchtCode}"
                                        data-mchtDescription="${mchtInfo.description}"
                                        <c:if test="${merchant.parentId == mchtInfo.id}">selected</c:if>
                                        value="${mchtInfo.id}">${mchtInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">商户类别<span style="color: red;"></span></label>
                    <div class="controls">
                        <c:choose>
                            <c:when test="${!fn:contains(merchant.signType, '5')}">
                                <label><input name="signType" type="checkbox" value="1" id="payMcht"
                                              onclick="checkPayMcht()"
                                              <c:if test="${fn:contains(merchant.signType, '1')}">checked="checked"
                                        <c:if test="${productCount == 1}"> disabled </c:if>
                                </c:if> />支付商户
                                </label>
                                <label><input name="signType" type="checkbox" value="2" id="requestMcht"
                                              <c:if test="${fn:contains(merchant.signType, '2')}">checked="checked"</c:if> />申报商户
                                </label>
                                <label><input name="signType" type="checkbox" value="3" id="serverMcht"
                                              <c:if test="${fn:contains(merchant.signType, '3')}">checked="checked"</c:if> />服务商
                                </label>
                                <label><input name="signType" type="checkbox" value="4" onclick="checkType()" id="agent"
                                              <c:if test="${fn:contains(merchant.signType, '4')}">checked="checked"</c:if> />代理商
                                </label>

                            </c:when>
                            <c:otherwise>
                                <c:if test="${fn:contains(merchant.signType, '51')}">
                                    <label><input name="signType" type="hidden" value="51">
                                        个人
                                    </label>
                                </c:if>
                                <c:if test="${fn:contains(merchant.signType, '52')}">
                                    <label><input name="signType" type="hidden" value="52">
                                        个体商户
                                    </label>
                                </c:if>
                                <c:if test="${fn:contains(merchant.signType, '53')}">
                                    <label><input name="signType" type="hidden" value="53">
                                        企业
                                    </label>
                                </c:if>
                                <c:if test="${fn:contains(merchant.signType, '54')}">
                                    <label><input name="signType" type="hidden" value="54">
                                        事业单位
                                    </label>
                                </c:if>

                            </c:otherwise></c:choose>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <label class="control-label">是否展示支付结果页</label>
                <div class="controls">
                    <div class="input-prepend">
                        <select style="width:50px;" name="isShowPayResultPage">
                            <option value="0" <c:if test="${merchant.isShowPayResultPage == '0'}">selected</c:if>>否
                            </option>
                            <option value="1" <c:if test="${merchant.isShowPayResultPage == '1'}">selected</c:if>>是
                            </option>
                        </select>
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
            <td>
                <label class="control-label">是否展示支付页</label>
                <div class="controls">
                    <div class="input-prepend">
                        <select style="width:50px;" name="isShowPayPage">
                            <option value="1" <c:if test="${merchant.isShowPayPage == '1'}">selected</c:if>>是
                            </option>
                            <option value="0" <c:if test="${merchant.isShowPayPage == '0'}">selected</c:if>>否
                            </option>
                        </select>
                    </div>
                    <p class="help-block"></p>
                </div>
            </td>
            <td colspan="3">
                <div class="control-group">
                    <label class="control-label" for="mchtPropertyTag">商户标签（半角逗号分隔）</label>
                    <div class="controls">
                        <textarea name="mchtPropertyTag" placeholder="" style="width:350px;" id="mchtPropertyTag"
                                  rows="3">${merchant.mchtPropertyTag}</textarea>
                    </div>
                </div>
            </td>

        </tr>
    </table>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>
            <th>商户充值信息</th>
        </label>
    </div>
    <table class="table">
        <tr>
            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">运营免审时间段</label>
                    <div class="controls">
                        <div class="controls">
                            <div class="control-group">
                                <div class="controls" style="    display: inline-block;">
                                    开始时间
                                    <input type="text" id="exemptReviewStartTime" value="${merchant.exemptReviewStartTime}" name="exemptReviewStartTime"
                                           onclick="WdatePicker({skin:'whyGreen',dateFmt:'H:mm:ss'})" class="Wdate"/>
                                </div>
                                <div class="controls" style="    display: inline-block;">
                                    结束时间
                                    <input type="text" id="exemptReviewEndTime" value="${merchant.exemptReviewEndTime}" name="exemptReviewEndTime"
                                           onclick="WdatePicker({skin:'whyGreen',dateFmt:'H:mm:ss'})" class="Wdate"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">运营免审金额<span style="color: red;"></span></label>
                    <div class="controls">
                        <input name="exemptReviewAmount" value="${merchant.exemptReviewAmount}" placeholder="" class="input-xlarge" type="text"
                               maxlength="64">
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">公司收款账户名<span style="color: red;"><span style="color: red;">*</span></span></label>
                    <div class="controls">
                        <input name="compReceiptAcctName" value="${merchant.compReceiptAcctName}" placeholder="" class="input-nomal"
                               type="text" />
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">公司收款账户号<span style="color: red;"><span style="color: red;">*</span></span></label>
                    <div class="controls">
                        <input name="compReceiptAcctNo" value="${merchant.compReceiptAcctNo}" placeholder="" class="input-nomal"
                               type="text" />
                    </div>
                </div>
            </td>
            <td >
                <div class="control-group">
                    <label class="control-label">商户汇款专属尾号 (单位为分:例 0.19, 填写 19)</label>
                    <div class="controls">
                        <input name="mchtRemittanceAmountSuffix" type="number" value="${merchant.mchtRemittanceAmountSuffix}" placeholder="" class="input-nomal"
                               type="text" />
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