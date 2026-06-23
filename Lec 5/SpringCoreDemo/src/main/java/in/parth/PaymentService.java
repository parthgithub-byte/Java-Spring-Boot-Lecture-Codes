package in.parth;


import org.springframework.stereotype.Component;

// tells spring core that this is a component that may create or require dependency
@Component
public class PaymentService {
    public void pay(){
        System.out.println("Payment done");
    }
}
