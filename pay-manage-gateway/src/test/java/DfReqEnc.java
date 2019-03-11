import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.df.TradeDFCreateOrderRequest;
import com.sys.boss.api.entry.trade.response.df.DFCreateOrderResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.AESUtil;
import com.sys.common.util.BeanUtils;
import com.sys.common.util.RSAUtils;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatProduct;
import com.sys.core.dao.dmo.PlatProxyBatch;
import com.sys.gateway.common.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.TreeMap;

public class DfReqEnc {
    public static void main(String[] args) {
        String data = "{\"head\":{\"biz\":\"df101\",\"mchtId\":\"2000117000721734\",\"version\":\"20\"},\"sign\":\"3983EE849DB069426680152311CAEA16\",\"encryptKey\":\"fwmt1CoOnRCTkLIZis8PqtzuUXinptPGL59y/jDfT8U9wpw70wa6cbvjf2t4BUtSfSRSFcAf5s6ttWeojGOZMht6mqLltBo9eefczxjH1HvSTFyqnnUdQfe+X9oYWrbcP6Pmgb+KDAeR/g+jxrmN5QiO0B1Q8XuFBnIKFUNg4Z0=\",\"body\":\"1FvNzaOkZST8ZCWtqL1LfREqdI9ETRn4+Pp3t7yroAB0Z4Aa/p24TZLhcxtSVEb//jwQhhHw9NcITZw4/F1vQ5HbZCCHyEjkN476UCMAIsmyE/MuHiX8krYNivb/Tz1rBs36p0vNUcvPtd08VrV+D/ogJGwZwE1C0Poq8Yb7v8LfNx6GdHuT5/iIHQ1KyOmHwga+nYXDK8MA07pWGS73oW5bDVedt4lXJPQxF4WRHVHBLI31Xt6+j9RGcESWsa1EbiUs3oLEkiTys3Z3hKnBTbwA5Hpm1HRT25hNCu2tZ3bBSyQKjPs4IoSCVXffr70GQIVpyCepNV/xlMp8Q3huY6EUBr/lW922bT97V+qQoDqiahkEJlJYvZsYsWHvKb5zQwLJImjkF8uMPY4TYaK3jQ==\"}";
            if (data.endsWith("=")) {
                data = data.substring(0, data.length() - 1);
            }
            DFCreateOrderResponse resp = new DFCreateOrderResponse();
            DFCreateOrderResponse.DFCreateResponseHead head = new DFCreateOrderResponse.DFCreateResponseHead();

            try {
                data = URLDecoder.decode(data, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //校验请求参数
            CommonResponse checkResp = checkParam(data);


    }



    public static CommonResponse checkParam(String paramStr) {

        String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKb75n2qDDofEg4pZsDHWFtVTs+iSYEEWWibAWEF1TYsicBBZ8cQlMYz7YRf0Pnn0nddfFw9JVqdV0zgtxOInJUmgVK6UiDTXBHbrKTunMarL5AlG/W598vX6i1FuCYMuGN5XXrXjwKbAYbePvGRU6qu/9+yI5WJh3URl2yK36+VAgMBAAECgYAqrYsVZxbE7BgDQpWeg9STU1RXQRUkQmZPk/5gO8toky2J062gdR5xIeh8wrh4hsWF+IDGWmJnrHsZjP19opbSJItbOvYnZ0EphvWfbYTruX41Cw1gTeyCP3N0UzbrvczuJ+pDOWiMm7BtAiXop+P5DGkrb4gYEPkJM38wnImYqQJBAPjBQkqXCvqwFeahLYiev8TqMhDsnZ7OFojzPHf88cG6kG3EiMVQz8wXGNzkMd/gy3NiysBCvJbNcBf4G5CCtzMCQQCr2PMGpno87GhoW2yXOwzyClWIBzBRf7i7izHe870pY07cJptjR6yNuxIM7OigKy16m/QDZoYYRt7J3tVOsd4XAkEAxfNEhYbcvdESHI12ZpmtkU2subsRUyY82I53OCSZXcjQ1gjnrd04/vgCWQVeiwGf5Rpgrc5ttLOW1/wgM0dR8QJAEjwA/ZGootJK+dfJm/puEzi5fqeUnvE4ft30Or4OMlgRRBbsogo26wsQjUKOJICwYLQvnjTAwc6zUSRN6f3BnQJAAyviDHadBZ51/Z0zTHz/q+PxwRmxccA4fO1QAYVvmNzbPJoannJTPVY5ncKuMUGJunWCI92sxrBXmuwNxOKyDA==";
        CommonResponse checkResp = new CommonResponse();
        String mbId = "";
        try {
            if (paramStr.endsWith("=")) {
                paramStr = paramStr.substring(0, paramStr.length() - 1);
            }

            //解析请求参数
            TradeDFCreateOrderRequest tradeRequest = JSON.parseObject(paramStr, TradeDFCreateOrderRequest.class);


            //校验head
            TradeReqHead head = tradeRequest.getHead();
            mbId = head.getMchtId() + "-";

            MchtInfo mcht = null;

            tradeRequest.setMchtInfo(mcht);

            //校验body
            String aesKey = null;//RES解密key
            try {
                aesKey = RSAUtils.decrypt(tradeRequest.getEncryptKey(), PRIVATE_KEY);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String body = null;//AES解密body
            try {
                body = AESUtil.Decrypt(JSON.toJSONString(tradeRequest.getBody()), aesKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TradeDFCreateOrderRequest.DFCreateOrderRequestBody requestBody = JSON.parseObject(body, TradeDFCreateOrderRequest.DFCreateOrderRequestBody.class);



            mbId = mbId + requestBody.getBatchOrderNo() + "-";
            tradeRequest.setRequestBody(requestBody);



            checkResp.setData(tradeRequest);
        } catch (Exception e) {

        }

        return checkResp;
    }
}
