#version 330 core

layout(location = 0) in vec3 position;

uniform float dx;

void main() {
    gl_Position = vec4(position + vec3(dx, 0.0, 0.0), 1.0);
}
