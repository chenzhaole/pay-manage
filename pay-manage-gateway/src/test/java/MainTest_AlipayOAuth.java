import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.sys.common.util.HttpUtil;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by chenzhaole on 2019/7/26.
 */
public class MainTest_AlipayOAuth {


    public static String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNN92Y79CRjcvpAhSNC8WhTwDltdlo0OOjZ8ZPtD2LnhBgL4rGC6qBGgE8Lcapxw4iKMVjozvHmMSud51QNTeIemyEnkil0r8wj9BribFBL5nlTa3rh5RWjIxugHwE8M+tydrR5KZ1CsYmxc/LM8pbrI20e03HPe1lrp1ZWIfS22c8p89B9x5ttR8CC2sIrbbMAcJ7aTwau6n2fpAEJq8d2RT+GcZYQdAJdbsOixngz73/ub3Ea7YccfAnWX0mQkQYlDJM+CtziOVJBOysIh1nIW4F4j7iz9CEbMBvWm/w5geFzix5jDe2y7hwy51ISudvz4+sSCVXNTOiFjw6Bv9zAgMBAAECggEAfw+wBAZq3DwAJ6Pmh836wRwLi6PmfSfOWl1qEpby9WeABntgWrdub4DNca8iW+otblDO4cqiZAGxneUkF2H6mILGl0CvzvvjaLdNaSTX5vYEe0w8W3p+3qzRQi+65tshkZXYnBgmKZNLHOHJCGWOApQsYCK9pbKzolTNYPGGLBJEzjK9CVyJcqOFnflzJurg03yuA8dFh8TRmqjANeoHXd7u4Io+FoK1zb/g7hVBSyzQnhK0Ty03HO7f54ciLctIcPhtDZtkrwKe9Ei9+CxkHDmNXbPCfQ2G3gXT8GimcBbFFGwyWde3wPXPov+dv/qKoV/bkPb55pi5MKVTPoEvyQKBgQDGApfonvbnAdsySeTVThqEizQdFvUh+5GL0Q/V5xy/NxKX6MiwaGVZRACl9qBjWPIeAkn+s10hfUHQBbr2GsYUIKFqrHrdFukorHTxct9odxzAdtMF8WlMyzatQcC1UIKFs48F6B/LvVBDP1M0aLZJ+wUeOzfSuU9UH3eW4rpn1QKBgQC2k2ooaJ6Q4k1uYLXG7+aBN6zrF335Gumq+sJeg/SM05kYjS6+DgoJ6auiNPJyoRJM2jnsmZDmWAp+r3333HCHmDC/viXnTtfPgpkHGiYBJfvFawnJQbL5z9Oz+oepJRDPqXU6bybxjovnoRvQEyagifqmgetKg2L2jYcGVDF2JwKBgCznPafFFNzCMQEwfLJaqezQ3JqUMDbqo9D/MgYiCQTb6l2Erq5Cnmkl4LfCPBELhKyFfF5EMqR7kUcpZYKWA8FgvPpB7wLgRTOyGDsA/+TiziRfTe+VFXoSw2168casYU0MsSx3vW4ommEFpUrHTD9uq3R1nW0uFO1QzX/sHrWZAoGBAJJQALrxXGFvee4Cwqnyhx72pzSfVuzSjH/hBPMJfGl/CSmLuvHD/neDM3CCTele+3NrTxA04NI3q/FqYeDIX8XKSQbgMy/zFy/M0SXH5rz025eR9/25ENzxmA6bryv18Er62l9BxEvAmI2/prJRJptw99WIOC82q5A6SwLfZePxAoGAN92KLkU9mMHN8eRcSdl2ZGekjFIjJCfU3uR9HuRATbvUDDPpZqUnuAH73vgFtD9iFR7tSMJIsOH2EGscCdu8cgITJfGYjsDLeap3RImFMlTgQQuJKpMFcc8TLkiMBn7dpmv1/6nTtXIIXo35eUzuIh+qeU+b2MVlJzcVwXlnxS8=";
    public static String APP_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjTfdmO/QkY3L6QIUjQvFoU8A5bXZaNDjo2fGT7Q9i54QYC+KxguqgRoBPC3GqccOIijFY6M7x5jErnedUDU3iHpshJ5IpdK/MI/Qa4mxQS+Z5U2t64eUVoyMboB8BPDPrcna0eSmdQrGJsXPyzPKW6yNtHtNxz3tZa6dWViH0ttnPKfPQfcebbUfAgtrCK22zAHCe2k8Grup9n6QBCavHdkU/hnGWEHQCXW7DosZ4M+9/7m9xGu2HHHwJ1l9JkJEGJQyTPgrc4jlSQTsrCIdZyFuBeI+4s/QhGzAb1pv8OYHhc4seYw3tsu4cMudSErnb8+PrEglVzUzohY8Ogb/cwIDAQAB";
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiNyCocH0BP93v4uY5v+EvwDF9HpU8bzLtasbuqHwkAcHdld50WRKlRdg9VBdqFTbq2ful17efZdFEBj1nmq7D4yxgQQfZZzR+2O2gpmXrW2H227OyLcU0WV4455eMngAgs58x7aO1kwMmx6/lfuvw1Thf2eHNglY6k3JC/BpNwK63REFVMQV/M7n7AflRfcZ65CEmI55mjgon72vPAm18M0EczN6JlXzMS6e2v1aCicM58gjZ0pI3OYeUwiGF43xdaGwvyM/jfGap+GyVIhMBJyiJJoZ72gLDv5LjIKFWkl8uVdwPDCYGQhMqSsGPIANvQm1khih3o94Ms4A9gdFBwIDAQAB";


    public static String SERVICE_WINDOW_NAME = "易付宝商务";
    public static String ALIPAY_APP_ID = "2019081066188046";
    public static String CHARSET = "UTF-8";


    public static void main(String[] args) throws Exception {
        //https://openapi.alipaydev.com/gateway.do 。莫要跟正式环境弄混。正式的不带dev
        //APP_ID位沙箱里面写的appid。APP_PRIVATE_KEY为自己的私钥，用于报文签名.  ALIPAY_PUBLIC_KEY 这个是支付宝公钥,用于验签

        String auth_code = "5c9618b1d3604f94b52816213431PX55";

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                ALIPAY_APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(auth_code);//这个就是第一步获取的auth_code
        request.setGrantType("authorization_code");//这个固定值,参考https://docs.open.alipay.com/api_9/alipay.system.oauth.token
        AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
        System.out.println(JSON.toJSONString(oauthTokenResponse));
        if(oauthTokenResponse.isSuccess()){
            System.out.println(oauthTokenResponse.getUserId());
            System.out.println(oauthTokenResponse.getAlipayUserId());

        }else{
            System.out.println(oauthTokenResponse.getSubMsg());
        }

    }


}
