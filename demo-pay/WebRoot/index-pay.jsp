<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
	<title>统一扫码支付请求页面</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="css/index.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
	<script type="text/javascript">
		$(function(){
			$('input[name=orderId]').val("TEST-"+new Date().getTime());
			$('.hideClass').hide();
			
			$('input[name=time_start]').val(getCurrentDate()); 
		});
		function getCurrentDate(){
			var date = new Date();
			return date.getFullYear() + '' + formatString(date.getMonth() + 1) + formatString(date.getDay()) + formatString(date.getHours()) + formatString(date.getMinutes()) + formatString(date.getSeconds()); 
		}
		function formatString(value){
			if(parseInt(value) < 10){
				return  0 + '' + value; 
			}
			return value;
		}
		//验证ip
		function isIP(ip) {  
		    var reSpaceCheck = /^(\d+)\.(\d+)\.(\d+)\.(\d+)$/;  
		    if (reSpaceCheck.test(ip)) {  
		        ip.match(reSpaceCheck);  
		        if (RegExp.$1<=255&&RegExp.$1>=0  
		          &&RegExp.$2<=255&&RegExp.$2>=0  
		          &&RegExp.$3<=255&&RegExp.$3>=0  
		          &&RegExp.$4<=255&&RegExp.$4>=0) {  
		            return true;   
		        } else {  
		            return false;  
		        }  
		    } else {  
		        return false;  
		    }  
		}  
		function doSubmit(){
			var orderId = $.trim($('input[name=orderId]').val());
			if(orderId == ''){
				alert('商户订单号不能为空');   
				return false; 
			}
			var goods = $.trim($('input[name=goods]').val());
			if(goods == ''){
				alert('商品描述不能为空');
				return false;
			}
			var amount = $.trim($('input[name=amount]').val());
			if(amount == ''){
				alert('总金额不能为空');
				return false;
			}
			var notifyUrl = $.trim($('input[name=notifyUrl]').val());
			if(notifyUrl == ''){
				alert('异步通知地址不能为空');
				return false;
			}
			var ip = $.trim($('input[name=ip]').val());
			if(!isIP(ip)){
				alert("ip格式不正确");
				return false;
			}
			$('form').submit();
		}
	</script>
</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0  topMargin=4>
	<div id="main">
        <div class="cashier-nav">
            <ol>
				<li class="current">提交信息（网页支付请求） </li>
            </ol>
        </div>
        <form action="testPay" method="post"  target="_blank">
            <div id="body" style="clear:left">
                <dl class="content">
                    
                    <dt>支付地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <select name="payUrl">
                            <option value="http://127.0.0.1:12080/gateway/cashier/mchtCall">本地 http://127.0.0.1:12080/gateway/cashier/mchtCall</option>
                            <option value="http://114.115.206.62:12080/gateway/cashier/mchtCall">开发 http://114.115.206.62:12080/gateway/cashier/mchtCall</option>
                            <option value="http://114.115.206.62:12080/gateway/cashier/mchtCall">测试 http://114.115.206.62:12080/gateway/cashier/mchtCall</option>
                            <option value="http://106.2.6.41:12080/gateway/cashier/mchtCall">生产 http://106.2.6.41:12080/gateway/cashier/mchtCall</option>
                        </select>
                        <span class="null-star">(payUrl)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>商户编号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="mchtId" value="17b1652b" maxlength="32" size="16"  placeholder="长度32"/>
                        <span class="null-star">(mchtId)*</span>
                        <span></span>
                    </dd>
                    <dt>版本号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="version" value="21" maxlength="32" size="2"  placeholder="长度32"/>
                        <span class="null-star">(version)*</span>
                        <span></span>
                    </dd>
					
                    <dt>支付类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <select name="biz">
                        <option value="wxH5">微信H5</option>
                        <option value="wxQrcode">微信扫码</option>
                        <option value="aliQrcode">支付宝扫码</option>
                        <option value="wxBarcode">微信条码</option>
                        <option value="aliBarcode">支付宝条码</option>
                        <option value="qqQrcode">qq扫码</option>
                        <option value="wxJspay">微信公众支付</option>
                        <option value="aliJspay">支付宝服务窗</option>
                        </select>
                        <span class="null-star">(biz)*</span>
                        <span></span>
                    </dd>
                    
                    
                    <dt>商户订单号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="orderId" value="" maxlength="32" size="30"  placeholder="长度32"/>
                        <span class="null-star">(orderId)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>商品名称：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="goods" value="测试商品" maxlength="127" size="30"  placeholder="长度127"/>
                        <span class="null-star">(goods)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>支付金额：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="amount" value="1" size="4" placeholder="单位：分"/> 分
                        <span class="null-star">(amount)*</span>  
                        <span></span>
                    </dd>
                    
                    <dt>异步通知地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="notifyUrl" value="http://103.235.224.17:8081/CzlTest/noticeRcvServlet" maxlength="128" size="30"  placeholder="长度128"/>
                        <span class="null-star">(notifyUrl)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>回调地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="callBackUrl" value="http://www.baidu.com" maxlength="128" size="30"  placeholder="长度128"/>
                        <span class="null-star">(callBackUrl)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>订单时间：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="orderTime" value="20171209230101" maxlength="128" size="30"  placeholder="长度128"/>
                        <span class="null-star">(orderTime)*</span>
                        <span></span>
                    </dd>
                    
                    
                    <dt>附加信息：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="param" value="" maxlength="128" size="30"  placeholder="可空"/>
                        <span>(param)</span>
                        <span></span>
                    </dd>
                    
                    <dt>应用ID：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="appId" value="" maxlength="128" size="30"  placeholder="可空"/>
                        <span>(appId)</span>
                        <span></span>
                    </dd>
                    
                    <dt>应用名称：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="appName" value="" maxlength="128" size="30"  placeholder="可空"/>
                        <span>(appName)</span>
                        <span></span>
                    </dd>
                    
                    <dt>操作员编号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="operator" value="" maxlength="128" size="30"  placeholder="可空"/>
                        <span>(operator)</span>
                        <span></span>
                    </dd>
                    
                    <dt>订单超时时间：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="expireTime" value="" maxlength="128" size="30"  placeholder="可空"/>
                        <span>(expireTime)</span>
                        <span></span>
                    </dd>
                    
                    
                    
                    <dt>终端IP：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="ip" value="127.0.0.1" maxlength="16"  placeholder="可空"/>
                        <span>(ip)</span>
                        <span></span>
                    </dd>
                    <dt>商户秘钥：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="key" value="36c7675514eb435aafcd774e2c81d67d" maxlength="32"  size="30" placeholder=""/>
                        <span class="null-star">(key)*</span>
                        <span></span>
                    </dd>
                   
                    </dd>
                    <dd>
                        <span class="new-btn-login-sp">
                            <button class="new-btn-login" type="button" onclick="doSubmit()" style="text-align:center;">确 认</button>
                        </span>
                    </dd>
                </dl>
            </div>
		</form>
	</div>
</body>
</html>