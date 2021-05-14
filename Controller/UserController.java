package Controller;

import java.util.HashMap;

public class UserController extends Controller {

    public String router(String[] path, HashMap<String, String> params) {
        return path[0] + "/" + path[1] + "\n" + params.toString();
    }

}
