package oz.common.scene;

import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import oz.common.scene.components.RenderComponent;

import java.util.ArrayList;
import java.util.List;

public class Node {

    String uuid;

    List<NodeComponent> components = new ArrayList<>();

    List<Node> children = new ArrayList<>();

    Node parent = null;

    private Transform localTransform = new Transform();

    private Transform globalTransform = new Transform();

    public Node() {
    }

    /**
     * @param component 付与するコンポーネント
     * @param <T> OZOComponentを継承したインスタンス
     */
    public <T extends NodeComponent> void addComponent(T component) {
        components.add(component);
    }

    public List<NodeComponent> getComponents() {
        return components;
    }

    public void addChild(Node child) {
        children.add(child);
        child.parent = this;
        child.updateGlobalTransform();
    }

    public List<Node> getChildren() {
        return children;
    }
    public void setTransform(Transform transform) {
        this.localTransform = transform;
        updateGlobalTransform();
    }

    /**
     * ローカルのモデル変換が変更された場合とノードの親子関係が変化した際に呼び出す
     */
    public void updateGlobalTransform() {
        if (parent == null) {
            this.globalTransform = getLocalTransform();
        } else {
            Matrix4f parentPose = parent.getGlobalTransform().toTransformMatrix();
            Matrix4f localPose = getLocalTransform().toTransformMatrix();
            Matrix4f globalPos = parentPose.mult(localPose);
            this.globalTransform.fromTransformMatrix(globalPos);
        }
    }

    public Transform getLocalTransform() {
        return this.localTransform;
    }

    public Transform getGlobalTransform() {
        return this.globalTransform;
    }

    public void startupRecursively() {
        for (NodeComponent component : components) {
            component.startup();
        }
        for (Node node : children) {
            node.startupRecursively();
        }
    }

    public void updateRecursively() {

    }

    public void lateUpdateRecursively() {

    }

    public final void renderRecursively() {
        for (NodeComponent component : components) {
            if (component instanceof RenderComponent) {
                component.render();
            }
        }
        for (Node child : children) {
            child.renderRecursively();
        }
    }

    public void lateRenderRecursively() {

    }

    public String toString() {
        return "";
    }

//    private AABB getAABB() {
//
//    }

}
