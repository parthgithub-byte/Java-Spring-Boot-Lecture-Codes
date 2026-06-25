package in.parth;

import in.arjun.CartService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
         /*older way*/
//        PaymentService service = new PaymentService();
//
//        OrderService order = new OrderService(service);
//
//        order.placeOrder();

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        OrderService order = context.getBean(OrderService.class);

        order.placeOrder();

        CartService cs = context.getBean(CartService.class);
        User u1 = context.getBean(User.class);

        System.out.println(u1.getAge());

        cs.addToCart();

/*
* Read the file notes.md to see the actual flow of the above program
* */
    }
}
