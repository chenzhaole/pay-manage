<%--

20190425 GwApiPayController层之间redirect调账,不需要这个中转页面

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>支付中……</title>
</head>
<body>
    <form name="createOrderForm" action="https://o2.qfpay.com/q/direct" method="get">

        <input type="hidden" name="mchntnm" value="<%=request.getAttribute("mchntnm")%>"/>
        <input type="hidden" name="txamt" value="<%=request.getAttribute("txamt")%>"/>
        <input type="hidden" name="goods_name" value="<%=request.getAttribute("goods_name")%>"/>
        <input type="hidden" name="redirect_url" value="<%=request.getAttribute("redirect_url")%>"/>
        <input type="hidden" name="package" value="<%=request.getAttribute("packageP")%>"/>
        <input type="hidden" name="timeStamp" value="<%=request.getAttribute("timeStamp")%>"/>
        <input type="hidden" name="signType" value="<%=request.getAttribute("signType")%>"/>
        <input type="hidden" name="paySign" value="<%=request.getAttribute("paySign")%>"/>
        <input type="hidden" name="appId" value="<%=request.getAttribute("appId")%>"/>
        <input type="hidden" name="nonceStr" value="<%=request.getAttribute("nonceStr")%>"/>
    </form>
</div>
</body>

<script type="text/javascript">
    document.forms[0].submit();
</script>

</html>

--%>