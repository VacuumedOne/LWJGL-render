package oz.common.io;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ResourceLoaderTest {
    @Test
    public void defaultLoaderTest() {
        ResourceLoader loader = ResourceLoader.DEFAULT;

        String text = loader.loadText("resource/shader/quad.frag");

        String path1 = Paths.get("").toAbsolutePath().toString();
        String path1_ = "/home/nocks_on/project/research/OZ2/common";
        String path2 = "/resource";
        String path3 = Paths.get(path2, path1).toString();
        System.out.println(path3);
        assertEquals(loader.imageFolder, "hoge");
    }
}