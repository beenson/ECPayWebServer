package Config;

import Util.PropertiesUtil;
import lombok.Getter;
import lombok.Setter;

public class WebConfig {

    @Getter
    @Setter
    public static int port = 8080;

    public static void loadSetting() {
        PropertiesUtil.loadProperties("setting/web.ini");
        port = PropertiesUtil.getProperty("port", port);
    }

}
