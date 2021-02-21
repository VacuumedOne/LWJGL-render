package oz.common.graphics.opengl;

import java.nio.IntBuffer;

public class IndexBufferObject {
    public int index;
    private IntBuffer data;
    private int indexCount;

    public IndexBufferObject(IntBuffer data) {
        this.data = data;
        this.indexCount = data.remaining();
    }

    public IntBuffer getData() {
        return data;
    }

    public int getIndexCount() {
        return indexCount;
    }
}
