package com.code.platform.task.web;


import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 工程首页
 */
@Controller
public class HomeController {

    /**
     * 主页
     */
    @RequestMapping("/")
    public String home() {
        // 转到登录页面
        return "login";
    }

    /**
     * 主页面
     */
    @RequestMapping("/index")
    public String index() {
        return "login";
    }

    /**
     * 头部
     */
    @RequestMapping("/top")
    public String top() {
        return "top";
    }

    @RequestMapping("/main")
    public String main() {
        return "index";
    }

    /**
     * 欢迎界面
     */
    @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    /**
     * 菜单
     */
    @RequestMapping("/menu")
    public String menu() {
        return "menu";
    }

    /**
     * 底部
     */
    @RequestMapping("/bottom")
    public String bottom() {
        return "bottom";
    }

    /**
     * 异常页面
     *
     * @param session 缓存对象
     * @return
     */
    @RequestMapping("/exception")
    public String exception(HttpSession session) {
        return "commons/error";
    }
}
