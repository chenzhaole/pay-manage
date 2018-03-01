package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.ProxyPayDetailStatusEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/proxy")
public class ProxyOrderController extends BaseController {

    @Autowired
    private ProxyBatchService proxyBatchService;

    @Autowired
    private ProxyDetailService proxyDetailService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ChanMchtPaytypeService chanMchtPaytypeService;

    @Autowired
    private ProductService productService;

    /**
     * 代付批次列表
     */
    @RequestMapping(value = {"proxyBatchList", ""})
    public String proxyBatchList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
        PlatProxyBatch proxyBatch = new PlatProxyBatch();
        proxyBatch.setChanId(paramMap.get("chanId"));
        proxyBatch.setMchtId(paramMap.get("mchtId"));
        proxyBatch.setMchtOrderId(paramMap.get("mchtOrderId"));
        proxyBatch.setPayStatus(paramMap.get("payStatus"));
        proxyBatch.setCheckStatus(paramMap.get("checkStatus"));

        //分页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        proxyBatch.setPageInfo(pageInfo);

        //查询商户列表
        List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
        //通道商户支付方式列表
//		List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
        //  上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

        model.addAttribute("chanInfos", chanInfoList);
        model.addAttribute("mchtInfos", mchtInfos);
//		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);

        int proxyCount = proxyBatchService.count(proxyBatch);
        if (proxyCount == 0) {
            return "modules/proxy/proxyBatchList";
        }

        List<PlatProxyBatch> proxyInfoList = proxyBatchService.list(proxyBatch);

        Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyInfoList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/proxyBatchList";
    }


    /**
     * 代付明细列表
     */
    @RequestMapping(value = {"proxyDetailList", ""})
    public String proxyDetailList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
        PlatProxyDetail proxyDetail = new PlatProxyDetail();
        proxyDetail.setChanId(paramMap.get("chanId"));
        proxyDetail.setMchtId(paramMap.get("mchtId"));
        proxyDetail.setId(paramMap.get("detailId"));
        proxyDetail.setPayStatus(paramMap.get("payStatus"));
        proxyDetail.setCheckStatus(paramMap.get("checkStatus"));
        proxyDetail.setBatchId(paramMap.get("batchId"));

        //分页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        proxyDetail.setPageInfo(pageInfo);

        //批次信息
        PlatProxyBatch proxyBatch = proxyBatchService.queryByKey(paramMap.get("batchId"));

        //查询商户列表
        List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
        //  上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
        //  产品
        List<PlatProduct> platProducts = productService.list(new PlatProduct());


        Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
        Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");
        Map<String, String> productMap = Collections3.extractToMap(platProducts, "id", "name");

        if (proxyBatch != null) {
            proxyBatch.setChanId(channelMap.get(proxyBatch.getChanId()));
            proxyBatch.setProductId(productMap.get(proxyBatch.getProductId()));
            proxyBatch.setExtend3(mchtMap.get(proxyBatch.getMchtId()));
            model.addAttribute("proxyBatch", proxyBatch);
        }
        model.addAttribute("chanInfos", chanInfoList);
        model.addAttribute("mchtInfos", mchtInfos);
//		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);

        int proxyCount = proxyDetailService.count(proxyDetail);

        List<PlatProxyDetail> proxyInfoList = proxyDetailService.list(proxyDetail);

        Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyInfoList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/proxyDetailList";
    }

    /**
     * 代付详情
     */
    @RequestMapping(value = {"proxyDetail", ""})
    public String proxyDetail(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
//        PlatProxyDetail proxyDetail = new PlatProxyDetail();
        String detailId = paramMap.get("detailId");
        String batchId = paramMap.get("batchId");

        PlatProxyDetail proxyDetail = proxyDetailService.queryByKey(detailId);

        //批次信息
        PlatProxyBatch proxyBatch = proxyBatchService.queryByKey(batchId);

        //查询商户列表
        List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
        //  上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

        Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
        Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");

        if (proxyDetail != null) {
            proxyDetail.setExtend2(mchtMap.get(proxyDetail.getMchtId()));
            proxyDetail.setExtend3(channelMap.get(proxyDetail.getChanId()));
        }
        model.addAttribute("chanInfos", chanInfoList);
        model.addAttribute("mchtInfos", mchtInfos);
        model.addAttribute("proxyBatch", proxyBatch);
        model.addAttribute("proxyDetail", proxyDetail);

        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/proxyDetail";
    }

    /**
     * 代付详情初始化，即在此发起代付
     */
    @RequestMapping(value = {"proxyDetailInit", ""})
    public String proxyDetailInit(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int result;
        String message = "", messageType = null;
        String opLogin = UserUtils.getUser().getLoginName();
        String opName = UserUtils.getUser().getName();
        String operatorUser = opLogin + "-" + opName;

        String oldProxyDetailId = paramMap.get("detailId");
        PlatProxyDetail oldProxyDetail = proxyDetailService.queryByKey(oldProxyDetailId);
        PlatProxyDetail newProxyDetail = (PlatProxyDetail) org.apache.commons.beanutils.BeanUtils.cloneBean(oldProxyDetail);

        if (null == oldProxyDetail) {
            message = "该笔代付数据异常";
            messageType = "error";
            redirectAttributes.addFlashAttribute("messageType", messageType);
            redirectAttributes.addFlashAttribute("message", message);
            logger.info("该笔代付数据异常：" + oldProxyDetail.getId());
            return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
        }

        if (ProxyPayDetailStatusEnum.DF_SUCCESS.getCode().equals(oldProxyDetail.getPayStatus())
                || ProxyPayDetailStatusEnum.RECREATEED.getCode().equals(oldProxyDetail.getPayStatus())) {
            message = "该笔代付已经成功或已重新发起,不可再次重新发起代付";
            messageType = "error";
            redirectAttributes.addFlashAttribute("messageType", messageType);
            redirectAttributes.addFlashAttribute("message", message);
            logger.info("该笔代付已经成功：" + oldProxyDetailId);
            return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
        }

        //代付失败的订单，允许初始化，并且创建一条新的代付订单
        if (ProxyPayDetailStatusEnum.DF_FAIL.getCode().equals(oldProxyDetail.getPayStatus())) {


            //新代付详情ID： "B + yyMMddHHmmss + xxxxxx + mID"
            String newId = "B" + DateUtils2.getNowTimeStr("yyMMddHHmmss") + IdUtil.buildRandom(6) + "2";
            //将该笔代付订单PayStatus值置为 20 即代付未处理
            newProxyDetail.setPayStatus(ProxyPayDetailStatusEnum.DF_INIT.getCode());
            newProxyDetail.setOperatorId(operatorUser);
            newProxyDetail.setId(newId);
            newProxyDetail.setCreateDate(new Date());
            newProxyDetail.setUpdateDate(new Date());
            newProxyDetail.setReturnMessage2("");
            newProxyDetail.setRemark("该笔代付由人工后台重新发起，操作者：" + operatorUser + "，对应旧的代付详情ID：" + oldProxyDetail.getId());

            logger.info("初始化新代付id：" + oldProxyDetailId + "，具体信息为：" + JSON.toJSONString(newProxyDetail));
            int addInt = proxyDetailService.create(newProxyDetail);
            logger.info("初始化新代付：" + newProxyDetail + "，修改数据库结果为：" + addInt);
            if (1 == addInt) {
                oldProxyDetail.setUpdateDate(new Date());
                //将该笔代付订单PayStatus值置为30 即已经重新发起代付
                oldProxyDetail.setPayStatus(ProxyPayDetailStatusEnum.RECREATEED.getCode());
                oldProxyDetail.setRemark("该笔代付已经人工重新发起，操作者：" + operatorUser + "，新创建的代付详情ID：" + newId);
                PlatProxyDetail selected = new PlatProxyDetail();
                selected.setId(oldProxyDetailId);
                int upInt = proxyDetailService.saveByKey(oldProxyDetail);
                message = "该笔代付初始化成功";
                messageType = "success";
                logger.info("该笔代付初始化成功：id=" + oldProxyDetailId);
            } else {
                message = "该笔代付初始化失败";
                messageType = "error";
                logger.info("该笔代付初始化失败：id=" + oldProxyDetailId);
            }
        } else {
            message = "该笔代付数据未失败，不允许初始化id=" + oldProxyDetailId;
            messageType = "error";
            logger.info("该笔代付数据未失败，不允许初始化：id=" + oldProxyDetailId);
        }


        redirectAttributes.addFlashAttribute("messageType", messageType);
        redirectAttributes.addFlashAttribute("message", message);
        response.setCharacterEncoding("UTF-8");
        return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
    }
}
