package Util;

public class IntegerUtil {

    public static boolean isInteger(String str) {
        int value;
        try{
            value = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isPositiveInteger(String str) {
        int value;
        try{
            value = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return value > 0;
    }

}
