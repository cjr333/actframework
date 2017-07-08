package helloworld;

import act.Act;
import org.osgl.mvc.annotation.GetAction;

public class Demo {
    @GetAction("/hello")
    public String hello() {
        return "Hello world";
    }
    public static void main(String[] args) throws Exception {
        Act.start("Hello World Demo");
    }
}
