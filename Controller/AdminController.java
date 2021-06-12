package Controller;

import Util.DateUtil;
import Util.JsonUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import handler.jwt.AuthVerify;
import model.AnalysisRecord;
import model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdminController extends Controller {

    public String router(Headers hs, String[] path, HashMap<String, String> params) {
        String token = "";
        User usr = null;
        if (hs.getFirst("Authorization") != null) {
            token = hs.getFirst("Authorization").replace("Bearer ", "");
            usr = AuthVerify.getAuth(token);
        }
        //if (usr != null) {
        //    if (usr.getAdmin() > 0) {
                switch (path[2]) {
                    //管理權限限定
                    case "getAnalysisRecord":
                        return this.getAnalysisRecord(params).toJSONString();
                    case "getTodayAnalysisRecord":
                        return this.getTodayAnalysisRecord(params).toJSONString();
                    case "getDefaultDayAnalysisRecord":
                        return this.getDefaultDayAnalysisRecord(params).toJSONString();
                }
        //    }
        //}
        return "401";
    }

    public JSONObject getAnalysisRecord(HashMap<String, String> params) {
        String startStr = params.get("start");
        String endStr = params.get("end");
        String key = params.get("key");
        Date start, end;
        if (startStr == null || endStr == null || key == null) {
            return JsonUtil.errParam();
        }
        try {
            start = DateUtil.getDate(startStr);
            end = DateUtil.getDate(endStr);
        } catch (Exception ex) {
            ex.printStackTrace();
            return JsonUtil.errParam();
        }
        JSONObject json = new JSONObject();
        List list = AnalysisRecord.getByKey(key, start, end);
        JSONArray records = new JSONArray(list);
        json.put("record", records);
        return json;
    }

    public JSONObject getTodayAnalysisRecord(HashMap<String, String> params) {
        String key = params.get("key");
        if (key == null) {
            return JsonUtil.errParam();
        }
        JSONObject json = new JSONObject();
        List list = AnalysisRecord.getListByTodayKey(key);
        JSONArray records = new JSONArray(list);
        json.put("record", records);
        return json;
    }

    public JSONObject getDefaultDayAnalysisRecord(HashMap<String, String> params) {
        String key = params.get("key");
        if (key == null) {
            return JsonUtil.errParam();
        }
        JSONObject json = new JSONObject();
        List list = AnalysisRecord.getListByDateKey(DateUtil.getDefaultDay(), key);
        JSONArray records = new JSONArray(list);
        json.put("record", records);
        return json;
    }
}
