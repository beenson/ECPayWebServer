package handler.jwt;

import Util.DateUtil;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import model.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthVerify {

    public static long expireTime = 10 * 1000;
    //public static long expireTime = 0 * 1000;

    public static User getAuth(String token) {
        DecodedJWT jwt = JwtHandler.decode(token);
        if (jwt == null) {
            return null;
        }
        Map<String, Claim> data = jwt.getClaims();
        int uid = Integer.parseInt(data.get("id").asString());
        User usr = new User(uid);
        if (usr.getId() == -1) {
            return null;
        }
        return usr;
    }

    public static String generateToken(User usr) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("id", usr.getId());
        data.put("name", usr.getName());
        return JwtHandler.create(data, DateUtil.getDateWithAddTime(expireTime));
    }

}
