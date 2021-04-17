package Config;

import Util.PropertiesUtil;
import lombok.Getter;
import lombok.Setter;

public class EcpayConfig {

    @Getter
    @Setter
    public static String HashKey = "";
    @Getter
    @Setter
    public static String HashIV = "";
    @Getter
    @Setter
    public static int MerchantId = 0;
    @Getter
    @Setter
    public static String ECPayIp = "127.0.0.1";
    @Getter
    @Setter
    public static int ECPayPort = 8080;

    public static void loadSetting() {
        PropertiesUtil.loadProperties("setting/ecpay.ini");
        HashKey = PropertiesUtil.getProperty("HashKey", HashKey);
        HashIV = PropertiesUtil.getProperty("HashIV", HashIV);
        MerchantId = PropertiesUtil.getProperty("MerchantId", MerchantId);
        ECPayIp = PropertiesUtil.getProperty("ECPayIp", ECPayIp);
        ECPayPort = PropertiesUtil.getProperty("ECPayPort", ECPayPort);
    }

}
