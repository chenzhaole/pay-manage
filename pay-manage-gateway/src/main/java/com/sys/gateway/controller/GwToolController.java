package com.sys.gateway.controller;


import com.alibaba.fastjson.JSON;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.DateUtils;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.gateway.common.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 平台工具接口
 */
@Controller
@RequestMapping(value = "")
public class GwToolController {

    protected final Logger logger = LoggerFactory.getLogger(GwToolController.class);
    private static final String jdbcUrl = "jdbc:mysql://47.93.242.185:52118/pay_pro";
    private static final String dbUser = "yunweizhrt";
    private static final String dbPw = "pJaUxWJd234!";


    /**
     * 商户支付流水对账单生成
     */
    @RequestMapping(value = "/gateway/api/tool/statAccFile")
    @ResponseBody
    public String statAccFile(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws Exception {
        System.out.println("商户支付流水对账单生成 ");
        String mchtId = request.getParameter("mchtId");//2001011000430888
        if (StringUtils.isBlank(mchtId)) {
            mchtId = "2001011000430888";
        }
        String date = request.getParameter("date");//yyyy-MM-dd

        File file = null;
        FileWriter fw = null;

        try {
            int yestodayUtc = DateUtils2.getNowUTC() - (3600 * 24);
            String startTime = DateUtils2.convertUTC2Str(yestodayUtc, "yyyy-MM-dd 00:00:00");
            String endTime = DateUtils2.convertUTC2Str(yestodayUtc, "yyyy-MM-dd 23:59:59");
            if (StringUtils.isNotBlank(date)) {//如果Date为空,默认统计昨天交易数据
                startTime = date + " 00:00:00";
                endTime = date + " 23:59:59";
            }
            String fileDateName = startTime.split(" ")[0].replaceAll("-", "");

//            String dir = "/home/accfile/" + mchtId+"/data";
            String dir = "/Users/chenzhaole/Downloads/" + mchtId;
            System.out.println("对账文件目录名称:" + dir);
            File dirFile = new File(dir);
            if (!dirFile.exists()) {//判断目录是否存在
                dirFile.mkdir();
            }

            //钱方好近对账单:  /home/accfile/2001011000430888/data/MNSACC01_20191024.txt
            String fullFileName = dir + File.separator + "MNSACC01_" + fileDateName + ".txt";

            file = new File(fullFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            List<Map> list = queryDB(mchtId, startTime, endTime);//查询数据库

            for (Map map : list) {
                Date update_date = (Date) map.get("update_date");
                String update_time = (String) map.get("update_time");
                String yyyyMMdd = DateUtils.formatYMDHMS(update_date, "yyyyMMdd", null);
                String HHmmss = update_time.toString().replaceAll(":", "");
                String from_mcht_id = StringUtils.isBlank((String) map.get("from_mcht_id")) ? "" : (String) map.get("from_mcht_id");
                String mcht_id = StringUtils.isBlank((String) map.get("mcht_id")) ? "" : (String) map.get("mcht_id");
                String mcht_order_id = StringUtils.isBlank((String) map.get("mcht_order_id")) ? "" : (String) map.get("mcht_order_id");
                String plat_order_id = StringUtils.isBlank((String) map.get("plat_order_id")) ? "" : (String) map.get("plat_order_id");
                String official_order_id = StringUtils.isBlank((String) map.get("official_order_id")) ? "" : (String) map.get("official_order_id");
                String pay_type = StringUtils.isBlank((String) map.get("pay_type")) ? "" : (String) map.get("pay_type");
                if (PayTypeEnum.PAY_REFUND.getCode().equalsIgnoreCase(pay_type)) {
                    pay_type = "2";
                } else {
                    pay_type = "1";
                }
                BigDecimal amount = (BigDecimal) map.get("amount");
                BigDecimal mcht_fee_rate = (BigDecimal) map.get("mcht_fee_rate");
                String mchtFeeRateStr = mcht_fee_rate == null ? "" : mcht_fee_rate.toString();
                BigDecimal mcht_fee_amount = (BigDecimal) map.get("mcht_fee_amount");
                String mchtFeeAmountStr = mcht_fee_amount == null ? "" : String.valueOf(mcht_fee_amount.intValue());//去除小数点

                //交易日期|交易时间|商户号|子商户号|商户订单号|平台订单号|上游订单号|交易类型(1-消费,2-退货）|交易金额(分)|商户费率(千分之）|商户手续费(分)|
                String line = yyyyMMdd + "|" + HHmmss + "|" + from_mcht_id + "|" + mcht_id + "|"
                        + mcht_order_id + "|" + plat_order_id + "|" + official_order_id + "|"
                        + pay_type + "|" + amount + "|" + mchtFeeRateStr + "|" + mchtFeeAmountStr + "|";
                fw.write(line + "\n");
            }


            return "" + fullFileName;
        } catch (Throwable e) {
            logger.error("清结算成功支付订单流水,定时任务调用异常", e);
        } finally {
            try {
                if (fw != null) {
                    fw.flush();
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ErrorCodeEnum.FAILURE.getCode();
    }

    /**
     * @param mchtId
     * @return
     */
    public List<Map> queryDB(String mchtId, String startTime, String endTime) {
        List<Map> list = new ArrayList<>();
        Connection conn = getConn();
        PreparedStatement pstmt = null;
        try {

            String suffix = startTime.split(" ")[0].replaceAll("-", "").substring(0, 6);
            String tableName = "mcht_gateway_order_" + suffix;// DateUtils2.getNowTimeStr("yyyyMM");


            StringBuffer sb = new StringBuffer();
            sb.append(" SELECT * ");
            sb.append(" FROM " + tableName + " ");
//            sb.append(" WHERE  `status`=2 AND from_mcht_id='" + mchtId + "' ");
            sb.append(" WHERE  `status`=2 ");
            sb.append(" AND update_time BETWEEN '" + startTime + "' AND '" + endTime + "' ");

            /**  补发时间段内所有商户订单 **/
            String sql = sb.toString();

            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int i = 0;
            System.out.println();
            System.out.println("执行SQL语句: " + sql);
            System.out.println("============================");
            while (rs.next()) {
                Map map = new HashMap<>();
                map.put("update_date", rs.getDate("update_time"));
                map.put("update_time", rs.getTime("update_time").toString());
                map.put("from_mcht_id", rs.getString("from_mcht_id"));
                map.put("mcht_id", rs.getString("mcht_id"));
                map.put("mcht_order_id", rs.getString("mcht_order_id"));
                map.put("plat_order_id", rs.getString("plat_order_id"));
                map.put("official_order_id", rs.getString("official_order_id"));
                map.put("pay_type", rs.getString("pay_type"));
                map.put("amount", rs.getBigDecimal("amount"));
                map.put("mcht_fee_rate", rs.getBigDecimal("mcht_fee_rate"));
                map.put("mcht_fee_amount", rs.getBigDecimal("mcht_fee_amount"));
                list.add(map);

                System.out.println("补发通知返回结果:" + JSON.toJSONString(map));
            }
            System.out.println("===================== 补单定时任务执行完毕 ====================");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 数据库连接
     */
    private static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = jdbcUrl;
        String username = dbUser;
        String password = dbPw;
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


    /**
     * 平台工具接口,获取平台订单ID(10位数字)
     */
    @RequestMapping(value = "/gateway/api/tool/buildPlatOrderId/int")
    @ResponseBody
    public String queryList(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        String platOrderId = "";
        String BIZ = "获取平台订单ID-";
        try {
            //请求ip
            String ip = IpUtil.getRemoteHost(request);
            logger.info("获取平台订单ID(10位数字),获取到客户端请求ip：" + ip);
            String id = IdUtil.createPlatOrderId("5");//P1910292359030002275
//            String tmp = id.substring(id.length() - 7, id.length() - 1);
//            platOrderId = "9" + id.substring(5, 7) + tmp;

            platOrderId = "20" + id.substring(1, id.length());
            logger.info("生成平台订单ID：" + platOrderId);


        } catch (Exception e) {
            logger.error("获取平台订单ID(10位数字),接口抛异常" + e);
        }
        return platOrderId;
    }


}
