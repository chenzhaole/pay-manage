var countDownStatusWx = "0"; //微信支付倒计时状态 0 初始值; 1 计时中; 2 计时结束
var countDownStatusAli = "0";//支付宝倒计时状态 0 初始值; 1 计时中; 2 计时结束
var countDownStatusQQWallet = "0";//qq钱包倒计时状态
var countDownStatusUnionQrCode = "0";//银联二维码倒计时状态
var countDownStatusJDQrCode = "0";//京东二维码倒计时状态
var countDownStatusSNQrCode = "0";//苏宁二维码倒计时状态
var platOrderId = "";

/**
 *  获取二维码
 **/
function getQrCode(paymentType) {

    var ajax = paymentType=="wx"? (countDownStatusWx=="0"?true:false) :
        paymentType=="al"?(countDownStatusAli=="0"?true:false) :
            paymentType=="qq"?(countDownStatusQQWallet=="0"?true:false) :
                paymentType=="yl"?(countDownStatusUnionQrCode=="0"?true:false):
                    paymentType=="jd"?(countDownStatusJDQrCode=="0"?true:false):
                        paymentType=="sn"?(countDownStatusSNQrCode=="0"?true:false):
               false;

    if(ajax){
        //表单序列化处理，开始请求支付
        $.ajax({
            type:"POST",
            url: "/gateway/cashier/ajaxPay",
            data:{"payType":paymentType,"sign":sign,"orderId":orderId},
            dataType:'text' ,
            async:true,

            success:function(data){
                //返回值转换成json
                var jsonData = JSON.parse(data);
                if(jsonData.respCode == "0000"){
                    platOrderId = jsonData.data.platOrderId;
                    var countDownTime = Date.parse(new Date())/1000+10;//TODO:默认倒计时60秒 待确认
                    if(paymentType == 'wx'){
                        $("#wxqrcode").attr("src",jsonData.data.payUrl);
                        countDown(countDownTime,"wxTimeout",tab);
                    }else if(paymentType == 'al'){
                        $("#aliqrcode").attr("src",jsonData.data.payUrl);
                        countDown(countDownTime,"aliTimeout",tab);
                    }else if(paymentType == 'qq'){
                        $("#qqWalletQrcode").attr("src",jsonData.data.payUrl);
                        countDown(countDownTime,"qqTimeout",tab);
                    }else if(paymentType == 'yl'){
                        $("#unionQrCode").attr("src",jsonData.data.payUrl);
                        countDown(countDownTime,"unionTimeout",tab);
                    }else if(paymentType == 'other_cardpay'){
                        $("#other_cardpay").html(jsonData.data.payInfo);
                        //form表单提交
                    }
                    queryResult(platOrderId);
                }else{
                    console.log(jsonData.respMsg);
                    $("#errorMsg").text(jsonData.respMsg);
                    tips(".nouse");
                }
            },
            error:function(){
                $("#errorMsg").text("哎呀！服务器开小差了。");
                tips(".nouse");
            }
        });
    }
}


/**
 *  刷新二维码
 **/
function reGetQrCode(paymentType,obj){
    $(obj).hide();
    if(paymentType == "wx"){
        countDownStatusWx = "0";
    }

    if(paymentType == "al"){
        countDownStatusAli = "0";
    }

    if(paymentType == "qq"){
        countDownStatusQQWallet = "0";
    }

    if(paymentType == "yl"){
        countDownStatusUnionQrCode = "0";
    }

    if(paymentType == "jd"){
        countDownStatusJDQrCode = "0";
    }

    if(paymentType == "sn"){
        countDownStatusSNQrCode = "0";
    }

    getQrCode(paymentType);
}

/**
 *  查询订单状态
 **/
function queryResult(platOrderId){
    if(platOrderId != null && platOrderId != ""){
        $.ajax({
            type:"POST",
            url: "/gateway/cashier/queryResult",
            data:{"platOrderId":platOrderId},
            dataType:'text' ,
            async:true,

            success:function(data){
                //返回值转换成json
                var jsonData = JSON.parse(data);
                if(jsonData.status == "2"){
                    window.location.href = jsonData.callbackUrl;
                }else{
                    setTimeout("queryResult('"+platOrderId+"')",5000);
                }
            },
            error:function(){

            }
        });
    }
}

/**
 *  倒计时
 **/
function countDown(countDownTime,attr,tab){
    //获取当前时间戳
    var nowTime = Date.parse(new Date())/1000;
    //用预设时间戳-当前时间戳获得倒计时时间
    var cdTime = countDownTime-nowTime;
    if (cdTime >= 1) {
        $("#"+attr).html(formatDate(cdTime));
        setTimeout("countDown("+countDownTime+",'"+attr+"','"+tab+"')",1000);

        if(attr == "wxTimeout"){
            countDownStatusWx = "1";
        }

        if(attr == "aliTimeout"){
            countDownStatusAli = "1";
        }

        if(attr == "qqTimeout"){
            countDownStatusQQWallet = "1";
        }

        if(attr == "unionTimeout"){
            countDownStatusUnionQrCode = "1";
        }
    }else {
        // var html = '二维码已过期，<a href="javascript:reGetQrCode(\''+tab+'\')">刷新 </a>页面重新获取二维码';
        $("#"+attr).parent().parent().find(".overdue").show();

        if(attr == "wxTimeout"){
            countDownStatusWx = "2";
        }
        if(attr == "aliTimeout"){
            countDownStatusAli = "2";
        }

        if(attr == "qqTimeout"){
            countDownStatusQQWallet = "2";
        }
        if(attr == "unionTimeout"){
            countDownStatusUnionQrCode = "2";
        }
    }
}


//---------start------------原生js公众号支付------------
// function wechat_public_pay(pub_payInfo, paymentType, orderId){
//     //公众号原生支付
//     var pub_payInfo_json = $.parseJSON(pub_payInfo);
//     //解析参数
//     var appId = pub_payInfo_json.appId;
//     var timeStamp = pub_payInfo_json.timeStamp;
//     var nonceStr = pub_payInfo_json.nonceStr;
//     var packageStr = pub_payInfo_json.package;
//     var signType = pub_payInfo_json.signType;
//     var paySign = pub_payInfo_json.paySign;
//
//     function onBridgeReady(){
//         WeixinJSBridge.invoke(
//             'getBrandWCPayRequest', {
//                 "appId" : appId, //公众号名称，由商户传入
//                 "timeStamp" : timeStamp, //时间戳，自1970年以来的秒数
//                 "nonceStr" : nonceStr, //随机串
//                 "package" : packageStr,
//                 "signType" : signType,//微信签名方式：
//                 "paySign" : paySign //微信签名
//             },
//             function(res){//20170619--运营提需求公众号支付失败不需要扫码
//                 if(res.err_msg != "get_brand_wcpay_request:cancel"){
//                     // location.href = "/platcallback/order/"+gatewayId+"/"+paymentType+"/"+orderId; todo 平台callback地址
//                 }
//                 /*  if(res.err_msg == "get_brand_wcpay_request:ok" ) {
//                  //支付成功，直接跳回到结果页面
//                  redirectResultPageJs(paymentType, orderId);
//                  }else{
//                  if(res.err_msg != "get_brand_wcpay_request:cancel"){
//                  //异步请求后台去下单
//                  failPubToScanPay(paymentType, orderId, uuid, isRaw);
//                  }
//                  }    */
//                 // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
//             }
//         );
//     }
//     if (typeof WeixinJSBridge == "undefined"){
//         if( document.addEventListener ){
//             document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
//         }else if (document.attachEvent){
//             document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
//             document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
//         }
//     }else{
//         onBridgeReady();
//     }
// }

/**
 *
 * 将指定秒转换成时分秒格式
 *
 * */
function formatDate(second){
    var h = 0;
    var m = 0;
    //说明有小时
    if (second>=3600){
        h = parseInt(second/3600);
        alert (h);
        second = second%3600;
    }
    //说明有分钟
    if (second>=60){
        m = parseInt(second/60);
        second = second%60;
    }
    var hstr = "";
    //小时不为0并且小时小于10
    if (h<10){
        hstr = "0"+h;
    }else if (h>=10){
        hstr = ""+h;
    }
    var mstr = "";
    if (m<10){
        mstr = "0"+m;
    }else if (m>=10){
        mstr = ""+m;
    }
    var sstr = "";
    if (second<10){
        sstr = "0"+second;
    }else if (second>=10){
        sstr = ""+second;
    }
    if (h<=0){
        return mstr + ":" + sstr;
    }
    if (h>0){
        return hstr + ":" + mstr + ":" + sstr;
    }
}