package in.parth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// tells spring core that this is a component that may create or require dependency
@Component
public class OrderService {
    private PaymentService paymentService;

    @Autowired
    public OrderService(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    public void placeOrder(){
        paymentService.pay();
        System.out.println("Order is placed.");
    }
}
