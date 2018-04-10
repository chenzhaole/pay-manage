/**
 *  start 选择银行卡
 */
$(document).on("click", ".list-container li", function(e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).addClass("active").siblings().removeClass("active");
});
/**
 *  end 选择银行卡
 */


/**
 *  start 解绑按钮
 * @param event
 * @param el
 */
function unbind(event, el) {
    var id = $(el).parent().attr("cid");
    layer.open({
        type: 0,
        title: '解绑',
        area: ['390px', '200px'],
        shade: 0,
        //					maxmin: true,
        offset: [ //为了演示，随机坐标
            ($(window).height() / 2 - 300 / 2), ($(window).width() / 2 - 390 / 2)
        ],
        content: '<img src="${ctxStatic}/images/popb.png" style="display:inline-block;height:36px;width:36px;margin-right:8px;"><span style="color:#666;font-size:14px;">你确定要解绑该快捷卡吗？</span>',
        btn: ['暂不解绑', '解绑']
        ,
        yes: function() {
            layer.closeAll();
        },
        btn2: function() {

            //解绑成功
            $(el).parent().parent().remove();
            alert("解绑" + id + "成功");

            $(".nobind").fadeIn();
            setTimeout(function() {
                $(".nobind").fadeOut();
            }, 1000)

            layer.closeAll();
        },
        zIndex: layer.zIndex //重点1
        ,
        success: function(layero) {
            layer.setTop(layero); //重点2
        }
    });
};
/**
 *  end 解绑按钮
 */


/**
 * start 更多银行卡
 */
$(".morecard").on("click", function() {
    $(".list-container").append('<li class="clearfix"><div class="flag"><div class="dotbox"><div></div></div></div><div class="cardinfo" cid="0000"><img src="${ctxStatic}/images/logo/北京银行.svg" alt="北京银行" /><span> <b>北京银行</b> 尾号4994  储蓄卡 | 快捷支付</span><span class="unbind" onclick="unbind(event,this)">解绑</span></div></li>');
});
/**
 *  end 更多银行卡
 */

/**
 *  start 新增卡支付
 */
$(".newcard").on("click", function() {
    layer.open({
        type: 2,
        area: ['800px', '600px'],
        fixed: false, //不固定
        //		maxmin: true,
        title: '',
        content: ['./addcard.html', 'no'],
        success: function(layero, index) {
            var body = layer.getChildFrame('body', index);
            body.find("#price").val(price);
        }
    });
});
/**
 *  end 新增卡支付
 */

/**
 *  start 快捷支付
 */
$(".speedpay").on("click", function() {
    layer.open({
        type: 2,
        area: ['800px', '600px'],
        fixed: false, //不固定
        //		maxmin: true,
        title: '',
        content: ['./speedpay.html', 'no'],
        success: function(layero, index) {
            var body = layer.getChildFrame('body', index),
                item = body.find(".layui-tab-title li");
            item.on("click", function(e) {
                $(this).addClass("layui-this").siblings().removeClass("layui-this");


                //弹窗-start
                layer.open({
                    type: 0,
                    title: '请在完成支付后选择',
                    area: ['390px', '200px'],
                    shade: 0,
                    id:"speedpop",
                    content: '<img src="${ctxStatic}/images/popb.png"  class="speedImg" style="height:36px;width:36px;margin-right:8px;"><div style="display:inline-block;vertical-align:middle;" class="speedtxt"><p>支付成功：<span class="weightFont">去支付成功页面</span></p><p>支付失败：<span  class="weightFont">重新选择其他支付方式</span></p></div>',
                    btn: ['选择其他支付方式', '重新支付'],
                    btnAlign:"c",
                    yes: function() {
                        layer.closeAll();
                    },
                    btn2: function() {

                        //重新支付


                        layer.closeAll();
                    },
                    zIndex: layer.zIndex //重点1
                    ,
                    success: function(layero,index) {
                        layer.setTop(layero);
                        var body = layer.getChildFrame('body', index),
                            item = body.find(".layui-layer-title");
                        item.css({"background":"#FFF",})

                    }
                });
                //弹窗-end

                window.open($(this).attr("href"));
            })
        },
    })
});
/**
 *  end 快捷支付
 */


/**
 *  start 银行卡立即支付
 */
$(".paybtn").on("click", function() {
    var cid = $(".list-container .active .cardinfo").attr("cid");
    layer.open({
        type: 2,
        title: false,
        closeBtn: 1, //不显示关闭按钮
        shade: [0],
        area: ['600px', '260px'],
        //offset: '', //右下角弹出
        //time: 2000, //2秒后自动关闭
        anim: 2,
        title: '',
        //maxmin:true,
        content: ['pay.html', 'no'], //iframe的url，no代表不显示滚动条
        success: function(layero, index) {
            var body = layer.getChildFrame('body', index);
            //							    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
            body.find("#cid").val(cid);
            body.find(".layui-btn").on("click", function() {
                var code = body.find('input').val();
                if(code !== '') {
                    layer.closeAll();

                    cardpay(price, cid, code);
                }
            })
        },
        end: function() { //此处用于演示

        }
    });
});
/**
 *  end 银行卡立即支付
 */