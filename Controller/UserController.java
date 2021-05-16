package Controller;

import handler.jwt.AuthVerify;
import model.User;

import java.util.HashMap;

public class UserController extends Controller {

    public String router(String[] path, HashMap<String, String> params) {
        String email, password, token;
        User usr;
        switch (path[2]) {
            case "login":
                email = params.get("email");
                password = params.get("password");
                usr = User.getByEmail(email);
                if (usr == null) {
                    return "user not found";
                }
                if (!usr.getPassword().equals(password)) {
                    return "wrong password";
                }
                token = AuthVerify.generateToken(usr);
                return token;
            case "getUser":
                token = params.get("token");
                usr = AuthVerify.getAuth(token);
                if (usr == null) {
                    return "error";
                }
                return usr.toString();
            case "register":
                return "register";
        }
        String res = "Unknown Request:: " + path[0] + "/" + path[1] + "\n" + params.toString();
        System.out.println(res);
        return res;
    }

}
