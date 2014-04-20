#ifdef GL_ES
    precision mediump float;
#endif

//SB will using texture unit 0
uniform sampler2D u_texture;

//"in" varyings from vertex shader from SB
varying vec4 v_color;
varying vec2 v_texCoords;

//set these with texture.bind(number)
uniform sampler2D u_texture1;
uniform sampler2D u_mask;


void main() {
    //sample the texture
    vec4 texColor0 = texture2D(u_texture, v_texCoords);
    
    //sample from second texture
    vec4 texColor1 = texture2D(u_texture1, v_texCoords);
    
    //get mask by sampling alpha
    float mask = texture2D(u_mask, v_texCoords).a;
    
    //final color
    gl_FragColor = v_color * mix(texColor0, texColor1, mask);
}