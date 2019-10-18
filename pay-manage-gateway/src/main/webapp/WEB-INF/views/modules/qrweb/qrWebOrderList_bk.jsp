<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>


<html>
<head>
    <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0">
    <link href="${ctxStatic}/qrweb/css/card/creditcard.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="${ctxStatic}/qrweb/js/card/jQuery.js"></script>
    <script type="text/javascript" src="${ctxStatic}/qrweb/js/card/swipe.js"></script>
    <script type="text/javascript" src="${ctxStatic}/qrweb/js/card/MicroAll.js"></script>
    <script type="text/javascript"
            src="http://api.map.baidu.com/api?v=2.0&ak=9KGMmhxIzXzh2PeC4IIg0Dt4Z4SUL9fC"></script>

    <script src="/js/userAgent.js"></script>
    <script src="/js/pager.js"></script>
    <script src="/js/common.js"></script>

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


    <!-- td埋点 -->
    <script type="text/javascript" src="/static/CN2/js/td-h5-website-sdk.js"
            td-appid="AD559D5C520946032000152A13AAAAAA"></script>

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
    </script>

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




    </script>

    <script type="text/javascript" language="javascript">
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


        function searchDet(merBrandId, brandSubtypeName, city, merId) {
//            document.getElementById("merBrandId").value=merBrandId;
//            document.getElementById("brandSubtypeName").value="111";
//            document.getElementById("cityName").value="111";
//            document.getElementById("merId").value="111";
//            document.getElementById("lat").value="111";
//            document.getElementById("lng").value="111";
//            document.getElementById("chooseCity").value="111";
//            document.getElementById("chooseArea").value="111";
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



</head>
<body style="background:#f5f5f5;">



<div id="conWrap">


    <!-- 提交表单 -->
    <form id="searchDetForm" name="searchDetForm" action="/gwqr/order/detail" method="post">
        <%--<input type="hidden" id="brandSubtypeName" name="brandSubtypeName" value="" />--%>
        <%--<input type="hidden" id="merBrandId" name="merBrandId" value=""/>--%>
        <%--<input type="hidden" id="cityName" name="cityName" value="北京"/>--%>
        <%--<input type="hidden" id="merId" name="merId" value=""/>--%>
        <%--<input type="hidden" id="merchantType" name="merchantType" value="7"/>--%>
        <%--<input type="hidden" id="lat" name="lat" value=""/>--%>
        <%--<input type="hidden" id="lng" name="lng" value=""/>--%>
        <%--<input type="hidden" id="chooseCity" name="chooseCity" value="" />--%>
        <%--<input type="hidden" id="chooseArea" name="chooseArea" value="" />--%>
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

        <div class="storeLi">
            <div class="li_top" onclick="searchDet('MP2015091510136158','教育培训类','北京市','MP2015091610136304')">
                <img width="75" height="75"
                     src='http://mps.95508.com/merchant//brand/MP2015091510136158_website_small_87214.png'
                     alt="北京樱花国际日语"/>
                <h3 class="storeName">支付宝用户 ¥ 12.36</h3>
                <div class="actTime">
                    <div class="li_middle">14:07:39 支付宝交易码 <b>4312</b></div>
                    <!-- <div class="tagbox storeCate">红色按钮</div> -->
                </div>
            </div>

        </div>

        <div class="storeLi">
            <div class="li_top" onclick="searchDet('MP2015091510135686','美容美发类','北京市','MP2015091510135877')">
                <img width="75" height="75"
                     src='http://mps.95508.com/merchant//brand/MP2015091510135686_website_small_58371.png' alt="北京思妍丽"/>
                <h3 class="storeName">微信用户 ¥ 12.36</h3>
                <div class="actTime">
                    <div class="li_middle">10:02:14 微信交易码 9923</div>
                    <div class="tagbox storeCate">红色按钮</div>
                </div>
            </div>

        </div>

    </section>

    <div id="pager_next_layout" style=" padding:8px 0;font-size:14px; background:#eee; text-align:center;"><div id="pager_next" class="pager"><a class="next" href="javascript:;">加载更多</a><a class="loading" href="javascript:;" style="display: none;">努力加载中...</a><a class="end" href="javascript:;" style="display: none;">没有更多商户啦~~</a></div></div>
    <div id="pager_next_anchor" style="width:100%; bottom:0; position:fixed;"><a href="javascript:;"></a></div>


    <div id="pager_next_layout" style=" padding:8px 0;font-size:14px; background:#eee; text-align:center;"></div>
    <div id="pager_next_anchor" style="width:100%; bottom:0; position:fixed;"><a href="javascript:;"></a></div>



</body>
</html>