#version 330

in vec3 outTexCoord;
in vec3 outColor;
in vec3 mvVertexPos;
flat in int selectionIndex;

layout(location = 0) out vec4 fragColor;
layout(location = 1) out vec4 position;

struct Fog {
	bool activ;
	vec3 color;
	float density;
};

uniform sampler2DArray texture_sampler;
uniform sampler2D break_sampler;
uniform Fog fog;
uniform Fog blockFog;
uniform int selectedIndex;
uniform vec2 windowSize;
uniform vec3 ambientLight;

uniform sampler2D positionBuffer;
uniform sampler2D colorBuffer;
uniform bool drawFrontFace;

vec4 ambientC;

void setupColors(vec3 textCoord) {
	vec4 bg = texture(texture_sampler, textCoord);
	ambientC = texture(break_sampler, fract(textCoord.xy))*float(selectedIndex == selectionIndex);
	ambientC = vec4(mix(vec3(bg), vec3(ambientC), ambientC.a), bg.a);
}

vec4 calcFog(vec3 pos, vec4 color, Fog fog) {
	float distance = length(pos);
	float fogFactor = 1.0/exp((distance*fog.density)*(distance*fog.density));
	fogFactor = clamp(fogFactor, 0.0, 1.0);
	vec3 resultColor = mix(fog.color, color.xyz, fogFactor);
	return vec4(resultColor.xyz, color.w + 1 - fogFactor);
}

void main() {
	setupColors(outTexCoord);
	if(ambientC.a == 1) discard;

	fragColor = ambientC*vec4(outColor, 1);

	if(fog.activ) {
		fragColor = calcFog(mvVertexPos, fragColor, fog);

		// Underwater fog:
		if(drawFrontFace) { // There is only fog betwen front and back face of the same volume.
			Fog blockFog; // TODO: Select fog from texture or uniform.
			blockFog.activ = true;
			blockFog.color = vec3(0.1, 0.5, 0)*ambientLight;
			blockFog.density = 0.1;
			vec2 frameBufferPos = gl_FragCoord.xy/windowSize;
			vec4 oldColor = texture(colorBuffer, frameBufferPos);
			vec3 oldPosition = texture(positionBuffer, frameBufferPos).xyz;
			oldColor = calcFog(oldPosition - mvVertexPos, oldColor, blockFog);
			fragColor = vec4((1 - fragColor.a) * oldColor.xyz + fragColor.a * fragColor.xyz, 1);
		}
	}

	position = vec4(mvVertexPos, 1);
}
