package oz.common.scene.components;


import oz.common.graphics.opengl.Uniform;
import oz.common.renderer.GLRenderer;
import oz.common.scene.Mesh;
import oz.common.scene.NodeComponent;
import oz.common.scene.material.Material;

public class RenderComponent extends NodeComponent {
    private Mesh mesh;
    private Material material;

    public RenderComponent(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }

    @Override
    public void startup() {
        mesh.prepare();
        material.prepare();
        GLRenderer.checkError();
    }

    @Override
    public void render() {
        material.beforeRender();
        GLRenderer.setUniform(new Uniform("dx", 0.5f));
        GLRenderer.drawElements(mesh);
        GLRenderer.checkError();
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Material getMaterial() {
        return material;
    }
}
