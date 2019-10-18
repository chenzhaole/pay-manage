function showTip(e, f) {
    if (f = "undefined")
        f = "modalStyle-2";
    $("#" + f).find(".modal-body p").html(e);
    $("object").hide();
    $(".btn-tip").click();
}


function toLogin() {
    if ($('input[name="username"]').val() == '') {
        showTip('账号不能为空');
        return false;
    } else {
        var regu = '[a-zA-Z0-9_]{6,32}';
        var reg = new RegExp(regu);
        if (!reg.test($('input[name="username"]').val())) {
            showTip('账号错误，请重新输入');
            return false;
        }
    }

    if ($('input[name="password"]').val() == '') {
        showTip('密码不能为空');
        return false;
    }

    var dragText = $('.drag_text').text();

    if (!dragText || dragText == '' || dragText != '验证通过') {
        showTip('验证未通过');
        return false;
    }

    $('#form1').submit();

}
// 点击密码过期提示按钮，判断是否有预留信息
$("#remindMsg").click(function () {
    var showReservedMsgFlag = $("#showReservedMsgFlag").val();
    if (showReservedMsgFlag == "1") {
        $("#yuliuText1").show();
        $("#yuliuText2").hide();
        $("#button1").show();
        $("#button2").hide();
        $(".btn-tip3").click();
    } else {
        login($(this));
    }

})

// 捕获登录回车键
$(document).keyup(function (e) {// 捕获文档对象的按键弹起事件
    if (e.keyCode == 13) {// 按键信息对象以参数的形式传递进来了
        toLogin();
    }
});
// 密码找回
function findPwd() {
    $('#step').val(new Date().getTime());
    $('#fd').submit();
}

// 全屏滚动
$(function () {
    var n = 0;
    var imgLength = $(".b-img a").length;
    var ctWidth = imgLength * 100;
    var itemWidth = 1 / imgLength * 100;
    $(".b-img").width(ctWidth + "%");
    $(".b-img a").width(itemWidth + "%");
    $(".b-list").width(imgLength * 30);
    if (imgLength > 1) {
        for (var i = 0; i < imgLength; i++) {
            var listSpan = $("<span></span>")
            $(".b-list").append(listSpan);
        }
    }
    $(".b-list span:eq(0)").addClass("spcss").siblings("span").removeClass(
        "spcss");
    $(".bar-right em").click(
        function () {
            if (n == imgLength - 1) {
                n = 0
            } else {
                n++;
            }
            var ctPosit = n * 100;
            $(".b-img").animate({
                "left": "-" + ctPosit + "%"
            }, 1000);
            $(".b-list span:eq(" + n + ")").addClass("spcss").siblings(
                "span").removeClass("spcss");

        })
    $(".bar-left em").click(
        function () {
            if (n == 0) {
                var stPosit = imgLength * 100;
                var etPosit = (imgLength - 1) * 100;
                $(".banner").prepend($(".b-img").clone());
                $(".b-img:first").css("left", "-" + stPosit + "%");
                $(".b-img:last").animate({
                    "left": "100%"
                }, 1000);
                $(".b-img:first").animate({
                    "left": "-" + etPosit + "%"
                }, 1000);
                var setTime0 = setTimeout(function () {
                    $(".banner .b-img:last").remove();
                }, 1000);
                n = imgLength - 1;
                $(".b-list span:eq(" + n + ")").addClass("spcss").siblings(
                    "span").removeClass("spcss");
            } else {
                n--;
                var ctPosit = n * 100;
                $(".b-img").animate({
                    "left": "-" + ctPosit + "%"
                }, 1000);
                $(".b-list span:eq(" + n + ")").addClass("spcss").siblings(
                    "span").removeClass("spcss");
            }
        })
    $(".b-list span").click(function () {
        var lsIndex = $(this).index();
        n = lsIndex;
        var ctPosit = n * 100;
        $(".b-img").animate({
            "left": "-" + ctPosit + "%"
        }, 1000);
        $(this).addClass("spcss").siblings("span").removeClass("spcss");
    })

    function rollEnvent() {
        if (n == imgLength - 1) {
            n = 0
        } else {
            n++;
        }
        var ctPosit = n * 100;
        $(".b-img").animate({
            "left": "-" + ctPosit + "%"
        }, 1000);
        $(".b-list span:eq(" + n + ")").addClass("spcss").siblings("span")
            .removeClass("spcss");

    }

    var slidesetInterval = setInterval(rollEnvent, 4000);
    // $(".banner").hover(function() { clearInterval(slidesetInterval); },
    // function() { slidesetInterval = setInterval(rollEnvent, 4000); });

    $(".slipping-area").css("height", "1000px");

    $(".slipping-area .li img").css("height", "1000px");



})


function setReservedMsg() {
    $("#yuliuText1").hide();
    $("#yuliuText2").show();
    $("#button1").hide();
    $("#button2").show();
}

function saveReservedMsg() {
    var context = $('#context').val();
    var username = $('input[name="username"]').val();
    var reservedMsg = $('input[name="reservedMsg"]').val();
    // $(".btn-tip3").css("display","none");
    // $("#modalStyle-3").hide();
    $.ajax({
        type: 'POST',
        url: context + '/saveReservedMsg',
        data: {
            "username": username,
            "reservedMsg": reservedMsg
        },
        dataType: 'json',
        success: function (data) {
            if (data.respCode == '000000') {
                $("#yuliuText1").show();
                $("#yuliuText2").hide();
                $("#button1").show();
                $("#button2").hide();
                $("#yuliu").html(reservedMsg);

                // $("#modalStyle-3").show();
                // $("#modalStyle-3").find(".modal-body p").html();
                $(".btn-tip3").click();
            } else {
                showTip(data.respDesc);
                return false;
            }
        },

        error: function (data) {
            if (data.status == '405') {
                window.location.reload();
            }
            showTip('网络异常，请重试');
            return false;
        }
    })
}

// 发送短信
function sendSms(obj) {
    var url = $('#context').val() + '/loginSendMsg';
    if ($('input[name="username"]').val() == '') {
        showTip('账号不能为空');
        return false;
    } else {
        var regu = '[a-zA-Z0-9_]{6,32}';
        var reg = new RegExp(regu);
        if (!reg.test($('input[name="username"]').val())) {
            showTip('账号错误，请重新输入');
            return false;
        }
    }
    var password;
    var bizSwitch0001 = $("#bizSwitch0001").val();
    if (bizSwitch0001 == "1") {
        if ($("#password").val() == '') {
            Ajax.request({
                url: $('#context').val() + "/getRandomNum?"
                + new Date().getTime(),
                type: "GET",
                async: false,
                success: function (xhr) {
                    var srand_num = pgeCtrl.trim(xhr.responseText);
                    $("#mcrypt_key").val(srand_num);
                    $("#mcryptKey").val(srand_num);
                    pgeditor.pwdSetSk(srand_num);

                }
            });
            var pwdResult = pgeditor.pwdResult();// 获取密码AES密文
            if (pwdResult == '') {
                showTip('密码不能为空！');
                return false;
            }
            pgeditor.pwdClear();
            $("#password").val(pwdResult);
            $("#_ocx_password").val(pwdResult);
            password = pwdResult;
        } else {
            password = $("#password").val();
        }
    } else {
        password = $('input[name="password"]').val();
    }
    if (password == '' || password == 'undefined') {
        showTip('密码不能为空');
        return false;
    }

    var dragText = $('.drag_text').text();
    if (!dragText || dragText == '' || dragText != '验证通过') {
        showTip('滑块验证未通过');
        return false;
    }
    $.ajax({
        type: "post",
        url: url,
        data: {
            "password": password,
            "username": $('input[name="username"]').val()
        },
        dataType: "json",
        success: function (data) {
            if (data.respCode == '000000') {
                btnColddown(obj);
            } else {
                if (bizSwitch0001 == "1") {
                    $("#password").val("");
                    $("#_ocx_password").val("");
                }
                showTip(data.respDesc);
                return false;
            }
        },
        error: function (data) {
            if (data.status == '405') {
                window.location.reload();
            }
            if (bizSwitch0001 == "1") {
                $("#password").val("");
                $("#_ocx_password").val("");
            }
            showTip("短信发送失败,请稍后重试");
            return false;
        }
    });

    // 倒计时120秒
    var codeImgCount = [];
    var sendInt = [];

    function btnColddown(btn) {
        var action = "";
        if (btn.hasClass("disabled"))
            return;
        btn.closest(".form-group").find(".help-block").show();
        btn.addClass('disabled');
        codeImgCount[action] = 120;
        btn.find("span").text("重新发送(" + codeImgCount[action] + ")");
        sendInt[action] = setInterval(function () {
            codeImgCount[action]--;
            btn.text('重新发送(' + codeImgCount[action] + ')');
            if (codeImgCount[action] <= 0) {
                btn.closest(".form-group").find(".help-block").hide();
                btn.removeClass('disabled');
                btn.text('发送验证码');
                clearInterval(sendInt[action]);
            }
        }, 1000);
    }

}
