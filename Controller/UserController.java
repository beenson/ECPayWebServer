package Controller;

import handler.jwt.AuthVerify;
import model.User;
import com.alibaba.fastjson.*;
import java.util.HashMap;

public class UserController extends Controller {

    public String router(String[] path, HashMap<String, String> params) {
        String email, password, token;
        User usr;
        switch (path[2]) {
            case "login":
                return this.login(params).toJSONString();
            case "getUser":
                token = params.get("token");
                usr = AuthVerify.getAuth(token);
                if (usr == null) {
                    return "error";
                }
                return usr.toString();
            case "register":
                return this.register(params).toJSONString();
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

}
