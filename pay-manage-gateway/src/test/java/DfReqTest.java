import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import java.text.SimpleDateFormat;
import java.util.*;

public class DfReqTest {


    private static JSONObject getHeadJson(String  mchtId, String version , String biz) {
        JSONObject head = new JSONObject();
        head.put("mchtId", mchtId);
        head.put("version", version);
        head.put("biz", biz);
        return head;
    }

    private static Map<String, String> getBodyMap(String batchOrderNo, String totalNum, String totalAmount, String notifyUrl, String detail) {
        Map<String, String> map = new HashMap();
        try {
            map.put("batchOrderNo", batchOrderNo);
            map.put("totalNum", totalNum);
            map.put("totalAmount", totalAmount);
            map.put("notifyUrl", notifyUrl);
            map.put("detail", detail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    public static void main(String[] args){

        /*try{
            //String payUrl = "/df/gateway/req/"; //
            String payUrl = "http://114.115.206.62:12080/df/gateway/req/";
            String key = "e201f962a16f4e7caeda91196c74092d";			//商户KEY
            String MCHT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcR+XUf3qvtjmTwwLa4rS0mqVQHMXVGTWjzUmJsssLTAbmEMuvlyJKpnvDDERBA0s7II4AAp+G0EbUzh/+KKE2A+Z+m6ApN5CUUIm3orwO6OM0VHp2VUr8PhDFOwPud792f0fnLL0/V28D7c7OazXMvhUNkGieeCtPMERnFcwUEQIDAQAB";
            String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJxH5dR/eq+2OZPDAtritLSapVAcxdUZNaPNSYmyywtMBuYQy6+XIkqme8MMREEDSzsgjgACn4bQRtTOH/4ooTYD5n6boCk3kJRQibeivA7o4zRUenZVSvw+EMU7A+53v3Z/R+csvT9XbwPtzs5rNcy+FQ2QaJ54K08wRGcVzBQRAgMBAAECgYEAiNT2qTCYleJB1VsoU8etGnhQh605ldRFv86NNZAi8Rg27hcEkBAOyTetIuDJnfapVvKxAddKH7qaWdbD8j9Wrdn5QhidOZmlYQTofa7oUnXdB0tONPARnaYPkES+dFKLoMxvZrDFu/DQpv7TE1T3zl3IBE0XndEaRYzdfBU1NAECQQDaDvCN7D4sszgRIcBAvkI2qJ1eMI40PEzBdC1lLtW99ZWQYFyDjy+I94i6oj9BSRuz3RUJs/QWpu5BXf8ZTntBAkEAt3ktkBKWlq0lvKdEQESBvUFaPIJOiPhRN/sdj2R6G/0kpMppnUwcUFg1J1eTTueRgQrDU/CS4AGrfdtTBOB00QJAVBphA4wzqs/w9wKagR4gsa41DAgUQOdk/1RM0fLPc7XN2uKfrApE3kIC1WEQnqCQ8714iMaEhVwbFtEnO95+QQJBALSXtl1lroxJiieTjP6lb/7VYPjGWn8/zNIJfyMxh3AdUABUrHiD/iqnaEp9TSYRkYsvCOyKlEeh3SdYxvK57MECQCbnO9JvI2dayFa0cc6+DigwyaEau3rFxqCfvzefxpikGTzwM5bRFzMV3O0Fq5F+7WT+m9xd2o9/R54lKDOixVk=";	//平台下发的私钥  就是页面点击生成的私钥

            JSONObject data = new JSONObject();

            String  mchtId = "2000814000811305";	//商户号
            String version ="20";
            String biz ="df101";

            JSONObject head = getHeadJson(mchtId, version, biz);
            data.put("head", head);



            String batchOrderNo = RandomStringUtils.randomNumeric(7);
            String totalNum= "50";		//总数
            String totalAmount= "5000";	//总金额
            String notifyUrl= "http://baidu.com";	//回调地址
            //String detail= "[{\"accType\":\"0\",\"amount\":\"500\",\"bankCode\":\"\",\"bankCardName\":\"--收款人名称----\",\"bankCardNo\":\"---收款人卡号--\",\"bankName\":\"---银行名称--\",\"seq\":\"001\"}]";
            String detail = "[{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"001\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"002\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"003\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"004\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"005\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"006\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"007\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"008\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"009\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0010\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0011\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0012\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0013\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0014\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0015\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0016\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0017\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0018\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0019\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0020\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0031\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0032\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0033\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0034\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0035\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0036\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0037\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0038\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0039\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0040\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0041\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0042\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0043\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0044\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0045\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0046\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0047\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0048\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0049\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0050\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0051\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0062\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0063\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0064\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0065\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0066\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0067\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0068\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0069\"},{\"accType\":\"0\",\"amount\":\"100\",\"bankCode\":\"ICBC\",\"bankCardName\":\"李浩\",\"bankCardNo\":\"62122602001503\",\"bankName\":\"中国工商银行\",\"seq\":\"0070\"}]";

            //获取body
            Map<String, String> bodyMap = getBodyMap(batchOrderNo, totalNum, totalAmount, notifyUrl, detail);
            System.out.println(JSONArray.toJSONString(bodyMap));

            //加密body中的detail   是我们下发的统一公钥
            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCm++Z9qgw6HxIOKWbAx1hbVU7PokmBBFlomwFhBdU2LInAQWfHEJTGM+2EX9D559J3XXxcPSVanVdM4LcTiJyVJoFSulIg01wR26yk7pzGqy+QJRv1uffL1+otRbgmDLhjeV16148CmwGG3j7xkVOqrv/fsiOViYd1EZdsit+vlQIDAQAB";
            String aesKey = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
            String body = JSON.toJSONString(bodyMap);
            System.out.println(body);

            //用AES加密detail
            String bodyAES = AESUtil.Encrypt(body, aesKey);
            //用RSA公钥加密AES的key
            String aesKeyRSA = RSAUtils.encrypt(aesKey, publicKey);

            data.put("encryptKey", URLEncoder.encode(aesKeyRSA, "UTF-8"));
            data.put("body", URLEncoder.encode(bodyAES, "UTF-8"));

            //签名
            String sign = SignUtil.md5Sign(bodyMap, key);
            data.put("sign", sign);

            System.out.println("请求参数: \r\n" + data.toString());
            String respStr = post(payUrl, data.toJSONString());
            System.out.println(respStr);

            respStr = URLDecoder.decode(respStr, "UTF-8");
            JSONObject resJson = JSON.parseObject(respStr);
            JSONObject resHead = resJson.getJSONObject("head");

            //返回成功信息
            if (resHead.getString("respCode").equals("0000")) {

                //AES解密body
                String aesKeyResp = RSAUtils.decrypt(resJson.getString("encryptKey"), PRIVATE_KEY);
                String bodyResp = AESUtil.Decrypt(JSON.toJSONString(resJson.getString("body")), aesKeyResp);
                System.out.println(bodyResp);

                SignUtil.checkSign(JSON.parseObject(bodyResp, new TypeReference<TreeMap<String, String>>() {
                }), resJson.getString("sign"), key);

                resJson.put("body", bodyResp);
                respStr = resJson.toString();
                System.out.println(respStr);

            }
        }catch (Exception e){

        }*/
        try{
            String url = "http://106.2.6.41:15680/accountAmount/taskInsertCurrentDayAcctAmount";
            String startDate = "2018-06-05";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(startDate));
            //6  25
            //7  31
            //8  31
            //9  30
            //10 31
            //11 30
            //12 31

            for(int i = 0 ; i< 205; i++){
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                String execDate = simpleDateFormat.format(calendar.getTime());
                System.out.println("执行时间为:" + execDate);

                String resp = post(url + "?day=" + execDate, "");
                System.out.println("执行响应为:" +resp);
                Thread.sleep(1000);
            }
        }catch (Exception e){

        }

    }

    public static String post(String url, String postContent) {
        String result = "";
        HttpClient httpclient = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 20000);
            HttpConnectionParams.setSoTimeout(params, 20000);
            HttpPost httppost = new HttpPost(url);
            StringEntity reqEntity = new StringEntity(postContent, "UTF-8");
            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");

            // 设置请求的数据
            httppost.setEntity(reqEntity);
            // 执行
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (Exception e) {

        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return result;
    }
}
