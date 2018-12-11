<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, maximum-scale=1.0, minimum-scale=1.0, initial-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="renderer" content="webkit">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta content="telephone=no" name="format-detection">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <title></title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/cashierBank.css">
    <script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
    <script type="text/javascript">
        var isReSubmit =false;
        $(document).ready(function(){
            if("${bankCodes}" == ""){
                $(".no_bank").css('display','');
                return ;
            }
            var serverBankCodes ="${bankCodes}".split(",");
            var localBankCodes =$("ul").find("input");

            for(var i=0;i<localBankCodes.length;i++){
                var localBankCode =localBankCodes[i].value;
                for(var j=0;j<serverBankCodes.length;j++){
                    if(localBankCode == serverBankCodes[j]){
                        $("#"+localBankCode).parent().css('display','');
                        break;
                    }
                }
            }
            $('body').removeClass('is-loading');
            $(".curtain").remove();

        });

        function submit(bankCode){
            if(!isReSubmit){
                isReSubmit =true;
                $.ajax({
                    type: "post",
                    url: $("#bankSubmit").attr("action"),
                    data: {"bankCode":bankCode},
                    dataType:"json",
                    async : false,
                    success: function (jsonData) {//data为返回json数据
                        if(jsonData.respCode == "0000"){
                            var data =eval("("+jsonData.data+")");
                            var clientPayWay =data.clientPayWay;
                            var payInfo =data.payInfo;
                            if(clientPayWay == "08"){
                                location.href =payInfo;
                            }else if(clientPayWay =="09"){
                                $("#payInfoForm").html(payInfo);
                                $("#payInfoForm form")[0].submit();
                            }else {
                                alert("返回参数有误");
                                return;
                            }

                        }else{
                            alert("错误码：["+jsonData.respCode+"],错误信息：["+jsonData.respMsg+"]");
                        }
                    }
                });
            }else {
                alert("重复提交，请重新下单");
            }
        };

    </script>
</head>
<body>
    <div id ="payInfoForm"></div>
    <form id ="bankSubmit" action ="${ctx}/gateway/cashier/platPcCall/${mchtId}/${mchtOrderId}/${payType}/${extraData}" method="post">
    <div class="order_info center">
        <h5 class="tips" >请及时付款，以便订单尽快处理！</h5>
        <div class="info_wrap">
            <p>订单编号：${mchtOrderId}</p>
            <div class="clearFloat">
                <p class="FL">商品名称：${goods}</p>
                <p class="FR">应付金额：<span class="fc_red">￥${amount}</span></p>
            </div>
        </div>
    </div>
    <div class="holdline center"></div>
    <div class="way_box center">
        <p class="way_title">请选择支付方式</p>
        <button class="wy_btn" type="button">网银支付</button>
        <ul class="bank_list">
            <!-- 循环 li 标签 -->
            <li style="display: none"><input id ="ICBC" type="hidden" name="bankCode" value="ICBC"/><a href="#" onclick="submit('ICBC')"><img src="${ctxStatic}/bankLogo/zhongguogongshangyinhang.png" alt="工商银行"></a></li>
            <li style="display: none"><input id ="CCB" type="hidden" name="bankCode" value="CCB"/><a href="#" onclick="submit('CCB')"><img src="${ctxStatic}/bankLogo/zhongguojiansheyinhang.png" alt="建设银行"></a></li>
            <li style="display: none"><input id ="CMB" type="hidden" name="bankCode" value="CMB"/><a href="#" onclick="submit('CMB')"><img src="${ctxStatic}/bankLogo/zhaoshangyinhang.png" alt="招商银行"></a></li>
            <li style="display: none"><input id ="ABC" type="hidden" name="bankCode" value="ABC"/><a href="#" onclick="submit('ABC')"><img src="${ctxStatic}/bankLogo/zhongguonongyeyinhang.png" alt="农业银行"></a></li>
            <li style="display: none"><input id ="COMM" type="hidden" name="bankCode" value="COMM"/><a href="#" onclick="submit('COMM')"><img src="${ctxStatic}/bankLogo/jiaotongyinhang-3.png" alt="交通银行"></a></li>
            <li style="display: none"><input id ="CGB" type="hidden" name="bankCode" value="CGB"/><a href="#" onclick="submit('CGB')"><img src="${ctxStatic}/bankLogo/guangfayinhang.png" alt="广发银行"></a></li>
            <li style="display: none"><input id ="BOC" type="hidden" name="bankCode" value="BOC"/><a href="#" onclick="submit('BOC')"><img src="${ctxStatic}/bankLogo/zhongguoyinhang.png" alt="中国银行"></a></li>
            <li style="display: none"><input id ="CMBC" type="hidden" name="bankCode" value="CMBC"/><a href="#" onclick="submit('CMBC')"><img src="${ctxStatic}/bankLogo/zhongguominshengyinhang.png" alt="民生银行"></a></li>
            <li style="display: none"><input id ="CIB" type="hidden" name="bankCode" value="CIB"/><a href="#" onclick="submit('CIB')"><img src="${ctxStatic}/bankLogo/xingyeyinhang.png" alt="兴业银行"></a></li>
            <li style="display: none"><input id ="CEB" type="hidden" name="bankCode" value="CEB"/><a href="#" onclick="submit('CEB')"><img src="${ctxStatic}/bankLogo/zhongguoguangdayinhang.png" alt="光大银行"></a></li>
            <li style="display: none"><input id ="PSBC" type="hidden" name="bankCode" value="PSBC"/><a href="#" onclick="submit('PSBC')"><img src="${ctxStatic}/bankLogo/zhongguoyouzhengchuxuyinhang.png" alt="邮政银行"></a></li>
            <li style="display: none"><input id ="CITIC" type="hidden" name="bankCode" value="CITIC"/><a href="#" onclick="submit('CITIC')"><img src="${ctxStatic}/bankLogo/zhongxinyinhang.png" alt="中信银行"></a></li>
            <li style="display: none"><input id ="BKSH" type="hidden" name="bankCode" value="BKSH"/><a href="#" onclick="submit('BKSH')"><img src="${ctxStatic}/bankLogo/shanghaiyinhang.png" alt="上海银行"></a></li>
            <li style="display: none"><input id ="SPDB" type="hidden" name="bankCode" value="SPDB"/><a href="#" onclick="submit('SPDB')"><img src="${ctxStatic}/bankLogo/pufa.png" alt="浦发银行"></a></li>
            <li style="display: none"><input id ="BOBJ" type="hidden" name="bankCode" value="BOBJ"/><a href="#" onclick="submit('BOBJ')"><img src="${ctxStatic}/bankLogo/beijingyinhang.png" alt="北京银行"></a></li>
            <li style="display: none"><input id ="PINGANBK" type="hidden" name="bankCode" value="PINGANBK"/><a href="#" onclick="submit('PINGANBK')"><img src="${ctxStatic}/bankLogo/pinganyinhang.png" alt="平安银行"></a></li>
            <li style="display: none"><input id ="NJCB" type="hidden" name="bankCode" value="NJCB"/><a href="#" onclick="submit('NJCB')"><img src="${ctxStatic}/bankLogo/nanjingyinhang.png" alt="南京银行"></a></li>
            <li style="display: none"><input id ="HZCB" type="hidden" name="bankCode" value="HZCB"/><a href="#" onclick="submit('HZCB')"><img src="${ctxStatic}/bankLogo/hangzhouyinhang.png" alt="杭州银行"></a></li>
            <li style="display: none"><input id ="SHRCB" type="hidden" name="bankCode" value="SHRCB"/><a href="#" onclick="submit('SHRCB')"><img src="${ctxStatic}/bankLogo/shanghainongshangyinhang.png" alt="上海农商银行"></a></li>
            <li style="display: none"><input id ="BOCD" type="hidden" name="bankCode" value="BOCD"/><a href="#" onclick="submit('BOCD')"><img src="${ctxStatic}/bankLogo/chengduyinhang.png" alt="成都银行"></a></li>
            <li style="display: none"><input id ="CQRCB" type="hidden" name="bankCode" value="CQRCB"/><a href="#" onclick="submit('CQRCB')"><img src="${ctxStatic}/bankLogo/chongqingnongcunshangyeyinhang.png" alt="重庆农村商业银行"></a></li>
            <li style="display: none"><input id ="XMCB" type="hidden" name="bankCode" value="XMCB"/><a href="#" onclick="submit('XMCB')"><img src="${ctxStatic}/bankLogo/xiamenyinhang.png" alt="厦门银行"></a></li>
            <li style="display: none"><input id ="BJRCB" type="hidden" name="bankCode" value="BJRCB"/><a href="#" onclick="submit('BJRCB')"><img src="${ctxStatic}/bankLogo/beijingnongshangyinhang.png" alt="北京农商银行"></a></li>
            <li style="display: none"><input id ="QDCB" type="hidden" name="bankCode" value="QDCB"/><a href="#" onclick="submit('QDCB')"><img src="${ctxStatic}/bankLogo/qingdaoyinhang.png" alt="青岛银行"></a></li>
            <li style="display: none"><input id ="CZCB" type="hidden" name="bankCode" value="CZCB"/><a href="#" onclick="submit('CZCB')"><img src="${ctxStatic}/bankLogo/zhejiangchouzhoushangye.png" alt="浙江稠州商业银行"></a></li>
            <li style="display: none"><input id ="NBCB" type="hidden" name="bankCode" value="NBCB"/><a href="#" onclick="submit('NBCB')"><img src="${ctxStatic}/bankLogo/ningboyinhang.png" alt="宁波银行"></a></li>

            <!-- 没有支持的银行显示此标签 -->
            <li class="no_bank" style="display:none;">暂无支持银行</li>
        </ul>
    </div>
        <input type="hidden" name="mchtOrderId" value="${mchtOrderId}"/>
        <input type="hidden" name="mchtId" value="${mchtId}"/>
        <input type="hidden" name="extraData" value="${extraData}"/>
    </form>
</body>
</html>