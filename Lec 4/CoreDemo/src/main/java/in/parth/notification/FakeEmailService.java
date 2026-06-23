package in.parth.notification;

//This is a testing class
public class FakeEmailService implements NotificationService{
    @Override
    public void sendNotification(){
        System.out.println("Dummy Email Sent");;
    }
}
