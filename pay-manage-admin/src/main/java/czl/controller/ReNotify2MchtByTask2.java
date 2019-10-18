package czl.controller;

import com.sys.common.util.DateUtils2;
import com.sys.common.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用12080网关补发通知
 * 自动定时调用1000000次
 */
public class ReNotify2MchtByTask2 {

    private Logger logger = LoggerFactory.getLogger(ReNotify2MchtByTask2.class);

    public void taskNotify(int totalTimes,int time) {

        if(totalTimes > 1)
        {
            System.out.println("这是第 " +  time + "次调用！");
            totalTimes--;
            time++;

            Connection conn = getConn();
            PreparedStatement pstmt = null;
            try {

                String suffix = DateUtils2.getNowTimeStr("yyyyMM");
                String startTime = DateUtils2.getNowTimeStr("yyyy-MM-dd 00:00:00");
                int nowUtc = DateUtils2.getNowUTC();
                int  utc = DateUtils2.getNowUTC()-10;
                String endTime = DateUtils2.convertUTC2Str(utc,"yyyy-MM-dd HH:mm:ss");

                StringBuffer sb = new StringBuffer();
                sb.append(" select o.mcht_code,m.`name`,m.mcht_key,o.notify_url,o.amount ,o.mcht_order_id,o.plat_order_id,o.update_time,o.supply_status ");
                sb.append(" from mcht_gateway_order_"+suffix+" o join mcht_info m on o.mcht_code = m.id ");
                sb.append(" where  o.`status`=2  and o.supply_status='' ");
                sb.append(" and o.update_time BETWEEN '"+startTime+"' AND '"+endTime+"' ");

                /**  补发时间段内所有商户订单 **/
                String sql = sb.toString();

                pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                int i=0;
                logger.info("执行SQL语句: "+sql);
                logger.info("============================");
                while (rs.next()) {
                    String mcht_code = rs.getString("mcht_code");
                    String mcht_key = rs.getString("mcht_key");
                    String notify_url = rs.getString("notify_url");
                    String amount = rs.getString("amount");
                    String mcht_order_id = rs.getString("mcht_order_id");
                    String plat_order_id = rs.getString("plat_order_id");
                    String update_time = rs.getString("update_time");

                    logger.info("待补发通知数据--> 平台订单号:"+plat_order_id+" 更新时间:"+update_time+" 商编:"+mcht_code+" 商户单号:"+mcht_order_id+"  ");

                    String gwNotifyUrl = "http://47.52.221.69:12080/gateway/renotify";
                    Map<String,String> param = new HashMap<>();
                    param.put("suffix",suffix);
                    param.put("orderId",plat_order_id);
                    //调用前置3网关
                    String resp = HttpUtil.post(gwNotifyUrl,param);
                    logger.info("补发通知返回结果:"+resp);
                }
                logger.info("===================== 补单定时任务执行完毕 ====================");
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

            try {
                Thread.sleep(1000*60l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            taskNotify(totalTimes, time);
        }
        else
        {
            System.out.println("调用结束，共调用了" + time + "次");
            return;
        }


        return ;
    }


    /**
     * 数据库连接
     */
    private static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://47.244.112.234:52118/pay_pro";
        String username = "pay_user";
        String password = "wku#Fh6kkrD";
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
     * 启动执行
     */
    public static void main(String[] args) {
//        new ReNotify2MchtByTask().taskNotify(null,null,null,null,null);
//        new ReNotify2MchtByTask2().taskNotify();

        int totalTimes = 1000000;
        int time = 1;
        long startTime = System.currentTimeMillis();
        System.out.println("嵌套调用起始时间：" + startTime);
        new ReNotify2MchtByTask2().taskNotify(totalTimes, time);
        System.out.println("嵌套调用结束时间：" + System.currentTimeMillis());
        System.out.println("总耗时：" + (System.currentTimeMillis() - startTime));
        System.out.println("------------------------------------------------------------");
    }


}
