package czl;

import com.alibaba.fastjson.JSONObject;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.MD5Util;

import java.sql.*;

/**
 * 平台异常时,手动批量补发异步通知
 *
 * 1:根据条件读取数据库订单流水表
 * 2:批量发送异步通知
 *
 * <p>
 * Created by chenzhaole on 2019/5/9.
 */
public class BathNotify2Mcht {

    /**
     * 数据库连接
     */
    private static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";


        //九宝
        String url = "jdbc:mysql://47.244.112.234:52118/pay_pro";
        String username = "pay_user";
        String password = "wku#Fh6kkrD";


//        //华讯
//        String url = "jdbc:mysql://47.244.157.60:52118/pay_pro";
//        String username = "root";
//        String password = "RZXq%0e";


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
     * 查询订单 -> 发送异步通知
     */
    public void batchNotify() {
        Connection conn = getConn();
        PreparedStatement pstmt = null;
        try {

              String mchtCode = "2001005000564399";
            String startTime = "2019-10-18 15:35:00";
            String endTime = "2019-10-18 16:00:00";

//172.31.53.143
//            /**  补发指定商户订单 **/
//            String sql =
//                    " select o.mcht_code,m.`name`,m.mcht_key,o.notify_url,o.amount ,o.mcht_order_id,o.plat_order_id,o.update_time " +
//                    " from mcht_gateway_order_201905 o join mcht_info m on o.mcht_code = m.id " +
//                    " where m.id='"+mchtCode+"' and o.`status`=2 and o.update_time BETWEEN '"+startTime+"' AND '"+endTime+"'";

            /**  补发时间段内所有商户订单 **/
//            String sql =
//                    " select o.mcht_code,m.`name`,m.mcht_key,o.notify_url,o.amount ,o.mcht_order_id,o.plat_order_id,o.update_time " +
//                    " from mcht_gateway_order_201910 o join mcht_info m on o.mcht_code = m.id " +
//                    " where  o.`status`=2 and o.update_time BETWEEN '"+startTime+"' AND '"+endTime+"'";


            /**  补发时间段内+指定有商户的订单 **/
            String sql =
                    " select o.mcht_code,m.`name`,m.mcht_key,o.notify_url,o.amount ,o.mcht_order_id,o.plat_order_id,o.update_time " +
                            " from mcht_gateway_order_201910 o join mcht_info m on o.mcht_code = m.id " +
                            " where m.id='"+mchtCode+"' and o.`status`=2 and o.update_time BETWEEN '"+startTime+"' AND '"+endTime+"'";



            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int i=0;
            System.out.println("执行SQL语句: "+sql);
            System.out.println("============================");
            while (rs.next()) {
                String mcht_code = rs.getString("mcht_code");
                String mcht_key = rs.getString("mcht_key");
                String notify_url = rs.getString("notify_url");
                String amount = rs.getString("amount");
                String mcht_order_id = rs.getString("mcht_order_id");
                String plat_order_id = rs.getString("plat_order_id");
                String update_time = rs.getString("update_time");

                update_time = update_time.replace("-","");
                update_time = update_time.replace(":","");
                update_time = update_time.replace(" ","");
                update_time = update_time.replace(".0","");

                this.send((++i),mcht_code,mcht_key,notify_url,mcht_order_id,plat_order_id,amount,update_time);
            }
            System.out.println("============================");
        } catch (SQLException e) {
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

    }

    /**
     * to商户服务器补发成功异步通知
     */
    private void send(int seq,String mcht_code, String mcht_key, String notify_url, String mcht_order_id, String plat_order_id, String amount,String update_time) {
        JSONObject head = new JSONObject();
        head.put("respCode","0000");
        head.put("respMsg","请求成功");

        JSONObject body = new JSONObject();
        body.put("amount",amount);
        body.put("bankCardNo","");
        body.put("chargeTime",update_time);
        body.put("mchtId",mcht_code);
        body.put("orderId",mcht_order_id);
        body.put("payType","wx");
        body.put("seq","123456");
        body.put("status","SUCCESS");
        body.put("tradeId",plat_order_id);

        String signSrc = "amount="+amount+"&chargeTime="+update_time+"&mchtId="+mcht_code+"&orderId="+mcht_order_id+"&payType=wx&seq=123456&status=SUCCESS&tradeId="+plat_order_id+"&key="+mcht_key;
        String sign = MD5Util.MD5Encode(signSrc);

//        System.out.println("异步通知签名原始字符串:"+signSrc);

        JSONObject data = new JSONObject();
        data.put("head",head);
        data.put("body",body);
        data.put("sign",sign);
        try {
            String resp = HttpUtil.postConnManager(notify_url, data.toJSONString(), "application/json", "UTF-8", "UTF-8");
            System.out.println(seq+" | 商户返回:"+resp+" | 数据:"+data.toJSONString()+" | 通知地址:"+notify_url);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 启动执行
     */
    public static void main(String[] args) {
        new BathNotify2Mcht().batchNotify();
    }
}
