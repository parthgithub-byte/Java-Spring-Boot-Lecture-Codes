By default, Beans in java are created with "Singleton" scope. That means, when we use the Spring
core, a bean is created once (exactly once) and the same bean is used by all the other components 
requiring it as the dependency.

OrderService:

@Component
public class OrderService {
public OrderService(){
System.out.println("OrderService created.");
}

    public void placeOrder(){
        System.out.println("Order Placed successfully.");
    }
}

