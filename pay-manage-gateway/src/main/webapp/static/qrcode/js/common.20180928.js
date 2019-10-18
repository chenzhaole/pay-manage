
/**
 * 构造post请求
 */
function postRequest() {
    var sessionId = $("input[name='sessionId']")[0].value;
    var transAmt = $(".payNum").first().text();
    var postUrl = $("input[name='postUrl']")[0].value;
    var params = {
        "sessionId" : sessionId,
        "transAmt" : transAmt
    };
    if(isEmpty(postUrl)) {
        //post("/qrcGtwWeb-web/front/confirmOrder", params);
        post("${ctx}/gwqr/confirmPage/code/data", params);
    } else {
        post(postUrl, params);
    }
}

/**
 * 模拟表单发起post请求
 * @param URL：post服务器地址
 * @param PARAMS ： post 数据
 */
function post(URL, PARAMS) {
    var temp = document.createElement("form");
    temp.action = URL;
    temp.method = "post";
    temp.style.display = "none";
    for ( var x in PARAMS) {
        var opt = document.createElement("input");
        opt.name = x;
        opt.value = PARAMS[x];
        temp.appendChild(opt);
    }
    document.body.appendChild(temp);
    temp.submit();
}

/**
 * 弹出错误提示框
 * @param msg   错误信息
 */
function alertError(msg) {
    hintWrapper = $(".hint-wrapper").first(), hint = $(".hint").first()
    hint.text(msg);
    hintWrapper.css("display", "block");
    setTimeout(function () {
        hintWrapper.css("display", "none");
    }, 2e3)
}

function isEmpty(str){
    if(str == null || str == undefined || $.trim(str) == ''){
        return true;
    }
    return false;
}
