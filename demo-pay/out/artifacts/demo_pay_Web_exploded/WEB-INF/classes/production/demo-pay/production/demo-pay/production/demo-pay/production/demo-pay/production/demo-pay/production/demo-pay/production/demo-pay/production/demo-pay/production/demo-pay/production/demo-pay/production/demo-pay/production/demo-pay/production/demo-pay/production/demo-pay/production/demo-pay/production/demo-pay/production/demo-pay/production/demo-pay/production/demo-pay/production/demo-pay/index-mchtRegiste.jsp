<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
	<title>商户入驻</title>
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

			$('form').submit();
		}
	</script>
</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0  topMargin=4>
	<div id="main">
        <div class="cashier-nav">
            <ol>
				<li class="current">提交信息（商户入驻请求） </li>
            </ol>
        </div>
        <form action="testTxMchtRegisteServlet" method="post"  target="_blank">
        <form action="http://127.0.0.1:12080/gateway/mchtRegiste" method="post"  target="_blank">
            <div id="body">
                <dl class="content">
                    
                    <dt>支付地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="payUrl" value="http://127.0.0.1:12080/gateway/mchtRegiste" maxlength="128" size="30"  placeholder="长度128"/>
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
                        <select name="biz">
                            <option value="11" selected="selected">商户入驻</option>
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

                    <dt>商户秘钥：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="key" value="a9b39888a0c44fe8883b82ed2ab49294" maxlength="32"  size="30" placeholder=""/>
                        <span class="null-star">(key)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>商户名称：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="name" value="车市街口" maxlength="127" size="30"  placeholder="长度127"/>
                        <span class="null-star">(name)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>商户简称：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="nickName" value="车市" size="30" placeholder=""/>
                        <span class="null-star">(nickName)</span>
                        <span></span>
                    </dd>
                    
                    <dt>省：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="province" value="" maxlength="128" size="30"  placeholder="长度128"/>
                        <span>(province)</span>
                        <span></span>
                    </dd>
                    
                    <dt>市：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="city" value="" maxlength="128" size="30"  placeholder="长度128"/>
                        <span>(city)</span>
                        <span></span>
                    </dd>
                    
                    <dt>区县：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="district" value="" maxlength="128" size="30"  placeholder="长度128"/>
                        <span>(district)</span>
                        <span></span>
                    </dd>
                    
                    
                    <dt>地址：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="address" value="车市口" maxlength="128" size="30"  placeholder="可空"/>
                        <span class="null-star">(address)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>联系电话：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="tel" value="13735522439" maxlength="128" size="30"  placeholder="可空"/>
                        <span>(tel)</span>
                        <span></span>
                    </dd>
                    
                    <dt>联系邮箱：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="email" value="" maxlength="128" size="30"  placeholder="可空"/>
                        <span>(email)</span>
                        <span></span>
                    </dd>
                    
                    <dt>商户类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="mchtType" value="51" maxlength="128" size="30"  placeholder="可空"/>
                        <span class="null-star">(mchtType)*</span>
                        <span></span>
                    </dd>
                    
                    <dt>经营范围：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="businessScope" value="" maxlength="128" size="30"  placeholder="可空"/>
                        <span>(businessScope)</span>
                        <span></span>
                    </dd>

                    <dt>营业执照类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="businessLicenseType" value="" maxlength="32"  placeholder="可空"/>
                        <span>(businessLicenseType)</span>
                        <span></span>
                    </dd>

                    <dt>营业执照编号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="businessLicenseCode" value="" maxlength="32"  placeholder="可空"/>
                        <span>(businessLicenseCode)</span>
                        <span></span>
                    </dd>

                    <dt>组织机构代码：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="organizationCode" value="" maxlength="32"  placeholder="可空"/>
                        <span>(organizationCode)</span>
                        <span></span>
                    </dd>

                    <dt>税务登记代码：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="taxRegistCode" value="" maxlength="32"  placeholder="可空"/>
                        <span>(taxRegistCode)</span>
                        <span></span>
                    </dd>

                    <dt>法人/个人姓名：<dt/>
                    <dd>
                        <span class="null-star"></span>
                        <input name="legalName" value="阿离" maxlength="8"  placeholder="可空"/>
                        <span class="null-star">(legalName)*</span>
                        <span></span>
                    </dd>

                    <dt>证件类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="legalCertType" value="1" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(legalCertType)*</span>
                        <span></span>
                    </dd>

                    <dt>证件号码：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="legalCertNo" value="458574984196854968" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(legalCertNo)*</span>
                        <span></span>
                    </dd>

                    <dt>客服电话：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="serviceTel" value="" maxlength="32"  placeholder="可空"/>
                        <span>(serviceTel)</span>
                        <span></span>
                    </dd>

                    <dt>结算户银行编码：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleBankNo" value="98510981" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(settleBankNo)*</span>
                        <span></span>
                    </dd>

                    <dt>银行卡类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleCardType" value="01" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(settleCardType)*01借记02信用</span>
                        <span></span>
                    </dd>

                    <dt>CVV：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleCardCvv" value="" maxlength="32"  placeholder="可空"/>
                        <span>(settleCardCvv)</span>
                        <span></span>
                    </dd>

                    <dt>信用卡有效期：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleCardExpDate" value="" maxlength="32"  placeholder="可空"/>
                        <span>(settleCardExpDate)</span>
                        <span></span>
                    </dd>

                    <dt>银行预留手机：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankAccountMobile" value="" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(bankAccountMobile)*</span>
                        <span></span>
                    </dd>

                    <dt>结算户卡号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleBankAccountNo" value="" maxlength="20"  placeholder="可空"/>
                        <span class="null-star">(settleBankAccountNo)*</span>
                        <span></span>
                    </dd>

                    <dt>结算银行户名：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleAccountName" value="" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(settleAccountName)*</span>
                        <span></span>
                    </dd>

                    <dt>结算卡开户行名：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleBankName" value="工商银行" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(settleBankName)*</span>
                        <span></span>
                    </dd>

                    <dt>账户类别：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleBankAcctType" value="2" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(settleBankAcctType)*</span>
                        <span></span>
                    </dd>

                    <dt>开户行省：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleBankProvince" value="" maxlength="32"  placeholder="可空"/>
                        <span>(settleBankProvince)</span>
                        <span></span>
                    </dd>

                    <dt>开户行市：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleBankCity" value="" maxlength="32"  placeholder="可空"/>
                        <span>(settleBankCity)</span>
                        <span></span>
                    </dd>

                    <dt>开户行联行号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="settleLineCode" value="" maxlength="32"  placeholder="可空"/>
                        <span>(settleLineCode)</span>
                        <span></span>
                    </dd>

                    <dt>商户类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankDmType" value="1" maxlength="32"  placeholder="可空"/>
                        <span> class="null-star"(bankDmType)*0无积分；1有</span>
                        <span></span>
                    </dd>

                    <dt>费率类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankRateType" value="3" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(bankRateType)*1单笔,2比率,3混和</span>
                        <span></span>
                    </dd>

                    <dt>结算周期：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankSettleCycle" value="D0" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(bankSettleCycle)*</span>
                        <span></span>
                    </dd>

                    <dt>费率（‰）：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankRate" value="" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(bankRate)*</span>
                        <span></span>
                    </dd>

                    <dt>单笔费用（分）：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="bankFee" value="" maxlength="32"  placeholder="可空"/>
                        <span>(bankFee)</span>
                        <span></span>
                    </dd>

                    <dt>操作类型：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="opType" value="0" maxlength="32"  placeholder="可空"/>
                        <span class="null-star">(opType)*1-申请；2-变更</span>
                        <span></span>
                    </dd>

                    <dt>绑定用户ID：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="userId" value="" maxlength="32"  placeholder="可空"/>
                        <span>(userId)</span>
                        <span></span>
                    </dd>

                    <dt>保留字段：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="param" value="" maxlength="32"  placeholder="可空"/>
                        <span>(param)</span>
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