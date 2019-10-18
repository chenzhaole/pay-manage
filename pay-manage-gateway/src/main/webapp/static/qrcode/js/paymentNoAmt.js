
$(function(){
    // 获取需要操作的dom元素
    var numKey = $(".keyNum .im-kb-bt"), submitKey = $(".im-kb-submit").first(), amtInput = $(".payNum").first(),
        deletekey = $(".im-kb-del").first(); 
        //laoding = $(".loading").first();

    // 为每个个数字添加监听触摸事件（触摸开始；触摸结束两个动作）
    numKey.each(function () {
        $(this).on("touchstart", function (e) {
            e.preventDefault();
            submitKey.css({"background-color": "#e72327",  "color": "#fff"});//确定按钮非空样式
            $(this).addClass("active");
        }),  $(this).on("touchend", function (e) {
            e.preventDefault();
            $(this).removeClass("active");
            var t = $(this).text(), currAmt = amtInput.text();
            if (currAmt) {
                if (-1 === currAmt.indexOf(".")) {
                    if( currAmt.length < 5){
                        amtInput.text(currAmt + t);
                    }
                } else if (0 === currAmt.indexOf(".")) {
                    amtInput.text("0" + currAmt + t);
                } else {
                    var a = currAmt.split(".");
                    if(2 === a.length && "." !== t && a[1].length < 2){
                        amtInput.text(currAmt + t);
                    }
                }
            } else {
                if("0" === t || "." === t){
                    amtInput.text(currAmt + "0.");
                } else {
                    amtInput.text(currAmt + t);
                }
            }
        });
    });

    // 为删除图标添加监听触摸事件
    deletekey.on("touchstart", function (e) {
        e.preventDefault();
        $(this).addClass("active");
    }), deletekey.on("touchend", function (e) {
        e.preventDefault();
        $(this).removeClass("active")
        var t =  amtInput.text();
        amtInput.text(t.substring(0, t.length - 1));
        if(1 === t.length){
            submitKey.css({"background-color": "#999999", "color": "#fff"});//清除金额后,确定按钮恢复默认样式
        }
    });

    // 为确认图标添加监听触摸事件
    submitKey.on("touchstart", function (e) {
        e.preventDefault(), submitKey.css({"background ": "#219fdb"});
    }), submitKey.on("touchend", function (t) {
        t.preventDefault();
        var n = amtInput.text();
        if("0" == n || "0.0" == n || "0.00" == n) {
            submitKey.css({"background ": "#219fdb"});
            alertError("付款金额不能为零！")
            return;
        }
        if(/^\d{1,5}(\.\d{1,2})?$/.test(n)) {
            //laoding.css("display", "block");
            postRequest();
        } else {
            submitKey.css({"background ": "#219fdb"});
            alertError("请输入正确金额!")
        } 
    })

});
