package in.parth;

import in.parth.payment.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

// tells spring core that this is a component that may create or require dependency
@Component
public class OrderService {

//    Field DI will involve taking the dependency, which is also a filed in the class, directly from the beans
//    @Autowired
    private final PaymentService paymentService;


//    Constructor DI, @Autowired annotation if the class has one and only constructor
//    @Autowired
    public OrderService(@Qualifier("up") PaymentService paymentService){
        this.paymentService = paymentService;
    }

//    Setter DI
//    @Autowired
//    public void setPaymentService(PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }

    public void placeOrder(){
        paymentService.pay();
        System.out.println("Order is placed.");
    }
}