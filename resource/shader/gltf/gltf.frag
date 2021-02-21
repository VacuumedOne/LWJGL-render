#version 330 core
#define PI 3.141592
layout(location=0)out vec4 colorResult;
layout(location=1)out vec4 depthResult;

in vec3 vsPosition;
in vec3 vsNormal;
in vec3 wsNormal;
in vec3 vs_lDir;
in mat4 shadowMat;
const vec3 lColor = vec3(1,1,1);

in float distance;
in vec2 Texcoord0;

in vec3 wsPosition;
in vec3 wsCameraPosition;
in float psDepth;

layout (std140) uniform GltfCommonMaterial {
    vec3 emissiveFactor;
// Unused 0
    vec3 textureFlags;
} commonMaterial;

layout (std140) uniform GltfPBRMetallicRoughness {
    vec4 baseColorFactor;
    vec2 metallicRoughnessFactor;
    vec4 textureFlags;
} mrMaterial;

// Textures
uniform sampler2D normalTexture;
uniform sampler2D occlusionTexture;
uniform sampler2D emissiveTexture;
uniform sampler2D baseColorTexture;
uniform sampler2D metallicRoughnessTexture;

uniform samplerCube envmapTexture;
uniform sampler2D brdfLUT;
uniform float envmapLod;


// light maps

uniform sampler2D shadowmap;

/*
* Struct types
*/
struct PerFragmentLightingArgument{
    vec3 normal;
    vec3 wNormal;
    vec3 view;
    vec3 reflect;
    vec3 wReflect;
    vec3 diffuseBase;
    vec3 specularBase;
    float reflectance;
    float reflectance90;
    float metallic;
    float roughness;
    float alpha;
    float nDotV;
};

struct PerLightLightingArgument{
    vec3 light;
    vec3 halfVector;
    float nDotL;
    float nDotH;
    float lDotH;
    float vDotH;
};


// Caluclate tangent vector without pre computed attribute values
// This can be performed with differential operation using dFdx,dFdy
// http://www.thetenthplanet.de/archives/1180
mat3 cotangent_frame(vec3 N, vec3 p, vec2 uv)
{
    // get edge vectors of the pixel triangle
    vec3 dp1 = dFdx( p );
    vec3 dp2 = dFdy( p );
    vec2 duv1 = dFdx( uv );
    vec2 duv2 = dFdy( uv );

    // solve the linear system
    vec3 dp2perp = cross( dp2, N );
    vec3 dp1perp = cross( N, dp1 );
    vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
    vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;

    // construct a scale-invariant frame
    float invmax = inversesqrt( max( dot(T,T), dot(B,B) ) );
    return mat3( T * invmax, B * invmax, N );
}

// Convert sRGB space color vector to linear color space
// Alpha will be passed through
vec4 srgb2linear(vec4 srgb){
    return vec4(pow(srgb.rgb,vec3(2.2)),srgb.a);
}

vec3 fresnel(PerFragmentLightingArgument frag,PerLightLightingArgument light){
    return vec3(frag.reflectance + (frag.reflectance90 - frag.reflectance) * pow(clamp(1.0 - light.vDotH, 0.0, 1.0), 5.0));
}

float geometric(PerFragmentLightingArgument frag,PerLightLightingArgument light){
    float NdotL = light.nDotL;
    float NdotV = frag.nDotV;
    float r = frag.alpha;

    float attenuationL = 2.0 * NdotL / (NdotL + sqrt(r * r + (1.0 - r * r) * (NdotL * NdotL)));
    float attenuationV = 2.0 * NdotV / (NdotV + sqrt(r * r + (1.0 - r * r) * (NdotV * NdotV)));
    return attenuationL * attenuationV;
}

float distribution(PerFragmentLightingArgument frag,PerLightLightingArgument light){
    float roughnessSq = frag.alpha * frag.alpha;
    float f = (light.nDotH * roughnessSq - light.nDotH) * light.nDotH + 1.0;
    return roughnessSq / (PI * f * f);
}

vec3 lightingPerLight(PerFragmentLightingArgument frag,PerLightLightingArgument light){
    vec3 F = fresnel(frag,light);
    float G = geometric(frag,light);
    float D = distribution(frag,light);

    vec3 diffuse = (1.0 - F) * frag.diffuseBase / PI;
    vec3 specular = F*G*D/(4.0 * light.nDotL * frag.nDotV);
    return diffuse + specular;
}

vec3 lighting(PerFragmentLightingArgument fragmentArg){
    vec3 result = vec3(0.);
    // TODO: Support multiple lights by for loop here
    // Compute lighting parameter depends lights
    vec3 lightDir = normalize(vs_lDir);
    vec3 halfVector = normalize(lightDir + fragmentArg.view);
    float nDotL = clamp(dot(fragmentArg.normal,lightDir),0.001,1.0);
    float nDotH = clamp(dot(fragmentArg.normal,halfVector),0.,1.);
    float lDotH = clamp(dot(lightDir,halfVector),0.,1.);
    float vDotH = clamp(dot(fragmentArg.view,halfVector),0.,1.);
    PerLightLightingArgument lightArg = PerLightLightingArgument(lightDir,halfVector,nDotL,nDotH,lDotH,vDotH);
    result += nDotL * lColor *lightingPerLight(fragmentArg,lightArg);
    return result;
}

vec3 ibl(PerFragmentLightingArgument fragmentArg){
    vec3 diffuse = fragmentArg.diffuseBase * srgb2linear(texture(envmapTexture,fragmentArg.wNormal)).rgb;
    vec3 brdf = srgb2linear(texture(brdfLUT,vec2(fragmentArg.nDotV,1.0 - fragmentArg.roughness))).rgb;
    vec3 specularL = textureLod(envmapTexture,fragmentArg.wReflect,envmapLod * fragmentArg.roughness).rgb;
    vec3 specular = specularL * (fragmentArg.specularBase * brdf.x + brdf.y);
    return diffuse + specular;
}

vec3 encodeToVec3(float value,float minVal,float maxVal )
{
    value        = clamp( (value-minVal) / (maxVal-minVal), 0.0, 1.0 );
    value       *= (256.0*256.0*256.0 - 1.0) / (256.0*256.0*256.0);
    vec4 encode  = fract( value * vec4(1.0, 256.0, 256.0*256.0, 256.0*256.0*256.0) );
    return encode.xyz - encode.yzw / 256.0 + 1.0/512.0;
}


void main(){
    // Shadow map
    depthResult = vec4(encodeToVec3(psDepth, -1.01, 1.01), 1.0);

    bool isShadow = false;
    vec4 sUVa = shadowMat * vec4(wsPosition,1.0);
    vec3 sUV = sUVa.xyz/sUVa.w;
    vec4 smc = texture(shadowmap,sUV.xy);
    float bias = 0;
    //    colorResult = vec4(vec3(sUV.z/2.0 + 0.5,smc.r,0),1.0);
    //    if(sUV.z/2.0 + 0.5 > smc.r + bias && smc.a > .9){
    //        isShadow = true;
    //    }

    //    if(sUV.x > 1.0 || sUV.x < 0.0 || sUV.y > 1.0 || sUV.y < 0.0){
    //        colorResult = vec4(0,0,1,1);
    //        return;
    //    }else{
    //        vec4 smc = texture(shadowmap,sUV.xy);
    //        colorResult = smc;
    //        if(smc.a < 0.5){
    //            colorResult = vec4(1,0,0,1);
    //        }
    //        if(colorResult.r*2.-1. > -sUV.z - 0.01){
    //            colorResult = vec4(0,0,0,1);
    //            return;
    //        }
    //        return;
    //    }
    //colorResult = vec4(wsPosition,1);
    // Reading arguments per fragments for PBRMetallicRoughness
    vec4 baseColor = mrMaterial.baseColorFactor;
    if(mrMaterial.textureFlags.r == 1.0){
        vec4 baseColorTex = texture(baseColorTexture,Texcoord0);
        baseColor*=srgb2linear(baseColorTex);
    }
    float metallic = mrMaterial.metallicRoughnessFactor.r;
    float roughness = mrMaterial.metallicRoughnessFactor.g;
    if(mrMaterial.textureFlags.g == 1.0){
        vec4 metallicRoughnesstex = texture(metallicRoughnessTexture,Texcoord0);
        metallic *= metallicRoughnesstex.b;
        roughness *= metallicRoughnesstex.g;
    }
    // Clamp roughness as a perceptual value
    roughness = clamp(roughness,0.04,1.0);
    metallic = clamp(metallic,0.0,1.0);
    float alpha = roughness * roughness;
    vec3 f0 = vec3(0.04);
    vec3 diffuseBase = baseColor.rgb * (vec3(1.0) - f0);
    diffuseBase *= 1.0 - metallic;
    vec3 specularBase = mix(f0,baseColor.rgb,metallic);
    float reflectance = max(max(specularBase.r,specularBase.g),specularBase.b);
    float reflectance90 = clamp(reflectance * 25.0 ,0.0 ,1.0);

    // Compute arguments that is independent of lights
    vec3 normal = normalize(vsNormal);
    // Normal mapping
    if(commonMaterial.textureFlags.r == 1.0){
        vec3 tsNormal = texture(normalTexture,Texcoord0).rgb;
        tsNormal = normalize(tsNormal * 2.0 - 1.0); // [0,1] to [-1,1]
        mat3 perFragmentCotFrame = cotangent_frame(normal,vsPosition,Texcoord0);
        normal = normalize(perFragmentCotFrame * tsNormal);
    }
    // View vector is same as the negated vsPosition because all of the vectors are in view space
    // , camera position in camera space will be zero vector naturally.
    vec3 v = normalize(-vsPosition);
    vec3 r = -normalize(reflect(v,normal));
    vec3 wr = -normalize(reflect(normalize(wsCameraPosition - wsPosition),wsNormal));
    float nDotV = clamp(abs(dot(normal,v)),0.001,1.0);

    // Construct lighting param
    PerFragmentLightingArgument pFrag = PerFragmentLightingArgument(normal,wsNormal,v,r,wr,diffuseBase,specularBase,reflectance,reflectance90,metallic,roughness,alpha,nDotV);

    // Lighting
    vec3 result = ibl(pFrag);
    if(!isShadow){
        result += lighting(pFrag);
    }

    // Ambient Occlusion
    if(commonMaterial.textureFlags.g == 1.0){
        float occlusion = texture(occlusionTexture,Texcoord0).r;
        result *= occlusion;
    }

    // Emissive Mapping
    if(commonMaterial.textureFlags.b == 1.0){
        vec3 emissiveTex = srgb2linear(texture(emissiveTexture,Texcoord0)).rgb;
        result += emissiveTex * commonMaterial.emissiveFactor;
    }
    colorResult = vec4(pow(result,vec3(1.0/2.2)),baseColor.a);

    // 白黒で表示
    // depthResult = vec4(vec3((psDepth+1.0)/2.0),1);

    // 色の遷移で表示
    // depthResult = vec4(encodeToVec3(psDepth, -1.01, 1.01), 1.0);
    // depthResult = encodeToVec3(psDepth, -1.0, 1.0);
    // depthResult = vec4(vec3(psDepth), 1.0);

    // デバッグ用・定数で決め打ち
    // depthResult = vec4(vec3(0.02), 1.);

    // デバッグ要・定数より上か下か
    // if (psDepth > 0.) {
    //     depthResult = vec4(vec3(1.0, 0.0, 0.0), 1.0);
    // } else {
    //     depthResult = vec4(vec3(0.0, 0.0, 1.0), 1.0);
    // }

}