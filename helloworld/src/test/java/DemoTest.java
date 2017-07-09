import helloworld.Demo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;
import java.util.Optional;

public class DemoTest {
    @Test
    public void StringTest() {
        System.out.println("bye " + null);
        System.out.println("bye " + Optional.ofNullable(null).orElse(""));
    }

    @Test
    public void byeTest() {
        String ret = new Demo().bye("cjr333");
        Assert.assertTrue(Objects.equals(ret, "bye cjr333"));
    }
}
