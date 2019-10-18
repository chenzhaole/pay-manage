package czl;

import com.sys.common.util.DateUtils2;
import com.sys.common.util.ExcelUtil;
import com.sys.common.util.IdUtil;
import com.sys.trans.api.entry.Registe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入商户信息
 * <p>
 * 读取商户信息xls文件to表mcht_info
 * <p>
 * Created by chenzhaole on 2019/9/25.
 */
public class ReadMchtImportFile2db {

    public static int seq = 0;


    public Object proccess() {
        Connection conn = null;
        try {
            conn = getConn();
            String filePath = "/Users/chenzhaole/Documents/syyfb_mcht_list_0924_1.xlsx";

            List<String[]> recordList = ExcelUtil.readexcel(new File(filePath), filePath);

            for (int i = 2; i < recordList.size(); i++) {

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

    /**
     * 1,读取excel,生成Registe对象
     *
     * @return
     */
    private List<Registe> readExcel() throws Exception {
        List<Registe> registeList = new ArrayList<Registe>();
        String txtFile = "/Users/chenzhaole/Documents/mcht_list_syyfb_" + DateUtils2.getNowTimeStr() + ".txt";

        String filePath = "/Users/chenzhaole/Documents/syyfb_mcht_list_0924_1.xlsx";
        List<String[]> rowList = ExcelUtil.readexcel(new File(filePath), filePath);


        FileWriter filewriter = new FileWriter(txtFile);
        System.out.println("list中的数据打印出来");

        for (int i = 2; i < rowList.size(); i++) {//行

            String[] column = rowList.get(i);
            Registe r = new Registe();

            r.setOperatorId(column[39]);//商户系统上边,或,商户系统管理员账号
            r.setName(column[1]);
            r.setNickName(column[2]);
            r.setProvinceName(column[4]);
            r.setCityName(column[5]);
            r.setDistrictName(column[6]);
            r.setAddress(column[7]);
            r.setBusinessLicenseCode(column[14]);//18位营业执照号不能重复


            r.setLegalName(column[21]);//法人
            r.setLegalCertType("1");//法人证件类型：1-身份证；2-护照；3-军官证；4-士兵证；5-回乡证；6-临时身份证；7-户口簿；8-警官证；9-台胞证；11-其它
            r.setLegalCertNo(column[23]);//法人身份证
            r.setLegalCerExpiryDate(column[24]);//1，非长期；2，长期
            r.setLegalCerStartDate(column[25]);
            r.setLegalCerEndDate(column[26]);


            r.setSettleAccountName(column[42]);//结算账户名
            r.setSettleBankAccountNo(column[43]);//结算账号
            r.setSettleBankAcctType("1".equals(column[41]) ? "1" : "2");//账户类别, 1=对私,2=对公
            r.setSettleBankProvince(column[44]);
            r.setSettleBankCity(column[45]);
            r.setSettleBankName(column[46]);
            r.setBankSettleCycle("d1");//结算周期(d0,d1,t0,t1……)
            r.setBankRate("0");//费率(百分比)
            r.setSettleSubBankName(column[90]);//支行名称
            r.setSettleLineCode(column[01]);//联行号

            r.setAuthCerType("1");//结算人证件类型
            r.setAuthCerNo(column[50]);
            r.setAuthCerStartTime(column[52]);
            r.setAuthCerEndTime(column[53]);

            r.setMchtFeeRateWxT0(column[59]);//微信支付手续费
            r.setMchtFeeRateWxT1(column[59]);//微信支付手续费
            r.setMchtFeeRateAlipayT0(column[67]);//支付宝手续费
            r.setMchtFeeRateAlipayT1(column[67]);//支付宝手续费


        }
        filewriter.flush();
        filewriter.close();
        return registeList;
    }

    /**
     * 根据商户类型,生成商户ID
     * (sign_type) 1=支付商户,2=身边商户,3=服务商,4=代理商,51=个人,52=个体商户,53=企业,54=事业单位
     *
     * @param signType 签约属性
     * @return
     */
    public static String createMchtId(String signType) {

        String bizId = signType + "0";
        String date = DateUtils2.getNowTimeStr("mmss");
        int random = IdUtil.buildRandom(4);

        ReadMchtImportFile2db.seq = ReadMchtImportFile2db.seq + 1;
        String tmp = "000000000000000" + seq;
        String seqNo = tmp.substring(tmp.length() - 4, tmp.length());
        String mchtId = bizId + date + seqNo + random;

        return mchtId;
    }


    public int insert2db(Registe r, Connection conn) {
        int rs = 0;

        PreparedStatement pstmt = null;
        try {

            String mchtType = r.getOrderId().substring(0, 2);
            String sql = "INSERT INTO mcht_info VALUES ('" + r.getOrderId() + "', 'syyfb', '" + r.getOperatorId() + "', '" + r.getOrderId() + "', '" + r.getName() + "', '" + r.getName() + "', '" + IdUtil.getUUID() + "', '" + mchtType + "', '0', '', '1', '', '', " +
                    "'" + r.getLegalName() + "', '" + r.getLegalCertType() + "', '" + r.getLegalCertNo() + "', '" + r.getCityCode() + "', '" + r.getBusinessLicenseCode() + "', null, '2039-09-19 09:38:33', '" + r.getBusinessScope() + "', null, null, null, null, " +
                    "'" + r.getAddress() + "', '', '" + r.getTel() + "', '', '', null, null, null, null, null, '', '', '', '', '', null, '1,2', null, null, null, null, null, '', null, '9', '', null, null, '0.00', " +
                    "'" + r.getSettleAccountName() + "', '" + r.getSettleBankAccountNo() + "', '" + r.getSettleBankName() + "', '', '" + r.getSettleBankCity() + "', null, null, null, null, null, '0.00', null, null, null, null, null, null, null, null, '3310', " +
                    "'" + DateUtils2.getNowTimeStr() + "', '" + DateUtils2.getNowTimeStr() + "', null, null, null, null, '', null, null, '', '', '', '', '', '', '2', null, '', null, null, null, null, '1', '1', '0', '0', null, null, null, null, " +
                    "'" + r.getProvinceCode() + "', '" + r.getDistrictCode() + "', null, '" + r.getAuthCerStartTime() + "', '" + r.getAuthCerEndTime() + "', 'qf', '" + r.getSettleBankAccountMobile() + "', " +
                    "'" + r.getMchtFeeRateWxT0() + "', '" + r.getMchtFeeRateWxT1() + "', '" + r.getMchtFeeRateAlipayT0() + "', '" + r.getMchtFeeRateAlipayT1() + "', '" + r.getMchtFeeRateDebitT0() + "', '" + r.getMchtFeeRateDebitT1() + "', '" + r.getMchtFeeRateCreditT0() + "', '" + r.getMchtFeeRateCreditT1() + "', '" + r.getMchtFeeAmountDebitMax() + "', " +
                    " 'oaid', 'chan_biz_code','" + r.getMccCode() + "','" + r.getMccName() + "','0')";
            System.out.println(sql);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeUpdate();
            System.out.println("插入数据库返回值 rs=" + rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rs;
    }

    /**
     * 数据库连接
     */
    private static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/mcht_info_syyfb_0925?useUnicode=true&characterEncoding=utf-8&useSSL=false";
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
        new ReadMchtImportFile2db().proccess();
    }

}
