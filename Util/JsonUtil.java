package Util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil {

    public static String toString(JSONObject json) {
        return JSON.toJSONString(json, SerializerFeature.DisableCircularReferenceDetect);
    }

    public static JSONObject errToken() {
        JSONObject json = new JSONObject();
        json.put("error", "wrong token.");
        return json;
    }

    public static JSONObject errPassword() {
        JSONObject json = new JSONObject();
        json.put("error", "wrong Password.");
        return json;
    }

    public static JSONObject errOldPassword() {
        JSONObject json = new JSONObject();
        json.put("error", "wrong oldPassword.");
        return json;
    }

    public static JSONObject errPath() {
        JSONObject json = new JSONObject();
        json.put("error", "error request path.");
        return json;
    }

    public static JSONObject errId() {
        JSONObject json = new JSONObject();
        json.put("error", "wrong Id.");
        return json;
    }

    public static JSONObject unknown(String name) {
        JSONObject json = new JSONObject();
        json.put("error", "Unknown "+name+".");
        return json;
    }

    public static JSONObject errParam() {
        JSONObject json = new JSONObject();
        json.put("error", "wrong parameter.");
        return json;
    }

    public static JSONObject duplicateEmail() {
        JSONObject json = new JSONObject();
        json.put("error", "duplicate Email.");
        return json;
    }

    public static JSONObject unknownErr() {
        JSONObject json = new JSONObject();
        json.put("error", "Unknown Error.");
        return json;
    }

    public static JSONObject unAuthorized() {
        JSONObject json = new JSONObject();
        json.put("error", "unAuthorized.");
        return json;
    }
}
