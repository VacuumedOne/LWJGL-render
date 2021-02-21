
package oz.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageUtilsTest {
    @Test
    public void testGetMessage() {
        assertEquals("Hello World!", MessageUtils.getMessage());
    }

    @Test
    public void testGetMessage2() {
        assertEquals("Hello World?", MessageUtils.getMessage2());
    }
}
