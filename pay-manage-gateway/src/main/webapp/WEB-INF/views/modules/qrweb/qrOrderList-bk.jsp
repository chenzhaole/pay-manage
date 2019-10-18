<%--<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>--%>
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">--%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<html>
<head>
    <%--<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>--%>
    <meta name="renderer" content="webkit"/>
    <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no"/>
    <!-- Page Action -->
    <meta http-equiv="Page-Enter" content="revealTrans(duration=1.0,transtion=6)">
    <meta http-equiv="Page-Exit" content="revealTrans(duration=1.0,transtion=6)">
    <meta name="Keywords" Content="收款二维码"/>
    <meta http-equiv="Cache-Control" content="no-cache,no-store,must-revalidate"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <script src="/static/qrcode/js/jquery.min.js"></script>
    <link rel="stylesheet" href="/static/qrcode/css/Style.css">
    <title>订单列表</title>
    <style type="text/css">

        .list_1 {
        ul {padding-left: 4.6rem;}
        li {
            border-bottom: 1px solid #ddd;
            padding-right: 1.6rem;
            position: relative;
        a {
            display: block;height: 4rem;line-height: 4rem;overflow: hidden;font-size: 1.4rem;
            background:url("../image/icon_goto.png") right center no-repeat;
            background-size: auto 1.4rem;
        }
        .ico {
            display: block;width: 2.4rem;height: 2.4rem;position: absolute;left: -3rem;top: .8rem;
            background: #f60;border-radius: 50%;
        }
        }
        }


    </style>
</head>



<script>


    $(function () {
        $("#more").click(function () {
            var page = parseInt($("#page").val());
            $(this).html("加载中...");
            status=$(this).attr("data-status");
            if(status==1) {
                status = $(this).attr("data-status", "0");
                $.ajax({
                    type: "post",
                    url: "/gwqr/toQrCodeOrderFinishPage?mchtOrderId=12345",
                    data: "page=" + page,
                    dataType: "json",
                    success: function (data) {
                        data = data.data;
                        /*数据不够10条隐藏按钮*/
                        if (data.length < 10) {
                            $(this).hide()
                        } else {
                            $("#page").val(page + 1);//记录页码
                        }
                        insertDiv(data);
                    }
                });
            }

        });
    });
    function insertDiv(data){
        var information = $(".information");
        var html = '';
        for (var i = 0; i < data.length; i++) {
            html +="<div>"+data[i].title+"</div>"+"<div>"+data[i].date+"</div>"
        }
        information.append(html);
        $("#more").html("加载更多");
        $("#more").attr("data-status","1");
    }


</script>




<body>



<%--<div id="more" data-status="1">--%>
    <%--加载更多--%>
<%--</div>--%>




<div class="list_1" id="" >
    <ul>
        <li><a href=""><i class="ico ico_1"></i>这是一个列表1</a></li>
        <li><a href=""><i class="ico ico_2"></i>这是一个列表2</a></li>
        <li><a href=""><i class="ico ico_3"></i>这是一个列表3</a></li>
        <li><a href=""><i class="ico ico_4"></i>这是一个列表4</a></li>
        <li><a href=""><i class="ico ico_5"></i>这是一个列表5</a></li>
        <li><a href=""><i class="ico ico_6"></i>这是一个列表6</a></li>
        <li><a href=""><i class="ico ico_7"></i>这是一个列表7</a></li>
        <li><a href=""><i class="ico ico_8"></i>这是一个列表8</a></li>
    </ul>
    <div id="more" data-status="1">
    加载更多
    </div>
</div>




<input type="hidden" id="page" value="2">


</body>




</html>