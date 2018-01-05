<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>支付遇到问题</title>
    <link rel="stylesheet" href="${ctxStatic}/layui/css/layui.css">
    <link rel="stylesheet" href="${ctxStatic}/css/pc.css">
    <style>body{background-color: #FFF;}</style>
</head>
<body>
<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
<script src="${ctxStatic}/layui/layui.js"></script>
<script type="text/javascript">
    var msg = '${commonResult.respMsg}';
    var code = '${commonResult.respCode}';
    layui.config({});
    layui.use('layer', function() {
        var layer = layui.layer;

        layer.open({
            type:1,
            title: false,
            closeBtn: false, //不显示关闭按钮
            shade: [0],
            area: ['600px', '260px'],
            content: '<div class="layui-tab-item nopay layui-show"><div class="warn-content"><div class="warntip"><img src="${ctxStatic}/images/warn.png" alt="已支付" />温馨提示</div><p>'+msg+' , 状态码：'+code+'。</p><button class="layui-btn close">重试</button></div>',
            success:function(layero, index){
                $(".close").on("click",function(){
                    alert("刷新操作！")
//							window.location.href = "http://www.baidu.com"
                })
            }
        })
    });
</script>
</body>

</html>
