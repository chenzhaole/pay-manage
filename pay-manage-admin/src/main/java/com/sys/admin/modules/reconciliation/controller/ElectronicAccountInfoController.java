package com.sys.admin.modules.reconciliation.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.modules.channel.service.ChannelAdminService;
import com.sys.admin.modules.reconciliation.service.ElectronicAdminAccountInfoService;
import com.sys.common.enums.SignTypeEnum;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.CaElectronicAccount;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatBank;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatBankService;
import com.sys.core.vo.ElectronicAccountVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * @author fengzhenzhong
 * @desc  电子账户基本信息维护
 */
@Controller
@RequestMapping("${adminPath}/electronic")
public class ElectronicAccountInfoController {
    @Autowired
    private PlatBankService platBankService;
    @Autowired
    private ChannelAdminService channelAdminService;
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ElectronicAdminAccountInfoService electronicAdminAccountInfoService;

    private static  final Logger logger = LoggerFactory.getLogger(ElectronicAccountInfoController.class);

    @RequestMapping("doAddAccount")
    public String addElectronicAccount(ElectronicAccountVo electronicAccountVo,RedirectAttributes redirectAttributes){
        ElectronicAccountVo vo =electronicAdminAccountInfoService.queryBykey(electronicAccountVo);
        String message, messageType;
        if(vo !=null){
            message = "已存在";
            messageType = "error";
            redirectAttributes.addFlashAttribute("messageType", messageType);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:" + GlobalConfig.getAdminPath()+"/electronic/list";
        }
        boolean flag=electronicAdminAccountInfoService.add(electronicAccountVo);
        if(flag){
            message = "保存成功";
            messageType = "success";
        }else{
            message = "保存失败";
            messageType = "error";
        }
        redirectAttributes.addFlashAttribute("messageType", messageType);
        redirectAttributes.addFlashAttribute("message", message);
        logger.info("响应参数:"+flag);
        return "redirect:" + GlobalConfig.getAdminPath()+"/electronic/list";
    }
    @RequestMapping("toAddAccount")
    public String toAddElectronicAccount(Model model){
        //所有银行
        List<PlatBank> platBanks = platBankService.list(new PlatBank());
        model.addAttribute("platBanks", platBanks);
        //通道
        List<ChanInfo> chanInfos = channelAdminService.getChannelList(new ChanInfo());
        model.addAttribute("chanInfos", chanInfos);
        //商户
        MchtInfo mchtInfo = new MchtInfo();
        mchtInfo.setMchtType(SignTypeEnum.SIGN_MCHT.getCode());
        List<MchtInfo> mchtList = merchantService.list(mchtInfo);
        model.addAttribute("mchtList", mchtList);

        return "/modules/reconciliation/electronic/electronicAccountAdd";
    }
    @RequestMapping("list")
    public String list(ElectronicAccountVo electronicAccountVo,Model model){
        logger.info("请求参数："+JSON.toJSONString(electronicAccountVo));
        model.addAttribute("electronicAccountVo",electronicAccountVo);
        //通道
        List<ChanInfo> chanInfos = channelAdminService.getChannelList(new ChanInfo());
        model.addAttribute("chanInfos", chanInfos);
        //商户
        MchtInfo mchtInfo = new MchtInfo();
        mchtInfo.setMchtType(SignTypeEnum.SIGN_MCHT.getCode());
        List<MchtInfo> mchtList = merchantService.list(mchtInfo);
        model.addAttribute("mchtList", mchtList);
        int count =electronicAdminAccountInfoService.count(electronicAccountVo);
        if(count==0){
            return "/modules/reconciliation/electronic/electronicAccountList";
        }
        if(electronicAccountVo.getPage()==null){
            PageInfo page = new PageInfo();
            electronicAccountVo.setPage(page);
        }

        //查询
        List<CaElectronicAccount> caElectronicAccountList =electronicAdminAccountInfoService.list(electronicAccountVo);
        Page page =new Page(electronicAccountVo.getPage().getPageNo(), electronicAccountVo.getPage().getPageSize(), count, caElectronicAccountList, true);
        model.addAttribute("page",page);
        return "/modules/reconciliation/electronic/electronicAccountList";
    }

    @RequestMapping("toEditAccount")
    public String toEditElectronicAccount(ElectronicAccountVo electronicAccountVo,Model model){
        logger.info("请求参数："+JSON.toJSONString(electronicAccountVo));
        //所有银行
        List<PlatBank> platBanks = platBankService.list(new PlatBank());
        model.addAttribute("platBanks", platBanks);
        //通道
        List<ChanInfo> chanInfos = channelAdminService.getChannelList(new ChanInfo());
        model.addAttribute("chanInfos", chanInfos);
        //商户
        MchtInfo mchtInfo = new MchtInfo();
        mchtInfo.setMchtType(SignTypeEnum.SIGN_MCHT.getCode());
        List<MchtInfo> mchtList = merchantService.list(mchtInfo);
        model.addAttribute("mchtList", mchtList);
        ElectronicAccountVo vo =electronicAdminAccountInfoService.queryBykey(electronicAccountVo);
        model.addAttribute("electronicAccountVo",vo);
        return "/modules/reconciliation/electronic/electronicAccountEdit";
    }

    @RequestMapping("doEditAccount")
    public String doEditElectronicAccount(ElectronicAccountVo electronicAccountVo,RedirectAttributes redirectAttributes){
        logger.info("请求参数："+JSON.toJSONString(electronicAccountVo));
        boolean flag=electronicAdminAccountInfoService.update(electronicAccountVo);
        String message, messageType;
        if(flag){
            message = "保存成功";
            messageType = "success";
        }else{
            message = "保存失败";
            messageType = "error";
        }
        redirectAttributes.addFlashAttribute("messageType", messageType);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:" + GlobalConfig.getAdminPath()+"/electronic/list";
    }
    @RequestMapping("doDeleteAccount")
    public String doDeleteElectronicAccount(ElectronicAccountVo electronicAccountVo){
        logger.info("请求参数："+JSON.toJSONString(electronicAccountVo));
        boolean flag=electronicAdminAccountInfoService.delete(electronicAccountVo);
        String message, messageType;
        if(flag){
            message = "保存成功";
            messageType = "success";
        }else{
            message = "保存失败";
            messageType = "error";
        }
        return "redirect:" + GlobalConfig.getAdminPath()+"/electronic/list";
    }
}
