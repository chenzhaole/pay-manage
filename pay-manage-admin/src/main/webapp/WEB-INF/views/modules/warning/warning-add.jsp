<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>通道管理</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">
        $(function(){
            $("#form").validate({
                debug: false, //调试模式取消submit的默认提交功能
                focusInvalid: false, //当为false时，验证无效时，没有焦点响应
                onkeyup: false,
                submitHandler: function(form){   //表单提交句柄,为一回调函数，带一个参数：form
                    document.forms[0].submit();
                },
                errorPlacement:function(error,element) {
                    error.appendTo(element.parent());
                },
                rules:{
                    code:{
                        required:true,
                    },
                    amount:{
                        required:true,
                        number:true,
                        digits:true
                    }
                },
                messages:{
                    amount:{
                        number:'数字格式不对',
                        digits:'数字格式不对'
                    }

                }
            });
        });

        function change(select1,select2,type){
            var value =$('#'+select1).val();
            var text = $('#'+select1).find("option:selected").text();
            if(value!=''){
                $('#type').val(type);
                $('#'+select2).attr("disabled",true);
                $('#name').val(text)
            }else{
                $('#'+select2).attr("disabled",false);
                $('#type').val('');
                $('#name').val('')
            }

        }


    </script>
</head>
<body>

<div class="breadcrumb">
    <label><th><a href="#">结算监控</a> > <a href="#"><b>已结算新增</b></a></th></label>
</div>

<form id="form" action="${ctx}/warning/doAdd" method="post" >
    <input type="hidden" name="type" value="" id="type">
    <input type="hidden" name="name" value="" id="name">
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">上游通道</label>
                    <div class="controls">
                        <select name="code" id="chanId"  class="selectpicker bla bla bli" data-live-search="true" onchange="change('chanId','chanMchtPaytypeId','0')">
                            <option value="">---请选择---</option>
                            <c:forEach var="chanInfo" items="${chanInfoList}">
                                <option value="${chanInfo.chanCode}" <c:if test="${code eq chanInfo.id}">selected</c:if> >${chanInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">通道商户支付方式</label>
                    <div class="controls">
                        <select name="code" id="chanMchtPaytypeId" class="selectpicker bla bla bli" data-live-search="true" onchange="change('chanMchtPaytypeId','chanId','1')">
                            <option value="">---请选择---</option>
                            <c:forEach var="chanMchtPayType" items="${chanMchtPaytypeList}">
                                <option value="${chanMchtPayType.id}" <c:if test="${code eq chanMchtPayType.id}">selected</c:if> >${chanMchtPayType.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label" >金额<span style="color: red;"><span style="color: red;">*</span></span></label>
                    <div class="controls">
                        <input name="amount" value="" placeholder="请输入字母或数字" class="input-small" type="text"/>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label" >备注</label>
                    <div class="controls">
                        <textarea name="operateMsg" placeholder="" style="width:350px;" id="remark"
                                  rows="3"></textarea>
                    </div>
                </div>
            </td>
        </tr>

    </table>
    <div class="breadcrumb">
        <input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
        <input name="btnSubmit" class="btn btn-primary" type="submit" value="保存"  style="margin-left: 5px;">
    </div>


</form>
</body>
</html>