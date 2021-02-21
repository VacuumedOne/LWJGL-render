package oz.common.graphics.opengl;

import com.jme3.math.*;

public class Uniform {
    private VarType varType;
    private String uniformName;
    private Object value;
    public Uniform(String name, float value) {
        varType = VarType.Float;
        set(name, value);
    }
    public Uniform(String name, Vector2f vec) {
        varType = VarType.Vector2;
        set(name, vec);
    }
    public Uniform(String name, Vector3f vec) {
        varType = VarType.Vector3;
        set(name, vec);
    }
    public Uniform(String name, Vector4f vec) {
        varType = VarType.Vector4;
        set(name, vec);
    }
    public Uniform(String name, int value) {
        varType = VarType.Int;
        set(name, value);
    }
    public Uniform(String name, Matrix3f mat) {
        varType = VarType.Matrix3;
        set(name, mat);
    }
    public Uniform(String name, Matrix4f mat) {
        varType = VarType.Matrix4;
        set(name, mat);
    }
    private void set(String name, Object value) {
        this.uniformName = name;
        this.value = value;
    }
    public VarType getVarType() {
        return varType;
    }
    public Object getValue() {
        return value;
    }
    public String getUniformName() {
        return uniformName;
    }
}
