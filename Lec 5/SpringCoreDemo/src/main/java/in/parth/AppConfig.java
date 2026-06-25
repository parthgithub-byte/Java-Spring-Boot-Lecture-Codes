package in.parth;

import in.arjun.CartService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
}
