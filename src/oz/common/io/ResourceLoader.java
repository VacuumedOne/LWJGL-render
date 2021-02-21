package oz.common.io;

import oz.common.graphics.opengl.Program;
import oz.common.graphics.opengl.Shader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {
    public static ResourceLoader DEFAULT;
    static {
        DEFAULT = new ResourceLoader();
        String projectRoot = DefaultFolderLocationProvider.getProjectRoot();
        String resource = Paths.get(projectRoot,"common/resource").toString();
        DEFAULT.imageFolder = Paths.get(resource,"image").toString();
        DEFAULT.shaderFolder = Paths.get(resource, "shader").toString();
    }

    public String imageFolder = "";
    public String shaderFolder = "";
    private final Map<String, Program> shaderCache = new HashMap<>();

    private ResourceLoader() {
    }

    /**
     * @param vertPath Relative path or absolute path of vertex shader
     * @param fragPath Relative path or absolute path of fragment shader
     * @return 生成された{@link oz.common.graphics.opengl.Program}オブジェクト
     */
    public Program loadProgram(String vertPath, String fragPath) {
        String absVertPath;
        if (Paths.get(vertPath).isAbsolute()) {
            absVertPath = vertPath;
        } else {
            absVertPath = Paths.get(shaderFolder, vertPath).toString();
        }
        String absFragPath;
        if (Paths.get(fragPath).isAbsolute()) {
            absFragPath = fragPath;
        } else {
            absFragPath = Paths.get(shaderFolder, fragPath).toString();
        }

        String key = absVertPath + ":" + absFragPath;
        // check if cache exists
        if (shaderCache.containsKey(key)) {
            return shaderCache.get(key);
        }

        String vpSource = loadText(absVertPath);
        String fpSource = loadText(absFragPath);
        Program program = new Program(vpSource, fpSource);

        // add cache
        shaderCache.put(key, program);

        return program;
    }

    public Shader loadShader(String shaderPath) {
        String filename = Paths.get(shaderPath).getFileName().toString();
        String source = loadText(shaderPath);
        return new Shader(filename, source);
    }

    /**
     * テキストファイルの読み込み
     * @param path Relative path or absolute path of text file
     * @return 読み込んだテキストファイル内容の文字列
     */
    public String loadText(String path) {
        Path absPath = Paths.get(path).toAbsolutePath();
        try {
            return Files.readString(absPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Fail to load %s" + absPath.toString());
            System.exit(1);
        }
        return "";
    }


    public String toString() {
         return "Resource Path\n"
                 + "\tImage: %s\n" + imageFolder
                 + "\tShader: %s\n" + shaderFolder;
    }
}
