package in.arjun.simple;

public class B {
    private A a;

    public B(){
        System.out.println("B object created.");
        this.a = new A();
    }
}
