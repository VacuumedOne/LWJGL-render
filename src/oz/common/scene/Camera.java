package oz.common.scene;

public class Camera extends Node {
    /**
     * Field of View
     */
    private float fovY;

    /**
     * ratio screenHeight / screenWidth
     */
    private float aspect;

    private float nearClip;

    private float farClip;

    public Camera(float fovY, float aspect, float near, float far) {

    }
    public Camera() {

    }
}
