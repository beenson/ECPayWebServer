package Controller;

import Util.IntegerUtil;
import Util.JsonUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import handler.ecpay.EcpayPayment;
import handler.ecpay.payment.ecpayOperator.EcpayFunction;
import handler.jwt.AuthVerify;
import model.*;
import org.apache.xpath.operations.Or;

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
                return this.getOrders(usr.getId()).toJSONString();
            case "create":
                return this.createOrder(usr.getId(), params).toJSONString();
            //管理權限限定
            case "admin":
                if (usr.getAdmin() <= 0) {
                    break;
                }
                // /admin/{id}
                if (IntegerUtil.isPositiveInteger(path[3])) {
                    int id = Integer.parseInt(path[3]);
                    if (path.length > 4) {
                        switch(path[4]) {
                            case "create": // /order/admin/{uId}/create 為user創建訂單
                                return this.createOrder(id, params).toJSONString();
                            case "list": // /order/admin/{uId}/list 查看user訂單
                                return this.getOrders(id).toJSONString();
                            case "update": // /order/admin/{oId}/update 更新訂單
                                return this.updateOrder(id, params).toJSONString();
                        }
                    }
                    return JsonUtil.toString(this.getOrder(id)); // 使用此方法避免檢測是否為本人訂單
                }else {
                    switch (path[3]) {
                        case "all": // /order/admin/all
                            return this.getAllOrders().toJSONString();
                    }
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

    public JSONArray getOrders(int uId) {
        JSONArray json = new JSONArray();
        HashMap<Integer, Order> orders = Order.loadAllByUserId(uId);
        for (Order order : orders.values()) {
            json.add(order);
        }
        return json;
    }

    public JSONArray getAllOrders() {
        JSONArray json = new JSONArray();
        HashMap<Integer, Order> orders = Order.loadAllFromDB();
        for (Order order : orders.values()) {
            json.add(order);
        }
        return json;
    }

    /** Request:: JSON String
     * data:[
     *      {productId: int, amount: int},...
     * ],
     * type: {ATM | CVS},
     * bank: {String}
     */
    public JSONObject createOrder(int uId, HashMap<String, String> params) {
        JSONArray data;
        String bank = "";
        OrderPayment.PaymentType type;
        if (params.get("data") == null) {
            return JsonUtil.errParam();
        }
        if (params.get("type") == null) {
            return JsonUtil.errParam();
        }
        type = OrderPayment.PaymentType.getByValue(Integer.parseInt(params.get("type")));
        if (type == OrderPayment.PaymentType.ATM) {
            if (params.get("bank") == null) {
                return JsonUtil.errParam();
            }
            bank = params.get("bank");
        }
        data = JSONObject.parseArray(params.get("data"));
        if (data == null) {
            return JsonUtil.errParam();
        }
        Order order = new Order(uId);
        order.saveToDB();
        ArrayList<OrderItem> items = addOrderItems(order.getId(), data);
        order.calculatePrice();
        OrderPayment payment = this.generatePayment(order, type, bank);
        JSONObject json = new JSONObject();
        json.put("Order", order);
        json.put("OrderItems", items);
        json.put("OrderPayment", payment);
        return json;
    }

    public JSONObject updateOrder(int uId, HashMap<String, String> params) {
        JSONArray data;
        if (params.get("data") == null) {
            return JsonUtil.errParam();
        }
        data = JSONObject.parseArray(params.get("data"));
        if (data == null) {
            return JsonUtil.errParam();
        }
        Order order = Order.loadById(uId);
        if(order == null) {
            return JsonUtil.errId();
        }
        order.getOrderItems().forEach(obj -> obj.deleteFromDB());
        ArrayList<OrderItem> items = addOrderItems(order.getId(), data);
        order.calculatePrice();
        JSONObject json = new JSONObject();
        json.put("Order", order);
        json.put("OrderItems", items);
        return json;
    }

    private ArrayList<OrderItem> addOrderItems(int oId, JSONArray data) {
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
            OrderItem item = new OrderItem(oId, pid, amount);
            item.saveToDB();
            items.add(item);
        });
        return items;
    }

    public OrderPayment generatePayment(Order order, OrderPayment.PaymentType type, String bank) {
        order.setStatus(Order.OrderStatus.createPayment);
        order.saveToDB();
        OrderPayment payment = new OrderPayment();
        payment.setBank(bank);
        payment.setOrderId(order.getId());
        payment.setType(type);
        payment.setStatus(OrderPayment.PaymentStatus.created);
        EcpayFunction.PaymentInfo info;
        if (type.equals(OrderPayment.PaymentType.ATM)) {
            info = EcpayPayment.genAioCheckOutTEST(order.getPrice());
        } else {
            info = EcpayPayment.genAioCheckOutTEST(order.getPrice());
            payment.setBank(info.getBankCode());
        }
        payment.setCode(info.getPaymentNo());
        payment.saveToDB();
        return payment;
    }
}
