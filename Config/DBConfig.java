package Config;

import Util.PropertiesUtil;
import lombok.Getter;
import lombok.Setter;

public class DBConfig {

    @Getter
    @Setter
    private static String host = "127.0.0.1";
    @Getter
    @Setter
    private static int port = 3306;
    @Getter
    @Setter
    private static String dbName = "db";
    @Getter
    @Setter
    private static String username = "root";
    @Getter
    @Setter
    private static String password = "";

    public static void loadSetting() {
        PropertiesUtil.loadProperties("setting/db.ini");
        setHost(PropertiesUtil.getProperty("host", host));
        setPort(PropertiesUtil.getProperty("port", port));
        setUsername(PropertiesUtil.getProperty("username", username));
        setDbName(PropertiesUtil.getProperty("dbName", dbName));
        setPassword(PropertiesUtil.getProperty("password", password));
    }

}
