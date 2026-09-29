package tests.oop;

import org.testng.annotations.Test;

public class PS1 extends PS {

    @Test
    public void testRun(){
        int a = 5;
        PS2 ps2=new PS2(a);
        doThis();
        System.out.println(ps2.increment());
        System.out.println(ps2.decrement());
//        PS3 ps3=new PS3(a);
        System.out.println(ps2.multiplyTwo());
        System.out.println(ps2.multiplyThree());
    }
}
