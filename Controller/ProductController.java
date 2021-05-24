package Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import handler.jwt.AuthVerify;
import model.Category;
import model.Product;
import model.User;
import org.apache.xpath.operations.Bool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductController extends Controller{

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
        if(params.get("name") == null || params.get("price") == null || params.get("desc") == null || params.get("sellAmount") == null || params.get("storageAmount") == null || params.get("onSell") == null)
            return "something have missed";

        try {
            int id = -1;
            String name = params.get("name");
            int price = Integer.parseInt(params.get("price"));
            String desc = params.get("desc");
            int sellAmount = Integer.parseInt(params.get("sellAmount"));
            int storageAmount = Integer.parseInt(params.get("storageAmount"));
            boolean onSell = Boolean.valueOf(params.get("onSell"));
            String photo = params.get("photo");
            int categoryId = Integer.parseInt(params.get("categoryId"));

            Product product = new Product(id, name, price, desc, sellAmount, storageAmount, onSell, photo, categoryId);
            product.saveToDB();
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

        Product product = Product.getById(path[3]);
        if (product == null || product.getId() == -1) {
            json.put("error", "Unknown product.");
            return json;
        }
        json.put("product", product);
        return json;
    }

    public JSONArray readAll() {
        JSONArray json = new JSONArray();
        HashMap<Integer, Product> products = Product.loadAllFromDB();
        for (Map.Entry<Integer, Product> product : products.entrySet()) {
            json.add(product.getValue());
        }
        return json;
    }

    public String update(String[] path, HashMap<String, String> params) {
        if (path.length < 5) {
            return "error request path.";
        }

        Product product = Product.getById(path[4]);
        if(product == null || product.getId() == -1) {
            return "product not found";
        }
        try {
            product.setName(params.get("name"));
            product.setPrice(Integer.parseInt(params.get("price")));
            product.setDesc(params.get("desc"));
            product.setSellAmount(Integer.parseInt(params.get("sellAmount")));
            product.setStorageAmount(Integer.parseInt(params.get("storageAmount")));
            product.setOnSell(Boolean.valueOf(params.get("onSell")));
            product.setPhoto(params.get("photo"));
            product.setCategory(Category.getById(params.get("categoryId")));

            product.saveToDB();
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

        Product product = Product.getById(path[4]);
        if(product == null || product.getId() == -1) {
            return "product not found";
        }
        product.deleteFromDB();
        return "success";
    }
}
