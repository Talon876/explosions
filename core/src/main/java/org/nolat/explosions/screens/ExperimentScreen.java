package org.nolat.explosions.screens;

import org.nolat.explosions.utils.InputAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ExperimentScreen implements Screen {

    private SpriteBatch batch;
    private ShaderProgram shader;

    Texture texture;
    Texture bg;

    Texture grass;
    Texture dirt;
    Texture mask;
    Texture waterfall;
    float tick = 0;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.setShader(shader);
        mask.bind(2);
        waterfall.bind(1);
        texture.bind(0);
        shader.setUniformi("u_texture1", 1);
        shader.setUniformi("u_mask", 2);
        shader.setUniformf("time", tick += delta);
        batch.draw(texture, Gdx.graphics.getWidth() / 2 - texture.getWidth() / 2 - 220, Gdx.graphics.getHeight() / 2
                - texture.getHeight() / 2);
        batch.draw(texture, Gdx.graphics.getWidth() / 2 - texture.getWidth() / 2 + 220, Gdx.graphics.getHeight() / 2
                - texture.getHeight() / 2);
        batch.setShader(null);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        bg = new Texture("backgrounds/title.png");
        texture = new Texture("images/awesome.png");
        dirt = new Texture("images/dirt.png");
        grass = new Texture("images/grass.png");
        mask = new Texture("images/mask.png");
        waterfall = new Texture("images/waterfall.jpg");

        shader = new ShaderProgram(Gdx.files.internal("shaders/vertex3.vert"), Gdx.files.internal("shaders/noise.frag"));

        if (shader.isCompiled()) {
            Gdx.app.log("Shader", "Shader compiled successfully!");
        } else {
            Gdx.app.log("Shader", shader.getLog());
        }

        batch = new SpriteBatch();
        //handle input processor
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.ESCAPE) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                } else if (keycode == Keys.R) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new ExperimentScreen());
                }
                return false;
            }
        });
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        shader.dispose();
    }

}
