package oz.common.io;

import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import org.lwjgl.BufferUtils;
import oz.common.graphics.opengl.IndexBufferObject;
import oz.common.graphics.opengl.VertexBufferObject;
import oz.common.scene.Mesh;
import oz.common.scene.components.RenderComponent;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Map;

/**
 * convert jme3 scene object(Spatial, Node, Geometry, etc) to
 */
public class Jme3SceneConverter {

    /**
     * vertexのattributeの対応
     */
    private static Map<VertexBuffer.Type, VertexBufferObject.Attribute> types = Map.ofEntries(
            Map.entry(VertexBuffer.Type.Position, VertexBufferObject.Attribute.POSITION),
            Map.entry(VertexBuffer.Type.Normal, VertexBufferObject.Attribute.NORMAL)
    );
    public Jme3SceneConverter() {
    }

    public oz.common.scene.Node convertSpatial(Spatial spatial) {
        oz.common.scene.Node node;
        Transform transform = spatial.getLocalTransform();
        if (spatial instanceof com.jme3.scene.Node) {
            node = convertNode((com.jme3.scene.Node) spatial);
            for (Spatial childSpatial : ((com.jme3.scene.Node)spatial).getChildren()) {
                oz.common.scene.Node childNode = convertSpatial(childSpatial);
                node.addChild(childNode);
            }
        } else if (spatial instanceof Geometry) {
            node = convertGeometry((Geometry) spatial);
        } else {
            node = new oz.common.scene.Node();
            System.err.println("something wrong");
        }
        node.setTransform(transform);

        return node;
    }

    public oz.common.scene.Node convertNode(com.jme3.scene.Node _node) {
        oz.common.scene.Node node = new oz.common.scene.Node();
        return node;
    }

    public oz.common.scene.Node convertGeometry(com.jme3.scene.Geometry geometry) {
        oz.common.scene.Node node = new oz.common.scene.Node();
        com.jme3.material.Material _material = geometry.getMaterial();
        System.out.println(_material.getName() + ": (material params)");
        for (String key : _material.getParamsMap().keySet()) {
            System.out.println(key);
        }
        System.out.println();

        oz.common.scene.Mesh mesh = new oz.common.scene.Mesh();
        oz.common.scene.material.Material material = new oz.common.scene.material.Material("gltf/primitive.vert", "gltf/pbr.frag");
        node.addComponent(new RenderComponent(mesh, material));

        com.jme3.scene.Mesh _mesh =  geometry.getMesh();
        System.out.println("vertex count: " + _mesh.getVertexCount());
        System.out.println("triangle count: " + _mesh.getTriangleCount());

        // Vertex - Position
//        VertexBuffer __position = _mesh.getBuffer(VertexBuffer.Type.Position);
//        System.out.println(__position.getBufferType());
//        System.out.println(__position.getFormat());
//        System.out.println(__position.getData());
//        FloatBuffer _position = _mesh.getFloatBuffer(VertexBuffer.Type.Position);
//        _position.rewind();
//        System.out.println("position " + _position);
//        FloatBuffer position = BufferUtils.createFloatBuffer(_position.capacity());
//        position.put(_position);
//        position.flip();
//        mesh.set(new VertexBufferObject(VertexBufferObject.Attribute.POSITION, position));

        // Vertex - TexCoord0
//        FloatBuffer _texcoord = _mesh.getFloatBuffer(VertexBuffer.Type.TexCoord);
//        if (_texcoord != null) {
//            _position.rewind();
//            System.out.println(_texcoord);
//            FloatBuffer texcoord = BufferUtils.createFloatBuffer(_texcoord.capacity());
//            texcoord.put(_texcoord);
//            texcoord.flip();
//            mesh.setTexCoord0(texcoord);
//        }

        // Vertex - Normal
//        FloatBuffer _normal = _mesh.getFloatBuffer(VertexBuffer.Type.Normal);
//        if (_normal != null) {
//            FloatBuffer normal = BufferUtils.createFloatBuffer(_normal.capacity());
//            normal.put(_normal);
//            normal.flip();
//            mesh.set(new VertexBufferObject(VertexBufferObject.Attribute.NORMAL, normal));
//        }

        // Vertex - Color
//        FloatBuffer _color = _mesh.getFloatBuffer(VertexBuffer.Type.Color);
//        if (_color != null) {
//            FloatBuffer color = BufferUtils.createFloatBuffer(_color.capacity());
//            color.put(_color);
//            color.flip();
//            mesh.setColor(color);
//        }

        // Element
//        Buffer _element = _mesh.getIndexBuffer().getBuffer();
//        if (_element instanceof IntBuffer) {
//            IntBuffer element = BufferUtils.createIntBuffer(((IntBuffer)_element).capacity());
//            element.put((IntBuffer)_element);
//            element.flip();
//            mesh.setIndex(element);
//        } else if (_element instanceof ShortBuffer) {
//            ShortBuffer tmp = (ShortBuffer)_element;
//            IntBuffer element = BufferUtils.createIntBuffer((tmp).capacity());
//            for (int i = 0; i < tmp.capacity(); i++) {
//                element.put(tmp.get());
//            }
//            element.flip();
//            mesh.setIndex(element);
//        } else {
//            System.out.println("not index buffer supported");
//        }

//        params = _material.getParam("BaseColor");
//        if (params != null) {
//            material.setBaseColor(((ColorRGBA)params.getValue()).toVector4f());
//        }
//
//        params = _material.getParam("Metallic");
//        if (params != null) {
//            material.setMetallic((float)params.getValue());
//        }
//
//        params = _material.getParam("Roughness");
//        if (params != null) {
//            material.setRoughness((float)params.getValue());
//        }

        return node;
    }

    /**
     * @param _mesh source mesh data which contains vertex buffers, an index buffer
     * @return
     */
    private oz.common.scene.Mesh parseMesh(com.jme3.scene.Mesh _mesh) {
        oz.common.scene.Mesh mesh = new Mesh();

        // attribute
        for (VertexBuffer.Type type : types.keySet()) {
            VertexBuffer vb = _mesh.getBuffer(type);
            if (vb != null) {
                VertexBufferObject vbo = null;
                VertexBufferObject.Attribute attribute = types.get(vb.getBufferType());
                if (vb.getFormat() == VertexBuffer.Format.Float) {
                    vbo = new VertexBufferObject(attribute, (FloatBuffer) vb.getData());
                } else {
                    System.err.println("not implemented.");
                    System.exit(1);
                }
                mesh.setVertex(vbo);
            }
        }

        // element
        VertexBuffer ib = _mesh.getBuffer(VertexBuffer.Type.Index);
        if (ib != null) {
            IndexBufferObject ibo = null;
            if (ib.getFormat() == VertexBuffer.Format.Int) {
                ibo = new IndexBufferObject((IntBuffer) ib.getData());
            } else if (ib.getFormat() == VertexBuffer.Format.Short) {
                ShortBuffer sbuf = (ShortBuffer) ib.getData();
                IntBuffer ibuf = BufferUtils.createIntBuffer(sbuf.limit());
                for (int i = 0; i < sbuf.limit(); i++) {
                    ibuf.put((int)sbuf.get());
                }
                ibuf.flip();
                ibo = new IndexBufferObject(ibuf);
            }
            mesh.setIndex(ibo);
        }

        // material

        return mesh;
    }

}
