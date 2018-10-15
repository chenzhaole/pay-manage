<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>测试Demo</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="css/index.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0 topMargin=4>
<div id="main">
    <div class="cashier-nav">
        <ol>
            <li class="current">密钥对</li>
        </ol>
    </div>
    <form action="rsa" method="post" target="_blank">
        <div id="body" style="clear:left">
            <dl class="">
                    私钥
                <dd>
                    <textarea rows="15" cols="60">${privateKey}</textarea>
                </dd>
            </dl>
            <dl class="">
                    公钥
                <dd>
                    <textarea rows="8" cols="40" >${publicKey}</textarea>
                </dd>
            </dl>
            <dl class="">
                <dd>
                    <span class="new-btn-login-sp">
                        <button class="new-btn-login" type="submit"
                                style="text-align:center;">生成</button>
                    </span>
                </dd>
            </dl>

        </div>
    </form>
</div>
</body>
</html>