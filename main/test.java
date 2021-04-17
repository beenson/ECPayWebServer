package main;

import Config.Config;
import Util.FileUtil;
import model.User;


public class test {

    public static void main(String [] argv) {
        System.out.println("目前執行路徑" + FileUtil.getWorkingPath());
        System.out.println("========================================");
        System.out.println("正在載入設定");
        Config.loadSetting();
        User usr = new User(1);
        System.out.println(usr.toString());

    }


}
