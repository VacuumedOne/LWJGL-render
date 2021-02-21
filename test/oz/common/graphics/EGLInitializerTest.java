package oz.common.graphics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EGLInitializerTest {
    @Test
    public void createContextTest() {
        try {
            EGLInitializer.createContext(0);
        } catch (RuntimeException e) {
            Assertions.assertEquals(0, 0);
            Assertions.fail("Fail to create EGL context");
        }
    }
}
