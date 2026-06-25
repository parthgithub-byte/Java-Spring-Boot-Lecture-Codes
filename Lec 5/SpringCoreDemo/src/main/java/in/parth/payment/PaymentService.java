package in.parth.payment;


import org.springframework.stereotype.Component;

// tells spring core that this is a component that may create or require dependency
//@Component
public interface PaymentService {
    void pay();
}
