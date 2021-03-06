#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    //v_color = vec4(1,1,1,1);
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
}