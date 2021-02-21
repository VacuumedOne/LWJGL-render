package oz.common.scene;

import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL11;
import oz.common.graphics.GLFWInitializer;
import oz.common.scene.components.RenderComponent;
import oz.common.scene.primitive.Quad;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuadTest {
    @Test
    public void quadTest() {
        Node root = new Quad();
        List<NodeComponent> components = root.getComponents();

        // componentは2つ
        assertEquals(components.size(), 1);

        boolean haveRenderer = false;
        for (NodeComponent component : components) {
            if (component instanceof RenderComponent) {
                haveRenderer = true;
            }
        }
        assertTrue(haveRenderer);
    }

    public static void main(String[] args) {
        GLFWInitializer context = new GLFWInitializer();
        try {
            context.createContext(0, 0, 512, 512);
        } catch (RuntimeException e) {
            fail();
        }

        Node root = new Quad();
        root.startupRecursively();

        float color = 0.0f;
        while (!context.isClosed()) {
            GL11.glClearColor(color, color, color, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            root.renderRecursively();
            context.swapBuffer();
        }
    }
}