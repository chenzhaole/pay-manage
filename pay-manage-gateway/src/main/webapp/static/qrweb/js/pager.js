/**
 * 构造函数
 * @constructor
 * @param parent{Element} 父级Element的ID，本分页菜单将会挂靠在该Element下
 * @param nextParent{Element} 往后查看Pager的父级Element的ID
 * @param reqURI(String) 请求地址
 * @param title(Object) 标题
 * @param totalPage{Number} 总页数
 * @param totalRecord{Number} 总条数
 * @param perPage{Number}(optional) 每页条数
 * @param toPage{Number} 到toPage页
 * @param toId{Number} 到toId条
 * 
 */

var Pager = function(option){
	if(typeof(arguments[0]) == 'undefined'){ return false; }
	var option = typeof(arguments[0]) == 'object' ? arguments[0] : {};
	this.parent = option.parent ? document.getElementById(option.parent) : 'document';
	this.parent = this.parent ? this.parent : document.body;
	this.nextParent = option.nextParent ? document.getElementById(option.nextParent) : 'document';
	this.nextParent = this.nextParent ? this.nextParent : document.body;
	this.reqURI = option.reqURI ? option.reqURI : '';
	this.title  = option.title ? option.title : {};
	
	this.totalPage = option.totalPage ? option.totalPage : 1;
	this.totalRecord = option.totalRecord ? option.totalRecord : 10;
	this.perPage = option.perPage ? option.perPage : 10;
	this.toPage = option.toPage ? option.toPage : 1;
	this.toId = option.toId ? option.toId : 10;
	this.autoLoadNextOffset = option.autoLoadNextOffset ? option.autoLoadNextOffset : 75;
	this.autoLoadNextAnchor = option.autoLoadNextAnchor ? document.getElementById(option.autoLoadNextAnchor) : null;
	
	this.locked = false;
	
	this.dragStart = 0;
	this.dragEnd = 0;
	
	this.userAgent = new UserAgent();
	
	this.init();
}

Pager.prototype = {
	init: function(){
		this.nextDom = document.createElement('DIV');
		this.nextDom.id = 'pager_next';
		this.nextDom.className = 'pager';
		
		this.nextMoreDom = document.createElement('A');
		this.nextMoreDom.className = 'next';
		this.nextMoreDom.href = 'javascript:;';
		this.nextMoreDom.innerHTML = this.title.nextMore;
		this.nextDom.appendChild(this.nextMoreDom);
		
		this.nextLoadingDom = document.createElement('A');
		this.nextLoadingDom.className = 'loading';
		this.nextLoadingDom.href = 'javascript:;';
		this.nextLoadingDom.innerHTML = this.title.nextLoading;
		this.nextDom.appendChild(this.nextLoadingDom);
		
		this.nextEndDom = document.createElement('A');
		this.nextEndDom.className = 'end';
		this.nextEndDom.href = 'javascript:;';
		this.nextEndDom.innerHTML = this.title.nextEnd;
		this.nextDom.appendChild(this.nextEndDom);
		
		this.view();
		
		this.nextParent.appendChild(this.nextDom);
		
		this.bind();
	},
	view: function(){
		this.nextDom.style.display = '';
		if(this.toPage>=this.totalPage){
			this.nextMoreDom.style.display = 'none';
			this.nextLoadingDom.style.display = 'none';
			this.nextEndDom.style.display = '';
		}else{
			this.nextMoreDom.style.display = '';
			this.nextLoadingDom.style.display = 'none';
			this.nextEndDom.style.display = 'none';
		}
	},
	turnPage: function(pageNum){
		if(this.locked)
			return;
		this.locked = true;
		var qid = -1;
		var order = -1;
		if(pageNum>this.toPage && pageNum<=this.totalPage){//next
			this.nextMoreDom.style.display = 'none';
			this.nextLoadingDom.style.display = '';
			this.nextEndDom.style.display = 'none';
			
			qid = this.toId+1;
			order = 1;
		}
		
		var reqData = {pn:pageNum,order:order,qId:qid};
		var _this = this;
		setTimeout(function(){
			$.ajax({
				url : _this.reqURI+"?pn="+pageNum+"&order="+order+"&qId="+qid,
				type : "post",
				data : reqData,
				cache : false,
				success : function(result) {
					if(pageNum>_this.toPage && pageNum<=_this.totalPage){//next
						
						var pageDom = document.createElement('DIV');
						pageDom.id = 'page_'+pageNum;
						pageDom.className = 'page';
						pageDom.setAttribute('name', 'page');
						pageDom.setAttribute('seq', pageNum);
						pageDom.innerHTML = result;
						_this.parent.appendChild(pageDom);
						
						_this.toPage = pageNum;
						_this.toId = _this.toPage*_this.perPage;
						_this.toId = _this.toId>_this.totalRecord ? _this.totalRecord :_this.toId;
						
						_this.view();
						_this.locked = false;
					}
				}
				/*complete : function(){
					_this.view();
					_this.locked = false;
				}*/
			});
		},500);
	},
	nextPage: function(){
		if(this.toPage >= this.totalPage){
			this.view();
			return;
		}
		this.turnPage(this.toPage+1);
	},
	bind: function(){
		var _this = this;
	
		if(this.userAgent.inputdevice.touch){
			
			this.nextMoreDom.ontouchstart = function(event){_this.nextPage();}
		
			this.parent.ontouchstart = function(event){
		 		var tPoint = event.targetTouches[0];
		 		_this.dragStart = _this.dragEnd = tPoint.pageY;
			}
			this.parent.ontouchmove = function(event){
				var tPoint = event.targetTouches[0];
				_this.dragEnd = tPoint.pageY ;
	           
	            var offset = _this.dragEnd - _this.dragStart;
	            //next
	            var nextOffset = 0;
	            if(_this.autoLoadNextAnchor){
	            	nextOffset = _this.autoLoadNextAnchor.getBoundingClientRect().top-_this.nextDom.getBoundingClientRect().top;
	            }else{
	            	nextOffset = document.body.clientHeight-_this.nextDom.getBoundingClientRect().top;
	            }
	            if(offset<0 && nextOffset>=_this.autoLoadNextOffset){
	            	_this.nextPage(); 
	            }
			}
			this.parent.ontouchend = function(){
				_this.dragStart = _this.dragEnd = 0;
			}
		}else{
			
			this.nextMoreDom.onclick = function(){ _this.nextPage(); }
		}
	}
}