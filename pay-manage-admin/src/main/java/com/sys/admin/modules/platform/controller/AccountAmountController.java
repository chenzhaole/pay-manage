package com.sys.admin.modules.platform.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.order.MchtAccAmountService;
import com.sys.boss.api.service.order.MchtAccountDetailOrderService;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.core.dao.dmo.MchtAccountDetail;
import com.sys.core.dao.dmo.MchtAccountDetailAmount;
import com.sys.core.service.TaskLogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/accountAmount")
public class AccountAmountController {

    private static Logger log = LoggerFactory.getLogger(AccountAmountController.class);


    @Autowired
    private MchtAccAmountService mchtAccAmountService;
    @Autowired
    private TaskLogService taskLogService;





    @RequestMapping("/taskInsertCurrentDayAcctAmount")
    @ResponseBody
    public String taskInsertCurrentDayAcctAmount(String day, @RequestParam(required = false, value = "id") Integer logId){
        log.info("开始执行余额入库.需要执行的时间为:" +day);
        try {
            if(StringUtils.isEmpty(day)){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                day = dateFormat.format(new Date());
            }
            CommonResult result = new CommonResult();
            result.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            result.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date currentData =  simpleDateFormat.parse(day);

            Calendar calendar =  Calendar.getInstance();
            calendar.setTime(currentData);
            calendar.add(Calendar.DATE, -1);// 日期减1

            String currentTime = simpleDateFormat.format(calendar.getTime());

            MchtAccountDetail mchtAccountDetail = new MchtAccountDetail();
            mchtAccountDetail.setCreateTime(calendar.getTime());
            mchtAccountDetail.setCreateTimeStr(simpleDateFormat.format(calendar.getTime()) + " 23:59:59");
            mchtAccountDetail.setSuffix(currentTime.replace("-", "").substring(0, 6));


            List<MchtAccountDetail> accountDetails = mchtAccAmountService.taskInsertCurrentDayAcctAmount(mchtAccountDetail);
            result.setData(JSONArray.toJSONString(accountDetails));
            taskLogService.recordLog(logId,result);
            log.info("开始执行余额入库，【定时任务发起代付】任务执行logId结束："+logId+" "+ JSON.toJSONString(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "SUCCESS";
    }



}
