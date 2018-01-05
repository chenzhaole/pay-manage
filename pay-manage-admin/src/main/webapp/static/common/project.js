/**
 * Created by Administrator on 2015/1/27.
 */
function viewProjectDetail(href){
    top.$.jBox.open('iframe:'+href,'项目详情',$(top.document).width()-220,$(top.document).height()-220,{
        buttons:{"关闭":true},
        loaded:function(h){
            $(".jbox-content", top.document).css("overflow-y","hidden");
            //$(".nav,.form-actions,[class=btn]", h.find("iframe").contents()).hide();
            $(".form-actions,[class=btn],[class=deleteImage]", h.find("iframe").contents()).hide();
            $(".viewShow", h.find("iframe").contents()).show();
            $("input,select,textarea", h.find("iframe").contents()).attr("disabled", true);;
            $("body", h.find("iframe").contents()).css("margin","10px");
        }
    });
    return false;
}