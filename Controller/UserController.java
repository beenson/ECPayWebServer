package Controller;

import handler.jwt.AuthVerify;
import model.User;
import com.alibaba.fastjson.*;

import java.util.ArrayList;
import java.util.HashMap;

public class UserController extends Controller {

    public String router(String[] path, HashMap<String, String> params) {
        switch (path[2]) {
            //普通權限
            case "login":
                return this.login(params).toJSONString();
            case "getAuth":
                return this.getAuth(params).toJSONString();
            case "register":
                return this.register(params).toJSONString();
            case "refreshToken":
                return this.refreshToken(params).toJSONString();
            //管理權限限定
            case "admin":
                User admin = this.getAuthUser(params);
                switch (path[3]) {
                    case "users":
                        return this.getUsers().toJSONString();
                    case "user"://user/{id}
                        return this.getUser(path).toJSONString();
                }
                break;
        }
        String res = "Unknown Request:: " + path[0] + "/" + path[1] + "\n" + params.toString();
        System.out.println(res);
        return res;
    }

    public JSONObject login(HashMap<String, String> params) {
        JSONObject json = new JSONObject ();
        String email = params.get("email");
        String password = params.get("password");
        if (email == null || password == null) {
            json.put("error", "error parameter.");
            return json;
        }
        User usr = User.getByEmail(email);
        if (usr == null) {
            json.put("error", "user not found.");
            return json;
        }
        if (!usr.getPassword().equals(password)) {
            json.put("error", "wrong password.");
            return json;
        }
        String token = AuthVerify.generateToken(usr);
        json.put("token", token);
        json.put("user", usr);
        return json;
    }

    public JSONObject register(HashMap<String, String> params) {
        JSONObject json = new JSONObject ();
        String email = params.get("email");
        String password = params.get("password");
        String name = params.get("name");
        String phone = params.get("phone");
        if (email == null || password == null || name == null || phone == null) {
            json.put("error", "error parameter.");
            return json;
        }
        User usr = User.getByEmail(email);
        if (usr != null) {
            json.put("error", "duplicate User");
            return json;
        }
        usr = new User(-1, 0, name, email, password, phone);
        usr.saveToDB();
        String token = AuthVerify.generateToken(usr);
        json.put("token", token);
        json.put("user", usr);
        return json;
    }

    public JSONObject getAuth(HashMap<String, String> params) {
        JSONObject json = new JSONObject();
        String token = params.get("token");
        if (token == null) {
            json.put("error", "error parameter.");
            return json;
        }
        User usr = AuthVerify.getAuth(token);
        if (usr == null) {
            json.put("error", "error token.");
            return json;
        }
        json.put("user", usr);
        return json;
    }

    public User getAuthUser(HashMap<String, String> params) {
        String token = params.get("token");
        if (token == null) {
            return null;
        }
        User usr = AuthVerify.getAuth(token);
        return usr;
    }

    public JSONObject refreshToken(HashMap<String, String> params) {
        JSONObject json = new JSONObject();
        String token = params.get("token");
        if (token == null) {
            json.put("error", "error parameter.");
            return json;
        }
        User usr = AuthVerify.getAuth(token);
        if (usr == null) {
            json.put("error", "error token.");
            return json;
        }
        String newToken = AuthVerify.generateToken(usr);
        json.put("oriToken", token);
        json.put("newToken", newToken);
        json.put("user", usr);
        return json;
    }

    public JSONArray getUsers() {
        JSONArray json = new JSONArray();
        ArrayList<User> users = User.loadAllFromDB();
        for (User user : users) {
            json.add(user);
        }
        return json;
    }

    public JSONObject getUser(String path[]) {
        JSONObject json = new JSONObject();
        if (path.length < 5) {
            json.put("error", "error request path.");
            return json;
        }
        int id = Integer.parseInt(path[4]);
        if (id <= 0) {
            json.put("error", "error User Id.");
            return json;
        }
        User usr = new User(id);
        if (usr == null || usr.getId() == -1) {
            json.put("error", "Unknown User.");
            return json;
        }
        json.put("user", usr);
        return json;
    }
}
