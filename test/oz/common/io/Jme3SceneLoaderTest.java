package oz.common.io;

import org.lwjgl.opengl.GL11;
import oz.common.graphics.GLFWInitializer;
import oz.common.scene.Node;
import oz.common.scene.NodeComponent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Jme3SceneLoaderTest {
    
    public static void main(String[] args) {
        GLFWInitializer context = new GLFWInitializer();
        context.createContext(0, 0, 512, 512);
        Jme3SceneLoader loader = new Jme3SceneLoader();
        Node root = loader.loadGltf("SparseGltf/sparse.gltf");
        List<Node> children = root.getChildren();
        assertEquals(children.size(), 1);
        children = children.get(0).getChildren();
        assertEquals(children.size(), 1);
        List<NodeComponent> components = children.get(0).getComponents();
        assertEquals(components.size(), 1);

        float color = 0.0f;
        while (!context.isClosed()) {
            GL11.glClearColor(color, color, color, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            root.renderRecursively();
            context.swapBuffer();
        }
    }
}