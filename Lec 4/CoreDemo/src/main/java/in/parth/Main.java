package in.parth;

import in.parth.notification.EmailService;
import in.parth.notification.FakeEmailService;
import in.parth.notification.NotificationService;
import in.parth.notification.PopUpNotificationService;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
//            OrderService order = new OrderService(); // older way

            NotificationService notification = new EmailService();
//            decide which way to call the notification, here itself

            OrderService order = new OrderService(notification);
//            Updated way of using the OrderService class

            order.placeOrder();

//            testing with the dummy class
            NotificationService notif = new FakeEmailService();
            OrderService o = new OrderService(notif);
            o.placeOrder();

//            using the setter method for dependency injection, instead of passing through the main function with constructor of OrderService
            OrderService o2 = new OrderService();
            NotificationService notif2 = new PopUpNotificationService();
            o2.setNotification(notif2);
            o2.placeOrder();

//            there is also a third type of dependency injection, field injection, that is not usable on the (by-default) private variables though

        /*
        * As we saw in this lecture, instead of a monolithic file handling all the functionalities, we first handled the individual responsibilities to the individual classes (Remember 'S' from SOLID)
        * Then we made it so that all the classes related to notifications, email, popup, sms, etc. are grouped in a package to be implemented by a common interface (Remember 'I' from SOLID)
        * Still, the high-level module in system (Main.java) depends on the creation of lower level modules (NotificationService types) in the middle level modules (Here OrderService). This breaks 'D' rule from SOLID.
        * The 'D' rule is nothing but Inversion of Control (IoC) which is a main principle in Spring Core, we do not give the control of managing the objects to the lower modules, but the higher modules in the tree.
        * Eg, Here, in the first approach we saw, Main -> OrderService and OrderService->NotificationService and lastly, Main ->can use-> NotificationService method
        * After the Dependency Injection:
        * Main -> OrderService , Main -> NotificationService, and later Main -> (using the NotificationService object in Main) -> OrderService
        * Thus the control of the EmailService (i.e. NotificationService) was shifted from the lower OrderService to the higher Main class.
        *
        * Now, in the Spring core, the IoC is not handled by the Main class file directly so that it does not become cluttered with object management. It should be easier to configure later. (Somewhat 'O' in SOLID)
        * For that we use IoC container, a higher level entity like Main.java, which handles such tasks:
        *  - Creates Objects (as seen above)
        *  - Manages Objects (creation and destruction of the object resources)
        *  - Connects Objects Together (Providing the required object dependency as the argument to the other, eg. how we provided the notification object as the dependency to the OrderDetails object through constructors or setters.
        *
        * For next lecture:
        * ## We will see IoC containers
        *
        *
        *
        *
        * ## What we have for:
        *       Java Code/ File structure: Objects
        *       IoC: Beans
        * similar, just a fancy name. Now, every Bean is indeed an object but not vice versa. They are specifically objects of the IoC containers. (I never knew this difference after studying WT for a full semester just recently)
        *
        * */
        }
}

