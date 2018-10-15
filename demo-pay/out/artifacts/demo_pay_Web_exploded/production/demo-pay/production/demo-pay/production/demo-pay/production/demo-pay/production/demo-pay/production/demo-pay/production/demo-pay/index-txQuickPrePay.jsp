<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
	<title>TX快捷预支付支付</title>
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
				<li class="current">提交信息（TX预支付请求） </li>
            </ol>
        </div>
        <form action="prePay" method="post"  target="_blank">
            <div id="body" style="clear:left">
                <dl class="content">
                    
                    <dt>支付地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="payUrl" value="http://127.0.0.1:12080/gateway/txQuickPrePay" maxlength="128" size="30"  placeholder="长度128"/>
                        <span class="null-star">(payUrl)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>商户编号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="mchtId" value="17359b78" maxlength="32" size="16"  placeholder="长度32"/>
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
                    
                    
                    <dt>商户订单号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="orderId" value="42424235265415250" maxlength="32" size="30"  placeholder="长度32"/>
                        <span class="null-star">(orderId)*</span>
                        <span></span>
                    </dd>

                    <dt>商品名称：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="goods" value="测试商品" maxlength="64" size="30"  placeholder="长度64"/>
                        <span class="null-star">(goods)*</span>
                        <span></span>
                    </dd>

                    <dt>持卡人姓名：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="accountName" value="王坤" maxlength="20" size="30"  placeholder="长度20"/>
                        <span class="null-star">(accountName)*</span>
                        <span></span>
                    </dd>

                    <dt>证件类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="certType" value="1" maxlength="4" size="30"  placeholder="可空，固定值 1"/>
                        <span  class="null-star">(certType)*</span>
                        <span></span>
                    </dd>

                    <dt>持卡人身份证号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="certificateNo" value="" maxlength="18" size="30"  placeholder="可空"/>
                        <span class="null-star">(certificateNo)*</span>
                        <span></span>
                    </dd>

                    <dt>银行编号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankCode" value="ABC" maxlength="32" size="30"  placeholder="可空"/>
                        <span class="">(bankCode)</span>
                        <span></span>
                    </dd>

                    <dt>银行卡类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="cardType" value="02" maxlength="2" size="30"  placeholder="可空"/>
                        <span class="null-star">(cardType)*</span>
                        <span></span>
                    </dd>

                    <dt>银行卡号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankCardNo" value="" maxlength="28" size="30"  placeholder="必填"/>
                        <span class="null-star">(bankCardNo)*</span>
                        <span></span>
                    </dd>

                    <dt>银行预留手机号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="mobilePhone" value="" maxlength="11" size="30"  placeholder="必填"/>
                        <span class="null-star">(mobilePhone)*</span>
                        <span></span>
                    </dd>

                    <dt>cvv：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="cvv" value="951" maxlength="3" size="30"  placeholder="银行卡类型为02信用卡时必输表示卡背面后3位数字"/>
                        <span class="">(cvv)</span>
                        <span></span>
                    </dd>

                    <dt>信用卡有效期：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="valid" value="0820" maxlength="4" size="30"  placeholder="格式 MMyy"/>
                        <span class="">(valid)</span>
                        <span></span>
                    </dd>

                    <dt>支付金额：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="amount" value="110" size="4" placeholder="单位：分"/> 分
                        <span class="null-star">(amount)*</span>
                        <span></span>
                    </dd>

                    <dt>订单时间：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="orderTime" value="20180202150800" maxlength="14" size="30"  placeholder="yyyyMMddHHmmss"/>
                        <span class="null-star">(orderTime)*</span>
                        <span></span>
                    </dd>

                    <dt>商品描述：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="desc" value="" maxlength="256" size="30"  placeholder="可空"/>
                        <span class="">(desc)</span>
                        <span></span>
                    </dd>

                    <dt>异步通知URL：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="notifyUrl" value="http://www.baidu.com" maxlength=" " size="30"  placeholder="接收推送通知的URL"/>
                        <span class="null-star">(notifyUrl)*</span>
                        <span></span>
                    </dd>

                    <dt>网页回调地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="callBackUrl" value="" maxlength="127" size="30"  placeholder="可空"/>
                        <span class="">(callBackUrl)</span>
                        <span></span>
                    </dd>

                    <dt>支行信息：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankBranch" value="" maxlength="100" size="30"  placeholder="可空"/>
                        <span class="">(bankBranch)</span>
                        <span></span>
                    </dd>

                    <dt>所属省：</dt>
                    <dd>
                        <span class=""></span>
                        <input name="province" value="" maxlength="20" size="30"  placeholder="可空"/>
                        <span class="">(province)</span>
                        <span></span>
                    </dd>

                    <dt>所属市：</dt>
                    <dd>
                        <span class=""></span>
                        <input name="city" value="" maxlength="20" size="30"  placeholder="可空"/>
                        <span class="">(city)</span>
                        <span></span>
                    </dd>

                    <dt>终端IP：</dt>
                    <dd>
                        <span class=""></span>
                        <input name="ip" value="127.0.0.1" maxlength="16"  placeholder="可空"/>
                        <span>(ip)</span>
                        <span></span>
                    </dd>

                    <dt>申报商户编号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="userId" value="18e1969b" maxlength="32" size="30"  placeholder="必填"/>
                        <span class="null-star">(userId)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>业务类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="txType" value="20" maxlength="8" size="30"  placeholder="必填"/>
                        <span class="null-star">(txType)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>商户类型：</dt>
                    <dd>
                        <span class=""></span>
                        <input name="dmType" value="20" maxlength="4" size="30"  placeholder="10:无积分；20:有积分"/>
                        <span class="">(dmType)</span>
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