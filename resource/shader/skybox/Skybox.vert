#version 330 core
#define EPS 0.0001

layout(location=0) in vec3 position;

layout (std140) uniform BasicCamera {
    mat4 view;
    mat4 proj;
    vec3 cameraPosition;
} camera;

out vec3 vNormal;
out vec2 uv;
void main(){
    vec4 screenPos = vec4(position.xy,1 - EPS,1);
    vec4 vp = inverse(camera.proj*camera.view)*screenPos;
    vNormal = vp.xyz/vp.w;
    uv = position.xy;
    gl_Position = screenPos;
}