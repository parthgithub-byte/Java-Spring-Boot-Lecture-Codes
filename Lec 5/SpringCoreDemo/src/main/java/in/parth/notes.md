
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

31:50