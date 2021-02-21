package oz.common.graphics;

import org.lwjgl.PointerBuffer;
import org.lwjgl.egl.EGL14;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.stream.LongStream;

import static org.lwjgl.egl.EXTDeviceBase.eglQueryDevicesEXT;
import static org.lwjgl.egl.EXTPlatformBase.eglGetPlatformDisplayEXT;
import static org.lwjgl.egl.EXTPlatformDevice.EGL_PLATFORM_DEVICE_EXT;

/**
 * EGLの初期化を行う関数の集合
 */
public class EGLInitializer {

    /**
     * @return 使用可能なすべてのDeviceを取得します
     */
    private static long[] queryDevices() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer devices = stack.mallocPointer(64);
            IntBuffer numDevices = stack.ints(0);
            eglQueryDevicesEXT(devices, numDevices);
            checkError();
            System.out.println(numDevices.get(0));
            return LongStream.range(0, numDevices.get(0))
                    .map(i -> devices.get((int)i))
                    .toArray();
        }
    }

    /**
     * @param devices queryDevicesで取得したdevice
     * @param index　
     * @return
     */
    private static long getSuitableDisplay(long[] devices, int index) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long display = EGL14.EGL_NO_DISPLAY;
            if (0 <= index && index < devices.length) {
                long device = devices[index];
                display = eglGetPlatformDisplayEXT(EGL_PLATFORM_DEVICE_EXT, device, (IntBuffer)null); // no attribute
                checkError();
                return display;
            } else {
                throw new RuntimeException("Illegal device index.");
            }
        }
    }

    private static void chooseConfig(long display, PointerBuffer config) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer attributes = stack.ints(
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_DEPTH_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_BIT,
                    EGL14.EGL_NONE
            );
            IntBuffer numConfigs = stack.ints(0);
            EGL14.eglChooseConfig(display, attributes, config, numConfigs);
            checkError();
        }
    }

    public static void initializeEGL(long display) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer major = stack.ints(0);
            IntBuffer minor = stack.ints(0);
            if (!EGL14.eglInitialize(display, major, minor)) {
                throw new RuntimeException("Failed to initialize egl.");
            }
            checkError();
        }
    }

    private static long createPbufferSurface(long display, long config) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
           IntBuffer attributes = stack.ints(
                   EGL14.EGL_WIDTH, 1,
                   EGL14.EGL_HEIGHT, 1,
                   EGL14.EGL_NONE
           );
           long surface = EGL14.eglCreatePbufferSurface(display, config, attributes);
           if (surface == EGL14.EGL_NO_SURFACE) {
               checkError();
           }
           return surface;
        }
    }

    private static void checkError() {
        int error = EGL14.eglGetError();
        if (error != EGL14.EGL_SUCCESS) {
            System.out.format("EGL error: code%d\n", error);
            System.out.println("see https://javadoc.lwjgl.org/index.html?org/lwjgl/egl/EGL.html");
            throw new RuntimeException("egl error happened.");
        }
    }

    public static void printInfo() {
        String glVersion = GL11.glGetString(GL11.GL_VERSION);
        String glVendor = GL11.glGetString(GL11.GL_VENDOR);
        String glRenderer = GL11.glGetString(GL11.GL_RENDERER);
        String glslVersion = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
        System.out.println("OpenGL version: " + glVersion);
        System.out.println("OpenGL vendor: " + glVendor);
        System.out.println("OpenGL renderer: " + glRenderer);
        System.out.println("GLSL version: " + glslVersion);
        String[] extensions = GL11.glGetString(GL11.GL_EXTENSIONS).split(" ");
        System.out.println(extensions.length + " extensions found.");
        for (String ext : extensions) {
            System.out.println("  " + ext);
        }
    }

    public static void createContext(int index) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // define environment
            long[] devices = queryDevices();
            long display = getSuitableDisplay(devices, index);
            initializeEGL(display);

            // define configuration
            PointerBuffer config = stack.mallocPointer(1);
            chooseConfig(display, config);
            long surface = createPbufferSurface(display, config.get(0));
            EGL14.eglBindAPI(EGL14.EGL_OPENGL_API);
            checkError();

            // create context
            long context = EGL14.eglCreateContext(display, config.get(0), EGL14.EGL_NO_CONTEXT, (IntBuffer)null);
            checkError();
            EGL14.eglMakeCurrent(display, surface, surface, context);
            GL.createCapabilities();

            printInfo();
        }
    }
}
