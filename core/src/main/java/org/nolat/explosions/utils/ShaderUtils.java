package org.nolat.explosions.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderUtils {
    private static final String SIMPLE_PASSTHROUGH_VERT = "attribute vec4 a_position;\r\n"
            + "attribute vec2 a_texCoord0;\r\n" + "attribute vec4 a_color;\r\n" + "uniform mat4 u_projTrans;\r\n"
            + "varying vec4 v_color;\r\n" + "varying vec2 v_texCoords;\r\n" + "void main() {\r\n"
            + "    v_color = a_color;\r\n" + "    v_texCoords = a_texCoord0;\r\n"
            + "    gl_Position = u_projTrans * a_position;\r\n" + "}";
    private static ShaderProgram wobbly;

    public static void init() {
        wobbly = new ShaderProgram(SIMPLE_PASSTHROUGH_VERT, Gdx.files.internal("shaders/wobbly.frag").readString());

        if (wobbly.isCompiled()) {
            Gdx.app.log("ShaderUtils", "Wobbly shader compiled successfully!");
        } else {
            Gdx.app.log("Shader", wobbly.getLog());
        }
    }

    public static void startWobbly(SpriteBatch batch, float time, float wobble) {
        if (wobbly != null) {
            batch.setShader(wobbly);
            wobbly.setUniformf("time", time);
            wobbly.setUniformf("wobble", wobble);
        } else {
            Gdx.app.log("ShaderUtils", "Shader is null, did you call init()?");
        }
    }

    public static void end(SpriteBatch batch) {
        batch.setShader(null);
    }

}
