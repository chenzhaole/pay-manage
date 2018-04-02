//---------start------------扫码轮训查单------------
//统计轮询时间
var intervalTime = 0;

/*调用轮询方案查询订单,每秒查询一次*/
function toOrderQuery(queryInfo){
    //刷新页面之后重新开始查
    intervalTime = 0;
    //启动新的查单线程前
    setTimeout("orderQueryDeal('"+queryInfo+"')",1000);
}

/*查单接口处理*/
function orderQueryDeal(queryInfo){
    intervalTime++;
    if (intervalTime>120){
        return ;
    }
    $.ajax({
        type:"POST",
        url: "/gateway/cashier/queryResult",
        data: queryInfo,
        dataType:'text' ,
        async:true,
        success:function(data){
            if (data=="2"||data=="-1" || data == '4002' || data == '4001'){
                //查到结果后，关闭轮询
                location.href=callbackUrl;
            }else {
                setTimeout("orderQueryDeal('"+queryInfo+"')",1000);
            }
        },
        error:function(){
            //alert ("error");
        }
    });
}