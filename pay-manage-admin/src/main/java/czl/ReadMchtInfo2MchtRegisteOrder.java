package czl;

import com.alibaba.fastjson.JSON;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.dmo.MchtInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取商户基本信息表
 * 插入商户通道入驻订单表
 * <p>
 * Created by chenzhaole on 2019/10/13.
 */
public class ReadMchtInfo2MchtRegisteOrder {
    public static int seq = 0;


    public Object proccess() {

        Connection conn = null;
        try {
            conn = getConn();
            List list = selectMchtInfo(conn);
            for (Object obj : list) {
                System.out.println(JSON.toJSONString(obj));
                int rs = insertMchtRegistOrder(conn, obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private List selectMchtInfo(Connection conn) throws Exception {
        List list = new ArrayList<>();

//        String sql = "select * from mcht_info where mcht_from='2001011000430888'";
        String sql = "select * from mcht_info where mcht_from='abc'";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        int i = 0;

        while (rs.next()) {
            Map map = new HashMap();
            map.put("id", rs.getString("id"));
            map.put("name", rs.getString("name"));
            map.put("chan_mcht_no", rs.getString("chan_mcht_no"));
            map.put("province", rs.getString("province"));
            map.put("city", rs.getString("city"));
            map.put("district", rs.getString("district"));
            map.put("address", rs.getString("company_adr"));
            map.put("mcht_type", rs.getString("mcht_type"));
            map.put("chan_oa_id", rs.getString("chan_oa_id"));
            map.put("chan_mcht_no", rs.getString("chan_mcht_no"));
            map.put("chan_biz_code", rs.getString("chan_biz_code"));
            map.put("mcht_fee_rate_wx_t0", rs.getString("mcht_fee_rate_wx_t0"));
            map.put("mcht_fee_rate_wx_t1", rs.getString("mcht_fee_rate_wx_t1"));
            map.put("mcht_fee_rate_alipay_t0", rs.getString("mcht_fee_rate_alipay_t0"));
            map.put("mcht_fee_rate_alipay_t1", rs.getString("mcht_fee_rate_alipay_t1"));
            map.put("mcht_fee_rate_debit_t0", rs.getString("mcht_fee_rate_debit_t0"));
            map.put("mcht_fee_rate_debit_t1", rs.getString("mcht_fee_rate_debit_t1"));
            map.put("mcht_fee_rate_credit_t0", rs.getString("mcht_fee_rate_credit_t0"));
            map.put("mcht_fee_rate_credit_t1", rs.getString("mcht_fee_rate_credit_t1"));
            map.put("mcht_fee_amount_debit_max", rs.getString("mcht_fee_amount_debit_max"));
            map.put("create_time", rs.getDate("create_date"));
            map.put("update_time", rs.getDate("update_date"));
            list.add(map);

        }
        return list;
    }

    private int insertMchtRegistOrder(Connection conn, Object obj) throws SQLException {
        String platOrderId ="R1910141420000060150";// IdUtil.createPlatCommonId("R", "0");
        Map rs = (Map) obj;
        String sql = "INSERT INTO mcht_chan_registe_order " +
                " (id,name,status,result,mcht_order_id,plat_order_id,chan_id, chan_code, mcht_id, mcht_code, chan_mcht_paytype_id, op_mcht_id, " +
                "chan_bind_id, province, city, district, address, mcht_type, chan_oa_id, chan_mcht_no, chan_biz_code, op_type," +
                "mcht_fee_rate_wx_t0, mcht_fee_rate_wx_t1, mcht_fee_rate_alipay_t0, mcht_fee_rate_alipay_t1, mcht_fee_rate_debit_t0, mcht_fee_rate_debit_t1, mcht_fee_rate_credit_t0, mcht_fee_rate_credit_t1, mcht_fee_amount_debit_max, " +
                "create_time, update_time) " +
                "VALUES " +
                "('510221900013044','"+rs.get("name")+"','2','2','" + platOrderId + "','" + platOrderId + "','yitong','yitong','" + rs.get("id") + "','" + rs.get("id") + "','chan_mcht_paytype_id','2001011000430888'," +
                "'" + rs.get("chan_mcht_no") + "','" + rs.get("province") + "','" + rs.get("city") + "','" + rs.get("district") + "','" + rs.get("address") + "','" + rs.get("mcht_type") + "','" + rs.get("chan_oa_id") + "','" + rs.get("chan_mcht_no") + "','" + rs.get("chan_biz_code") + "','0'," +
                "'" + rs.get("mcht_fee_rate_wx_t0") + "','" + rs.get("mcht_fee_rate_wx_t1") + "','" + rs.get("mcht_fee_rate_alipay_t0") + "','" + rs.get("mcht_fee_rate_alipay_t1") + "','" + rs.get("mcht_fee_rate_debit_t0") + "','" + rs.get("mcht_fee_rate_debit_t1") + "','" + rs.get("mcht_fee_rate_credit_t0") + "','" + rs.get("mcht_fee_rate_credit_t1") + "','" + rs.get("mcht_fee_amount_debit_max") + "'," +
                "'" + rs.get("create_time") + "','" + rs.get("update_time") + "')";
        System.out.println( " sql= " + sql);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        boolean rs2 = pstmt.execute();
        return 0;
    }


    /**
     * 数据库连接
     */
    private static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/pay_pro?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        String username = "root";
        String password = "root";
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

    public static void main(String[] args) throws Exception {
        new ReadMchtInfo2MchtRegisteOrder().proccess();
//        String plat_order_id = IdUtil.createPlatCommonId("R", "0");
//        System.out.println(plat_order_id);
    }
}
