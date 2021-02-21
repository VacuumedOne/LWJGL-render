#version 330 core
layout(location=0) out vec4 color;

in vec3 vNormal;
in vec2 uv;
uniform samplerCube skybox;


void main(){
    if(isnan(vNormal.x) || isnan(vNormal.y) || isnan(vNormal.z)){
        color = vec4(1,0,0,1);
    }else {
        color = texture(skybox, vNormal);
    }
}