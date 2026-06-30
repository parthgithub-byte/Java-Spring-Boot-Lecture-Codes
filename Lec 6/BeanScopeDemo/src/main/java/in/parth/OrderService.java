package in.parth;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//@Component
@Scope("singleton") // optional
//@Scope("prototype")
public class OrderService {
    public OrderService(){
        System.out.println("OrderService created.");
    }

    public void placeOrder(){
        System.out.println("Order Placed successfully.");
    }
}
