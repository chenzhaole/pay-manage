/*
 * Matrix v3.0.0 2017 ChinaPnR.com
 * MIT License: http://ued.chinapnr.com/matrix/license.txt
 */
$(function(){
  $(document).on('click','.system-bar > ul > li > a',function(){
    var $body = $('body');
    var $systemBar = $('.system-bar').eq(0);
    var $mainBar = $('.main-bar').eq(1);
    // 记录当前系统
    if( $('.system-bar').data('currentSys') === undefined ){
      var idx = $('.system-bar > ul > .active').index();
      $('.system-bar').data('currentSys',idx);
    }
    if($body.hasClass('sidebar-open') && $(this).parent().index()===0){
      $body.removeClass('sidebar-open');
      // 还原当前系统 active 标记
      $('.system-bar > ul > li').eq($('.system-bar').data('currentSys')).addClass('active').siblings().removeClass('active');
    }else{
      $body.addClass('sidebar-open');
    }
  })
  $(document).on('click','.nav-sidebar >li > a',function(){
    // 点击打开二级菜单
    var $root = $(this).closest('.sidebar');
    var $li = $(this).parent();
    if($li.index()===0 && !$root.hasClass('main-bar')) return;
    if($li.hasClass('active')){
      $li.removeClass('active');
    }else{
      $li.addClass('active').siblings().removeClass('active');
    }

  })
  /*** 设置默认打开的二级菜单 ***/
  $('ul.nav > .active').each(function(){
    $(this).parent().closest('li').addClass('active');
  })
  /*** 设置二级菜单高度，css动画需要高度的绝对值 ***/
  var styleTxt = '\n';
  $('ul.nav ul.nav').each(function(){
    var liLength = $(this).find('li').length;
    var $root = $(this).closest('.sidebar');
    var idx = $(this).parent().index()+1;
    if($root.hasClass('main-bar')){
      styleTxt += '.main-bar li:nth-child('+idx+').active ul.nav{height:'+liLength*40+'px}\n';
      $(this).closest('ul').prev().append('<b>›</b>')
    }else{
      styleTxt += '.sidebar-open .system-bar li:nth-child('+idx+').active ul.nav{height:'+liLength*40+'px}\n';
    }
  })
  $("<style></style>").text(styleTxt).appendTo($("head"));
  /*** 初始 最左侧菜单 的 tooltip ***/
  $('.system-bar [data-toggle="tooltip"]').tooltip({'container': 'body'});
})