package tests.oop;

public class PS2 extends PS3{
    int a;

    public PS2(int i){
        super(i); // parent constructor call
        this.a=i;
    }
    public int increment(){
        a=a+1;
        return a;
    }

    public int decrement(){
        a=a-1;
        return a;
    }
}
