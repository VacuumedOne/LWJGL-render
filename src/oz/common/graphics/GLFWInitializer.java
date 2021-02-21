package oz.common.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
// see https://github.com/jMonkeyEngine/jmonkeyengine/blob/5d00bb3fff5a0188c87ed998319547e5ffa9ef3a/jme3-lwjgl3/src/main/java/com/jme3/system/lwjgl/LwjglContext.java
// see https://github.com/jMonkeyEngine/jmonkeyengine/blob/5d00bb3fff5a0188c87ed998319547e5ffa9ef3a/jme3-lwjgl3/src/main/java/com/jme3/system/lwjgl/LwjglWindow.java
public class GLFWInitializer {
    private long window = 0L;

    public void createContext(int posX, int posY, int width, int height) {
        GLFWErrorCallback.createPrint(System.err);

        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

        window = GLFW.glfwCreateWindow(width, height, "", 0, 0);
        if (window == 0L) {
            throw new RuntimeException("Unable to initialize GLFW window");
        }
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);
        GL.createCapabilities();
        GLFW.glfwSetWindowPos(window, posX, posY);
    }

    public boolean isClosed() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void swapBuffer() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }
}
