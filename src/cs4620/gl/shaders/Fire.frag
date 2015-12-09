#version 120

// You May Use The Following Functions As RenderMaterial Input
// vec4 getDiffuseColor(vec2 uv) // samples fire.png
// vec4 getNormalColor(vec2 uv)  // samples noise.png

uniform float time; // seconds passed since beginning of program

const vec3 texture_scales = vec3(1.0, 2.0, 3.0);
const vec3 scroll_speeds = vec3(1.0, 1.0, 1.0); // V-coord unit/sec

varying vec2 fUV;
varying vec3 fPos;

void main() {
    // TODO#PPA2 SOLUTION START
    // scale fUV by texture_scales
    vec2 t1 = fUV*texture_scales.x;
    vec2 t2 = fUV*texture_scales.y;
    vec2 t3 = fUV*texture_scales.z;
    
    t1.y -= time*scroll_speeds.x;
    t2.y -= time*scroll_speeds.y;
    t3.y -= time*scroll_speeds.z;
    t1 = abs(t1);
    t2 = abs(t2);
    t3 = abs(t3);
    while (t1.y >= 1.0) {
        t1.y -= 1;
    }
    while (t2.y >= 1.0) {
        t2.y -= 1;
    }
    while (t3.y >= 1.0) {
        t3.y -= 1;
    }
    
    while (t1.x >= 1.0) {
        t1.x -= 1;
    }
    while (t2.x >= 1.0) {
        t2.x -= 1;
    }
    while (t3.x >= 1.0) {
        t3.x -= 1;
    }
    
    vec2 UV = (getNormalColor(t1).xy + getNormalColor(t2).xy + getNormalColor(t3).xy) / 3;
    while (UV.x >= 1.0) {
        UV.x -= 1;
    }
    while (UV.y >= 1.0) {
        UV.y -= 1;
    }
    
    //Color Variance
    vec4 wicked = -(fPos.y+1) * vec4(.4,.4,.4,0);
    
    //Flicker term (overall)
    float timely = time;
    while (timely>=4.0){
        timely -=4;
    }
    float c = getNormalColor(vec2(timely/4,timely/4)).x;
    vec4 flickr = -0.1*(3-fPos.y)*vec4(c,c,c,c);// /timely;
    
    //Brightness Term
    float bright = .7;
    
    gl_FragColor = bright*(getDiffuseColor(UV) + wicked + flickr);
    // SOLUTION END
}