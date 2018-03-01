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

</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">交易管理</a> > <a href="#"><b>商户入驻流水详情</b></a></th>
    </label>
</div>
<div class="breadcrumb">
<input id="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);" name="btnCancel"/>
</div>
<table class="table table-striped table-bordered table-condensed">
    <tr>
        <td colspan="4" style="text-align: center;"><b>入驻结果</b></td>
    </tr>
    <tr>
        <td width="25%"><b>入驻状态</b></td>
        <td width="25%"> ${fns:getDictLabel(registeOrder.status,'mcht_registe_status' , '')}</td>
        <td width="25%"><b>申报时间</b></td>
        <td width="25%"><fmt:formatDate value="${registeOrder.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td></td>
    </tr>
    <tr>
        <td width="25%"><b>平台订单号</b></td>
        <td width="25%" colspan="3">${registeOrder.platOrderId}</td>
    </tr>
</table>
<table class="table table-striped table-bordered table-condensed">
    <tr>
        <td colspan="4" style="text-align: center;"><b>上游响应</b></td>
    </tr>
    <tr>
        <td width="25%"><b>上游通道代码</b></td>
        <td width="25%"> ${registeOrder.chanCode}</td>
        <td width="25%"><b>通道支付方式银行</b></td>
        <td width="25%"> ${chanMchtPayType}</td>
    </tr>
    <tr>
        <td width="25%"><b>上游响应详情</b></td>
        <td width="25%"> ${registeOrder.chan2platResMsg}</td>
        <td width="25%"><b>上游订单号</b></td>
        <td width="25%"> ${registeOrder.chanOrderId}</td>
    </tr>

</table>
<table class="table table-striped table-bordered table-condensed">
    <tr>
        <td colspan="4" style="text-align: center;"><b>基本信息</b></td>
    </tr>
    <tr>
        <td width="25%"><b>商户订单号</b></td>
        <td width="25%"> ${registeOrder.mchtOrderId}</td>
        <td width="25%"><b>商户名称</b></td>
        <td width="25%"> ${registeOrder.name}</td>
    </tr>
    <%--<tr>--%>
        <%--<td width="25%"><b>商户简称</b></td>--%>
        <%--<td width="25%" colspan="3"> ${registeOrder.chan2platResMsg}</td>--%>
    <%--</tr>--%>
    <tr>
        <td width="25%"><b>省</b></td>
        <td width="25%"> ${registeOrder.province}</td>
        <td width="25%"><b>市</b></td>
        <td width="25%"> ${registeOrder.city}</td>
    </tr>
    <tr>
        <td width="25%"><b>区</b></td>
        <td width="25%"> ${registeOrder.district}</td>
        <td width="25%"><b>地址</b></td>
        <td width="25%"> ${registeOrder.address}</td>
    </tr>
    <tr>
        <td width="25%"><b>手机号</b></td>
        <td width="25%"> ${registeOrder.tel}</td>
        <td width="25%"><b>邮箱</b></td>
        <td width="25%"> ${registeOrder.email}</td>
    </tr>

</table>
<table class="table table-striped table-bordered table-condensed">
    <tr>
        <td colspan="4" style="text-align: center;"><b>经营信息</b></td>
    </tr>
    <tr>
        <td width="25%"><b>ip</b></td>
        <td width="25%"> ${registeOrder.mchtIp}</td>
        <td width="25%"><b>商户类型</b></td>
        <td width="25%">
            <c:if test="${registeOrder.mchtType=='1'}">支付商户</c:if><c:if test="${registeOrder.mchtType=='2'}">申报商户</c:if>
            <c:if test="${registeOrder.mchtType=='3'}">服务商</c:if><c:if test="${registeOrder.mchtType=='4'}">代理商</c:if>
            <c:if test="${registeOrder.mchtType=='51'}">个人</c:if><c:if test="${registeOrder.mchtType=='52'}">个体商户</c:if>
            <c:if test="${registeOrder.mchtType=='53'}">企业</c:if><c:if test="${registeOrder.mchtType=='54'}">事业单位</c:if>
        </td>
    </tr>
    <tr>
        <td width="25%"><b>营业执照类型</b></td>
        <td width="25%" colspan="3">
            <c:if test="${registeOrder.businessLicenseType=='1'}">营业执照</c:if>
            <c:if test="${registeOrder.businessLicenseType=='2'}">营业执照（三证合一）</c:if>
            <c:if test="${registeOrder.businessLicenseType=='3'}">事业单位法人证书</c:if>
        </td>
    </tr>
    <tr>
    <td width="25%"><b>法人姓名</b></td>
    <td width="25%"> ${registeOrder.legalPerson}</td>
    <td width="25%"><b>法人证件类型</b></td>
    <td width="25%">
        <c:if test="${registeOrder.legalCardType=='1'}">身份证</c:if><c:if test="${registeOrder.legalCardType=='2'}">护照</c:if>
        <c:if test="${registeOrder.legalCardType=='3'}">军官证</c:if><c:if test="${registeOrder.legalCardType=='4'}">士兵证</c:if>
        <c:if test="${registeOrder.legalCardType=='5'}">回乡证</c:if><c:if test="${registeOrder.legalCardType=='6'}">临时身份证</c:if>
        <c:if test="${registeOrder.legalCardType=='7'}">户口簿</c:if><c:if test="${registeOrder.legalCardType=='8'}">警官证</c:if>
        <c:if test="${registeOrder.legalCardType=='9'}">台胞证</c:if><c:if test="${registeOrder.legalCardType=='11'}">其他</c:if>
    </td>
    <tr>
        <td width="25%"><b>法人证件号码</b></td>
        <td width="25%"> ${registeOrder.legalCardNo}</td>
        <td width="25%"><b>客服信息</b></td>
        <td width="25%">
            ${registeOrder.servicePhone}
        </td>
    </tr>
</table>
<table class="table table-striped table-bordered table-condensed">
    <tr>
        <td colspan="4" style="text-align: center;"><b>结算账户信息</b></td>
    </tr>
    <tr>
        <td width="25%"><b>结算账户所属银行编码</b></td>
        <td width="25%"> ${registeOrder.settleBankNo}</td>
        <td width="25%"><b>银行卡类型</b></td>
        <td width="25%"> <c:if test="${registeOrder.settleCardType=='1'}">借记卡</c:if><c:if test="${registeOrder.settleCardType=='2'}">信用卡</c:if></td>
    </tr>
    <%--<tr>--%>
        <%--<td width="25%">CVV</td>--%>
        <%--<td width="25%"> ${registeOrder.settleCardCvv}</td>--%>
        <%--<td width="25%">信用卡有效期</td>--%>
        <%--<td width="25%"> ${registeOrder.settleCard}</td>--%>
    <%--</tr>--%>
    <tr>
        <td width="25%"><b>银行卡预留手机号</b></td>
        <td width="25%"> ${registeOrder.settleBankMobile}</td>
        <td width="25%"><b>结算账户账号（卡号）</b></td>
        <td width="25%"> ${registeOrder.settleBankAccountNo}</td>
    </tr>
    <tr>
        <td width="25%"><b>结算银行账户户名</b></td>
        <td width="25%">${registeOrder.settleAccountName} </td>
        <td width="25%"><b>结算卡开户行名称</b></td>
        <td width="25%"> ${registeOrder.settleBankName}</td>
    </tr>
    <tr>
        <td width="25%"><b>账户类别</b></td>
        <td width="25%">
            <c:if test="${registeOrder.settleBankAccountType=='1'}">对公</c:if>
            <c:if test="${registeOrder.settleBankAccountType=='2'}">对私</c:if>
        </td>
        <td width="25%"><b>联行号</b></td>
        <td width="25%"> ${registeOrder.settleLineCode}</td>
    </tr>
    <tr>
        <td width="25%"><b>开户行省</b></td>
        <td width="25%"> ${registeOrder.settleBankProvince}</td>
        <td width="25%"><b>开户行市</b></td>
        <td width="25%"> ${registeOrder.settleBankCity}</td>
    </tr>

</table>
<table class="table table-striped table-bordered table-condensed">
    <tr>
        <td colspan="4" style="text-align: center;"><b>费率信息</b></td>
    </tr>
    <tr>
        <td width="25%"><b>商户类型</b></td>
        <td width="25%">
            <c:if test="${registeOrder.bankDmType=='1'}">无积分</c:if><c:if test="${registeOrder.bankDmType=='2'}">有积分</c:if>
            <c:if test="${registeOrder.bankDmType=='3'}">封顶</c:if>
        </td>
        <td width="25%"><b>费率类型</b></td>
        <td width="25%">
            <c:if test="${registeOrder.bankRateType=='1'}">单笔</c:if>
            <c:if test="${registeOrder.bankRateType=='2'}">比率</c:if>
            <c:if test="${registeOrder.bankRateType=='3'}">混合</c:if>
        </td>
    </tr>
    <tr>
        <td width="25%"><b>费率（‰）</b></td>
        <td width="25%" colspan="3">
            ${registeOrder.bankRate}
        </td>
    </tr>
</table>
</body>
</html>
