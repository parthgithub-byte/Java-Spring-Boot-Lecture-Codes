Firstly, let's see the file contents:

Main.java:

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
context.getBean(OrderService.class).placeOrder();
```

OrderService.java:

```java
@Component
public class OrderService {
private final PaymentService paymentService;

public OrderService(PaymentService paymentService){
this.paymentService = paymentService;
}

    public void placeOrder(){
        paymentService.pay();
        System.out.println("Order processed and placed.");
    }
    
    public void getOrderDetails(){
        System.out.println("This method returns order details.....");
    }
}
```

PaymentService.java:

```java
@Component
public class PaymentService {

    private OrderService orderService;
    
    public PaymentService(OrderService orderService){
        this.orderService = orderService;
    }
    
    public void pay(){
        System.out.println("Payment completed");
        orderService.getOrderDetails();
    }
}
```

Thus, only seeing at the functions, the execution flow is ideally:
- Main calling placeOrder on OrderService
- OrderService calling `pay()` on PaymentService
- PaymentService calling `getOrderDetails()` on OrderService

Seems ok, but we are talking about the Spring handling dependencies here. Notice, OrderService gets its dependency
through the constructor, so it first needs PaymentService object/ Bean created. But PaymentService will also later need a OrderService instance, so even
it will take OrderService instance as dependency nd will require its bean as a dependency. This is the Circular Dependency.

O/P:
Error creating bean with name 'orderService': Requested bean is currently in creation: 
Is there an unresolvable circular reference or an asynchronous initialization dependency?

Now, this circular dependency is not a Spring problem, it is a bad code issue. This problem can
even be seen with normal Java class dependencies.

Eg:
class A:

```java
public class A {
private B b;

    public A(){
        System.out.println("A object created.");
        this.b = new B();
    }
}
```

class B:

```java
public class B {
private A a;

    public B(){
        System.out.println("B object created.");
        this.a = new A();
    }
}
```

In Main:

```java
//(rest of the code is commented for spring to ignore)
A a = new A();  // infinite object creation; new A calls new B, which requires new A and so on until stack overflow 
```

O/P:
A object created.
B object created.
A object created.
B object created.
A object created.
B object created.
.
.
.
Exception in thread "main" java.lang.StackOverflowError

-----------------------------------------------------------------

Using field D.I. to avoid halting object creation in constructor D.I.:
Here, the dependencies that are included as private fields from another class are first resolved
by the Spring automatically w/o using constructor or setter (Spring uses the Reflection API for this by taking the Reflection of that other class).

PaymentService:

```java
@Component
public class PaymentService {

    @Autowired
    private OrderService orderService;

    public void pay(){
        System.out.println("Payment completed");
        orderService.getOrderDetails();
    }
}
```

OrderService:

```java
@Component
public class OrderService {

    @Autowired
    private PaymentService paymentService;

    public void placeOrder(){
        paymentService.pay();
        System.out.println("Order processed and placed.");
    }

    public void getOrderDetails(){
        System.out.println("This method returns order details.....");
    }
}
```

O/P:
Payment completed
This method returns order details.....
Order processed and placed.

Flow Above:
1. Create OrderService empty object
2. Create PaymentService empty object
3. Inject paymentService to OrderService
4. Inject orderService to PaymentService


Note that, this is not the usual flow of bean creation in spring applications. In general,
the flow is:
1. Create A
2. Inject the dependency
3. Create B
4. Inject the dependency

Our way of design breaks above idea. In spring core, our way actually creates partial beans that are referred by the other
beans for their use. First, both OrderService (O.S.) and PaymentService (P.S.) are partially created beans, then we saw O.S. using P.S. however it is
in its method placeOrder() as the dependency. Then, P.S., while performing the pay() method, takes the partial O.S. as its dependency and calls it.
Now, the O.S. has covered all its necessities and got the structure finished. Similarly, now the P.S. bean is also completed with its dependency O.S.
being finished.

In general, Spring calls for avoiding the circular dependency. Spring Boot actually completely blocks it, unless you allow the flag to be true, it is by default false:
    spring.main.allow-circular-dependency : false;

# Ways to Solve Circular Dependency:
Instead of a shortcut of field/ setter injection, we have certain approaches:

1. Don't Let the Code Make include any Circular Dependency (CD):
best way to handle it is not letting it happen. In our last code, OrderService needs PaymentService object for applying the pay() function before placing the
order. Fair. Then in pay(), after payment is done, it further calls OrderService's getOrderDetails(). Now, is it necessary for that method to be called here?
No. Is it the responsibility of PaymentService to get order details? No. It can simply be called by OrderService itself in the placeOrder() method.

PaymentService.java:

```java
@Component
public class PaymentService {

//    @Autowired
//    private OrderService orderService;
//    This dependency is not needed now
    
//    public PaymentService(OrderService orderService){
//        this.orderService = orderService;
//    }
//    Now the constructor does not need O.S. as its D.I.

    public void pay(){
        System.out.println("Payment completed");
//        orderService.getOrderDetails();
//        Not necessary to call this here
    }
}
```

OrderService.java:

```java
@Component
public class OrderService {

//    @Autowired
    private PaymentService paymentService;

    public OrderService(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    public void placeOrder(){
        paymentService.pay();
        System.out.println("Order processed and placed.");
        getOrderDetails();
//        class calling its own method
    }

    public void getOrderDetails(){
        System.out.println("This method returns order details.....");
    }
}
```

O/P:
Payment completed
Order processed and placed.
This method returns order details.....

-------------------------------------------------------------------------------------------

# Bean Scopes

