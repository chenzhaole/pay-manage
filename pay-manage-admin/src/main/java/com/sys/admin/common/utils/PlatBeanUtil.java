package com.sys.admin.common.utils;

import com.sys.common.enums.PayStatusEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.DateUtils;
import com.sys.common.util.DateUtils2;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by chenzhaole on 2019/8/17.
 */
public class PlatBeanUtil {

    /**
     * 组装搜索参数
     *
     * @param paramMap
     * @return
     */
    public static MchtGatewayOrder buildMchtGatewayOrder4Search(Map<String, String> paramMap) {

        MchtGatewayOrder order = new MchtGatewayOrder();

        String id = paramMap.get("id");
        if (StringUtils.isNotBlank(id)) {
            order.setId(id);
        }

        //设置商户订单号
        if (StringUtils.isNotBlank(paramMap.get("mchtOrderId"))) {
            order.setMchtOrderId(paramMap.get("mchtOrderId").trim());
        }

        //设置平台订单号
        if (StringUtils.isNotBlank(paramMap.get("platOrderId"))) {
            order.setPlatOrderId(paramMap.get("platOrderId").trim());
        }

        //官方订单号
        if (StringUtils.isNotBlank(paramMap.get("officialSeq"))) {
            order.setOfficialOrderId(paramMap.get("officialSeq").trim());
        }

        //商户ID
        if (StringUtils.isNotBlank(paramMap.get("mchtId"))) {
            order.setMchtId(paramMap.get("mchtId").trim());
        }

        //商户Code
        if (StringUtils.isNotBlank(paramMap.get("mchtCode"))) {
            order.setMchtCode(paramMap.get("mchtCode").trim());
        }

        //代理商Id
        if (StringUtils.isNotBlank(paramMap.get("agentMchtId"))) {
            order.setAgentMchtId(paramMap.get("agentMchtId").trim());
        }

        //代理商Code
        if (StringUtils.isNotBlank(paramMap.get("agentMchtCode"))) {
            order.setAgentMchtCode(paramMap.get("agentMchtCode").trim());
        }

        //订单状态
        if (StringUtils.isNotBlank(paramMap.get("status"))) {
            order.setStatus(paramMap.get("status"));
        } else {
            //不显示下单失败的流水,使用!做个标记
            order.setStatus("!" + PayStatusEnum.SUBMIT_FAIL.getCode());
        }

        //补单状态
        if (StringUtils.isNotBlank(paramMap.get("supplyStatus"))) {
            order.setSupplyStatus(paramMap.get("supplyStatus"));
        }

        //支付方式--需要特殊处理下， 例如：支付宝扫码和扫码转h5，统一为支付宝扫码支付
        if (StringUtils.isNotBlank(paramMap.get("payType"))) {
            StringBuffer sb = new StringBuffer();
            if ("wx".equals(paramMap.get("payType"))) {
                sb.append(PayTypeEnum.WX_APP.getCode()).append("&").append(PayTypeEnum.WX_BARCODE_H5.getCode()).append("&")
                        .append(PayTypeEnum.WX_BARCODE.getCode()).append("&").append(PayTypeEnum.WX_BARCODE_PC.getCode()).append("&")
                        .append(PayTypeEnum.WX_GROUP.getCode()).append("&").append(PayTypeEnum.WX_WAP.getCode()).append("&")
                        .append(PayTypeEnum.WX_PUBLIC_NATIVE.getCode()).append("&").append(PayTypeEnum.WX_PUBLIC_NOT_NATIVE.getCode()).append("&")
                        .append(PayTypeEnum.WX_QRCODE.getCode()).append("&").append(PayTypeEnum.WX_BARCODE_PC.getCode());

            } else if ("al".equals(paramMap.get("payType"))) {
                sb.append(PayTypeEnum.ALIPAY_GROUP.getCode()).append("&").append(PayTypeEnum.ALIPAY_H5.getCode()).append("&")
                        .append(PayTypeEnum.ALIPAY_ONLINE_SCAN2WAP.getCode()).append("&").append(PayTypeEnum.ALIPAY_PC.getCode()).append("&")
                        .append(PayTypeEnum.ALIPAY_APP.getCode()).append("&").append(PayTypeEnum.ALIPAY_ONLINE_QRCODE.getCode()).append("&")
                        .append(PayTypeEnum.ALIPAY_BARCODE.getCode()).append("&").append(PayTypeEnum.ALIPAY_BARCODE_PC.getCode()).append("&")
                        .append(PayTypeEnum.ALIPAY_BARCODE_H5.getCode()).append("&").append(PayTypeEnum.ALIPAY_SERVICE_WINDOW.getCode());
            } else if ("sn".equals(paramMap.get("payType"))) {
                sb.append(PayTypeEnum.SUNING_GROUP.getCode()).append("&").append(PayTypeEnum.SUNING_H5.getCode()).append("&")
                        .append(PayTypeEnum.SUNING_SCAN2WAP.getCode()).append("&").append(PayTypeEnum.SUNING_PC.getCode()).append("&")
                        .append(PayTypeEnum.SUNING_QRCODE.getCode()).append("&").append(PayTypeEnum.SUNING_BARCODE.getCode()).append("&")
                        .append(PayTypeEnum.SUNING_BARCODE_PC.getCode()).append("&").append(PayTypeEnum.SUNING_BARCODE_H5.getCode());

            } else if ("qq".equals(paramMap.get("payType"))) {
                sb.append(PayTypeEnum.QQ_GROUP.getCode()).append("&").append(PayTypeEnum.QQ_WAP.getCode()).append("&")
                        .append(PayTypeEnum.QQ_SCAN2WAP.getCode()).append("&").append(PayTypeEnum.QQ_PC.getCode()).append("&")
                        .append(PayTypeEnum.QQ_QRCODE.getCode()).append("&").append(PayTypeEnum.QQ_BARCODE.getCode()).append("&")
                        .append(PayTypeEnum.QQ_BARCODE_PC.getCode()).append("&").append(PayTypeEnum.QQ_BARCODE_H5.getCode());

            } else if ("jd".equals(paramMap.get("payType"))) {
                sb.append(PayTypeEnum.JD_GROUP.getCode()).append("&").append(PayTypeEnum.JD_WAP.getCode()).append("&")
                        .append(PayTypeEnum.JD_SCAN2WAP.getCode()).append("&").append(PayTypeEnum.JD_PC.getCode()).append("&")
                        .append(PayTypeEnum.JD_SCAN.getCode()).append("&").append(PayTypeEnum.JD_BARCODE.getCode()).append("&")
                        .append(PayTypeEnum.JD_BARCODE_PC.getCode()).append("&").append(PayTypeEnum.JD_BARCODE_H5.getCode());

            } else if ("yl".equals(paramMap.get("payType"))) {
                sb.append(PayTypeEnum.UNIONPAY_GROUP.getCode()).append("&").append(PayTypeEnum.UNIONPAY_H5.getCode()).append("&")
                        .append(PayTypeEnum.UNIONPAY_QRCODE.getCode()).append("&").append(PayTypeEnum.UNIONPAY_SCAN2WAP.getCode()).append("&")
                        .append(PayTypeEnum.UNIONPAY_BARCODE.getCode()).append("&").append(PayTypeEnum.UNIONPAY_BARCODE_PC.getCode()).append("&")
                        .append(PayTypeEnum.UNIONPAY_BARCODE_h5.getCode());

            } else if ("qj".equals(paramMap.get("payType"))) {
                sb.append(PayTypeEnum.QUICK_GROUP.getCode()).append("&").append(PayTypeEnum.QUICK_PAY.getCode()).append("&")
                        .append(PayTypeEnum.QUICK_COMB_DK.getCode());

            } else if ("df101".equals(paramMap.get("payType"))) {
                sb.append(PayTypeEnum.SINGLE_DF.getCode());
            }

            order.setPayType(sb.toString());
        }

        //初始化页面开始时间
        String beginDate = paramMap.get("beginDate");
        if (StringUtils.isBlank(beginDate)) {
            order.setCreateTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 00:00:00"));
            paramMap.put("beginDate", DateUtils.getDate("yyyy-MM-dd") + " 00:00:00");
        } else {
            paramMap.put("beginDate", beginDate);
            order.setCreateTime(DateUtils.parseDate(beginDate));
        }
        String endDate = paramMap.get("endDate");
        //初始化页面结束时间
        if (StringUtils.isBlank(endDate)) {
            order.setUpdateTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 23:59:59"));
            paramMap.put("endDate", DateUtils.getDate("yyyy-MM-dd") + " 23:59:59");
        } else {
            paramMap.put("endDate", endDate);
            order.setUpdateTime(DateUtils.parseDate(endDate));
        }

        //表扩展名
        if (StringUtils.isNotBlank(paramMap.get("yyyyMM"))) {
            order.setSuffix(paramMap.get("yyyyMM"));
        } else if (StringUtils.isNotBlank(paramMap.get("platOrderId"))) {
            String platOrderId = paramMap.get("platOrderId");
            String suffix = "20" + platOrderId.substring(1, 5);
            order.setSuffix(suffix);
        } else if (StringUtils.isNotBlank(beginDate)) {
            order.setSuffix(DateUtils.formatDate(order.getCreateTime(), "yyyyMM"));
        } else {
            order.setSuffix(DateUtils2.getNowTimeStr("yyyyMM"));
        }


        PageInfo pageInfo = new PageInfo();
        //翻页-页码
        if (StringUtils.isNotBlank(paramMap.get("pageNo"))) {
            String pageNo = paramMap.get("pageNo");
            pageInfo.setPageNo(Integer.parseInt(pageNo));
        }
        //翻页-每页数量
        if (StringUtils.isNotBlank(paramMap.get("pageSize"))) {
            String pageSize = paramMap.get("pageSize");
            pageInfo.setPageSize(Integer.parseInt(pageSize));
        }
        order.setPageInfo(pageInfo);


        return order;
    }


}
