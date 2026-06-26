package in.parth;

import in.arjun.CartService;
import in.parth.payment.CardPayment;
import in.parth.payment.PaymentService;
import in.parth.payment.UpiPayment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan("in.parth")
public class AppConfig {
    @Bean
    public User createUser(){
        return new User("Parth", 21);
    }
//    you can name the method anything, the value are for initialization, you can set them anytime after the Beans are formed using setters

    @Bean
    public CartService createCartService(){
        return new CartService();
    }

//    using bean instead of components for the below
    @Bean
//    @Qualifier("cp")
    public CardPayment createCardPayment(){
        return new CardPayment();
    }

//    @Primary
//    @Qualifier("up")
//    @Bean
//    public UpiPayment upiPayment() {
//        return new UpiPayment();
//    }

//    @Bean
//    public OrderService orderService(@Qualifier("cp") PaymentService paymentService){
//        return new OrderService(paymentService);
//    }

//    @Bean
//    public OrderService orderService(PaymentService paymentService){
//        return new OrderService(paymentService);
//    }

    @Bean
    public OrderService orderService(){
        OrderService order = new OrderService();
        return order;
    }
}
