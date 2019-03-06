<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>支付通道列表</title>
	<meta name="decorator" content="default"/>

	<script type="text/javascript">
        function add(){
            document.forms[0].action="${ctx}/channel/addChanMchtPayTypePage";
            document.forms[0].submit();
        }

        function del(id){
            if(confirm("是否确认删除ID为“"+id+"”的记录？")){
                document.forms[0].action="${ctx}/channel/repaymentDel?id="+id;
                document.forms[0].submit();
            }
        }

        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        var dataMap = new Map();
        <c:forEach items="${page.list}" var="chanInfo">
			<c:if test="${chanInfo.payType == 'df101' || chanInfo.payType == 'df102'}">
				dataMap.set(${chanInfo.id},${chanInfo.id});
			</c:if>
		</c:forEach>

        function queryBalance(chanId) {
            alert(chanId);
            var checkUrl = "/admin/channel/queryBalance?chanId="+chanId;
            $.ajax({
                url: checkUrl, //服务器端请求地址
                dataType: 'json', //返回值类型 一般设置为json
                success: function (data) {  //服务器成功响应处理函数
                   $('#'+chanId).text(data);
                },
                error: function (data, e) {//服务器响应失败处理函数
                    $('#'+chanId).text("ajax请求异常");
                }
            })
        }

        //下拉搜索框初始化
        $(window).on('load', function () {
            $('.selectpicker').selectpicker({});
            //
            for(var key in map){
                queryBalance(key);
			}

        });

	</script>
</head>
<body>
<div class="breadcrumb">
	<label><a href="#">通道管理</a> > <a href="#"><b>通道商户支付方式列表</b></a></label>
</div>

<tags:message content="${message}" type="${messageType}"/>

<form id="searchForm" action="${ctx}/channel/mchPaytypeList" method="post" class="breadcrumb form-search">
	<table>
		<tr>
			<td>
				<div class="control-group">

					<div class="controls">
						商户名称：<select name="mchtCode" id="mchtId"  class="selectpicker bla bla bli" data-live-search="true">
						<option value="">---请选择---</option>
						<c:forEach var="mcht" items="${mchtList}">
							<option value="${mcht.mchtCode}" <c:if test="${paramMap.mchtCode eq mcht.id}">selected</c:if> >${mcht.name}</option>
						</c:forEach>
					</select>
					</div>
				</div>
			</td>
			<td>
				<label>通道名称：</label><input value="${paramMap.chanName}" name="chanName" type="text" maxlength="64" class="input-medium"/>
			</td>
			<td>
				<label>支付方式：</label>
				<select name="payType" class="input-medium" id="payType">
					<option value="">--请选择--</option>
					<c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
						<option value="${paymentTypeInfo.code}"
								<c:if test="${paymentTypeInfo.code eq paramMap.payType }">selected </c:if>
						>${paymentTypeInfo.desc}</option>
					</c:forEach>
				</select>
			</td>
			<%--<td>--%>
			<%--<label>结算方式：</label>--%>
			<%--<select name="settleType" class="input-medium" id="fund_settle_mode">--%>
			<%--<option value="">--请选择--</option>--%>
			<%--<option value="1">收单对公</option>--%>
			<%--<option value="2">收单对私</option>--%>
			<%--<option value="3">代付</option>--%>
			<%--<option value="4">银行直清</option>--%>
			<%--</select>--%>
			<%--</td>--%>
			<%--<td>--%>
			<%--<label>结算周期：</label>--%>
			<%--<select name="settleCycle" class="input-medium" id="fund_settle_day">--%>
			<%--<option value="">--请选择--</option>--%>
			<%--<option value="1">S0</option>--%>
			<%--<option value="2">D0</option>--%>
			<%--<option value="3">T0</option>--%>
			<%--<option value="4">T1</option>--%>
			<%--<option value="5">T2</option>--%>
			<%--</select>--%>
			<%--</td>--%>
			<td>
				<input id="pageNo" name="pageNo" type="hidden" value="${1}"/>
				<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" style="margin-left: 5px;">
				<shiro:hasPermission name="channel:addChanMchtPayTypePage">
					<input id="clearButton" class="btn btn-primary" type="button" value="新增支付通道" onclick="add()" style="margin-left: 5px;"/>
				</shiro:hasPermission>
			</td>
		</tr>
	</table>
</form>


<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead>
	<tr >
		<th>NO</th>
		<th>名称</th>
		<th>商户名称</th>
		<th>通道名称</th>
		<th>支付方式</th>
		<th>结算方式</th>
		<th>结算周期</th>
		<th>状态</th>
		<th>待结算金额(元)</th>
		<th>操作</th>
	</tr>
	</thead>
	<tbody>
	<%int i=0; %>
	<c:forEach items="${page.list}" var="chanInfo">
		<%i++; %>
		<tr>
			<td><%=i%></td>
			<td>${chanInfo.name}</td>
				<%--<td><a href="${ctx}/merchant/detailByNo?mchtNo=${chanInfo.mchtId}">${chanInfo.mchtName}</a></td>--%>
				<%--<td><a href="${ctx}/channel/detailByNo?chanInfoNo=${chanInfo.id}">${chanInfo.chanName}</a></td>--%>
			<td>${chanInfo.mchtName}</td>
			<td>${chanInfo.chanName}</td>
			<td><c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
				<c:if test="${chanInfo.payType == paymentTypeInfo.code}"> ${paymentTypeInfo.desc}</c:if>
			</c:forEach></td>

			<c:choose><c:when test="${chanInfo.settleMode == 1}"><td>收单对公</td></c:when>
				<c:when test="${chanInfo.settleMode == 2}"><td>收单对私</td></c:when>
				<c:when test="${chanInfo.settleMode == 3}"><td>代付</td></c:when>
				<c:when test="${chanInfo.settleMode == 4}"><td>银行直清</td></c:when>
				<c:otherwise><td></td></c:otherwise></c:choose>

			<td>${chanInfo.settleCycle}</td>

			<c:choose><c:when test="${chanInfo.status == 1}"><td>启用</td></c:when>
				<c:when test="${chanInfo.status == 2}"><td>停用</td></c:when><c:otherwise><td></td></c:otherwise></c:choose>
			<td>
				<fmt:formatNumber value="${chanInfo.limitAmount * 0.01}" type="number" maxFractionDigits="2" />
			</td>
			<td>
				<shiro:hasPermission name="channel:editChanMchtPayTypePage">
					<a href="${ctx}/channel/addChanMchtPayTypePage?id=${chanInfo.id}">修改</a>
				</shiro:hasPermission>
				<shiro:hasPermission name="channel:deleteChanMchPayType">
					|<a href="${ctx}/channel/deleteChanMchPayType?id=${chanInfo.id}" onclick="return confirmx('是否确认删除“${chanInfo.name}”？', this.href)">删除</a>
				</shiro:hasPermission>
				<c:if test="${chanInfo.payType == 'df101' || chanInfo.payType == 'df102'}">
					|<a href="#" onclick="queryBalance('${chanInfo.id}')">查询余额</a><span id="${chanInfo.id}"></span> </c:if>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>