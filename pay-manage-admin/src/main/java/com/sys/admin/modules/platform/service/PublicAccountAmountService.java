package com.sys.admin.modules.platform.service;

import com.sys.admin.modules.platform.bo.ProductFormInfo;
import com.sys.admin.modules.platform.bo.ProductRelaFormInfo;
import com.sys.core.dao.dmo.AccountAmount;
import com.sys.core.dao.dmo.PlatProductSearch;

import java.util.List;

/**
 * 公户账务金额
 * @author duanjintang
 * 2019-01-03
 */
public interface PublicAccountAmountService {
    /**
     * 转换excel中数据为账务金额模型数据
     * @param  publicAccountCode  公户编号
     * @param  modelName
     * @return
     */
    List<AccountAmount> convertExcelDataToAccountAmount(String publicAccountCode, String modelName, List<String[]> data);

    /**
     * 批量导入公户账务数据
     * @return
     */
    int batchAccountAmount(List<AccountAmount> accountAmounts);
}
