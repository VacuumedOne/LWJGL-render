package oz.common.graphics.opengl;


import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

/**
 * OpenGLのVertex Buffer Object(VBO)の作成
 */
public class VertexBufferObject {

    /**
     * attributeとしてのlocation, 型, shaderに付与するdefineの対応
     */
    public enum Attribute {
        POSITION(0, VarType.Vector3, null),
        NORMAL(1, VarType.Vector3, "#define HAS_NORMALS 1");

        public int location;
        public VarType vartype;
        public String define;
        Attribute(int location, VarType varType, String def) {
            this.location = location;
            this.vartype = varType;
            this.define = def;
        }
    }

    public enum Format {
        Float(4, GL20.GL_FLOAT);
//        Double(8),
//        Short(2);
        private int componentSize = 0;
        private int glFrag;

        Format(int componentSize, int glFrag) {
            this.componentSize = componentSize;
            this.glFrag = glFrag;
        }

        /**
         * Returns the size in bytes of this data type.
         *
         * @return Size in bytes of this data type.
         */
        public int getComponentSize() {
            return componentSize;
        }
        public int getFrag() {
            return glFrag;
        }
    }

     static int TARGET = GL20.GL_ARRAY_BUFFER;
     static int USAGE = GL20.GL_STATIC_DRAW;
     public int index;
     public Attribute attribute;
     int vertexCount;
     int stride = 0;
     public Format format;
     private FloatBuffer data;

     public VertexBufferObject(Attribute attribute, FloatBuffer data) {
         this.attribute = attribute;
         this.data = data;
         this.format = Format.Float;
         this.vertexCount = data.remaining() / Format.Float.componentSize;
     }

     public FloatBuffer getData() {
         return data;
     }
}


