import com.sys.common.util.HttpUtil;

import java.io.IOException;

/**
 * Created by chenzhaole on 2019/7/26.
 */
public class MainTest_WXOAuth {
    public static void main(String[] args) throws Exception {
        String url ="https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=wxde0382a243819f04" +
                "&secret=SECRET" +
                "&code=061fpFp82C8EhL03pHm823VQp82fpFpK" +
                "&grant_type=authorization_code";
        String resp = HttpUtil.get(url);
        System.out.println("url: "+url);
        System.out.println("resp: "+resp);
    }

}
