<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>商户通道管理</title>
    <meta name="decorator" content="default"/>
    <style>
        .table th, .table td {
            border-top: none;
        }

        .control-group {
            border-bottom: none;
        }
    </style>
    <script type="text/javascript">
        function edit(id) {
            document.forms[0].action = "${ctx}/bowei/repaymentEdit?id=" + id;
            document.forms[0].submit();
        }

        function del(id) {
            if (confirm("是否确认删除ID为“" + id + "”的记录？")) {
                document.forms[0].action = "${ctx}/bowei/repaymentDel?id=" + id;
                document.forms[0].submit();
            }
        }

        function ok() {
            var op = $("#op").attr("value");
            var url = "${ctx}/platform/editPlatConfMchtChan";
            document.forms[0].action = url;
            document.forms[0].submit();
        }

        //上移
        function upTr(obj) {
            var nowTr = $(obj).parent().parent().parent().parent();
            if ($(nowTr).prev().html() == null) { //获取tr的前一个相同等级的元素是否为空
                alert("已经是最顶部了!");
                return;
            }
            {
                $(nowTr).insertBefore($(nowTr).prev());
                for (var i = 1; i < $("#confMchtTable tr").length; i++) {
                    $("#confMchtTable tr").eq(i).find("td:first").find("label").text(i);
                    $("#confMchtTable tr").eq(i).find("input[type='hidden']").val(i);
                }
            }
        }

        //下移
        function downTr(obj) {
            var nowTr = $(obj).parent().parent().parent().parent();
            if ($(nowTr).next().html() == null) { //获取tr的前一个相同等级的元素是否为空
                alert("已经是最底部了!");
                return;
            }
            {
                $(nowTr).insertAfter($(nowTr).next());
                for (var i = 1; i < $("#confMchtTable tr").length; i++) {
                    $("#confMchtTable tr").eq(i).find("td:first").find("label").text(i);
                    $("#confMchtTable tr").eq(i).find("input[type='hidden']").val(i);
                }
            }
        }
    </script>
</head>
<body>

<div class="breadcrumb">
    <label><a href="#">商户通道管理</a> > <a href="#"><b>商户通道编辑</b></a></label>
</div>

<form id="searchForm" action="${ctx}/platform/editPlatConfMchtChan" method="post">
    <tags:message content="${message}" type="${messageType}"/>
    <input type="hidden" id="op" name="op" value="${op }"/>
    <input type="hidden" name="mchtId" value="${mchtChanFormInfos.mchtId }"/>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>商户基本信息</label>
    </div>
    <tags:message content="${message}" type="${messageType}"/>
    <table class="table">
        <tr>

            <td>
                <div class="control-group">
                    <label class="control-label">商户名称</label>
                    <div class="controls">
                        <span name="productName"  placeholder=""
                              class="input-xlarge" type="text" id="mchtName">${mchtChanFormInfos.mchtName }</span>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">商户编码</label>
                    <div class="controls">
                        <span name="productCode" placeholder=""
                              class="input-xlarge" type="text" id="mchtCode"> ${mchtChanFormInfos.mchtCode }</span>
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">商户状态</label>
                    <div class="controls">
                        <span name="productCode" placeholder=""
                              class="input-xlarge" type="text" id="mchtStatus">
                            <c:if test="${mchtChanFormInfos.mchtStatus == 1}">启用</c:if>
                            <c:if test="${mchtChanFormInfos.mchtStatus == 2}">禁用</c:if></span>
                    </div>
                </div>
            </td>

        </tr>
        <%--<tr>--%>
            <%--<td colspan="3">--%>
                <%--<div class="control-group">--%>
                    <%--<label class="control-label">说明</label>--%>
                    <%--<div class="controls">--%>
                        <%--<p name="desc" placeholder="" style="width:500px;" id="desc">--%>
                            <%--${mchtChanFormInfos.mchtDesc}--%>
                        <%--</p>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</td>--%>
        <%--</tr>--%>

    </table>
    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label>通道相关信息</label>
    </div>
    <tags:message content="${message}" type="${messageType}"/>
    <table class="table" id="confMchtTable">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">序号</label>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">通道名称</label>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">状态</label>
                </div>
            </td>
        </tr>

        <%int i=0; %>
        <c:forEach items="${chanInfos}" var="chanInfo">
            <%i++; %>
        <tr>
            <input type="hidden" name="chanId<%=i-1%>" value="${chanInfo.chanId}">
            <td>
                <div class="control-group">
                    <label class="control-label"> <%=i%> </label>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">${chanInfo.chanName}</label>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <div class="controls">
                        <select name="isValid<%=i-1%>" class="input-small" id="isValid<%=i-1%>">
                            <option <c:if test="${chanInfo.isValid == 1}">selected</c:if> value="1">启用</option>
                            <option <c:if test="${chanInfo.isValid == 0}">selected</c:if> value="0">停用</option>
                        </select>
                    </div>
                </div>
            </td>
        </tr>
        </c:forEach>

    </table>

    <div class="breadcrumb">
        <input id="btnCancel" class="btn center-block" type="button" value="返 回" onclick="window.history.go(-1);"/>
        <input id="btnSubmit" class="btn btn-primary" type="button" value="保存" onclick="javascript:ok();"
               style="margin-left: 5px;">
    </div>


</form>
</body>
</html>