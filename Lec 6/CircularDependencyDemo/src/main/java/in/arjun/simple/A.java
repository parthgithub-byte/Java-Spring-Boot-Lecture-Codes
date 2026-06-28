package in.arjun.simple;

public class A {
    private B b;

    public A(){
        System.out.println("A object created.");
        this.b = new B();
    }
}
