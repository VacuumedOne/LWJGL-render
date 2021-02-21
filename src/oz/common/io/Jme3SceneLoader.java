package oz.common.io;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.plugins.J3MLoader;
import com.jme3.scene.plugins.MTLLoader;
import com.jme3.scene.plugins.OBJLoader;
import com.jme3.scene.plugins.gltf.BinLoader;
import com.jme3.scene.plugins.gltf.GltfLoader;
import com.jme3.system.JmeSystem;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.texture.plugins.TGALoader;
import oz.common.scene.Node;

/**
 * JMonkeyEngine3のシーンツリーとしてロードする機構。
 * objファイル, gltfファイル等のロードを行う役割を担う。
 */
public class Jme3SceneLoader {
    private AssetManager am;
    private Jme3SceneConverter converter = new Jme3SceneConverter();

    public Jme3SceneLoader() {
        am = JmeSystem.newAssetManager();

        // add resource folder
        am.registerLocator("common/resource/model", FileLocator.class);
        // add folder including .j3m,.j3md definition files
        am.registerLocator("/", ClasspathLocator.class);
        am.registerLoader(OBJLoader.class, "obj");
        am.registerLoader(MTLLoader.class,"mtl");
        am.registerLoader(J3MLoader.class, "j3m");
        am.registerLoader(J3MLoader.class, "j3md");
        am.registerLoader(TGALoader.class, "tga");
        am.registerLoader(GltfLoader.class, "gltf");
        am.registerLoader(BinLoader.class, "bin");
        am.registerLoader(AWTLoader.class, "jpg", "png", "jpeg", "bmp", "gif");
    }
    public Node loadGltf(String path) {
        return converter.convertSpatial(am.loadModel(path));
    }
}
