var countDownStatusWx = "0"; //微信支付倒计时状态 0 初始值; 1 计时中; 2 计时结束
var countDownStatusAli = "0";//支付宝倒计时状态 0 初始值; 1 计时中; 2 计时结束
var countDownStatusQQWallet = "0";//qq钱包倒计时状态
var countDownStatusUnionQrCode = "0";//银联二维码倒计时状态
var countDownStatusJDQrCode = "0";//京东二维码倒计时状态
var countDownStatusSNQrCode = "0";//苏宁二维码倒计时状态


/**
 * start pc页面是否显示收银台
 */
$(function () {
    var pcIscashierInit = $("#pcIscashier").val();
    if("1" == pcIscashierInit){
        //pc页面直接下单
        var payTypeInit = $("#payType").val();
        payTypeInit = payTypeInit.slice(0, 2);
        //支付选中状态
        $("#"+payTypeInit).addClass("layui-this").siblings().removeClass("layui-this");
        var className = payTypeInit + '-content';
        $("."+className).parent().addClass("layui-show");
        //开启倒计时及页面轮询查单
        var platOrderIdInit = $("#platOrderId").val();
        var countdownTimeStrInit = $("#countdownTime").val();
        //下单后，开启倒计时及页面轮询查单
        notPcCashierDownTimeAndStartQuery(payTypeInit, countdownTimeStrInit, platOrderIdInit);
    }else if("2" == pcIscashierInit){
        //pc页面显示收银台
        $(".layui-tab-item.nopay").addClass("layui-show").siblings().removeClass("layui-show");
    }
});
/**
 * end pc页面是否显示收银台
 */


/**
 * start 页面点击获取二维码
 * 2表示展示收银台， 1表示不展示收银台
 */
var pcIscashier = $("#pcIscashier").val();
layui.config({});
var layer;
layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element'], function() {
    var laydate = layui.laydate, //日期
        laypage = layui.laypage, //分页
        element = layui.element; //元素操作
        layer = layui.layer; //弹层

    if('2' == pcIscashier){
        //监听Tab切换
        var index =0,inter=[],ele=[0,0,0,0,0];
        element.on('tab(pay)', function(data) {
            //加载--start
            var indexLoad = layer.load(1, {shade: false}); //0代表加载的风格，支持0-2
            $("div[class='layui-tab-content']").show();
            //this是li标签,即拿到li标签的id属性值
            var tab = $(this).attr("id");
            //暂时将二维码倒计时跟展示二维码隐藏,下单成功后在显示出来
            $("div[class='"+tab+"-content']").hide();
            if(tab.indexOf("qj") == 0){
            //银行卡的时候退出,不在获取二维码,而是去获取用户已绑定的卡信息
                return;
            }else{
                //点击支付宝，微信图标时，发送请求操作,调用下边getAsynQrCode函数异步获取二维码
                getAsynQrCode(tab);
            }

            //延迟半秒显示
            setTimeout(function () {
                //关闭加载效果
                layer.close(indexLoad);
                //显示二维码
                $("div[class='"+tab+"-content']").show();

            }, 1000);
        });
    }


    //银联二维码支持的客户端列表
    $(document).on("click",".openSupportList",function(e){
        layer.open({
            type: 2,
            title: false,
            area: ['750px','80%'],
            shade: 0.8,
            closeBtn: 1,
            shadeClose: true,
            content: 'https://billcloud.unionpay.com/upwxs-mktc/web/mapp/wxe990cdbcc189456e/custom/alllist'
        });
    });
});
/**
 * end 页面点击获取二维码
 */


/**
 * start 点击查看支付结果
 */
$(".qrcode .btn").on("click", function() {
    //先查询下订单状态
    var platOrderId = $("#platOrderId").val();
    queryOrderStatus(platOrderId);
    var status = $("#status").val();
    if("2" == status) {
        //跳转回掉页面
        refuse();
    }else{
        var ctxStatic = $("#ctxStatic").val();
        layer.open({
            type: 1,
            title: false,
            closeBtn: 0, //不显示关闭按钮
            shade: [0],
            area: ['600px', '260px'],
            //							  anim: 2,
            title: '',
            //maxmin:true,
            content: '<div class="layui-tab-item nopay layui-show"><div class="warn-content"><div class="warntip"><img src="'+ctxStatic+'/images/warn.png" alt="支付结果" />温馨提示</div><p>支付结果未知!</p> <div class=""><button class="layui-btn" style="width:48%!important;float:left;margin:0;" onclick="refuse()">残忍拒绝</button><button class="layui-btn close" style="width:48%!important;float:right;margin:0;">返回支付</button></div></div>', //iframe的url，no代表不显示滚动条
            success: function(layero, index) {
                $(".close").on("click", function() {
                    layer.closeAll();
                })
            },
            end: function() {

            }
        })
    }
});
/**
 * end 点击查看支付结果
 */


/**
 *  start 点击残忍拒绝时的操作
 */
function refuse() {
    var platOrderId = $("#platOrderId").val();
    var payType = $("#payType").val();
    var callbackUrl = "/gateway/cashier/chanCallBack/"+platOrderId+"/"+payType;
    window.location.href = callbackUrl;
}
/**
 *  end 点击残忍拒绝时的操作
 */


/**
 *  start 异步获取二维码
 **/
function getAsynQrCode(paymentType) {

    var isAjax = paymentType=="wx"? (countDownStatusWx=="0"?true:false) :
        paymentType=="al"?(countDownStatusAli=="0"?true:false) :
            paymentType=="qq"?(countDownStatusQQWallet=="0"?true:false) :
                paymentType=="yl"?(countDownStatusUnionQrCode=="0"?true:false):
                    paymentType=="jd"?(countDownStatusJDQrCode=="0"?true:false):
                        paymentType=="sn"?(countDownStatusSNQrCode=="0"?true:false):
               false;

    var mchtId = $("#mchtId").val();
    var mchtOrderId = $("#mchtOrderId").val();
    var extraData = $("#extraData").val();

       //表单序列化处理，开始请求支付
    $.ajax({
        type:"get",
        url: "/gateway/cashier/platPcCall/"+mchtId+"/"+mchtOrderId+"/"+paymentType+"/"+extraData,
        dataType:'json' ,
        async:false,

        success:function(data){
            console.log(data);
            if(data.respCode == "0000"){
                var payJsondata = JSON.parse(data.data);
                var payInfo = payJsondata.payInfo;
                if(payJsondata.hasOwnProperty("clientPayWay")){
                    var clientPayWay = payJsondata.clientPayWay;
                    chanCashierPay(payInfo, clientPayWay);
                }else{
                    var platOrderId = payJsondata.platOrderId;
                    var payType = payJsondata.payType;
                    //将平台订单存入页面，查单时使用
                    $("#platOrderId").val(platOrderId);
                    $("#payType").val(payType);
                    var countdownTimeStr = payJsondata.countdownTime;
                    //开启倒计时及页面轮询查单
                    if(isAjax) {
                        pcCashierDownTimeAndStartQuery(paymentType, countdownTimeStr, platOrderId, payInfo);
                    }
                }

            }else{
                if(data.respCode == "E8003"){
                    //该笔订单已成功
                    $("#alreadySucc").css("display","block");
                }
                $("#respCode").html(data.respCode);
                $("#respMsg").html(data.respMsg);
                tips(".nouse");
            }
        },
        error:function(){
            $("#respCode").html("unKnow");
            $("#respMsg").html("哎呀！服务器开小差了");
            tips(".nouse");
        }
    });

}
/**
 *  end 异步获取二维码
 **/

/**
 *  start pc收银台方式，点击某种支付方式后，开启倒计时，并开始轮询查单
 */
function pcCashierDownTimeAndStartQuery(paymentType, countdownTimeStr, platOrderId, payInfo) {
    //倒计时，后台配置
    var countdownTimeInt = parseInt(countdownTimeStr);
    var countDownTime = Date.parse(new Date())/1000+countdownTimeInt;
    if(paymentType == 'wx'){
        $("#wxqrcode").attr("src",payInfo);
        countDown(countDownTime,"wxTimeout",paymentType);
    }else if(paymentType == 'al'){
        $("#aliqrcode").attr("src",payInfo);
        countDown(countDownTime,"alTimeout",paymentType);
    }else if(paymentType == 'qq'){
        $("#qqWalletQrcode").attr("src",payInfo);
        countDown(countDownTime,"qqTimeout",paymentType);
    }else if(paymentType == 'jd'){
        $("#jdqrcode").attr("src",payInfo);
        countDown(countDownTime,"jdTimeout",paymentType);
    }else if(paymentType == 'sn'){
        $("#snWalletQrcode").attr("src",payInfo);
        countDown(countDownTime,"snTimeout",paymentType);
    }else if(paymentType == 'yl'){
        $("#unionQrCode").attr("src",payInfo);
        countDown(countDownTime,"ylTimeout",paymentType);
    }else if(paymentType == 'other_cardpay'){
        $("#other_cardpay").html(payInfo);
        //form表单提交
    }
    //3秒后，开启轮询查单
    setTimeout(function () {
        queryResult(platOrderId);
    }, 3000);
}
/**
 *  end pc收银台方式，点击某种支付方式后，开启倒计时，并开始轮询查单
 */



/**
 *  start pc非收银台方式，直接发起支付后，开启倒计时，并开始轮询查单
 */
function notPcCashierDownTimeAndStartQuery(paymentType, countdownTimeStr, platOrderId) {
    var countdownTimeInt = parseInt(countdownTimeStr);
    var countDownTime = Date.parse(new Date())/1000+countdownTimeInt;

    if(paymentType == 'wx'){
        countDown(countDownTime,"wxTimeout",paymentType);
    }else if(paymentType == 'al'){
        countDown(countDownTime,"alTimeout",paymentType);
    }else if(paymentType == 'qq'){
        countDown(countDownTime,"qqTimeout",paymentType);
    }else if(paymentType == 'jd'){
        countDown(countDownTime,"jdTimeout",paymentType);
    }else if(paymentType == 'sn'){
        countDown(countDownTime,"snTimeout",paymentType);
    }else if(paymentType == 'yl'){
        countDown(countDownTime,"ylTimeout",paymentType);
    }
    //开启轮询查单
    queryResult(platOrderId);

}
/**
 *  end pc非收银台方式，直接发起支付后，开启倒计时，并开始轮询查单
 */


/**
 *  start 刷新二维码
 **/
function reGetAsynQrCode(paymentType,obj){
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

    getAsynQrCode(paymentType);
}

/**
 *  end 刷新二维码
 **/

/**
 *  start 查询订单状态结果
 **/
function queryOrderStatus(platOrderId){
    if(platOrderId != null && platOrderId != ""){
        $.ajax({
            type:"POST",
            url: "/gateway/cashier/queryResult",
            data:{"platOrderId":platOrderId},
            dataType:'text' ,
            async:false,
            success:function(data){
                $("#status").val(data);
            },
            error:function(){

            }
        });
    }
}
/**
 * end 查询订单状态结果
 */


/**
 *  start 页面轮询时使用，查询订单状态
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
                if(data == "2"){
                    refuse();
                }else{
                    setTimeout("queryResult('"+platOrderId+"')",1000);
                }
            },
            error:function(){

            }
        });
    }
}
/**
 *  end 页面轮询时使用，查询订单状态
 **/

/**
 *  start 倒计时
 **/
function countDown(countDownTime,attr,paymentType){
    //获取当前时间戳
    var nowTime = Date.parse(new Date())/1000;
    //用预设时间戳-当前时间戳获得倒计时时间
    var cdTime = countDownTime-nowTime;
    if (cdTime >= 1) {
        $("#"+attr).html(formatDate(cdTime));
        setTimeout("countDown("+countDownTime+",'"+attr+"','"+paymentType+"')",1000);

        if(attr == "wxTimeout"){
            countDownStatusWx = "1";
        }

        if(attr == "alTimeout"){
            countDownStatusAli = "1";
        }

        if(attr == "qqTimeout"){
            countDownStatusQQWallet = "1";
        }

        if(attr == "jdTimeout"){
            countDownStatusJDQrCode = "1";
        }

        if(attr == "snTimeout"){
            countDownStatusSNQrCode = "1";
        }

        if(attr == "ylTimeout"){
            countDownStatusUnionQrCode = "1";
        }

    }else {
        // var html = '二维码已过期，<a href="javascript:reGetAsynQrCode(\''+paymentType+'\')">刷新 </a>页面重新获取二维码';
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
        if(attr == "jdTimeout"){
            countDownStatusJDQrCode = "2";
        }
        if(attr == "snTimeout"){
            countDownStatusSNQrCode = "2";
        }
        if(attr == "ylTimeout"){
            countDownStatusUnionQrCode = "2";
        }
    }
}
/**
 *  end 倒计时
 **/


/**
 *  start 将指定秒转换成时分秒格式
 *
 * */
function formatDate(second){
    var h = 0;
    var m = 0;
    //说明有小时
    if (second>=3600){
        h = parseInt(second/3600);
        // alert (h);
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
/**
 *  end 将指定秒转换成时分秒格式
 *
 * */


//tips("nobind");
function tips(ele) {
    $(ele).addClass("layui-show").siblings().removeClass("layui-show");
}

function chanCashierPay(payInfo, clientPayWay) {
    if("08" == clientPayWay){
        //掉起上游收银台支付唤起支付--url方式
        location.href = payInfo;
    }else if("09" == clientPayWay){
        //掉起上游收银台支付唤起支付--form表单方式
        //往body动态添加form表单，并提交
        $("body").append(payInfo);
        $("form")[0].submit();
    }else if("10" == clientPayWay){
        //掉起上游收银台支付唤起支付--js方式
        //TODO
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


