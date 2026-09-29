package tests.oop;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class PS {

    public void doThis(){
        System.out.println("Hello");
    }

    @BeforeMethod
    public void beforeMethod(){
        System.out.println("Run me first");
    }

    @AfterMethod
    public void afterMethod(){
        System.out.println("Run me last");
    }
}
