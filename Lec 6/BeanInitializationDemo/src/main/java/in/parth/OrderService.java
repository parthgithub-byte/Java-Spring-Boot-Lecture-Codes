package in.parth;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class OrderService {
    public OrderService(){
        System.out.println("OrderService created.");
    }
}
