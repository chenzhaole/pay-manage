package czl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import com.sys.trans.api.entry.Result;

import com.sys.trans.exception.TransException;
import czl.util.HttpUtil;
import czl.util.SignUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenzhaole on 2019/5/10.
 */
public class BatchQueryChannUpdateDB {

    public int process(String queryUrl, String chanMchtId,String chanMchtPassword,String opAccount,String platOrderNo) throws Exception {
        Result result = new Result();
        result.setRespCode(Result.RESP_CODE_SUCCESS);

        try {
            String respData =null;
            Map<String,String> params = getReqParam( chanMchtId, chanMchtPassword, opAccount, platOrderNo);
            Map<String,String> respMap  = HttpUtil.postConnManager(queryUrl,params);
            System.out.println("上游返回参数" +JSONObject.toJSONString(respData));

            if (respMap ==null) {
                result.setStatus(Result.STATUS_UNKNOWN);
                System.out.println("交易返回结果为空"  + "平台判断为未知");
                return -1;
            }

            String sysSign=respMap.get("X-QF-SIGN");
            respMap.remove("X-QF-SIGN");
            String oriString = respMap.get("oriString");
            if(!SignUtil.checkSign(oriString,sysSign,chanMchtPassword,"")){
                result.setStatus(Result.STATUS_UNKNOWN);
                result.setRespMsg("验证签名不通过");
                System.out.println("平台验证签名不通道,平台判断为未知");
                return -2;
            }

            String status =respMap.get("respcd");
            String msg = respMap.get("resperr");
                /*data内的respcd参数返回码0000表示支付交易成功，data外的respcd参数返回码0000表示请求下单成功。
                 */
            if(!"0000".equals(status)){
                result.setStatus(Result.STATUS_FAIL);
                result.setRespMsg(msg);
                System.out.println("返回下单状态码："+status+"平台判断下单已经失败");
                return -3;
            }

            List<Map<String,String>> datails = JSON.parseObject(respMap.get("data"),
                    new TypeReference<List<Map<String,String>>>(){});
            if(datails == null || datails.size()==0){
                result.setStatus(Result.STATUS_UNKNOWN);
                result.setRespMsg("订单明细数据为空s");
                System.out.println("订单明细为空,平台判断下单已经未知");
            }
            Map<String,String> datail =datails.get(0);
            status =datail.get("respcd");
            msg = datail.get("errmsg");
                /*支付结果返回码 0000表示交易支付成功；
                1143、1145表示交易中，需继续查询交易结果； 其他返回码表示失败*/
            if("0000".equals(status)){
                result.setStatus(Result.STATUS_SUCCESS);
                System.out.println("查询返回结果" + status + "平台判断为成功");
            }else if("1143".equals(status) || "1145".equals(status)){
                result.setStatus(Result.STATUS_DOING);
                System.out.println("查询返回结果" +  status + "平台判断为支付中");
            }else {
                result.setStatus(Result.STATUS_FAIL);
                System.out.println("查询返回结果" +  status + "平台判断为失败");
            }
            result.setRespMsg(msg);
            result.setOriRespData(respData);
            result.setOriRespData(oriString);
            result.setChannOrderNo(datail.get("syssn"));
        } catch (Exception var11) {
            result.setRespCode(Result.ERROR_CODE_COMMON);
            throw new TransException(var11);
        }
        System.out.println("查询订单响应客户端请求参数[end]" + " Result:" + JSONObject.toJSONString(result));
        return 1;
    }

    protected  Map<String,String> getReqParam(String chanMchtId,String chanMchtPassword,String opAccount,String platOrderNo) throws Exception {
        Map<String,String> params = new HashMap<>();
        //子商户号，标识子商户身份，由统一分配；
        params.put("mchid", chanMchtId);
        //外部订单号查询，开发者平台订单号；
        params.put("out_trade_no",platOrderNo);
        String sign =SignUtil.md5Sign(params,chanMchtPassword,"");
        params.put("X-QF-APPCODE",opAccount);
        params.put("X-QF-SIGN",sign);
        return params;
    }

    public static void main(String[] args) throws Exception {
        String queryUrl="https://openapi.qfpay.com/trade/v1/query";
        String chanMchtId="lkDgKtlOeY";
        String chanMchtPassword="F9BE6251766D4C30BD5BDB7FAA260F3A";
        String opAccount="3B458B6B644E46329689B25BB99F1557";
        String platOrderNo="P1905101648460760180";
        int rs = new BatchQueryChannUpdateDB().process(queryUrl,chanMchtId,chanMchtPassword,opAccount,platOrderNo);
        System.out.println("---->>>>>> "+rs);


    }
}
