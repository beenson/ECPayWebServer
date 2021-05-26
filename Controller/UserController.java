package Controller;

import Util.JsonUtil;
import com.sun.net.httpserver.Headers;
import handler.jwt.AuthVerify;
import model.User;
import com.alibaba.fastjson.*;

import java.util.ArrayList;
import java.util.HashMap;

public class UserController extends Controller {

    public String router(Headers hs, String[] path, HashMap<String, String> params) {
        String token = "";
        User usr = null;
        if (hs.getFirst("Authorization") != null) {
            token = hs.getFirst("Authorization").replace("Bearer ", "");
            usr = AuthVerify.getAuth(token);
        }
        if (usr == null) {
            switch (path[2]) {
                //普通權限
                case "login":
                    return this.login(params).toJSONString();
                case "register":
                    return this.register(params).toJSONString();
            }
        } else {
            switch (path[2]) {
                //普通權限
                case "getAuth":
                    return this.getAuth(usr).toJSONString();
                case "refreshToken":
                    return this.refreshToken(usr).toJSONString();
                case "editProfile":
                    return this.editProfile(usr, params).toJSONString();
                //管理權限限定
                case "admin":
                    if (usr.getAdmin() <= 0) {
                        break;
                    }
                    switch (path[3]) {
                        case "users":
                            return this.getUsers().toJSONString();
                        case "user"://user/{id}
                            return this.getUser(path).toJSONString();
                    }
                    break;
            }
        }
        return "401";
    }

    public JSONObject login(HashMap<String, String> params) {
        String email = params.get("email");
        String password = params.get("password");
        if (email == null || password == null) {
            return JsonUtil.errParam();
        }
        User usr = User.getByEmail(email);
        if (usr == null) {
            return JsonUtil.unknown("User");
        }
        if (!usr.getPassword().equals(password)) {
            return JsonUtil.errPassword();
        }
        String token = AuthVerify.generateToken(usr);
        JSONObject json = new JSONObject ();
        json.put("token", token);
        json.put("user", usr);
        return json;
    }

    public JSONObject register(HashMap<String, String> params) {
        String email = params.get("email");
        String password = params.get("password");
        String name = params.get("name");
        String phone = params.get("phone");
        if (email == null || password == null || name == null || phone == null) {
            return JsonUtil.errParam();
        }
        User usr = User.getByEmail(email);
        if (usr != null) {
            return JsonUtil.duplicateEmail();
        }
        usr = new User(-1, 0, name, email, password, phone);
        usr.saveToDB();
        String token = AuthVerify.generateToken(usr);
        JSONObject json = new JSONObject ();
        json.put("token", token);
        json.put("user", usr);
        return json;
    }

    public JSONObject getAuth(User usr) {
        JSONObject json = new JSONObject();
        json.put("user", usr);
        return json;
    }

    public JSONObject editProfile(User usr, HashMap<String, String> params) {
        String newName = params.get("newName");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        String newPhone = params.get("newPhone");
        if (usr == null) {
            return JsonUtil.errToken();
        }
        if (newName != null) {
            usr.setName(newName);
        }
        if (newPassword != null) {
            if (oldPassword != null) {
                if (oldPassword.equals(usr.getPassword())) {
                    usr.setPassword(newPassword);
                } else {
                    return JsonUtil.errOldPassword();
                }
            } else {
                return JsonUtil.errOldPassword();
            }
        }
        if (newPhone != null) {
            usr.setPhone(newPhone);
        }
        usr.saveToDB();
        JSONObject json = new JSONObject();
        json.put("status", "success");
        json.put("user", usr);
        return json;
    }

    public JSONObject refreshToken(User usr) {
        if (usr == null) {
            return JsonUtil.errToken();
        }
        JSONObject json = new JSONObject();
        String newToken = AuthVerify.generateToken(usr);
        json.put("token", newToken);
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
        if (path.length < 5) {
            return JsonUtil.errPath();
        }
        int id = Integer.parseInt(path[4]);
        if (id <= 0) {
            return JsonUtil.errId();
        }
        User usr = new User(id);
        if (usr == null || usr.getId() == -1) {
            return JsonUtil.unknown("User");
        }
        JSONObject json = new JSONObject();
        json.put("user", usr);
        return json;
    }
}
