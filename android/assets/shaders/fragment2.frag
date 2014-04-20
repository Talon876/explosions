#ifdef GL_ES
    precision mediump float;
#endif

//SB will using texture unit 0
uniform sampler2D u_texture;

//"in" varyings from vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    //sample the texture
    vec4 texColor = texture2D(u_texture, v_texCoords.st);
    
    //invert rgb
    texColor.rgb = 1.0 - texColor.rgb;
    
    //final color
    gl_FragColor = v_color * texColor;
}