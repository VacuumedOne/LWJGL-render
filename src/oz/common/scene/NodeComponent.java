package oz.common.scene;

public class NodeComponent {
    private Node ozo = null;

    public NodeComponent() {
    }

    public Node getNode() {
        return ozo;
    }

    public void startup() {}

    public void update() {}

    public void lateUpdate() {}

    public void render() {}

    public void lateRender() {}

    public String toString() {
        return "component";
    }

}
