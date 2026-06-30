package in.parth;

import org.springframework.stereotype.Component;

@Component
public class OrderService {
    public OrderService(){
        System.out.println("OrderService created.");
    }

    public void placeOrder(){
        System.out.println("Order Placed successfully.");
    }
}
