/**
 * 构造函数
 * @constructor
 * 
 */
var UserAgent = function(){
	this.browser={};
	this.OS={};
	this.inputdevice={};
	
	this.ua = navigator.userAgent.toLowerCase();
	var browserMatch = this.uaMatch(this.ua);
	if ( browserMatch.browser ) {
		this.browser[ browserMatch.browser ] = true;
		this.browser.version = browserMatch.version;
		try{
			this.browser.version = parseFloat(browserMatch.version);
		}catch(e){}
	}
	if ( this.browser.webkit ) {
		this.browser.safari = true;
	}
	
	this.OS.windows = this.ua.match(/windows/i);
	this.OS.mac = this.ua.match(/Macintosh/i);
	this.OS.iOS = this.ua.match(/ipad|iphone|ipod|itouch/i);
	this.OS.android = this.ua.match(/android/i);
	
	this.inputdevice.keyboard = (this.OS.windows||this.OS.mac)?true:false; 
	this.inputdevice.mouse = (this.OS.windows||this.OS.mac)?true:false; 
	this.inputdevice.touch = (this.OS.iOS||this.OS.android)?true:false; 
}
UserAgent.prototype = {
	uaMatch: function( ua ) {
		// Useragent RegExp
		var rwebkit = /(webkit)[ \/]([\w.]+)/;
		var ropera = /(opera)(?:.*version)?[ \/]([\w.]+)/;
		var rmsie = /(msie) ([\w.]+)/;
		var rmozilla = /(mozilla)(?:.*? rv:([\w.]+))?/;
	
		ua = ua.toLowerCase();
		var match = rwebkit.exec( ua ) ||
					ropera.exec( ua ) ||
					rmsie.exec( ua ) ||
					ua.indexOf("compatible") < 0 && rmozilla.exec( ua ) || [];
		return { browser: match[1] || "", version: match[2] || "0" };
	}
}