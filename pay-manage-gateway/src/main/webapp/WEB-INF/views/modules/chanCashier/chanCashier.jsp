<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0"/>
    <meta name="format-detection" content="telephone=no,email=no,date=no,address=no">
    <title>支付中心</title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/css/loading.css?v=1.0.0" />
</head>
<body class="is-loading">
<div class="curtain">
    <div class="loader">
        loading...
    </div>
</div>

    <input id="clientPayWay" type="hidden" value="${clientPayWay}">
    <div id="payInfo" style="display: none;">${payInfo}</div>
</body>
<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript">
    $(document).ready(function(){
        $('body').removeClass('is-loading');
        $(".curtain").remove();
    });

    $(function(){
        var clientPayWay = $("#clientPayWay").val();

//        var payInfo = $("#payInfo").html();
        var payInfo = <%=request.getAttribute("payInfo")%>
        <%
            System.out.println(request.getAttribute("payInfo"));
        %>
//        alert(request.getAttribute("pageInfo"));
//        alert($("#payInfo").html());
        if("08" == clientPayWay){
            payInfo = document.getElementById("payInfo").innerText;
            //掉起上游收银台支付唤起支付--url方式
            location.href = payInfo;
        }else if("09" == clientPayWay){
            //掉起上游收银台支付唤起支付--form表单方式
            $("form")[0].submit();
        }else if("10" == clientPayWay){
            //掉起上游收银台支付唤起支付--js方式
            //TODO
        }
    });

</script>
</html>
