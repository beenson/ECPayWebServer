package Util;

import lombok.Getter;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {

    @Getter
    private static final Properties props = new Properties();

    /**
     *  取得伺服器資料夾路徑
     * @return
     */
    public static String getPath() {
        return System.getProperty("path", "");
    }

    /**
     *  取得伺服器路徑加上path路徑
     * @param path
     * @return
     */
    public static String getPath(String path) {
        return (System.getProperty("path", "") + path);
    }

    /**
     * 輸入資料夾路徑, 得到資料夾路徑下的所有檔案名稱
     * @param path 路徑
     */
    public static String[] getFileList(String path){
        String[] fileList = null;
        try{
            File folder = new File(path);
            fileList = folder.list();
        } catch (Exception ex) {
            System.out.println("讀取\"" + getPath(path) + "\"檔案失敗 " + ex);
        }
        return fileList;
    }

    /**
     * 載入設定檔
     */
    public static void loadProperties(String path) {
        try {
            FileInputStream in = new FileInputStream(getPath(path));
            BufferedReader bf = new BufferedReader(new InputStreamReader(in, PropertiesUtil.codeString(getPath(path))));
            props.load(bf);
            bf.close();
        } catch (IOException ex) {
            System.out.println("讀取\"" + getPath(path) + "\"檔案失敗 " + ex);
        }
    }

    public static void setProperty(String prop, String newInf) {
        props.setProperty(prop, newInf);
    }

    public static void setProperty(String prop, boolean newInf) {
        props.setProperty(prop, String.valueOf(newInf));
    }

    public static void setProperty(String prop, byte newInf) {
        props.setProperty(prop, String.valueOf(newInf));
    }

    public static void setProperty(String prop, short newInf) {
        props.setProperty(prop, String.valueOf(newInf));
    }

    public static void setProperty(String prop, int newInf) {
        props.setProperty(prop, String.valueOf(newInf));
    }

    public static void setProperty(String prop, long newInf) {
        props.setProperty(prop, String.valueOf(newInf));
    }

    public static void removeProperty(String prop) {
        props.remove(prop);
    }

    public static String getProperty(String s, String def) {
        String property = props.getProperty(s);
        if (property != null) {
            return property;
        }
        System.out.println("缺少設定參數::"+s+" \t\t 採用預設::"+def);
        return def;
    }

    public static boolean getProperty(String s, boolean def) {
        return getProperty(s, def ? "true" : "false").equalsIgnoreCase("true");
    }

    public static byte getProperty(String s, byte def) {
        String property = props.getProperty(s);
        if (property != null) {
            return Byte.parseByte(property);
        }
        System.out.println("缺少設定參數::"+s+" \t\t 採用預設::"+def);
        return def;
    }

    public static short getProperty(String s, short def) {
        String property = props.getProperty(s);
        if (property != null) {
            return Short.parseShort(property);
        }
        System.out.println("缺少設定參數::"+s+" \t\t 採用預設::"+def);
        return def;
    }

    public static int getProperty(String s, int def) {
        String property = props.getProperty(s);
        if (property != null) {
            return Integer.parseInt(property);
        }
        System.out.println("缺少設定參數::"+s+" \t\t 採用預設::"+def);
        return def;
    }

    public static long getProperty(String s, long def) {
        String property = props.getProperty(s);
        if (property != null) {
            return Long.parseLong(property);
        }
        System.out.println("缺少設定參數::"+s+" \t\t 採用預設::"+def);
        return def;
    }

    public static String codeString(String fileName) throws FileNotFoundException {
        return codeString(new File(fileName));
    }

    /**
     * 判斷檔案的編碼格式
     *
     * @param file :file
     * @return 檔案的編碼格式
     * @throws java.io.FileNotFoundException
     */

    public static String codeString(File file) throws FileNotFoundException {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
        int p = 0;
        try {
            p = (bin.read() << 8) + bin.read();
            bin.close();
        } catch (IOException ex) {
        }
        String code;

        switch (p) {
            case 0xFFFE:
                code = "Unicode";
                break;
            case 0xFEFF:
                code = "UTF-16BE";
                break;
            case 0xEFBB:
            default:
                code = "UTF-8";
        }
        return code;
    }
}