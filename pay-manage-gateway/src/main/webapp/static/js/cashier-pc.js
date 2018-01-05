var countDownStatusWx = "0"; //微信支付倒计时状态 0 初始值; 1 计时中; 2 计时结束
var countDownStatusAli = "0";//支付宝倒计时状态 0 初始值; 1 计时中; 2 计时结束
var countDownStatusQQWallet = "0";//qq钱包倒计时状态
var countDownStatusUnionQrCode = "0";//银联二维码倒计时状态
var wxPlatOrderId = "";//微信支付平台订单号
var aliPlatOrderId = "";//支付宝支付平台订单号
var qqPlatOrderId = "";
var unionPlatOrderId = "";

/**
 *  获取二维码
 **/
function getQrCode(tab) {
    console.log(tab+"******************");
    var paytype = tab=="wxTab"?"24": tab=="aliTab"?"31":tab=="qqWalletTab"?"26":tab=="unionQrCodeTab"?"58":"";

    var ajax = paytype=="24"? (countDownStatusWx=="0"?true:false) :
            paytype=="31"?(countDownStatusAli=="0"?true:false) :
            paytype=="26"?(countDownStatusQQWallet=="0"?true:false) :
            paytype=="58"?(countDownStatusUnionQrCode=="0"?true:false):false;

    console.log(ajax+"******************");
    if(ajax){
        //表单序列化处理，开始请求支付
        $.ajax({
            type:"POST",
            url: "ajaxPay",
            data:{"payType":paytype,"sign":sign},
            dataType:'text' ,
            async:true,

            success:function(data){
                //返回值转换成json
                var jsonData = JSON.parse(data);

                console.log(jsonData);

                if(jsonData.respCode == "0000"){
                    if(paytype == '24'){
                        wxPlatOrderId = jsonData.data.platOrderId;
                        $("#wxqrcode").attr("src",jsonData.payUrl);
                        $(".wxpay-content").children().first().html('距离二维码过期还剩<span class="timeout" id="wxTimeout"></span>秒，过期后请刷新页面重新获取二维码');
                        var countDownTime = Date.parse(new Date())/1000+10;//TODO:默认倒计时60秒 待确认
                        countDown(countDownTime,"wxTimeout");

                        queryResult(wxPlatOrderId);
                    }else if(paytype == '31'){
                        aliPlatOrderId = jsonData.data.platOrderId;
                        $("#aliqrcode").attr("src",jsonData.payUrl);
                        $(".alipay-content").children().first().html('距离二维码过期还剩<span class="timeout" id="aliTimeout"></span>秒，过期后请刷新页面重新获取二维码');
                        var countDownTime = Date.parse(new Date())/1000+10;//TODO:默认倒计时60秒 待确认
                        countDown(countDownTime,"aliTimeout");

                        queryResult(aliPlatOrderId);
                    }else if(paytype == "26"){
                        qqPlatOrderId = jsonData.data.platOrderId;
                        $("#qqWalletQrcode").attr("src",jsonData.payUrl);
                        $(".tenpay-content").children().first().html('距离二维码过期还剩<span class="timeout" id="qqTimeout"></span>秒，过期后请刷新页面重新获取二维码');
                        var countDownTime = Date.parse(new Date())/1000+10;//TODO:默认倒计时60秒 待确认
                        countDown(countDownTime,"qqTimeout");
                        queryResult(qqPlatOrderId);
                    }else if(paytype == "58"){
                        unionPlatOrderId = jsonData.data.platOrderId;
                        $("#unionQrCode").attr("src",jsonData.payUrl);
                        $(".unionpay-content").children().first().html('距离二维码过期还剩<span class="timeout" id="unionTimeout"></span>秒，过期后请刷新页面重新获取二维码');
                        var countDownTime = Date.parse(new Date())/1000+10;//TODO:默认倒计时60秒 待确认
                        countDown(countDownTime,"unionTimeout");
                        queryResult(unionPlatOrderId);
                    }
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
function reGetQrCode(tab){
    if(tab == "wxTab"){
        countDownStatusWx = "0";
    }

    if(tab == "aliTab"){
        countDownStatusAli = "0";
    }

    if(tab == "qqWalletTab"){
        countDownStatusQQWallet = "0";
    }

    if(tab == "unionQrCodeTab"){
        countDownStatusUnionQrCode = "0";
    }
    getQrCode(tab);
}

/**
 *  查询订单状态
 **/
function queryResult(platOrderId){
    if(platOrderId != null && platOrderId != ""){
        $.ajax({
            type:"POST",
            url: "queryResult",
            data:{"platOrderId":platOrderId},
            dataType:'text' ,
            async:true,

            success:function(data){
                //返回值转换成json
                var jsonData = JSON.parse(data);
                if(jsonData.status == "2"){
                    window.location.href = jsonData.callbackUrl;
                }else{
                    setTimeout("queryResult('"+platOrderId+"')",2000);
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
function countDown(countDownTime,attr){
    //获取当前时间戳
    var nowTime = Date.parse(new Date())/1000;
    //用预设时间戳-当前时间戳获得倒计时时间
    var cdTime = countDownTime-nowTime;
    if (cdTime >= 1) {
        $("#"+attr).html(formatDate(cdTime));
        setTimeout("countDown("+countDownTime+",'"+attr+"')",1000);

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
        if(attr == "wxTimeout"){
            countDownStatusWx = "2";
            $("#wxTimeout").parent().html('二维码已过期，<a href="javascript:reGetQrCode(\'wxTab\')">刷新 </a>页面重新获取二维码');
        }
        if(attr == "aliTimeout"){
            countDownStatusAli = "2";
            $("#aliTimeout").parent().html('二维码已过期，<a href="javascript:reGetQrCode(\'aliTab\')">刷新 </a>页面重新获取二维码');
        }

        if(attr == "qqTimeout"){
            countDownStatusQQWallet = "2";
            $("#qqTimeout").parent().html('二维码已过期，<a href="javascript:reGetQrCode(\'qqWalletTab\')">刷新 </a>页面重新获取二维码');
        }
        if(attr == "unionTimeout"){
            countDownStatusUnionQrCode = "2";
            $("#unionTimeout").parent().html('二维码已过期，<a href="javascript:reGetQrCode(\'unionQrCodeTab\')">刷新 </a>页面重新获取二维码');
        }
    }
}

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