# Eager vs Lazy Initialization

Initially, we had:

Main: 

public class Main {
    static void main() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    }
}

PaymentService:

@Component
public class PaymentService {
    public PaymentService(){
        System.out.println("PaymentService created.");
    }
}

OrderService:

@Component
public class OrderService {
    public OrderService(){
        System.out.println("OrderService created.");
    }
}

O/P:
OrderService created.
PaymentService created.


Right now, the components are of Singleton scope. So, they have eager initialization by nature.
 But we can make it lazy.
Just put the @Lazy annotation in the component class:

PaymentService:

@Component
@Lazy
public class PaymentService {
    public PaymentService(){
        System.out.println("PaymentService created.");
    }
}

For now, I am letting OrderService as it is.
O/P:
OrderService created.

Thus, this time around only OrderService was 'eagerly' initialized.
Making both lazy:
O/P


No object created, since not actually called in Main.
Changing the Main contents:

Main:
public class Main {
    static void main() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService order = context.getBean(OrderService.class);
        PaymentService payment = context.getBean(PaymentService.class);
    }
}

O/P:

OrderService created.
PaymentService created.

Now if Main only had:
    PaymentService payment = context.getBean(PaymentService.class);
O/P:
    PaymentService created.


## Prototype scope is always lazy inititalization
Since there can be zero to multiple instances created for the same Component by its design, 
we always have Prototype scope in lazy initialization. 
Try putting @Scope("prototype") in the components and check.

Thus
1. Singleton scope -> Eager (default), Lazy (with @Lazy)
2. Prototype -> Lazy (always)

