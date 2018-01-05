var Ecity = {};
Ecity._m = {
    /* 选择元素 */
    $:function (arg, context) {
        var tagAll, n, eles = [], i, sub = arg.substring(1);
        context = context || document;
        if (typeof arg == 'string') {
            switch (arg.charAt(0)) {
                case '#':
                    return document.getElementById(sub);
                    break;
                case '.':
                    if (context.getElementsByClassName) return context.getElementsByClassName(sub);
                    tagAll = Ecity._m.$('*', context);
                    n = tagAll.length;
                    for (i = 0; i < n; i++) {
                        if (tagAll[i].className.indexOf(sub) > -1) eles.push(tagAll[i]);
                    }
                    return eles;
                    break;
                default:
                    return context.getElementsByTagName(arg);
                    break;
            }
        }
    },

    /* 绑定事件 */
    on:function (node, type, handler) {
        node.addEventListener ? node.addEventListener(type, handler, false) : node.attachEvent('on' + type, handler);
    },

    /* 获取事件 */
    getEvent:function(event){
        return event || window.event;
    },

    /* 获取事件目标 */
    getTarget:function(event){
        return event.target || event.srcElement;
    },

    /* 获取元素位置 */
    getPos:function (node) {
        var scrollx = document.documentElement.scrollLeft || document.body.scrollLeft,
                scrollt = document.documentElement.scrollTop || document.body.scrollTop;
        var pos = node.getBoundingClientRect();
        return {top:pos.top + scrollt, right:pos.right + scrollx, bottom:pos.bottom + scrollt, left:pos.left + scrollx }
    },

    /* 添加样式名 */
    addClass:function (c, node) {
        if(!node)return;
        node.className = Ecity._m.hasClass(c,node) ? node.className : node.className + ' ' + c ;
    },

    /* 移除样式名 */
    removeClass:function (c, node) {
        var reg = new RegExp("(^|\\s+)" + c + "(\\s+|$)", "g");
        if(!Ecity._m.hasClass(c,node))return;
        node.className = reg.test(node.className) ? node.className.replace(reg, '') : node.className;
    },

    /* 是否含有CLASS */
    hasClass:function (c, node) {
        if(!node || !node.className)return false;
        return node.className.indexOf(c)>-1;
    },

    /* 阻止冒泡 */
    stopPropagation:function (event) {
        event = event || window.event;
        event.stopPropagation ? event.stopPropagation() : event.cancelBubble = true;
    },
    /* 去除两端空格 */
    trim:function (str) {
        return str.replace(/^\s+|\s+$/g,'');
    }
};

/* 所有城市数据,可以按照格式自行添加（北京|beijing|bj），前16条为热门城市 */

Ecity.allCity =  [];

/* 正则表达式 筛选中文城市名、拼音、首字母 */

Ecity.regEx = /^([\u4E00-\u9FA5\uf900-\ufa2d]+)\|(\w+)\|(\w)\w*$/i;
Ecity.regExHot = /^([\u4E00-\u9FA5\uf900-\ufa2d]+)\|\|$/i;
Ecity.regExChiese = /([\u4E00-\u9FA5\uf900-\ufa2d]+)/;

/* *
 * 格式化城市数组为对象oCity，按照a-h,i-p,q-z,hot热门城市分组：
 * {HOT:{hot:[]},ABCDEFGH:{a:[1,2,3],b:[1,2,3]},IJKLMNOP:{i:[1.2.3],j:[1,2,3]},QRSTUVWXYZ:{}}
 * */

Ecity.CitySelectorInit = function () {
    $(".portSelector").remove();
    var citys = Ecity.allCity, match, letter,
        regEx = Ecity.regEx,regExHot=Ecity.regExHot,
        reg2 = /^[a-e]$/i,reg3 = /^[f-j]$/i,reg4 = /^[k-p]$/i, reg5 = /^[q-w]$/i, reg6 = /^[x-z]$/i;
        Ecity.oCity = {ABCDE:{},FGHIJ:{},KLMNOP:{}, QRSTUVW:{}, XYZ:{}};
        //console.log(citys.length);
        for (var i = 0, n = citys.length; i < n; i++) {
            match = regEx.exec(citys[i]);
            if(match!=null){
                letter = match[3].toUpperCase();
                if (reg2.test(letter)) {
                    if (!Ecity.oCity.ABCDE[letter]) Ecity.oCity.ABCDE[letter] = [];
                    Ecity.oCity.ABCDE[letter].push(match[1]);
                } else if (reg3.test(letter)) {
                    if (!Ecity.oCity.FGHIJ[letter]) Ecity.oCity.FGHIJ[letter] = [];
                    Ecity.oCity.FGHIJ[letter].push(match[1]);
                } else if (reg4.test(letter)) {
                    if (!Ecity.oCity.KLMNOP[letter]) Ecity.oCity.KLMNOP[letter] = [];
                    Ecity.oCity.KLMNOP[letter].push(match[1]);
                } else if (reg5.test(letter)) {
                    if (!Ecity.oCity.QRSTUVW[letter]) Ecity.oCity.QRSTUVW[letter] = [];
                    Ecity.oCity.QRSTUVW[letter].push(match[1]);
                } else if (reg6.test(letter)) {
                    if (!Ecity.oCity.XYZ[letter]) Ecity.oCity.XYZ[letter] = [];
                    Ecity.oCity.XYZ[letter].push(match[1]);
                }
            }

            /* 热门城市 前16条 */
            //if(i<16){
            //    if(!Ecity.oCity.hot['hot']) Ecity.oCity.hot['hot'] = [];
            //    Ecity.oCity.hot['hot'].push(match[1]);
            //}
        }
};
/* 城市HTML模板 */
Ecity._template = [
    '<p class="tip">支持中文/拼音输入</p>',
    '<ul>',
    '<li class="on">ABCDE</li>',
	'<li>FGHIJ</li>',
	'<li>KLMNOP</li>',
    '<li>QRSTUVW</li>',
    '<li>XYZ</li>',
    '</ul>'
];

/* *
 * 城市控件构造函数
 * @CitySelector
 * */

Ecity.CitySelector = function () {
    this.initialize.apply(this, arguments);
};

Ecity.CitySelector.prototype = {

    constructor:Ecity.CitySelector,

    /* 初始化 */

    initialize :function (options) {
        var input = options.input;
        this.input = Ecity._m.$('#'+ input);
        this.inputEvent();
    },

    /* *
     * @createWarp
     * 创建城市BOX HTML 框架
     * */

    createWarp:function(){
        var inputPos = Ecity._m.getPos(this.input);
        var div = this.rootDiv = document.createElement('div');
        var that = this;

        // 设置DIV阻止冒泡
        Ecity._m.on(this.rootDiv,'click',function(event){
            Ecity._m.stopPropagation(event);
        });

        // 设置点击文档隐藏弹出的城市选择框
        Ecity._m.on(document, 'click', function (event) {
            event = Ecity._m.getEvent(event);
            var target = Ecity._m.getTarget(event);
            if(target == that.input) return false;
            //console.log(target.className);
            if (that.cityBox)Ecity._m.addClass('hide', that.cityBox);
            if (that.ul)Ecity._m.addClass('hide', that.ul);
            if(that.myIframe)Ecity._m.addClass('hide',that.myIframe);
        });
        div.className = 'citySelector portSelector';
        div.style.position = 'absolute';
        div.style.left = inputPos.left + 'px';
        div.style.top = inputPos.bottom + 'px';
        div.style.zIndex = 999999;

        // 判断是否IE6，如果是IE6需要添加iframe才能遮住SELECT框
        var isIe = (document.all) ? true : false;
        var isIE6 = this.isIE6 = isIe && !window.XMLHttpRequest;
        if(isIE6){
            var myIframe = this.myIframe =  document.createElement('iframe');
            myIframe.frameborder = '0';
            myIframe.src = 'about:blank';
            myIframe.style.position = 'absolute';
            myIframe.style.zIndex = '-1';
            this.rootDiv.appendChild(this.myIframe);
        }

        var childdiv = this.cityBox = document.createElement('div');
        childdiv.className = 'cityBox';
        childdiv.id = 'cityBox';
        childdiv.innerHTML = Ecity._template.join('');
        var hotCity = this.hotCity =  document.createElement('div');
        hotCity.className = 'hotCity';
        childdiv.appendChild(hotCity);
        div.appendChild(childdiv);
        this.createHotCity();
    },

    /* *
     * @createHotCity
     * TAB下面DIV：hot,a-h,i-p,q-z 分类HTML生成，DOM操作
     * {HOT:{hot:[]},ABCDEFGH:{a:[1,2,3],b:[1,2,3]},IJKLMNOP:{},QRSTUVWXYZ:{}}
     **/

    createHotCity:function(){
        var odiv,odl,odt,odd,odda=[],str,key,ckey,sortKey,regEx = Ecity.regEx,
                oCity = Ecity.oCity;
        for(key in oCity){
            odiv = this[key] = document.createElement('div');
            // 先设置全部隐藏hide
            odiv.className = key + ' ' + 'cityTab hide';
            sortKey=[];
            for(ckey in oCity[key]){
                sortKey.push(ckey);
                // ckey按照ABCDEDG顺序排序
                sortKey.sort();
            }
            for(var j=0,k = sortKey.length;j<k;j++){
                odl = document.createElement('dl');
                odt = document.createElement('dt');
                odd = document.createElement('dd');
                odt.innerHTML = sortKey[j] == 'ABCDE'?'&nbsp;':sortKey[j];
                odda = [];
                for(var i=0,n=oCity[key][sortKey[j]].length;i<n;i++){
                    str = '<a href="#">' + oCity[key][sortKey[j]][i] + '</a>';
                    odda.push(str);
                }
                odd.innerHTML = odda.join('');
                odl.appendChild(odt);
                odl.appendChild(odd);
                odiv.appendChild(odl);
            }

            // 移除热门城市的隐藏CSS
            Ecity._m.removeClass('hide',this.ABCDE);
            this.hotCity.appendChild(odiv);
        }
        document.body.appendChild(this.rootDiv);
        /* IE6 */
        this.changeIframe();

        this.tabChange();
        this.linkEvent();
    },

    /* *
     *  tab按字母顺序切换
     *  @ tabChange
     * */

    tabChange:function(){
        var lis = Ecity._m.$('li',this.cityBox);
        var divs = Ecity._m.$('div',this.hotCity);
        var that = this;
        for(var i=0,n=lis.length;i<n;i++){
            lis[i].index = i;
            lis[i].onclick = function(){
                for(var j=0;j<n;j++){
                    Ecity._m.removeClass('on',lis[j]);
                    Ecity._m.addClass('hide',divs[j]);
                }
                Ecity._m.addClass('on',this);
                Ecity._m.removeClass('hide',divs[this.index]);
                /* IE6 改变TAB的时候 改变Iframe 大小*/
                that.changeIframe();
            };
        }
    },

    /* *
     * 城市LINK事件
     *  @linkEvent
     * */

    linkEvent:function(){
        var links = Ecity._m.$('a',this.hotCity);
        var that = this;
        for(var i=0,n=links.length;i<n;i++){
            links[i].onclick = function(){
                that.input.value = this.innerHTML;
                var forName = document.getElementById("endName_search");
                if (forName) {
                    forName.value = this.innerHTML;
                }
                Ecity._m.addClass('hide',that.cityBox);
                /* 点击城市名的时候隐藏myIframe */
                Ecity._m.addClass('hide',that.myIframe);
            }
        }
    },

    /* *
     * INPUT城市输入框事件
     * @inputEvent
     * */

    inputEvent:function(){
        var that = this;
        Ecity._m.on(this.input,'click',function(event){
            event = event || window.event;
            if(!that.cityBox){
                that.createWarp();
            }else if(!!that.cityBox && Ecity._m.hasClass('hide',that.cityBox)){
                // slideul 不存在或者 slideul存在但是是隐藏的时候 两者不能共存
                if(!that.ul || (that.ul && Ecity._m.hasClass('hide',that.ul))){
                    Ecity._m.removeClass('hide',that.cityBox);

                    /* IE6 移除iframe 的hide 样式 */
                    //alert('click');
                    Ecity._m.removeClass('hide',that.myIframe);
                    that.changeIframe();
                }
            }
        });
        Ecity._m.on(this.input,'focus',function(){
            that.input.select();
            if(that.input.value == '目的地') {
                $("#endName_search").val("");
                that.input.value = '';
            }
        });
        Ecity._m.on(this.input,'blur',function(){
            if(that.input.value == '') {
                $("#endName_search").val("");
                that.input.value = '目的地';
            }
        });
        Ecity._m.on(this.input,'keyup',function(event){
            event = event || window.event;
            var keycode = event.keyCode;
            Ecity._m.addClass('hide',that.cityBox);
            that.createUl();

            /* 移除iframe 的hide 样式 */
            Ecity._m.removeClass('hide',that.myIframe);

            // 下拉菜单显示的时候捕捉按键事件
            if(that.ul && !Ecity._m.hasClass('hide',that.ul) && !that.isEmpty){
                that.KeyboardEvent(event,keycode);
            }
        });
    },

    /* *
     * 生成下拉选择列表
     * @ createUl
     * */

    createUl:function () {
        //console.log('createUL');
        $("#endName_search").val("");
        var str;
        var value = Ecity._m.trim(this.input.value);
        // 当value不等于空的时候执行
        if (value !== '') {
            var reg = new RegExp("^" + value + "|\\|" + value, 'gi');
            // 此处需设置中文输入法也可用onpropertychange
            var searchResult = [];
            for (var i = 0, n = Ecity.allCity.length; i < n; i++) {
                if (reg.test(Ecity.allCity[i])) {
                    var match = Ecity.regEx.exec(Ecity.allCity[i]);
                    if (match) {
                        if (searchResult.length !== 0) {
                            str = '<li><b class="cityname">' + match[1] + '</b><b class="cityspell">' + match[2] + '</b></li>';
                        } else {
                            str = '<li class="on"><b class="cityname">' + match[1] + '</b><b class="cityspell">' + match[2] + '</b></li>';
                        }
                        searchResult.push(str);
                    }
                }
            }
            this.isEmpty = false;
            // 如果搜索数据为空
            if (searchResult.length == 0) {
                this.isEmpty = true;
                str = '<li class="empty">对不起，没有找到：<em>' + value + '</em></li>';
                searchResult.push(str);
            }
            // 如果slideul不存在则添加ul
            if (!this.ul) {
                var ul = this.ul = document.createElement('ul');
                ul.className = 'cityslide';
                this.rootDiv && this.rootDiv.appendChild(ul);
                // 记录按键次数，方向键
                this.count = 0;
            } else if (this.ul && Ecity._m.hasClass('hide', this.ul)) {
                this.count = 0;
                Ecity._m.removeClass('hide', this.ul);
            }
            this.ul.innerHTML = searchResult.join('');

            /* IE6 */
            this.changeIframe();

            // 绑定Li事件
            this.liEvent();
        }else{
            Ecity._m.addClass('hide',this.ul);

            Ecity._m.removeClass('hide',this.cityBox);

            Ecity._m.removeClass('hide',this.myIframe);

            this.changeIframe();
        }
    },

    /* IE6的改变遮罩SELECT 的 IFRAME尺寸大小 */
    changeIframe:function(){
        if(!this.isIE6)return;
        this.myIframe.style.width = this.rootDiv.offsetWidth + 'px';
        this.myIframe.style.height = this.rootDiv.offsetHeight + 'px';
    },

    /* *
     * 特定键盘事件，上、下、Enter键
     * @ KeyboardEvent
     * */

    KeyboardEvent:function(event,keycode){
        var lis = Ecity._m.$('li',this.ul);
        var len = lis.length;
        switch(keycode){
            case 40: //向下箭头↓
                this.count++;
                if(this.count > len-1) this.count = 0;
                for(var i=0;i<len;i++){
                    Ecity._m.removeClass('on',lis[i]);
                }
                Ecity._m.addClass('on',lis[this.count]);
                break;
            case 38: //向上箭头↑
                this.count--;
                if(this.count<0) this.count = len-1;
                for(i=0;i<len;i++){
                    Ecity._m.removeClass('on',lis[i]);
                }
                Ecity._m.addClass('on',lis[this.count]);
                break;
            case 13: // enter键
                this.input.value = Ecity.regExChiese.exec(lis[this.count].innerHTML)[0];
                Ecity._m.addClass('hide',this.ul);
                Ecity._m.addClass('hide',this.ul);
                /* IE6 */
                Ecity._m.addClass('hide',this.myIframe);
                break;
            default:
                break;
        }
    },

    /* *
     * 下拉列表的li事件
     * @ liEvent
     * */

    liEvent:function(){
        var that = this;
        var lis = Ecity._m.$('li',this.ul);
        for(var i = 0,n = lis.length;i < n;i++){
            Ecity._m.on(lis[i],'click',function(event){
                event = Ecity._m.getEvent(event);
                var target = Ecity._m.getTarget(event);
                that.input.value = Ecity.regExChiese.exec(target.innerHTML)[0];
                var forName = document.getElementById("endName_search");
                if (forName) {
                    forName.value =Vcity.regExChiese.exec(target.innerHTML)[0];
                }
                Ecity._m.addClass('hide',that.ul);
                /* IE6 下拉菜单点击事件 */
                Ecity._m.addClass('hide',that.myIframe);
            });
            Ecity._m.on(lis[i],'mouseover',function(event){
                event = Ecity._m.getEvent(event);
                var target = Ecity._m.getTarget(event);
                Ecity._m.addClass('on',target);
            });
            Ecity._m.on(lis[i],'mouseout',function(event){
                event = Ecity._m.getEvent(event);
                var target = Ecity._m.getTarget(event);
                Ecity._m.removeClass('on',target);
            })
        }
    }
};
