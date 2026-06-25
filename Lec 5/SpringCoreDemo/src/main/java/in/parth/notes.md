
* In the new way, we do not leave dependency handling to even the main class, but the IoC container.
* For that, search in the MVN Repository for 'Spring Context'. Select ver. 7, it is compatible with SpringBoot 4.
* Paste the dependecy XML in pom.xml under the <dependencies> and reload/ sync the pom file
* Now, what we will be fetching will not be the objects created in the Main.java,
* but Beans in the IoC container, for dependency injections (in constructors or
* getters of the lower classes).
There are two ways in which the Spring understands which are the dependencies that need to be handled.
- Annotation-based (modern, widely-used, easier)
- XML-configuration-based (legacy way)

Before starting, let's understand a pretty imp concept widely used in coding 
with Spring tools:

## Reflections in Java 
Consider the following code:
```java
class Student{
//    has some member functions
    private String name;
    private int age;
    
//    may include a defined constructor
    
//    or some methods
    public void getAttendance(String name){
        
    }
    public void print(){
        
    }
}
```
Now,
```java
Student c1 = new Student();
```
may create a new student object, but when we say this:
```java
Class<Student> c1 = Student.class;
```

it means that we are taking in the metadata of a class Student in a reference variable c1.
Here the uppercase Class is an actual class used primarily for Java Reflection to inspect or manipulate code at runtime.
It is a generic class declared as Class<T>, where T is the type of the class modeled.
Using .class (Class Literal):
Used when the type name is known at compile time. Works on primitives too.
```java
Class<String> stringClass = String.class;
Class<Integer> intClass = int.class;
```

In our above eg: the metadata stored is-
class Name - Student;
fields - name, age;
Constructors - Student();
Methods - getAttendance(), print()
Annotations, etc.

## IoC Container:
We store the metadata from the file called AppConfig.java, which in turn, tells that it is a configuration file
and scans for the components in the package by reading the annotations.

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```
Here, ApplicationContext is the interface for an empty IoC container. Here we initialize it using a 
run-time polymorphism using the AnotationConfigApplicationContext class. It is a concrete class which helps 
implement the ApplicationContext IoC container stored as a reference variable: context, using annotations 
in the given file: AppConfig.java.
(Eg: It works similar to: 
```java
List<String>list = new ArrayList<>(); // where List is an interface implemented by ArrayList
```
)

Here, the AnotationConfigApplicationContext created an IoC using the "AppConfig.class" it received, i.e. 
the reflection of class AppConfig.java (i.e. Class<AppConfig> metadata).

Spring did all it by scanning the annotations. Firstly, having the annotation `@Configuration` tells that AppConfig.java
is the full congfiguration file or a special configuration class. Then we search for the `@ComponentScan("...")`.
That is, we searched for all the components inside the string argument which is the package for searching all 
components. In our case, it is `@ComponentScan("in.parth")`. Note, Spring core also scans through all the sub-packages/ 
any internal directories of the given package. Also, we can also annote as only `@ComponentScan` instead of specifying 
the exact package; it by default searches for the present package containing AppConfig.java then.

Ok, so once below line is read:
```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```
all the things we discussed above are carried out, giving us the 'Beans' of the components stored in the IoC Container.
Let us see how we fetch them w/o explicit object creation.
```java
OrderService order = context.getBean(OrderService.class);
order.placeOrder();
```

Note, here we are not creating the OrderService instance, rather 'having' it from the top level entity IoC 
container: context from one of its beans using the function: getBean(). Even here we refer to the type of bean we want
using the Reflection using .class method.
Now, notice above that, we got the bean for order service to call its method, but as per our code, OrderService
needs the object (or bean here) of PaymentService in its constuctor to get executed. But we don't really need to 
fetch its bean again like for OrderService. Spring can handle it on its own. For this, we just need to add this 
annotation on top of the OrderService constructor.
`Autowired`
Done, now run the main file with just 2 above lines.

O/P:
Payment done  
Order is placed.

[//]: # (31:50)

Types of DI (Dependency Injections) under Spring IoC container:
Above we saw the usual DI:
1) DI using Constructor

```java
@Autowired
public OrderService(PaymentService paymentService){
    this.paymentService = paymentService;
}
```

There are also two other types to achieve this in a similar way:
2) DI using setters:

Similar to how we use constructors for setting up the dependency of one object to other class,
we use setter methods. This method can be especially intuitive in since, unlike parametrized constructor above,
we do not require any parameter in the main code while declaring the object to call, however this issue does not occur in Main if context container
is the one managing the resources anyway. So, obtaining the other class dependency this way becomes round-about if using the Spring IoC container.
How to do DI using setter?
=> Just declare the `@Autowired` annotation at the top of the setter instead of the constructor.

```java
@Autowired
public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

O/P:
Payment done
Order is placed.
i.e. works fine

3) Field Injection:
Since our dependency in OrderService is just an instance of class PaymentService that we use its own field,
we straight up take it from the Beans. Just add the `@Autowired` annotation above the line of dependency field.

```java
@Autowired
private PaymentService paymentService;
```

By the way, in newer versions of spring, if a class has only one constructor, then to add a DI, we do not need
the `@Autowired` annotation. Spring automatically resolves the dependencies even if we write:
```java
public OrderService(PaymentService paymentService){
    this.paymentService = paymentService;
}
```


## Why is the Constructor DI mostly preferred?
- Dependency gets wired at the time of object creation
- Final can be used
- Easy to test the class

### 1.
We know the first point; a constructor is anyway called when an instance is created, so having that same method
hardwire DI is a much more straightforward design compared to having another setter-like method to do so or 
direct access to a field like in Field DI. Here, the object literally cannot be created without the dependency.
The compiler enforces it. This avoids cases of NullPointerException for the class needing dependency.

### 2.
Now, in Java, final has a very important use-case.
For variables, final means:
`This reference can be assigned only once.`

Example:

```java
final int x = 10;
x = 20; // ❌ Compile-time error
```

For object references:
```java
final PaymentService paymentService;
```

means:
```
The variable paymentService must point to exactly one object.
It cannot be reassigned later. 
```

Now,
Java allows a final field to be assigned:

1. At declaration
```java
final PaymentService paymentService = new PaymentService();
```

OR

2. Inside a constructor
```java
final PaymentService paymentService;

public OrderService(PaymentService paymentService) {
this.paymentService = paymentService;
}
```
After that assignment, it is locked.

## Why not in a setter?
because a setter can be called multiple times. Java cannot guarantee "assigned exactly once".

## Why is that beneficial?

Imagine:
```java
OrderService orderService = ...
```
and later:
```
orderService.setPaymentService(new FakePaymentService()); // using some setter method
```
Now the dependency changed unexpectedly. This can lead to bugs.
With constructor injection:
```java
private final PaymentService paymentService;
```
the dependency is fixed forever.

### 3.
Why does DI at constructor make unit testing faster?
Unlike in production, for a unit test, you don't want to call the real payment service 
(it might contact a database or payment gateway).
We can easily do the unit tests from the parameterised constructor:
```java
PaymentService dummy = new DummyPaymentService();
OrderService order = new OrderService(dummy);
```
[Note, the unit tests usually don't use Spring Core, but plain Java object passing]

# Understanding the Steps of Spring Core Execution in Brief:
- Step 1: Spring starts the container
- Step 2: Spring reads AppConfig.java
  `ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);`

- Step 3: Spring processes @ComponentScan (Into a specific package if classified, else same directory)
- Step 4: Spring finds @Component classes
- Step 5: Spring creates Bean definitions (Metadata of Beans to be created for Autowiring, works like PCB)
  PaymentService:
  ```json
  "Bean name" : "paymentService",
  "Bean class" : "PaymentService",
  "Bean object" : "new PaymentService()",
  "Scope" : "ToBeDiscussedInNextLectures",
  "Dependency" : "no"
  ```
- Step 6: Spring starts creating objects
  ```java
  PaymentService payment = new PaymentService(); // independent objects are created first
  OrderSevice order = new OrderService(payment); // requires dependency, handled by Spring itself
  ```
- Step 7: Our application uses these beans (int the main block)
```java
OrderService order = context.getBean(OrderService.class);
order.placeOrder();
```

Right now, there is only one time of PaymentService as dependency for 
the dependent class. What if there are multiple objects available that
are components eligible as dependency from above, how are they autowired?
Right now, our system is tightly coupled. 

Let me first write the present 
files as they are present right now:

1. Main.java:
```java
package in.parth;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class Main {
  static void main() {
    /*older way*/
//        PaymentService service = new PaymentService();
//
//        OrderService order = new OrderService(service);
//
//        order.placeOrder();

    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    OrderService order = context.getBean(OrderService.class);
    order.placeOrder();
  }
}

```

2. AppConfig.java
```java
package in.parth;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
@Configuration
@ComponentScan("in.parth")
public class AppConfig {
}
```

3. OrderService.java

```java
package in.parth;

import in.parth.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// tells spring core that this is a component that may create or require dependency
@Component
public class OrderService {
  //    Field DI will involve taking the dependency, which is also a filed in the class, directly from the beans
//    @Autowired
  private final PaymentService paymentService;

  //    Constructor DI, @Autowired annotation if the class has one and only constructor
//    @Autowired
  public OrderService(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

//    Setter DI
//    @Autowired
//    public void setPaymentService(PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }

  public void placeOrder() {
    paymentService.pay();
    System.out.println("Order is placed.");
  }
}
```

4. PaymentService.java
```java
package in.parth;

import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    public void pay(){
        System.out.println("Payment done");
    }
}
```

Now, let us move PaymentService to the package "payment" as
an interface and create another classes CardPayment and 
UpiPayment as actual implementations.
For now, PaymentService is just an interface with no implementaion 
for the pay() method. So, running the code, I got:

O/P:
Error:-
No qualifying bean of type 'in.parth.payment.PaymentService' available: expected at least 1 bean which qualifies as autowire candidate.

as the error. Ok, now remove the annotation `@Component` from above the interface to the actual implementation, 
say `CardPayment`. [Btw, since PaymentService is an interface now, having or not having the `@Component` annotation
will not make any difference.]

O/P:
Paying via card
Order is placed.

Similar output is generated if the annotation component is placed in the UpiPayment class instead.
But what if the annotation @Component is present in both the classes?

O/P:
Error:-
Unsatisfied dependency expressed through constructor parameter 0: No qualifying bean of type 'in.parth.payment.PaymentService' available: 
expected single matching bean but found 2: cardPayment,upiPayment

Solutions:
1. `@Primary` annotation
2. `@Qualifier` annotation

Using @Primary gives an easy way out. Just note @Primary on the top of one of the vomponent that you want to always be the one being selected unless sppecified exactly.
Eg. I am mentioning @Component above both classes but only mentioning @Primary above UPI payment.

O/P:
Paying by UPI
Order is placed.

This approach is best when only one service is actually ever needed and we want to show this quickly, eg. other implementations are test classes. 
However, that is not true in most cases, and even for other being secondary/ test implementations, there should be way to implement even those.
Solution:
@Qualifier annotation.
Since we have multiple Beans eligible to be sent as dependencies, we just need to manually mention which dependency is exactly required out of all the present components 
to be injected.

Write `@Qualifier` above both the classes similar to the `@Component` annotation.
Now which qualifier would you actually like to pass in the Constructor where we actually pass the dependency.
```java
public OrderService(@Qualifier("cardPayment") PaymentService paymentService){
        this.paymentService = paymentService;
}
```

=> we pass the Bean name, it is the same as the class name, just in the camel case.
O/P:
Paying via card
Order is placed.

Now, even if there is or there is not a @Primary class, we can choose which exact dependency to inject as we need.

We can also set custom bean names through the @Qualifier annotations with parameter strings. 
Eg: `@Qualifier("up")` or `@Qualifier("cp")`


```java
public OrderService(@Qualifier("up") PaymentService paymentService){
this.paymentService = paymentService;
}
```

O/P:
Paying by UPI
Order is placed.


# Cases where the @Component does not work, i.e. Sring cannot handle the dependencies

1) When dependency class is a primitive/ non-defined class object
2) When using external JAR files (with precompiled read-only .class files) for functionalities

First, see the below constructor input:

```java
package in.parth;
import org.springframework.stereotype.Component;
@Component
public class User {
private String name;
private int age;

    public User(String name, int age){
        this.name = name;
        this.age = age;
    }
//  ....
```
O/P:
Error:-
No qualifying bean of type 'java.lang.String' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}

here, you can observe that, even if Spring tries to create a Bean out of this class, it cannot resolve/ figure-out the dependency of the Constructor parameters. It will search for the Beans String and Integer, but it won't get their objects in the context since they are never defined, unlike objects like PaymentService

Similarly, when using the .class form .jar files, we can't really add @Component to the precompiled files. Here, I created another project, installed it to the local repo (.m2) through maven and now we can access it by addid following to the pom.xml of the current project and rebuilding it.

```xml
<dependency>
    <groupId>in.arjun</groupId>
    <artifactId>SpringCoreDemo2</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Now, I can access its functions very well through the main function by manually creating and pssing the objects (w/o Spring core initializing it).
```java
CartService cs = new CartService();
cs.addToCart();
```

O/P:
Added to cart

But can we make spring to handle this? No.
Yet, we can make spring figure it out: using AppConfig.java
Using the @Bean annotation, create methods returning the class methods directly:

```java
    @Bean
    public User createUser(){
      return new User("Parth", 21);
    }
//    you can name the method anything, the value are for initialization, you can set them anytime after the Beans are formed using setters

    @Bean
    public CartService createCartService(){
        return new CartService();
    }
```

Using the beans as usual now:
```java
CartService cs = context.getBean(CartService.class);
User u1 = context.getBean(User.class);
System.out.println(u1.getAge());
cs.addToCart();
```

O/P:
21
Added to cart


Thus Beans can be created in two ways:
1. @Component: Class wide, normal use
2. @Bean: Method wide, used when Bean cannot be created in above discussed cases