package com.sys.admin.modules.reconciliation.controller;

import com.sys.core.service.ElectronicAccountInfoService;
import com.sys.core.vo.ElectronicAccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * @author fengzhenzhong
 * @desc  电子账户基本信息维护
 */
@Controller
@RequestMapping("electronic")
public class ElectronicAccountInfoController {
    @Autowired
    private ElectronicAccountInfoService electronicAccountInfoService;

    @RequestMapping("addAccount")
    public String addElectronicAccount(ElectronicAccountVo electronicAccountVo){
        //数据入库操作
        boolean flag=electronicAccountInfoService.add(electronicAccountVo);

        return null;
    }
}
