<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>公户账务编辑</title>
    <meta name="decorator" content="default"/>
    <link href="${ctxStatic}/city/SG_area_select.css" rel="stylesheet"/>
    <script type="text/javascript" src="${ctxStatic}/city/SG_area_select.js"></script>
    <script type="text/javascript" src="${ctxStatic}/city/iscroll.js"></script>

    <script src="https://cdn.bootcss.com/jquery-toast-plugin/1.3.1/jquery.toast.min.js" type="text/javascript"></script>
    <link href="https://cdn.bootcss.com/jquery-toast-plugin/1.3.1/jquery.toast.min.css" rel="stylesheet"
          type="text/css"/>

    <script type="text/javascript">

    </script>
</head>
<body>

<div class="breadcrumb">
    <label>
        <th><a href="#">平台管理</a> ><a href="#"><b>公户账务管理</b></a> <a href="#"><b>公户账务编辑</b></a></th>
    </label>
</div>

<form id="publicaccountform" method="post" action="${ctx}/publicaccount/edit">
    <input type="hidden" id="id" name="id" value="${accountAmount.id}">
    <table class="table">
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">公户名称</label>
                    <div class="controls">
                        ${paisMap[accountAmount.publicAccountCode].publicAccountName}
                    </div>
                </div>
            </td>

            <td>

                <div class="control-group">
                    <label class="control-label">交易时间</label>
                    <div class="controls">
                        <fmt:formatDate value="${accountAmount.tradeTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                    </div>
                </div>
            </td>

            <td>

                <div class="control-group">
                    <label class="control-label">借方发生额</label>
                    <div class="controls">
                        ${accountAmount.reduceAmount}
                    </div>
                </div>
            </td>

        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">贷方发生额</label>
                    <div class="controls">
                        ${accountAmount.addAmount}
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">账户余额</label>
                    <div class="controls">
                        ${accountAmount.balance}
                    </div>
                </div>
            </td>

            <td>

                <div class="control-group">
                    <label class="control-label">对方账号</label>
                    <div class="controls">
                        ${accountAmount.accountNo}
                    </div>
                </div>
            </td>

        </tr>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">对方账户名</label>
                    <div class="controls">
                        ${accountAmount.accountName}
                    </div>
                </div>
            </td>

            <td>
                <div class="control-group">
                    <label class="control-label">对方开户行</label>
                    <div class="controls">
                        ${accountAmount.openAccountBankName}
                    </div>
                </div>
            </td>

            <td>

                <div class="control-group">
                    <label class="control-label">摘要</label>
                    <div class="controls">
                        ${accountAmount.summary}
                    </div>
                </div>
            </td>

        </tr>
        <tr>

            <td colspan="3">
                <div class="control-group">
                    <label class="control-label">描述</label>
                    <div class="controls">
                        <textarea name="desc" placeholder="" style="width:350px;" id="desc"
                                  rows="3">${accountAmount.desc}</textarea>
                    </div>
                </div>
            </td>

        </tr>
    </table>

    <div class="breadcrumb">
        <input name="btnCancel" class="btn" type="button" value="返 回" onclick="window.history.go(-1);"/>
        <input name="btnSubmit" class="btn btn-primary" type="submit" value="保存" style="margin-left: 5px;">
    </div>

</form>
</body>
</html>