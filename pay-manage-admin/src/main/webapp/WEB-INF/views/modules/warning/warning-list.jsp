<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>结算监控</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
            <c:if test="${vo.code_0 ==null && vo.code_1 !=null}">
            $('#chanId').attr("disabled",true);
            </c:if>
            <c:if test="${vo.code_1 ==null && vo.code_0 !=null}">
            $('#chanMchtPaytypeId').attr("disabled",true);
            </c:if>
        });

        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        function add(){
            location.href="${ctx}/warning/toAdd";
        }

        function tijiao(){
            $('#searchForm').submit();
        }

        function change(select1,select2){
            var value =$('#'+select1).val();
            if(value!=''){
                $('#'+select2).attr('');
                $('#'+select2).attr("disabled",true);
            }else{
                $('#'+select2).attr("disabled",false);

            }

        }
    </script>
</head>
<body>


<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/warning/list">已结算列表</a></li>
    <li><a href="${ctx}/warning/settleList">未结算列表</a></li>
</ul>
<form id="searchForm" action="${ctx}/warning/list" method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageInfo.pageNo" type="hidden" value="${1}"/>
    <input id="pageSize" name="pageInfo.pageSize" type="hidden" value="${page.pageSize}"/>
    <table>
        <tr>
            <td>
                <div class="control-group">
                    <label class="control-label">上游通道：</label>
                    <div class="controls">
                        <select name="code_0" id="chanId"  class="selectpicker bla bla bli" data-live-search="true"  onchange="change('chanId','chanMchtPaytypeId')">
                            <option value="">---请选择---</option>
                            <c:forEach var="chanInfo" items="${chanInfoList}">
                                <option value="${chanInfo.chanCode}" <c:if test="${vo.code_0 eq chanInfo.id}">selected</c:if> >${chanInfo.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label">通道商户支付方式：</label>
                    <div class="controls">
                        <select name="code_1" id="chanMchtPaytypeId" class="selectpicker bla bla bli" data-live-search="true"  onchange="change('chanMchtPaytypeId','chanId')">
                            <option value="">---请选择---</option>
                            <c:forEach var="chanMchtPayType" items="${chanMchtPaytypeList}">
                                <option value="${chanMchtPayType.id}" <c:if test="${vo.code_1 eq chanMchtPayType.id}">selected</c:if>>${chanMchtPayType.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div class="btn-group">
                    <input  class="btn btn-primary pull-right" type="button" value="查询" onclick="tijiao();">
                </div>
            </td>
            <td>
                <div class="btn-group">
                    <input class="btn btn-primary pull-right" type="button" value="新增" onclick="add();">
                </div>
            </td>
        </tr>
        <!--
        <tr>

            <td colspan="2">
                <div class="control-group">
                    <label class="control-label">时间：</label>
                    <div class="controls">
                        <input id="createTime" name="createTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                               value="${vo.createTime}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,readOnly:true,maxDate:$dp.$('updateTime').value,isShowOK:false,isShowToday:false});"/>至

                        <input id="updateTime" name="updateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                               value="${vo.updateTime}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,readOnly:true,minDate:$dp.$('createTime').value,isShowOK:false,isShowToday:false});"/>
                    </div>

                </div>
            </td>


        </tr>
         -->
    </table>
</form>


<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr >
        <th>通道/通道支付方式(编码)</th>
        <th>通道/通道支付方式(名称)</th>
        <th>金额(元)</th>
        <th>时间</th>
        <th>操作人</th>
        <td>备注</td>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="item">
        <tr>
            <td>${item.code}</td>
            <td>${item.name}</td>
            <td><fmt:formatNumber value="${item.amount * 0.01}" type="number" maxFractionDigits="2"/></td>
            <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>${item.operateAuditUserid}</td>
            <td>${item.operateMsg}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
