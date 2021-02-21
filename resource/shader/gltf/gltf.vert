#version 330 core

layout(location=0) in vec4 POSITION;
layout(location=1) in vec3 NORMAL;
layout(location=2) in vec2 TEXCOORD_0;

layout (std140) uniform BasicCamera {
    mat4 view;
    mat4 proj;
    vec3 cameraPosition;
} camera;

uniform mat4 pose;
uniform mat4 shadowmapVP;
uniform mat4 shadowmapC;

const vec3 lDir = vec3(0,1,1);

out vec3 vsPosition;
out vec3 wsPosition;
out vec3 wsCameraPosition;
out vec3 wsNormal;
out vec3 vsNormal;
out float psDepth;
out vec2 Texcoord0;
out float distance;
out mat4 shadowMat;

out vec3 vs_lDir;

void main(){
    vec4 pos = POSITION;
    vec4 viewSpacePos = camera.view * pose * vec4(pos.xyz,1.0);
    gl_Position =  camera.proj * viewSpacePos;
    vsNormal = (camera.view * pose * vec4(NORMAL,0)).xyz;
    wsNormal = (pose * vec4(NORMAL,0)).xyz;
    vsPosition = viewSpacePos.xyz/viewSpacePos.w;
    vec4 p = pose * vec4(pos.xyz,1.0);
    wsPosition = pos.xyz;
    wsCameraPosition = camera.cameraPosition;
    Texcoord0 = TEXCOORD_0;
    distance = gl_Position.z/gl_Position.w;
    vs_lDir= (camera.view * vec4(lDir,0.0)).xyz;
    psDepth = gl_Position.z/gl_Position.w;
    shadowMat = shadowmapC * shadowmapVP;
}