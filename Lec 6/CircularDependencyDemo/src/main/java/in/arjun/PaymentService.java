package in.arjun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {

//    @Autowired
//    private OrderService orderService;

//    public PaymentService(OrderService orderService){
//        this.orderService = orderService;
//    }
//    Now the constructor does not need O.S. as its D.I.


    public void pay(){
        System.out.println("Payment completed");
//        orderService.getOrderDetails();
//        not the responsibility of PaymentService to call it here
    }
}
