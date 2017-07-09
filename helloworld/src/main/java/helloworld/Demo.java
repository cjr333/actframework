package helloworld;

import act.Act;
import org.osgl.mvc.annotation.GetAction;

import java.util.Optional;

public class Demo {
    @GetAction("/hello")
    public String hello() {
        return "Hello world";
    }

    @GetAction("/bye/{name}")
    public String bye(String name) {
        return "bye " + Optional.ofNullable(name).orElse("");
    }

    public static void main(String[] args) throws Exception {
        Act.start("Hello World Demo");
    }
}
