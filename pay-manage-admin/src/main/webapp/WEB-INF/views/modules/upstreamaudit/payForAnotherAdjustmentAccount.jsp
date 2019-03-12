<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>调账记录列表</title>
    <meta name="decorator" content="default"/>
    <style type="text/css">
        .wrap{
            width: 100px; //设置需要固定的宽度
        white-space: nowrap; //不换行
        text-overflow: ellipsis; //超出部分用....代替
        overflow: hidden; //超出隐藏
        }
    </style>
    <script type="text/javascript">
        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        $(document).ready(function() {
            $("#btnExport").click(function(){
                $("#searchForm").attr("action","${ctx}/platform/adjust/export?flag=1");
                $("#searchForm").submit();
                $("#searchForm").attr("action","${ctx}/platform/adjust/list");
            });
        });
    </script>
</head>

<body>

<shiro:hasPermission name="platform:adjust:apply">
    <ul class="nav nav-tabs">
        <li class="active"><a href="${ctx}/platform/adjust">调账列表</a></li>
        <li><a href="${ctx}/caAccountAudit/toPayForAnotherAdjustment">调账添加</a></li>
    </ul>
</shiro:hasPermission>

<tags:message content="${message}" type="${messageType}"/>

<form:form id="searchForm" modelAttribute="platAccountAdjust" action="${ctx}/caAccountAudit/queryCaAccountAudits?type=5" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${pageInfo.pageSize}"/>
    <table>
        <tr>
            <td>
                <label>电子账户：</label>
                <div class="controls">
                    <select name="accountId" class="selectpicker bla bla bli" data-live-search="true">
                        <option value="">--请选择--</option>
                        <c:forEach items="${electronicAccounts}" var="account">
                            <option value="${account.id}"
                                    <c:if test="${paramMap.accountId == account.id}">selected</c:if>
                            >${account.electronicAccountName}</option>
                        </c:forEach>
                    </select>
                </div>
            </td>
            <td>
                <label class="control-label">订单时间：</label>
                <div class="controls">
                    <input id="applyCreateTime" name="applyCreateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                           value="${paramMap.applyCreateTime}"  onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
                    至
                    <input id="applyEndTime" name="applyEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                           value="${paramMap.applyEndTime}"  onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
                </div>
            </td>
            <td>
                <label class="control-label">订单时间：</label>
                <div class="controls">
                    <input id="approvalCreateTime" name="approvalCreateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                           value="${paramMap.approvalCreateTime}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
                    至
                    <input id="approvalEndTime" name="approvalEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                           value="${paramMap.approvalEndTime}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <label>审批状态: </label>
                <div class="controls">
                    <select name="auditStatus" class="selectpicker bla bla bli" data-live-search="true">
                        <option value="">--请选择--</option>
                        <option value="1" <c:if test="${paramMap.auditStatus == 1}">selected</c:if> >创建未审核</option>
                        <option value="4" <c:if test="${paramMap.auditStatus == 4}">selected</c:if> >运营审核通过</option>
                        <option value="5" <c:if test="${paramMap.auditStatus == 5}">selected</c:if> >运营审核拒绝</option>
                    </select>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div class="controls">
                    <label class="control-label">
                        <div class="btn-group">
                            <input id="btnSubmit" class="btn btn-primary pull-right" type="submit" value="查询">
                        </div>
                    </label>
                </div>
            </td>
        </tr>
    </table>
</form:form>


<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>代付调账订单号</th>
        <th>电子账户号</th>
        <th>通道商户</th>
        <th>账户类型</th>
        <th>调账方向</th>
        <th>申请调账金额／元</th>
        <th>申请调账日期</th>
        <th>申请人</th>
        <th>审批日期</th>
        <th>审批人</th>
        <th>审批状态</th>
        <th>备注</th>
        <shiro:hasPermission name="platform:adjust:audit"><th>操作</th></shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${caAccountAudits}" var="adjust">
        <tr>
            </td>
            <td>${adjust.id}</td>
            <td>${adjust.accountId}</td>
            <td>${adjust.electronicAccount.electronicAccountName}</td>
            <td>
                <c:if test="${adjust.accountType=='1'}">电子账户</c:if>
                <c:if test="${adjust.accountType=='2'}">公户账户</c:if>
            </td>
            <td>
                <c:if test="${adjust.adjustType=='1'}">增加</c:if>
                <c:if test="${adjust.adjustType=='2'}">减少</c:if>
            </td>
            <td>
                <fmt:formatNumber type="number" value="${adjust.amount}" pattern="0.00" maxFractionDigits="2"/>
            </td>
            <td>
                <fmt:formatDate value="${adjust.createdTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                ${adjust.customerAuditUserName}
            </td>
            <td>
                <fmt:formatDate value="${adjust.operateAuditTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                ${adjust.operateAuditUserName}
            </td>
            <td>
                <c:if test="${adjust.auditStatus eq '1'}">
                    未审核
                </c:if>
                <c:if test="${adjust.auditStatus eq '4'}">
                    运营审核通过
                </c:if>
                <c:if test="${adjust.auditStatus eq '5'}">
                    运营审核未通过
                </c:if>
            </td>
            <td>
                <div class="wrap" style="width: 100px; word-break: break-all; word-wrap: break-word;">
                        ${adjust.customerMsg}
                </div>
            </td>
            <shiro:hasPermission name="platform:adjust:audit">
                <td>
                    <c:if test="${adjust.auditStatus!='4' and adjust.auditStatus!='5'}">
                        <a href="${ctx}/caAccountAudit/findCaAccountAuditDetail?id=${adjust.id}">审批</a>
                    </c:if>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
