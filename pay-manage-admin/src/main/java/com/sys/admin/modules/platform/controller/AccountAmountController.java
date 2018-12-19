package com.sys.admin.modules.platform.controller;


import com.sys.boss.api.service.order.MchtAccAmountService;
import com.sys.boss.api.service.order.MchtAccountDetailOrderService;
import com.sys.core.dao.dmo.MchtAccountDetail;
import com.sys.core.dao.dmo.MchtAccountDetailAmount;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping(value = "/accountAmount")
public class AccountAmountController {

    private static Logger log = LoggerFactory.getLogger(AccountAmountController.class);


    @Autowired
    private MchtAccAmountService mchtAccAmountService;





    @RequestMapping("/taskInsertCurrentDayAcctAmount")
    @ResponseBody
    public String taskInsertCurrentDayAcctAmount(String day){
        try {
            if(StringUtils.isEmpty(day)){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                day = dateFormat.format(new Date());
            }


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date currentData =  simpleDateFormat.parse(day);

            Calendar calendar =  Calendar.getInstance();
            calendar.setTime(currentData);
            calendar.add(Calendar.DATE, -1);// 日期减1

            MchtAccountDetail mchtAccountDetail = new MchtAccountDetail();
            mchtAccountDetail.setCreateTime(calendar.getTime());
            mchtAccountDetail.setCreateTimeStr(simpleDateFormat.format(calendar.getTime()));
            mchtAccountDetail.setSuffix(day.replace("-", "").substring(0, 6));

            mchtAccAmountService.taskInsertCurrentDayAcctAmount(mchtAccountDetail);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "SUCCESS";
    }



}
