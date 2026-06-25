package in.parth.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@Qualifier("up")
public class UpiPayment implements PaymentService{
    @Override
    public void pay(){
        System.out.println("Paying by UPI");
    }
}
