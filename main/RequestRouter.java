package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestRouter implements HttpHandler {

    @Override
    public void handle(HttpExchange Request){
        try{
            // 解析請求資料
            String requestedURL = Request.getRequestHeaders().getFirst("Host") + Request.getRequestURI();
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            InputStreamReader isr = new InputStreamReader(Request.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);

            String returnString = "URL:: " + requestedURL + "\n";
            returnString += "query:: " + query;
            String response = URLDecoder.decode(returnString, "UTF-8");//判別並執行請求後編碼
            Request.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            Request.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = Request.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception ex) {

        }
    }



    public static void parseQuery(String query, HashMap<String, Object> parameters) throws UnsupportedEncodingException {
        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
                }
                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],System.getProperty("file.encoding"));
                }
                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof ArrayList<?>) {
                        ArrayList<String> values = (ArrayList<String>) obj;
                        values.add(value);
                    } else if (obj instanceof String) {
                        ArrayList<String> values = new ArrayList<>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }
}
