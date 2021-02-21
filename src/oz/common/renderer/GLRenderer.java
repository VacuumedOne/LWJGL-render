package oz.common.renderer;

import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import oz.common.graphics.opengl.*;
import org.lwjgl.opengl.GL20;
import oz.common.scene.Mesh;

import java.util.List;

public class GLRenderer {
    private static VertexArrayObject bindingVAO = null;
    private static IndexBufferObject bindingIBO = null;
    private static Program usingProgram = null;

    /**
     * @param vbo upload vertex data to GPU
     */
    public static void updateVertexBuffer(VertexBufferObject vbo) {
        vbo.index = GL20.glGenBuffers();
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, vbo.index);
        GL20.glBufferData(GL20.GL_ARRAY_BUFFER, vbo.getData(), GL20.GL_STATIC_DRAW);
    }

    public static void updateIndexBuffer(IndexBufferObject ibo) {
        ibo.index = GL20.glGenBuffers();
        GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, ibo.index);
        GL20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, ibo.getData(), GL20.GL_STATIC_DRAW);
    }

    public static void updateVertexArray(VertexArrayObject vao, List<VertexBufferObject> vboList) {
        vao.index = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao.index);
        for (VertexBufferObject vbo : vboList) {
            updateVertexBuffer(vbo);
            setVertexAttrib(vbo);
        }
    }

    /**
     * Assuming Vertex Array Object is binding, set vertex attribute.
     *
     * @param vbo
     */
    public static void setVertexAttrib(VertexBufferObject vbo) {
        int location = vbo.attribute.location;
        int size = vbo.attribute.vartype.getSize();
        int format = vbo.format.getFrag();
        GL20.glVertexAttribPointer(location, size, format, false, 0, 0);
        GL20.glEnableVertexAttribArray(location);
    }

    public static int getUniformLocation(Program program, String uniformName) {
        return GL20.glGetUniformLocation(program.getIndex(), uniformName);
    }

    public static void setUniform(Uniform uniform) {
        if (usingProgram == null) {
            System.err.println("no program");
            System.exit(1);
        }
        int location = getUniformLocation(usingProgram, uniform.getUniformName());

        switch (uniform.getVarType()) {
            case Float:
                GL20.glUniform1f(location, (float) uniform.getValue());
                break;
            case Int:
                GL20.glUniform1i(location, (int) uniform.getValue());
                break;
            case Vector3:
                Vector3f vec3 = (Vector3f) uniform.getValue();
                GL20.glUniform3f(location, vec3.x, vec3.y, vec3.z);
                break;
            case Vector4:
                Vector4f vec4 = (Vector4f) uniform.getValue();
                GL30.glUniform4f(location, vec4.x, vec4.y, vec4.z, vec4.w);
                break;
            case Matrix3:
                Matrix3f mat3 = (Matrix3f) uniform.getValue();
                GL30.glUniformMatrix3fv(location, false, mat3.toFloatBuffer());
                break;
            case Matrix4:
                Matrix4f mat4 = (Matrix4f) uniform.getValue();
                GL30.glUniformMatrix4fv(location, false, mat4.toFloatBuffer());
                break;
            default:
                System.err.println("not implemented uniform type.");
        }
    }

    public static void useProgram(Program program) {
        GL20.glUseProgram(program.getIndex());
        usingProgram = program;
    }

    public static void drawElements(Mesh mesh) {
        GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBO().index);
        GL30.glBindVertexArray(mesh.getVAO().index);
        GL20.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
    }

    public static void checkError() {
        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR) {
            if (error == GL11.GL_INVALID_ENUM) {
                System.err.println("GL error: invalid enum");
            } else if (error == GL11.GL_INVALID_VALUE) {
                System.err.println("GL error: invalid value");
            } else if (error == GL11.GL_INVALID_OPERATION) {
                System.err.println("GL error: invalid operation");
            } else if (error == GL11.GL_OUT_OF_MEMORY) {
                System.err.println("GL error: out of memory");
            } else if (error == GL11.GL_STACK_UNDERFLOW) {
                System.err.println("GL error: stack underflow");
            } else if (error == GL11.GL_STACK_OVERFLOW) {
                System.err.println("GL error: stack overflow");
            } else if (error == GL30.GL_INVALID_FRAMEBUFFER_OPERATION) {
                System.err.println("GL error: invalid framebuffer operation");
            } else {
                System.err.println("GL error: illegal error");
            }
            System.exit(1);
        }
    }


}
