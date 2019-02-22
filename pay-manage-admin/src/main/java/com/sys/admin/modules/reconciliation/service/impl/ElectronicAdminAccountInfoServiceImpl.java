package com.sys.admin.modules.reconciliation.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.admin.modules.reconciliation.controller.ElectronicAccountInfoController;
import com.sys.admin.modules.reconciliation.service.ElectronicAdminAccountInfoService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.IdUtil;
import com.sys.common.util.RandomNumberUtil;
import com.sys.core.dao.dmo.CaBankElectronicAccount;
import com.sys.core.dao.dmo.CaBankElectronicAccountBank;
import com.sys.core.dao.dmo.CaElectronicAccount;
import com.sys.core.dao.dmo.PlatFeerate;
import com.sys.core.service.ElectronicAccountInfoService;
import com.sys.core.service.PlatFeerateService;
import com.sys.core.vo.ElectronicAccountVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ElectronicAdminAccountInfoServiceImpl implements ElectronicAdminAccountInfoService {
    @Autowired
    private ElectronicAccountInfoService electronicAccountInfoService;

    @Override
    public List<CaElectronicAccount> list(ElectronicAccountVo electronicAccountVo) {
        return electronicAccountInfoService.list(electronicAccountVo);
    }

    @Override
    public int count(ElectronicAccountVo electronicAccountVo) {
        return electronicAccountInfoService.count(electronicAccountVo);
    }

    private static  final Logger logger = LoggerFactory.getLogger(ElectronicAdminAccountInfoServiceImpl.class);
    @Override
    public boolean add(ElectronicAccountVo electronicAccountVo) {
        Date currDate = new Date();
        //获取操作用户id
        Long userId =UserUtils.getUser().getId();
        CaElectronicAccount caElectronicAccount =electronicAccountVo.getCaElectronicAccount();
        CaBankElectronicAccount caBankElectronicAccount =electronicAccountVo.getCaBankElectronicAccount();
        List<CaBankElectronicAccountBank> caBankElectronicAccountBankList =electronicAccountVo.getCaBankElectronicAccountBankList();
        //电子账户ID
        String caElectronicAccountId =IdUtil.createCaCommonId("0");
        //银行电子账户ID
        String caBankElectronicAccountId =IdUtil.createCaCommonId("0");
        //支持银行列表ID
        String caBankElectronicAccountBankId=null;
        List<CaBankElectronicAccountBank> caBankElectronicAccountBanks = new ArrayList<>();

        caElectronicAccount.setId(caElectronicAccountId);
        caElectronicAccount.setBankElectronicAccountId(caBankElectronicAccountId);
        caElectronicAccount.setCreateOperatorId(userId);
        caElectronicAccount.setCreateTime(currDate);
        caBankElectronicAccount.setId(caBankElectronicAccountId);
        caBankElectronicAccount.setCreateOperatorId(userId);
        caBankElectronicAccount.setCreateTime(currDate);
        for(CaBankElectronicAccountBank caBankElectronicAccountBank:caBankElectronicAccountBankList){
            caBankElectronicAccountBankId=IdUtil.createCaCommonId("0");
            caBankElectronicAccountBank.setId(caBankElectronicAccountBankId);
            caBankElectronicAccountBank.setBankElectronicAccountId(caBankElectronicAccountId);
            caBankElectronicAccountBank.setCreateOperatorId(userId);

        }
        if(electronicAccountVo.getPlatFeerate()!=null){
            PlatFeerate platFeerate =electronicAccountVo.getPlatFeerate();
            String feeID = "F"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
            platFeerate.setId(feeID);
            platFeerate.setBizName(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getdesc());
            platFeerate.setBizType(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getCode());
            platFeerate.setBizRefId(caElectronicAccountId);
            platFeerate.setCreateTime(new Date());
            platFeerate.setStatus(StatusEnum.VALID.getCode());
        }
        logger.info("获取请求参数:"+JSON.toJSONString(electronicAccountVo));
        //数据入库操作
        boolean flag=electronicAccountInfoService.add(electronicAccountVo);
        logger.info("获取响应参数"+flag);
        return flag;
    }

    @Override
    public boolean update(ElectronicAccountVo electronicAccountVo) {
        Date currDate = new Date();
        Long userId =UserUtils.getUser().getId();
        CaElectronicAccount caElectronicAccount =electronicAccountVo.getCaElectronicAccount();
        CaBankElectronicAccount caBankElectronicAccount =electronicAccountVo.getCaBankElectronicAccount();
        List<CaBankElectronicAccountBank> caBankElectronicAccountBankList =electronicAccountVo.getCaBankElectronicAccountBankList();
        caElectronicAccount.setUpdateOperatorId(userId);
        caElectronicAccount.setUpdateTime(currDate);
        caBankElectronicAccount.setUpdateOperatorId(userId);
        caBankElectronicAccount.setUpdateTime(currDate);
        //支持银行列表ID
        String caBankElectronicAccountBankId=null;
        for(CaBankElectronicAccountBank caBankElectronicAccountBank:caBankElectronicAccountBankList){
            caBankElectronicAccountBankId=IdUtil.createCaCommonId("0");
            caBankElectronicAccountBank.setId(caBankElectronicAccountBankId);
            caBankElectronicAccountBank.setBankElectronicAccountId(caBankElectronicAccount.getId());
            caBankElectronicAccountBank.setCreateOperatorId(userId);
        }

            if(StringUtils.isNotBlank(electronicAccountVo.getPlatFeerate().getFeeType())){
                PlatFeerate platFeerate =electronicAccountVo.getPlatFeerate();
                String feeID = "F"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
                platFeerate.setId(feeID);
                platFeerate.setBizName(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getdesc());
                platFeerate.setBizType(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getCode());
                platFeerate.setBizRefId(electronicAccountVo.getCaElectronicAccount().getId());
                platFeerate.setCreateTime(new Date());
                platFeerate.setStatus(StatusEnum.VALID.getCode());
            }

        return electronicAccountInfoService.update(electronicAccountVo);
    }

    @Override
    public boolean delete(ElectronicAccountVo electronicAccountVo) {

        return electronicAccountInfoService.delete(electronicAccountVo);
    }

    @Override
    public ElectronicAccountVo queryBykey(ElectronicAccountVo electronicAccountVo) {

        return electronicAccountInfoService.queryBykey(electronicAccountVo);
    }
}
