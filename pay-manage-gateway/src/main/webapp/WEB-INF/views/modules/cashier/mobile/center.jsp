<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0"/>
    <meta name="format-detection" content="telephone=no,email=no,date=no,address=no">
    <title>支付中心</title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/aui.css" />
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/app.css" />
</head>
<body id="Error" class="bgF">
<header class="aui-bar aui-bar-nav">
    <a class="aui-pull-left aui-btn" onclick="history.back()">
        <img src="${ctxStatic}/images/back.png"/>
    </a>
    <div class="aui-title">支付中心</div>
</header>
<div class="item">
    <div style="display:none" >
        ${result.payInfo}
    </div>
    <div class="tipText">
        <p class="warnTip"><img src="${ctxStatic}/images/lamp.png" alt="温馨提示"  class="lamp"/>温馨提示</p>
        <p>支付时请注意身边安全,并注意避免重复支付。</p>
    </div>

    <div class="submitBox">
        <a href="">
            <input type="submit" class="reverseBtn" value="返回">
        </a>
    </div>
</div>
<div class="tipbox">
    <p>如遇支付问题请联系客服</p>
    <p><span class="tag">客服热线 </span><span> 400-000-0000</span></p>
    <p><span class="tag">客服QQ </span><span> 0000000000</span></p>
</div>
</body>
<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript">
    $(function(){
        var callMode = '${result.callMode}';
        if(callMode == '01'){//h5 链接重定向
            location.href = '${result.payInfo}';
        }else if(callMode == '02'){//表单自动提交
            $("#cardpayForm").submit();
        }else if(callMode == '03'){//公众号支付

        }
    });
</script>
</html>
