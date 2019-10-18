
<!doctype html>
<html>
<head>
    <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0">
    <link href="/mobile/css/card/creditcard.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="/mobile/js/card/jQuery.js"></script>
    <script type="text/javascript" src="/mobile/js/card/swipe.js"></script>
    <script type="text/javascript" src="/mobile/js/card/MicroAll.js"></script>
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=9KGMmhxIzXzh2PeC4IIg0Dt4Z4SUL9fC"></script>
    <title>商户列表_广发信用卡_广发银行手机官网</title>
    <!-- td埋点 -->
    <script type="text/javascript" src="/static/CN2/js/td-h5-website-sdk.js" td-appid ="AD559D5C520946032000152A13AAAAAA"></script>
    <script type="text/javascript">
        var urlLink = location.href;
        urlLink = urlLink.split("?")[0]; //去除参数条件
        var noHostUrl = urlLink.replace(/^http:\/\/[^/]+/, "").replace("/","");  //去掉域名的url
        noHostUrl = noHostUrl.replace(".","_");
        noHostUrl = noHostUrl.replace(/[\/]/g,"_");  //将url里所有'/'字符换成'_'
        var URL = "",urlLen = 0;
        for(var i=0; i<noHostUrl.length; i++){
            if((noHostUrl.charCodeAt(i) & 0xff00) !=0){ urlLen ++; }
            urlLen++;
        }
        if(urlLen>30){
            noHostUrl = noHostUrl.substring(noHostUrl.length-30);
        }
        URL = location.host.substr(0,1) + "_" + noHostUrl;
        try{
            if(TDAPP){
                if(TDAPP!=null && TDAPP!=''){
                    TDAPP.onEvent(URL);
                }
            }
        }catch(e){

        }
    </script>
    <style>
        .cate_li i.down {margin:0 0 0 5px;}
        input:focus{outline:none;}
        .loading{position:relative;padding:8px 0;background:#eee;}
    </style>
    <script type="text/javascript">
        $(function(){
            $('.wrap h2 span').mouseover(function(){
                $(this).addClass('current').siblings().removeClass();
                $('.box div.wrap1').eq($(this).index()).addClass('current').siblings().removeClass('current');
            });
        });
    </script>
</head>
<body style="background:#f5f5f5;">
<!-- 菜单 -->



<nav id="menuNav">

    <header class="nav_head">快捷导航<button onclick="menuLeft()" class="back"></button> </header>


    <a href="https://wap.cgbchina.com.cn/queryApply.do?sid=0.7710854143363747"><div class="nav_row">
        <h2 class="row_con" style="border-left-color: #df4031;">
            进度查询</h2>
        <span class="second_nav">查看卡片申请进度、激活卡片</span>
    </div></a>


    <a href="/Channel/21320534"><div class="nav_row">
        <h2 class="row_con" style="border-left-color: #ffbc59;">
            优惠活动</h2>
        <span class="second_nav">买一送一、各种优惠即刻掌握</span>
    </div></a>


    <a href="/Channel/21320774"><div class="nav_row">
        <h2 class="row_con" style="border-left-color: #97cb5c;">
            分期指南</h2>
        <span class="second_nav">账单随意分期，快速借款</span>
    </div></a>


    <a href="/Channel/21321342"><div class="nav_row">
        <h2 class="row_con" style="border-left-color: #2ca82c;">
            优惠商户</h2>
        <span class="second_nav">刷卡打折、消费攻略全都有</span>
    </div></a>


    <a href="/Channel/21329278"><div class="nav_row">
        <h2 class="row_con" style="border-left-color: #df4031;">
            奖励查询</h2>
        <span class="second_nav">优惠活动奖励快速查询</span>
    </div></a>


    <a href="http://xyk.cgbchina.com.cn/subsite/201906/22902578/index.html" class="button">

        <img src="/CMS5_G20306002Resource?info=21323829;res=14956005089541747056630" class="banka" alt="我要办卡">
        我要办卡
    </a>

    <a href="/Channel/21319737" class="button">

        <img src="/CMS5_G20306002Resource?info=21323734;res=1495600535192626289102" class="banka" alt="返回首页">
        返回首页
    </a>


</nav>

<script type="text/javascript">
    function menuLeft(){
        var Obj=document.getElementById("conWrap");
        var Obj_nav=document.getElementById("menuNav");
        if(Obj.style.left==0){ Obj.style.left="-281px"; Obj_nav.style.display="block";
        }else{   Obj.style.left="";Obj_nav.style.display="none";}
    }
</script>

<div id="conWrap">

    <header class="inner_head">
        <div class="tableCell icons return" onclick="javascript:window.history.go(-1);"></div>
        <div class="tableCell">

            <a href="/Channel/21321342" class="h_cate "><h1>优惠商户</h1></a>

            <a href="/Channel/21479765" class="h_cate h_cate_on"><h1>分期商户</h1></a></div>
        <div class="tableCell icons menu1" onclick="menuLeft()"></div>
    </header>

    <form id="locationForm" name="locationForm" action="/mobile/jsp/creditcard/merchant_condition.jsp" method="post">
        <input type="hidden" id="brandSubtype" name="brandSubtype" value="" />
        <input type="hidden" id="city" name="city" value=""/>
        <input type="hidden" id="mArea" name="mArea" value="" />
        <input type="hidden" id="key" name="key" value="" />
        <input type="hidden" id="merchantType" name="merchantType" value="7"/>
        <input type="hidden" id="lat" name="lat" value=""/>
        <input type="hidden" id="lng" name="lng" value=""/>
    </form>
    <form id="searchDetForm" name="searchDetForm" action="/mobile/jsp/creditcard/merchantsDetail.jsp" method="post">
        <input type="hidden" id="brandSubtypeName" name="brandSubtypeName" value="" />
        <input type="hidden" id="merBrandId" name="merBrandId" value=""/>
        <input type="hidden" id="cityName" name="cityName" value="北京"/>
        <input type="hidden" id="merId" name="merId" value=""/>
        <input type="hidden" id="merchantType" name="merchantType" value="7"/>
        <input type="hidden" id="lat" name="lat" value=""/>
        <input type="hidden" id="lng" name="lng" value=""/>
        <input type="hidden" id="chooseCity" name="chooseCity" value="" />
        <input type="hidden" id="chooseArea" name="chooseArea" value="" />
    </form>
    <!-- 条件筛选 -->












    <section class="list_cate">
        <div class="cate_li">
            <div class="li_name">全部<i class="down down1"></i></div>
            <div class="slideDown" style="">
                <ul>

                    <li onclick='selectedChange("MP2014052410000796")'>旅游类</li>

                    <li onclick='selectedChange("MP2014052410000800")'>汽车服务类</li>

                    <li onclick='selectedChange("MP2014052410000799")'>美容美发类</li>

                    <li onclick='selectedChange("MP2014052610007569")'>纤体健身类</li>

                    <li onclick='selectedChange("MP2014052410000798")'>其他类</li>

                    <li onclick='selectedChange("MP2014052410000803")'>家电类</li>

                    <li onclick='selectedChange("MP2014070710072249")'>IT数码类</li>

                    <li onclick='selectedChange("MP2014070710072250")'>电信运营商</li>

                    <li onclick='selectedChange("MP2014070710072251")'>教育培训类</li>

                </ul>
            </div>

        </div>
        <div class="cate_li" onclick='localSelect("地区")'>
            <div class="li_name"><span id="areaNow">东城</span><i class="down down1"></i></div>
        </div>
        <div class="cate_li" onclick="localSelect('城市')">
            <div class="li_name"><span id=cityNow>北京</span><i class="down down1"></i></div>
        </div>
    </section>

    <!-- 搜索 -->
    <section class="search_box">
        <input placeholder="找商户，找优惠" type="text" id=key value="" onchange="inputChange(this.value)" />
    </section>

    <script>
        function selectedChange(typeValue){
            if(brandSubtype!=typeValue){
                document.getElementById("brandSubtype").value=typeValue;
                submitForm(document.getElementById("cityNow").innerText,document.getElementById("areaNow").innerText);
            }
        }
        function inputChange(keyValue){
            document.getElementById("key").value = keyValue;
            submitForm();
        }
        function localSelect(local){
            window.location.href="/mobile/jsp/creditcard/localSelect.jsp?localtype="+local+"&merchantType=7&city=北京";
        }
    </script>


    <!-- 商户列表 -->

    <section class="store_list" id="content">

        <div class="storeLi">
            <div class="li_top" onclick="searchDet('MP2015091510136158','教育培训类','北京市','MP2015091610136304')">

                <img width="75" height="75" src='http://mps.95508.com/merchant//brand/MP2015091510136158_website_small_87214.png' alt="北京樱花国际日语" />

                <h3 class="storeName">北京樱花国际日语</h3>
                <div class="actTime">

                    <i class="atime"></i>2014.10.01-2050.10.01

                    <div class="tagbox storeCate">教育培训类</div>
                </div>

            </div>
            <div class="li_middle">12、24期0首付、0利息、0手续费。</div>
            <div class="li_bottom">
                <div class="location" onclick="mapShow('北京市东城区东长安街1号东方广场w1座7层','樱花日语东方广场')"><i class="local"></i><a>樱花日语东方广场</a></div>
            </div>

        </div>

        <div class="storeLi">
            <div class="li_top" onclick="searchDet('MP2015091510135686','美容美发类','北京市','MP2015091510135877')">

                <img width="75" height="75" src='http://mps.95508.com/merchant//brand/MP2015091510135686_website_small_58371.png' alt="北京思妍丽" />

                <h3 class="storeName">北京思妍丽</h3>
                <div class="actTime">

                    <i class="atime"></i>2014.10.01-2020.10.01

                    <div class="tagbox storeCate">美容美发类</div>
                </div>

            </div>
            <div class="li_middle">3期0%,6期0%,12期0%</div>
            <div class="li_bottom">
                <div class="location" onclick="mapShow('东长安街1号东方广场汇贤豪庭公寓西侧一层','思妍丽(东方广场店)')"><i class="local"></i><a>思妍丽(东方广场店)</a></div>
            </div>

        </div>

        <div class="storeLi">
            <div class="li_top" onclick="searchDet('MP2015091510135686','美容美发类','北京市','MP2015091510135876')">

                <img width="75" height="75" src='http://mps.95508.com/merchant//brand/MP2015091510135686_website_small_58371.png' alt="北京思妍丽" />

                <h3 class="storeName">北京思妍丽</h3>
                <div class="actTime">

                    <i class="atime"></i>2014.10.01-2020.10.01

                    <div class="tagbox storeCate">美容美发类</div>
                </div>

            </div>
            <div class="li_middle">3期0%,6期0%,12期0%</div>
            <div class="li_bottom">
                <div class="location" onclick="mapShow('北京市西城区宣武门外大街10号庄胜广场中央办公楼北翼6层','思妍丽（崇光百货店）')"><i class="local"></i><a>思妍丽（崇光百货店）</a></div>
            </div>

        </div>

        <div class="storeLi">
            <div class="li_top" onclick="searchDet('MP2015091510136158','教育培训类','北京市','MP2015091610136305')">

                <img width="75" height="75" src='http://mps.95508.com/merchant//brand/MP2015091510136158_website_small_87214.png' alt="北京樱花国际日语" />

                <h3 class="storeName">北京樱花国际日语</h3>
                <div class="actTime">

                    <i class="atime"></i>2014.10.01-2050.10.01

                    <div class="tagbox storeCate">教育培训类</div>
                </div>

            </div>
            <div class="li_middle">12、24期0首付、0利息、0手续费。</div>
            <div class="li_bottom">
                <div class="location" onclick="mapShow('北京市东城区东直门外大街46号天恒大厦807','樱花日语东直门')"><i class="local"></i><a>樱花日语东直门</a></div>
            </div>

        </div>

        <div class="storeLi">
            <div class="li_top" onclick="searchDet('MP2015091510136012','教育培训类','北京市','MP2015091610136207')">

                <img width="75" height="75" src='http://mps.95508.com/merchant//brand/MP2015091510136012_website_small_08253.png' alt="西岸英语" />

                <h3 class="storeName">西岸英语</h3>
                <div class="actTime">

                    <i class="atime"></i>2014.10.01-2050.10.01

                    <div class="tagbox storeCate">教育培训类</div>
                </div>

            </div>
            <div class="li_middle">12、24期0首付、0利息、0手续费</div>
            <div class="li_bottom">
                <div class="location" onclick="mapShow('北京市朝阳区东三环中路9号富尔大厦705','西岸英语国贸店')"><i class="local"></i><a>西岸英语国贸店</a></div>
            </div>

        </div>

        <div class="storeLi">
            <div class="li_top" onclick="searchDet('MP2015091510136158','教育培训类','北京市','MP2015091610136306')">

                <img width="75" height="75" src='http://mps.95508.com/merchant//brand/MP2015091510136158_website_small_87214.png' alt="北京樱花国际日语" />

                <h3 class="storeName">北京樱花国际日语</h3>
                <div class="actTime">

                    <i class="atime"></i>2014.10.01-2050.10.01

                    <div class="tagbox storeCate">教育培训类</div>
                </div>

            </div>
            <div class="li_middle">12、24期0首付、0利息、0手续费。</div>
            <div class="li_bottom">
                <div class="location" onclick="mapShow('北京市朝阳区建国路118号招商局大厦4层','樱花日语国贸店')"><i class="local"></i><a>樱花日语国贸店</a></div>
            </div>

        </div>


    </section>

    <div id="pager_next_layout" style=" padding:8px 0;font-size:14px; background:#eee; text-align:center;"></div>
    <div id="pager_next_anchor" style="width:100%; bottom:0; position:fixed;"><a href="javascript:;"></a></div>


    <script src="/js/userAgent.js"></script>
    <script src="/js/pager.js"></script>
    <script src="/js/common.js"></script>
    <script type="text/javascript" language="javascript">
        var contentContainer = 'content';                   //内容容器ID
        var nextBtnContainer = 'pager_next_layout';          //下一页按钮容器ID
        var pagerReqURI = '/mobile/jsp/creditcard/merchant_list_more.jsp';       //翻页请求的服务器URL
        var pagerTotalPage = 0;                 //总页数
        var pagerTotalRecord = 0;                //总记录数
        var pagerPerPage =6;                     //每页大小
        var pagerToPage = 1;                                 //当前页数
        var pagerToId = 6;                       //当前最后的记录ID
        var pagerAutoLoadNextAnchor = 'pager_next_anchor';    //触发加载下一页的锚点
        var pagerAutoLoadNextOffset = 20;                    //当"加载更多"标签距离锚点多大开始自动加载
        var pagerTitle = {nextMore:'加载更多',nextLoading:'努力加载中...',nextEnd:'没有更多商户啦~~'};  //"加载更多"在不同状态下的标题
    </script>


    <script type="text/javascript">
        $(".li_name").on("click",function(){
            $(this).next().stop().slideToggle(200);
        });

        if(""=="null"){
            var brandSubtype = "";
        }else{
            var brandSubtype = "";
        }
        var merchantType = "7";
        var citys = "北京";
        function  submitForm(city,area){
            if(new RegExp(/'/).test(key) || new RegExp(/"/).test(key)){
                alert("请输入正确的搜索条件");
                return false;
            }
            document.getElementById("merchantType").value="7";
            if(city==undefined){
                document.getElementById("city").value="北京";
            }else{document.getElementById("city").value=city;}

            if(area!="" || area != undefined){
                document.getElementById("mArea").value=area;
            }
            document.getElementById("lat").value=sessionStorage.getItem("lat");
            document.getElementById("lng").value=sessionStorage.getItem("lng");
            document.getElementById("locationForm").submit();
        }


        function searchDet(merBrandId,brandSubtypeName, city,merId){
            document.getElementById("merBrandId").value=merBrandId;
            document.getElementById("brandSubtypeName").value=brandSubtypeName;
            document.getElementById("cityName").value=city;
            document.getElementById("merId").value=merId;
            document.getElementById("lat").value=sessionStorage.getItem("lat");
            document.getElementById("lng").value=sessionStorage.getItem("lng");
            document.getElementById("chooseCity").value=document.getElementById("cityNow").innerText;
            document.getElementById("chooseArea").value=document.getElementById("areaNow").innerText;
            document.getElementById("searchDetForm").submit();
        }

        function mapShow(addr,title){
            var myGeo = new BMap.Geocoder();
            myGeo.getPoint(addr,function(point){
                if(point){
                    window.location.href="http://api.map.baidu.com/marker?location="+point.lat+","+point.lng+"&title="+title+"&content="+addr+"&output=html";
                }else{
                    return false;
                }
            });
        }
    </script>
</body>
</html>