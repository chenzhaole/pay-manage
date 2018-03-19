<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>确认代付提现</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">
        table {
            width: 98%;
            margin: 10px auto;
            font: Georgia 11px;
            font-size: 12px;
            color: #333333;
            text-align: center;
            border-collapse: collapse;
        }

        table td {
            height: 25px;
            padding: 2px;
            border: 1px solid #dadade;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function(){
            //发送验证码
            $("#sendMsg").click(function () {
                var batchId = $("#batchId").val();
                var phone = $("#phone").val();
                var mchtId = $("#mchtId").val();
                $.ajax({
                    url:'${ctx}/proxy/sendMsg',
                    type:'POST', //GET
                    async:true,    //或false,是否异步
                    data:{
                        'batchId':batchId,'phone':phone,'mchtId':mchtId
                    },
                    timeout:5000,    //超时时间
                    dataType:'text',    //返回的数据格式：json/xml/html/script/jsonp/text
                    success:function(data){
                        console.log(data);
                        if(data=='ok')
                            alert("发送成功");
                        else
                            alert("发送失败，请联系管理员！");
                    }
                });
            });

            //提交确认代付
            $("#submitBut").click(function () {
                var batchId = $("#batchId").val();
                var phone = $("#phone").val();
                var mchtId = $("#mchtId").val();
                var smsCode = $("#smsCode").val().trim();
                if(smsCode == ""){
                    alert("请输入验证码！");
                }else{
                    $.ajax({
                        url:'${ctx}/proxy/confirmCommitBatch',
                        type:'POST', //GET
                        async:true,    //或false,是否异步
                        data:{
                            'batchId':batchId,'phone':phone,'mchtId':mchtId,'smsCode':smsCode
                        },
                        timeout:5000,    //超时时间
                        dataType:'text',    //返回的数据格式：json/xml/html/script/jsonp/text
                        success:function(data){
                            console.log(data);
                            if(data=='ok') {
                                alert("代付提交成功");
                                window.location.href = "${ctx}/proxy/proxyBatchList";
                            }else if(data == 'batch exsit') {
                                alert("代付提交失败，该代付批次已经存在！");
                            }else if(data == 'smscode error') {
                                alert("代付提交失败，短信验证码有误！");
                            }else {
                                alert("系统异常，请联系管理员！");
                            }
                        }
                    });

                }
            });

        });
    </script>
</head>
<body>
<form action="${ctx}/proxy/confirmCommitBatch" method="post" id="inputForm">
    <input type="hidden" value="${batch.id}" name="batchId" id="batchId"/>
    <input type="hidden" value="${phone}" name="phone" id="phone"/>
    <input type="hidden" value="${mchtId}" name="mchtId" id="mchtId"/>
    <table width="100%"  style="border:0" cellspacing="0" cellpadding="1">
        <tr>
            <td align="left">提现总金额：<fmt:formatNumber type="number" value="${batch.totalAmount*0.01}" maxFractionDigits="2"/> 元</td>
            <td align="left">总笔数：${batch.totalNum}</td>
        </tr>
        <tr>
            <td align="left">所需总金额：<fmt:formatNumber type="number" value="${proxyAmount*0.01}" maxFractionDigits="2"/> 元</td>
            <td align="left">总手续费：<fmt:formatNumber type="number" value="${proxyFee*0.01}" maxFractionDigits="2"/> 元</td>
        </tr>
        <tr>
            <td align="left">预留手机号码：${phone}</td>
            <td align="left">
                手机验证码：<input type="text" name="smsCode" id="smsCode"/> <input type="button" value="发送短信" id="sendMsg"/>&nbsp;&nbsp;<input type="button" value="确认提交" id="submitBut"/>
            </td>
        </tr>
    </table>
</form>
<table width="100%"  style="border:0" cellspacing="0" cellpadding="1">
    <tr><td>No.</td> <td>批次编号</td> <td>明细编号</td> <td>收款账号</td> <td>收款户名</td> <td>银行名称</td> <td>付款金额</td></tr>
    <c:forEach var="detail" items="${details}" varStatus="vt" >
        <tr>
            <td>${vt.index+1}</td>
            <td>${detail.batchId}</td>
            <td>${detail.id}</td>
            <td>${detail.bankCardNo}</td>
            <td>${detail.bankCardName}</td>
            <td>${detail.bankName}</td>
            <td><fmt:formatNumber type="number" value="${detail.amount*0.01}" maxFractionDigits="2"/> 元</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
