<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>3</title>

    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=0">

    <link href="/static/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">

    <link href="/static/modules/sys/login/css/matrix.css?version=v1" rel="stylesheet">
    <link href="/static/modules/sys/login/css/login.css?version=v1" rel="stylesheet">
    <link href="/static/modules/sys/login/css/drag.css?version=v1" rel="stylesheet">


</head>


<body style="margin: 0px; overflow-x: hidden; min-width: 100%;">

<div class="login-wrap">


    <div class="login-header">
        <img src="/static/modules/sys/login/img/logo_top_9b.png" alt="" height="30">
        <div class="login-header">
            <img src="/static/modules/sys/login/img/logo_top_9b.png" alt="" height="30">
            <div class="phone-wrap">
                <img src="/static/modules/sys/login/img/tel.png">联系我们
                <span class="code">
                    请联系商务
                    <%--<img src="img/code.png" alt="">--%>
                </span>
            </div>
        </div>
    </div>

    <div class="banner">

        <div class="b-main">

            <div class="main-w1200">
                <div class="login-box">
                    <form id="form1" action="${ctx}/login" method="post">
                        <input name="sessionId" type="hidden" id="sessionId">
                        <input name="bizSwitch0001" type="hidden" id="bizSwitch0001" value="0">
                        <input name="bizSwitch0002" type="hidden" id="bizSwitch0002" value="0">
                        <div class="gradient-border login-user error">
                            <div class="login-item">
                                <label class="login-icon"></label>
                                <div class="sep-line"></div>
                                <input name="username" id="username" class="userName" type="text" placeholder="请输入您的账号"
                                       required="required" oninvalid="setCustomValidity('请输入合法的账号');"
                                       oninput="setCustomValidity('');" pattern="[a-zA-Z0-9_]{6,32}" maxlength="32"
                                       autocomplete="off">
                            </div>
                        </div>
                        <div class="gradient-border login-key">

                            <div class="login-item">
                                <label class="login-icon"></label>
                                <div class="sep-line"></div>
                                <input type="password" id="password" name="password" class="userPassword"
                                       placeholder="请输入您的密码" required="required"
                                       oninvalid="setCustomValidity('请正确输入您的密码');"
                                       oninput="setCustomValidity('');" autocomplete="off">
                            </div>
                        </div>
                        <div id="drag"></div>

                        <c:if test="${isValidateCodeLogin}">
                            <div class="validateCode">
                                <label class="input-label mid" for="validateCode">验证码</label>
                                <tags:validateCode name="validateCode" inputCssStyle="margin-bottom:0;"/>
                            </div>
                        </c:if>

                        <div class="gradient-border login-btn">

                            <button class="btn-warning" type="button" name="login" id="login" onclick="toLogin()">登录</button>

                        </div>

                    </form>

                </div>
            </div>
        </div>
    </div>

</div>

<script src="/static/jquery/jquery-1.9.1.min.js"></script>
<script src="/static/bootstrap/3.3.7/js/bootstrap.min.js"></script>


<script src="/static/modules/sys/login/js/matrix.js?version=v1"></script>
<script src="/static/modules/sys/login/js/main.js?version=v1"></script>
<script src="/static/modules/sys/login/js/login.js?version=v1"></script>
<script src="/static/modules/sys/login/js/drag.js?version=v1"></script>


</body>
</html>