package oz.common.graphics.opengl;

public class Shader {
    enum Type {
        Vertex,
        Fragment,
        Geometry,
        TesselationControl,
        TesselationEvaluation
    }
    private String filename;
    private String source;
    private Type type;
    public Shader(String filename, String source) {
        if (filename.endsWith(".vert")) {
            type = Type.Vertex;
        } else if (filename.endsWith(".frag")) {
            type = Type.Fragment;
        } else if (filename.endsWith(".geom")) {
            type = Type.Geometry;
        } else if (filename.endsWith(".tesc")) {
            type = Type.TesselationControl;
        } else if (filename.endsWith(".tese")) {
            type = Type.TesselationEvaluation;
        } else {
            System.err.println("illegal shader type.");
            System.exit(1);
        }
    }

}
