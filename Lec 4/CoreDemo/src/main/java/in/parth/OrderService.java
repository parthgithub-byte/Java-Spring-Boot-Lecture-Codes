//package in.parth;
//
//import in.parth.notification.EmailService;
//import in.parth.notification.NotificationService;
//import in.parth.notification.PopUpNotificationService;
//import in.parth.notification.SmsService;
//
//public class OrderService {
//    NotificationService n1 = new EmailService();
//    NotificationService n2 = new SmsService();
//    NotificationService n3 = new PopUpNotificationService();
//
//    public void placeOrder(){
//        System.out.println("Order placed");
//        n1.sendNotification();
//        n2.sendNotification();
//        n3.sendNotification();
//    }
//}

/*    Instead of calling any 3 of them and changing their types as per the need eah time, we will just not take on that responsibility. A class ust only have its independent responsibilities to be taken care of. We will leave deciding and creating the NotificationService object to the Main.java file only.
Thus, in this way, the class focuses only on the business logic, not creating and managing the objects (factory).
 */

package in.parth;

import in.parth.notification.EmailService;
import in.parth.notification.NotificationService;
import in.parth.notification.PopUpNotificationService;
import in.parth.notification.SmsService;

public class OrderService {
    NotificationService notification;   // some instance of the NotificationService class which the class OrderService 'has' in it

//    dependency injection using constructor
    public OrderService(NotificationService notification){
        this.notification = notification;   // this is how the notification object was prsent as a part of the class, it was created before creating the instance of OrderService and passed as a required argument to the constructor in the calling class (Main.java).
    }

    public void placeOrder(){
        System.out.println("Order placed");
        notification.sendNotification();
    }

//    if no object of NotificationService is passed in constructor, eg
    public OrderService(){

    }
//    then, we can use another type of dependency injection can be used, i.e. using Setters
//    we can automatically create setters/ fetters in IntelliJ IDEA by right-clicking
//    ths presents ready-made setting methods for data members that are not initialized
    public void setNotification(NotificationService notification) {
        this.notification = notification;
    }
}