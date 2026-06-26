# Spring Core & Dependency Injection — Notes

These notes cover the fundamentals of Spring Core, from setting up the IoC container to the various methods of Dependency Injection. Topics include Java Reflections, `@Component` vs `@Bean`, resolving multiple bean conflicts with `@Primary` and `@Qualifier`, and best practices around constructor injection. Code examples are included throughout.

---

## Setup: Adding Spring Context

In the new approach, dependency handling is delegated entirely to the IoC container rather than the main class.

- Search the MVN Repository for **Spring Context**, select **ver. 7** (compatible with Spring Boot 4).
- Paste the dependency XML into `pom.xml` under `<dependencies>` and reload/sync the pom file.

What we fetch now are not objects created in `Main.java`, but **Beans** from the IoC container, used for dependency injections in constructors or getters of lower classes.

Spring understands which dependencies need to be handled in two ways:

- **Annotation-based** — modern, widely used, and easier
- **XML-configuration-based** — the legacy way

---

## Java Reflections

Before diving into Spring's IoC container, it's important to understand Java Reflections, a concept heavily used throughout Spring tooling.

Consider:

```java
class Student {
    private String name;
    private int age;

    public void getAttendance(String name) { }
    public void print() { }
}
```

When you write:

```java
Student c1 = new Student();
```

...you create a new `Student` object. But when you write:

```java
Class<Student> c1 = Student.class;
```

...you are capturing the **metadata** of the `Student` class into a reference variable `c1`.

Here, the uppercase `Class` is an actual Java class used for **Java Reflection** — to inspect or manipulate code at runtime. It is a generic class declared as `Class<T>`, where `T` is the type of the class being modeled.

**Using `.class` (Class Literal):** used when the type name is known at compile time. Works on primitives too.

```java
Class<String> stringClass = String.class;
Class<Integer> intClass = int.class;
```

In the `Student` example, the metadata stored includes:

- Class name: `Student`
- Fields: `name`, `age`
- Constructors: `Student()`
- Methods: `getAttendance()`, `print()`
- Annotations, etc.

---

## The IoC Container

Spring stores metadata from a file called `AppConfig.java`, which declares itself as a configuration file and scans for components in the package by reading annotations.

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

- `ApplicationContext` is the **interface** for an empty IoC container.
- It is initialized via runtime polymorphism using `AnnotationConfigApplicationContext` — a concrete class that implements `ApplicationContext`.
- The container is stored in the reference variable `context`.
- `AppConfig.class` is the reflection (i.e., `Class<AppConfig>` metadata) passed to the constructor.

This works similarly to:

```java
List<String> list = new ArrayList<>(); // List is an interface implemented by ArrayList
```

Spring processes `AppConfig.java` by reading:

- `@Configuration` — marks `AppConfig.java` as the full configuration class.
- `@ComponentScan("in.parth")` — tells Spring to scan all components inside the specified package (and all sub-packages/internal directories).

> **Note:** Using `@ComponentScan` without a package argument defaults to scanning the package that contains `AppConfig.java`.

Once the line below is executed, Spring creates **Beans** for all discovered components and stores them in the IoC container:

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

Fetching a bean — without explicit object creation:

```java
OrderService order = context.getBean(OrderService.class);
order.placeOrder();
```

Spring resolves the `PaymentService` dependency of `OrderService` automatically. All you need is the `@Autowired` annotation on the `OrderService` constructor:

```java
@Autowired
public OrderService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

**Output:**
```
Payment done
Order is placed.
```

---

## Types of Dependency Injection

### 1. Constructor Injection

```java
@Autowired
public OrderService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

### 2. Setter Injection

Similar to constructor injection, but uses a setter method instead. The `@Autowired` annotation goes on the setter:

```java
@Autowired
public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

**Output:**
```
Payment done
Order is placed.
```

### 3. Field Injection

The dependency field is directly injected from the Beans. Place `@Autowired` above the field declaration:

```java
@Autowired
private PaymentService paymentService;
```

> **Note:** In newer versions of Spring, if a class has only one constructor, `@Autowired` is not required — Spring automatically resolves the dependency.

---

## Why Constructor Injection is Preferred

### 1. Dependency is wired at object creation time

A constructor is called when an instance is created, so the dependency is hardwired from the start. The object **literally cannot be created** without its dependency — the compiler enforces it. This eliminates the risk of `NullPointerException` for the dependent class.

### 2. Supports `final` fields

In Java, `final` on a variable means: *this reference can be assigned only once.*

```java
final int x = 10;
x = 20; // ❌ Compile-time error
```

For object references:

```java
final PaymentService paymentService;
```

This means `paymentService` must point to exactly one object and cannot be reassigned later.

Java allows a `final` field to be assigned in only two ways:

**At declaration:**
```java
final PaymentService paymentService = new PaymentService();
```

**Inside a constructor:**
```java
final PaymentService paymentService;

public OrderService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

After that assignment, it is locked.

**Why not in a setter?** A setter can be called multiple times — Java cannot guarantee "assigned exactly once." Without `final`, a dependency can be swapped unexpectedly:

```java
orderService.setPaymentService(new FakePaymentService()); // dependency changed!
```

With constructor injection and `final`, the dependency is **fixed forever**.

### 3. Easier unit testing

In unit tests, you don't want to call the real payment service (it might contact a database or payment gateway). With constructor injection, you can pass a dummy implementation directly:

```java
PaymentService dummy = new DummyPaymentService();
OrderService order = new OrderService(dummy);
```

> Unit tests typically don't use Spring Core — they rely on plain Java object passing.

---

## Spring Core Execution: Step by Step

| Step | What Happens |
|------|-------------|
| 1 | Spring starts the container |
| 2 | Spring reads `AppConfig.java` |
| 3 | Spring processes `@ComponentScan` |
| 4 | Spring finds all `@Component` classes |
| 5 | Spring creates **Bean definitions** (metadata, works like a PCB) |
| 6 | Spring creates objects, resolving dependencies in order |
| 7 | Application uses the beans |

**Example Bean definition (Step 5):**
```json
{
  "Bean name": "paymentService",
  "Bean class": "PaymentService",
  "Bean object": "new PaymentService()",
  "Scope": "ToBeDiscussedInNextLectures",
  "Dependency": "no"
}
```

**Example object creation order (Step 6):**
```java
PaymentService payment = new PaymentService();        // independent, created first
OrderService order = new OrderService(payment);       // Spring handles dependency
```

---

## Handling Multiple Beans of the Same Type

If there are multiple `@Component` classes implementing the same interface (e.g., `CardPayment` and `UpiPayment` both implementing `PaymentService`), Spring cannot pick one automatically:

```
Error: expected single matching bean but found 2: cardPayment, upiPayment
```

### Solution 1: `@Primary`

Mark one implementation as the default choice:

```java
@Primary
@Component
public class UpiPayment implements PaymentService { ... }
```

**Output:**
```
Paying by UPI
Order is placed.
```

Best used when one implementation is almost always the intended one (e.g., other implementations are test/stub classes).

### Solution 2: `@Qualifier`

Explicitly specify which bean to inject:

```java
public OrderService(@Qualifier("cardPayment") PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

The qualifier value is the bean name — same as the class name in camelCase.

You can also set **custom bean names** via `@Qualifier`:

```java
@Qualifier("cp")
@Component
public class CardPayment implements PaymentService { ... }

@Qualifier("up")
@Component
public class UpiPayment implements PaymentService { ... }
```

```java
public OrderService(@Qualifier("up") PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

**Output:**
```
Paying by UPI
Order is placed.
```

`@Qualifier` works even when a `@Primary` class exists — it takes precedence.

---

## Current Project Files (Reference State)

### `Main.java`
```java
package in.parth;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    static void main() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService order = context.getBean(OrderService.class);
        order.placeOrder();
    }
}
```

### `AppConfig.java`
```java
package in.parth;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("in.parth")
public class AppConfig {
}
```

### `OrderService.java`
```java
package in.parth;

import in.parth.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void placeOrder() {
        paymentService.pay();
        System.out.println("Order is placed.");
    }
}
```

### `PaymentService.java`
```java
package in.parth;

import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    public void pay() {
        System.out.println("Payment done");
    }
}
```

---

## Cases Where `@Component` Does Not Work

There are two scenarios where Spring cannot automatically handle dependencies using `@Component`:

### 1. Primitive or Undefined Class Dependencies

```java
@Component
public class User {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

**Error:**
```
No qualifying bean of type 'java.lang.String' available
```

Spring searches for beans of type `String` and `int`, but these are never defined as beans in the context — unlike custom classes like `PaymentService`.

### 2. External JAR Files

When using `.class` files from `.jar` libraries, we cannot add `@Component` to precompiled files.

---

## The `@Bean` Annotation

For the above cases, use `@Bean` inside `AppConfig.java` to manually define beans:

```java
@Bean
public User createUser() {
    return new User("Parth", 21);
}

@Bean
public CartService createCartService() {
    return new CartService();
}
```

Usage in `Main`:

```java
CartService cs = context.getBean(CartService.class);
User u1 = context.getBean(User.class);
System.out.println(u1.getAge());
cs.addToCart();
```

**Output:**
```
21
Added to cart
```

Spring also auto-injects dependencies between `@Bean` methods within `AppConfig.java`:

```java
@Bean
public CardPayment cardPayment() {
    return new CardPayment();
}

@Bean
public OrderService orderService(PaymentService paymentService) {
    return new OrderService(paymentService);
}
```

**Output:**
```
Paying via card
Order is placed.
```

Since everything is declared in one file, Spring can see that `CardPayment` is a `PaymentService` bean and injects it automatically — no `@Autowired` needed.

### Multiple `@Bean` Conflicts

If two beans of the same type exist in `AppConfig.java`, the same `@Primary` / `@Qualifier` solutions apply:

```java
@Primary
@Bean
public UpiPayment upiPayment() {
    return new UpiPayment();
}
```

Or with qualifiers:

```java
@Bean
@Qualifier("cp")
public CardPayment cardPayment() {
    return new CardPayment();
}

@Primary
@Bean
@Qualifier("up")
public UpiPayment upiPayment() {
    return new UpiPayment();
}

@Bean
public OrderService orderService(@Qualifier("cp") PaymentService paymentService) {
    return new OrderService(paymentService);
}
```

> Using `@Qualifier` on `@Bean` methods in `AppConfig` is generally unnecessary since you're already controlling exactly which objects are created. It's more relevant for `@Component`-based wiring.

---

## `@Bean` with Setter Injection

Without constructor DI, bean setup in `AppConfig.java` requires manual setter calls:

```java
@Bean
public OrderService orderService() {
    PaymentService paymentService = createCardPayment();
    OrderService order = new OrderService();
    order.setPaymentService(paymentService);
    return order;
}
```

> Remember to remove the `final` keyword from the `PaymentService` field when using setter injection — `final` fields cannot be set via setters.

This can be automated using `@Autowired` on the setter in `OrderService`:

```java
// In OrderService.java
@Autowired
public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

```java
// In AppConfig.java
@Bean
public OrderService orderService() {
    return new OrderService();
}
```

Spring will call the `@Autowired` setter on the returned bean object automatically.

**Output:**
```
Paying via card
Order is placed.
```

This is another reason constructor injection is preferred — setter injection requires extra manual steps or relies on post-construction wiring.

---

## `@Bean` vs `@Component`: Summary

| | `@Component` | `@Bean` |
|---|---|---|
| **Scope** | Class-wide | Method-wide |
| **Usage** | Standard Spring-managed classes | Primitives, external JARs, or manual control |
| **Control** | Spring auto-creates and wires | You explicitly define creation logic |

**What if both are defined for the same class?**

If a class has `@Component` and a corresponding `@Bean` method in `AppConfig.java`, only **one** Bean is created — the one defined with `@Bean`. Spring uses the manually defined bean over the auto-scanned one.

> `@Bean` > `@Component`. It takes full precedence. (OG!)