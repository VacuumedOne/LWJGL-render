package oz.common.graphics.opengl;

import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.Map;

// TODO: Add Geometry Shader support
// TODO: Add Tesselation Shader support
public class Program {
    private int index;
    int vertexShader;
    int fragmentShader;
    private Map<String, Integer> uniforms = new HashMap<>();

    public Program(String vpSource, String fpSource) {

        index = GL20.glCreateProgram();
        vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        // compile
        GL20.glShaderSource(vertexShader, vpSource);
        GL20.glCompileShader(vertexShader);
        checkCompileStatus(vertexShader);
        GL20.glShaderSource(fragmentShader, fpSource);
        GL20.glCompileShader(fragmentShader);
        checkCompileStatus(fragmentShader);

        // attach
        GL20.glAttachShader(index, vertexShader);
        GL20.glAttachShader(index, fragmentShader);

        // link
        GL20.glLinkProgram(index);
        checkLinkStatus();
    }

    public int getIndex() {
        return index;
    }

    public void checkCompileStatus(int shaderID) {
        int status = GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS);
        if (status == GL20.GL_FALSE) {
            System.err.println("shader compile error");
            String msg = GL20.glGetShaderInfoLog(shaderID);
            System.err.println(msg);
            System.exit(1);
        }
    }

    public void checkLinkStatus() {
        int status = GL20.glGetProgrami(index, GL20.GL_LINK_STATUS);
        if (status == GL20.GL_FALSE) {
            System.err.println("program link error");
            String msg = GL20.glGetProgramInfoLog(index);
            System.err.println(msg);
            System.exit(1);
        }
    }

}
