package in.arjun;

import in.arjun.simple.A;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            context.getBean(OrderService.class).placeOrder();   // done in short form this time
//        A a = new A();
    }
}
