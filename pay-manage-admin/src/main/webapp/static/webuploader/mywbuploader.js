var customUploader = function () {
    return {
        tUploader: {},

        picIdField:"",

        init: function init(customOptions) {
            var options = {

                // 选完文件后，是否自动上传。
                auto: true,

                compress : false,

                //是否允许在文件传输时提前把下一个文件准备好
                prepareNextFile: true,

                //上传数量限制
                fileNumLimit: 8,

                //每张图片大小（字节）
                fileSingleSizeLimit: (5 * 1024 * 1024),

                //去重
                duplicate: false,

                //缩略图

                thumb:{
                    width: 1,
                    height: 1,

                    // 图片质量，只有type为`image/jpeg`的时候才有效。
                    quality: 70,

                    // 是否允许放大，如果想要生成小图的时候不失真，此选项应该设置为false.
                    allowMagnify: false,

                    // 是否允许裁剪。
                    crop: false

                },

                // swf文件路径
                swf: '/webuploader/Uploader.swf',

                // 文件接收服务端。
                server: '/admin/picture/upload',

                // 选择文件的按钮。可选。
                // 内部根据当前运行是创建，可能是input元素，也可能是flash.
                pick: '.filePicker',

                // 只允许选择图片文件。
                accept: {
                    title: 'Images',
                    extensions: 'gif,jpg,jpeg,bmp,png',
                    mimeTypes: 'image/jpg,image/jpeg,image/png'
                }
            };

            var theOption = $.extend(options, customOptions);
            var uploader = WebUploader.create(theOption);

            customUploader.tUploader = uploader;

            if(theOption.picIdField){
                customUploader.picIdField = theOption.picIdField;
            }else{
                customUploader.picIdField = "picId";
            }



            // 加入到队列前
            uploader.on('beforeFileQueued', function (file) {
                var limitFileNum = uploader.option('fileNumLimit');
                var listSize = $("#fileList_" + window.id).children().length;
                // if(listSize < limitFileNum) return true;
                // $.toast({
                //     heading: '上传',
                //     text: '超出上传数量限制！当前上传限制为' + limitFileNum + '张',
                //     showHideTransition: 'fade',
                //     position: "top-right",
                //     icon: 'error'
                // })
                // return false;
            });
            // 上传前添加额外参数
            uploader.on('uploadBeforeSend', function (obj,data,headers) {
                data.location = theOption.location;
            });

            // 当有文件添加进来的时候
            uploader.on('fileQueued', function (file) {
                var $li = $(
                    '<div id="' + file.id + '" class="file-item thumbnail">' +
                    '<img>' +
                    '<div class="info">' + file.name + '</div>' +
                    '</div>'
                    ),
                    $img = $li.find('img');


                // $("#fileList")为容器jQuery实例
                $("#fileList_" + window.id).append($li);

                // 创建缩略图
                // 如果为非图片文件，可以不用调用此方法。
                // thumbnailWidth x thumbnailHeight 为 100 x 100
                uploader.makeThumb(file, function (error, src) {
                    if (error) {
                        $img.replaceWith('<span>不能预览</span>');
                        return;
                    }

                    $img.attr('src', src);

                    //删除按钮
                    var c_close_parent = $("<a>", {"href": "#", "class": "btn red icn-only"});
                    var c_close_btn = $("<i>", {"class": "icon-remove"});
                    c_close_parent.append(c_close_btn);
                    $li.append(c_close_parent);
                    c_close_parent.click(function () {
                        uploader.removeFile(file, true);
                        customUploader.setPicId("");
                        $li.remove();
                    });

                });
            });
            // 文件上传过程中创建进度条实时显示。
            uploader.on('uploadProgress', function (file, percentage) {
                var $li = $('#' + file.id);
//                    var $percent = $li.find('.progress span');
                var $percent = $("#tbar");

                // 避免重复创建
                if (!$percent.length) {
                    $('<div class="progress progress-striped progress-danger active"><div class="bar" id="tbar"></div></div>')
                        .appendTo($li);
                    $percent = $("#tbar");
                }

                $percent.css('width', percentage * 100 + '%');
//                    var $percent = $("#tprogress");
//                    if(!$percent.length){
//
//                        $percent = $("<span>",{"style":"background-color: #0b94ea;","id":"tprogress"}).html("&nbsp;");
//                        $li.append($percent);
//                    }
//                    $percent.css('width', percentage * 100 + '%');
            });

            // 文件上传成功，给item添加成功class, 用样式标记上传成功。
            uploader.on('uploadSuccess', function (file, response) {
                $('#' + file.id).addClass('upload-state-done');
                customUploader.setPicId(response.picURL);
            });

            // 文件接受，获取id
            uploader.on('uploadAccept', function (obj, res) {
                //将异步上传图片返回的图片ID赋值到隐藏域
                customUploader.setPicId(res.id);
            });

            // 文件上传失败，显示上传出错。
            uploader.on('uploadError', function (file) {
                var $li = $('#' + file.id),
                    $error = $li.find('div.error');

                // 避免重复创建
                if (!$error.length) {
                    $error = $('<div></div>').appendTo($li);
                }
                var errortext = $("<span>", {"style": "color:red;"}).text("上传失败");
                $error.append(errortext);
            });

            // 完成上传完了，成功或者失败，先删除进度条。
            uploader.on('uploadComplete', function (file) {
                $('#' + file.id).find('.progress').remove();
            });

            // 完成上传完了，成功或者失败，先删除进度条。
            uploader.on('error', function (type) {
                if (type == "Q_EXCEED_NUM_LIMIT") {
                    $.toast({
                        heading: '上传',
                        text: '超出上传数量限制！当前上传限制为' + uploader.option('fileNumLimit') + '张',
                        showHideTransition: 'fade',
                        position: "top-right",
                        icon: 'error'
                    })
                }
                if (type == "Q_TYPE_DENIED") {
                    $.toast({
                        heading: '上传',
                        text: '文件类型不符，可上传类型为：' + uploader.option('accept')[0].mimeTypes,
                        showHideTransition: 'fade',
                        position: "top-right",
                        icon: 'error'
                    })
                }
                if (type == "F_EXCEED_SIZE") {
                    $.toast({
                        heading: '上传',
                        text: '图片大小超出限制，限制为：' + (uploader.option('fileSingleSizeLimit') / 1024 / 1024) + "M",
                        showHideTransition: 'fade',
                        position: "top-right",
                        icon: 'error'
                    })
                }
            });
        },
        showImg:function (url,id) {
            if(!(url && id)) return;
            var $li = $(
                '<div id="' + "" + '" class="file-item thumbnail">' +
                '<img style="width: 100%;height:100%;">' +
                '<div class="info">' + "图片" + '</div>' +
                '</div>'
                ),
                $img = $li.find('img');


            // $("#fileList")为容器jQuery实例
            $("#fileList_" + window.id).append($li);
            $img.attr('src', url);
            //删除按钮
            var c_close_parent = $("<a>", {"href": "#", "class": "btn red icn-only"});
            var c_close_btn = $("<i>", {"class": "icon-remove icon-white"});
            c_close_parent.append(c_close_btn);
            $li.append(c_close_parent);
            c_close_parent.click(function () {
                customUploader.setPicId("");
                $li.remove();
            });
            //picId
            //将异步上传图片返回的图片ID赋值到隐藏域
            customUploader.setPicId(id);
            $li.addClass('upload-state-done');
        },
        removeAllFiles:function () {
            $("#fileList_" + window.id).children().remove();
            var allFiles = this.tUploader.getFiles();
            if(allFiles){
                for(fileIndex in allFiles ){
                    this.tUploader.removeFile(allFiles[fileIndex]);
                    customUploader.setPicId("");
                }
            }
        },
        setPicId:function (id) {
            $("#"+ window.id).val(id);
        }
    }
}();