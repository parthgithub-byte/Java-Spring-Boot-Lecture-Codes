package in.parth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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
