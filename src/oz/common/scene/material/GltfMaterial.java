package oz.common.scene.material;

import com.jme3.math.Vector4f;

public class GltfMaterial extends Material {
    // Uniform
    private float metallic;
    private float roughness;
    private Vector4f baseColor;

    public GltfMaterial() {
        super("gltf/primitive.vert", "gltf/pbr.frag");
    }

    public void setMetallic(float metallic) {
        this.metallic = metallic;
    }

    public void setRoughness(float roughness) {
        this.roughness = roughness;
    }

    public void setBaseColor(Vector4f baseColor) {
        this.baseColor = baseColor;
    }
}
