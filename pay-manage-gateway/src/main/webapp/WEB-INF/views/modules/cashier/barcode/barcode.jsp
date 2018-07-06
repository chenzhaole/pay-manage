<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no, width=device-width">
		<link rel="stylesheet" href="${ctxStatic}/layui/css/layui.css">
		<link rel="stylesheet" href="${ctxStatic}/css/pc.css">
		<link rel="stylesheet" href="${ctxStatic}/css/jquery.mCustomScrollbar.min.css" />

		<title>付款码支付</title>
		<style type="text/css">
			.layui-row>div{height:inherit;word-break: break-all;}
			.width-180{width:180px}
			.margin-right-5{margin-right:5px}
			.padding-bottom-50{padding-bottom:50px}
			.card{position:relative;display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-orient:vertical;-webkit-box-direction:normal;-ms-flex-direction:column;flex-direction:column;min-width:0;word-wrap:break-word;background-color:#fff;background-clip:border-box;border:1px solid rgba(0,0,0,.125);border-radius:.25rem}
			.card-header:first-child{border-radius:calc(.25rem - 1px) calc(.25rem - 1px) 0 0}
			.card-header{padding:.75rem 1.25rem;margin-bottom:0;background-color:rgba(0,0,0,.03);border-bottom:1px solid rgba(0,0,0,.125)}
			.card-header h6{margin-bottom:.5rem;font-family:inherit;font-weight:500;line-height:1.2;color:inherit;font-size:0.9rem}
			.flexbox{display:-webkit-box;display:-ms-flexbox;display:flex;-ms-flex-wrap:wrap;flex-wrap:wrap;height:auto!important}
			.text-muted{line-height:2;color:#6c757d!important}
			.text-danger{color:#dc3545!important}
			.font-weight-bold{font-weight:700!important}
			.text-truncate{margin-bottom:1rem;font-size:0.8rem;word-break: break-all;}
			small{font-size:80%;font-weight:400}
			.card>.list-group:last-child .list-group-item:last-child{border-bottom-right-radius:.25rem;border-bottom-left-radius:.25rem}
			.list-group-item{position:relative;display:block;padding:.75rem 1.25rem;margin-bottom:-1px;background-color:#fff;border:1px solid rgba(0,0,0,.125)}
			.form-group{margin-bottom:1rem}
			.form-control{display:block;width:100%;padding:.375rem .75rem;font-size:1rem;line-height:1.5;color:#495057;background-color:#fff;background-clip:padding-box;border:1px solid #ced4da;border-radius:.25rem;transition:border-color .15s ease-in-out,box-shadow .15s ease-in-out;overflow:visible;box-sizing:border-box;-webkit-box-sizing:border-box;-moz-box-sizing:border-box}
			.form-control:focus{color:#495057;background-color:#fff;border-color:#80bdff;outline:0;box-shadow:0 0 0 .2rem rgba(0,123,255,.25)}
			.form-group>label{display:inline-block;margin-bottom:.5rem}
			.form-text{display:inline-block;margin-top:.25rem}
			.wx-pay-code{height:auto!important;font-weight:400;font-size:1rem;width:100%;padding-right:15px;padding-left:15px;box-sizing:border-box;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;-webkit-text-size-adjust:100%;-webkit-tap-highlight-color:transparent}
			.form-group{margin-bottom:1rem}
			.btn:not(:disabled):not(.disabled){cursor:pointer}
			.btn-block{display:block;width:100%}
			.btn-primary{color:#fff;background-color:#007bff;border-color:#007bff}
			.btn{display:inline-block;font-weight:400;text-align:center;white-space:nowrap;vertical-align:middle;-webkit-user-select:none;-moz-user-select:none;-ms-user-select:none;user-select:none;border:1px solid transparent;padding:.375rem .75rem;font-size:1rem;line-height:1.5;border-radius:.25rem;transition:color .15s ease-in-out,background-color .15s ease-in-out,border-color .15s ease-in-out,box-shadow .15s ease-in-out}
			.text-right{text-align:right!important}
			.img-fluid{max-width:100%;height:auto;display:block;margin:10px auto}
			.help{max-width:80%}
			.help .layui-layer-content{height:auto!important}
			.help .layui-layer-btn .layui-layer-btn0{background-color:#1E9FFF!important;border-color:#1E9FFF!important}
			.help .layui-layer-btn{border-top:1px solid #eee}
			.imgbox{display:none}
			.pt-4,.py-4{padding-top:1.5rem!important}
			@media (min-width:576px){.wx-pay-code{max-width:540px}
			}
			@media (min-width:768px){.wx-pay-code{max-width:720px}
			}
			@media (min-width:992px){.wx-pay-code{max-width:960px}
			}
			@media (min-width:1200px){.wx-pay-code{max-width:1140px}
			}
			.wx-pay-code{width:100%;padding-right:15px;padding-left:15px;margin-right:auto;margin-left:auto}
            .layui-layer-hui{
                background: rgba(0,0,0,.9)!important;
            }
            .layui-layer-hui .layui-layer-content{
                padding:25px!important;
            }
			.tips{
				text-align:left!important;
				text-decoration: underline;
				color: #dc3545!important;
			}
		</style>

	</head>

	<body class="container pt-4 wx-pay-code">
		<div class="card">
			<div class="card-header">
				<div class="layui-row  flexbox">
					<div class="layui-col-md12">
						<h6>订单提交成功，请您尽快付款!</h6>
						<small class="text-muted">请您在提交订单后的5分钟内完成支付，否则订单将自动取消。</small>
						<p class="text-truncate">订单号：<span class="text-danger font-weight-bold" id="orderIdSpan">${mchtOrderId}</span></p>
						<p class="text-truncate">订单金额(元)：<span class="text-danger font-weight-bold" id="money"><b>￥</b><b class="totalprice">${amount}</b></span></p>
					</div>
					<!--             <div class="col-md-2"> -->
					<!--             </div> -->
				</div>
			</div>
			<ul class="list-group list-group-flush">
				<li class="list-group-item" style="height: 13em; padding-top:30px;">
					<form method="post" name="myform" id="myform" action="">
						<div class="form-group">
							<label>
								<c:if test="${paymentType=='wx'}"><span name="payName">微信</span></c:if>
								<c:if test="${paymentType=='al'}"><span name="payName">支付宝</span></c:if>
								<c:if test="${paymentType=='qq'}"><span name="payName">QQ</span></c:if>
								<c:if test="${paymentType=='jd'}"><span name="payName">京东</span></c:if>
								<c:if test="${paymentType=='sn'}"><span name="payName">苏宁</span></c:if>
								<c:if test="${paymentType=='yl'}"><span name="payName">银联</span></c:if>付款码
								<span id="barcodeError" style="color: red;margin-left: 3px;display: none;">输入的条码数字格式不正确</span>
							</label>
							<input type="tel" class="form-control" id="authCode" name="authCode" placeholder="请输入18位条码数字"  maxlength="18">
							<input type="hidden" class="form-control" id="platOrderId" name="platOrderId" value="${platOrderId}">
							<input type="hidden" class="form-control" id="payType" name="payType" value="${payType}">
							<input type="hidden" class="form-control" id="g" name="g" value="175">
							<small id="emailHelp" class="form-text text-muted">
								<c:if test="${paymentType=='wx'}"><span name="payName">微信</span></c:if>
								<c:if test="${paymentType=='al'}"><span name="payName">支付宝</span></c:if>
								<c:if test="${paymentType=='qq'}"><span name="payName">QQ</span></c:if>
								<c:if test="${paymentType=='jd'}"><span name="payName">京东</span></c:if>
								<c:if test="${paymentType=='sn'}"><span name="payName">苏宁</span></c:if>
								<c:if test="${paymentType=='yl'}"><span name="payName">银联</span></c:if>付款，查看并输入付款码18位数字
							</small>
							<small id="Help" style="cursor:pointer;" class="text-right form-text text-muted tips">点击查看<c:if test="${paymentType=='wx'}"><span name="payName">微信</span></c:if>
									<c:if test="${paymentType=='al'}"><span name="payName">支付宝</span></c:if>
									<c:if test="${paymentType=='qq'}"><span name="payName">QQ</span></c:if>
									<c:if test="${paymentType=='jd'}"><span name="payName">京东</span></c:if>
									<c:if test="${paymentType=='sn'}"><span name="payName">苏宁</span></c:if>
									<c:if test="${paymentType=='yl'}"><span name="payName">银联</span></c:if>刷卡使用帮助
							</small>
						</div>
					</form>
					<button type="button" class="btn btn-block btn-primary" id="btnsubmit">立即支付</button>
				</li>
			</ul>
		</div>



		<c:if test="${paymentType=='wx'}"><div class="imgbox"><img id="helpImg" class="img-fluid" src="${ctxStatic}/images/weixn.gif" width="80%"></div></c:if>
		<c:if test="${paymentType=='al'}"><div class="imgbox"><img id="helpImg" class="img-fluid" src="${ctxStatic}/images/ali.gif" width="80%"></div></c:if>
		<c:if test="${paymentType=='qq'}"><div class="imgbox"><img id="helpImg" class="img-fluid" src="${ctxStatic}/images/qq.gif" width="80%"></div></c:if>
		<c:if test="${paymentType=='jd'}"><div class="imgbox"><img id="helpImg" class="img-fluid" src="${ctxStatic}/images/jd.gif" width="80%"></div></c:if>
		<c:if test="${paymentType=='sn'}"><div class="imgbox"><img id="helpImg" class="img-fluid" src="${ctxStatic}/images/sn.gif" width="80%"></div></c:if>
		<c:if test="${paymentType=='yl'}"><div class="imgbox"><img id="helpImg" class="img-fluid" src="${ctxStatic}/images/yl.gif" width="80%"></div></c:if>


		<!-- Optional JavaScript -->

		<script src="${ctxStatic}/js/jquery-3.2.1.min.js"></script>
		<script src="${ctxStatic}/js/jquery.mCustomScrollbar.js"></script>
		<script src="${ctxStatic}/layui/layui.js"></script>

		<script>

			layui.use(['layer', 'element'], function(e) {
				var layer = layui.layer;
				window.onload = function(){
					$("#Help").click(function() {
						layer.open({
							type: 1,
							title: "使用帮助动画",
							closeBtn: 1,
							shadeClose: true,
							area: ["400px", "auto"],
							skin: 'help',
							btn: ['确定'],
							content: $(".imgbox")
						});
					});
					$("#btnsubmit").click(function() {
                        //处理下单
                        var authCode = $("#authCode").val();
						var reg = /^\d{18}$/;//18位数字
						if('' == authCode || null == authCode || undefined == authCode || !reg.test(authCode)){
							$("#barcodeError").css("display","inline-block");
						    return;
						}
						var platOrderId = $("#platOrderId").val();
						var payType = $("#payType").val();
                        //弹出加载项
                        var layer = layui.layer;
                        layer.closeAll();
                        var indexLoad = layer.load(1, {shade: false}); //0代表加载的风格，支持0-2
                        //将按钮置黑。不允许再点击
                        $("#btnsubmit").attr("disabled",true);
                        $("#btnsubmit").css("background-color", "gray");
						$.ajax({
                            type:"get",
                            url: "/gateway/cashier/platBarcodeCall/"+authCode+"/"+platOrderId,
                            dataType:'json' ,
                            async:false,
                            success:function(data){
                                console.log(data);
                                //status: 2:请求成功， 1：请求不成功
								var status = data;
                                if(status == "2"){
									//开始轮询查单
                                    //1秒后，开启轮询查单
                                    setTimeout(function () {
                                        //查单时间-countDownTime
                                        var countDownTime  = Date.parse(new Date())/1000 + 10*60;//10分钟
                                        queryResult(platOrderId, payType, countDownTime, layer);
                                    }, 1000);
                                }else{
                                    //请求失败，即支付失败，直接弹出支付失败框
                                    resultJump("支付失败", platOrderId, payType);
                                }
                            },
                            error:function(){
                                //服务器异常
                                resultJump("哎呀！服务器开小差了", platOrderId, payType);
                            }
                        });
					});
				}
			});	

            //下单之后页面跳转
		    function resultJump(msg, platOrderId, payType) {
                //callbackUrl地址
                var url = "/gateway/cashier/chanCallBack/"+platOrderId+"/"+payType;
                var layer = layui.layer;
                //20180704
                //倒计时跳转
                var text = ""+msg+"",url = ""+url+""
                layer.msg("<div class='countDown'><span>3</span>s</div><p>"+ text +"</p>",{
                    time:3000
                });
                var  s = $(".countDown span");
                var interval = setInterval(function(){
                    s.text(s.text()-1);
                    if(s.text() <0){
                        clearInterval(interval);
                        window.location.href = url;
                    }
                },1000)
                //20180704 end
            }

            /**
             *  start 页面轮询时使用，查询订单状态
             **/
            function queryResult(platOrderId, payType, queryCountDownTime){
                //获取当前时间戳
                var nowTime = Date.parse(new Date())/1000;
                //用预设时间戳-当前时间戳获得倒计时时间
                var cdTime = queryCountDownTime-nowTime;
                if (cdTime >= 1 && platOrderId != null && platOrderId != ""){
                    $.ajax({
                        type:"POST",
                        url: "/gateway/cashier/queryResult",
                        data:{"platOrderId":platOrderId},
                        dataType:'text' ,
                        async:true,
                        success:function(data){
                            if(data == "2"){
                                resultJump("支付成功", platOrderId, payType);
                            }else{
                                setTimeout("queryResult('"+platOrderId+"','"+payType+"','"+queryCountDownTime+"')",1000);
                            }
                        },
                        error:function(){

                        }
                    });
                }else if(cdTime < 1){
                    resultJump("支付结果未知，请联系客服", platOrderId, payType);
                }
            }


		</script>

	</body>

</html>