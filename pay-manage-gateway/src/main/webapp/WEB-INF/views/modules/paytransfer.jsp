<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>支付页面</title>
</head>
<body onload="onload();">
<div id="payInfo"></div>
<script type="text/javascript">
    function onload(){
        var html ="<form action=\"${payUrl}\" name=\"reload\" style='display:none' method='post'>";

        <c:forEach items="${map}" var="m">
            html =html + "<input type=\"hidden\" name=\"${m.key}\" value=\"${m.value}\">";
        </c:forEach>

        html =html +"</form>";
        document.getElementById("payInfo").innerHTML=html;
        document.getElementsByTagName('form')[0].submit();
    };

</script>
</body>
</html>
