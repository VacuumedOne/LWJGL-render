package oz.common.scene;

import org.lwjgl.opengl.GL11;
import oz.common.graphics.opengl.IndexBufferObject;
import oz.common.graphics.opengl.VertexArrayObject;
import oz.common.graphics.opengl.VertexBufferObject;
import oz.common.renderer.GLRenderer;

import java.util.ArrayList;
import java.util.List;

// TODO: GL_POINT, GL_LINE への対応
public class Mesh {

    private int topology = GL11.GL_TRIANGLES;

    private String name = "no name";
    private int vertexCount = -1;
    private int primitiveCount = -1;
    private int indexCount = -1;
    List<VertexBufferObject> vboList = new ArrayList<>();
    private IndexBufferObject ibo;
    private VertexArrayObject vao;

    public Mesh() {
    }

    /**
     * invoke OpenGL functions to prepare mesh data
     * before calling prepare(), set mesh data by {@link Mesh#setVertex(VertexBufferObject)} and {@link Mesh#setIndex(IndexBufferObject)}
     */
    public void prepare() {
        vao = new VertexArrayObject();
        GLRenderer.updateVertexArray(vao, vboList);
        GLRenderer.updateIndexBuffer(ibo);
    }

    public int getPrimitiveVertexCount() {
        switch (topology) {
            case GL11.GL_TRIANGLES:
                return 3;
            case GL11.GL_LINE:
                return 2;
            case GL11.GL_POINT:
                return 1;
        }
        return 0;
    }

    public void setVertex(VertexBufferObject vbo) {
        vboList.add(vbo);
    }

    public void setIndex(IndexBufferObject ibo) {
        this.ibo = ibo;
    }

    // TODO: 残りのAttributeの対応

    private void updateVertexCount(int vertCnt) {
        if (vertexCount == -1) {
            vertexCount = vertCnt / 3;
        } else if (vertCnt / 3 != vertexCount) {
            // 他のAttributeと頂点の数が一致しない
            System.err.println("vertex size consistency was lost.");
        }
    }

    private void updateIndexCount(int indexCnt) {
        if (indexCount == -1) {
            indexCount = indexCnt;
        }
        if (primitiveCount == -1) {
            primitiveCount = indexCnt / getPrimitiveVertexCount();
        }
        System.out.println("index: " + indexCnt);
        System.out.println("primitive: " + primitiveCount);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public IndexBufferObject getIBO() {
        return ibo;
    }

    public VertexArrayObject getVAO() {
        return vao;
    }

    public int getIndexCount() {
        return ibo.getIndexCount();
    }

//    public void drawElements() {
//        for (VertexBufferObject vbo : vboList) {
//            vao.bind();
//            vbo.enable();
//        }
//        vboList.bind();
//        GL20.glDrawElements(topology, indexCount, GL20.GL_UNSIGNED_INT, 0);
//    }
}
