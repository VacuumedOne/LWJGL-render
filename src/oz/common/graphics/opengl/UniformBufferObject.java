package oz.common.graphics.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import java.nio.ByteBuffer;

public class UniformBufferObject {
    public static int TARGET = GL31.GL_UNIFORM_BUFFER;
    public static int USAGE = GL20.GL_STATIC_DRAW;

    /**
     * index of UBO
     */
    public int index;
    private int size;
    private ByteBuffer buffer;
    private boolean needs_sending = true;

    private UniformBufferObject() {
        index = GL20.glGenBuffers();
    }

    public static UniformBufferObject create(int size) {
        UniformBufferObject ubo = new UniformBufferObject();
        ubo.size = size;
        ubo.buffer = BufferUtils.createByteBuffer(size);
        return ubo;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void use(Program program, String blockName, int uniformSlot) {
        if (needs_sending) {
            bind();
            GL20.glBufferData(TARGET, buffer, USAGE);
        }
        // bind to uniform slot
    }

    public void bind() {
        GL20.glBindBuffer(TARGET, index);
    }
}
