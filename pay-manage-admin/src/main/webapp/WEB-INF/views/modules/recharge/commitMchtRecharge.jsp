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
                $("#chongzhi_recharge_id").attr("checked","checked");
                $("#pay_recharge_id").removeAttr("checked");

                //document.getElementById("huikuang_id").style="display:block";
                //document.getElementById("zhifu_id").style="display:none";
                $("#huikuang_id").css("display","block");
                $("#zhifu_id").css("display","none");
            }else if(val == 2){
                $("#pay_recharge_id").attr("checked","checked");
                $("#chongzhi_recharge_id").removeAttr("checked");

                //document.getElementById("huikuang_id").style="display:none";
                //document.getElementById("zhifu_id").style="display:block";
                $("#huikuang_id").css("display","none");
                $("#zhifu_id").css("display","block");
            }else{
                $("#huikuang_id").css("display","block");
                $("#zhifu_id").css("display","none");
                //document.getElementById("huikuang_id").style="display:block";
                //document.getElementById("zhifu_id").style="display:none";
            }
        }


        function submitRecharge() {

            var czFreeRate = '${hkPlatFeerate.feeRate}';
            var zfFreeRate = '${czPlatFeerate.feeRate}';



            //1 汇款充值  2 支付充值
            //充值类型
            var rechargeType = $("input[name='rechargeType']:checked").val();

            var rechargeAmount = 0;
            var payAmount = 0;

            if(1 == rechargeType){
                if(czFreeRate == null || czFreeRate == '' || czFreeRate =='0.00'){
                    alert('未配置充值费率，请联系客服');
                    return false;
                }

                //充值金额
                rechargeAmount = $("#rechargeAmountId").val();
                if(Number(rechargeAmount) < 1000){
                    alert('充值金额最低1000元');
                    return false;
                }
                //判断尾数
                if(rechargeAmount!= null){
                    var amountSuffix = rechargeAmount.split(".");
                    if(amountSuffix.length ==2 && amountSuffix[1] == '${rechargeConfig.mchtRemittanceAmountSuffix}'){
                    }else{
                        if(confirm('您输入的充值金额尾数与您的专属尾数不同,请修改尾数,若尾数不符,为保障您的充值能及时处理,请提交完成后联系客服')){

                        }else{
                            return false;
                        }
                    }
                }

                //图片
                /*
                var upFileVal = $("#upFile").val();
                if(upFileVal == null || upFileVal == ''){
                    alert('请上传支付凭证.');
                    return false;
                }*/
                $("#proofImage").val($("#preview").attr("src"));
                $("#rechargeFromId").submit();
            }else if(2 == rechargeType){
                var payFalg = "${payFalg}";
                if(zfFreeRate == null || zfFreeRate == '' || zfFreeRate == '0.00'){
                    alert('未配置支付费率，请联系客服');
                    return false;
                }
                if(payFalg == "false"){
                    alert("未配置产品,请联系客服");
                    return false;
                }


                //支付金额
                payAmount = $("#payAmountId").val();
                if(Number(payAmount) < 1000){
                    alert('支付金额最低1000元');
                    return false;
                }
                $("#rechargeFromId").attr("action", "${ctx}/mchtRecharge/commitMchtRechargePayInfo")
                $("#rechargeFromId").submit();
            }else{
                alert('请选择支付类型');
                return false;
            }

        }



        //限制图片大小.
        function limitJpg(fileId, imgId, urlId) {
            var max_size = 500;// 300k
            var tmpFile = document.getElementById(fileId);
            if (tmpFile.value == '' || tmpFile.value == null) {
                alert("请上传图片");
                return false;
            }
            if (!/\.(jpg|jpeg|png|JPEG|JPG|PNG)$/.test(tmpFile.value)) {
                alert("图片类型必须是[jpeg,jpg,png]中的一种");
                tmpFile.value = "";
                return false;
            } else {
                var fileData = tmpFile.files[0];
                var size = fileData.size;
                if (size > max_size * 1024) {
                    alert("图片大小不能超过500k");
                    tmpFile.value = "";
                } else {
                    ajaxFileUpload(fileId, imgId, urlId);
                }
            }
        }
    </script>

</head>
<body>



<tags:message content="${message}" type="${messageType}"/>
<div class="breadcrumb">
    <label>
        <th><a href="#">代付管理</a> > <a href="#"><b>发起充值</b></a></th>
    </label>
</div>
<form id="rechargeFromId" action="${ctx}/mchtRecharge/commitMchtRechargeInfo" method="post" enctype="multipart/form-data">
    <span style="font-size:15px;padding-left:15px;">商户名称：${mchtName}&nbsp;&nbsp;&nbsp;&nbsp;账户余额：<fmt:formatNumber type="number" value="${balance*0.01}" maxFractionDigits="2"/>元</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <div style="border: solid 1px #7D7D7D; padding: 2% 2%;">
        <span style="margin-left: 5%">
            充值方式:
        </span>
        <span style="margin-left: 5%">
            <input type="radio" id="chongzhi_recharge_id" checked="checked" name="rechargeType" onclick="chongZhiOnClick('1')" value="1"/>&nbsp;&nbsp;汇款充值
        </span>
        <span style="margin-left: 5%">
            <input type="radio" id="pay_recharge_id" name="rechargeType" value="2" onclick="chongZhiOnClick('2')"/>&nbsp;&nbsp;支付充值
        </span>
    </div>
    <div id="huikuang_id">
            <br/>
            <br/>
            <div id="huikuang_content_id" style="width: 600px; margin: 0 auto;">
                充值金额: <input type="number"  id="rechargeAmountId" name="rechargeAmount"/> &nbsp;元
                <label style="color: red">(提示:您输入的金额尾数需为${rechargeConfig.mchtRemittanceAmountSuffix} :如 1000.${rechargeConfig.mchtRemittanceAmountSuffix})</label>
                <br/>
                <br/>

                <br/>
                汇款留言: <textarea id="mchtMessage" name="mchtMessage" maxlength="50"></textarea>
                &nbsp;&nbsp;<label style="color: red">最多输入50个字符</label>

                <br/>
                <br/>


                <div>
                    汇款凭证:
                </div>
                <div>
                    <input type="hidden" name="proofImage" id="proofImage" value=""/>
                    <div style="width:400px;height:200px;border: 1px solid #000;   position: relative; left: 65px;" id="showimage">

                    </div>
                    <!--
                    <div style="position: relative; left: 290px; top: -50px; background-color: #0D8BBD; color: #ffffff; width: 90px">
                        <label>
                            上传/更改图片<input type="file" id="upFile" name="proofImage" style="display:none" onchange="limitJpg('upFile')" />
                        </label>
                    </div>
                    <div style="position: relative; left: 290px; top: -50px;">
                        (图片大小不超过500K, 支持JPG,JPEG,PNG格式)
                    </div>
                    -->
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
                        汇款充值费率: ${hkPlatFeerate.feeRate}‰
                    </td>
                    <td style="text-align: center">
                        <div style="text-align: left; margin-left: 38%;">
                            1.公司收款账户名:${rechargeConfig.compReceiptAcctName}
                        </div>
                        <div style="text-align: left; margin-left: 38%;">
                            2.公司收款账户号:${rechargeConfig.compReceiptAcctNo}
                        </div>
                        <div style="text-align: left; margin-left: 38%;">
                            3.汇款金额最低1000元
                        </div>
                        <div style="text-align: left; margin-left: 38%;">
                            4.图片大小不超过500K
                        </div>
                        <div style="text-align: left; margin-left: 38%;">
                            5.商户汇款专属尾数 :${rechargeConfig.mchtRemittanceAmountSuffix}
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
                充值金额: <input type="number" id="payAmountId" name="payAmount"/> &nbsp;元
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
                            支付充值费率: ${czPlatFeerate.feeRate}‰
                        </div>
                    </td>
                    <td>
                        1.支付金额最低1000元
                    </td>
                </tr>
                </tr>
            </table>
    </div>


    <div style="margin-left: 50%;">
        <input type="button" onclick="submitRecharge()" value="确认提交"/>
    </div>
</form>
<script src="${ctxStatic}/js/paste.js"></script>
<script>
    // 初始化插件，传入展示图片标签的jq对象
    upImageInit($("#showimage"));
</script>
</body>


</html>
