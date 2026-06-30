By default, Beans in java are created with "Singleton" scope. That means, when we use the Spring
core, a bean is created once (exactly once) and the same bean is used by all the other components 
requiring it as the dependency.

OrderService:

@Component
@Scope("singleton)      // optional
public class OrderService {
    public OrderService(){
        System.out.println("OrderService created.");
    }

    public void placeOrder(){
        System.out.println("Order Placed successfully.");
    }
}

Main:

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService order = context.getBean(OrderService.class);

        OrderService order2 = context.getBean(OrderService.class);

        System.out.println(order == order2); 

Output:
OrderService created.
true

Thus, we saw that the constructor for the Bean creation is called exactly once and the resultant
reference variables point to the same object/ bean. In fact, since OrderService is always detected 
by the @Component annotation, it means the Spring core always loads it, even if the Main code is 
blank after we 'up' the AppConfig in the context container.

Main:

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

O/P:
OrderService created.

Singleton scope helps in stateless services where single service is used by other components 
as they require in common.

Creating two classes A and B:

A:

@Component
public class A {
    private OrderService orderService;
    
    public A(OrderService orderService){
        this.orderService = orderService;
    }
}

B:

@Component
public class B {
    private OrderService orderService;

    public A(OrderService orderService){
        this.orderService = orderService;
    }
}

O/P:
OrderService created.

Now, note that, in the Singleton scope, the bean is always created, though once.

Now, let us make the OrderService as a "prototype" scope bean.


OrderService:

@Component
//@Scope("singleton") // optional
@Scope("prototype")
public class OrderService {
    public OrderService(){
        System.out.println("OrderService created.");
    }

    public void placeOrder(){
        System.out.println("Order Placed successfully.");
    }
}


Main.java:

public class Main {
    static void main() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService order = context.getBean(OrderService.class);

        OrderService order2 = context.getBean(OrderService.class);
        System.out.println(order == order2);    // both refer to the different objects
    }
}

O/P:
OrderService created.
OrderService created.
OrderService created.
OrderService created.
false

Thus, each reference variable caused the creation of its separate object. Now, if I removed 
order and order2 from the Main, only two objects of OrderService are created. These are because 
OrderService needs to be initialized for dependencies in A and B. If I unmarked A and B as components [or
 remove the Constructor D.I.], no Bean is created. 

This is unlike the case where by default in Singleton scope, Spring always initializes the 
Beans of all the components that will be given as dependencies in other Beans, eg OrderService
for A/ B.


Thus, we say, these scopes have:
1. Singleton Scope (also default): Eager Initialization
2. Prototype Scope: Lazy Initialization

# Imp: Singleton Scope and Singleton Design Pattern:

The name singleton scope comes from the singleton design pattern, that says, there must exist 
only one Java object for a class to be used. This is similar to above Singleton scope for 
beans creation, but not exactly the same. Here, in signgleton/ default scope for a bean, it 
means Spring creates single Bean object per declaration. What does that mean?
See, a Bean is created from every component in the package scanned. So, by default Spring does 
create a single object as its "Singleton" nature. But there can indeed be multiple Beans for the 
same class if we declare it so. This happens if we declare the beans in AppConfig.

Let us return two similarly declared Bean declarations in AppConfig. Also comment all other 
code in Main and comment @Component in all the classes (including OrderService) to ignore them for 
now for making their own Beans automatically. Just up the context.

Main:

public class Main {
static void main() {
    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    }
}


AppConfig:

@Configuration
@ComponentScan
public class AppConfig {
    @Bean
    public OrderService giveOrder(){
        return new OrderService();
    }

    @Bean
    public OrderService putOrder(){
        return new OrderService();
    }
}

OrderService:
@Component
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

O/P:
OrderService created.
OrderService created.

Thus, multiple objects in Spring IoC container CAN exist. This is a common confusion.
[
Now, what if we uncomment @Component from A and B classes?
We get an error saying no unique bean exists for the dependencies and spring cannot 
decide on its own, which to choose. We already saw solving this using Primary/ Qualifier
]

When to use which scope?
Spring by default chooses Singleton Beans, but they are not always the right choice. 
Correct choice:
1. Singleton: stateless
2. Prototype: stateful

Notice below User class:

public class User {
    private string username;
    private int age;
    
    //    contains states of user -> stateful

    public string getUsername() {
        return username;
    }

    public void setUsername(string username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

Such classes contain specific "data members" that should be unique for each reference variable
as separate instances. Thus called "stateful". Perfect candidate for Prototype Scope Beans.


Our previous classes like OrderService have no "states", i.e. instance specific data stored. 
They are "stateless". Perfect Candidates for Singleton Scope Beans. 
Eg. Typically has "service", "manager" or "driver" classes that perform specific actions for
their methods.

There are other scopes too. We'll see them later.

3. Request Scope: Create objects when a HTTP request comes
4. Session scope: Create objects for each new user session
5. Application scope: Create an object for a complete application run (similar to singleton)

This section is completed, off to BeanInitializationDemo ->>>