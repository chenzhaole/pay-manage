package czl;

import com.sys.common.util.DateUtils2;
import com.sys.common.util.ExcelUtil;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.trans.api.entry.Registe;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取商户表,查询易通通道商户ID
 * Created by chenzhaole on 2019/10/10.
 */
public class ReadDbQueryChan4MchtId {
    public static int seq = 0;


    public Object proccess() {

        Connection conn = null;
        try {
            conn = getConn();
            List<MchtInfo> list = selectDb(conn);
            for (MchtInfo mchtInfo : list) {
                String chanMchtId = queryMchtIdFromChan(mchtInfo);
                int rs = updateDb(mchtInfo);
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

    private List<MchtInfo> selectDb(Connection conn) throws Exception {
        List<MchtInfo> list = new ArrayList<>();

        String sql = "select * from mcht_info";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        int i = 0;

        while (rs.next()) {
            MchtInfo mchtInfo = new MchtInfo();
            mchtInfo.setId(rs.getString("id"));
            mchtInfo.setChanOaId(rs.getString("chan_oa_id"));
            list.add(mchtInfo);
        }
        return list;
    }

    private String queryMchtIdFromChan(MchtInfo mchtInfo) {
        return null;
    }

    private int updateDb(MchtInfo mchtInfo) {
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
        new ReadDbQueryChan4MchtId().proccess();
    }
}
