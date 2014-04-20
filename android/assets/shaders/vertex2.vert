// "in" attributes from SB
attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_color;

//combined projection and view matrix from SB
uniform mat4 u_projTrans;

// "out" varyings to our fragment shader
varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    v_color = a_color;
    v_texCoords = a_texCoord0;
    
    //transform 2d space to 3d world space
    gl_Position = u_projTrans * a_position;
}


//This is just a simple "pass through" vertex shader that passes along the color and texcoord attributes