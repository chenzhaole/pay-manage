(function( window, undefined ) {
    "use strict";
    var auiToast = function() {
        // this.create();
    };
    var isShow = false;
    auiToast.prototype = {
        create: function(params,callback) {
            var self = this;
            var toastHtml = '';
            switch (params.type) {
                case "success":
                    var iconHtml = '<i class="aui-iconfont aui-icon-correct"></i>';
                    break;
                case "fail":
                    var iconHtml = '<i class="aui-iconfont aui-icon-close"></i>';
                    break;
                case "custom":
                    var iconHtml = params.html;
                    break;
                case "loading":
                    var iconHtml = '<div class="aui-toast-loading"></div>';
                    break;
            }

            var titleHtml = params.title ? '<div class="aui-toast-content">'+params.title+'</div>' : '';
            toastHtml = '<div class="aui-toast">'+iconHtml+titleHtml+'</div>';
            if(document.querySelector(".aui-toast"))return;
            document.body.insertAdjacentHTML('beforeend', toastHtml);
            var duration = params.duration ? params.duration : "2000";
            self.show();
            if(params.type == 'loading'){
                if(callback){
                    callback({
                        status: "success"
                    });
                };
            }else{
                setTimeout(function(){
                    self.hide();
                }, duration)
            }
        },
        show: function(){
            var self = this;
            document.querySelector(".aui-toast").style.display = "block";
            document.querySelector(".aui-toast").style.marginTop =  "-"+Math.round(document.querySelector(".aui-toast").offsetHeight/2)+"px";
            if(document.querySelector(".aui-toast"))return;
        },
        hide: function(){
            var self = this;
            if(document.querySelector(".aui-toast")){
                document.querySelector(".aui-toast").parentNode.removeChild(document.querySelector(".aui-toast"));
            }
        },
        remove: function(){
            if(document.querySelector(".aui-dialog"))document.querySelector(".aui-dialog").parentNode.removeChild(document.querySelector(".aui-dialog"));
            if(document.querySelector(".aui-mask")){
                document.querySelector(".aui-mask").classList.remove("aui-mask-out");
            }
            return true;
        },
        success: function(params,callback){
            var self = this;
            params.type = "success";
            return self.create(params,callback);
        },
        fail: function(params,callback){
            var self = this;
            params.type = "fail";
            return self.create(params,callback);
        },
        custom:function(params,callback){
            var self = this;
            params.type = "custom";
            return self.create(params,callback);
        },
        loading:function(params,callback){
            var self = this;
            params.type = "loading";
            return self.create(params,callback);
        }
    };
    window.auiToast = auiToast;
})(window);
;(function(window, undefined) {
	"use strict";
	var auiDialog = function() {};
	var isShow = false;
	auiDialog.prototype = {
		params: {
			title: '',
			msg: '',
			buttons: ['取消', '确定'],
			input: false
		},
		create: function(params, callback) {
			var self = this;
			var dialogHtml = '';
			var buttonsHtml = '';
			var headerHtml = params.title ? '<div class="aui-dialog-header">' + params.title + '</div>' : '<div class="aui-dialog-header">' + self.params.title + '</div>';
			if(params.input) {
				params.text = params.text ? params.text : '';
				var msgHtml = '<div class="aui-dialog-body"><input type="text" placeholder="' + params.text + '"></div>';
			} else {
				var msgHtml = params.msg ? '<div class="aui-dialog-body">' + params.msg + '</div>' : '<div class="aui-dialog-body">' + self.params.msg + '</div>';
			}
			var buttons = params.buttons ? params.buttons : self.params.buttons;
			if(buttons && buttons.length > 0) {
				for(var i = 0; i < buttons.length; i++) {
					buttonsHtml += '<div class="aui-dialog-btn" tapmode button-index="' + i + '">' + buttons[i] + '</div>';
				}
			}
			var footerHtml = '<div class="aui-dialog-footer">' + buttonsHtml + '</div>';
			dialogHtml = '<div class="aui-dialog">' + headerHtml + msgHtml + footerHtml + '</div>';
			document.body.insertAdjacentHTML('beforeend', dialogHtml);
			// listen buttons click
			var dialogButtons = document.querySelectorAll(".aui-dialog-btn");
			if(dialogButtons && dialogButtons.length > 0) {
				for(var ii = 0; ii < dialogButtons.length; ii++) {
					dialogButtons[ii].onclick = function() {
						if(callback) {
							if(params.input) {
								callback({
									buttonIndex: parseInt(this.getAttribute("button-index")) + 1,
									text: document.querySelector("input").value
								});
							} else {
								callback({
									buttonIndex: parseInt(this.getAttribute("button-index")) + 1
								});
							}
						};
						self.close();
						return;
					}
				}
			}
			self.open();
		},
		open: function() {
			if(!document.querySelector(".aui-dialog")) return;
			var self = this;
			document.querySelector(".aui-dialog").style.marginTop = "-" + Math.round(document.querySelector(".aui-dialog").offsetHeight / 2) + "px";
			if(!document.querySelector(".aui-mask")) {
				var maskHtml = '<div class="aui-mask"></div>';
				document.body.insertAdjacentHTML('beforeend', maskHtml);
			}
			// document.querySelector(".aui-dialog").style.display = "block";
			setTimeout(function() {
				document.querySelector(".aui-dialog").classList.add("aui-dialog-in");
				document.querySelector(".aui-mask").classList.add("aui-mask-in");
				document.querySelector(".aui-dialog").classList.add("aui-dialog-in");
			}, 10)
			document.querySelector(".aui-mask").addEventListener("touchmove", function(e) {
				e.preventDefault();
			})
			document.querySelector(".aui-dialog").addEventListener("touchmove", function(e) {
				e.preventDefault();
			})
			return;
		},
		close: function() {
			var self = this;
			document.querySelector(".aui-mask").classList.remove("aui-mask-in");
			document.querySelector(".aui-dialog").classList.remove("aui-dialog-in");
			document.querySelector(".aui-dialog").classList.add("aui-dialog-out");
			if(document.querySelector(".aui-dialog:not(.aui-dialog-out)")) {
				setTimeout(function() {
					if(document.querySelector(".aui-dialog")) document.querySelector(".aui-dialog").parentNode.removeChild(document.querySelector(".aui-dialog"));
					self.open();
					return true;
				}, 200)
			} else {
				document.querySelector(".aui-mask").classList.add("aui-mask-out");
				document.querySelector(".aui-dialog").addEventListener("webkitTransitionEnd", function() {
					self.remove();
				})
				document.querySelector(".aui-dialog").addEventListener("transitionend", function() {
					self.remove();
				})
			}
		},
		remove: function() {
			if(document.querySelector(".aui-dialog")) document.querySelector(".aui-dialog").parentNode.removeChild(document.querySelector(".aui-dialog"));
			if(document.querySelector(".aui-mask")) {
				document.querySelector(".aui-mask").classList.remove("aui-mask-out");
			}
			return true;
		},
		alert: function(params, callback) {
			var self = this;
			return self.create(params, callback);
		},
		prompt: function(params, callback) {
			var self = this;
			params.input = true;
			return self.create(params, callback);
		}
	};
	window.auiDialog = auiDialog;
})(window);


apiready = function() {
	api.parseTapmode();
}
$.toast = new auiToast({});

function showTip(str) {
	$.toast.custom({
		title: str,
		html: "",
		duration: 800
	})
}


		//发送验证码
			
			function myCode(ms) { 
				var i = ms; // 倒计时时间
				function time(t) {
					if(i == 0) {
						t.removeClass('bg-gray');
						t.html('重新获取');
						i = ms; // 与声明的倒计时时间相同
						t.bind('click'); // 时间结束后，再次绑定click事件
						
					} else {
						var timeWord = i < 10 ? "0" + i : i;
						t.html(timeWord + 's'); // 显示的倒计时
						t.addClass('bg-gray');
						t.unbind('click'); // 取消click事件
						i--;
						setTimeout(function() {
							time(t);
						}, 1000);
					}
				}
				$(document).on('click', '.getConfirmCode', function(e) {
					var mobile = $(".mobile").val();
					if(/^1(3|5|7|8)\d{9}$/.test(mobile)) {
						if($(e.target).hasClass('bg-gray')) {
							return false;
						} else {
							time($(this));
						}
						//  发送验证码
						$.ajax();
			
					} else {
						showTip("请输入正确的手机号！");
					}
				})
		}
			

function checkMobile(e) {
	console.log(12345)
	var s = $(e).val();
	if((!(/^1(3|5|7|8)\d{9}$/.test(s)) && s.length == 11) || s.length > 11) {
		showTip("请输入正确手机号");
		$(e).val("");
	}
}
function checkName(e) {
	var s = $(e).val(),
		reg = /^[\u4E00-\u9FA5]{2,4}$/;
	if(s.length < 2) {
		reg = /^[\u4E00-\u9FA5]+/;
		if(!reg.test(s)) {
			showTip("请输入2到4个汉字")
		}
	};
	if(!reg.test(s)) {
		showTip("请输入2到4个汉字");
		$(e).val('')
	}
}

// 验证码
function checkCode(e) {
	var s = $(e).val();
	if(s.length < 4) return;
	if(s.length > 6) {
		showTip("请输入4-6位数字验证码");
		$(e).val("");
	};
}
//长度验证
function checkLength(e, n, t) {
	var s = $(e).val();
	if(s.length > n) {
		$(e).val(s.substr(0, n));
		if(t){
			showTip("请输入" + n + "位" + t);
		}
		
	}
}
//身份证号
function checkIden(e) {
	var v = $(e).val();
	if(v.length == 18) {
		if(!IdentityCodeValid(v)) {
			showTip('请确认您的身份证号正确！');
		}
	}
}

function IdentityCodeValid(code) {
	var city = {
		11: "北京",
		12: "天津",
		13: "河北",
		14: "山西",
		15: "内蒙古",
		21: "辽宁",
		22: "吉林",
		23: "黑龙江 ",
		31: "上海",
		32: "江苏",
		33: "浙江",
		34: "安徽",
		35: "福建",
		36: "江西",
		37: "山东",
		41: "河南",
		42: "湖北 ",
		43: "湖南",
		44: "广东",
		45: "广西",
		46: "海南",
		50: "重庆",
		51: "四川",
		52: "贵州",
		53: "云南",
		54: "西藏 ",
		61: "陕西",
		62: "甘肃",
		63: "青海",
		64: "宁夏",
		65: "新疆",
		71: "台湾",
		81: "香港",
		82: "澳门",
		91: "国外 "
	};
	var tip = "";
	var pass = true;
	if(!code || !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[12])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(code)) {
		tip = "身份证号格式错误";
		pass = false;
	} else if(!city[code.substr(0, 2)]) {
		tip = "地址编码错误";
		pass = false;
	} else {
		//18位身份证需要验证最后一位校验位
		if(code.length == 18) {
			code = code.split('');
			//∑(ai×Wi)(mod 11)
			//加权因子
			var factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
			//校验位
			var parity = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2];
			var sum = 0;
			var ai = 0;
			var wi = 0;
			for(var i = 0; i < 17; i++) {
				ai = code[i];
				wi = factor[i];
				sum += ai * wi;
			}
			var last = parity[sum % 11];
			if(parity[sum % 11] != code[17]) {
				tip = "校验位错误";
				pass = false;
			}
		}
	}
	// if(!pass) return (tip);
	return pass;
}