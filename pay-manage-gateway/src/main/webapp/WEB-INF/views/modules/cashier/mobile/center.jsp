<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0"/>
    <meta name="format-detection" content="telephone=no,email=no,date=no,address=no">
    <title>支付中心</title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/aui.css" />
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/app.css?v=1.0.0" />
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/loading.css?v=1.0.0" />
</head>
<body id="Error" class="bgF dialog is-loading">
<div class="curtain">
    <div class="loader">
        loading...
    </div>
</div>
<header class="aui-bar aui-bar-nav">
    <a class="aui-pull-left aui-btn" onclick="history.back()">
        <img src="${ctxStatic}/images/back.png"/>
    </a>
    <div class="aui-title">支付中心</div>
</header>
<div class="item">
    <div class="tipText">
        <p class="warnTip"><img src="${ctxStatic}/images/lamp.png" alt="温馨提示"  class="lamp"/>温馨提示</p>
        <p>支付时请注意身边安全,并注意避免重复支付。</p>
    </div>
    <c:if test="${'01' == callMode && '0' == iframe}">
        <iframe id="ifrmname" name="ifrmname" style="display: none;" src="${payInfo}"></iframe>
    </c:if>
    <div style="display: none" id="formSubmit">${payInfo}</div>
    <div class="submitBox">
        <a>
            <div class="aui-btn aui-btn-define1" onclick="goback(this)">返回</div>

            <input style="display: none" id="selectStatus" value=""/>
        </a>
    </div>
</div>
<div class="tipbox">
    <c:if test="${ !empty mobile || !empty qq}">
        <p>如遇支付问题请联系客服</p>
    </c:if>
    <c:if test="${ !empty mobile}">
        <p><span class="tag">客服热线 </span><span> ${mobile}</span></p>
    </c:if>
    <c:if test="${ !empty qq}">
        <p><span class="tag">客服QQ </span><span> ${qq}</span></p>
    </c:if>
</div>
</body>
<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
<script src="${ctxStatic}/js/app.js?v=1.0.0"></script>
<script src="${ctxStatic}/js/rotationOrder.js?version=1.0.0"></script>
<script type="text/javascript">
    //拼接页面回调地址
    var callbackUrl = "/gateway/cashier/chanCallBack/${platOrderId}/${payType}";
    //轮训查单需要的参数
    var queryInfo = "platOrderId="+ "${platOrderId}";
    var callMode = '${callMode}';
    var iframe = '${iframe}';

    $(document).ready(function(){
        $('body').removeClass('is-loading');
        $(".curtain").remove();
    });

    $(function(){
        //3秒之后执行查单处理
        setTimeout("toOrderQuery('"+queryInfo+"')",3000);

        //判断掉起支付的方式
        if("01" == callMode){
            //是否通过iframe标签掉起支付，0：使用， 1：不使用
            if(1 == iframe){
                //01：h5支付通过location.href方式唤起支付
                location.href = '${payInfo}';
            }
        }else if("02" == callMode){
            //02：h5支付通过form表单方式唤起支付
            $("#formSubmit form").submit();
        }else if("03" == callMode){
            //h5支付通过原生方式唤起支付
            //TODO
        }else if("06" == callMode){
            //公众号原生支付方式唤起支付
            //TODO
        }else if("07" == callMode){
            //公众号非原生支付方式唤起支付
            //TODO
        }else if("08" == callMode){
            //掉起上游收银台支付唤起支付
            //TODO
        }

    });



    var dialog = new auiDialog({});
    function goback(el){
        //首先查缓存，看订单是否已经成功
        orderStatusQuery(queryInfo);
        var status = $("#selectStatus").val();
        //2 支付成功, -1 未知失败, 4001 支付失败 ,4002 提交支付失败
        if(2 != status && -1 != status && 4001 != status && 4002 != status ){
            dialog.alert({
                title: '温馨提示',
                msg: '客官,您尚未完成支付',
                buttons: ['残忍拒绝', '返回支付'],
            }, function (ret) {
                if (ret.buttonIndex == 2) {
                    //返回支付
                    //判断掉起支付的方式
                    if("01" == callMode){
                        //是否通过iframe标签掉起支付，0：使用， 1：不使用
                        if(1 == iframe){
                            //01：h5支付通过location.href方式唤起支付
                            location.href = '${payInfo}';
                        }else{
                            //需要重新加载iframe
                            $("#ifrmname").attr('src', $("#ifrmname").attr("src"));
                        }
                    }else if("02" == callMode){
                        //02：h5支付通过form表单方式唤起支付
                        $("#formSubmit form").submit();
                    }else if("03" == callMode){
                        //h5支付通过原生方式唤起支付
                        //TODO
                    }else if("06" == callMode){
                        //公众号原生支付方式唤起支付
                        //TODO
                    }else if("07" == callMode){
                        //公众号非原生支付方式唤起支付
                        //TODO
                    }else if("08" == callMode){
                        //掉起上游收银台支付唤起支付
                        //TODO
                    }

                } else {
                    //拒绝操作 todo
                    location.href = callbackUrl;
                }
            });
        }
    }


</script>
</html>
