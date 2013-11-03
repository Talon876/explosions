package org.nolat.explosions.screens;

import org.nolat.explosions.InputAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ExperimentScreen implements Screen {

    private SpriteBatch batch;

    private ParticleEffect effect;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setColor(Color.GREEN);
        effect.draw(batch, delta);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("particles/confetti.p"), Gdx.files.internal("images/"));
        effect.setPosition(100, 100);
        effect.findEmitter("confetti").getTint().setColors(new float[] { 0, 1, 0, 1 });
        effect.start();

        //handle input processor
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                effect.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.ESCAPE) {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
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
        effect.dispose();
    }

}
