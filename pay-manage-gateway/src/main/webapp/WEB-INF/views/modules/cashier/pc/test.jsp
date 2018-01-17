<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form id="search" action="${ctx}/gateway/cashier/call" method="post" >
        biz: <input type="text" value="21" name="biz"/> <br>
        mchtId: <input type="text" value="17f2b3c4" name="mchtId"/><br>
        version: <input type="text" value="20" name="version"/><br>
        amount: <input type="text" value="100" name="amount"/><br>
        desc: <input type="text" value="test" name="desc"/><br>
        goods: <input type="text" value="test" name="goods"/><br>
        notifyUrl: <input type="text" value="http://www.baidu.com" name="notifyUrl"/><br>
        orderId: <input type="text" value="20171104-014" name="orderId"/><br>
        operator: <input type="text" value="001" name="operator"/><br>
        orderTime: <input type="text" value="20171109113911" name="orderTime"/><br>
        mchtKey: <input type="text" value="5bbc9990ea044bfc8250508418ed1516" name="mchtKey"/><br>
        sign: <input type="text" name="sign" id="sign"> <input type="button" value="签名" id="genSign"/><br>
        <input type="submit" value="提交"/>
    </form>
</body>
<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>

<script type="text/javascript">
    $(document).ready(function () {
        $("#genSign").click(function () {
            var param = $("#search").serialize();
            console.log(param);
            $.ajax({
                type:"POST",
                url: "${ctx}/gateway/cashier/genSign?"+param,
//                data:{"payType":paytype,"sign":sign},
                dataType:'text' ,
                async:true,

                success:function(data){
                    //返回值转换成json
                    console.log(data);
                    $("#sign").val(data);
                },
                error:function(){
                    //请求错误，提示1000错误码
                    console.log("[error...]");
                }
            });
        });
    });
</script>

</html>
