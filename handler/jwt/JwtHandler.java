package handler.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtHandler {

    private static String KEY_ALGORITHM_RSA = "RSA";
    private static RSAPublicKey publicKey = null;
    private static RSAPrivateKey privateKey = null;
    private static Algorithm algorithm = null;


    public static void main(String [] argv) {
        genKey();
        test();
    }

    public static void init() {
        genKey();
    }

    public static void test() {
        HashMap<String, Object> data = new HashMap<>();
        Date date = new Date("2021/05/20 17:00:05");
        data.put("key1", "value1");
        data.put("key2", "value2");
        data.put("key3", "value3");
        data.put("key4", "value4");
        String token = create(data, date);
        System.out.println(token);
        DecodedJWT jwt = decode(token);
        System.out.println(jwt.getClaims());
        System.out.println(jwt.getExpiresAt());
    }

    public static void genKey() {
        try {
            // 實例化密鑰對生成器
            KeyPairGenerator key_pair_generator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA);
            // 初始化密鑰對生成器
            key_pair_generator.initialize(2048);
            // 生成密鑰對
            KeyPair key_pair = key_pair_generator.generateKeyPair();
            // 公鑰
            RSAPublicKey public_key = (RSAPublicKey) key_pair.getPublic();
            // 私鑰
            RSAPrivateKey private_key = (RSAPrivateKey) key_pair.getPrivate();
            publicKey = public_key;
            privateKey = private_key;
            algorithm = Algorithm.RSA256(publicKey, privateKey);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    public static String create(HashMap<String, Object> payload, Date expire) {
        try {
            JWTCreator.Builder jwt = JWT.create();
            for(Map.Entry<String, Object> kv : payload.entrySet()) {
                jwt.withClaim(kv.getKey(), kv.getValue().toString());
            }
            jwt.withExpiresAt(expire);
            String token = jwt.sign(algorithm);
            return token;
        } catch (JWTCreationException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static DecodedJWT decode(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            return jwt;
        } catch (JWTVerificationException ex){
            //ex.printStackTrace();
        }
        return null;
    }
}
