if($(".remChoose").val()==1){
	(function(){
		function w(){
			var r = document.documentElement;
			var a = r.getBoundingClientRect().width;
				var rem = a /3.75;
				r.style.fontSize = rem + "px";
		}
		var t;
		w();
		$(".rows").css("visibility","visible");
		window.addEventListener("resize",function(){
			clearTimeout(t);
			t = setTimeout(w,300)
		},false);
	})();
}else{
	(function(){
		function w(){
			var r = document.documentElement;
			var a = r.getBoundingClientRect().width;
				var rem = a /7.2;
				r.style.fontSize = rem + "px";
		}
		var t;
		w();
        $(".rows").css("visibility","visible");
		window.addEventListener("resize",function(){
			clearTimeout(t);
			t = setTimeout(w,300)
		},false);
	})();
}
