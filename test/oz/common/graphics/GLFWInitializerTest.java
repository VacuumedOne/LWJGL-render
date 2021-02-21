package oz.common.graphics;

import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL11;

import static org.junit.jupiter.api.Assertions.*;

class GLFWInitializerTest {
    @Test
    public void testCreateContext() {
        GLFWInitializer context = new GLFWInitializer();
        try {
            context.createContext(0, 0, 512, 512);
        } catch (RuntimeException e) {
            fail();
        }

        long start = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        while (!context.isClosed() && (now - start < 5000)) {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            context.swapBuffer();
            now = System.currentTimeMillis();
        }
    }

    public static void main(String[] args) {
        GLFWInitializer context = new GLFWInitializer();
        try {
            context.createContext(0, 0, 512, 512);
        } catch (RuntimeException e) {
            fail();
        }

        float color = 0.0f;
        float step = 0.01f;
        boolean up = true;
        while (!context.isClosed()) {
            GL11.glClearColor(color, color, color, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            context.swapBuffer();
            if (up) {
                color += step;
                if (color > 1.0) up = false;
            } else {
                color -= step;
                if (color < 0.0) up = true;
            }
        }
    }
}