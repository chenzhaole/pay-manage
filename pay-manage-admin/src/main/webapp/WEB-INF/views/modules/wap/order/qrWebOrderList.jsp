<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>

<head>
    <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0">
    <link href="${ctxStatic}/modules/wap/order/css/card/creditcard.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="${ctxStatic}/modules/wap/order/js/card/jQuery.js"></script>

    <script type="text/javascript" src="${ctxStatic}/modules/wap/order/js/card/swipe.js"></script>
    <script type="text/javascript" src="${ctxStatic}/modules/wap/order/js/card/MicroAll.js"></script>
    <script type="text/javascript" src="${ctxStatic}/modules/wap/order/js/userAgent.js"></script>
    <script type="text/javascript" src="${ctxStatic}/modules/wap/order/js/pager.js"></script>
    <script type="text/javascript" src="${ctxStatic}/modules/wap/order/js/common.js"></script>
    <!-- td埋点 -->
    <script type="text/javascript" src="${ctxStatic}/modules/wap/order/js/CN2/td-h5-website-sdk.js" td-appid="AD559"></script>

    <title>交易列表</title>

    <style>
        .cate_li i.down {
            margin: 0 0 0 5px;
        }

        input:focus {
            outline: none;
        }

        .loading {
            position: relative;
            padding: 8px 0;
            background: #eee;
        }
    </style>


</head>


<script type="text/javascript">

    var urlLink = location.href;
    urlLink = urlLink.split("?")[0]; //去除参数条件
    var noHostUrl = urlLink.replace(/^http:\/\/[^/]+/, "").replace("/", "");  //去掉域名的url
    noHostUrl = noHostUrl.replace(".", "_");
    noHostUrl = noHostUrl.replace(/[\/]/g, "_");  //将url里所有'/'字符换成'_'
    var URL = "", urlLen = 0;
    for (var i = 0; i < noHostUrl.length; i++) {
        if ((noHostUrl.charCodeAt(i) & 0xff00) != 0) {
            urlLen++;
        }
        urlLen++;
    }
    if (urlLen > 30) {
        noHostUrl = noHostUrl.substring(noHostUrl.length - 30);
    }
    URL = location.host.substr(0, 1) + "_" + noHostUrl;
    try {
        if (TDAPP) {
            if (TDAPP != null && TDAPP != '') {
                TDAPP.onEvent(URL);
            }
        }
    } catch (e) {

    }

    /** 加载更多(点击更多-翻页) */
    $(function () {
        $("#pager_next").click(function () {
            var pageNo = parseInt($("#pageNo").val());
            $(this).html("加载中...");
            status = $(this).attr("data-status");
            if (status == 1) {
                status = $(this).attr("data-status", "0");
                $.ajax({
                    type: "post",
                    url: "${ctx}/wap/order/list" + "?mchtId=" + "3310000000666666",
                    data: "pageNo=" + pageNo,
                    dataType: "json",
                    success: function (data) {
//                        data = data.data;
                        /*数据不够10条隐藏按钮*/
                        if (data.orderList.length < 10) {
                            $(this).hide()
                        } else {
                            $("#pageNo").val(pageNo + 1);//记录页码
                        }
                        insertDiv(data.orderList);
                    }
                });
            }

        });
    });

    /** 加载更多(页面元素) */
    function insertDiv(data) {
        var information = $("#more");
        var html = '';
        for (var i = 0; i < data.length; i++) {

            html += " <div class='storeLi'>";
            html += "     <div class='li_top' onclick='searchDet(\""+data[i].orderId+"\",\""+data[i].yyyyMM+"\")'>";
            if(data[i].payType == 'wx'){
                html += "         <img width='75' height='75' src='/static/images/wechat.png' />";
                html += "         <h3 class='storeName'>微信用户 &nbsp;&nbsp; <b>¥ " + data[i].amount + "</b></h3>";
            }
            if(data[i].payType == 'al'){
                html += "         <img width='75' height='75' src='/static/images/alipay.png' />";
                html += "         <h3 class='storeName'>支付宝用户 &nbsp;&nbsp; ¥ " + data[i].amount + "</h3>";
            }

            html += "         <div class='actTime'>";
            html += "             <div class='li_middle'>" + data[i].orderTime + " 交易码 &nbsp;<b>4312</b></div>";
            html += "         </div>";
            html += "     </div>";
            html += " </div>";


        }
        information.append(html);
        $("#pager_next").html("加载更多");
        $("#pager_next").attr("data-status", "1");
    }


    /** 加载更多(滑动底部-翻页) */
    $(window).scroll(function () {
        var scrollTop = $(this).scrollTop();    //滚动条距离顶部的高度
        var scrollHeight = $(document).height();   //当前页面的总高度
        var clientHeight = $(this).height();    //当前可视的页面高度
        if (scrollTop + clientHeight >= scrollHeight - 50) {   //距离顶部+当前高度 >=文档总高度 即代表滑动到底部 注：-50 上拉加载更灵敏
            //加载数据
            $('#pager_next').click();
        }
    });


</script>


<body style="background:#f5f5f5;">

<div id="conWrap">

    <!-- 提交表单 -->
    <form id="searchDetForm" name="searchDetForm" action="" method="post">
    </form>


    <!-- 条件筛选 -->
    <section class="list_cate">
        <div class="cate_li">
            <div class="li_name">全部<i class="down down1"></i></div>
            <div class="slideDown" style="">
                <ul>
                    <li onclick='selectedChange("MP2014052410000796")'>全部</li>
                    <li onclick='selectedChange("MP2014052410000800")'>微信</li>
                    <li onclick='selectedChange("MP2014052410000799")'>支付宝</li>
                </ul>
            </div>
        </div>


        <div class="cate_li">
            <div class="li_name">时间<i class="down down1"></i></div>
            <div class="slideDown" style="">
                <ul>
                    <li onclick='selectedChange("MP2014052410000796")'>2019年8月</li>
                    <li onclick='selectedChange("MP2014052410000800")'>2019年7月</li>
                    <li onclick='selectedChange("MP2014052410000800")'>2019年6月</li>
                </ul>
            </div>
        </div>


        <div class="cate_li">
            <div class="li_name">店铺<i class="down down1"></i></div>
            <div class="slideDown" style="">
                <ul>
                    <li onclick='selectedChange("MP2014052410000796")'>总店</li>
                </ul>
            </div>
        </div>


    </section>

    <!-- 搜索 -->
    <section class="search_box">
        <input placeholder="订单编号" type="text" id="key" value="" onchange="inputChange(this.value)">
    </section>


    <!-- 商户列表 -->

    <section class="store_list" id="content">


        <c:forEach items="${orderList}" var="order">
            <div class="storeLi">
                <div class="li_top" onclick="searchDet('${order.orderId}','${order.yyyyMM}')">
                    <c:if test="${order.payType == 'wx'}">
                        <img width="75" height="75" src='/static/images/wechat.png'/>
                        <h3 class="storeName"> 微信用户:  &nbsp;&nbsp; <b>¥ ${order.amount}</b></h3>
                    </c:if>
                    <c:if test="${order.payType == 'al'}">
                        <img width="75" height="75" src='/static/images/alipay.png'/>
                        <h3 class="storeName"> 支付宝用户:  &nbsp;&nbsp; <b>¥ ${order.amount}</b></h3>
                    </c:if>
                    <div class="actTime">
                        <div class="li_middle">${order.orderTime} 交易码:&nbsp; <b>${order.verifyCode}</b></div>
                        <%--<div class="tagbox storeCate">红色按钮</div>--%>
                    </div>
                </div>
            </div>
        </c:forEach>


        <div id="more"></div>

    </section>

    <div id="pager_next_layout" style=" padding:8px 0;font-size:14px; background:#eee; text-align:center;">
        <div id="pager_next" class="pager" data-status="1">
            <a class="next" href="javascript:;">加载更多</a>
            <a class="loading" href="javascript:;" style="display: none;">努力加载中...</a>
            <a class="end" href="javascript:;" style="display: none;">没有更多数据啦~~</a>
        </div>
    </div>

    <div id="pager_next_anchor" style="width:100%; bottom:0; position:fixed;">
        <a href="javascript:;"></a>
    </div>

    <input type="hidden" id="pageNo" value="${pageNo}">

</body>

<script type="text/javascript">


    $(function () {
        $('.wrap h2 span').mouseover(function () {
            $(this).addClass('current').siblings().removeClass();
            $('.box div.wrap1').eq($(this).index()).addClass('current').siblings().removeClass('current');
        });
    });


    function selectedChange(typeValue) {
        if (brandSubtype != typeValue) {
            document.getElementById("brandSubtype").value = typeValue;
            submitForm(document.getElementById("cityNow").innerText, document.getElementById("areaNow").innerText);
        }
    }


    function inputChange(keyValue) {
        document.getElementById("key").value = keyValue;
        submitForm();
    }


    function localSelect(local) {
        window.location.href = "/mobile/jsp/creditcard/localSelect.jsp?localtype=" + local + "&merchantType=7&city=北京";
    }


    var contentContainer = 'content';                   //内容容器ID
    var nextBtnContainer = 'pager_next_layout';          //下一页按钮容器ID
    var pagerReqURI = '/mobile/jsp/creditcard/merchant_list_more.jsp';       //翻页请求的服务器URL
    var pagerTotalPage = 0;                 //总页数
    var pagerTotalRecord = 0;                //总记录数
    var pagerPerPage = 6;                     //每页大小
    var pagerToPage = 1;                                 //当前页数
    var pagerToId = 6;                       //当前最后的记录ID
    var pagerAutoLoadNextAnchor = 'pager_next_anchor';    //触发加载下一页的锚点
    var pagerAutoLoadNextOffset = 20;                    //当"加载更多"标签距离锚点多大开始自动加载
    var pagerTitle = {nextMore: '加载更多', nextLoading: '努力加载中...', nextEnd: '没有更多商户啦~~'};  //"加载更多"在不同状态下的标题

    $(".li_name").on("click", function () {
        $(this).next().stop().slideToggle(200);
    });

    if ("" == "null") {
        var brandSubtype = "";
    } else {
        var brandSubtype = "";
    }

    var merchantType = "7";
    var citys = "北京";

    function submitForm(city, area) {
        if (new RegExp(/'/).test(key) || new RegExp(/"/).test(key)) {
            alert("请输入正确的搜索条件");
            return false;
        }
        document.getElementById("merchantType").value = "7";
        if (city == undefined) {
            document.getElementById("city").value = "北京";
        } else {
            document.getElementById("city").value = city;
        }

        if (area != "" || area != undefined) {
            document.getElementById("mArea").value = area;
        }
        document.getElementById("lat").value = sessionStorage.getItem("lat");
        document.getElementById("lng").value = sessionStorage.getItem("lng");
        document.getElementById("locationForm").submit();
    }


    function searchDet(platOrderId,yyyyMM) {
//            document.getElementById("merBrandId").value=merBrandId;
//            document.getElementById("brandSubtypeName").value="111";
//            document.getElementById("cityName").value="111";
//            document.getElementById("merId").value="111";
//            document.getElementById("lat").value="111";
//            document.getElementById("lng").value="111";
//            document.getElementById("chooseCity").value="111";
//            document.getElementById("chooseArea").value="111";

        var url = "${ctx}/wap/order/detail?platOrderId="+platOrderId+"&yyyyMM="+yyyyMM;
        document.getElementById("searchDetForm").action = url;
        document.getElementById("searchDetForm").submit();
    }

    function mapShow(addr, title) {
        var myGeo = new BMap.Geocoder();
        myGeo.getPoint(addr, function (point) {
            if (point) {
                window.location.href = "http://api.map.baidu.com/marker?location=" + point.lat + "," + point.lng + "&title=" + title + "&content=" + addr + "&output=html";
            } else {
                return false;
            }
        });
    }

</script>


</html>