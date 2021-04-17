package model;

import lombok.Getter;
import lombok.Setter;

public class OrderPayment {

    @Getter @Setter
    private int id, orderId;// id, 訂單編號
    @Getter @Setter
    private String orderNumber, bank, code;// 訂單編號(綠界), 銀行代碼, 繳費代碼|銀行帳號
    @Getter @Setter
    private PaymentStatus status;
    @Getter @Setter
    private PaymentStatus type;

    public enum PaymentType {
        CVS(0),// 超商代碼
        ATM(1);
        @Getter private int value;
        PaymentType(int value) {
            this.value = value;
        }
    }
    public enum PaymentStatus {
        created(0),
        purchased(1),
        expired(2);
        @Getter private int value;
        PaymentStatus(int value) {
            this.value = value;
        }
    }

}
