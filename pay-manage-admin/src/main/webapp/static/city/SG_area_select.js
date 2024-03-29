$(document).ready(function () {
    $.areaSelect = function (init) {

        var Province, State, City;
        var CityJson = init;
        createDom();
        // writeData();
        replyOperation();
        var myscrol0 = new iScroll("wrapper0", {
            hScroll: false, vScrollbar: false, onScrollEnd: function () {

            }
        });

        /*var myscroll1 = new iScroll("wrapper1");
        var myscroll2 = new iScroll("wrapper2");*/

        /*生成地区选择界面结构*/
        function createDom() {
            var html = '';
            html += '<div class="sg-mask">';
            html += '<div class="popups-box">';
            html += '<div class="title-min"><span class="query">取消</span>请选择地区<span class="submit">确定</span></div>';
            html += '<div class="title-max">请选择地区<span class="close">X</span></div>';
            html += '<div class="result-box">';
            html += '<label>已选择</label>';
            html += '<input id="hasCheck" type="text" name="area">';
            html += '<input id="hasCheckCode" type="hidden" name="areaCode">';
            html += '</div>';
            html += '<div class="tab-box">';
            html += '<div class="tab-menu">';
            html += '<ul>';
            html += '<li class="tab-1 isCheck">省份</li>';
            html += '<li class="tab-2">城市</li>';
            html += '<li class="tab-3">县区</li>';
            html += '</ul>';
            html += '</div>';
            html += '<div class="content-box">';
            html += '<div id="wrapper0" class="tab-content tab-list-1 isBlock">';
            html += '<ul></ul>';
            html += '</div>';
            html += '<div id="wrapper1"  class="tab-content tab-list-2">';
            html += '<ul></ul>';
            html += '</div>';
            html += '<div id="wrapper2"  class="tab-content tab-list-3">';
            html += '<ul></ul>';
            html += '</div>';
            html += '</div>';
            html += '</div> ';
            html += '<div class="footer">';
            html += '<button id="submitBtn">确定</button>';
            html += '</div> ';
            html += '</div>';
            html += '</div>';
            $('body').append(html);

            //$('.popups-box').addClass('fadeUp');
            $(".popups-box").show().animate({bottom: '0px'})
        }

        /*样式设置*/
        function setDomStyle() {
        }

        /*选择操作响应*/
        function replyOperation() {

            var provinceDom = '';
            var regionCode = 11;
            var cityCode = 01;

            //PC端页签切换
            var listOne = $('.tab-list-1');
            var listtwo = $('.tab-list-2');
            var listthree = $('.tab-list-3');
            $('.tab-1').click(function () {
                listOne.addClass('isBlock').siblings().removeClass('isBlock');
                $(this).addClass('isCheck').siblings().removeClass('isCheck');
            })
            $('.tab-2').click(function () {
                listtwo.addClass('isBlock').siblings().removeClass('isBlock');
                $(this).addClass('isCheck').siblings().removeClass('isCheck');
            })
            $('.tab-3').click(function () {
                listthree.addClass('isBlock').siblings().removeClass('isBlock');
                $(this).addClass('isCheck').siblings().removeClass('isCheck');
            })

            for (var i = 0; i < CityJson.length; i++) {
                provinceDom += '<li><a>' + CityJson[i].region.name + '<input value=' + CityJson[i].region.code + ' style="display:none"></a></li>';
            }
            $('.tab-list-1 ul').html(provinceDom);

            //PC端选择省份
            $('.tab-list-1 li a').click(function () {
                $(this).parent().addClass('minISClick').siblings().removeClass('minISClick');
                regionCode = $(this).children('input').val();
                Province = $(this).text();
                $('#hasCheck').val(Province);
                $('#hasCheckCode').val(regionCode);
                $('.tab-list-2 ul').empty();
                $('.tab-list-3 ul').empty();
                createState();
                //PC端选择城市
                $('.tab-list-2 li a').click(function () {
                    $(this).parent().addClass('minISClick').siblings().removeClass('minISClick');
                    cityCode = $(this).children('input').val();
                    State = $(this).text();
                    $('#hasCheck').val(Province + '-' + State);
                    $('#hasCheckCode').val(regionCode + ',' + cityCode);
                    $('.tab-list-3 ul').empty();
                    createCity();
                    //PC端选择县区
                    $('.tab-list-3 li a').click(function () {
                        $(this).parent().addClass('minISClick').siblings().removeClass('minISClick');
                        City = $(this).text();
                        $('#hasCheck').val(Province + '-' + State + '-' + City);
                        $('#hasCheckCode').val(regionCode + ',' + cityCode + ',' + $(this).children('input').val());
                    })

                })

            })

            //创建二级菜单
            function createState() {
                var stateDom = '';
                for (var j = 0; j < CityJson.length; j++) {
                    if (CityJson[j].region.code == regionCode) {
                        for (var k = 0; k < CityJson[j].region.state.length; k++) {
                            stateDom += '<li><a>' + CityJson[j].region.state[k].name + '<input value="' + CityJson[j].region.state[k].code + '" style="display:none"></a></li>';
                        }
                        $('.tab-list-2 ul').html(stateDom);
                    }
                }

                //页签切换
                $('.tab-2').click();
                var myscrol1 = new iScroll("wrapper1", {
                    hScroll: false, vScrollbar: false,
                });
            }

            //创建三级菜单
            function createCity() {
                var cityDom = '';
                for (var j = 0; j < CityJson.length; j++) {
                    if (CityJson[j].region.code == regionCode) {
                        for (var k = 0; k < CityJson[j].region.state.length; k++) {
                            if (CityJson[j].region.state[k].code == cityCode) {
                                for (var l = 0; l < CityJson[j].region.state[k].city.length; l++) {
                                    cityDom += '<li><a>' + CityJson[j].region.state[k].city[l].name + '<input value="' + CityJson[j].region.state[k].city[l].code + '" style="display:none"></a></li>';
                                }
                                $('.tab-list-3 ul').html(cityDom);
                            }

                        }
                    }
                }
                //页签切换
                $('.tab-3').click();
                var myscrol2 = new iScroll("wrapper2", {
                    hScroll: false, vScrollbar: false,
                });
            }
        }

        /*PC关闭操作响应*/
        $('.popups-box .close').click(function () {
            $('.sg-area-result').val($('#hasCheck').val());
            $('.sg-mask').css('display', 'none');
            $('.sg-mask').remove();

        });
        /*PC确定操作响应*/
        $('#submitBtn').click(function () {
            $('.sg-area-result').val($('#hasCheck').val());
            $('.sg-area-resultCode').val($('#hasCheckCode').val());
            $('.sg-mask').css('display', 'none');
            $('.sg-mask').remove();
        });


        /*移动端取消操作响应*/
        $('.title-min .query').on('click', function () {
            $('.sg-area-result').val();
            $(".popups-box").show().animate({bottom: '-251px'}, function () {
                $('.sg-mask').css('display', 'none');
                $('.sg-mask').remove();
            })

        })

        /*移动端确定操作响应*/
        $('.title-min .submit').on('click', function () {
            $('.sg-area-result').val($('#hasCheck').val());
            $(".popups-box").show().animate({bottom: '-251px'}, function () {
                $('.sg-mask').css('display', 'none');
                $('.sg-mask').remove();
            })
        })

        /*地区信息*/
        function writeData() {
            CityJson = [{
                region: {
                    name: '北京市',
                    code: '11',
                    state: [{
                        name: '北京',
                        code: '01',
                        city: [{name: '东城区', code: '01'}, {name: '西城区', code: '02'}, {
                            name: '崇文区',
                            code: '03'
                        }, {name: '宣武区', code: '04'}, {name: '朝阳区', code: '05'}, {name: '丰台区', code: '06'}, {
                            name: '石景山区',
                            code: '07'
                        }, {name: '海淀区', code: '08'}, {name: '门头沟区', code: '09'}, {name: '房山区', code: '11'}, {
                            name: '通州区',
                            code: '12'
                        }, {name: '顺义区', code: '13'}, {name: '昌平区', code: '14'}, {name: '大兴区', code: '15'}, {
                            name: '怀柔区',
                            code: '16'
                        }, {name: '平谷区', code: '17'}, {name: '密云县', code: '28'}, {name: '延庆县', code: '29'}]
                    }]
                }
            }, {
                region: {
                    name: '天津市',
                    code: '12',
                    state: [{
                        name: '天津',
                        code: '01',
                        city: [{name: '和平区', code: '01'}, {name: '河东区', code: '02'}, {
                            name: '河西区',
                            code: '03'
                        }, {name: '南开区', code: '04'}, {name: '河北区', code: '05'}, {name: '红桥区', code: '06'}, {
                            name: '塘沽区',
                            code: '07'
                        }, {name: '汉沽区', code: '08'}, {name: '大港区', code: '09'}, {name: '东丽区', code: '10'}, {
                            name: '西青区',
                            code: '11'
                        }, {name: '津南区', code: '12'}, {name: '北辰区', code: '13'}, {name: '武清区', code: '14'}, {
                            name: '宝坻区',
                            code: '15'
                        }, {name: '宁河县', code: '21'}, {name: '静海县', code: '23'}, {name: '蓟县', code: '25'}]
                    }]
                }
            }, {
                region: {
                    name: '河北省',
                    code: '13',
                    state: [{
                        name: '石家庄市',
                        code: '01',
                        city: [{name: '长安区', code: '02'}, {name: '桥东区', code: '03'}, {
                            name: '桥西区',
                            code: '04'
                        }, {name: '新华区', code: '05'}, {name: '井陉矿区', code: '07'}, {name: '裕华区', code: '08'}, {
                            name: '井陉县',
                            code: '21'
                        }, {name: '正定县', code: '23'}, {name: '栾城县', code: '24'}, {name: '行唐县', code: '25'}, {
                            name: '灵寿县',
                            code: '26'
                        }, {name: '高邑县', code: '27'}, {name: '深泽县', code: '28'}, {name: '赞皇县', code: '29'}, {
                            name: '无极县',
                            code: '30'
                        }, {name: '平山县', code: '31'}, {name: '元氏县', code: '32'}, {name: '赵县', code: '33'}, {
                            name: '辛集市',
                            code: '81'
                        }, {name: '藁城市', code: '82'}, {name: '晋州市', code: '83'}, {name: '新乐市', code: '84'}, {
                            name: '鹿泉市',
                            code: '85'
                        }]
                    }, {
                        name: '唐山市',
                        code: '02',
                        city: [{name: '路南区', code: '02'}, {name: '路北区', code: '03'}, {
                            name: '古冶区',
                            code: '04'
                        }, {name: '开平区', code: '05'}, {name: '丰南区', code: '07'}, {name: '丰润区', code: '08'}, {
                            name: '滦县',
                            code: '23'
                        }, {name: '滦南县', code: '24'}, {name: '乐亭县', code: '25'}, {name: '迁西县', code: '27'}, {
                            name: '玉田县',
                            code: '29'
                        }, {name: '唐海县', code: '30'}, {name: '遵化市', code: '81'}, {name: '迁安市', code: '83'}]
                    }, {
                        name: '秦皇岛市',
                        code: '03',
                        city: [{name: '海港区', code: '02'}, {name: '山海关区', code: '03'}, {
                            name: '北戴河区',
                            code: '04'
                        }, {name: '青龙满族自治县', code: '21'}, {name: '昌黎县', code: '22'}, {
                            name: '抚宁县',
                            code: '23'
                        }, {name: '卢龙县', code: '24'}]
                    }, {
                        name: '邯郸市',
                        code: '04',
                        city: [{name: '邯山区', code: '02'}, {name: '丛台区', code: '03'}, {
                            name: '复兴区',
                            code: '04'
                        }, {name: '峰峰矿区', code: '06'}, {name: '邯郸县', code: '21'}, {name: '临漳县', code: '23'}, {
                            name: '成安县',
                            code: '24'
                        }, {name: '大名县', code: '25'}, {name: '涉县', code: '26'}, {name: '磁县', code: '27'}, {
                            name: '肥乡县',
                            code: '28'
                        }, {name: '永年县', code: '29'}, {name: '邱县', code: '30'}, {name: '鸡泽县', code: '31'}, {
                            name: '广平县',
                            code: '32'
                        }, {name: '馆陶县', code: '33'}, {name: '魏县', code: '34'}, {name: '曲周县', code: '35'}, {
                            name: '武安市',
                            code: '81'
                        }]
                    }, {
                        name: '邢台市',
                        code: '05',
                        city: [{name: '桥东区', code: '02'}, {name: '桥西区', code: '03'}, {
                            name: '邢台县',
                            code: '21'
                        }, {name: '临城县', code: '22'}, {name: '内丘县', code: '23'}, {name: '柏乡县', code: '24'}, {
                            name: '隆尧县',
                            code: '25'
                        }, {name: '任县', code: '26'}, {name: '南和县', code: '27'}, {name: '宁晋县', code: '28'}, {
                            name: '巨鹿县',
                            code: '29'
                        }, {name: '新河县', code: '30'}, {name: '广宗县', code: '31'}, {name: '平乡县', code: '32'}, {
                            name: '威县',
                            code: '33'
                        }, {name: '清河县', code: '34'}, {name: '临西县', code: '35'}, {name: '南宫市', code: '81'}, {
                            name: '沙河市',
                            code: '82'
                        }]
                    }, {
                        name: '保定市',
                        code: '06',
                        city: [{name: '新市区', code: '02'}, {name: '北市区', code: '03'}, {
                            name: '南市区',
                            code: '04'
                        }, {name: '满城县', code: '21'}, {name: '清苑县', code: '22'}, {name: '涞水县', code: '23'}, {
                            name: '阜平县',
                            code: '24'
                        }, {name: '徐水县', code: '25'}, {name: '定兴县', code: '26'}, {name: '唐县', code: '27'}, {
                            name: '高阳县',
                            code: '28'
                        }, {name: '容城县', code: '29'}, {name: '涞源县', code: '30'}, {name: '望都县', code: '31'}, {
                            name: '安新县',
                            code: '32'
                        }, {name: '易县', code: '33'}, {name: '曲阳县', code: '34'}, {name: '蠡县', code: '35'}, {
                            name: '顺平县',
                            code: '36'
                        }, {name: '博野县', code: '37'}, {name: '雄县', code: '38'}, {name: '涿州市', code: '81'}, {
                            name: '定州市',
                            code: '82'
                        }, {name: '安国市', code: '83'}, {name: '高碑店市', code: '84'}]
                    }, {
                        name: '张家口市',
                        code: '07',
                        city: [{name: '桥东区', code: '02'}, {name: '桥西区', code: '03'}, {
                            name: '宣化区',
                            code: '05'
                        }, {name: '下花园区', code: '06'}, {name: '宣化县', code: '21'}, {name: '张北县', code: '22'}, {
                            name: '康保县',
                            code: '23'
                        }, {name: '沽源县', code: '24'}, {name: '尚义县', code: '25'}, {name: '蔚县', code: '26'}, {
                            name: '阳原县',
                            code: '27'
                        }, {name: '怀安县', code: '28'}, {name: '万全县', code: '29'}, {name: '怀来县', code: '30'}, {
                            name: '涿鹿县',
                            code: '31'
                        }, {name: '赤城县', code: '32'}, {name: '崇礼县', code: '33'}]
                    }, {
                        name: '承德市',
                        code: '08',
                        city: [{name: '双桥区', code: '02'}, {name: '双滦区', code: '03'}, {
                            name: '鹰手营子矿区',
                            code: '04'
                        }, {name: '承德县', code: '21'}, {name: '兴隆县', code: '22'}, {name: '平泉县', code: '23'}, {
                            name: '滦平县',
                            code: '24'
                        }, {name: '隆化县', code: '25'}, {name: '丰宁满族自治县', code: '26'}, {
                            name: '宽城满族自治县',
                            code: '27'
                        }, {name: '围场满族蒙古族自治县', code: '28'}]
                    }, {
                        name: '沧州市',
                        code: '09',
                        city: [{name: '新华区', code: '02'}, {name: '运河区', code: '03'}, {name: '沧县', code: '21'}, {
                            name: '青县',
                            code: '22'
                        }, {name: '东光县', code: '23'}, {name: '海兴县', code: '24'}, {name: '盐山县', code: '25'}, {
                            name: '肃宁县',
                            code: '26'
                        }, {name: '南皮县', code: '27'}, {name: '吴桥县', code: '28'}, {name: '献县', code: '29'}, {
                            name: '孟村回族自治县',
                            code: '30'
                        }, {name: '泊头市', code: '81'}, {name: '任丘市', code: '82'}, {name: '黄骅市', code: '83'}, {
                            name: '河间市',
                            code: '84'
                        }]
                    }, {
                        name: '廊坊市',
                        code: '10',
                        city: [{name: '安次区', code: '02'}, {name: '广阳区', code: '03'}, {
                            name: '固安县',
                            code: '22'
                        }, {name: '永清县', code: '23'}, {name: '香河县', code: '24'}, {name: '大城县', code: '25'}, {
                            name: '文安县',
                            code: '26'
                        }, {name: '大厂回族自治县', code: '28'}, {name: '霸州市', code: '81'}, {name: '三河市', code: '82'}]
                    }, {
                        name: '衡水市',
                        code: '11',
                        city: [{name: '桃城区', code: '02'}, {name: '枣强县', code: '21'}, {
                            name: '武邑县',
                            code: '22'
                        }, {name: '武强县', code: '23'}, {name: '饶阳县', code: '24'}, {name: '安平县', code: '25'}, {
                            name: '故城县',
                            code: '26'
                        }, {name: '景县', code: '27'}, {name: '阜城县', code: '28'}, {name: '冀州市', code: '81'}, {
                            name: '深州市',
                            code: '82'
                        }]
                    }]
                }
            }, {
                region: {
                    name: '山西省',
                    code: '14',
                    state: [{
                        name: '太原市',
                        code: '01',
                        city: [{name: '小店区', code: '05'}, {name: '迎泽区', code: '06'}, {
                            name: '杏花岭区',
                            code: '07'
                        }, {name: '尖草坪区', code: '08'}, {name: '万柏林区', code: '09'}, {name: '晋源区', code: '10'}, {
                            name: '清徐县',
                            code: '21'
                        }, {name: '阳曲县', code: '22'}, {name: '娄烦县', code: '23'}, {name: '古交市', code: '81'}]
                    }, {
                        name: '大同市',
                        code: '02',
                        city: [{name: '城区', code: '02'}, {name: '矿区', code: '03'}, {name: '南郊区', code: '11'}, {
                            name: '新荣区',
                            code: '12'
                        }, {name: '阳高县', code: '21'}, {name: '天镇县', code: '22'}, {name: '广灵县', code: '23'}, {
                            name: '灵丘县',
                            code: '24'
                        }, {name: '浑源县', code: '25'}, {name: '左云县', code: '26'}, {name: '大同县', code: '27'}]
                    }, {
                        name: '阳泉市',
                        code: '03',
                        city: [{name: '城区', code: '02'}, {name: '矿区', code: '03'}, {name: '郊区', code: '11'}, {
                            name: '平定县',
                            code: '21'
                        }, {name: '盂县', code: '22'}]
                    }, {
                        name: '长治市',
                        code: '04',
                        city: [{name: '城区', code: '02'}, {name: '郊区', code: '11'}, {name: '长治县', code: '21'}, {
                            name: '襄垣县',
                            code: '23'
                        }, {name: '屯留县', code: '24'}, {name: '平顺县', code: '25'}, {name: '黎城县', code: '26'}, {
                            name: '壶关县',
                            code: '27'
                        }, {name: '长子县', code: '28'}, {name: '武乡县', code: '29'}, {name: '沁县', code: '30'}, {
                            name: '沁源县',
                            code: '31'
                        }, {name: '潞城市', code: '81'}]
                    }, {
                        name: '晋城市',
                        code: '05',
                        city: [{name: '城区', code: '02'}, {name: '沁水县', code: '21'}, {name: '阳城县', code: '22'}, {
                            name: '陵川县',
                            code: '24'
                        }, {name: '泽州县', code: '25'}, {name: '高平市', code: '81'}]
                    }, {
                        name: '朔州市',
                        code: '06',
                        city: [{name: '朔城区', code: '02'}, {name: '平鲁区', code: '03'}, {name: '山阴县', code: '21'}, {
                            name: '应县',
                            code: '22'
                        }, {name: '右玉县', code: '23'}, {name: '怀仁县', code: '24'}]
                    }, {
                        name: '晋中市',
                        code: '07',
                        city: [{name: '榆次区', code: '02'}, {name: '榆社县', code: '21'}, {
                            name: '左权县',
                            code: '22'
                        }, {name: '和顺县', code: '23'}, {name: '昔阳县', code: '24'}, {name: '寿阳县', code: '25'}, {
                            name: '太谷县',
                            code: '26'
                        }, {name: '祁县', code: '27'}, {name: '平遥县', code: '28'}, {name: '灵石县', code: '29'}, {
                            name: '介休市',
                            code: '81'
                        }]
                    }, {
                        name: '运城市',
                        code: '08',
                        city: [{name: '盐湖区', code: '02'}, {name: '临猗县', code: '21'}, {
                            name: '万荣县',
                            code: '22'
                        }, {name: '闻喜县', code: '23'}, {name: '稷山县', code: '24'}, {name: '新绛县', code: '25'}, {
                            name: '绛县',
                            code: '26'
                        }, {name: '垣曲县', code: '27'}, {name: '夏县', code: '28'}, {name: '平陆县', code: '29'}, {
                            name: '芮城县',
                            code: '30'
                        }, {name: '永济市', code: '81'}, {name: '河津市', code: '82'}]
                    }, {
                        name: '忻州市',
                        code: '09',
                        city: [{name: '忻府区', code: '02'}, {name: '定襄县', code: '21'}, {name: '五台县', code: '22'}, {
                            name: '代县',
                            code: '23'
                        }, {name: '繁峙县', code: '24'}, {name: '宁武县', code: '25'}, {name: '静乐县', code: '26'}, {
                            name: '神池县',
                            code: '27'
                        }, {name: '五寨县', code: '28'}, {name: '岢岚县', code: '29'}, {name: '河曲县', code: '30'}, {
                            name: '保德县',
                            code: '31'
                        }, {name: '偏关县', code: '32'}, {name: '原平市', code: '81'}]
                    }, {
                        name: '临汾市',
                        code: '10',
                        city: [{name: '尧都区', code: '02'}, {name: '曲沃县', code: '21'}, {
                            name: '翼城县',
                            code: '22'
                        }, {name: '襄汾县', code: '23'}, {name: '洪洞县', code: '24'}, {name: '古县', code: '25'}, {
                            name: '安泽县',
                            code: '26'
                        }, {name: '浮山县', code: '27'}, {name: '吉县', code: '28'}, {name: '乡宁县', code: '29'}, {
                            name: '大宁县',
                            code: '30'
                        }, {name: '隰县', code: '31'}, {name: '永和县', code: '32'}, {name: '蒲县', code: '33'}, {
                            name: '汾西县',
                            code: '34'
                        }, {name: '侯马市', code: '81'}, {name: '霍州市', code: '82'}]
                    }, {
                        name: '吕梁市',
                        code: '11',
                        city: [{name: '离石区', code: '02'}, {name: '文水县', code: '21'}, {name: '交城县', code: '22'}, {
                            name: '兴县',
                            code: '23'
                        }, {name: '临县', code: '24'}, {name: '柳林县', code: '25'}, {name: '石楼县', code: '26'}, {
                            name: '岚县',
                            code: '27'
                        }, {name: '方山县', code: '28'}, {name: '中阳县', code: '29'}, {name: '交口县', code: '30'}, {
                            name: '孝义市',
                            code: '81'
                        }, {name: '汾阳市', code: '82'}]
                    }]
                }
            }, {
                region: {
                    name: '内蒙古',
                    code: '15',
                    state: [{
                        name: '呼和浩特市',
                        code: '01',
                        city: [{name: '新城区', code: '02'}, {name: '回民区', code: '03'}, {
                            name: '玉泉区',
                            code: '04'
                        }, {name: '赛罕区', code: '05'}, {name: '土默特左旗', code: '21'}, {
                            name: '托克托县',
                            code: '22'
                        }, {name: '和林格尔县', code: '23'}, {name: '清水河县', code: '24'}, {name: '武川县', code: '25'}]
                    }, {
                        name: '包头市',
                        code: '02',
                        city: [{name: '东河区', code: '02'}, {name: '昆都仑区', code: '03'}, {
                            name: '青山区',
                            code: '04'
                        }, {name: '石拐区', code: '05'}, {name: '白云矿区', code: '06'}, {name: '九原区', code: '07'}, {
                            name: '土默特右旗',
                            code: '21'
                        }, {name: '固阳县', code: '22'}, {name: '达尔罕茂明安联合旗', code: '23'}]
                    }, {
                        name: '乌海市',
                        code: '03',
                        city: [{name: '海勃湾区', code: '02'}, {name: '海南区', code: '03'}, {name: '乌达区', code: '04'}]
                    }, {
                        name: '赤峰市',
                        code: '04',
                        city: [{name: '红山区', code: '02'}, {name: '元宝山区', code: '03'}, {
                            name: '松山区',
                            code: '04'
                        }, {name: '阿鲁科尔沁旗', code: '21'}, {name: '巴林左旗', code: '22'}, {
                            name: '巴林右旗',
                            code: '23'
                        }, {name: '林西县', code: '24'}, {name: '克什克腾旗', code: '25'}, {
                            name: '翁牛特旗',
                            code: '26'
                        }, {name: '喀喇沁旗', code: '28'}, {name: '宁城县', code: '29'}, {name: '敖汉旗', code: '30'}]
                    }, {
                        name: '通辽市',
                        code: '05',
                        city: [{name: '科尔沁区', code: '02'}, {name: '科尔沁左翼中旗', code: '21'}, {
                            name: '科尔沁左翼后旗',
                            code: '22'
                        }, {name: '开鲁县', code: '23'}, {name: '库伦旗', code: '24'}, {name: '奈曼旗', code: '25'}, {
                            name: '扎鲁特旗',
                            code: '26'
                        }, {name: '霍林郭勒市', code: '81'}]
                    }, {
                        name: '鄂尔多斯市',
                        code: '06',
                        city: [{name: '东胜区', code: '02'}, {name: '达拉特旗', code: '21'}, {
                            name: '准格尔旗',
                            code: '22'
                        }, {name: '鄂托克前旗', code: '23'}, {name: '鄂托克旗', code: '24'}, {name: '杭锦旗', code: '25'}, {
                            name: '乌审旗',
                            code: '26'
                        }, {name: '伊金霍洛旗', code: '27'}]
                    }, {
                        name: '呼伦贝尔市',
                        code: '07',
                        city: [{name: '海拉尔区', code: '02'}, {name: '阿荣旗', code: '21'}, {
                            name: '莫力达瓦达斡尔族自治旗',
                            code: '22'
                        }, {name: '鄂伦春自治旗', code: '23'}, {name: '鄂温克族自治旗', code: '24'}, {
                            name: '陈巴尔虎旗',
                            code: '25'
                        }, {name: '新巴尔虎左旗', code: '26'}, {name: '新巴尔虎右旗', code: '27'}, {
                            name: '满洲里市',
                            code: '81'
                        }, {name: '牙克石市', code: '82'}, {name: '扎兰屯市', code: '83'}, {
                            name: '额尔古纳市',
                            code: '84'
                        }, {name: '根河市', code: '85'}]
                    }, {
                        name: '巴彦淖尔市',
                        code: '08',
                        city: [{name: '临河区', code: '02'}, {name: '五原县', code: '21'}, {
                            name: '磴口县',
                            code: '22'
                        }, {name: '乌拉特前旗', code: '23'}, {name: '乌拉特中旗', code: '24'}, {
                            name: '乌拉特后旗',
                            code: '25'
                        }, {name: '杭锦后旗', code: '26'}]
                    }, {
                        name: '乌兰察布市',
                        code: '09',
                        city: [{name: '集宁区', code: '02'}, {name: '卓资县', code: '21'}, {
                            name: '化德县',
                            code: '22'
                        }, {name: '商都县', code: '23'}, {name: '兴和县', code: '24'}, {
                            name: '凉城县',
                            code: '25'
                        }, {name: '察哈尔右翼前旗', code: '26'}, {name: '察哈尔右翼中旗', code: '27'}, {
                            name: '察哈尔右翼后旗',
                            code: '28'
                        }, {name: '四子王旗', code: '29'}, {name: '丰镇市', code: '81'}]
                    }, {
                        name: '兴安盟',
                        code: '22',
                        city: [{name: '乌兰浩特市', code: '01'}, {name: '阿尔山市', code: '02'}, {
                            name: '科尔沁右翼前旗',
                            code: '21'
                        }, {name: '科尔沁右翼中旗', code: '22'}, {name: '扎赉特旗', code: '23'}, {name: '突泉县', code: '24'}]
                    }, {
                        name: '锡林郭勒盟',
                        code: '25',
                        city: [{name: '二连浩特市', code: '01'}, {name: '锡林浩特市', code: '02'}, {
                            name: '阿巴嘎旗',
                            code: '22'
                        }, {name: '苏尼特左旗', code: '23'}, {name: '苏尼特右旗', code: '24'}, {
                            name: '东乌珠穆沁旗',
                            code: '25'
                        }, {name: '西乌珠穆沁旗', code: '26'}, {name: '太仆寺旗', code: '27'}, {
                            name: '镶黄旗',
                            code: '28'
                        }, {name: '正镶白旗', code: '29'}, {name: '正蓝旗', code: '30'}, {name: '多伦县', code: '31'}]
                    }, {
                        name: '阿拉善盟',
                        code: '29',
                        city: [{name: '阿拉善左旗', code: '21'}, {name: '阿拉善右旗', code: '22'}, {name: '额济纳旗', code: '23'}]
                    }]
                }
            }, {
                region: {
                    name: '辽宁省',
                    code: '21',
                    state: [{
                        name: '沈阳市',
                        code: '01',
                        city: [{name: '和平区', code: '02'}, {name: '沈河区', code: '03'}, {
                            name: '大东区',
                            code: '04'
                        }, {name: '皇姑区', code: '05'}, {name: '铁西区', code: '06'}, {name: '苏家屯区', code: '11'}, {
                            name: '东陵区',
                            code: '12'
                        }, {name: '沈北新区', code: '13'}, {name: '于洪区', code: '14'}, {name: '辽中县', code: '22'}, {
                            name: '康平县',
                            code: '23'
                        }, {name: '法库县', code: '24'}, {name: '新民市', code: '81'}]
                    }, {
                        name: '大连市',
                        code: '02',
                        city: [{name: '中山区', code: '02'}, {name: '西岗区', code: '03'}, {
                            name: '沙河口区',
                            code: '04'
                        }, {name: '甘井子区', code: '11'}, {name: '旅顺口区', code: '12'}, {name: '金州区', code: '13'}, {
                            name: '长海县',
                            code: '24'
                        }, {name: '瓦房店市', code: '81'}, {name: '普兰店市', code: '82'}, {name: '庄河市', code: '83'}]
                    }, {
                        name: '鞍山市',
                        code: '03',
                        city: [{name: '铁东区', code: '02'}, {name: '铁西区', code: '03'}, {
                            name: '立山区',
                            code: '04'
                        }, {name: '千山区', code: '11'}, {name: '台安县', code: '21'}, {
                            name: '岫岩满族自治县',
                            code: '23'
                        }, {name: '海城市', code: '81'}]
                    }, {
                        name: '抚顺市',
                        code: '04',
                        city: [{name: '新抚区', code: '02'}, {name: '东洲区', code: '03'}, {
                            name: '望花区',
                            code: '04'
                        }, {name: '顺城区', code: '11'}, {name: '抚顺县', code: '21'}, {
                            name: '新宾满族自治县',
                            code: '23'
                        }, {name: '清原满族自治县', code: '23'}]
                    }, {
                        name: '本溪市',
                        code: '05',
                        city: [{name: '平山区', code: '02'}, {name: '溪湖区', code: '03'}, {
                            name: '明山区',
                            code: '04'
                        }, {name: '南芬区', code: '05'}, {name: '本溪满族自治县', code: '21'}, {name: '桓仁满族自治县', code: '22'}]
                    }, {
                        name: '丹东市',
                        code: '06',
                        city: [{name: '元宝区', code: '02'}, {name: '振兴区', code: '03'}, {
                            name: '振安区',
                            code: '04'
                        }, {name: '宽甸满族自治县', code: '24'}, {name: '东港市', code: '81'}, {name: '凤城市', code: '82'}]
                    }, {
                        name: '锦州市',
                        code: '07',
                        city: [{name: '古塔区', code: '02'}, {name: '凌河区', code: '03'}, {
                            name: '太和区',
                            code: '11'
                        }, {name: '黑山县', code: '26'}, {name: '义县', code: '27'}, {name: '凌海市', code: '81'}, {
                            name: '北镇市',
                            code: '82'
                        }]
                    }, {
                        name: '营口市',
                        code: '08',
                        city: [{name: '站前区', code: '02'}, {name: '西市区', code: '03'}, {
                            name: '鲅鱼圈区',
                            code: '04'
                        }, {name: '老边区', code: '11'}, {name: '盖州市', code: '81'}, {name: '大石桥市', code: '82'}]
                    }, {
                        name: '阜新市',
                        code: '09',
                        city: [{name: '海州区', code: '02'}, {name: '新邱区', code: '03'}, {
                            name: '太平区',
                            code: '04'
                        }, {name: '清河门区', code: '05'}, {name: '细河区', code: '11'}, {
                            name: '阜新蒙古族自治县',
                            code: '21'
                        }, {name: '彰武县', code: '22'}]
                    }, {
                        name: '辽阳市',
                        code: '10',
                        city: [{name: '白塔区', code: '02'}, {name: '文圣区', code: '03'}, {
                            name: '宏伟区',
                            code: '04'
                        }, {name: '弓长岭区', code: '05'}, {name: '太子河区', code: '11'}, {name: '辽阳县', code: '21'}, {
                            name: '灯塔市',
                            code: '81'
                        }]
                    }, {
                        name: '盘锦市',
                        code: '11',
                        city: [{name: '双台子区', code: '02'}, {name: '兴隆台区', code: '03'}, {
                            name: '大洼县',
                            code: '21'
                        }, {name: '盘山县', code: '22'}]
                    }, {
                        name: '铁岭市',
                        code: '12',
                        city: [{name: '银州区', code: '02'}, {name: '清河区', code: '04'}, {
                            name: '铁岭县',
                            code: '21'
                        }, {name: '西丰县', code: '23'}, {name: '昌图县', code: '24'}, {name: '调兵山市', code: '81'}, {
                            name: '开原市',
                            code: '82'
                        }]
                    }, {
                        name: '朝阳市',
                        code: '13',
                        city: [{name: '双塔区', code: '02'}, {name: '龙城区', code: '03'}, {
                            name: '朝阳县',
                            code: '21'
                        }, {name: '建平县', code: '22'}, {name: '喀喇沁左翼蒙古族自治县', code: '24'}, {
                            name: '北票市',
                            code: '81'
                        }, {name: '凌源市', code: '82'}]
                    }, {
                        name: '葫芦岛市',
                        code: '14',
                        city: [{name: '连山区', code: '02'}, {name: '龙港区', code: '03'}, {
                            name: '南票区',
                            code: '04'
                        }, {name: '绥中县', code: '21'}, {name: '建昌县', code: '22'}, {name: '兴城市', code: '81'}]
                    }]
                }
            }, {
                region: {
                    name: '吉林省',
                    code: '22',
                    state: [{
                        name: '长春市',
                        code: '01',
                        city: [{name: '南关区', code: '02'}, {name: '宽城区', code: '03'}, {
                            name: '朝阳区',
                            code: '04'
                        }, {name: '二道区', code: '05'}, {name: '绿园区', code: '06'}, {name: '双阳区', code: '12'}, {
                            name: '农安县',
                            code: '22'
                        }, {name: '九台市', code: '81'}, {name: '榆树市', code: '82'}, {name: '德惠市', code: '83'}]
                    }, {
                        name: '吉林市',
                        code: '02',
                        city: [{name: '昌邑区', code: '02'}, {name: '龙潭区', code: '03'}, {
                            name: '船营区',
                            code: '04'
                        }, {name: '丰满区', code: '11'}, {name: '永吉县', code: '21'}, {name: '蛟河市', code: '81'}, {
                            name: '桦甸市',
                            code: '82'
                        }, {name: '舒兰市', code: '83'}, {name: '磐石市', code: '84'}]
                    }, {
                        name: '四平市',
                        code: '03',
                        city: [{name: '铁西区', code: '02'}, {name: '铁东区', code: '03'}, {
                            name: '梨树县',
                            code: '22'
                        }, {name: '伊通满族自治县', code: '23'}, {name: '公主岭市', code: '81'}, {name: '双辽市', code: '82'}]
                    }, {
                        name: '辽源市',
                        code: '04',
                        city: [{name: '龙山区', code: '02'}, {name: '西安区', code: '03'}, {
                            name: '东丰县',
                            code: '21'
                        }, {name: '东辽县', code: '22'}]
                    }, {
                        name: '通化市',
                        code: '05',
                        city: [{name: '东昌区', code: '02'}, {name: '二道江区', code: '03'}, {
                            name: '通化县',
                            code: '21'
                        }, {name: '辉南县', code: '23'}, {name: '柳河县', code: '24'}, {name: '梅河口市', code: '81'}, {
                            name: '集安市',
                            code: '82'
                        }]
                    }, {
                        name: '白山市',
                        code: '06',
                        city: [{name: '八道江区', code: '02'}, {name: '江源区', code: '04'}, {
                            name: '抚松县',
                            code: '21'
                        }, {name: '靖宇县', code: '22'}, {name: '长白朝鲜族自治县', code: '23'}, {name: '临江市', code: '81'}]
                    }, {
                        name: '松原市',
                        code: '07',
                        city: [{name: '宁江区', code: '02'}, {name: '前郭尔罗斯蒙古族自治县', code: '21'}, {
                            name: '长岭县',
                            code: '22'
                        }, {name: '乾安县', code: '23'}, {name: '扶余县', code: '24'}]
                    }, {
                        name: '白城市',
                        code: '08',
                        city: [{name: '洮北区', code: '02'}, {name: '镇赉县', code: '21'}, {
                            name: '通榆县',
                            code: '22'
                        }, {name: '洮南市', code: '81'}, {name: '大安市', code: '82'}]
                    }, {
                        name: '延边朝鲜族自治州',
                        code: '24',
                        city: [{name: '延吉市', code: '01'}, {name: '图们市', code: '02'}, {
                            name: '敦化市',
                            code: '03'
                        }, {name: '珲春市', code: '04'}, {name: '龙井市', code: '05'}, {name: '和龙市', code: '06'}, {
                            name: '汪清县',
                            code: '24'
                        }, {name: '安图县', code: '26'}]
                    }]
                }
            }, {
                region: {
                    name: '黑龙江省',
                    code: '23',
                    state: [{
                        name: '哈尔滨市',
                        code: '01',
                        city: [{name: '道里区', code: '02'}, {name: '南岗区', code: '03'}, {
                            name: '道外区',
                            code: '04'
                        }, {name: '平房区', code: '08'}, {name: '松北区', code: '09'}, {name: '香坊区', code: '10'}, {
                            name: '呼兰区',
                            code: '11'
                        }, {name: '阿城区', code: '12'}, {name: '依兰县', code: '23'}, {name: '方正县', code: '24'}, {
                            name: '宾县',
                            code: '25'
                        }, {name: '巴彦县', code: '26'}, {name: '木兰县', code: '27'}, {name: '通河县', code: '28'}, {
                            name: '延寿县',
                            code: '29'
                        }, {name: '双城市', code: '82'}, {name: '尚志市', code: '83'}, {name: '五常市', code: '84'}]
                    }, {
                        name: '哈尔滨市',
                        code: '02',
                        city: [{name: '龙沙区', code: '02'}, {name: '建华区', code: '03'}, {
                            name: '铁锋区',
                            code: '04'
                        }, {name: '昂昂溪区', code: '05'}, {name: '富拉尔基区', code: '06'}, {
                            name: '碾子山区',
                            code: '07'
                        }, {name: '梅里斯达斡尔族区', code: '08'}, {name: '龙江县', code: '21'}, {
                            name: '依安县',
                            code: '23'
                        }, {name: '泰来县', code: '24'}, {name: '甘南县', code: '25'}, {name: '富裕县', code: '27'}, {
                            name: '克山县',
                            code: '29'
                        }, {name: '克东县', code: '30'}, {name: '拜泉县', code: '31'}, {name: '讷河市', code: '81'}]
                    }, {
                        name: '鸡西市',
                        code: '03',
                        city: [{name: '鸡冠区', code: '02'}, {name: '恒山区', code: '03'}, {
                            name: '滴道区',
                            code: '04'
                        }, {name: '梨树区', code: '05'}, {name: '城子河区', code: '06'}, {name: '麻山区', code: '07'}, {
                            name: '鸡东县',
                            code: '21'
                        }, {name: '虎林市', code: '81'}, {name: '密山市', code: '82'}]
                    }, {
                        name: '鹤岗市',
                        code: '04',
                        city: [{name: '向阳区', code: '02'}, {name: '工农区', code: '03'}, {
                            name: '南山区',
                            code: '04'
                        }, {name: '兴安区', code: '05'}, {name: '东山区', code: '06'}, {name: '兴山区', code: '07'}, {
                            name: '萝北县',
                            code: '21'
                        }, {name: '绥滨县', code: '22'}]
                    }, {
                        name: '双鸭山市',
                        code: '05',
                        city: [{name: '尖山区', code: '02'}, {name: '岭东区', code: '03'}, {
                            name: '四方台区',
                            code: '05'
                        }, {name: '宝山区', code: '06'}, {name: '集贤县', code: '21'}, {name: '友谊县', code: '22'}, {
                            name: '宝清县',
                            code: '23'
                        }, {name: '饶河县', code: '24'}]
                    }, {
                        name: '大庆市',
                        code: '06',
                        city: [{name: '萨尔图区', code: '02'}, {name: '龙凤区', code: '03'}, {
                            name: '让胡路区',
                            code: '04'
                        }, {name: '红岗区', code: '05'}, {name: '大同区', code: '06'}, {name: '肇州县', code: '21'}, {
                            name: '肇源县',
                            code: '22'
                        }, {name: '林甸县', code: '23'}, {name: '杜尔伯特蒙古族自治县', code: '24'}]
                    }, {
                        name: '伊春市',
                        code: '07',
                        city: [{name: '伊春区', code: '02'}, {name: '南岔区', code: '03'}, {
                            name: '友好区',
                            code: '04'
                        }, {name: '西林区', code: '05'}, {name: '翠峦区', code: '06'}, {name: '新青区', code: '07'}, {
                            name: '美溪区',
                            code: '08'
                        }, {name: '金山屯区', code: '09'}, {name: '五营区', code: '10'}, {name: '乌马河区', code: '11'}, {
                            name: '汤旺河区',
                            code: '12'
                        }, {name: '带岭区', code: '13'}, {name: '乌伊岭区', code: '14'}, {name: '红星区', code: '15'}, {
                            name: '上甘岭区',
                            code: '16'
                        }, {name: '嘉荫县', code: '22'}, {name: '铁力市', code: '81'}]
                    }, {
                        name: '佳木斯市',
                        code: '08',
                        city: [{name: '向阳区', code: '03'}, {name: '前进区', code: '04'}, {name: '东风区', code: '05'}, {
                            name: '郊区',
                            code: '11'
                        }, {name: '桦南县', code: '22'}, {name: '桦川县', code: '26'}, {name: '汤原县', code: '28'}, {
                            name: '抚远县',
                            code: '33'
                        }, {name: '同江市', code: '81'}, {name: '富锦市', code: '82'}]
                    }, {
                        name: '七台河市',
                        code: '09',
                        city: [{name: '新兴区', code: '02'}, {name: '桃山区', code: '03'}, {
                            name: '茄子河区',
                            code: '04'
                        }, {name: '勃利县', code: '21'}]
                    }, {
                        name: '牡丹江市',
                        code: '10',
                        city: [{name: '东安区', code: '02'}, {name: '阳明区', code: '03'}, {
                            name: '爱民区',
                            code: '04'
                        }, {name: '西安区', code: '05'}, {name: '东宁县', code: '24'}, {name: '林口县', code: '25'}, {
                            name: '绥芬河市',
                            code: '81'
                        }, {name: '海林市', code: '83'}, {name: '宁安市', code: '84'}, {name: '穆棱市', code: '85'}]
                    }, {
                        name: '黑河市',
                        code: '11',
                        city: [{name: '爱辉区', code: '02'}, {name: '嫩江县', code: '21'}, {
                            name: '逊克县',
                            code: '23'
                        }, {name: '孙吴县', code: '24'}, {name: '北安市', code: '81'}, {name: '五大连池市', code: '82'}]
                    }, {
                        name: '绥化市',
                        code: '12',
                        city: [{name: '北林区', code: '02'}, {name: '望奎县', code: '21'}, {
                            name: '兰西县',
                            code: '22'
                        }, {name: '青冈县', code: '23'}, {name: '庆安县', code: '24'}, {name: '明水县', code: '25'}, {
                            name: '绥棱县',
                            code: '26'
                        }, {name: '安达市', code: '81'}, {name: '肇东市', code: '82'}, {name: '海伦市', code: '83'}]
                    }, {
                        name: '大兴安岭地区',
                        code: '27',
                        city: [{name: '加格达奇区', code: '01'}, {name: '松岭区', code: '02'}, {
                            name: '新林区',
                            code: '03'
                        }, {name: '呼中区', code: '04'}, {name: '呼玛县', code: '21'}, {name: '塔河县', code: '22'}, {
                            name: '漠河县',
                            code: '23'
                        }]
                    }]
                }
            }, {
                region: {
                    name: '上海市',
                    code: '31',
                    state: [{
                        name: '上海',
                        code: '01',
                        city: [{name: '黄浦区', code: '01'}, {name: '卢湾区', code: '03'}, {
                            name: '徐汇区',
                            code: '04'
                        }, {name: '长宁区', code: '05'}, {name: '静安区', code: '06'}, {name: '普陀区', code: '07'}, {
                            name: '闸北区',
                            code: '08'
                        }, {name: '虹口区', code: '09'}, {name: '杨浦区', code: '10'}, {name: '闵行区', code: '12'}, {
                            name: '宝山区',
                            code: '13'
                        }, {name: '嘉定区', code: '14'}, {name: '浦东新区', code: '15'}, {name: '金山区', code: '16'}, {
                            name: '松江区',
                            code: '17'
                        }, {name: '青浦区', code: '18'}, {name: '南汇区', code: '19'}, {name: '奉贤区', code: '20'}, {
                            name: '崇明县',
                            code: '30'
                        }]
                    }]
                }
            }, {
                region: {
                    name: '江苏省',
                    code: '32',
                    state: [{
                        name: '南京市',
                        code: '01',
                        city: [{name: '玄武区', code: '02'}, {name: '白下区', code: '03'}, {
                            name: '秦淮区',
                            code: '04'
                        }, {name: '建邺区', code: '05'}, {name: '鼓楼区', code: '06'}, {name: '下关区', code: '07'}, {
                            name: '浦口区',
                            code: '11'
                        }, {name: '栖霞区', code: '13'}, {name: '雨花台区', code: '14'}, {name: '江宁区', code: '15'}, {
                            name: '六合区',
                            code: '16'
                        }, {name: '溧水县', code: '24'}, {name: '高淳县', code: '25'}]
                    }, {
                        name: '无锡市',
                        code: '02',
                        city: [{name: '崇安区', code: '02'}, {name: '南长区', code: '03'}, {
                            name: '北塘区',
                            code: '04'
                        }, {name: '锡山区', code: '05'}, {name: '惠山区', code: '06'}, {name: '滨湖区', code: '11'}, {
                            name: '江阴市',
                            code: '81'
                        }, {name: '宜兴市', code: '82'}]
                    }, {
                        name: '徐州市',
                        code: '03',
                        city: [{name: '鼓楼区', code: '02'}, {name: '云龙区', code: '03'}, {
                            name: '九里区',
                            code: '04'
                        }, {name: '贾汪区', code: '05'}, {name: '泉山区', code: '11'}, {name: '丰县', code: '21'}, {
                            name: '沛县',
                            code: '22'
                        }, {name: '铜山县', code: '23'}, {name: '睢宁县', code: '24'}, {name: '新沂市', code: '81'}, {
                            name: '邳州市',
                            code: '82'
                        }]
                    }, {
                        name: '常州市',
                        code: '04',
                        city: [{name: '天宁区', code: '02'}, {name: '钟楼区', code: '04'}, {
                            name: '戚墅堰区',
                            code: '05'
                        }, {name: '新北区', code: '11'}, {name: '武进区', code: '12'}, {name: '溧阳市', code: '81'}, {
                            name: '金坛市',
                            code: '82'
                        }]
                    }, {
                        name: '苏州市',
                        code: '05',
                        city: [{name: '沧浪区', code: '02'}, {name: '平江区', code: '03'}, {
                            name: '金阊区',
                            code: '04'
                        }, {name: '虎丘区', code: '05'}, {name: '吴中区', code: '06'}, {name: '相城区', code: '07'}, {
                            name: '常熟市',
                            code: '81'
                        }, {name: '张家港市', code: '82'}, {name: '昆山市', code: '83'}, {name: '吴江市', code: '84'}, {
                            name: '太仓市',
                            code: '85'
                        }]
                    }, {
                        name: '南通市',
                        code: '06',
                        city: [{name: '崇川区', code: '02'}, {name: '港闸区', code: '11'}, {
                            name: '海安县',
                            code: '21'
                        }, {name: '如东县', code: '23'}, {name: '启东市', code: '81'}, {name: '如皋市', code: '82'}, {
                            name: '通州市',
                            code: '83'
                        }, {name: '海门市', code: '84'}]
                    }, {
                        name: '连云港市',
                        code: '07',
                        city: [{name: '连云区', code: '03'}, {name: '新浦区', code: '05'}, {
                            name: '海州区',
                            code: '06'
                        }, {name: '赣榆县', code: '21'}, {name: '东海县', code: '22'}, {name: '灌云县', code: '23'}, {
                            name: '灌南县',
                            code: '24'
                        }]
                    }, {
                        name: '淮安市',
                        code: '08',
                        city: [{name: '清河区', code: '02'}, {name: '楚州区', code: '03'}, {
                            name: '淮阴区',
                            code: '04'
                        }, {name: '清浦区', code: '11'}, {name: '涟水县', code: '26'}, {name: '洪泽县', code: '29'}, {
                            name: '盱眙县',
                            code: '30'
                        }, {name: '金湖县', code: '31'}]
                    }, {
                        name: '盐城市',
                        code: '09',
                        city: [{name: '亭湖区', code: '02'}, {name: '盐都区', code: '03'}, {
                            name: '响水县',
                            code: '21'
                        }, {name: '滨海县', code: '22'}, {name: '阜宁县', code: '23'}, {name: '射阳县', code: '24'}, {
                            name: '建湖县',
                            code: '25'
                        }, {name: '东台市', code: '81'}, {name: '大丰市', code: '82'}]
                    }, {
                        name: '扬州市',
                        code: '10',
                        city: [{name: '广陵区', code: '02'}, {name: '邗江区', code: '03'}, {
                            name: '维扬区',
                            code: '11'
                        }, {name: '宝应县', code: '23'}, {name: '仪征市', code: '81'}, {name: '高邮市', code: '84'}, {
                            name: '江都市',
                            code: '88'
                        }]
                    }, {
                        name: '镇江市',
                        code: '11',
                        city: [{name: '京口区', code: '02'}, {name: '润州区', code: '11'}, {
                            name: '丹徒区',
                            code: '12'
                        }, {name: '丹阳市', code: '81'}, {name: '扬中市', code: '82'}, {name: '句容市', code: '83'}]
                    }, {
                        name: '泰州市',
                        code: '12',
                        city: [{name: '海陵区', code: '02'}, {name: '高港区', code: '03'}, {
                            name: '兴化市',
                            code: '81'
                        }, {name: '靖江市', code: '82'}, {name: '泰兴市', code: '83'}, {name: '姜堰市', code: '84'}]
                    }, {
                        name: '宿迁市',
                        code: '13',
                        city: [{name: '宿城区', code: '02'}, {name: '宿豫区', code: '11'}, {
                            name: '沭阳县',
                            code: '22'
                        }, {name: '泗阳县', code: '23'}, {name: '泗洪县', code: '24'}]
                    }]
                }
            }, {
                region: {
                    name: '浙江省',
                    code: '33',
                    state: [{
                        name: '杭州市',
                        code: '01',
                        city: [{name: '上城区', code: '02'}, {name: '下城区', code: '03'}, {
                            name: '江干区',
                            code: '04'
                        }, {name: '拱墅区', code: '05'}, {name: '西湖区', code: '06'}, {name: '滨江区', code: '08'}, {
                            name: '萧山区',
                            code: '09'
                        }, {name: '余杭区', code: '10'}, {name: '桐庐县', code: '22'}, {name: '淳安县', code: '27'}, {
                            name: '建德市',
                            code: '82'
                        }, {name: '富阳市', code: '83'}, {name: '临安市', code: '85'}]
                    }, {
                        name: '宁波市',
                        code: '02',
                        city: [{name: '海曙区', code: '03'}, {name: '江东区', code: '04'}, {
                            name: '江北区',
                            code: '05'
                        }, {name: '北仑区', code: '06'}, {name: '镇海区', code: '11'}, {name: '鄞州区', code: '12'}, {
                            name: '象山县',
                            code: '25'
                        }, {name: '宁海县', code: '26'}, {name: '余姚市', code: '81'}, {name: '慈溪市', code: '82'}, {
                            name: '奉化市',
                            code: '83'
                        }]
                    }, {
                        name: '温州市',
                        code: '03',
                        city: [{name: '鹿城区', code: '02'}, {name: '龙湾区', code: '03'}, {
                            name: '瓯海区',
                            code: '04'
                        }, {name: '洞头县', code: '22'}, {name: '永嘉县', code: '24'}, {name: '平阳县', code: '26'}, {
                            name: '苍南县',
                            code: '27'
                        }, {name: '文成县', code: '28'}, {name: '泰顺县', code: '29'}, {name: '瑞安市', code: '81'}, {
                            name: '乐清市',
                            code: '82'
                        }]
                    }, {
                        name: '嘉兴市',
                        code: '04',
                        city: [{name: '秀城区', code: '02'}, {name: '秀洲区', code: '11'}, {
                            name: '嘉善县',
                            code: '21'
                        }, {name: '海盐县', code: '24'}, {name: '海宁市', code: '81'}, {name: '平湖市', code: '82'}, {
                            name: '桐乡市',
                            code: '83'
                        }]
                    }, {
                        name: '湖州市',
                        code: '05',
                        city: [{name: '吴兴区', code: '02'}, {name: '南浔区', code: '03'}, {
                            name: '德清县',
                            code: '21'
                        }, {name: '长兴县', code: '22'}, {name: '安吉县', code: '23'}]
                    }, {
                        name: '绍兴市',
                        code: '06',
                        city: [{name: '越城区', code: '02'}, {name: '绍兴县', code: '21'}, {
                            name: '新昌县',
                            code: '24'
                        }, {name: '诸暨市', code: '81'}, {name: '上虞市', code: '82'}, {name: '嵊州市', code: '83'}]
                    }, {
                        name: '金华市',
                        code: '07',
                        city: [{name: '婺城区', code: '02'}, {name: '金东区', code: '03'}, {
                            name: '武义县',
                            code: '23'
                        }, {name: '浦江县', code: '26'}, {name: '磐安县', code: '27'}, {name: '兰溪市', code: '81'}, {
                            name: '义乌市',
                            code: '82'
                        }, {name: '东阳市', code: '83'}, {name: '永康市', code: '84'}]
                    }, {
                        name: '衢州市',
                        code: '08',
                        city: [{name: '柯城区', code: '02'}, {name: '衢江区', code: '03'}, {
                            name: '常山县',
                            code: '22'
                        }, {name: '开化县', code: '24'}, {name: '龙游县', code: '25'}, {name: '江山市', code: '81'}]
                    }, {
                        name: '舟山市',
                        code: '09',
                        city: [{name: '定海区', code: '02'}, {name: '普陀区', code: '03'}, {
                            name: '岱山县',
                            code: '21'
                        }, {name: '嵊泗县', code: '22'}]
                    }, {
                        name: '台州市',
                        code: '10',
                        city: [{name: '椒江区', code: '02'}, {name: '黄岩区', code: '03'}, {
                            name: '路桥区',
                            code: '04'
                        }, {name: '玉环县', code: '21'}, {name: '三门县', code: '22'}, {name: '天台县', code: '23'}, {
                            name: '仙居县',
                            code: '24'
                        }, {name: '温岭市', code: '81'}, {name: '临海市', code: '82'}]
                    }, {
                        name: '丽水市',
                        code: '11',
                        city: [{name: '莲都区', code: '02'}, {name: '青田县', code: '21'}, {
                            name: '缙云县',
                            code: '22'
                        }, {name: '遂昌县', code: '23'}, {name: '松阳县', code: '24'}, {name: '云和县', code: '25'}, {
                            name: '庆元县',
                            code: '26'
                        }, {name: '景宁畲族自治县', code: '27'}, {name: '龙泉市', code: '81'}]
                    }]
                }
            }, {
                region: {
                    name: '安徽省',
                    code: '34',
                    state: [{
                        name: '合肥市',
                        code: '01',
                        city: [{name: '瑶海区', code: '02'}, {name: '庐阳区', code: '03'}, {
                            name: '蜀山区',
                            code: '04'
                        }, {name: '包河区', code: '11'}, {name: '长丰县', code: '21'}, {name: '肥东县', code: '22'}, {
                            name: '肥西县',
                            code: '23'
                        }]
                    }, {
                        name: '芜湖市',
                        code: '02',
                        city: [{name: '镜湖区', code: '02'}, {name: '弋江区', code: '03'}, {
                            name: '鸠江区',
                            code: '07'
                        }, {name: '三山区', code: '08'}, {name: '芜湖县', code: '21'}, {name: '繁昌县', code: '22'}, {
                            name: '南陵县',
                            code: '23'
                        }]
                    }, {
                        name: '蚌埠市',
                        code: '03',
                        city: [{name: '龙子湖区', code: '02'}, {name: '蚌山区', code: '03'}, {
                            name: '禹会区',
                            code: '04'
                        }, {name: '淮上区', code: '11'}, {name: '怀远县', code: '21'}, {name: '五河县', code: '22'}, {
                            name: '固镇县',
                            code: '23'
                        }]
                    }, {
                        name: '淮南市',
                        code: '04',
                        city: [{name: '大通区', code: '02'}, {name: '田家庵区', code: '03'}, {
                            name: '谢家集区',
                            code: '04'
                        }, {name: '八公山区', code: '05'}, {name: '潘集区', code: '06'}, {name: '凤台县', code: '21'}]
                    }, {
                        name: '马鞍山市',
                        code: '05',
                        city: [{name: '金家庄区', code: '02'}, {name: '花山区', code: '03'}, {
                            name: '雨山区',
                            code: '04'
                        }, {name: '当涂县', code: '21'}]
                    }, {
                        name: '淮北市',
                        code: '06',
                        city: [{name: '杜集区', code: '02'}, {name: '相山区', code: '03'}, {
                            name: '烈山区',
                            code: '04'
                        }, {name: '濉溪县', code: '21'}]
                    }, {
                        name: '铜陵市',
                        code: '07',
                        city: [{name: '铜官山区', code: '02'}, {name: '狮子山区', code: '03'}, {
                            name: '郊区',
                            code: '11'
                        }, {name: '铜陵县', code: '21'}]
                    }, {
                        name: '安庆市',
                        code: '08',
                        city: [{name: '迎江区', code: '02'}, {name: '大观区', code: '03'}, {
                            name: '宜秀区',
                            code: '11'
                        }, {name: '怀宁县', code: '22'}, {name: '枞阳县', code: '23'}, {name: '潜山县', code: '24'}, {
                            name: '太湖县',
                            code: '25'
                        }, {name: '宿松县', code: '26'}, {name: '望江县', code: '27'}, {name: '岳西县', code: '28'}, {
                            name: '桐城市',
                            code: '81'
                        }]
                    }, {
                        name: '黄山市',
                        code: '10',
                        city: [{name: '屯溪区', code: '02'}, {name: '黄山区', code: '03'}, {name: '徽州区', code: '04'}, {
                            name: '歙县',
                            code: '21'
                        }, {name: '休宁县', code: '22'}, {name: '黟县', code: '23'}, {name: '祁门县', code: '24'}]
                    }, {
                        name: '滁州市',
                        code: '11',
                        city: [{name: '琅琊区', code: '02'}, {name: '南谯区', code: '03'}, {
                            name: '来安县',
                            code: '22'
                        }, {name: '全椒县', code: '24'}, {name: '定远县', code: '25'}, {name: '凤阳县', code: '26'}, {
                            name: '天长市',
                            code: '81'
                        }, {name: '明光市', code: '82'}]
                    }, {
                        name: '阜阳市',
                        code: '12',
                        city: [{name: '颍州区', code: '02'}, {name: '颍东区', code: '03'}, {
                            name: '颍泉区',
                            code: '04'
                        }, {name: '临泉县', code: '21'}, {name: '太和县', code: '22'}, {name: '阜南县', code: '25'}, {
                            name: '颍上县',
                            code: '26'
                        }, {name: '颍上县', code: '82'}]
                    }, {
                        name: '宿州市',
                        code: '13',
                        city: [{name: '埇桥区', code: '02'}, {name: '砀山县', code: '21'}, {name: '萧县', code: '22'}, {
                            name: '灵璧县',
                            code: '23'
                        }, {name: '泗县', code: '24'}]
                    }, {
                        name: '巢湖市',
                        code: '14',
                        city: [{name: '居巢区', code: '02'}, {name: '庐江县', code: '21'}, {
                            name: '无为县',
                            code: '22'
                        }, {name: '含山县', code: '23'}, {name: '和县', code: '24'}]
                    }, {
                        name: '六安市',
                        code: '15',
                        city: [{name: '金安区', code: '02'}, {name: '裕安区', code: '03'}, {name: '寿县', code: '21'}, {
                            name: '霍邱县',
                            code: '22'
                        }, {name: '舒城县', code: '23'}, {name: '金寨县', code: '24'}, {name: '霍山县', code: '25'}]
                    }, {
                        name: '亳州市',
                        code: '16',
                        city: [{name: '谯城区', code: '02'}, {name: '涡阳县', code: '21'}, {
                            name: '蒙城县',
                            code: '22'
                        }, {name: '利辛县', code: '23'}]
                    }, {
                        name: '池州市',
                        code: '17',
                        city: [{name: '贵池区', code: '02'}, {name: '东至县', code: '21'}, {
                            name: '石台县',
                            code: '22'
                        }, {name: '青阳县', code: '23'}]
                    }, {
                        name: '宣城市',
                        code: '18',
                        city: [{name: '宣州区', code: '02'}, {name: '郎溪县', code: '21'}, {name: '广德县', code: '22'}, {
                            name: '泾县',
                            code: '23'
                        }, {name: '绩溪县', code: '24'}, {name: '旌德县', code: '25'}, {name: '宁国市', code: '81'}]
                    }]
                }
            }, {
                region: {
                    name: '福建省',
                    code: '35',
                    state: [{
                        name: '福州市',
                        code: '01',
                        city: [{name: '鼓楼区', code: '02'}, {name: '台江区', code: '03'}, {
                            name: '仓山区',
                            code: '04'
                        }, {name: '马尾区', code: '05'}, {name: '晋安区', code: '11'}, {name: '闽侯县', code: '21'}, {
                            name: '连江县',
                            code: '22'
                        }, {name: '罗源县', code: '23'}, {name: '闽清县', code: '24'}, {name: '永泰县', code: '25'}, {
                            name: '平潭县',
                            code: '28'
                        }, {name: '福清市', code: '81'}, {name: '长乐市', code: '82'}]
                    }, {
                        name: '厦门市',
                        code: '02',
                        city: [{name: '思明区', code: '03'}, {name: '海沧区', code: '05'}, {
                            name: '湖里区',
                            code: '06'
                        }, {name: '集美区', code: '11'}, {name: '同安区', code: '12'}, {name: '翔安区', code: '13'}]
                    }, {
                        name: '莆田市',
                        code: '03',
                        city: [{name: '城厢区', code: '02'}, {name: '涵江区', code: '03'}, {
                            name: '荔城区',
                            code: '04'
                        }, {name: '秀屿区', code: '05'}, {name: '仙游县', code: '22'}]
                    }, {
                        name: '三明市',
                        code: '04',
                        city: [{name: '梅列区', code: '02'}, {name: '三元区', code: '03'}, {
                            name: '明溪县',
                            code: '21'
                        }, {name: '清流县', code: '23'}, {name: '宁化县', code: '24'}, {name: '大田县', code: '25'}, {
                            name: '尤溪县',
                            code: '26'
                        }, {name: '沙县', code: '27'}, {name: '将乐县', code: '28'}, {name: '泰宁县', code: '29'}, {
                            name: '建宁县',
                            code: '30'
                        }, {name: '永安市', code: '81'}]
                    }, {
                        name: '泉州市',
                        code: '05',
                        city: [{name: '鲤城区', code: '02'}, {name: '丰泽区', code: '03'}, {
                            name: '洛江区',
                            code: '04'
                        }, {name: '泉港区', code: '05'}, {name: '惠安县', code: '21'}, {name: '安溪县', code: '24'}, {
                            name: '永春县',
                            code: '25'
                        }, {name: '德化县', code: '26'}, {name: '金门县', code: '27'}, {name: '石狮市', code: '81'}, {
                            name: '晋江市',
                            code: '82'
                        }, {name: '南安市', code: '83'}]
                    }, {
                        name: '漳州市',
                        code: '06',
                        city: [{name: '芗城区', code: '02'}, {name: '龙文区', code: '03'}, {
                            name: '云霄县',
                            code: '22'
                        }, {name: '漳浦县', code: '23'}, {name: '诏安县', code: '24'}, {name: '长泰县', code: '25'}, {
                            name: '东山县',
                            code: '26'
                        }, {name: '南靖县', code: '27'}, {name: '平和县', code: '28'}, {name: '华安县', code: '29'}, {
                            name: '龙海市',
                            code: '81'
                        }]
                    }, {
                        name: '南平市',
                        code: '07',
                        city: [{name: '延平区', code: '02'}, {name: '顺昌县', code: '21'}, {
                            name: '浦城县',
                            code: '22'
                        }, {name: '光泽县', code: '23'}, {name: '松溪县', code: '24'}, {name: '政和县', code: '25'}, {
                            name: '邵武市',
                            code: '81'
                        }, {name: '武夷山市', code: '82'}, {name: '建瓯市', code: '83'}, {name: '建阳市', code: '84'}]
                    }, {
                        name: '龙岩市',
                        code: '08',
                        city: [{name: '新罗区', code: '02'}, {name: '长汀县', code: '21'}, {
                            name: '永定县',
                            code: '22'
                        }, {name: '上杭县', code: '23'}, {name: '武平县', code: '24'}, {name: '连城县', code: '25'}, {
                            name: '漳平市',
                            code: '81'
                        }]
                    }, {
                        name: '宁德市',
                        code: '09',
                        city: [{name: '蕉城区', code: '02'}, {name: '霞浦县', code: '21'}, {
                            name: '古田县',
                            code: '22'
                        }, {name: '屏南县', code: '23'}, {name: '寿宁县', code: '24'}, {name: '周宁县', code: '25'}, {
                            name: '柘荣县',
                            code: '26'
                        }, {name: '福安市', code: '81'}, {name: '福鼎市', code: '82'}]
                    }]
                }
            }, {
                region: {
                    name: '江西省',
                    code: '36',
                    state: [{
                        name: '南昌市',
                        code: '01',
                        city: [{name: '东湖区', code: '02'}, {name: '西湖区', code: '03'}, {
                            name: '青云谱区',
                            code: '04'
                        }, {name: '湾里区', code: '05'}, {name: '青山湖区', code: '11'}, {name: '南昌县', code: '21'}, {
                            name: '新建县',
                            code: '22'
                        }, {name: '安义县', code: '23'}, {name: '进贤县', code: '24'}]
                    }, {
                        name: '景德镇市',
                        code: '02',
                        city: [{name: '昌江区', code: '02'}, {name: '珠山区', code: '03'}, {
                            name: '浮梁县',
                            code: '22'
                        }, {name: '乐平市', code: '81'}]
                    }, {
                        name: '萍乡市',
                        code: '03',
                        city: [{name: '安源区', code: '02'}, {name: '湘东区', code: '13'}, {
                            name: '莲花县',
                            code: '21'
                        }, {name: '上栗县', code: '22'}, {name: '芦溪县', code: '23'}]
                    }, {
                        name: '九江市',
                        code: '04',
                        city: [{name: '庐山区', code: '02'}, {name: '浔阳区', code: '03'}, {
                            name: '九江县',
                            code: '21'
                        }, {name: '武宁县', code: '23'}, {name: '修水县', code: '24'}, {name: '永修县', code: '25'}, {
                            name: '德安县',
                            code: '26'
                        }, {name: '星子县', code: '27'}, {name: '都昌县', code: '28'}, {name: '湖口县', code: '29'}, {
                            name: '彭泽县',
                            code: '30'
                        }, {name: '瑞昌市', code: '81'}]
                    }, {
                        name: '新余市',
                        code: '05',
                        city: [{name: '渝水区', code: '02'}, {name: '分宜县', code: '21'}]
                    }, {
                        name: '鹰潭市',
                        code: '06',
                        city: [{name: '月湖区', code: '02'}, {name: '余江县', code: '22'}, {name: '贵溪市', code: '81'}]
                    }, {
                        name: '赣州市',
                        code: '07',
                        city: [{name: '章贡区', code: '02'}, {name: '赣县', code: '21'}, {name: '信丰县', code: '22'}, {
                            name: '大余县',
                            code: '23'
                        }, {name: '上犹县', code: '24'}, {name: '崇义县', code: '25'}, {name: '安远县', code: '26'}, {
                            name: '龙南县',
                            code: '27'
                        }, {name: '定南县', code: '28'}, {name: '全南县', code: '29'}, {name: '宁都县', code: '30'}, {
                            name: '于都县',
                            code: '31'
                        }, {name: '兴国县', code: '32'}, {name: '会昌县', code: '33'}, {name: '寻乌县', code: '34'}, {
                            name: '石城县',
                            code: '35'
                        }, {name: '瑞金市', code: '81'}, {name: '南康市', code: '82'}]
                    }, {
                        name: '吉安市',
                        code: '08',
                        city: [{name: '吉州区', code: '02'}, {name: '青原区', code: '03'}, {
                            name: '吉安县',
                            code: '21'
                        }, {name: '吉水县', code: '22'}, {name: '峡江县', code: '23'}, {name: '新干县', code: '24'}, {
                            name: '永丰县',
                            code: '25'
                        }, {name: '泰和县', code: '26'}, {name: '遂川县', code: '27'}, {name: '万安县', code: '28'}, {
                            name: '安福县',
                            code: '29'
                        }, {name: '永新县', code: '30'}, {name: '井冈山市', code: '81'}]
                    }, {
                        name: '宜春市',
                        code: '09',
                        city: [{name: '袁州区', code: '02'}, {name: '奉新县', code: '21'}, {
                            name: '万载县',
                            code: '22'
                        }, {name: '上高县', code: '23'}, {name: '宜丰县', code: '24'}, {name: '靖安县', code: '25'}, {
                            name: '铜鼓县',
                            code: '26'
                        }, {name: '丰城市', code: '81'}, {name: '樟树市', code: '82'}, {name: '高安市', code: '83'}]
                    }, {
                        name: '抚州市',
                        code: '10',
                        city: [{name: '临川区', code: '02'}, {name: '南城县', code: '21'}, {
                            name: '黎川县',
                            code: '22'
                        }, {name: '南丰县', code: '23'}, {name: '崇仁县', code: '24'}, {name: '乐安县', code: '25'}, {
                            name: '宜黄县',
                            code: '26'
                        }, {name: '金溪县', code: '27'}, {name: '资溪县', code: '28'}, {name: '东乡县', code: '29'}, {
                            name: '广昌县',
                            code: '30'
                        }]
                    }, {
                        name: '上饶市',
                        code: '11',
                        city: [{name: '信州区', code: '02'}, {name: '上饶县', code: '21'}, {
                            name: '广丰县',
                            code: '22'
                        }, {name: '玉山县', code: '23'}, {name: '铅山县', code: '24'}, {name: '横峰县', code: '25'}, {
                            name: '弋阳县',
                            code: '26'
                        }, {name: '余干县', code: '27'}, {name: '鄱阳县', code: '28'}, {name: '万年县', code: '29'}, {
                            name: '婺源县',
                            code: '30'
                        }, {name: '德兴市', code: '31'}]
                    }]
                }
            }, {
                region: {
                    name: '山东省',
                    code: '37',
                    state: [{
                        name: '济南市',
                        code: '01',
                        city: [{name: '历下区', code: '02'}, {name: '市中区', code: '03'}, {
                            name: '槐荫区',
                            code: '04'
                        }, {name: '天桥区', code: '05'}, {name: '历城区', code: '12'}, {name: '长清区', code: '13'}, {
                            name: '平阴县',
                            code: '24'
                        }, {name: '济阳县', code: '25'}, {name: '商河县', code: '26'}, {name: '章丘市', code: '81'}]
                    }, {
                        name: '青岛市',
                        code: '02',
                        city: [{name: '市南区', code: '02'}, {name: '市北区', code: '03'}, {
                            name: '四方区',
                            code: '05'
                        }, {name: '黄岛区', code: '11'}, {name: '崂山区', code: '12'}, {name: '李沧区', code: '13'}, {
                            name: '城阳区',
                            code: '14'
                        }, {name: '胶州市', code: '81'}, {name: '即墨市', code: '82'}, {name: '平度市', code: '83'}, {
                            name: '胶南市',
                            code: '84'
                        }, {name: '莱西市', code: '85'}]
                    }, {
                        name: '淄博市',
                        code: '03',
                        city: [{name: '淄川区', code: '02'}, {name: '张店区', code: '03'}, {
                            name: '博山区',
                            code: '04'
                        }, {name: '临淄区', code: '05'}, {name: '周村区', code: '06'}, {name: '桓台县', code: '21'}, {
                            name: '高青县',
                            code: '22'
                        }, {name: '沂源县', code: '23'}]
                    }, {
                        name: '枣庄市',
                        code: '04',
                        city: [{name: '市中区', code: '02'}, {name: '薛城区', code: '03'}, {
                            name: '峄城区',
                            code: '04'
                        }, {name: '台儿庄区', code: '05'}, {name: '山亭区', code: '06'}, {name: '滕州市', code: '81'}]
                    }, {
                        name: '东营市',
                        code: '05',
                        city: [{name: '东营区', code: '02'}, {name: '河口区', code: '03'}, {
                            name: '垦利县',
                            code: '21'
                        }, {name: '利津县', code: '22'}, {name: '广饶县', code: '23'}]
                    }, {
                        name: '烟台市',
                        code: '06',
                        city: [{name: '芝罘区', code: '02'}, {name: '福山区', code: '11'}, {
                            name: '牟平区',
                            code: '12'
                        }, {name: '莱山区', code: '13'}, {name: '长岛县', code: '34'}, {name: '龙口市', code: '81'}, {
                            name: '莱阳市',
                            code: '82'
                        }, {name: '莱州市', code: '83'}, {name: '蓬莱市', code: '84'}, {name: '招远市', code: '85'}, {
                            name: '栖霞市',
                            code: '86'
                        }, {name: '海阳市', code: '87'}]
                    }, {
                        name: '潍坊市',
                        code: '07',
                        city: [{name: '潍城区', code: '02'}, {name: '寒亭区', code: '03'}, {
                            name: '坊子区',
                            code: '04'
                        }, {name: '奎文区', code: '05'}, {name: '临朐县', code: '24'}, {name: '昌乐县', code: '25'}, {
                            name: '青州市',
                            code: '81'
                        }, {name: '诸城市', code: '82'}, {name: '寿光市', code: '83'}, {name: '安丘市', code: '84'}, {
                            name: '高密市',
                            code: '85'
                        }, {name: '昌邑市', code: '86'}]
                    }, {
                        name: '济宁市',
                        code: '08',
                        city: [{name: '市中区', code: '02'}, {name: '任城区', code: '11'}, {
                            name: '微山县',
                            code: '26'
                        }, {name: '鱼台县', code: '27'}, {name: '金乡县', code: '28'}, {name: '嘉祥县', code: '29'}, {
                            name: '汶上县',
                            code: '30'
                        }, {name: '泗水县', code: '31'}, {name: '梁山县', code: '32'}, {name: '曲阜市', code: '81'}, {
                            name: '兖州市',
                            code: '82'
                        }, {name: '邹城市', code: '83'}]
                    }, {
                        name: '泰安市',
                        code: '09',
                        city: [{name: '泰山区', code: '02'}, {name: '岱岳区', code: '03'}, {
                            name: '宁阳县',
                            code: '21'
                        }, {name: '东平县', code: '23'}, {name: '新泰市', code: '82'}, {name: '肥城市', code: '83'}]
                    }, {
                        name: '威海市',
                        code: '10',
                        city: [{name: '环翠区', code: '02'}, {name: '文登市', code: '81'}, {
                            name: '荣成市',
                            code: '82'
                        }, {name: '乳山市', code: '83'}]
                    }, {
                        name: '日照市',
                        code: '11',
                        city: [{name: '东港区', code: '02'}, {name: '岚山区', code: '03'}, {name: '五莲县', code: '21'}, {
                            name: '莒县',
                            code: '22'
                        }]
                    }, {
                        name: '莱芜市',
                        code: '12',
                        city: [{name: '莱城区', code: '02'}, {name: '钢城区', code: '03'}]
                    }, {
                        name: '临沂市',
                        code: '13',
                        city: [{name: '兰山区', code: '02'}, {name: '罗庄区', code: '11'}, {
                            name: '河东区',
                            code: '12'
                        }, {name: '沂南县', code: '21'}, {name: '郯城县', code: '22'}, {name: '沂水县', code: '23'}, {
                            name: '苍山县',
                            code: '24'
                        }, {name: '费县', code: '25'}, {name: '平邑县', code: '26'}, {name: '莒南县', code: '27'}, {
                            name: '蒙阴县',
                            code: '28'
                        }, {name: '临沭县', code: '29'}]
                    }, {
                        name: '德州市',
                        code: '14',
                        city: [{name: '德城区', code: '02'}, {name: '陵县', code: '21'}, {name: '宁津县', code: '22'}, {
                            name: '庆云县',
                            code: '23'
                        }, {name: '临邑县', code: '24'}, {name: '齐河县', code: '25'}, {name: '平原县', code: '26'}, {
                            name: '夏津县',
                            code: '27'
                        }, {name: '武城县', code: '28'}, {name: '乐陵市', code: '81'}, {name: '禹城市', code: '82'}]
                    }, {
                        name: '聊城市',
                        code: '15',
                        city: [{name: '东昌府区', code: '02'}, {name: '阳谷县', code: '21'}, {
                            name: '莘县',
                            code: '22'
                        }, {name: '茌平县', code: '23'}, {name: '东阿县', code: '24'}, {name: '冠县', code: '25'}, {
                            name: '高唐县',
                            code: '26'
                        }, {name: '临清市', code: '27'}]
                    }, {
                        name: '滨州市',
                        code: '16',
                        city: [{name: '滨城区', code: '02'}, {name: '惠民县', code: '21'}, {
                            name: '阳信县',
                            code: '22'
                        }, {name: '无棣县', code: '23'}, {name: '沾化县', code: '24'}, {name: '博兴县', code: '25'}, {
                            name: '邹平县',
                            code: '26'
                        }]
                    }, {
                        name: '菏泽市',
                        code: '17',
                        city: [{name: '牡丹区', code: '02'}, {name: '曹县', code: '21'}, {name: '单县', code: '22'}, {
                            name: '成武县',
                            code: '23'
                        }, {name: '巨野县', code: '24'}, {name: '郓城县', code: '25'}, {name: '鄄城县', code: '26'}, {
                            name: '定陶县',
                            code: '27'
                        }, {name: '东明县', code: '28'}]
                    }]
                }
            }, {
                region: {
                    name: '河南省',
                    code: '41',
                    state: [{
                        name: '郑州市',
                        code: '01',
                        city: [{name: '中原区', code: '02'}, {name: '二七区', code: '03'}, {
                            name: '管城回族区',
                            code: '04'
                        }, {name: '金水区', code: '05'}, {name: '上街区', code: '06'}, {name: '惠济区', code: '08'}, {
                            name: '中牟县',
                            code: '22'
                        }, {name: '巩义市', code: '81'}, {name: '荥阳市', code: '82'}, {name: '新密市', code: '83'}, {
                            name: '新郑市',
                            code: '84'
                        }, {name: '登封市', code: '85'}]
                    }, {
                        name: '开封市',
                        code: '02',
                        city: [{name: '龙亭区', code: '02'}, {name: '顺河回族区', code: '03'}, {
                            name: '鼓楼区',
                            code: '04'
                        }, {name: '禹王台区', code: '05'}, {name: '金明区', code: '11'}, {name: '杞县', code: '21'}, {
                            name: '通许县',
                            code: '22'
                        }, {name: '尉氏县', code: '23'}, {name: '开封县', code: '24'}, {name: '兰考县', code: '25'}]
                    }, {
                        name: '洛阳市',
                        code: '03',
                        city: [{name: '老城区', code: '02'}, {name: '西工区', code: '03'}, {
                            name: '廛河回族区',
                            code: '04'
                        }, {name: '涧西区', code: '05'}, {name: '吉利区', code: '06'}, {name: '洛龙区', code: '07'}, {
                            name: '孟津县',
                            code: '22'
                        }, {name: '新安县', code: '23'}, {name: '栾川县', code: '24'}, {name: '嵩县', code: '25'}, {
                            name: '汝阳县',
                            code: '26'
                        }, {name: '宜阳县', code: '27'}, {name: '洛宁县', code: '28'}, {name: '伊川县', code: '29'}, {
                            name: '偃师市',
                            code: '81'
                        }]
                    }, {
                        name: '平顶山市',
                        code: '04',
                        city: [{name: '新华区', code: '02'}, {name: '卫东区', code: '03'}, {
                            name: '石龙区',
                            code: '04'
                        }, {name: '湛河区', code: '11'}, {name: '宝丰县', code: '21'}, {name: '叶县', code: '22'}, {
                            name: '鲁山县',
                            code: '23'
                        }, {name: '郏县', code: '25'}, {name: '舞钢市', code: '81'}, {name: '汝州市', code: '82'}]
                    }, {
                        name: '安阳市',
                        code: '05',
                        city: [{name: '文峰区', code: '02'}, {name: '北关区', code: '03'}, {
                            name: '殷都区',
                            code: '05'
                        }, {name: '龙安区', code: '06'}, {name: '安阳县', code: '22'}, {name: '汤阴县', code: '23'}, {
                            name: '滑县',
                            code: '26'
                        }, {name: '内黄县', code: '27'}, {name: '林州市', code: '81'}]
                    }, {
                        name: '鹤壁市',
                        code: '06',
                        city: [{name: '鹤山区', code: '02'}, {name: '山城区', code: '03'}, {name: '淇滨区', code: '11'}, {
                            name: '浚县',
                            code: '21'
                        }, {name: '淇县', code: '22'}]
                    }, {
                        name: '新乡市',
                        code: '07',
                        city: [{name: '红旗区', code: '02'}, {name: '卫滨区', code: '03'}, {
                            name: '凤泉区',
                            code: '04'
                        }, {name: '牧野区', code: '11'}, {name: '新乡县', code: '21'}, {name: '获嘉县', code: '24'}, {
                            name: '原阳县',
                            code: '25'
                        }, {name: '延津县', code: '26'}, {name: '封丘县', code: '27'}, {name: '长垣县', code: '28'}, {
                            name: '卫辉市',
                            code: '81'
                        }, {name: '辉县市', code: '82'}]
                    }, {
                        name: '焦作市',
                        code: '08',
                        city: [{name: '解放区', code: '02'}, {name: '中站区', code: '03'}, {
                            name: '马村区',
                            code: '04'
                        }, {name: '山阳区', code: '11'}, {name: '修武县', code: '21'}, {name: '博爱县', code: '22'}, {
                            name: '武陟县',
                            code: '23'
                        }, {name: '温县', code: '25'}, {name: '济源市', code: '81'}, {name: '沁阳市', code: '82'}, {
                            name: '孟州市',
                            code: '83'
                        }]
                    }, {
                        name: '濮阳市',
                        code: '09',
                        city: [{name: '华龙区', code: '02'}, {name: '清丰县', code: '22'}, {name: '南乐县', code: '23'}, {
                            name: '范县',
                            code: '26'
                        }, {name: '台前县', code: '27'}, {name: '濮阳县', code: '28'}]
                    }, {
                        name: '许昌市',
                        code: '10',
                        city: [{name: '魏都区', code: '02'}, {name: '许昌县', code: '23'}, {
                            name: '鄢陵县',
                            code: '24'
                        }, {name: '襄城县', code: '25'}, {name: '禹州市', code: '81'}, {name: '长葛市', code: '82'}]
                    }, {
                        name: '漯河市',
                        code: '11',
                        city: [{name: '源汇区', code: '02'}, {name: '郾城区', code: '03'}, {
                            name: '召陵区',
                            code: '04'
                        }, {name: '舞阳县', code: '21'}, {name: '临颍县', code: '22'}]
                    }, {
                        name: '三门峡市',
                        code: '12',
                        city: [{name: '湖滨区', code: '02'}, {name: '渑池县', code: '21'}, {name: '陕县', code: '22'}, {
                            name: '卢氏县',
                            code: '24'
                        }, {name: '义马市', code: '81'}, {name: '灵宝市', code: '82'}]
                    }, {
                        name: '南阳市',
                        code: '13',
                        city: [{name: '宛城区', code: '02'}, {name: '卧龙区', code: '03'}, {
                            name: '南召县',
                            code: '21'
                        }, {name: '方城县', code: '22'}, {name: '西峡县', code: '23'}, {name: '镇平县', code: '24'}, {
                            name: '内乡县',
                            code: '25'
                        }, {name: '淅川县', code: '26'}, {name: '社旗县', code: '27'}, {name: '唐河县', code: '28'}, {
                            name: '新野县',
                            code: '29'
                        }, {name: '桐柏县', code: '30'}, {name: '邓州市', code: '81'}]
                    }, {
                        name: '商丘市',
                        code: '14',
                        city: [{name: '梁园区', code: '02'}, {name: '睢阳区', code: '03'}, {name: '民权县', code: '21'}, {
                            name: '睢县',
                            code: '22'
                        }, {name: '宁陵县', code: '23'}, {name: '柘城县', code: '24'}, {name: '虞城县', code: '25'}, {
                            name: '夏邑县',
                            code: '26'
                        }, {name: '永城市', code: '81'}]
                    }, {
                        name: '信阳市',
                        code: '15',
                        city: [{name: '浉河区', code: '02'}, {name: '平桥区', code: '03'}, {
                            name: '罗山县',
                            code: '21'
                        }, {name: '光山县', code: '22'}, {name: '新县', code: '23'}, {name: '商城县', code: '24'}, {
                            name: '固始县',
                            code: '25'
                        }, {name: '潢川县', code: '26'}, {name: '淮滨县', code: '27'}, {name: '息县', code: '28'}]
                    }, {
                        name: '周口市',
                        code: '16',
                        city: [{name: '川汇区', code: '02'}, {name: '扶沟县', code: '21'}, {
                            name: '西华县',
                            code: '22'
                        }, {name: '商水县', code: '23'}, {name: '沈丘县', code: '24'}, {name: '郸城县', code: '25'}, {
                            name: '淮阳县',
                            code: '26'
                        }, {name: '太康县', code: '27'}, {name: '鹿邑县', code: '28'}, {name: '项城市', code: '81'}]
                    }, {
                        name: '驻马店市',
                        code: '17',
                        city: [{name: '驿城区', code: '02'}, {name: '西平县', code: '21'}, {
                            name: '上蔡县',
                            code: '22'
                        }, {name: '平舆县', code: '23'}, {name: '正阳县', code: '24'}, {name: '确山县', code: '25'}, {
                            name: '泌阳县',
                            code: '26'
                        }, {name: '汝南县', code: '27'}, {name: '遂平县', code: '28'}, {name: '新蔡县', code: '29'}]
                    }]
                }
            }, {
                region: {
                    name: '湖北省',
                    code: '42',
                    state: [{
                        name: '武汉市',
                        code: '01',
                        city: [{name: '江岸区', code: '02'}, {name: '江汉区', code: '03'}, {
                            name: '硚口区',
                            code: '04'
                        }, {name: '汉阳区', code: '05'}, {name: '武昌区', code: '06'}, {name: '青山区', code: '07'}, {
                            name: '洪山区',
                            code: '11'
                        }, {name: '东西湖区', code: '12'}, {name: '汉南区', code: '13'}, {name: '蔡甸区', code: '14'}, {
                            name: '江夏区',
                            code: '15'
                        }, {name: '黄陂区', code: '16'}, {name: '新洲区', code: '17'}]
                    }, {
                        name: '黄石市',
                        code: '02',
                        city: [{name: '黄石港区', code: '02'}, {name: '西塞山区', code: '03'}, {
                            name: '下陆区',
                            code: '04'
                        }, {name: '铁山区', code: '05'}, {name: '阳新县', code: '22'}, {name: '大冶市', code: '81'}]
                    }, {
                        name: '十堰市',
                        code: '03',
                        city: [{name: '茅箭区', code: '02'}, {name: '张湾区', code: '03'}, {name: '郧县', code: '21'}, {
                            name: '郧西县',
                            code: '22'
                        }, {name: '竹山县', code: '23'}, {name: '竹溪县', code: '24'}, {name: '房县', code: '25'}, {
                            name: '丹江口市',
                            code: '81'
                        }]
                    }, {
                        name: '宜昌市',
                        code: '05',
                        city: [{name: '西陵区', code: '02'}, {name: '伍家岗区', code: '03'}, {
                            name: '点军区',
                            code: '04'
                        }, {name: '猇亭区', code: '05'}, {name: '夷陵区', code: '06'}, {name: '远安县', code: '25'}, {
                            name: '兴山县',
                            code: '26'
                        }, {name: '秭归县', code: '27'}, {name: '长阳土家族自治县', code: '28'}, {
                            name: '五峰土家族自治县',
                            code: '29'
                        }, {name: '宜都市', code: '81'}, {name: '当阳市', code: '82'}, {name: '枝江市', code: '83'}]
                    }, {
                        name: '襄樊市',
                        code: '06',
                        city: [{name: '襄城区', code: '02'}, {name: '樊城区', code: '06'}, {
                            name: '襄阳区',
                            code: '07'
                        }, {name: '南漳县', code: '24'}, {name: '谷城县', code: '25'}, {name: '保康县', code: '26'}, {
                            name: '老河口市',
                            code: '82'
                        }, {name: '枣阳市', code: '83'}, {name: '宜城市', code: '84'}]
                    }, {
                        name: '鄂州市',
                        code: '07',
                        city: [{name: '梁子湖区', code: '02'}, {name: '华容区', code: '03'}, {name: '鄂城区', code: '04'}]
                    }, {
                        name: '荆门市',
                        code: '08',
                        city: [{name: '东宝区', code: '02'}, {name: '掇刀区', code: '04'}, {
                            name: '京山县',
                            code: '21'
                        }, {name: '沙洋县', code: '22'}, {name: '钟祥市', code: '81'}]
                    }, {
                        name: '孝感市',
                        code: '09',
                        city: [{name: '孝南区', code: '02'}, {name: '孝昌县', code: '21'}, {
                            name: '大悟县',
                            code: '22'
                        }, {name: '云梦县', code: '23'}, {name: '应城市', code: '81'}, {name: '安陆市', code: '82'}, {
                            name: '汉川市',
                            code: '84'
                        }]
                    }, {
                        name: '荆州市',
                        code: '10',
                        city: [{name: '沙市区', code: '02'}, {name: '荆州区', code: '03'}, {
                            name: '公安县',
                            code: '22'
                        }, {name: '监利县', code: '23'}, {name: '江陵县', code: '24'}, {name: '石首市', code: '25'}, {
                            name: '洪湖市',
                            code: '83'
                        }, {name: '松滋市', code: '87'}]
                    }, {
                        name: '黄冈市',
                        code: '11',
                        city: [{name: '黄州区', code: '02'}, {name: '团风县', code: '21'}, {
                            name: '红安县',
                            code: '22'
                        }, {name: '罗田县', code: '23'}, {name: '英山县', code: '24'}, {name: '浠水县', code: '25'}, {
                            name: '蕲春县',
                            code: '26'
                        }, {name: '黄梅县', code: '27'}, {name: '麻城市', code: '81'}, {name: '武穴市', code: '82'}]
                    }, {
                        name: '咸宁市',
                        code: '12',
                        city: [{name: '咸安区', code: '02'}, {name: '嘉鱼县', code: '21'}, {
                            name: '通城县',
                            code: '22'
                        }, {name: '崇阳县', code: '23'}, {name: '通山县', code: '24'}, {name: '赤壁市', code: '81'}]
                    }, {
                        name: '随州市',
                        code: '13',
                        city: [{name: '曾都区', code: '02'}, {name: '广水市', code: '81'}]
                    }, {
                        name: '恩施土家族苗族自治州',
                        code: '28',
                        city: [{name: '恩施市', code: '01'}, {name: '利川市', code: '02'}, {
                            name: '建始县',
                            code: '22'
                        }, {name: '巴东县', code: '23'}, {name: '宣恩县', code: '25'}, {name: '咸丰县', code: '26'}, {
                            name: '来凤县',
                            code: '27'
                        }, {name: '鹤峰县', code: '28'}]
                    }, {
                        name: '直辖行政单位',
                        code: '90',
                        city: [{name: '仙桃市', code: '04'}, {name: '潜江市', code: '05'}, {
                            name: '天门市',
                            code: '06'
                        }, {name: '神农架林区', code: '21'}]
                    }]
                }
            }, {
                region: {
                    name: '湖南省',
                    code: '43',
                    state: [{
                        name: '长沙市',
                        code: '01',
                        city: [{name: '芙蓉区', code: '02'}, {name: '天心区', code: '03'}, {
                            name: '岳麓区',
                            code: '04'
                        }, {name: '开福区', code: '05'}, {name: '雨花区', code: '11'}, {name: '长沙县', code: '21'}, {
                            name: '望城县',
                            code: '22'
                        }, {name: '宁乡县', code: '24'}, {name: '浏阳市', code: '81'}]
                    }, {
                        name: '株洲市',
                        code: '02',
                        city: [{name: '荷塘区', code: '02'}, {name: '芦淞区', code: '03'}, {
                            name: '石峰区',
                            code: '04'
                        }, {name: '天元区', code: '11'}, {name: '株洲县', code: '21'}, {name: '攸县', code: '23'}, {
                            name: '茶陵县',
                            code: '24'
                        }, {name: '炎陵县', code: '25'}, {name: '醴陵市', code: '81'}]
                    }, {
                        name: '湘潭市',
                        code: '03',
                        city: [{name: '雨湖区', code: '02'}, {name: '岳塘区', code: '04'}, {
                            name: '湘潭县',
                            code: '21'
                        }, {name: '湘乡市', code: '81'}, {name: '韶山市', code: '82'}]
                    }, {
                        name: '衡阳市',
                        code: '04',
                        city: [{name: '珠晖区', code: '05'}, {name: '雁峰区', code: '06'}, {
                            name: '石鼓区',
                            code: '07'
                        }, {name: '蒸湘区', code: '08'}, {name: '南岳区', code: '12'}, {name: '衡阳县', code: '21'}, {
                            name: '衡南县',
                            code: '22'
                        }, {name: '衡山县', code: '23'}, {name: '衡东县', code: '24'}, {name: '祁东县', code: '26'}, {
                            name: '耒阳市',
                            code: '81'
                        }, {name: '常宁市', code: '82'}]
                    }, {
                        name: '邵阳市',
                        code: '05',
                        city: [{name: '双清区', code: '02'}, {name: '大祥区', code: '03'}, {
                            name: '北塔区',
                            code: '11'
                        }, {name: '邵东县', code: '21'}, {name: '新邵县', code: '22'}, {name: '邵阳县', code: '23'}, {
                            name: '隆回县',
                            code: '24'
                        }, {name: '洞口县', code: '25'}, {name: '绥宁县', code: '27'}, {
                            name: '新宁县',
                            code: '28'
                        }, {name: '城步苗族自治县', code: '29'}, {name: '武冈市', code: '81'}]
                    }, {
                        name: '岳阳市',
                        code: '06',
                        city: [{name: '岳阳楼区', code: '02'}, {name: '云溪区', code: '03'}, {
                            name: '君山区',
                            code: '11'
                        }, {name: '岳阳县', code: '21'}, {name: '华容县', code: '23'}, {name: '湘阴县', code: '24'}, {
                            name: '平江县',
                            code: '26'
                        }, {name: '汨罗市', code: '81'}, {name: '临湘市', code: '82'}]
                    }, {
                        name: '常德市',
                        code: '07',
                        city: [{name: '武陵区', code: '02'}, {name: '鼎城区', code: '03'}, {
                            name: '安乡县',
                            code: '21'
                        }, {name: '汉寿县', code: '22'}, {name: '澧县', code: '23'}, {name: '临澧县', code: '24'}, {
                            name: '桃源县',
                            code: '25'
                        }, {name: '石门县', code: '26'}, {name: '津市市', code: '81'}]
                    }, {
                        name: '张家界市',
                        code: '08',
                        city: [{name: '永定区', code: '02'}, {name: '武陵源区', code: '11'}, {
                            name: '慈利县',
                            code: '21'
                        }, {name: '桑植县', code: '22'}]
                    }, {
                        name: '益阳市',
                        code: '09',
                        city: [{name: '资阳区', code: '02'}, {name: '赫山区', code: '03'}, {name: '南县', code: '21'}, {
                            name: '桃江县',
                            code: '22'
                        }, {name: '安化县', code: '23'}, {name: '沅江市', code: '81'}]
                    }, {
                        name: '郴州市',
                        code: '10',
                        city: [{name: '北湖区', code: '02'}, {name: '苏仙区', code: '03'}, {
                            name: '桂阳县',
                            code: '21'
                        }, {name: '宜章县', code: '22'}, {name: '永兴县', code: '23'}, {name: '嘉禾县', code: '24'}, {
                            name: '临武县',
                            code: '25'
                        }, {name: '汝城县', code: '26'}, {name: '桂东县', code: '27'}, {name: '安仁县', code: '28'}, {
                            name: '资兴市',
                            code: '81'
                        }]
                    }, {
                        name: '永州市',
                        code: '11',
                        city: [{name: '零陵区', code: '02'}, {name: '冷水滩区', code: '01'}, {
                            name: '祁阳县',
                            code: '21'
                        }, {name: '东安县', code: '22'}, {name: '双牌县', code: '23'}, {name: '道县', code: '24'}, {
                            name: '江永县',
                            code: '25'
                        }, {name: '宁远县', code: '26'}, {name: '蓝山县', code: '27'}, {
                            name: '新田县',
                            code: '28'
                        }, {name: '江华瑶族自治县', code: '29'}]
                    }, {
                        name: '怀化市',
                        code: '12',
                        city: [{name: '鹤城区', code: '02'}, {name: '中方县', code: '21'}, {
                            name: '沅陵县',
                            code: '22'
                        }, {name: '辰溪县', code: '23'}, {name: '溆浦县', code: '24'}, {
                            name: '会同县',
                            code: '25'
                        }, {name: '麻阳苗族自治县', code: '26'}, {name: '新晃侗族自治县', code: '27'}, {
                            name: '芷江侗族自治县',
                            code: '28'
                        }, {name: '靖州苗族侗族自治县', code: '29'}, {name: '通道侗族自治县', code: '30'}, {name: '洪江市', code: '81'}]
                    }, {
                        name: '娄底市',
                        code: '13',
                        city: [{name: '娄星区', code: '02'}, {name: '双峰县', code: '21'}, {
                            name: '新化县',
                            code: '22'
                        }, {name: '冷水江市', code: '81'}, {name: '涟源市', code: '82'}]
                    }, {
                        name: '湘西土家族苗族自治州',
                        code: '31',
                        city: [{name: '吉首市', code: '01'}, {name: '泸溪县', code: '22'}, {
                            name: '凤凰县',
                            code: '23'
                        }, {name: '花垣县', code: '24'}, {name: '保靖县', code: '25'}, {name: '古丈县', code: '26'}, {
                            name: '永顺县',
                            code: '27'
                        }, {name: '龙山县', code: '30'}]
                    }]
                }
            }, {
                region: {
                    name: '广东省',
                    code: '44',
                    state: [{
                        name: '广州市',
                        code: '01',
                        city: [{name: '荔湾区', code: '03'}, {name: '越秀区', code: '04'}, {
                            name: '海珠区',
                            code: '05'
                        }, {name: '天河区', code: '06'}, {name: '白云区', code: '11'}, {name: '黄埔区', code: '12'}, {
                            name: '番禺区',
                            code: '13'
                        }, {name: '花都区', code: '14'}, {name: '南沙区', code: '15'}, {name: '萝岗区', code: '16'}, {
                            name: '增城市',
                            code: '83'
                        }, {name: '从化市', code: '84'}]
                    }, {
                        name: '韶关市',
                        code: '02',
                        city: [{name: '武江区', code: '03'}, {name: '浈江区', code: '04'}, {
                            name: '曲江区',
                            code: '05'
                        }, {name: '始兴县', code: '22'}, {name: '仁化县', code: '24'}, {
                            name: '翁源县',
                            code: '29'
                        }, {name: '乳源瑶族自治县', code: '32'}, {name: '新丰县', code: '33'}, {
                            name: '乐昌市',
                            code: '81'
                        }, {name: '南雄市', code: '82'}]
                    }, {
                        name: '深圳市',
                        code: '03',
                        city: [{name: '罗湖区', code: '03'}, {name: '福田区', code: '04'}, {
                            name: '南山区',
                            code: '05'
                        }, {name: '宝安区', code: '06'}, {name: '龙岗区', code: '07'}, {name: '盐田区', code: '08'}]
                    }, {
                        name: '珠海市',
                        code: '04',
                        city: [{name: '香洲区', code: '02'}, {name: '斗门区', code: '03'}, {name: '金湾区', code: '04'}]
                    }, {
                        name: '汕头市',
                        code: '05',
                        city: [{name: '龙湖区', code: '07'}, {name: '金平区', code: '11'}, {
                            name: '濠江区',
                            code: '12'
                        }, {name: '潮阳区', code: '13'}, {name: '潮南区', code: '14'}, {name: '澄海区', code: '15'}, {
                            name: '南澳县',
                            code: '23'
                        }]
                    }, {
                        name: '佛山市',
                        code: '06',
                        city: [{name: '禅城区', code: '04'}, {name: '南海区', code: '05'}, {
                            name: '顺德区',
                            code: '06'
                        }, {name: '三水区', code: '07'}, {name: '高明区', code: '08'}]
                    }, {
                        name: '江门市',
                        code: '07',
                        city: [{name: '蓬江区', code: '03'}, {name: '江海区', code: '04'}, {
                            name: '新会区',
                            code: '05'
                        }, {name: '台山市', code: '81'}, {name: '开平市', code: '83'}, {name: '鹤山市', code: '84'}, {
                            name: '恩平市',
                            code: '85'
                        }]
                    }, {
                        name: '湛江市',
                        code: '08',
                        city: [{name: '赤坎区', code: '02'}, {name: '霞山区', code: '03'}, {
                            name: '坡头区',
                            code: '04'
                        }, {name: '麻章区', code: '11'}, {name: '遂溪县', code: '23'}, {name: '徐闻县', code: '25'}, {
                            name: '廉江市',
                            code: '81'
                        }, {name: '雷州市', code: '82'}, {name: '吴川市', code: '83'}]
                    }, {
                        name: '茂名市',
                        code: '09',
                        city: [{name: '茂南区', code: '02'}, {name: '茂港区', code: '03'}, {
                            name: '电白县',
                            code: '23'
                        }, {name: '高州市', code: '81'}, {name: '化州市', code: '82'}, {name: '信宜市', code: '83'}]
                    }, {
                        name: '肇庆市',
                        code: '12',
                        city: [{name: '端州区', code: '02'}, {name: '鼎湖区', code: '03'}, {
                            name: '广宁县',
                            code: '23'
                        }, {name: '怀集县', code: '24'}, {name: '封开县', code: '25'}, {name: '德庆县', code: '26'}, {
                            name: '高要市',
                            code: '83'
                        }, {name: '四会市', code: '84'}]
                    }, {
                        name: '惠州市',
                        code: '13',
                        city: [{name: '惠城区', code: '02'}, {name: '惠阳区', code: '03'}, {
                            name: '博罗县',
                            code: '22'
                        }, {name: '惠东县', code: '23'}, {name: '龙门县', code: '24'}]
                    }, {
                        name: '梅州市',
                        code: '14',
                        city: [{name: '梅江区', code: '02'}, {name: '梅县', code: '21'}, {name: '大埔县', code: '22'}, {
                            name: '丰顺县',
                            code: '23'
                        }, {name: '五华县', code: '24'}, {name: '平远县', code: '26'}, {name: '蕉岭县', code: '27'}, {
                            name: '兴宁市',
                            code: '81'
                        }]
                    }, {
                        name: '汕尾市',
                        code: '15',
                        city: [{name: '城区', code: '02'}, {name: '海丰县', code: '21'}, {name: '陆河县', code: '22'}, {
                            name: '陆丰市',
                            code: '81'
                        }]
                    }, {
                        name: '河源市',
                        code: '16',
                        city: [{name: '源城区', code: '02'}, {name: '紫金县', code: '21'}, {
                            name: '龙川县',
                            code: '22'
                        }, {name: '连平县', code: '23'}, {name: '和平县', code: '24'}, {name: '东源县', code: '25'}]
                    }, {
                        name: '阳江市',
                        code: '17',
                        city: [{name: '江城区', code: '02'}, {name: '阳西县', code: '21'}, {
                            name: '阳东县',
                            code: '23'
                        }, {name: '阳春市', code: '81'}]
                    }, {
                        name: '清远市',
                        code: '18',
                        city: [{name: '清城区', code: '02'}, {name: '佛冈县', code: '21'}, {
                            name: '阳山县',
                            code: '23'
                        }, {name: '连山壮族瑶族自治县', code: '25'}, {name: '连南瑶族自治县', code: '26'}, {
                            name: '清新县',
                            code: '27'
                        }, {name: '英德市', code: '81'}, {name: '连州市', code: '82'}]
                    }, {name: '东莞市', code: '19', city: [{name: '市辖区', code: '01'}]}, {
                        name: '中山市',
                        code: '20',
                        city: [{name: '市辖区', code: '01'}]
                    }, {
                        name: '潮州市',
                        code: '51',
                        city: [{name: '湘桥区', code: '02'}, {name: '潮安县', code: '21'}, {name: '饶平县', code: '22'}]
                    }, {
                        name: '揭阳市',
                        code: '52',
                        city: [{name: '榕城区', code: '02'}, {name: '揭东县', code: '21'}, {
                            name: '揭西县',
                            code: '22'
                        }, {name: '惠来县', code: '24'}, {name: '普宁市', code: '81'}]
                    }, {
                        name: '云浮市',
                        code: '53',
                        city: [{name: '云城区', code: '02'}, {name: '新兴县', code: '21'}, {
                            name: '郁南县',
                            code: '22'
                        }, {name: '云安县', code: '23'}, {name: '罗定市', code: '81'}]
                    }]
                }
            }, {
                region: {
                    name: '广西',
                    code: '45',
                    state: [{
                        name: '南宁市',
                        code: '01',
                        city: [{name: '兴宁区', code: '02'}, {name: '青秀区', code: '03'}, {
                            name: '江南区',
                            code: '05'
                        }, {name: '西乡塘区', code: '07'}, {name: '良庆区', code: '08'}, {name: '邕宁区', code: '09'}, {
                            name: '武鸣县',
                            code: '22'
                        }, {name: '隆安县', code: '23'}, {name: '马山县', code: '24'}, {name: '上林县', code: '25'}, {
                            name: '宾阳县',
                            code: '26'
                        }, {name: '横县', code: '27'}]
                    }, {
                        name: '柳州市',
                        code: '02',
                        city: [{name: '城中区', code: '02'}, {name: '鱼峰区', code: '03'}, {
                            name: '柳南区',
                            code: '04'
                        }, {name: '柳北区', code: '05'}, {name: '柳江县', code: '21'}, {name: '柳城县', code: '22'}, {
                            name: '鹿寨县',
                            code: '23'
                        }, {name: '融安县', code: '24'}, {name: '融水苗族自治县', code: '25'}, {name: '三江侗族自治县', code: '26'}]
                    }, {
                        name: '桂林市',
                        code: '03',
                        city: [{name: '秀峰区', code: '02'}, {name: '叠彩区', code: '03'}, {
                            name: '象山区',
                            code: '04'
                        }, {name: '七星区', code: '05'}, {name: '雁山区', code: '11'}, {name: '阳朔县', code: '21'}, {
                            name: '临桂县',
                            code: '22'
                        }, {name: '灵川县', code: '23'}, {name: '全州县', code: '24'}, {name: '兴安县', code: '25'}, {
                            name: '永福县',
                            code: '26'
                        }, {name: '灌阳县', code: '27'}, {name: '龙胜各族自治县', code: '28'}, {
                            name: '资源县',
                            code: '29'
                        }, {name: '平乐县', code: '30'}, {name: '荔蒲县', code: '31'}, {name: '恭城瑶族自治县', code: '32'}]
                    }, {
                        name: '梧州市',
                        code: '04',
                        city: [{name: '万秀区', code: '03'}, {name: '蝶山区', code: '04'}, {
                            name: '长洲区',
                            code: '05'
                        }, {name: '苍梧县', code: '21'}, {name: '藤县', code: '22'}, {name: '蒙山县', code: '23'}, {
                            name: '岑溪市',
                            code: '81'
                        }]
                    }, {
                        name: '北海市',
                        code: '05',
                        city: [{name: '海城区', code: '02'}, {name: '银海区', code: '03'}, {
                            name: '铁山港区',
                            code: '12'
                        }, {name: '合浦县', code: '21'}]
                    }, {
                        name: '防城港市',
                        code: '06',
                        city: [{name: '港口区', code: '02'}, {name: '防城区', code: '03'}, {
                            name: '上思县',
                            code: '21'
                        }, {name: '东兴市', code: '81'}]
                    }, {
                        name: '钦州市',
                        code: '07',
                        city: [{name: '钦南区', code: '02'}, {name: '钦北区', code: '03'}, {
                            name: '灵山县',
                            code: '21'
                        }, {name: '浦北县', code: '22'}]
                    }, {
                        name: '贵港市',
                        code: '08',
                        city: [{name: '港北区', code: '02'}, {name: '港南区', code: '03'}, {
                            name: '覃塘区',
                            code: '04'
                        }, {name: '平南县', code: '21'}, {name: '桂平市', code: '81'}]
                    }, {
                        name: '玉林市',
                        code: '09',
                        city: [{name: '玉州区', code: '02'}, {name: '容县', code: '21'}, {name: '陆川县', code: '22'}, {
                            name: '博白县',
                            code: '23'
                        }, {name: '兴业县', code: '24'}, {name: '北流市', code: '81'}]
                    }, {
                        name: '百色市',
                        code: '10',
                        city: [{name: '右江区', code: '02'}, {name: '田阳县', code: '21'}, {
                            name: '田东县',
                            code: '22'
                        }, {name: '平果县', code: '23'}, {name: '德保县', code: '24'}, {name: '靖西县', code: '25'}, {
                            name: '那坡县',
                            code: '26'
                        }, {name: '凌云县', code: '27'}, {name: '乐业县', code: '28'}, {name: '田林县', code: '29'}, {
                            name: '西林县',
                            code: '30'
                        }, {name: '隆林各族自治县', code: '31'}]
                    }, {
                        name: '贺州市',
                        code: '11',
                        city: [{name: '八步区', code: '02'}, {name: '昭平县', code: '21'}, {
                            name: '钟山县',
                            code: '22'
                        }, {name: '富川瑶族自治县', code: '23'}]
                    }, {
                        name: '河池市',
                        code: '12',
                        city: [{name: '金城江区', code: '02'}, {name: '南丹县', code: '21'}, {
                            name: '天峨县',
                            code: '22'
                        }, {name: '凤山县', code: '23'}, {name: '东兰县', code: '24'}, {
                            name: '罗城仫佬族自治县',
                            code: '25'
                        }, {name: '环江毛南族自治县', code: '26'}, {name: '巴马瑶族自治县', code: '27'}, {
                            name: '都安瑶族自治县',
                            code: '28'
                        }, {name: '大化瑶族自治县', code: '29'}, {name: '宜州市', code: '81'}]
                    }, {
                        name: '来宾市',
                        code: '13',
                        city: [{name: '兴宾区', code: '02'}, {name: '忻城县', code: '21'}, {
                            name: '象州县',
                            code: '22'
                        }, {name: '武宣县', code: '23'}, {name: '金秀瑶族自治县', code: '24'}, {name: '合山市', code: '81'}]
                    }, {
                        name: '崇左市',
                        code: '14',
                        city: [{name: '江洲区', code: '02'}, {name: '扶绥县', code: '21'}, {
                            name: '宁明县',
                            code: '22'
                        }, {name: '龙州县', code: '23'}, {name: '大新县', code: '24'}, {name: '天等县', code: '25'}, {
                            name: '凭祥市',
                            code: '81'
                        }]
                    }]
                }
            }, {
                region: {
                    name: '海南省',
                    code: '46',
                    state: [{
                        name: '海口市',
                        code: '01',
                        city: [{name: '秀英区', code: '05'}, {name: '龙华区', code: '06'}, {
                            name: '琼山区',
                            code: '07'
                        }, {name: '美兰区', code: '08'}]
                    }, {
                        name: '三亚市',
                        code: '02',
                        city: [{name: '直辖县级行政单位', code: '90'}, {name: '五指山市', code: '01'}, {
                            name: '琼海市',
                            code: '02'
                        }, {name: '儋州市', code: '03'}, {name: '文昌市', code: '05'}, {name: '万宁市', code: '06'}, {
                            name: '东方市',
                            code: '07'
                        }, {name: '定安县', code: '25'}, {name: '屯昌县', code: '26'}, {name: '澄迈县', code: '27'}, {
                            name: '临高县',
                            code: '28'
                        }, {name: '白沙黎族自治县', code: '30'}, {name: '昌江黎族自治县', code: '31'}, {
                            name: '乐东黎族自治县',
                            code: '33'
                        }, {name: '陵水黎族自治县', code: '34'}, {name: '保亭黎族苗族自治县', code: '35'}, {
                            name: '琼中黎族苗族自治县',
                            code: '36'
                        }, {name: '西沙群岛', code: '37'}, {name: '南沙群岛', code: '38'}, {name: '中沙群岛的岛礁及其海域', code: '39'}]
                    }]
                }
            }, {
                region: {
                    name: '重庆市',
                    code: '50',
                    state: [{
                        name: '市辖区',
                        code: '01',
                        city: [{name: '万州区', code: '01'}, {name: '涪陵区', code: '02'}, {
                            name: '渝中区',
                            code: '03'
                        }, {name: '大渡口区', code: '04'}, {name: '江北区', code: '05'}, {name: '沙坪坝区', code: '06'}, {
                            name: '九龙坡区',
                            code: '07'
                        }, {name: '南岸区', code: '08'}, {name: '北碚区', code: '09'}, {name: '万盛区', code: '10'}, {
                            name: '双桥区',
                            code: '11'
                        }, {name: '渝北区', code: '12'}, {name: '巴南区', code: '13'}, {name: '黔江区', code: '14'}, {
                            name: '长寿区',
                            code: '15'
                        }, {name: '江津区', code: '16'}, {name: '合川区', code: '17'}, {name: '永川区', code: '18'}, {
                            name: '永川区',
                            code: '19'
                        }]
                    }, {
                        name: '县',
                        code: '02',
                        city: [{name: '綦江县', code: '22'}, {name: '潼南县', code: '23'}, {
                            name: '铜梁县',
                            code: '24'
                        }, {name: '大足县', code: '25'}, {name: '荣昌县', code: '26'}, {name: '璧山县', code: '27'}, {
                            name: '梁平县',
                            code: '28'
                        }, {name: '城口县', code: '29'}, {name: '丰都县', code: '30'}, {name: '垫江县', code: '31'}, {
                            name: '武隆县',
                            code: '32'
                        }, {name: '忠县', code: '33'}, {name: '开县', code: '34'}, {name: '云阳县', code: '35'}, {
                            name: '奉节县',
                            code: '36'
                        }, {name: '巫山县', code: '37'}, {name: '巫溪县', code: '38'}, {
                            name: '石柱土家族自治县',
                            code: '40'
                        }, {name: '秀山土家族苗族自治县', code: '41'}, {name: '酉阳土家族苗族自治县', code: '42'}, {
                            name: '彭水苗族土家族自治县',
                            code: '43'
                        }]
                    }]
                }
            }, {
                region: {
                    name: '四川省',
                    code: '51',
                    state: [{
                        name: '成都市',
                        code: '01',
                        city: [{name: '锦江区', code: '04'}, {name: '青羊区', code: '05'}, {
                            name: '金牛区',
                            code: '06'
                        }, {name: '武侯区', code: '07'}, {name: '成华区', code: '08'}, {name: '龙泉驿区', code: '12'}, {
                            name: '青白江区',
                            code: '13'
                        }, {name: '新都区', code: '14'}, {name: '温江区', code: '15'}, {name: '金堂县', code: '21'}, {
                            name: '双流县',
                            code: '22'
                        }, {name: '郫县', code: '24'}, {name: '大邑县', code: '29'}, {name: '蒲江县', code: '31'}, {
                            name: '新津县',
                            code: '32'
                        }, {name: '都江堰市', code: '81'}, {name: '彭州市', code: '82'}, {name: '邛崃市', code: '83'}, {
                            name: '崇州市',
                            code: '84'
                        }]
                    }, {
                        name: '自贡市',
                        code: '03',
                        city: [{name: '自流井区', code: '02'}, {name: '贡井区', code: '03'}, {
                            name: '大安区',
                            code: '04'
                        }, {name: '沿滩区', code: '11'}, {name: '荣县', code: '21'}, {name: '富顺县', code: '22'}]
                    }, {
                        name: '攀枝花市',
                        code: '04',
                        city: [{name: '东区', code: '02'}, {name: '西区', code: '03'}, {name: '仁和区', code: '11'}, {
                            name: '米易县',
                            code: '21'
                        }, {name: '盐边县', code: '22'}]
                    }, {
                        name: '泸州市',
                        code: '05',
                        city: [{name: '江阳区', code: '02'}, {name: '纳溪区', code: '03'}, {
                            name: '龙马潭区',
                            code: '04'
                        }, {name: '泸县', code: '21'}, {name: '合江县', code: '22'}, {name: '叙永县', code: '24'}, {
                            name: '古蔺县',
                            code: '25'
                        }]
                    }, {
                        name: '德阳市',
                        code: '06',
                        city: [{name: '旌阳区', code: '02'}, {name: '中江县', code: '23'}, {
                            name: '罗江县',
                            code: '26'
                        }, {name: '广汉市', code: '81'}, {name: '什邡市', code: '82'}, {name: '绵竹市', code: '83'}]
                    }, {
                        name: '绵阳市',
                        code: '07',
                        city: [{name: '涪城区', code: '03'}, {name: '游仙区', code: '04'}, {
                            name: '三台县',
                            code: '22'
                        }, {name: '盐亭县', code: '23'}, {name: '安县', code: '24'}, {name: '梓潼县', code: '25'}, {
                            name: '北川羌族自治县',
                            code: '26'
                        }, {name: '平武县', code: '27'}, {name: '江油市', code: '28'}]
                    }, {
                        name: '广元市',
                        code: '08',
                        city: [{name: '市中区', code: '02'}, {name: '元坝区', code: '11'}, {
                            name: '朝天区',
                            code: '12'
                        }, {name: '旺苍县', code: '21'}, {name: '青川县', code: '22'}, {name: '剑阁县', code: '23'}, {
                            name: '苍溪县',
                            code: '24'
                        }]
                    }, {
                        name: '遂宁市',
                        code: '09',
                        city: [{name: '船山区', code: '03'}, {name: '安居区', code: '04'}, {
                            name: '蓬溪县',
                            code: '21'
                        }, {name: '射洪县', code: '22'}, {name: '大英县', code: '23'}]
                    }, {
                        name: '内江市',
                        code: '10',
                        city: [{name: '市中区', code: '02'}, {name: '东兴区', code: '11'}, {
                            name: '威远县',
                            code: '24'
                        }, {name: '资中县', code: '25'}, {name: '隆昌县', code: '28'}]
                    }, {
                        name: '乐山市',
                        code: '11',
                        city: [{name: '市中区', code: '02'}, {name: '沙湾区', code: '11'}, {
                            name: '五通桥区',
                            code: '12'
                        }, {name: '金口河区', code: '13'}, {name: '犍为县', code: '23'}, {name: '井研县', code: '24'}, {
                            name: '夹江县',
                            code: '26'
                        }, {name: '沐川县', code: '29'}, {name: '峨边彝族自治县', code: '32'}, {
                            name: '马边彝族自治县',
                            code: '33'
                        }, {name: '峨眉山市', code: '81'}]
                    }, {
                        name: '南充市',
                        code: '13',
                        city: [{name: '顺庆区', code: '02'}, {name: '高坪区', code: '03'}, {
                            name: '嘉陵区',
                            code: '04'
                        }, {name: '南部县', code: '21'}, {name: '营山县', code: '22'}, {name: '蓬安县', code: '23'}, {
                            name: '仪陇县',
                            code: '24'
                        }, {name: '西充县', code: '25'}, {name: '阆中市', code: '81'}]
                    }, {
                        name: '眉山市',
                        code: '14',
                        city: [{name: '东坡区', code: '02'}, {name: '仁寿县', code: '21'}, {
                            name: '彭山县',
                            code: '22'
                        }, {name: '洪雅县', code: '23'}, {name: '丹棱县', code: '24'}, {name: '青神县', code: '25'}]
                    }, {
                        name: '宜宾市',
                        code: '15',
                        city: [{name: '翠屏区', code: '02'}, {name: '宜宾县', code: '21'}, {
                            name: '南溪县',
                            code: '22'
                        }, {name: '江安县', code: '23'}, {name: '长宁县', code: '24'}, {name: '高县', code: '25'}, {
                            name: '珙县',
                            code: '26'
                        }, {name: '筠连县', code: '27'}, {name: '兴文县', code: '28'}, {name: '屏山县', code: '29'}]
                    }, {
                        name: '广安市',
                        code: '16',
                        city: [{name: '广安区', code: '02'}, {name: '岳池县', code: '21'}, {
                            name: '武胜县',
                            code: '22'
                        }, {name: '邻水县', code: '23'}, {name: '华蓥市', code: '81'}]
                    }, {
                        name: '达州市',
                        code: '17',
                        city: [{name: '通川区', code: '02'}, {name: '达县', code: '21'}, {name: '宣汉县', code: '22'}, {
                            name: '开江县',
                            code: '23'
                        }, {name: '大竹县', code: '24'}, {name: '渠县', code: '25'}, {name: '万源市', code: '81'}]
                    }, {
                        name: '雅安市',
                        code: '18',
                        city: [{name: '雨城区', code: '02'}, {name: '名山县', code: '21'}, {
                            name: '荥经县',
                            code: '22'
                        }, {name: '汉源县', code: '23'}, {name: '石棉县', code: '24'}, {name: '天全县', code: '25'}, {
                            name: '芦山县',
                            code: '26'
                        }, {name: '宝兴县', code: '27'}]
                    }, {
                        name: '巴中市',
                        code: '19',
                        city: [{name: '巴州区', code: '02'}, {name: '通江县', code: '21'}, {
                            name: '南江县',
                            code: '22'
                        }, {name: '平昌县', code: '23'}]
                    }, {
                        name: '资阳市',
                        code: '20',
                        city: [{name: '雁江区', code: '02'}, {name: '安岳县', code: '21'}, {
                            name: '乐至县',
                            code: '22'
                        }, {name: '简阳市', code: '23'}]
                    }, {
                        name: '阿坝藏族羌族自治州',
                        code: '32',
                        city: [{name: '汶川县', code: '21'}, {name: '理县', code: '22'}, {name: '茂县', code: '23'}, {
                            name: '松潘县',
                            code: '24'
                        }, {name: '九寨沟县', code: '25'}, {name: '金川县', code: '26'}, {name: '小金县', code: '27'}, {
                            name: '黑水县',
                            code: '28'
                        }, {name: '马尔康县', code: '29'}, {name: '壤塘县', code: '30'}, {name: '阿坝县', code: '31'}, {
                            name: '若尔盖县',
                            code: '32'
                        }, {name: '红原县', code: '33'}]
                    }, {
                        name: '甘孜藏族自治州',
                        code: '33',
                        city: [{name: '康定县', code: '21'}, {name: '泸定县', code: '22'}, {
                            name: '丹巴县',
                            code: '23'
                        }, {name: '九龙县', code: '24'}, {name: '雅江县', code: '25'}, {name: '道孚县', code: '26'}, {
                            name: '炉霍县',
                            code: '27'
                        }, {name: '甘孜县', code: '28'}, {name: '新龙县', code: '29'}, {name: '德格县', code: '30'}, {
                            name: '白玉县',
                            code: '31'
                        }, {name: '石渠县', code: '32'}, {name: '色达县', code: '33'}, {name: '理塘县', code: '34'}, {
                            name: '巴塘县',
                            code: '35'
                        }, {name: '乡城县', code: '36'}, {name: '稻城县', code: '37'}, {name: '得荣县', code: '38'}]
                    }, {
                        name: '凉山彝族自治州',
                        code: '34',
                        city: [{name: '西昌市', code: '01'}, {name: '木里藏族自治县', code: '22'}, {
                            name: '盐源县',
                            code: '23'
                        }, {name: '德昌县', code: '24'}, {name: '会理县', code: '25'}, {name: '会东县', code: '26'}, {
                            name: '宁南县',
                            code: '27'
                        }, {name: '普格县', code: '28'}, {name: '布拖县', code: '29'}, {name: '金阳县', code: '30'}, {
                            name: '昭觉县',
                            code: '31'
                        }, {name: '喜德县', code: '32'}, {name: '冕宁县', code: '33'}, {name: '越西县', code: '34'}, {
                            name: '甘洛县',
                            code: '35'
                        }, {name: '美姑县', code: '36'}, {name: '雷波县', code: '37'}]
                    }]
                }
            }, {
                region: {
                    name: '贵州省',
                    code: '52',
                    state: [{
                        name: '贵阳市',
                        code: '01',
                        city: [{name: '南明区', code: '02'}, {name: '云岩区', code: '03'}, {
                            name: '花溪区',
                            code: '11'
                        }, {name: '乌当区', code: '12'}, {name: '白云区', code: '13'}, {name: '小河区', code: '14'}, {
                            name: '开阳县',
                            code: '21'
                        }, {name: '息烽县', code: '22'}, {name: '修文县', code: '23'}, {name: '清镇市', code: '81'}]
                    }, {
                        name: '六盘水市',
                        code: '02',
                        city: [{name: '钟山区', code: '01'}, {name: '六枝特区', code: '03'}, {
                            name: '水城县',
                            code: '21'
                        }, {name: '盘县', code: '22'}]
                    }, {
                        name: '遵义市',
                        code: '03',
                        city: [{name: '红花岗区', code: '02'}, {name: '汇川区', code: '03'}, {
                            name: '遵义县',
                            code: '21'
                        }, {name: '桐梓县', code: '22'}, {name: '绥阳县', code: '23'}, {
                            name: '正安县',
                            code: '24'
                        }, {name: '道真仡佬族苗族自治县', code: '25'}, {name: '务川仡佬族苗族自治县', code: '26'}, {
                            name: '凤冈县',
                            code: '27'
                        }, {name: '湄潭县', code: '28'}, {name: '余庆县', code: '29'}, {name: '习水县', code: '30'}, {
                            name: '赤水市',
                            code: '81'
                        }, {name: '仁怀市', code: '82'}]
                    }, {
                        name: '安顺市',
                        code: '04',
                        city: [{name: '西秀区', code: '02'}, {name: '平坝县', code: '21'}, {
                            name: '普定县',
                            code: '22'
                        }, {name: '镇宁布依族苗族自治县', code: '23'}, {name: '关岭布依族苗族自治县', code: '24'}, {
                            name: '紫云苗族布依族自治县',
                            code: '25'
                        }]
                    }, {
                        name: '铜仁地区',
                        code: '22',
                        city: [{name: '铜仁市', code: '01'}, {name: '江口县', code: '22'}, {
                            name: '玉屏侗族自治县',
                            code: '23'
                        }, {name: '石阡县', code: '24'}, {name: '思南县', code: '25'}, {
                            name: '印江土家族苗族自治县',
                            code: '26'
                        }, {name: '德江县', code: '27'}, {name: '沿河土家族自治县', code: '28'}, {
                            name: '松桃苗族自治县',
                            code: '29'
                        }, {name: '万山特区', code: '30'}]
                    }, {
                        name: '黔西南布依族苗族自治州',
                        code: '23',
                        city: [{name: '兴义市', code: '01'}, {name: '兴仁县', code: '22'}, {
                            name: '普安县',
                            code: '23'
                        }, {name: '晴隆县', code: '24'}, {name: '贞丰县', code: '25'}, {name: '望谟县', code: '26'}, {
                            name: '册亨县',
                            code: '27'
                        }, {name: '安龙县', code: '28'}]
                    }, {
                        name: '毕节地区',
                        code: '24',
                        city: [{name: '毕节市', code: '01'}, {name: '大方县', code: '22'}, {
                            name: '黔西县',
                            code: '23'
                        }, {name: '金沙县', code: '24'}, {name: '织金县', code: '25'}, {
                            name: '纳雍县',
                            code: '26'
                        }, {name: '威宁彝族回族苗族自治县', code: '27'}, {name: '赫章县', code: '28'}]
                    }, {
                        name: '黔东南苗族侗族自治州',
                        code: '26',
                        city: [{name: '凯里市', code: '01'}, {name: '黄平县', code: '22'}, {
                            name: '施秉县',
                            code: '23'
                        }, {name: '三穗县', code: '24'}, {name: '镇远县', code: '25'}, {name: '岑巩县', code: '26'}, {
                            name: '天柱县',
                            code: '27'
                        }, {name: '锦屏县', code: '28'}, {name: '剑河县', code: '29'}, {name: '台江县', code: '30'}, {
                            name: '黎平县',
                            code: '31'
                        }, {name: '榕江县', code: '32'}, {name: '从江县', code: '33'}, {name: '雷山县', code: '34'}, {
                            name: '麻江县',
                            code: '35'
                        }, {name: '丹寨县', code: '36'}]
                    }, {
                        name: '黔南布依族苗族自治州',
                        code: '27',
                        city: [{name: '都匀市', code: '01'}, {name: '福泉市', code: '02'}, {
                            name: '荔波县',
                            code: '22'
                        }, {name: '贵定县', code: '23'}, {name: '瓮安县', code: '25'}, {name: '独山县', code: '26'}, {
                            name: '平塘县',
                            code: '27'
                        }, {name: '罗甸县', code: '28'}, {name: '长顺县', code: '29'}, {name: '龙里县', code: '30'}, {
                            name: '惠水县',
                            code: '31'
                        }, {name: '三都水族自治县', code: '32'}]
                    }]
                }
            }, {
                region: {
                    name: '云南省',
                    code: '53',
                    state: [{
                        name: '昆明市',
                        code: '01',
                        city: [{name: '五华区', code: '02'}, {name: '盘龙区', code: '03'}, {
                            name: '官渡区',
                            code: '11'
                        }, {name: '西山区', code: '12'}, {name: '东川区', code: '13'}, {name: '呈贡县', code: '21'}, {
                            name: '晋宁县',
                            code: '22'
                        }, {name: '富民县', code: '24'}, {name: '宜良县', code: '25'}, {
                            name: '石林彝族自治县',
                            code: '26'
                        }, {name: '嵩明县', code: '27'}, {name: '禄劝彝族苗族自治县', code: '28'}, {
                            name: '寻甸回族彝族自治县',
                            code: '29'
                        }, {name: '安宁市', code: '81'}]
                    }, {
                        name: '曲靖市',
                        code: '03',
                        city: [{name: '麒麟区', code: '02'}, {name: '马龙县', code: '21'}, {
                            name: '陆良县',
                            code: '22'
                        }, {name: '师宗县', code: '23'}, {name: '罗平县', code: '24'}, {name: '富源县', code: '25'}, {
                            name: '会泽县',
                            code: '26'
                        }, {name: '沾益县', code: '28'}, {name: '宣威市', code: '81'}]
                    }, {
                        name: '玉溪市',
                        code: '04',
                        city: [{name: '红塔区', code: '02'}, {name: '江川县', code: '21'}, {
                            name: '澄江县',
                            code: '22'
                        }, {name: '通海县', code: '23'}, {name: '华宁县', code: '24'}, {
                            name: '易门县',
                            code: '25'
                        }, {name: '峨山彝族自治县', code: '26'}, {name: '新平彝族傣族自治县', code: '27'}, {
                            name: '元江哈尼族彝族傣族自治县',
                            code: '28'
                        }]
                    }, {
                        name: '保山市',
                        code: '05',
                        city: [{name: '隆阳区', code: '02'}, {name: '施甸县', code: '21'}, {
                            name: '腾冲县',
                            code: '22'
                        }, {name: '龙陵县', code: '23'}, {name: '昌宁县', code: '24'}]
                    }, {
                        name: '昭通市',
                        code: '06',
                        city: [{name: '昭阳区', code: '02'}, {name: '鲁甸县', code: '21'}, {
                            name: '巧家县',
                            code: '22'
                        }, {name: '盐津县', code: '23'}, {name: '大关县', code: '25'}, {name: '永善县', code: '25'}, {
                            name: '绥江县',
                            code: '26'
                        }, {name: '镇雄县', code: '27'}, {name: '彝良县', code: '28'}, {name: '威信县', code: '29'}, {
                            name: '水富县',
                            code: '30'
                        }]
                    }, {
                        name: '丽江市',
                        code: '07',
                        city: [{name: '古城区', code: '02'}, {name: '玉龙纳西族自治县', code: '21'}, {
                            name: '永胜县',
                            code: '22'
                        }, {name: '华坪县', code: '23'}, {name: '宁蒗彝族自治县', code: '24'}]
                    }, {
                        name: '思茅市',
                        code: '08',
                        city: [{name: '翠云区', code: '02'}, {name: '普洱哈尼族彝族自治县', code: '21'}, {
                            name: '墨江哈尼族自治县',
                            code: '22'
                        }, {name: '景东彝族自治县', code: '23'}, {name: '景谷傣族彝族自治县', code: '24'}, {
                            name: '镇沅彝族哈尼族拉祜族自治县',
                            code: '25'
                        }, {name: '江城哈尼族彝族自治县', code: '26'}, {name: '孟连傣族拉祜族佤族自治县', code: '27'}, {
                            name: '澜沧拉祜族自治县',
                            code: '28'
                        }, {name: '西盟佤族自治县', code: '29'}]
                    }, {
                        name: '临沧市',
                        code: '09',
                        city: [{name: '临翔区', code: '02'}, {name: '凤庆县', code: '21'}, {name: '云县', code: '22'}, {
                            name: '永德县',
                            code: '23'
                        }, {name: '镇康县', code: '24'}, {name: '双江拉祜族佤族布朗族傣族自治县', code: '25'}, {
                            name: '耿马傣族佤族自治县',
                            code: '26'
                        }, {name: '沧源佤族自治县', code: '27'}]
                    }, {
                        name: '楚雄彝族自治州',
                        code: '23',
                        city: [{name: '楚雄市', code: '01'}, {name: '双柏县', code: '22'}, {
                            name: '牟定县',
                            code: '23'
                        }, {name: '南华县', code: '24'}, {name: '姚安县', code: '25'}, {name: '大姚县', code: '26'}, {
                            name: '永仁县',
                            code: '27'
                        }, {name: '元谋县', code: '28'}, {name: '武定县', code: '29'}, {name: '禄丰县', code: '31'}]
                    }, {
                        name: '红河哈尼族彝族自治州',
                        code: '25',
                        city: [{name: '个旧市', code: '01'}, {name: '开远市', code: '02'}, {
                            name: '蒙自县',
                            code: '22'
                        }, {name: '屏边苗族自治县', code: '23'}, {name: '建水县', code: '24'}, {
                            name: '石屏县',
                            code: '25'
                        }, {name: '弥勒县', code: '26'}, {name: '泸西县', code: '27'}, {name: '元阳县', code: '28'}, {
                            name: '红河县',
                            code: '29'
                        }, {name: '金平苗族瑶族傣族自治县', code: '30'}, {name: '绿春县', code: '31'}, {name: '河口瑶族自治县', code: '32'}]
                    }, {
                        name: '文山壮族苗族自治州',
                        code: '26',
                        city: [{name: '文山县', code: '21'}, {name: '砚山县', code: '22'}, {
                            name: '西畴县',
                            code: '23'
                        }, {name: '麻栗坡县', code: '24'}, {name: '马关县', code: '25'}, {name: '丘北县', code: '26'}, {
                            name: '广南县',
                            code: '27'
                        }, {name: '富宁县', code: '28'}]
                    }, {
                        name: '西双版纳傣族自治州',
                        code: '28',
                        city: [{name: '景洪市', code: '01'}, {name: '勐海县', code: '22'}, {name: '勐腊县', code: '23'}]
                    }, {
                        name: '大理白族自治州',
                        code: '29',
                        city: [{name: '大理市', code: '01'}, {name: '漾濞彝族自治县', code: '22'}, {
                            name: '祥云县',
                            code: '23'
                        }, {name: '宾川县', code: '24'}, {name: '弥渡县', code: '25'}, {
                            name: '南涧彝族自治县',
                            code: '26'
                        }, {name: '巍山彝族回族自治县', code: '27'}, {name: '永平县', code: '28'}, {
                            name: '云龙县',
                            code: '29'
                        }, {name: '洱源县', code: '30'}, {name: '剑川县', code: '31'}, {name: '鹤庆县', code: '32'}]
                    }, {
                        name: '德宏傣族景颇族自治州',
                        code: '31',
                        city: [{name: '瑞丽市', code: '02'}, {name: '潞西市', code: '03'}, {
                            name: '梁河县',
                            code: '22'
                        }, {name: '盈江县', code: '23'}, {name: '陇川县', code: '24'}]
                    }, {
                        name: '怒江傈僳族自治州',
                        code: '33',
                        city: [{name: '泸水县', code: '21'}, {name: '福贡县', code: '23'}, {
                            name: '贡山独龙族怒族自治县',
                            code: '24'
                        }, {name: '兰坪白族普米族自治县', code: '25'}]
                    }, {
                        name: '迪庆藏族自治州',
                        code: '34',
                        city: [{name: '香格里拉县', code: '21'}, {name: '德钦县', code: '22'}, {name: '维西傈僳族自治县', code: '23'}]
                    }]
                }
            }, {
                region: {
                    name: '西藏',
                    code: '54',
                    state: [{
                        name: '拉萨市',
                        code: '01',
                        city: [{name: '城关区', code: '02'}, {name: '林周县', code: '21'}, {
                            name: '当雄县',
                            code: '22'
                        }, {name: '尼木县', code: '23'}, {name: '曲水县', code: '24'}, {name: '堆龙德庆县', code: '25'}, {
                            name: '达孜县',
                            code: '26'
                        }, {name: '墨竹工卡县', code: '27'}]
                    }, {
                        name: '昌都地区',
                        code: '21',
                        city: [{name: '昌都县', code: '21'}, {name: '江达县', code: '22'}, {
                            name: '贡觉县',
                            code: '23'
                        }, {name: '类乌齐县', code: '24'}, {name: '丁青县', code: '25'}, {name: '察雅县', code: '26'}, {
                            name: '八宿县',
                            code: '27'
                        }, {name: '左贡县', code: '28'}, {name: '芒康县', code: '29'}, {name: '洛隆县', code: '32'}, {
                            name: '边坝县',
                            code: '33'
                        }]
                    }, {
                        name: '山南地区',
                        code: '22',
                        city: [{name: '乃东县', code: '21'}, {name: '扎囊县', code: '22'}, {
                            name: '贡嘎县',
                            code: '23'
                        }, {name: '桑日县', code: '24'}, {name: '琼结县', code: '25'}, {name: '曲松县', code: '26'}, {
                            name: '措美县',
                            code: '27'
                        }, {name: '洛扎县', code: '28'}, {name: '加查县', code: '29'}, {name: '隆子县', code: '31'}, {
                            name: '错那县',
                            code: '32'
                        }, {name: '浪卡子县', code: '33'}]
                    }, {
                        name: '日喀则地区',
                        code: '23',
                        city: [{name: '日喀则市', code: '01'}, {name: '南木林县', code: '22'}, {
                            name: '江孜县',
                            code: '23'
                        }, {name: '定日县', code: '24'}, {name: '萨迦县', code: '25'}, {name: '拉孜县', code: '26'}, {
                            name: '昂仁县',
                            code: '27'
                        }, {name: '谢通门县', code: '28'}, {name: '白朗县', code: '29'}, {name: '仁布县', code: '30'}, {
                            name: '康马县',
                            code: '31'
                        }, {name: '定结县', code: '32'}, {name: '仲巴县', code: '33'}, {name: '亚东县', code: '34'}, {
                            name: '吉隆县',
                            code: '35'
                        }, {name: '聂拉木县', code: '36'}, {name: '萨嘎县', code: '37'}, {name: '岗巴县', code: '38'}]
                    }, {
                        name: '那曲地区',
                        code: '24',
                        city: [{name: '那曲县', code: '21'}, {name: '嘉黎县', code: '22'}, {
                            name: '比如县',
                            code: '23'
                        }, {name: '聂荣县', code: '24'}, {name: '安多县', code: '25'}, {name: '申扎县', code: '26'}, {
                            name: '索县',
                            code: '27'
                        }, {name: '班戈县', code: '28'}, {name: '巴青县', code: '29'}, {name: '尼玛县', code: '30'}]
                    }, {
                        name: '阿里地区',
                        code: '25',
                        city: [{name: '普兰县', code: '21'}, {name: '札达县', code: '22'}, {
                            name: '噶尔县',
                            code: '23'
                        }, {name: '日土县', code: '24'}, {name: '革吉县', code: '25'}, {name: '改则县', code: '26'}, {
                            name: '措勤县',
                            code: '27'
                        }]
                    }, {
                        name: '林芝地区',
                        code: '26',
                        city: [{name: '林芝县', code: '21'}, {name: '工布江达县', code: '22'}, {
                            name: '米林县',
                            code: '23'
                        }, {name: '墨脱县', code: '24'}, {name: '波密县', code: '25'}, {name: '察隅县', code: '26'}, {
                            name: '朗县',
                            code: '27'
                        }]
                    }]
                }
            }, {
                region: {
                    name: '陕西省',
                    code: '61',
                    state: [{
                        name: '西安市',
                        code: '01',
                        city: [{name: '新城区', code: '02'}, {name: '碑林区', code: '03'}, {
                            name: '莲湖区',
                            code: '04'
                        }, {name: '灞桥区', code: '11'}, {name: '未央区', code: '12'}, {name: '雁塔区', code: '13'}, {
                            name: '阎良区',
                            code: '14'
                        }, {name: '临潼区', code: '15'}, {name: '长安区', code: '16'}, {name: '蓝田县', code: '22'}, {
                            name: '周至县',
                            code: '24'
                        }, {name: '户县', code: '25'}, {name: '高陵县', code: '26'}]
                    }, {
                        name: '铜川市',
                        code: '02',
                        city: [{name: '王益区', code: '02'}, {name: '印台区', code: '03'}, {
                            name: '耀州区',
                            code: '04'
                        }, {name: '宜君县', code: '22'}]
                    }, {
                        name: '宝鸡市',
                        code: '03',
                        city: [{name: '渭滨区', code: '02'}, {name: '金台区', code: '03'}, {
                            name: '陈仓区',
                            code: '04'
                        }, {name: '凤翔县', code: '22'}, {name: '岐山县', code: '23'}, {name: '扶风县', code: '24'}, {
                            name: '眉县',
                            code: '26'
                        }, {name: '陇县', code: '27'}, {name: '千阳县', code: '28'}, {name: '麟游县', code: '29'}, {
                            name: '凤县',
                            code: '30'
                        }, {name: '太白县', code: '31'}]
                    }, {
                        name: '咸阳市',
                        code: '04',
                        city: [{name: '秦都区', code: '02'}, {name: '杨凌区', code: '03'}, {
                            name: '渭城区',
                            code: '04'
                        }, {name: '三原县', code: '22'}, {name: '泾阳县', code: '23'}, {name: '乾县', code: '24'}, {
                            name: '礼泉县',
                            code: '25'
                        }, {name: '永寿县', code: '26'}, {name: '彬县', code: '27'}, {name: '长武县', code: '28'}, {
                            name: '旬邑县',
                            code: '29'
                        }, {name: '淳化县', code: '30'}, {name: '武功县', code: '31'}, {name: '兴平市', code: '81'}]
                    }, {
                        name: '渭南市',
                        code: '05',
                        city: [{name: '临渭区', code: '02'}, {name: '华县', code: '21'}, {name: '潼关县', code: '22'}, {
                            name: '大荔县',
                            code: '23'
                        }, {name: '合阳县', code: '24'}, {name: '澄城县', code: '25'}, {name: '蒲城县', code: '26'}, {
                            name: '白水县',
                            code: '27'
                        }, {name: '富平县', code: '28'}, {name: '韩城市', code: '81'}, {name: '华阴市', code: '82'}]
                    }, {
                        name: '延安市',
                        code: '06',
                        city: [{name: '宝塔区', code: '02'}, {name: '延长县', code: '21'}, {
                            name: '延川县',
                            code: '22'
                        }, {name: '子长县', code: '23'}, {name: '安塞县', code: '24'}, {name: '志丹县', code: '25'}, {
                            name: '吴起县',
                            code: '26'
                        }, {name: '甘泉县', code: '27'}, {name: '富县', code: '28'}, {name: '洛川县', code: '29'}, {
                            name: '宜川县',
                            code: '30'
                        }, {name: '黄龙县', code: '31'}, {name: '黄陵县', code: '32'}]
                    }, {
                        name: '汉中市',
                        code: '07',
                        city: [{name: '汉台区', code: '02'}, {name: '南郑县', code: '21'}, {name: '城固县', code: '22'}, {
                            name: '洋县',
                            code: '23'
                        }, {name: '西乡县', code: '24'}, {name: '勉县', code: '25'}, {name: '宁强县', code: '26'}, {
                            name: '略阳县',
                            code: '27'
                        }, {name: '镇巴县', code: '28'}, {name: '留坝县', code: '29'}, {name: '佛坪县', code: '30'}]
                    }, {
                        name: '榆林市',
                        code: '08',
                        city: [{name: '榆阳区', code: '02'}, {name: '神木县', code: '21'}, {
                            name: '府谷县',
                            code: '22'
                        }, {name: '横山县', code: '23'}, {name: '靖边县', code: '24'}, {name: '定边县', code: '25'}, {
                            name: '绥德县',
                            code: '26'
                        }, {name: '米脂县', code: '27'}, {name: '佳县', code: '28'}, {name: '吴堡县', code: '29'}, {
                            name: '清涧县',
                            code: '30'
                        }, {name: '子洲县', code: '31'}]
                    }, {
                        name: '安康市',
                        code: '09',
                        city: [{name: '汉滨区', code: '02'}, {name: '汉阴县', code: '21'}, {
                            name: '石泉县',
                            code: '22'
                        }, {name: '宁陕县', code: '23'}, {name: '紫阳县', code: '24'}, {name: '岚皋县', code: '25'}, {
                            name: '平利县',
                            code: '26'
                        }, {name: '镇坪县', code: '27'}, {name: '旬阳县', code: '28'}, {name: '白河县', code: '29'}]
                    }, {
                        name: '商洛市',
                        code: '10',
                        city: [{name: '商州区', code: '02'}, {name: '洛南县', code: '21'}, {
                            name: '丹凤县',
                            code: '22'
                        }, {name: '商南县', code: '23'}, {name: '山阳县', code: '24'}, {name: '镇安县', code: '25'}, {
                            name: '柞水县',
                            code: '26'
                        }]
                    }]
                }
            }, {
                region: {
                    name: '甘肃省',
                    code: '62',
                    state: [{
                        name: '兰州市',
                        code: '01',
                        city: [{name: '城关区', code: '02'}, {name: '七里河区', code: '03'}, {
                            name: '西固区',
                            code: '04'
                        }, {name: '安宁区', code: '05'}, {name: '红古区', code: '11'}, {name: '永登县', code: '21'}, {
                            name: '皋兰县',
                            code: '22'
                        }, {name: '榆中县', code: '23'}]
                    }, {name: '嘉峪关市', code: '02', city: []}, {
                        name: '金昌市',
                        code: '03',
                        city: [{name: '金川区', code: '02'}, {name: '永昌县', code: '21'}]
                    }, {
                        name: '白银市',
                        code: '04',
                        city: [{name: '白银区', code: '02'}, {name: '平川区', code: '03'}, {
                            name: '靖远县',
                            code: '21'
                        }, {name: '会宁县', code: '22'}, {name: '景泰县', code: '23'}]
                    }, {
                        name: '天水市',
                        code: '05',
                        city: [{name: '秦城区', code: '02'}, {name: '北道区', code: '03'}, {
                            name: '清水县',
                            code: '21'
                        }, {name: '秦安县', code: '22'}, {name: '甘谷县', code: '23'}, {
                            name: '武山县',
                            code: '24'
                        }, {name: '张家川回族自治县', code: '25'}]
                    }, {
                        name: '武威市',
                        code: '06',
                        city: [{name: '凉州区', code: '02'}, {name: '民勤县', code: '21'}, {
                            name: '古浪县',
                            code: '22'
                        }, {name: '天祝藏族自治县', code: '23'}]
                    }, {
                        name: '张掖市',
                        code: '07',
                        city: [{name: '甘州区', code: '02'}, {name: '肃南裕固族自治县', code: '21'}, {
                            name: '民乐县',
                            code: '22'
                        }, {name: '临泽县', code: '23'}, {name: '高台县', code: '24'}, {name: '山丹县', code: '25'}]
                    }, {
                        name: '平凉市',
                        code: '08',
                        city: [{name: '崆峒区', code: '02'}, {name: '泾川县', code: '21'}, {
                            name: '灵台县',
                            code: '22'
                        }, {name: '崇信县', code: '23'}, {name: '华亭县', code: '24'}, {name: '庄浪县', code: '25'}, {
                            name: '静宁县',
                            code: '26'
                        }]
                    }, {
                        name: '酒泉市',
                        code: '09',
                        city: [{name: '肃州区', code: '02'}, {name: '金塔县', code: '21'}, {
                            name: '瓜州县',
                            code: '22'
                        }, {name: '肃北蒙古族自治县', code: '23'}, {name: '阿克塞哈萨克族自治县', code: '24'}, {
                            name: '玉门市',
                            code: '81'
                        }, {name: '敦煌市', code: '82'}]
                    }, {
                        name: '庆阳市',
                        code: '10',
                        city: [{name: '西峰区', code: '02'}, {name: '庆城县', code: '21'}, {name: '环县', code: '22'}, {
                            name: '华池县',
                            code: '23'
                        }, {name: '合水县', code: '24'}, {name: '正宁县', code: '25'}, {name: '宁县', code: '26'}, {
                            name: '镇原县',
                            code: '27'
                        }]
                    }, {
                        name: '定西市',
                        code: '11',
                        city: [{name: '安定区', code: '02'}, {name: '通渭县', code: '21'}, {
                            name: '陇西县',
                            code: '22'
                        }, {name: '渭源县', code: '23'}, {name: '临洮县', code: '24'}, {name: '漳县', code: '25'}, {
                            name: '岷县',
                            code: '26'
                        }]
                    }, {
                        name: '陇南市',
                        code: '12',
                        city: [{name: '武都区', code: '02'}, {name: '成县', code: '21'}, {name: '文县', code: '22'}, {
                            name: '宕昌县',
                            code: '23'
                        }, {name: '康县', code: '24'}, {name: '西和县', code: '25'}, {name: '礼县', code: '26'}, {
                            name: '徽县',
                            code: '27'
                        }, {name: '两当县', code: '28'}]
                    }, {
                        name: '临夏回族自治州',
                        code: '29',
                        city: [{name: '临夏市', code: '01'}, {name: '临夏县', code: '21'}, {
                            name: '康乐县',
                            code: '22'
                        }, {name: '永靖县', code: '23'}, {name: '广河县', code: '24'}, {name: '和政县', code: '25'}, {
                            name: '东乡族自治县',
                            code: '26'
                        }, {name: '积石山保安族东乡族撒拉族自治县', code: '27'}]
                    }, {
                        name: '甘南藏族自治州',
                        code: '30',
                        city: [{name: '合作市', code: '01'}, {name: '临潭县', code: '21'}, {
                            name: '卓尼县',
                            code: '22'
                        }, {name: '舟曲县', code: '23'}, {name: '迭部县', code: '24'}, {name: '玛曲县', code: '25'}, {
                            name: '碌曲县',
                            code: '26'
                        }, {name: '夏河县', code: '27'}]
                    }]
                }
            }, {
                region: {
                    name: '青海',
                    code: '63',
                    state: [{
                        name: '西宁市',
                        code: '01',
                        city: [{name: '城东区', code: '02'}, {name: '城中区', code: '03'}, {
                            name: '城西区',
                            code: '04'
                        }, {name: '城北区', code: '05'}, {name: '大通回族土族自治县', code: '21'}, {
                            name: '湟中县',
                            code: '22'
                        }, {name: '湟源县', code: '23'}]
                    }, {
                        name: '海东地区',
                        code: '21',
                        city: [{name: '平安县', code: '21'}, {name: '民和回族土族自治县', code: '22'}, {
                            name: '乐都县',
                            code: '23'
                        }, {name: '互助土族自治县', code: '26'}, {name: '化隆回族自治县', code: '27'}, {name: '循化撒拉族自治县', code: '28'}]
                    }, {
                        name: '海北藏族自治州',
                        code: '22',
                        city: [{name: '门源回族自治县', code: '21'}, {name: '祁连县', code: '22'}, {
                            name: '海晏县',
                            code: '23'
                        }, {name: '刚察县', code: '24'}]
                    }, {
                        name: '黄南藏族自治州',
                        code: '23',
                        city: [{name: '同仁县', code: '21'}, {name: '尖扎县', code: '22'}, {
                            name: '泽库县',
                            code: '23'
                        }, {name: '河南蒙古族自治县', code: '24'}]
                    }, {
                        name: '海南藏族自治州',
                        code: '25',
                        city: [{name: '共和县', code: '21'}, {name: '同德县', code: '22'}, {
                            name: '贵德县',
                            code: '23'
                        }, {name: '兴海县', code: '24'}, {name: '贵南县', code: '25'}]
                    }, {
                        name: '果洛藏族自治州',
                        code: '26',
                        city: [{name: '玛沁县', code: '21'}, {name: '班玛县', code: '22'}, {
                            name: '甘德县',
                            code: '23'
                        }, {name: '达日县', code: '24'}, {name: '久治县', code: '25'}, {name: '玛多县', code: '26'}]
                    }, {
                        name: '玉树藏族自治州',
                        code: '27',
                        city: [{name: '玉树县', code: '21'}, {name: '杂多县', code: '22'}, {
                            name: '称多县',
                            code: '23'
                        }, {name: '治多县', code: '24'}, {name: '囊谦县', code: '25'}, {name: '曲麻莱县', code: '26'}]
                    }, {
                        name: '海西蒙古族藏族自治州',
                        code: '28',
                        city: [{name: '格尔木市', code: '01'}, {name: '德令哈市', code: '02'}, {
                            name: '乌兰县',
                            code: '21'
                        }, {name: '都兰县', code: '22'}, {name: '天峻县', code: '23'}]
                    }]
                }
            }, {
                region: {
                    name: '宁夏',
                    code: '64',
                    state: [{
                        name: '银川市',
                        code: '01',
                        city: [{name: '兴庆区', code: '04'}, {name: '西夏区', code: '05'}, {
                            name: '金凤区',
                            code: '06'
                        }, {name: '永宁县', code: '21'}, {name: '贺兰县', code: '22'}, {name: '灵武市', code: '81'}]
                    }, {
                        name: '石嘴山市',
                        code: '02',
                        city: [{name: '大武口区', code: '02'}, {name: '惠农区', code: '05'}, {name: '平罗县', code: '21'}]
                    }, {
                        name: '吴忠市',
                        code: '03',
                        city: [{name: '利通区', code: '02'}, {name: '盐池县', code: '23'}, {
                            name: '同心县',
                            code: '24'
                        }, {name: '青铜峡市', code: '81'}]
                    }, {
                        name: '固原市',
                        code: '04',
                        city: [{name: '原州区', code: '02'}, {name: '西吉县', code: '22'}, {
                            name: '隆德县',
                            code: '23'
                        }, {name: '泾源县', code: '24'}, {name: '彭阳县', code: '25'}]
                    }, {
                        name: '中卫市',
                        code: '05',
                        city: [{name: '沙坡头区', code: '02'}, {name: '中宁县', code: '21'}, {name: '海原县', code: '22'}]
                    }]
                }
            }, {
                region: {
                    name: '新疆',
                    code: '65',
                    state: [{
                        name: '乌鲁木齐市',
                        code: '01',
                        city: [{name: '天山区', code: '02'}, {name: '沙依巴克区', code: '03'}, {
                            name: '新市区',
                            code: '04'
                        }, {name: '水磨沟区', code: '05'}, {name: '头屯河区', code: '06'}, {name: '达坂城区', code: '07'}, {
                            name: '东山区',
                            code: '08'
                        }, {name: '乌鲁木齐县', code: '21'}]
                    }, {
                        name: '克拉玛依市',
                        code: '02',
                        city: [{name: '独山子区', code: '02'}, {name: '克拉玛依区', code: '03'}, {
                            name: '白碱滩区',
                            code: '04'
                        }, {name: '乌尔禾区', code: '05'}]
                    }, {
                        name: '吐鲁番地区',
                        code: '21',
                        city: [{name: '吐鲁番市', code: '01'}, {name: '鄯善县', code: '22'}, {name: '托克逊县', code: '23'}]
                    }, {
                        name: '哈密地区',
                        code: '22',
                        city: [{name: '哈密市', code: '01'}, {name: '巴里坤哈萨克自治县', code: '22'}, {name: '伊吾县', code: '23'}]
                    }, {
                        name: '昌吉回族自治州',
                        code: '23',
                        city: [{name: '昌吉市', code: '01'}, {name: '阜康市', code: '02'}, {
                            name: '米泉市',
                            code: '03'
                        }, {name: '呼图壁县', code: '23'}, {name: '玛纳斯县', code: '24'}, {
                            name: '奇台县',
                            code: '25'
                        }, {name: '吉木萨尔县', code: '27'}, {name: '木垒哈萨克自治县', code: '28'}]
                    }, {
                        name: '博尔塔拉蒙古自治州',
                        code: '27',
                        city: [{name: '博乐市', code: '01'}, {name: '精河县', code: '22'}, {name: '温泉县', code: '23'}]
                    }, {
                        name: '巴音郭楞蒙古自治州',
                        code: '28',
                        city: [{name: '库尔勒市', code: '01'}, {name: '轮台县', code: '22'}, {
                            name: '尉犁县',
                            code: '23'
                        }, {name: '若羌县', code: '24'}, {name: '且末县', code: '25'}, {
                            name: '焉耆回族自治县',
                            code: '26'
                        }, {name: '和静县', code: '27'}, {name: '和硕县', code: '28'}, {name: '博湖县', code: '29'}]
                    }, {
                        name: '阿克苏地区',
                        code: '29',
                        city: [{name: '阿克苏市', code: '01'}, {name: '温宿县', code: '22'}, {
                            name: '库车县',
                            code: '23'
                        }, {name: '沙雅县', code: '24'}, {name: '新和县', code: '25'}, {name: '拜城县', code: '26'}, {
                            name: '乌什县',
                            code: '27'
                        }, {name: '阿瓦提县', code: '28'}, {name: '柯坪县', code: '29'}]
                    }, {
                        name: '克孜勒苏柯尔克孜自治州',
                        code: '30',
                        city: [{name: '阿图什市', code: '01'}, {name: '阿克陶县', code: '22'}, {
                            name: '阿合奇县',
                            code: '23'
                        }, {name: '乌恰县', code: '24'}]
                    }, {
                        name: '喀什地区',
                        code: '31',
                        city: [{name: '喀什市', code: '01'}, {name: '疏附县', code: '21'}, {
                            name: '疏勒县',
                            code: '22'
                        }, {name: '英吉沙县', code: '23'}, {name: '泽普县', code: '24'}, {name: '莎车县', code: '25'}, {
                            name: '叶城县',
                            code: '26'
                        }, {name: '麦盖提县', code: '27'}, {name: '岳普湖县', code: '28'}, {name: '伽师县', code: '29'}, {
                            name: '巴楚县',
                            code: '30'
                        }, {name: '塔什库尔干塔吉克自治县', code: '31'}]
                    }, {
                        name: '和田地区',
                        code: '32',
                        city: [{name: '和田市', code: '01'}, {name: '和田县', code: '21'}, {
                            name: '墨玉县',
                            code: '22'
                        }, {name: '皮山县', code: '23'}, {name: '洛浦县', code: '24'}, {name: '策勒县', code: '25'}, {
                            name: '于田县',
                            code: '26'
                        }, {name: '民丰县', code: '27'}]
                    }, {
                        name: '伊犁哈萨克',
                        code: '40',
                        city: [{name: '伊宁市', code: '02'}, {name: '奎屯市', code: '03'}, {
                            name: '伊宁县',
                            code: '21'
                        }, {name: '察布查尔锡伯自治县', code: '22'}, {name: '霍城县', code: '23'}, {
                            name: '巩留县',
                            code: '24'
                        }, {name: '新源县', code: '25'}, {name: '昭苏县', code: '26'}, {name: '特克斯县', code: '27'}, {
                            name: '尼勒克县',
                            code: '28'
                        }]
                    }, {
                        name: '塔城地区',
                        code: '42',
                        city: [{name: '塔城市', code: '01'}, {name: '乌苏市', code: '02'}, {
                            name: '额敏县',
                            code: '21'
                        }, {name: '沙湾县', code: '23'}, {name: '托里县', code: '24'}, {
                            name: '裕民县',
                            code: '25'
                        }, {name: '和布克赛尔蒙古自治县', code: '26'}]
                    }, {
                        name: '阿勒泰地区',
                        code: '43',
                        city: [{name: '阿勒泰市', code: '01'}, {name: '布尔津县', code: '21'}, {
                            name: '富蕴县',
                            code: '22'
                        }, {name: '福海县', code: '23'}, {name: '哈巴河县', code: '24'}, {name: '青河县', code: '25'}, {
                            name: '吉木乃县',
                            code: '26'
                        }]
                    }, {name: '石河子市', code: '91', city: []}, {name: '阿拉尔市', code: '92', city: []}, {
                        name: '图木舒克市',
                        code: '93',
                        city: []
                    }, {name: '五家渠市', code: '94', city: []}]
                }
            }, {
                region: {
                    name: '台湾',
                    code: '71',
                    state: [{name: '台北', code: '01', city: []}, {name: '高雄', code: '02', city: []}, {
                        name: '基隆',
                        code: '03',
                        city: []
                    }, {name: '台中', code: '04', city: []}, {name: '台南', code: '05', city: []}, {
                        name: '新竹',
                        code: '06',
                        city: []
                    }, {name: '嘉义', code: '07', city: []}, {name: '宜兰', code: '08', city: []}, {
                        name: '桃园',
                        code: '09',
                        city: []
                    }, {name: '苗栗', code: '10', city: []}, {name: '彰化', code: '11', city: []}, {
                        name: '南投',
                        code: '12',
                        city: []
                    }, {name: '云林', code: '13', city: []}, {name: '屏东', code: '14', city: []}, {
                        name: '台东',
                        code: '15',
                        city: []
                    }, {name: '花莲', code: '16', city: []}, {name: '澎湖', code: '17', city: []}]
                }
            }, {
                region: {
                    name: '香港',
                    code: '81',
                    state: [{name: '香港岛', code: '01', city: []}, {name: '九龙', code: '02', city: []}, {
                        name: '新界',
                        code: '03',
                        city: []
                    }]
                }
            }, {
                region: {
                    name: '澳门',
                    code: '82',
                    state: [{name: '澳门半岛', code: '01', city: []}, {name: '氹仔岛', code: '02', city: []}, {
                        name: '路环岛',
                        code: '03',
                        city: []
                    }, {name: '路氹城', code: '04', city: []}]
                }
            }]
        }
    }

})