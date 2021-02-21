package oz.common.scene.material;

import oz.common.graphics.opengl.Program;
import oz.common.io.ResourceLoader;
import oz.common.renderer.GLRenderer;

// see https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-core/src/main/java/com/jme3/material/Material.java
public class Material {

    enum MaterialParam {
        BASE_COLOR(4, null),
        METALLIC(1, null),
        ROUGHNESS(1, null);
        int size;
        String define;
        MaterialParam(int size, String def) {

        }
    }

    protected Program program;
    private String vsPath;
    private String fsPath;

    public Material(String vsPath, String fsPath) {
        this.vsPath = vsPath;
        this.fsPath = fsPath;
    }

    public void prepare() {
        program = ResourceLoader.DEFAULT.loadProgram(vsPath, fsPath);
    }

    public void beforeRender() {
        GLRenderer.useProgram(program);
    }

}
