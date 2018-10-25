import com.sys.common.util.RSAUtils;

import java.net.URLDecoder;

public class TestSDK {


    private static String key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKF5Tx9UEq7Kxf6LWBMlmMLluCFXN5MaIPxOLCMDEnAPncbu4boryOXXoJ6xC4uMOSiAC7rQKGD6sVgCK8vKcsmo/nkTQb7cVFXgAmw7u3+Q+VNROhBkHcS4x4e66BRGmDCCMxfgp6H6b/xHwqtJOC0ELR9YdbanhlGdYX3UWm2ZAgMBAAECgYAtKZrBrmgTT0+VZC/cGxXg3RNk79nxYMt9Bfg+SqurgBKnA0VLJtfnwSmBeQ87lpqeA0JEm9fPWA6mCOVq+FKmNQx9UjFCqE3ngsNdM1SLZl6XUIFWWTlbjcqXlq77MP+j8/+3eHZvxy12j9cwv3bG1uslscQBMy8p/cIgMPz1IQJBAPJAebl9XrlDxjbE/AyYQFZ+CE+vImy4bkF1Qe2l/WxjuiXRBvozn76MR9YhltSWbVvZH0ouKj5SeqgkBTDeX30CQQCqo0LTYF2oNdp7h8tt7uQ9cUwxGjXpi6ZrnZ1oYvCAZAu4XnE1XQRcuHgepUoawI3GTLqv31HDq5jhII/c3plNAkA8yZsBxYjIWk5ZnfJVQrsaOQPi4uJzf5ADEuZZOMavtRKXvKzQibWy/cZk4AbWD20fmAr29UCaZN3sZSV8TsCNAkB5ZkHK5Mp9PCM+s7P1eHqFhC+y13T3vVUfRzmP5KQ6k/MKrpZYYt8RoGRFWU9JjSjQphJDwdwIG/NeZ64yc2QZAkEA3Cv7KWrDRw9uGVRYl2g4dFOzUJ33mdGDUHF13A98qgW9itbebHeejgGndrNffgKQYrx+S2/y/E7XjG8D5VV9Ng==";
    public static void main(String[] args) {
        String data = "BRotL%2FpPXPRGxps%2BTHl21Q1lzoFU90lNhoZ8f9AZyvzuOhs0fI3l8w8OLfa%2FO8YXMH78qaq%2FiLvDry%2FZei%2FIZiO6bWbd%2BCQY8gWJ5Hhi32CA3c81ZEIohZCHX70n2z6bJjyybQOsUkhnP1%2Bs6dgmCNxers36pcldXI4d2aNJvNg%3D";
        try {
            data = URLDecoder.decode(data, "utf-8");
            String srcData = RSAUtils.decrypt(data, key);
            System.out.println("srcData:" + srcData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
