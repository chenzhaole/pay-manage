package com.sys.gateway.controller;

import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.trade.handler.ITradeTxDFSingleHandler;
import com.sys.core.service.TaskLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TxDFController {
    @Autowired
    private ITradeTxDFSingleHandler tradeTxDFSingleHandler;
    @Autowired
    private TaskLogService taskLogService;

    @RequestMapping(value="/gateway/txCreateDF")
    @ResponseBody
    public String createDF(@RequestParam(required = false,value = "id") Integer logId) {
        CommonResult result = tradeTxDFSingleHandler.process();
        taskLogService.recordLog(logId,result);
        return "ok";
    }
}
