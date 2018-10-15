<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
	<title>TX快捷支付</title>
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
			$('form').submit();
		}
	</script>
</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0  topMargin=4>
	<div id="main">
        <div class="cashier-nav">
            <ol>
				<li class="current">提交信息（TX支付请求） </li>
            </ol>
        </div>
        <form action="testTxQuickCommPayServlet" method="post"  target="_blank">
            <div id="body" style="clear:left">
                <dl class="content">
                    
                    <dt>支付地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="payUrl" value="http://127.0.0.1:12080/gateway/txQuickCommPay" maxlength="128" size="30"  placeholder="长度128"/>
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
                        <input name="version" value="20" maxlength="32" size="2"  placeholder="长度32"/>
                        <span class="null-star">(version)*</span>
                        <span></span>
                    </dd>
					
                    <dt>支付类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="biz" value="59" maxlength="32" size="2"  placeholder="长度32"/>
                        <span class="null-star">(biz)*</span>
                        <span></span>
                    </dd>

                    <dt>预消费流水号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="tradeId" value="${tradeId}" maxlength="64" size="30"  placeholder="长度32"/>
                        <span class="null-star">(tradeId)*</span>
                    </dd>
                    
                    <dt>短信验证码：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="smsCode" value="" maxlength="64" size="30"  placeholder="长度127"/>
                        <span class="null-star">(smsCode)*</span>
                        <span></span>
                    </dd>

                    <dt>持卡人姓名：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="accountName" value="王坤" maxlength="20" size="30"  placeholder="长度20"/>
                        <span class="null-star">(accountName)*</span>
                        <span></span>
                    </dd>

                    <dt>银行卡号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankCardNo" value="" maxlength="28" size="30"  placeholder="必填"/>
                        <span class="null-star">(bankCardNo)</span>
                        <span></span>
                    </dd>

                    <dt>支付金额：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="amount" value="110" size="4" placeholder="单位：分"/> 分
                        <span class="null-star">(amount)*</span>
                        <span></span>
                    </dd>

                    <dt>异步通知 URL：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="notifyUrl" value="http://www.baidu.com" maxlength=" " size="30"  placeholder="接收推送通知的URL"/>
                        <span class="null-star">(notifyUrl)*</span>
                        <span></span>
                    </dd>

                    <dt>申报商户编号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="userId" value="18e1969b" maxlength="32" size="30"  placeholder="必填"/>
                        <span class="null-star">(userId)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>商户秘钥：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="key" value="a9b39888a0c44fe8883b82ed2ab49294" maxlength="32"  size="30" placeholder=""/>
                        <span class="null-star">(key)*</span>
                        <span></span>
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