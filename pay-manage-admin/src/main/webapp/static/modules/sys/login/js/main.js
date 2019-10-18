$(function(){
    //判断页面是否有步骤栏
    setStep();

    //判断页面table是否有checkbox
    hasCheckBox();

    //判断页面table是否有赛选功能
//    needScreen();
    
    //开关
    $(".toggle").on("click", function() {
        setMenu($(this));
    });
    
    //自动加载层级信息
    autoCompleteLevel();
    
    

})

function setStep(){
    if($(".step-main").length<=0) return;
    var _num = $(".step-main .info").length;
    var _distance = 100/(_num-1);
    var _index = $(".step-main .step").data("complete")?$(".step-main .step").data("complete"):0;
    $(".step-main .info").each(function(e){
        if(e+1<=_index){
            $(this).addClass("complete");
        }
        $(this).css("left",e*_distance+"%");
    })
    $(".step-main .bg").css("width",(_index-1)*_distance+"%");
}

function showTip(e,f){
    if(f="undefined") f="modalStyle-2";
    $("#"+f).find(".modal-body p").html(e);
    $(".btn-tip").click();
}


function showTipPic(e,f){
    if(f=="undefined") f="modalStyle-pic";
    var _img = "<img src='"+e+"'>"
    $("#"+f).find(".modal-body").empty().html(_img);
    $(".btn-tip-pic").click();
}


function hasCheckBox(){
    if($(".has-checkbox").length<=0) return;
    $(document).on("click",".has-checkbox thead tr th:eq(0) input:checkbox",function(){
        if ($(this).prop("checked")) {
            $(".has-checkbox tbody tr td input").prop("checked", true);
        } else {
            $(".has-checkbox tbody tr td input").prop("checked", false);
        }
    })

    $(document).on("click",".has-checkbox tbody tr td input:checkbox",function(){
        checkInputNum();
    })
}


function checkInputNum(){
    if($(".has-checkbox").find("tbody input:checked").length>0){
        if($(".has-checkbox").find("tbody tr input:checked").length == $(".has-checkbox").find("tbody input:checkbox").length){
            $(".has-checkbox").find("th input").prop("checked",true)
        }else{
            $(".has-checkbox").find("th input").prop("checked",false)
        }
    }
}
//1.0  S
function needScreen(displayTitle){
    if($(".table.need-screen").length<=0) return;
    var _screenData = '<th width="30"><span class="icon-down"><span class="main-list"><span class="title"><input type="checkbox" name="selectAll"/> 全选</span><span class="list">查询信息<br />';
    $(".table.need-screen thead th").each(function(){
        if($(this).hasClass("disabled")){
            _screenData += '<span class="info" style="display:none;"><input type="checkbox" name="selectTitle" checked disabled="disabled"/> '+$(this).text()+'</span>';
        }else{
        	if(displayTitle.indexOf($(this).text())!=-1){
                _screenData += '<span class="info"><input type="checkbox" name="selectTitle" checked/> '+$(this).text()+'</span>';
        	}else{
        		_screenData += '<span class="info"><input type="checkbox" name="selectTitle"/> '+$(this).text()+'</span>';
        	}
        }
    })
    _screenData += '</span></span></th>';
    $(".table.need-screen thead tr").prepend(_screenData);
    $(".table.need-screen tbody tr").prepend('<td>&nbsp;</td>');
    
    if($(".main-list .list input:checked").length == $(".main-list .list input:checkbox").length){
        $(".main-list .title input:checkbox").prop("checked", true);
    }
    
   	 $(document).on("click",".main-list .title input[name='selectAll']:checkbox",function(){
	        if ($(this).prop("checked")) {
	            $(".main-list .list input").prop("checked", true)
	        }else{
	            $(".main-list .list input:not(:disabled)").prop("checked", false)
	        }
	        setTableDataShow($('#funcId').val());
	    });
	 $(document).on("click",".main-list .list input[name='selectTitle']:checkbox",function(){
	        if ($(this).prop("checked")) {
	            if($(".main-list .list input:checked").length == $(".main-list .list input:checkbox").length){
	                $(".main-list .title input:checkbox").prop("checked", true)
	            }
	        }else{
	            $(".main-list .title input:checkbox").prop("checked", false)
	        }
	        setTableDataShow($('#funcId').val());
	  });
  
   	setTableDataShow($('#funcId').val());
   
}
function setTableDataShow(funcId){
    $(".table.need-screen thead tr th").hide();
    $(".table.need-screen tbody tr td").hide();
    $(".table.need-screen thead tr th:eq(0)").show();
    $(".table.need-screen tbody tr").each(function(){
        $(this).find("td:eq(0)").show();
    });

     var newtitle="";
  
    $(".main-list .list input:checkbox").each(function(e){
        if ($(this).prop("checked")) {
        	 if(!$(this).prop("disabled")){
        		 newtitle=newtitle+$(this).closest(".info").text().trim()+"|";
        	 }
            var _index = $(this).closest(".info").index();
            $(".table.need-screen thead tr th:eq("+_index+")").show();
            $(".table.need-screen tbody tr").each(function(){
                $(this).find("td:eq("+_index+")").show();
            })
        }
    });
    $("#newtitle").val(newtitle.substring(0,newtitle.lastIndexOf("|")));
    recordNewTitle(funcId);
}

function recordNewTitle(funcId){
	if($('#newtitle').val()!=''||$('#title').val()!=''){
		if(($('#newtitle').val()).indexOf($('#title').val())!=0||($('#title').val()).indexOf($('#newtitle').val())!=0){//需要进行后台的维护
			$.ajax({  
				   type: "post",  
		           url: $('#baseContentUrl').val()+"/common/recordTitle?sessionId="+$('#sessionId').val(),
		           data : {
		        	   newtitle:$('#newtitle').val(),
		        	   funcId:funcId//相应的功能号
		           },
		           dataType: "json",
		           success: function (data) {
		        	   if(data.respCode!='000000'){
		        		   showTip("显示字段维护失败！");
		        		   $('#newtitle').val("");
		        	   }else{
		        		   $('#title').val($('#newtitle').val());
		        	   }
		           },
		           error : function(data){
		          		if(data.status == '405'){
		          			window.location.reload();
		          		}
		          		showTip("网络繁忙，请稍后重试！");
		          	 }
		    });
		}
	}
}
//1.0  E

function autoCompleteLevel(){
	
	 //商户名称模糊匹配
	 $(document).on('keyup','input.ajax_merCustID',function(e){
		    $('#merId').val("");
	        var _text = $(this).val().replace(/\s/g,"");
//	        if(_text==""||_text==undefined) return;
	        var _url =  $(this).data("url")+"?sessionId="+$('#sessionId').val();
	        setAutocomplete($(this),_url);
	    });

	    //点击下拉框给input赋值
	    $(document).on("click",".autocomplete li",function(){
	        $(this).parents(".form-group").find("input").val($(this).text());
	        $(this).parents(".form-group").find("div.autocomplete").remove();
	    });
	    if($(".hasMerData").length>0){
	    	if($(".mer-layer-menu span").length>1){
	    		$('.hasMerData .mer-layer .mer-layer-list .mer-layer-data').css("display","block");//展示
	    		var indexA = $('#orgLevelA').val();
	    		 var merLayerNum = '';
	    		if(indexA && indexA !=''){
	    			merLayerNum = parseInt(indexA)-1;
	    		}else{
	    			merLayerNum = parseInt($('#orgLevel').val())-1;
	    		}
//		        var merLayerNum = parseInt($('#orgLevel').val())-1;
		        
		        $(document).on("click",".hasMerData",function(){
		            $(this).addClass("active");
		            $(this).find(".mer-layer").show();
		        });
	
		        $(document).on("click",".mer-layer-menu span",function(){
		        	//处理回显问题，如果需要回显，需要设置orgLevelA,总部的层级参照B1502UpdateInfo
		        	var indexA = $('#orgLevelA').val();
		        	if(indexA && indexA != ''){
		        		var _index =(parseInt(indexA)-1)+$(this).index();
		        	}else{
		        		var _index =(parseInt($('#orgLevel').val())-1)+$(this).index();
		        	}
		        	
		            if(_index<=merLayerNum){
		                $(this).addClass("current").siblings().removeClass("current");
		                $(".mer-layer-list .mer-layer-data:eq("+_index+")").show().siblings().hide();
		            }
		            
		            dealLevel(_index);
		        });
		        $(document).on("click",".mer-layer-list .mer-layer-data span",function(){
		            $(this).addClass("current").siblings().removeClass("current");
		            $(this).closest(".hasMerData").find(".merinfo").text($(this).text());
		            $('#orgLevelInfos').val($(this).data("merid"));
//		            $(this).closest(".hasMerData").find("input[name='merCustId']").val($(this).data("merid"));
		            var _url = $(this).closest(".hasMerData").data("url")+"?sessionId="+$('#sessionId').val();
		            var _thisId = $(this).data("merid");
	                var dataMers=_thisId.split("-");
		            var _index = $(this).closest(".mer-layer-data").index();
		            
		            var txt = $(this).context.dataset.merid.split('-');
		            dealLevel(txt[2]);
		            
		            $.ajax({
		                type: "POST",
		                url: _url,
		                data:{
		                	orgHeadId:dataMers[0],
		                	orgLevelId:dataMers[1],
		                	orgLevel:dataMers[2]
		                },
		                success: function (msg) {
		                	 var msg = eval("(" + msg + ")");//转换为json对象
		                	 if(msg.respCode=="000000"){
		                		 var _data=msg.orgLevelInfoVo;
		                		 var _aData = _data;//转换为json对象subOrgLevelInfoVos
		                		 if(_aData.subOrgLevelInfoVos.length>0){
			                            merLayerNum =_index+1;
			                            var _html = "";
			                            for(var i=0;i<_aData.subOrgLevelInfoVos.length;i++){
			                                _html += "<span data-merid="+_aData.subOrgLevelInfoVos[i].orgHeadId+'-'+_aData.subOrgLevelInfoVos[i].orgLevelId+'-'+_aData.subOrgLevelInfoVos[i].orgLevel+">"+_aData.subOrgLevelInfoVos[i].orgLevelName+"</span>"
			                            }
			                            
			                            var indexA = $('#orgLevelA').val();
			        		        	if(indexA && indexA != ''){
			        		        		$(".mer-layer-menu span:eq("+(merLayerNum-(parseInt(indexA)-1))+")").addClass("current").siblings().removeClass("current");
				                            $(".mer-layer-list .mer-layer-data:eq("+(merLayerNum)+")").html(_html).show().siblings().hide();
			        		        	}else{
			        		        		$(".mer-layer-menu span:eq("+(merLayerNum-(parseInt($('#orgLevel').val())-1))+")").addClass("current").siblings().removeClass("current");
				                            $(".mer-layer-list .mer-layer-data:eq("+(merLayerNum)+")").html(_html).show().siblings().hide();
			        		        	}
			                            
			                            
	
			                        }else{
			                            $(".hasMerData").removeClass("active");
			                            $(".hasMerData").find(".mer-layer").hide();
			                            
			                            dealLevel(merLayerNum);
			                     }
		                		 
		                		 //商户列表
		                		 getMerOptions(msg);
		                		 
		                		 if(_index>=9){
		     		                setTimeout(function(){
		     		                    $(".hasMerData").removeClass("active");
		     		                    $(".hasMerData").find(".mer-layer").hide();
		     		                },500);
		     		                return;
		     		            }
		                	 }
		                },
		                error: function (_aData) {
		                	if(_aData.status == '405'){
		               			window.location.reload();
		               		}
		        			showTip("网络超时,请稍后重试");
		                }
		            });
		        });
		    }
	    }
	    //点击外部删除下拉框
	    $(document).click(function(e){
	        if($(".autocomplete").length>0){
	            $(".autocomplete").remove();
	        }
	        if($(".hasMerData").length>0){
	            if($(e.target).closest(".hasMerData").length<=0){
	                $(".hasMerData").removeClass("active");
	                $(".hasMerData").find(".mer-layer").hide();
	            }
	        }
	    })
}

/*
 * 实现Autocomplete功能
 * e input 本身
 * f 显示下拉的宽度
 * g 显示left数值
 * h ajax地址
 * */
function setAutocomplete(e,h,f,g,fun){
    //获得input本身
    var _this = e;

    //根据屏幕获得下拉框的高度,最高300
    var _maxheight = $(window).height() - (_this.offset().top - $(document).scrollTop())  - _this.outerHeight() - 10;
    _maxheight = _maxheight > 300 ? 300:_maxheight;
    if(f==undefined || f==""){ f = _this.outerWidth(true) };
    if(g==undefined || g==""){
        var _a = 0;
        _this.prevAll().each(function(){
            _a += $(this).outerWidth(true);
        })
        g = _a + 4;
    };
    var blurMerName=_this.val();
    $.ajax({
        type: "POST",
        url: h,
        data:{
            "blurMerName":blurMerName,
            "isLeftMatch":$('#isLeftMatch').val()
        },
        success: function (msg) {
            var _aData = eval("(" + msg + ")");//转换为json对象
            _this.parent().find("div.autocomplete").remove(); //删除下拉框
            if(_aData.respCode=="000000"){
                //如果值为空return
                if(_aData.merList.length<=0){
                    return;
                }
                if(_aData.merList.length==1){
                    _this.val(_aData.merList[0].memberName+"("+_aData.merList[0].memberId+")");
                    $('#merId').val(_aData.merList[0].memberId);//有效选中后的处理
                    $('#merchInfo').val(_aData.merList[0].memberId+'-'+_aData.merList[0].memberName+'-'+_aData.merList[0].merOrgLevelId);
                    _this.focus();

                    if(typeof fun == "function"){
                        fun(_this);
                    }
                }
                //_text为临时保存html变量
                var _text ="";
                _text += "<div class='autocomplete' style='width:"+f+"px; left:"+g+"px; max-height:"+_maxheight+"px'><ul>";
                for(var i=0;i< _aData.merList.length;i++){
                	var merchInfo="\""+_aData.merList[i].memberId+"-"+_aData.merList[0].memberName+"-"+_aData.merList[i].merOrgLevelId+"\"";//转换为json对象;
                	var memberName=_aData.merList[i].memberName;
                	if(memberName.indexOf(blurMerName)>=0){
                		_text += "<li onclick='selectedMerId(this,"+merchInfo+")'>"+_aData.merList[i].memberName+"("+_aData.merList[i].memberId+")"+"</li>"
                	}
                }
                _text += "</ul></div>";
                _this.parent().append(_text);
                if(typeof fun == "function"){
                    $('.autocomplete li').unbind('click').click(function () {
                        setTimeout(function(){
                            fun(_this);
                        },200)
                    });
                }

            }else{
               showTip(_aData.respDesc);
            }
        },
        error: function (_aData) {
        	if(_aData.status == '405'){
       			window.location.reload();
       		}
			showTip("网络超时,请稍后重试");
        }
    });
}
//开关功能
function setMenu(e){
    var _this = e;
    var s = _this;
    var k = _this.find("a");
    var t = $(k).css("left") == "0px" && !_this.hasClass("off");
    var m = t ? -35 : 0;
    $(k).animate({
        left: m
    }, 200, function() {
        _this.find("input:checkbox").attr("checked", !t);
        t ? $(s).addClass("off").removeClass("on") : $(s).addClass("on").removeClass("off");
    })
}

/*
 * merId:主要是商户名称模糊匹配时，存它对应的memberId
 * merchInfo：主要是在组织架构维护当中使用到
 */
function selectedMerId(obj,merchInfo){//有效选中后的处理
	 $('#merId').val(obj.innerText.substring(obj.innerText.indexOf('(')+1,obj.innerText.indexOf(')')));
	 $('#merchInfo').val(merchInfo);
}

//动态控制层级模态框的隐藏和显示
function dealLevel(index){
	if(index == '0'){
		$('#C1').show();
		$('#C2').hide();
		$('#C3').hide();
		$('#C4').hide();
		$('#C5').hide();
		$('#C6').hide();
		$('#C7').hide();
		$('#C8').hide();
		$('#C9').hide();
		$('#C10').hide();
	}else if(index == '1'){
		$('#C1').show();
		$('#C2').show();
		$('#C3').hide();
		$('#C4').hide();
		$('#C5').hide();
		$('#C6').hide();
		$('#C7').hide();
		$('#C8').hide();
		$('#C9').hide();
		$('#C10').hide();
	}else if(index == '2'){
		$('#C1').show();
		$('#C2').show();
		$('#C3').show();
		$('#C4').hide();
		$('#C5').hide();
		$('#C6').hide();
		$('#C7').hide();
		$('#C8').hide();
		$('#C9').hide();
		$('#C10').hide();
	}else if(index == '3'){
		$('#C1').show();
		$('#C2').show();
		$('#C3').show();
		$('#C4').show();
		$('#C5').hide();
		$('#C6').hide();
		$('#C7').hide();
		$('#C8').hide();
		$('#C9').hide();
		$('#C10').hide();
	}else if(index == '4'){
		$('#C1').show();
		$('#C2').show();
		$('#C3').show();
		$('#C4').show();
		$('#C5').show();
		$('#C6').hide();
		$('#C7').hide();
		$('#C8').hide();
		$('#C9').hide();
		$('#C10').hide();
	}else if(index == '5'){
		$('#C1').show();
		$('#C2').show();
		$('#C3').show();
		$('#C4').show();
		$('#C5').show();
		$('#C6').show();
		$('#C7').hide();
		$('#C8').hide();
		$('#C9').hide();
		$('#C10').hide();
	}else if(index == '6'){
		$('#C1').show();
		$('#C2').show();
		$('#C3').show();
		$('#C4').show();
		$('#C5').show();
		$('#C6').show();
		$('#C7').show();
		$('#C8').hide();
		$('#C9').hide();
		$('#C10').hide();
	}else if(index == '7'){
		$('#C1').show();
		$('#C2').show();
		$('#C3').show();
		$('#C4').show();
		$('#C5').show();
		$('#C6').show();
		$('#C7').show();
		$('#C8').show();
		$('#C9').hide();
		$('#C10').hide();
	}else if(index == '8'){
		$('#C1').show();
		$('#C2').show();
		$('#C3').show();
		$('#C4').show();
		$('#C5').show();
		$('#C6').show();
		$('#C7').show();
		$('#C8').show();
		$('#C9').show();
		$('#C10').hide();
	}else if(index == '9'){
		$('#C1').show();
		$('#C2').show();
		$('#C3').show();
		$('#C4').show();
		$('#C5').show();
		$('#C6').show();
		$('#C7').show();
		$('#C8').show();
		$('#C9').show();
		$('#C10').show();
	}
}

/**
 * 默认获取其下默认的层级信息
 */
function getDefaultMerOptions(orgHeadId,orgLevelId,orgLevel,_url){

    $.ajax({
        type: "POST",
        url: _url,
        data:{
        	orgHeadId:orgHeadId,
        	orgLevelId:orgLevelId,
        	orgLevel:orgLevel
        },
        success: function (msg) {
        	 var msg = eval("(" + msg + ")");//转换为json对象
        	 if(msg.respCode=="000000"){
        		 var _data=msg.orgLevelInfoVo;
        		 var _aData = _data;//转换为json对象subOrgLevelInfoVos
        		 
        		 //商户列表
        		 getMerOptions(msg);
        	 }
        },
        error: function (_aData) {
        	if(_aData.status == '405'){
       			window.location.reload();
       		}
			showTip("网络超时,请稍后重试");
        }
    });
}

/*
 *层级与商户列表进行联动，需要进行联动，请在页面的select标签中添加id=memberId1,就可以直接使用 
 *_aData :后台返回的商户列表信息对象
 */
function getMerOptions(_aData){
	var optionStr = '';//拼接的option串
	var options = _aData.merList;
	for(var i in options){
		optionStr += "<option value='"+options[i].memberId+"'>" + options[i].memberName + "</option>";
	}
	$('#memberId1').html("<option value=''>请选择商户</option> " + optionStr);
}

/*
 *日期格式化方法
 *格式可以自定义
 *调用方式：new Date().Format('yyyy-MM-dd'); 
 */
Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


//标题记忆  2.0 S
/*  为了便于维护和使用，重写了标题筛选功能
1.在TitleEnum 上配置功能号
2.添加如下两个隐藏域
	<input type="hidden" th:value="${#strings.isEmpty(session.B1302)}?'B1302':${session.B1302}" id="newTitle">
	<input type="hidden" th:value="${baseContentUrl}" name="baseUrl">
2.每个页面添加隐藏域的时候，那个功能号需要和配置中的功能号一致
3.添加名称的baseUrl,是为了不会与设置的id产生冲突，也便于维护使用
4.添加该段代码即可
*/
var i = 0;
function newNeedScreen(){
	console.log(i);
	var title = $('#newTitle').val();//one1301,one 说明是首次，且数据未存入session，1301，对应功能号
    if($(".table.need-screen").length<=0) return;
    var _screenData = '<th width="30"><span class="icon-down"><span class="main-list"><span class="title"><input type="checkbox" id="selectAll"/> 全选</span><span class="list">查询信息<br />';
    $(".table.need-screen thead th").each(function(){
        if($(this).hasClass("disabled")){
            _screenData += '<span class="info" style="display:none"><input type="checkbox" checked disabled="disabled" /> '+$(this).text()+'</span>';
        }else{
        	if(title && title !='' && title.indexOf('|')==-1){/* title.indexOf('|')==-1 表明是首次加载 */
        		_screenData += '<span class="info"><input type="checkbox"  checked/> '+$(this).text()+'</span>';
        	}else{
        		if( title.indexOf($(this).text())!=-1){
	        		_screenData += '<span class="info"><input type="checkbox"  checked/> '+$(this).text()+'</span>';
	        	}else{
	        		_screenData += '<span class="info"><input type="checkbox"/> '+$(this).text()+'</span>';
	        	}
        	}
        }
    })
    _screenData += '</span></span></th>'
    $(".table.need-screen thead tr").prepend(_screenData);
    $(".table.need-screen tbody tr").prepend('<td>&nbsp;</td>');
//    if(title && title !='' && title.indexOf('|')==-1){
//		console.log($('#selectAll').prop('checked'));
//		$('#selectAll').prop('checked',true);
//		_screenData += '<span class="info"><input type="checkbox" checked/> '+$(this).text()+'</span>';
//	}
    
    //处理直接点击查询时，全选标志
	var flag = true;
	var index = 0;
	$(":checkbox").each(function(){
	    if(this.checked == false){
	    	if(index!=0){
	    		 flag = false;
	    	}
	    	index++;
	    }
	});
	if(flag && index == 1){
		$('#selectAll').prop('checked',true);
	}
			
	
    setNewTableDataShow('');//初始化，不选择某些字段
    //全选
    $(document).on("click",".main-list .title input:checkbox",function(){
        if ($(this).prop("checked")) {
            $(".main-list .list input").prop("checked", true)
        }else{
            $(".main-list .list input:not(:disabled)").prop("checked", false)
        }
        var title = $('#newTitle').val();
        var title1 = title.split("|");
        setNewTableDataShow(title1[0]);
    })
    //checkbox 
    $(document).on("click",".main-list .list input:checkbox",function(){
        if ($(this).prop("checked")) {
            if($(".main-list .list input:checked").length == $(".main-list .list input:checkbox").length){
                $(".main-list .title input:checkbox").prop("checked", true)
            }
        }else{
            $(".main-list .title input:checkbox").prop("checked", false)
        }
        var title = $('#newTitle').val();
        var title1 = title.split("|");
        setNewTableDataShow(title1[0]);
    })
}

function setNewTableDataShow(funcId){
	i++;
    $(".table.need-screen thead tr th").hide();
    $(".table.need-screen tbody tr td").hide();
    $(".table.need-screen thead tr th:eq(0)").show();
    $(".table.need-screen tbody tr").each(function(){
        $(this).find("td:eq(0)").show();
    })
    var newTitle = "";
    $(".main-list .list input:checkbox").each(function(e){
        if ($(this).prop("checked")) {
        	newTitle = newTitle + $(this).closest(".info").text().trim()+"|";
            var _index = $(this).closest(".info").index();
            $(".table.need-screen thead tr th:eq("+_index+")").show();
            $(".table.need-screen tbody tr").each(function(){
                $(this).find("td:eq("+_index+")").show();
            })
        }
    })
   if(funcId !='' && newTitle !=''){
	   var sessionId = $("#sessionId").val();
	   var url = $('#context').val() + '/common/dealTile?sessionId='+sessionId;
	   $.ajax({
		   type:'post',
		   url:url,
		   data: {"newTitle":newTitle,"funcId":funcId},
		   dataType:'json',
		   success:function(data){
			    console.log('success,标题记住了'+data.newTitle);
			    $('#newTitle').val(data.newTitle);
		   },
		   error:function(data){
			   console.log('error,标题未记住'+data.newTitle);
			   $('#newTitle').val(data.newTitle);
		   }
	   });
	   return false;
   }
}

/**
 * 下载按钮disable
 */
function exsTimer(selector, time) {
	var e = $(selector);
	if (e.length < 1) {
		e = $('.downloadCSV');
	}
	var btn_text = e.text();
	e.attr("disabled", true);
	e.html(time <= 0 ? btn_text : ("" + (time) + "秒后可操作"));
	var hander = setInterval(function() {
		if (time <= 0) {
			clearInterval(hander); //清除倒计时
			e.html(btn_text);
			e.attr("disabled", false);
			return false;
		} else {
			e.html("" + (time--) + "秒后可操作");
		}
	}, 1000);
}
//标题记忆结束 2.0 E


