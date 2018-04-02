<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>发起代付</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">
        table {
            width: 98%;
            margin: 10px auto;
            font: Georgia 11px;
            font-size: 12px;
            color: #333333;
            text-align: center;
            border-collapse: collapse;
        }

        table td {
            height: 25px;
            padding: 2px;
            border: 1px solid #dadade;
        }
    </style>
</head>
<body>
<tags:message content="${message}" type="${messageType}"/>

<span style="font-size:15px;padding-left:15px;">商户名称：${mchtName}&nbsp;&nbsp;&nbsp;&nbsp;账户余额：<fmt:formatNumber type="number" value="${balance*0.01}" maxFractionDigits="2"/>元</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<span style="color: red;font-size:8px;">提示：账户余额小于35.0元时不能代付</span>
<form action="${ctx}/proxy/commitBatch" method="post" enctype="multipart/form-data">
<table width="100%" border="1" cellspacing="0" cellpadding="1">
    <tr>
        <td width="20%" align="center" colspan="2"><b>代付文件上传</b></td>
    </tr>
    <tr>
        <td align="right" width="40%">代付模版下载：</td>
        <td align="left">
            <a href="${ctxStatic}/doc/template.xls">Excel模版下载</a>
        </td>
    </tr>
    <tr>
        <td align="right" width="40%">
            选择上传文件：
        </td>
        <td align="left">
            <input type="file"  name="file" />
        </td>
    </tr>
    <tr>
        <td width="20%" align="center" colspan="2"><b>代付服务说明</b></td>
    </tr>
    <tr>
        <td colspan="4">
                <table >
                    <tr>
                        <td align="center">
                            服务标准
                        </td>
                        <td align="center">
                            支持银行
                        </td>
                        <td align="center">
                            收款帐户信息
                        </td>
                    </tr>
                    <tr>
                        <td align="left">
                            7*24小时支持付款<br/>
                            实时代付<br/>
                            仅支持付款至对私账户<br/>
                            支持工作日、节假日
                        </td>
                        <td align="center">
                            支持主流的17家银行
                        </td>
                        <td align="center">
                            银行帐户<br/>
                            银行户名<br/>
                            银行名称
                        </td>
                    </tr>
                    <tr>
                        <td align="left" colspan="3">
                            说明1:<br/>
                            支付17家主流银行包括:<br/>
                            1.中国农业银行；2.中国建设银行；3.中国工商银行；4.中国银行；5.招商银行 6.中国光大银行；
                            7.中国民生银行；8.交通银行；9.兴业银行；10.中信银行；11.中国邮政储蓄银行；12.浙商银行；
                            13.华夏银行；14.广发银行；15.上海浦东发展银行；16.平安银行；17.渤海银行
                        </td>
                    </tr>
                    <tr>
                        <td align="left" colspan="3">
                            说明2：单个Excel文件最多支持100笔付款
                        </td>
                    </tr>
                    <tr>
                        <td align="left" colspan="3">
                            说明3：付款有金额限制，单笔5w，单卡每日20万
                        </td>
                    </tr>
                    <tr>
                        <td align="left" colspan="3">
                            说明4：如果要素齐全，一般15分钟到账
                        </td>
                    </tr>
                   <%-- <tr>
                        <td align="left" colspan="3">
                            更多说明：<a href="${ctxStatic}/doc/readme.pdf">商户操作手册下载</a>
                        </td>
                    </tr>--%>
                </table>
        </td>
    </tr>
    <tr>
        <td colspan="4">

            <input id="btnSubmit" class="btn btn-primary" type="submit" value="提交代付文件" <c:if test="${balance lt 3500}">disabled</c:if>/>
        </td>
    </tr>
</table>
</form>
</body>
</html>
