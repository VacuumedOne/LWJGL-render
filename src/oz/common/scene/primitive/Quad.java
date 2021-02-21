package oz.common.scene.primitive;

import org.lwjgl.BufferUtils;
import oz.common.graphics.opengl.IndexBufferObject;
import oz.common.graphics.opengl.VertexBufferObject;
import oz.common.scene.components.RenderComponent;
import oz.common.scene.material.Material;
import oz.common.scene.Mesh;
import oz.common.scene.Node;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Quad extends Node {
    public Quad() {

        float[] positionArr = {
                -0.5f, -0.5f, 0f,
                -0.5f,  0.5f, 0f,
                0.5f,  -0.5f, 0f,
                0.5f,  0.5f,  0f
        };
        int[] indexArr = {
                0,1,2,
                1,3,2
        };

        FloatBuffer position = BufferUtils.createFloatBuffer(positionArr.length);
        position.put(positionArr);
        position.flip();

        IntBuffer index = BufferUtils.createIntBuffer(indexArr.length);
        index.put(indexArr);
        index.flip();

        Mesh mesh = new Mesh();
        mesh.setVertex(new VertexBufferObject(VertexBufferObject.Attribute.POSITION, position));
        mesh.setIndex(new IndexBufferObject(index));
        Material material = new Material("quad/quad.vert", "quad/quad.frag");

        addComponent(new RenderComponent(mesh, material));
    }
}
