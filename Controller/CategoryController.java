package Controller;

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
        String token = "";
        User usr = null;
        if (hs.containsKey("Authorization")) {
            token = hs.getFirst("Authorization").replace("Bearer ", "");
            usr = AuthVerify.getAuth(token);
        }
        if(usr == null) {
            return "login first";
        }

        switch (path[2]) {
            //普通權限
            case "read":
                return this.read(path).toJSONString();
            case "readAll":
                return this.readAll().toJSONString();
            //管理權限限定
            case "admin":
                if (usr.getAdmin() <= 0) {
                    break;
                }
                switch (path[3]) {
                    case "create":
                        return this.create(params);
                    case "update":
                        return this.update(path, params);
                    case "delete":
                        return this.delete(path);
                }
        }

        String res = "Unknown Request:: " + path[1] + "/" + path[2] + "\n" + params.toString();
        System.out.println(res);
        return res;
    }

    public String create(HashMap<String, String> params) {
        if(params.get("name") == null || params.get("priority") == null)
            return "something have missed";

        try {
            int id = -1;
            String name = params.get("name");
            int priority = Integer.parseInt(params.get("priority"));

            Category category = new Category(id, name, priority);
            category.saveToDB();
            return "success";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "fail";
    }

    public JSONObject read(String[] path) {
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

    public JSONArray readAll() {
        JSONArray json = new JSONArray();
        HashMap<Integer, Category> categories = Category.loadAllFromDB();
        for (Map.Entry<Integer, Category> catogery : categories.entrySet()) {
            json.add(catogery.getValue());
        }
        return json;
    }

    public String update(String[] path, HashMap<String, String> params) {
        if (path.length < 5) {
            return "error request path.";
        }

        Category category = Category.getById(path[4]);
        if(category == null || category.getId() == -1) {
            return "category not found";
        }
        try {
            category.setName(params.get("name"));
            category.setPriority(Integer.parseInt(params.get("priority")));

            category.saveToDB();
            return "success";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    public String delete(String[] path) {
        if (path.length < 5) {
            return "error request path.";
        }

        Category category = Category.getById(path[4]);
        if(category == null || category.getId() == -1) {
            return "category not found";
        }
        category.deleteFromDB();
        return "success";
    }
}
