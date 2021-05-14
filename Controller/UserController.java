package Controller;

import model.User;

import java.util.HashMap;

public class UserController extends Controller {

    public String router(String[] path, HashMap<String, String> params) {
        switch (path[2]) {
            case "login":
                String email = params.get("email");
                String password = params.get("password");
                User usr = User.getByEmail(email);
                if (usr == null) {
                    return "user not found";
                }
                if (!usr.getPassword().equals(password)) {
                    return "wrong password";
                }
                return "loginnnn";
            case "register":
                return "register";
        }
        return path[0] + "/" + path[1] + "\n" + params.toString();
    }

}
