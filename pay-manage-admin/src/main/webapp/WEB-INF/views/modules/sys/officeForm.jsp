<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>微门户管理</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/treeview.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#name").focus();
            $("#inputForm").validate({
                rules: {
                    companyLogoFile: {
                        extension: "gif|jpg|png|bmp|jpeg"
                    },
                    qrcodeFile: {
                        extension: "gif|jpg|png|bmp|jpeg"
                    },
                    companyIconFile: {
                        extension: "gif|jpg|png|bmp|jpeg"
                    }
                },
                messages: {
                    companyLogoFile: {
                        extension: "请选择正确的图片文件，格式包括：gif,jpg,png,bmp,jpeg"
                    },
                    qrcodeFile: {
                        extension: "请选择正确的图片文件，格式包括：gif,jpg,png,bmp,jpeg"
                    },
                    companyIconFile: {
                        extension: "请选择正确的图片文件，格式包括：gif,jpg,png,bmp,jpeg"
                    }
                },
                submitHandler: function (form) {
                    var ids = [], nodes = tree.getCheckedNodes(true);
                    for (var i = 0; i < nodes.length; i++) {
                        if (nodes[i].level == 0) {
                            continue;
                        }
                        if (nodes[i].isParent) {
                            continue;
                        }
                        ids.push(nodes[i].id);
                    }

                    var isValidate = true;
                    //验证短信联系人规则
                    $("#phone").val($.trim($("#phone").val()).replace('，', ','));
                    var phoneTest = /1[3|5|7|8|][0-9]{9}/;
                    if ($("#phone").val().split(',').length > 0) {
                        var testArr = new Array();
                        var phoneArray = $("#phone").val().split(',');
                        for (var i = 0; i < phoneArray.length; i++) {
                            if (phoneArray[i] != '' && phoneArray[i].length != 11) {
                                top.$.jBox.tip('第 ' + eval(i + 1) + ' 个手机报表接收号码 ' + phoneArray[i] + ' 长度错误', '系统提示');
                                isValidate = false;
                                return false;
                            }
                            if (phoneArray[i] != '' && !phoneTest.test(phoneArray[i])) {
                                top.$.jBox.tip('第 ' + eval(i + 1) + ' 个手机报表接收号码 ' + phoneArray[i] + ' 格式错误', '系统提示');
                                isValidate = false;
                                return false;
                            }
                            if (phoneArray[i] != ''  && testArr.length > 0 && in_array(phoneArray[i], testArr)) {
                                top.$.jBox.tip('第 ' + eval(i + 1) + ' 个手机报表接收号码 ' + phoneArray[i] + ' 重复了', '系统提示');
                                isValidate = false;
                                return false;
                            }
                            testArr.push(phoneArray[i]);
                        }
                    }

                    if (isValidate) {
                        $("#stationIds").val(ids);
                        loading('正在提交，请稍等...');
                        form.submit();
                    }
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });

            var setting = {
                check: {enable: true, nocheckInherit: true}, view: {selectedMulti: false},
                data: {simpleData: {enable: true}}, callback: {
                    beforeClick: function (id, node) {
                        tree.checkNode(node, !node.checked, true, true);
                        return false;
                    }
                }
            };

            // 用户-菜单
            var zNodes = [
                    <c:forEach items="${treeData}" var="station">{
                    id: "${station.id}",
                    pId: "${station.pId}",
                    name: "${station.name}"
                },
                </c:forEach>];
            // 初始化树结构
            var tree = $.fn.zTree.init($("#stationTree"), setting, zNodes);
            // 默认选择节点
            var ids = "${stationIds}".split(",");
            for (var i = 0; i < ids.length; i++) {
                var node = tree.getNodeByParam("id", ids[i]);
                try {
                    tree.checkNode(node, true, true);
                } catch (e) {
                }
            }

            function in_array(search,array){
                for(var i in array){
                    if(array[i]==search){
                        return true;
                    }
                }
                return false;
            }
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sys/office/">微门户列表</a></li>
    <li class="active"><a
            href="${ctx}/sys/office/form?id=${office.id}&parent.id=${office.parent.id}">微门户<shiro:hasPermission
            name="sys:office:edit">${not empty office.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="sys:office:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="office" action="${ctx}/sys/office/save" method="post" class="form-horizontal"
           enctype="multipart/form-data">
    <form:hidden path="id"/>
    <form:hidden path="parent.id" value="1"/>
    <form:hidden path="portalInfo.officeId"/>
    <form:hidden path="portalInfo.status"/>
    <tags:message content="${message}"/>
    <%--<div class="control-group">--%>
    <%--<label class="control-label">上级微门户:</label>--%>
    <%--<div class="controls">--%>
    <%--<tags:treeselect id="office" name="parent.id" value="${office.parent.id}" labelName="parent.name" labelValue="${office.parent.name}"--%>
    <%--title="微门户" url="/sys/office/treeData" extId="${office.id}" cssClass="required"/>--%>
    <%--</div>--%>
    <%--</div>--%>
    <div class="control-group">
        <label class="control-label">归属区域:<span style="color: red"><span style="color: red;">*</span></span></label>

        <div class="controls">
            <tags:treeselect id="area" name="area.id" value="${office.area.id}" labelName="area.name"
                             labelValue="${office.area.name}"
                             title="区域" url="/sys/area/treeData" cssClass="required"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">微门户名称:<span style="color: red"><span style="color: red;">*</span></span></label>

        <div class="controls">
            <form:input path="name" htmlEscape="false" maxlength="50" class="required"/>
        </div>
    </div>
    <div class="control-group" style="display: none;">
        <label class="control-label">微门户编码:</label>

        <div class="controls">
            <form:input path="code" htmlEscape="false" maxlength="10"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">联系地址:</label>

        <div class="controls">
            <form:input path="address" htmlEscape="false" maxlength="50"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">邮政编码:</label>

        <div class="controls">
            <form:input path="zipCode" htmlEscape="false" maxlength="6" class="zipCode"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">负责人:</label>

        <div class="controls">
            <form:input path="master" htmlEscape="false" maxlength="50"/>
        </div>
    </div>
    <%--<div class="control-group">--%>
    <%--<label class="control-label">电话:</label>--%>
    <%--<div class="controls">--%>
    <%--<form:input path="phone" htmlEscape="false" maxlength="50" class="simplePhone"/>--%>
    <%--</div>--%>
    <%--</div>--%>
    <div class="control-group">
        <label class="control-label">手机报表接收号码:</label>

        <div class="controls">
            <form:textarea path="phone" htmlEscape="false" rows="2" maxlength="100" class="input-xlarge"/>&nbsp;多个手机号码以英文逗号(,)分隔
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">传真:</label>

        <div class="controls">
            <form:input path="fax" htmlEscape="false" maxlength="15" class="fax"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">企业邮箱:</label>

        <div class="controls">
            <form:input path="email" htmlEscape="false" maxlength="50" class="email"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">类型:<span style="color: red"><span style="color: red;">*</span></span></label>

        <div class="controls">
            <form:select path="portalInfo.type">
                <form:options items="${fns:getDictList('portal_type')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">星级:<span style="color: red"><span style="color: red;">*</span></span></label>

        <div class="controls">
            <form:select path="portalInfo.starLevel">
                <form:options items="${fns:getDictList('portal_star_level')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">微门户网页端Logo:</label>

        <div class="controls">
            <c:if test="${not empty office.portalInfo.companyLogo}">
                <img style="max-height: 200px;max-width: 200px;" src="${imgServer}${office.portalInfo.companyLogo}">
                <br>
            </c:if>
            <input id="companyLogoFile" name="companyLogoFile" type="file" >
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">微门户手机端图标:</label>
        <div class="controls">
            <c:if test="${not empty office.portalInfo.companyIcon}">
                <img style="max-height: 200px;max-width: 200px;" src="${imgServer}${office.portalInfo.companyIcon}">
                <br>
            </c:if>
            <input id="companyIconFile" name="companyIconFile" type="file" >请上传200*200尺寸的图片
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">域名地址:</label>

        <div class="controls">
            <form:input path="portalInfo.domainAddress" htmlEscape="false" maxlength="250" class="url"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">微信公众号:</label>

        <div class="controls">
            <form:input path="portalInfo.wechatAccount" htmlEscape="false" maxlength="50"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">微信公众号二维码:</label>

        <div class="controls">
            <c:if test="${not empty office.portalInfo.wechatBarcode}">
                <img style="max-height: 200px;max-width: 200px;" src="${imgServer}${office.portalInfo.wechatBarcode}">
                <br>
            </c:if>
            <input id="qrcodeFile" name="qrcodeFile" type="file" >
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">客服电话:</label>

        <div class="controls">
            <form:input path="portalInfo.customerServicePhone" htmlEscape="false" maxlength="12" class="simplePhone"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">客服邮箱:</label>

        <div class="controls">
            <form:input path="portalInfo.customerServiceEmail" htmlEscape="false" maxlength="50" class="email"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">财务账户:</label>

        <div class="controls">
            <form:input path="portalInfo.financialAccountInfo" htmlEscape="false" maxlength="100" class="digits"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">业务招商接口人:</label>

        <div class="controls">
            <form:input path="portalInfo.businessInvestmentInfo" htmlEscape="false" maxlength="100"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">公司简介:</label>

        <div class="controls">
            <form:textarea path="portalInfo.description" htmlEscape="false" rows="3" maxlength="200"
                           class="input-xlarge"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">关于我们:</label>

        <div class="controls">
            <form:textarea id="about" htmlEscape="false" path="portalInfo.about" rows="4" maxlength="200"
                           class="input-xxlarge"/>
            <tags:ckeditor replace="about" uploadPath="/portal"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备注:</label>

        <div class="controls">
            <form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">关联车站:</label>

        <div class="controls">
            <div id="stationTree" class="ztree" style="margin-top:3px;float:left;"></div>
            <input id="stationIds" name="stationIds" type="hidden">
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="sys:office:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                           value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回"
               onclick="window.location.href='${ctx}/sys/office/'"/>
    </div>
</form:form>
</body>
</html>