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
import org.apache.xpath.operations.Bool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductController extends Controller{

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
            case "list":// /list
                return this.getProducts().toJSONString();
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
            default:
                if (IntegerUtil.isPositiveInteger(path[2])) {// /product/{id}
                    int id = Integer.parseInt(path[2]);
                    return this.getProduct(id).toJSONString();
                }
        }
        String res = "Unknown Request:: " + path[1] + "/" + path[2] + "\n" + params.toString();
        System.out.println(res);
        return res;
    }

    public JSONObject create(HashMap<String, String> params) {
        if(params.get("name") == null || params.get("price") == null || params.get("desc") == null || params.get("sellAmount") == null || params.get("storageAmount") == null || params.get("onSell") == null) {
            return JsonUtil.errParam();
        }
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
            JSONObject json = new JSONObject();
            json.put("status", "success");
            json.put("product", product);
            return json;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return JsonUtil.unknownErr();
    }

    public JSONObject getProduct(int id) {
        Product product = Product.getById(id);
        if (product == null || product.getId() == -1) {
            return JsonUtil.unknown("Product");
        }
        JSONObject json = new JSONObject();
        json.put("product", product);
        return json;
    }

    public JSONArray getProducts() {
        JSONArray json = new JSONArray();
        HashMap<Integer, Product> products = Product.loadAllFromDB();
        for (Map.Entry<Integer, Product> product : products.entrySet()) {
            json.add(product.getValue());
        }
        return json;
    }

    public JSONObject update(int id, HashMap<String, String> params) {
        Product product = Product.getById(id);
        if(product == null || product.getId() == -1) {
            return JsonUtil.unknown("Product");
        }
        try {
            product.setName(params.get("name"));
            product.setPrice(Integer.parseInt(params.get("price")));
            product.setDesc(params.get("desc"));
            product.setSellAmount(Integer.parseInt(params.get("sellAmount")));
            product.setStorageAmount(Integer.parseInt(params.get("storageAmount")));
            product.setOnSell(Boolean.valueOf(params.get("onSell")));
            product.setPhoto(params.get("photo"));
            product.setCategoryId(Integer.parseInt(params.get("categoryId")));
            product.saveToDB();
            JSONObject json = new JSONObject();
            json.put("status", "success");
            json.put("product", product);
            return json;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return JsonUtil.unknownErr();
    }

    public JSONObject delete(int id) {
        Product product = Product.getById(id);
        if(product == null || product.getId() == -1) {
            return JsonUtil.unknown("Product");
        }
        product.deleteFromDB();
        JSONObject json = new JSONObject();
        json.put("status", "success");
        return json;
    }
}
