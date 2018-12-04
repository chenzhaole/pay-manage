;
function upImageInit($obj) {
  var $UpBox = $obj.empty().append('<div id="upBox" style="width:100%;height:100%;position:relative;"></div>').find('#upBox');
  
  creatUpTools($UpBox);
};

(function() {
  var $, Paste, createHiddenEditable, dataURLtoBlob, isFocusable;

  $ = window.jQuery;

  $.paste = function(pasteContainer) {
    var pm;
    if (typeof console !== "undefined" && console !== null) {
      console.log("DEPRECATED: This method is deprecated. Please use $.fn.pastableNonInputable() instead.");
    }
    pm = Paste.mountNonInputable(pasteContainer);
    return pm._container;
  };

  $.fn.pastableNonInputable = function() {
    var el, j, len, ref;
    ref = this;
    for (j = 0, len = ref.length; j < len; j++) {
      el = ref[j];
      if (el._pastable || $(el).is('textarea, input:text, [contenteditable]')) {
        continue;
      }
      Paste.mountNonInputable(el);
      el._pastable = true;
    }
    return this;
  };

  $.fn.pastableTextarea = function() {
    var el, j, len, ref;
    ref = this;
    for (j = 0, len = ref.length; j < len; j++) {
      el = ref[j];
      if (el._pastable || $(el).is(':not(textarea, input:text)')) {
        continue;
      }
      Paste.mountTextarea(el);
      el._pastable = true;
    }
    return this;
  };

  $.fn.pastableContenteditable = function() {
    var el, j, len, ref;
    ref = this;
    for (j = 0, len = ref.length; j < len; j++) {
      el = ref[j];
      if (el._pastable || $(el).is(':not([contenteditable])')) {
        continue;
      }
      Paste.mountContenteditable(el);
      el._pastable = true;
    }
    return this;
  };

  dataURLtoBlob = function(dataURL, sliceSize) {
    var b64Data, byteArray, byteArrays, byteCharacters, byteNumbers, contentType, i, m, offset, ref, slice;
    if (sliceSize == null) {
      sliceSize = 512;
    }
    if (!(m = dataURL.match(/^data\:([^\;]+)\;base64\,(.+)$/))) {
      return null;
    }
    ref = m, m = ref[0], contentType = ref[1], b64Data = ref[2];
    byteCharacters = atob(b64Data);
    byteArrays = [];
    offset = 0;
    while (offset < byteCharacters.length) {
      slice = byteCharacters.slice(offset, offset + sliceSize);
      byteNumbers = new Array(slice.length);
      i = 0;
      while (i < slice.length) {
        byteNumbers[i] = slice.charCodeAt(i);
        i++;
      }
      byteArray = new Uint8Array(byteNumbers);
      byteArrays.push(byteArray);
      offset += sliceSize;
    }
    return new Blob(byteArrays, {
      type: contentType
    });
  };

  createHiddenEditable = function() {
    return $(document.createElement('div')).attr('contenteditable', true).attr('aria-hidden', true).attr('tabindex', -1).css({
      width: 1,
      height: 1,
      position: 'fixed',
      left: -100,
      overflow: 'hidden',
      opacity: 1e-17
    });
  };

  isFocusable = function(element, hasTabindex) {
    var fieldset, focusableIfVisible, img, map, mapName, nodeName;
    map = void 0;
    mapName = void 0;
    img = void 0;
    focusableIfVisible = void 0;
    fieldset = void 0;
    nodeName = element.nodeName.toLowerCase();
    if ('area' === nodeName) {
      map = element.parentNode;
      mapName = map.name;
      if (!element.href || !mapName || map.nodeName.toLowerCase() !== 'map') {
        return false;
      }
      img = $('img[usemap=\'#' + mapName + '\']');
      return img.length > 0 && img.is(':visible');
    }
    if (/^(input|select|textarea|button|object)$/.test(nodeName)) {
      focusableIfVisible = !element.disabled;
      if (focusableIfVisible) {
        fieldset = $(element).closest('fieldset')[0];
        if (fieldset) {
          focusableIfVisible = !fieldset.disabled;
        }
      }
    } else if ('a' === nodeName) {
      focusableIfVisible = element.href || hasTabindex;
    } else {
      focusableIfVisible = hasTabindex;
    }
    focusableIfVisible = focusableIfVisible || $(element).is('[contenteditable]');
    return focusableIfVisible && $(element).is(':visible');
  };

  Paste = (function() {
    Paste.prototype._target = null;

    Paste.prototype._container = null;

    Paste.mountNonInputable = function(nonInputable) {
      var paste;
      paste = new Paste(createHiddenEditable().appendTo(nonInputable), nonInputable);
      $(nonInputable).on('click', (function(_this) {
        return function(ev) {
          if (!(isFocusable(ev.target, false) || window.getSelection().toString())) {
            return paste._container.focus();
          }
        };
      })(this));
      paste._container.on('focus', (function(_this) {
        return function() {
          return $(nonInputable).addClass('pastable-focus');
        };
      })(this));
      return paste._container.on('blur', (function(_this) {
        return function() {
          return $(nonInputable).removeClass('pastable-focus');
        };
      })(this));
    };

    Paste.mountTextarea = function(textarea) {
      var ctlDown, paste, ref, ref1;
      if ((typeof DataTransfer !== "undefined" && DataTransfer !== null ? DataTransfer.prototype : void 0) && ((ref = Object.getOwnPropertyDescriptor) != null ? (ref1 = ref.call(Object, DataTransfer.prototype, 'items')) != null ? ref1.get : void 0 : void 0)) {
        return this.mountContenteditable(textarea);
      }
      paste = new Paste(createHiddenEditable().insertBefore(textarea), textarea);
      ctlDown = false;
      $(textarea).on('keyup', function(ev) {
        var ref2;
        if ((ref2 = ev.keyCode) === 17 || ref2 === 224) {
          ctlDown = false;
        }
        return null;
      });
      $(textarea).on('keydown', function(ev) {
        var ref2;
        if ((ref2 = ev.keyCode) === 17 || ref2 === 224) {
          ctlDown = true;
        }
        if ((ev.ctrlKey != null) && (ev.metaKey != null)) {
          ctlDown = ev.ctrlKey || ev.metaKey;
        }
        if (ctlDown && ev.keyCode === 86) {
          paste._textarea_focus_stolen = true;
          paste._container.focus();
          paste._paste_event_fired = false;
          setTimeout((function(_this) {
            return function() {
              if (!paste._paste_event_fired) {
                $(textarea).focus();
                return paste._textarea_focus_stolen = false;
              }
            };
          })(this), 1);
        }
        return null;
      });
      $(textarea).on('paste', (function(_this) {
        return function() {};
      })(this));
      $(textarea).on('focus', (function(_this) {
        return function() {
          if (!paste._textarea_focus_stolen) {
            return $(textarea).addClass('pastable-focus');
          }
        };
      })(this));
      $(textarea).on('blur', (function(_this) {
        return function() {
          if (!paste._textarea_focus_stolen) {
            return $(textarea).removeClass('pastable-focus');
          }
        };
      })(this));
      $(paste._target).on('_pasteCheckContainerDone', (function(_this) {
        return function() {
          $(textarea).focus();
          return paste._textarea_focus_stolen = false;
        };
      })(this));
      return $(paste._target).on('pasteText', (function(_this) {
        return function(ev, data) {
          var content, curEnd, curStart;
          curStart = $(textarea).prop('selectionStart');
          curEnd = $(textarea).prop('selectionEnd');
          content = $(textarea).val();
          $(textarea).val("" + content.slice(0, curStart) + data.text + content.slice(curEnd));
          $(textarea)[0].setSelectionRange(curStart + data.text.length, curStart + data.text.length);
          return $(textarea).trigger('change');
        };
      })(this));
    };

    Paste.mountContenteditable = function(contenteditable) {
      var paste;
      paste = new Paste(contenteditable, contenteditable);
      $(contenteditable).on('focus', (function(_this) {
        return function() {
          return $(contenteditable).addClass('pastable-focus');
        };
      })(this));
      return $(contenteditable).on('blur', (function(_this) {
        return function() {
          return $(contenteditable).removeClass('pastable-focus');
        };
      })(this));
    };

    function Paste(_container, _target) {
      this._container = _container;
      this._target = _target;
      this._container = $(this._container);
      this._target = $(this._target).addClass('pastable');
      this._container.on('paste', (function(_this) {
        return function(ev) {
          var _i, clipboardData, file, fileType, item, j, k, l, len, len1, len2, pastedFilename, reader, ref, ref1, ref2, ref3, ref4, stringIsFilename, text;
          _this.originalEvent = (ev.originalEvent !== null ? ev.originalEvent : null);
          _this._paste_event_fired = true;
          if (((ref = ev.originalEvent) != null ? ref.clipboardData : void 0) != null) {
            clipboardData = ev.originalEvent.clipboardData;
            if (clipboardData.items) {
              pastedFilename = null;
              _this.originalEvent.pastedTypes = [];
              ref1 = clipboardData.items;
              for (j = 0, len = ref1.length; j < len; j++) {
                item = ref1[j];
                if (item.type.match(/^text\/(plain|rtf|html)/)) {
                  _this.originalEvent.pastedTypes.push(item.type);
                }
              }
              ref2 = clipboardData.items;
              for (_i = k = 0, len1 = ref2.length; k < len1; _i = ++k) {
                item = ref2[_i];
                if (item.type.match(/^image\//)) {
                  reader = new FileReader();
                  reader.onload = function(event) {
                    return _this._handleImage(event.target.result, _this.originalEvent, pastedFilename);
                  };
                  try {
                    reader.readAsDataURL(item.getAsFile());
                  } catch (error) {}
                  ev.preventDefault();
                  break;
                }
                if (item.type === 'text/plain') {
                  if (_i === 0 && clipboardData.items.length > 1 && clipboardData.items[1].type.match(/^image\//)) {
                    stringIsFilename = true;
                    fileType = clipboardData.items[1].type;
                  }
                  item.getAsString(function(string) {
                    if (stringIsFilename) {
                      pastedFilename = string;
                      return _this._target.trigger('pasteText', {
                        text: string,
                        isFilename: true,
                        fileType: fileType,
                        originalEvent: _this.originalEvent
                      });
                    } else {
                      return _this._target.trigger('pasteText', {
                        text: string,
                        originalEvent: _this.originalEvent
                      });
                    }
                  });
                }
                if (item.type === 'text/rtf') {
                  item.getAsString(function(string) {
                    return _this._target.trigger('pasteTextRich', {
                      text: string,
                      originalEvent: _this.originalEvent
                    });
                  });
                }
                if (item.type === 'text/html') {
                  item.getAsString(function(string) {
                    return _this._target.trigger('pasteTextHtml', {
                      text: string,
                      originalEvent: _this.originalEvent
                    });
                  });
                }
              }
            } else {
              if (-1 !== Array.prototype.indexOf.call(clipboardData.types, 'text/plain')) {
                text = clipboardData.getData('Text');
                setTimeout(function() {
                  return _this._target.trigger('pasteText', {
                    text: text,
                    originalEvent: _this.originalEvent
                  });
                }, 1);
              }
              _this._checkImagesInContainer(function(src) {
                return _this._handleImage(src, _this.originalEvent);
              });
            }
          }
          if (clipboardData = window.clipboardData) {
            if ((ref3 = (text = clipboardData.getData('Text'))) != null ? ref3.length : void 0) {
              setTimeout(function() {
                _this._target.trigger('pasteText', {
                  text: text,
                  originalEvent: _this.originalEvent
                });
                return _this._target.trigger('_pasteCheckContainerDone');
              }, 1);
            } else {
              ref4 = clipboardData.files;
              for (l = 0, len2 = ref4.length; l < len2; l++) {
                file = ref4[l];
                _this._handleImage(URL.createObjectURL(file), _this.originalEvent);
              }
              _this._checkImagesInContainer(function(src) {});
            }
          }
          return null;
        };
      })(this));
    }

    Paste.prototype._handleImage = function(src, e, name) {
      var loader;
      if (src.match(/^webkit\-fake\-url\:\/\//)) {
        return this._target.trigger('pasteImageError', {
          message: "You are trying to paste an image in Safari, however we are unable to retieve its data."
        });
      }
      this._target.trigger('pasteImageStart');
      loader = new Image();
      loader.crossOrigin = "anonymous";
      loader.onload = (function(_this) {
        return function() {
          var blob, canvas, ctx, dataURL;
          canvas = document.createElement('canvas');
          canvas.width = loader.width;
          canvas.height = loader.height;
          ctx = canvas.getContext('2d');
          ctx.drawImage(loader, 0, 0, canvas.width, canvas.height);
          dataURL = null;
          try {
            dataURL = canvas.toDataURL('image/png');
            blob = dataURLtoBlob(dataURL);
          } catch (error) {}
          if (dataURL) {
            _this._target.trigger('pasteImage', {
              blob: blob,
              dataURL: dataURL,
              width: loader.width,
              height: loader.height,
              originalEvent: e,
              name: name
            });
          }
          return _this._target.trigger('pasteImageEnd');
        };
      })(this);
      loader.onerror = (function(_this) {
        return function() {
          _this._target.trigger('pasteImageError', {
            message: "Failed to get image from: " + src,
            url: src
          });
          return _this._target.trigger('pasteImageEnd');
        };
      })(this);
      return loader.src = src;
    };

    Paste.prototype._checkImagesInContainer = function(cb) {
      var img, j, len, ref, timespan;
      timespan = Math.floor(1000 * Math.random());
      ref = this._container.find('img');
      for (j = 0, len = ref.length; j < len; j++) {
        img = ref[j];
        img["_paste_marked_" + timespan] = true;
      }
      return setTimeout((function(_this) {
        return function() {
          var k, len1, ref1;
          ref1 = _this._container.find('img');
          for (k = 0, len1 = ref1.length; k < len1; k++) {
            img = ref1[k];
            if (!img["_paste_marked_" + timespan]) {
              cb(img.src);
              $(img).remove();
            }
          }
          return _this._target.trigger('_pasteCheckContainerDone');
        };
      })(this), 1);
    };

    return Paste;

  })();

}).call(this);

function checkIEVersion(){
  var userAgent = navigator.userAgent;
  console.log('浏览器信息:\n'+userAgent);
  var lessIE11 = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1;
  var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1;

  if(lessIE11) {
    var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
    reIE.test(userAgent);
    var fIEVersion = parseFloat(RegExp["$1"]);
    return fIEVersion;
  }else if(isIE11) {
    return 11;
  }else{
    return -1;
  };
};

function creatUpTools($obj) {

  var tipImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAqCAMAAAAOCBKjAAAAM1BMVEUAAADNzc3Nzc3Nzc3Nzc3Pz8/Pz8/Nzc3Nzc3Nzc3Nzc3Pz8/Pz8/MzMzOzs7MzMzNzc0b0d5XAAAAEHRSTlMAwIDwYDAQ0KDgkEAgr09fInJA9AAAALxJREFUOMvNk9sSgjAMBSlpegHE/P/XKqgcp3LazvjCPu/QTWiHb2IaWiQz31C8PdGqorYhoaIEsZ1YcaK9SZXeA897gdJeIIH2gsh7QeK9wPNeoKSXdqOXd09ux4B78btKKDIA5rgOZ+pwxks40uE4zP6XMyKIOiuchTkKZ+aDdfyM2+Fk6tzJZSaH5cBfc3tFi0CKga4RiOejgTzpmRPKN+3SuMGOA7n83uxK4yxMk2A8NJUs61bh588CHkosHl5Vw+pyAAAAAElFTkSuQmCC";
  var ieVersion = checkIEVersion();

  if (ieVersion < 10 && ieVersion > 0){
    $obj.empty().append('<div style="height:25%;"></div><p style="font-size:14px;color:#888;text-align:center;line-height:1.5;padding:0;margin:0;">您的浏览版本太低，无法使用图片上传功能<br>建议升级您的浏览器版本或者使用其他浏览器<br>推荐使用<a href="https://www.baidu.com/s?word=谷歌浏览器" target="_blank" title="谷歌浏览器下载" style="margin:0 5px;color:#777;">谷歌浏览器</a>或<a href="https://www.baidu.com/s?word=火狐浏览器" target="_blank" title="火狐浏览器下载" style="margin:0 5px;color:#777;">火狐浏览器</a>、<a href="https://www.baidu.com/s?word=360浏览器" target="_blank" title="360浏览器下载" style="color:#777;">360浏览器</a></p>')
  }else if(ieVersion == 10) {
    $obj.empty().append('<div style="-ms-box-sizing:border-box;-moz-box-sizing:border-box;-o-box-sizing:border-box;-webkit-box-sizing:border-box;box-sizing:border-box;width:104px;font-size:0;padding:4px;background-color:#5f94e4;box-shadow:0 0 3px #51a9a5;position:absolute;top:10px;left:50%;z-index:10;-ms-transform: translateX(-50%);-o-transform: translateX(-50%);-moz-transform: translateX(-50%);-webkit-transform: translateX(-50%);transform: translateX(-50%);"><div style="display:inline-block;vertical-align:middle;width:95px;height:30px;line-height:30px;text-align:center;font-size:14px;color:#fff;background-color:#00b7ee;border-radius:5px;cursor:pointer;position:relative;">点击上传图片<input id="upImagefile" type="file" accept="image/jpg, image/jpeg, image/png" value="上传图片" title="点击上传图片文件" style="width:100%;height:100%;cursor:pointer;position:absolute;top:0;left:0;opacity:0;"></div><div style="width:160px;font-size:13px;color:#aaa;background:url('+ tipImage +') no-repeat;background-size:auto 18px;padding:8px 0 0 0;position:absolute;left:6px;top:40px;z-index:9;"><span style="padding-left:15px;">图片大小不超过500K</span><br><span style="padding-left:15px;">支持JPG,JPEG,PNG格式</span><br><span style="display:block;margin-top:6px;">温馨提示：<br>您的浏览器版本太低<br>升级或使用<a href="https://www.baidu.com/s?word=谷歌浏览器" target="_blank" title="谷歌浏览器下载" style="color:#777;">谷歌浏览器</a><br>可使用图片快捷复制功能<span></div></div>');

    creatUptool();
  }else{
    $obj.empty().append('<div style="-ms-box-sizing:border-box;-moz-box-sizing:border-box;-o-box-sizing:border-box;-webkit-box-sizing:border-box;box-sizing:border-box;width:295px;font-size:0;padding:4px;background-color:#5f94e4;box-shadow:0 0 3px #51a9a5;position:absolute;top:10px;left:50%;z-index:10;-ms-transform: translateX(-50%);-o-transform: translateX(-50%);-moz-transform: translateX(-50%);-webkit-transform: translateX(-50%);transform: translateX(-50%);"><div style="display:inline-block;vertical-align:middle;width:150px;height:30px;line-height:30px;font-size:12px;text-align:center;color:#3b4380;background-color:#f6f6f6;position:relative;">点此后右键粘贴或Ctrl+V<div id="contenteditableDiv" contenteditable style="-ms-box-sizing:border-box;-moz-box-sizing:border-box;-o-box-sizing:border-box;-webkit-box-sizing:border-box;box-sizing:border-box;width:100%;height:100%;line-height:1.5;font-size:0;text-align:left;color:#000;padding:3px;border:2px dotted #999;outline:none;position:absolute;top:0;left:0;"></div></div><span style="display:inline-block;vertical-align:middle;font-size:18px;color:#fdfdfd;margin:0 10px;">或</span><div style="display:inline-block;vertical-align:middle;width:95px;height:30px;line-height:30px;text-align:center;font-size:14px;color:#fff;background-color:#00b7ee;border-radius:5px;cursor:pointer;position:relative;">点击上传图片<input id="upImagefile" type="file" accept="image/jpg, image/jpeg, image/png" value="上传图片" title="点击上传图片文件" style="width:100%;height:100%;cursor:pointer;position:absolute;top:0;left:0;opacity:0;"></div><div style="width:240px;font-size:13px;color:#aaa;background:url('+ tipImage +') no-repeat;background-size:auto 25px;padding:15px 0 0 20px;position:absolute;left:15px;top:40px;z-index:9;">将截图、聊天图片复制后，到此处粘贴<br>如果粘贴失败或粘贴按钮不可用<br>可能是剪贴板没有图片信息<br>或浏览器不支持此方式<br>请改用右侧"上传图片"方式上传图片文件<br>图片大小不超过500K,支持JPG,JPEG,PNG格式</div></div>');

    creatPastetool();
    creatUptool();
  };
};

function creatPastetool() {
  $('#contenteditableDiv').pastableContenteditable();
  $('#contenteditableDiv').focus(function(){
    $('#contenteditableDiv').css({"border-style":"solid"});
  }).blur(function(){
    $('#contenteditableDiv').css({"border-style":"dotted"});
  });
  $('#contenteditableDiv').on('pasteImage',function(ev,data){
    var imageInfo = {url: data.dataURL,width: data.width,height: data.height};
    showImage(imageInfo);
  }).on('pasteImageError',function(ev, data){
    upToolTips('图片粘贴失败，请重新复制或改用文件上传方式');
    console.log('pasteImageError:\n'+ data);
  }).on('pasteText',function(){
    $('#contenteditableDiv').empty();
    upToolTips('剪切板没有图片信息，请使用截图或复制图片后再试');
  }).on('pasteTextRich',function(){
    $('#contenteditableDiv').empty();
    upToolTips('剪切板没有图片信息，请使用截图或复制图片后再试');
  });
};

function creatUptool(){
  $('#upImagefile').change(function(el){
    if(!$('#upImagefile')[0].files){
      upToolTips('您的浏览器版本太低，无法上传文件，请升级或更换浏览器后再试');
    }else{
      var file = $('#upImagefile')[0].files[0];

      if(!/\/(jpg|jpeg|png|JPG|JPEG|PNG)$/.test(file.type)){
        upToolTips('图片类型必须是[jpeg,jpg,png]中的一种');
      }else{
        if (file.size > 500*1024) {
          upToolTips('图片不能大于500KB！');
        }else{
          var reader = new FileReader();
          reader.readAsDataURL(file);
          reader.onload=function(e){
            showImage({url: e.target.result})
          };
          reader.onerror=function(e){
            upToolTips('图片上传失败');
          };
          reader.onabort=function(e){
            upToolTips('图片上传中断')
          };
        };
      };
    };
  });
};

function showImage(data) {
  $('#upBox').empty().append('<img id="preview" src="'+ data.url +'" style="display:block;width:auto;height:100%;max-width:100%;max-height:100%;margin:0 auto;"><div id="deleteImage" style="width:40px;height:40px;background-color:rgba(0,0,0,0.5);border-radius:50%;cursor:pointer;opacity:0.3;position:absolute;top:50%;left:50%;-ms-transform:translate(-50%,-50%);-o-transform:translate(-50%,-50%);-moz-transform:translate(-50%,-50%);-webkit-transform:translate(-50%,-50%);transform:translate(-50%,-50%);" title="删除此图片"><i style="display:block;width:24px;height:24px;background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAMAAABg3Am1AAAAOVBMVEUAAAASltsRltsQj98QltsQl98RltsSltoQldwRldoQldoRltoSltsRldsRldsSldoQl9sQldoSltvdLNgVAAAAEnRSTlMAgMAQcCDwoE/QYN+P4LCfQDD5hr9VAAAAyklEQVRIx+2WSw6EIAxAW0v5iT/uf9hpIpOoQBNXMwveRgWfgbS2wAP2eMMzqGCuQFAwuYFRhLklzIog065eIylCteRdhvbbCOEFmZ3wxiZD2+WZIORXBLDvBAvxlbHE63b1eJbbIQBwMmf8U6IzfxOrgqRrBMGVLDQSW1UoF5gkqb5rGcIQhvA3wvT4p6OIqsBYqoYxpWog/7CQub7gaiFIcyHoQFb23mjpwTcV8nOjvdOSVZbqW4dVe+EBFbz2318ZCp2jj3IE+gDIFjICwrqy6gAAAABJRU5ErkJggg==) no-repeat;background-size:100%;margin:8px auto;"></i></div>');

  $('#upBox #deleteImage').hover(function(){
    $('#upBox #deleteImage').css("opacity","1");
  },function(){
    $('#upBox #deleteImage').css("opacity","0.3");
  }).click(function(){
    creatUpTools($('#upBox'));
  });
};

function upToolTips(txt) {
  $('#upBox').append('<div id="toolTips" style="width:100%;height:100%;background-color:rgba(0,0,0,.5);position:absolute;top:0;left:0;z-index:100;"><div style="width:184px;background-color:#fff;border-radius:5px;padding:8px;position:absolute;top:50%;left:50%;-ms-transform:translate(-50%,-50%);-o-transform:translate(-50%,-50%);-moz-transform:translate(-50%,-50%);-webkit-transform:translate(-50%,-50%);transform:translate(-50%,-50%);"><div id="tips_body" style="text-align:center;font-size:15px;color:#454545;">'+ txt +'</div><div id="tips_ok" style="width:80px;height:26px;line-height:25px;font-size:16px;text-align:center;color:#fff;letter-spacing:5px;background:#20A0FF;border-radius:5px;margin:10px auto 0;cursor:pointer;">确定</div></div><div id="close_tips" style="width:20px;height:20px;line-height:17px;font-size:25px;text-align:center;color:#FFF;background-color:#20A0FF;border-radius:50%;position:absolute;top:5px;right:8px;cursor:pointer;">×</div></div>');
  $('#close_tips').click(closeTips);
  $('#tips_ok').click(closeTips);
  function closeTips(){
    $('#toolTips').remove();
  };
};

