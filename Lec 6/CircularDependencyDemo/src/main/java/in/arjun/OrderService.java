package in.arjun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderService {

    @Autowired
    private PaymentService paymentService;

////    (optional)
////    @Autowired
//    public OrderService(PaymentService paymentService){
//        this.paymentService = paymentService;
//    }
//    removed constructor to avoid problems in creation using constructor D.I. by using Field or Setter D.I.

    public void placeOrder(){
        paymentService.pay();
        System.out.println("Order processed and placed.");
        getOrderDetails();  // best suited here
    }

    public void getOrderDetails(){
        System.out.println("This method returns order details.....");
    }
}
