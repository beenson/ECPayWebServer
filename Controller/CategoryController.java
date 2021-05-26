package Controller;

import Util.IntegerUtil;
import Util.JsonUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import handler.jwt.AuthVerify;
import model.Category;
import model.Product;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class CategoryController extends Controller{

    @Override
    public String router(Headers hs, String[] path, HashMap<String, String> params) {
        String token;
        User usr = null;
        if (hs.containsKey("Authorization")) {
            token = hs.getFirst("Authorization").replace("Bearer ", "");
            usr = AuthVerify.getAuth(token);
        }
        if(usr == null) {
            return JsonUtil.unAuthorized().toJSONString();
        }

        switch (path[2]) {
            //普通權限
            case "category":
                return this.getCategory(path).toJSONString();
            case "categorys":
                return this.getCategorys().toJSONString();
            //管理權限限定
            case "admin":
                if (usr.getAdmin() <= 0) {
                    break;
                }
                // /admin/{id}
                if (IntegerUtil.isPositiveInteger(path[3])) {
                    int id = Integer.parseInt(path[3]);
                    switch (path[4]) {
                        case "update": // /admin/{id}/update
                            return this.update(id, params).toJSONString();
                        case "delete": // /admin/{id}/delete
                            return this.delete(id).toJSONString();
                    }
                } else {
                    switch (path[3]) {
                        case "create": // /admin/create
                            return this.create(params).toJSONString();
                    }
                }
        }

        String res = "Unknown Request:: " + path[1] + "/" + path[2] + "\n" + params.toString();
        System.out.println(res);
        return res;
    }

    public JSONObject create(HashMap<String, String> params) {
        if(params.get("name") == null || params.get("priority") == null) {
            return JsonUtil.errParam();
        }
        try {
            int id = -1;
            String name = params.get("name");
            int priority = Integer.parseInt(params.get("priority"));

            Category category = new Category(id, name, priority);
            category.saveToDB();
            JSONObject json = new JSONObject();
            json.put("status", "success");
            json.put("category", category);
            return json;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return JsonUtil.unknownErr();
    }

    public JSONObject getCategory(String[] path) {
        JSONObject json = new JSONObject();
        if (path.length < 4) {
            json.put("error", "error request path.");
            return json;
        }

        Category category = Category.getById(path[3]);
        if (category == null || category.getId() == -1) {
            json.put("error", "Unknown category.");
            return json;
        }
        json.put("category", category);
        return json;
    }

    public JSONArray getCategorys() {
        JSONArray json = new JSONArray();
        HashMap<Integer, Category> categories = Category.loadAllFromDB();
        for (Map.Entry<Integer, Category> catogery : categories.entrySet()) {
            json.add(catogery.getValue());
        }
        return json;
    }

    public JSONObject update(int id, HashMap<String, String> params) {
        Category category = Category.getById(id);
        if(category == null || category.getId() == -1) {
            return JsonUtil.unknown("Category");
        }
        JSONObject json = new JSONObject();
        category.setName(params.get("name"));
        category.setPriority(Integer.parseInt(params.get("priority")));
        category.saveToDB();
        json.put("status", "success");
        json.put("category", category);
        return json;
    }

    public JSONObject delete(int id) {
        Category category = Category.getById(id);
        if(category == null || category.getId() == -1) {
            return JsonUtil.unknown("Category");
        }
        category.deleteFromDB();
        JSONObject json = new JSONObject();
        json.put("status", "success");
        return json;
    }
}
