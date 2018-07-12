<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>商户首页</title>
    <meta name="decorator" content="default"/>
    <style>
        .table th, .table td {
            border-top: none;
        }

        .control-group {
            border-bottom: none;
        }
    </style>
    <script type="application/javascript">
        function manyInfo() {
           $("#attachInfoHead").css("display","block");
           $("#attachInfo").css("display","block");
           $("#manyInfo").css("display","none");
        }
    </script>
</head>
<body>

<div class="breadcrumb">
    <label><a href="#">商户首页</a></label>
</div>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label><h4>交易信息</h4></label>
    </div>
    <table class="table" id="payInfo">
        <tr>
            <td width="25%">
                <div class="control-group">
                    <label class="control-label"><h5>成功笔数</h5></label>
                    <div class="controls">
                        <label id="succCount">${payData.succCount}</label>
                    </div>
                </div>
            </td>
            <td width="25%">
                <div class="control-group">
                    <label class="control-label"><h5>成功金额(元)</h5></label>
                    <div class="controls">
                        <label id="succMoney">${payData.succMoney}</label>
                    </div>
                </div>
            </td>
            <td width="25%">
                <div class="control-group">
                    <label class="control-label"><h5>冻结金额(元)</h5></label>
                    <div class="controls">
                        <label id="freezeMoney">${mchtAccountDetailData.freezeTotalAmount}</label>
                    </div>
                </div>
            </td>
            <td width="25%">
                <div class="control-group">
                    <label class="control-label"><h5>可提现金额(元)</h5></label>
                    <div class="controls">
                        <label id="settleMoney">${mchtAccountDetailData.settleTotalAmount}</label>
                    </div>
                </div>
            </td>
        </tr>
    </table>

    <!-- ********************************************************************** -->
    <div class="breadcrumb">
        <label><h4>商户基本信息</h4></label>
    </div>
    <table class="table" id="commonInfo">
        <tr>
            <td width="33%">
                <div class="control-group">
                    <label class="control-label"><h5>商户ID</h5></label>
                    <div class="controls">
                        <label id="mchtCode">${mchtInfoData.mchtCode}</label>
                    </div>
                </div>
            </td>
            <td width="33%">
                <div class="control-group">
                    <label class="control-label"><h5>商户名称</h5></label>
                    <div class="controls">
                        <label id="name">${mchtInfoData.name}</label>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label"><h5>商户地址</h5></label>
                      <div class="control-group">
                        <div class="controls">
                            <label id="companyAdr">${mchtInfoData.companyAdr}</label>
                        </div>
                      </div>
                </div>
            </td>
        </tr>

        <tr>
            <td width="33%">
                <div class="control-group">
                    <label class="control-label"><h5>联系人</h5></label>
                    <div class="controls">
                        <label id="contactName">${mchtInfoData.contactName}</label>
                    </div>
                </div>
            </td>
            <td width="33%">
                <div class="control-group">
                    <label class="control-label"><h5>联系电话</h5></label>
                    <div class="controls">
                        <label id="phone">${mchtInfoData.phone}</label>
                    </div>
                </div>
            </td>
            <td>
                <div class="control-group">
                    <label class="control-label"><h5>联系邮箱</h5></label>
                    <div class="control-group">
                        <div class="controls">
                            <label id="email">${mchtInfoData.email}</label>
                        </div>
                    </div>
                </div>
            </td>
        </tr>
    </table>
    <a href="javaScript:void(0)" onclick="manyInfo()" id="manyInfo">更多</a>
    <!-- ********************************************************************** -->
    <div class="breadcrumb" id = "attachInfoHead" style="display: none;">
        <label><h4>商户附加信息</h4></label>
    </div>
    <table class="table" id="attachInfo"  style="display: none;">
        <tr>
            <td width="33%">
                <div class="control-group">
                    <label class="control-label"><h5>商户key</h5></label>
                    <div class="controls">
                        <label id="mchtKey">${mchtInfoData.mchtKey}</label>
                    </div>
                </div>
            </td>
            <td width="33%">
                <div class="control-group">
                    <label class="control-label"><h5>鉴权ip地址</h5></label>
                    <div class="controls">
                        <label id="clientIp">${mchtInfoData.clientIp}</label>
                    </div>
                </div>
            </td>
            <td width="33%">
                <div class="control-group">
                    <label class="control-label"><h5>商户公钥</h5></label>
                    <div class="control-group">
                        <div class="controls">
                            <label id="certContent1" style="word-break:break-all;">
                                ${mchtInfoData.certContent1}
                            </label>
                        </div>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td width="100%" colspan="3">
                <div class="control-group">
                    <label class="control-label"><h5>商户费率</h5></label>
                    <div class="controls">
                            <c:forEach items="${mchtFeerateInfoMap}" var="map" varStatus="status">
                                <label style="text-align: left;margin-top: 2%; width: 22%;">
                                    ${map.key}=${map.value}
                                </label>
                                <c:if test="${(status.index+1) % 4 == 0}">
                                    <br />
                                </c:if>

                            </c:forEach>
                    </div>
                </div>
            </td>
        </tr>
    </table>
</body>
</html>