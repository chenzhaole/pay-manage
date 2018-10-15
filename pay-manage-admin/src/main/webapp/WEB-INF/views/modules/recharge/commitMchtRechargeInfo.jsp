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
    <script type="text/javascript">
        function chongZhiOnClick(val) {
            if(val == 1){
                document.getElementById("huikuang_id").style="display:block";
                document.getElementById("zhifu_id").style="display:none";
            }else if(val == 2){
                document.getElementById("huikuang_id").style="display:none";
                document.getElementById("zhifu_id").style="display:block";
            }else{
                document.getElementById("huikuang_id").style="display:block";
                document.getElementById("zhifu_id").style="display:none";
            }
        }
    </script>
    <script type="text/javascript" src="${ctxStatic}/js/img_upload/pictureHandle.js"/>
    <script type="text/javascript" src="${ctxStatic}/js/img_upload/tools.js"/>

</head>
<body>
<div class="breadcrumb">
    <label>
        <th><a href="#">代付管理</a> > <a href="#"><b>发起充值</b></a></th>
    </label>
</div>
<tags:message content="${message}" type="${messageType}"/>

<form action="${ctx}/mchtRecharge/commitBatch" method="post" enctype="multipart/form-data">
    <span style="font-size:15px;padding-left:15px;">商户名称：${mchtName}&nbsp;&nbsp;&nbsp;&nbsp;账户余额：<fmt:formatNumber type="number" value="${balance*0.01}" maxFractionDigits="2"/>元</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <div style="border: solid 1px #7D7D7D; padding: 2% 2%;">
        <span style="margin-left: 5%">
            充值方式:
        </span>
        <span style="margin-left: 5%">
            <input type="radio" checked="checked" name="rechargeType" onclick="chongZhiOnClick('1')" value="1"/>&nbsp;&nbsp;汇款充值
        </span>
        <span style="margin-left: 5%">
            <input type="radio" name="rechargeType" value="2" onclick="chongZhiOnClick('2')"/>&nbsp;&nbsp;支付充值
        </span>
    </div>
    <div id="huikuang_id">
            <br/>
            <br/>
            <div id="huikuang_content_id" style="width: 600px; margin: 0 auto;">
                充值金额: <input type="text" name=""/> &nbsp;元
                <div>
                    汇款凭证:
                </div>
                <div>
                    <div style="width: 201px; height: 201px; border: 1px solid #000;   position: relative; left: 65px;">
                        <img src="" id="preview" />
                    </div>
                    <div style="position: relative; left: 290px; top: -50px; background-color: #0D8BBD; color: #ffffff; width: 90px">
                        <label>
                            上传/更改图片<input type="file" id="upFile" name="" style="display:none" />
                        </label>
                    </div>
                    <div style="position: relative; left: 290px; top: -50px;">
                        (图片大小不超过500K, 支持JPG,JPEG,PNG格式)
                    </div>
                </div>
            </div>

            <table width="100%" border="1" cellspacing="0" cellpadding="1">
                <tr >
                    <td colspan="2">
                        汇款充值服务说明
                    </td>
                </tr>
                <tr>
                    <td>
                        服务标准
                    </td>
                    <td>
                        使用说明
                    </td>
                </tr>
                <tr>
                    <td>
                        汇款充值费率: ${rechargeConfig.tradeFeeAmount}%
                    </td>
                    <td style="text-align: center">
                        <div style="text-align: left; margin-left: 38%;">
                            1.收款账户:${rechargeConfig.compReceiptAcctName}
                        </div>
                        <div style="text-align: left; margin-left: 38%;">
                            2.收款银行:${rechargeConfig.compReceiptAcctNo}
                        </div>
                        <div style="text-align: left; margin-left: 38%;">
                            3.金额需大于1000元
                        </div>
                        <div style="text-align: left; margin-left: 38%;">
                            4.图片大小不超过500K
                        </div>
                        <div style="text-align: left; margin-left: 38%;">
                            5.专属汇款尾数${rechargeConfig.mchtRemittanceAmountSuffix}
                        </div>
                    </td>
                </tr>
                </tr>
            </table>

    </div>
    <div id="zhifu_id" style="display: none;">
            <br/>
            <br/>
            <div id="zhifu_content_id" style="width: 600px; margin: 0 auto;">
                充值金额: <input type="text" name=""/> &nbsp;元
            </div>

            <table width="100%" border="1" cellspacing="0" cellpadding="1">
                <tr >
                    <td colspan="2">
                        支付充值服务说明
                    </td>
                </tr>
                <tr>
                    <td>
                        服务标准
                    </td>
                    <td>
                        使用说明
                    </td>
                </tr>
                <tr>
                    <td>
                        <div>
                            支付充值费率: ${rechargeConfig.compReceiptAcctName}%
                        </div>
                        <div>
                            支付产品:
                        </div>
                    </td>
                    <td>
                        1.金额需大于1000元
                    </td>
                </tr>
                </tr>
            </table>
    </div>
</form>
</body>


</html>
