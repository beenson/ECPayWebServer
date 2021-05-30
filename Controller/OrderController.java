package Controller;

import Util.IntegerUtil;
import Util.JsonUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import handler.jwt.AuthVerify;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderController extends Controller{

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
            case "list":
                return this.getOrders(usr).toJSONString();
            //管理權限限定
            case "admin":
                if (usr.getAdmin() <= 0) {
                    break;
                }
                // /admin/{id}
                if (IntegerUtil.isPositiveInteger(path[3])) {
                    int id = Integer.parseInt(path[3]);
                    if (path.length > 4) {
                        // TODO: something?
                    }
                    return JsonUtil.toString(this.getOrder(id)); // 使用此方法避免檢測是否為本人訂單
                }
            default:
                if (IntegerUtil.isPositiveInteger(path[2])) {
                    int id = Integer.parseInt(path[2]);
                    return JsonUtil.toString(this.getUserOrder(usr, id));
                }
        }

        String res = "Unknown Request:: " + path[1] + "/" + path[2] + "\n" + params.toString();
        System.out.println(res);
        return res;
    }

    public JSONObject getUserOrder(User usr, int orderId) {
        JSONObject json = new JSONObject();
        Order order = Order.loadById(orderId);
        if (order == null) {
            return JsonUtil.unknown("Order");
        }
        if (order.getUserId() != usr.getId()) {
            return JsonUtil.unaccess();
        }
        json.put("Order", order);
        json.put("OrderItems", order.getOrderItems());
        return json;
    }

    public JSONObject getOrder(int orderId) {
        JSONObject json = new JSONObject();
        Order order = Order.loadById(orderId);
        if (order == null) {
            return JsonUtil.unknown("Order");
        }
        json.put("Order", order);
        json.put("OrderItems", order.getOrderItems());
        return json;
    }

    public JSONArray getOrders(User usr) {
        JSONArray json = new JSONArray();
        HashMap<Integer, Order> orders = Order.loadAllByUserId(usr.getId());
        for (Order order : orders.values()) {
            json.add(order);
        }
        return json;
    }

    /** Request:: JSON String
     * data:[
     *      {productId: int, amount: int},...
     * ]
     */
    public JSONObject createOrder(User usr, HashMap<String, String> params) {
        JSONArray data;
        if (params.get("data") == null) {
            return JsonUtil.errParam();
        }
        data = JSONObject.parseArray(params.get("data"));
        if (data == null) {
            return JsonUtil.errParam();
        }
        Order order = new Order(usr.getId());
        order.saveToDB();
        ArrayList<OrderItem> items = new ArrayList<>();
        data.forEach( obj -> {
            JSONObject json = (JSONObject) obj;
            if (json == null) {
                return;
            }
            int pid = Integer.parseInt(json.get("productId").toString());
            int amount = Integer.parseInt(json.get("amount").toString());
            if (Product.getById(pid) == null) {
                return;
            }
            if (amount <= 0) {
                return;
            }
            OrderItem item = new OrderItem(order.getId(), pid, amount);
            item.saveToDB();
            items.add(item);
        });
        order.calculatePrice();
        JSONObject json = new JSONObject();
        json.put("Order", order);
        json.put("OrderItems", items);
        return json;
    }

    public JSONObject generatePayment(User usr, int orderId, HashMap<String, String> params) {
        JSONObject json = new JSONObject();
        Order order = Order.loadById(orderId);
        if (order == null) {
            return JsonUtil.unknown("Order");
        }
        if (order.getUserId() != usr.getId()) {
            return JsonUtil.unaccess();
        }
        if (order.getStatus() != Order.OrderStatus.createOrder) {
            return JsonUtil.notAllowed();
        }
        OrderPayment.PaymentType type ;
        String bank = "";
        if (params.get("type") == null) {
            return JsonUtil.errParam();
        }
        type = OrderPayment.PaymentType.getByValue(Integer.parseInt(params.get("type")));
        if (type == null) {
            return JsonUtil.errParam();
        }
        if (type == OrderPayment.PaymentType.ATM) {
            if (params.get("bank") == null) {
                return JsonUtil.errParam();
            }
            bank = params.get("bank");
        }
        OrderPayment payment = new OrderPayment();
        payment.setBank(bank);
        payment.setOrderId(order.getId());
        payment.setType(type);
        payment.setStatus(OrderPayment.PaymentStatus.created);
        // TODO: ECPay generate Code
        payment.saveToDB();
        json.put("order", order);
        json.put("payment", payment);
        return json;
    }
}
