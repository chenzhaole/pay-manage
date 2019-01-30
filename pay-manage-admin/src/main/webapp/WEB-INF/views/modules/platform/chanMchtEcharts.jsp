<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折线图</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/echarts/echarts.min.js" type="text/javascript"></script>
	<style type="text/css">
		.table th{
			white-space: normal;
			align:center
		}
	</style>
	<script src="${ctxStatic}/js/select2.js"></script>
	<link href="${ctxStatic}/css/select2.css" rel="stylesheet" />
	<script type="text/javascript">
	$(document).ready(function() {

        $('.selectpicker').select2({
        });

		$("#btnSubmit").click(function(){
			$("#searchForm").submit();
		});
        onChange();
        setDate();
        if(${paramMap.echartsType!=null}){
            init();
		}

	});
	function init(){
	    var dataFrist ='${xAxis!=null?xAxis[0]:""}';
	    var dataLast ='${xAxis!=null?xAxis[fn:length(xAxis)-1]:""}';
        var myChart = echarts.init(document.getElementById('echart'));
		var option = {
            title: {
                text:
					<c:choose>
					<c:when test="${paramMap.echartsType!=null}">
							'${paramMap.echartsType.split(",")[1]}'
				     </c:when>
					<c:otherwise>
						''
				   </c:otherwise>
                	</c:choose>
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data:[
				<c:forEach items="${data}" var="item">
				'${item}',
				</c:forEach>
				]
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            toolbox: {
                feature: {
                    saveAsImage: {}
                }
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data:
				[
				    <c:forEach items="${xAxis}" var="item">
					'${item}',
					</c:forEach>
				],
                axisLabel:{
                     interval:1
				},
				max:dataLast,
				min:dataFrist
            },
            yAxis: {
                type:'value',
				<c:if test="${echartsType == '1' || echartsType=='2'}">
                axisLabel: {
                    show: true,
                    interval: 'auto',
                    formatter: '{value} %'
                },
				</c:if>

            },
            series: [
                <c:forEach items="${data}" var="report" varStatus="status">
                {
                    name:'${report}',
                    type:'line',
                    data:${series[status.index]}
                },
                </c:forEach>
            ]
        };
        myChart.setOption(option);
	}

	function setDate(){
	    if($('#beginDate').val()!=null && $('#beginDate').val()!=''){
	        return ;
		}
        var sd=new Date();
        var sy=sd.getFullYear();
        var sm = sd.getMonth()+1;
        var sdd=sd.getDate();
        if (sm >= 1 && sm <= 9) {
            sm = "0" + sm;
        }
        if (sdd >= 0 && sdd <= 9) {
            sdd = "0" + sdd;
        }

        $("#beginDate").val(sy+sm+sdd);
        $("#endDate").val(sy+sm+sdd);
        $("#beginTime").val("000000");
        $("#endTime").val("000000");

	}

	function onChange(){
	    var value =$('#echartsType').val();
	    if(value ==null || value==''){
			$('#td_0_1').css('display','none');
            $('#td_2').css('display','none');
            $('#tr_2').css('display','none');
		}
		var value =value.split(",")[0];
		if(value =='2'){
            $('#td_0_1').css('display','none');
            $('#td_2').css('display','block');
            $('#tr_2').css('display','block');
		}else if(value =='1' || value =='0'){
            $('#td_0_1').css('display','block');
            $('#td_2').css('display','none');
            $('#tr_2').css('display','none');
		}
	}

	</script>

</head>
<body>
<div class="breadcrumb">
	<label><a href="#">折线图</a><a href="#"><b>折线图</b></a></label>
</div>
 	<form:form id="searchForm" action="${ctx}/platform/original/statistice/chanMchtEcharts"  method="post" class="breadcrumb form-search">
		<table>
			<tr>
				<td>
					<label class="control-label">折线图类型：</label>
					<select name="echartsType" id="echartsType"  value="${paramMap.echartsType}" class="bla bla bli" data-live-search="true" onchange="onChange()">
						<option value="" <c:if test="${paramMap.echartsType ==''}">selected</c:if>>请选择</option>
						<option value="0,商户交易量折线图" <c:if test="${paramMap.echartsType =='0,商户交易量折线图'}">selected</c:if>>商户交易量折线图</option>
						<option value="1,商户交易成功率折线图" <c:if test="${paramMap.echartsType =='1,商户交易成功率折线图'}">selected</c:if>>商户交易成功率折线图</option>
						<option value="2,通道成功率折线图" <c:if test="${paramMap.echartsType =='2,通道成功率折线图'}">selected</c:if>>通道成功率折线图</option>
					</select>
				</td>
				<td id="td_0_1" style="display: none">
					<label class="control-label">商户名称：</label>
					<select name="mchtCode" id="mchtCode"  class="selectpicker" data-live-search="true">
						<option value="">---全部---</option>
						<c:forEach var="mcht" items="${mchtList}">
							<option value="${mcht.mchtCode}" <c:if test="${paramMap.mchtCode eq mcht.mchtCode}">selected</c:if> >${mcht.name}</option>
						</c:forEach>
					</select>

					<label class="control-label">支付方式</label>
					<select name="payType" class="input-xlarge" id="payType">
						<option value="">--全部--</option>
						<c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
							<option
									<c:if test="${paramMap.payType == paymentTypeInfo.code.concat(',').concat(paymentTypeInfo.desc)}">selected</c:if>
									value="${paymentTypeInfo.code},${paymentTypeInfo.desc}">${paymentTypeInfo.desc}</option>
						</c:forEach>
					</select>
				</td>

				<td id="td_2" style="display: none">
					<label class="control-label">通道商户支付方式</label>
					<select name="chanMchtPayTypeId" class="selectpicker" data-live-search="true" >
						<option value="">--全部--</option>
						<c:forEach items="${chanInfoList}" var="chan">
							<option <c:if test="${paramMap.chanMchtPaytypeId == chan.id}">selected</c:if>
									value="${chan.id}">${chan.name}</option>
						</c:forEach>
					</select>

					<label class="control-label">通道名称</label>
					<select name="chanCode" class="selectpicker" id="chanCode">
						<option value="">--全部--</option>
						<c:forEach items="${chanInfos}" var="chanInfo">
							<option data-chanCode="${chanInfo.chanCode }"
									<c:if test="${paramMap.chanCode == chanInfo.chanCode}">selected</c:if>
									value="${chanInfo.chanCode}">${chanInfo.name}</option>
						</c:forEach>
					</select>

				</td>
			</tr>
			<tr id="tr_2" style="display: none">
				<td>
					<label class="control-label">支付方式</label>
					<select name="payType" class="selectpicker" id="payType">
						<option value="">--全部--</option>
						<c:forEach items="${paymentTypeInfos}" var="paymentTypeInfo">
							<option
									<c:if test="${paramMap.payType == paymentTypeInfo.code.concat(',').concat(paymentTypeInfo.desc)}">selected</c:if>
									value="${paymentTypeInfo.code},${paymentTypeInfo.desc}">${paymentTypeInfo.desc}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label>统计日期:</label>

					<input id="beginDate" type="text" class="input-medium Wdate" name ="beginDate" value="${paramMap.beginDate}"
						   onclick="WdatePicker({dateFmt:'yyyyMMdd',isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true,minDate:'#F{$dp.$D(\'endDate\',{d:-4});}',maxDate: '#F{$dp.$D(\'endDate\')}'})"/>-
					<input id ="endDate" type="text" class="input-medium Wdate" name ="endDate" value="${paramMap.endDate}" onclick="WdatePicker({dateFmt:'yyyyMMdd',isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true,maxDate:'%y-%M-%d'});"/>
				</td>
				<td>
					<label>统计时间:</label>
					<input id="beginTime" type="text" class="input-medium Wdate" name ="beginTime" value="${paramMap.beginTime}"
						   onclick="WdatePicker({dateFmt:'HHmmss',quickSel:['%H-00-00','%H-15-00','%H-30-00','%H-45-00','\#{%H+1}-00-00'],isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true});"/>-
					<input id="endTime" type="text" class="input-medium Wdate" name ="endTime" value="${paramMap.endTime}" onclick="WdatePicker({dateFmt:'HHmmss',quickSel:['%H-00-00','%H-15-00','%H-30-00','%H-45-00','\#{%H+1}-00-00'],isShowClear:true,readOnly:true,isShowOK:true,isShowToday:true});"/>

					<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
				</td>
			</tr>
		</table>
 	</form:form>
    <div id ="echart" style="width:90%;height:600px;"></div>
</body>
</html>